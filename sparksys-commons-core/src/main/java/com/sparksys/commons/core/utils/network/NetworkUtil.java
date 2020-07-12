package com.sparksys.commons.core.utils.network;

import cn.hutool.core.net.NetUtil;

import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
 * description：Network工具类
 *
 * @author zhouxinlei
 * @date  2020/6/9 0009
 */
public class NetworkUtil extends NetUtil {

    private static InetAddress localINetAddress;

    public static InetAddress getLocalINetAddress() {
        if (localINetAddress == null) {
            load();
        }
        return localINetAddress;
    }

    public static String getLocalHostAddress() {
        if (localINetAddress == null) {
            load();
        }
        return localINetAddress.getHostAddress();
    }

    public static String getLocalHostName() {
        if (localINetAddress == null) {
            load();
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return localINetAddress.getHostName();
        }
    }

    private static InetAddress findValidateIp(List<Address> addresses) {
        InetAddress local = null;
        int size = addresses.size();
        int maxWeight = -1;

        for (int i = 0; i < size; i++) {
            Address address = addresses.get(i);
            if (address.isInet4Address()) {
                int weight = 0;

                if (address.isSiteLocalAddress()) {
                    weight += 8;
                }

                if (address.isLinkLocalAddress()) {
                    weight += 4;
                }

                if (address.isLoopbackAddress()) {
                    weight += 2;
                }

                if (address.hasHostName()) {
                    weight += 1;
                }

                if (weight > maxWeight) {
                    maxWeight = weight;
                    local = address.getAddress();
                }
            }
        }

        return local;
    }

    private static String getProperty() {
        String value;
        value = System.getProperty("host.ip");
        if (value == null) {
            value = System.getenv("host.ip");
        }
        return value;
    }

    private static void load() {
        String ip = getProperty();

        if (ip != null) {
            try {
                localINetAddress = InetAddress.getByName(ip);
                return;
            } catch (Exception e) {
                System.err.println(e);
                // ignore
            }
        }

        try {
            List<NetworkInterface> nis = Collections.list(NetworkInterface.getNetworkInterfaces());
            List<Address> addresses = new ArrayList<>();
            InetAddress local = null;

            try {
                for (NetworkInterface ni : nis) {
                    if (ni.isUp() && !ni.isLoopback()) {
                        List<InetAddress> list = Collections.list(ni.getInetAddresses());

                        for (InetAddress address : list) {
                            addresses.add(new Address(address, ni));
                        }
                    }
                }
                local = findValidateIp(addresses);
            } catch (Exception e) {
                // ignore
            }
            localINetAddress = local;
        } catch (SocketException e) {
            // ignore it
        }
    }

    static class Address {
        private InetAddress m_address;

        private boolean m_loopback;

        public Address(InetAddress address, NetworkInterface ni) {
            m_address = address;

            try {
                if (ni != null && ni.isLoopback()) {
                    m_loopback = true;
                }
            } catch (SocketException e) {
                // ignore it
            }
        }

        public InetAddress getAddress() {
            return m_address;
        }

        public boolean hasHostName() {
            return !m_address.getHostName().equals(m_address.getHostAddress());
        }

        public boolean isLinkLocalAddress() {
            return !m_loopback && m_address.isLinkLocalAddress();
        }

        public boolean isLoopbackAddress() {
            return m_loopback || m_address.isLoopbackAddress();
        }

        public boolean isSiteLocalAddress() {
            return !m_loopback && m_address.isSiteLocalAddress();
        }

        public boolean isInet4Address() {
            return m_address instanceof Inet4Address;
        }
    }

    public static void main(String[] args) {
        System.out.println(NetworkUtil.getLocalHostAddress());
        System.out.println(NetworkUtil.getLocalHostName());
        System.out.println(NetworkUtil.getLocalINetAddress());
    }
}
