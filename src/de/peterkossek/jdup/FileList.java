package de.peterkossek.jdup;

import java.io.File;
import java.util.ArrayList;

public class FileList extends ArrayList<File> {
	
	public FileList getSameNamedFiles(File file) {
		FileList fl = new FileList();
		for (File f : this) {
			if (f.getName().equalsIgnoreCase(file.getName()) && !f.equals(file))
				fl.add(f);
		}
		return fl;
	}
	
	public FileList getSameSizedFiles(File file) {
		FileList fl = new FileList();
		for (File f : this) {
			if (f.length() == file.length() && !f.equals(file))
				fl.add(f);
		}
		return fl;
	}
	
}
