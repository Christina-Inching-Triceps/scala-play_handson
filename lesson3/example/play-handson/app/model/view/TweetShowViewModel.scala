package model.view

import model.view.layout.LayoutViewModel
import slick.models.{Tweet,User}

case class TweetShowViewModel(
  layout: LayoutViewModel,
  tweet:  Tweet
)

object TweetShowViewModel {
  def from(layout: LayoutViewModel, tweet: Tweet): TweetShowViewModel =
    TweetShowViewModel(
      layout,
      tweet
    )
}

