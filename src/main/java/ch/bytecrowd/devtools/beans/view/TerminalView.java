package ch.bytecrowd.devtools.beans.view;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.apache.log4j.Logger;
import org.apache.poi.util.IOUtils;

import ch.bytecrowd.devtools.utils.OSInfoUtil;

@Named("terminalView")
@SessionScoped
public class TerminalView implements Serializable {

	private static final Logger LOG = Logger.getLogger(TerminalView.class);
	private static final long serialVersionUID = 5969895348296613587L;

	public String execCommand(String command, String[] params) {
		StringBuilder builderCommand = new StringBuilder(command);
		StringBuilder builderResponse = new StringBuilder();
		for (String s : params) {
			builderCommand.append(" ").append(s);
		}
		
		String commandString = builderCommand.toString();
		if (!commandString.trim().isEmpty()) {
			try {
				String[] sysCommand = null;
				if (OSInfoUtil.OS_NAME.toLowerCase().contains("windows"))
					sysCommand = new String[]{"cmd", "/c", commandString};
				else	
					sysCommand = new String[]{"sh", "-c", commandString};
				LOG.info("CMD: " + Arrays.toString(sysCommand));
				Process process = Runtime.getRuntime().exec(sysCommand);
				try (InputStream inputStream = process.getInputStream();
						InputStream errorStream = process.getErrorStream();){
					builderResponse.append(new String(IOUtils.toByteArray(inputStream)));
					builderResponse.append(new String(IOUtils.toByteArray(errorStream)));
				} catch (Exception e) {
					builderResponse.append(e);
					LOG.error("execCommand-> processing Streams failed", e);
				}
				
			} catch (IOException e) {
				builderResponse.append(e);
				LOG.error("execCommand failed", e);
			}
		}
		return builderResponse.toString();
	}
}
