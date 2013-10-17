package org.nyer.sns.util.page;

public class Pager<T> {
	private PagerSource<T> source;
	public Pager(PagerSource<T> source) {
		this.source = source;
	}
	
	/**
	 * 获取指定页的数据
	 * @param page 指定页，以1开始
	 * @param pageSize
	 * @return 如果没有指定页数据，返回null
	 */
	public Page<T> getPage(int page, int pageSize) {
		return this.source.getPage(page, pageSize);
	}
	
	public PageEnumeration<T> pageEnumerator(int pageSize) {
		return new PageEnumeration<T>(source, pageSize);
	}
}
