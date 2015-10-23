package de.ur.mi.bonatali.piebrowser;

import java.util.Arrays;

import javafx.scene.image.Image;

public class IconMap {
	
	//most common file extensions grouped as categories 
	private static String [] imageFormats = {"jpg", "jpeg", "png", "bmp"} ;
	private static String [] archiveFormats = {"zip", "rar", "7z"};
	private static String [] documentFormats = {"pdf", "doc", "docx", "odt"};
	private static String [] audioFormats = {"mp3", "wav", "wma"};
	private static String [] videoFormats = {"mp4", "wmv", "mpg", "mpeg", "avi"};
	
	
	private static Image folderImg = new Image ("/res/img/folder.png"); 
	private static Image imageImg = new Image ("/res/img/img.png"); 
	private static Image archiveImg = new Image ("/res/img/archive.png"); 
	private static Image documentImg = new Image ("/res/img/document.png"); 
	private static Image audioImg = new Image ("/res/img/audio.png"); 
	private static Image videoImg = new Image ("/res/img/video.png"); 
	private static Image defaultImg = documentImg; 
	
	public static Image getFileIcon (String fileName) {
		
		
		//extract file extension
		String fileType;
		int typeStart = fileName.lastIndexOf('.');
		
		if (typeStart == -1) {
			//no dot -> no file extension -> folder
			return folderImg;
		} else {
			fileType = fileName.substring(typeStart+1, fileName.length());
		}
		
		
		//set image according to type
		if (isImage(fileType)) {
			return imageImg;
			
		}
	
		
		
		if (isArchive(fileType)) {
			return archiveImg;
			
		}
		
		if (isDocument(fileType)) {
			return documentImg;
			
		}
		
		if (isAudio(fileType)) {
			return audioImg;
			
		}
		
		if (isVideo(fileType)) {
			return videoImg;
			
		}

		return defaultImg;
	}
	
	public static Image getFolderIcon () {
		return folderImg;
	}
	
	private static boolean isImage (String fileExtension) {
		return Arrays.asList(imageFormats).contains(fileExtension);
	}
	
	private static boolean isDocument (String fileExtension) {
		return Arrays.asList(documentFormats).contains(fileExtension);
	}
	
	private static boolean isArchive (String fileExtension) {
		return Arrays.asList(archiveFormats).contains(fileExtension);
	}
	
	private static boolean isAudio (String fileExtension) {
		return Arrays.asList(audioFormats).contains(fileExtension);
	}
	
	private static boolean isVideo (String fileExtension) {
		return Arrays.asList(videoFormats).contains(fileExtension);
	}

}
