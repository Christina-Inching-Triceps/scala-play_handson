package model.view

import model.view.layout.LayoutViewModel
import play.api.data.Form
import model.form.LoginForm
import slick.models.User

case class LoginViewModel(
  layout: LayoutViewModel,
  form:   Form[LoginForm]
)

object LoginViewModel {
  def from(layout: LayoutViewModel, form: Form[LoginForm]): LoginViewModel =
    LoginViewModel(
      layout,
      form
    )
}

