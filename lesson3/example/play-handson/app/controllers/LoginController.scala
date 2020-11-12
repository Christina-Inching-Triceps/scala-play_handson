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
import mvc.{AuthenticateHelpers, AuthedOrNotRequest, AuthenticateActionHelpers}
import model.view.LoginViewModel
import model.view.layout.LayoutViewModel
import model.form._
import services.AuthenticateService

@Singleton
class LoginController @Inject() (
  val controllerComponents: ControllerComponents,
  userRepository:           UserRepository,
  authService:              AuthenticateService
)(implicit ec:              ExecutionContext)
  extends BaseController
     with I18nSupport
     with AuthenticateHelpers
     with AuthenticateActionHelpers {

  def index() = AuthNOrNotAction(authService.authenticate) { implicit request: AuthedOrNotRequest[AnyContent] =>
    Ok(views.html.login(
      LoginViewModel.from(
        layout = LayoutViewModel.from(request.user),
        form   = LoginForm.form
      )
    ))
  }

  def login() = AuthNOrNotAction(authService.authenticate) async { implicit request: AuthedOrNotRequest[AnyContent] =>
    LoginForm.form.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(BadRequest(views.html.login(
          LoginViewModel.from(
            layout = LayoutViewModel.from(request.user),
            form   = formWithErrors
          )
        )))
      },
      form => {
        val bcryptEncoder   = new BCryptPasswordEncoder()

        for {
          userOpt <- userRepository.findByEmail(form.email)
        } yield {
          userOpt match {
            case Some(user) if bcryptEncoder.matches(form.password, user.password) =>
              Redirect("/tweet/list").withSession((SESSION_ID, user.id.get.toString))

            case _ =>
              BadRequest(
                views.html.login(
                  LoginViewModel.from(
                    layout = LayoutViewModel.from(request.user),
                    form   = LoginForm.form.fill(form).withGlobalError("error.authenticate")
                  )
                )
              )
          }
        }
      })
  }


}
