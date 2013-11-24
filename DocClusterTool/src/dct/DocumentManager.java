package dct;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;

import weka.core.tokenizers.WordTokenizer;
import weka.core.stemmers.SnowballStemmer;

public class DocumentManager {
	public DocumentManager() {}
	
	private boolean isTag(String word) {
		if(word.charAt(0) == '<' && word.charAt(word.length() - 1) == '>')
			return true;
		return false;
	}
	
	private String cleanWord(String dirty) {
		String noise = "`~@#$%^&*=+\\|;:\'\",./";
		String clean = dirty;
		
		for(int i = 0; i < noise.length(); i++) {
			clean = clean.replace(noise.charAt(i) + "", "");
		}
		
		return clean;
	}
	
	public void readCollection(String dataDir) throws IOException {
		File dir = new File(dataDir);
		String delimiters = " \r\n\t.,;:\'\"()[]{}?!-_";
				
		WordTokenizer tokenizer = new WordTokenizer();
		SnowballStemmer stemmer = new SnowballStemmer();
		tokenizer.setDelimiters(delimiters);
		
		if(dir.isDirectory()) {
			String files[] = dir.list();
			
			for(int i = 0; i < files.length; i++) {
				File file = new File(dir + "/" + files[i]);
			
				if(!file.isDirectory()) {
					BufferedReader reader = new BufferedReader(
							new FileReader(file));
					String line;
					
					while((line = reader.readLine()) != null) {
						System.out.println(line);
						tokenizer.tokenize(line);
						
						while(tokenizer.hasMoreElements()) {
							String token = (String) tokenizer.nextElement();
							token = cleanWord(token);
							if(!isTag(token))
								System.out.println(token + " " + stemmer.stem(token.toLowerCase()));
						}
					}
					reader.close();
				}
			}
		}
		else {
			System.out.println(dataDir + " is not a directory.");
		}
	}
}
