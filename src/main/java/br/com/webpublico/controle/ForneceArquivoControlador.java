/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.negocios.ArquivoFacade;
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
 * @author Munif
 */
//TODO Proteger os arquivos por nível de acesso
@WebServlet(name = "ForneceArquivoControlador", urlPatterns = {"/arquivos/*"})
public class ForneceArquivoControlador extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(ForneceArquivoControlador.class);

    @EJB
    private ArquivoFacade arquivoFacade;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        OutputStream out = response.getOutputStream();
        try {
            if (!request.getParameter("id").equals("")) {
                Long id = Long.parseLong(request.getParameter("id").replaceAll("[^0-9]", ""));
                Arquivo arquivo = arquivoFacade.recupera(id);
                if (arquivo != null) {
                    response.setContentType(arquivo.getMimeType());
                    arquivoFacade.recupera(id, out);
                } else {
                    out.write("SEM ARQUIVO".getBytes());
                }
            }
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
        } finally {
            out.close();
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException,
        IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException,
        IOException {
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
