package com.uab.dedam.API.persistence;

import com.uab.dedam.API.models.Usuario;

public final class MySQLQueries {
	public static String getInsertUserQuery(){
		return "INSERT INTO  `DedamSQLDB`.`usuario` " + 
				"(`self`,`alias`,`name`,`surname`,`age`,`phone`,`idgrupo`,`photo`)" +
				" VALUES (?,?,?,?,?,?,?,?)";
	}
	
	public static String getInsertGroupQuery(){
		return "INSERT INTO  `DedamSQLDB`.`grupo` " + 
				"(`self`,`name`)" +
				" VALUES (?,?)";
	}
	
	public static String getSelectUserQuery(Integer userId){
		String Sql = "SELECT `idusuario`,usr.self usrself,`alias`,usr.name usrname,`surname`,`age`,`phone`, usr.idgrupo,`photo`," +
				" grp.name grpname, grp.self grpself " +
				" FROM `DedamSQLDB`.`usuario` usr left join DedamSQLDB.grupo grp "+
				" on usr.idgrupo = grp.idgrupo " +
				" WHERE 1 = 1 " + 
				(userId > 0 ? " AND usr.idusuario = " + userId : "");
		return Sql;
	}
	
	public static String getSelectGroupQuery(
			Integer groupId,
			String groupName){
		String Sql = "SELECT `idgrupo`,`name`,`self`" + 
				" FROM `DedamSQLDB`.`grupo` WHERE 1 = 1" + 
				(groupId > 0 ? " AND idgrupo = ?" : "") +
				(groupName != null && !groupName.isEmpty() ? " AND name = ?" : "");
		
		return Sql;
	}

	public static String getUpdateUserQuery(Usuario usuario, String propriedade) {
		propriedade = propriedade.equals("group") ? "idgrupo" : propriedade;
		String Sql = "UPDATE `DedamSQLDB`.`usuario` " + 
				" SET " + propriedade +  " = ? " + 
				" WHERE idusuario = ?";
		
		return Sql;
	}
	
	public static String getDeleteUserQuery() {
		String Sql = "delete from `DedamSQLDB`.`usuario` " +  
				" WHERE idusuario = ?";
		return Sql;
	}
	
	public static String getDeleteGroupQuery() {
		String Sql = "delete from `DedamSQLDB`.`grupo` " +  
				" WHERE idgrupo = ?";
		return Sql;
	}

	public static String getSelectUserQueryByAlias() {
		String Sql = "SELECT `idusuario`,usr.self usrself,`alias`,usr.name usrname,`surname`,`age`,`phone`, usr.idgrupo,`photo`," +
				" grp.name grpname, grp.self grpself " +
				" FROM `DedamSQLDB`.`usuario` usr left join DedamSQLDB.grupo grp "+
				" on usr.idgrupo = grp.idgrupo " +
				" WHERE 1 = 1 " + 
				" AND usr.alias = ?";
		return Sql;
	}
}
