package com.uab.dedam.API;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;

import javax.xml.ws.WebServiceRef;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.sun.xml.bind.api.TypeReference;
import com.uab.dedam.API.models.Grupo;
import com.uab.dedam.API.persistence.EnumPersistence;
import com.uab.dedam.API.persistence.IPersistenceService;
import com.uab.dedam.API.persistence.PersistenceFactory;

public class GrupoServiceTest {
	
	private static IPersistenceService persistencia;
	
	@Before
	public void setUp() throws Exception {
		if(persistencia == null)
			persistencia = PersistenceFactory.getPersistence(EnumPersistence.MySQLPersitence);
		
		Grupo grupoUnitTest = persistencia.getGrupo("Grupo Unit Test");
		if(grupoUnitTest != null)
			persistencia.deleteGroup(grupoUnitTest.getId());
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testInsertGrupo() {
		
		try {
			URL url = new URL("http://localhost:8080/grupo");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			String input = "{\"name\":\"Grupo Unit Test\"}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				fail("Fail to insert Grupo.\nResponse code: " + conn.getResponseCode() +
						"\nResponse Text: " + conn.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			if ((output = br.readLine()) != null) {
				Grupo grupo = new ObjectMapper().readValue(output, Grupo.class);
				try {
					Grupo grupoInserido = persistencia.getGrupo("Grupo Unit Test");
					assertTrue(grupo.getId().equals(grupoInserido.getId()));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			conn.disconnect();
		  } catch (MalformedURLException e) {
			e.printStackTrace();
		  } catch (IOException e) {

			e.printStackTrace();
		 }
	}
	
	@Test
	public void testGetGrupos() {
		try {
			persistencia.saveGrupo(new Grupo(0,"Grupo Unit Test", ""));
			URL url = new URL("http://localhost:8080/grupo");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				fail("Fail to get Groups.\nResponse code: " + conn.getResponseCode() +
						"\nResponse Text: " + conn.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			if ((output = br.readLine()) != null) {
				ObjectMapper mapper = new ObjectMapper();
				Grupo[] grupos = mapper.readValue(output, Grupo[].class);
				assertTrue("Groups returned", grupos.length > 0);
			}
			conn.disconnect();
		  } catch (MalformedURLException e) {
			e.printStackTrace();
		  } catch (IOException e) {

			e.printStackTrace();
		 } catch (SQLException e) {
			fail("SQL Error: " + e.getMessage());
		}
	}

	@Test
	public void testGetGruposString() {
		try {
			Grupo grupoTeste = new Grupo(0,"Grupo Unit Test", "");
			persistencia.saveGrupo(grupoTeste);
			URL url = new URL("http://localhost:8080/grupo/" + grupoTeste.getId());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				fail("Fail to get Groups.\nResponse code: " + conn.getResponseCode() +
						"\nResponse Text: " + conn.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			if ((output = br.readLine()) != null) {
				ObjectMapper mapper = new ObjectMapper();
				Grupo grupo = mapper.readValue(output, Grupo.class);
				assertTrue("Group returned", grupo.getId().equals(grupoTeste.getId()));
			}
			conn.disconnect();
		  } catch (MalformedURLException e) {
			e.printStackTrace();
		  } catch (IOException e) {

			e.printStackTrace();
		 } catch (SQLException e) {
			fail("SQL Error: " + e.getMessage());
		}
	}
}
