package fliptxo

import calico.*
import calico.html.io.{given,*}
import cats.effect.*
import fs2.dom.*

object FliptxoApp extends IOWebApp:
  val hash = scoin.Crypto.sha256(scodec.bits.ByteVector("abc".getBytes)).toHex
  val pub = scoin.Crypto.PrivateKey(BigInt(5)).publicKey
  def render: Resource[IO, HtmlElement[IO]] =
    div(s"Fliptxo - From Alice to Bob to Alice to Bob to Alice ... N times, $hash, $pub")