package de.lh.tool.service.web.interfaces;

import lombok.Builder;
import lombok.Getter;

public interface ReactRenderService {
	@Builder
	static class RenderPath {
		@Getter
		private String basePath;
		@Getter
		private String contextPath;
		@Getter
		private String fullPath;
	}
	
	@Builder
	static class RenderResult {
		@Getter
		private String renderedContent;
		@Getter
		private String pageTitle;
		@Getter
		private String resolverPayload;
	}

	RenderResult render(RenderPath renderPath);

}
