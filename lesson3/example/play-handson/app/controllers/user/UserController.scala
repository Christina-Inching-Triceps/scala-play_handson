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
import mvc.AuthenticateHelpers
import services.AuthenticateService
import mvc.AuthenticateActionHelpers
import mvc.AuthedOrNotRequest
import model.view.UserRegisterViewModel
import model.form.UserForm
import model.form.SignUp

@Singleton
class UserController @Inject() (
  val controllerComponents: ControllerComponents,
  userRepository:           UserRepository,
  authService:              AuthenticateService
)(implicit ec:              ExecutionContext)
  extends BaseController
     with I18nSupport
     // 認証後にsessionに持たせるidのKEYをとるためにwith
     with AuthenticateHelpers
     with AuthenticateActionHelpers {

  def register() = AuthNOrNotAction(authService.authenticate) { implicit request: AuthedOrNotRequest[AnyContent] =>
    Ok(views.html.user.store(
      UserRegisterViewModel.from(request.user, UserForm.signUpForm)
    ))
  }

  def store() = AuthNOrNotAction(authService.authenticate) async { implicit request: AuthedOrNotRequest[AnyContent] =>
    UserForm.signUpForm
      .bindFromRequest().fold(
        (formWithErrors: Form[SignUp]) => {
          Future.successful(BadRequest(views.html.user.store(
            UserRegisterViewModel.from(request.user, formWithErrors)
          )))
        },
        (form: SignUp) => {
          val bcryptEncoder   = new BCryptPasswordEncoder()
          val encodedPassowrd = bcryptEncoder.encode(form.password)
          for {
            id <- userRepository.insert(
              User(
                name     = form.name,
                email    = form.email,
                password = encodedPassowrd
              )
            )
          } yield {
            // UserIdをsessionのkeyとして利用
            Redirect("/").withSession((SESSION_ID, id.toString))
          }
        }
      )
  }

}
