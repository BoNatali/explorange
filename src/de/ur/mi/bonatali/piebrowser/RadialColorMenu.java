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

import java.util.List;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import com.mrlonee.radialfx.core.RadialMenuItem;
import com.mrlonee.radialfx.core.RadialMenuItemBuilder;

import de.ur.mi.bonatali.piebrowser.IconMap;

/*This class is a modfied version of MrLoNees RadialColorMenu.*/

public class RadialColorMenu extends Group {

	
    private final double minOffset = 5;
   
    
    private Circle center;
    private String rootName;
    private MenuItemLabel centerText;
    private StackPane centerStack = new StackPane ();
    private ObjectProperty<Paint> selectedColor;
    
  
    
    private ArrayList <Pane> itemAssets = new ArrayList <Pane>();
    private Color [] orangeHues = new Color [8];
    
    private int setSize = 8;
    private int itemSet = 0;
    private int maxNumOfSets;
    
    private String [] allNames;
    private Image [] allIcons;
    
    private String [] nameSet;
    
    private RadialMenuItem activeItem;
    private ArrayList <RadialMenuItem> items = new ArrayList <RadialMenuItem>();
    private ArrayList <MenuItemLabel> labels = new ArrayList <MenuItemLabel>();
    private ArrayList <ImageView> icons = new ArrayList <ImageView>();
    
    


    public RadialColorMenu(String [] itemNames, Image [] itemIcons, String rootName) {
    	selectedColor = new SimpleObjectProperty<Paint>(Color.web("#fdbc55"));

    	new HashMap<RadialMenuItem, List<Text>>();
    	
    	
    	this.rootName = rootName;
    	this.allNames = itemNames;
    	this.allIcons = itemIcons;

    	
    	
    	if (allNames.length < setSize){
    		setSize = itemNames.length;
    		nameSet = itemNames;
    		maxNumOfSets = 0;
    	} else {
    		int rest = allNames.length%8;
    		maxNumOfSets = (allNames.length-rest)/8;
    	}
    	
    	
    	
    	updateSlices ();

		mapIconsAndNamesToItems();
		updateLabels (); 
		
		setUpMenuCenter();
		getChildren().add(centerStack);
		centerStack.toFront();
	
    }
 
	private void setUpMenuCenter() {
    	setUpCircle ();
    	setUpDetails ();
    	centerStack.translateXProperty().bind(centerStack.widthProperty().divide(-2.0));
    	centerStack.translateYProperty().bind(centerStack.heightProperty().divide(-2.0));
		
	}
	private void setUpDetails() {
    	centerText = new MenuItemLabel (rootName);
    	StackPane.setAlignment(centerText, Pos.BOTTOM_CENTER);
    	StackPane.setMargin(centerText, new Insets (0.0, 0.0, 20.0, 0.0));
    	
    	ImageView centerIcon = new ImageView ();
    	centerIcon.setImage(IconMap.getFolderIcon());
    	
    	centerStack.getChildren().addAll(centerText, centerIcon); 
    	centerStack.toFront();
    	
    	Circle centerShadow = new Circle();
    	Color highlight = Color.web("#36022D");
    	centerShadow.fillProperty().bind( new SimpleObjectProperty<Paint>(highlight));
    	centerShadow.setRadius (center.getRadius() + minOffset-1.0);
    	centerShadow.setCenterX(0);
		centerShadow.setCenterY(0);
		
    	centerStack.getChildren().add(centerShadow);
    	centerShadow.toBack();
    	centerStack.setOnMouseEntered(new EventHandler<MouseEvent>(){
            public void handle(MouseEvent mouseEvent){
            	center.setRadius(center.getRadius()+ 20.0);
            	centerShadow.setRadius (center.getRadius() + minOffset-1.0);
            }
        });
    	
    	centerStack.setOnMouseExited(new EventHandler<MouseEvent>(){
            public void handle(MouseEvent mouseEvent){
            	center.setRadius(center.getRadius()- 20.0);
            	centerShadow.setRadius (center.getRadius()+ minOffset-1.0);
            }
        });
	}
	
	private void setUpCircle() {
		center = new Circle();
		center.fillProperty().bind(selectedColor);
		center.setRadius(80);
		center.setCenterX(0);
		center.setCenterY(0);
    	
    	centerStack.getChildren().add(center);
	}
	
	private void updateCenterLabel (String text) {
		centerText.setClippedText(text);
	}
	
