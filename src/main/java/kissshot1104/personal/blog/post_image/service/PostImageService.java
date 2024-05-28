package kissshot1104.personal.blog.post_image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.UUID;
import kissshot1104.personal.blog.global.exception.BusinessException;
import kissshot1104.personal.blog.global.exception.ErrorCode;
import kissshot1104.personal.blog.global.util.s3.S3ImageUploader;
import kissshot1104.personal.blog.member.entity.Member;
import kissshot1104.personal.blog.post_image.entity.PostImage;
import kissshot1104.personal.blog.post_image.repository.PostImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostImageService {

    private final S3ImageUploader s3ImageUploader;
    private final PostImageRepository postImageRepository;

    public String savePostImage(final MultipartFile image, final Member member) {

        // 파일을 InputStream으로 변환
        String originalFilename = image.getOriginalFilename();
        String filename = UUID.randomUUID() + "_" + originalFilename;

        // S3에 파일 업로드
        s3ImageUploader.uploadImage(image, filename);
        final URL imagePath = s3ImageUploader.getImageUrl(filename);
        //todo imagePath null체크 해야하는가?
        final PostImage postImage = PostImage.builder()
                .member(member)
                .imagePath(imagePath.toString())
                .build();

        postImageRepository.save(postImage);
        return postImage.getImagePath();
    }
}

