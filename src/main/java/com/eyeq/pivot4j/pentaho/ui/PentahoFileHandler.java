package com.eyeq.pivot4j.pentaho.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.olap4j.OlapDataSource;
import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.PentahoAccessControlException;
import org.pentaho.platform.api.repository2.unified.IRepositoryFileData;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.api.repository2.unified.RepositoryFile;
import org.pentaho.platform.api.repository2.unified.data.simple.SimpleRepositoryFileData;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.repository2.unified.RepositoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.impl.PivotModelImpl;
import com.eyeq.pivot4j.pentaho.datasource.PentahoDataSourceManager;
import com.eyeq.pivot4j.primefaces.datasource.ConnectionMetadata;
import com.eyeq.pivot4j.primefaces.state.ViewState;
import com.eyeq.pivot4j.primefaces.state.ViewStateHolder;

@ManagedBean(name = "pentahoFileHandler")
@SessionScoped
public class PentahoFileHandler {

	@ManagedProperty(value = "#{viewStateHolder}")
	private ViewStateHolder viewStateHolder;

	@ManagedProperty(value = "#{dataSourceManager}")
	private PentahoDataSourceManager dataSourceManager;

	private IPentahoSession session;

	private IUnifiedRepository repository;

	@PostConstruct
	protected void initialize() {
		this.session = PentahoSessionHolder.getSession();
		this.repository = PentahoSystem.get(IUnifiedRepository.class, session);
	}

	/**
	 * @param viewId
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public ViewState load(String viewId, RepositoryFile file)
			throws IOException, ClassNotFoundException {
		SimpleRepositoryFileData data = repository.getDataForRead(file.getId(),
				SimpleRepositoryFileData.class);

		ObjectInputStream in = new ObjectInputStream(data.getStream());

		ConnectionMetadata connectionInfo = (ConnectionMetadata) in
				.readObject();

		OlapDataSource dataSource = dataSourceManager
				.createDataSource(connectionInfo);

		PivotModel model = new PivotModelImpl(dataSource);
		model.restoreState((Serializable) in.readObject());

		ViewState state = new ViewState(viewId, connectionInfo, model);
		state.setRendererState((Serializable) in.readObject());

		in.close();

		return state;
	}

	/**
	 * @throws PentahoAccessControlException
	 * @throws IOException
	 */
	public void save() throws PentahoAccessControlException, IOException {
		FacesContext context = FacesContext.getCurrentInstance();

		Map<String, String> parameters = context.getExternalContext()
				.getRequestParameterMap();
		String viewId = parameters.get("viewId");

		String fileName = parameters.get("fileName");
		if (!fileName.endsWith(".pivot4j")) {
			fileName += ".pivot4j";
		}

		String path = parameters.get("path");

		if (path.endsWith(fileName)) {
			path = path.substring(0, path.length() - fileName.length() - 1);
		}

		if (!path.endsWith(RepositoryFile.SEPARATOR)) {
			path += RepositoryFile.SEPARATOR;
		}

		boolean overwrite = Boolean.parseBoolean(parameters.get("overwrite"));

		save(viewId, path, fileName, overwrite);
	}

	/**
	 * @param viewId
	 * @param path
	 * @param fileName
	 * @param overwrite
	 * @throws PentahoAccessControlException
	 * @throws IOException
	 */
	public void save(String viewId, String path, String fileName,
			boolean overwrite) throws PentahoAccessControlException,
			IOException {
		Logger logger = LoggerFactory.getLogger(getClass());
		if (logger.isInfoEnabled()) {
			logger.info("Saving report content to repository :");
			logger.info("	- viewId : " + viewId);
			logger.info("	- path : " + path);
			logger.info("	- fileName : " + fileName);
			logger.info("	- overwrite : " + overwrite);
		}

		ViewState state = viewStateHolder.getState(viewId);

		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		ObjectOutputStream out = new ObjectOutputStream(bout);

		out.writeObject(state.getConnectionInfo());
		out.writeObject(state.getModel().bookmarkState());
		out.writeObject(state.getRendererState());

		out.flush();
		out.close();

		String filePath = path + fileName;

		IRepositoryFileData data = new SimpleRepositoryFileData(
				new ByteArrayInputStream(bout.toByteArray()), "UTF-8",
				"application/pivot4j.pentaho");

		RepositoryFile file = repository.getFile(filePath);

		if (file == null) {
			RepositoryUtils utils = new RepositoryUtils(repository);
			utils.saveFile(filePath, data, true, overwrite, false, false, null);
		} else {
			repository.updateFile(file, data, null);
		}

		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Report Saved",
				"Current report has been successfully saved to : " + fileName));
	}

	/**
	 * @return the viewStateHolder
	 */
	public ViewStateHolder getViewStateHolder() {
		return viewStateHolder;
	}

	/**
	 * @param viewStateHolder
	 *            the viewStateHolder to set
	 */
	public void setViewStateHolder(ViewStateHolder viewStateHolder) {
		this.viewStateHolder = viewStateHolder;
	}

	/**
	 * @return the dataSourceManager
	 */
	public PentahoDataSourceManager getDataSourceManager() {
		return dataSourceManager;
	}

	/**
	 * @param dataSourceManager
	 *            the dataSourceManager to set
	 */
	public void setDataSourceManager(PentahoDataSourceManager dataSourceManager) {
		this.dataSourceManager = dataSourceManager;
	}

	/**
	 * @return the session
	 */
	protected IPentahoSession getSession() {
		return session;
	}

	/**
	 * @return the repository
	 */
	protected IUnifiedRepository getRepository() {
		return repository;
	}
}
