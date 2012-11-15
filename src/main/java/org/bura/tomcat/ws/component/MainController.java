package org.bura.tomcat.ws.component;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MainController {

	@RequestMapping("/")
	public String index() {
		return "index.html";
	}

}
