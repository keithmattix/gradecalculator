package based.category;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.*;

import panel.images.*;

public class CalculateGrade extends JFrame implements ActionListener {
	private JLabel test, quiz, classWork, homeWork, finalLabel, honorsLabel;
	private JTextField testField, quizField, classWorkField, homeWorkField;
	private JTextPane grades;
	private JComboBox<String> honorsClass;
	private JButton submit, back,print;
	private JPanel panel, panel2;
	private BufferedImage image;
	private NumberFormat nf;
	private double homePercent, classPercent, testPercent, quizPercent;
	private static final long serialVersionUID = 1L;
	private double testContribution, classContribution, quizContribution,
			homeContribution;
	private String[] honors = { "Yes", "No" };
	private boolean isHonorsClass = false;
	private static CalculateGrade window;

	public CalculateGrade() {
		super("Grade Calculator");
		panel = new BackgroundImagePanel();
		panel.setLayout(new FlowLayout());
		homeWorkField = new JTextField(15);
		classWorkField = new JTextField(15);
		testField = new JTextField(15);
		quizField = new JTextField(15);
		submit = new JButton("Submit");
		submit.addActionListener(this);
		test = new JLabel("Test Percentage");
		quiz = new JLabel("Quiz Percentage");
		classWork = new JLabel("Classwork Percentage");
		homeWork = new JLabel("Homework Percentage");
		honorsLabel = new JLabel("Is this an Honors class?");
		print = new JButton("Print");
		print.addActionListener(this);
		honorsLabel.setForeground(Color.WHITE);
		honorsClass = new JComboBox<String>(honors);
		honorsClass.setSelectedIndex(1);
		honorsClass.addActionListener(this);
		finalLabel = new JLabel();
		back = new JButton("Back");
		back.addActionListener(this);
		try {
			image = ImageIO.read(new File("School.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		panel.add(test);
		panel.add(testField);
		panel.add(quiz);
		panel.add(quizField);
		panel.add(classWork);
		panel.add(classWorkField);
		panel.add(homeWork);
		panel.add(homeWorkField);
		panel.add(honorsLabel);
		panel.add(honorsClass);
		panel.add(submit);
		add(panel);
		setSize(image.getWidth() - 150, image.getHeight());
		setLocationRelativeTo(null);
		if (System.getProperty("os.name").contains("Mac")) {
			setResizable(true);
		} else {
			setResizable(false);
		}
		try {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException ex) {EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						GradeCalculator2 frame = new GradeCalculator2();
						frame.setVisible(true);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
				Logger.getLogger(CalculateGrade.class.getName()).log(
						Level.SEVERE, null, ex);
			} catch (InstantiationException ex) {
				Logger.getLogger(CalculateGrade.class.getName()).log(
						Level.SEVERE, null, ex);
			} catch (IllegalAccessException ex) {
				Logger.getLogger(CalculateGrade.class.getName()).log(
						Level.SEVERE, null, ex);
			}
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		setIconImage(image);
		setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(submit)) {
			double finalGrade = 0.0D;
			double sumOfContributions = 0.0D;
			double sumOfWeights = 0.0D;
			try {
				if (testField.getText().contains(".5"))
					testPercent = (int) Math.ceil(Double.parseDouble(testField
							.getText()));
				else {
					testPercent = Double.parseDouble(testField.getText());
				}
				testPercent = Math.round(testPercent);
				sumOfWeights += Test.getWeight();
				testContribution = testPercent * (Test.getWeight() * 100.0D)
						/ 100.0D;
			} catch (NumberFormatException nfe) {
			}
			try {
				if (quizField.getText().contains(".5"))
					quizPercent = (int) Math.ceil(Double.parseDouble(quizField
							.getText()));
				else {
					quizPercent = Double.parseDouble(quizField.getText());
				}
				quizPercent = Math.round(quizPercent);
				sumOfWeights += Quiz.getWeight();
				quizContribution = quizPercent * (Quiz.getWeight() * 100.0D)
						/ 100.0D;
			} catch (NumberFormatException nfe) {
			}
			try {
				if (classWorkField.getText().contains(".5"))
					classPercent = (int) Math.ceil(Double
							.parseDouble(classWorkField.getText()));
				else {
					classPercent = Double.parseDouble(classWorkField.getText());
				}
				classPercent = Double.parseDouble(classWorkField.getText());
				classPercent = Math.round(classPercent);
				sumOfWeights += ClassWork.getWeight();
				classContribution = classPercent
						* (ClassWork.getWeight() * 100.0D) / 100.0D;
			} catch (NumberFormatException nfe) {
			}
			try {
				if (homeWorkField.getText().contains(".5"))
					homePercent = (int) Math.ceil(Double
							.parseDouble(homeWorkField.getText()));
				else {
					homePercent = Double.parseDouble(homeWorkField.getText());
				}
				homePercent = Integer.parseInt(homeWorkField.getText());
				homePercent = Math.round(homePercent);
				sumOfWeights += HomeWork.getWeight();
				homeContribution = homePercent
						* (HomeWork.getWeight() * 100.0D) / 100.0D;
			} catch (NumberFormatException nfe) {
			}
			sumOfContributions = testContribution + quizContribution
					+ homeContribution + classContribution;

			finalGrade = sumOfContributions / sumOfWeights;
			finalGrade = Math.round(finalGrade);
			if (isHonorsClass) {
				finalGrade += 3.0D;
			}
			nf = NumberFormat.getInstance();
			grades = new JTextPane();
			grades.setText("Test Average: " + nf.format(testPercent) + "\n"
					+ "Quiz Average: " + nf.format(quizPercent) + "\n"
					+ "Classwork Average: " + nf.format(classPercent) + "\n"
					+ "HomeWork Average: " + nf.format(homePercent) + "\n\n"
					+ "Total Average: " + nf.format(finalGrade));

			grades.setFont(new Font("Serif", 1, 22));
			remove(panel);
			finalLabel = new JLabel();
			finalLabel.setFont(new Font("Serif", 1, 22));
			finalLabel.setForeground(Color.RED);
			finalLabel.setLocation(500, 500);
			finalLabel.setText("Your grade is: " + nf.format(finalGrade) + "%");
			if (finalGrade - 3.0D <= 0.0D) {
				finalLabel.setText("No Grades Entered");
			}
			panel2 = new BackgroundImagePanel();
			panel2.add(finalLabel);
			panel.add(grades);
			panel2.add(back);
			panel2.add(print);
			add(panel2);
			setVisible(true);
		} else if (e.getSource().equals(back)) {
			remove(panel2);
			add(panel);
			setVisible(true);
		} else if (e.getSource().equals(honorsClass)) {
			if (honorsClass.getSelectedItem().equals(honors[0])) {
				setHonorsClass(true);
				honorsClass.setSelectedIndex(0);
			} else {
				setHonorsClass(false);
				honorsClass.setSelectedIndex(1);
			}
		} else if (e.getSource().equals(print)) {
			try {
				String subject = JOptionPane.showInputDialog(this,
						"What subject is this?");

				grades.print(new MessageFormat(subject), new MessageFormat(
						subject), true, null, null, true);
			} catch (PrinterException e1) {
				e1.printStackTrace();
			} catch (NullPointerException ex) {
			}
		}
	}

	public static void main(String[] args) {
		window = new CalculateGrade();
		window.setDefaultCloseOperation(3);
	}

	public boolean isHonorsClass() {
		return isHonorsClass;
	}

	public void setHonorsClass(boolean isHonorsClass) {
		this.isHonorsClass = isHonorsClass;
	}

}