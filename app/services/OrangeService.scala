package services

import api.OrangeApi
import javax.inject.{Inject, Singleton}
import logging.DomainEvent

@Singleton
class OrangeService @Inject()(api: OrangeApi) {

  def squeeze(orangeId: String): Either[DomainEvent, Int] = {
    api.squeeze(orangeId)
  }

}
