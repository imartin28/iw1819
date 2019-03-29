package es.ucm.fdi.iw.service;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.integration.FileRepository;
import es.ucm.fdi.iw.model.CFile;

@Service
public class FileServiceImpl implements FileService {
	
	@Autowired
	private FileRepository fileRepo;
	
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public CFile findById(long id) {
		return fileRepo.findById(id);
	}
	
	@Override
	public List<CFile> getAll() {
		return fileRepo.findAll();
	}

}
