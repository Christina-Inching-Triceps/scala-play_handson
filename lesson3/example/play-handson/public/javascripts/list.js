// DOM読み込みが完了してから処理
document.addEventListener("DOMContentLoaded",function(){
  // HTMLCollectionを配列に変換しつつ削除アイコンを取得
  Array.from(
    document.getElementsByClassName("delete")
    // それぞれのアイコンに削除フォーム実行のonclickイベントを設定
  ).forEach(action => {
    // eventを取得して、クリックされた要素(target)の親要素であるformをsubmitする
    action.addEventListener("click", (e) => {
      e.currentTarget.parentNode.submit();
      // 親要素にある詳細ページへのリンクを止める
      e.stopPropagation();
    });
  });

  // Cardのクリックで詳細ページへ飛ばす
  Array.from(
    document.getElementsByClassName("card")
  ).forEach(card => {
    card.addEventListener("click", (e) => {
      location.href = e.currentTarget.getAttribute("data-href");
    })
  })
});
