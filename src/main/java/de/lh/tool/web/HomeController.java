package de.lh.tool.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	@GetMapping("/")
	public String root() {
		return "redirect:web";
	}

	@GetMapping("/web/**")
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView("home");
		mv.addObject("js", "document.write('JavaScript works!');");
		return mv;
	}
}
