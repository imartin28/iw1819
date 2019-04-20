package es.ucm.fdi.iw.util;

import java.util.List;

public class PostTagFile {
	private List<Long> tagsIds;
	private Long fileId;

	public List<Long> getTagsIds() {
		return tagsIds;
	}

	public void setTagsIds(List<Long> tagsIds) {
		this.tagsIds = tagsIds;
	}

	public Long getFileId() {
		return fileId;
	}

	public void setFileId(Long fileId) {
		this.fileId = fileId;
	}
}
