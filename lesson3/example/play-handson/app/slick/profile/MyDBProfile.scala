package slick.profile

import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

/* LocalDateTimeをプロダクトに適した形に処理できるようにProfile設定を独自に拡張 */
trait MyDBProfile extends slick.jdbc.JdbcProfile with slick.jdbc.MySQLProfile {
  import java.sql.{PreparedStatement, ResultSet}
  import slick.ast.FieldSymbol

  @inline
  private[this] def stringToMySqlString(value : String) : String = {
    value match {
      case null => "NULL"
      case _ =>
        val sb = new StringBuilder
        sb append '\''
        for(c <- value) c match {
          case '\'' => sb append "\\'"
          case '"' => sb append "\\\""
          case 0 => sb append "\\0"
          case 26 => sb append "\\Z"
          case '\b' => sb append "\\b"
          case '\n' => sb append "\\n"
          case '\r' => sb append "\\r"
          case '\t' => sb append "\\t"
          case '\\' => sb append "\\\\"
          case _ => sb append c
        }
        sb append '\''
        sb.toString
    }
  }

  override val columnTypes = new JdbcTypes

  // Customise the types...
  class JdbcTypes extends super.JdbcTypes {

    // PostgresのProfileを参考にミリ秒も含めて対応できるformatterを実装
    private[this] val formatter = {
      new DateTimeFormatterBuilder()
        .append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        .optionalStart()
        .appendFraction(ChronoField.NANO_OF_SECOND,0,9,true)
        .optionalEnd()
        .toFormatter()
    }

    override val localDateTimeType : LocalDateTimeJdbcType = new LocalDateTimeJdbcType {
      override def sqlType : Int = {
        java.sql.Types.VARCHAR
      }

      override def setValue(v: LocalDateTime, p: PreparedStatement, idx: Int) : Unit = {
        p.setString(idx, if (v == null) null else v.toString)
      }
      override def getValue(r: ResultSet, idx: Int) : LocalDateTime = {
        r.getString(idx) match {
          case null       => null
          // 文字列から日付型にパースできるようにparseにformatterを渡す
          case dateString => LocalDateTime.parse(dateString, formatter)
        }
      }
      override def updateValue(v: LocalDateTime, r: ResultSet, idx: Int) = {
        r.updateString(idx, if (v == null) null else v.toString)
      }
      override def valueToSQLLiteral(value: LocalDateTime) : String = {
        stringToMySqlString(value.toString)
      }
    }
  }
}

object MyDBProfile extends MyDBProfile

