package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.concursos.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.concursos.*;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "avaliacaoProvaConcursoControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-avaliacao-prova-concurso", pattern = "/concursos/avaliacao-prova/novo/", viewId = "/faces/rh/concursos/avaliacao-provas/edita.xhtml"),
        @URLMapping(id = "editar-avaliacao-prova-concurso", pattern = "/concursos/avaliacao-prova/editar/#{avaliacaoProvaConcursoControlador.id}/", viewId = "/faces/rh/concursos/avaliacao-provas/edita.xhtml"),
        @URLMapping(id = "ver-avaliacao-prova-concurso", pattern = "/concursos/avaliacao-prova/ver/#{avaliacaoProvaConcursoControlador.id}/", viewId = "/faces/rh/concursos/avaliacao-provas/edita.xhtml"),
        @URLMapping(id = "listar-avaliacao-prova-concurso", pattern = "/concursos/avaliacao-prova/listar/", viewId = "/faces/rh/concursos/avaliacao-provas/lista.xhtml")
})
public class AvaliacaoProvaConcursoControlador extends PrettyControlador<AvaliacaoProvaConcurso> implements Serializable, CRUD {

    @EJB
    private AvaliacaoProvaConcursoFacade avaliacaoProvaConcursoFacade;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private CargoConcursoFacade cargoConcursoFacade;
    @EJB
    private FaseConcursoFacade faseConcursoFacade;
    @EJB
    private ProvaConcursoFacade provaConcursoFacade;
    @EJB
    private InscricaoConcursoFacade inscricaoConcursoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private FaseConcurso faseSelecionada;
    private CargoConcurso cargoSelecionado;
    private NotaCandidatoConcurso notaSelecionada;

    public NotaCandidatoConcurso getNotaSelecionada() {
        return notaSelecionada;
    }

    public void setNotaSelecionada(NotaCandidatoConcurso notaSelecionada) {
        this.notaSelecionada = notaSelecionada;
    }

    public AvaliacaoProvaConcursoControlador() {
        super(AvaliacaoProvaConcurso.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return avaliacaoProvaConcursoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/avaliacao-prova/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-avaliacao-prova-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setInicioRecurso(DataUtil.adicionaDias(UtilRH.getDataOperacao(), 1));
        selecionado.setFimRecurso(DataUtil.adicionarMeses(selecionado.getInicioRecurso(), 1));
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
    }

    @URLAction(mappingId = "ver-avaliacao-prova-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        carregarParametrosIniciais();
    }

    @URLAction(mappingId = "editar-avaliacao-prova-concurso", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        carregarParametrosIniciais();
    }

    private void carregarParametrosIniciais() {
        selecionado = avaliacaoProvaConcursoFacade.recuperarAvaliacaoComNotas(getId());
        faseSelecionada = selecionado.getProva().getFaseConcurso();
        cargoSelecionado = selecionado.getProva().getCargoConcurso();
    }

    public ConverterAutoComplete getConverterCargoConcurso() {
        return new ConverterAutoComplete(CargoConcurso.class, cargoConcursoFacade);
    }

    public ConverterAutoComplete getConverterProvaConcurso() {
        return new ConverterAutoComplete(ProvaConcurso.class, provaConcursoFacade);
    }

    public Converter getConverterConcurso() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {

                try {
                    return concursoFacade.recuperarConcursosParaTelaAvaliacaoDeProvas(Long.parseLong(value));
                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                String resultado = null;
                if (value == null) {
                    return null;
                } else {
                    if (value instanceof Long) {
                        resultado = String.valueOf(value);
                    } else {
                        try {
                            return "" + ((Concurso) value).getId();
                        } catch (Exception e) {
                            resultado = String.valueOf(value);
                        }
                    }
                }
                return resultado;
            }
        };
    }

