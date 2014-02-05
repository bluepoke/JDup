package de.peterkossek.jdup;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public abstract class ComparationMethod {

	public static List<ComparationMethod> availableComparationMethods = new ArrayList<ComparationMethod>();
	private String	name;
	
	public ComparationMethod(String name) {
		registerComparationMethod(this);
		this.name = name;
	}
	
	public static void registerComparationMethod(ComparationMethod cm) {
		availableComparationMethods.add(cm);
	}
	
	public abstract boolean filesEqual(File a, File b) throws IOException, NoSuchAlgorithmException;
	
	@Override
	public String toString() {
		return this.name;
	}
}
