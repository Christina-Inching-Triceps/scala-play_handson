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

case class TweetFormData(content: String)

/**
  * @SingletonでPlayFrameworkの管理下でSingletonオブジェクトとして本クラスを扱う指定をする
  * @Injectでconstructorの引数をDIする
  * BaseControllerにはprotected の controllerComponentsが存在するため、そこに代入されている。
  * controllerComponentsがActionメソッドを持つため、Actionがコールできる
  *   ActionはcontrollerComponents.actionBuilderと同じ
  */
@Singleton
class TweetController @Inject()(
  val controllerComponents: ControllerComponents,
  tweetRepository:          TweetRepository
)(implicit ec: ExecutionContext)
extends BaseController
with I18nSupport {

  // Tweet登録用のFormオブジェクト
  val form = Form(
    // html formのnameがcontentのものを140文字以下の必須文字列に設定する
    mapping(
      "content" -> nonEmptyText(maxLength = 140)
    )(TweetFormData.apply)(TweetFormData.unapply)
  )

  /**
    * Tweetを一覧表示
    *   Action.asyncとすることでreturnの型としてFuture[Result]を受け取れるように修正
    */
  def list() =  Action async { implicit request: Request[AnyContent] =>
    // DBから値を取得してreturnするように修正
    for {
      results <- tweetRepository.all()
    } yield {
      Ok(views.html.tweet.list(results))
    }
  }

  /**
    * 対象IDのTweet詳細を表示
    */
  def show(id: Long) = Action async { implicit request: Request[AnyContent] =>
    // idが存在して、値が一致する場合にfindが成立
    for {
      tweetOpt <- tweetRepository.findById(id)
    } yield {
      tweetOpt match {
        case Some(tweet) => Ok(views.html.tweet.show(tweet))
        case None        => NotFound(views.html.error.page404())
      }
    }
  }

  /**
    * 登録画面の表示用
    */
  def register() = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.tweet.store(form))
  }

  /**
    * 登録処理実を行う
    */
   def store() = Action async { implicit request: Request[AnyContent] =>
     // foldでデータ受け取りの成功、失敗を分岐しつつ処理が行える
     form.bindFromRequest().fold(
       // 処理が失敗した場合に呼び出される関数
       (formWithErrors: Form[TweetFormData]) => {
         Future.successful(BadRequest(views.html.tweet.store(formWithErrors)))
       },
       // 処理が成功した場合に呼び出される関数
       (tweetFormData: TweetFormData) => {
         for {
           // データを登録。returnのidは不要なので捨てる
           _ <- tweetRepository.insert(Tweet(None, tweetFormData.content))
         } yield {
           Redirect(routes.TweetController.list())
         }
       }
     )
   }

  /**
    * 編集画面を開く
    */
  def edit(id: Long) = Action async { implicit request: Request[AnyContent] =>
    for {
      tweetOpt <- tweetRepository.findById(id)
   } yield {
     tweetOpt match {
       case Some(tweet) =>
         Ok(views.html.tweet.edit(
           // データを識別するためのidを渡す
           id,
           // fillでformに値を詰める
           form.fill(TweetFormData(tweet.content))
         ))
       case None        =>
         NotFound(views.html.error.page404())
     }
   }
  }

  /**
    * 対象のツイートを更新する
    */
  def update(id: Long) = Action async { implicit request: Request[AnyContent] =>
    form.bindFromRequest().fold(
      (formWithErrors: Form[TweetFormData]) => {
        Future.successful(BadRequest(views.html.tweet.edit(id, formWithErrors)))
      },
      (data: TweetFormData) => {
        for {
          count <- tweetRepository.updateContent(id, data.content)
        } yield {
          count match {
            case 0 => NotFound(views.html.error.page404())
            case _ => Redirect(routes.TweetController.list())
          }
        }
      }
      )
  }

  /**
   * 対象のデータを削除する
   */
  def delete() = Action async { implicit request: Request[AnyContent] =>
    // requestから直接値を取得するサンプル
    val idOpt = request.body.asFormUrlEncoded.get("id").headOption
    for {
      result <- tweetRepository.delete(idOpt.map(_.toLong))
    } yield {
      // 削除対象の有無によって処理を分岐
      result match {
        case 0 => NotFound(views.html.error.page404())
        case _ => Redirect(routes.TweetController.list())
      }
    }
  }
}
