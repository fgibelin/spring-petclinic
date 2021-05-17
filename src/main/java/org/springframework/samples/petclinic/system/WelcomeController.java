/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.samples.petclinic.system;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;

/* Add CloudBees Feature Flags imports */
import org.springframework.samples.petclinic.flags.FlagsController;

@Controller
class WelcomeController {

        private static final Logger logger = LoggerFactory.getLogger(WelcomeController.class);

        // Initialize Flags container class
        @Autowired
        private FlagsController flags;

        @GetMapping("/")
        public String welcome() {
                // Boolean flag example
                if (flags.enableTutorial.isEnabled()) {
                        logger.info("Tutorial is ENABLED");
                }
                else {
                        logger.info("Tutorial is DISABLED");
                }

                // Get the welcome image value and display it
                String welcomeImage = flags.welcomeImage.getValue();
                logger.info("Flag welcomeImage is {}", welcomeImage);
                if (welcomeImage.equals("Koala")) {
                        logger.info("Let's display Koala");
                        return "welcome_koala";
                }
                else {
                        logger.info("Let's display default");
                        return "welcome";
                }
        }

}