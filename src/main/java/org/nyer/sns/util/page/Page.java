package org.nyer.sns.util.page;

import java.util.List;


public class Page<T> {
	private int pageSize;
	private int curPage;
	private List<T> content;
	private int nextCursor = -1;
	private int prevCursor = -1;
	
	public Page(int curPage, int pageSize) {
		this.curPage = curPage;
		this.pageSize = pageSize;
	}
	
	public int getPageSize() {
		return pageSize;
	}
	public int getCurPage() {
		return curPage;
	}
	
	public void setContent(List<T> content) {
		this.content = content;
	}
	
	public List<T> getContent() {
		return content;
	}
	
	public int getNextCursor() {
		return nextCursor;
	}
	public int getPrevCursor() {
		return prevCursor;
	}

	public void setNextCursor(int nextCursor) {
		this.nextCursor = nextCursor;
	}

	public void setPrevCursor(int prevCursor) {
		this.prevCursor = prevCursor;
	}

	@Override
	public String toString() {
		return "Page [curPageSize=" + this.content.size() + ", pageSize=" + pageSize + ", curPage=" + curPage
				+ ", content=" + content + ", nextCursor=" + nextCursor
				+ ", prevCursor=" + prevCursor + "]";
	}
}
