package slick.samples

import java.time.LocalDateTime
import java.time.{LocalDateTime}
import slick.jdbc.{GetResult}

// case classについての説明は省略
// 参考: https://docs.scala-lang.org/ja/tour/case-classes.html
case class TweetV2(
  id:        Option[Long],
  content:   String,
  postedAt:  LocalDateTime = LocalDateTime.now,
  createdAt: LocalDateTime = LocalDateTime.now,
  updatedAt: LocalDateTime = LocalDateTime.now
)

object TweetV2 {
  def apply(
    id:        Option[Long],
    content:   String,
    postedAt:  MySQLDateTime,
    createdAt: MySQLDateTime,
    updatedAt: MySQLDateTime
  ): TweetV2 = {
    TweetV2(
      id, content,
      postedAt.toLocalDateTime,
      createdAt.toLocalDateTime,
      updatedAt.toLocalDateTime
    )
  }
}
