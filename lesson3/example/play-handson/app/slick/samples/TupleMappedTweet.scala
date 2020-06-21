package slick.samples

import java.time.LocalDateTime
import slick.jdbc.{GetResult}
import java.time.format.DateTimeFormatter
import slick.models.Tweet

/* def *のtupleでマッピングをするサンプル実装 */
object TupleMappedTweetTable extends {
  val profile    = slick.jdbc.MySQLProfile
} with TupleMappedTweetTable

trait TupleMappedTweetTable {
  val profile: slick.jdbc.JdbcProfile
  val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

  import profile.api._

  /* Slick3.3ではDATETIME, TIMESTAMPなどをStringで受け取るため、モデルとの相互変換部分で吸収する */
  implicit def GetResultTweet(implicit e0: GetResult[Long], e1: GetResult[String]): GetResult[Tweet] = GetResult{
    prs => import prs._
    Tweet.tupled((
      <<[Option[Long]],
      <<[String],
      LocalDateTime.parse(<<[String], format),
      LocalDateTime.parse(<<[String], format),
      LocalDateTime.parse(<<[String], format)
    ))
  }

  /* Slick3.3ではDATETIME, TIMESTAMPなどをStringで受け取るため、モデルとの相互変換部分で吸収する */
  class TupleMappedTweetTable(_tableTag: Tag) extends profile.api.Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {
    def * = (id, content, postedAt, createdAt, updatedAt) <> (
      (x: (Long, String, String, String, String)) => {
        Tweet(
          Some(x._1),
          x._2,
          LocalDateTime.parse(x._3, format),
          LocalDateTime.parse(x._4, format),
          LocalDateTime.parse(x._5, format)
        )
      },
      (tweet: Tweet) => {
        Some((tweet.id.getOrElse(0L), tweet.content, tweet.postedAt.toString, tweet.createdAt.toString, tweet.updatedAt.toString))
      }
    )

    def ? = ((Rep.Some(id), Rep.Some(content), Rep.Some(postedAt), Rep.Some(createdAt), Rep.Some(updatedAt))).shaped.<>({r=>import r._; _1.map(_=> Tweet.tupled((Option(_1.get), _2.get, LocalDateTime.parse(_3.get, format), LocalDateTime.parse(_4.get, format), LocalDateTime.parse(_5.get, format))))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    val id:        Rep[Long]   = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val content:   Rep[String] = column[String]("content", O.Length(120,varying=true))
    val postedAt:  Rep[String] = column[String]("posted_at")
    val createdAt: Rep[String] = column[String]("created_at")
    val updatedAt: Rep[String] = column[String]("updated_at")
  }

  lazy val query = new TableQuery(tag => new TupleMappedTweetTable(tag))
}
