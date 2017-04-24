package com.uab.dedam.API.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.uab.dedam.API.persistence.EnumPersistence;
import com.uab.dedam.API.persistence.IPersistenceService;
import com.uab.dedam.API.persistence.PersistenceFactory;

public class PersistenceFactoryTest {

	@Test
	public void testGetPersistenceMySQL() {
		IPersistenceService persistencia = PersistenceFactory.getPersistence(EnumPersistence.MySQLPersitence);
		assertNotNull("Persistence instance for MySQL is null.", persistencia);
		
	}

}
