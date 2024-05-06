package kissshot1104.personal.blog.upload_file.repository;

import kissshot1104.personal.blog.upload_file.entity.UploadFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {
}
