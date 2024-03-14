package api.bpartners.annotator.model;

import lombok.Builder;

@Builder
public record CustomS3Object(String bucketName, String key, long size, int width, int height) {}
