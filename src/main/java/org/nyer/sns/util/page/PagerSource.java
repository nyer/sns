package org.nyer.sns.util.page;

public interface PagerSource<T> {
	/**
	 * 获取指定一页
	 * @param page
	 * @param pageSize
	 * @return 如果没有，返回null
	 */
	Page<T> getPage(int page, int pageSize);
}
