/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.entidades.ArquivoParte;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.negocios.ArquivoFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonArquivosInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Terminal-2
 */
@WebServlet(name = "ForneceImagemUsuario", urlPatterns = {"/imagem-usuario"})
public class ForneceImagemUsuario extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ForneceImagemUsuario.class);
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        OutputStream out = response.getOutputStream();
        try {
            if (!request.getParameter("id").equals("")) {
                Long id = Long.parseLong(request.getParameter("id"));
                UsuarioSistema recuperar = usuarioSistemaFacade.recuperar(id);
                Arquivo arquivo = arquivoFacade.recupera(recuperar.getPessoaFisica().getArquivo().getId());
                if (arquivo != null) {
                    response.setContentType(arquivo.getMimeType());
                    for (ArquivoParte arquivoParte : arquivo.getPartes()) {
                        out.write(arquivoParte.getDados());
                    }
                } else {
                    out.write("/resources/images/user-default.jpg".getBytes());
                }
            } else {
                out.write("/resources/images/user-default.jpg".getBytes());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        } finally {
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
