package model.view

import model.view.layout.LayoutViewModel
import slick.models.{Tweet,User}

case class TweetListViewModel(
  layout: LayoutViewModel,
  tweets: Seq[Tweet]
)

object TweetListViewModel {

  def from(layout: LayoutViewModel, tweets: Seq[Tweet]): TweetListViewModel =
    TweetListViewModel(layout, tweets)
}

