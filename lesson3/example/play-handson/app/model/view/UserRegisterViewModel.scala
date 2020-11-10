package model.view

import model.view.HeaderViewModel
import play.api.data.Form
import slick.models.User
import model.form._

case class UserRegisterViewModel(
  headerViewModel: HeaderViewModel,
  form:            Form[SignUp]
)

object UserRegisterViewModel {
  def from(userOpt: Option[User], form: Form[SignUp]): UserRegisterViewModel =
    UserRegisterViewModel(
      HeaderViewModel.from(userOpt),
      form
    )
}

