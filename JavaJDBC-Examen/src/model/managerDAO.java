package model;

public class managerDAO {
	private ChampionsDAO champDAO;
	private ObjetosDAO objectDAO;
	private HabilidadesDAO skillDAO;
	
	private static managerDAO instance;
	
	private managerDAO() {
		
	}

	public static managerDAO getInstance() {
		if(instance==null) {
			instance = new managerDAO();
		}
		return instance;
	}
	
	public ChampionsDAO getChampDAO() {
		return champDAO;
	}

	public void setChampDAO(ChampionsDAO champDAO) {
		this.champDAO = champDAO;
	}

	public ObjetosDAO getObjectDAO() {
		return objectDAO;
	}

	public void setObjectDAO(ObjetosDAO objectDAO) {
		this.objectDAO = objectDAO;
	}

	public HabilidadesDAO getSkillDAO() {
		return skillDAO;
	}

	public void setSkillDAO(HabilidadesDAO skillDAO) {
		this.skillDAO = skillDAO;
	}
}
