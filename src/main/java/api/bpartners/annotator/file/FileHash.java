package api.bpartners.annotator.file;

import api.bpartners.annotator.PojaGenerated;

@PojaGenerated
@SuppressWarnings("all")
public record FileHash(FileHashAlgorithm algorithm, String value) {}
