package org.openlca.app.editors.epds;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.openlca.app.M;
import org.openlca.app.components.FileChooser;
import org.openlca.app.tools.openepd.CategoryDialog;
import org.openlca.app.tools.openepd.LoginPanel;
import org.openlca.app.tools.openepd.model.Api;
import org.openlca.app.tools.openepd.model.Ec3CategoryTree;
import org.openlca.app.tools.openepd.model.Ec3InternalEpd;
import org.openlca.app.util.Controls;
import org.openlca.app.util.ErrorReporter;
import org.openlca.app.util.MsgBox;
import org.openlca.app.util.UI;
import org.openlca.jsonld.Json;

import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Consumer;

class ExportDialog extends FormDialog  {

	private final Ec3InternalEpd epd;
	private Ec3CategoryTree categories;
	private LoginPanel loginPanel;

	public static void show(Ec3InternalEpd epd) {
		if (epd == null)
			return;
		new ExportDialog(epd).open();
	}

	private ExportDialog(Ec3InternalEpd epd) {
		super(UI.shell());
		this.epd = Objects.requireNonNull(epd);
		this.categories = Ec3CategoryTree.loadFromCacheFile();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Export an openEPD document");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(800, 600);
	}

	@Override
	protected void createFormContent(IManagedForm mForm) {
		var tk = mForm.getToolkit();
		var body = UI.formBody(mForm.getForm(), tk);
		loginPanel = LoginPanel.create(body, tk);

		// info section
		var infoSection = UI.section(body, tk, M.GeneralInformation);
		var comp = UI.sectionClient(infoSection, tk, 2);

		// name and unit
		Controls.set(
			UI.formText(comp, tk, M.Name),
			epd.name, name -> epd.name = name);
		Controls.set(
			UI.formText(comp, tk, "Declared unit"),
			epd.declaredUnit, s -> epd.declaredUnit = s);

		// category link
		UI.formLabel(comp, tk, M.Category);
		var categoryLink = tk.createImageHyperlink(comp, SWT.NONE);
		categoryLink.setText(epd.category == null
			? " - none -"
			: categories.pathOf(epd.category));
		Controls.onClick(categoryLink, $ -> {
			if (categories.isEmpty()) {
				var client = loginPanel.login().orElse(null);
				if (client == null)
					return;
				categories = Api.getCategoryTree(client);
				if (categories.isEmpty()) {
					MsgBox.error("No categories could be loaded",
						"No categories could be loaded from "
							+ loginPanel.credentials().queryUrl());
					return;
				}
			}
			epd.category = CategoryDialog.selectFrom(categories);
			if (epd.category != null) {
				epd.categoryId = epd.category.id;
			}
			categoryLink.setText(epd.category == null
				? " - none -"
				: categories.pathOf(epd.category));
			categoryLink.getParent().layout();
		});

		// description
		Controls.set(
			UI.formMultiText(comp, tk, "Description"),
			epd.description, s -> epd.description = s);

		// date fields
		UI.formLabel(comp, tk, "Date of issue");
		var issueDate = new DateTime(comp, SWT.DROP_DOWN);
		// UI.gridData(issueDate, false, false).widthHint = 120;
		tk.adapt(issueDate);
		date(issueDate, epd.dateOfIssue, d -> epd.dateOfIssue = d);
		UI.formLabel(comp, tk, "End of validity");
		var endDate = new DateTime(comp, SWT.DROP_DOWN);
		// UI.gridData(endDate, false, false).widthHint = 120;
		tk.adapt(endDate);
		date(endDate, epd.dateValidityEnds, d -> epd.dateValidityEnds = d);
	}

	private void date(
		DateTime widget, LocalDate initial, Consumer<LocalDate> onChange) {
		if (initial != null) {
			widget.setDate(
				initial.getYear(),
				initial.getMonthValue() - 1, // !
				initial.getDayOfMonth());
		}
		widget.addSelectionListener(Controls.onSelect($ -> {
			var newDate = LocalDate.of(
				widget.getYear(), widget.getMonth() + 1, widget.getDay());
			onChange.accept(newDate);
		}));
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, "Upload", true);
		createButton(parent, 999, "Save as file", false);
		createButton(parent, IDialogConstants.CANCEL_ID,
			IDialogConstants.CANCEL_LABEL, false);
	}

	@Override
	protected void okPressed() {


	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			okPressed();
			return;
		}
		if (buttonId == IDialogConstants.CANCEL_ID) {
			cancelPressed();
			return;
		}

		// save as file
		var json = epd.toJson();
		var file = FileChooser.forSavingFile(
			"Save openEPD document", epd.name + ".json");
		if (file == null)
			return;
		try {
			Json.write(json, file);
			super.okPressed();
		} catch (Exception e) {
			ErrorReporter.on("Failed to save openEPD document", e);
		}
	}
}