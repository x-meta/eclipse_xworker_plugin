package org.xworker.plugin;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.xmeta.ActionContext;
import org.xmeta.Thing;
import org.xmeta.World;
import org.xworker.plugin.editors.ThingPackageViewer;
import org.xworker.plugin.preferences.PreferenceConstants;
import org.xworker.plugin.resources.ThingResourceChangeListener;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.xworker.plugin";

	// The shared instance
	private static Activator plugin;

	public static ExplorerHelper helper = new ExplorerHelper();
	
	// 事物控制台
	public static IOConsole ioConsole = new IOConsole("XWorker Console", null);
	static{
		ioConsole.addPatternMatchListener(new XWorkerPatternMatchListener(helper));
	}

	// ActionContext
	public static ActionContext explorerActionContext;

	IResourceChangeListener resourceChangeListener = new ThingResourceChangeListener();

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		
		plugin = this;		

		init();
		/*
		Display.getCurrent().asyncExec(new Runnable(){
			public void run(){
				init();
			}
		});*/		
	}

	public void init() {
		XWorker.init();
		
		explorerActionContext.put("helper", helper);

		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				resourceChangeListener, IResourceChangeEvent.POST_CHANGE);

		
		final World world = World.getInstance();
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String xworkerPath = store.getString(PreferenceConstants.P_PATH);
		if("".equals(xworkerPath)){
			xworkerPath = null;
		}
		world.init(xworkerPath);		
		// world.init("E:\\xworker\\org.xworker.alpha\\data");		

		try {
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getShell();
			explorerActionContext.getScope(0).put("shell", shell);
		} catch (Exception e) {
		}

		Thread.currentThread().setContextClassLoader(world.getClassLoader());
		
		// 初始化事物浏览
		Thing explorerActionsThing = world.getThing(Constants.EXPLORER_ACTIONS_THING);
		if (explorerActionsThing != null) {
			explorerActionsThing.doAction("create", explorerActionContext);
		}
	
		final Thing scripts = World.getInstance().getThing(Constants.EXPLOERE_SCRIPTS_THING);		
		if (scripts != null) {
				scripts.doAction("init", explorerActionContext);						
		}
		
		// 初始化eclips中其他项目所配置的事物项目
		ThingResourceChangeListener.initThingPaths();

		NavigationView.init();
		ThingPackageViewer.init();
		
		//初始化Jetty
		try{
			//World.getInstance().getAction("xworker.ide.config.functions/@node2/@net/@startJettry").run();
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);

		ResourcesPlugin.getWorkspace().removeResourceChangeListener(
				resourceChangeListener);

	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
}
