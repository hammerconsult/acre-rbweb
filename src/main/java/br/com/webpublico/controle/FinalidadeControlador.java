/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Finalidade;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.FinalidadeFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;

/**
 * @author leonardo
 */
@ManagedBean(name = "finalidadeControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoFinalidade", pattern = "/tributario/configuracoes/certidoes/finalidade/novo/", viewId = "/faces/tributario/certidao/finalidade/edita.xhtml"),
        @URLMapping(id = "listaFinalidade", pattern = "/tributario/configuracoes/certidoes/finalidade/listar/", viewId = "/faces/tributario/certidao/finalidade/lista.xhtml"),
        @URLMapping(id = "verFinalidade", pattern = "/tributario/configuracoes/certidoes/finalidade/ver/#{finalidadeControlador.id}/", viewId = "/faces/tributario/certidao/finalidade/visualizar.xhtml"),
        @URLMapping(id = "editarFinalidade", pattern = "/tributario/configuracoes/certidoes/finalidade/editar/#{finalidadeControlador.id}/", viewId = "/faces/tributario/certidao/finalidade/edita.xhtml"),
})

public class FinalidadeControlador extends PrettyControlador<Finalidade> implements Serializable, CRUD {


    @EJB
    private FinalidadeFacade finalidadeFacade;
    private String caminho;

    public FinalidadeControlador() {
        super(Finalidade.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return finalidadeFacade;
    }

    public String getCaminho() {
        return caminho;
    }

    public void setCaminho(String caminho) {
        this.caminho = caminho;
    }

    public String caminho() {
        return this.caminho;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/configuracoes/certidoes/finalidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

      @Override
    @URLAction(mappingId = "novoFinalidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        this.selecionado = new Finalidade();
        this.selecionado.setCodigo(this.finalidadeFacade.ultimoCodigoMaisUm());
    }


    @Override
    @URLAction(mappingId = "editarFinalidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
       super.editar();
    }

    @Override
    @URLAction(mappingId = "verFinalidade", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
       super.ver();
    }

    public List<Finalidade> getLista() {
        return this.finalidadeFacade.lista();
    }

    public void salvar() {

        if (validaCampos()) {
            try {
                if (this.selecionado.getId() == null) {
                    this.finalidadeFacade.salvarNovo(selecionado);
                } else {
                    this.finalidadeFacade.salvar(selecionado);
                }
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "", "Salvo com sucesso!"));
                redireciona();
            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Não foi possível continuar!", "Erro ao salvar " + e.getMessage()));
            }
        }
        return;
    }

    public void excluirSelecionado() {
        try {
            this.finalidadeFacade.remover(selecionado);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Este registro não pode ser excluído porque possui dependências."));
        }
    }

    private boolean validaCampos() {
        boolean retorno = true;
        if (selecionado.getCodigo() == null || selecionado.getCodigo() <= 0) {
            FacesContext.getCurrentInstance().addMessage("Formulario:codigo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe um código maior que zero."));
            retorno = false;
        } else if (selecionado.getId() == null && finalidadeFacade.existeCodigo(selecionado.getCodigo())) {
            selecionado.setCodigo(finalidadeFacade.ultimoCodigoMaisUm());
            FacesContext.getCurrentInstance().addMessage("Formulario:codigo", new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção!", "O Código informado já está em uso em outro registro. O sistema gerou um novo código. Por favor, pressione o botão SALVAR novamente."));
            retorno = false;
        } else if (selecionado.getId() != null && !finalidadeFacade.existeCodigoFinalidade(selecionado)) {
            FacesContext.getCurrentInstance().addMessage("Formulario:codigo", new FacesMessage(FacesMessage.SEVERITY_ERROR, "", "O Código informado já existe."));
            retorno = false;
        }
        if (this.selecionado.getDescricao() == null || "".equals(this.selecionado.getDescricao())) {
            FacesContext.getCurrentInstance().addMessage("Formulario:descricao", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Não foi possível continuar!", "Informe a Descrição."));
            retorno = false;
        }
        return retorno;
    }

}
