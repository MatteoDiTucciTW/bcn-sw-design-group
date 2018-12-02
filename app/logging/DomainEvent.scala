package logging

abstract case class DomainEvent(logLevel: LogLevel, message: String)

sealed abstract class LogLevel

object LogLevel {

  case object Warning extends LogLevel

  case object Error extends LogLevel

}

