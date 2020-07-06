package mvc

import play.api.mvc.ActionBuilder
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.BodyParser
import scala.concurrent.ExecutionContext
import play.api.mvc.Result
import scala.concurrent.Future

trait AuthenticatedActionBuilder extends ActionBuilder[Request, AnyContent]

object AuthenticatedActionBuilder {
  def apply(parser: BodyParser[AnyContent])(implicit ec: ExecutionContext): AuthenticatedActionBuilder =
    new AuthenticatedActionBuilderImpl(parser)
}

class AuthenticatedActionBuilderImpl(
  val parser:      BodyParser[AnyContent]
)(implicit ec: ExecutionContext)
  extends AuthenticatedActionBuilder {

  override def executionContext: ExecutionContext = ec

  def invokeBlock[A](
    request: Request[A],
    block:   Request[A] => Future[Result]
  ): Future[Result] = ???
}

// jwtなので認証自体はjwt検証のみで良いはず
// User情報が必要な場合にはRefinerを利用して拡張する
// ただし厳密にはDBまで検証した方が正しい
