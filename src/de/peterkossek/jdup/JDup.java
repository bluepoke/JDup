package de.peterkossek.jdup;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class JDup extends JFrame implements ActionListener {
	private JList folderList;
	private JButton btnAddFolder;
	private File lastDir;
	private JButton btnRemoveFolder;

	public JDup() {
		setMinimumSize(new Dimension(600, 400));
		setTitle("DuplicateFinder");
		
		JPanel pnlStatus = new JPanel();
		getContentPane().add(pnlStatus, BorderLayout.SOUTH);
		
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
		gbc_list.insets = new Insets(0, 0, 5, 5);
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
		setLocationRelativeTo(null);
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
			jfc.setMultiSelectionEnabled(false);
			int dialogResult = jfc.showOpenDialog(this);
			if (dialogResult == JFileChooser.APPROVE_OPTION) {
				File file = jfc.getSelectedFile();
				DefaultListModel model = (DefaultListModel) folderList.getModel();
				if (!model.contains(file)) {
					model.addElement(file);
				}
			}
		} else if (source.equals(btnRemoveFolder)) {
			Object selectedValue = folderList.getSelectedValue();
			DefaultListModel model = (DefaultListModel) folderList.getModel();
			model.removeElement(selectedValue);
		}
		
	}

}
