package model.view

import model.view.HeaderViewModel
import play.api.data.Form
import controllers.user.UserForm
import slick.models.User

case class UserRegisterViewModel(
  headerViewModel: HeaderViewModel,
  form:            Form[UserForm]
)

object UserRegisterViewModel {
  def from(userOpt: Option[User], form: Form[UserForm]): UserRegisterViewModel =
    UserRegisterViewModel(
      HeaderViewModel.from(userOpt),
      form
    )
}

