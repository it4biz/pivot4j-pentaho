package com.eyeq.pivot4j.pentaho.content;

import java.io.Serializable;

import com.eyeq.pivot4j.PivotModel;
import com.eyeq.pivot4j.primefaces.datasource.ConnectionMetadata;
import com.eyeq.pivot4j.ui.PivotRenderer;

public class Pivot4JReport implements Serializable {

	private static final long serialVersionUID = -3265594129256706069L;

	private ConnectionMetadata connectionInfo;

	private Serializable modelState;

	private Serializable rendererState;

	private String author;

	private String description;

	/**
	 * @param modelState
	 * @param rendererState
	 */
	private Pivot4JReport(Serializable modelState, Serializable rendererState) {
		this.modelState = modelState;
		this.rendererState = rendererState;
	}

	/**
	 * @param model
	 * @param renderer
	 * @return
	 */
	public static Pivot4JReport saave(PivotModel model, PivotRenderer renderer) {
		return new Pivot4JReport(model.bookmarkState(),
				renderer.bookmarkState());
	}

	/**
	 * @param model
	 * @param renderer
	 */
	public void load(PivotModel model, PivotRenderer renderer) {
		model.restoreState(modelState);
		renderer.restoreState(rendererState);
	}
}
