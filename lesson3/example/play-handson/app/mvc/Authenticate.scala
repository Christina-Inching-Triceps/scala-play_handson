package mvc

import play.api.mvc.RequestHeader
import slick.models.User
import scala.concurrent.ExecutionContext
import play.api.mvc.BaseControllerHelpers

trait AuthenticateHelpers {
  val SESSION_ID = "sid"
}

// parserなど利用したいことと、Controllerでしか使わないことから
// BaseControllerHelpersと混ぜる
trait AuthenticateActionHelpers {
  self: BaseControllerHelpers =>

  def Authenticated(authenticate: RequestHeader => Option[User])(implicit ec: ExecutionContext) = {
    AuthenticateActionBuilder(authenticate, parse.default)
  }
}
