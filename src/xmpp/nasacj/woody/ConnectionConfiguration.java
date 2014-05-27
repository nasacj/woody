package xmpp.nasacj.woody;

import javax.net.SocketFactory;

import xmpp.nasacj.woody.proxy.ProxyInfo;
import xmpp.nasacj.woody.util.DNSUtil;

public class ConnectionConfiguration implements Cloneable
{
	/**
	 * Hostname of the XMPP server. Usually servers use the same service name as
	 * the name of the server. However, there are some servers like google where
	 * host would be talk.google.com and the serviceName would be gmail.com.
	 */
	private String serviceName;

	private String host;
	private int port;

	protected ProxyInfo proxy;

	private boolean compressionEnabled = false;

	private boolean saslAuthenticationEnabled = true;

	// Holds the socket factory that is used to generate the socket in the
	// connection
	private SocketFactory socketFactory;

	/**
	 * Creates a new ConnectionConfiguration for the specified service name. A
	 * DNS SRV lookup will be performed to find out the actual host address and
	 * port to use for the connection.
	 * 
	 * @param serviceName
	 *            the name of the service provided by an XMPP server.
	 */
	public ConnectionConfiguration(String serviceName)
	{
		// Perform DNS lookup to get host and port to use
		DNSUtil.HostAddress address = DNSUtil.resolveXMPPDomain(serviceName);
		init(address.getHost(), address.getPort(), serviceName,
				ProxyInfo.forDefaultProxy());
	}

	/**
	 * Creates a new ConnectionConfiguration using the specified host, port and
	 * service name. This is useful for manually overriding the DNS SRV lookup
	 * process that's used with the {@link #ConnectionConfiguration(String)}
	 * constructor. For example, say that an XMPP server is running at localhost
	 * in an internal network on port 5222 but is configured to think that it's
	 * "example.com" for testing purposes. This constructor is necessary to
	 * connect to the server in that case since a DNS SRV lookup for example.com
	 * would not point to the local testing server.
	 * 
	 * @param host
	 *            the host where the XMPP server is running.
	 * @param port
	 *            the port where the XMPP is listening.
	 * @param serviceName
	 *            the name of the service provided by an XMPP server.
	 */
	public ConnectionConfiguration(String host, int port, String serviceName)
	{
		init(host, port, serviceName, ProxyInfo.forDefaultProxy());
	}

	/**
	 * Creates a new ConnectionConfiguration for the specified service name with
	 * specified proxy. A DNS SRV lookup will be performed to find out the
	 * actual host address and port to use for the connection.
	 * 
	 * @param serviceName
	 *            the name of the service provided by an XMPP server.
	 * @param proxy
	 *            the proxy through which XMPP is to be connected
	 */
	public ConnectionConfiguration(String serviceName, ProxyInfo proxy)
	{
		// Perform DNS lookup to get host and port to use
		DNSUtil.HostAddress address = DNSUtil.resolveXMPPDomain(serviceName);
		init(address.getHost(), address.getPort(), serviceName, proxy);
	}

	/**
	 * Creates a new ConnectionConfiguration using the specified host, port and
	 * service name. This is useful for manually overriding the DNS SRV lookup
	 * process that's used with the {@link #ConnectionConfiguration(String)}
	 * constructor. For example, say that an XMPP server is running at localhost
	 * in an internal network on port 5222 but is configured to think that it's
	 * "example.com" for testing purposes. This constructor is necessary to
	 * connect to the server in that case since a DNS SRV lookup for example.com
	 * would not point to the local testing server.
	 * 
	 * @param host
	 *            the host where the XMPP server is running.
	 * @param port
	 *            the port where the XMPP is listening.
	 * @param serviceName
	 *            the name of the service provided by an XMPP server.
	 * @param proxy
	 *            the proxy through which XMPP is to be connected
	 */
	public ConnectionConfiguration(String host, int port, String serviceName,
			ProxyInfo proxy)
	{
		init(host, port, serviceName, proxy);
	}

