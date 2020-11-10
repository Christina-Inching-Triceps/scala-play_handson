package mvc

import slick.models.User
import play.api.mvc.WrappedRequest
import play.api.mvc.Request

// ログイン認証済のリクエスト
class AuthedRequest[A](
  val user: User,
  request:  Request[A]
) extends WrappedRequest[A](request)

// ログイン成功、失敗どちらでも良い場合に利用するリクエスト
class AuthedOrNotRequest[A](
  val user: Option[User],
  request:  Request[A]
) extends WrappedRequest[A](request)

// JWTでの認証用リクエスト
class AuthendicatedRequest[A](
  val sid: Long,
  request: Request[A]
) extends WrappedRequest[A](request)

