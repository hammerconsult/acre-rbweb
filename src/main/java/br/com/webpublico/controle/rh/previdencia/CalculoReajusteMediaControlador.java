package br.com.webpublico.controle.rh.previdencia;

import br.com.webpublico.DateUtils;
import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.configuracao.ReajusteMediaAposentadoria;
import br.com.webpublico.entidades.rh.previdencia.ReajusteAplicado;
import br.com.webpublico.entidadesauxiliares.ProcessoCalculoReajuste;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.configuracao.ReajusteAplicadoFacade;
import br.com.webpublico.negocios.rh.configuracao.ReajusteMediaAposentadoriaFacade;
import br.com.webpublico.negocios.rh.previdencia.CalculoReajusteMediaFacade;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.ComparisonChain;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * @Author peixe on 18/01/2016  11:33.
 */
@ManagedBean(name = "calculoReajusteMediaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "processo-calculo-reajuste-media", pattern = "/previdencia/calculo-reajuste-media/novo/", viewId = "/faces/rh/previdencia/calculo-reajuste-media/processo.xhtml"),
    @URLMapping(id = "editar-calculo-reajuste-media", pattern = "/previdencia/calculo-reajuste-media/editar/#{calculoReajusteMediaControlador.id}/", viewId = "/faces/rh/previdencia/calculo-reajuste-media/processo.xhtml"),
    @URLMapping(id = "listar-calculo-reajuste-media", pattern = "/previdencia/calculo-reajuste-media/listar/", viewId = "/faces/rh/previdencia/calculo-reajuste-media/lista.xhtml"),
    @URLMapping(id = "ver-calculo-reajuste-media", pattern = "/previdencia/calculo-reajuste-media/ver/#{calculoReajusteMediaControlador.id}/", viewId = "/faces/rh/previdencia/calculo-reajuste-media/visualizar.xhtml"),
})
public class CalculoReajusteMediaControlador extends PrettyControlador<ReajusteAplicado> implements Serializable, CRUD {

    private List<ReajusteMediaAposentadoria> reajustesMediaAposentadoria;
    private List<ProcessoCalculoReajuste> calculos;

    @EJB
    private ReajusteAplicadoFacade reajusteAplicadoFacade;
    @EJB
    private ReajusteMediaAposentadoriaFacade reajusteMediaAposentadoriaFacade;
    @EJB
    private CalculoReajusteMediaFacade calculoReajusteMediaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private PensionistaFacade pensionistaFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    private ConverterAutoComplete converterReajuste;
    private Boolean todosItensMarcados;

    @Override
    public String getCaminhoPadrao() {
        return "/previdencia/calculo-reajuste-media/";
    }

    public CalculoReajusteMediaControlador() {
        super(ReajusteAplicado.class);
    }

    @Override
    public Object getUrlKeyValue() {
        return getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return calculoReajusteMediaFacade;
    }

    public List<ReajusteMediaAposentadoria> getReajustesMediaAposentadoria() {
        return reajustesMediaAposentadoria;
    }

