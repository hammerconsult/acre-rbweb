/*
 * Codigo gerado automaticamente em Fri Nov 11 10:09:29 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.ContratoVeiculoDePublicacao;
import br.com.webpublico.entidades.Entidade;
import br.com.webpublico.entidades.VeiculoDePublicacao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EntidadeFacade;
import br.com.webpublico.negocios.VeiculoDePublicacaoFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "veiculoDePublicacaoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-veiculo-publicacao",   pattern = "/veiculo-publicacao/novo/",                                        viewId = "/faces/tributario/cadastromunicipal/veiculodepublicacao/edita.xhtml"),
        @URLMapping(id = "editar-veiculo-publicacao", pattern = "/veiculo-publicacao/editar/#{veiculoDePublicacaoControlador.id}/", viewId = "/faces/tributario/cadastromunicipal/veiculodepublicacao/edita.xhtml"),
        @URLMapping(id = "ver-veiculo-publicacao",    pattern = "/veiculo-publicacao/ver/#{veiculoDePublicacaoControlador.id}/",    viewId = "/faces/tributario/cadastromunicipal/veiculodepublicacao/visualizar.xhtml"),
        @URLMapping(id = "listar-veiculo-publicacao", pattern = "/veiculo-publicacao/listar/",                                      viewId = "/faces/tributario/cadastromunicipal/veiculodepublicacao/lista.xhtml")
})
public class VeiculoDePublicacaoControlador extends PrettyControlador<VeiculoDePublicacao> implements Serializable, CRUD {

    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    private List<ContratoVeiculoDePublicacao> listaContratoVP;
    private ContratoVeiculoDePublicacao contratoVeiculoDePublicacaoAux;
    @EJB
    private EntidadeFacade entidadeFacade;
    private ConverterAutoComplete converterEntidade;

    public VeiculoDePublicacaoControlador() {
        super(VeiculoDePublicacao.class);
    }

    public VeiculoDePublicacaoFacade getFacade() {
        return veiculoDePublicacaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return veiculoDePublicacaoFacade;
    }

    public List<ContratoVeiculoDePublicacao> getListaContratoVP() {
        return listaContratoVP;
    }

    public void setListaContratoVP(List<ContratoVeiculoDePublicacao> listaContratoVP) {
        this.listaContratoVP = listaContratoVP;
    }

    public ContratoVeiculoDePublicacao getContratoVeiculoDePublicacaoAux() {
        return contratoVeiculoDePublicacaoAux;
    }

    public void setContratoVeiculoDePublicacaoAux(ContratoVeiculoDePublicacao contratoVeiculoDePublicacaoAux) {
        this.contratoVeiculoDePublicacaoAux = contratoVeiculoDePublicacaoAux;
    }

    public ConverterAutoComplete getConverterEntidade() {
        if (converterEntidade == null) {
            converterEntidade = new ConverterAutoComplete(Entidade.class, entidadeFacade);
        }
        return converterEntidade;
    }

    public List<Entidade> completaEntidade(String parte) {
        return entidadeFacade.listaFiltrando(parte.trim(), "nome");
    }

    public void adicionarContrato() {
        if (validaAdicinar()) {
            contratoVeiculoDePublicacaoAux.setVeiculoDePublicacao(((VeiculoDePublicacao) selecionado));
            selecionado.getListaContrato().add(contratoVeiculoDePublicacaoAux);
            contratoVeiculoDePublicacaoAux = new ContratoVeiculoDePublicacao();
        }
    }


    public Boolean validaAdicinar() {
        Boolean retorno = Boolean.TRUE;
        if (contratoVeiculoDePublicacaoAux.getInicioContratacao() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " Campo Início de Contratação é obrigatório para adiconar."));
            retorno = Boolean.FALSE;
        }
        if (contratoVeiculoDePublicacaoAux.getFinalContratacao() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " Campo Fim de Contratação é obrigatório para adiconar."));
            retorno = Boolean.FALSE;
        }
        if (contratoVeiculoDePublicacaoAux.getContratante() == null) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Campo obrigatório! ", " Campo Contratante é obrigatório para adiconar."));
            retorno = Boolean.FALSE;
        }
        for (ContratoVeiculoDePublicacao cvp : selecionado.getListaContrato()) {
            if (cvp.getContratante().equals(contratoVeiculoDePublicacaoAux.getContratante())) {
                FacesContext.getCurrentInstance().addMessage("Formulario:panelContratoVP", new FacesMessage(FacesMessage.SEVERITY_ERROR, "Contrato já existe!", "O Contrato já foi adicionado na lista."));
                retorno = Boolean.FALSE;
            }
        }
        return retorno;
    }

    public void removerContrato() {
        selecionado.getListaContrato().remove(contratoVeiculoDePublicacaoAux);
        contratoVeiculoDePublicacaoAux = new ContratoVeiculoDePublicacao();
    }

    @URLAction(mappingId = "novo-veiculo-publicacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        contratoVeiculoDePublicacaoAux = new ContratoVeiculoDePublicacao();
    }

    @URLAction(mappingId = "ver-veiculo-publicacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaEditaVer();
    }

    @URLAction(mappingId = "editar-veiculo-publicacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperaEditaVer();
        contratoVeiculoDePublicacaoAux = new ContratoVeiculoDePublicacao();
    }

    public void recuperaEditaVer() {
        selecionado = veiculoDePublicacaoFacade.recuperar(selecionado.getId());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/veiculo-publicacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}
