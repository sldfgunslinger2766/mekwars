package common.campaign.clientutils;

import java.util.Date;
import java.util.TreeMap;
import java.util.Vector;

import common.MMGame;
import common.campaign.clientutils.protocol.CConnector;
import common.campaign.clientutils.protocol.commands.IProtCommand;
import megamek.common.event.GameBoardChangeEvent;
import megamek.common.event.GameBoardNewEvent;
import megamek.common.event.GameCFREvent;
import megamek.common.event.GameEndEvent;
import megamek.common.event.GameEntityChangeEvent;
import megamek.common.event.GameEntityNewEvent;
import megamek.common.event.GameEntityNewOffboardEvent;
import megamek.common.event.GameEntityRemoveEvent;
import megamek.common.event.GameListener;
import megamek.common.event.GameMapQueryEvent;
import megamek.common.event.GameNewActionEvent;
import megamek.common.event.GamePhaseChangeEvent;
import megamek.common.event.GamePlayerChangeEvent;
import megamek.common.event.GamePlayerChatEvent;
import megamek.common.event.GamePlayerConnectedEvent;
import megamek.common.event.GamePlayerDisconnectedEvent;
import megamek.common.event.GameReportEvent;
import megamek.common.event.GameSettingsChangeEvent;
import megamek.common.event.GameTurnChangeEvent;
import megamek.common.event.GameVictoryEvent;
import megamek.server.Server;

public abstract class GameHost implements GameListener, IGameHost {
    public static final int STATUS_DISCONNECTED = 0;
    public static final int STATUS_LOGGEDOUT = 1;
    public static final int STATUS_RESERVE = 2;
    public static final int STATUS_ACTIVE = 3;
    public static final int STATUS_FIGHTING = 4;
    
    public static final String CAMPAIGN_PREFIX = "/"; // prefix for campaign commands
    public static final String CAMPAIGN_PATH = "data/campaign/";
    public static final String COMMAND_DELIMITER = "|"; // delimiter for client commands
    
    public String myUsername = "";// public b/c used in RGTS command to set server status. HACK!
    
    protected TreeMap<String, IProtCommand> ProtCommands;
    
    protected IClientConfig Config;

    protected CConnector Connector;
    
    protected Server myServer = null;
    protected Date mytime = new Date(System.currentTimeMillis());
    protected TreeMap<String, MMGame> servers = new TreeMap<String, MMGame>();// hostname,mmgame
    protected Vector<String> decodeBuffer = new Vector<String>(1, 1);// used to buffer incoming data until CMainFrame is built

    
	@Override
	public void gameBoardChanged(GameBoardChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameBoardNew(GameBoardNewEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameClientFeedbackRquest(GameCFREvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameEnd(GameEndEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameEntityChange(GameEntityChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameEntityNew(GameEntityNewEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameEntityNewOffboard(GameEntityNewOffboardEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameEntityRemove(GameEntityRemoveEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameMapQuery(GameMapQueryEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameNewAction(GameNewActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gamePhaseChange(GamePhaseChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gamePlayerChange(GamePlayerChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gamePlayerChat(GamePlayerChatEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gamePlayerConnected(GamePlayerConnectedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gamePlayerDisconnected(GamePlayerDisconnectedEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameReport(GameReportEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameSettingsChange(GameSettingsChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameTurnChange(GameTurnChangeEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameVictory(GameVictoryEvent arg0) {
		// TODO Auto-generated method stub
		
	}
    
    public boolean isAdmin() {
        return getUser(getUsername()).getUserlevel() >= 200;
    }

    public boolean isMod() {
        return getUser(getUsername()).getUserlevel() >= 100;
    }
	
    public String getUsername() {
        return myUsername;
    }
    
    protected abstract IClientUser getUser(String name);
    
}