    public Converter getConverterFase() {
        return new Converter() {
            @Override
            public Object getAsObject(FacesContext context, UIComponent component, String value) {

                try {
                    return faseConcursoFacade.recuperarFaseComProvas(Long.parseLong(value));
                } catch (Exception ex) {
                }
                return null;
            }

            @Override
            public String getAsString(FacesContext context, UIComponent component, Object value) {
                String resultado = null;
                if (value == null) {
                    return null;
                } else {
                    if (value instanceof Long) {
                        resultado = String.valueOf(value);
                    } else {
                        try {
                            return "" + ((FaseConcurso) value).getId();
                        } catch (Exception e) {
                            resultado = String.valueOf(value);
                        }
                    }
                }
                return resultado;
            }
        };
    }

    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, "Selecione um Concurso..."));
        for (Concurso c : concursoFacade.getUltimosConcursosQuePossuemFases()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public List<SelectItem> getFases() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (selecionado.getConcurso() == null) {
            toReturn.add(new SelectItem(null, ""));
            return toReturn;
        }
        toReturn.add(new SelectItem(null, "Selecione uma Fase..."));
        Util.ordenarListas(selecionado.getConcurso().getFases());
        for (FaseConcurso f : selecionado.getConcurso().getFases()) {
            toReturn.add(new SelectItem(f, "" + f));
        }
        return toReturn;
    }

    public List<SelectItem> getCargos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (selecionado.getConcurso() == null) {
            toReturn.add(new SelectItem(null, ""));
            return toReturn;
        }
        toReturn.add(new SelectItem(null, "Selecione um Cargo..."));
        for (CargoConcurso c : selecionado.getConcurso().getCargos()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public List<SelectItem> getProvas() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        if (faseSelecionada == null || cargoSelecionado == null) {
            toReturn.add(new SelectItem(null, ""));
            return toReturn;
        }
        toReturn.add(new SelectItem(null, "Selecione uma Prova..."));
        for (ProvaConcurso p : faseSelecionada.getProvas()) {
            if (p.getCargoConcurso().equals(cargoSelecionado)) {
                toReturn.add(new SelectItem(p, "" + p));
            }
        }
        return toReturn;
    }

    public FaseConcurso getFaseSelecionada() {
        return faseSelecionada;
    }

    public void setFaseSelecionada(FaseConcurso faseSelecionada) {
        this.faseSelecionada = faseSelecionada;
    }

    public CargoConcurso getCargoSelecionado() {
        return cargoSelecionado;
    }

    public void setCargoSelecionado(CargoConcurso cargoSelecionado) {
        this.cargoSelecionado = cargoSelecionado;
    }

    public void limparCamposConsulta() {
        faseSelecionada = null;
        cargoSelecionado = null;
        selecionado.setProva(null);
    }

    private boolean possuiInscricoes(List<InscricaoConcurso> inscricoes) {
        if (inscricoes == null || inscricoes.isEmpty()) {
            FacesUtil.addOperacaoNaoRealizada("Não foram localizados candidatos inscritos para o cargo informado");
            return false;
        }
        return true;
    }

    private boolean jaForamLancadasNotasParaProvaInformada() {
        if (provaConcursoFacade.jaForamLancadasNotasParaProvaInformada(selecionado.getProva())) {
            FacesUtil.addOperacaoNaoPermitida("Esta prova já foi avaliada. Verifique as informações e tente novamente.");
            return false;
        }
        return true;
    }

    public void carregarCandidatos() {
        if (!validarCamposObrigatorios()) {
            return;
        }

        if (!jaForamLancadasNotasParaProvaInformada()) {
            return;
        }
        List<InscricaoConcurso> inscricoes = inscricaoConcursoFacade.getInscricoesDoCargo(cargoSelecionado);
        if (!possuiInscricoes(inscricoes)) {
            selecionado.setNotas(new ArrayList<NotaCandidatoConcurso>());
            return;
        }

        criarListaDeNotasDosCandidatos(inscricoes);
    }

    private void criarListaDeNotasDosCandidatos(List<InscricaoConcurso> inscricoes) {
        for (InscricaoConcurso i : inscricoes) {
            if (selecionado.jaPossuiCandidatoAdicionado(i)) {
                continue;
            }
            NotaCandidatoConcurso nota = new NotaCandidatoConcurso();
            nota.setAvaliacaoProvaConcurso(selecionado);
            nota.setInscricao(i);
            selecionado.setNotas(Util.adicionarObjetoEmLista(selecionado.getNotas(), nota));
        }
    }

    public List<SelectItem> getStatusAprovadoNaoAprovado() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(new BigDecimal("10"), "APROVADO"));
        toReturn.add(new SelectItem(new BigDecimal("0"), "NÃO APROVADO"));
        return toReturn;
    }

    public void selecionarNota(NotaCandidatoConcurso nota) {
        notaSelecionada = nota;
    }

    public void cancelarNota() {
        notaSelecionada = null;
    }

    public void confirmarNota() {
        selecionado.setNotas(Util.adicionarObjetoEmLista(selecionado.getNotas(), notaSelecionada));
        cancelarNota();
    }

    private boolean validarCamposObrigatorios() {
        boolean retorno = true;
        if (!Util.validaCampos(selecionado)) {
            retorno = false;
        }

        if (faseSelecionada == null) {
            FacesUtil.addCampoObrigatorio("O campo Fase deve ser informado.");
            retorno = false;
        }

        if (cargoSelecionado == null) {
            FacesUtil.addCampoObrigatorio("O campo Cargo deve ser informado.");
            retorno = false;
        }

        return retorno;
    }

    private boolean existeNotaForaDosLimites() {
        boolean resultado = true;
        for (NotaCandidatoConcurso nota : selecionado.getNotas()) {
            if (nota.getNota() == null || nota.getNota().toString().trim().isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("O nota do candidato " + nota.getInscricao() + " deve ser informada.");
                resultado = false;
                continue;
            }
            if (nota.getNota().compareTo(BigDecimal.ZERO) < 0 || nota.getNota().compareTo(BigDecimal.TEN) > 0) {
                FacesUtil.addOperacaoNaoPermitida("O candidato " + nota.getInscricao() + " está com a nota fora dos parâmetros permitidos. Por favor, informe um valor entre 0(zero) e 10(dez).");
                resultado = false;
            }
        }

        return resultado;
    }

    private boolean existemCandidatosComNotas() {
        if (selecionado.getNotas() == null || selecionado.getNotas().isEmpty()) {
            FacesUtil.addOperacaoNaoPermitida("Não foram carregados candidatos/informadas notas dos candidatos.");
            return false;
        }
        return true;
    }

    @Override
    public void salvar() {
        if (isOperacaoNovo()) {
            selecionado.setOrdem(avaliacaoProvaConcursoFacade.getProximaOrdemAvaliacao(selecionado.getProva().getId()));
        }
        if (!validarCamposObrigatorios()) {
            return;
        }
        if (!existeNotaForaDosLimites()) {
            return;
        }
        if (!existemCandidatosComNotas()) {
            return;
        }
        super.salvar();
    }
}
