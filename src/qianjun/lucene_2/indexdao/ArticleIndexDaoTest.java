package qianjun.lucene_2.indexdao;

import static org.junit.Assert.fail;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import qianjun.lucene.helloworld.domain.Article;
import qianjun.lucene_2.domain.QueryResult;
import qianjun.lucene_2.utils.LuceneUtil;

public class ArticleIndexDaoTest {

	private ArticleIndexDao articleIndexDao = new ArticleIndexDao();
	@Test
	public void test() {
		fail("Not yet implemented");
	}
	
	@After
	public void shuoDown(){
		LuceneUtil.close();
		System.out.println("----------------------------------->End Test-----------------------------------------");
	}
	
	@Before
	public void start(){
		System.out.println("----------------------------------->Start Test-----------------------------------------");
	}
	
	@Test
	public void testAdd_1(){
		Article article = new Article();
		
		article.setId("22");
		article.setTitle("钱俊第二版Luncene_1哈哈");
		article.setContent("XXX安徽省安庆市区");
		
		articleIndexDao.add(article);
	}
	
	@Test
	public void testAdd_20(){
		for (int i = 2; i < 22; i++) {
			Article article = new Article();
			
			article.setId(String.valueOf(i));
			article.setTitle(i + "钱俊第二版Luncene_1");
			article.setContent(i + "XXX安徽省上海市");
			
			articleIndexDao.add(article);
		}
	}
	@SuppressWarnings("unchecked")
	@Test
	public void testSearcher(){
//		String queryString ="哈哈";
		String queryString = "oyxm";
//		String queryString = "Oyxm";
		int firstResult = 0;
		int maxResult = 21;
		QueryResult<Article> queryResult = articleIndexDao.searcher(queryString, firstResult, maxResult);
		
		//显示结果
		System.out.println("符合条件的总结果数是: " + queryResult.getCount());
		List<Article> articleList = queryResult.getArticleList();
		
		for (Article article : articleList) {
			System.out.println("---------->id = "+ article.getId());
			System.out.println("title         = " + article.getTitle());
			System.out.println("content       = " + article.getContent());
		}
		
	}
	
	@Test
	public void testDelete(){
		articleIndexDao.delete("22");
	}
	
	@Test
	public void testUpdate(){
		Article article = new Article();
		
		article.setId("22");
		article.setTitle("钱俊第二版Luncene_1哈哈，已经不是原来的第一个le");
		article.setContent("XXX安徽省安庆市区");
		
		articleIndexDao.add(article);
		articleIndexDao.update(article);
	}

}
