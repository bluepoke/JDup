package de.peterkossek.jdup;

import java.io.File;

public class Duplicate {

	private File fileA;
	private File fileB;
	
	public Duplicate(File a, File b) {
		fileA = a;
		fileB = b;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Duplicate) {
			Duplicate d = (Duplicate) o;
			if ((d.fileA.equals(fileA) && d.fileB.equals(fileB)) || (d.fileA.equals(fileB) && d.fileB.equals(fileA))) {
				return true;
			}
		}
		return false;
	}
}
