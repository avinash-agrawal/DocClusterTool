package dct;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;

public class Interface extends JFrame {

	private static final long serialVersionUID = 18L;

	private boolean mSelectedCranfield;
	private boolean mSelectedNewsgroups;
	private boolean mSelectedReuters;
	private String mCollDir;
	private String mTfIdfOptions;
	private String mDataFile;
	private int mNumOfClusters;
	private String mLinkType;
	private String mDistanceFunction;
	private boolean mUseBranchLength;
	private String mClusterOptions;
	private JTextField txtFldDataFile;

	public Interface() throws IOException {
		mSelectedCranfield = false;
		mSelectedNewsgroups = false;
		mSelectedReuters = false;
		mCollDir = "";
		mTfIdfOptions = "";
		mDataFile = "";
		mNumOfClusters = 1;
		mLinkType = "";
		mDistanceFunction = "";
		mUseBranchLength = false;
		mClusterOptions = "";

		initUI();
	}

	public static JTextArea console(final InputStream out) {
		final JTextArea area = new JTextArea();

		// handle "System.out"
		new SwingWorker<Void, String>() {
			@Override
			protected Void doInBackground() throws Exception {
				Scanner s = new Scanner(out);
				while(s.hasNextLine())
					publish(s.nextLine() + "\n");
				s.close();
				return null;
			}

			@Override
			protected void process(List<String> chunks) {
				for(String line : chunks)
					area.append(line);
			}
		}.execute();

		return area;
	}

	private final void initUI() {
		setTitle("Document Cluster Tool");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 900, 480);
		setLocationRelativeTo(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane, BorderLayout.CENTER);

		JPanel panelDocumentManager = new JPanel();
		tabbedPane.addTab("Document Manager", null, panelDocumentManager, null);

		String descDocMan = "<html>The Document Manager takes a document collection and calculates "
				+ "the document vectors and puts them in a format that is compatible with the WEKA"
				+ "clustering algorithms. Each collection requires a different type of conversion "
				+ "technique."
				+ "<br /><br />"
				+ "The Cranfield collection was download from the UTD Linux servers at "
				+ "/people/cs/s/sanda/cs6322/Cranfield."
				+ "<br /><br />"
				+ "The 20 Newsgroups collection can be downloaded at "
				+ "http://qwone.com/~jason/20Newsgroups/20news-18828.tar.gz"
				+ "<br /><br />"
				+ "The Reuters collection can be downloaded at "
				+ "http://kdd.ics.uci.edu/databases/reuters21578/reuters21578.tar.gz</html>";

		String descClustMan = "<html>The Clustering Manager takes the file created by the Document "
				+ "Manager and performs WEKA's hiearchical cluster algorithms. The algorithm is "
				+ "determined by your selection here.</html>";

		final ButtonGroup btnGrpCollection = new ButtonGroup();
		final ButtonGroup btnGrpTermRep = new ButtonGroup();

		JLabel lblDescDocMan = new JLabel(descDocMan);
		lblDescDocMan.setBounds(12, 12, 564, 213);
		lblDescDocMan.setVerticalAlignment(SwingConstants.TOP);

		JLabel lblCollection = new JLabel("Collection");
		lblCollection.setBounds(95, 237, 70, 15);

