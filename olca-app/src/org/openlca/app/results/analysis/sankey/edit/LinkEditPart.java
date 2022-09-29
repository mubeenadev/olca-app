package org.openlca.app.results.analysis.sankey.edit;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Objects;

import org.eclipse.draw2d.*;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.openlca.app.results.analysis.sankey.model.SankeyLink;
import org.openlca.app.tools.graphics.figures.CurvedConnection;

import static org.eclipse.swt.SWT.ON;
import static org.openlca.app.results.analysis.sankey.SankeyConfig.ROUTER_CURVE;

public class LinkEditPart extends AbstractConnectionEditPart
		implements PropertyChangeListener {

	public void activate() {
		super.activate();
		getModel().addPropertyChangeListener(this);
	}

	public void activateFigure() {
		super.activateFigure();
		/*
		 * Once the figure has been added to the ConnectionLayer, start
		 * listening for its router to change.
		 */
		getFigure().addPropertyChangeListener(
				Connection.PROPERTY_CONNECTION_ROUTER, this);
	}

	public void deactivate() {
		getModel().removePropertyChangeListener(this);
		super.deactivate();
	}

	public void deactivateFigure() {
		getFigure().removePropertyChangeListener(
				Connection.PROPERTY_CONNECTION_ROUTER, this);
		super.deactivateFigure();
	}

	@Override
	protected IFigure createFigure() {
		var config = getModel().getSourceNode().getDiagram().getConfig();
		var color = ColorConstants.red;
		var orientation = getModel().getSourceNode().getDiagram().orientation;

		var connection = Objects.equals(config.connectionRouter(), ROUTER_CURVE)
				? new CurvedConnection(orientation) {
			@Override
			public void paint(Graphics g) {
				setAntialias(ON);
				setForegroundColor(color);
				setAlpha(180);
				super.paint(g);
			}
		}
				: new PolylineConnection() {
			@Override
			public void paint(Graphics g) {
				setAntialias(ON);
				setForegroundColor(color);
				setAlpha(180);
				super.paint(g);
			}
		};

		if (connection instanceof CurvedConnection con)
			con.setOffset(100);
		connection.setLineWidth(getModel().getLineWidth());
		return connection;
	}

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new ConnectionEndpointEditPolicy());
	}

	public SankeyLink getModel() {
		return (SankeyLink) super.getModel();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {}

}