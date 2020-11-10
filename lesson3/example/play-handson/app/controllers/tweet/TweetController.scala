package controllers.tweet

import javax.inject.{Inject, Singleton}
import play.api.mvc.ControllerComponents
import play.api.mvc.BaseController
import play.api.mvc.Request
import play.api.mvc.AnyContent
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.I18nSupport
import scala.concurrent.ExecutionContext
import slick.models.Tweet
import slick.repositories.TweetRepository
import scala.concurrent.Future
import services.AuthenticateService
import mvc.AuthenticateActionHelpers
import mvc.AuthedRequest
import model.view.{TweetListViewModel, TweetEditViewModel, TweetShowViewModel, TweetRegisterViewModel}
import model.view.HeaderViewModel
import model.form._

/**
  * @SingletonでPlayFrameworkの管理下でSingletonオブジェクトとして本クラスを扱う指定をする
  * @Injectでconstructorの引数をDIする
  * BaseControllerにはprotected の controllerComponentsが存在するため、そこに代入されている。
  * controllerComponentsがActionメソッドを持つため、Actionがコールできる
  *   ActionはcontrollerComponents.actionBuilderと同じ
  */
@Singleton
class TweetController @Inject() (
  val controllerComponents: ControllerComponents,
  // FIXME: DIで入れるが、Actionを使う時の見た目を通常のActionに合わせるためにUpperCamelで命名
  // AuthNAction:   AuthNActionAction,
  tweetRepository: TweetRepository,
  authService:     AuthenticateService
)(implicit ec:     ExecutionContext)
  extends BaseController
     with I18nSupport
     with AuthenticateActionHelpers {

  /**
    * Tweetを一覧表示
    *   Action.asyncとすることでreturnの型としてFuture[Result]を受け取れるように修正
    */
  def list() = AuthNAction(authService.authenticate) async { implicit request: AuthedRequest[AnyContent] =>
    // DBから値を取得してreturnするように修正
    for {
      // FIXME: EntityModelは認証埋め込み後に修正する
      tweets <- tweetRepository.selectByUser(request.user.id.get)
    } yield {
      val viewModel = TweetListViewModel.from(
        userOpt = Some(request.user),
        tweets  = tweets
      )
      Ok(views.html.tweet.list(viewModel))
    }
  }

  /**
    * 対象IDのTweet詳細を表示
    */
  def show(id: Long) = AuthNAction(authService.authenticate) async { implicit request: AuthedRequest[AnyContent] =>
    // idが存在して、値が一致する場合にfindが成立
    for {
      tweetOpt <- tweetRepository.findByIdAndUser(id, request.user.id.get)
    } yield {
      tweetOpt match {
        case Some(tweet) => Ok(views.html.tweet.show(
          TweetShowViewModel.from(Some(request.user), tweet)
        ))
        case None        => NotFound(views.html.error.page404(HeaderViewModel.from(Some(request.user))))
      }
    }
  }

  /**
    * 登録画面の表示用
    */
  def register() = AuthNAction(authService.authenticate) { implicit request: AuthedRequest[AnyContent] =>
    Ok(views.html.tweet.store(
      TweetRegisterViewModel.from(
        Some(request.user),
        TweetForm.tweetContentForm
      )
    ))
  }

  /**
    * 登録処理実を行う
    */
  def store() = AuthNAction(authService.authenticate) async { implicit request: AuthedRequest[AnyContent] =>
    // foldでデータ受け取りの成功、失敗を分岐しつつ処理が行える
    TweetForm.tweetContentForm
      .bindFromRequest().fold(
        // 処理が失敗した場合に呼び出される関数
        (formWithErrors: Form[TweetContent]) => {
          Future.successful(BadRequest(views.html.tweet.store(
            TweetRegisterViewModel.from(
              Some(request.user),
              formWithErrors
            )
          )))
        },
        // 処理が成功した場合に呼び出される関数
        (tweetFormData: TweetContent) => {
          for {
            // データを登録。returnのidは不要なので捨てる
            _ <- tweetRepository.insert(Tweet(
              None,
              request.user.id.get,
              tweetFormData.content)
            )
          } yield {
            Redirect(routes.TweetController.list())
          }
        }
      )
  }

  /**
    * 編集画面を開く
    */
  def edit(id: Long) = AuthNAction(authService.authenticate) async { implicit request: AuthedRequest[AnyContent] =>
    for {
      tweetOpt <- tweetRepository.findByIdAndUser(id, request.user.id.get)
    } yield {
      tweetOpt match {
        case Some(tweet) =>
          Ok(
            views.html.tweet.edit(
              TweetEditViewModel.from(
                Some(request.user),
                id, // データを識別するためのidを渡す
                TweetForm.tweetContentForm.fill(TweetContent(tweet.content)) // fillでformに値を詰める
              )
            )
          )
        case None        =>
          NotFound(views.html.error.page404(HeaderViewModel.from(Some(request.user))))
      }
    }
  }

  /**
    * 対象のツイートを更新する
    */
  def update(id: Long) = AuthNAction(authService.authenticate) async { implicit request: AuthedRequest[AnyContent] =>
    TweetForm.tweetContentForm
      .bindFromRequest().fold(
        (formWithErrors: Form[TweetContent]) => {
          Future
            .successful(BadRequest(views.html.tweet.edit(
              TweetEditViewModel.from(
                Some(request.user),
                id,
                formWithErrors
              )
            )))
        },
        (data: TweetContent) => {
          for {
            count <- tweetRepository.updateContent(id, data.content)
          } yield {
            count match {
              case 0 =>
                NotFound(views.html.error.page404(HeaderViewModel.from(Some(request.user))))
              case _ =>
                Redirect(routes.TweetController.list())
            }
          }
        }
      )
  }

  /**
    * 対象のデータを削除する
    */
  def delete() = AuthNAction(authService.authenticate) async { implicit request: AuthedRequest[AnyContent] =>
    // requestから直接値を取得するサンプル
    val idOpt = request.body.asFormUrlEncoded.get("id").headOption
    for {
      result <- tweetRepository.delete(
        idOpt.map(_.toLong),
        request.user.id.get
      )
    } yield {
      // 削除対象の有無によって処理を分岐
      result match {
        case 0 =>
          NotFound(views.html.error.page404(HeaderViewModel.from(Some(request.user))))
        case _ =>
          Redirect(routes.TweetController.list())
      }
    }
  }
}
