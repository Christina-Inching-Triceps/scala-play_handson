package http

import javax.inject._
import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router
import scala.concurrent._
import model.view.HeaderViewModel
import services.AuthenticateService

/**
  * 参照: https://www.playframework.com/documentation/2.8.x/ScalaErrorHandling
  */
@Singleton
class CustomErrorHandler @Inject() (
  env:          Environment,
  config:       Configuration,
  sourceMapper: OptionalSourceMapper,
  router:       Provider[Router],
  authService:  AuthenticateService
)(implicit ec:  ExecutionContext)
  extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  override def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    authService.authenticate(request) map {
      _ match {
        case Right(user) =>
          NotFound(views.html.error.page404(HeaderViewModel.from(Some(user))))
        case Left(_)     =>
          NotFound(views.html.error.page404(HeaderViewModel.from(None)))
      }
    }
  }
}
