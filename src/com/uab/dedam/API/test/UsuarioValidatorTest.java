package com.uab.dedam.API.test;

import static org.junit.Assert.*;

import java.security.InvalidParameterException;

import org.junit.Test;

import com.uab.dedam.API.models.Usuario;
import com.uab.dedam.API.util.UsuarioValidator;

public class UsuarioValidatorTest {

	@Test
	public void testIsUsuarioValid() {
		Usuario usuario = new Usuario();
		try{
			usuario.setAlias("sonic");
			usuario.setName("Sonic");
			usuario.setSurname("The Hedgehodge");
			UsuarioValidator.isUsuarioValid(usuario);
			assertTrue("Validando usuario", true);
		} catch(InvalidParameterException ex) {
			fail("Failed to validate the user. Parameter: " + ex.getMessage());
		}
	}

}
