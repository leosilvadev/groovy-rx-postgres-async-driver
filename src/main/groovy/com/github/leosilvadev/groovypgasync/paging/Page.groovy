package com.github.leosilvadev.groovypgasync.paging

import groovy.transform.Immutable

@Immutable
class Page<T> {
	
	List<T> items
	Long totalItems
	Long itemsPerPage
	Long currentPage
	Long pages
	
}
