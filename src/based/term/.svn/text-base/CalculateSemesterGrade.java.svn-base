package based.term;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.text.NumberFormat;

import javax.imageio.ImageIO;
import javax.swing.*;

import panel.images.BackgroundImagePanel;

public class CalculateSemesterGrade extends JFrame {

	private static final long serialVersionUID = 1L;
	private JLabel q1, q2, exam;
	private JTextField q1Field, q2Field, examField;
	private BufferedImage image;
	private NumberFormat nf;
	private JButton submit, back, print;
	private double q1Percent, q2Percent, examPercent, q1Contribution,
			q2Contribution, examContribution;
	private JTextPane grades;
	private JPanel panel, panel2;
	private JLabel finalLabel;
	private static CalculateSemesterGrade window;

	public CalculateSemesterGrade() {
		super("Grade Calculator");

		panel = new BackgroundImagePanel();
		panel.setLayout(new FlowLayout());

		q1 = new JLabel("Quarter 1 Average");
		q2 = new JLabel("Quarter 2 Average");
		exam = new JLabel("Semester Exam");

		q1Field = new JTextField(15);
		q2Field = new JTextField(15);
		examField = new JTextField(15);

		submit = new JButton("Submit");
		back = new JButton("Back");
		print = new JButton("Print");

		ListenerHandler handler = new ListenerHandler();

		submit.addActionListener(handler);
		back.addActionListener(handler);
		print.addActionListener(handler);

		try {
			image = ImageIO.read(new File("School.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		panel.add(q1);
		panel.add(q1Field);
		panel.add(q2);
		panel.add(q2Field);
		panel.add(exam);
		panel.add(examField);
		panel.add(submit);
		add(panel);
		setVisible(true);
		setSize(image.getWidth() - 150, image.getHeight());
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		setIconImage(image);
	}

	public static void main(String[] args) {
		 window = new CalculateSemesterGrade();
	}

	private class ListenerHandler implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource().equals(submit)) {
				double finalGrade = 0.0D;
				double sumOfContributions = 0.0D;
				double sumOfWeights = 0.0D;
				try {
					q1Percent = Double.parseDouble(q1Field.getText());
					q1Percent = Math.round(q1Percent);
					sumOfWeights += Quarter1.getWeight();
					q1Contribution = q1Percent * (Quarter1.getWeight() * 100)
							/ 100;
				} catch (NumberFormatException ex) {
				}

				try {
					q2Percent = Double.parseDouble(q2Field.getText());
					q2Percent = Math.round(q2Percent);
					sumOfWeights += Quarter2.getWeight();
					q2Contribution = q2Percent * (Quarter2.getWeight() * 100)
							/ 100;
				} catch (NumberFormatException ex) {
				}

				try {
					examPercent = Double.parseDouble(examField.getText());
					examPercent = Math.round(examPercent);
					sumOfWeights += Exam.getWeight();
					examContribution = examPercent * (Exam.getWeight() * 100)
							/ 100;
				} catch (NumberFormatException ex) {
				}

				sumOfContributions = q1Contribution + q2Contribution
						+ examContribution;

				finalGrade = sumOfContributions / sumOfWeights;
				finalGrade = Math.round(finalGrade);

				nf = NumberFormat.getInstance();
				grades = new JTextPane();
				grades.setText("Quarter 1 Average: " + nf.format(q1Percent)
						+ "\n" + "Quarter 2 Average: " + nf.format(q2Percent)
						+ "\n" + "Semester Exam: " + nf.format(examPercent)
						+ "\n\n" + "Total Average: " + nf.format(finalGrade));

				grades.setFont(new Font("Serif", 1, 22));
				remove(panel);
				finalLabel = new JLabel();
				finalLabel.setFont(new Font("Serif", 1, 22));
				finalLabel.setForeground(Color.RED);
				finalLabel.setLocation(500, 500);
				finalLabel.setText("Your grade is: " + nf.format(finalGrade)
						+ "%");
				if (finalGrade - 3.0D <= 0.0D) {
					finalLabel.setText("No Grades Entered");
				}
				panel2 = new BackgroundImagePanel();
				panel2.add(finalLabel);
				panel2.add(back);
				panel2.add(print);
				add(panel2);
				setVisible(true);
			} else if (e.getSource().equals(back)) {
				remove(panel2);
				add(panel);
				setVisible(true);
			} else if (e.getSource().equals(print)) {
				try {
					String subject = JOptionPane.showInputDialog(window,
							"What Subject is this?",
							JOptionPane.INFORMATION_MESSAGE);

					grades.print(new MessageFormat(subject), new MessageFormat(
							subject), true, null, null, true);
				} catch (PrinterException e1) {
					e1.printStackTrace();
				} catch (NullPointerException ex) {
				}
			}

		}

	}
}
