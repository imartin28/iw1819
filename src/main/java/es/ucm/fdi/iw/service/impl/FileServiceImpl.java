package es.ucm.fdi.iw.service.impl;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.integration.FileRepository;
import es.ucm.fdi.iw.model.CFile;
import es.ucm.fdi.iw.service.FileService;

@Service
public class FileServiceImpl implements FileService {
	
	@Autowired
	private FileRepository fileRepo;
	
	@Override
	public CFile findById(long id) {
		return fileRepo.findById(id);
	}
	
	@Override
	public CFile save(CFile file) {
        if(file != null)
        	file = fileRepo.save(file);
        return file;
	}
	
	@Override
	public List<CFile> getAll() {
		return fileRepo.findAll();
	}

	@Override
	public void deleteFile(CFile file) {
		
		fileRepo.delete(file);
	}

	@Override
	public List<CFile> getAllById(List<Long> ids) {
		 
		return fileRepo.findAllById(ids);
	}

	@Override
	public void deleteFiles(List<CFile> files) {
		for (CFile file : files) {
			this.deleteFile(file);
		}
	}

	@Override
	public List<CFile> findAllBysha256(String sha256) {
		return fileRepo.findAllBysha256(sha256);
	}

	
	

}
