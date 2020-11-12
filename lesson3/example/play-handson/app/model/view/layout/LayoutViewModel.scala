package model.view.layout

import slick.models.User


case class LayoutViewModel(
  header: HeaderViewModel,
  footer: FooterViewModel
)

object LayoutViewModel {
  def from(userOpt: Option[User]) =
    new LayoutViewModel(
      HeaderViewModel.from(userOpt),
      FooterViewModel.from()
    )
}

