package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * Clase per gestionar la persistència de les dades de les players
 * @author manuel
 *
 */
public class ObjetosDAO {

	private Connection conexionBD;

	public ObjetosDAO(Connection conexionBD) {
		this.conexionBD = conexionBD;
	}
	
	public List<Objeto> getObjetosList() {
		List<Objeto> objetosList = new ArrayList<Objeto>();
		try (ResultSet result = conexionBD.createStatement().executeQuery("SELECT * FROM players")) {
			while (result.next()) {
				objetosList.add(new Objeto(result.getInt("id"), result.getString("nombre"), result.getInt("valor"),result.getString("efecto")));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return objetosList;
	}

	public boolean save(Objeto player){
		try {
			String sql = "";
			PreparedStatement stmt = null;
			if (this.find(player.getId()) == null){
				sql = "INSERT INTO players VALUES(?,?,?,?)";
				stmt = conexionBD.prepareStatement(sql);
				int i = 1;
				stmt.setInt(i++, player.getId());
				stmt.setString(i++, player.getName());
				stmt.setDouble(i++, player.getValor());
				stmt.setString(i++, player.getEfecto());
			} else{
				sql = "UPDATE players SET nombre=?,valor=?,efecto=? WHERE id = ?";
				stmt = conexionBD.prepareStatement(sql);
				int i = 1;
				stmt.setString(i++, player.getName());
				stmt.setDouble(i++, player.getValor());
				stmt.setString(i++, player.getEfecto());
				stmt.setInt(i++, player.getId());
			}
			int rows = stmt.executeUpdate();
			if (rows == 1) return true;
			else return false;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public boolean delete(Integer id){
		try {
			String sql = "";
			PreparedStatement stmt = null;
			if (this.find(id) != null){
				sql = "DELETE FROM players WHERE id = ?";
				stmt = conexionBD.prepareStatement(sql);
				int i = 1;
				stmt.setInt(i++, id);
			}
			int rows = stmt.executeUpdate();
			if (rows == 1) return true;
			else return false;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}

	public Objeto find(Integer id){
		if (id == null || id == 0){
			return null;
		}

		Objeto p = null;
		try (PreparedStatement stmt = conexionBD.prepareStatement("SELECT * FROM players WHERE id = ?")){
			stmt.setInt(1, id); //informem el primer paràmetre de la consulta amb ?
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				p = new Objeto(result.getInt("id"), result.getString("nombre"), result.getInt("valor"),result.getString("efecto"));
				p.imprimir();
			}	
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return p;
	}

	public void showAll(){
		try (ResultSet result = conexionBD.createStatement().executeQuery("SELECT * FROM players")) {
			while (result.next()) {
				Objeto p = new Objeto(result.getInt("id"), result.getString("nombre"), result.getInt("valor"),result.getString("efecto"));
				p.imprimir();
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	public List<Objeto> findObjectById(Integer id){
		if (id == null || id == 0){
			return null;
		}

		List<Objeto> p= new ArrayList<Objeto>();
		try (PreparedStatement stmt = conexionBD.prepareStatement("SELECT * FROM players WHERE id = ?")){
			stmt.setInt(1, id); //informem el primer paràmetre de la consulta amb ?
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				 Objeto object =new Objeto(result.getInt("id"), result.getString("nombre"), result.getInt("valor"),result.getString("efecto"));
				 object.imprimir();
				 p.add(object);
			}	
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return p;
	}
}

