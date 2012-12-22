package based.category;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import com.jgoodies.forms.factories.DefaultComponentFactory;

public class NewCategoryDialog extends JDialog {
	
	private static final long serialVersionUID = -3081453578732779417L;
	private final JPanel contentPanel = new JPanel();
	private JTextField newCategoryNameTextField;
	private JTextField newCategoryWeightTextField;
	private Category category;

	/**
	 * Create the dialog.
	 */
	public NewCategoryDialog() {
		this(null);
	}
	
	public NewCategoryDialog(Frame owner) {
		super(owner);
		setTitle("New Category");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 446, 238);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		contentPanel.setLayout(null);
		{
			JLabel categoryNameLabel = new JLabel("Name:");
			categoryNameLabel.setFont(new Font("Dialog", Font.BOLD, 13));
			categoryNameLabel.setBounds(149, 112, 70, 15);
			contentPanel.add(categoryNameLabel);
		}
		{
			JLabel newCategoryLabel = DefaultComponentFactory.getInstance().createTitle("New Category");
			newCategoryLabel.setBounds(149, 46, 160, 24);
			newCategoryLabel.setFont(new Font("Dialog", Font.BOLD, 20));
			contentPanel.add(newCategoryLabel);
		}
		
		newCategoryNameTextField = new JTextField();
		newCategoryNameTextField.setBounds(252, 110, 114, 19);
		contentPanel.add(newCategoryNameTextField);
		newCategoryNameTextField.setColumns(10);
		
		JLabel newCategoryWeightLabel = new JLabel("Weight(%):");
		newCategoryWeightLabel.setBounds(149, 143, 85, 15);
		contentPanel.add(newCategoryWeightLabel);
		
		newCategoryWeightTextField = new JTextField();
		newCategoryWeightTextField.setBounds(252, 141, 114, 19);
		contentPanel.add(newCategoryWeightTextField);
		newCategoryWeightTextField.setColumns(10);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setBounds(0, 238, 446, 35);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(!Base.hasConnection()) {
							Base.open("com.mysql.jdbc.Driver", "jdbc:mysql://localhost/gradecalculator", "root", "mysql");
						}
						try {
							double totalWeight = 0.0;
							LazyList<Category> allCategories = Category.findAll();
							for (Category category : allCategories) {
								totalWeight += category.getDouble("weight");
							}
							if(totalWeight + Double.parseDouble(newCategoryWeightTextField.getText()) / 100 <= 1.0) {
								category = Category.createIt("name", newCategoryNameTextField.getText(), "weight", Double.parseDouble(newCategoryWeightTextField.getText()) / 100);
								GradeCalculator owner = (GradeCalculator)NewCategoryDialog.this.getOwner();
								owner.addCategoryFields(category);
								NewCategoryDialog.this.dispose();
								Base.close();
							} else {
								JOptionPane.showMessageDialog(NewCategoryDialog.this, "Error: Weights must add up to no more than 100%", "Error", JOptionPane.ERROR_MESSAGE);
								Base.close();
							}
						} catch(NumberFormatException ex) {
							JOptionPane.showMessageDialog(NewCategoryDialog.this, "Please input a correct percent value", "Error", JOptionPane.ERROR_MESSAGE);
							Base.close();
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
				setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
				setVisible(true);
			}
		}
	}
	
	public Category getCreatedCategory() {
		return category;
	}
}
