package calculateprice;

import java.util.logging.Logger;
import java.awt.Desktop;
import java.net.URI;

import org.camunda.bpm.client.ExternalTaskClient;

public class CalculatePriceWorker {
    private final static Logger LOGGER = Logger.getLogger(CalculatePriceWorker.class.getName());

    public static void main(String[] args) {
        ExternalTaskClient client = ExternalTaskClient.create()
                .baseUrl("http://localhost:8080/engine-rest")
                .asyncResponseTimeout(10000) // long polling timeout
                .build();


        client.subscribe("calculate-price")
                .lockDuration(1000)
                .handler((externalTask, externalTaskService) -> {


                    boolean broken = (boolean) externalTask.getVariable("broken");
                    String newTitle = (String) externalTask.getVariable("newTitle");
                    String oldTitle = (String) externalTask.getVariable("oldTitle");
                    String releaseYear = (String) externalTask.getVariable("releaseYear");
                    String price = (String) externalTask.getVariable("price");


                    double priceN = Double.parseDouble(price);
                    double releaseYearN = Double.parseDouble(releaseYear);


                    if (releaseYearN < 2010 && releaseYearN > 2000 )
                        priceN = priceN*0.95;

                    if (releaseYearN < 2015 && releaseYearN > 2010 )
                        priceN = priceN*0.90;

                    if (releaseYearN < 2020 && releaseYearN > 2015 )
                        priceN = priceN*0.80;

                    if (releaseYearN == 2020 )
                        priceN = priceN*0.70;


                        LOGGER.info( "For the game:" + oldTitle + "the game: " + newTitle + "is discounted to : " + priceN);

                        // IN A NEW EXTERNAL CAMUNDA TASK:
                        // send invoice
                        // send text messeage (RabitMQ)
                        // save deal in database



                    try {
                      //  Desktop.getDesktop().browse(new URI("https://docs.camunda.org/get-started/quick-start/complete"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // externalTaskService.complete(externalTask, Collections.singletonMap("price", newPrice);
                    externalTaskService.complete(externalTask);
                })
                .open();
    }
}
