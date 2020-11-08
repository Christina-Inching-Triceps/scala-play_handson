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

case class UserForm(
  name:            String,
  email:           String,
  password:        String,
  confirmPassword: String
)

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

  val form = Form(
    mapping(
      "name"             -> nonEmptyText(maxLength = 255),
      "email"            -> nonEmptyText(maxLength = 255),
      "password"         -> nonEmptyText(minLength = 8, maxLength = 72),
      "confirm_password" -> nonEmptyText
    )(UserForm.apply)(UserForm.unapply).verifying(
      "error.passwordDisagreement",
      v => v.password == v.confirmPassword
    )
  )

  def register() = AuthNOrNotAction(authService.authenticateOrNot) { implicit request: AuthedOrNotRequest[AnyContent] =>
    Ok(views.html.user.store(
      UserRegisterViewModel.from(request.user, form)
    ))
  }

  def store() = AuthNOrNotAction(authService.authenticateOrNot) async { implicit request: AuthedOrNotRequest[AnyContent] =>
    form
      .bindFromRequest().fold(
        (formWithErrors: Form[UserForm]) => {
          Future.successful(BadRequest(views.html.user.store(
            UserRegisterViewModel.from(request.user, formWithErrors)
          )))
        },
        (form: UserForm) => {
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
