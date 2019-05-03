package com.mma.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {
	
	private final static Logger log = LoggerFactory.getLogger(DateUtil.class);
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static Date convertStringToDate(String dateStr, String dateFormat) {
		try {
			return new SimpleDateFormat(dateFormat).parse(dateStr);
		} catch (ParseException e) {
			log.error("Error occured while converting string [" + dateStr + "] to date in the following format [" + dateFormat + "].");
		}
		return null;
	}
}
