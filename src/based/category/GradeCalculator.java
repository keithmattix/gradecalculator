package based.category;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import panel.images.BackgroundImagePanel;

public class GradeCalculator extends JFrame {
	private static final long serialVersionUID = 1187869233288937276L;
	private JPanel panel, averagePanel;
	static GradeCalculator window;
	private BufferedImage background;
	private ConcurrentHashMap<Long, HashMap<JLabel, JTextField>> fields;
	private JButton newCategoryButton, submitButton, backButton, printButton;
	private JTextPane grades;
	private NumberFormat nf;
	private JLabel finalLabel;
	private JMenuItem editCategoriesMenuItem;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Base.open("com.mysql.jdbc.Driver",
				"jdbc:mysql://localhost/gradecalculator", "root", "mysql");
		window = new GradeCalculator();
	}

	/**
	 * Create the frame.
	 */
	public GradeCalculator() {
		setTitle("Grade Calculator");
		nf = NumberFormat.getInstance();
		fields = new ConcurrentHashMap<Long, HashMap<JLabel, JTextField>>();
		try {
			background = ImageIO.read(new File("School.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		finalLabel = new JLabel();
		averagePanel = new BackgroundImagePanel();

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);

		JMenuItem closeMenuItem = new JMenuItem("Close");
		closeMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		editCategoriesMenuItem = new JMenuItem("Edit Categories");
		editCategoriesMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LazyList<Category> allCategories = Category.findAll();
				new EditCategoryDialog(allCategories, GradeCalculator.this);
			}
		});
		fileMenu.add(editCategoriesMenuItem);
		fileMenu.add(closeMenuItem);
		panel = new BackgroundImagePanel();
		setContentPane(panel);
		newCategoryButton = new JButton("New Category");
		newCategoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final NewCategoryDialog dialog = new NewCategoryDialog(
						GradeCalculator.this);
				dialog.setLocationRelativeTo(null);
			}
		});
		submitButton = new JButton("Submit");
		submitButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				calculateAverage();
			}
		});
		panel.setLayout(null);
		newCategoryButton.setFont(new Font("Dialog", Font.BOLD, 9));
		newCategoryButton.setBounds(130, 240, 111, 24);
		submitButton.setFont(new Font("Fialog", Font.BOLD, 9));
		submitButton.setBounds(7, 240, 111, 24);
		getContentPane().add(newCategoryButton);
		getContentPane().add(submitButton);
		LazyList<Category> allCategories = Category.findAll();
		if (!allCategories.isEmpty()) {
			for (Category category : allCategories) {
				addCategoryFields(category);
			}
		}
		backButton = new JButton("Back");
		backButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setContentPane(panel);
				setVisible(true);
			}
		});
		printButton = new JButton("Print");
		printButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String subject = JOptionPane.showInputDialog(
							GradeCalculator.this, "What course is this?",
							"Course Name", JOptionPane.QUESTION_MESSAGE);
					String gradingPeriod = JOptionPane.showInputDialog(
							GradeCalculator.this,
							"What grading period is this?", "Grading Period",
							JOptionPane.QUESTION_MESSAGE);
					grades.print(new MessageFormat(subject + " "
							+ gradingPeriod), new MessageFormat(subject + " "
							+ gradingPeriod), true, null, null, true);
				} catch (PrinterException | NullPointerException e1) {
					e1.printStackTrace();
				}
			}
		});
		backButton.setBounds(submitButton.getBounds());
		printButton.setBounds(newCategoryButton.getBounds());
		setResizable(true);
		setSize(new Dimension(256, 344));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(background);
		if(allCategories.isEmpty()) {
			submitButton.setEnabled(false);
		}
		setVisible(true);
	}

	public void updateCategoryFields() {
		for (long l : fields.keySet()) {
			getContentPane().remove(
					(JLabel) fields.get(l).keySet().toArray()[0]);
			getContentPane().remove(
					(JTextField) fields.get(l).values().toArray()[0]);
			fields.remove(l);
		}
		LazyList<Category> categories = Category.findAll();
		for (Category currentCategory: categories) {
			addCategoryFields(currentCategory);
		}
		if(categories.isEmpty()) {
			submitButton.setEnabled(false);
		}
		getContentPane().revalidate();
	}

	protected void calculateAverage() {
		Base.open("com.mysql.jdbc.Driver",
				"jdbc:mysql://localhost/gradecalculator", "root", "mysql");
		HashMap<String, Double> percentages = new HashMap<String, Double>();
		double sumOfWeights = 0.0;
		double sumOfContributions = 0.0;
		double finalGrade = 0.0;
		for (Long l : fields.keySet()) {
			Category currentCategory = Category.findById(l);
			HashMap<JLabel, JTextField> maps = fields.get(l);
			Object[] textFieldsObjects = maps.values().toArray();
			JTextField categoryField = (JTextField) textFieldsObjects[0];
			if (!(categoryField.getText().trim().equals(""))
					|| categoryField.getText().equals(null)) {
				double categoryPercent = 0.0;
				try {
					if (categoryField.getText().contains(".5")) {
						categoryPercent = Math.round(Math.ceil(Double
								.parseDouble(categoryField.getText())));
						percentages.put(currentCategory.getString("name"),
								categoryPercent);
					}

					else {
						categoryPercent = Double.parseDouble(categoryField
								.getText());
						percentages.put(currentCategory.getString("name"),
								categoryPercent);
					}
					sumOfWeights += currentCategory.getDouble("weight");
					double categoryContribution = categoryPercent
							* (currentCategory.getDouble("weight") * 100) / 100;
					sumOfContributions += categoryContribution;
				} catch (NumberFormatException ex) {
					JOptionPane
							.showMessageDialog(
									this,
									"Error. Please input valid percentages for all categories",
									"Error", JOptionPane.ERROR_MESSAGE);
					Base.close();
					return;
				}
			}

		}
		finalGrade = sumOfContributions / sumOfWeights;
		finalGrade = Math.round(finalGrade);
		nf = NumberFormat.getInstance();
		grades = new JTextPane();
		if (percentages.keySet().isEmpty()) {
			grades.setText("No Grades Entered");
		} else {
			String[] categoryNames = new String[4];
			for (int i = 0; i < percentages.keySet().toArray().length; i++) {
				categoryNames[i] = percentages.keySet().toArray()[i].toString();
			}
			StringBuilder gradesBuilder = new StringBuilder();
			Object[] allPercentages = percentages.values().toArray();
			for (int i = 0; i < allPercentages.length; i++) {
				String averageString = categoryNames[i] + " Average:"
						+ nf.format(allPercentages[i]) + "\n";
				gradesBuilder.append(averageString);
			}
			gradesBuilder.append("\nTotal Average: " + nf.format(finalGrade));
			grades.setText(gradesBuilder.toString());
		}
		grades.setFont(new Font("Serif", 1, 22));
		finalLabel.setFont(new Font("Serif", 1, 22));
		finalLabel.setForeground(Color.RED);
		finalLabel.setLocation(500, 500);
		finalLabel.setText("Your grade is: " + nf.format(finalGrade) + "%");
		if (finalGrade - 3.0D <= 0.0D) {
			finalLabel.setText("No Grades Entered");
		}
		averagePanel.add(finalLabel);
		panel.add(grades);
		averagePanel.add(backButton);
		averagePanel.add(printButton);
		setContentPane(averagePanel);
		setVisible(true);
		Base.close();
	}

	public void addCategoryFields(Category g) {
		JLabel categoryLabel = new JLabel(g.getString("name"));
		JTextField categoryField = new JTextField(15);
		categoryLabel.setForeground(Color.BLACK);
		if (fields.isEmpty()) {
			categoryLabel.setBounds(95, 20, 124, 24);
			categoryField.setBounds(categoryLabel.getX() - 30,
					categoryLabel.getY() + 30, 110, 20);
		} else {
			Number key = (Number)g.getId();
			Category currentCategory = Category.findById(key.longValue());
			List<Category> list = new ArrayList<Category>();
			List<Category> categoryList = Arrays.asList(Category.findAll().toArray(new Category[list.size()]));
			for(Category c : categoryList) {
				Number key2 = (Number)c.getId();
				Number key3 = (Number)currentCategory.getId();
				if(key2.longValue() == key3.longValue()) {
					currentCategory = c;
				}
			}
			int categoryIndex = categoryList.indexOf(currentCategory);
			Category previousCategory = categoryList.get(categoryIndex - 1);
			Number previousKey = (Number)previousCategory.getId();
			HashMap<JLabel, JTextField> maps = fields.get(previousKey.longValue());
			Object[] textFieldsObjects = maps.values().toArray();
			JTextField lastField = (JTextField) textFieldsObjects[textFieldsObjects.length - 1];
			Object[] labelObjects = maps.keySet().toArray();
			JLabel lastLabel = (JLabel) labelObjects[labelObjects.length - 1];
			categoryLabel.setBounds(lastLabel.getX(), lastLabel.getY() + 60,
					124, 24);
			categoryField.setBounds(lastField.getX(), lastField.getY() + 60,
					110, 20);
		}

		HashMap<JLabel, JTextField> categoryFields = new HashMap<JLabel, JTextField>();
		categoryFields.put(categoryLabel, categoryField);
		Number temp = (Number)g.getId();
		fields.put(temp.longValue(), categoryFields);
		if (categoryField.getY() >= newCategoryButton.getY()
				- newCategoryButton.getHeight()
				|| categoryLabel.getY() >= newCategoryButton.getY()
						- newCategoryButton.getHeight()) {
			setSize(getWidth(), getHeight() + 30);
			newCategoryButton.setLocation(newCategoryButton.getX(),
					newCategoryButton.getY() + 30);
			submitButton.setLocation(submitButton.getX(),
					submitButton.getY() + 30);
		}
		panel.add(categoryLabel);
		panel.add(categoryField);
		panel.revalidate();
		if (fields.size() >= 4) {
			newCategoryButton.setEnabled(false);
		} else {
			newCategoryButton.setEnabled(true);
		}
		submitButton.setEnabled(true);
	}
}
