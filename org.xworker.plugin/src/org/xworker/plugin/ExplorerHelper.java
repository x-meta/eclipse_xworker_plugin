package org.xworker.plugin;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xworker.plugin.editors.ThingComposite;
import org.xworker.plugin.editors.ThingEditor;
import org.xworker.plugin.editors.ThingEditorInput;
import org.xworker.plugin.editors.ThingPackageViewer;
import org.xworker.plugin.editors.ThingPackageViewerInput;

public class ExplorerHelper {
	/**
	 * 打开一个事物进行编辑。
	 * 
	 * @param thingPath
	 */
	public void openThing(String thingPath){
		openThing(World.getInstance().getThing(thingPath));
	}
	
	/**
	 * 打开一个动作事物并定位到指定的行数。
	 * 
	 * @param actionThing
	 * @param line
	 * @return
	 */
	public ActionContext openAction(Thing actionThing, int line){
		ActionContext ac = openThing(actionThing);
		if(ac != null){
			ActionContext modelBin = (ActionContext) ac.get("currentModelContext");
			if(modelBin == null){
				return ac;
			}
			
			StyledText input = (StyledText) modelBin.get("codeInput");
            if(input != null){
                try{
                    int lIndex = line - 4;                    
                    int offset = input.getOffsetAtLine(lIndex);
                    input.setCaretOffset(offset);
                    input.setSelection(offset, offset);
                    input.showSelection();
                }catch(Exception e){
                }
            }
			return ac;
		}else{
			return null;
		}
	}
	
	/**
	 * 打开一个事物进行编辑。
	 * 
	 * @param thing
	 */
	public ActionContext openThing(Thing thing){
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		try {
			for(IEditorReference editorRef : page.getEditorReferences()){
				IEditorPart editor = editorRef.getEditor(false);
				if(editor instanceof ThingEditor){
					ThingEditor thingEditor = (ThingEditor) editor;
					Thing editorThing = thingEditor.getThing();
					if(isSameWithRoot(editorThing, thing)){
						//page.showEditor(editorRef);
						page.activate(editor);		
						
						thingEditor.selectThing(thing);
						return thingEditor.getActionContext();
					}
				}				
			}
			
			IEditorPart editor = IDE.openEditor(page, new ThingEditorInput(thing), ThingEditor.ID);
			if(editor instanceof ThingEditor){
				return ((ThingEditor) editor).getActionContext();
			}
		} catch (PartInitException e) {
			//TODO　记录打开事物编辑器错误日志
			e.printStackTrace();
		}
		
		return null;
	}
	
	private boolean isSameWithRoot(Thing one, Thing tow){
		if(one == null || tow == null){
			return false;
		}
		if(one.getRoot().getMetadata().getPath().equals(tow.getRoot().getMetadata().getPath())){
			return true;			
		}else{
			return false;
		}
	}
	
	/**
	 * 现实PackageViewer。
	 */
	public Object showPackageViewer(){
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		try {
			for(IEditorReference editorRef : page.getEditorReferences()){
				if(editorRef.getEditorInput() instanceof ThingPackageViewerInput){
					ThingPackageViewer packageViewer = (ThingPackageViewer) editorRef.getEditor(true);
					//page.showEditor(editorRef);
					if(!page.isPartVisible(packageViewer)){
						page.activate(packageViewer);
					}
					return packageViewer.getActionContext();
				}
			}
			
			ThingPackageViewer packageViewer = (ThingPackageViewer) IDE.openEditor(page, ThingPackageViewerInput.instance, ThingPackageViewer.ID);
			return packageViewer.getActionContext();
		} catch (PartInitException e) {
			//TODO　记录打开事物编辑器错误日志
			e.printStackTrace();
			
			return null;
		}
	}
	
	/**
	 * 打开一个使用事物定义的Composite。
	 * 
	 * @param thing
	 */
	public ActionContext openThingComposite(Thing thing){
		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		try {
			for(IEditorReference editorRef : page.getEditorReferences()){
				IEditorPart editor = editorRef.getEditor(false);
				if(editor instanceof ThingComposite){
					ThingComposite thingComposite = (ThingComposite) editor;
					ThingEditorInput thingEditorInput = (ThingEditorInput) thingComposite.getEditorInput();
					if(thingEditorInput.getThing().getMetadata().getPath().equals(thing.getMetadata().getPath())){
						//page.showEditor(editorRef);
						page.activate(editor);
						
						return thingComposite.getActionContext();
					}
				}				
			}
			
			ThingComposite tc = (ThingComposite) IDE.openEditor(page, new ThingEditorInput(thing), ThingComposite.ID);
			return tc.getActionContext();
		} catch (PartInitException e) {
			//TODO　记录打开事物编辑器错误日志
			e.printStackTrace();
			
			return null;
		}
	}
}
