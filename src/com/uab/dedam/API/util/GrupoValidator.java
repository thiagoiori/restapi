package com.uab.dedam.API.util;

import java.security.InvalidParameterException;

import com.uab.dedam.API.models.Grupo;
import com.uab.dedam.API.models.Usuario;

public class GrupoValidator {
	public static void isGrupoValid(Grupo grupo) throws InvalidParameterException{
		if(grupo.getSelf() == null || grupo.getSelf().isEmpty()){
			throw new InvalidParameterException("Field 'self' not set.");
		}
		
		if(grupo.getName() == null || grupo.getName().isEmpty()){
			throw new InvalidParameterException("Field 'name' not set.");
		}
	}
	
	public static boolean isProperty(String property){
		boolean isProperty = true;
		try {
			Grupo.class.getDeclaredField(property);
		} catch (NoSuchFieldException | SecurityException e) {			
			isProperty = false;
		}
		
		return isProperty;
	}
}
