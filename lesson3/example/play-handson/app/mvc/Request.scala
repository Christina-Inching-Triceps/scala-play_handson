package mvc

import slick.models.User
import play.api.mvc.WrappedRequest
import play.api.mvc.Request
import play.api.mvc.PreferredMessagesProvider
import play.api.mvc.MessagesRequestHeader
import play.api.i18n.MessagesApi

// 認証タイミングでユーザ情報取得までするパターンのリクエスト
class UserRequest[A](
  val user: User,
  request:  Request[A],
) extends WrappedRequest[A](request)

// JWTでの認証用リクエスト
class AuthenticatedRequest[A](
  val sid: Long,
  request: Request[A],
) extends WrappedRequest[A](request)

