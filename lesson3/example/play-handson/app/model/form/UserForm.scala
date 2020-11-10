package model.form

import play.api.data.Form
import play.api.data.Forms._

case class SignUp(
  name:            String,
  email:           String,
  password:        String,
  confirmPassword: String
)

object UserForm {
  def signUpForm: Form[SignUp] = Form(
    mapping(
      "name"             -> nonEmptyText(maxLength = 255),
      "email"            -> nonEmptyText(maxLength = 255),
      "password"         -> nonEmptyText(minLength = 8, maxLength = 72),
      "confirm_password" -> nonEmptyText
    )(SignUp.apply)(SignUp.unapply).verifying(
      "error.passwordDisagreement",
      v => v.password == v.confirmPassword
    )
  )
}

