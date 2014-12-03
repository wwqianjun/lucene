package qianjun.lucene_2.domain;

import java.util.List;

public class QueryResult<T> {

	private int count ; //总记录数
	
	private List<T> articleList ;//一页数据

	
	public QueryResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	

	public QueryResult(int count, List<T> articleList) {
		super();
		this.count = count;
		this.articleList = articleList;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<T> getArticleList() {
		return articleList;
	}

	public void setArticleList(List<T> articleList) {
		this.articleList = articleList;
	}
	
	
	
}
