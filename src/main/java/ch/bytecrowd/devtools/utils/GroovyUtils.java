package ch.bytecrowd.devtools.utils;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.StringWriter;

public class GroovyUtils {

	public static String execGroovy(String script) {
		Binding binding = new Binding();
		final StringWriter writer = new StringWriter();
		binding.setProperty("out", writer);
		GroovyShell shell = new GroovyShell(binding);

		Object value = shell.evaluate(script);
		return "### RETURN VALUE ####\n\n" + (value != null ? value.toString() : "")
				+ "\n\n### STD_OUT ####\n\n" + writer.toString();
	}
}