	public StackPane getCenter () {
    	return centerStack;
    }
    
  

	private Color [] randomizeColors() {
		Color [] colorHues = new Color[] { Color.web("#fdbc55"), Color.web("#fdbc55"), Color.web("#ffdba2"),
				Color.web("#fdbc68"), Color.web("#edcc97"), Color.web("#edb459"), 
				Color.web("#e09c31"),Color.web("#edcc97"),Color.web("#ffb43e"), 
				Color.web("ffb43e"), Color.web("f9ae5e"), Color.web("eeae5e"), Color.web("e5Ab4d")};
		
		Color [] colors = new Color [8];
		
		Random rGen = new Random ();
		for (int i=0; i<colors.length ;i++) {
			int rInt = rGen.nextInt(colorHues.length);
			colors [i] = colorHues [rInt];
		}
		return colors;
	}
    

    private void updateSlices () {
    	orangeHues = randomizeColors ();

		final Color[] colors = orangeHues;

		int i = colors.length;
		double lengthMax = 360d / colors.length;
		
		for (final Color color : colors) {
			if (true){
				if (i>2){
					addColorItem(color, (i * 360d / colors.length)  + 67.50, 360d / colors.length); 
				} else {
					addColorItem(color, (i * 360d / colors.length) + 67.50, lengthMax);
				}
				
			}
		    
		    i--;
		}
		
		getChildren().addAll(items);
		getChildren().addAll(itemAssets);
		setEventListenersForItems();
		
    }

