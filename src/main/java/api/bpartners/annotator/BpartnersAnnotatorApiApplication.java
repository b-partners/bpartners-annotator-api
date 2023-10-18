package api.bpartners.annotator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BpartnersAnnotatorApiApplication {

  public static void main(String[] args) {
    SpringApplication.run(BpartnersAnnotatorApiApplication.class, args);
  }

}
