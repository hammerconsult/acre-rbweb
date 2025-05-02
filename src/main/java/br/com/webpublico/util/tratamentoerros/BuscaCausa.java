package br.com.webpublico.util.tratamentoerros;

import org.hibernate.StaleStateException;
import org.xml.sax.SAXException;

import javax.ejb.EJBException;
import javax.transaction.RollbackException;
import java.sql.SQLException;

/**
 * @author Gustavo
 */
public class BuscaCausa {

    public static Throwable desenrolarException(Throwable th) {
        if (th instanceof SAXException) {
            SAXException sax = (SAXException) th;
            if (sax.getException() != null) {
                return desenrolarException(sax.getException());
            }
        } else if (th instanceof SQLException) {
            SQLException sql = (SQLException) th;
            if (sql.getNextException() != null) {
                return desenrolarException(sql.getNextException());
            }
        } else if (th instanceof StaleStateException || th instanceof RollbackException) {
            if (th.getCause() != null) {
                return desenrolarException(th.getCause());
            }
        } else if (th instanceof EJBException) {
            EJBException ejbException = (EJBException) th;
            Exception cause = ejbException.getCausedByException();
            while (null != cause && cause instanceof EJBException) {
                ejbException = (EJBException) cause;
                cause = ejbException.getCausedByException();
            }
            return desenrolarException(cause);
        } else if (th.getCause() != null) {
            return desenrolarException(th.getCause());
        }

        try {
            return th;
        } catch (ClassCastException cce) {
            return new Exception(th.getCause());
        }
    }

    public static String getCausedBy(Throwable aThrowable) {
        aThrowable = desenrolarException(aThrowable);
        final StringBuilder result = new StringBuilder(" ");
        final String NEW_LINE = System.getProperty("line.separator");
        for (StackTraceElement element : aThrowable.getStackTrace()) {
            if (element.toString().contains("br.com.webpublico.") || element.toString().contains("Caused by: ")) {
                result.append(element);
                result.append(NEW_LINE);
            }
        }
        return result.toString();
    }
}
