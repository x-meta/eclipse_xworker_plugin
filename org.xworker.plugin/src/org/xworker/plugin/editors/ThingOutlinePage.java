package org.xworker.plugin.editors;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xmeta.util.ActionContainer;

public class ThingOutlinePage implements IContentOutlinePage{
	public static final int BROWSER_THING = 1;
	public static final int BROWSER_COMPOSITE = 2;
	Object thingEditor = null;
	Control control = null;
	int browserType = BROWSER_THING;
	
	public ThingOutlinePage(Object thingEditor, int browserType){
		this.thingEditor = thingEditor;
		this.browserType = browserType;
	}

	@Override
	public void createControl(Composite parent) {
		///parent.setLayout(new FillLayout());
		
		World world = World.getInstance();
		Thing outlineBrowser;
		if(browserType == BROWSER_THING){
			outlineBrowser = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@outlineBrowser");
		}else{
			outlineBrowser = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@OutlineComposite");
		}
		//Thing outlineBrowser = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@OutlineComposite");
		//Thing outlineBrowser = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@outlineBrowser");
		//Thing outlineThing = world.getThing("xworker.ide.worldExplorer.eclipse.Outline/@rightSash");
		//Thing treeThing = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@outlineTree");
		//Thing childThing = world.getThing("xworker.ide.worldExplorer.swt.dataExplorerParts.ThingEditor/@propertiesComposite");
		
		ActionContext ac = null;
		if(thingEditor instanceof ThingEditor){
			ac = ((ThingEditor) thingEditor).editorActionContext;
		}else if(thingEditor instanceof ThingComposite){
			ac = ((ThingComposite) thingEditor).actionContext;
		}
		if(ac != null){
			ac.put("parent", parent);
			control = (Control) outlineBrowser.doAction("create", ac);
		}
		
		
		//打开时刷新outline的内容
		Thing currentThing = ac.getObject("currentThing");
		if(currentThing != null){
			((ActionContainer) ac.getObject("actions")).doAction("initOutlineBrowser", ac, "descriptor", currentThing.getDescriptor(), "thing", currentThing);
		}
		//thingEditor.editorActionContext.put("parent", thingEditor.editorActionContext.get("outlineComposite"));
		//treeThing.doAction("create", thingEditor.editorActionContext);
		//OutlineTreeDragAndDrop.attach(outlineTree, editorActionContext);
		        
		//thingEditor.editorActionContext.put("parent", thingEditor.editorActionContext.get("forAddchildComposite"));
		//childThing.doAction("create", thingEditor.editorActionContext);
		
		//JavaContentOutlinePage jc = new ContentOutlinePage();
		

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
