2# tusinhentyo
まだ作成中です！  
参考にする際は注意してください  
通信専攻科課題の変調をする課題作っています。  
依存関係を減らすためにjavaで書いています。pythonは暇があったら入れるかも。  

プロジェクト自体はintellijで管理しているのでintellijで起動すると必要ライブラリなどを自動でダウンロードしてきてくれます。  

きっと[じょにお](https://twitter.com/joniojonijoni)がPython版を実装してくれるに違いない。

# Python版との違い
- 振幅を1に統一
- 信号空間ダイヤグラムをwikipedia準拠（隣り合う符号は1ビットの差になる分布)
- マルチスレッド化で実行時間が数分の一に短縮

# 進捗
|変調|進捗|
|---|---|
bpsk|ぴらがやってくれた
qpsk|出来た
16qam|よくわからんがそれっぽい

# 実行結果
![bpsk](https://raw.githubusercontent.com/Khromium/tusinhentyo/master/res/BPSK2.jpg)
![qpsk](https://raw.githubusercontent.com/Khromium/tusinhentyo/master/res/QPSK2.jpg)
![16qam](https://raw.githubusercontent.com/Khromium/tusinhentyo/master/res/16QAM2.jpg)

# 使用環境
- java8
- intellij

# 使用ライブラリ
本プログラムでは以下のライブラリをmavenから取得して使用しています  
ライセンスなども以下から確認できます。
- [JFreeChart](http://www.jfree.org/jfreechart/)
