package model.form

import play.api.data.Form
import play.api.data.Forms._

case class LoginForm(
  email:           String,
  password:        String
)

object LoginForm {
  def form = Form(
    mapping(
      "email"            -> nonEmptyText(maxLength = 255),
      "password"         -> nonEmptyText(minLength = 8, maxLength = 72)
    )(LoginForm.apply)(LoginForm.unapply)
  )
}
