package xmpp.nasacj.learn;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.security.auth.callback.PasswordCallback;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

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

	protected XmlPullParser parser;

	private PipedReader in;
	private PipedWriter out;

	Thread writerThread;
	Thread readerThread;
	Thread outputThread;
	private boolean isOutPutRunning;

	private void connectUsingConfiguration(ConnectionConfiguration config)
			throws IOException, InterruptedException, XmlPullParserException
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

		initReaderAndWriter();
		resetParser(reader);
		initReaderThread();
		initWriterThread();
		startup();
	}

	private void resetParser(Reader inReader) throws XmlPullParserException
	{
		parser = new MXParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		parser.setInput(inReader);
	}

	private void initReaderAndWriter() throws UnsupportedEncodingException,
			IOException, XmlPullParserException
	{
		reader = new BufferedReader(new InputStreamReader(
				socket.getInputStream(), "UTF-8"));
		writer = new BufferedWriter(new OutputStreamWriter(
				socket.getOutputStream(), "UTF-8"));

		in = new PipedReader();
		out = new PipedWriter(in);

	}

	private void askTLS() throws IOException, InterruptedException
	{
		Thread.sleep(500);
		writer.write("<starttls xmlns=\"urn:ietf:params:xml:ns:xmpp-tls\"/>");
		writer.flush();
	}

	private void openStream() throws IOException, InterruptedException
	{
		StringBuilder stream = new StringBuilder();
		stream.append("<stream:stream");
		stream.append(" to=\"").append(config.getServiceName()).append("\"");
		stream.append(" xmlns=\"jabber:client\"");
		stream.append(" xmlns:stream=\"http://etherx.jabber.org/streams\"");
		stream.append(" version=\"1.0\">");
		writer.write(stream.toString());
		writer.flush();

	}

	void proceedTLSReceived() throws Exception
	{
		// Socket plain = socket;
		// SSLSocketFactory factory = (SSLSocketFactory)
		// SSLSocketFactory.getDefault();
		// // Secure the plain connection
		// socket = factory.createSocket(plain,
		// plain.getInetAddress().getHostName(), plain.getPort(), true);
		// socket.setSoTimeout(0);
		// socket.setKeepAlive(true);

		SSLContext context = SSLContext.getInstance("TLS");
		KeyStore ks = null;
		KeyManager[] kms = null;
		PasswordCallback pcb = null;

		// Verify certificate presented by the server
		context.init(kms, new javax.net.ssl.TrustManager[]
		{ new ServerTrustManager(config.getServiceName(), config) },
				new java.security.SecureRandom());
		Socket plain = socket;
		// Secure the plain connection
		socket = context.getSocketFactory().createSocket(plain,
				plain.getInetAddress().getHostName(), plain.getPort(), true);
		socket.setSoTimeout(0);
		socket.setKeepAlive(true);

		// Initialize the reader and writer with the new secured version
		// stopOutPutThread();
		//outputThread.stop();
		initReaderAndWriter();
		// Proceed to do the handshake
		System.err.println("Start Handshake!");
		((SSLSocket) socket).startHandshake();
		System.err.println("Handshake finish!");

		openStream();
	}

	public void stopOutPutThread() throws InterruptedException
	{
		isOutPutRunning = false;
		Thread.sleep(2000);
	}

	public void restartOutPutThread() throws InterruptedException
	{
		isOutPutRunning = false;
		Thread.sleep(5000);
		outputThread = new Thread()
		{
			public void run()
			{
				try
				{
					outputPureDatas(this);
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
		// writerThread.setName("Smack Packet Writer (" +
		// connection.connectionCounterValue + ")");
		outputThread.setName("Smack Out Put Thread");
		outputThread.setDaemon(true);
		isOutPutRunning = true;
		outputThread.start();
	}

	public void startOutPutThread()
	{
		isOutPutRunning = true;
		outputThread = new Thread()
		{
			public void run()
			{
				try
				{
					outputPureDatas(this);
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
		// writerThread.setName("Smack Packet Writer (" +
		// connection.connectionCounterValue + ")");
		outputThread.setName("Smack Out Put Thread");
		outputThread.setDaemon(true);
		outputThread.start();
	}

	public void initWriterThread()
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
		// writerThread.setName("Smack Packet Writer (" +
		// connection.connectionCounterValue + ")");
		writerThread.setName("Smack Packet Writer");
		// writerThread.setDaemon(true);

	}

	public void writePackets(Thread thread) throws IOException,
			InterruptedException
	{
		openStream();
		askTLS();
		Thread.sleep(Long.MAX_VALUE);
	}

	public void outputPureDatas(Thread thread) throws IOException,
			InterruptedException
	{
		System.err.println("OutPut Thread start...");
		while (isOutPutRunning)
		{
			char[] char_buffer = new char[1];
			reader.read(char_buffer);
			out.write(char_buffer);
			System.out.print(char_buffer);
			// Thread.sleep(20);
		}
		System.err.println("OutPut Thread finish...");
	}

	public void initReaderThread()
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
				} catch (XmlPullParserException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		// readerThread.setName("Smack Packet Reader (" +
		// connection.connectionCounterValue + ")");
		readerThread.setName("Smack Packet Reader");
		// readerThread.setDaemon(true);
	}

	public void parsePackets(Thread thread) throws Exception
	{
		// BufferedReader bufferedReader = (BufferedReader)reader;
		int eventType = parser.getEventType();
		while (true)
		{
			// Thread.sleep(500);
			if (eventType == XmlPullParser.START_TAG)
			{
				System.err.println(parser.getName());
				if (parser.getName().equals("proceed"))
				{
					stopOutPutThread();
					proceedTLSReceived();
					resetParser(in);
					startOutPutThread();
				}
			}
			eventType = parser.next();
			// System.out.println("next");

		}
	}

	public void startup() throws InterruptedException
	{
		readerThread.start();
		// Thread.sleep(1000);
		writerThread.start();
		//outputThread.start();
	}

	public void ExamTry(String serverName) throws IOException,
			InterruptedException, XmlPullParserException
	{
		ConnectionConfiguration configuration = new ConnectionConfiguration(
				serverName);
		connectUsingConfiguration(configuration);
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws XmlPullParserException
	 */
	public static void main(String[] args) throws IOException,
			InterruptedException, XmlPullParserException
	{
		NetworkConnection networkConnection = new NetworkConnection();
		networkConnection.ExamTry("jabber.org");
		// Thread.sleep(Long.MAX_VALUE);
	}
}
