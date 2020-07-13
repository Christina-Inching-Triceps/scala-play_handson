package controllers

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
import mvc.AuthenticateHelpers

case class LoginForm(
  email:           String,
  password:        String
)

@Singleton
class LoginController @Inject() (
  val controllerComponents: ControllerComponents,
  userRepository:           UserRepository
)(implicit ec:              ExecutionContext)
  extends BaseController
     with I18nSupport {

  val loginForm = Form(
    mapping(
      "email"            -> nonEmptyText(maxLength = 255),
      "password"         -> nonEmptyText(minLength = 8, maxLength = 72)
    )(LoginForm.apply)(LoginForm.unapply)
  )

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.login(loginForm))
  }

  def login() = Action async { implicit request: Request[AnyContent] =>
    loginForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.login(formWithErrors)))
      },
      form => {
        val bcryptEncoder   = new BCryptPasswordEncoder()

        for {
          userOpt <- userRepository.findByEmail(form.email)
        } yield {
          userOpt match {
            case Some(user) if bcryptEncoder.matches(form.password, user.password) =>
              Redirect("/tweet/list")
            case _ =>
              BadRequest(
                views.html.login(
                  loginForm.fill(form).withGlobalError("error.authenticate")
                )
              )
          }
        }
      })
  }


}
