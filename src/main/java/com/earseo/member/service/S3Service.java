package com.earseo.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private static final String CDN_DOMAIN = "https://cdn.earseo.click/";

    public String uploadFile(MultipartFile file, String directory) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("업로드할 파일이 없습니다.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = directory + "/" + UUID.randomUUID() + extension;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        try {
            amazonS3.putObject(new PutObjectRequest(bucket, savedFilename, file.getInputStream(), metadata));
        } catch (IOException e) {
            log.error("S3 파일 업로드 실패: {}", e.getMessage());
            throw new RuntimeException("파일 업로드에 실패했습니다.");
        }

//        return amazonS3.getUrl(bucket, savedFilename).toString();
        return CDN_DOMAIN + savedFilename;
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            String key = extractKeyFromUrl(fileUrl);
            if (key == null || key.isEmpty()) {
                log.warn("알 수 없는 파일 URL 형식입니다: {}", fileUrl);
                return;
            }
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
            log.info("S3 파일 삭제 완료: {}", key);
        } catch (Exception e) {
            log.error("S3 파일 삭제 실패: {}", e.getMessage(), e);
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        if(fileUrl.startsWith(CDN_DOMAIN)) {
            return fileUrl.substring(CDN_DOMAIN.length());
        }

        if (fileUrl.contains(bucket)) {
            int bucketIndex = fileUrl.indexOf(bucket);
            if (bucketIndex == -1) {
                return null;
            }
            int keyStart = bucketIndex + bucket.length() + 1;
            if (keyStart >= fileUrl.length()) {
                return null;
            }
            return fileUrl.substring(keyStart);
        }

        if (!fileUrl.startsWith("http://") && !fileUrl.startsWith("https://")) {
            return fileUrl;
        }

        return null;
    }
}