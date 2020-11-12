package model.view

import model.view.layout.LayoutViewModel
import slick.models.{Tweet,User}
import play.api.data.Form
import model.form.TweetContent

case class TweetRegisterViewModel(
  layout: LayoutViewModel,
  form:   Form[TweetContent]
)

object TweetRegisterViewModel {
  def from(layout: LayoutViewModel, form: Form[TweetContent]): TweetRegisterViewModel =
    TweetRegisterViewModel(
      layout,
      form
    )
}

