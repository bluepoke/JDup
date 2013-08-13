package de.peterkossek.jdup;

public interface StatusDisplay {

	public void displayStatus(String message, int min, int max, int value, boolean indeterminate);
}
