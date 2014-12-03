package qianjun.lucene_2.indexdao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import qianjun.lucene.helloworld.domain.Article;
import qianjun.lucene_2.domain.QueryResult;
import qianjun.lucene_2.utils.ArticleDocumentUtil;
import qianjun.lucene_2.utils.Configuration;


/**
 * 对索引库的增删查改
 * 
 * @author QianJun
 *
 */
public class ArticleIndexDao {
	
	/**
	 * 保存到索引库(第一次创建索引库)
	 * 
	 * @param article
	 */
	public void add(Article article){
		//1，将Article --->Document
		Document doc = ArticleDocumentUtil.articleToDocument(article);
		
		//2，将Document添加到索引库
		IndexWriter indexWriter = null;
		try {
			indexWriter = new IndexWriter(Configuration.getDirectory(), Configuration.getAnalyzer(), MaxFieldLength.LIMITED);
			indexWriter.addDocument(doc);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			try {
				indexWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		
	}


	
	/**
	 * 更新索引
	 * 
	 * @param article （如果article的ID索引目录中没有，效果add方法一样） 
	 */
	public void update(Article article){
		IndexWriter indexWriter = null;
		try {
			Term term = new Term("id",article.getId());
			Document doc = ArticleDocumentUtil.articleToDocument(article);
			
			indexWriter = new IndexWriter(Configuration.getDirectory(), Configuration.getAnalyzer(), MaxFieldLength.LIMITED);
			//更新就是先删除，再创建
			indexWriter.updateDocument(term, doc); 
			
			//也可以写成这样
//			indexWriter.deleteDocuments(term);
//			indexWriter.addDocument(doc);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally{
			try {
				indexWriter.close();
			}  catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException();
			}
		}
	}
	
	/**
	 * 删除索引
	 * 
	 * Term：某Field中一个关键词
	 * 
	 * @param articleId（索引目录中没有articleId，不会报错，和有一样的效果）
	 */
	public void delete(String articleId){
		IndexWriter indexWriter = null;
		try {
			Term term = new Term("id", articleId);
			
			indexWriter = new IndexWriter(Configuration.getDirectory(), Configuration.getAnalyzer(), MaxFieldLength.LIMITED);
			indexWriter.deleteDocuments(term);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}finally{
			try {
				indexWriter.close();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * 搜索
	 * 
	 * @param queryString
	 * 				搜索条件(搜索关键词)
	 * @param firstResult
	 * @param maxResult
	 */
	@SuppressWarnings("rawtypes")
	public QueryResult searcher(String queryString, int firstResult, int maxResult){
		IndexSearcher indexSearcher = null;
		try {
			//1，查询字符串转化成Query对象（这里多个Field查询）
			QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_30, 
															new String[]{ "title","content" }, 
															Configuration.getAnalyzer()
									  );
			Query query = queryParser.parse(queryString);
			
			//2，搜索得到中间结果
			indexSearcher = new IndexSearcher(Configuration.getDirectory());
			TopDocs topDocs = indexSearcher.search(query, firstResult+maxResult);//最多返回前n条记录
			int count = topDocs.totalHits;
			ScoreDoc [] scoreDocs = topDocs.scoreDocs;
			
			//3，处理结果，得到数据列表
			List<Article> articleList = new ArrayList<Article>();
			int endIndex = Math.min(firstResult+maxResult, scoreDocs.length);
			
			for (int i = 0; i < endIndex; i++) {
				//a，从所在的目录取Document的编号  
				int docId = scoreDocs[i].doc;	
				
				//b，从存储的真正的数据中 根据编号取出Document
				Document doc = indexSearcher.doc(docId);
				
				//c，将Document转化为Article
				Article article = ArticleDocumentUtil.DocumentToArticle(doc);
				
				//d，添加到数据列表
				articleList.add(article);
			}
			
			//4，封装结果并返回
			return new QueryResult<Article>(count, articleList);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally{
			try {
				indexSearcher.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
