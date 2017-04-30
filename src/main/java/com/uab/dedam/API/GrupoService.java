package com.uab.dedam.API;

import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.uab.dedam.API.models.*;
import com.uab.dedam.API.persistence.EnumPersistence;
import com.uab.dedam.API.persistence.IPersistenceService;
import com.uab.dedam.API.persistence.PersistenceFactory;
import com.uab.dedam.API.util.GrupoValidator;

@Path("/grupo")
public class GrupoService {
	
	private static IPersistenceService persistencia;
	
	public GrupoService(){
		if(persistencia == null)
			persistencia = PersistenceFactory.getPersistence(EnumPersistence.MySQLPersitence);
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGrupos() {
		List<Grupo> listaGrupos = new ArrayList<Grupo>();
		try {
			listaGrupos = persistencia.getGrupos();
		} catch(Exception ex) {
			System.out.print("Erro SQL " + ex.getMessage());
		}
		
		GenericEntity<List<Grupo>> list = new GenericEntity<List<Grupo>>(listaGrupos){
			
		};
		return Response.ok(list).build();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGrupos(
			@PathParam("id") String id) {
		Grupo grupo = new Grupo();
		try {
			grupo = persistencia.getGrupo(Integer.parseInt(id));
		} catch(Exception ex) {
			String responseText = "{\"error\":\"" + ex.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		
		return Response.ok(grupo).build();
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertGrupo(Grupo grupo){
		
		try{
			GrupoValidator.isGrupoValid(grupo);
		}
		catch(InvalidParameterException argEx){
			String responseText = "{\"error\":\"" + argEx.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		
		try {
			Grupo grupoFound = persistencia.getGrupo(grupo.getName());
			if(grupoFound != null){
				String responseText = "{\"error\":\"Grupo already exist.\"}";
				return Response.status(400).entity(responseText).build();
			} 
		} catch (SQLException e) {
			String responseText = "{\"error\":\"" + e.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		
		try{
			persistencia.saveGrupo(grupo);
		}
		catch(SQLException argEx){
			String responseText = "{\"error\":\"" + argEx.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		
		return Response.status(201).entity("{\"id\":" + grupo.getId() + "}").build();
	}
}
