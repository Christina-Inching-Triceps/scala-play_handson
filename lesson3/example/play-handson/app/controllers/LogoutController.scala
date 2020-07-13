package controllers

import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents
import play.api.mvc.BaseController
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.i18n.I18nSupport
import scala.concurrent.ExecutionContext

@Singleton
class LogoutController @Inject() (
  val controllerComponents: ControllerComponents
)(implicit ec:              ExecutionContext)
  extends BaseController
     with I18nSupport {

  def logout() = Action { implicit request: Request[AnyContent] =>
    Redirect("/login").withNewSession
  }
}
