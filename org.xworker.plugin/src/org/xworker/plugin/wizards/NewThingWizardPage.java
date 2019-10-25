package org.xworker.plugin.wizards;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.xmeta.ActionContext;
import org.xmeta.Category;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.index.CategoryIndex;
import org.xmeta.thingManagers.FileThingManager;
import org.xworker.plugin.XWorker;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (xer).
 */

public class NewThingWizardPage extends WizardPage {

	private ISelection selection;
	
	private ActionContext ac = new ActionContext();

	IContainer folder;
	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param pageName
	 */
	public NewThingWizardPage(ISelection selection) {
		super("wizardPage");
		setTitle("Create Thing");
		setDescription("This wizard creates a new Thing.");
		this.selection = selection;
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
				
		Thing descCompositeThing = World.getInstance().getThing("xworker.ide.worldExplorer.swt.dialogs.NewThingDialog/@shell/@mainComposite");
		ac.put("shell", parent.getShell());
		ac.put("parent", parent);
		Composite container = (Composite) descCompositeThing.doAction("create", ac);
		
		//取消自己的buttonCell
		((Composite) ac.get("buttonCell")).setVisible(false); 
		
		initialize();
		//dialogChanged();
		parent.layout();
		setControl(container);	
	}

	public IContainer getFolder(){
		return folder;
	}
	
	public IPath getPath(Object obj){
		try{
			Method method = obj.getClass().getDeclaredMethod("getPath", new Class<?>[0]);
			if(method != null){
				return (IPath) method.invoke(obj, new Object[0]);
			}else{
				return null;
			}
		}catch(Throwable t){
			t.printStackTrace();
			
			return null;
		}
	}
	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */

	private void initialize() {
		if (selection != null && selection.isEmpty() == false
				&& selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if(!(obj instanceof IContainer)){
				try{
					//解决source目录下创建事物的问题，这个不是最好的代码
					IPath path = getPath(obj);
					IFolder folder = ResourcesPlugin.getWorkspace().getRoot().getFolder(path);
					if(folder.getFileExtension() != null){
						obj = folder.getParent();
					}else{
						obj = folder;
					}	
				}catch(Throwable t){
					
				}
			}
			
			if(obj instanceof IContainer){
				folder = (IContainer) obj;
				Category category = getCategory((IContainer) obj, null);
				if(category != null){
					ac.getScope(0).put("index", new CategoryIndex(null, category));
					if(category.getName() != null){
						((Text) ac.get("pathText")).setText(category.getName());
					}else{
						((Text) ac.get("pathText")).setText("");
					}
									
					return;
				}else{
					this.setErrorMessage("Must under xworker project directory. Create file dml.prj or xworker.properties to make a director is xworker project directory.\n" + 
								"If use xworker.properties, please create things directory and thing is under things directory.");
										
				}
			}else{
				this.setErrorMessage("Unsupport directory " + obj.getClass());
			}
		}
		
		ac.getScope(0).put("index", null);
		((Text) ac.get("pathText")).setText("");
	}
	
	private Category getCategory(IContainer container, String path){
		IContainer c = container;
		while(c != null){
			ThingManager  thingManager = XWorker.loadThingManager(c);
			if(thingManager == null){
				if(path == null){
					path = c.getName();
				}else{
					path = c.getName() + "." + path;
				}
				c = c.getParent();
			}else{
				FileThingManager fm = (FileThingManager) thingManager;
				if(fm.hasThingsDir()){
					if(path == null || !path.startsWith("things")){
						return null;
					}
					
					if("things".equals(path)){
						path = "";
					}else{
						int index = path.indexOf(".");
						path = path.substring(index + 1, path.length());
					}
				}
				
				
				Category cat = fm.getCategory(path);
				if(cat == null){
					fm.refresh();
					fm.refreshParentCategory(path);
				}
				cat = fm.getCategory(path);
				return cat;
			}
		}
		
		return null;
	}
	
	public String getDescriptorName(){
		Text text = (Text) ac.get("descriptorText");
		return text.getText();
	}
	
	public String getCodeType(){
		Combo combo = (Combo) ac.get("codecTypeCombo");
		return combo.getText();
	}
	
	public String getThingName(){
		return ((Text) ac.get("dataObjectNameText")).getText();
	}
	
	public Index getIndex(){
		return (Index) ac.get("index");
	}
}