package fliptxo

import scoin.*
import Crypto._
import scodec.bits.*
import cats.syntax.all.*
import cats._

/**
  * Encapsulate into an `F` context all the things that we need to be able to
  * do with a FliptxoPeer.
  * 
  * It is up to the implementation to hide private information.
  */
trait FliptxoPeer[F[_]]{

  /**
    * the seed from which all deterministic magic for this peer stems
    *
    * @return
    */
  def seed: F[ByteVector32]

  def priv: F[PrivateKey]

  def pub: F[PublicKey]

    /**
    * calculate the i'th per commitment secret
    */
  def perCommitSecret(i: Int): F[PrivateKey]

    /** calculate the i'th per commitment point
   * */
  def perCommitPoint(i: Int): F[PublicKey] 

  /** a blinded public key */
  def revocationPubKey(remoteNodePerCommitPoint: PublicKey): F[PublicKey]

  /** Calculate the i'th "publishing" secret
   *  The publisher of a commitment transaction is forced to reveal
   *  this secret thereby allowing the other party to punish (if necessary)
   * */
  def publishingSecret(i: Int): F[PrivateKey]

  /**
   * The i'th "publishing" public key. This is the point which the remote
   * party tweaks their real signature with, to get `adapterSig`. If the
   * real signature `sig` is ever broadcast, the local party can calculate the
   * publishing secret (it is just `sig - adapterSig`). */
  def publishingPubKey(i: Int): F[PublicKey]

}

object FliptxoPeer {
  def apply[F[_]: FliptxoPeer]: FliptxoPeer[F] = implicitly

  def apply[F[_]](seedBytes: ByteVector32)(implicit me: MonadError[F,Throwable]): FliptxoPeer[F] =
    new FliptxoPeer[F] {
      val F = MonadError[F,Throwable]
      val seed: F[ByteVector32] = F.pure(seedBytes)

      val priv: F[PrivateKey] = seed.map(s => PrivateKey(sha256(s)))

      val pub: F[PublicKey] = priv.map(_.publicKey)

      def perCommitSecret(i: Int): F[PrivateKey] = seed.map{
        s => ln.Generators.perCommitSecret(s,i)
      }

      def perCommitPoint(i: Int): F[PublicKey] = seed.map{
        s => ln.Generators.perCommitPoint(s,i)
      }

      def revocationPubKey(remoteNodePerCommitPoint: PublicKey): F[PublicKey] =
        pub.map(p => ln.Generators.revocationPubKey(p,remoteNodePerCommitPoint))

      def publishingSecret(i: Int): F[PrivateKey] = seed.map{
        s => ln.Generators.perCommitSecret(
              //naive re-use of ln stuff here
              sha256(ByteVector("publishing secret".getBytes) ++ s),
              index = i
            )
      }

      def publishingPubKey(i: Int): F[PublicKey] = 
        publishingSecret(i).map(_.publicKey)
    }

  /**
    * A very insecure implementation of `FliptxoPeer`. The hash of the name
    * you provide will be used as the seed.
    */
  def SelfDummy[F[_]](name: String)(using MonadError[F,Throwable]) = 
    apply[F](sha256(ByteVector(name.getBytes)))

  /**
   * A very insecure implementation of `FliptxoPeer`. The hash of the name
   * you provide will be used as the seed.
   * */
  def RemoteDummy[F[_]](name: String)(using MonadError[F,Throwable]): FliptxoPeer[F] =
    apply[F](sha256(ByteVector(name.getBytes)))
}