package de.lh.tool.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import io.swagger.annotations.ApiOperation;
import lombok.Data;
import lombok.NonNull;

@Controller
public class HomeController {
	@Data
	private class GlobalWebConfig {
		@NonNull
		private String basePath;
	}

	@GetMapping("/")
	@ApiOperation(value = "Root path. Redirects to /web.")
	public String root() {
		return "redirect:web";
	}

	@GetMapping("/web/**")
	@ApiOperation(value = "Web frontend.")
	public ModelAndView home(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		String basePath = contextPath + "/web";

		GlobalWebConfig globalConfig = new GlobalWebConfig(basePath);

		ModelAndView mv = new ModelAndView("home");
		mv.addObject("globalConfig", new Gson().toJson(globalConfig));
		mv.addObject("contextPath", contextPath);
		return mv;
	}
}
