package de.peterkossek.jdup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import javax.swing.SwingWorker;

public class DuplicateFinderWorker extends SwingWorker<List<PotentialDuplicates>, WorkerProgress> {

	private List<File> folders;
	private StatusDisplay display;

	public DuplicateFinderWorker(List<File> folders, StatusDisplay display) {
		this.folders = folders;
		this.display = display;
	}
	
	@Override
	protected List<PotentialDuplicates> doInBackground() throws Exception {
		// find all files in the folders
		FileList masterList = new FileList();
		publish(new WorkerProgress("Reading all files in the folders",0,1,0,true));
		for (File folder : folders) {
			masterList.addAll(getAllFiles(folder));
		}
		System.out.println(masterList.size());
		// find duplicates
		FileList findDups = new FileList();
		findDups.addAll(masterList);
		HashSet<File> foundNameDups = new HashSet<File>();
		HashSet<File> foundSizeDups = new HashSet<File>();
		ArrayList<PotentialDuplicates> potentialDuplicates = new ArrayList<PotentialDuplicates>();
		for (int i=0; i<findDups.size(); i++) {
			File currentFile = findDups.get(i);
			publish(new WorkerProgress("Finding duplicates for "+currentFile.getAbsolutePath(),0,findDups.size(),i+1,false));
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
			int i = potentialDuplicates.indexOf(potDup);
			publish(new WorkerProgress("Checking duplicates of "+potDup.source.getAbsolutePath(),0,potentialDuplicates.size(),i+1,false));
			potDup.checkDuplicates(PotentialDuplicates.COMPARE_MD5);
			if (!potDup.hasDuplicates())
				potDupIt.remove();
		}
		for (PotentialDuplicates potDup : potentialDuplicates) {
			System.out.println(potDup);
		}
		publish(new WorkerProgress("Finished analysing folders for duplicates.", 0, 1, 1, false));
		return potentialDuplicates;
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
	
	@Override
	protected void process(List<WorkerProgress> progresses) {
		for (WorkerProgress wp : progresses) {
			display.displayStatus(wp.percentage()+" "+wp.message, wp.min, wp.max, wp.value, wp.indeterminate);
			System.out.println(wp);
		}
	}

}
