package services

import api.OrangeApi
import logging.{DomainEvent, LogLevel}
import org.scalatest.{FunSuite, MustMatchers, WordSpec}
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

class OrangeServiceSpec extends WordSpec with MockitoSugar with MustMatchers{
  val orangeId = "an-orange-id"

  "OrangeService" when {

    "underlying api is able to squeeze an orange" should {
      val expectedLitersOfJuice = 1
      val api = successfulApiWith(expectedLitersOfJuice)
      val service = createServiceWith(api)

      "return success" in {
        service.squeeze(orangeId) mustBe Right(expectedLitersOfJuice)
      }

      "contact the underlying api" in {
        verify(api).squeeze(orangeId)
      }
    }

    "underlying api is not able to squeeze an orange" should {
      object TestError extends DomainEvent(LogLevel.Error, "an api error occurred")
      val api = createFailingApiWith(result = TestError)
      val service = createServiceWith(api)

      "return failure" in {
        service.squeeze(orangeId) mustBe Left(TestError)
      }

      "contact the underlying api" in {
        verify(api).squeeze(orangeId)
      }
    }
  }

  private def successfulApiWith(litersOfJuice: Int) = {
    val api = mock[OrangeApi]
    when(api.squeeze(orangeId)).thenReturn(Right(litersOfJuice))
    api
  }

  private def createServiceWith(api: OrangeApi) = {
    new OrangeService(api)
  }

  private def createFailingApiWith(result: DomainEvent) = {
    val api = mock[OrangeApi]
    when(api.squeeze(orangeId)).thenReturn(Left(result))
    api
  }
}
