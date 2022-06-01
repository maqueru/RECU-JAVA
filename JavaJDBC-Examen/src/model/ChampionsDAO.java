package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
/**
 * Clase per gestionar la persistència de les dades de les champions
 * @author manuel
 *
 */
public class ChampionsDAO {

	private Connection conexionBD;

	public ChampionsDAO(Connection conexionBD) {
		this.conexionBD = conexionBD;
	}
	
	public List<Campeon> getChampionsList() {
		List<Campeon> championsList = new ArrayList<Campeon>();
		try (ResultSet result = conexionBD.createStatement().executeQuery("SELECT * FROM champions")) {
			while (result.next()) {
				championsList.add(new Campeon(result.getInt("id"), result.getString("nombre"), result.getString("raza"),result.getInt("vida"),result.getInt("energia"),managerDAO.getInstance().getSkillDAO().findSkillById(result.getInt("id1")),managerDAO.getInstance().getObjectDAO().findObjectById(result.getInt("id1"))));
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return championsList;
	}

	public boolean save(Campeon champion){
		try {
			String sql = "";
			PreparedStatement stmt = null;
			if (this.find(champion.getId()) == null){
				sql = "INSERT INTO champions VALUES(?,?,?,?)";
				stmt = conexionBD.prepareStatement(sql);
				int i = 1;
				stmt.setInt(i++, champion.getId());
				stmt.setString(i++, champion.getName());
				stmt.setString(i++, champion.getRaza());
				stmt.setInt(i++, champion.getVida());
				stmt.setInt(i++, champion.getEnergia());
				/*stmt.setList(i++, champion.getHabilidades());
				stmt.setList(i++, champion.getObjetos());*/
			} else{
				sql = "UPDATE champions SET nombre=?,position=?,rol=? WHERE id = ?";
				stmt = conexionBD.prepareStatement(sql);
				int i = 1;
				stmt.setString(i++, champion.getName());
				stmt.setString(i++, champion.getRaza());
				stmt.setInt(i++, champion.getVida());
				stmt.setInt(i++, champion.getEnergia());
				/*stmt.setList(i++, champion.getHabilidades());
				stmt.setList(i++, champion.getObjetos());*/
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
				sql = "DELETE FROM champions WHERE id = ?";
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

	public Campeon find(Integer id){
		if (id == null || id == 0){
			return null;
		}

		Campeon c = null;
		try (PreparedStatement stmt = conexionBD.prepareStatement("SELECT * FROM champions WHERE id = ?")){
			stmt.setInt(1, id); //informem el primer paràmetre de la consulta amb ?
			ResultSet result = stmt.executeQuery();
			if (result.next()) {
				c = new Campeon(result.getInt("id"), result.getString("nombre"), result.getString("raza"),result.getInt("vida"),result.getInt("energia"),managerDAO.getInstance().getSkillDAO().findSkillById(result.getInt("id1")),managerDAO.getInstance().getObjectDAO().findObjectById(result.getInt("id1")));
				c.imprimir();
			}	
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return c;
	}

	public void showAll(){
		try (ResultSet result = conexionBD.createStatement().executeQuery("SELECT * FROM champions")) {
			while (result.next()) {
				Campeon c = new Campeon(result.getInt("id"), result.getString("nombre"), result.getString("raza"),result.getInt("vida"),result.getInt("energia"),managerDAO.getInstance().getSkillDAO().findSkillById(result.getInt("id1")),managerDAO.getInstance().getObjectDAO().findObjectById(result.getInt("id1")));
				c.imprimir();
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
}

