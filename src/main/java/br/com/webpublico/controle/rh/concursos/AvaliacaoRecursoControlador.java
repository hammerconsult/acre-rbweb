package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.enums.rh.concursos.Abrangencia;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.concursos.AvaliacaoRecursoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Buzatto on 02/09/2015.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-avaliacao-recurso", pattern = "/concursos/avaliacao-recurso/novo/", viewId = "/faces/rh/concursos/avaliacao-recurso/edita.xhtml"),
    @URLMapping(id = "editar-avaliacao-recurso", pattern = "/concursos/avaliacao-recurso/editar/#{avaliacaoRecursoControlador.id}/", viewId = "/faces/rh/concursos/avaliacao-recurso/edita.xhtml"),
    @URLMapping(id = "ver-avaliacao-recurso", pattern = "/concursos/avaliacao-recurso/ver/#{avaliacaoRecursoControlador.id}/", viewId = "/faces/rh/concursos/avaliacao-recurso/edita.xhtml"),
    @URLMapping(id = "listar-avaliacao-recurso", pattern = "/concursos/avaliacao-recurso/listar/", viewId = "/faces/rh/concursos/avaliacao-recurso/lista.xhtml")
})
public class AvaliacaoRecursoControlador extends PrettyControlador<AvaliacaoRecurso> implements Serializable, CRUD {

    @EJB
    private AvaliacaoRecursoFacade avaliacaoRecursoFacade;
    private Concurso concursoSelecionado;
    private AvaliacaoProvaConcurso avaliacao;

