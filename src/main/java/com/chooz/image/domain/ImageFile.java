package com.chooz.image.domain;

import com.chooz.common.domain.BaseEntity;
import com.chooz.image.presentation.dto.ImageFileDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "image_files")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ImageFile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String originImageName;

    @Column(nullable = false, length = 200)
    private String imageUrl;

    @Column(nullable = false, length = 200)
    private String thumbnailUrl;

    private ImageFile(String originImageName, String imageUrl, String thumbnailUrl) {
        this.originImageName = originImageName;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
    }

    public static ImageFile create(ImageFileDto dto) {
        return new ImageFile(dto.originFileName(), dto.imageUrl(), dto.thumbnailUrl());
    }
}
