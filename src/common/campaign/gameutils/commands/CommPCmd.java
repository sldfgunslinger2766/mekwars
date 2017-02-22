package common.campaign.gameutils.commands;

import java.util.StringTokenizer;

import client.MWClient;
import common.campaign.gameutils.protocol.IClient;
import common.campaign.gameutils.protocol.TransportCodec;

/**
 * Comm command
 */

public class CommPCmd extends CProtCommand
{
	public CommPCmd(IClient mwclient) 
	{
		super(mwclient);
		name = "comm";
	}

	// execute command
	@Override
	public boolean execute(String input) {
		
		StringTokenizer ST = new StringTokenizer(input, delimiter);
		if (check(ST.nextToken()) && ST.hasMoreTokens()) {
			input = TransportCodec.unescape(ST.nextToken());
			if (!client.isDedicated()) {client.doParseDataInput(input);}
			else {client.parseDedDataInput(input);}
			return true;
		}
		
		//else
		return false; 
	}

	// echo command in GUI
	@Override
	protected void echo(String input) {}
}