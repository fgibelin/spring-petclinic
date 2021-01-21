package org.springframework.samples.petclinic.flags;

import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import javax.annotation.PostConstruct;
import io.rollout.configuration.RoxContainer;
import io.rollout.rox.server.Rox;
import io.rollout.flags.RoxFlag;
import io.rollout.flags.RoxVariant;

// Create Roxflags in the Flags container class
@Component
public class FlagsController implements RoxContainer {
    // Define the feature flags
    public RoxFlag enableTutorial = new RoxFlag(true);
    public RoxVariant titleColors = new RoxVariant("White", new String[] { "White", "Blue", "Green" });

    @Value("${devEnvKey}")
    private String devEnvKey;

    @PostConstruct
    void postConstruct() {
        try {
            // Register the flags container
            Rox.register("", this);
            // Setup the Rollout environment key
            Rox.setup(devEnvKey).get();

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
