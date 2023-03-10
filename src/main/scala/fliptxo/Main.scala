package fliptxo

import calico.*
import calico.html.io.{given,*}
import cats.effect.*
import fs2.dom.*

object Main extends IOWebApp:
  val hash = scoin.Crypto.sha256(scodec.bits.ByteVector("abc".getBytes)).toHex
  val pub = scoin.Crypto.PrivateKey(BigInt(5)).publicKey
  def render: Resource[IO, HtmlElement[IO]] = CalicoConsole[IO].component