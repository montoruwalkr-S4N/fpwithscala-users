package co.s4ncampus.fpwithscala.users.domain

import cats.data.OptionT

trait UserRepositoryAlgebra[F[_]] {
  /**
    * Definición del comportamiento de cración de Users
    * @param user
    * @return Promesa de creación de User
    */
  def create(user: User): F[User]

  /**
    * Deficinición del comportamiento de busqueda de usuario
    * @param legalId Documento de identificación del usuario
    * @return
    */
  def findByLegalId(legalId: String): OptionT[F, User]

  /**
    * @todo delete
    * @todo update
    */
}