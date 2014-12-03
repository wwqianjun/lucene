package qianjun.lucene_2.utils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;

import qianjun.lucene.helloworld.domain.Article;

public class ArticleDocumentUtil {
	
	/**
	 * 将Article转化为Document
	 * 
	 * @param article
	 */
	public static Document articleToDocument(Article article) {
		Document doc = new Document();
		
		doc.add(new Field("id",article.getId(),Store.YES, Index.NOT_ANALYZED));
		doc.add(new Field("title", article.getTitle(), Store.YES, Index.ANALYZED));
		doc.add(new Field("content", article.getContent(), Store.YES, Index.ANALYZED));
		
		return doc;
	}
	
	public static Article DocumentToArticle(Document doc){
		Article article = new Article();
		
		article.setId(doc.get("id"));
		article.setTitle(doc.get("title"));
		article.setContent(doc.get("content"));
		
		return article;
	}
}
