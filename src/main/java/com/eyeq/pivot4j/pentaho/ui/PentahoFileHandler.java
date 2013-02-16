package com.eyeq.pivot4j.pentaho.ui;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.pentaho.platform.api.engine.IPentahoSession;
import org.pentaho.platform.api.engine.PentahoAccessControlException;
import org.pentaho.platform.api.repository.ISolutionRepository;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;

import com.eyeq.pivot4j.primefaces.ui.PivotStateManager;

@ManagedBean(name = "pentahoFileHandler")
@RequestScoped
public class PentahoFileHandler {

	@ManagedProperty(value = "#{pivotStateManager}")
	private PivotStateManager pivotStateManager;

	private IPentahoSession session;

	private ISolutionRepository repository;

	@PostConstruct
	protected void initialize() {
		this.session = PentahoSessionHolder.getSession();
		this.repository = PentahoSystem.get(ISolutionRepository.class, session);
	}

	public String load() {
		return "index";
	}

	public void save() {
		FacesContext context = FacesContext.getCurrentInstance();

		Map<String, String> parameters = context.getExternalContext()
				.getRequestParameterMap();

		String fileName = parameters.get("fileName");
		String path = parameters.get("path");

		Map<String, Object> session = context.getExternalContext()
				.getSessionMap();

		// TODO implement save function

		context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
				"Report Saved",
				"Current report has been successfully saved to : " + fileName));
	}

	/**
	 * @return the pivotStateManager
	 */
	public PivotStateManager getPivotStateManager() {
		return pivotStateManager;
	}

	/**
	 * @param pivotStateManager
	 *            the pivotStateManager to set
	 */
	public void setPivotStateManager(PivotStateManager pivotStateManager) {
		this.pivotStateManager = pivotStateManager;
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
	protected ISolutionRepository getRepository() {
		return repository;
	}
}
