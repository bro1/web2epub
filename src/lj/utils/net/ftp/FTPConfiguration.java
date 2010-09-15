package lj.utils.net.ftp;

import java.util.ResourceBundle;

public class FTPConfiguration {

	public boolean isEnabled() {
		return !(getExportFTPServer() == null || ""
				.equals(getExportFTPServer()));
	}

	public String getExportFTPUserName() {
		return props.getString("ftp.username");
	}

	public String getExportFTPPassword() {
		return props.getString("ftp.password");
	}

	public String getExportFTPServer() {
		return props.getString("ftp.servername");
	}

	static private ResourceBundle props;

	static {
		props = ResourceBundle.getBundle("/settings");
	}

}
