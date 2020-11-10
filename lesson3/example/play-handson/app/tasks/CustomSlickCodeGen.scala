package com.example

import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator
import slick.jdbc.MySQLProfile
import slick.jdbc.MySQLProfile.api.Database
import java.time.LocalDateTime
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Failure}
import scala.concurrent.Await
import scala.concurrent.duration.Duration


object CustomSlickCodeGen extends App {
  // typesafe configを利用してapplication.confをロード
  val config      = ConfigFactory.load()
  val defaultPath = "slick.dbs.default"

  // 末尾の$を削除
  val profile   = config.getString(s"$defaultPath.profile").dropRight(1)
  val driver    = config.getString(s"$defaultPath.db.driver")
  val url       = config.getString(s"$defaultPath.db.url")
  val user      = config.getString(s"$defaultPath.db.user")
  val password  = config.getString(s"$defaultPath.db.password")

  // pathが別なので直接呼び出し
  val outputDir = config.getString("slick.codegen.outputDir")
  val pkg       = config.getString("application.package")

  val db  = Database.forURL(
    url      = this.url,
    driver   = this.driver,
    user     = this.user,
    password = this.password
  )

  // evolutions用のテーブルを対象から外す
  val ignoreTables        = Seq("play_evolutions")
  val codegenTargetTables = MySQLProfile.createModel(Some(
    MySQLProfile.defaultTables.map(
      _.filter(table => !ignoreTables.contains(table.name.name.toLowerCase))
    )
  ))

  // モデルを生成したい対象を渡す
  val modelFuture = db.run(codegenTargetTables)

  // CustomCodeGeneratorを生成しつつ、writeToFileで書き出す
  val codegenFuture = modelFuture.map(model => new SourceCodeGenerator(model) {
    // LocalDateTimeのimportを追加
    override def code = "import java.time.{LocalDateTime}" + "\n" + super.code

    // override table generator
    override def Table = new Table(_){
      // disable entity class generation for tables with more than 22 columns
      override def hugeClassEnabled = false

      override def Column = new Column(_){
        // datetimeはデファオルトでjava.sql.Timestamp型になるので、LocalDateTimeに書き換え
        override def rawType = model.tpe match {
          case "java.sql.Timestamp" => "LocalDateTime"
          case _                    => super.rawType
        }
      }
    }
  }.writeToFile(profile, outputDir, pkg))

  // 処理が完了するまで待つ
  Await.result(codegenFuture, Duration.Inf)
}

