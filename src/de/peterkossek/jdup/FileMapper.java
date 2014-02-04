package de.peterkossek.jdup;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class FileMapper {

	private Map<String, Set<File>> nameMap = new HashMap<String, Set<File>>();
	private Map<Long, Set<File>> sizeMap = new HashMap<Long, Set<File>>();
	
	public void add(File file) {
		String name = file.getName();
		Set<File> nameSet = nameMap.get(name);
		if (nameSet == null) {
			nameSet = new HashSet<File>();
		}
		nameSet.add(file);
		nameMap.put(name, nameSet);
		
		long size = file.length();
		Set<File> sizeSet = sizeMap.get(size);
		if (sizeSet == null) {
			sizeSet = new HashSet<File>();
		}
		sizeSet.add(file);
		sizeMap.put(size, sizeSet);
	}
	
	public void cleanUp() {
		Set<String> names = nameMap.keySet();
		Set<String> namesToRemove = new HashSet<String>();
		for (String name : names) {
			Set<File> nameSet = nameMap.get(name);
			System.out.println(name + "=" + nameSet.size());
			if (nameSet.size()<2)
				namesToRemove.add(name);
		}
		
		for (String name : namesToRemove) {
			nameMap.remove(name);
		}
		
		Set<Long> sizes = sizeMap.keySet();
		Set<Long> sizesToRemove = new HashSet<Long>();
		for (Long size : sizes) {
			Set<File> sizeSet = sizeMap.get(size);
			System.out.println(size + "=" + sizeSet.size());
			if (sizeSet.size()<2)
				sizesToRemove.add(size);
		}
		for (Long size : sizesToRemove) {
			sizeMap.remove(size);
		}
	}
	
	public Map<String, Set<File>> getNameMap() {
		return nameMap;
	}
	
	public Map<Long, Set<File>> getSizeMap() {
		return sizeMap;
	}
	
}
