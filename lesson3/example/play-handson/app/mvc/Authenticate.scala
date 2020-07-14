package mvc

import play.api.mvc.RequestHeader
import slick.models.User
import scala.concurrent.ExecutionContext
import play.api.mvc.BaseControllerHelpers
import scala.concurrent.Future
import play.api.i18n.Messages
import play.api.i18n.I18nSupport

trait AuthenticateHelpers {
  val SESSION_ID = "sid"
}

// parserなど利用したいことと、Controllerでしか使わないことから
// BaseControllerHelpersと混ぜる
trait AuthenticateActionHelpers {
  self: BaseControllerHelpers =>

  def Authenticated(authenticate: RequestHeader => Future[Option[User]]) = {
    AuthenticateActionBuilder(authenticate, parse.default)(defaultExecutionContext)
  }
}
