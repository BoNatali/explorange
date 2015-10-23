package de.ur.mi.bonatali.piebrowser;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.io.File;
import java.util.ArrayList;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.input.ScrollEvent;

import com.mrlonee.radialfx.core.RadialMenuItem;

/*This class is the main controller of the application.
 * 
 * */
public class ExplorangeMain extends Application{
	
	public static void main(final String[] args) {
		launch(args);
	}

	// main layout vars
	private PieMenuBrowser pie;
	private PieSubMenu subPie;
	private Group root;
	private StackPane controlsBottom;
	private StackPane controlsTop;
	
	
	
	private Label gamepadNode;
	private HBox pathBox;

	private InputManager inputManager;
	private Task<Void> listenToGamepad;
	private boolean gamepadActive = false;
	private boolean hoveringSubmenu = false;
	private boolean hoveringCenter = false;
	
	private Robot bot;
	
	private Folder parentFolder;
	private Folder subFolder;
	
	private Label sysStatus; 
	private String noFilesMsg = "Folder does not contain files.";
	private String noGamepad = "Gamepad not plugged in.";
	
	private ImageView infoImg;
	private Label instructionLabel;
	private String instructionPath = "Enter the path to the folder you want to explore.";
	private String instructionMouse = "Click items to open files. Click the center area to \nnavigate back to previous folder. Hover orclick folders to see \ntheir content. Scroll to see more items.";
	private String instructionGamepad = "Press X to open a file. Use left stick to navigate and right stick to \nscroll through items. Press Button A to end Gamepad Mode.";
	
	
	
	
	private String [] itemNames;
	private Image [] itemIcons;
	
	
	private int cursorX; 
	private int cursorY;
	
	private Scene scene;
	
	private Image logoImg;
	private ImageView logoView; 
	
	private double screenWidth; 
	private double screenHeight;
	
	private ArrayList<RadialMenuItem> items;
	private ArrayList<RadialMenuItem> subItems;
	
	private int activeIndex = 0;
	private int activeIndexBuffer = 0;

	
	private TextField pathInput;
	
	@Override
	public void start(Stage stage) throws Exception {

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = screenSize.getWidth();
		screenHeight = screenSize.getHeight();
		
		inputManager = new InputManager();
		
		root = new Group ();
		
		controlsBottom = new StackPane ();
		controlsTop = new StackPane ();
		controlsTop.setPrefWidth(screenWidth - 100);
		controlsBottom.setPrefWidth(screenWidth - 100);
		
		controlsBottom.setLayoutX(0 + 25);
		controlsBottom.setLayoutY(screenHeight - 100);
		
		controlsTop.setLayoutX(0 + 25);
		controlsTop.setLayoutY(0 + 25);

		
		
		bot = new Robot();
		
		
		setUpStatusFeedback();
		setUpDeviceOptions();
		setupLogo ();
		setupURLInput();
		setUpInstructions();
		controlsTop.getChildren().add(logoView);
		StackPane.setAlignment(logoView, Pos.TOP_LEFT);
		root.getChildren().addAll(controlsBottom, controlsTop);
		

		
		
		//scene = new Scene(root, Color.web("#4DA9AC")); //BLUE
		scene = new Scene(root, Color.web("#36022D")); //BERRY
		
		
		stage.setScene(scene);
		scene.getStylesheets().add
		 (getClass().getResource("/style/styles.css").toExternalForm());
		stage.centerOnScreen();
		stage.setWidth(screenWidth);
		stage.setHeight(screenHeight);
		stage.setFullScreen(true);
		stage.toFront();
		stage.show();
		
	}
	
	
	private void setUpStatusFeedback() {
		sysStatus = new Label();
		StackPane.setAlignment(sysStatus, Pos.TOP_RIGHT);
		sysStatus.setTextAlignment(TextAlignment.CENTER);
		controlsTop.getChildren().add(sysStatus);
	}
	
