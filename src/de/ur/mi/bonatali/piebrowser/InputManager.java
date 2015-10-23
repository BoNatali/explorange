package de.ur.mi.bonatali.piebrowser;

import net.java.games.input.Component;
import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Event;
import net.java.games.input.EventQueue;

import javafx.concurrent.Task;

public class InputManager {
	
	private static boolean mouseActive = true;
	private static boolean gamepadActive = false;
	private static boolean gamepadPluggedIn = false;
	
	private static Task<Void> pollGamepad;
	
	private static Event gamePadEv;
	
	private static Controller gamePad;
	
	private Component axisX;
	private Component axisY;
	private Component axisZ;
	private Component rotationZ;
	private Component buttonX;
	private Component buttonA;
	private Component pov;
	
	float buttonACurrentValue = 0.0f;
	private float buttonXCurrentValue = 0.0f;
	private float axisXValue = 0.0f;
	private float axisYValue = 0.0f; 
	
	private int yDir = 0;
	private int xDir = 0;
	private int zDir = 0;
	private int zRot = 0;
	
	public InputManager () {
		
	}
	
	public void readInputDevices () {
		setUpPollTask ();

		Controller[] ca = ControllerEnvironment.getDefaultEnvironment().getControllers();

	     for(int i =0;i<ca.length && gamePad == null;i++){
	         System.out.println("Controller:" + ca[i].getName());
	         System.out.println("Type: "+ca[i].getType().toString());
	         
	         if (ca[i].getType() == Controller.Type.GAMEPAD){
	        	 gamepadPluggedIn = true;
	        	 gamePad = ca[i];
	        	 
	        	 Component[] components = ca[i].getComponents();
		            for(int j=0;j<components.length;j++){
		                mapComponent(components[j]);
		            }
	         }
	     }
	}
	
	public void pollGamepad () {
		if (gamePad != null) {
			System.out.println("pollTheData");
			Thread thG = new Thread(pollGamepad);
	        thG.start();
		}
		
	}
	
	public void stopPolling() {
		if (gamePad != null) {
			gamepadActive=false;
		}
	}
	
	public boolean buttonXPressed () {
		if (buttonXCurrentValue != 0.0f) {
			buttonXCurrentValue = 0.0f;
			System.out.println("button pressed once");
			return true;
		} else {
			return false;
		}
	}
	
	public boolean buttonAPressed () {
		if (buttonACurrentValue != 0.0f) {
			buttonACurrentValue = 0.0f;
			System.out.println("button a pressed once");
			return true;
		} else {
			return false;
		}
	}
	
	public int giveXDirection () {
		int x = xDir; 
		xDir = 0;
		return x;
	}
	
	public int giveYDirection () {
		int y = yDir;
		yDir = 0;
		return y;
	}
	
	public int giveZDirection () {
		System.out.println("Thank you for data" + zDir);
		int z = zDir; 
		zDir = 0;
		return z;
	}
	
	public int giveZRotation () {
		System.out.println("Thank you for data" + zDir);
		int z = zRot; 
		zRot = 0;
		return z;
	}
	
	public void updateInput () {
		
	}
	
	private void setUpPollTask() {
		pollGamepad = new Task<Void>() {
		    @Override protected Void call() throws Exception {
		    	while (gamepadActive) {
		    		
		    		if (isCancelled()) {
		                updateMessage("Cancelled");
		                break;
		            }
		    		
					gamePad.poll();
					
					 EventQueue queue = gamePad.getEventQueue();
					 gamePadEv = new Event ();
					 
					 while(queue.getNextEvent(gamePadEv)) {
			               StringBuffer buffer = new StringBuffer(gamePad.getName());
			               gamePadEv.getNanos();
			               
			               Component comp = gamePadEv.getComponent();
			               float value = gamePadEv.getValue(); 
			               setComponentValue (comp, value);
			               System.out.println("Name: " + comp.getName()+"identiefier: " + comp.getIdentifier() + "Value: " + value);
			               if(comp.isAnalog()) {
			            	   //component has distinc value
			                  buffer.append(value);
			               } else {
			                  if(value==1.0f) {
			                	  //component is on
			                  } else {
			                	  //component is off
			                  }
			               }
			            }
		    		
					 
					 try {
				         Thread.sleep(20);
				      } catch (InterruptedException e) {
				    	  if (isCancelled()) {
			                    updateMessage("Cancelled");
			                    break;
			                }
				      }
			}
		            
		        return null;
		    }
		};
		
	}
	

	private void setComponentValue (Component comp, float value) {
		Component currentComp = comp;
		
		if(currentComp == pov &&value > 0.0f){
			if(value > 0.0f)
			buttonXCurrentValue = value;
		}
		
		if(currentComp == buttonX &&value > 0.0f){
			if(value > 0.0f)
			buttonXCurrentValue = value;
		}
		
		if(currentComp == buttonA && value > 0.0f){
			buttonACurrentValue = value;
		}
		
		if(currentComp == axisX){
			axisXValue = value;
			if (axisXValue < -0.5f){
				xDir = -1;
			} 
			if (axisXValue > 0.5f){
				xDir = 1;
			}
		}
		
		if(currentComp == axisY){
			axisYValue = value;
			// values swapped because of value anomalies with tested controller
			if (axisYValue < -0.5f){
				yDir = 1;
			} 
			if (axisYValue >0.5f){
				yDir = -1;
			}
		}
		
		if(currentComp == axisZ){
			if (value < -0.6f){
				zDir = -1;
			} 
			if (value >0.6f){
				zDir = 1;
			}
		}
		
		if(currentComp == rotationZ){
			if (value < -0.8f){
				zRot = -1;
			} 
			if (value > 0.8f){
				zRot = 1;
			}
		}
	}

	private void mapComponent(Component comp) {
		if (pov == null && comp.getIdentifier() == Component.Identifier.Axis.POV) {
			pov = comp;
		}
		
		if (axisY == null && comp.getIdentifier() == Component.Identifier.Axis.Y) {
			axisY = comp;
		}
		
		if (axisX == null && comp.getIdentifier() == Component.Identifier.Axis.X) {
			axisX = comp;
		}
		
		if (axisZ == null && comp.getIdentifier() == Component.Identifier.Axis.Z) {
			axisZ = comp;
		}
		
		if (rotationZ == null && comp.getIdentifier() == Component.Identifier.Axis.RZ) {
			rotationZ = comp;
		}

		if (buttonX == null && comp.getIdentifier()== Component.Identifier.Button._3) {
			buttonX = comp;
		}
		
		if (buttonA == null && comp.getIdentifier()== Component.Identifier.Button._2){
			buttonA = comp;
		}
		
	}

	public void setMouse () {
		mouseActive = true;
		gamepadActive = false;
	}
	
	public void setGamepad () {
		gamepadActive = true;
		mouseActive = false;
		
	}
	
	public  boolean isMouse () {
		return mouseActive;
	}
	
	public boolean isGamepad () {
		return gamepadActive;
	}
	
	public boolean gamePadIsPluggedIn () {
		return gamepadPluggedIn;
	}
}
