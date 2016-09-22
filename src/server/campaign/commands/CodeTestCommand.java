/*
 * MekWars - Copyright (C) 2008
 * 
 * Original author - Jason Tighe (torren@users.sourceforge.net)
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package server.campaign.commands;

import java.util.Date;
import java.util.StringTokenizer;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;
import static org.quartz.SimpleScheduleBuilder.*;
import static org.quartz.CronScheduleBuilder.*;
import static org.quartz.CalendarIntervalScheduleBuilder.*;
import static org.quartz.JobKey.*;
import static org.quartz.TriggerKey.*;
import static org.quartz.DateBuilder.*;
import static org.quartz.impl.matchers.KeyMatcher.*;
import static org.quartz.impl.matchers.GroupMatcher.*;
import static org.quartz.impl.matchers.AndMatcher.*;
import static org.quartz.impl.matchers.OrMatcher.*;
import static org.quartz.impl.matchers.EverythingMatcher.*;
import common.CampaignData;


import server.campaign.CampaignMain;
import server.campaign.util.scheduler.RepeatingJob;

public class CodeTestCommand implements Command {
	
	int accessLevel = 0;
	public int getExecutionLevel(){return 200;}
	public void setExecutionLevel(int i) {}
	String syntax = "start or stop";
	public String getSyntax() { return syntax;}

	final int ACTION_START = 0;
    final int ACTION_STOP = 1;
	
	public void process(StringTokenizer command,String Username) {
		if (accessLevel != 0) {
			int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
			if(userLevel < getExecutionLevel()) {
				CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
				return;
			}
		}
		
		
		int action;
		
		if(!command.hasMoreTokens()) {
		  CampaignMain.cm.toUser("AM: invalid syntax: try /codetest start or /codetest stop", Username, false);
		  return;
		}
		
		String s = command.nextToken();
		
		if (s.equalsIgnoreCase("start")) {
		  action = ACTION_START;	
		} else if (s.equalsIgnoreCase("stop")) {
		  action = ACTION_STOP;	
		} else {
			CampaignMain.cm.toUser("AM: invalid syntax: try /codetest start or /codetest stop", Username, false);
			return;	
		}
		
		switch (action) {
		case ACTION_START:
						
			JobDetail job = newJob(RepeatingJob.class)
							  .withIdentity("job1", "group1")
							  .build();
			Date runTime = evenMinuteDate(new Date());
			
			Trigger trigger = newTrigger()
								.withIdentity("trigger1", "group1")
								.withSchedule(cronSchedule("0/5 * * * * ?"))
								.startAt(runTime)
								.build();
			
			try {
				CampaignMain.cm.getScheduler().scheduleJob(job, trigger);
			} catch (SchedulerException e) {
				CampaignData.mwlog.errLog(e);
			}
			
			break;
		case ACTION_STOP:
			try {
				CampaignMain.cm.getScheduler().unscheduleJob(new TriggerKey("trigger1", "group1"));
				Scheduler sc = CampaignMain.cm.getScheduler();
				for (String group : sc.getJobGroupNames()) {
					for(JobKey jobKey : sc.getJobKeys(jobGroupEquals(group))) {
						CampaignMain.cm.doSendModMail("SERVER", "Found Job Identified by: " + jobKey);
					}
				}
				
			} catch (SchedulerException e) {
				CampaignData.mwlog.errLog(e);
			}
			break;
		}
	}
}