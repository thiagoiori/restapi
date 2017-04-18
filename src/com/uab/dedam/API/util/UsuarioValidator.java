package com.uab.dedam.API.util;

import java.security.InvalidParameterException;

import com.uab.dedam.API.models.Usuario;

public class UsuarioValidator {
	public static void isUsuarioValid(Usuario usuario) throws InvalidParameterException{
		if(usuario.getAlias() == null || usuario.getAlias().isEmpty()){
			throw new InvalidParameterException("Field 'alias' not set.");
		}
		
		if(usuario.getName() == null || usuario.getName().isEmpty()){
			throw new InvalidParameterException("Field 'name' not set.");
		}
		
		if(usuario.getSurname() == null || usuario.getSurname().isEmpty()){
			throw new InvalidParameterException("Field 'surname' not set.");
		}
	}
	
	public static boolean isProperty(String property){
		boolean isProperty = true;
		try {
			Usuario.class.getDeclaredField(property);
		} catch (NoSuchFieldException | SecurityException e) {			
			isProperty = false;
		}
		
		if(property.contains("group"));
			isProperty = true;
		
		return isProperty;
	}
}
