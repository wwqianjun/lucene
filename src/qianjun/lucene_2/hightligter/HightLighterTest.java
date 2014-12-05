package qianjun.lucene_2.hightligter;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;
import org.junit.Test;

import qianjun.lucene.helloworld.domain.Article;
import qianjun.lucene_2.utils.ArticleDocumentUtil;
import qianjun.lucene_2.utils.Configuration;

public class HightLighterTest {
	
	/**
	 * 代码从HelloWorld中的Searcher拷贝后修改
	 * @throws Exception 
	 */
	@Test
	public void testHightLighter() throws Exception{
		//1,搜索条件是什么
//		String queryString = "安徽";
		String queryString = "20";

		
		//a,把搜索字符串转化为Query对象
		QueryParser queryParse = new MultiFieldQueryParser(Version.LUCENE_30,new String[]{"title","content"},Configuration.getAnalyzer());
		Query query = queryParse.parse(queryString);
		
		//b,搜索目录，得到中间结果
		IndexSearcher indexSearcher = new IndexSearcher(Configuration.getDirectory());
		TopDocs topDocs= indexSearcher.search(query, 100); //返回前100条记录
		
		int count = topDocs.totalHits;//搜索到的索引库中的总结果数量
		ScoreDoc [] scoreDocs = topDocs.scoreDocs; //前n条记录信息
		
		//=======================创建并配置高亮器=====================
		//	配置：摘要大小（字符数）、显示效果（前缀、后缀）
		//	使用：要指定：查询条件（关键字）
		
		//显示效果 
		Formatter formatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
		//查询条件
		Scorer fragmentScorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, fragmentScorer);
		//摘要大小（具体根据前端样式大小200-300）,默认100，
		highlighter.setTextFragmenter(new SimpleFragmenter(10));
		//=========================================================
		
		
		//c，处理结果[**]
		List<Article> list = new ArrayList<Article>();
		
		for (int i = 0; i < scoreDocs.length; i++) {
			ScoreDoc scoreDoc = scoreDocs[i];//获得一个文档的信息
			
			int docId = scoreDoc.doc; //文档编号
			
			Document doc = indexSearcher.doc(docId); //根据文档编号取出索引库“数据部分”相应的Document
			
			//=========================使用高亮器并进行高亮操作==============
			//	返回高亮后的一段文本,没有关键词返回null
			String text = highlighter.getBestFragment(Configuration.getAnalyzer(), "title", doc.get("title"));
			//使用高亮后的文本内容
			if(null != text)
				doc.getField("title").setValue(text);
			
			String content = highlighter.getBestFragment(Configuration.getAnalyzer(), "content", doc.get("content"));
			//使用高亮后的文本内容
			if(null != content)
				doc.getField("content").setValue(content);
			//=========================================================
			list.add(ArticleDocumentUtil.DocumentToArticle(doc));//将Document转化为Article
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
