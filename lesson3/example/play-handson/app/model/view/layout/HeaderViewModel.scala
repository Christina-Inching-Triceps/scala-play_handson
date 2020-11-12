package model.view.layout

import slick.models.User

case class HeaderViewModel(
  loginUser: Option[User] = None
){
  def loggedIn: Boolean = loginUser.isDefined
}

object HeaderViewModel {
  def from(userOpt: Option[User]): HeaderViewModel =
    HeaderViewModel(
      loginUser = userOpt
    )
}


