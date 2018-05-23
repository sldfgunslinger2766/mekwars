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

import java.awt.GridLayout;
import java.util.HashMap;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import common.Unit;
import common.VerticalLayout;

/**
 * @author Spork
 * @author Salient - Added sol free build options
 */
public class NewbieHousePanel extends JPanel {

	private static final long serialVersionUID = -4626004177197981829L;

	private JTextField baseTextField = new JTextField(5);
	private JCheckBox BaseCheckBox = new JCheckBox();

	private void init() {

		setLayout(new VerticalLayout(5, VerticalLayout.CENTER, VerticalLayout.TOP));

		HashMap<Integer, String> abbreviations = new HashMap<Integer, String>();
		abbreviations.put(Unit.MEK, "Meks");
		abbreviations.put(Unit.VEHICLE, "Vehs");
		abbreviations.put(Unit.INFANTRY, "Inf");
		abbreviations.put(Unit.BATTLEARMOR, "BattleArmor");
		abbreviations.put(Unit.PROTOMEK, "ProtoMek");
		abbreviations.put(Unit.AERO, "Aero");

		JPanel topPanel = new JPanel();
		JPanel middlePanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		JPanel bottomPanel2 = new JPanel();

		topPanel.setLayout(new GridLayout(0,5));
		topPanel.setBorder(BorderFactory.createTitledBorder("Starting Units"));
		topPanel.add(new JLabel(" "));
		topPanel.add(new JLabel("Light"));
		topPanel.add(new JLabel("Medium"));
		topPanel.add(new JLabel("Heavy"));
		topPanel.add(new JLabel("Assault"));

		for (int type = Unit.MEK; type < Unit.MAXBUILD; type++) {
			topPanel.add(new JLabel(Unit.getTypeClassDesc(type)));
			for (int weight = Unit.LIGHT; weight <= Unit.ASSAULT; weight++) {
		        baseTextField = new JTextField(5);
		        baseTextField.setToolTipText("Number of " + Unit.getWeightClassDesc(weight).toLowerCase() + " " + Unit.getTypeClassDesc(type).toLowerCase() + (type==Unit.INFANTRY?"":"s") + " given to SOL players");
		        baseTextField.setName("SOL" + Unit.getWeightClassDesc(weight) + abbreviations.get(type));
		        topPanel.add(baseTextField);
			}
		}

		middlePanel.setBorder(BorderFactory.createTitledBorder("Hangar Reset"));

		baseTextField = new JTextField(5);
        middlePanel.add(new JLabel("Units to Reset:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + "SOL player needs to have this many (or<br>" + "fewer) units to trigger a hangar reset</HTML>");
        baseTextField.setName("NumUnitsToQualifyForNew");
        middlePanel.add(baseTextField);

        baseTextField = new JTextField(5);
        middlePanel.add(new JLabel("Resets while immune:", SwingConstants.TRAILING));
        baseTextField.setToolTipText("<HTML>" + "Number of resets a SOL player is allowed<br>" + "after a game, while immune from attack." + "</HTML>");
        baseTextField.setName("NumResetsWhileImmune");
        middlePanel.add(baseTextField);

		bottomPanel.setBorder(BorderFactory.createTitledBorder("SOL Free Build"));

		BaseCheckBox = new JCheckBox("Allow Sol Free Build");
		BaseCheckBox.setToolTipText("<HTML>Allows new players to create their own units based<br> on the build table of your choice, starting units should be set to 0</HTML>");
		BaseCheckBox.setName("Sol_FreeBuild");
		bottomPanel.add(BaseCheckBox);
		
		BaseCheckBox = new JCheckBox("Use All Tables");
		BaseCheckBox.setToolTipText("<HTML>Lets Sol players build from all build tables instead of defining a table.</HTML>");
		BaseCheckBox.setName("Sol_FreeBuild_UseAll");
		bottomPanel.add(BaseCheckBox);

		baseTextField = new JTextField(30);
		bottomPanel.add(new JLabel("Build Table Name:", SwingConstants.TRAILING));
		baseTextField.setToolTipText("<HTML>Set name of build table for new players to build units from.<br>default is Common, or you can create a new set of build tables if you'd like(ex Starter) <br>Starter_Light Starter_Medium and so on. Frequency is irrelavant, if creating a new build table, just set frequency to 1. </HTML>");
		baseTextField.setName("Sol_FreeBuild_BuildTable");
		bottomPanel.add(baseTextField);
		
		baseTextField = new JTextField(5);
		bottomPanel.add(new JLabel("Build Limit", SwingConstants.TRAILING));
		baseTextField.setToolTipText("<html>How many units are players allowed to build. Zero or less disables limit.</html>");
		baseTextField.setName("Sol_FreeBuild_Limit");
		bottomPanel.add(baseTextField);
		
		bottomPanel2.setBorder(BorderFactory.createTitledBorder("Post Defection Free Build"));
				
		BaseCheckBox = new JCheckBox("Allow Free Build Post Defection");
		BaseCheckBox.setToolTipText("<HTML>Set this if you want players to be able to build free units after defection. Uses house table.</HTML>");
		BaseCheckBox.setName("Sol_FreeBuild_PostDefection");
		bottomPanel2.add(BaseCheckBox);
		
		BaseCheckBox = new JCheckBox("Limit Post Defection Only");
		BaseCheckBox.setToolTipText("<HTML>Set this if you want SOL free build to work without limits, and only limit free build after defection</HTML>");
		BaseCheckBox.setName("Sol_FreeBuild_LimitPostDefOnly");
		bottomPanel2.add(BaseCheckBox);
		
		BaseCheckBox = new JCheckBox("Must Create All FreeMeks Before Able to Go Active");
		BaseCheckBox.setToolTipText("<HTML>Set this if you want to disable going active if player has free meks remaining</HTML>");
		BaseCheckBox.setName("Sol_FreeBuild_LimitGoActive");
		bottomPanel2.add(BaseCheckBox);
				
		add(topPanel);
		add(middlePanel);
		add(bottomPanel);
		add(bottomPanel2);
	}

	public NewbieHousePanel() {
		super();
		init();
	}
}
