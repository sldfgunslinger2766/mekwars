/*
 * MekWars - Copyright (C) 2011
 *
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 */

package admin.dialog.serverConfigDialogs;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.ToolTipManager;

import client.MWClient;

import javax.swing.JCheckBox;
import common.VerticalLayout;

/**
 *
 * @author Salient - contains link area options options
 */
public class MiniCampaignPanel extends JPanel 
{

	private static final long serialVersionUID = -4626004179999981829L;

		private JTextField baseTextField = new JTextField(5);
		private JCheckBox baseCheckBox = new JCheckBox();

		public MiniCampaignPanel(MWClient mwclient) 
		{
			super();

			setLayout(new VerticalLayout(5, VerticalLayout.CENTER, VerticalLayout.TOP));

			String description = "<HTML>Mini Campaign allows for a different form of play. Every player has a cycle that limits<br>"
					+ " their ability to purchase, or even use, their units. Each personal cycle has a trigger that initiates<br>"
					+ " a currency injection. This signals the end of the players personal campaign cycle. They can then<br>"
					+ " purchase new forces, once they go active all currency is striped until next cycle.<br>";

			JPanel panel0 = new JPanel();
			JPanel panel4 = new JPanel();
			JPanel panel4a = new JPanel();
			JPanel panel4b = new JPanel();
			JPanel panel4c = new JPanel();
			JPanel panel4d = new JPanel();
			JPanel panel4e = new JPanel();
		
	        String fluName = mwclient.getserverConfigs("FluShortName");
	        String rpName = mwclient.getserverConfigs("RPShortName");
	        String cbName = mwclient.getserverConfigs("MoneyShortName");

	        panel0.setBorder(BorderFactory.createTitledBorder("Mini Campaign"));
	        
	        panel0.add(new JLabel(description));
		
			panel4.setBorder(BorderFactory.createTitledBorder("Options"));
			panel4.setLayout(new VerticalLayout(5, VerticalLayout.CENTER, VerticalLayout.TOP));
			
			baseCheckBox = new JCheckBox("Enable Mini Campaigns");
			baseCheckBox.setToolTipText("<HTML>!!Do NOT reward selected currencies ANY other way or you will break MC.!! Do NOT use xmas code!! <br> After a certain point, allows player to rebuild forces. Can only use units once per mini campaign.</HTML>");
			baseCheckBox.setName("Enable_MiniCampaign");
			panel4a.add(baseCheckBox);
			
			panel4b.add(new JLabel("Restock Triggers -> ", SwingConstants.TRAILING));
			
			baseTextField = new JTextField(5);
			panel4b.add(new JLabel("% Hangar BV", SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML>(-1 to disable) Initiate 'restock' of players hangar when hangar bv drops below this percentage </HTML>");
			baseTextField.setName("Percent_HangarRestock");
			panel4b.add(baseTextField);

			baseTextField = new JTextField(5);
			panel4b.add(new JLabel("Flat Hangar BV", SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML>(-1 to disable) Initiate 'restock' of players hangar when hangar bv drops below this value </HTML>");
			baseTextField.setName("MinBV_HangarRestock");
			panel4b.add(baseTextField);
			
			baseTextField = new JTextField(5);
			panel4b.add(new JLabel("Unit Count", SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML>(-1 to disable) Initiate 'restock' of players hangar when hangar unit count drops below this value </HTML>");
			baseTextField.setName("Unit_HangarRestock");
			panel4b.add(baseTextField);
			
			panel4c.add(new JLabel("Currency Injection -> ", SwingConstants.TRAILING));
			
			baseTextField = new JTextField(5);
			panel4c.add(new JLabel(cbName, SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML>(-1 to disable) when restock is triggered, inject this amount of "+cbName+". Note resets amount to zero before injection.</HTML>");
			baseTextField.setName("RestockCB_Injection");
			panel4c.add(baseTextField);
			
			baseTextField = new JTextField(5);
			panel4c.add(new JLabel(rpName, SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML>(-1 to disable) when restock is triggered, inject this amount of " +rpName+". Note resets amount to zero before injection.</HTML>");
			baseTextField.setName("RestockRP_Injection");
			panel4c.add(baseTextField);
			
			baseTextField = new JTextField(5);
			panel4c.add(new JLabel(fluName, SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML>(-1 to disable) when restock is triggered, inject this amount of "+fluName+". Note resets amount to zero before injection.</HTML>");
			baseTextField.setName("RestockFLU_Injection");
			panel4c.add(baseTextField);
			
			baseTextField = new JTextField(5);
			panel4c.add(new JLabel("MT", SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML>(MT = MekTokens used with freebuild limits)(-1 to disable)(PostDefection freebuild w/limit must be enabled)<br> when restock is triggered, inject this amount of Free Mek Tokens. Note resets amount to zero before injection. </HTML>");
			baseTextField.setName("RestockMT_Injection");
			panel4c.add(baseTextField);
			
			panel4d.add(new JLabel("Required Currency Usage % -> ", SwingConstants.TRAILING));
			
			baseTextField = new JTextField(5);
			panel4d.add(new JLabel(cbName, SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML> % of currency player must use before being able to go active and start the next cycle </HTML>");
			baseTextField.setName("RestockCB_LeewayPercentage");
			panel4d.add(baseTextField);
			
			baseTextField = new JTextField(5);
			panel4d.add(new JLabel(rpName, SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML> % of currency player must use before being able to go active and start the next cycle </HTML>");
			baseTextField.setName("RestockRP_LeewayPercentage");
			panel4d.add(baseTextField);
			
			baseTextField = new JTextField(5);
			panel4d.add(new JLabel(fluName, SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML> % of currency player must use before being able to go active and start the next cycle </HTML>");
			baseTextField.setName("RestockFLU_LeewayPercentage");
			panel4d.add(baseTextField);
			
			baseTextField = new JTextField(5);
			panel4d.add(new JLabel("MT", SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML>(MT = MekTokens used with freebuild limits)(-1 to disable)(PostDefection freebuild w/limit must be enabled)<br> % of currency player must use before being able to go active and start the next cycle </HTML>");
			baseTextField.setName("RestockMT_LeewayPercentage");
			panel4d.add(baseTextField);
			
			panel4e.add(new JLabel("Unit Lock Options -> ", SwingConstants.TRAILING));
			
			baseCheckBox = new JCheckBox("Enable Locking");
			baseCheckBox.setToolTipText("<HTML>Enable unit locking, units are locked after used, meaning they can only be used once per mini campaign cycle.</HTML>");
			baseCheckBox.setName("LockUnits");
			panel4e.add(baseCheckBox);
			
			baseCheckBox = new JCheckBox("Lock Salvage");
			baseCheckBox.setToolTipText("<HTML>Units awarded by salvage after a match are awarded locked</HTML>");
			baseCheckBox.setName("LockSalvagedUnits");
			panel4e.add(baseCheckBox);
			
			baseTextField = new JTextField(5);
			panel4e.add(new JLabel("Reset %", SwingConstants.TRAILING));
			baseTextField.setToolTipText("<HTML>(-1 to disable)<br> Example: if set to 70 -> if 70 percent of the players units are locked all units unlock without ending the cycle. Injections will NOT occur.  </HTML>");
			baseTextField.setName("UnlockUnits_Percentage");
			panel4e.add(baseTextField);
			
			baseCheckBox = new JCheckBox("Remove BV");
			baseCheckBox.setToolTipText("<HTML> With this set, Locked units do NOT count towards hangar BV calculations. Injections WILL occur due to locked units. </HTML>");
			baseCheckBox.setName("LockedUnits_RemoveBV");
			panel4e.add(baseCheckBox);
								
			panel4.add(panel4a);
			panel4.add(panel4b);
			panel4.add(panel4c);
			panel4.add(panel4d);
			panel4.add(panel4e);

			add(panel0);
			add(panel4);

	}
}
