package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import _root_.mvc.AuthenticateActionHelpers
import services.AuthenticateService
import _root_.mvc.AuthedOrNotRequest
import scala.concurrent.ExecutionContext

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 */
@Singleton
class HomeController @Inject()(
  val controllerComponents: ControllerComponents,
  authService:              AuthenticateService
)(implicit ec:              ExecutionContext)
extends BaseController
with    AuthenticateActionHelpers {

  /**
   * Create an Action to render an HTML page.
   *
   * The configuration in the `routes` file means that this method
   * will be called when the application receives a `GET` request with
   * a path of `/`.
   */
  def index() = AuthNOrNotAction(authService.authenticate) { implicit request: AuthedOrNotRequest[AnyContent] =>
    request.user match {
      case Some(user) =>
        Redirect(tweet.routes.TweetController.list())
      case None       =>
        Redirect(routes.LoginController.index())
    }
  }
}
