package org.xworker.plugin;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.ui.console.IHyperlink;
import org.xmeta.Thing;
import org.xmeta.World;

public class XWorkerClassHyperlink implements IHyperlink{
	String actionClassName;
	int line;
	ExplorerHelper helper;
	
	public XWorkerClassHyperlink(ExplorerHelper helper, String actionClassName, int line){
		this.actionClassName = actionClassName;
		this.line = line;
		this.helper = helper;
	}
	
	@Override
	public void linkActivated() {
		Thing actionThing = getActionThing(actionClassName);
		if(actionThing != null){
			helper.openAction(actionThing, line);
		}
	}

	@Override
	public void linkEntered() {
		
	}

	@Override
	public void linkExited() {
			
	}

	/**
	 * 根据动作的类名获取动作。
	 * 
	 * @param className
	 * @return
	 */
	public static Thing getActionThing(String actionClassName){
		String fileName = World.getInstance().getPath() + "/actionSources/";
		
		String packageName = "";
		String className = actionClassName;
		int lastIndex = actionClassName.lastIndexOf(".");
		if(lastIndex != -1){
			packageName = actionClassName.substring(0, lastIndex);
			className = actionClassName.substring(lastIndex + 1, actionClassName.length());
		}
		
		fileName = fileName + packageName.replace('.', '/');
		File f = new File(fileName);
		if(f.isDirectory()){
			for(File childFile : f.listFiles()){
				if(childFile.isFile() && childFile.getName().startsWith(className + ".")){
					//取第一行
					try {
						BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(childFile)));
						String line = br.readLine();
						br.close();
						String thingPath = line.substring(7, line.length() - 2);
						return World.getInstance().getThing(thingPath);
					} catch (IOException e) {						
					}
				}
			}
		}
		
		return null;
	}
}
