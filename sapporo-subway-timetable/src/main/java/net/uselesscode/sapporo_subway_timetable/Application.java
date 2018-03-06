package net.uselesscode.sapporo_subway_timetable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.uselesscode.sapporo_subway_timetable.constant.ColumnConst;
import net.uselesscode.sapporo_subway_timetable.constant.RouteConst;
import net.uselesscode.sapporo_subway_timetable.constant.TargetConst;
import net.uselesscode.sapporo_subway_timetable.entity.Route;
import net.uselesscode.sapporo_subway_timetable.entity.TargetURL;

public class Application {

	private static final int OUTPUT_TYPE_FULL = 1;

	private static final int OUTPUT_TYPE_CD = 2;

	public static void main(String[] args) throws IOException {

		List<Map<String, String>> timetable = new ArrayList<Map<String, String>>();

		// URLリストに設定されたURLに対してスクレイピング実行
		for (TargetURL target : TargetConst.LIST) {
			Route route = getRouteIdFromUrlId(target.getUrlId());
			String station = target.getStationName();
			System.out.println("駅:" + station);
			String url = TargetConst.BaseURL + target.getUrlId() + ".html";
			Document document = Jsoup.connect(url).get();
			// HTMLから方向、平/休日、時刻表の部分を取得
			Elements elements = document.select("#tmp_contents h2,h3, .thListHead, td > p");

			String direction = null;
			String dateType = null;
			String hour = null;
			String[] minutesList;
			for (Element element : elements) {
				String text = element.text().trim().replaceAll("[\\xc2\\xa0]", "");
				String tagName = element.tagName().trim();

				if ("h2".equals(tagName)) {
					direction = text;
					System.out.println("進行方向:" + direction);
				} else if ("h3".equals(tagName)) {
					dateType = text;
					System.out.println("日区分:" + dateType);
				} else if ("th".equals(tagName)) {
					hour = text;
					System.out.println("時:" + hour);
				} else if ("p".equals(tagName)) {
					minutesList = text.split(" ");
					System.out.println("分:" + text);

					for (String minutes : minutesList) {
						Map<String, String> rowMap = new HashMap<String, String>();
						rowMap.put(ColumnConst.ROUTE_ID, route.getRouteId());
						rowMap.put(ColumnConst.ROUTE_NAME, route.getRouteName());
						rowMap.put(ColumnConst.STATION_ID, createStationId(route.getRouteId(), target.getUrlId()));
						rowMap.put(ColumnConst.STATION_NAME, station);
						rowMap.put(ColumnConst.DIRECTION_ID, TargetConst.DIRECTION_MAP.get(direction));
						rowMap.put(ColumnConst.DIRECTION_NAME, direction);
						rowMap.put(ColumnConst.DATE_TYPE_CD, TargetConst.DATE_TYPE_MAP.get(dateType));
						rowMap.put(ColumnConst.DATE_TYPE_NAME, dateType);
						rowMap.put(ColumnConst.HOURS, hour);
						rowMap.put(ColumnConst.MINUTES, minutes);

						timetable.add(rowMap);
					}

					hour = null;
				}
			}

		}
		outputCSV(timetable, OUTPUT_TYPE_FULL);
		outputCSV(timetable, OUTPUT_TYPE_CD);
	}

	/**
	 * URLIDから路線情報取得
	 *
	 * @param urlId
	 * @return
	 */
	public static Route getRouteIdFromUrlId(String urlId) {
		String urlType = urlId.substring(0, 1);
		for (Route route : RouteConst.LIST) {
			if (route.getUrlType().equals(urlType)) {
				return route;
			}
		}
		return null;
	}

	/**
	 * 駅IDを生成
	 *
	 * @param routeId
	 * @param urlId
	 * @return
	 */
	public static String createStationId(String routeId, String urlId) {
		Integer routeIdInt = Integer.parseInt(routeId) * 100;
		Integer stationIdInt = Integer.parseInt(urlId.substring(1));
		String stationId = Integer.toString(routeIdInt + stationIdInt);
		return stationId;
	}

	/**
	 * CSVファイルを吐き出す
	 *
	 * @param timetable
	 * @param outputType
	 */
	public static void outputCSV(List<Map<String, String>> timetable, int outputType) {
		System.out.println("ファイル出力開始");
		FileWriter fileWriter;
		try {
			String fileName = "";
			if (outputType == OUTPUT_TYPE_FULL) {
				fileName = "timetable-full";
			} else if (outputType == OUTPUT_TYPE_CD) {
				fileName = "timetable";
			}
			fileWriter = new FileWriter("./out/" + fileName + ".csv", false);
			PrintWriter printWriter = new PrintWriter(new BufferedWriter(fileWriter));

			// ヘッダー部出力
			if (outputType == OUTPUT_TYPE_FULL) {
				printWriter.print("路線ID,路線名,駅ID,駅名,方面ID,方面名,日区分,日区分名,時,分");
			} else if (outputType == OUTPUT_TYPE_CD) {
				printWriter.print("路線ID,駅ID,方面ID,日区分,時,分");
			}

			for (Map<String, String> map : timetable) {
				printWriter.println();
				printWriter.print(map.get(ColumnConst.ROUTE_ID) + ",");
				if (outputType == OUTPUT_TYPE_FULL) {
					printWriter.print(map.get(ColumnConst.ROUTE_NAME) + ",");
				}

				printWriter.print(map.get(ColumnConst.STATION_ID) + ",");
				if (outputType == OUTPUT_TYPE_FULL) {
					printWriter.print(map.get(ColumnConst.STATION_NAME) + ",");
				}

				printWriter.print(map.get(ColumnConst.DIRECTION_ID) + ",");
				if (outputType == OUTPUT_TYPE_FULL) {
					printWriter.print(map.get(ColumnConst.DIRECTION_NAME) + ",");
				}
				printWriter.print(map.get(ColumnConst.DATE_TYPE_CD) + ",");
				if (outputType == OUTPUT_TYPE_FULL) {
					printWriter.print(map.get(ColumnConst.DATE_TYPE_NAME) + ",");
				}
				printWriter.print(map.get(ColumnConst.HOURS) + ",");
				printWriter.print(map.get(ColumnConst.MINUTES));
			}

			printWriter.close();
			System.out.println("ファイル出力完了");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
