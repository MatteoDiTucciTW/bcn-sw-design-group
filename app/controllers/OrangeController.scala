package controllers

import api.ApiDomainEvent.{InvalidOrange, Timeout}
import javax.inject.{Inject, Singleton}
import logging.DomainEvent
import org.slf4j.Logger
import play.api.mvc._
import services.OrangeService

import scala.concurrent.ExecutionContext

@Singleton
class OrangeController @Inject()(orangeService: OrangeService, logger: Logger)(cc: ControllerComponents)(
    implicit ex: ExecutionContext)
    extends AbstractController(cc) {

  def squeeze(id: String): Action[AnyContent] = Action {
    orangeService.squeeze(id) match {

      case Left(domainEvent) =>
        logDomainEvent(domainEvent)
        InternalServerError

      case Right(litersOfJuice) => Ok(litersOfJuice.toString)
    }
  }

  private def logDomainEvent(domainEvent: DomainEvent): Unit =
    domainEvent match {
      case Timeout       => logger.warn(domainEvent.message)
      case InvalidOrange => logger.error(domainEvent.message)
      case _             => logger.error(domainEvent.message)
    }
}
