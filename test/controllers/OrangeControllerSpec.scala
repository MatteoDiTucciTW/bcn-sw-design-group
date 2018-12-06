package controllers

import api.ApiDomainEvent.Timeout
import logging.{DomainEvent, LogLevel}
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{MustMatchers, WordSpec}
import org.slf4j.Logger
import play.api.mvc.Results
import play.api.test.Helpers._
import play.api.test.{FakeRequest, Helpers}
import services.OrangeService

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

class OrangeControllerSpec extends WordSpec with MockitoSugar with MustMatchers {
  val orangeId = "an-orange-id"

  "Orange Controller" when {

    "underlying service is able to squeeze an orange" should {
      val expectedLitersOfJuice = 1
      val service = successfulServiceWith(expectedLitersOfJuice)
      val controller = controllerWith(service)

      val response = Await.result(controller.squeeze(orangeId)(FakeRequest()), 1.second)

      "return success with the amount of juice" in {
        response mustEqual Results.Ok(expectedLitersOfJuice.toString)
      }

      "contact the underlying service" in {
        verify(service).squeeze(orangeId)
      }
    }

    "underlying service is not able to squeeze an orange" should {
      val logger = mock[Logger]
      object TestError extends DomainEvent(LogLevel.Error, "a service error occurred")
      val service = {
        unSuccessfulServiceWith(TestError)
      }
      val controller = controllerWith(service, logger)

      val response = controller.squeeze(orangeId)(FakeRequest())

      "return an error" in {
        status(response) mustBe INTERNAL_SERVER_ERROR
      }

      "contact the underlying service" in {
        verify(service).squeeze(orangeId)
      }

      "log an error" in {
        verify(logger).error(TestError.message)
      }
    }

    "underlying api times out" should {
      val logger = mock[Logger]
      val service = {
        unSuccessfulServiceWith(Timeout)
      }
      val controller = controllerWith(service, logger)

      val response = controller.squeeze(orangeId)(FakeRequest())

      "return an error" in {
        status(response) mustBe INTERNAL_SERVER_ERROR
      }

      "contact the underlying service" in {
        verify(service).squeeze(orangeId)
      }

      "log an warning" in {
        verify(logger).warn(Timeout.message)
      }
    }
  }

  private def successfulServiceWith(litersOfJuice: Int) = {
    val service = mock[OrangeService]
    when(service.squeeze(orangeId)).thenReturn(Right(litersOfJuice))
    service
  }

  private def unSuccessfulServiceWith(result: DomainEvent) = {
    val service = mock[OrangeService]
    when(service.squeeze(orangeId)).thenReturn(Left(result))
    service
  }

  private def controllerWith(service: OrangeService = mock[OrangeService], logger: Logger = mock[Logger]) =
    new OrangeController(orangeService = service, logger = logger)(Helpers.stubControllerComponents())

}
