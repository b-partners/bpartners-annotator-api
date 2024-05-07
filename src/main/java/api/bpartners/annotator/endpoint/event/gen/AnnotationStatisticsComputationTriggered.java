package api.bpartners.annotator.endpoint.event.gen;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.mail.internet.InternetAddress;
import java.io.Serializable;
import javax.annotation.processing.Generated;
import lombok.Builder;

@Generated("EventBridge")
@Builder(toBuilder = true)
public record AnnotationStatisticsComputationTriggered(
    @JsonProperty("job_id") String jobId, @JsonProperty("email_cc") InternetAddress emailCC)
    implements Serializable {}
