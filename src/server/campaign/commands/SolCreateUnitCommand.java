/*
 * MekWars - Copyright (C) 2004
 *
 * Derived from MegaMekNET (http://www.sourceforge.net/projects/megameknet)
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

package server.campaign.commands;

import java.util.StringTokenizer;
//import java.util.TreeSet;
//import java.util.Vector;

//import server.MWChatServer.auth.IAuthenticator;
import server.campaign.CampaignMain;
import server.campaign.SHouse;
import common.House;
//import common.Unit;
import server.campaign.SPlayer;
import server.campaign.SUnit;
//import server.campaign.SArmy;
import server.campaign.commands.Command;
//import server.campaign.pilot.SPilot;
//import server.campaign.pilot.SPilotSkills;
//import server.campaign.pilot.skills.SPilotSkill;
//import server.campaign.pilot.skills.TraitSkill;
//import server.campaign.BuildTable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
//import java.util.Arrays;
import java.util.List;
//import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;
import common.CampaignData;

/**
 * A command to create a unit
 * <p>
 * This command allows a SOL player to create a unit, which is then dropped into his hangar.
 * It also checks the sol free build build table to ensure that a legal unit has been
 * requested.
 *
 * @Salient (mwosux@gmail.com)
 * 2017.9.01
 */
public class SolCreateUnitCommand implements Command {

	int accessLevel = 1;
	String syntax = "filename#weightclass";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	
	private SPlayer p;
	private SHouse h;
	private SUnit u;
	
	List<String> houseList = new ArrayList<String>();
	
	public void process(StringTokenizer command,String Username) {

		//access checks
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		p = CampaignMain.cm.getPlayer(Username);
		h = p.getMyHouse();
		
		if(userLevel < getExecutionLevel()) {
			CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return;
		}
		
		if(!Boolean.parseBoolean(CampaignMain.cm.getConfig("SOL_FreeBuild"))) {
			CampaignMain.cm.toUser("AM:This command is disabled on this server.",Username,true);
			return;
		}
		
		
		if( !h.getName().equalsIgnoreCase(CampaignMain.cm.getConfig("NewbieHouseName"))) {
			CampaignMain.cm.toUser("AM: Only players in " + CampaignMain.cm.getConfig("NewbieHouseName") + " can use this command.",Username,true);
			return;
		}
     
		String filename;
		String FlavorText = "Created by " + p.getName();
		//get pilot values from house config
		int gunnery = h.getBaseGunner();
		int piloting = h.getBasePilot();
		String skillTokens = null;
		
		try {
			filename = command.nextToken();
		}catch(Exception ex) {
			CampaignMain.cm.toUser(syntax, Username);
			return;
		}

		int weight = SUnit.LIGHT;

		if ( command.hasMoreElements() )
			weight = Integer.parseInt(command.nextToken());
		
		// Create Unit
		u = SUnit.create(filename, FlavorText, gunnery, piloting, weight, skillTokens);		

		//debug
		//CampaignMain.cm.toUser("DEBUG: u.getType() = " + u.getType() + " u.getWeightclass() = " + u.getWeightclass(), p.getName() ,true);
		
		// Check to see if player has enough bay space for this unit.
		if(!p.hasRoomForUnit(u.getType(), u.getWeightclass())) {
			CampaignMain.cm.toUser("AM:You have reached the limit for this weight class of this type of unit.",Username,true);
			return;
		}
		
		if(SUnit.getHangarSpaceRequired(u, h) > p.getFreeBays() ) {
			CampaignMain.cm.toUser("AM:You do not have enough free bays to create this unit. You can delete an existing unit by right clicking on it and choosing transactions -> delete to make some room.",Username,true);
			return;
		}
		
		//Get a collection of all houses in game and covert it to a list
        //Need to set the list of houses before calling checkiflegal method
		houseList.clear();
        Iterator<House> i = CampaignMain.cm.getData().getAllHouses().iterator();
           
        while (i.hasNext()) {
           House aHouse = i.next();
      
           if (aHouse.getId() > -1) {
        	   houseList.add(aHouse.getName().trim());
        	   //debug
       		   //CampaignMain.cm.toUser("DEBUG: houseList = " + aHouse.getName() , p.getName() ,true);
           }
        }
        
        //add non-faction build tables to list if they aren't already present
        if(!CampaignMain.cm.getConfig("Sol_FreeBuild_BuildTable").equalsIgnoreCase("Common") &&
        		!houseList.contains("Common")){
        	houseList.add("Common");
        }
        
        if(!CampaignMain.cm.getConfig("Sol_FreeBuild_BuildTable").equalsIgnoreCase("Rare") &&
        		!houseList.contains("Rare")){
        	houseList.add("Rare");
        }
        
        if(!CampaignMain.cm.getConfig("Sol_FreeBuild_BuildTable").equalsIgnoreCase("Contest") &&
        		!houseList.contains("Contest")){
        	houseList.add("Contest");
        }
  
        
        //debug list contents of houseList
        //houseList.forEach(x->{
        //	CampaignMain.cm.toUser("DEBUG: houseList = " + x , p.getName() ,true);
        //});

		//Here we will check to see if it's a legal freebuild unit
		// /*
		try {
			if(!CheckIfLegal(CampaignMain.cm.getConfig("Sol_FreeBuild_BuildTable"),u)) 
			{
				CampaignMain.cm.toUser("AM:This is not a legal unit!",Username,true);
				//add some logging here, mod mail possible cheating attempt or bt ERROR
				CampaignData.mwlog.errLog("User: " + Username + "  tried to create " + u.getUnitFilename() + " unit was not found in build tables");
				CampaignData.mwlog.modLog("User: " + Username + "  tried to create " + u.getUnitFilename() + " unit was not found in build tables");
				return;
			}
		} catch (IOException e) {
			//Auto-generated catch block
			CampaignData.mwlog.errLog(e);
		}
		// */
		//finally add the unit
		p.addUnit(u, true);
		
	
		//remove extension to make msg to user more readable
		filename = filename.substring(0, filename.indexOf(".") - 1);
		CampaignMain.cm.toUser("Unit created: " + filename + ", " + FlavorText + ". Pilot: " + gunnery + "/" + piloting + ". ID #" + u.getId(),Username,true);

		
	}
	
