package slick.repositories

import java.time.LocalDateTime
import play.api.db.slick.{HasDatabaseConfigProvider,DatabaseConfigProvider}
import javax.inject.{Inject, Singleton}
import slick.jdbc.{JdbcProfile, GetResult}
import scala.concurrent.{Future, ExecutionContext}
import slick.models.Tweet

/**
 * TweetRepository
 * Tweet Tableのデータに対して処理を行う
 */
@Singleton
class TweetRepository @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val query = new TableQuery(tag => new TweetTable(tag))

  // ########## [DBIO Methods] ##########

  /**
    * tweetを全件取得
    */
  def all(): Future[Seq[Tweet]] = db.run(query.result)

  /**
   * idを指定してTweetを取得
   */
  def findById(id: Long): Future[Option[Tweet]] = db.run(
    query.filter(x => x.id  === id).result.headOption
  )

  /**
   * tweetを1件登録する
   */
  def insert(tweet: Tweet): Future[Long]= db.run(
    // returningメソッドを利用することで、このメソッドに指定したデータを登録結果として返却するようにできる
    (query returning query.map(_.id)) += tweet
  )

  /**
   * 対象のtweetを更新する
   */
  def update(tweet: Tweet): Future[Option[Tweet]] = db.run {
    val row = query.filter(_.id === tweet.id)
    for {
      old <- row.result.headOption
      _    = old match {
        case Some(_) => row.update(tweet)
        case None    => DBIO.successful(0)
      }
    } yield old
  }

  /**
   * 対象のTweetの内容を更新する
   */
  def updateContent(id: Long, content: String): Future[Int] = {
    db.run(
      query.filter(_.id === id).map(_.content).update(content)
    )
  }

  /**
   * 対象のデータを削除する
   */
  def delete(id: Long): Future[Int] = delete(Some(id))

  /**
   * 対象のデータを削除する
   */
  def delete(idOpt: Option[Long]) = db.run(
    query.filter(_.id === idOpt).delete
  )


  // ########## [Table Mapping] ##########
  private class TweetTable(_tableTag: Tag) extends Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {

    // Tableとのカラムマッピング
    val id:        Rep[Long]          = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val content:   Rep[String]        = column[String]("content", O.Length(120,varying=true))
    val postedAt:  Rep[LocalDateTime] = column[LocalDateTime]("posted_at")
    val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
    val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")

    // Plain SQLでデータ取得を行う用のマッピング
    implicit def GetResultTweet(implicit e0: GetResult[Long], e1: GetResult[String], e2: GetResult[LocalDateTime]): GetResult[Tweet] = GetResult{
      prs => import prs._
      Tweet.tupled((Some(<<[Long]), <<[String], <<[LocalDateTime], <<[LocalDateTime], <<[LocalDateTime]))
    }

    // model -> db用タプル, dbからのデータ -> modelの変換を記述する処理
    // O.PrimaryKeyはColumnOptionTypeとなるためid.?でidをOptionとして取り扱い可能
    def * = (id.?, content, postedAt, createdAt, updatedAt) <> (Tweet.tupled, Tweet.unapply)

    // Maps whole row to an option. Useful for outer joins.
    def ? = ((
      Rep.Some(id),
      Rep.Some(content),
      Rep.Some(postedAt),
      Rep.Some(createdAt),
      Rep.Some(updatedAt)
    )).shaped.<>(
    { r =>
      import r._;
      _1.map( _=>
          Tweet.tupled((
            Some(_1.get), // モデル側はidがOptionなのでOptionで包んでいる
            _2.get,
            _3.get,
            _4.get,
            _5.get
          ))
    )},
    (_:Any) =>
      throw new Exception("Inserting into ? projection not supported.")
    )
  }
}
