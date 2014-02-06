package de.peterkossek.jdup;

import java.awt.Component;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class ResultWindow extends JFrame implements ActionListener {

	private List<Duplicate>	duplicates;
	private JTable table;

	/**
	 * @wbp.parser.constructor
	 */
	public ResultWindow(List<Duplicate> duplicates, Component relativeLocation) {
		super("Duplicates");
		this.duplicates = duplicates;

		table = new JTable();
		DuplicateTableModel tableModel = new DuplicateTableModel();
		for (Duplicate duplicate : duplicates) {
			Vector<Object> v = new Vector<Object>();
			File fileA = duplicate.getFileA();
			v.add(fileA);
			DeleteButton btnDelA = new DeleteButton(fileA);
			btnDelA.addActionListener(this);
			v.add(btnDelA);
			File fileB = duplicate.getFileB();
			v.add(fileB);
			DeleteButton btnDelB = new DeleteButton(fileB);
			v.add(btnDelB);
			tableModel.addRow(v);
		}
		table.setModel(tableModel);
		
		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(table);
		pack();
		setLocationRelativeTo(relativeLocation);
		setVisible(true);
	}
	
	public ResultWindow(List<Duplicate> duplicates) {
		this(duplicates, null);
	}
	
	
	private static class DuplicateTableModel extends DefaultTableModel {

		private static final String[]	COLUMN_TITLES	= new String[] {"File A", "Action", "File B", "Action"};
		boolean[] columnEditables = new boolean[] {false, true, false, true};

		public DuplicateTableModel() {
			super(new Object[][]{}, COLUMN_TITLES);
		}
		
		public boolean isCellEditable(int row, int column) {
			return columnEditables[column];
		}
	}
	
	private static class DeleteButton extends JButton {

		private File	file;

		public DeleteButton(File f) {
			super("Delete");
			this.file = f;
		}
		
		public File getFile() {
			return file;
		}
	}


	@Override
	public void actionPerformed(ActionEvent paramActionEvent) {
		// TODO Auto-generated method stub
		
	}
}
