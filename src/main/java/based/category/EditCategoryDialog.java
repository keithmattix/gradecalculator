package main.java.based.category;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

public class EditCategoryDialog extends JDialog {

	private JScrollPane scrollPane;
	private static final long serialVersionUID = 7622647759626730382L;
	private JTable categoriesTable;
	private HashMap<Integer, Long> categoryTableRowIndexes;
	private GradeCalculator owner;

	/**
	 * Create the dialog.
	 */
	public EditCategoryDialog(List<Category> categories,
			final GradeCalculator owner) {
		super(owner);
		this.owner = owner;
		if(!Base.hasConnection()) {
			Base.open("com.mysql.jdbc.Driver",
					"jdbc:mysql://localhost/gradecalculator", "root", "mysql");
		}
		setBounds(100, 100, 450, 300);
		categoryTableRowIndexes = new HashMap<Integer, Long>();
		categoriesTable = createTable(categories);
		Action delete = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				JTable table = (JTable) e.getSource();
				int rowIndex = Integer.valueOf(e.getActionCommand());
				if(!Base.hasConnection()) {
					Base.open("com.mysql.jdbc.Driver",
							"jdbc:mysql://localhost/gradecalculator", "root",
							"mysql");
				}
				Category deletedCategory = Category
						.findById(categoryTableRowIndexes.get(rowIndex + 1));
				((DefaultTableModel) table.getModel()).removeRow(rowIndex);
				deletedCategory.delete();
				owner.updateCategoryFields();
				Base.close();
			}
		};
		ButtonColumn buttonColumn = new ButtonColumn(categoriesTable, delete, 2);
		categoriesTable.getColumn("Delete Category").setCellRenderer(
				buttonColumn);
		categoriesTable.getModel().addTableModelListener(
				new TableModelListener() {
					@Override
					public synchronized void tableChanged(TableModelEvent e) {
						if(e.getColumn() == 2) {
							return;
						}
						if (e.getType() == TableModelEvent.UPDATE) {
							if (e.getColumn() == 0) {
								Category affectedCategory = Category
										.findById(categoryTableRowIndexes.get(e
												.getFirstRow() + 1));
								String newName = categoriesTable.getValueAt(
										e.getFirstRow(), e.getColumn())
										.toString();
								affectedCategory.set(categoriesTable
										.getColumnName(e.getColumn()), newName);
								affectedCategory.saveIt();
								owner.updateCategoryFields();
							}

							else {
								Category affectedCategory = Category
										.findById(categoryTableRowIndexes.get(e
												.getFirstRow() + 1));
								StringBuffer tableValue = new StringBuffer(
										categoriesTable.getValueAt(
												e.getFirstRow(), e.getColumn())
												.toString());
								if (!tableValue.toString().endsWith("%")) {
									JOptionPane
											.showMessageDialog(
													EditCategoryDialog.this,
													"Please ensure that there is a percent sign after each weight",
													"Error",
													JOptionPane.ERROR_MESSAGE);
									Base.close();
									return;
								}
								tableValue.deleteCharAt(tableValue.length() - 1);
								double newWeight = Double
										.parseDouble(tableValue.toString()) / 100;
								double totalWeight = 0.0;
								LazyList<Category> allCategories = Category
										.findAll();
								for (Category category : allCategories) {
									totalWeight += category.getDouble("weight");
								}
								totalWeight -= Category.findById(
										new Long((Integer) affectedCategory
												.getId())).getDouble("weight");
								if (totalWeight + newWeight <= 1.0) {
									affectedCategory.set(categoriesTable
											.getColumnName(e.getColumn()),
											newWeight);
									affectedCategory.saveIt();
									owner.updateCategoryFields();
								} else {
									JOptionPane
											.showMessageDialog(
													EditCategoryDialog.this,
													"Error: Weights must add up to no more than 100%",
													"Error",
													JOptionPane.ERROR_MESSAGE);
									Base.close();
								}
							}
						}

					}
				});
		if (categoriesTable.getRowCount() <= 0) {
			JOptionPane.showMessageDialog(this, "No categories to display",
					"No categories", JOptionPane.WARNING_MESSAGE);
		} else {
			scrollPane = new JScrollPane(categoriesTable);
		}
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	public EditCategoryDialog(List<Category> categories) {
		this(categories, null);
	}

	public JTable createTable(List<Category> categories) {
		JTable categoriesTable = new JTable();
		final DefaultTableModel editableTableModel = new DefaultTableModel();
		editableTableModel.setColumnIdentifiers(new String[] { "Name",
				"Weight", "Delete Category" });
		for (Category c : categories) {
			String weightPercentageRepresentation = new BigDecimal(
					c.getDouble("weight") * 100).stripTrailingZeros()
					.toPlainString()
					+ "%";
			Object[] categoryInfo = { c.getString("name"),
					weightPercentageRepresentation, "Delete" };
			editableTableModel.addRow(categoryInfo);
			categoryTableRowIndexes.put(editableTableModel.getRowCount(),
					new Long((Integer) c.getId()));
		}
		categoriesTable.setModel(editableTableModel);
		return categoriesTable;
	}

}
