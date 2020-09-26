package org.openlca.app.results.analysis.sankey.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.openlca.core.matrix.ProcessProduct;
import org.openlca.core.results.Sankey;

public class ProcessNode extends Node {

	public static String CONNECTION = "Connection";
	
	public final ProcessProduct product;
	public final ProductSystemNode parent;

	public ProcessFigure figure;
	public final List<Link> links = new ArrayList<>();

	public final double upstreamResult;
	public final double upstreamContribution;
	public final double directResult;
	public double directContribution;

	ProcessPart editPart;
	private Rectangle xyLayoutConstraints = new Rectangle(0, 0, 0, 0);

	public ProcessNode(ProductSystemNode parent, Sankey.Node node) {
		this.parent = parent;
		this.product = node.product;
		this.upstreamResult = node.total;
		this.upstreamContribution = node.share;
		this.directResult = node.direct;
	}

	public void add(Link link) {
		links.add(link);
	}

	public Rectangle getXyLayoutConstraints() {
		return xyLayoutConstraints;
	}

	public void setXyLayoutConstraints(Rectangle xyLayoutConstraints) {
		this.xyLayoutConstraints = xyLayoutConstraints;
	}

}
