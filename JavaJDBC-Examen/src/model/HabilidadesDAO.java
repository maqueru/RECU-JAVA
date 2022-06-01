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
public class HabilidadesDAO {

	private Connection conexionBD;

	public HabilidadesDAO(Connection conexionBD) {
		this.conexionBD = conexionBD;
	}
	
	public List<Habilidad> getHabilidadesList() {
		List<Habilidad> habilidadesList = new ArrayList<Habilidad>();
		try (ResultSet result = conexionBD.createStatement().executeQuery("SELECT * FROM players")) {
			while (result.next()) {
				habilidadesList.add(new Habilidad(result.getInt("id"), result.getString("nombre"), result.getDouble("coste"),result.getString("descripcion")));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return habilidadesList;
	}

	public boolean save(Habilidad player){
		try {
			String sql = "";
			PreparedStatement stmt = null;
			if (this.find(player.getId()) == null){
				sql = "INSERT INTO players VALUES(?,?,?,?)";
				stmt = conexionBD.prepareStatement(sql);
				int i = 1;
				stmt.setInt(i++, player.getId());
				stmt.setString(i++, player.getName());
				stmt.setDouble(i++, player.getCost());
				stmt.setString(i++, player.getDescripcion());
			} else{
				sql = "UPDATE players SET nombre=?,coste=?,descripcion=? WHERE id = ?";
				stmt = conexionBD.prepareStatement(sql);
				int i = 1;
				stmt.setString(i++, player.getName());
				stmt.setDouble(i++, player.getCost());
				stmt.setString(i++, player.getDescripcion());
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

	public Habilidad find(Integer id){
		if (id == null || id == 0){
			return null;
		}

		Habilidad p = null;
		try (PreparedStatement stmt = conexionBD.prepareStatement("SELECT * FROM players WHERE id = ?")){
			stmt.setInt(1, id); //informem el primer paràmetre de la consulta amb ?
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				p = new Habilidad(result.getInt("id"), result.getString("nombre"), result.getDouble("coste"),result.getString("descripcion"));
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
				Habilidad p = new Habilidad(result.getInt("id"), result.getString("nombre"), result.getDouble("coste"),result.getString("descripcion"));
				p.imprimir();
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	public List<Habilidad>  findSkillById(Integer id){
		if (id == null || id == 0){
			return null;
		}

		List<Habilidad> p= new ArrayList<Habilidad>();
		try (PreparedStatement stmt = conexionBD.prepareStatement("SELECT * FROM players WHERE id = ?")){
			stmt.setInt(1, id); //informem el primer paràmetre de la consulta amb ?
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				Habilidad skill = new Habilidad(result.getInt("id"), result.getString("nombre"), result.getDouble("coste"),result.getString("descripcion"));
				skill.imprimir();
				p.add(skill);
			}	
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return p;
	}
}