	private void setEventListenersForItems() {
		for (int i=0; i<setSize; i++){
			ItemOnEventHandler mouseHandler = new ItemOnEventHandler(items.get(i));
			items.get(i).addEventHandler(MouseEvent.MOUSE_ENTERED, mouseHandler);
			items.get(i).addEventHandler(MouseEvent.MOUSE_EXITED, mouseHandler);
			itemAssets.get(i).addEventHandler(MouseEvent.MOUSE_ENTERED, mouseHandler);
			itemAssets.get(i).addEventHandler(MouseEvent.MOUSE_EXITED, mouseHandler);
			itemAssets.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, mouseHandler);
		}
	}
	
	public void setClickHandlerForAssets (EventHandler<MouseEvent> handler) {
		for (int i=0; i<itemAssets.size(); i++){
			itemAssets.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, handler);
		}
	}
	public void setHoverHandlerForAssets (EventHandler<MouseEvent> handler) {
		for (int i=0; i<itemAssets.size(); i++){
			itemAssets.get(i).addEventHandler(MouseEvent.MOUSE_ENTERED, handler);
		}
	}
	
	private void addColorItem(final Color color, final double startAngle,
	    final double length) {

    double screenHeight = Toolkit.getDefaultToolkit().getScreenSize().getHeight();
	

	final RadialMenuItem colorItem = RadialMenuItemBuilder.create()
		.startAngle(startAngle).length(length).backgroundFill(color)
		.backgroundMouseOnFill(color).strokeVisible(false).offset(minOffset)
		.innerRadius(80).radius(screenHeight/2 - 140).build();
	
	if (items.size() <1){
		activeItem = colorItem;
	}
	
	createAssets (colorItem);
	items.add(colorItem);

    }
	


    private void createAssets(RadialMenuItem colorItem) {
		MenuItemLabel label = new MenuItemLabel (); 
		
		ImageView icon = new ImageView ();
		icon.setPreserveRatio(true);
		icon.setFitHeight(42);
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
    	
		double assetX = assetRadius * Math.cos(Math.toRadians(assetAngle))- 60.0;
		double assetY = -assetRadius * Math.sin(Math.toRadians(assetAngle)) - 32.0;

    	double translateX = parent.getOffset() * Math.cos(Math.toRadians(parent.getStartAngle() + (parent.getLength() / 2.0)));
    	double translateY = -parent.getOffset() * Math.sin(Math.toRadians(parent.getStartAngle() + (parent.getLength()/ 2.0)));
    	   
    	assets.setTranslateX(assetX + translateX);
        assets.setTranslateY(assetY + translateY); 
	}



	private final class ItemOnEventHandler implements EventHandler<MouseEvent> {

	private final RadialMenuItem colorItem;
	
	private ItemOnEventHandler(final RadialMenuItem colorItem) {
	    this.colorItem = colorItem; 
	}

	@Override
	public void handle(final MouseEvent event) {
		EventType<? extends MouseEvent> action = event.getEventType ();
		double delta = 12;
		double deltaInner = 6;
		activeItem = colorItem;
		
		
	    if (action == MouseEvent.MOUSE_ENTERED) {
	    	double radiusBuffer = activeItem.getRadius();
	    	activeItem.setRadius (radiusBuffer + delta);
	    	
	    	double innerBuffer = activeItem.getInnerRadius();
	    	activeItem.setInnerRadius (innerBuffer - deltaInner);
	    	
	    }
	    
	    if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
	    	
		}
	    
	    if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
	    	double radiusBuffer = activeItem.getRadius();
	    	activeItem.setRadius (radiusBuffer - delta);
	    	double innerBuffer = activeItem.getInnerRadius();
	    	activeItem.setInnerRadius (innerBuffer + deltaInner);
	    }
	}
    }
    
    


    public void updatePie (Image [] fileIcons, String [] fileNames, String rootName) {
    	
    	itemSet = 0;
    	setSize = 8;
    	
    	this.allNames = fileNames.clone();
    	this.allIcons = fileIcons.clone();
    	this.rootName = rootName;

    	if (allNames.length < setSize){
    		setSize = fileNames.length;
    		nameSet = fileNames;
    		maxNumOfSets = 0;
    	} else {
    		int rest = allNames.length%8;
    		maxNumOfSets = (allNames.length-rest)/8;
    	}
    	
    	this.rootName = rootName;
    	updateCenterLabel (rootName);
    	getChildren().removeAll(itemAssets);
    	getChildren().removeAll(labels);
    	getChildren().removeAll(items);
    	
    	items.clear();
    	labels.clear();
    	icons.clear ();
    	itemAssets.clear();
    	
    	updateSlices();
    	mapIconsAndNamesToItems ();
    	updateLabels (); 
    	centerStack.toFront();
    }
    
    
	private void mapIconsAndNamesToItems() {
		nameSet = new String [setSize];
		
		boolean hitRangeEnd = false;
		int j = 0;
		
		for (int i = 0; i < setSize; i++) {
			
			if (hitRangeEnd) {
				j++;
			}
			
			if (!hitRangeEnd) {
				j = i + itemSet*setSize;
			}
			
			if (j == allNames.length) {
				hitRangeEnd = true;
				j = 0;
			}
			
			
			nameSet [i] = allNames[j];
			icons.get(i).setImage(allIcons[j]);
			
		}
		
	}
		
	public void previousItemSet () {
		if (allNames.length>8) {
			
			if (itemSet == 0) {
				itemSet = maxNumOfSets;
			} else {
				itemSet --;
			}
	
	    	getChildren().removeAll(itemAssets);
	    	getChildren().removeAll(labels);
	    	getChildren().removeAll(items);
	    	
	    	items.clear();
	    	labels.clear();
	    	icons.clear ();
	    	itemAssets.clear();
			
			
			updateSlices();
			mapIconsAndNamesToItems();
			updateLabels();
			centerStack.toFront();
		}
	}
	
	public void nextItemSet() {
		if (allNames.length>8) {

			
			if (itemSet == maxNumOfSets){
				itemSet = 0;
			} else {
				itemSet ++;
			}
			
			System.out.println("SET1: " + itemSet);
			
			
	    	getChildren().removeAll(itemAssets);
	    	getChildren().removeAll(labels);
	    	getChildren().removeAll(items);
	    	
	    	items.clear();
	    	labels.clear();
	    	icons.clear ();
	    	itemAssets.clear();
			
			
			updateSlices();
			mapIconsAndNamesToItems();
			updateLabels();
			centerStack.toFront();
		}
	}
	
	private void updateLabels() {
		for (int i=0; i< nameSet.length; i++) {
		  labels.get(i).setClippedText(nameSet[i]);
		}
		
				
	}
	
	public void spinPieBackwards () {
		
	}

	public RadialMenuItem getActiveItem () {
		return activeItem;
		
	}
	
	public String getActiveItemName() {
		return nameSet[items.indexOf(activeItem)];
	}
	
	public ArrayList <RadialMenuItem> getItems () {
		ArrayList <RadialMenuItem> set = new ArrayList <RadialMenuItem> ();
		for (int i=0; i<setSize; i++){
			set.add(items.get(i));
		}
		
		return set;
	}
	
    public ObjectProperty<Paint> selectedColorProperty() {
	return selectedColor;
    }

    public Paint getSelectedColor() {
	return selectedColor.get();
    }
}
