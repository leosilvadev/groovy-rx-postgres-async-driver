package com.github.leosilvadev.groovypgasync.exceptions

import groovy.transform.CompileStatic
import groovy.transform.TypeChecked

@CompileStatic
@TypeChecked
class ResultMapException extends RuntimeException {

	ResultMapException(String message){
		super(message)
	}
	
}