    //thought of alternate method of doing this that updates a container class with legal lists that lives in 
    //memory attached to campaignmain.cm. That object will hold legal lists for each weight class
    //the lists will update only when a failure occurs (to make sure that this wasnt an intentional
    //server side build table update) and only then fail to produce a unit (likely cheating attempt or bad BT).
    //Could be more stable and faster especially if this is a very popular mekwars server.
    //Or it may be overkill.
	/**        
	 * @Return a Boolean as true if the SUnit is found in available build tables
	 */
	private Boolean CheckIfLegal(String buildTableName, SUnit unitToCheck) throws IOException
	{
		
		Boolean result = false;
		
        buildTableName += "_" + SUnit.getWeightClassDesc(unitToCheck.getWeightclass());
        		
        //debug
        //CampaignMain.cm.toUser("DEBUG: Unit Type is... " + SUnit.getTypeClassDesc(unitToCheck.getType()), p.getName(), true);	
        
		if(unitToCheck.getType() != 0)
        {
        	buildTableName += SUnit.getTypeClassDesc(unitToCheck.getType());
        }
        
        buildTableName += ".txt";
        
        //debug
		//CampaignMain.cm.toUser("Searching in: " + buildTableName + ", For Unit: " + unitToCheck.getUnitFilename().trim(), p.getName(), true);	
        
        //we should now have the correct path.
        Path path = Paths.get("data/buildtables/standard/" + buildTableName).toAbsolutePath();
        
        //make sure this build table file exists
        if(Files.notExists(path)) 
        {
			CampaignMain.cm.toUser("DEBUG: Error Build Table file " + buildTableName + " does not exist", p.getName() ,true);
			return false;
        }
        
        //create list of allowedUnits
        List<String> allowedUnits = Files.lines(path).collect(Collectors.toList());
        
        //debug
        //CampaignMain.cm.toUser("DEBUG: allowedUnits.size()  = " + allowedUnits.size() , p.getName() ,true);
        
        //debug list contents of houseList
        //allowedUnits.forEach(x->{
        //	CampaignMain.cm.toUser("DEBUG: allowedUnits = " + x , p.getName() ,true);
        //});

        
        //remove frequency numbers   
        for(int i = 0; i < allowedUnits.size(); i++)
        {
        	String temp = allowedUnits.get(i);
        	
        	//Catch blank lines and continue
        	if(temp.equals("") || temp == null || temp.trim().isEmpty() || temp.equals("\n") || temp.equals("\r\n"))
        	{
        		temp = "error.mtf";
        		allowedUnits.set(i, temp);
        		//CampaignMain.cm.toUser("DEBUG: Allowed Unit = " + temp , p.getName() ,true);
        		continue;
        	}
        	
        	//Check for space, if we assume a BT with no errors this isn't needed, until it is...
        	if(temp.contains(" "))
        	{
        		int firstSpace = temp.indexOf(" ");
        		temp = temp.substring(firstSpace);
        	}
        	
        	temp = temp.trim();
        	allowedUnits.set(i, temp);
        	
        	//debug
    		//CampaignMain.cm.toUser("DEBUG: Allowed Unit = " + temp, p.getName() ,true);
        }
        
        result = allowedUnits.contains(unitToCheck.getUnitFilename().trim());
        
        //is stream any faster than contains?
        //result = allowedUnits.stream().anyMatch(p->p.equalsIgnoreCase(unitToCheck.getUnitFilename().trim()));
        
        //debug
        //CampaignMain.cm.toUser("DEBUG: Pre Faction Search... Result = " + result , p.getName() ,true);
        
        //If the result is false, check to see if there were any other BTs in the list
        if(!result)	
        {
        	//debug
    		//CampaignMain.cm.toUser("DEBUG: Check 1", p.getName(), true);	

            for(int i = 0; i < allowedUnits.size(); i++)
            {
            	//iterate and check if unit or another built table
            	//if its another build table it wont have the . character
            	if(!allowedUnits.get(i).contains("."))
            	{	
            		//debug
            		//CampaignMain.cm.toUser("DEBUG: Check 2 " + allowedUnits.get(i), p.getName() , true);	

            		//so now that we've found another BT, we'll find out which house it is
            		for(int z = 0; z < houseList.size(); z++)
            		{
            			//debug
            			//CampaignMain.cm.toUser("DEBUG: Check 3 .." + allowedUnits.get(i).trim() + ".. ?= .." + houseList.get(z)+ "..", p.getName(), true);	
            			
            			if(houseList.get(z).equalsIgnoreCase(allowedUnits.get(i)))
            			{	
            	    		//debug
            				//CampaignMain.cm.toUser("DEBUG: Check 4 " + houseList.get(z), p.getName(), true);	

            				//once we find it, it's important to remove it from the list
            				String temp = houseList.get(z);
            				houseList.remove(z);
            				//search other BTs recursively
            				result = CheckIfLegal(temp,u);
            				//if it's true, stop searching, return it.
            				if(result) { 
            					//debug
            					//CampaignMain.cm.toUser("DEBUG: result = " + result + " BT = " + buildTableName + " Unit = " + unitToCheck.getUnitFilename(), p.getName() ,true);
            					return result; 
            				}
            			}
            		}
            	}
            }
        }
        
		//debug
        //CampaignMain.cm.toUser("DEBUG: result = " + result + " BT = " + buildTableName + " Unit = " + unitToCheck.getUnitFilename(), p.getName() ,true);
		
        return result;
        		
	}//end checkiflegal
}
