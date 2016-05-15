package com.github.leosilvadev.groovypgasync

import java.sql.Time
import java.sql.Timestamp

import rx.Observable

import com.github.pgasync.ConnectionPoolBuilder
import com.github.pgasync.Db
import com.github.pgasync.ResultSet


enum PgDbType {
	
	BIG_DECIMAL('BigDecimal'),
	BIG_INTEGER('BigInteger'),
	BOOLEAN('Boolean'),
	DATE('Date'),
	DOUBLE('Double'),
	INTEGER('Int'),
	LONG('Long'),
	STRING('String'),
	TIME('Time'),
	TIMESTAMP('Timestamp')
	
	private final String value
	
	private PgDbType(String value){
		this.value = value
	}
	
	String value(){ value }
	
}
