package de.lh.tool.web;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import de.lh.tool.service.web.interfaces.ReactRenderService;
import de.lh.tool.service.web.interfaces.ReactRenderService.RenderPath;
import de.lh.tool.service.web.interfaces.ReactRenderService.RenderResult;
import io.swagger.annotations.ApiOperation;
import lombok.Builder;
import lombok.Getter;

@Controller
public class HomeController {
	@Builder
	private static class GlobalWebConfig {
		@Getter
		private String basePath;
		@Getter
		private String contextPath;
	}
	
	@Autowired
	private ReactRenderService reactRenderService;

	@GetMapping("/")
	@ApiOperation(value = "Root path. Redirects to /web/login.")
	public ModelAndView root() {
		return new ModelAndView("redirect:web/login");
	}

	@GetMapping("/web/**")
	@ApiOperation(value = "Web frontend.")
	public ModelAndView home(HttpServletRequest request) {
		String contextPath = request.getContextPath();
		String basePath = contextPath + "/web";
		String fullPath = request.getRequestURI() + "?" + request.getQueryString();
		
		RenderPath renderPath = RenderPath
				.builder()
				.contextPath(contextPath)
				.basePath(basePath)
				.fullPath(fullPath)
				.build();
		
		RenderResult renderResult = reactRenderService.render(renderPath);

		GlobalWebConfig globalConfig = GlobalWebConfig.builder().basePath(basePath).contextPath(contextPath).build();

		ModelAndView mv = new ModelAndView("home");
		mv.addObject("globalConfig", new Gson().toJson(globalConfig));
		mv.addObject("contextPath", contextPath);
		mv.addObject("renderedContent", renderResult.getRenderedContent());
		mv.addObject("pageTitle", renderResult.getPageTitle());
		mv.addObject("resolverPayload", renderResult.getResolverPayload());
		return mv;
	}
}
