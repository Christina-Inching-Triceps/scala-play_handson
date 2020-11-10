package model.view

import model.view.HeaderViewModel
import slick.models.{Tweet,User}
import play.api.data.Form
import model.form.TweetContent

case class TweetEditViewModel(
  headerViewModel: HeaderViewModel,
  id:              Long,
  form:            Form[TweetContent]
)

object TweetEditViewModel {
  def from(userOpt: Option[User], id: Long, form: Form[TweetContent]): TweetEditViewModel =
    TweetEditViewModel(
      HeaderViewModel.from(userOpt),
      id,
      form
    )
}

