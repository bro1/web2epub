package bro1.mine.utils;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;


import bro1.mine.FTPConfiguration;
import bro1.utils.log.Log;

public class FtpUtil {

	Logger logger = Log.getLogger(FtpUtil.class);

	public void upload(FTPConfiguration ftpConfig, String subDirectory,
			File... files) throws Exception {

		FTPConfiguration config;

		if (ftpConfig == null) {
			config = new FTPConfiguration();
		} else {
			config = ftpConfig;
		}

		FTPClient ftp = new FTPClient();

		try {
			int reply;
			ftp.connect(config.getExportFTPServer());

			// After connection attempt, you should check the reply code to
			// verify success.
			reply = ftp.getReplyCode();

			if (!FTPReply.isPositiveCompletion(reply)) {
				logger.error("Could not connect to the FTP server");
				ftp.disconnect();
				throw new Exception("FTP server refused connection.");
			}

			ftp.login(config.getExportFTPUserName(),
					config.getExportFTPPassword());

			if (subDirectory.length() != 0) {
				logger.info("Changing to directory: " + subDirectory);

				ftp.cwd(subDirectory);
				reply = ftp.getReplyCode();

				if (!FTPReply.isPositiveCompletion(reply)) {
					logger.error("Could not change to directory '" + subDirectory
							+ "', continuing anyway");
				}
			}

			for (File file : files) {

				ftp.setFileType(FTP.BINARY_FILE_TYPE);
				logger.info("Uploading: " + file.getAbsolutePath());
				boolean uploadSuccessfullInd = ftp.storeFile(file.getName(),
						new FileInputStream(file));
				logger.info("Finished uploading: " + file.getAbsolutePath());

				if (!uploadSuccessfullInd) {
					logger.error("Upload failed: " + file.getName());
					throw new Exception("Could not upload file");
				}

			}

			ftp.logout();

		} finally {

			if (ftp.isConnected()) {
				ftp.disconnect();
			}
		}

		logger.info("Successfully finished uploading all specified files");

	}

}
