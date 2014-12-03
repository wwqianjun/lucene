package qianjun.lucene.helloworld;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

import qianjun.lucene.helloworld.domain.Article;

/**
 * Lucene一个简单的应用，可直接运行createIndex()、search()
 * @author QianJun
 *
 */
public class HelloWorld {

	//创建索引
	@Test
	public void createIndex() throws IOException{
		//1，模拟一条文章
		Article article = new Article();
		article.setId("3");
		article.setTitle("信息检索系统_Lucene");
		article.setContent("重复几次做实验_lucene_重复几次做实验");
		
		//2，保存到索引库
		//2.1 Article --> Document
		Document doc = new Document();
		doc.add(new Field("id",article.getId(),Store.YES,Index.NOT_ANALYZED));
		doc.add(new Field("title",article.getTitle(),Store.YES,Index.ANALYZED));
		doc.add(new Field("content",article.getContent(),Store.YES,Index.ANALYZED));
		
		//2.2 索引库中添加 Document
		//地址是什么，怎么样的方式存，一次存多少
		Directory directory = FSDirectory.open(new File("./IndexDir/")); //throw IOException
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		IndexWriter indexWriter = new IndexWriter(directory, analyzer, MaxFieldLength.LIMITED);
		
		indexWriter.addDocument(doc);
		indexWriter.close();
	}
	//搜索索引
	@Test
	public void search() throws IOException, ParseException{
		//1,搜索条件是什么
		String queryString = "钱俊";
		
		//2,从哪里搜索，搜索方式？
		Directory directory = FSDirectory.open(new File("./IndexDir/"));
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_30);
		
		//a,把搜索字符串转化为Query对象
		QueryParser queryParse = new QueryParser(Version.LUCENE_30,"title",analyzer);
		Query query = queryParse.parse(queryString);
		
		//b,搜索目录，得到中间结果
		IndexSearcher indexSearcher = new IndexSearcher(directory);
		TopDocs topDocs= indexSearcher.search(query, 100); //返回前100条记录
		
		int count = topDocs.totalHits;//搜索到的索引库中的总结果数量
		ScoreDoc [] scoreDocs = topDocs.scoreDocs; //前n条记录信息
		
		//c，处理结果
		List<Article> list = new ArrayList<Article>();
		
		for (int i = 0; i < scoreDocs.length; i++) {
			//获得一个文档的信息
			ScoreDoc scoreDoc = scoreDocs[i];
			float score = scoreDoc.score;//相关度得分
			int docId = scoreDoc.doc; //文档编号
			
			//根据文档编号取出索引库“数据部分”相应的Document
			Document doc = indexSearcher.doc(docId); 
			
			//将Document转化为Article
			Article article = new Article();
			
			article.setId(doc.get("id"));
			article.setTitle(doc.get("title"));
			article.setContent(doc.get("content"));
			
			list.add(article);
		}
		
		
		//显示结果
		for (Article article : list) {
			System.out.println("--------->Id = " + article.getId());
			System.out.println("title        = " + article.getTitle());
			System.out.println("content      = " + article.getContent());
		}
		
		indexSearcher.close();
	}
	
	
	
	
	
	
	
	
	
	
	
}
