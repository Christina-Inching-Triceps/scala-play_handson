package model.view.layout

case class FooterViewModel(){
  def copylight = "© christina.inching.triceps"
}

object FooterViewModel {
  def from(): FooterViewModel =
    FooterViewModel()
}


