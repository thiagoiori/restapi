package com.uab.dedam.API.persistence;

public class PersistenceFactory {

	public static IPersistenceService getPersistence(EnumPersistence persistence){
		
		switch(persistence){
		case MySQLPersitence:
			return new MySQLPersistence();
		default:
			return null;
		}
	}
}
