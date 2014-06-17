package xmpp.nasacj.learn;

import java.io.IOException;
import java.io.StringReader;
import java.util.Map;
import java.util.TreeMap;

import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.AuthorizeCallback;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.Sasl;
import javax.security.sasl.SaslClient;
import javax.security.sasl.SaslException;
import javax.security.sasl.SaslServer;

import xmpp.nasacj.woody.util.Base64;

public class AuthMain
{

	/**
	 * 
	 * @param args
	 * 
	 * @throws SaslException
	 */

	public static void main(String[] args) throws SaslException
	{

		Map<String, String> props = new TreeMap<String, String>();

		//props.put(Sasl.QOP, "auth");

		//SaslServer ss = Sasl.createSaslServer("DIGEST-MD5", "xmpp", "jabber.org",

		//props, (CallbackHandler) new ServerCallbackHandler());

		//byte[] token = new byte[0];

		//byte[] challenge = ss.evaluateResponse(token);

		SaslClient sc = Sasl.createSaslClient(new String[]
		{ "DIGEST-MD5" },

		"nasacj", "xmpp", "jabber.org", null, new ClientCallbackHandler());

//		byte response[];
//
//		if (challenge != null)
//		{
//
//			response = sc.evaluateChallenge(challenge);
//
//		} else
//		{
//
//			response = sc.evaluateChallenge(null);
//
//		}
//
//		ss.evaluateResponse(response);
//
//		if (ss.isComplete())
//		{
//
//			System.out.println("auth success");
//
//		}
		String authenticationText = null;
		if(sc.hasInitialResponse()) {
            byte[] response = sc.evaluateChallenge(new byte[0]);
            authenticationText = Base64.encodeBytes(response,Base64.DONT_BREAK_LINES);
        }
		
		// Send the authentication to the server
        //getSASLAuthentication().send(new AuthMechanism(getName(), authenticationText));
		String challenge = "bm9uY2U9InhUbllxa3VZb2xGNXNCa1htU3N2dGh3S3NXQ1MwYU9NdThoTDlzdngzR2c9IixyZWFsbT0iamFiYmVyLm9yZyIscW9wPSJhdXRoIixtYXhidWY9MTYzODQsY2hhcnNldD11dGYtOCxhbGdvcml0aG09bWQ1LXNlc3M=";
		byte[] response = sc.evaluateChallenge(Base64.decode(challenge));
		String reString = Base64.encodeBytes(response,Base64.DONT_BREAK_LINES);
		System.out.println(reString);

	}

}

class ClientCallbackHandler implements CallbackHandler
{

	public void handle(Callback[] callbacks) throws IOException,

	UnsupportedCallbackException
	{

		for (int i = 0; i < callbacks.length; i++)
		{

			if (callbacks[i] instanceof NameCallback)
			{

				NameCallback ncb = (NameCallback) callbacks[i];

				ncb.setName("nasacj");

			} else if (callbacks[i] instanceof PasswordCallback)
			{

				PasswordCallback pcb = (PasswordCallback) callbacks[i];

				pcb.setPassword("1993Nasa".toCharArray());

			} else if (callbacks[i] instanceof RealmCallback)
			{

				RealmCallback rcb = (RealmCallback) callbacks[i];

				rcb.setText("jabber.org");

			} else
			{

				throw new UnsupportedCallbackException(callbacks[i]);

			}

		}

	}

}

class ServerCallbackHandler implements CallbackHandler
{

	public ServerCallbackHandler()
	{

	}

	public void handle(final Callback[] callbacks) throws IOException,

	UnsupportedCallbackException
	{

		for (Callback callback : callbacks)
		{

			if (callback instanceof RealmCallback)
			{

				// do your business

			} else if (callback instanceof NameCallback)
			{

				// do your business

			} else if (callback instanceof PasswordCallback)
			{

				((PasswordCallback) callback).setPassword("admin1"

				.toCharArray());

			} else if (callback instanceof AuthorizeCallback)
			{

				AuthorizeCallback authCallback = ((AuthorizeCallback) callback);

				authCallback.setAuthorized(true);

			} else
			{

				System.out.println(callback.getClass().getName());

				throw new UnsupportedCallbackException(callback,

				"Unrecognized Callback");

			}

		}

	}

}