package fliptxo
import cats.effect.*

trait FliptxoApp {
  val run = IO.println("hello world")
}