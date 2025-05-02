package br.com.webpublico.listeners;

import com.ocpsoft.pretty.PrettyContext;
import com.ocpsoft.pretty.faces.config.mapping.UrlMapping;
import com.ocpsoft.pretty.faces.config.rewrite.RewriteRule;
import com.ocpsoft.pretty.faces.rewrite.Processor;
import com.ocpsoft.pretty.faces.url.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ModificadorURL implements Processor {

    @Override
    public String processInbound(HttpServletRequest hsr, HttpServletResponse hsr1, RewriteRule rr, String string) {
        if (string.endsWith("/")) {
            return string;
        }

        PrettyContext context = PrettyContext.getCurrentInstance(hsr);
        URL u = context.getRequestURL();

        String toReturn = string;
        if (string.endsWith(".xhtml")) {
            for (UrlMapping map : context.getConfig().getMappings()) {
                if (u.toURL().equals("/faces/login.xhtml")) {
                    break;
                }
                if (map.getViewId().equals(u.toURL()) && !map.getPattern().contains("#")) {
                    toReturn = map.getPattern();
                    break;
                }
            }
        }

        if (!context.getConfig().isURLMapped(u)) {
            URL u2 = new URL(u + "/");
            if (context.getConfig().isURLMapped(u2)) {
                toReturn += "/";
            }
        }

        return toReturn;
    }

    @Override
    public String processOutbound(HttpServletRequest hsr, HttpServletResponse hsr1, RewriteRule rr, String string) {
        return string;
    }

}



