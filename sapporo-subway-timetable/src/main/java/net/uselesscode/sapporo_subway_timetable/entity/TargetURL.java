package net.uselesscode.sapporo_subway_timetable.entity;

public class TargetURL {

	/** 時刻表URL_ID */
	private String urlId;

	/** 駅名 */
	private String stationName;

	public TargetURL(String urlId, String stationName) {
		this.urlId = urlId;
		this.stationName = stationName;
	}

	/**
	 * @return urlId
	 */
	public String getUrlId() {
		return urlId;
	}

	/**
	 * @return stationName
	 */
	public String getStationName() {
		return stationName;
	}

}
