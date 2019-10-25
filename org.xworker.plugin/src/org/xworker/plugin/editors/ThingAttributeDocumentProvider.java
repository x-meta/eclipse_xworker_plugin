package org.xworker.plugin.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.javaeditor.CompilationUnitEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.IElementStateListener;

public class ThingAttributeDocumentProvider implements IDocumentProvider{
	public ThingAttributeDocumentProvider(){
		System.out.println("ThingAttributeDocumentProvider inited");
		CompilationUnitEditor javaEditor = new CompilationUnitEditor();
	}
	
	@Override
	public void aboutToChange(Object element) {
		System.out.println("ThingAttributeDocumentProvider aboutToChange");
	}

	@Override
	public void addElementStateListener(IElementStateListener listener) {
		System.out.println("ThingAttributeDocumentProvider addElementStateListener");
	}

	@Override
	public boolean canSaveDocument(Object element) {
		System.out.println("ThingAttributeDocumentProvider canSaveDocument");
		return true;
	}

	@Override
	public void changed(Object element) {
		System.out.println("ThingAttributeDocumentProvider changed");
	}

	@Override
	public void connect(Object element) throws CoreException {
		System.out.println("ThingAttributeDocumentProvider connect");
		
	}

	@Override
	public void disconnect(Object element) {
		System.out.println("ThingAttributeDocumentProvider disconnect");
	}

	@Override
	public IAnnotationModel getAnnotationModel(Object element) {
		System.out.println("ThingAttributeDocumentProvider getAnnotationModel");
		return null;
	}

	@Override
	public IDocument getDocument(Object element) {
		System.out.println("ThingAttributeDocumentProvider getDocument");
		ThingAttributeInput input = (ThingAttributeInput) element;
		return input.getDocument();
	}

	@Override
	public long getModificationStamp(Object element) {
		System.out.println("ThingAttributeDocumentProvider getModificationStamp");
		return 0;
	}

	@Override
	public long getSynchronizationStamp(Object element) {
		System.out.println("ThingAttributeDocumentProvider getSynchronizationStamp");
		return 0;
	}

	@Override
	public boolean isDeleted(Object element) {
		System.out.println("ThingAttributeDocumentProvider isDeleted");
		return false;
	}

	@Override
	public boolean mustSaveDocument(Object element) {
		System.out.println("ThingAttributeDocumentProvider mustSaveDocument");
		return false;
	}

	@Override
	public void removeElementStateListener(IElementStateListener listener) {
		System.out.println("ThingAttributeDocumentProvider removeElementStateListener");
		
	}

	@Override
	public void resetDocument(Object element) throws CoreException {
		System.out.println("ThingAttributeDocumentProvider resetDocument");
	}

	@Override
	public void saveDocument(IProgressMonitor monitor, Object element,
			IDocument document, boolean overwrite) throws CoreException {
		System.out.println("ThingAttributeDocumentProvider saveDocument");
	}

}
