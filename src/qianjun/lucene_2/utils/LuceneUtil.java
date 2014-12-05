package qianjun.lucene_2.utils;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
/**
 * 
 * static 全局同一个IndexWriter
 * 因为多线程不能同时操作一份文件
 * @author QianJun
 *
 */
public class LuceneUtil {
	
	private static IndexWriter indexWriter =null; 
	
	static{
		try {
			indexWriter = new IndexWriter(Configuration.getDirectory(), 
										  Configuration.getAnalyzer(), 
										  MaxFieldLength.LIMITED);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * 返回IndexWriter
	 */
	public static IndexWriter getIndexWriter() {
		return indexWriter;
	}

	/**
	 * 关闭IndexWriter
	 */
	public static void close(){
		try {
			indexWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
}
