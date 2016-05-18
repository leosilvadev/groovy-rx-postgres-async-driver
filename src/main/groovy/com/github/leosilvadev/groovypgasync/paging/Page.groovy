package com.github.leosilvadev.groovypgasync.paging

import groovy.transform.Immutable

@Immutable
class Page {
	
	List items
	Long totalItems
	Long itemsPerPage
	Long currentPage
	Long pages
	
}
