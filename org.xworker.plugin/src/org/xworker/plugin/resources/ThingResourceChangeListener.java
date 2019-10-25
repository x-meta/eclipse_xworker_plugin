package org.xworker.plugin.resources;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.xmeta.World;

public class ThingResourceChangeListener implements IResourceChangeListener {

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResource res = event.getResource();
		switch (event.getType()) {
		case IResourceChangeEvent.PRE_CLOSE:
			System.out.print("Project ");
			System.out.print(res.getFullPath());
			System.out.println(" is about to close.");
			break;
		case IResourceChangeEvent.PRE_DELETE:
			System.out.print("Project ");
			System.out.print(res.getFullPath());
			System.out.println(" is about to be deleted.");
			break;
		case IResourceChangeEvent.POST_CHANGE:
			initThingPaths();
			break;
		case IResourceChangeEvent.PRE_BUILD:
			System.out.println("Build about to run.");
			break;
		case IResourceChangeEvent.POST_BUILD:
			System.out.println("Build complete.");
			break;
		}
	}

	public static void initThingPaths(){
		if(World.getInstance().getClassLoader() == null){
			//World未初始化
			return;
		}
		
		/*
		for(IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()){
	    	IFolder thingsFolder = project.getFolder("things");
	    	if(thingsFolder != null){
	    		//String path = thingsFolder.getLocationURI().toString();
	    		File file = new File(thingsFolder.getLocationURI());
	    		World.getInstance().addOrUpdateOuterProject(file.getAbsolutePath());
	    	}
	    }
	    */
	}
}
