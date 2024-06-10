package kissshot1104.personal.blog.global.util.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class S3ImageUploader {
    @Value("${application.bucket.name}")
    private String bucketName;
    private final AmazonS3 s3Client;


    public void uploadImage(final MultipartFile image, final String filename) {
        try {
            InputStream fileInputStream = image.getInputStream();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(image.getSize());
            s3Client.putObject(new PutObjectRequest(bucketName, filename, fileInputStream, metadata));
            fileInputStream.close();
        } catch (IOException io) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
    }

    public URL getImageUrl(final String filename) {
        URL imageUrl = s3Client.getUrl(bucketName, filename);
        return imageUrl;
    }
}
