package fliptxo

import calico.*
import calico.html.io.{given,*}
import cats.effect.*
import fs2.dom.*
import cats.syntax.all.*

object Main extends IOWebApp:

  val fliptxoApp = CalicoConsole[IO].mkResource(FliptxoApp(_).run)

  val render: Resource[IO, HtmlElement[IO]] =
    div(
      div(p(
        "Source code and some more explanation here: ",
        a("https://github.com/VzxPLnHqr/fliptxo", href := "https://github.com/VzxPLnHqr/fliptxo")
      )),
      div(fliptxoApp)
    )