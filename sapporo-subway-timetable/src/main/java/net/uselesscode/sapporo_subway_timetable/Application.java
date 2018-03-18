package net.uselesscode.sapporo_subway_timetable;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
			// System.out.println("駅:" + station);
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
					// System.out.println("進行方向:" + direction);
				} else if ("h3".equals(tagName)) {
					dateType = text;
					// System.out.println("日区分:" + dateType);
				} else if ("th".equals(tagName)) {
					hour = text;
					// System.out.println("時:" + hour);
				} else if ("p".equals(tagName)) {
					minutesList = text.split(" ");
					// System.out.println("分:" + text);

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
		outputTimeTableCSV(timetable, OUTPUT_TYPE_FULL);
		outputTimeTableCSV(timetable, OUTPUT_TYPE_CD);
		outputTimeTableJSON(timetable, OUTPUT_TYPE_FULL);
		outputTimeTableJSON(timetable, OUTPUT_TYPE_CD);

		LinkedHashMap<String, String> stationMap = createStationMap();
		outputMapJSON(stationMap, "station");
		LinkedHashMap<String, String> routeMap = createRouteMap();
		outputMapJSON(routeMap, "route");
		outputMapJSON(TargetConst.DATE_TYPE_MAP, "datetype");
		outputMapJSON(TargetConst.DIRECTION_MAP, "direction");
	}

	/**
	 * 駅データのMAPオブジェクト作成
	 *
	 * @return
	 */
	private static LinkedHashMap<String, String> createStationMap() {
		LinkedHashMap<String, String> stationMap = new LinkedHashMap<String, String>();
		TargetConst.LIST.forEach(item -> {
			Route route = getRouteIdFromUrlId(item.getUrlId());
			String stationId = createStationId(route.getRouteId(), item.getUrlId());
			stationMap.put(stationId, item.getStationName());
		});
		return stationMap;
	}

	/**
	 * 路線データのMAPオブジェクト作成
	 *
	 * @return
	 */
	private static LinkedHashMap<String, String> createRouteMap() {
		LinkedHashMap<String, String> routeMap = new LinkedHashMap<String, String>();
		RouteConst.LIST.forEach(item -> {
			routeMap.put(item.getRouteId(), item.getRouteName());
		});
		return routeMap;
	}

	/**
	 * URLIDから路線情報取得
	 *
	 * @param urlId
	 * @return
	 */
	private static Route getRouteIdFromUrlId(String urlId) {
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
	private static String createStationId(String routeId, String urlId) {
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
	private static void outputTimeTableCSV(List<Map<String, String>> timetable, int outputType) {
		System.out.println("csvファイル出力開始");
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
			System.out.println("csvファイル出力完了");

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * JSONファイルを吐き出す
	 *
	 * @param timetable
	 * @param outputType
	 */
	private static void outputTimeTableJSON(List<Map<String, String>> timetable, int outputType) {
		System.out.println("jsonファイル出力開始");
		JSONArray jsonArray = new JSONArray();
		for (Map<String, String> map : timetable) {
			JSONObject jsonObj = new JSONObject();
			for (Map.Entry<String, String> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				if (outputType == OUTPUT_TYPE_CD) {
					if (ColumnConst.ROUTE_NAME.equals(key) || ColumnConst.STATION_NAME.equals(key)
							|| ColumnConst.DIRECTION_NAME.equals(key) || ColumnConst.DATE_TYPE_NAME.equals(key)) {
						continue;
					}
				}
				try {
					jsonObj.put(key, value);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			jsonArray.put(jsonObj);
		}

		FileWriter fileWriter = null;
		String fileName = "";
		if (outputType == OUTPUT_TYPE_FULL) {
			fileName = "timetable-full";
		} else if (outputType == OUTPUT_TYPE_CD) {
			fileName = "timetable";
		}
		try {
			fileWriter = new FileWriter("./out/" + fileName + ".json", false);
			jsonArray.write(fileWriter);
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("jsonファイル出力完了");
	}

	/**
	 *
	 * @param map
	 * @param fileName
	 */
	private static void outputMapJSON(Map<String, String> map, String fileName) {
		System.out.println("jsonファイル出力開始");
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();
			try {
				jsonObj.put(key, value);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		jsonArray.put(jsonObj);

		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter("./out/" + fileName + ".json", false);
			jsonArray.write(fileWriter);
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("jsonファイル出力完了");
	}

}
