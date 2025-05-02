/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.negocios.UnidadeOrganizacionalFacade;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author andregustavo
 */
@ManagedBean
public class RelatorioUFControle extends AbstractReport implements Serializable {

    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public void setUnidadeOrganizacionalFacade(UnidadeOrganizacionalFacade unidadeOrganizacionalFacade) {
        this.unidadeOrganizacionalFacade = unidadeOrganizacionalFacade;
    }

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public String geraUrlImagemDir() {
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        final StringBuffer url = request.getRequestURL();
        String test = url.toString();
        String[] quebrado = test.split("/");
        StringBuilder b = new StringBuilder();
        b.append(quebrado[0]);
        b.append("/").append(quebrado[1]);
        b.append("/").append(quebrado[2]);
        b.append("/").append(quebrado[3]).append("/");
        return b.toString();
    }

    public void gerarRelatorioUF() throws JRException, IOException {
        String arquivoJasper = "ListaUfs.jasper";

        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.responseComplete();
        ServletContext scontext = (ServletContext) facesContext.getExternalContext().getContext();
        String subReport = scontext.getRealPath("/WEB-INF/report/Cabecalho_subreport1_1.jasper");

        HashMap parameters = new HashMap();
        String imgdef = geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif";
        //System.out.println("A URL é ---- " + imgdef);
        parameters.put("IMG", imgdef);
        parameters.put("UNIDADE_ORG", unidadeOrganizacionalFacade.getRaiz(sistemaControlador.getExercicioCorrente()));
        parameters.put("SUB", subReport);

        gerarRelatorio(arquivoJasper, parameters);
    }
}
