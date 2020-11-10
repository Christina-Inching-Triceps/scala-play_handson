package model.form

import play.api.data.Form
import play.api.data.Forms._

case class TweetContent(content: String)

object TweetForm {

  // Tweet登録・更新用のFormオブジェクト
  def tweetContentForm = Form(
    // html formのnameがcontentのものを140文字以下の必須文字列に設定する
    mapping(
      "content" -> nonEmptyText(maxLength = 140)
    )(TweetContent.apply)(TweetContent.unapply)
  )
}

