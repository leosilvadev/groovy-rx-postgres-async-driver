package com.github.leosilvadev.groovypgasync.objects

import com.github.leosilvadev.groovypgasync.sql.annotations.Table

import java.time.LocalDateTime

@Table("Logs")
class LogWithAnnotation {
	
	Long id
	String type
	String details
	String description
	LocalDateTime registration
	Map config
	Status status = Status.ACTIVE

	enum Status {
		ACTIVE, INACTIVE
	}

}
