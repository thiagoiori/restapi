package com.uab.dedam.API;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;



import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.impl.SimpleBeanPropertyFilter;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import com.uab.dedam.API.models.*;
import com.uab.dedam.API.persistence.EnumPersistence;
import com.uab.dedam.API.persistence.IPersistenceService;
import com.uab.dedam.API.persistence.PersistenceFactory;
import com.uab.dedam.API.util.UsuarioValidator;

@Path("/usuario")
public class UsuarioService {
	
	private static IPersistenceService persistencia;
	
	public UsuarioService(){
		if(persistencia == null)
			persistencia = PersistenceFactory.getPersistence(EnumPersistence.MySQLPersitence);
	}

	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUsuarios() {
		List<Usuario> listaUsuarios = new ArrayList<Usuario>();
		try {
			listaUsuarios = persistencia.getUsuarios(0);
		} catch(Exception ex) {
			String responseText = "{\"error\":\"" + ex.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		
		GenericEntity<List<Usuario>> list = new GenericEntity<List<Usuario>>(listaUsuarios){
			
		};
		return Response.ok(list).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}")
	public Response getUsuario(@PathParam("id") String id) {
		Integer idUsuario = Integer.parseInt(id);
		Usuario usuario = new Usuario();
		try {
			usuario = persistencia.getUsuarios(idUsuario).get(0);
		} catch(Exception ex) {
			System.out.print("Erro SQL " + ex.getMessage());
		}
			
		return Response.ok(usuario).build();
	}
	
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/{propriedade}")
	public Response getUsuario(
			@PathParam("id") String id,
			@PathParam("propriedade") String propriedade) {
		if(!UsuarioValidator.isProperty(propriedade)){
			return Response.status(404).build();
		}
		String jsonReturn = "{}";
		Set<String> propertiesSet = new HashSet<String>();
		propertiesSet.add("self");
		propertiesSet.add(propriedade);
		ObjectMapper mapper = new ObjectMapper();
		FilterProvider provider = new SimpleFilterProvider().addFilter("anyFilter", SimpleBeanPropertyFilter.filterOutAllExcept(propertiesSet));
		mapper.filteredWriter(provider);
		Integer idUsuario = Integer.parseInt(id);		
		UsuarioFiltered usuario = new UsuarioFiltered();

		usuario = persistencia.getUsuarioFiltered(idUsuario).get(0);
		usuario.setSelf(usuario.getSelf() + "/" + propriedade);

		try {
			jsonReturn = mapper.filteredWriter(provider).writeValueAsString(usuario);
		} catch (IOException e) {
			String responseText = "{\"error\":\"" + e.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		
		return Response.ok(jsonReturn).build();
	}
	
	@PUT
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/photo")
	public Response updatePhotoUsuario(
			@PathParam("id") String id,
			@FormDataParam("meta") String jsonMeta,
			@FormDataParam("photo") InputStream file,
			@FormDataParam("photo") FormDataContentDisposition fileDetail
			) {
		
		Integer read = 0;
		byte[] data = new byte[1024];
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			while((read = file.read(data)) != -1){
				baos.write(data, 0, read);
			}
		} catch (IOException e1) {
			String errorMessage = "Error: " + e1.getMessage();
			return Response.status(500).entity(errorMessage).build();
		}
		
		byte[] encoded = Base64.encodeBase64(baos.toByteArray());
		String encodedString = new String(encoded);
		Integer idUsuario = Integer.parseInt(id);
		Usuario usuario = new Usuario();
		usuario.setId(idUsuario);
		usuario.setPhoto(encodedString);
		try {
			persistencia.updateUser(usuario, "photo");
		} catch (SQLException e) {
			String errorMessage = "Error: " + e.getMessage();
			return Response.status(500).entity(errorMessage).build();
		}
        return Response.status(200).build();
	}
	
	@POST
	@Path("/")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response insertUsuario(
			Usuario usuario){
		
		try{
			UsuarioValidator.isUsuarioValid(usuario);
		}
		catch(InvalidParameterException argEx){
			String responseText = "{\"error\":\"" + argEx.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		
		try {
			Grupo grupo = null;
			if(usuario.getGrupo() != null && !usuario.getGrupo().getName().isEmpty()){
				grupo = persistencia.getGrupo(usuario.getGrupo().getName());
				if(grupo != null){
					GrupoUsuario usuarioGrupo = new GrupoUsuario(grupo.getId(),
							grupo.getName(),
							grupo.getSelf());
					usuario.setGrupo(usuarioGrupo);
				} else {
					String responseText = "{\"error\":\"Group does not exist.\"}";
					return Response.status(400).entity(responseText).build();
				}
			}
			
			if(persistencia.getUsuarioByAlias(usuario.getAlias()) != null){
				String responseText = "{\"error\":\"Field 'Alias' already in use.\"}";
				return Response.status(400).entity(responseText).build();
			}
			
		} catch (SQLException e) {
			String responseText = "{\"error\":\"" + e.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		
		
		
		try{
			usuario.setSelf("");
			persistencia.saveUsuario(usuario);
		}
		catch(SQLException argEx){
			String responseText = "{\"error\":\"" + argEx.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		
		return Response.status(201).entity("{\"id\":" + usuario.getId() + "}").build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/{propriedade}")
	public Response updateUsuario(
			@PathParam("id") String usuarioId,
			@PathParam("propriedade") String propriedade,
			Usuario usuario){
		usuario.setId(Integer.parseInt(usuarioId));
		try {
			persistencia.updateUser(usuario, propriedade);
		} catch (SQLException e) {
			String responseText = "{\"error\":\"" + e.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		return Response.status(201).build();
	}
	
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/group")
	public Response updateUsuarioGrupo(
			@PathParam("id") String usuarioId,
			@PathParam("propriedade") String propriedade,
			Usuario usuario){
		Grupo grupo = new Grupo();
		try {
			grupo = persistencia.getGrupo(usuario.getGrupo().getName());
			if(grupo == null)
				return Response.status(400).build();
		} catch (SQLException e1) {
			String responseText = "{\"error\":\"" + e1.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		GrupoUsuario usuarioGrupo = new GrupoUsuario(grupo.getId(),
				grupo.getName(),
				grupo.getSelf());
		usuario.setGrupo(usuarioGrupo);
		usuario.setId(Integer.parseInt(usuarioId));
		try {
			persistencia.updateUser(usuario, "group");
		} catch (SQLException e) {
			String responseText = "{\"error\":\"" + e.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		return Response.status(201).build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("usuario/{id}")
	public Response deleteUsuario(@PathParam("id") String usuarioId){

		try {
			persistencia.deleteUser(Integer.parseInt(usuarioId));
		} catch (SQLException e) {
			String responseText = "{\"error\":\"" + e.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		return Response.status(201).build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/{propriedade}")
	public Response updateUsuarioProperty(
			@PathParam("id") String usuarioId,
			@PathParam("propriedade") String propriedade){
		Usuario usuario = new Usuario();
		usuario.setId(Integer.parseInt(usuarioId));
		try {
			persistencia.deleteUserProperty(usuario, propriedade);
		} catch (SQLException e) {
			String responseText = "{\"error\":\"" + e.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		return Response.status(201).build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/group")
	public Response updateUsuarioGroup(
			@PathParam("id") String usuarioId){
		Usuario usuario = new Usuario();
		usuario.setId(Integer.parseInt(usuarioId));
		try {
			persistencia.deleteUserProperty(usuario, "group");
		} catch (SQLException e) {
			String responseText = "{\"error\":\"" + e.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		return Response.status(201).build();
	}
	
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/{id}/photo")
	public Response updateUsuarioPhoto(
			@PathParam("id") String usuarioId){
		Usuario usuario = new Usuario();
		usuario.setId(Integer.parseInt(usuarioId));
		try {
			persistencia.deleteUserProperty(usuario, "photo");
		} catch (SQLException e) {
			String responseText = "{\"error\":\"" + e.getMessage() + "\"}";
			return Response.status(400).entity(responseText).build();
		}
		return Response.status(201).build();
	}
}