	private void setUpInstructions() {
		
		Group instructions = new Group();
		instructionLabel = new Label (instructionPath);
		
		infoImg = new ImageView (new Image("/res/img/info.png"));
		infoImg.setId("info-img");
		fadeOutInstructions();
		
		instructionLabel.setTranslateX(85);
		
		infoImg.setOnMouseEntered(new EventHandler<MouseEvent>(){
			
			
            public void handle(MouseEvent mouseEvent){
                fadeInInstructions();
            }
        });
		
		infoImg.setOnMouseExited(new EventHandler<MouseEvent>(){
 
            public void handle(MouseEvent mouseEvent){   
                fadeOutInstructions();
            }
        });
		
		instructions.getChildren().addAll(infoImg, instructionLabel);
		controlsBottom.getChildren().add(instructions);
		StackPane.setAlignment(instructions, Pos.BOTTOM_LEFT);
	}
	
	private void fadeInInstructions () {
		 FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), instructionLabel);
		 fadeTransition.setFromValue(0.0);
		 fadeTransition.setToValue(1.0);
		 fadeTransition.play();
	}
	
	private void fadeOutInstructions () {
		FadeTransition fadeTransition = new FadeTransition(Duration.millis(500), instructionLabel);
		fadeTransition.setFromValue(1.0);
		fadeTransition.setToValue(0.0);
		fadeTransition.play();
	}


	private void setUpDeviceOptions() {
		gamepadNode = new Label ("Gamepad");
		gamepadNode.setId("gamepad-opt");
		
		
		
		gamepadNode.setOnMouseClicked(new EventHandler<MouseEvent>(){
            public void handle(MouseEvent mouseEvent){
            	inputManager.readInputDevices();
            	if (inputManager.gamePadIsPluggedIn()) {
					scene.setCursor(Cursor.NONE);
					centerCursor();
            		focusFirstItem();
					gamepadActive = true;
					inputManager.setGamepad();
					setGamepadListener();
					instructionLabel.setText(instructionGamepad);
					Thread tG = new Thread(listenToGamepad);
					tG.start();
					fadeInInstructions();
					inputManager.pollGamepad();
					gamepadNode.setVisible(false);
				} else {
						sysStatus.setText(noGamepad);
						showDialog(sysStatus);
				}
            }

			
        });
		
		StackPane.setAlignment(gamepadNode, Pos.BOTTOM_RIGHT);
		controlsBottom.getChildren().add(gamepadNode);
		
	}

	private void setGamepadListener() {
		listenToGamepad = new Task<Void>() {
		    @Override protected Void call() throws Exception {
		    	while (gamepadActive) {
		    		
		    		
		    		int xDir = inputManager.giveXDirection();
		    		
		    		if (xDir < 0) {
		    			previousItemActive();
		    		} 
		    		if (xDir>0){
		    			nextItemActive();
		    		}
		    		
		    		
		    		int yDir  = inputManager.giveYDirection();

		    		if (yDir < 0) {
		    			moveDownSub();
		    		} 
		    		if (yDir>0){
		    			moveUpSub();
		    		}
		    		
		    		
		    		int zDir  = inputManager.giveZDirection();
		    		
		    		if (zDir < 0) {
		    			nextItemSet();
		    		} 
		    		if (zDir>0){
		    			previousItemSet();
		    		}
		    		
		    		int zRot  = inputManager.giveZRotation();
		    		if (zRot < 0) {
		    			moveDownCenter();
		    		} 
		    		if (zRot>0){
		    			moveUpCenter();
		    		}
		    		
		    		if (inputManager.buttonAPressed()){
		    			break;
		    		}
		    		
		    		if (inputManager.buttonXPressed()){
		    			virtClick();
		    		}
		    		
		    		
					 try {
				         Thread.sleep(700);
				      } catch (InterruptedException e) {
				    	  e.printStackTrace();
				      }
			}
		    	inputManager.stopPolling();
    			resetToMouseInput();
				return null;
		    }
		};
		
	}
				
	protected void moveUpCenter() {
		if (!hoveringSubmenu) {
			if (!hoveringCenter) {
				hoveringCenter = true;
				centerCursor();
			} else {
				hoveringCenter = false;
				focusFirstItem();
			} 
		}
		
	}


	protected void moveDownCenter() {
		moveUpCenter();
		
	}


	protected void previousItemSet() {
		 Platform.runLater(new Runnable() {
		        @Override
		        public void run() {
		        	
		        	if(!hoveringSubmenu){
		    			pie.previousItemSet();
		    			setEventHandlerForItems();
		    		} else {
		    			subPie.previousItemSet();
		    		}
		        }
		    });
		 
		
	}


	protected void nextItemSet() {
		 Platform.runLater(new Runnable() {
		        @Override
		        public void run() {
		        	if(!hoveringSubmenu){
		        		pie.nextItemSet();
		    			setEventHandlerForItems();
		    		} else {
		    			subPie.nextItemSet();
		    		}
		        	
		        }
		    });
		 
		
		
	}


	protected void moveDownSub() {
		if (!hoveringSubmenu){
			submenuActive();
		} else {
			submenuInactive();
		}
	}


	private void submenuActive() {
		 Platform.runLater(new Runnable() {
		        @Override
		        public void run() {
		        	 if (subPie != null){
		     			hoveringSubmenu = true;
		     			activeIndexBuffer = activeIndex;
		     			activeIndex = 0;
		     			moveToItem();
		     		}
		        }
		    });
		
			
	}
	
	private void submenuInactive() {
		hoveringSubmenu = false;
		activeIndex = activeIndexBuffer;
		
	}

	protected void moveUpSub() {
		if (!hoveringSubmenu){
			submenuActive();
		} else {
			submenuInactive();
		}
	}


	


	protected void resetToMouseInput() {
		gamepadActive = false;
    	inputManager.setMouse();
		 Platform.runLater(new Runnable() {
		        @Override
		        public void run() {
		        	scene.setCursor(Cursor.DEFAULT);
		        	instructionLabel.setText(instructionMouse);
		        	fadeOutInstructions();
		        	gamepadNode.setVisible(true);
		        }
		    });
		
    	
	}


	private void moveToItem () {
		RadialMenuItem activeItem = (hoveringSubmenu) ? subItems.get(activeIndex) : items.get(activeIndex);
		Bounds boundsInScene = activeItem.localToScene(activeItem.getBoundsInLocal());
		double x = boundsInScene.getMinX() + boundsInScene.getWidth()/2;
		double y = boundsInScene.getMinY()  + boundsInScene.getHeight()/2;
		bot.mouseMove((int)x, (int)y);
	}
	
	private void centerCursor() {
		hoveringCenter = true;
		cursorX =(int)screenWidth/2;
		cursorY = (int)screenHeight/2;
		bot.mouseMove (cursorX, cursorY);
	}
	
	private void focusFirstItem() {
		moveToItem ();
		
	}
	
	private void nextItemActive() {
		int max = (hoveringSubmenu) ? subItems.size()-1 : items.size()-1;
		if (activeIndex < max){
			activeIndex ++;
		} else {
			activeIndex = 0;
		}

		moveToItem();
	}
	
	private void previousItemActive () {
		if (activeIndex > 0){
			activeIndex --;
		} else {
			activeIndex = (hoveringSubmenu) ? subItems.size()-1 : items.size()-1;
		}
		
		moveToItem();
	}


	private void setupURLInput() {
		pathInput = new TextField ();
		pathInput.setMinWidth(300);
		
		pathBox = new HBox();
		pathBox.getChildren().add(pathInput);
		pathBox.setSpacing(10);
		pathBox.setTranslateX(screenWidth/2 - 300/2);
		pathBox.setTranslateY(screenHeight/2 - pathBox.getHeight()/2);
		root.getChildren().add(pathBox);
		
		
		
		PathEnteredEventHandler pathEnteredHandler = new PathEnteredEventHandler ();
		pathInput.addEventFilter(KeyEvent.KEY_PRESSED, pathEnteredHandler);
	}

	
	private class PathEnteredEventHandler implements EventHandler <KeyEvent> {
		

		@Override
		public void handle(KeyEvent e) {
			if (e.getCode() == KeyCode.ENTER) {
				String path = pathInput.getText();
				
				if (checkIfValidPath(path)){
					setUpPie (path);
					root.getChildren().remove(pathBox);
					instructionLabel.setText(instructionMouse);
				} else {
					pathInput.setText("PATH NOT VALID.");
					
				}
					 
			}
			
		}
			
		}

		private boolean checkIfValidPath(String path) {
			File testFile = new File (path);
			if (testFile.exists()){
				return true;
			} else {
				return false;
			}
		}
		

	private void showDialog(Label dialog) {
		FadeTransition fadeIn  = new FadeTransition(Duration.millis(200), dialog);
		fadeIn.setFromValue(0.0);
		fadeIn.setToValue(1.0);
		fadeIn.play();
		
		FadeTransition fadeOut = new FadeTransition(Duration.millis(1200), dialog);
		fadeOut.setFromValue(1.0);
		fadeOut.setToValue(0.0);
		fadeOut.play();
			
		}


	private void setUpPie(String rootUrl) {
		prepareFolder (rootUrl);
		prepareContents ();
		initiatePie ();
		setEventHandlerForPie();
		setEventHandlerForItems();
		setEventHandlerForCenter();
		StackPane.setAlignment(pie, Pos.CENTER);
		root.getChildren().addAll(pie);
		
	}
	

	private void initiatePie() {
		pie = new PieMenuBrowser(itemNames, itemIcons, parentFolder.getName());
		pie.minHeight(screenHeight);
		pie.prefHeight(screenHeight);
		pie.setTranslateX(screenWidth/2); 
		pie.setTranslateY(screenHeight/2);
		
	}


	private void prepareContents() {
		itemNames = parentFolder.getAllFileNames(true, false);
		itemIcons = new Image [itemNames.length];
		
		for (int i = 0; i<itemNames.length; i++){
			boolean isFolder = parentFolder.isFolder(itemNames [i]);
			itemIcons [i] = (isFolder) ?  IconMap.getFolderIcon() : IconMap.getFileIcon(itemNames [i]);
		}
		
	}


	private void prepareFolder(String rootUrl) {
		parentFolder = new Folder (rootUrl);
	}


	private void setEventHandlerForPie() {
		ItemScrollEventHandler onItemScroll = new ItemScrollEventHandler();
		pie.addEventHandler(ScrollEvent.SCROLL, onItemScroll);
	}


	private void setEventHandlerForCenter() {
		StackPane center = pie.getCenter();
		center.setId("center");
		CenterHoverEventHandler onCenterEnter = new CenterHoverEventHandler();
		CenterClickEventHandler onCenterClick = new CenterClickEventHandler();
		center.addEventFilter(MouseEvent.MOUSE_CLICKED, onCenterClick);
		center.addEventFilter(MouseEvent.MOUSE_ENTERED, onCenterEnter);
		
	}


	private void setEventHandlerForItems () {
		items = pie.getItems();
		
		for (int i=0; i<items.size(); i++) {
			ItemHoverEventHandler onItemEnter = new ItemHoverEventHandler();
			ItemClickEventHandler onItemClick = new ItemClickEventHandler();
			items.get(i).addEventHandler(MouseEvent.MOUSE_ENTERED, onItemEnter);
			items.get(i).addEventHandler(MouseEvent.MOUSE_CLICKED, onItemClick);
		}
		
		ItemClickEventHandler onItemClick = new ItemClickEventHandler();
		ItemHoverEventHandler onItemEnter = new ItemHoverEventHandler();
		pie.setClickHandlerForAssets(onItemClick);
		pie.setHoverHandlerForAssets(onItemEnter);
	}
	


	private void setupLogo() {
		logoImg = new Image ("/res/img/logo7.png");
		logoView = new ImageView ();
		logoView.setImage(logoImg);
		logoView.setPreserveRatio(true);
		logoView.setFitWidth(300);
		StackPane.setAlignment(logoView, Pos.TOP_LEFT);
	}

	private void prepareRootContent () {
		prepareContents();
		pie.updatePie(itemIcons, itemNames, parentFolder.getName());
		setEventHandlerForItems();
	}

	private final class ItemHoverEventHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			RadialMenuItem parent = pie.getActiveItem();
			String activeItemName = pie.getActiveItemName ();

			
			if (root.getChildren().contains(subPie) && !(subPie.getParentItem()==parent)){
					removeSubMenu();
			}
			
			if (!root.getChildren().contains(subPie) && parentFolder.itemContainsFiles(activeItemName)) {
				drawSubPie (parent, activeItemName);
			} else {
				if (parentFolder.isFolder(activeItemName)&& !parentFolder.itemContainsFiles(activeItemName)){
					sysStatus.setText(noFilesMsg);
					showDialog(sysStatus);
				}
				
			}
		}
	}
	
	private final class ItemClickEventHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			
			String activeItemName = pie.getActiveItemName ();
			
			if (!parentFolder.isFolder(activeItemName)){
				parentFolder.openFile(activeItemName);
				return;
			}
		
			if (parentFolder.itemContainsFiles(activeItemName)){
				removeSubMenu();
				String filePath = parentFolder.getFilePath(activeItemName);
				parentFolder = new Folder (filePath);
				prepareRootContent();
			} else {
				sysStatus.setText(noFilesMsg);
				showDialog(sysStatus);
			}
		}
	}
	
	private final class SubItemClickEventHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			String activeItemName = subPie.getActiveItemName ();
			
			if (!subFolder.isFolder(activeItemName)){
				subFolder.openFile(activeItemName);
				return; 
			}
			
			if (subFolder.itemContainsFiles(activeItemName)){
				
				String filePath = subFolder.getFilePath(activeItemName);
				prepareFolder(filePath);
				prepareRootContent();
				removeSubMenu();
		
			} else {
				sysStatus.setText(noFilesMsg);
				showDialog(sysStatus);
			}
		}
	}
	
	private final class CenterClickEventHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			backToParentFolder ();
		}
	}
	
	private final class CenterHoverEventHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent event) {
			if (subPie != null){
				removeSubMenu();
			}
		}
	}
	
	private final class ItemScrollEventHandler implements EventHandler<ScrollEvent> {


		@Override
		public void handle(ScrollEvent e) {
			if (e.getDeltaY()<0){
				pie.nextItemSet();
			}
			if (e.getDeltaY()>0){
				pie.previousItemSet();
			}
			
			setEventHandlerForItems();
		}
	}
	
	private final class SubItemScrollEventHandler implements EventHandler<ScrollEvent> {


		@Override
		public void handle(ScrollEvent e) {
			if (e.getDeltaY()<0){
				subPie.nextItemSet();
			}
			if (e.getDeltaY()>0){
				subPie.previousItemSet();
			}
		}
	}
	
	
	private void drawSubPie(RadialMenuItem parent, String parentName) {
		
		subFolder = new Folder (parentFolder.getFilePath(parentName));
		
		String [] itemNames = subFolder.getAllFileNames(true, false);
		Image [] itemIcons = new Image [itemNames.length];
	
		for (int i = 0; i<itemNames.length; i++){
			
			boolean isFolder = subFolder.isFolder(itemNames [i]);
			itemIcons [i] = (isFolder) ?  IconMap.getFolderIcon() : IconMap.getFileIcon(itemNames [i]);
		}
		
		
		subPie = new PieSubMenu (parent, itemNames, itemIcons);
		subPie.draw();
		subPie.setTranslateX(screenWidth/2); 
		subPie.setTranslateY(screenHeight/2);
		root.getChildren().addAll(subPie);
		setEventHandlerForSubItems();
	} 
	


	private void setEventHandlerForSubItems () {
		SubItemScrollEventHandler onSubItemScroll = new SubItemScrollEventHandler();
		subPie.addEventHandler(ScrollEvent.SCROLL, onSubItemScroll);
		subItems = subPie.getItems();
		
		for (int i=0; i<subItems.size(); i++) {
			RadialMenuItem currentItem = subItems.get(i);
			SubItemClickEventHandler onItemClick = new SubItemClickEventHandler();
			currentItem.addEventHandler(MouseEvent.MOUSE_CLICKED, onItemClick);
		}
		
		SubItemClickEventHandler onItemClick = new SubItemClickEventHandler();
		subPie.setHandlerForAssets(onItemClick);
	}
	
	
	public void backToParentFolder() {
		String rootURL = parentFolder.getFolderParentURL();
		if (!rootURL.endsWith("Users")) {
			parentFolder = new Folder (rootURL);
			prepareRootContent ();
		}
	}
	

	private void virtClick() throws AWTException {
		 bot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
		 bot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
	}

	private void removeSubMenu () {
		root.getChildren().removeAll(subPie);
		subPie.vanish();
		subPie = null;
		subFolder = null;
	}
}
