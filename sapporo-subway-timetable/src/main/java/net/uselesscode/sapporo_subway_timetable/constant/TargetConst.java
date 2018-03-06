package net.uselesscode.sapporo_subway_timetable.constant;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.uselesscode.sapporo_subway_timetable.entity.TargetURL;

public class TargetConst {

	/** ベースURL */
	public static final String BaseURL = "http://www.city.sapporo.jp/st/subway/route_time/h26/";

	/** ID-駅名リスト */
	public static final List<TargetURL> LIST = Collections.unmodifiableList(new LinkedList<TargetURL>() {
		{
			add(new TargetURL("n01", "麻生"));
			add(new TargetURL("n02", "北34条"));
			add(new TargetURL("n03", "北24条"));
			add(new TargetURL("n04", "北18条"));
			add(new TargetURL("n05", "北12条"));
			add(new TargetURL("n06", "さっぽろ"));
			add(new TargetURL("n07", "大通"));
			add(new TargetURL("n08", "すすきの"));
			add(new TargetURL("n09", "中島公園"));
			add(new TargetURL("n10", "幌平橋"));
			add(new TargetURL("n11", "中の島"));
			add(new TargetURL("n12", "平岸"));
			add(new TargetURL("n13", "南平岸"));
			add(new TargetURL("n14", "澄川"));
			add(new TargetURL("n15", "自衛隊前"));
			add(new TargetURL("n16", "真駒内"));
			add(new TargetURL("t01", "宮の沢"));
			add(new TargetURL("t02", "発寒南"));
			add(new TargetURL("t03", "琴似"));
			add(new TargetURL("t04", "二十四軒"));
			add(new TargetURL("t05", "西28丁目"));
			add(new TargetURL("t06", "円山公園"));
			add(new TargetURL("t07", "西18丁目"));
			add(new TargetURL("t08", "西11丁目"));
			add(new TargetURL("t09", "大通"));
			add(new TargetURL("t10", "バスセンター前"));
			add(new TargetURL("t11", "菊水"));
			add(new TargetURL("t12", "東札幌"));
			add(new TargetURL("t13", "白石"));
			add(new TargetURL("t14", "南郷7丁目"));
			add(new TargetURL("t15", "南郷13丁目"));
			add(new TargetURL("t16", "南郷18丁目"));
			add(new TargetURL("t17", "大谷地"));
			add(new TargetURL("t18", "ひばりが丘"));
			add(new TargetURL("t19", "新さっぽろ"));
			add(new TargetURL("h01", "栄町"));
			add(new TargetURL("h02", "新道東"));
			add(new TargetURL("h03", "元町"));
			add(new TargetURL("h04", "環状通東"));
			add(new TargetURL("h05", "東区役所前"));
			add(new TargetURL("h06", "北13条東"));
			add(new TargetURL("h07", "さっぽろ"));
			add(new TargetURL("h08", "大通"));
			add(new TargetURL("h09", "豊水すすきの"));
			add(new TargetURL("h10", "学園前"));
			add(new TargetURL("h11", "豊平公園"));
			add(new TargetURL("h12", "美園"));
			add(new TargetURL("h13", "月寒中央"));
			add(new TargetURL("h14", "福住"));

		}
	});

	public static final Map<String, String> DIRECTION_MAP = Collections.unmodifiableMap(new HashMap<String, String>() {
		{
			put("麻生方面", "1");
			put("真駒内方面", "2");
			put("新さっぽろ方面", "3");
			put("宮の沢方面", "4");
			put("福住方面", "5");
			put("栄町方面", "6");

		}
	});

	public static final Map<String, String> DATE_TYPE_MAP = Collections.unmodifiableMap(new HashMap<String, String>() {
		{
			put("平日", "1");
			put("土曜日・日曜日・祝日", "2");
		}
	});
}
