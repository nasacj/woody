package xmpp.nasacj.learn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringBufferInputStream;

import org.xmlpull.mxp1.MXParser;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ParserLearn
{

	/**
	 * @param args
	 * @throws XmlPullParserException 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws XmlPullParserException, IOException
	{
		XmlPullParser parser = new MXParser();
		parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
		String xml = "<?xml version='1.0'?><stream:stream xmlns='jabber:client' xmlns:stream='http://etherx.jabber.org/streams' from='jabber.org' id='c285c2683ebca6f1' version='1.0'><stream:features><starttls xmlns='urn:ietf:params:xml:ns:xmpp-tls'/><mechanisms xmlns='urn:ietf:params:xml:ns:xmpp-sasl'><mechanism>DIGEST-MD5</mechanism></mechanisms></stream:features>";
		Reader reader = new BufferedReader(new InputStreamReader(new StringBufferInputStream(xml)));
		parser.setInput(reader);
		
		int eventType = parser.getEventType();
		do
		{
			if (eventType == XmlPullParser.START_TAG)
			{
				System.err.println(parser.getText());
				if (parser.getName().equals("stream"))
					System.err.println("Get stream");
					
				System.out.print("Start_Tag:------");
				System.out.println("<" + parser.getName() + ">");
				
				int  attrCount = parser.getAttributeCount();
				System.out.println("There are " + attrCount + " attributes");
				for (int i = 0; i < attrCount; i++ )
				{
					System.out.print(parser.getAttributeName(i) + ":");
					System.out.println(parser.getAttributeValue(i));
				}
			}
			
			if (eventType == XmlPullParser.END_TAG)
				System.out.println("-----------" + parser.getName() + "------------");
			eventType = parser.next();
		}while(eventType != XmlPullParser.END_DOCUMENT);
		
		

	}

}
