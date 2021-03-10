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
}

object UserService{
  def apply[F[_]](
                 repositoryAlgebra: UserRepositoryAlgebra[F],
                 validationAlgebra: UserValidationAlgebra[F],
                 ): UserService[F] =
    new UserService[F](repositoryAlgebra, validationAlgebra)
}