		final JRadioButton rdbtnCranfield = new JRadioButton("Cranfield");
		rdbtnCranfield.setBounds(177, 233, 89, 23);
		btnGrpCollection.add(rdbtnCranfield);
		rdbtnCranfield.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnCranfield.isSelected()) {
					mSelectedCranfield = true;
				}
				else {
					mSelectedCranfield = false;
				}
			}
		});

		final JRadioButton rdbtnNewsgroups = new JRadioButton("20 News Groups");
		rdbtnNewsgroups.setBounds(271, 233, 139, 23);
		btnGrpCollection.add(rdbtnNewsgroups);
		rdbtnNewsgroups.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnNewsgroups.isSelected()) {
					mSelectedNewsgroups = true;
				}
				else {
					mSelectedNewsgroups = false;
				}
			}
		});

		final JRadioButton rdbtnReuters = new JRadioButton("Reuters");
		rdbtnReuters.setBounds(415, 233, 81, 23);
		btnGrpCollection.add(rdbtnReuters);
		rdbtnReuters.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnReuters.isSelected()) {
					mSelectedReuters = true;
				}
				else {
					mSelectedReuters = false;
				}
			}
		});

		final JButton btnBrowseCollection = new JButton("Browse");
		btnBrowseCollection.setBounds(77, 264, 87, 25);

		final JTextField txtFldCollDir = new JTextField();
		txtFldCollDir.setBounds(176, 267, 400, 19);
		txtFldCollDir.setColumns(10);

		btnBrowseCollection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Select Collection Directory");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = fc.showOpenDialog(Interface.this);

				if(returnVal == JFileChooser.APPROVE_OPTION) {
					mCollDir = fc.getSelectedFile().toString();
					txtFldCollDir.setText(mCollDir);
				}
				else {
					txtFldCollDir.setText("");
				}
				txtFldCollDir.setCaretPosition(mCollDir.length());
			}
		});

		JLabel lblTermRep = new JLabel("Term Representation");
		lblTermRep.setBounds(12, 309, 150, 15);

		final JRadioButton rdbtnRawCounts = new JRadioButton("Raw Counts");
		rdbtnRawCounts.setBounds(176, 294, 109, 23);
		btnGrpTermRep.add(rdbtnRawCounts);
		rdbtnRawCounts.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnRawCounts.isSelected()) {
					mTfIdfOptions = "-C";
				}
			}
		});

		final JRadioButton rdbtnTfIdfFormula1 = new JRadioButton("log(1 + tf[i][j])");
		rdbtnTfIdfFormula1.setBounds(290, 294, 122, 23);
		btnGrpTermRep.add(rdbtnTfIdfFormula1);
		rdbtnTfIdfFormula1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnTfIdfFormula1.isSelected()) {
					mTfIdfOptions = "-C -T -N 1";
				}
			}
		});

		final JRadioButton rdbtnTfIdfFormula2 = new JRadioButton("tf * log(collectionsize / df)");
		rdbtnTfIdfFormula2.setBounds(176, 321, 205, 23);
		btnGrpTermRep.add(rdbtnTfIdfFormula2);
		rdbtnTfIdfFormula2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(rdbtnTfIdfFormula2.isSelected()) {
					mTfIdfOptions = "-C -I -N 1";
				}
			}
		});

		final PipedInputStream outDocMan = new PipedInputStream();

		final JTextArea txtAreaDocManConsole = console(outDocMan);
		txtAreaDocManConsole.setBounds(588, 12, 293, 400);

		final JButton btnRunDocMan = new JButton("Run");
		btnRunDocMan.setBounds(241, 368, 61, 25);
		btnRunDocMan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					System.setOut(new PrintStream(new PipedOutputStream(outDocMan), true));
				}
				catch(IOException error) {
					System.err.println("Unable to write to Document Manager Console.");
				}

				System.out.println("Running...");

				DocumentManager docMan = new DocumentManager();

				try {
					if(mSelectedCranfield) {
						docMan.createCranDataset(mCollDir, mTfIdfOptions);
					}
					else if(mSelectedNewsgroups) {

					}
					else if(mSelectedReuters) {

					}
					else {

					}
					System.out.println("Done.");
				}
				catch(Exception error) {
					System.out.println("Failed.");
					System.err.println(error.getMessage());
					System.err.println(error.getStackTrace());
				}
				System.setOut(System.out);
			}
		});

		panelDocumentManager.setLayout(null);
		panelDocumentManager.add(lblDescDocMan);
		panelDocumentManager.add(lblCollection);
		panelDocumentManager.add(rdbtnCranfield);
		panelDocumentManager.add(rdbtnNewsgroups);
		panelDocumentManager.add(rdbtnReuters);
		panelDocumentManager.add(btnBrowseCollection);
		panelDocumentManager.add(txtFldCollDir);
		panelDocumentManager.add(lblTermRep);
		panelDocumentManager.add(rdbtnRawCounts);
		panelDocumentManager.add(rdbtnTfIdfFormula1);
		panelDocumentManager.add(rdbtnTfIdfFormula2);
		panelDocumentManager.add(txtAreaDocManConsole);
		panelDocumentManager.add(btnRunDocMan);

		JPanel panelClusterManager = new JPanel();
		tabbedPane.addTab("Clustering Manager", null, panelClusterManager, null);

		JLabel lblDescClustMan = new JLabel(descClustMan);
		lblDescClustMan.setBounds(12, 52, 509, 45);
		lblDescClustMan.setVerticalAlignment(SwingConstants.TOP);

		JLabel lblNumOfClusters = new JLabel("# of Clusters");
		lblNumOfClusters.setBounds(143, 203, 91, 15);

		final JButton btnBrowseDataFile = new JButton("Browse");
		btnBrowseDataFile.setBounds(12, 158, 87, 25);

		txtFldDataFile = new JTextField();
		txtFldDataFile.setBounds(105, 161, 416, 19);
		txtFldDataFile.setColumns(10);

		btnBrowseDataFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				fc.setCurrentDirectory(new File("data/"));
				fc.setDialogTitle("Select ARFF Data File");
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = fc.showOpenDialog(Interface.this);

				if(returnVal == JFileChooser.APPROVE_OPTION) {
					mDataFile = fc.getSelectedFile().toString();
					txtFldDataFile.setText(mDataFile);
				}
				else {
					txtFldDataFile.setText("");
				}
				txtFldDataFile.setCaretPosition(mDataFile.length());
			}
		});

		final JSpinner spnrNumOfClusters = new JSpinner();
		spnrNumOfClusters.setBounds(252, 201, 72, 20);
		spnrNumOfClusters.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null,
				new Integer(1)));

		JLabel lblLinkType = new JLabel("Link Type");
		lblLinkType.setBounds(167, 244, 67, 15);

		final JComboBox<String> cmbBxLinkType = new JComboBox<String>();
		cmbBxLinkType.setBounds(252, 239, 189, 24);
		cmbBxLinkType.setModel(new DefaultComboBoxModel<String>(new String[] {"single-link",
				"complete-link", "average-link", "mean", "centroid", "ward",
				"adjacent complete-link", "neighbor-joining"}));
		cmbBxLinkType.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cmbBxLinkType.getSelectedItem() == "single-link") {
					mLinkType = "-L SINGLE";
				}
				else if(cmbBxLinkType.getSelectedItem() == "complete-link") {
					mLinkType = "-L COMPLETE";
				}
				else if(cmbBxLinkType.getSelectedItem() == "average-link") {
					mLinkType = "-L AVERAGE";
				}
				else if(cmbBxLinkType.getSelectedItem() == "mean") {
					mLinkType = "-L MEAN";
				}
				else if(cmbBxLinkType.getSelectedItem() == "centroid") {
					mLinkType = "-L CENTROID";
				}
				else if(cmbBxLinkType.getSelectedItem() == "ward") {
					mLinkType = "-L WARD";
				}
				else if(cmbBxLinkType.getSelectedItem() == "adjacent complete-link") {
					mLinkType = "-L ADJCOMPLETE";
				}
				else if(cmbBxLinkType.getSelectedItem() == "neighbor-joining") {
					mLinkType = "-L NEIGHBOR_JOINING";
				}
				else {
					mLinkType = "";
				}
			}
		});

		JLabel lblDistFunc = new JLabel("Distance Function");
		lblDistFunc.setBounds(107, 286, 127, 15);

		final JComboBox<String> cmbBxDistFunc = new JComboBox<String>();
		cmbBxDistFunc.setBounds(252, 281, 171, 24);
		cmbBxDistFunc.setModel(new DefaultComboBoxModel<String>(new String[] {"Chebyshev distance",
				"edit distance", "Euclidean distance", "Manhattan distance"}));
		cmbBxDistFunc.setSelectedIndex(2);
		cmbBxDistFunc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cmbBxDistFunc.getSelectedItem() == "Chebyshev distance") {
					mDistanceFunction = "-A weka.core.ChebyshevDistance";
				}
				else if(cmbBxDistFunc.getSelectedItem() == "edit distance") {
					mDistanceFunction = "-A weka.core.EditDistance";
				}
				else if(cmbBxDistFunc.getSelectedItem() == "Euclidean distance") {
					mDistanceFunction = "-A weka.core.EuclideanDistance";
				}
				else if(cmbBxDistFunc.getSelectedItem() == "Manhattan distance") {
					mDistanceFunction = "-A weka.core.ManhattanDistance";
				}
				else {
					mDistanceFunction = "";
				}
			}
		});

		JLabel lblDistanceBranchLength = new JLabel("Distance Branch Length");
		lblDistanceBranchLength.setBounds(65, 328, 169, 15);

		final JToggleButton tglbtnUseBranchLength = new JToggleButton("False");
		tglbtnUseBranchLength.setBounds(252, 323, 72, 25);
		tglbtnUseBranchLength.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(tglbtnUseBranchLength.isSelected()) {
					tglbtnUseBranchLength.setText("True");
					mUseBranchLength = true;
				}
				else {
					tglbtnUseBranchLength.setText("False");
					mUseBranchLength = false;
				}
			}
		});

		final PipedInputStream outClustMan = new PipedInputStream();

		JTextArea txtAreaClustManConsole = console(outClustMan);
		txtAreaClustManConsole.setEditable(false);
		txtAreaClustManConsole.setBounds(533, 12, 348, 400);

		final JButton btnRunClustMan = new JButton("Run");
		btnRunClustMan.setBounds(213, 366, 61, 25);
		btnRunClustMan.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					System.setOut(new PrintStream(new PipedOutputStream(outClustMan), true));
				}
				catch(IOException error) {
					System.err.println("Unable to write to Clustering Manager Console.");
				}
				System.out.println("Running...");

				ClusterManager clustMan = new ClusterManager();

				mNumOfClusters = (int)spnrNumOfClusters.getValue();

				if(mUseBranchLength) {
					mClusterOptions = "-N " + mNumOfClusters + " " + mLinkType + " "
							+ mDistanceFunction + " -B";
				}
				else {
					mClusterOptions = "-N " + mNumOfClusters + " " + mLinkType + " "
							+ mDistanceFunction;
				}
				try {
					clustMan.clusterCollection(mDataFile, mClusterOptions);
					System.out.println("Done.");
				}
				catch(Exception error) {
					System.out.println("Failed.");
					System.err.println(error.getMessage());
					System.err.println(error.getStackTrace());
				}
				System.setOut(System.out);
			}
		});

		panelClusterManager.setLayout(null);
		panelClusterManager.add(btnBrowseDataFile);
		panelClusterManager.add(txtFldDataFile);
		panelClusterManager.add(lblLinkType);
		panelClusterManager.add(lblNumOfClusters);
		panelClusterManager.add(lblDistFunc);
		panelClusterManager.add(lblDistanceBranchLength);
		panelClusterManager.add(cmbBxDistFunc);
		panelClusterManager.add(cmbBxLinkType);
		panelClusterManager.add(tglbtnUseBranchLength);
		panelClusterManager.add(spnrNumOfClusters);
		panelClusterManager.add(btnRunClustMan);
		panelClusterManager.add(lblDescClustMan);
		panelClusterManager.add(txtAreaClustManConsole);
	}

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
}
