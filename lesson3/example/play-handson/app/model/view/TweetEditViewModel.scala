package model.view

import model.view.layout.LayoutViewModel
import slick.models.{Tweet,User}
import play.api.data.Form
import model.form.TweetContent

case class TweetEditViewModel(
  layout: LayoutViewModel,
  id:     Long,
  form:   Form[TweetContent]
)

object TweetEditViewModel {
  def from(layout: LayoutViewModel, id: Long, form: Form[TweetContent]): TweetEditViewModel =
    TweetEditViewModel(
      layout,
      id,
      form
    )
}

