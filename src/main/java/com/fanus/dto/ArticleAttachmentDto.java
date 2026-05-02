package com.fanus.dto;

public record ArticleAttachmentDto(
    Long id,
    String fileUrl,
    String fileName,
    String fileType,
    Long fileSizeBytes,
    int displayOrder
) {}
