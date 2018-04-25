package library.view;

import javax.swing.table.DefaultTableModel;

public class MyTableModel extends DefaultTableModel{

	private static final long serialVersionUID = 1L;
	
	public MyTableModel(String[][] data, String[] columns){
		super(data, columns);
	}

	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	

}