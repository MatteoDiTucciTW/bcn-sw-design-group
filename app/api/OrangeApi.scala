package api

import api.ApiDomainEvent.{InvalidOrange, Timeout}
import javax.inject.Singleton
import logging.DomainEvent

// The use of different ids to codify the behaviour of OrangeApi is just because I was lazy not to setup a mock server
// (e.g. WireMock instance) for stubbing an external endpoint in OrangeApiSpec

@Singleton
class OrangeApi {
  private val ONE_LITER_OF_JUICE = 1

  def squeeze(orangeId: String): Either[DomainEvent, Int] = {
    if ("a valid orange id" == orangeId) {
      return Right(ONE_LITER_OF_JUICE)
    }
    if ("a timeout orange id" == orangeId) {
      return Left(Timeout)
    }

    Left(InvalidOrange)
  }

}
