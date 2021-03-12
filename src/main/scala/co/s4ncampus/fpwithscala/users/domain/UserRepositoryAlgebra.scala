package co.s4ncampus.fpwithscala.users.domain

import cats.data.OptionT

trait UserRepositoryAlgebra[F[_]] {
  /**
    * Definición del comportamiento de cración de Users
    * @param user Objeto de tipo User
    * @return Promesa de creación de User
    */
  def create(user: User): F[User]

  /**
    * Definición del comportamiento de busqueda de usuario por parámetro
    * @param legalId Documento de identificación del usuario
    * @return Un OptionT con Some si existen datos o None en caso contrario
    */
  def findByLegalId(legalId: String): OptionT[F, User]

  /**
    * Definición del comportamiento de búsqueda de todos los usuarios
    * @return Promesa de retorno de lista
    */
  def findAll(): F[List[User]]

  /**
    * Definición del comportamiento de actualización de un usuario
    *
    * @param legalId Documento de identificación del usuario
    * @param user Objeto de tipo User
    * @return Promesa de actualización exitosa o fallida
    */
  def updateUser(legalId:String, user: User):  F[Boolean]

  /**
    * Definición del comportamiento de eliminación de un usuario
    *
    * @param legalId Documento de identificación del usuario
    * @return Promesa de eliminación exitosa o fallida
    */
  def deleteByLegalId(legalId: String) : F[Boolean]

}