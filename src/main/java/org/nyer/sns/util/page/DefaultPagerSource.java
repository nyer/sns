package org.nyer.sns.util.page;

import java.util.ArrayList;
import java.util.List;

public class DefaultPagerSource<T> implements PagerSource<T> {
	private List<T> data;
	public DefaultPagerSource(List<T> data) {
		this.data = data;
	}
	
	@Override
	public Page<T> getPage(int page, int pageSize) {
		Page<T> _page = null;
		if (page > 0 && pageSize > 0) {
			int offset = (page - 1) * pageSize;
			int limit = offset + pageSize;
			if (offset < data.size()) {
				_page = new Page<T>(page, pageSize);
				if (limit > data.size()) {
					limit = data.size();
				}
				
			    _page.setContent(data.subList(offset, limit));
			}
		}
		
		return _page;
	}

	public static void main(String[] args) {
		List<String> data = new ArrayList<String>();
		for (int i = 1; i <= 32;i ++)
			data.add("" + i);
		
		PagerSource<String> source = new DefaultPagerSource<String>(data);
		Pager<String> pager = new Pager<String>(source);
		
		PageEnumeration<String> pageEnumerator = pager.pageEnumerator(5);
		Page<String> page = null;
		while ((page = pageEnumerator.nextPage()) != null) {
			System.out.println(page);
		}
	}
}
