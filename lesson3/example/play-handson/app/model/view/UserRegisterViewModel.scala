package model.view

import model.view.layout.LayoutViewModel
import play.api.data.Form
import slick.models.User
import model.form._

case class UserRegisterViewModel(
  layout: LayoutViewModel,
  form:   Form[SignUp]
)

object UserRegisterViewModel {
  def from(layout: LayoutViewModel, form: Form[SignUp]): UserRegisterViewModel =
    UserRegisterViewModel(
      layout,
      form
    )
}