    public List<ProcessoCalculoReajuste> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<ProcessoCalculoReajuste> calculos) {
        this.calculos = calculos;
    }

    public void setReajustesMediaAposentadoria(List<ReajusteMediaAposentadoria> reajustesMediaAposentadoria) {
        this.reajustesMediaAposentadoria = reajustesMediaAposentadoria;
    }

    @URLAction(mappingId = "processo-calculo-reajuste-media", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        reajustesMediaAposentadoria = new LinkedList<>();
        calculos = new LinkedList<>();
        todosItensMarcados = Boolean.FALSE;
        List<Exercicio> exercicios = reajusteMediaAposentadoriaFacade.buscarExerciciosDoReajuste();
        if (exercicios != null && !exercicios.isEmpty()) {
            selecionado.setExercicio(exercicios.get(0));
            selecionado.setExercicioReferencia(exercicioFacade.getExercicioPorAno((exercicios.get(0).getAno() - 1)));
            selecionado.setInicioVigenciaReajustes(DateUtils.dataSemHorario(DateUtils.getData(1, 1, selecionado.getExercicio().getAno())));
        }
        selecionado.setDataReferencia(sistemaFacade.getDataOperacao());
    }

    @URLAction(mappingId = "ver-calculo-reajuste-media", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        reajustesMediaAposentadoria = new LinkedList<>();
        calculos = new LinkedList<>();
        buscarReajustesPorExercicio(selecionado.getExercicio(), selecionado.getExercicioReferencia());
        ordenarReajustes(reajustesMediaAposentadoria);
        todosItensMarcados = Boolean.FALSE;
    }

    private void buscarReajustesPorExercicio(Exercicio exercicioAplicacao, Exercicio exercicioReferencia) {
        reajustesMediaAposentadoria = calculoReajusteMediaFacade.buscarReajustesPorExercicio(exercicioAplicacao, exercicioReferencia, selecionado.getDataReferencia());
        for (ReajusteMediaAposentadoria reajusteMediaAposentadoria : reajustesMediaAposentadoria) {
            if (reajusteMediaAposentadoria.getProcessosCalculo() != null) {
                ordenarProcessos(reajusteMediaAposentadoria.getProcessosCalculo());
                calculos.addAll(reajusteMediaAposentadoria.getProcessosCalculo());
            }
        }
    }

    @URLAction(mappingId = "editar-calculo-reajuste-media", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        reajustesMediaAposentadoria = new LinkedList<>();
        calculos = new LinkedList<>();
        todosItensMarcados = Boolean.FALSE;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (Exercicio object : reajusteMediaAposentadoriaFacade.buscarExerciciosDoReajuste()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterReajuste() {
        if (converterReajuste == null) {
            converterReajuste = new ConverterAutoComplete(Exercicio.class, exercicioFacade);
        }
        return converterReajuste;
    }

    public void iniciarProcesso() {
        try {
            validarReajustes();
            calculos = new LinkedList<>();
            reajustesMediaAposentadoria = reajusteMediaAposentadoriaFacade.buscarReajustesVigentesPorExercicios(selecionado.getDataReferencia(), selecionado.getExercicio(), selecionado.getExercicioReferencia());
            Collections.sort(reajustesMediaAposentadoria);
            calculoReajusteMediaFacade.iniciarProcessamento(selecionado.getExercicioReferencia(), reajustesMediaAposentadoria, calculos, selecionado.getInicioVigenciaReajustes(), selecionado.getExercicio());
            if (calculos.isEmpty()) {
                FacesUtil.addOperacaoRealizada("Não foi encontrado nenhum item para aplicar o reajuste no exercício de " + selecionado.getExercicio());
            } else {
                FacesUtil.addOperacaoRealizada("Foram processados " + calculos.size() + " itens, para prosseguir com o reajuste, analise os dados e clique no botão <b>Efetivar Reajuste</b>");
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addError("Erro processando reajuste.", e.getMessage());
        }
    }

    private void validarReajustes() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado");
        } else {
            List<ReajusteMediaAposentadoria> reajusteMediaAposentadorias = reajusteMediaAposentadoriaFacade.buscarReajustesVigentesPorExercicios(selecionado.getDataReferencia(), selecionado.getExercicio(), selecionado.getExercicioReferencia());
            if (reajusteMediaAposentadorias.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Percentuais de reajuste não encontradas para o ano de aplicação " + selecionado.getExercicio().getAno() + " e ano de referência " + selecionado.getExercicioReferencia().getAno());
            }
        }
        if (selecionado.getInicioVigenciaReajustes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Início Vigência dos Reajustes deve ser informado");
        }
        ve.lancarException();
    }

    public void efetivarReajuste() {
        try {
            temCalculoMarcado();
            selecionado = getReajusteAplicadoPorExercicio(selecionado);
            selecionado.setAplicadoEm(sistemaFacade.getDataOperacao());
            List<ProcessoCalculoReajuste> calculosMarcados = getCalculosMarcados();
            calculoReajusteMediaFacade.efetivarReajuste(calculosMarcados, selecionado);
            FacesUtil.addOperacaoRealizada("Foi reajustado com sucesso os segurados da previdência para o exercício de " + selecionado.getExercicio());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro: ", e);
            FacesUtil.addError("Erro salvando reajuste.", e.getMessage());
        }
    }

    private boolean temCalculoMarcado() {
        ValidacaoException ve = new ValidacaoException();
        for (ReajusteMediaAposentadoria reajuste : reajustesMediaAposentadoria) {
            if (!reajuste.getCalculosMarcados().isEmpty()) {
                return true;
            }
        }
        ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório marcar pelo menos um cálculo!");
        throw ve;
    }

    public ReajusteAplicado getReajusteAplicadoPorExercicio(ReajusteAplicado reajusteAplicado) {
        try {
            reajusteAplicado = reajusteAplicadoFacade.buscarPorExercicioEDataReferenciaEInicioVigencia(reajusteAplicado.getExercicio(), reajusteAplicado.getExercicioReferencia(), reajusteAplicado.getDataReferencia(), reajusteAplicado.getInicioVigenciaReajustes());
        } catch (ExcecaoNegocioGenerica eng) {
            logger.debug(eng.getMessage());
        }
        return reajusteAplicado;
    }

    public void reverterReajustes() {
        try {
            temCalculoMarcado();
            List<ProcessoCalculoReajuste> calculos = getCalculosMarcados();

            for (ProcessoCalculoReajuste calculo : calculos) {
                VinculoFP vinculoFP = calculo.getVinculoFP();

                reverterReajustePensionista(calculo, vinculoFP);
                reverterReajusteAposentadoria(calculo, vinculoFP);
            }

            super.excluir();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
            FacesUtil.addError("Erro ao reverter reajustes.", ex.getMessage());
        }
    }

    @Override
    public boolean validaRegrasParaExcluir() {
        List<ItemValorPensionista> itensPensionista = pensionistaFacade.buscarItensComReajusteDoExercicio(selecionado.getExercicio(), selecionado.getExercicioReferencia());
        if (!itensPensionista.isEmpty()) {
            logger.debug("Foi encontrado item valor pensionista com reajuste para o ano " + selecionado.getExercicio().getAno() + ". O reajuste aplicado não será excluído!");
            redireciona();
            return false;
        }
        List<ItemAposentadoria> itensAposentadoria = aposentadoriaFacade.buscarItensComReajusteDoExercicio(selecionado.getExercicio(), selecionado.getExercicioReferencia());
        if (!itensAposentadoria.isEmpty()) {
            logger.debug("Foi encontrado item aposentadoria com reajuste para o ano " + selecionado.getExercicio().getAno() + ". O reajuste aplicado não será excluído!");
            redireciona();
            return false;
        }
        return true;
    }

    public void reverterReajusteAposentadoria(ProcessoCalculoReajuste calculo, VinculoFP vinculoFP) {
        if (vinculoFP instanceof Aposentadoria) {
            Aposentadoria aposentadoria = (Aposentadoria) vinculoFP;
            aposentadoria = aposentadoriaFacade.recuperarItensAposentadoria(aposentadoria.getId());

            List<ItemAposentadoria> itensReajustado = getItensReajustadosPorAposentado(calculo, aposentadoria);
            removerItensReajustadosPorAposentadoria(aposentadoria, itensReajustado);
            atualizarFinalVigenciaItensMantidosPorAposentadoria(aposentadoria, itensReajustado);

            aposentadoriaFacade.salvarAposentadoria(aposentadoria);
        }
    }

    public void reverterReajustePensionista(ProcessoCalculoReajuste calculo, VinculoFP vinculoFP) {
        if (vinculoFP instanceof Pensionista) {
            Pensionista pensionista = (Pensionista) vinculoFP;
            pensionista = pensionistaFacade.recuperarPensionistaComItens(pensionista.getId());

            List<ItemValorPensionista> itensReajustado = getItensReajustadosPorPensionista(calculo, pensionista);
            removerItensReajustadosPorPensionista(pensionista, itensReajustado);
            atualizarFinalVigenciaItensMantidosPorPensionista(pensionista, itensReajustado);

            pensionistaFacade.salvar(pensionista);
        }
    }

    public void atualizarFinalVigenciaItensMantidosPorPensionista(Pensionista pensionista, List<ItemValorPensionista> itensReajustado) {
        for (ItemValorPensionista itemReajustado : itensReajustado) {
            ItemValorPensionista ultimoItem = null;
            for (ItemValorPensionista item : pensionista.getItensValorPensionista()) {
                if (item.getEventoFP().equals(itemReajustado.getEventoFP())) {
                    if (item.getInicioVigencia().before(itemReajustado.getInicioVigencia()) && (ultimoItem == null || (ultimoItem.getInicioVigencia().before(item.getInicioVigencia())))) {
                        ultimoItem = item;
                    }
                }
            }
            if (ultimoItem != null) {
                ultimoItem.setFinalVigencia(itemReajustado.getFinalVigencia());
            }
        }
    }

    public void atualizarFinalVigenciaItensMantidosPorAposentadoria(Aposentadoria aposentadoria, List<ItemAposentadoria> itensReajustado) {
        for (ItemAposentadoria itemReajustado : itensReajustado) {
            ItemAposentadoria ultimoItem = null;
            for (ItemAposentadoria item : aposentadoria.getItemAposentadorias()) {
                if (item.getEventoFP().equals(itemReajustado.getEventoFP())) {
                    if (item.getInicioVigencia().before(itemReajustado.getInicioVigencia()) && (ultimoItem == null || (ultimoItem.getInicioVigencia().before(item.getInicioVigencia())))) {
                        ultimoItem = item;
                    }
                }
            }
            if (ultimoItem != null) {
                ultimoItem.setFinalVigencia(itemReajustado.getFinalVigencia());
            }
        }
    }

    public void removerItensReajustadosPorPensionista(Pensionista pensionista, List<ItemValorPensionista> itensReajustado) {
        for (ItemValorPensionista itemReajustado : itensReajustado) {
            pensionista.removerItem(itemReajustado);
        }
    }

    public void removerItensReajustadosPorAposentadoria(Aposentadoria aposentadoria, List<ItemAposentadoria> itensReajustado) {
        for (ItemAposentadoria itemReajustado : itensReajustado) {
            aposentadoria.removerItem(itemReajustado);
        }
    }

    public List<ItemValorPensionista> getItensReajustadosPorPensionista(ProcessoCalculoReajuste calculo, Pensionista pensionista) {
        List<ItemValorPensionista> itensReajustado = new ArrayList<>();
        for (ItemValorPensionista item : pensionista.getItensValorPensionista()) {
            if (item.temReajusteRecebido()) {
                if (item.equals(calculo.getItemValorPrevidenciaNovo())) {
                    itensReajustado = Util.adicionarObjetoEmLista(itensReajustado, item);
                }
            }
        }
        return itensReajustado;
    }

    public List<ItemAposentadoria> getItensReajustadosPorAposentado(ProcessoCalculoReajuste calculo, Aposentadoria aposentadoria) {
        List<ItemAposentadoria> itensReajustado = new ArrayList<>();
        for (ItemAposentadoria item : aposentadoria.getItemAposentadorias()) {
            if (item.temReajusteRecebido()) {
                if (item.equals(calculo.getItemValorPrevidenciaNovo())) {
                    itensReajustado = Util.adicionarObjetoEmLista(itensReajustado, item);
                }
            }
        }
        return itensReajustado;
    }

    public void marcarTodosItens(ReajusteMediaAposentadoria reajuste) {
        if (reajuste.getProcessosCalculo() != null) {
            for (ProcessoCalculoReajuste pcr : reajuste.getProcessosCalculo()) {
                if (!pcr.getProcessoTransiente()) {
                    marcarItem(pcr);
                }
            }
        }
    }

    public void marcarTodos() {
        if (reajustesMediaAposentadoria != null) {
            for (ReajusteMediaAposentadoria reajuste : reajustesMediaAposentadoria) {
                if (reajuste.getProcessosCalculo() != null) {
                    for (ProcessoCalculoReajuste pcr : reajuste.getProcessosCalculo()) {
                        if (!pcr.getReajusteMediaAposentadoria().getReajusteTransiente() && !pcr.getProcessoTransiente()) {
                            marcarItem(pcr);
                        }
                    }
                }
            }
            todosItensMarcados = Boolean.TRUE;
        }
    }

    public void desmarcarTodos() {
        if (reajustesMediaAposentadoria != null) {
            for (ReajusteMediaAposentadoria reajuste : reajustesMediaAposentadoria) {
                if (reajuste.getProcessosCalculo() != null) {
                    for (ProcessoCalculoReajuste pcr : reajuste.getProcessosCalculo()) {
                        if (!pcr.getReajusteMediaAposentadoria().getReajusteTransiente() && !pcr.getProcessoTransiente()) {
                            desmarcarItem(pcr);
                        }
                    }
                }
            }
            todosItensMarcados = Boolean.FALSE;
        }
    }


    public void desmarcarTodosItens(ReajusteMediaAposentadoria reajuste) {
        for (ProcessoCalculoReajuste pcr : reajuste.getProcessosCalculo()) {
            if (!pcr.getProcessoTransiente()) {
                desmarcarItem(pcr);
            }
        }
    }

    public void marcarItem(ProcessoCalculoReajuste calculo) {
        calculo.setSelecionado(Boolean.TRUE);
    }

    public void desmarcarItem(ProcessoCalculoReajuste calculo) {
        calculo.setSelecionado(Boolean.FALSE);
    }

    public Boolean getTodosItensMarcados() {
        return todosItensMarcados;
    }

    public void setTodosItensMarcados(Boolean todosItensMarcados) {
        this.todosItensMarcados = todosItensMarcados;
    }

    public List<ProcessoCalculoReajuste> getCalculosMarcados() {
        List<ProcessoCalculoReajuste> calculosMarcados = new ArrayList<>();
        for (ReajusteMediaAposentadoria reajuste : reajustesMediaAposentadoria) {
            for (ProcessoCalculoReajuste calculo : reajuste.getCalculosMarcados()) {
                calculosMarcados.add(calculo);
            }
        }
        return calculosMarcados;
    }

    private void ordenarReajustes(List<ReajusteMediaAposentadoria> reajustes) {
        Collections.sort(reajustes, new Comparator<ReajusteMediaAposentadoria>() {
            @Override
            public int compare(ReajusteMediaAposentadoria o1, ReajusteMediaAposentadoria o2) {
                return o1.getMes().getNumeroMes().compareTo(o2.getMes().getNumeroMes());
            }
        });
    }

    private void ordenarProcessos(List<ProcessoCalculoReajuste> cdas) {
        Collections.sort(cdas, new Comparator<ProcessoCalculoReajuste>() {
            @Override
            public int compare(ProcessoCalculoReajuste o1, ProcessoCalculoReajuste o2) {
                return ComparisonChain.start()
                    .compare(o1.getVinculoFP().getMatriculaFP().getPessoa().getNome(), o2.getVinculoFP().getMatriculaFP().getPessoa().getNome())
                    .compare(o1.getVinculoFP().getMatriculaFP().getMatricula(), o2.getVinculoFP().getMatriculaFP().getMatricula())
                    .compare(o1.getVinculoFP().getNumero(), o2.getVinculoFP().getNumero())
                    .result();
            }
        });
    }
}
