package org.xworker.plugin.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;

public class ThingPackageViewerOutlinePage implements IContentOutlinePage{
	ThingPackageViewer pacakgeViewer = null;
	Control control = null;
	
	public ThingPackageViewerOutlinePage(ThingPackageViewer pacakgeViewer){
		this.pacakgeViewer = pacakgeViewer;
	}

	@Override
	public void createControl(Composite parent) {
		World world = World.getInstance();
		Thing outlineBrowser = world.getThing("xworker.ide.worldExplorer.swt.util.PackageViewer/@packageViewerHelpBrowser");
		
		ActionContext actionContext = pacakgeViewer.getActionContext();
		actionContext.put("parent", parent);
		control = (Control) outlineBrowser.doAction("create", actionContext);
		
		Thing globalConfig = world.getThing("_local.xworker.config.GlobalConfig");    
	    String webUrl = globalConfig.getString("webUrl");
	    ((Browser) control).setUrl(webUrl + "do?sc=xworker.ide.worldExplorer.swt.http.ThingDoc/@desc&thing=xworker.ide.worldExplorer.swt.help.ProjectHelps/@World");
	    
	}

	@Override
	public void dispose() {
		control.dispose();
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void setActionBars(IActionBars actionBars) {
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
	}

	@Override
	public ISelection getSelection() {
		return null;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
	}

	@Override
	public void setSelection(ISelection selection) {
	}

}

