package de.seelenoede.converter;

import java.io.File;

import javax.swing.JFrame;
import javax.swing.JTextField;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridLayout;
import java.awt.Color;

import javax.swing.JProgressBar;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;

import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;

public class MainForm {

	private JFrame frame;
	private JTextField textField;
	private static Converter converter;
	private JLabel lblZip;
	private JLabel lblDone;
	private JPanel panel_labels;
	private JLabel lblExtract;
	private JLabel lblPath;
	private boolean alreadyRunning;
	private JPanel panel_progressBars;
	private JProgressBar progressBar_Extraction;
	private JProgressBar progressBar_Zip;
	private JPanel panel_progress;
	private JButton btnBrowse;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
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
		alreadyRunning = false;
		frame = new JFrame("PDF To CBZ Converter");
		frame.getContentPane().setBackground(Color.LIGHT_GRAY);
		frame.setResizable(false);
		frame.setBackground(Color.WHITE);
		frame.setBounds(100, 100, 489, 150);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		JPanel panel_path = new JPanel();
		panel_path.setBackground(Color.WHITE);
		frame.getContentPane().add(panel_path, BorderLayout.NORTH);

		lblPath = new JLabel("Path: ");
		panel_path.add(lblPath);

		textField = new JTextField();
		panel_path.add(textField);
		textField.setColumns(30);

		btnBrowse = new JButton("Browse");
		btnBrowse.setBackground(Color.LIGHT_GRAY);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(alreadyRunning){
					return;
				}
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF file", "pdf");
				chooser.setFileFilter(filter);
				int val = chooser.showOpenDialog(null);

				if (val == JFileChooser.APPROVE_OPTION) {
					textField.setText(chooser.getSelectedFile()
							.getAbsolutePath());
				}
			}
		});
		panel_path.add(btnBrowse);

		JButton btnStart = new JButton("Start");
		btnStart.setSelected(true);
		btnStart.setBackground(Color.LIGHT_GRAY);

		frame.getRootPane().setDefaultButton(btnStart);
		
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Thread noBlock = new Thread() {
					public void run() {
						resetElements();
						startConvert();
					}
				};
				if (!alreadyRunning) {
					noBlock.start();
					alreadyRunning = true;
				}

			}
		});

		frame.getContentPane().add(btnStart, BorderLayout.SOUTH);

		panel_progress = new JPanel();
		frame.getContentPane().add(panel_progress, BorderLayout.CENTER);
		panel_progress.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("116px"), ColumnSpec.decode("318px:grow"), },
				new RowSpec[] { RowSpec.decode("60px"), }));

		panel_labels = new JPanel();
		panel_labels.setBorder(new EmptyBorder(0, 5, 0, 0));
		panel_progress.add(panel_labels, "1, 1, fill, fill");
		panel_labels.setBackground(Color.WHITE);
		panel_labels.setLayout(new GridLayout(0, 1, 0, 0));

		lblExtract = new JLabel("");
		lblExtract.setBackground(Color.WHITE);
		panel_labels.add(lblExtract);

		lblZip = new JLabel("");
		lblZip.setBackground(Color.WHITE);
		panel_labels.add(lblZip);

		lblDone = new JLabel("");
		lblDone.setBackground(Color.WHITE);
		panel_labels.add(lblDone);

		panel_progressBars = new JPanel();
		panel_progress.add(panel_progressBars, "2, 1, fill, fill");
		panel_progressBars.setBorder(new EmptyBorder(0, 0, 0, 10));
		panel_progressBars.setBackground(Color.WHITE);
		panel_progressBars.setLayout(new GridLayout(3, 1, 0, 0));

		progressBar_Extraction = new JProgressBar();
		panel_progressBars.add(progressBar_Extraction);

		progressBar_Zip = new JProgressBar();
		panel_progressBars.add(progressBar_Zip);
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

		lblExtract.setText("Extract PDF...");
		converter.convertToImage(filePath, progressBar_Extraction);
		lblZip.setText("Zip To CBZ...");
		converter.zip(filePath, progressBar_Zip);
		lblDone.setText("Done!");
		alreadyRunning = false;
	}

	private void resetElements() {
		lblExtract.setText("");
		lblZip.setText("");
		lblDone.setText("");

		progressBar_Extraction.setValue(0);
		progressBar_Zip.setValue(0);
	}
}