	/**
	 * Creates a new ConnectionConfiguration for a connection that will connect
	 * to the desired host and port.
	 * 
	 * @param host
	 *            the host where the XMPP server is running.
	 * @param port
	 *            the port where the XMPP is listening.
	 */
	public ConnectionConfiguration(String host, int port)
	{
		init(host, port, host, ProxyInfo.forDefaultProxy());

		// Setting the SocketFactory according to proxy supplied
		socketFactory = proxy.getSocketFactory();
	}

	private void init(String host, int port, String serviceName, ProxyInfo proxy)
	{
		this.host = host;
		this.port = port;
		this.serviceName = serviceName;
		this.proxy = proxy;
	}

	/**
	 * Sets the server name, also known as XMPP domain of the target server.
	 * 
	 * @param serviceName
	 *            the XMPP domain of the target server.
	 */
	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}

	/**
	 * Returns the server name of the target server.
	 * 
	 * @return the server name of the target server.
	 */
	public String getServiceName()
	{
		return serviceName;
	}

	/**
	 * Returns the host to use when establishing the connection. The host and
	 * port to use might have been resolved by a DNS lookup as specified by the
	 * XMPP spec (and therefore may not match the {@link #getServiceName service
	 * name}.
	 * 
	 * @return the host to use when establishing the connection.
	 */
	public String getHost()
	{
		return host;
	}

	/**
	 * Returns the port to use when establishing the connection. The host and
	 * port to use might have been resolved by a DNS lookup as specified by the
	 * XMPP spec.
	 * 
	 * @return the port to use when establishing the connection.
	 */
	public int getPort()
	{
		return port;
	}

	/**
	 * Returns true if the connection is going to use stream compression. Stream
	 * compression will be requested after TLS was established (if TLS was
	 * enabled) and only if the server offered stream compression. With stream
	 * compression network traffic can be reduced up to 90%. By default
	 * compression is disabled.
	 * 
	 * @return true if the connection is going to use stream compression.
	 */
	public boolean isCompressionEnabled()
	{
		return compressionEnabled;
	}

	/**
	 * Sets if the connection is going to use stream compression. Stream
	 * compression will be requested after TLS was established (if TLS was
	 * enabled) and only if the server offered stream compression. With stream
	 * compression network traffic can be reduced up to 90%. By default
	 * compression is disabled.
	 * 
	 * @param compressionEnabled
	 *            if the connection is going to use stream compression.
	 */
	public void setCompressionEnabled(boolean compressionEnabled)
	{
		this.compressionEnabled = compressionEnabled;
	}

	/**
	 * Returns true if the client is going to use SASL authentication when
	 * logging into the server. If SASL authenticatin fails then the client will
	 * try to use non-sasl authentication. By default SASL is enabled.
	 * 
	 * @return true if the client is going to use SASL authentication when
	 *         logging into the server.
	 */
	public boolean isSASLAuthenticationEnabled()
	{
		return saslAuthenticationEnabled;
	}

	/**
	 * Sets whether the client will use SASL authentication when logging into
	 * the server. If SASL authenticatin fails then the client will try to use
	 * non-sasl authentication. By default, SASL is enabled.
	 * 
	 * @param saslAuthenticationEnabled
	 *            if the client is going to use SASL authentication when logging
	 *            into the server.
	 */
	public void setSASLAuthenticationEnabled(boolean saslAuthenticationEnabled)
	{
		this.saslAuthenticationEnabled = saslAuthenticationEnabled;
	}

	/**
	 * Returns the socket factory used to create new xmppConnection sockets.
	 * This is useful when connecting through SOCKS5 proxies.
	 * 
	 * @return socketFactory used to create new sockets.
	 */
	public SocketFactory getSocketFactory()
	{
		return this.socketFactory;
	}

	/**
	 * Sets the socket factory used to create new xmppConnection sockets. This
	 * is useful when connecting through SOCKS5 proxies.
	 * 
	 * @param socketFactory
	 *            used to create new sockets.
	 */
	public void setSocketFactory(SocketFactory socketFactory)
	{
		this.socketFactory = socketFactory;
	}
}
