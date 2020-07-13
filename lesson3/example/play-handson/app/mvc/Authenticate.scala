package mvc

import play.api.mvc.RequestHeader
import slick.models.User
import scala.concurrent.ExecutionContext
import play.api.mvc.BaseControllerHelpers
import scala.concurrent.Future

trait AuthenticateHelpers {
  val SESSION_ID = "sid"
}

// parserなど利用したいことと、Controllerでしか使わないことから
// BaseControllerHelpersと混ぜる
trait AuthenticateActionHelpers {
  self: BaseControllerHelpers =>

  def Authenticated(authenticate: RequestHeader => Future[Option[User]])(implicit ec: ExecutionContext) = {
    AuthenticateActionBuilder(authenticate, parse.default)
  }
}
