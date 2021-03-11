package co.s4ncampus.fpwithscala.users.controller

import co.s4ncampus.fpwithscala.users.domain._

import cats.effect.Sync
import cats.syntax.all._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl


import org.http4s.{EntityDecoder, HttpRoutes}

import co.s4ncampus.fpwithscala.users.domain.User

class UsersController[F[_]: Sync] extends Http4sDsl[F] {

    implicit val userDecoder: EntityDecoder[F, User] = jsonOf

    /**
      * Se encarga de manejar las peticiones de creación de usuario
      * @param userService Objeto tipo UserService
      * @return Right si el objeto se ha guardado correctamente y Left en caso deue 
      */
    private def createUser(userService: UserService[F]): HttpRoutes[F] =
        HttpRoutes.of[F] {
            case req @ POST -> Root =>
                val action = for {
                    user <- req.as[User]
                    result <- userService.create(user).value
                } yield result

                action.flatMap {
                    case Right(saved) => Ok(saved.asJson)
                    case Left(UserAlreadyExistsError(existing)) => Conflict(s"The user with legal id ${existing.legalId} already exists")
                }
        }

    /**
     * Se encarga de manejar las peticiones de búsqueda por parámetro
     * @param userService Objeto tipo UserService
     * @return OptionT de Some si se encontró el usuario o un None si no se encontró el objeto
     */
    private def findUserByLegalId(userService: UserService[F]): HttpRoutes[F] =
        HttpRoutes.of[F] {
            case GET -> Root / id =>
                val user = s"$id"
                val action = for {
                    result <- userService.findByLegalId(user).value
                } yield result

                action.flatMap {
                    case Some(saved) => Ok(saved.asJson)
                    case None => Conflict(s"The user with legal id $id does not exists")
                }
        }

    /**
      * Se encarga de manejar las peticiones de búsqueda para todos los usuarios sin parámetros
      * @param userService Objeto tipo UserService
      * @return OptionT de Some(List()) si no hay usuarios, un Some() si se encontró usuarios o un None si no se encontró algo
      */
    private def findAll(userService: UserService[F]): HttpRoutes[F] =
        HttpRoutes.of[F] {
            case GET -> Root =>
                val action = for {
                    result <- userService.findAll().value
                } yield result

                action.flatMap {
                    case Some(List()) => Conflict(s"There is no users in the db")
                    case Some(saved) => Ok(saved.asJson)
                    case None => Conflict(s"An unexpected error has occurred")
                }
        }

    /**
      * Se encarga de manejar las peticiones de actualización de usuarios en la base de datos
      *
      * @param userService Objeto tipo UserService
      * @return Mensaje de operacion exitosa con codigo 1 y de operacion no exitosa con codigo 0
      */
    private def updateUser(userService: UserService[F]): HttpRoutes[F] =
        HttpRoutes.of[F] {
            case req@PUT -> Root / legalId =>
                val action = for {
                    user <- req.as[User]
                    result <- userService.updateUser(legalId.toString, user).value
                } yield result

                action.flatMap {
                    case Some(saved) if saved == 1 => Ok(s"User with $legalId has been succesfully updated")
                    case Some(saved) if saved == 0 => Conflict(s"There is no such a user with legal id $legalId in the db")
                    case None => Conflict(s"An unexpected error has occurred")
                }
        }

    /**
      * Se encarga de manejar las peticiones de borrado de usuarios en la base de datos
      *
      * @param userService Objeto tipo UserService
      * @return Mensaje de operacion exitosa con codigo 1 y de operacion no exitosa con codigo 0
      */
    private def deleteByLegalId(userService: UserService[F]): HttpRoutes[F] =
        HttpRoutes.of[F] {
            case DELETE -> Root / legalId =>
                val user = s"$legalId"
                val action = for {
                    result <- userService.deleteByLegalId(user).value
                } yield result
                action.flatMap {
                    case Some(saved) if saved == 1 => Ok(s"User with $legalId has been succesfully deleted")
                    case Some(saved) if saved == 0 => Conflict(s"There is no such a user with legal id $legalId in the db")
                    case None => Conflict(s"An unexpected error has occurred")
                }

        }

    /**
      * Se definen los métodos del endpoint por su tipo
      * @param userService Objeto tipo UserService
      * @return Lista de endpoints
      */
    def endpoints(userService: UserService[F]): HttpRoutes[F] = {
        //To convine routes use the function `<+>`
        createUser(userService) <+> findUserByLegalId(userService) <+> findAll(userService) <+> updateUser(userService) <+> deleteByLegalId(userService)
    }
}

object UsersController {
    def endpoints[F[_]: Sync](userService: UserService[F]): HttpRoutes[F] =
        new UsersController[F].endpoints(userService)
}