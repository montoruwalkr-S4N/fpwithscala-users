package co.s4ncampus.fpwithscala.users.infraestructure.repository

import co.s4ncampus.fpwithscala.users.domain._
import cats.data._
import cats.syntax.all._
import doobie._
import doobie.implicits._
import cats.effect.Bracket


private object UserSQL {

  /**
    * Inserta un nuevo usuario en la tabla USERS
    *
    * @param user   Objeto tipo User
    * @return       Actualización de estado de la tabla USERS
    * 
    */
  def insert(user: User): Update0 = sql"""
    INSERT INTO USERS (LEGAL_ID, FIRST_NAME, LAST_NAME, EMAIL, PHONE)
    VALUES (${user.legalId}, ${user.firstName}, ${user.lastName}, ${user.email}, ${user.phone})
  """.update

  /**
    * Buscar usuario usando el legalId
    *
    * @param legalId   String que representa el legal id del usuario
    * @return          Objeto de tipo User
    * 
    */
  def selectByLegalId(legalId: String): Query0[User] = sql"""
    SELECT ID, LEGAL_ID, FIRST_NAME, LAST_NAME, EMAIL, PHONE
    FROM USERS
    WHERE LEGAL_ID = $legalId
  """.query[User]

  /**
    * Buscar todos los usuarios de la tabla USERS
    *
    * @return ConnectionIO de lista de usuarios
    */
  def listAll(): ConnectionIO[List[User]] = sql"""
    SELECT ID, LEGAL_ID, FIRST_NAME, LAST_NAME, EMAIL, PHONE
    FROM USERS
  """.query[User].to[List]

  def removeByLegalId(legallId: String): Update0 = sql"""
    DELETE
    FROM USERS
    WHERE LEGAL_ID = $legallId
  """.update
}



class DoobieUserRepositoryInterpreter[F[_]: Bracket[?[_], Throwable]](val xa: Transactor[F])
    extends UserRepositoryAlgebra[F] {
  import UserSQL._

  /**
    * Crea un nuevo usuario generando un id unico usando la función insert
    *
    * @param user    Objeto de tipo User
    * @return        Resultado de la query (filas afectadas en la base de datos)
    * 
    */
  def create(user: User): F[User] = 
    insert(user).withUniqueGeneratedKeys[Long]("ID").map(id => user.copy(id = id.some)).transact(xa)

  /**
    * Busca un usuario por el legal id en la base de datos
    *
    * @param legalId  String que representa el legal id del usuario
    * @return         Promesa de retornar un valor o el Usuario que cumple con el parametro de legalId
    * 
    */
  def findByLegalId(legalId: String): OptionT[F, User] = OptionT(selectByLegalId(legalId).option.transact(xa))
  /**
    * Buscar todos los usuarios en la base de datos
    *
    *  @return Promesa de retorno de lista
    */
  def findAll(): F[List[User]] = listAll().transact(xa)

  def deleteByLegalId(legalId: String): F[Int] = removeByLegalId(legalId).run.transact(xa)

}

object DoobieUserRepositoryInterpreter {
  def apply[F[_]: Bracket[?[_], Throwable]](xa: Transactor[F]): DoobieUserRepositoryInterpreter[F] =
    new DoobieUserRepositoryInterpreter[F](xa)
}