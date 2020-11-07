# 実装方針

## execution contextの差し替え、渡し方

- ControllerComponentsを独自実装して、そこにDI or 指定
- ActionBuilder単位でExecutionContextを直接指定
- helpersでcontextごとにfunctionをつけるでも良い

このレベルで変更できればおそらく実運用で困らない
