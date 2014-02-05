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

public class DuplicateFinderWorker extends SwingWorker<List<Duplicate>, WorkerProgress> {

	private List<File> folders;
	private StatusDisplay display;
	private ComparationMethod	comparationMethod;

	public DuplicateFinderWorker(List<File> folders, StatusDisplay display, ComparationMethod method) {
		this.folders = folders;
		this.display = display;
		this.comparationMethod = method;
	}
	
	@Override
	protected  List<Duplicate> doInBackground() throws Exception {
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
		
		List<Duplicate> duplicates = new ArrayList<Duplicate>();
		
		Map<String, Set<File>> nameMap = fileMapper.getNameMap();
		System.out.println("Total name duplicates "+nameMap.size());
		
		Map<Long, Set<File>> sizeMap = fileMapper.getSizeMap();
		System.out.println("Total size duplicates "+sizeMap.size());
		
		int nSize = nameMap.size();
		int current = 0;
		for (Set<File> set : nameMap.values()) {
			current++;
			File[] files = set.toArray(new File[set.size()]);
			for (int i=0; i<files.length-1; i++) {
				for (int j=i+1; j<files.length; j++) {
					File fileA = files[i];
					File fileB = files[j];
					if (comparationMethod.filesEqual(fileA, fileB)) {
						duplicates.add(new Duplicate(fileA, fileB));
					}
				}
			}
			publish(new WorkerProgress("Comparing files with same name using "+comparationMethod.toString(), 0, nSize, current, false));
		}
		
		int sSize = sizeMap.size();
		current = 0;
		for (Set<File> set : sizeMap.values()) {
			current++;
			File[] files = set.toArray(new File[set.size()]);
			for (int i=0; i<files.length-1; i++) {
				for (int j=i+1; j<files.length; j++) {
					File fileA = files[i];
					File fileB = files[j];
					Duplicate duplicate = new Duplicate(fileA, fileB);
					if (!duplicates.contains(duplicate) && comparationMethod.filesEqual(fileA, fileB)) {
						duplicates.add(duplicate);
					}
				}
			}
			publish(new WorkerProgress("Comparing files with same size using "+comparationMethod.toString(), 0, sSize, current, false));
		}
		
		publish(new WorkerProgress("Finished analysing folders for duplicates.", 0, 1, 1, false));
		return duplicates;
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
