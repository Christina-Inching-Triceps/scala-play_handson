package slick.samples

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/* MySQLDateTimeを直接マッピング対象にできないので、mappingに利用するクラスを作成する */
case class MySQLDateTime(v: String) {
  def toLocalDateTime: LocalDateTime = LocalDateTime.parse(v, MySQLDateTime.format)
}

object MySQLDateTime {
  val format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
  def apply(time: LocalDateTime): MySQLDateTime = MySQLDateTime(time.format(format))
}


trait LocalDateTimeColumMapper {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._

  implicit lazy val localDateTimeMapper = MappedColumnType.base[MySQLDateTime, String] (
    { ldt => ldt.v },
    { str => MySQLDateTime(str)}
  )
}
