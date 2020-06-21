package slick.samples

import java.time.LocalDateTime
import slick.jdbc.{GetResult}
import slick.models.Tweet

/* def *のtupleでマッピングをするサンプル実装 */
object SlickMappedTweetTableV1 extends {
  val profile    = slick.jdbc.MySQLProfile
} with SlickMappedTweetTableV1

trait SlickMappedTweetTableV1 extends LocalDateTimeColumMapper {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._

  /* Slick3.3ではDATETIME, TIMESTAMPなどをStringで受け取るため、モデルとの相互変換部分で吸収する */
  implicit def GetResultTweet(implicit e0: GetResult[Long], e1: GetResult[String], e2: GetResult[MySQLDateTime]): GetResult[Tweet] = GetResult{
    prs => import prs._
    Tweet.tupled((
      <<[Option[Long]],
      <<[String],
      <<[MySQLDateTime].toLocalDateTime,
      <<[MySQLDateTime].toLocalDateTime,
      <<[MySQLDateTime].toLocalDateTime
    ))
  }

  /* Slick3.3ではDATETIME, TIMESTAMPなどをStringで受け取るため、モデルとの相互変換部分で吸収する */
 class SlickMappedTweetTableV1(_tableTag: Tag) extends profile.api.Table[Tweet](_tableTag, Some("twitter_clone"), "tweet") {

    def * = (id, content, postedAt, createdAt, updatedAt) <> (
      (x: (Long, String, MySQLDateTime, MySQLDateTime, MySQLDateTime)) => {
        Tweet(
          Some(x._1),
          x._2,
          x._3.toLocalDateTime,
          x._4.toLocalDateTime,
          x._5.toLocalDateTime
        )
      },
      (tweet: Tweet) => {
        Some((
          tweet.id.getOrElse(0L),
          tweet.content,
          MySQLDateTime(tweet.postedAt.toString),
          MySQLDateTime(tweet.createdAt.toString),
          MySQLDateTime(tweet.updatedAt.toString)
        ))
      }
    )

    def ? = ((Rep.Some(id), Rep.Some(content), Rep.Some(postedAt), Rep.Some(createdAt), Rep.Some(updatedAt))).shaped.<>({r=>import r._; _1.map(_=> Tweet.tupled((Option(_1.get), _2.get, MySQLDateTime(_3.get.toString).toLocalDateTime, MySQLDateTime(_4.get.toString).toLocalDateTime, MySQLDateTime(_5.get.toString).toLocalDateTime)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    val id:        Rep[Long]          = column[Long]("id", O.AutoInc, O.PrimaryKey)
    val content:   Rep[String]        = column[String]("content", O.Length(120,varying=true))
    val postedAt:  Rep[MySQLDateTime] = column[MySQLDateTime]("posted_at")
    val createdAt: Rep[MySQLDateTime] = column[MySQLDateTime]("created_at")
    val updatedAt: Rep[MySQLDateTime] = column[MySQLDateTime]("updated_at")
  }

  lazy val query = new TableQuery(tag => new SlickMappedTweetTableV1(tag))
}
