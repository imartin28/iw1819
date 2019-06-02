package es.ucm.fdi.iw.transfer;

import es.ucm.fdi.iw.model.CGroup;

public class GroupTransfer {

	
	private Long id;
	private String name;
	
	
	public GroupTransfer(CGroup group) {
		this.id = group.getId();
		this.name = group.getName();
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
