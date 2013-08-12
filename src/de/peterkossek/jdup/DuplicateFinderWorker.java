package de.peterkossek.jdup;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

public class DuplicateFinderWorker extends SwingWorker {

	private List<File> folders;

	public DuplicateFinderWorker(List<File> folders) {
		this.folders = folders;
	}
	
	@Override
	protected Object doInBackground() throws Exception {
		// find all files in the folders
		FileList masterList = new FileList();
		for (File folder : folders) {
			masterList.addAll(getAllFiles(folder));
		}
		System.out.println(masterList.size());
		// find duplicates
		FileList findDups = new FileList();
		findDups.addAll(masterList);
		Iterator<File> iterator = findDups.iterator();
		while (iterator.hasNext()) {
			File currentFile = iterator.next();
			FileList sameNamedFiles = masterList.getSameNamedFiles(currentFile);
			FileList sameSizedFiles = masterList.getSameSizedFiles(currentFile);
			System.out.println(currentFile+": Same Name: "+sameNamedFiles.toString()+" Same Size: "+sameSizedFiles);
		}
		return null;
	}

	private List<File> getAllFiles(File folder) {
		ArrayList<File> list = new ArrayList<File>();
		File[] files = folder.listFiles();
		for (File file : files) {
			System.out.println(file);
			if (file.isFile())
				list.add(file);
			if (file.isDirectory())
				list.addAll(getAllFiles(file));
		}
		return list;
	}

}