    public AvaliacaoRecursoControlador() {
        super(AvaliacaoRecurso.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/avaliacao-recurso/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return avaliacaoRecursoFacade;
    }

    @Override
    @URLAction(mappingId = "novo-avaliacao-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setData(avaliacaoRecursoFacade.getConcursoFacade().getSistemaFacade().getDataOperacao());
    }

    @Override
    @URLAction(mappingId = "ver-avaliacao-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-avaliacao-recurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        recuperarDadosIniciais();
    }

    public void recuperarDadosIniciais() {
        concursoSelecionado = selecionado.getConcurso();
        avaliacao = getAvaliacaoDaProva(selecionado.getProvaConcurso());
    }

    @Override
    public void salvar() {
        if (podeSalvar()) {
            try {
                avaliacaoRecursoFacade.salvar(selecionado, avaliacao);
                FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                return;
            } catch (Exception e) {
                descobrirETratarException(e);
            }
            redireciona();
        }
    }

    @Override
    public void excluir() {
        super.excluir();
    }

    private boolean podeSalvar() {
        if (!Util.validaCampos(selecionado)) {
            return false;
        }
        if (!validarCampoAbrangencia()) {
            return false;
        }
        if (!temNotaLancadaParaCandidatosObrigatorios()) {
            return false;
        }
        return true;
    }

    private boolean validarCampoAbrangencia() {
        if (selecionado.isAceito() && !selecionado.isAbrangenciaInformada()) {
            FacesUtil.addCampoObrigatorio("O campo abrangência deve ser informado.");
            return false;
        }
        return true;
    }

    private boolean temNotaLancadaParaCandidatosObrigatorios() {
        if (selecionado.isAbrangenciaIndividual()) {
            if (!avaliacao.temNotaLancadaParaCandidato(selecionado.getRecursoConcurso().getInscricaoConcurso())) {
                FacesUtil.addCampoObrigatorio("É obrigatório lançar a nova nota do candidato solicitante do recurso.");
                return false;
            }
        }
        if (selecionado.isAbrangenciaGeral()) {
            if (!avaliacao.temNotaLancadaParaTodosCandidatos()) {
                FacesUtil.addCampoObrigatorio("É obrigatório lançar a nova nota para todos candidatos.");
                return false;
            }
        }
        return true;
    }

    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Selecione um Concurso..."));
        for (Concurso concurso : avaliacaoRecursoFacade.getConcursoFacade().getUltimosConcursosComRecurso()) {
            toReturn.add(new SelectItem(concurso, concurso.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getRecursos() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Selecione um Recurso..."));
        if (selecionado.temConcurso()) {
            Collections.sort(selecionado.getConcurso().getRecursos());
            for (RecursoConcurso recursoConcurso : selecionado.getConcurso().getRecursos()) {
                if (recursoConcurso.isSituacaoEmAndamento()) {
                    toReturn.add(new SelectItem(recursoConcurso, recursoConcurso.toString()));
                }
            }
        }
        return toReturn;
    }

    public List<SelectItem> getAbrangencias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Selecione a Abrangência..."));
        for (Abrangencia abrangencia : Abrangencia.values()) {
            toReturn.add(new SelectItem(abrangencia, abrangencia.toString()));
        }
        return toReturn;
    }

    public void carregarDadosDoConcurso() {
        if (concursoSelecionado != null) {
            concursoSelecionado = avaliacaoRecursoFacade.getConcursoFacade().recuperarConcursoParaTelaDeAvaliacaoRecurso(concursoSelecionado.getId());
            selecionado.setConcurso(concursoSelecionado);
        } else {
            cancelarConcursoAndRecursoAndFaseAndProvaAndAvaliacaoAndNovaAvaliacao();
        }
    }

    private void cancelarConcursoAndRecursoAndFaseAndProvaAndAvaliacaoAndNovaAvaliacao() {
        cancelarConcursoDaAvaliacao();
        cancelarRecursoDaAvaliacao();
        cancelarFaseAndProvaAndRecursoAceitoAndAbrangenciaAndAvaliacaoAndNovaAvaliacao();
    }

    public void cancelarRecursoDaAvaliacao() {
        selecionado.setRecursoConcurso(null);
    }

    public void cancelarConcursoDaAvaliacao() {
        selecionado.setConcurso(null);
        concursoSelecionado = null;
    }

    public void setarFaseAndProva() {
        if (selecionado.temRecurso()) {
            setarFase();
            setarProva();
        } else {
            cancelarFaseAndProvaAndRecursoAceitoAndAbrangenciaAndAvaliacaoAndNovaAvaliacao();
        }
    }

    private void cancelarFaseAndProvaAndRecursoAceitoAndAbrangenciaAndAvaliacaoAndNovaAvaliacao() {
        cancelarFaseDaAvaliacao();
        cancelarProvaDaAvaliacao();
        cancelarRecursoAceito();
        cancelarAbrangencia();
        cancelarAvaliacao();
    }

    private void cancelarRecursoAceito() {
        selecionado.setRecursoAceito(null);
    }

    private AvaliacaoProvaConcurso getAvaliacaoDaProva(ProvaConcurso prova) {
        return avaliacaoRecursoFacade.getAvaliacaoProvaConcursoFacade().buscarAvaliacaoPorProva(prova);
    }

    private void cancelarAvaliacao() {
        setAvaliacao(null);
    }

    public void cancelarProvaDaAvaliacao() {
        selecionado.setProvaConcurso(null);
    }

    public void cancelarFaseDaAvaliacao() {
        selecionado.setFaseConcurso(null);
    }

    public void setarProva() {
        if (selecionado.getRecursoConcurso().temProva()) {
            selecionado.setProvaConcurso(selecionado.getRecursoConcurso().getProvaConcurso());
        }
    }

    public void setarFase() {
        if (selecionado.getRecursoConcurso().temFase()) {
            selecionado.setFaseConcurso(selecionado.getRecursoConcurso().getFaseConcurso());
        }
    }

    public Concurso getConcursoSelecionado() {
        return concursoSelecionado;
    }

    public void setConcursoSelecionado(Concurso concursoSelecionado) {
        this.concursoSelecionado = concursoSelecionado;
    }

    public AvaliacaoProvaConcurso getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(AvaliacaoProvaConcurso avaliacao) {
        this.avaliacao = avaliacao;
    }

    public boolean mostraInfosDoRecurso() {
        return selecionado.temConcurso() && selecionado.temRecurso();
    }

    public void processarAvaliacao() {
        if (selecionado.isAceito()) {
            avaliacao = avaliacaoRecursoFacade.getAvaliacaoProvaConcursoFacade().buscarAvaliacaoPorProva(selecionado.getProvaConcurso());
        } else {
            cancelarAvaliacao();
        }
        cancelarAbrangencia();
    }

    private void cancelarAbrangencia() {
        selecionado.setAbrangencia(null);
    }

    public boolean podeLancarNovaNota(NotaCandidatoConcurso nota) {
        if (selecionado.isAbrangenciaGeral()) {
            return true;
        }
        if (isAbrangenciaIndividual() && isCandidatoDaNotaIgualAoCandidatoSolicitanteDoRecurso(nota)) {
            return true;
        }
        return false;
    }

    public boolean isCandidatoDaNotaIgualAoCandidatoSolicitanteDoRecurso(NotaCandidatoConcurso nota) {
        if (selecionado.temRecurso()) {
            return nota.getInscricao().equals(selecionado.getRecursoConcurso().getInscricaoConcurso());
        }
        return false;
    }

    public boolean isAbrangenciaIndividual() {
        return selecionado.isAbrangenciaIndividual();
    }
}
