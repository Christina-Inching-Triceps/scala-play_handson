package model.view

import model.view.HeaderViewModel
import slick.models.{Tweet,User}

case class TweetShowViewModel(
  headerViewModel: HeaderViewModel,
  tweet:           Tweet
)

object TweetShowViewModel {
  def from(userOpt: Option[User], tweet: Tweet): TweetShowViewModel =
    TweetShowViewModel(
      HeaderViewModel.from(userOpt),
      tweet
    )
}

