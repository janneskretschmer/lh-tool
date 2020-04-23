package de.lh.tool.service.web.impl;

import static java.util.Optional.ofNullable;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.eclipsesource.v8.JavaCallback;
import com.eclipsesource.v8.NodeJS;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import com.eclipsesource.v8.V8Value;

import de.lh.tool.service.web.interfaces.ReactRenderService;

@Service
public class ReactRenderServiceImpl implements ReactRenderService {

	private void executeServerFile(NodeJS nodeJS) {
		try {
			File nodeScript = new ClassPathResource("static/built/server.js").getFile();
			nodeJS.exec(nodeScript);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}
	
	private Supplier<RenderResult> attachCompletionCallback(V8 runtime) {
		AtomicReference<RenderResult> renderResult = new AtomicReference<>();
		
		JavaCallback callback = (receiver, parameters) -> {
			renderResult.set(RenderResult.builder()
					.renderedContent(parameters.getString(0))
					.pageTitle(parameters.getString(1))
					.resolverPayload(parameters.getString(2))
					.build());
			return null;
		};
		runtime.registerJavaMethod(callback, "renderDoneCallback");
		
		return renderResult::get;
	}
	
	private V8Object attachGlobalConfig(V8 runtime, RenderPath renderPath, String accessToken) {
		V8Object globalConfigV8 = new V8Object(runtime);
		runtime.add("__GLOBAL_CONFIG__", globalConfigV8);
		globalConfigV8.add("basePath", renderPath.getBasePath());
		globalConfigV8.add("contextPath", renderPath.getContextPath());
		globalConfigV8.add("fullPath", renderPath.getFullPath());
		globalConfigV8.add("apiPathPrefix", renderPath.getApiPathPrefix());
		globalConfigV8.add("accessToken", accessToken);
		return globalConfigV8;
	}
	
	private void waitForCompletion(NodeJS nodeJS) {
		while (nodeJS.isRunning()) {
			nodeJS.handleMessage();
		}
	}

	@Override
	public RenderResult render(RenderPath renderPath, String accessToken) {
		Supplier<RenderResult>  resultSupplier = null;

		NodeJS nodeJS = null;
		V8Object globalConfigV8 = null;

		try {

			nodeJS = NodeJS.createNodeJS();
			V8 runtime = nodeJS.getRuntime();

			resultSupplier = attachCompletionCallback(runtime);
			executeServerFile(nodeJS);

			globalConfigV8 = attachGlobalConfig(runtime, renderPath, accessToken);

			waitForCompletion(nodeJS);

		} finally {
			ofNullable(globalConfigV8).ifPresent(V8Value::release);
			ofNullable(nodeJS).ifPresent(NodeJS::release);
		}
		
		return ofNullable(resultSupplier)
				.map(Supplier::get)
				.orElseThrow(() -> new IllegalStateException("render result is not populated after rendering"));
	}

}
