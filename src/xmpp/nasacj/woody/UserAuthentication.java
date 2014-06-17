package xmpp.nasacj.woody;

import javax.security.auth.callback.CallbackHandler;

/**
 * There are two ways to authenticate a user with a server. Using SASL or Non-SASL
 * authentication. This interface makes {@link SASLAuthentication} and
 * {@link NonSASLAuthentication} polyphormic.
 *
 * @author Gaston Dombiak
 * @author Jay Kline
 */
interface UserAuthentication {

    /**
     * Authenticates the user with the server.  This method will return the full JID provided by
     * the server.  The server may assign a full JID with a username and resource different than
     * requested by this method.
     *
     * Note that using callbacks is the prefered method of authenticating users since it allows
     * more flexability in the mechanisms used.
     *
     * @param username the requested username (authorization ID) for authenticating to the server
     * @param resource the requested resource.
     * @param cbh the CallbackHandler used to obtain authentication ID, password, or other
     * information
     * @return the full JID provided by the server while binding a resource for the connection.
     * @throws XMPPException if an error occurs while authenticating.
     */
    String authenticate(String username, String resource, CallbackHandler cbh) throws
            XMPPException;

    /**
     * Authenticates the user with the server. This method will return the full JID provided by
     * the server. The server may assign a full JID with a username and resource different than
     * the requested by this method.
     *
     * It is recommended that @{link #authenticate(String, String, CallbackHandler)} be used instead
     * since it provides greater flexability in authenticaiton and authorization.
     *
     * @param username the username that is authenticating with the server.
     * @param password the password to send to the server.
     * @param resource the desired resource.
     * @return the full JID provided by the server while binding a resource for the connection.
     * @throws XMPPException if an error occures while authenticating.
     */
    String authenticate(String username, String password, String resource) throws
            XMPPException;

    /**
     * Performs an anonymous authentication with the server. The server will created a new full JID
     * for this connection. An exception will be thrown if the server does not support anonymous
     * authentication.
     *
     * @return the full JID provided by the server while binding a resource for the connection.
     * @throws XMPPException if an error occures while authenticating.
     */
    String authenticateAnonymously() throws XMPPException;
}
