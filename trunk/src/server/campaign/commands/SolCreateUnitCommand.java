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
	String syntax = "filename#weightclass#factionTable";
	public int getExecutionLevel(){return accessLevel;}
	public void setExecutionLevel(int i) {accessLevel = i;}
	public String getSyntax() { return syntax;}
	
	
	private SPlayer player;
	private SHouse house;
	private SUnit unit;
	
	List<String> houseList = new ArrayList<String>();
	
	public void process(StringTokenizer command,String Username) {

		//access checks
		int userLevel = CampaignMain.cm.getServer().getUserLevel(Username);
		player = CampaignMain.cm.getPlayer(Username);
		house = player.getMyHouse();
		
		if(!accessChecks(Username, userLevel))
			return;		
     
		unit = readCommandReturnSUnit(command, Username);
		if(unit == null)
			return;		
		
		//used if 'useall' flag or post defection is set to true in SO
		String factionTable = null;
		if(command.hasMoreElements())
			factionTable = command.nextToken();

		if(!playerUnitLimitChecks(Username))
			return;
		
		initHouseList();
        addNonFactionBuildTables();

		//check to see if it's a legal freebuild unit
		try {
			if( CampaignMain.cm.getConfig("Sol_FreeBuild_UseAll").equalsIgnoreCase("true") &&
				house.getName().equalsIgnoreCase(CampaignMain.cm.getConfig("NewbieHouseName"))	)
			{
				if(!CheckIfLegal(factionTable,unit)) 
				{
					CampaignMain.cm.toUser("AM:This is not a legal unit!",Username,true);
					//add some logging here, mod mail possible cheating attempt or bt ERROR
					CampaignData.mwlog.errLog("User: " + Username + "  tried to create " + unit.getUnitFilename() + " unit was not found in build tables");
					CampaignData.mwlog.modLog("User: " + Username + "  tried to create " + unit.getUnitFilename() + " unit was not found in build tables");
					return;
				}
			}
			else if ( CampaignMain.cm.getConfig("Sol_FreeBuild_PostDefection").equalsIgnoreCase("true") &&
					  !house.getName().equalsIgnoreCase(CampaignMain.cm.getConfig("NewbieHouseName")))
			{
				if(!CheckIfLegal(house.getName().trim(),unit)) 
				{
					CampaignMain.cm.toUser("AM:This is not a legal unit!",Username,true);
					//add some logging here, mod mail possible cheating attempt or bt ERROR
					CampaignData.mwlog.errLog("User: " + Username + "  tried to create " + unit.getUnitFilename() + " unit was not found in build tables");
					CampaignData.mwlog.modLog("User: " + Username + "  tried to create " + unit.getUnitFilename() + " unit was not found in build tables");
					return;
				}	
			}
			else
			{
				if(!CheckIfLegal(CampaignMain.cm.getConfig("Sol_FreeBuild_BuildTable"),unit)) 
				{
					CampaignMain.cm.toUser("AM:This is not a legal unit!",Username,true);
					//add some logging here, mod mail possible cheating attempt or bt ERROR
					CampaignData.mwlog.errLog("User: " + Username + "  tried to create " + unit.getUnitFilename() + " unit was not found in build tables");
					CampaignData.mwlog.modLog("User: " + Username + "  tried to create " + unit.getUnitFilename() + " unit was not found in build tables");
					return;
				}
			}
		} catch (IOException e) {
			//Auto-generated catch block
			CampaignData.mwlog.errLog(e);
		}

		//finally add the unit
		player.addUnit(unit, true);
		
	    handleFreeMekLimit(Username);
			
		//remove extension to make msg to user more readable
		//String unitName =  unit.getUnitFilename().substring(0, unit.getUnitFilename().indexOf(".") - 1);
		CampaignMain.cm.toUser("Unit created: " + unit.getSmallDescription() + "  ID #" + unit.getId(),Username,true);

		
		
	}
	
	/**
	 *  makes sure that infinite sol free build can co-exist with limited post defection freebuild
	 */
	private void handleFreeMekLimit(String Username) 
	{

		if( CampaignMain.cm.getConfig("Sol_FreeBuild_LimitPostDefOnly").equalsIgnoreCase("true") &&
			!house.getName().equalsIgnoreCase(CampaignMain.cm.getConfig("NewbieHouseName")) &&
			Integer.parseInt((CampaignMain.cm.getConfig("Sol_FreeBuild_Limit"))) > 0)
		{
			player.addMekToken(1);
			int remaining = Integer.parseInt(CampaignMain.cm.getConfig("Sol_FreeBuild_Limit")) - player.getMekToken();
			CampaignMain.cm.toUser(remaining + " Free units remaining.",Username,true);
		}
	
		if( CampaignMain.cm.getConfig("Sol_FreeBuild_LimitPostDefOnly").equalsIgnoreCase("false") &&
			house.getName().equalsIgnoreCase(CampaignMain.cm.getConfig("NewbieHouseName")) &&
			Integer.parseInt((CampaignMain.cm.getConfig("Sol_FreeBuild_Limit"))) > 0)
		{
			player.addMekToken(1);
			int remaining = Integer.parseInt(CampaignMain.cm.getConfig("Sol_FreeBuild_Limit")) - player.getMekToken();
			CampaignMain.cm.toUser(remaining + " Free units remaining.",Username,true);
		}
	}
	
	/**
	 * add non-faction build tables to list if they aren't already present
	 */
	private void addNonFactionBuildTables() 
	{
		
        if(!CampaignMain.cm.getConfig("Sol_FreeBuild_BuildTable").equalsIgnoreCase("Common") &&
           !houseList.contains("Common"))
        {
        	houseList.add("Common");
        }
        
        if(!CampaignMain.cm.getConfig("Sol_FreeBuild_BuildTable").equalsIgnoreCase("Rare") &&
           !houseList.contains("Rare"))
        {
        	houseList.add("Rare");
        }
        
        if(!CampaignMain.cm.getConfig("Sol_FreeBuild_BuildTable").equalsIgnoreCase("Contest") &&
           !houseList.contains("Contest"))
        {
        	houseList.add("Contest");
        }
  
        
        //debug list contents of houseList
        //houseList.forEach(x->{
        //	CampaignMain.cm.toUser("DEBUG: houseList = " + x , p.getName() ,true);
        //});
	}
	
	/**
	 * 	Get a collection of all houses in game and covert it to a list
     *  Need to set the list of houses before calling checkiflegal method
	 */
	private void initHouseList() 
	{
		houseList.clear();
        Iterator<House> i = CampaignMain.cm.getData().getAllHouses().iterator();
           
        while (i.hasNext()) 
        {
           House aHouse = i.next();
      
           if (aHouse.getId() > -1) 
           {
        	   houseList.add(aHouse.getName().trim());
        	   //debug
       		   //CampaignMain.cm.toUser("DEBUG: houseList = " + aHouse.getName() , p.getName() ,true);
           }
        }
	}
	
	private Boolean playerUnitLimitChecks(String Username) 
	{
		if(!player.hasRoomForUnit(unit.getType(), unit.getWeightclass())) 
		{
			CampaignMain.cm.toUser("AM:You have reached the limit for this type of unit at this weight class.",Username,true);
			return false;
		}
		
		if(SUnit.getHangarSpaceRequired(unit, house) > player.getFreeBays()) 
		{
			if( !house.getName().equalsIgnoreCase(CampaignMain.cm.getConfig("NewbieHouseName")))
			{
				CampaignMain.cm.toUser("AM:You do not have enough free bays to create this unit.",Username,true);
				return false;
			}
			else
			{
				CampaignMain.cm.toUser("AM:You do not have enough free bays to create this unit. You can delete an existing unit by right clicking on it and choosing transactions -> delete to make some room.",Username,true);
				return false;
			}				
		}
		
		return true;
	}
	
	private SUnit readCommandReturnSUnit(StringTokenizer command, String Username) {
		
		String filename;
		String FlavorText = "Built by " + player.getName();
		String skillTokens = null;
		
		try 
		{
			filename = command.nextToken();
		}
		catch(Exception ex) 
		{
			CampaignMain.cm.toUser(syntax, Username);
			return null;
		}

		int weight = SUnit.LIGHT;

		if(command.hasMoreElements())
			weight = Integer.parseInt(command.nextToken());

		return SUnit.create(filename, FlavorText, house.getBaseGunner(), house.getBasePilot(), weight, skillTokens);
	}
	
	private Boolean accessChecks(String Username, int userLevel) 
	{
		if(userLevel < getExecutionLevel()) 
		{
			CampaignMain.cm.toUser("AM:Insufficient access level for command. Level: " + userLevel + ". Required: " + accessLevel + ".",Username,true);
			return false;
		}
		
		// if Sol_FreeBuild and post defection are set to false, return
		if(!Boolean.parseBoolean(CampaignMain.cm.getConfig("Sol_FreeBuild")) &&
		   !Boolean.parseBoolean(CampaignMain.cm.getConfig("Sol_FreeBuild_PostDefection"))) 
		{
			CampaignMain.cm.toUser("AM:This command is disabled on this server.",Username,true);
			return false;
		}
		
		// if build limit set to 0, return
		if(Integer.parseInt((CampaignMain.cm.getConfig("Sol_FreeBuild_Limit"))) == 0) 
		{
			CampaignMain.cm.toUser("AM:Build limit set to 0, was this intentional? If so, uncheck Sol Free Build instead.",Username,true);
			return false;
		}
		
		// if the player isn't in SOL and Sol_FreeBuild_PostDefection is false, return
		if(!house.getName().equalsIgnoreCase(CampaignMain.cm.getConfig("NewbieHouseName")) &&
				!Boolean.parseBoolean(CampaignMain.cm.getConfig("Sol_FreeBuild_PostDefection"))) 
		{
			CampaignMain.cm.toUser("AM: Only players in " + CampaignMain.cm.getConfig("NewbieHouseName") + " can use this command.",Username,true);
			return false;
		}
		
		// if a limit has been set, check to make sure player has not exceeded limit
		if(Integer.parseInt((CampaignMain.cm.getConfig("Sol_FreeBuild_Limit"))) > 0 &&
		   player.getMekToken() == Integer.parseInt((CampaignMain.cm.getConfig("Sol_FreeBuild_Limit")))) 
		{
			CampaignMain.cm.toUser("AM:You have reached the server limit of free units.",Username,true);
			return false;
		}
		
		return true;
	}
	
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
			CampaignMain.cm.toUser("DEBUG: Error Build Table file " + buildTableName + " does not exist", player.getName() ,true);
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
            				result = CheckIfLegal(temp,unit);
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
        
        //thought of alternate method of doing this that updates a container class with legal lists that lives in 
        //memory attached to campaignmain.cm. That object will hold legal lists for each weight class
        //the lists will update only when a failure occurs (to make sure that this wasnt an intentional
        //server side build table update) and only then fail to produce a unit (likely cheating attempt or bad BT).
        //Could be more stable and faster especially if this is a very popular mekwars server.
        //Or it may be overkill.
        		
	}//end checkiflegal
}
