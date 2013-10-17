package org.nyer.sns.util.page;


public class PageEnumeration<T> {
	private PagerSource<T> source;
	private int pageSize;
	private int currentPage;
	public PageEnumeration(PagerSource<T> source, int pageSize) {
		this.source = source;
		this.pageSize = pageSize;
		currentPage = 0;
	}
	
	/**
	 * 返回一页数据
	 * 如果返回null,表示没有下一页数据
	 * @return 下面页数据
	 */
	public Page<T> nextPage() {
		currentPage ++;
		return source.getPage(currentPage, pageSize);
	}
}
