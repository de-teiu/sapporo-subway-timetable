package net.uselesscode.sapporo_subway_timetable;

import java.io.IOException;
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
				if ("h2".equals(element.tagName())) {
					direction = element.text();
					System.out.println("進行方向:" + direction);
				} else if ("h3".equals(element.tagName())) {
					dateType = element.text();
					System.out.println("日区分:" + dateType);
				} else if ("th".equals(element.tagName())) {
					hour = element.text();
					System.out.println("時:" + hour);
				} else if ("p".equals(element.tagName())) {
					minutesList = element.text().split(" ");
					System.out.println("分:" + element.text());
					hour = null;

					for (String minutes : minutesList) {
						Map<String, String> rowMap = new HashMap<String, String>();
						rowMap.put(ColumnConst.ROUTE_ID, route.getRouteId());
						rowMap.put(ColumnConst.ROUTE_NAME, route.getRouteName());
						rowMap.put(ColumnConst.STATION_ID, createStationId(route.getRouteId(), target.getUrlId()));
						rowMap.put(ColumnConst.STATION_NAME, station);
					}
				}
			}
		}
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
		Integer stationIdInt = Integer.parseInt(urlId.substring(1, 2));
		String stationId = Integer.toString(routeIdInt + stationIdInt);
		return stationId;
	}
}
