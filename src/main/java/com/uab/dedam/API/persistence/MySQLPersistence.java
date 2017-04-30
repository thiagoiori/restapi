package com.uab.dedam.API.persistence;

import com.uab.dedam.API.models.Grupo;
import com.uab.dedam.API.models.GrupoUsuario;
import com.uab.dedam.API.models.Usuario;
import com.uab.dedam.API.models.UsuarioFiltered;
import com.uab.dedam.API.util.EnvironmentVariables;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQLPersistence implements IPersistenceService {

    private static String DB_DRIVER = "com.mysql.jdbc.Driver";
    private static String MYSQL_URL = "127.0.0.1";
    private static String MYSQL_PORT = "3306";
    private static String DB_USER = "restapiuser";
    private static String DB_PASSWORD = "DedamRestAPIDB";

    private static Connection getDBConnection() {
        Connection dbConnection = null;
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            String url = EnvironmentVariables.getVariableValue("MYSQL_URL", MYSQL_URL);
            String port = EnvironmentVariables.getVariableValue("MYSQL_PORT", MYSQL_PORT);
            String user = EnvironmentVariables.getVariableValue("MYSQL_USER", DB_USER);
            String password = EnvironmentVariables.getVariableValue("MYSQL_PASSWORD", DB_PASSWORD);

            String dbConnectionString = "jdbc:mysql://" + url + ":" + port + "/DedamSQLDB";

            dbConnection = DriverManager.getConnection(dbConnectionString, user, password);
            return dbConnection;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return dbConnection;
    }

    @Override
    public List<Usuario> getUsuarios(Integer idUsuario) throws SQLException {
        PreparedStatement statement = null;
        List<Usuario> usuarios = new ArrayList<Usuario>();
        try (Connection connection = getDBConnection()) {
            String sql = MySQLQueries.getSelectUserQuery(idUsuario);
            statement = connection.prepareStatement(sql);
            try (ResultSet queryResults = statement.executeQuery()) {
                while (queryResults.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(queryResults.getInt(1));
                    usuario.setSelf("/rest/usuario/" + usuario.getId());
                    usuario.setAlias(queryResults.getString(3));
                    usuario.setName(queryResults.getString(4));
                    usuario.setSurname(queryResults.getString(5));
                    usuario.setAge(queryResults.getInt(6));
                    usuario.setPhone(queryResults.getInt(7));
                    if (queryResults.getInt(8) > 0) {
                        GrupoUsuario grupo = new GrupoUsuario(queryResults.getInt(8),
                                queryResults.getString(10),
                                queryResults.getString(11));
                        usuario.setGrupo(grupo);
                    } else
                        usuario.setGrupo(null);
                    usuario.setPhoto(queryResults.getString(9));
                    usuarios.add(usuario);
                }
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
        return usuarios;
    }

    @Override
    public Usuario getUsuarioByAlias(String alias) throws SQLException {
        PreparedStatement statement = null;
        Usuario usuario = null;
        try (Connection connection = getDBConnection()) {
            String sql = MySQLQueries.getSelectUserQueryByAlias();
            statement = connection.prepareStatement(sql);
            statement.setString(1, alias);
            try (ResultSet queryResults = statement.executeQuery()) {
                if (queryResults.next()) {
                    usuario = new Usuario();
                    usuario.setId(queryResults.getInt(1));
                    usuario.setSelf("/rest/usuario/" + usuario.getId());
                    usuario.setAlias(queryResults.getString(3));
                    usuario.setName(queryResults.getString(4));
                    usuario.setSurname(queryResults.getString(5));
                    usuario.setAge(queryResults.getInt(6));
                    usuario.setPhone(queryResults.getInt(7));
                    if (queryResults.getInt(8) > 0) {
                        GrupoUsuario grupo = new GrupoUsuario(queryResults.getInt(8),
                                queryResults.getString(10),
                                queryResults.getString(11));
                        usuario.setGrupo(grupo);
                    } else
                        usuario.setGrupo(null);
                    usuario.setPhoto(queryResults.getString(9));

                }
            } finally {
                if (statement != null) {
                    statement.close();
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
        return usuario;
    }

    @Override
    public void saveUsuario(Usuario usuario) throws SQLException {
        PreparedStatement statement = null;
        int affectedRows = 0;
        try (Connection connection = getDBConnection()) {
            statement = createInsertUserPreparedStatement(connection, usuario);
            affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    usuario.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating user failed, no ID obtained.");
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    private PreparedStatement createInsertUserPreparedStatement(
            Connection connection,
            Usuario usuario) throws SQLException {
        PreparedStatement statement = null;
        String sql = MySQLQueries.getInsertUserQuery();
        statement = connection.prepareStatement(sql);
        statement.setString(1, usuario.getSelf());
        statement.setString(2, usuario.getAlias());
        statement.setString(3, usuario.getName());
        statement.setString(4, usuario.getSurname());
        if (usuario.getAge() == null)
            statement.setNull(5, java.sql.Types.INTEGER);
        else
            statement.setInt(5, usuario.getAge());
        if (usuario.getPhone() == null)
            statement.setNull(6, java.sql.Types.INTEGER);
        else
            statement.setInt(6, usuario.getPhone());
        if (usuario.getGrupo() != null)
            statement.setInt(7, usuario.getGrupo().getId());
        else
            statement.setNull(7, java.sql.Types.INTEGER);
        statement.setString(8, usuario.getPhoto());
        return statement;
    }

    @Override
    public Grupo getGrupo(String groupName) throws SQLException {
        return getGrupo(0, groupName);
    }

    @Override
    public Grupo getGrupo(Integer idGrupo) throws SQLException {
        return getGrupo(idGrupo, "");
    }

    private Grupo getGrupo(Integer idGroup, String groupName) throws SQLException {
        Grupo grupo = null;
        PreparedStatement statement = null;

        try (Connection connection = getDBConnection()) {
            statement = createSelectGroupPreparedStatement(connection, idGroup, groupName);

            try (ResultSet queryResults = statement.executeQuery()) {
                if (queryResults.next()) {
                    grupo = new Grupo();
                    grupo.setId(queryResults.getInt(1));
                    grupo.setName(queryResults.getString(2));
                    grupo.setSelf(queryResults.getString(3));
                }
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
        return grupo;
    }

    @Override
    public void saveGrupo(Grupo grupo) throws SQLException {
        int affectedRows = 0;
        try (Connection connection = getDBConnection();
             PreparedStatement statement = createInsertGroupPreparedStatement(connection, grupo)) {

            affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Creating user failed, no rows affected.");
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    grupo.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Creating group failed, no ID obtained.");
                }
            }
        }
    }

    private PreparedStatement createInsertGroupPreparedStatement(
            Connection connection,
            Grupo grupo) throws SQLException {
        PreparedStatement statement = null;
        String sql = MySQLQueries.getInsertGroupQuery();
        statement = connection.prepareStatement(sql);
        statement.setString(1, grupo.getSelf());
        statement.setString(2, grupo.getName());
        return statement;
    }

    private PreparedStatement createSelectGroupPreparedStatement(
            Connection connection,
            Integer idGroup,
            String groupName) throws SQLException {
        PreparedStatement statement = null;
        Integer indiceColuna = 0;
        String sql = MySQLQueries.getSelectGroupQuery(idGroup, groupName);
        statement = connection.prepareStatement(sql);
        if (idGroup != null && idGroup > 0)
            statement.setInt(++indiceColuna, idGroup);
        if (groupName != null && !groupName.isEmpty())
            statement.setString(++indiceColuna, groupName);
        return statement;
    }

    @Override
    public void updateUser(Usuario usuario, String propriedade) throws SQLException {
        PreparedStatement statement = null;
        int affectedRows = 0;
        try (Connection connection = getDBConnection()) {
            statement = createUpdateUsuarioPreparedStatement(connection, usuario, propriedade);
            affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update user failed, no rows affected.");
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    private PreparedStatement createUpdateUsuarioPreparedStatement(
            Connection connection,
            Usuario usuario,
            String propriedade)
            throws
            SQLException {
        PreparedStatement statement = null;
        String sql = MySQLQueries.getUpdateUserQuery(usuario, propriedade);
        statement = connection.prepareStatement(sql);
        switch (propriedade) {
            case "alias":
                statement.setString(1, usuario.getAlias());
                break;
            case "name":
                statement.setString(1, usuario.getName());
                break;
            case "surname":
                statement.setString(1, usuario.getSurname());
                break;
            case "age":
                statement.setInt(1, usuario.getAge());
                break;
            case "phone":
                statement.setInt(1, usuario.getPhone());
                break;
            case "photo":
                statement.setString(1, usuario.getPhoto());
                break;
            case "group":
                statement.setInt(1, usuario.getGrupo().getId());
                break;
            default:
                break;
        }

        statement.setInt(2, usuario.getId());

        return statement;
    }

    @Override
    public List<UsuarioFiltered> getUsuarioFiltered(Integer idUsuario) {
        List<UsuarioFiltered> usuarios = new ArrayList<UsuarioFiltered>();
        String sql = MySQLQueries.getSelectUserQuery(idUsuario);
        try (Connection connection = getDBConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet queryResults = statement.executeQuery()) {

            while (queryResults.next()) {
                UsuarioFiltered usuario = new UsuarioFiltered();
                usuario.setId(queryResults.getInt(1));
                usuario.setSelf("/rest/usuario/" + usuario.getId());
                usuario.setAlias(queryResults.getString(3));
                usuario.setName(queryResults.getString(4));
                usuario.setSurname(queryResults.getString(5));
                usuario.setAge(queryResults.getInt(6));
                usuario.setPhone(queryResults.getInt(7));
                if (queryResults.getInt(8) > 0) {
                    GrupoUsuario grupo = new GrupoUsuario(queryResults.getInt(8),
                            queryResults.getString(10),
                            queryResults.getString(11));
                    //usuario.setGrupo(getGrupo(queryResults.getInt(8)));
                } else
                    usuario.setGrupo(null);
                usuario.setPhoto(queryResults.getString(9));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
        return usuarios;
    }

    @Override
    public List<Grupo> getGrupos() throws SQLException {
        List<Grupo> grupos = new ArrayList<Grupo>();
        try (Connection connection = getDBConnection();
             PreparedStatement statement = createSelectGroupPreparedStatement(connection, 0, "");
             ResultSet queryResults = statement.executeQuery()) {
            while (queryResults.next()) {
                Grupo grupo = new Grupo();
                grupo.setId(queryResults.getInt(1));
                grupo.setName(queryResults.getString(2));
                grupo.setSelf(queryResults.getString(3));
                grupos.add(grupo);
            }
        }
        return grupos;
    }

    @Override
    public void deleteUser(int usuarioId) throws SQLException {
        try (Connection connection = getDBConnection();
             PreparedStatement statement = createDeleteUsuarioPreparedStatement(connection, usuarioId)) {
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update user failed, no rows affected.");
            }
        }
    }

    private PreparedStatement createDeleteUsuarioPreparedStatement(
            Connection connection,
            int usuarioId)
            throws
            SQLException {
        PreparedStatement statement = null;
        String sql = MySQLQueries.getDeleteUserQuery();
        statement = connection.prepareStatement(sql);
        statement.setInt(1, usuarioId);

        return statement;
    }

    @Override
    public void deleteUserProperty(Usuario usuario, String propriedade) throws SQLException {
        PreparedStatement statement = null;
        int affectedRows = 0;
        try (Connection connection = getDBConnection()) {
            statement = createDeleteUsuarioPropertyPreparedStatement(
                    connection,
                    usuario,
                    propriedade);
            affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Delete user property failed, no rows affected.");
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    private PreparedStatement createDeleteUsuarioPropertyPreparedStatement(
            Connection connection,
            Usuario usuario,
            String propriedade)
            throws
            SQLException {
        PreparedStatement statement = null;
        String sql = MySQLQueries.getUpdateUserQuery(usuario, propriedade);
        statement = connection.prepareStatement(sql);
        switch (propriedade) {
            case "alias":
            case "name":
            case "surname":
            case "photo":
                statement.setNull(1, java.sql.Types.VARCHAR);
                break;
            case "age":
            case "phone":
            case "group":
                statement.setNull(1, java.sql.Types.INTEGER);
                break;
            default:
                break;
        }

        statement.setInt(2, usuario.getId());

        return statement;
    }

    @Override
    public void deleteGroup(Integer groupId) throws SQLException {
        PreparedStatement statement = null;
        int affectedRows = 0;
        try (Connection connection = getDBConnection()) {
            statement = createDeleteGrupoPreparedStatement(connection, groupId);
            affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Update user failed, no rows affected.");
            }
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    private PreparedStatement createDeleteGrupoPreparedStatement(
            Connection connection,
            int grupoId)
            throws
            SQLException {
        PreparedStatement statement = null;
        String sql = MySQLQueries.getDeleteGroupQuery();
        statement = connection.prepareStatement(sql);
        statement.setInt(1, grupoId);

        return statement;
    }
}
