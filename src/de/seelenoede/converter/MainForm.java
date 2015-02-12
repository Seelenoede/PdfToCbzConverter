package de.seelenoede.converter;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JTextPane;

import java.awt.GridLayout;
import java.awt.Color;

public class MainForm {

	private JFrame frame;
	private JTextField textField;
	private static Converter converter;
	private JTextPane textPane_1;
	private JTextPane textPane_2;
	private JPanel panel_1;
	private JLabel label;
	private JLabel lblPath;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainForm window = new MainForm();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public MainForm() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("PDF To CBZ Converter");
		frame.setBackground(Color.WHITE);
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 142);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		frame.getContentPane().add(panel, BorderLayout.NORTH);

		lblPath = new JLabel("Path: ");
		panel.add(lblPath);

		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(30);

		panel_1 = new JPanel();
		panel_1.setBackground(Color.WHITE);
		frame.getContentPane().add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new GridLayout(0, 1, 0, 0));

		label = new JLabel("");
		label.setBackground(Color.WHITE);
		panel_1.add(label);

		textPane_1 = new JTextPane();
		textPane_1.setBackground(Color.WHITE);
		textPane_1.setEditable(false);
		panel_1.add(textPane_1);

		textPane_2 = new JTextPane();
		textPane_2.setBackground(Color.WHITE);
		textPane_2.setEditable(false);
		panel_1.add(textPane_2);

		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startConvert();
			}
		});
		frame.getContentPane().add(btnStart, BorderLayout.SOUTH);
	}

	/**
	 * Creates temporary folder and starts the conversion process
	 */
	private void startConvert() {

		String filePath = textField.getText();

		String tempPath = filePath.substring(0, filePath.lastIndexOf('\\'))
				+ "\\tmp";
		converter = new Converter(tempPath + "\\");

		while (true) {
			File file = new File(converter.tmpPath);
			int i = 0;
			if (!file.exists()) {
				file.mkdir();
				break;
			} else
				converter.tmpPath = tempPath + i + "\\";
			i++;
		}

		lblPath.setText("Detsumm: ");
		label.setText("Extract PDF...");
		converter.convertToImage(filePath);
		textPane_1.setText("Zip To CBZ...");
		panel_1.repaint();
		converter.zip(filePath);

		textPane_2.setText("Done!");
		panel_1.repaint();

	}
}
