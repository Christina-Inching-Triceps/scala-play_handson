# 実装方針

## execution contextの差し替え、渡し方

- ControllerComponentsを独自実装して、そこにDI or 指定
- ActionBuilder単位でExecutionContextを直接指定
- helpersでcontextごとにfunctionをつけるでも良い

このレベルで変更できればおそらく実運用で困らない

## ファイル構成

実際のTwitterを踏襲しようとすると現状の作りではカオスになる。
特に/homeと/{username}の区別を考えると、TweetControllerなどは命名が広範囲すぎる

## 共通layoutの管理の仕方

現状各Actionごとにデータセットアップが必要
headerのデータが変更されると全Actionで更新作業が必要になるため、あまりいい設計じゃないと思う


## modelイメージ

case class TweetContentViewModel(
  displayName: String,
  accountId:   String,
  postedAt:    LocalDate,
  content:     String,
  media:       Seq[Media],
  tags:        Seq[Tag],
)

case class TweetViewModel(
  content: TweetContentViewModel,
  menu: TweetMenuViewModel,
  
)

case class ProfileTweetMenuViewModel() extends TweetMenuViewModel
case class HomeTweetMenuViewModel() extends TweetMenuViewModel
case class DefaultTweetMenuViewModel() extends TweetMenuViewModel
trait TweetMenuViewModel {
  def items: Seq[MenuItemViewModel]
}

case class MenuItemViewModel(
  icon:  URL,
  titel: String,
  link:  Caller,
  method: HttpMethod,
  params: Map[String, String]
)
