package dct;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;

import weka.clusterers.ClusterEvaluation;
import weka.clusterers.HierarchicalClusterer;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.gui.explorer.ClustererPanel;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.VisualizePanel;

public class ClusterManager {
	public ClusterManager() {}

	private void clusterCollection(String dataFile, String[] options)
			throws IOException, Exception {
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(dataFile));

		Instances data = loader.getDataSet();

		HierarchicalClusterer clusterer = new HierarchicalClusterer();
		clusterer.setOptions(options);
		clusterer.buildClusterer(data);

		ClusterEvaluation eval = new ClusterEvaluation();
		eval.setClusterer(clusterer);
		eval.evaluateClusterer(data);

		System.out.println(eval.clusterResultsToString());

		PlotData2D plotData =
				ClustererPanel.setUpVisualizableInstances(data, eval);
		plotData.setPlotName(data.relationName());

		VisualizePanel vp = new VisualizePanel();
		vp.setName("(" + data.relationName() + ")");
		plotData.setPlotName("(" + data.relationName() + ")");
		vp.addPlot(plotData);

		String plotName = vp.getName();
		final javax.swing.JFrame jf =
				new javax.swing.JFrame("Weka Clusterer Visualize: " + plotName);
		jf.setSize(1024, 768);
		jf.getContentPane().setLayout(new BorderLayout());
		jf.getContentPane().add(vp, BorderLayout.CENTER);
		jf.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				jf.dispose();
			}
		});
		jf.setVisible(true);
	}

	public static void main(String[] args) throws IOException, Exception {
		String dataFile = "/home/marc/data/arff/cranfield_wv.arff";
		String[] options = weka.core.Utils.splitOptions("-N 10 -L COMPLETE");
		ClusterManager clustMan = new ClusterManager();
		clustMan.clusterCollection(dataFile, options);
	}
}
