package model.view

import model.view.HeaderViewModel
import slick.models.{Tweet,User}
import play.api.data.Form
import controllers.tweet.TweetFormData

case class TweetEditViewModel(
  headerViewModel: HeaderViewModel,
  id:              Long,
  form:            Form[TweetFormData]
)

object TweetEditViewModel {
  def from(userOpt: Option[User], id: Long, form: Form[TweetFormData]): TweetEditViewModel =
    TweetEditViewModel(
      HeaderViewModel.from(userOpt),
      id,
      form
    )
}

