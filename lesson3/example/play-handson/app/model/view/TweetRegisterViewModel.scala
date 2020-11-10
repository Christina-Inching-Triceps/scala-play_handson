package model.view

import model.view.HeaderViewModel
import slick.models.{Tweet,User}
import play.api.data.Form
import model.form.TweetContent

case class TweetRegisterViewModel(
  headerViewModel: HeaderViewModel,
  form:            Form[TweetContent]
)

object TweetRegisterViewModel {
  def from(userOpt: Option[User], form: Form[TweetContent]): TweetRegisterViewModel =
    TweetRegisterViewModel(
      HeaderViewModel.from(userOpt),
      form
    )
}

