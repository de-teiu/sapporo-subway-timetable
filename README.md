# sapporo-subway-timetable
札幌市営地下鉄の時刻表を加工したやーつ

## 説明
札幌市営地下鉄の時刻表データ(HTML)を加工してJSON、CSV形式で出力します。  
Javaで時刻表のページをスクレイピングして上記の形式に加工しているだけです。
出力データは[こちら](https://github.com/de-teiu/sapporo-subway-timetable/tree/master/sapporo-subway-timetable/out)に入ってます。

## 出力ファイル
- timetable-full.json  
 時刻表情報本体。発車時刻の他、路線名や駅名などの名称データも入っている
- timetable-full.csv
 timetable-fullのcsv版
- timetable.json
 timetable-fullから名称データを除いたもの
- timetable.csv
timetable-fullから名称データを除いたもの
- route.json
路線IDと路線名称の定義
- station.json
駅IDと駅名称の定義
- direction.json
方面IDと方面名称(麻生方面とか)の定義
- datetype.json
日種別IDと日種別名の定義(平日or土日祝日)

## 使用データ
[DATA SMART CITY SAPPORO 時刻表（地下鉄・市電）](https://ckan.pf-sapporo.jp/dataset/st_time)
