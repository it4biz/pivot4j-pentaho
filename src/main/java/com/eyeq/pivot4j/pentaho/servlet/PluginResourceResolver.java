package com.eyeq.pivot4j.pentaho.servlet;

import java.net.URL;
import java.util.List;

import org.apache.myfaces.view.facelets.impl.DefaultResourceResolver;
import org.pentaho.platform.api.engine.IPluginResourceLoader;
import org.pentaho.platform.engine.core.system.PentahoSystem;

public class PluginResourceResolver extends DefaultResourceResolver {

	public static final String RESOURCE_PREFIX = "./webapp";

	private IPluginResourceLoader resourceLoader;

	public PluginResourceResolver() {
		this.resourceLoader = PentahoSystem.get(IPluginResourceLoader.class,
				null);
	}

	/**
	 * @see org.apache.myfaces.view.facelets.impl.DefaultResourceResolver#resolveUrl(java.lang.String)
	 */
	@Override
	public URL resolveUrl(String path) {
		List<URL> resources = resourceLoader.findResources(getClass()
				.getClassLoader(), RESOURCE_PREFIX + path);
		if (resources.isEmpty()) {
			return super.resolveUrl(path);
		} else {
			return resources.get(0);
		}
	}
}
