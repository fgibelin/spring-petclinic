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

package org.springframework.samples.petclinic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.rollout.rox.server.Rox;
import org.springframework.samples.petclinic.flags.FlagsController;

/**
 * PetClinic Spring Boot Application.
 *
 * @author Dave Syer
 *
 */
@SpringBootApplication(proxyBeanMethods = false)
public class PetClinicApplication {

	public static void main(String[] args) {

		// Initialize Flags container class
		FlagsController flags = new FlagsController();

		//Register the flags container
		Rox.register("", flags);
    	// Setup the Rollout environment key
		try {
			Rox.setup("5ffee55d0abfc1f2a9537456").get();
			// Boolean flag example
			if (flags.enableTutorial.isEnabled()) {
				System.out.println("Tutorial is ENABLED");
			} else {
				System.out.println("Tutorial is DISABLED");
			}

			String titleColor = flags.titleColors.value();
			System.out.println(String.format("Title color is %s", titleColor));
		} catch (Exception e) {
			System.err.println(e);
		}

		SpringApplication.run(PetClinicApplication.class, args);
	}

}
