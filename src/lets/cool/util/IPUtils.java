/*
 * LC-jlibs, lets.cool java libraries
 * Copyright (C) 2015-2018 Yuchi Chen (yuchi518@gmail.com)

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation. For the terms of this
 * license, see <http://www.gnu.org/licenses>.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

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


