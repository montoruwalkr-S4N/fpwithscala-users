package co.s4ncampus.fpwithscala.users.domain

import cats.data._
import cats.Monad


class UserService[F[_]](repository: UserRepositoryAlgebra[F], validation: UserValidationAlgebra[F]) {
  /**
    * Antes de insertar un nuevo usuario se valida de que no exista, de lo contrario, lo crea
    * @param user Objeto de tipo User
    * @param M Mónada implícito
    * @return Validación o guardado del objeto
    */
  def create(user: User)(implicit M: Monad[F]): EitherT[F, UserAlreadyExistsError, User] =
    for {
      _ <- validation.doesNotExist(user)
      saved <- EitherT.liftF(repository.create(user))
    } yield saved

  /**
    * Realiza una búsqueda a Users usando como parámetro el Legal Id introducido por el usuario
    * @param legalId Parámetro de búsqueda
    * @param M Mónada implícita
    * @return Objeto de tipo User que cumpla con la búsqueda
    */
  def findByLegalId(legalId:String)(implicit M: Monad[F]): OptionT[F, User] =
    for {
      founded <- repository.findByLegalId(legalId)
    } yield founded

  /**
    * Lista todos los usuarios de la base de datos
    * @param M Mónada implícita
    * @return Lista de objetos de tipo User. En caso de que no exístan datos, se retorna lista vacía
    */
  def findAll()(implicit M: Monad[F]): OptionT[F, List[User]] =
    for {
      allElements <- OptionT.liftF(repository.findAll())
    } yield allElements


  def updateUser(id:Long, user: User)(implicit M: Monad[F]): OptionT[F, User] =
    for {
      saved <- OptionT.liftF(repository.updateUser(id, user))
    } yield saved


}

object UserService{
  def apply[F[_]](
                 repositoryAlgebra: UserRepositoryAlgebra[F],
                 validationAlgebra: UserValidationAlgebra[F],
                 ): UserService[F] =
    new UserService[F](repositoryAlgebra, validationAlgebra)
}