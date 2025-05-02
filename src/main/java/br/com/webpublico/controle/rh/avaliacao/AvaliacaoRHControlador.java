package br.com.webpublico.controle.rh.avaliacao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.avaliacao.AvaliacaoRH;
import br.com.webpublico.entidades.rh.avaliacao.AvaliacaoVinculoFP;
import br.com.webpublico.entidades.rh.avaliacao.ResumoRespostaDTO;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.avaliacao.AvaliacaoRHFacade;
import br.com.webpublico.negocios.rh.avaliacao.MontagemAvaliacaoFacade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "avaliacaoRHControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-avaliacao-rh", pattern = "/avaliacao/avaliacao-rh/novo/", viewId = "/faces/rh/avaliacao/avaliacao-rh/edita.xhtml"),
    @URLMapping(id = "editar-avaliacao-rh", pattern = "/avaliacao/avaliacao-rh/editar/#{avaliacaoRHControlador.id}/", viewId = "/faces/rh/avaliacao/avaliacao-rh/edita.xhtml"),
    @URLMapping(id = "ver-avaliacao-rh", pattern = "/avaliacao/avaliacao-rh/ver/#{avaliacaoRHControlador.id}/", viewId = "/faces/rh/avaliacao/avaliacao-rh/visualizar.xhtml"),
    @URLMapping(id = "listar-avaliacao-rh", pattern = "/avaliacao/avaliacao-rh/listar/", viewId = "/faces/rh/avaliacao/avaliacao-rh/lista.xhtml")
})
public class AvaliacaoRHControlador extends PrettyControlador<AvaliacaoRH> implements Serializable, CRUD {

    @EJB
    private AvaliacaoRHFacade facade;

    @EJB
    private MontagemAvaliacaoFacade montagemAvaliacaoFacade;
    private AvaliacaoVinculoFP avaliacaoVinculoFP;
    private List<ResumoRespostaDTO> respostas;
    private List<AvaliacaoVinculoFP> excluidos;

    public AvaliacaoRHControlador() {
        super(AvaliacaoRH.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/avaliacao/avaliacao-rh/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-avaliacao-rh", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-avaliacao-rh", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        excluidos = Lists.newArrayList();
        respostas = facade.buscarResumoRespostas(this.getId());
        buscarAvaliacoesVinculo();
    }

    @URLAction(mappingId = "editar-avaliacao-rh", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        excluidos = Lists.newArrayList();
        buscarAvaliacoesVinculo();
    }

    private void buscarAvaliacoesVinculo() {
        if (selecionado.getId() != null) {
            selecionado.setAvaliacaoVinculoFPs(facade.buscarAvaliacaoVinculoDTO(selecionado));
        }
    }

    public AvaliacaoVinculoFP getAvaliacaoVinculoFP() {
        return avaliacaoVinculoFP;
    }

    public void setAvaliacaoVinculoFP(AvaliacaoVinculoFP avaliacaoVinculoFP) {
        this.avaliacaoVinculoFP = avaliacaoVinculoFP;
    }

    public void novoAvaliado() {
        this.avaliacaoVinculoFP = new AvaliacaoVinculoFP();
        this.avaliacaoVinculoFP.setAvaliacaoRH(selecionado);
    }

    public void confirmarAvaliado() {
        if (avaliacaoVinculoFP != null && avaliacaoVinculoFP.getVinculoFP() != null) {
            adicionarQuestao();
            cancelarPainelAvaliado();
        }
    }

    public void adicionarQuestao() {
        selecionado.setAvaliacaoVinculoFPs(Util.adicionarObjetoEmLista(selecionado.getAvaliacaoVinculoFPs(), this.avaliacaoVinculoFP));
    }


    public void cancelarPainelAvaliado() {
        this.avaliacaoVinculoFP = null;
    }

    public void removerAvaliado(AvaliacaoVinculoFP avaliado) {
        if (selecionado.getAvaliacaoVinculoFPs().contains(avaliado)) {
            selecionado.getAvaliacaoVinculoFPs().remove(avaliado);
            excluidos.add(avaliado);
        }
    }

    public List<ResumoRespostaDTO> getRespostas() {
        return respostas;
    }

    public void setRespostas(List<ResumoRespostaDTO> respostas) {
        this.respostas = respostas;
    }

    public List<SelectItem> getMontagens() {
        return Util.getListSelectItem(montagemAvaliacaoFacade.lista(), false);
    }

    @Override
    public void salvar() {
        if (validaRegrasParaSalvar()) {
            if (Operacoes.NOVO.equals(operacao)) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
            String caminhoPadrao = ((CRUD) this).getCaminhoPadrao();
            Object key = ((CRUD) this).getUrlKeyValue();
            Redirecionar redirecionar = Redirecionar.LISTAR;
            switch (redirecionar) {
                case VER:
                    redireciona(caminhoPadrao + "ver/" + key);
                    break;
                case EDITAR:
                    redireciona(caminhoPadrao + "editar/" + key);
                    break;
                case LISTAR:
                    redireciona();
                    break;
            }
        }
    }
}
