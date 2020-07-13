package slick.repositories

import java.time.LocalDateTime
import play.api.db.slick.{HasDatabaseConfigProvider,DatabaseConfigProvider}
import javax.inject.{Inject, Singleton}
import slick.jdbc.{JdbcProfile, GetResult}
import scala.concurrent.{Future, ExecutionContext}
import slick.models.User

/**
 * UserRepository
 * User Tableのデータに対して処理を行う
 */
@Singleton
class UserRepository @Inject()(
  protected val dbConfigProvider: DatabaseConfigProvider
)(implicit ec: ExecutionContext)
extends HasDatabaseConfigProvider[JdbcProfile] {
  import profile.api._

  private val query = new TableQuery(tag => new UserTable(tag))

  // ########## [DBIO Methods] ##########

  /**
    * userを全件取得
    */
  def all(): Future[Seq[User]] = db.run(query.result)

  /**
   * idを指定してUserを取得
   */
  def findById(id: Long): Future[Option[User]] = db.run(
    query.filter(x => x.id  === id).result.headOption
  )

  /**
   * emailを指定してユーザを取得
   */
  def findByEmail(email: String): Future[Option[User]] = db.run {
    query.filter(x => x.email  === email).result.headOption
  }

  /**
   * userを1件登録する
   */
  def insert(user: User): Future[Long]= db.run(
    // returningメソッドを利用することで、このメソッドに指定したデータを登録結果として返却するようにできる
    (query returning query.map(_.id)) += user
  )

  /**
   * 対象のuserを更新する
   */
  def update(user: User): Future[Option[User]] = db.run {
    val row = query.filter(_.id === user.id)
    for {
      old <- row.result.headOption
      _    = old match {
        case Some(_) => row.update(user)
        case None    => DBIO.successful(0)
      }
    } yield old
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
  private class UserTable(_tableTag: Tag) extends Table[User](_tableTag, Some("twitter_clone"), "user") {

    // Tableとのカラムマッピング
    val id:        Rep[Long]          = column[Long]         ("id",       O.AutoInc, O.PrimaryKey)
    val name:      Rep[String]        = column[String]       ("name",     O.Length(255,varying=true))
    val email:     Rep[String]        = column[String]       ("email",    O.Length(255,varying=true))
    val password:  Rep[String]        = column[String]       ("password", O.Length(60, varying=true))
    val createdAt: Rep[LocalDateTime] = column[LocalDateTime]("created_at")
    val updatedAt: Rep[LocalDateTime] = column[LocalDateTime]("updated_at")

    // Plain SQLでデータ取得を行う用のマッピング
    implicit def GetResultUser(implicit e0: GetResult[Long], e1: GetResult[String], e2: GetResult[LocalDateTime]): GetResult[User] = GetResult{
      prs => import prs._
      User.tupled((Some(<<[Long]), <<[String], <<[String], <<[String], <<[LocalDateTime], <<[LocalDateTime]))
    }

    // model -> db用タプル, dbからのデータ -> modelの変換を記述する処理
    // O.PrimaryKeyはColumnOptionTypeとなるためid.?でidをOptionとして取り扱い可能
    def * = (id.?, name, email, password, createdAt, updatedAt) <> (User.tupled, User.unapply)

    // Maps whole row to an option. Useful for outer joins.
    def ? = ((
      Rep.Some(id),
      Rep.Some(name),
      Rep.Some(email),
      Rep.Some(password),
      Rep.Some(createdAt),
      Rep.Some(updatedAt)
    )).shaped.<>(
    { r =>
      import r._;
      _1.map( _=>
          User.tupled((
            Some(_1.get), // モデル側はidがOptionなのでOptionで包んでいる
            _2.get,
            _3.get,
            _4.get,
            _5.get,
            _6.get
          ))
    )},
    (_:Any) =>
      throw new Exception("Inserting into ? projection not supported.")
    )
  }
}
