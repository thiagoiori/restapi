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
			UsuarioValidator.isUsuarioValid(usuario);
		} catch(InvalidParameterException ex) {
			assertTrue("Usuario invalido", true);
		}
	}

}
