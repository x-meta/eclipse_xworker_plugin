package org.xworker.plugin.wizards;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.xmeta.Category;
import org.xmeta.Index;
import org.xmeta.Thing;
import org.xmeta.ThingCoderException;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.codes.XerCoder;
import org.xmeta.index.ThingManagerIndex;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "xer". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class NewThingWizard extends Wizard implements INewWizard {
	private NewThingWizardPage page;
	private ISelection selection;

	/**
	 * Constructor for NewThingWizard.
	 */
	public NewThingWizard() {
		super();
		setNeedsProgressMonitor(true);
	}
	
	/**
	 * Adding the page to the wizard.
	 */

	public void addPages() {
		page = new NewThingWizardPage(selection);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	public boolean performFinish() {		
		final String thingName = page.getThingName();
		final Index index = page.getIndex();
		final String descriptorName = page.getDescriptorName();
		
		if(thingName == null || "".equals(thingName.trim())){
			MessageDialog.openWarning(getShell(), "Create Thing", "Thing name cann't be null.");
			return false;
		}
		
		if(index == null){
			MessageDialog.openWarning(getShell(), "Create Thing", "Must select a thing category.");
			return false;
		}
		
		String thingPath = null;
		ThingManager manager;
		if(index instanceof ThingManagerIndex){
			thingPath = thingName;
			manager = (ThingManager) ((ThingManagerIndex) index).getIndexObject();
		}else{
			String categoryPath =  ((Category) index.getIndexObject()).getName();
			if(categoryPath != null && !"".equals(categoryPath)){
				thingPath = categoryPath + "." + thingName;
			}else{
				thingPath = thingName;
			}
			
			manager = ((Category) index.getIndexObject()).getThingManager();
		}
		
		World world = World.getInstance();
		if(world.getThing(thingPath) != null){
			MessageDialog.openWarning(getShell(), "Create Thing", "Thing already exists.");
			return false;
		}
		
		//创建事物
		Thing thing = null;
		if(descriptorName == null || "".equals(descriptorName)){
			thing = new Thing("xworker.lang.MetaDescriptor3");
		}else{
			thing = new Thing(descriptorName);
		}
		thing.put("name", thingName);
		String codeType = page.getCodeType();
		if(codeType == null){
			codeType = "dml_property";
		}
		thing.getMetadata().setCoderType(codeType);
		thing.saveAs(manager.getName(), thingPath);
				
		//IStructuredSelection ssel = (IStructuredSelection) selection;
		//Object obj = ssel.getFirstElement();
		IContainer folder = page.getFolder();
		if(folder != null){
			try {
				folder.refreshLocal(1, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*
		if(obj instanceof IContainer){
			try {
				((IContainer) obj).refreshLocal(1, null);
			} catch (CoreException e) {
			}
		}*/
		return true;
	}
	
	/**
	 * The worker method. It will find the container, create the
	 * file if missing or just replace its contents, and open
	 * the editor on the newly created file.
	 */

	private void doFinish(
		String containerName,
		String fileName,
		String descriptorName,
		IProgressMonitor monitor)
		throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));

		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try {
			InputStream stream = openContentStream(fileName, descriptorName);
			if (file.exists()) {
				file.setContents(stream, true, true, monitor);
			} else {
				file.create(stream, true, monitor);
			}
			stream.close();
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page =
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		monitor.worked(1);
	}
	
	/**
	 * We will initialize file contents with a sample text.
	 */

	private InputStream openContentStream(String fileName, String descriptorName) {
		String thingName = fileName.substring(0, fileName.lastIndexOf("."));
		Thing thing = new Thing(thingName, null, descriptorName, false);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			XerCoder.encode(thing, out, new HashMap<Thing, String>());
		} catch (IOException e) {
			throw new ThingCoderException(e);
		}
		return new ByteArrayInputStream(out.toByteArray());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status =
			new Status(IStatus.ERROR, "org.xworker.plugin", IStatus.OK, message, null);
		throw new CoreException(status);
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}