package qianjun.lucene.first;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

/**
 * 供命令行运行方式的搜索Lucene索引功能,与Indexer相辅相成[]
 * 命令行参数
 * 	由Indexer创建的索引文件路径
 * 	用于搜索索引的查询
 * @author QianJun
 *
 */
public class Searcher {

	public static void main(String[] args) throws IOException, ParseException {
		if(2 != args.length ){
			throw new IllegalArgumentException("Usage: java "+
					Searcher.class.getName()+
					" <index dir> <query>");
		}
		//已创建索引的 路径
		String indexDir = args[0];
		//要解析的查询字符串
		String q = args[1];
		
		search(indexDir,q);
	}
	
	@SuppressWarnings("deprecation")
	public static void search(String indexDir, String q) throws IOException, ParseException{
		//从存放索引的位置打开索引文件
		Directory dir = FSDirectory.open(new File(indexDir));
		IndexSearcher is = new IndexSearcher(dir);
		
		//解析查询字符串
		QueryParser parser = new QueryParser(Version.LUCENE_30,
								"contents",
								new StandardAnalyzer(Version.LUCENE_30)
							);
		Query query = parser.parse(q); //throw parser
		long startTime = System.currentTimeMillis();
		//搜索索引,并返回搜索结果集，此处搜索过程没有立即加载匹配的文档，TopDocs是文档引用
		TopDocs hits = is.search(query, 10);
		long endTime = System.currentTimeMillis();
		
		//记录搜索状态
		System.err.println("Found "+ hits.totalHits + 
				" document(s) (in " + (endTime- startTime)+
				" milliseconds) that matched query '"+
				q + "':");
		
		for(ScoreDoc scoreDoc : hits.scoreDocs){
			Document doc = is.doc(scoreDoc.doc); //scoreDoc.doc 文档编号，文档在此才本加载，返回匹配文本
			System.out.println(doc.get("fullpath"));//显示匹配文件名
		}
		
		is.close();//关闭IndexSearcher
	}

}
