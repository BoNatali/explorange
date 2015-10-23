/**
 * Copyright 2014 (C) Mr LoNee - (Laurent NICOLAS) - www.mrlonee.com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.ur.mi.bonatali.piebrowser;

import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.beans.property.ObjectProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import com.mrlonee.radialfx.core.RadialMenuItem;
import com.mrlonee.radialfx.core.RadialMenuItemBuilder;

public class PieSubMenu extends Group {

   
    private final double minOffset = 5;
    private ArrayList <RadialMenuItem> subItems = new ArrayList <RadialMenuItem>();
    private ArrayList <MenuItemLabel> labels = new ArrayList <MenuItemLabel>();
    private ArrayList <ImageView> icons = new ArrayList <ImageView>();
    private ArrayList <Pane> itemAssets = new ArrayList <Pane>();
   

    private RadialMenuItem parent;
    private RadialMenuItem activeItem;
    
    private int setSize = 8;
    private int setNum = 0;
    private int maxNumOfSets = 0;
    
    private String [] allNames;
    private String [] setNames;
    private Image [] allIcons;


    public PieSubMenu(RadialMenuItem parent, String [] itemNames, Image [] itemIcons) {
    	
    	this.parent = parent;
    	this.allNames = itemNames;
		this.allIcons = itemIcons;
		
		if (itemNames.length<setSize) {
			setSize = itemNames.length;
			maxNumOfSets = 0;
		} else {
			int rest = allNames.length%8;
			maxNumOfSets = (allNames.length-rest)/8;
		}

    }
    
    private void setUpSubMenu (Color parentColor, double startangle) {
    	final Color[] colors = new Color[8];
    	Arrays.fill(colors, parentColor);
    	
    		int i = -4;
    		double decLength = 180d/8;
    		for (final Color color : colors) {
    				addColorSubItem(color, (startangle - (i*  decLength)), decLength);
    				i++;
    		}
    }

    private void addColorSubItem(Color color, double startAngle, double length) {
    	double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();


    	
    	
    	RadialMenuItem subItem = RadialMenuItemBuilder.create()
    			.startAngle(startAngle).length(length).backgroundFill(color)
    			.backgroundMouseOnFill(color).strokeVisible(false).offset(minOffset)
    			.innerRadius(screenHeight/2 - 140+4).radius(screenHeight/2 - 20). build();
    	
    	subItems.add (subItem);
    	
    	createAssets (subItem);
		
	}
    
    private void createAssets(RadialMenuItem colorItem) {
		MenuItemLabel label = new MenuItemLabel (); 
		
		ImageView icon = new ImageView ();
		icon.setPreserveRatio(true);
		icon.setFitHeight(32);
		icon.setMouseTransparent(true);
		
		Pane assets = new Pane();
		VBox assetBox = new VBox();
		assetBox.setAlignment(Pos.CENTER);
		assetBox.getChildren().addAll(icon, label);
		assets.getChildren().addAll(assetBox);
		
		
		positionAssets (assets, colorItem);
		
		icons.add(icon);
		labels.add(label);
		itemAssets.add(assets);
	}

	private void positionAssets(Pane assets, RadialMenuItem parent) {
		double assetAngle = parent.getStartAngle()+ (parent.getLength() / 2.0);
    	double assetRadius = parent.getInnerRadius() + (parent.getRadius() - parent.getInnerRadius()) / 2.0;
    	
		double assetX = assetRadius * Math.cos(Math.toRadians(assetAngle))- 42.0;
		double assetY = -assetRadius * Math.sin(Math.toRadians(assetAngle)) - 22.0;

    	double translateX = parent.getOffset() * Math.cos(Math.toRadians(parent.getStartAngle() + (parent.getLength() / 2.0)));
    	double translateY = -parent.getOffset() * Math.sin(Math.toRadians(parent.getStartAngle() + (parent.getLength()/ 2.0)));
    	   
    	assets.setTranslateX(assetX + translateX);
        assets.setTranslateY(assetY + translateY); 
	}
    
    private void setEventListenersForItems() {
		for (int i=0; i<setSize; i++){
			SubItemEventHandler mouseHandler = new SubItemEventHandler(subItems.get(i));
			subItems.get(i).addEventHandler(MouseEvent.MOUSE_ENTERED, mouseHandler);
			subItems.get(i).addEventHandler(MouseEvent.MOUSE_EXITED, mouseHandler);
			itemAssets.get(i).addEventHandler(MouseEvent.MOUSE_ENTERED, mouseHandler);
			itemAssets.get(i).addEventHandler(MouseEvent.MOUSE_EXITED, mouseHandler);
			itemAssets.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseHandler);
			
		}
	}
    


    private final class SubItemEventHandler implements EventHandler<MouseEvent> {
    	
	    RadialMenuItem subItem; 
	    
	    private SubItemEventHandler (RadialMenuItem subItem) {
	    	this.subItem = subItem;
	    }
	
		@Override
		public void handle(final MouseEvent event) {
			EventType<? extends MouseEvent> action = event.getEventType ();
			double delta = 12;
			activeItem = subItem;
		   
		   if (action == MouseEvent.MOUSE_ENTERED) {
		    	double radiusBuffer = activeItem.getRadius();
		    	activeItem.setRadius (radiusBuffer + delta);
		    
		    	
		    }
		    
		    if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
		    	
			}
		    
		    if (event.getEventType() == MouseEvent.MOUSE_EXITED ) {
		    	double radiusBuffer = activeItem.getRadius();
		    	activeItem.setRadius (radiusBuffer - delta);
		    }
		    
		}
	
    }

	public RadialMenuItem getActiveItem () {
		return activeItem;
	}
	
	
	public void vanish () {
		getChildren().removeAll(subItems);
		getChildren().removeAll(itemAssets);
    	subItems.clear();
    	labels.clear();
    	itemAssets.clear();
	}
	
	public void draw () {
		ObjectProperty<Paint> parentPaint = parent.backgroundFillProperty();
		Color parentColor =  (Color) parentPaint.getValue();
		double startAngle = parent.getStartAngle();
		setUpSubMenu(parentColor, startAngle);
		mapIconsAndNamesToItems();
		updateLabels();
		setEventListenersForItems();
		getChildren().addAll(subItems); 
		getChildren().addAll(itemAssets);
	}
	
	
	public void setHandlerForAssets (EventHandler<MouseEvent> handler) {
		for (int i=0; i<itemAssets.size(); i++){
			itemAssets.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
		}
	}

	public ArrayList <RadialMenuItem> getItems () {	
		ArrayList <RadialMenuItem> set = new ArrayList <RadialMenuItem> ();
		for (int i=0; i<setSize; i++){
			set.add(subItems.get(i));
		}
		return set;
	}
	

	public String getActiveItemName() {
		return setNames[subItems.indexOf(activeItem)];
	}
	
	
	
	private void mapIconsAndNamesToItems() {
		setNames = new String [setSize];
		
		boolean hitRangeEnd = false;
		int j = 0;
		
		for (int i = 0; i < setSize; i++) {
			
			if (hitRangeEnd) {
				j++;
			}
			
			if (!hitRangeEnd) {
				j = i + setNum*setSize;
			}
			
			if (j == allNames.length) {
				hitRangeEnd = true;
				j = 0;
			}
			
			
			setNames [i] = allNames[j];
			icons.get(i).setImage(allIcons[j]);
			
		}
	}
		
	
	public void previousItemSet () {
		if (allNames.length>8) {
			
			if (setNum == 0) {
				setNum = maxNumOfSets;
			} else {
				setNum --;
			}
			
			mapIconsAndNamesToItems();
			updateLabels();
		}
	}
	
	public void nextItemSet() {
		if (allNames.length>8) {
			if (setNum == maxNumOfSets){
				setNum = 0;
			} else {
				setNum ++;
			}
			mapIconsAndNamesToItems();
			updateLabels();
		}
	}
	
	public RadialMenuItem getParentItem () {
		return parent;
	}
	
	private void updateLabels() {
		for (int i=0; i< setSize; i++) {
		  labels.get(i).setClippedText(setNames[i]);
		}
		
				
	}

}
