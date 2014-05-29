package xmpp.nasacj.learn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;

import xmpp.nasacj.woody.*;

public class NetworkConnection
{
	Socket socket = null;
	ConnectionConfiguration config = null;

	/**
	 * The Reader which is used for the {@see debugger}.
	 */
	protected Reader reader;

	/**
	 * The Writer which is used for the {@see debugger}.
	 */
	protected Writer writer;

	Thread writerThread;
	Thread readerThread;

	private void connectUsingConfiguration(ConnectionConfiguration config)
			throws IOException, InterruptedException
	{
		this.config = config;
		String host = config.getHost();
		int port = config.getPort();
		try
		{
			if (config.getSocketFactory() == null)
			{
				this.socket = new Socket(host, port);
			} else
			{
				this.socket = config.getSocketFactory()
						.createSocket(host, port);
			}
		} catch (UnknownHostException uhe)
		{
			throw uhe;
			// String errorMessage = "Could not connect to " + host + ":" + port
			// + ".";
			// throw new XMPPException(errorMessage, new XMPPError(
			// XMPPError.Condition.remote_server_timeout, errorMessage),
			// uhe);
		} catch (IOException ioe)
		{
			throw ioe;
			// String errorMessage = "XMPPError connecting to " + host + ":"
			// + port + ".";
			// throw new XMPPException(errorMessage, new XMPPError(
			// XMPPError.Condition.remote_server_error, errorMessage), ioe);
		}
		
		initConnection();
		initReader();
		initWriter();
		startup();
	}

	private void initConnection() throws UnsupportedEncodingException,
			IOException
	{
		reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream(), "UTF-8"));
		writer = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream(), "UTF-8"));
	}

	private void openStream() throws IOException
	{
		StringBuilder stream = new StringBuilder();
		stream.append("<stream:stream");
		stream.append(" to=\"").append(config.getServiceName()).append("\"");
		stream.append(" xmlns=\"jabber:client\"");
		stream.append(" xmlns:stream=\"http://etherx.jabber.org/streams\"");
		stream.append(" version=\"1.0\">");
		writer.write(stream.toString());
		writer.flush();
		
		writer.write("<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>");
		writer.flush();
	}

	public void initWriter()
	{
		writerThread = new Thread()
		{
			public void run()
			{
				try
				{
					writePackets(this);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		// writerThread.setName("Smack Packet Writer (" + connection.connectionCounterValue + ")");
		writerThread.setName("Smack Packet Writer");
		//writerThread.setDaemon(true);

	}
	
	public void writePackets(Thread thread) throws IOException, InterruptedException
	{
		openStream();
		Thread.sleep(Long.MAX_VALUE);
	}

	public void initReader()
	{
		readerThread = new Thread()
		{
			public void run()
			{
				try
				{
					parsePackets(this);
				} catch (IOException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		//readerThread.setName("Smack Packet Reader (" + connection.connectionCounterValue + ")");
		readerThread.setName("Smack Packet Reader");
		//readerThread.setDaemon(true);
	}
	
	public void parsePackets(Thread thread) throws IOException, InterruptedException
	{
		//BufferedReader bufferedReader = (BufferedReader)reader;
		while (true)
		{
			char[] char_buffer = new char[1];
			reader.read(char_buffer);
			//String lineString = bufferedReader.readLine();
			System.out.print(char_buffer);
			Thread.sleep(20);
		}
	}
	
	public void startup() throws InterruptedException {
		readerThread.start();
		//Thread.sleep(1000);
        writerThread.start();
    }
	
	public void ExamTry(String serverName) throws IOException, InterruptedException
	{
		ConnectionConfiguration configuration = new ConnectionConfiguration(serverName);
		connectUsingConfiguration(configuration);
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException
	{
		NetworkConnection networkConnection = new NetworkConnection();
		networkConnection.ExamTry("jabber.org");
		//Thread.sleep(Long.MAX_VALUE);
	}
}
