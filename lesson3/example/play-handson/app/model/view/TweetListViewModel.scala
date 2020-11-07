package model.view

import model.view.HeaderViewModel
import slick.models.{Tweet,User}

case class TweetListViewModel(
  headerViewModel: HeaderViewModel,
  tweets:          Seq[Tweet]
)

object TweetListViewModel {
  def from(userOpt: Option[User], tweets: Seq[Tweet]): TweetListViewModel =
    TweetListViewModel(
      HeaderViewModel.from(userOpt),
      tweets
    )
}

