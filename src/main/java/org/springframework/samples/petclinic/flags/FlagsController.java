package org.springframework.samples.petclinic.flags;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;

import io.rollout.client.ConfigurationFetchedHandler;
import io.rollout.client.FetcherResults;
import io.rollout.configuration.RoxContainer;
import io.rollout.rox.server.Rox;
import io.rollout.rox.server.RoxOptions;
import io.rollout.flags.RoxFlag;
import io.rollout.flags.RoxVariant;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

// Create Roxflags in the Flags container class
@Controller
public class FlagsController implements RoxContainer {

    private final Logger logger = LoggerFactory.getLogger(FlagsController.class);

    // Define the feature flags
    public RoxFlag enableTutorial = new RoxFlag(true);
    public RoxVariant titleColors = new RoxVariant("White", new String[] { "White", "Blue", "Green" });

    @Value("${ffEnvKey}")
    private String ffEnvKey;

    @PostConstruct
    void postConstruct() {
        try {
            RoxOptions options = new RoxOptions.Builder()
                    .withConfigurationFetchedHandler(new ConfigurationFetchedHandler() {
                        @Override
                        public void onConfigurationFetched(FetcherResults results) {
                            logger.info("Fetched configuration origin: {}", results.getFetcherStatus());
                            logger.info("Fetched configuration date: {}", results.getCreationDate());
                        }
                    }).build();
            // Register the flags container
            Rox.register("", this);
            // Setup the Rollout environment key
            Rox.setup(ffEnvKey, options).get();

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }
}
