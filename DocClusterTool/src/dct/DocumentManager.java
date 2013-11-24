package dct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.tokenizers.WordTokenizer;

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

    public void createCranDataset(String dataDir) throws IOException {
        File inDir = new File(dataDir);
        ArffSaver saver = new ArffSaver();
        saver.setFile(new File(dataDir + "/arff/cranfield.arff"));

        Attribute filename = new Attribute("filename");
        Attribute contents = new Attribute("contents");

        FastVector groupnv = new FastVector(1);
        groupnv.addElement("cranfield");
        Attribute group = new Attribute("class", groupnv);

        FastVector attrs = new FastVector(3);
        attrs.addElement(filename);
        attrs.addElement(contents);
        attrs.addElement(group);

        Instances dataset = new Instances("cranfield", attrs, 0);

        String delimiters = " \r\n\t.,;:\'\"()[]{}?!-_";

        WordTokenizer tokenizer = new WordTokenizer();
        tokenizer.setDelimiters(delimiters);

        if(inDir.isDirectory()) {
            String files[] = inDir.list();

            for(int i = 0; i < files.length; i++) {
                File file = new File(inDir + "/" + files[i]);
                System.out.println(files[i]);

                if(!file.isDirectory()) {
                    BufferedReader reader = new BufferedReader(new FileReader(
                            file));
                    Instance inst = new Instance(2);
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
                    inst.setValue((Attribute)attrs.elementAt(2), "cranfield");
                    dataset.add(inst);
                    reader.close();
                }
            }
            saver.writeBatch();
        }
        else {
            System.out.println(dataDir + " is not a directory.");
        }
    }

    public static void main(String[] args) throws IOException {
        String cranDir = "/home/marc/data/cranfield";
        String news20Dir = "/home/marc/data/20news-18828";
        String reutersDir = "/home/marc/data/reuters21578";

        DocumentManager docMan = new DocumentManager();
        docMan.createCranDataset(cranDir);
    }
}
