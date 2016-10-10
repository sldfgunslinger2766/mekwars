/*
 * MekWars - Copyright (C) 2016
 * 
 * original author: Bob Eldred (billypinhead@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 */

package server.campaign.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import server.campaign.CampaignMain;
import server.campaign.util.scheduler.MWScheduler;
import common.CampaignData;

/**
 * A class to handle scheduling of distribution of meks during the Christmas season.  Historically,
 * we've had to hand them out manually.
 * 
 * @author Spork
 *
 */
public class ChristmasHandler {
	private static ChristmasHandler handler;
	
	private Date startDate;
	private Date endDate;
	
	/**
	 * Exists solely to defeat instantiation.
	 * @return
	 */
	protected ChristmasHandler() {}
	
	public static ChristmasHandler getInstance() {
		if (handler == null) {
			handler = new ChristmasHandler();
		}
		return handler;
	}

	/**
	 * @return the startDate
	 */
	public Date getStartDate() {
		return startDate;
	}

	/**
	 * @param startDate the startDate to set
	 */
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	/**
	 * @return the endDate
	 */
	public Date getEndDate() {
		return endDate;
	}

	/**
	 * @param endDate the endDate to set
	 */
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public void schedule(Date start, Date end) {
		setStartDate(start);
		setEndDate(end);
		schedule();
	}
	
	public void schedule() {
		if(getStartDate() == null || getEndDate() == null) {
			CampaignData.mwlog.errLog("Cannot start Christmas Schedules.  Start or End dates are null");
			return;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date start = new Date();
		Date end = new Date();
		try {
			start = sdf.parse(CampaignMain.cm.getConfig("Christmas_StartDate"));
			end = sdf.parse(CampaignMain.cm.getConfig("Christmas_EndDate"));
		} catch (ParseException e) {
			CampaignData.mwlog.errLog(e);
		}
		MWScheduler.getInstance().scheduleChristmas(start, end);
	}
}
