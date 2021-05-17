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
package org.springframework.samples.petclinic.vet;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/* Import CloudBees Feature Flags packages */
import org.springframework.samples.petclinic.flags.FlagsController;

/**
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @author Ken Krebs
 * @author Arjen Poutsma
 */
@Controller
class VetController {

	private static final Logger logger = LoggerFactory.getLogger(VetController.class);

	// Initialize Flags container class
	@Autowired
	private FlagsController flags;

	private final VetRepository vets;

	public VetController(VetRepository clinicService) {
		this.vets = clinicService;
	}

	@GetMapping("/vets.html")
	public String showVetList(Map<String, Object> model) {
		// Here we are returning an object of type 'Vets' rather than a collection of
		// Vet
		// objects so it is simpler for Object-Xml mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vets.findAll());
		model.put("vets", vets);

		// Boolean flag example
		if (flags.enableTutorial.isEnabled()) {
			logger.info("Tutorial is ENABLED");
		} else {
			logger.info("Tutorial is DISABLED");
		}
		String titleColor = flags.titleColors.value();
		logger.info("Title color is {}", titleColor);
		return "vets/vetList";
	}

	@GetMapping({ "/vets" })
	public @ResponseBody Vets showResourcesVetList() {
		// Here we are returning an object of type 'Vets' rather than a collection of
		// Vet
		// objects so it is simpler for JSon/Object mapping
		Vets vets = new Vets();
		vets.getVetList().addAll(this.vets.findAll());
		return vets;
	}

}
