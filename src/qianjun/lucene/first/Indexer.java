package qianjun.lucene.first;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * 
 * @author qianjun
 *
 */
public class Indexer {
	
	private IndexWriter writer;
	
	@SuppressWarnings("deprecation")
	public Indexer(String indexDir) throws IOException {
		Directory dir = FSDirectory.open(new File(indexDir));
		//构建Lucene IndexWriter
		writer = new IndexWriter(dir,
					 new StandardAnalyzer(
							 Version.LUCENE_30),
					 true,
					 IndexWriter.MaxFieldLength.UNLIMITED
					 );
	}
	
	//关闭IndexWriter
	public void close() throws IOException{
		writer.close();
	}
	
	
	private int index(String dataDir, FileFilter filter) throws IOException {
		File [] files = new File(dataDir).listFiles();
		
		for (File file : files) {
			if(!file.isDirectory() &&
			   !file.isHidden() &&
			   file.exists() &&
			   file.canRead() &&
			   (filter == null) || filter.accept(file)){
				indexFile(file);
			}//End if
		}//End for
		return writer.numDocs(); //throw  返回被索引文档数量
	}

	protected Document getDocument(File file) throws IOException{
		Document doc = new Document();
		//索引文件内容
		doc.add(new Field("contents", new FileReader(file))); //throw fileNotFind
		//索引文件名
		doc.add(new Field("filename", file.getName(),
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		//索引文件完整路径
		doc.add(new Field("fullpath", file.getCanonicalPath(),//throw IO
				Field.Store.YES, Field.Index.NOT_ANALYZED));
		return doc;
	}
	private void indexFile(File file) throws IOException {
		System.out.println("Indexing " + file.getCanonicalPath());
		Document doc = getDocument(file);
		writer.addDocument(doc);
	}

	//只索引.java文件 采用FileFilter
	private static class TextFilesFilter implements FileFilter{
		@Override
		public boolean accept(File path) {
			return path.getName().toLowerCase().endsWith(".java");
		}
	}
	
	public static void main(String[] args) throws CloneNotSupportedException, IOException {
		if( 2 != args.length ){
			throw new IllegalArgumentException("Uasge: java" + Indexer.class.getName() 
					+ " <inder dir> <data dir>" );
		}
		
		String indexDir = args[0]; //在指定目录下存储创建的索引
		String dataDir = args[1];  //对指定目录中的.java文件进行索引
		
		long startTime = System.currentTimeMillis();
		Indexer indexer = new Indexer(indexDir);
		int numIndexed ;
		try{
			numIndexed =  indexer.index(dataDir,new TextFilesFilter());
		}finally{
			indexer.close(); //throw
		}
		
		long endTime = System.currentTimeMillis();
		
		System.out.println("Indexing " + numIndexed + " file took " 
				+ (endTime - startTime) + " milliseconds");
	}//End main
}
