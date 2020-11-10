package com.example
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import java.time.{LocalDateTime}
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Tweet.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Tweet
   *  @param id Database column id SqlType(BIGINT), AutoInc, PrimaryKey
   *  @param content Database column content SqlType(VARCHAR), Length(120,true)
   *  @param postedAt Database column posted_at SqlType(DATETIME)
   *  @param createdAt Database column created_at SqlType(DATETIME)
   *  @param updatedAt Database column updated_at SqlType(DATETIME) */
  case class TweetRow(id: Long, content: String, postedAt: LocalDateTime, createdAt: LocalDateTime, updatedAt: LocalDateTime)
  /** GetResult implicit for fetching TweetRow objects using plain SQL queries */
  implicit def GetResultTweetRow(implicit e0: GR[Long], e1: GR[String], e2: GR[LocalDateTime]): GR[TweetRow] = GR{
    prs => import prs._
    TweetRow.tupled((<<[Long], <<[String], <<[LocalDateTime], <<[LocalDateTime], <<[LocalDateTime]))
  }
  /** Table description of table tweet. Objects of this class serve as prototypes for rows in queries. */
  class Tweet(_tableTag: Tag) extends profile.api.Table[TweetRow](_tableTag, Some("twitter_clone"), "tweet") {
    def * = (id, content, postedAt, createdAt, updatedAt) <> (TweetRow.tupled, TweetRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = ((Rep.Some(id), Rep.Some(content), Rep.Some(postedAt), Rep.Some(createdAt), Rep.Some(updatedAt))).shaped.<>({r=>import r._; _1.map(_=> TweetRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column content SqlType(VARCHAR), Length(120,true) */
    val content: Rep[String] = column[String]("content", O.Length(120,varying=true))
    /** Database column posted_at SqlType(DATETIME) */
    val postedAt: Rep[LocalDateTime] = column[LocalDateTime]("posted_at")
    /** Database column created_at SqlType(DATETIME) */
    val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
    /** Database column updated_at SqlType(DATETIME) */
    val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")
  }
  /** Collection-like TableQuery object for table Tweet */
  lazy val Tweet = new TableQuery(tag => new Tweet(tag))
}
