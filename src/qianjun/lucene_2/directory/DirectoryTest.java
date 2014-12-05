package qianjun.lucene_2.directory;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Test;

import qianjun.lucene.helloworld.domain.Article;
import qianjun.lucene_2.indexdao.ArticleIndexDao;
import qianjun.lucene_2.utils.ArticleDocumentUtil;
import qianjun.lucene_2.utils.Configuration;
import qianjun.lucene_2.utils.LuceneUtil;
import sun.security.krb5.Config;

/**
 * 索引库的优化操作
 * 
 * @author QianJun
 *
 */
public class DirectoryTest {

	/**
	 * 优化索引库（合并文件，提高I/O速度）
	 * @throws Exception
	 */
	@Test
	public void testOptimize() throws Exception{
		LuceneUtil.getIndexWriter().optimize();//合并多个小文件为一个大文件
		LuceneUtil.close();
		
	}
	
	@Test
	public void testOptimize_AutoSize(){
		//文件数多达小个数量之后自动合并为一个大文件,默认为10
		LuceneUtil.getIndexWriter().setMergeFactor(3);
		Article article = new Article();
		
		article.setId("oyxm22");
		article.setTitle("oyxm");
		article.setContent("钱俊正在实习ing");
		
		ArticleIndexDao articleIndexDao = new ArticleIndexDao();
		articleIndexDao.add(article);
		LuceneUtil.close();
	}
	/**
	 * 用时从文件系统加载到内存，最后在保存到文件系统中
	 * 
	 * @throws Exception
	 */
	@Test
	public void testＤirectory() throws Exception{
//一：模拟启动应用程序时加载索引库数据到内存中（在构造方法中传递另一个Directory）
		//文件系统中真实的文件或文件夹,相对较慢,可常时间存储
		Directory fsDir = FSDirectory.open(new File("./IndexDir_2"));
		//内存中模拟的文件夹与文件,速度相对快,程序结束就没了,带参数就是从文件系统中加载到内存中
		Directory ramDir = new RAMDirectory(Configuration.getDirectory());
		
		Article article = new Article();
		
		article.setId(String.valueOf(System.currentTimeMillis()));
		article.setTitle("oyxm");
		article.setContent("钱俊正在实习ing");
	/*
		ArticleIndexDao articleIndexDao = new ArticleIndexDao();
		articleIndexDao.add(article);
	*/
		//保存到索引库
		Document doc = ArticleDocumentUtil.articleToDocument(article);
		IndexWriter ramIndexWriter = new IndexWriter(ramDir,Configuration.getAnalyzer(),MaxFieldLength.LIMITED);
		ramIndexWriter.addDocument(doc);
		ramIndexWriter.close();

//二：模拟退出应用程序退出前保存索引库数据到文件系统中
		//IndexWriter fsIndexWriter = new IndexWriter(fsDir,Configuration.getAnalyzer(),MaxFieldLength.LIMITED);
		/****************************************************************************/
		//相对于上面的构造方法使用新的构造方法
		//没有新增的boolean型参数构造方法，则：如果索引库不存在，就创建；如果存在，就追加
		//第三个参数作用是：true创建或覆盖已经存在的索引库；false追加已经存在的索引库（如果索引库不存在，报错）
		IndexWriter fsIndexWriter = new IndexWriter(fsDir,Configuration.getAnalyzer(),true,MaxFieldLength.LIMITED);
		/****************************************************************************/
		//将内存中的索引库
		fsIndexWriter.addIndexesNoOptimize(ramDir);
		fsIndexWriter.close();
	}
}
