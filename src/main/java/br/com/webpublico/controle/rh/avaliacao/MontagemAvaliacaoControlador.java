package br.com.webpublico.controle.rh.avaliacao;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.avaliacao.MontagemAvaliacao;
import br.com.webpublico.entidades.rh.avaliacao.QuestaoAlternativa;
import br.com.webpublico.entidades.rh.avaliacao.QuestaoAvaliacao;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.avaliacao.MontagemAvaliacaoFacade;
import br.com.webpublico.negocios.rh.avaliacao.NivelRespostaFacade;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "montagemAvaliacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-montagem-avaliacao", pattern = "/avaliacao/montagem-avaliacao/novo/", viewId = "/faces/rh/avaliacao/montagem-avaliacao/edita.xhtml"),
    @URLMapping(id = "editar-montagem-avaliacao", pattern = "/avaliacao/montagem-avaliacao/editar/#{montagemAvaliacaoControlador.id}/", viewId = "/faces/rh/avaliacao/montagem-avaliacao/edita.xhtml"),
    @URLMapping(id = "ver-montagem-avaliacao", pattern = "/avaliacao/montagem-avaliacao/ver/#{montagemAvaliacaoControlador.id}/", viewId = "/faces/rh/avaliacao/montagem-avaliacao/visualizar.xhtml"),
    @URLMapping(id = "listar-montagem-avaliacao", pattern = "/avaliacao/montagem-avaliacao/listar/", viewId = "/faces/rh/avaliacao/montagem-avaliacao/lista.xhtml")
})
public class MontagemAvaliacaoControlador extends PrettyControlador<MontagemAvaliacao> implements Serializable, CRUD {

    @EJB
    private MontagemAvaliacaoFacade facade;
    @EJB
    private NivelRespostaFacade nivelRespostaFacade;
    private QuestaoAvaliacao questaoAvaliacao;
    private QuestaoAlternativa questaoAlternativa;

    public MontagemAvaliacaoControlador() {
        super(MontagemAvaliacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/avaliacao/montagem-avaliacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-montagem-avaliacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-montagem-avaliacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-montagem-avaliacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public QuestaoAvaliacao getQuestaoAvaliacao() {
        return questaoAvaliacao;
    }

    public void setQuestaoAvaliacao(QuestaoAvaliacao questaoAvaliacao) {
        this.questaoAvaliacao = questaoAvaliacao;
    }

    public QuestaoAlternativa getQuestaoAlternativa() {
        return questaoAlternativa;
    }

    public void setQuestaoAlternativa(QuestaoAlternativa questaoAlternativa) {
        this.questaoAlternativa = questaoAlternativa;
    }

    public void novaQuestao() {
        this.questaoAvaliacao = new QuestaoAvaliacao();
        this.questaoAvaliacao.setMontagemAvaliacao(selecionado);
    }

    public void confirmarQuestao() {
        if (questaoAvaliacao != null && questaoAvaliacao.getOrdem() != null && questaoAvaliacao.getQuestao() != null) {
            adicionarQuestao();
            cancelarPainelQuestao();
        }
    }

    public void adicionarQuestao() {
        selecionado.setQuestoes(Util.adicionarObjetoEmLista(selecionado.getQuestoes(), this.questaoAvaliacao));
    }


    public void cancelarPainelQuestao() {
        this.questaoAvaliacao = null;
    }

    public void removerQuestao(QuestaoAvaliacao questao) {
        if (selecionado.getQuestoes().contains(questao)) {
            selecionado.getQuestoes().remove(questao);
        }
    }

    public void vincularQuestaoAlternativa(QuestaoAvaliacao questao) {
        cancelarAlternativa();
        questaoAvaliacao = questao;
        questaoAlternativa = new QuestaoAlternativa();
        questaoAlternativa.setQuestaoAvaliacao(questao);
    }

    public void adicionarAlternativaNaQuestao() {
        if (questaoAvaliacao != null && questaoAlternativa != null) {
            questaoAvaliacao.setAlternativas(Util.adicionarObjetoEmLista(questaoAvaliacao.getAlternativas(), this.questaoAlternativa));
        }
        cancelarAlternativa();
    }

    public void cancelarAlternativa() {
        questaoAvaliacao = null;
        questaoAlternativa = null;
    }

    public List<SelectItem> getNiveis() {
        return Util.getListSelectItem(nivelRespostaFacade.lista(), false);
    }

}
