package lets.cool.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class IPUtils {

	public static String[] getLocalIP() throws SocketException {
		List<String> ips = new ArrayList<>();
		
		Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
		while(en.hasMoreElements()) {
			NetworkInterface in = en.nextElement();
			
			if (in.getDisplayName().startsWith("vnic")) continue;
			if (in.isLoopback()) continue;
			if (in.isVirtual()) continue;
			//if (in.is)
			//if (in.is)
			
			Enumeration<InetAddress> addr = in.getInetAddresses();
			while(addr.hasMoreElements()) {

				InetAddress ad = addr.nextElement();
				String publicIP = ad.getHostAddress();
				String publicHostName = ad.getHostName();
				
				//if (!ad.isLinkLocalAddress()) continue;
				//if (ad.getHostAddress().contains(":")) continue; // we don't like ipv6
				
				String macAddr = "";
				for (byte b:in.getHardwareAddress()) {
					if (macAddr.length()==0) {
						macAddr += Integer.toHexString(b<0?b+256:b);
					} else {
						macAddr += "." + Integer.toHexString(b<0?b+256:b);
					}
				}
				
				System.out.printf("Used %s/%s - IP:%s, MAC:%s\n", in.getDisplayName(), publicHostName, publicIP, macAddr);
				
				ips.add(publicIP);
			}
		}
		
		return ips.toArray(new String[ips.size()]);
	}

}


