package api.bpartners.annotator;

import api.bpartners.annotator.endpoint.event.EventConf;
import api.bpartners.annotator.endpoint.event.EventConsumer;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import software.amazon.awssdk.services.sqs.SqsClient;

import static api.bpartners.annotator.endpoint.event.EventConsumer.toAcknowledgeableTypedEvent;

@Slf4j
public class MailboxEventHandler implements RequestHandler<SQSEvent, String> {
  private static int anAvailableRandomPort() {
    try(ServerSocket serverSocket = new ServerSocket(0)) {
      return serverSocket.getLocalPort();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public String handleRequest(SQSEvent event, Context context) {
    log.info("Following received events : {}\n", event);
    List<SQSEvent.SQSMessage> messages = event.getRecords();
    log.info("Following received messages : {}\n", messages);

    ConfigurableApplicationContext applicationContext = applicationContext();
    EventConsumer eventConsumer = applicationContext.getBean(EventConsumer.class);
    EventConf eventConf = applicationContext.getBean(EventConf.class);
    SqsClient sqsClient = applicationContext.getBean(SqsClient.class);

    eventConsumer.accept(toAcknowledgeableTypedEvent(eventConf, sqsClient, messages));

    applicationContext.close();
    return "{message: ok}";
  }

  private ConfigurableApplicationContext applicationContext(String... args) {
    SpringApplication application = new SpringApplication(BpartnersAnnotatorApiApplication.class);
    application.setDefaultProperties(Collections.singletonMap(
        "server.port", anAvailableRandomPort()));
    return application.run(args);
  }
}