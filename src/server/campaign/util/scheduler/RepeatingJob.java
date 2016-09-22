package server.campaign.util.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import common.CampaignData;

import server.campaign.CampaignMain;

public class RepeatingJob implements Job {

	public RepeatingJob() {
		
	}
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {

		CampaignMain.cm.doSendModMail("SERVER", System.currentTimeMillis() + "Scheduled Job running...");
		CampaignData.mwlog.mainLog("Scheduled Job!!!!");
	}

}
