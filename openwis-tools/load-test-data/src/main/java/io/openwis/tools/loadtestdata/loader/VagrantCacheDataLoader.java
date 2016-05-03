package io.openwis.tools.loadtestdata.loader;

import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * A cache data loader which will connect to the data Vagrant VM via ssh and
 * invoke the "load-cache-data.py" script.
 * 
 * @author lmika
 *
 */
public class VagrantCacheDataLoader implements CacheDataLoader {

	/**
	 * Command that will be invoked on the Vagrant VM to generate cache data.
	 */
	private static final String GEN_CACHE_DATA_CMD = "/vagrant/resources/vagrant/scripts/gen-cache-files.py";
	
	private final String dataHostname;
	private final int dataSshPort;
	private final String username;
	private final String password;

	public VagrantCacheDataLoader(String dataHostname, int dataSshPort,
			String username, String password) {
		this.dataHostname = dataHostname;
		this.dataSshPort = dataSshPort;
		this.username = username;
		this.password = password;
	}

	@Override
	public void loadCacheData() {

		JSch jsch = new JSch();

		Session session = null;
		ChannelExec channel = null;

		try {
			UserInfo ui = new MyUserInfo();

			session = jsch.getSession(username, dataHostname, dataSshPort);
			session.setUserInfo(ui);
			session.connect();

			channel = (ChannelExec) session.openChannel("exec");

			channel.setCommand(GEN_CACHE_DATA_CMD);
			channel.setInputStream(null);
			channel.setOutputStream(System.out);
			channel.setErrStream(System.err);

			channel.connect();

			relayOutputToStdout(channel);
		} catch (Exception e) {
			throw new RuntimeException("Cannot start cache generation script", e);
		} finally {
			if (channel != null) {
				channel.disconnect();
			}

			if (session != null) {
				session.disconnect();
			}
		}
	}

	/**
	 * Relay any output from the exec channel to stdout.
	 * 
	 * @param channel
	 * @throws IOException 
	 */
	private void relayOutputToStdout(ChannelExec channel) throws IOException {
		InputStream in = channel.getInputStream();
		
		byte[] tmp = new byte[1024];
		while (true) {
			while (in.available() > 0) {
				int i = in.read(tmp, 0, 1024);
				if (i < 0)
					break;
				System.out.print(new String(tmp, 0, i));
			}
			if (channel.isClosed()) {
				if (in.available() > 0)
					continue;
				System.out.println("exit-status: " + channel.getExitStatus());
				break;
			}
			try {
				Thread.sleep(1000);
			} catch (Exception ee) {
			}
		}
	}

	private class MyUserInfo implements UserInfo {

		@Override
		public String getPassphrase() {
			return password;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public boolean promptPassphrase(String arg0) {
			return true;
		}

		@Override
		public boolean promptPassword(String arg0) {
			return true;
		}

		@Override
		public boolean promptYesNo(String arg0) {
			return true;
		}

		@Override
		public void showMessage(String arg0) {
			// Ignore
		}
	}
}
