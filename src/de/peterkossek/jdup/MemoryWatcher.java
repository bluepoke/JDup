package de.peterkossek.jdup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingWorker;

public class MemoryWatcher implements ActionListener {

	private final double mb = 1024*1024;
	private MemoryDisplay display;

	public MemoryWatcher(MemoryDisplay display) {
		this.display = display;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Runtime rt = Runtime.getRuntime();
		double free = rt.freeMemory() / mb;
		double total = rt.totalMemory() / mb;
		double used = total - free;
		display.displayMemory(String.format("Used: %.2f MB, Free: %.2f MB", used, free));
	}

}
