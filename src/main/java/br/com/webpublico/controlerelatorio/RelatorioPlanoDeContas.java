/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.HashMap;

/**
 * @author Romanini
 */
@SessionScoped
@ManagedBean
public class RelatorioPlanoDeContas extends AbstractReport implements Serializable {

    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public void gerarRelatorio(Long id) {
        try {
            String arquivoJasper = "relatorioplanodecontas.jasper";
            HashMap parameters = new HashMap();

            parameters.put("IMAGEM", getCaminhoImagem());
            //System.out.println("id ...: " + id.toString());
            parameters.put("ID", id.toString());

            if (sistemaControlador.getUsuarioCorrente().getPessoaFisica() != null) {
                parameters.put("USER", sistemaControlador.getUsuarioCorrente().getPessoaFisica().getNome());
            } else {
                parameters.put("USER", sistemaControlador.getUsuarioCorrente().getLogin());
            }

            String subReport1 = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF");
            subReport1 += "/report/";

            gerarRelatorio(arquivoJasper, parameters);
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, " ", "Ocorreu um erro ao gerar o relatório: " + ex.getMessage()));
        }
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
