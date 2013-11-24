package dct;

import weka.clusterers.*;
import weka.core.*;
import weka.core.converters.*;
import weka.core.converters.ConverterUtils.*;
import weka.gui.explorer.ClustererPanel;
import weka.gui.visualize.*;
 
import java.awt.*;
import java.io.*;
import java.text.*;
import java.util.*;
 
import javax.swing.*;

//import dct.DocumentManager;

public class DocClusterTool {
//	private Instances textToInstance(String dataDir) throws Exception {
//		TextDirectoryLoader loader = new TextDirectoryLoader();
//		String[] options = weka.core.Utils.splitOptions("-F -dir \"" + dataDir + "\"");
//		loader.setOptions(options);
//		
//		Instances dataRaw = loader.getDataSet();
//		
//		System.out.println(dataRaw.toString());
//		
//		return dataRaw;
//	}
//	
//	private void rawDataToWordVector(Instances raw) throws Exception {
//		StringToWordVector filter = new StringToWordVector();
//		Stemmer stemmer = new SnowballStemmer();
//		String[] options = weka.core.Utils.splitOptions("-C -L -S");
//		
//		filter.setOptions(options);
//		filter.setStemmer(stemmer);
//		
//		filter.setInputFormat(raw);
//		Instances data = Filter.useFilter(raw, filter);
//		
//		ArffSaver saver = new ArffSaver();
//		saver.setInstances(data);
//		saver.setFile(new File("/home/marc/data/arff/20news-18828.arff"));
//		saver.writeBatch();
//		
//		System.out.println(data.toString());
//	}
//	
//	private void wordVectorToClusterEval(String link) throws Exception {
//		ArffLoader loader = new ArffLoader();
//		loader.setFile(new File("/home/marc/data/arff/20news-18828.arff"));
//		Instances data = loader.getStructure();
//		
//		
//		HierarchicalClusterer cluster = new HierarchicalClusterer();
//		String[] options = weka.core.Utils.splitOptions("-L " + link);
//		cluster.setOptions(options);
//		
//		cluster.buildClusterer(data);
//		
//		System.out.println("Cluster:\n" + cluster.toString());
//		
//		ClusterEvaluation eval = new ClusterEvaluation();
//		eval.setClusterer(cluster);
//		eval.evaluateClusterer(data);
//		
//		System.out.println(eval.toString());
//	}
//	
//	private void clusterDocCollection(String dataDir, String tech) throws Exception {
//		rawDataToWordVector(textToInstance(dataDir));
//		wordVectorToClusterEval(tech);
//	}
	
	public static void main(String[] args) throws Exception {
//		String dataDir = "/home/marc/data/20news-18828";
//		DocClusterTool tool = new DocClusterTool();
//		
//		tool.clusterDocCollection(dataDir, "SINGLE");
		
		// load data
		String filename = "/home/marc/data/arff/20news-18828.arff";
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(filename));
		
		
	    Instances train = new Instances(new BufferedReader(new FileReader(filename)));;
	    // some data formats store the class attribute information as well
	    if (train.classIndex() != -1)
	      throw new IllegalArgumentException("Data cannot have class attribute!");
	 
	    // instantiate clusterer
	    String[] options = Utils.splitOptions("weka.clusterers.HierarchicalClusterer -L SINGLE");
	    String classname = options[0];
	    options[0] = "";
	    Clusterer clusterer = AbstractClusterer.forName(classname, options);
	 
	    // evaluate clusterer
	    clusterer.buildClusterer(train);
	    
	    Instance current;
	    while((current = train.g))
	    
	    ClusterEvaluation eval = new ClusterEvaluation();
	    eval.setClusterer(clusterer);
	    eval.evaluateClusterer(train);
	 
	    // setup visualization
	    // taken from: ClustererPanel.startClusterer()
	    PlotData2D predData = ClustererPanel.setUpVisualizableInstances(train, eval);
	    String name = (new SimpleDateFormat("HH:mm:ss - ")).format(new Date());
	    String cname = clusterer.getClass().getName();
	    if (cname.startsWith("weka.clusterers."))
	      name += cname.substring("weka.clusterers.".length());
	    else
	      name += cname;
	 
	    VisualizePanel vp = new VisualizePanel();
	    vp.setName(name + " (" + train.relationName() + ")");
	    predData.setPlotName(name + " (" + train.relationName() + ")");
	    vp.addPlot(predData);
	 
	    // display data
	    // taken from: ClustererPanel.visualizeClusterAssignments(VisualizePanel)
	    String plotName = vp.getName();
	    final javax.swing.JFrame jf = 
	      new javax.swing.JFrame("Weka Clusterer Visualize: " + plotName);
	    jf.setSize(500,400);
	    jf.getContentPane().setLayout(new BorderLayout());
	    jf.getContentPane().add(vp, BorderLayout.CENTER);
	    jf.addWindowListener(new java.awt.event.WindowAdapter() {
	      public void windowClosing(java.awt.event.WindowEvent e) {
	        jf.dispose();
	      }
	    });
	    jf.setVisible(true);
	}
}
