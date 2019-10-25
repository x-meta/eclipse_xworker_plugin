package org.xworker.plugin.editors;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.xmeta.Thing;
import org.xmeta.World;

public class ThingEditorInput implements IEditorInput{
	Thing thing = null;
	boolean isFlow = false;
	
	public ThingEditorInput(Thing thing){
		this.thing = thing;
	}
	
	public ThingEditorInput(Thing thing, boolean isFlow){
		this.thing = thing;
		this.isFlow = isFlow;
	}
	
	public Thing getThing(){
		return thing;
	}
	
	@Override
	public boolean exists() {
		return  thing != null && World.getInstance().get(thing.getMetadata().getPath()) != null;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {		
		return thing.getMetadata().getName();
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return thing.getMetadata().getPath();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

}
