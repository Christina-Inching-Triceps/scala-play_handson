package model.view

import model.view.HeaderViewModel
import slick.models.{Tweet,User}
import play.api.data.Form
import controllers.tweet.TweetFormData

case class TweetRegisterViewModel(
  headerViewModel: HeaderViewModel,
  form:            Form[TweetFormData]
)

object TweetRegisterViewModel {
  def from(userOpt: Option[User], form: Form[TweetFormData]): TweetRegisterViewModel =
    TweetRegisterViewModel(
      HeaderViewModel.from(userOpt),
      form
    )
}

