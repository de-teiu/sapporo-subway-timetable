package net.uselesscode.sapporo_subway_timetable.entity;

public class Route {

	/** URLタイプ */
	private String urlType;

	/** 路線ID */
	private String routeId;

	/** 路線名 */
	private String routeName;

	/**
	 * コンストラクタ
	 *
	 * @param urlType
	 * @param routeId
	 * @param routeName
	 */
	public Route(String urlType, String routeId, String routeName) {
		this.urlType = urlType;
		this.routeId = routeId;
		this.routeName = routeName;
	}

	/**
	 * @return urlType
	 */
	public String getUrlType() {
		return urlType;
	}

	/**
	 * @return routeId
	 */
	public String getRouteId() {
		return routeId;
	}

	/**
	 * @return routeName
	 */
	public String getRouteName() {
		return routeName;
	}
}
