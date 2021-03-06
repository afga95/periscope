package br.ufmt.periscope.model;



public enum UserLevel {
	
	ADMIN(10),USER(5);
	
	private int accessLevel;
	
	private UserLevel(int level){
		this.accessLevel = level;
	}
	
	public int getAccessLevel(){
		return accessLevel;
	}
	
	@Override
	public String toString() {
		return this.name().charAt(0) + this.name().toLowerCase().substring(1);
	}

}
