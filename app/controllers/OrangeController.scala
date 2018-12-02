package controllers

import javax.inject.{Inject, Singleton}
import logging.{DomainEvent, LogLevel}
import org.slf4j.Logger
import play.api.mvc._
import services.OrangeService

import scala.concurrent.ExecutionContext

@Singleton
class OrangeController @Inject()(orangeService: OrangeService, logger: Logger)(cc: ControllerComponents)(implicit ex: ExecutionContext)
  extends AbstractController(cc) {

  def squeeze(id: String): Action[AnyContent] = Action {
    orangeService.squeeze(id) match {

      case Left(domainEvent) =>
        logDomainEvent(domainEvent)
        InternalServerError

      case Right(litersOfJuice) => Ok(litersOfJuice.toString)
    }
  }

  private def logDomainEvent(domainEvent: DomainEvent): Unit = {
    if (domainEvent.logLevel == LogLevel.Error) {
      logger.error(domainEvent.message)
    } else {
      logger.warn(domainEvent.message)
    }
  }
}
