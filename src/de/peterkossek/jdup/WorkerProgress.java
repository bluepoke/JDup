package de.peterkossek.jdup;

public class WorkerProgress {

	int min;
	int max;
	int value;
	boolean indeterminate;
	String message;
	double percentage;
	
	public WorkerProgress(String message, int min, int max, int value, boolean indeterminate) {
		super();
		this.min = min;
		this.max = max;
		this.value = value;
		this.indeterminate = indeterminate;
		this.message = message;
		this.percentage = (double) value / (double) max * 100;
	}
	
	@Override
	public String toString() {
		return message+" "+min+" <= "+value+" <= "+max+(indeterminate?" indeterminate":"");
	}
	
	public String percentage() {
		return String.format("[%.2f%%]", percentage);
	}
}
