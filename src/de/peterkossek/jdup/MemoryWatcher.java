package de.peterkossek.jdup;

import javax.swing.JLabel;
import javax.swing.SwingWorker;

public class MemoryWatcher extends SwingWorker {

	private JLabel label;
	private final double mb = 1024*1024;

	public MemoryWatcher(JLabel label) {
		this.label = label;
	}

	@Override
	protected Object doInBackground() throws Exception {
		Runtime rt = Runtime.getRuntime();
		while (!isCancelled()) {
			double free = rt.freeMemory() / mb;
			double total = rt.totalMemory() / mb;
			double used = total - free;
			label.setText(String.format("Used: %.2f MB, Free: %.2f MB", used, free));
			Thread.sleep(1000);
		}
		return null;
	}

}
