package es.ucm.fdi.iw.service;

import java.util.List;

import es.ucm.fdi.iw.model.CFile;

public interface FileService {
	
	CFile findById(long id);
	List<CFile> findAllBysha256(String sha256);
	List<CFile> getAll();
	List<CFile> getAllById(List<Long> ids);
	void deleteFile(CFile file);
	void deleteFiles(List<CFile> files);
	
}
