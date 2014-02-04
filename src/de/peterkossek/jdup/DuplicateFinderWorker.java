package de.peterkossek.jdup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

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
		FileMapper fileMapper = new FileMapper();
		publish(new WorkerProgress("Reading all files in the folders",0,1,0,true));
		Set<File> masterSet = new HashSet<File>();
		for (File folder : folders) {
			masterSet.addAll(getAllFiles(folder));
		}
		ArrayList<File> masterList = new ArrayList<File>(masterSet);
		System.out.println(masterList.size());
		// find duplicates
		for (int i=0; i<masterList.size(); i++) {
			File currentFile = masterList.get(i);
			publish(new WorkerProgress("Finding duplicates for "+currentFile.getAbsolutePath(),0,masterList.size(),i+1,false));
			fileMapper.add(currentFile);
		}
		
		publish(new WorkerProgress("Cleaning up collected data...",0,1,0,true));
		fileMapper.cleanUp();
		
		Map<String, Set<File>> nameMap = fileMapper.getNameMap();
		System.out.println("Total name duplicates "+nameMap.size());
		for (Entry<String, Set<File>> entry : nameMap.entrySet()) {
			System.out.println(entry.getKey()+" : "+entry.getValue());
		}
		
		Map<Long, Set<File>> sizeMap = fileMapper.getSizeMap();
		System.out.println("Total size duplicates "+sizeMap.size());
		for (Entry<Long, Set<File>> entry : sizeMap.entrySet()) {
			System.out.println(entry.getKey()+" : "+entry.getValue());
		}
		publish(new WorkerProgress("Finished analysing folders for duplicates.", 0, 1, 1, false));
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
	
	@Override
	protected void process(List<WorkerProgress> progresses) {
		for (WorkerProgress wp : progresses) {
			display.displayStatus(wp.percentage()+" "+wp.message, wp.min, wp.max, wp.value, wp.indeterminate);
			System.out.println(wp);
		}
	}

}
