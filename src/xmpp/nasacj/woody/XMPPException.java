package xmpp.nasacj.woody;


import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * A generic exception that is thrown when an error occurs performing an
 * XMPP operation. XMPP servers can respond to error conditions with an error code
 * and textual description of the problem, which are encapsulated in the XMPPError
 * class. When appropriate, an XMPPError instance is attached instances of this exception.<p>
 *
 * When a stream error occured, the server will send a stream error to the client before
 * closing the connection. Stream errors are unrecoverable errors. When a stream error
 * is sent to the client an XMPPException will be thrown containing the StreamError sent
 * by the server.
 *
 * @see XMPPError
 * @author Matt Tucker
 */
public class XMPPException extends Exception {

//    private StreamError streamError = null;
//    private XMPPError error = null;
//    private Throwable wrappedThrowable = null;

    /**
     * Creates a new XMPPException.
     */
    public XMPPException() {
        super();
    }
}