package org.xworker.plugin.editors;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;
import org.xmeta.Thing;

public class ThingAttributeInput implements IStorageEditorInput {
	Thing thing = null;
	String name;
	boolean isFlow = false;
	IDocument document;
	IStorage storage;
	
	public ThingAttributeInput(Thing thing, String name){
		this.thing = thing;
		this.name = name;
		
		String code = thing.getString("name");
		if(code != null){
			document = new Document(code);
		}else{
			document = new Document();
		}
		storage = new ThingAttributeStorage(thing, name);
		
		document.addDocumentListener(new IDocumentListener(){
			@Override
			public void documentAboutToBeChanged(DocumentEvent event) {
			}

			@Override
			public void documentChanged(DocumentEvent event) {
				// TODO Auto-generated method stub
				
			}
			
		});
	}
	
	public Thing getThing(){
		return thing;
	}
	
	public IDocument getDocument(){
		return document;
	}
	
	@Override
	public boolean exists() {
		return  false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {		
		return name;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return thing.getMetadata().getPath() + "/@" + name;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public IStorage getStorage() throws CoreException {
		return storage;
	}
	
}
