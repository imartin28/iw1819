package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.CascadeType;
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
		+ " WHERE uf.user.id = :id_currentUser "),
@NamedQuery(name="findAllById", query="SELECT file "
		+ " FROM CFile file"
		+ " WHERE file.id IN :ids "),
@NamedQuery(name="deleteInBatch", query="DELETE "
		+ " FROM CFile file"
		+ " WHERE file.id IN :ids ")
})
public class CFile {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private long id;
	
	private String name;
	private String path;
	private String mimetype;
	private String extension;
	private Long size;
	private String metadata;
	 
	@OneToMany(targetEntity=UserFile.class, mappedBy="file", cascade=CascadeType.ALL)
	private List<UserFile> filePermissions;
	
	@ManyToMany(targetEntity=CGroup.class, mappedBy="files")
	private List<CGroup> groups;

	@ManyToMany(targetEntity=Tag.class)
	private List<Tag> tags;
	
	
	
	public CFile() {
		
	}
	
	public CFile(String name, Long size, String mimetype) {
		this.name = name;
		this.extension = getExtension(this.name);
		this.size = size;
		this.mimetype = mimetype;
		this.metadata = "{}";
	}

	
	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getMimetype() {
		return mimetype;
	}

	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
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
	

	private String getExtension(String name) {
		String pattern = "^[^\\.]+\\.([a-z]+)$";
		Pattern r = Pattern.compile(pattern);
		Matcher m = r.matcher(name);
		if (m.find( )) {
	         return m.group(1);
		}
		return "";
	}
}
