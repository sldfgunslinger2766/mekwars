package server.campaign.util.scheduler;

import java.util.Date;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

public class ActivityJob implements Job {

	// parameter names specific to this job
    public static final String PLAYER_NAME = "player name";
    public static final String FACTION_NAME = "faction name";
    public static final String ARMY_WEIGHT = "army weight";
	
    public ActivityJob(){}
    
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {

        // This job simply prints out its job name and the
        // date and time that it is running
        JobKey jobKey = context.getJobDetail().getKey();
        
        // Grab and print passed parameters
        JobDataMap data = context.getJobDetail().getJobDataMap();
        String playerName = data.getString(PLAYER_NAME);
        String factionName = data.getString(FACTION_NAME);
        Double armyWeight = data.getDoubleFromString(ARMY_WEIGHT);
        
        //CampaignMain.cm.toUser("ActivityJob: " + jobKey + " executing at " + new Date() + ": You are " + playerName + " fighting for Faction " + factionName + ". Your armies have an effective weight of " + armyWeight + ".", playerName, false);
        String message = "ActivityJob: " + jobKey + " executing at " + new Date() + ": You are " + playerName + " fighting for Faction " + factionName + ". Your armies have an effective weight of " + armyWeight + ".";
        //CampaignMain.cm.toUser(message, playerName);
        //CampaignData.mwlog.infoLog(message);
	}

}
