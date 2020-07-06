package controllers.user

import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents
import play.api.mvc.BaseController
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import slick.repositories.UserRepository
import slick.models.User

@Singleton
class HomeController @Inject() (
  val controllerComponents: ControllerComponents
)(implicit ec:              ExecutionContext)
  extends BaseController
     with I18nSupport {

  def home() = Action { implicit request: Request[AnyContent] =>
    Ok
  }
}
