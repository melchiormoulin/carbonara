package carbonara.relay

import scala.io.StdIn

object Run extends App {
  Bootstrap.run(args)
  println("Hello world!")
  StdIn.readLine()
  println("Bye!")
}
