package qianjun.lucene.crud;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;


public class IndexingTest extends TestCase{
	protected String[] ids = {"1", "2"};
	protected String[] unindexed = {"Netherlands", "Italy"};
	protected String[] unstored = {"Amsterdam has lots of bridges",
									"Venice has lots of canals"
								   };
	protected String[] text = {"Amsterdam", "Venice"};
	
	private Directory directory;

	//1.每次测试前运行：
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		directory = new RAMDirectory();
		
		//2.创建IndexWriter对象
		IndexWriter writer = getWriter();
		
		//3.添加文档
		for(int i = 0; i < ids.length; i++){
			Document doc = new Document();
			doc.add(new Field("id", ids[i],
					Field.Store.YES,
					Field.Index.NOT_ANALYZED)
					);
			doc.add(new Field("country", unindexed[i],
					Field.Store.YES,
					Field.Index.NO)
					);
			doc.add(new Field("contents",unstored[i],
					Field.Store.NO,
					Field.Index.ANALYZED)
					);
			doc.add(new Field("city",text[i],
					Field.Store.YES,
					Field.Index.ANALYZED)
					);
			writer.addDocument(doc);
		}
		
		writer.close();
	}
	/**
	 * 向新建的索引中添加文档
	 * @author QianJun
	 * 2014-10-17 11：00
	 */
	//2.创建IndexWriter对象
	private IndexWriter getWriter() throws CorruptIndexException, LockObtainFailedException, IOException {
		
		return new IndexWriter(directory,new WhitespaceAnalyzer(),
								IndexWriter.MaxFieldLength.UNLIMITED);
	}
	
	protected int getHitCount(String fieldName,String searchString) throws CorruptIndexException, IOException{
		//4.创建新的IndexSearcher对象
		IndexSearcher searcher = new IndexSearcher(directory);
		//5.建立简单的单Term查询
		Term term = new Term(fieldName,searchString);
		Query query = new TermQuery(term);
		//6.获取命中数
		int hitCount = TestUtil.hitCount(searcher,query);
		searcher.close();
		return hitCount;
		
	}
	
	//核对写入的文档数
	public void testIndexWriter() throws CorruptIndexException, LockObtainFailedException, IOException{
		IndexWriter writer = getWriter();
		assertEquals(ids.length,writer.numDocs());
		writer.close();		
	}
	
	//核对读入的文档数
	public void testIndexReader() throws IOException{
		IndexReader reader = IndexReader.open(directory);
		assertEquals(ids.length,reader.maxDoc());
		assertEquals(ids.length,reader.numDocs());
		reader.close();
	}
	
	//======================================
	//	从索引中删除文档
	//	在所有的情况下，删除操作不会马山执行[commit/close,存储文档的磁盘空间爱你不会马上释放，只是将该文档标记为删除]，
	//	而是放入内存缓冲区，最后Lucene会周期性刷新文档来执行操作
	//	
	//======================================
	
	public void testDeleteBeforeOptimize() throws IOException{
		IndexWriter writer = getWriter();
		//确认索引中的两个文档
		assertEquals(2,writer.numDocs());
		//删除第一个文档
		writer.deleteDocuments(new Term("id","1"));
		//在不关闭writer的情况下提交更改，保留writer至下一次提交时刻
		writer.commit();
		
		//确认被标记为删除的文档
		assertTrue(writer.hasDeletions());
		assertEquals(2,writer.maxDoc());  //包含删除的索引文档数	
		assertEquals(1,writer.numDocs()); //不包含删除的索引文档数
		
		writer.close();
	}
	
	//========================
	//	通过索引月优化强制Lucene在删除一个文档后合并索引段
	//	删除和优化操作完成后。lucene实际上已经将该文档删除
	//========================
	@SuppressWarnings("deprecation")
	public void testDeleteAfterOptimize() throws IOException{
		IndexWriter writer = getWriter();
		//确认索引中的两个文档
		assertEquals(2,writer.numDocs());
		//删除第一个文档
		writer.deleteDocuments(new Term("id","1"));
		writer.optimize();
		
		//在不关闭writer的情况下提交更改，保留writer至下一次提交时刻
		writer.commit();
		
		//确认被标记为删除的文档
		assertFalse(writer.hasDeletions());
		assertEquals(1,writer.maxDoc());  //包含删除的索引文档数	
		assertEquals(1,writer.numDocs()); //不包含删除的索引文档数
		
		writer.close();
	}
}
