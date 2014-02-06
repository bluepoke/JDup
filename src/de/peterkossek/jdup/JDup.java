package de.peterkossek.jdup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import javax.swing.JProgressBar;

public class JDup extends JFrame implements ActionListener, StatusDisplay, MemoryDisplay, PropertyChangeListener {
	private static final String TITLE = "DuplicateFinder";
	private JList folderList;
	private JButton btnAddFolder;
	private File lastDir;
	private JButton btnRemoveFolder;
	private JButton btnFindDuplicates;
	private JLabel lblStatus;
	private JProgressBar progressBar;

	public JDup() {
		
		setMinimumSize(new Dimension(600, 400));
		setTitle(TITLE);
		
		JPanel pnlStatus = new JPanel();
		getContentPane().add(pnlStatus, BorderLayout.SOUTH);
		pnlStatus.setLayout(new BorderLayout(0, 0));
		
		lblStatus = new JLabel("Status");
		pnlStatus.add(lblStatus);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		
		JPanel tabDuplicates = new JPanel();
		tabbedPane.addTab("New tab", null, tabDuplicates, null);
		GridBagLayout gbl_tabDuplicates = new GridBagLayout();
		gbl_tabDuplicates.columnWidths = new int[]{0, 0, 0};
		gbl_tabDuplicates.rowHeights = new int[]{0, 0, 0, 0};
		gbl_tabDuplicates.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_tabDuplicates.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		tabDuplicates.setLayout(gbl_tabDuplicates);
		
		folderList = new JList(new DefaultListModel());
		GridBagConstraints gbc_list = new GridBagConstraints();
		gbc_list.gridheight = 3;
		gbc_list.anchor = GridBagConstraints.NORTH;
		gbc_list.insets = new Insets(0, 0, 0, 5);
		gbc_list.fill = GridBagConstraints.HORIZONTAL;
		gbc_list.gridx = 0;
		gbc_list.gridy = 0;
		tabDuplicates.add(folderList, gbc_list);
		
		btnAddFolder = new JButton("Add Folder");
		btnAddFolder.addActionListener(this);
		GridBagConstraints gbc_btnAddFolder = new GridBagConstraints();
		gbc_btnAddFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnAddFolder.insets = new Insets(0, 0, 5, 0);
		gbc_btnAddFolder.anchor = GridBagConstraints.NORTH;
		gbc_btnAddFolder.gridx = 1;
		gbc_btnAddFolder.gridy = 0;
		tabDuplicates.add(btnAddFolder, gbc_btnAddFolder);
		
		btnRemoveFolder = new JButton("Remove Folder");
		btnRemoveFolder.addActionListener(this);
		GridBagConstraints gbc_btnRemoveFolder = new GridBagConstraints();
		gbc_btnRemoveFolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemoveFolder.insets = new Insets(0, 0, 5, 0);
		gbc_btnRemoveFolder.gridx = 1;
		gbc_btnRemoveFolder.gridy = 1;
		tabDuplicates.add(btnRemoveFolder, gbc_btnRemoveFolder);
		
		btnFindDuplicates = new JButton("Find duplicates");
		btnFindDuplicates.addActionListener(this);
		GridBagConstraints gbc_btnFindDuplicates = new GridBagConstraints();
		gbc_btnFindDuplicates.anchor = GridBagConstraints.SOUTH;
		gbc_btnFindDuplicates.gridx = 1;
		gbc_btnFindDuplicates.gridy = 2;
		tabDuplicates.add(btnFindDuplicates, gbc_btnFindDuplicates);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		MemoryWatcher memoryWatcher = new MemoryWatcher(this);
		Timer t = new Timer(1000, memoryWatcher);
		t.start();
		progressBar = new JProgressBar();
		pnlStatus.add(progressBar, BorderLayout.NORTH);
	}
	
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		new JDup().setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source.equals(btnAddFolder)) {
			JFileChooser jfc = new JFileChooser(lastDir);
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfc.setMultiSelectionEnabled(true);
			int dialogResult = jfc.showOpenDialog(this);
			if (dialogResult == JFileChooser.APPROVE_OPTION) {
				File[] files = jfc.getSelectedFiles();
				for (File file : files) {
					DefaultListModel model = (DefaultListModel) folderList.getModel();
					if (!model.contains(file)) {
						model.addElement(file);
					}
					lastDir = file;
				}
			}
		} else if (source.equals(btnRemoveFolder)) {
			Object selectedValue = folderList.getSelectedValue();
			DefaultListModel model = (DefaultListModel) folderList.getModel();
			model.removeElement(selectedValue);
		} else if (source.equals(btnFindDuplicates)) {
			
			Enumeration<File> elements = (Enumeration<File>) ((DefaultListModel)folderList.getModel()).elements();
			ArrayList<File> folders = new ArrayList<File>();
			while (elements.hasMoreElements())
				folders.add(elements.nextElement());
			DuplicateFinderWorker worker = new DuplicateFinderWorker(folders, new MD5Comparation());
			worker.addPropertyChangeListener(this);
			try {
				worker.execute();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}

	@Override
	public void displayStatus(String message, int min, int max, int value,
			boolean indeterminate) {
		lblStatus.setText(message);
		progressBar.setMinimum(min);
		progressBar.setMaximum(max);
		progressBar.setValue(value);
		progressBar.setIndeterminate(indeterminate);
	}

	@Override
	public void displayMemory(String memoryMessage) {
		this.setTitle(TITLE+" ("+memoryMessage+")");
	}

	@Override
	public void propertyChange(PropertyChangeEvent pce) {
		Object source = pce.getSource();
		if (source instanceof DuplicateFinderWorker) {
			String propertyName = pce.getPropertyName();
			Object newValue = pce.getNewValue();
			DuplicateFinderWorker worker = (DuplicateFinderWorker) source;
			if (propertyName.equals(DuplicateFinderWorker.WORKER_FINISHED) && newValue.equals(true)) {
				try {
					List<Duplicate> duplicates = worker.get();
					System.out.println(duplicates);
					System.out.println(duplicates.size());
					ResultWindow rw = new ResultWindow(duplicates);
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			} else if (propertyName.equals(DuplicateFinderWorker.PROGRESS)) {
				WorkerProgress wp = (WorkerProgress) newValue;
				displayStatus(wp.message, wp.min, wp.max, wp.value, wp.indeterminate);
			}
		}
	}

}
