package http

import javax.inject._
import play.api.http.DefaultHttpErrorHandler
import play.api._
import play.api.mvc._
import play.api.mvc.Results._
import play.api.routing.Router
import scala.concurrent._
import play.api.i18n.I18nSupport
import play.api.i18n.MessagesProvider
import play.api.i18n.MessagesApi
import play.api.i18n.DefaultMessagesApi
import play.api.i18n.Langs
import play.api.i18n.MessagesImpl

/**
  * 参照: https://www.playframework.com/documentation/2.8.x/ScalaErrorHandling
  */
@Singleton
class CustomErrorHandler @Inject() (
  env:          Environment,
  config:       Configuration,
  sourceMapper: OptionalSourceMapper,
  router:       Provider[Router],
  langs:         Langs,
  messagesApi:  MessagesApi
) extends DefaultHttpErrorHandler(env, config, sourceMapper, router) {

  // FIXME: 適切に取得
  val lang = langs.availables.head

  override def onNotFound(request: RequestHeader, message: String): Future[Result] = {
    Future.successful(
      NotFound(
        views.html.error.page404()(new MessagesImpl(lang, messagesApi), request)
    ))
  }
}
