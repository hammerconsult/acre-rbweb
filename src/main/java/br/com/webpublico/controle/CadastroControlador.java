/*
 * Codigo gerado automaticamente em Mon Feb 28 16:58:30 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Cadastro;
import br.com.webpublico.entidades.MigracaoHistorico;
import br.com.webpublico.negocios.CadastroFacade;
import br.com.webpublico.enums.Operacoes;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class CadastroControlador implements Serializable {

    @EJB
    private CadastroFacade cadastroFacade;
    private Cadastro selecionado;
    private Operacoes operacao;
    private String caminho;


    public CadastroControlador() {
    }

    public Cadastro getSelecionado() {
        return selecionado;
    }

    public String getCaminho() {
        return caminho;
    }

    public String caminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public void setSelecionado(Cadastro selecionado) {
        this.selecionado = selecionado;
    }

    public List<MigracaoHistorico> getLista() {
        if (selecionado != null) {
            return new ArrayList<MigracaoHistorico>();
        }
        return cadastroFacade.listaHistoricoMigracaoPorCadastro(selecionado);
    }

}
