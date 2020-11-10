package mvc

import play.api.mvc.RequestHeader
import slick.models.User
import scala.concurrent.ExecutionContext
import play.api.mvc.BaseControllerHelpers
import scala.concurrent.Future
import play.api.mvc.Result

trait AuthenticateHelpers {
  val SESSION_ID = "sid"
}

trait AuthenticateActionHelpers {
  // parserなどのデータを利用せず、Controllerでしか使わないことからBaseControllerHelpersと混ぜる
  self: BaseControllerHelpers =>

  def AuthNAction(authenticate: RequestHeader => Future[Either[Result, User]]) = {
    AuthenticateActionBuilder(authenticate, parse.default)(defaultExecutionContext)
  }

  def AuthNOrNotAction(authenticateOrNot: RequestHeader => Future[Option[User]]) = {
    AuthenticateOrNotActionBuilder(authenticateOrNot, parse.default)(defaultExecutionContext)
  }
}
