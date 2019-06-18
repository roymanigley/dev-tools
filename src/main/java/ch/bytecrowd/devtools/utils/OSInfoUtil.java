package ch.bytecrowd.devtools.utils;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named("osInfoUtils")
public final class OSInfoUtil implements Serializable {
	
	private static final long serialVersionUID = 8418286295150252695L;
	
	public static final String OS_NAME = System.getProperty("os.name");
	public static final String OS_TYPE = System.getProperty("os.arch");;
	public static final String OS_VERSION = System.getProperty("os.version");

	public static String getOsName() {
		return OS_NAME;
	}
	public static String getOsType() {
		return OS_TYPE;
	}
	public static String getOsVersion() {
		return OS_VERSION;
	}
	
	public String getOsIcon() {
		if (OS_NAME.toLowerCase().contains("windows"))
			return "windows.png";
		else if (OS_NAME.toLowerCase().contains("mac"))
			return "apple.png";
		else if (OS_NAME.toLowerCase().contains("linux"))
			return "linux.ico";
		else 
			return "unknown.png";
	}
}
