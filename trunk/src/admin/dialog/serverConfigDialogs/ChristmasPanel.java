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

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.SpringLayout;

import common.util.SpringLayoutHelper;

/**
 * @author jtighe
 * @author Spork
 */
public class ChristmasPanel extends JPanel {

	private static final long serialVersionUID = -1527400045914887851L;
	private JCheckBox BaseCheckBox = new JCheckBox();
	
	public ChristmasPanel() {
		super();

        JPanel dbSpring = new JPanel(new SpringLayout());

        BaseCheckBox = new JCheckBox("Allow Scrapping");
        BaseCheckBox.setToolTipText("Allow Christmas Units to be scrapped");
        BaseCheckBox.setName("Christmas_AllowScrap");
        dbSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow BM Sales");
        BaseCheckBox.setToolTipText("Allow Christmas Units to be sold on the Black Market");
        BaseCheckBox.setName("Christmas_AllowBM");
        dbSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Direct Sales");
        BaseCheckBox.setToolTipText("Allow Christmas Units to be sold to other players");
        BaseCheckBox.setName("Christmas_AllowDirectSell");
        dbSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Transfer");
        BaseCheckBox.setToolTipText("Allow Christmas Units to be transferred to other players");
        BaseCheckBox.setName("Christmas_AllowTransfer");
        dbSpring.add(BaseCheckBox);

        BaseCheckBox = new JCheckBox("Allow Donate");
        BaseCheckBox.setToolTipText("Allow Christmas Units to be donated to faction bays");
        BaseCheckBox.setName("Christmas_AllowDonate");
        dbSpring.add(BaseCheckBox);

        
        SpringLayoutHelper.setupSpringGrid(dbSpring, 2);
        add(dbSpring);
	}

}