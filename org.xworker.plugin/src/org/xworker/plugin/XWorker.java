package org.xworker.plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.util.Properties;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.xmeta.ActionContext;
import org.xmeta.ThingCoder;
import org.xmeta.ThingManager;
import org.xmeta.World;
import org.xmeta.thingManagers.FileThingManager;

public class XWorker {
	static boolean inited = false;
	
	public static void init() {
		inited = false;
		if (inited) {
			return;
		}

		// 得到默认控制台管理器
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();

		// 得到所有的控制台实例
		boolean haveConsole = false;
		IConsole[] existing = manager.getConsoles();
		for (IConsole console : existing) {
			if (console == Activator.ioConsole) {
				haveConsole = true;
				break;
			}
		}

		if (!haveConsole) {
			manager.addConsoles(new IOConsole[] { Activator.ioConsole });
		}

		OutputStream ioOut = Activator.ioConsole.newOutputStream();
		Log4jConsoleAppender.setStaticOutputStream(ioOut);
		//BasicConfigurator.configure();

		Activator.explorerActionContext = new ActionContext();

		PrintStream systemOut = new PrintStream(ioOut);
		System.setOut(systemOut);
		System.setErr(systemOut);
		System.setIn(Activator.ioConsole.getInputStream());
		Activator.explorerActionContext.put("systemOut", systemOut);
		Activator.explorerActionContext.put("systemIn", Activator.ioConsole.getInputStream());
		
		inited = true;
	}
	
	public static ThingManager loadThingManager(IContainer container){
		IFile file = getFile(container, "xworker.properties");
		if(file != null && file.exists()){
			return loadXWorkerThingManager(container, file);
		}
		
		file = getFile(container, "dml.prj");
		if(file == null || file.exists() == false) {
			file = getFile(container, ".dml");
		}
		if(file == null || file.exists() == false) {
			file = getFile(container, "dml.properties");
		}
		if(file != null && file.exists()){
			return loadDmlThingManager(container, file);
		}
		
		return null;
	}
	
	public static IFile getFile(IContainer container, String fileName){
		try{
			return container.getFile(new Path(fileName));
		}catch(Exception e){
			return null;
		}
	}
	
	public static ThingManager loadXWorkerThingManager(IContainer container, IFile file){
		Properties p = new Properties();
		try {
			p.load(file.getContents());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		String name = p.getProperty("projectName");
		if(name == null || "".equals(name)){
			name = container.getFullPath().toPortableString();
		}
		ThingManager manager = World.getInstance().getThingManager(name); 
		if(manager == null){
			//初始化项目
			String filePath = null;;
			try {
				filePath = container.getLocationURI().toURL().getFile();
			} catch (MalformedURLException e) {
			}
			ThingManager fileManager = new FileThingManager(name, new File(filePath) , true);
			World.getInstance().addThingManagerFirst(fileManager);
			return fileManager;
		}else{
			return manager;
		}
	}
	
	public static ThingManager loadDmlThingManager(IContainer container, IFile dmlPrj){
		Properties p = new Properties();
		try {
			InputStream in = dmlPrj.getContents();
			p.load(in);
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		String name = p.getProperty("projectName");
		if(name == null || "".equals(name)){
			name = container.getFullPath().toPortableString();
		}
		ThingManager manager = World.getInstance().getThingManager(name); 
		if(manager == null){
			//初始化项目
			String filePath = null;;
			try {
				filePath = container.getLocationURI().toURL().getFile();
			} catch (MalformedURLException e) {
			}
			ThingManager fileManager = new FileThingManager(name, new File(filePath) , false);
			World.getInstance().addThingManagerFirst(fileManager);
			return fileManager;
		}else{
			return manager;
		}
	}
	
	public static String getThingPathByFile(IFile file){
		//是否是模型
		String name = file.getName();
		boolean isModel = false;
		String codeType = null;
		for(ThingCoder coder : World.getInstance().getThingCoders()){
			if(name.endsWith("." + coder.getType())){
				codeType = coder.getType();
				isModel = true;
				break;
			}
		}
		if(!isModel){
			return null;
		}
		
		//int index = name.lastIndexOf(".");
		String path = name.substring(0, name.length() - codeType.length() - 1);
		IContainer c = file.getParent();
		while(c != null){
			ThingManager  thingManager = loadThingManager(c);
			if(thingManager == null){
				path = c.getName() + "." + path;
				c = c.getParent();
			}else{
				FileThingManager fm = (FileThingManager) thingManager;
				if(fm.hasThingsDir()){
					if(path == null || !path.startsWith("things")){
						return null;
					}
					
					int index = path.indexOf(".");
					return path.substring(index + 1, path.length());
				}else{
					return path;
				}
			}
		}
		
		return null;
	}
}
