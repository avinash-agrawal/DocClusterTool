package dct;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.LayoutStyle.ComponentPlacement;

public class Interface extends JFrame {

	private static final long serialVersionUID = 18L;
	private final ButtonGroup btnGrpCollection = new ButtonGroup();
	private final ButtonGroup btnGrpTermRep = new ButtonGroup();
	private JTextField txtFldDataDir;

	private boolean mSelectedCranfield = false;
	private boolean mSelected20Newsgroups = false;
	private boolean mSelectedReuters = false;
	private String mDataDir;
	private String mTfIdfOption;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface frame = new Interface();
					frame.setVisible(true);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private JRadioButton getSelectedRadioButton(ButtonGroup bg) {
		for(Enumeration<AbstractButton> e = bg.getElements(); e
				.hasMoreElements();) {
			JRadioButton b = (JRadioButton)e.nextElement();
			if(b.getModel() == bg.getSelection()) {
				return b;
			}
		}
		return null;
	}

	/**
	 * Create the frame.
	 */
	public Interface() {
		setTitle("Document Cluster Tool");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		setLocationRelativeTo(null);

		String description = "<html>The Document Manager takes a document "
				+ "collection and calculates the document vectors and puts them"
				+ " in a format that is compatible with the WEKA clustering "
				+ "algorithms. Each collection requires a different type of "
				+ "conversion technique.<br /><br />"
				+ "The Cranfield collection was download from the UTD Linux "
				+ "servers at /people/cs/s/sanda/cs6322/Cranfield.<br /><br />"
				+ "The 20 Newsgroups collection can be downloaded at "
				+ "http://qwone.com/~jason/20Newsgroups/20news-18828.tar.gz"
				+ "<br /><br />"
				+ "The Reuters collection can be downloaded at "
				+ "http://kdd.ics.uci.edu/databases/reuters21578/reuters21578."
				+ "tar.gz</html>";

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panel = new JPanel();
		tabbedPane.addTab("Document Manager", null, panel, null);

		JLabel lblCollection = new JLabel("Collection");

		JRadioButton rdbtnCranfield = new JRadioButton("Cranfield");
		btnGrpCollection.add(rdbtnCranfield);

		JRadioButton rdbtnNewsGroups = new JRadioButton("20 News Groups");
		btnGrpCollection.add(rdbtnNewsGroups);

		JRadioButton rdbtnReuters = new JRadioButton("Reuters");
		btnGrpCollection.add(rdbtnReuters);

		JRadioButton collectionSelected = getSelectedRadioButton(btnGrpCollection);
		if(collectionSelected == rdbtnCranfield) {
			mSelectedCranfield = true;
		}
		else if(collectionSelected == rdbtnNewsGroups) {
			mSelected20Newsgroups = true;
		}
		else if(collectionSelected == rdbtnReuters) {
			mSelectedReuters = true;
		}
		else {
			mSelectedCranfield = false;
			mSelected20Newsgroups = false;
			mSelectedReuters = false;
		}

		JButton btnBrowse = new JButton("Browse");

		txtFldDataDir = new JTextField();
		txtFldDataDir.setColumns(10);

		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Select Collection Directory");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(Interface.this);

				if(returnVal == JFileChooser.APPROVE_OPTION) {
					mDataDir = fc.getSelectedFile().toString();
					txtFldDataDir.setText(mDataDir);
				}
				else {
					txtFldDataDir.setText("");
				}
				txtFldDataDir.setCaretPosition(mDataDir.length());
			}
		});

		JLabel lblTermRep = new JLabel("Term Representation");

		JRadioButton rdbtnRawCounts = new JRadioButton("Raw Counts");
		btnGrpTermRep.add(rdbtnRawCounts);

		JRadioButton rdbtnTfIdfFormula1 = new JRadioButton("log(1 + tf[i][j])");
		btnGrpTermRep.add(rdbtnTfIdfFormula1);

		JRadioButton rdbtnTfIdfFormula2 = new JRadioButton(
				"tf * log(collectionSize / df)");
		btnGrpTermRep.add(rdbtnTfIdfFormula2);

		JRadioButton selectedTermRep = getSelectedRadioButton(btnGrpTermRep);
		if(selectedTermRep == rdbtnRawCounts) {
			mTfIdfOption = "-C";
		}
		else if(selectedTermRep == rdbtnTfIdfFormula1) {
			mTfIdfOption = "-C -T -N 1";
		}
		else if(selectedTermRep == rdbtnTfIdfFormula2) {
			mTfIdfOption = "-C -I -N 1";
		}
		else {
			mTfIdfOption = "";
		}

		JButton btnRun = new JButton("Run");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				DocumentManager docMan = new DocumentManager();
				try {
					if(mSelectedCranfield) {
						docMan.createCranDataset(mDataDir, mTfIdfOption);
					}
					else if(mSelected20Newsgroups) {

					}
					else if(mSelectedReuters) {

					}
					else {

					}
				}
				catch(Exception exception) {
					exception.getMessage();
				}
			}
		});

		JLabel lblDescription = new JLabel(description);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.LEADING)
												.addGroup(
														gl_panel.createSequentialGroup()
																.addContainerGap()
																.addComponent(
																		lblDescription))
												.addGroup(
														gl_panel.createSequentialGroup()
																.addGap(101)
																.addGroup(
																		gl_panel.createParallelGroup(
																				Alignment.LEADING)
																				.addGroup(
																						gl_panel.createSequentialGroup()
																								.addGroup(
																										gl_panel.createParallelGroup(
																												Alignment.TRAILING)
																												.addComponent(
																														btnBrowse)
																												.addComponent(
																														lblCollection)
																												.addComponent(
																														lblTermRep))
																								.addGroup(
																										gl_panel.createParallelGroup(
																												Alignment.LEADING)
																												.addGroup(
																														gl_panel.createSequentialGroup()
																																.addGap(12)
																																.addComponent(
																																		rdbtnCranfield)
																																.addGap(18)
																																.addComponent(
																																		rdbtnNewsGroups)
																																.addGap(18)
																																.addComponent(
																																		rdbtnReuters))
																												.addComponent(
																														txtFldDataDir,
																														GroupLayout.PREFERRED_SIZE,
																														369,
																														GroupLayout.PREFERRED_SIZE)
																												.addGroup(
																														gl_panel.createSequentialGroup()
																																.addGap(18)
																																.addGroup(
																																		gl_panel.createParallelGroup(
																																				Alignment.TRAILING)
																																				.addComponent(
																																						rdbtnTfIdfFormula2)
																																				.addGroup(
																																						gl_panel.createSequentialGroup()
																																								.addComponent(
																																										rdbtnRawCounts)
																																								.addGap(56)
																																								.addComponent(
																																										rdbtnTfIdfFormula1))))))
																				.addGroup(
																						gl_panel.createSequentialGroup()
																								.addGap(226)
																								.addComponent(
																										btnRun)))))
								.addContainerGap(GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));
		gl_panel.setVerticalGroup(gl_panel
				.createParallelGroup(Alignment.LEADING)
				.addGroup(
						gl_panel.createSequentialGroup()
								.addGap(22)
								.addComponent(lblDescription,
										GroupLayout.PREFERRED_SIZE, 208,
										GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(ComponentPlacement.UNRELATED)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(rdbtnCranfield)
												.addComponent(rdbtnNewsGroups)
												.addComponent(rdbtnReuters)
												.addComponent(lblCollection))
								.addGap(46)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE, false)
												.addComponent(
														txtFldDataDir,
														GroupLayout.PREFERRED_SIZE,
														23,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(btnBrowse))
								.addGap(39)
								.addGroup(
										gl_panel.createParallelGroup(
												Alignment.BASELINE)
												.addComponent(
														rdbtnTfIdfFormula1)
												.addComponent(rdbtnRawCounts)
												.addComponent(
														lblTermRep,
														GroupLayout.PREFERRED_SIZE,
														15,
														GroupLayout.PREFERRED_SIZE))
								.addPreferredGap(ComponentPlacement.RELATED)
								.addComponent(rdbtnTfIdfFormula2).addGap(47)
								.addComponent(btnRun)
								.addContainerGap(51, Short.MAX_VALUE)));
		panel.setLayout(gl_panel);
	}
}