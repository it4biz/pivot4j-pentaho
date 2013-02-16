package com.eyeq.pivot4j.pentaho.content;

import java.io.InputStream;

import org.pentaho.platform.api.engine.IFileInfo;
import org.pentaho.platform.api.engine.ISolutionFile;
import org.pentaho.platform.api.engine.SolutionFileMetaAdapter;
import org.pentaho.platform.engine.core.solution.FileInfo;

public class PluginFileMetaProvider extends SolutionFileMetaAdapter {

	/**
	 * @see org.pentaho.platform.api.engine.ISolutionFileMetaProvider#getFileInfo(org.pentaho.platform.api.engine.ISolutionFile,
	 *      java.io.InputStream)
	 */
	@Override
	public IFileInfo getFileInfo(ISolutionFile file, InputStream in) {
		String title = file.getFileName();

		if (title != null && title.endsWith(".p4j")) {
			title = title.substring(0, title.indexOf(".p4j"));
		}

		IFileInfo info = new FileInfo();

		info.setAuthor("");
		info.setDescription("");
		info.setTitle(title);
		info.setIcon("logo16.png");

		return info;
	}
}