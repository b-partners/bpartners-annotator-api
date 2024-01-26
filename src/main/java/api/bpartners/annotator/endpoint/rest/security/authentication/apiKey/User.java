package api.bpartners.annotator.endpoint.rest.security.authentication.apiKey;

import java.io.Serializable;

public record User(String email) implements Serializable {}
