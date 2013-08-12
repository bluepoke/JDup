package de.peterkossek.jdup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
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
		HashSet<File> foundNameDups = new HashSet<File>();
		HashSet<File> foundSizeDups = new HashSet<File>();
		ArrayList<PotentialDuplicates> potentialDuplicates = new ArrayList<PotentialDuplicates>();
		while (iterator.hasNext()) {
			File currentFile = iterator.next();
			FileList sameNamedFiles;
			String message = currentFile+": ";
			PotentialDuplicates potDup = new PotentialDuplicates(currentFile);
			boolean hasPotentialDuplicates = false;
			if (!foundNameDups.contains(currentFile)) {
				sameNamedFiles = masterList.getSameNamedFiles(currentFile);
				if (!sameNamedFiles.isEmpty()) {
					foundNameDups.addAll(sameNamedFiles);
					potDup.addPotentialDuplicates(sameNamedFiles);
					hasPotentialDuplicates = true;
					message+=" Same Name: "+sameNamedFiles;
				}
			} else {
				message+=" Same Name: skipped";
			}
			FileList sameSizedFiles;
			if (!foundSizeDups.contains(currentFile)) {
				sameSizedFiles = masterList.getSameSizedFiles(currentFile);
				if (!sameSizedFiles.isEmpty()) {
					foundSizeDups.addAll(sameSizedFiles);
					potDup.addPotentialDuplicates(sameSizedFiles);
					hasPotentialDuplicates = true;
					message += " Same Size: " + sameSizedFiles;
				}
			} else {
				message+=" Same size: skipped";
			}
			if (hasPotentialDuplicates)
				potentialDuplicates.add(potDup);
			System.out.println(message);
		}
		
		Iterator<PotentialDuplicates> potDupIt = potentialDuplicates.iterator();
		while (potDupIt.hasNext()) {
			PotentialDuplicates potDup = potDupIt.next();
			potDup.checkDuplicates(PotentialDuplicates.COMPARE_MD5);
			if (!potDup.hasDuplicates())
				potDupIt.remove();
		}
		for (PotentialDuplicates potDup : potentialDuplicates) {
			System.out.println(potDup);
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
