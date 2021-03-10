package co.s4ncampus.fpwithscala.users.domain

/**
  * @constructor      Construye la clase User
  * @param id         Identificación única del usuario generada aleatoriamente
  */

case class User(
    id: Option[Long],
    legalId: String,
    firstName: String,
    lastName: String,
    email: String,
    phone: String)