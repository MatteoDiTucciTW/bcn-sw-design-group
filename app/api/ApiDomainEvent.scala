package api

import logging.{DomainEvent, LogLevel}

sealed abstract class ApiDomainEvent(logLevel: LogLevel, message: String) extends DomainEvent(logLevel, message)

object ApiDomainEvent {
  object InvalidOrange extends ApiDomainEvent(LogLevel.Warning, "the orange id is invalid")
  object Timeout extends ApiDomainEvent(LogLevel.Warning, "a timeout occurred")
}
