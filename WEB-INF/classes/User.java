

public class User {
	int id;
	public User(int id, String name, String role) {
		super();
		this.id = id;
		this.name = name;
		
		this.role = role;
	}

	String name,role;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
}
