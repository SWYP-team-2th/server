package com.chooz.image.presentation;

import com.chooz.image.application.FileService;
import com.chooz.image.application.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final ImageService r2Service;
    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createImageFile(@RequestPart("files") MultipartFile... files) {
//        ImageFileResponse response = r2Service.uploadImageFile(files);
        String preSignedUrl = fileService.getPreSignedUrl(files[0].getOriginalFilename());
        return ResponseEntity.ok(preSignedUrl);
    }

}
