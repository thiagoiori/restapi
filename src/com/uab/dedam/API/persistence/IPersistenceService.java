package com.uab.dedam.API.persistence;

import java.sql.SQLException;
import java.util.List;

import com.uab.dedam.API.models.Grupo;
import com.uab.dedam.API.models.Usuario;
import com.uab.dedam.API.models.UsuarioFiltered;

public interface IPersistenceService {
	
	void saveUsuario(Usuario usuario) throws SQLException;
	Grupo getGrupo(String groupName) throws SQLException;
	Grupo getGrupo(Integer idGroup) throws SQLException;
	void saveGrupo(Grupo grupo) throws SQLException;
	List<Usuario> getUsuarios(Integer idUsuario) throws SQLException;
	void updateUser(Usuario usuario, String propriedade) throws SQLException;
	List<UsuarioFiltered> getUsuarioFiltered(Integer idUsuario);
	List<Grupo> getGrupos() throws SQLException;
	void deleteUser(int usuarioId) throws SQLException;
	void deleteUserProperty(Usuario usuario, String propriedade) throws SQLException;
	Usuario getUsuarioByAlias(String alias) throws SQLException;
	
}
