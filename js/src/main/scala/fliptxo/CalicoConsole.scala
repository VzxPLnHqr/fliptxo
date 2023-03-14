package fliptxo

import calico.*
import calico.html.io.{given,*}
import calico.syntax.*
import cats.syntax.all.*
import cats.effect.*
import cats.effect.syntax.all.*
import fs2.*
import fs2.concurrent.*
import fs2.dom.*
import org.scalajs.dom.KeyValue

// should use cats.std.Console instead/eventually
// this will do for now though
trait SimpleConsole[F[_]]{

  def println(msg: String): F[Unit]

  def readLine: F[String]

}

trait CalicoConsole[F[_]] {

  def mkResource(app: std.Console[F] => F[Unit]): Resource[F, HtmlElement[F]] 

}

object CalicoConsole {

  def apply[F[_] : CalicoConsole]: CalicoConsole[F] = implicitly

  implicit val calicoConsoleIO: CalicoConsole[IO] = new CalicoConsole[IO] {

    val stdOut = SignallingRef[IO].of(List.empty[String]).toResource
    val stdIn = SignallingRef[IO].of("").toResource
    val inputDisabledIO = SignallingRef[IO].of(true).toResource
    val submittedIO = SignallingRef[IO].of(false).toResource
    val stdIO = (stdIn,stdOut,inputDisabledIO,submittedIO).tupled

    val console = stdIO.flatMap{
      case(in,out,disableIn,submitted) =>
        IO{ new std.Console[IO] {

            // Members declared in cats.effect.std.Console
            def error[A](a: A)(implicit S: cats.Show[A]): cats.effect.IO[Unit] = ???
            def errorln[A](a: A)(implicit S: cats.Show[A]): cats.effect.IO[Unit] = ???
            
            def print[A](a: A)(implicit S: cats.Show[A]): cats.effect.IO[Unit] = ???
            def println[A : cats.Show](msg: A): IO[Unit] = out.update(_.appended(msg.show))

            def readLineWithCharset(charset: java.nio.charset.Charset): IO[String] =
                disableIn.set(false) 
                  >> submitted.waitUntil(_ == true) 
                    >> in.getAndSet("")
                      .flatTap(_ => disableIn.set(true) >> submitted.set(false))
          }
        }.flatMap(c => IO(in,out,disableIn,submitted,c)).toResource
    }

    def mkResource(app: std.Console[IO] => IO[Unit]): Resource[IO, HtmlElement[IO]] = console.flatMap { 
      case (in,out, disableIn, submitted, simpleconsole) =>
        app(simpleconsole).background.flatMap{ _ => 
          div(
            div(out.map(xs => ul(xs.map(x => li(pre(x)))))),
            input.withSelf{ self =>
              (
                placeholder <-- disableIn.map{ 
                                    case true => ""
                                    case false => "type here and press enter"
                                  },
                onKeyUp --> (_.foreach
                                (v => 
                                  self.value.get.flatMap(in.set) 
                                    >> (
                                        if(v.key == KeyValue.Enter) 
                                          submitted.set(true) 
                                        else 
                                          IO.unit
                                      )
                                )
                            ),
                disabled <-- disableIn,
                value <-- in
              )
            },
            button.withSelf{ self => 
              (
                "OK",
                disabled <-- disableIn,
                onClick --> (_.foreach(_ => submitted.set(true)))
              )
            }
          )
        }
    }
  }
}