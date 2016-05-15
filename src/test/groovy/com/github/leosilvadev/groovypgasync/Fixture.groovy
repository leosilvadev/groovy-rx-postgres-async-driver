package com.github.leosilvadev.groovypgasync

class Fixture {

	static final String CREATE_LOGS_TABLE = '''
		CREATE TABLE IF NOT EXISTS Logs (
		   id SERIAL NOT NULL, 
		   type CHARACTER VARYING(50) NOT NULL, 
		   description TEXT NOT NULL, 
		   details TEXT, 
		   registration TIMESTAMP NOT NULL,
		   CONSTRAINT "pk-logs" PRIMARY KEY (id)
		)
	'''
	
	static final String DELETE_ALL_LOGS = '''
		DELETE FROM Logs
	'''
	
}
