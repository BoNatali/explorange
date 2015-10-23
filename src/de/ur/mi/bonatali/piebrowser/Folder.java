package de.ur.mi.bonatali.piebrowser;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Folder {
	

	private File folder;
	private String name;
	private String [] names;
	private File [] items;

	
	
	public Folder (String url) {
		folder = new File (url);
		name = folder.getName();
		items = folder.listFiles(); //directories and files
		
		listFileNames();

	}
	
	private void listFileNames() {
		names = new String [items.length];
		
		for (int i=0; i<items.length; i++){
			names [i] = items [i].getName();
		}
		
	}

	public String [] getAllFileNames (boolean alphabetical, boolean foldersFirst) {
		if (alphabetical) {
			names = (foldersFirst)? sortAlphabeticallyFoldersFirst (names): names;
			return names;
		} else {
			names = sortChronologically (names);
		}
		return names;
	}
	
	
	private String [] sortChronologically(String[] names) {
		for (int i=0; i<names.length; i++) {
			for (int j=0; j<names.length-1; j++) {
				File a = findFileByName(names [i]);
				File b = findFileByName(names [j]);
				if (a.lastModified()<b.lastModified()) {
					names [i] = b.getName();
					names [j] = a.getName();
				}
			}
		}
		return names;
		
	}

	private String [] sortAlphabeticallyFoldersFirst(String[] names) {
		ArrayList <String> directories = new ArrayList <String> ();
		ArrayList <String> files = new ArrayList <String> ();
		
		String [] buffer = new String [names.length];
		
		for (int i=0; i<names.length; i++) {
			if (findFileByName(names[i]).isDirectory()){
				directories.add(names[i]);
			} else {
				files.add(names[i]);
			}
		}
		
		for (int i=0; i<directories.size(); i++){
			buffer [i] = directories.get(i); 
			System.out.println(i + buffer[i]);
		}
		
		for (int i=directories.size(); i<files.size()+directories.size(); i++){
			buffer [i] = files.get(i - directories.size());
			System.out.println(i + buffer[i]);
		}
		
		
		
		return buffer;
	}

	public String getFolderParentURL (){
		String url = folder.getParent();
		return url;
	}
	
	public String getName () {
		return name;
	}
	
	public boolean isFolder (String itemName) {
		File file = findFileByName(itemName);
		return file.isDirectory();
	}
	
	public void openFile (String itemName) {
		File file = findFileByName (itemName);
		try {
			Desktop.getDesktop().open(file);
		} catch (IOException e) {
			System.out.println ("file not executable.");
		}
	}
	
	public String getFilePath (String itemName) {
		File file = findFileByName (itemName);
		return file.getAbsolutePath();
	}
	
	public boolean containsFiles () {
		return (items.length > 0);
	}
	
	public boolean itemContainsFiles (String itemName) {
		File file = findFileByName (itemName);

		if (!file.isDirectory()){
			return false;
		}
		if (file.listFiles().length > 0) {
			return true;
		}
		return false;
	}
	
	private File findFileByName(String name) {
		File item = null;
		for (File file : items){
			if (file.getName().equals(name)){
				item = file;
				return item;
			}
		}
		return item;
		
	}

}

