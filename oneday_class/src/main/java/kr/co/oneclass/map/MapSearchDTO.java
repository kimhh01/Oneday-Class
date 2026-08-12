package kr.co.oneclass.map;

import java.sql.Date;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MapSearchDTO {
	private int categoryId;
	private Date classDate;
	private String startTime;
	private double minLat;
	private double maxLat;
	private double minLng;
	private double maxLng;
	private double myLat;
	private double myLng;
}
