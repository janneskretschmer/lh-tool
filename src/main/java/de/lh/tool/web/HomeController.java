package de.lh.tool.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import io.swagger.annotations.ApiOperation;

@Controller
public class HomeController {
	@GetMapping("/")
	@ApiOperation(value = "Root path. Redirects to /web.")
	public String root() {
		return "redirect:web";
	}

	@GetMapping("/web/**")
	@ApiOperation(value = "Web frontend.")
	public ModelAndView home() {
		ModelAndView mv = new ModelAndView("home");
		mv.addObject("js", "document.write('JavaScript works!');");
		return mv;
	}
}
