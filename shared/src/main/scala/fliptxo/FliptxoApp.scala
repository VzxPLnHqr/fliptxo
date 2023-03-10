package fliptxo
import cats.effect.*
import std.Console

trait FliptxoApp[F[_]] {

  val console: Console[F]

  val run = console.println("hello")
}