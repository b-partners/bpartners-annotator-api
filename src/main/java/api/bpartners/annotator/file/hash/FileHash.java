package api.bpartners.annotator.file.hash;

import api.bpartners.annotator.PojaGenerated;

@PojaGenerated
@SuppressWarnings("all")
public record FileHash(FileHashAlgorithm algorithm, String value) {}
