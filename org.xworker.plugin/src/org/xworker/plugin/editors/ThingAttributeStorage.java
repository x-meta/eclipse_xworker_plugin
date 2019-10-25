package org.xworker.plugin.editors;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xmeta.Thing;

public class ThingAttributeStorage implements IStorage{
	Thing thing;
	String name;
	
	public ThingAttributeStorage(Thing thing, String name){
		this.thing = thing;
		this.name = name;
	}
	
	@Override
	public InputStream getContents() throws CoreException {
		String code = thing.getString(name);
		if(code == null){
			code = "";
		}
		return new ByteArrayInputStream(code.getBytes());
	}

	@Override
	public IPath getFullPath() {
		return new Path("/abcd");
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isReadOnly() {
		return false;
	}

	@Override
	public Object getAdapter(Class adapter) {
		return null;
	}

}
