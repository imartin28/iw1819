package es.ucm.fdi.iw.integration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import es.ucm.fdi.iw.model.CFile;

@Repository("fileRepository")
public interface FileRepository extends JpaRepository<CFile, Long> {
	CFile findById(long id);
}
