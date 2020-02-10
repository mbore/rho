package org.http4s.rho.bits

import cats.Monad
import eu.timepit.refined.api.{RefType, Validate}

import scala.reflect.runtime.universe._

package object refined {
  implicit def refinedParser[F[_], T, P, R[_, _]](
                                                   implicit
                                                   tt: TypeTag[R[T, P]],
                                                   underlying: StringParser[F, T],
                                                   validate: Validate[T, P],
                                                   refType: RefType[R]
                                                 ): StringParser[F, R[T, P]] = new StringParser[F, R[T, P]] {
    /** Attempt to parse the `String`. */
    override def parse(s: String)(implicit F: Monad[F]): ResultResponse[F, R[T, P]] =
      underlying.parse(s).flatMap { r =>
        refType.refine(r) match {
          case Left(value) => FailureResponseOps[F].error(value)
          case Right(value) => SuccessResponse(value)
        }
      }

    /** TypeTag of the type R[T, P]] */
    override def typeTag: Option[TypeTag[R[T, P]]] = Some(tt)
  }
}
