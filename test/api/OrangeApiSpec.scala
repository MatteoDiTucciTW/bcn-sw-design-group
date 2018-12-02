package api

import api.ApiDomainEvent.{InvalidOrange, Timeout}
import org.scalatest.{MustMatchers, WordSpec}

// The use of different ids to codify the behaviour of OrangeApi is just because I was lazy not to setup a mock server
// (e.g. WireMock instance) for stubbing an external endpoint

class OrangeApiSpec extends WordSpec with MustMatchers{
  "OrangeApi" when {

    "receiving a valid orange Id" should {
      val validOrangeId = "a valid orange id"

      "return the liters of juice squeezed" in {
        new OrangeApi().squeeze(validOrangeId) mustBe Right(1)
      }
    }

    "receiving an timeout orange Id" should {
      val timeoutOrangeId = "a timeout orange id"

      "return a timeout domain event" in {
        new OrangeApi().squeeze(timeoutOrangeId) mustBe Left(Timeout)
      }
    }

    "receiving an invalid orange Id" should {
      val invalidOrangeId = "an invalid orange id"

      "return an invalid orange domain event" in {
        new OrangeApi().squeeze(invalidOrangeId) mustBe Left(InvalidOrange)
      }
    }
  }

}
