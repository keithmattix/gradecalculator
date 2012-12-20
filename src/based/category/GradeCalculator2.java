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
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.LazyList;

import panel.images.BackgroundImagePanel;

public class GradeCalculator2 extends JFrame {
	private static final long serialVersionUID = 1187869233288937276L;
	private JPanel panel, averagePanel;
	static GradeCalculator2 window;
	private BufferedImage background;
	private HashMap<Long, HashMap<JLabel, JTextField>> fields;
	private JButton newCategoryButton, submitButton, backButton, printButton;
	private JTextPane grades;
	private NumberFormat nf;
	private JLabel finalLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Base.open("com.mysql.jdbc.Driver",
				"jdbc:mysql://localhost/gradecalculator", "root", "mysql");
		window = new GradeCalculator2();
	}

	/**
	 * Create the frame.
	 */
	public GradeCalculator2() {
		setTitle("Grade Calculator");
		nf = NumberFormat.getInstance();
		fields = new HashMap<Long, HashMap<JLabel, JTextField>>();
		try {
			background = ImageIO.read(new File("School.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		finalLabel = new JLabel();
		averagePanel = new BackgroundImagePanel();
		panel = new BackgroundImagePanel();
		setContentPane(panel);
		newCategoryButton = new JButton("New Category");
		newCategoryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				final NewCategoryDialog dialog = new NewCategoryDialog(
						GradeCalculator2.this);
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
					String subject = JOptionPane.showInputDialog(GradeCalculator2.this,
							"What course is this?", "Course Name", JOptionPane.QUESTION_MESSAGE);
					String gradingPeriod = JOptionPane.showInputDialog(GradeCalculator2.this,
							"What grading period is this?", "Grading Period", JOptionPane.QUESTION_MESSAGE);
					grades.print(new MessageFormat(subject + " " + gradingPeriod), new MessageFormat(
							subject + " " + gradingPeriod), true, null, null, true);
				} catch (PrinterException | NullPointerException e1) {
					e1.printStackTrace();
				}
			}
		});
		backButton.setBounds(submitButton.getBounds());
		printButton.setBounds(newCategoryButton.getBounds());
		setResizable(true);
		setSize(new Dimension(256, 324));
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setIconImage(background);
		setVisible(true);
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
			HashMap<JLabel, JTextField> maps = fields.get(new Long((Integer) g
					.getId()) - 1);
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
		fields.put(new Long((Integer) g.getId()), categoryFields);
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
		if (fields.size() == 4) {
			newCategoryButton.setEnabled(false);
		}
	}
}
