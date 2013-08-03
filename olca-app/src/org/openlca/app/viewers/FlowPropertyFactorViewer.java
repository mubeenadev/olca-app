package org.openlca.app.viewers;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.openlca.app.Messages;
import org.openlca.app.components.ObjectDialog;
import org.openlca.app.resources.ImageType;
import org.openlca.app.util.Numbers;
import org.openlca.app.util.UI;
import org.openlca.app.viewers.AbstractTableViewer.IModelChangedListener.Type;
import org.openlca.app.viewers.modify.CheckBoxCellModifier;
import org.openlca.app.viewers.modify.TextCellModifier;
import org.openlca.core.database.FlowPropertyDao;
import org.openlca.core.database.IDatabase;
import org.openlca.core.model.Flow;
import org.openlca.core.model.FlowPropertyFactor;
import org.openlca.core.model.ModelType;
import org.openlca.core.model.descriptors.BaseDescriptor;
import org.openlca.core.model.descriptors.FlowPropertyDescriptor;

import com.google.common.base.Objects;

public class FlowPropertyFactorViewer extends
		AbstractTableViewer<FlowPropertyFactor> {

	private interface LABEL {
		String NAME = Messages.Common_Name;
		String CONVERSION_FACTOR = Messages.Flows_ConversionFactor;
		String REFERENCE_UNIT = Messages.Common_ReferenceUnit;
		String IS_REFERENCE = Messages.Flows_IsReference;
	}

	private static final String[] COLUMN_HEADERS = { LABEL.NAME,
			LABEL.CONVERSION_FACTOR, LABEL.REFERENCE_UNIT, LABEL.IS_REFERENCE };

	private Flow flow;
	private final FlowPropertyDao flowPropertyDao;

	public FlowPropertyFactorViewer(Composite parent, IDatabase database) {
		super(parent);
		getCellModifySupport().support(LABEL.CONVERSION_FACTOR,
				new ConversionFactorModifier());
		getCellModifySupport().support(LABEL.IS_REFERENCE,
				new ReferenceModifier());
		flowPropertyDao = new FlowPropertyDao(database);
	}

	public void setInput(Flow flow) {
		this.flow = flow;
		setInput(flow.getFlowPropertyFactors().toArray(
				new FlowPropertyFactor[flow.getFlowPropertyFactors().size()]));
	}

	@Override
	protected IBaseLabelProvider getLabelProvider() {
		return new FactorLabelProvider();
	}

	@Override
	protected String[] getColumnHeaders() {
		return COLUMN_HEADERS;
	}

	@OnCreate
	protected void onCreate() {
		BaseDescriptor descriptor = ObjectDialog
				.select(ModelType.FLOW_PROPERTY);
		if (descriptor != null)
			add(descriptor);
	}

	private void add(BaseDescriptor descriptor) {
		FlowPropertyFactor factor = new FlowPropertyFactor();
		factor.setFlowProperty(flowPropertyDao.getForId(descriptor.getId()));
		fireModelChanged(Type.CREATE, factor);
		setInput(flow.getFlowPropertyFactors().toArray(
				new FlowPropertyFactor[flow.getFlowPropertyFactors().size()]));
	}

	@OnRemove
	protected void onRemove() {
		for (FlowPropertyFactor factor : getAllSelected()) {
			flow.getFlowPropertyFactors().remove(factor);
			fireModelChanged(Type.REMOVE, factor);
		}
		setInput(flow.getFlowPropertyFactors().toArray(
				new FlowPropertyFactor[flow.getFlowPropertyFactors().size()]));
	}

	@OnDrop
	protected void onDrop(FlowPropertyDescriptor descriptor) {
		if (descriptor != null)
			add(descriptor);
	}

	private class FactorLabelProvider implements ITableLabelProvider,
			ITableFontProvider {

		private Font boldFont;

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
			if (boldFont != null && !boldFont.isDisposed())
				boldFont.dispose();
		}

		@Override
		public Image getColumnImage(Object element, int column) {
			if (column == 0)
				return ImageType.FLOW_PROPERTY_ICON.get();
			if (column != 3)
				return null;
			FlowPropertyFactor refFactor = flow != null ? flow
					.getReferenceFactor() : null;
			if (refFactor != null && refFactor.equals(element))
				return ImageType.CHECK_TRUE.get();
			return ImageType.CHECK_FALSE.get();
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			if (!(element instanceof FlowPropertyFactor))
				return null;
			FlowPropertyFactor factor = (FlowPropertyFactor) element;
			switch (columnIndex) {
			case 0:
				return factor.getFlowProperty().getName();
			case 1:
				return Numbers.format(factor.getConversionFactor());
			case 2:
				return factor.getFlowProperty().getUnitGroup()
						.getReferenceUnit().getName();
			default:
				return null;
			}
		}

		@Override
		public Font getFont(Object element, int columnIndex) {
			FlowPropertyFactor refFactor = flow != null ? flow
					.getReferenceFactor() : null;
			if (refFactor != null && refFactor.equals(element)) {
				if (boldFont == null)
					boldFont = UI.boldFont(getViewer().getTable());
				return boldFont;
			}
			return null;
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	}

	private class ConversionFactorModifier extends
			TextCellModifier<FlowPropertyFactor> {

		@Override
		protected String getText(FlowPropertyFactor element) {
			return Double.toString(element.getConversionFactor());
		}

		@Override
		protected void setText(FlowPropertyFactor element, String text) {
			try {
				element.setConversionFactor(Double.parseDouble(text));
				fireModelChanged(Type.CHANGE, element);
			} catch (NumberFormatException e) {

			}
		}
	}

	private class ReferenceModifier extends
			CheckBoxCellModifier<FlowPropertyFactor> {

		@Override
		protected boolean isChecked(FlowPropertyFactor element) {
			return flow != null
					&& Objects.equal(flow.getReferenceFactor(), element);
		}

		@Override
		protected void setChecked(FlowPropertyFactor element, boolean value) {
			if (value) {
				flow.setReferenceFlowProperty(element.getFlowProperty());
				fireModelChanged(Type.CHANGE, element);
			}
		}

	}

}
