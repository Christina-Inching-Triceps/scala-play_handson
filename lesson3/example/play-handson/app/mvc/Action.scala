package mvc

import javax.inject.{Inject, Singleton}
import play.api.mvc.ActionBuilder
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.mvc.BodyParser
import scala.concurrent.ExecutionContext
import play.api.mvc.Result
import scala.concurrent.Future
import controllers.routes
import play.api.mvc.Results
import scala.util.Try
import play.api.mvc.PlayBodyParsers


// 認証処理サンプル1: 実装としてはシンプル。ユーザ検証までしてないので、ちょっと不安が残る
@Singleton
class AuthenticatedAction @Inject()(
  playBodyParser:  PlayBodyParsers //DIするのでBodyParserではなくPlayBodyParsers
)(implicit ec: ExecutionContext)
  extends ActionBuilder[AuthendicatedRequest, AnyContent]
     with AuthenticateHelpers
     with Results {

  override def executionContext: ExecutionContext = ec
  override def parser: BodyParser[AnyContent]     = playBodyParser.anyContent

  def invokeBlock[A](
    request: Request[A],
    block:   AuthendicatedRequest[A] => Future[Result]
  ): Future[Result] = {
    request.session.get(SESSION_ID) match {
      case Some(sid) if Try(sid.toLong).isSuccess =>
        block(new AuthendicatedRequest(sid.toLong, request))
      case None      =>
        Future.successful(Redirect("/"))
    }
  }
}

// jwtなので認証自体はjwt検証のみで良いはず
// User情報が必要な場合にはRefinerを利用して拡張する
// ただし厳密にはDBまで検証した方が正しい
