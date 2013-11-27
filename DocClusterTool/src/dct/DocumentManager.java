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
import weka.core.tokenizers.WordTokenizer;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class DocumentManager {
	public DocumentManager() {}

	private boolean isTag(String word) {
		if(word.charAt(0) == '<' && word.charAt(word.length() - 1) == '>')
			return true;
		return false;
	}

	private String cleanLine(String dirty) {
		String noise = "`~@#$%^&*=+\\|;:\'\",./";
		String clean = dirty;

		for(int i = 0; i < noise.length(); i++) {
			clean = clean.replace(noise.charAt(i) + "", "");
		}

		return clean;
	}

	private void createWordVector(String filetxt, String tfIdfOption)
			throws Exception {
		ArffLoader loader = new ArffLoader();
		loader.setFile(new File(filetxt));
		Instances textData = loader.getDataSet();

		StringToWordVector filter = new StringToWordVector();
		filter.setOptions(Utils.splitOptions(tfIdfOption + " -L -S"
				+ " -stemmer weka.core.stemmers.SnowballStemmer"));
		filter.setInputFormat(textData);
		Instances wvData = Filter.useFilter(textData, filter);
		wvData.setRelationName(FilenameUtils.getBaseName(filetxt));

		String filewv = FilenameUtils.getFullPath(filetxt)
				+ FilenameUtils.getBaseName(filetxt) + "_wv."
				+ FilenameUtils.getExtension(filetxt);

		ArffSaver saver = new ArffSaver();
		saver.setFile(new File(filewv));
		saver.setInstances(wvData);
		saver.writeBatch();
	}

	public void createCranDataset(String cranDir, String tfIdfOption)
			throws Exception {
		String collectionName = FilenameUtils.getBaseName(cranDir);
		File inDir = new File(cranDir);
		File arffText = new File("data/" + collectionName + ".arff");
		ArffSaver saver = new ArffSaver();
		saver.setFile(arffText);

		Attribute filename = new Attribute("filename", (FastVector)null);
		Attribute contents = new Attribute("contents", (FastVector)null);

		FastVector groupnv = new FastVector(1);
		groupnv.addElement(collectionName);
		Attribute group = new Attribute("class", (FastVector)null);

		FastVector attrs = new FastVector(3);
		attrs.addElement(filename);
		attrs.addElement(contents);
		attrs.addElement(group);

		Instances data = new Instances(collectionName, attrs, 0);
		data.setClass(group);

		String delimiters = " \r\n\t.,;:\'\"()[]{}?!-_";

		WordTokenizer tokenizer = new WordTokenizer();
		tokenizer.setDelimiters(delimiters);

		if(inDir.isDirectory()) {
			String files[] = inDir.list();

			for(int i = 0; i < files.length; i++) {
				File file = new File(inDir + "/" + files[i]);

				if(!file.isDirectory()) {
					BufferedReader reader = new BufferedReader(new FileReader(
							file));
					Instance inst = new Instance(3);
					inst.setValue((Attribute)attrs.elementAt(0), files[i]);

					String rawLine = "";
					String line = "";

					while((rawLine = reader.readLine()) != null) {
						rawLine = cleanLine(rawLine);
						tokenizer.tokenize(rawLine);

						while(tokenizer.hasMoreElements()) {
							String token = tokenizer.nextElement().toString();
							if(!isTag(token))
								line += token + ' ';
						}
					}
					inst.setValue((Attribute)attrs.elementAt(1), line);
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
		createWordVector(arffText.toString(), tfIdfOption);
	}
}
