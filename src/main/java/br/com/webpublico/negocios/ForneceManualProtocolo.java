package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.ConfiguracaoProtocoloFacade;
import br.com.webpublico.entidades.Arquivo;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@WebServlet(name = "forneceManualProtocolo", urlPatterns = {"/manualprotocolo"})
public class ForneceManualProtocolo extends HttpServlet{

    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private ConfiguracaoProtocoloFacade configuracaoProtocoloFacade;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        OutputStream out = response.getOutputStream();
        try {
            Arquivo arquivo = configuracaoProtocoloFacade.retornaUltima().getArquivoManual();
            if (arquivo.getId() != null) {
                response.setContentType(arquivo.getMimeType());
                arquivoFacade.recupera(arquivo.getId(), out);
            } else {
                out.write("ARQUIVO NÃO ENCONTRADO".getBytes());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            out.close();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
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
