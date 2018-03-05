package net.uselesscode.sapporo_subway_timetable.constant;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import net.uselesscode.sapporo_subway_timetable.entity.Route;

public class RouteConst {

	/** 路線定義 */
	public static final List<Route> LIST = Collections.unmodifiableList(new LinkedList<Route>() {
		{
			add(new Route("n", "1", "南北線"));
			add(new Route("t", "2", "東西線"));
			add(new Route("h", "3", "東豊線"));
		}
	});
}
