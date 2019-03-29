package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

 


@Entity
@NamedQueries({
@NamedQuery(name="FilesUser", query="SELECT cf "
		+ " FROM UserFile uf JOIN uf.file cf "
		+ " WHERE uf.user.id = :id_currentUser ")
})
public class CFile {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String metadata;
	 
	@OneToMany(targetEntity=UserFile.class, mappedBy="file")
	private List<UserFile> filePermissions;
	
	@ManyToMany(targetEntity=CGroup.class, mappedBy="files")
	private List<CGroup> groups;

	@ManyToMany(targetEntity=Tag.class)
	private List<Tag> tags;
	
	public CFile() {
		
	}
	
	
	public CFile(String metadata) {
		this.metadata = metadata;
	}
	

	public List<UserFile> getFilePermissions() {
		return filePermissions;
	}

	public void setFilePermissions(List<UserFile> filePermissions) {
		this.filePermissions = filePermissions;
	}

	
	public void setGroups(List<CGroup> groups) {
		this.groups = groups;
	}

	public List<CGroup> getGroups() {
		return this.groups;
	}
	
	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}
	
	 


	public String getMetadata() {
		return metadata;
	}


	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public List<String> tagNameList() {
		List<String> strings = new ArrayList<String>(tags.size());
		for (Tag tag : tags) {
		    strings.add(tag != null ? tag.getName() : null);
		}
		
		return strings;
	}
	

}
