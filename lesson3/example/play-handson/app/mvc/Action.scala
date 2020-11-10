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
import play.api.mvc.RequestHeader
import slick.models.User
import play.api.mvc.Action


// 認証処理サンプル1: 実装としてはシンプル。ユーザ検証までしてないので、ちょっと不安が残る
@Singleton
class AuthenticatedAction @Inject()(
  playBodyParser:  PlayBodyParsers //DIするので抽象型のBodyParserではなく実体のPlayBodyParsers
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

// =============================== //
//          Authenticate           //
// =============================== //
//
// 認証処理サンプル2: ActionBuilderはDIせずにHelpers経由で呼び出し
// 認証ロジックのService(Module)をDIで取得して引き渡すようなやつ
// AuthenticateService, AuthenticateActionHelpersを追加
trait  AuthenticateActionBuilder extends ActionBuilder[AuthedRequest, AnyContent]
object AuthenticateActionBuilder {
  def apply(
    authenticate: RequestHeader => Future[Either[Result, User]],
    parser:       BodyParser[AnyContent]
  )(implicit ec: ExecutionContext) = new AuthenticateActionBuilderImpl(authenticate, parser)
}

class AuthenticateActionBuilderImpl (
  val authenticate: RequestHeader => Future[Either[Result, User]],
  val parser:       BodyParser[AnyContent]
)(implicit ec: ExecutionContext)
  extends AuthenticateActionBuilder
     with Results {

  override def executionContext: ExecutionContext = ec

  def invokeBlock[A](
    request: Request[A],
    block:   AuthedRequest[A] => Future[Result]
  ): Future[Result] = authenticate(request) flatMap {
    _ match {
      case Right(user) =>
        block(new AuthedRequest(user, request))
      case Left(result)=> Future.successful(result)
    }
  }
}

// =============================== //
//        AuthenticateOrNot        //
// =============================== //

trait  AuthenticateOrNotActionBuilder extends ActionBuilder[AuthedOrNotRequest, AnyContent]
object AuthenticateOrNotActionBuilder {
  def apply(
    authenticateOrNot: RequestHeader => Future[Option[User]],
    parser:            BodyParser[AnyContent]
  )(implicit ec: ExecutionContext) = new AuthenticateOrNotActionBuilderImpl(authenticateOrNot, parser)
}

class AuthenticateOrNotActionBuilderImpl (
  val authenticateOrNot: RequestHeader => Future[Option[User]],
  val parser:       BodyParser[AnyContent]
)(implicit ec: ExecutionContext)
  extends AuthenticateOrNotActionBuilder
     with Results {

  override def executionContext: ExecutionContext = ec

  def invokeBlock[A](
    request: Request[A],
    block:   AuthedOrNotRequest[A] => Future[Result]
  ): Future[Result] = authenticateOrNot(request) flatMap { userOpt =>
    block(new AuthedOrNotRequest(userOpt, request))
  }
}



