package api.bpartners.annotator.file;

import api.bpartners.annotator.PojaGenerated;

@PojaGenerated
public record FileHash(FileHashAlgorithm algorithm, String value) {}
