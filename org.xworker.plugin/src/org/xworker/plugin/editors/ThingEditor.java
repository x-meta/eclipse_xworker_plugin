package org.xworker.plugin.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPartConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xworker.plugin.Activator;
import org.xworker.plugin.XWorker;

public class ThingEditor  extends  EditorPart {
	public static final String ID  = "org.xworker.plugin.editors.ThingEditor";
	
	ThingEditorInput thingEditorInput;
	Thing thing;
	Thing currentThing;
	ActionContext editorActionContext = new ActionContext(); 
	ThingOutlinePage outlinePage = null;
	Thing scripts = null;
	boolean dirty = false;
	
	@Override
	public void doSave(IProgressMonitor monitor) {		
		scripts.doAction("save", editorActionContext);
		
		dirty = false;
		this.firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
	}

	@Override
	public void doSaveAs() {		
	}
	
	public ActionContext getActionContext(){
		return editorActionContext;
	}

	/**
	 * 选中编辑事物的一个节点事物。
	 * 
	 * @param thing
	 */
	public void selectThing(Thing thing){
		World world = World.getInstance();
		
		editorActionContext.put("thing", thing);
		Thing scriptThing = world.getThing("xworker.ide.worldExplorer.eclipse.Scripts");
		scriptThing.doAction("selectThing", editorActionContext);
		
		currentThing = thing;
	}
	
	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		super.setSite(site);
		super.setInput(input);
		
		editorActionContext.put("editorSite", site);		
		editorActionContext.put("editorInput", input);
		
		if(input instanceof ThingEditorInput){
			thingEditorInput = (ThingEditorInput) input;			
			currentThing = thingEditorInput.getThing();
			thing = currentThing.getRoot();
			this.setPartName(thing.getMetadata().getLabel());
			
			if(thingEditorInput.isFlow){
				
			}
		}else if(input instanceof IFileEditorInput){
			IFileEditorInput fInput = (IFileEditorInput) input;
			/*
			String fileName = fInput.getName();
			String thingPath = fileName.substring(0, fileName.indexOf("."));
			thingPath = getThingPath(fInput.getFile().getParent(), thingPath);
			
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		    if (window != null)    {
		        IStructuredSelection selection = (IStructuredSelection) window.getSelectionService().getSelection();
		        Object firstElement = selection.getFirstElement();
		        if (firstElement instanceof IAdaptable)   {
		            IProject project = (IProject)((IAdaptable)firstElement).getAdapter(IProject.class);
		            
		            if(project instanceof IJavaProject.) {
		            	
		            }
		            IPath path = project.getFullPath();
		            System.out.println(path);
		        }
		    }*/
		    
			String thingPath = XWorker.getThingPathByFile(fInput.getFile());
			Thing thing = World.getInstance().getThing(thingPath);
			if(thing == null){
				throw new PartInitException(getTip(input.getName()));
			}else{
				thingEditorInput = new ThingEditorInput(thing);
				currentThing = thing;
				this.thing = thing;
				this.setPartName(thing.getMetadata().getLabel());
			}
		}else{
			throw new PartInitException(input.getName());
		}
	}
	
	protected String getTip(String name) {
		StringBuilder sb = new StringBuilder("'");
		sb.append(name);
		sb.append("' can not be identified as a model.");
		sb.append("\nThe model must be placed under the model project. Create a dml.prj or.dml file under a directory to mark the directory as the root of the model project.");
		return sb.toString();
	}
	public Thing getThing(){
		return thing;
	}
	
	public String getThingPath(IContainer container, String path){
		IContainer parent = container;
		if(parent == null){
			return path;
		}
		
		if("things".equals(parent.getName())){
			//检查或初始化ThingManager
			XWorker.loadThingManager( parent.getParent());
			
			return path;
		}else{
			return getThingPath(container.getParent(), container.getName() + "." + path);
		}
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty){
		this.dirty = dirty;
		
		this.firePropertyChange(IWorkbenchPartConstants.PROP_DIRTY);
	}
	
	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			return outlinePage;
		}

		return super.getAdapter(adapter);
	}

	public Thing getRootThing(){
		return thing.getRoot();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		World world = World.getInstance();
		
		//创建编辑器
		//
		scripts = world.getThing("xworker.ide.worldExplorer.eclipse.Scripts");
		editorActionContext.put("explorerContext", Activator.explorerActionContext);
		editorActionContext.put("explorerActions", Activator.explorerActionContext.get("actions"));
		editorActionContext.put("parent", parent);
		editorActionContext.put("thingEditor", this);
		scripts.doAction("createEditor", editorActionContext);
		
		//创建Outline
		//
		outlinePage = new ThingOutlinePage(this, ThingOutlinePage.BROWSER_THING);
		
		//ExplorerContext必须有shell变量，以便打开事物等时使用
		//
        if(Activator.explorerActionContext.get("shell") == null){
        	Activator.explorerActionContext.put("shell", parent.getShell());
        }
        
		//显示编辑的事物
		Thing scripts = world.getThing("xworker.ide.worldExplorer.eclipse.Scripts");
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("thing", thing);
		scripts.doAction("setThing", editorActionContext, parameters);
		parameters.put("thing", currentThing);
		scripts.doAction("selectThing", editorActionContext, parameters);
	}

	@Override
	public void setFocus() {
	}
	
	
}
