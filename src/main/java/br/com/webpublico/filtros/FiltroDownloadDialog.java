package br.com.webpublico.filtros;

import com.google.common.base.Strings;
import org.primefaces.model.StreamedContent;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.faces.context.FacesContext;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FiltroDownloadDialog extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String filename = httpServletRequest.getParameter("filename");
        if (!Strings.isNullOrEmpty(filename)) {
            httpServletResponse.setHeader("Content-Disposition", "inline; filename=" + filename);
            ServletOutputStream servletOut = httpServletResponse.getOutputStream();
            StreamedContent relatorio = (StreamedContent) httpServletRequest.getSession().getAttribute("relatorio");
            InputStream is = relatorio.getStream();
            is.reset();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int reads = is.read();
            while (reads != -1) {
                baos.write(reads);
                reads = is.read();
            }
            httpServletResponse.setContentLength(baos.size());
            servletOut.write(baos.toByteArray());
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
