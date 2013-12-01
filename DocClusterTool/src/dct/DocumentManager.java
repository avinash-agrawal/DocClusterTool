package dct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.apache.commons.io.FilenameUtils;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.core.converters.ArffLoader;
import weka.core.converters.ArffSaver;
import weka.core.converters.TextDirectoryLoader;
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class DocumentManager {
	private WordTokenizer mTokenizer;

	public DocumentManager() {
		mTokenizer = new WordTokenizer();
		mTokenizer.setDelimiters(" \r\n\t.,;:?!-_()[]{}");
	}

	private boolean isTag(String word) {
		if(word.charAt(0) == '<' && word.charAt(word.length() - 1) == '>')
			return true;
		return false;
	}

	private String cleanWord(String dirty) {
		String noise = "`~#$%^&*=+\\|\'\"/";
		StringBuilder clean = new StringBuilder(dirty);

		for(int i = 0; i < noise.length(); i++) {
			for(int j = 0; j < clean.length(); j++) {
				if(clean.charAt(j) == noise.charAt(i)) {
					clean.deleteCharAt(j);
					j--;
				}
			}
		}

		return clean.toString();
	}

	private String tokenize(String rawLine) {
		mTokenizer.tokenize(rawLine);
		StringBuilder line = new StringBuilder();

		while(mTokenizer.hasMoreElements()) {
			String token = mTokenizer.nextElement().toString();
			if(!isTag(token)) {
				line.append(cleanWord(token) + ' ');
			}
		}
		return line.toString();
	}

	private void createWordVector(String filetxt, String tfIdfOptions) throws Exception {
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(filetxt));
		Instances textData = loader.getDataSet();

		StringToWordVector filter = new StringToWordVector();
		filter.setOptions(Utils.splitOptions(tfIdfOptions + " -L -S"
				+ " -stemmer weka.core.stemmers.SnowballStemmer"));
		filter.setInputFormat(textData);
		Instances wvData = Filter.useFilter(textData, filter);
		wvData.setRelationName(FilenameUtils.getBaseName(filetxt));

		String filewv = FilenameUtils.getFullPath(filetxt) + FilenameUtils.getBaseName(filetxt)
				+ "_wv."
				+ FilenameUtils.getExtension(filetxt);

		ArffSaver saver = new ArffSaver();
		saver.setFile(new File(filewv));
		saver.setInstances(wvData);
		saver.writeBatch();
	}

	public void createCranDataset(String cranDir, String tfIdfOptions) throws Exception {
		String collectionName = FilenameUtils.getBaseName(cranDir);
		File inDir = new File(cranDir);
		File arffText = new File("data/" + collectionName + ".arff");
		ArffSaver saver = new ArffSaver();
		saver.setFile(arffText);

		Attribute contents = new Attribute("contents", (FastVector)null);
		Attribute filename = new Attribute("filename", (FastVector)null);

		FastVector groupnv = new FastVector(1);
		groupnv.addElement(collectionName);
		Attribute group = new Attribute("class", (FastVector)null);

		FastVector attrs = new FastVector(3);
		attrs.addElement(contents);
		attrs.addElement(filename);
		attrs.addElement(group);

		Instances data = new Instances(collectionName, attrs, 0);
		data.setClass(group);

		if(inDir.isDirectory()) {
			String files[] = inDir.list();

			for(int i = 0; i < files.length; i++) {
				File file = new File(inDir + "/" + files[i]);

				if(!file.isDirectory()) {
					BufferedReader reader = new BufferedReader(new FileReader(file));
					Instance inst = new Instance(3);

					String rawLine = "";
					String line = "";

					while((rawLine = reader.readLine()) != null)
						line += tokenize(rawLine);

					inst.setValue((Attribute)attrs.elementAt(0), line);
					inst.setValue((Attribute)attrs.elementAt(1), files[i]);
					inst.setValue((Attribute)attrs.elementAt(2), collectionName);
					data.add(inst);
					reader.close();
				}
			}
			saver.setInstances(data);
			saver.writeBatch();
		}
		else {
			System.out.println(cranDir + " is not a directory.");
		}
		createWordVector(arffText.toString(), tfIdfOptions);
	}

	public void create20NewsDataset(String newsDir, String tfIdfOptions) throws Exception {
		String collectionName = FilenameUtils.getBaseName(newsDir);
		String arffText = "data/" + collectionName + ".arff";

		TextDirectoryLoader loader = new TextDirectoryLoader();
		loader.setOptions(Utils.splitOptions("-F -dir \"" + newsDir + "\""));
		Instances data = loader.getDataSet();
		data.setRelationName(collectionName);
		data.setClassIndex(2);

		for(int i = 0; i < data.numInstances(); i++) {
			String rawLine = data.instance(i).stringValue(0);
			String line = tokenize(rawLine);
			data.instance(i).setValue(0, line);
		}

		ArffSaver saver = new ArffSaver();
		saver.setFile(new File(arffText));
		saver.setInstances(data);
		saver.writeBatch();

		createWordVector(arffText, tfIdfOptions);
	}
}
