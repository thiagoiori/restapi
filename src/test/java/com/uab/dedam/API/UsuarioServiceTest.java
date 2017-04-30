package com.uab.dedam.API;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;

import com.uab.dedam.API.models.Grupo;
import com.uab.dedam.API.models.Usuario;
import com.uab.dedam.API.persistence.EnumPersistence;
import com.uab.dedam.API.persistence.IPersistenceService;
import com.uab.dedam.API.persistence.PersistenceFactory;

public class UsuarioServiceTest {
	
	private static IPersistenceService persistencia;

	@Test
	public void testGetUsuario() {
		try {
			if(persistencia == null)
				persistencia = PersistenceFactory.getPersistence(EnumPersistence.MySQLPersitence);
			
			Usuario usuario = new Usuario();
			usuario.setAlias("user_xpto");
			usuario.setName("John");
			usuario.setSurname("Doe");
			try {
				persistencia.saveUsuario(usuario);
			} catch (SQLException e1) {
				fail(e1.getMessage());
			}
			URL url = new URL("http://localhost:8080/usuario/" + usuario.getId());
			
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Content-Type", "application/json");

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				fail("Fail to get Usuario.\nResponse code: " + conn.getResponseCode() +
						"\nResponse Text: " + conn.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			if ((output = br.readLine()) != null) {
				Usuario usuarioGet = new ObjectMapper().readValue(output, Usuario.class);
				assertTrue(usuarioGet.getId().equals(usuario.getId()));
			}
			conn.disconnect();
		  } catch (MalformedURLException e) {
			e.printStackTrace();
		  } catch (IOException e) {

			e.printStackTrace();
		 }
	}


	@Test
	public void testInsertUsuario() {
		try {
			if(persistencia == null)
				persistencia = PersistenceFactory.getPersistence(EnumPersistence.MySQLPersitence);
			Usuario usuarioAnterior;
			try {
				usuarioAnterior = persistencia.getUsuarioByAlias("mmxolm_TesteUnitario");
				if(usuarioAnterior != null)
					persistencia.deleteUser(usuarioAnterior.getId());
			} catch (SQLException e) {
				fail(e.getMessage());
			}
			
			URL url = new URL("http://localhost:8080/usuario");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");

			String input = "{\"name\":\"NomeUsr1\", \"alias\":\"mmxolm_TesteUnitario\", \"surname\":\"McDonalds\"}";

			OutputStream os = conn.getOutputStream();
			os.write(input.getBytes());
			os.flush();

			if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
				fail("Fail to insert User.\nResponse code: " + conn.getResponseCode() +
						"\nResponse Text: " + conn.getResponseMessage());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

			String output;
			if ((output = br.readLine()) != null) {
				Usuario usuario = new ObjectMapper().readValue(output, Usuario.class);	
				assertTrue("Usuario successfully inserted", true);
				
			}
			conn.disconnect();
		  } catch (MalformedURLException e) {
			e.printStackTrace();
		  } catch (IOException e) {

			e.printStackTrace();
		 }
	}
}
