package fliptxo

import cats.MonadThrow
import cats.syntax.all.*

class FliptxoApp[F[_] : MonadThrow](console: cats.effect.std.Console[F]) {
  
  def run: F[Unit] = console.println("name?") 
              >> console.readLine
              .flatTap(m => console.println(s"hello $m"))
              .map(m => scoin.Crypto.sha256(scodec.bits.ByteVector(m.getBytes)))
              .flatTap(h => console.println(s"the hash of your name is ${h.toHex}"))
              .as(())

}