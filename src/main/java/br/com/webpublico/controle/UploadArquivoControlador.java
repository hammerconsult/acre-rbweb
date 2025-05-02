/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.negocios.ArquivoFacade;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

/**
 * @author Munif
 */
@WebServlet(name = "UploadArquivoControlador", urlPatterns = {"/upload"})
public class UploadArquivoControlador extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        String proximaPagina = "../";
        PrintWriter out = response.getWriter();
        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            upload.setSizeMax(100 * 1024 * 1024);
            List items = upload.parseRequest(request);
            Iterator iter = items.iterator();
            while (iter.hasNext()) {
                FileItem fi = (FileItem) iter.next();
                if (!fi.isFormField()) {
                    Arquivo arquivo = new Arquivo();
                    arquivo.setDescricao("Novo Arquivo " + System.currentTimeMillis());
                    arquivo.setMimeType(fi.getContentType());
                    arquivo.setNome(fi.getName());
                    arquivoFacade.novoArquivo(arquivo, fi.getInputStream());
                }
            }
            //request.getRequestDispatcher(proximaPagina).forward(request, response);
            out.println("OK");
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            out.close();
        }

    }

    public void uploadArquivo(FileUploadEvent evento) {
        try {
            UploadedFile arquivo = evento.getFile();
            Arquivo arq = new Arquivo();
            arq.setNome(arquivo.getFileName());
            arq.setMimeType(arquivo.getContentType());
            arq.setDescricao("Arquivo upload");
            arquivoFacade.novoArquivo(arq, arquivo.getInputstream());
        } catch (Exception ex) {
            ex.printStackTrace();
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
            throws ServletException, IOException {
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
