package qianjun.lucene_2.utils;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

/**
 * 使用 static预加载 统一索引目录、分词器
 * 
 * @author QianJun
 *
 */
public class Configuration {

	private static Directory directory = null;
	private static Analyzer analyzer = null;
//	private static IndexWriter indexWriter = null;
	
	static{
		try {
			directory = FSDirectory.open(new File("./IndexDir_2/"));
			analyzer = new IKAnalyzer();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public static Analyzer getAnalyzer() {
		return analyzer;
	}

	public static void setAnalyzer(Analyzer analyzer) {
		Configuration.analyzer = analyzer;
	}

	public static void setDirectory(Directory directory) {
		Configuration.directory = directory;
	}

	public static Directory getDirectory(){
		return  directory;
	}
	
//	public static IndexWriter getIndexWriter() throws Exception{
//		return  new IndexWriter(directory, analyzer, MaxFieldLength.LIMITED);
//	}
	
}
