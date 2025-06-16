package com.chooz.image.presentation;

import com.chooz.image.application.ImageService;
import com.chooz.image.presentation.dto.PresignedUrlRequest;
import com.chooz.image.presentation.dto.PresignedUrlResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/image")
public class ImageController {

    private final ImageService imageService;

    @PostMapping(value = "/upload")
    public ResponseEntity<PresignedUrlResponse> createPresignedUrl(@Valid @RequestBody PresignedUrlRequest request) {
        PresignedUrlResponse response = imageService.getPresignedUrl(request);
        return ResponseEntity.ok(response);
    }
}
