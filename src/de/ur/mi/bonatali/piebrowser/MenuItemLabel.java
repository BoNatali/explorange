package de.ur.mi.bonatali.piebrowser;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.TextAlignment;

public class MenuItemLabel extends Label {
	
	//like Label, but shortens the Text and shows when hovering

	private String labelText;
	private String unclippedText;
	private int textSize = 10;
	
	public MenuItemLabel (String text) {
		labelText = text;
		unclippedText = text;
	
		setText (formatText (text)); 
		setTextAlignment(TextAlignment.CENTER);
		
		setMouseListeners();
	
	}
	
	public MenuItemLabel () {
		setMouseListeners();
	}
	
	private void setMouseListeners() {
		setOnMouseEntered(new EventHandler<MouseEvent>(){
			 public void handle(MouseEvent mouseEvent){  
				 if (unclippedText != null){
					 setText (unclippedText);
				 }
				 
			 }
		 });
		 
		 setOnMouseExited(new EventHandler<MouseEvent>(){
			 public void handle(MouseEvent mouseEvent){  
				 if (unclippedText != null) {
					 setText (labelText);
				 }
				 
			 }
		 });
	}
	
	private String clipLabelText(String text) {
		String clipped = text.substring(0, textSize-3) + "\u2026" + text.substring(text.length()-4, text.length());
		return clipped;
	}
	
	private String extendLabelText(String text) {
		String extended = text;
		int delta = textSize-text.length();
		for (int i=delta; i>0; i-=2) {
			extended = " " + extended + " ";
		}
		return extended;
	}
	
	public void setClippedText (String text) {
		unclippedText = null;
		labelText = null;
		setText (formatText (text));
		setTextAlignment(TextAlignment.CENTER);
	}

	private String formatText(String text) {
		if (text.length() == textSize) {
			return text;
		}
		
		if (text.length() > textSize +1){
			labelText = clipLabelText(text);
			unclippedText = text;
			return labelText;
		} 
		
		if (text.length() < textSize) {
			labelText = extendLabelText(text);
			return labelText;
		}
		return text;
		
		
	}

	
	
}
