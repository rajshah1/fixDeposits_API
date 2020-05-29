package systems.rajshah.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class fdprojectRestController {

	@GetMapping("/")
	public String hello() {
		return "Hello";
	}
}
