package model.view

import model.view.HeaderViewModel
import play.api.data.Form
import controllers.LoginForm
import slick.models.User

case class LoginViewModel(
  headerViewModel: HeaderViewModel,
  form:            Form[LoginForm]
)

object LoginViewModel {
  def from(userOpt: Option[User], form: Form[LoginForm]): LoginViewModel =
    LoginViewModel(
      HeaderViewModel.from(userOpt),
      form
    )
}

