/*
 * MekWars - Copyright (C) 2016
 *
 * Original author - Bob Eldred (billypinhead@users.sourceforge.net)
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 */

package server.campaign.util.scheduler;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;

import server.campaign.CampaignMain;
import server.campaign.SHouse;
import server.campaign.SPlayer;

import common.CampaignData;


/**
 * A class to handle generation of Components due to player activity
 * 
 * @author Spork
 * @version 2016.10.06
 */
public class UserActivityComponentsJob implements Job {

	// parameter names specific to this job
    public static final String PLAYER_NAME = "player name";
    public static final String FACTION_NAME = "faction name";
    public static final String ARMY_WEIGHT = "army weight";
	
    public UserActivityComponentsJob(){}
    
    /**
     * This method is called every X seconds, where X is defined by the server config variable
     * "Scheduler_PlayerActivity_comps." 
     * 
     * @param JobExecutionContext - data provided by the Quartz Scheduler
     */
    @Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
        
        // Grab and print passed parameters
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String playerName = data.getString(PLAYER_NAME);
        //String factionName = data.getString(FACTION_NAME);
        Double armyWeight = data.getDoubleFromString(ARMY_WEIGHT);
        
        //CampaignMain.cm.toUser("ActivityJob: " + jobKey + " executing at " + new Date() + ": You are " + playerName + " fighting for Faction " + factionName + ". Your armies have an effective weight of " + armyWeight + ".", playerName, false);
        //String message = "ComponentJob: " + jobKey + " executing at " + new Date() + ": Generating Components for " + factionName;
        //CampaignMain.cm.toUser(message, playerName);
        //CampaignData.mwlog.infoLog(message);
        SPlayer p = CampaignMain.cm.getPlayer(playerName);
        SHouse house = p.getMyHouse();
        
        if (playerCountsForProduction(p)) {
        	house.addActivityPP(armyWeight);
            String toShow = "AM:You counted towards production";
            DecimalFormat myFormatter = new DecimalFormat("###.##");
            String output = myFormatter.format(armyWeight);
            toShow += " (" + output + " points worth)";
            CampaignMain.cm.toUser(toShow + ".", p.getName(), true);        	
        }
        
	}

	/**
	 * A method to build the Components Job and get it into the scheduler.  Called when the user
	 * issues an Activate command
	 * @param userName - the name of the user going active
	 * @param weightedArmyValue the value of the player's armies
	 * @param factionName the faction the player fights for
	 */
    public static void submit(String userName, Double weightedArmyValue, String factionName) {
        JobDetail job = newJob(UserActivityComponentsJob.class)
				.withIdentity(userName + "_comps", "ActivityGroup")
				.build();
        
        int frequency = CampaignMain.cm.getIntegerConfig("Scheduler_PlayerActivity_comps");
        
		Trigger trigger = newTrigger()
				.withIdentity(userName + "_compsTrigger", "ActivityGroup")
				.startAt(new Date(Calendar.getInstance().getTimeInMillis() + frequency*1000))
				.withSchedule(simpleSchedule()
						.withIntervalInSeconds(frequency)
						.repeatForever())
				.build();
		
        job.getJobDataMap().put(UserActivityComponentsJob.ARMY_WEIGHT, Double.toString(weightedArmyValue));
        job.getJobDataMap().put(UserActivityComponentsJob.FACTION_NAME, factionName);
        job.getJobDataMap().put(UserActivityComponentsJob.PLAYER_NAME, userName);
        
		try {
			CampaignMain.cm.getScheduler().scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			CampaignData.mwlog.errLog(e);
		}
	}
	
	/**
	 * A method to stop execution of this job and remove it from the scheduler.  Called when the player deactivates.
	 * @param userName
	 */
    public static void stop(String userName) {
		try {
			Scheduler scheduler = CampaignMain.cm.getScheduler();
			TriggerKey key = new TriggerKey(userName + "_compsTrigger", "ActivityGroup");
			scheduler.unscheduleJob(key);
		} catch (SchedulerException e) {
			CampaignData.mwlog.errLog(e);
		} finally {
			
		}
	}
	
	/**
	 * A method to determine if the player counts for generating components, based on server settings.
	 * @param p - the player
	 * @return true if the player qualifies, false if not
	 */
    private boolean playerCountsForProduction(SPlayer p) {
		if (p.getWeightedArmyNumber() <= 0) {
			return false;
		}
		
        if (System.currentTimeMillis() < p.getActiveSince() + Long.parseLong(p.getMyHouse().getConfig("InfluenceTimeMin"))) {
            return false;
        }
        
		return true;
	}
}
