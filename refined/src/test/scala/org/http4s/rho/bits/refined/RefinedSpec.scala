package org.http4s.rho.bits.refined

import cats.effect.IO
import eu.timepit.refined.api.Refined
import eu.timepit.refined.numeric.Positive
import eu.timepit.refined.collection.NonEmpty
import org.http4s.rho.bits.{FailureResponse, SuccessResponse, refined}
import org.specs2.mutable.Specification

class RefinedSpec extends Specification {
  private def unwrapRefined[A](ref: Refined[A,_]): A = ref.value

  "StringParser for refined types" should {
    "parse positive int" in {
      refined.refinedParser[IO, Int, Positive, Refined].parse("1").map(unwrapRefined) === SuccessResponse(1)
      refined.refinedParser[IO, Int, Positive, Refined].parse("-1") must haveClass[FailureResponse[IO]]
    }

    "parse not empty string" in {
      refined.refinedParser[IO, String, NonEmpty , Refined].parse("1").map(unwrapRefined) === SuccessResponse("1")
      refined.refinedParser[IO, Int, Positive, Refined].parse("") must haveClass[FailureResponse[IO]]
    }

  }


}
