package carbonara.relay

import com.typesafe.scalalogging.Logger

import scala.io.StdIn

object Run extends App {

  val logger = Logger(getClass)
  logger.info("Starting carbonara-relay...")

  Bootstrap.run(args)
  println("Hello world!")
  StdIn.readLine()
  println("Bye!")
}
