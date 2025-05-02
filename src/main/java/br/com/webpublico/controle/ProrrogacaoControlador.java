package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.administracaodepagamento.LancamentoTercoFeriasAut;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.PropositoAtoLegal;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.administracaodepagamento.LancamentoTercoFeriasAutFacade;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.time.LocalDate;
import java.util.Map;

/**
 * Created by Buzatto on 19/07/2016.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoProrrogacao", pattern = "/provimento/prorrogacao/novo/", viewId = "/faces/rh/administracaodepagamento/provimentofp/prorrogacao/edita.xhtml"),
    @URLMapping(id = "editarProrrogacao", pattern = "/provimento/prorrogacao/editar/#{prorrogacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/provimentofp/prorrogacao/edita.xhtml"),
    @URLMapping(id = "verProrrogacao", pattern = "/provimento/prorrogacao/ver/#{prorrogacaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/provimentofp/prorrogacao/visualizar.xhtml"),
    @URLMapping(id = "listarProrrogacao", pattern = "/provimento/prorrogacao/listar/", viewId = "/faces/rh/administracaodepagamento/provimentofp/prorrogacao/lista.xhtml")
})
public class ProrrogacaoControlador extends PrettyControlador<Prorrogacao> implements CRUD, Serializable {

    @EJB
    private ProrrogacaoFacade prorrogacaoFacade;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private PeriodoAquisitivoFLFacade periodoAquisitivoFLFacade;
    @EJB
    private LancamentoTercoFeriasAutFacade lancamentoTercoFeriasAutFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private BaseCargoFacade baseCargoFacade;
    private Boolean temFolhaEfetivadaTercoOuCargoSemBasePA;
    private List<String> mensagens;

    public ProrrogacaoControlador() {
        super(Prorrogacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return prorrogacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/provimento/prorrogacao/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novoProrrogacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        validarProvimentoFPProrrogacao();
        atribuirDadosIniciais();
    }

    @Override
    @URLAction(mappingId = "verProrrogacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editarProrrogacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        temFolhaEfetivadaTercoOuCargoSemBasePA = Boolean.FALSE;
        mensagens = Lists.newArrayList();
    }

    @Override
    public void salvar() {
        try {
            validarProrrogacao();
            Map<PeriodoAquisitivoFL, LancamentoTercoFeriasAut> periodosParaExclusao = buscarPeriodosParaExclusao();

            if (isOperacaoNovo()) {
                prorrogacaoFacade.salvarNovo(selecionado, periodosParaExclusao, temFolhaEfetivadaTercoOuCargoSemBasePA);
            } else {
                prorrogacaoFacade.salvar(selecionado, periodosParaExclusao, temFolhaEfetivadaTercoOuCargoSemBasePA);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            for (String mensagem : mensagens) {
                FacesUtil.addAtencao(mensagem);
            }
            redireciona();
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public void excluir() {
        try {
            Map<PeriodoAquisitivoFL, LancamentoTercoFeriasAut> periodosParaExclusao = buscarPeriodosParaExclusao();
            prorrogacaoFacade.remover(selecionado, periodosParaExclusao, temFolhaEfetivadaTercoOuCargoSemBasePA);
            redireciona();
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoExcluir());
            for (String mensagem : mensagens) {
                FacesUtil.addAtencao(mensagem);
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada("Não foi possível excluír o registro: " + e.getMessage());
            logger.error("Nao foi possivel excluir a prorrogação: {}" + e);
        }
    }

    private Map<PeriodoAquisitivoFL, LancamentoTercoFeriasAut> buscarPeriodosParaExclusao() {
        ValidacaoException ve = new ValidacaoException();
        Map<PeriodoAquisitivoFL, LancamentoTercoFeriasAut> periodosParaExclusao = new HashMap<>();
        PeriodoAquisitivoFL ultimoPAConcedido = periodoAquisitivoFLFacade.recuperaUltimoPeriodoConcedidoPorContrato((ContratoFP) selecionado.getProvimentoFP().getVinculoFP());
        List<PeriodoAquisitivoFL> periodos = periodoAquisitivoFLFacade.recuperaPeriodosNaoConcedidosDepoisDaDataReferenciaPorContrato((ContratoFP) selecionado.getProvimentoFP().getVinculoFP(), ultimoPAConcedido != null ? ultimoPAConcedido.getFinalVigencia() : null);
        for (PeriodoAquisitivoFL pa : periodos) {
            LancamentoTercoFeriasAut tercoFerias = lancamentoTercoFeriasAutFacade.recuperaLancamentoTercoFeriasAutPorPeriodoAquisitivo(pa);
            if (tercoFerias != null) {
                List<FichaFinanceiraFP> fichas = fichaFinanceiraFPFacade.buscarFichasFinanceirasPorVinculoFPMesAnoTiposFolha(selecionado.getProvimentoFP().getVinculoFP(), Mes.getMesToInt(tercoFerias.getMes()).getNumeroMesIniciandoEmZero(), tercoFerias.getAno(), TipoFolhaDePagamento.NORMAL.name(), TipoFolhaDePagamento.COMPLEMENTAR.name());
                if (fichas == null || fichas.isEmpty()) {
                    periodosParaExclusao.put(pa, tercoFerias);
                } else {
                    for (FichaFinanceiraFP ficha : fichas) {
                        if (ficha.getFolhaDePagamento().getEfetivadaEm() != null) {
                            temFolhaEfetivadaTercoOuCargoSemBasePA = Boolean.TRUE;
                            mensagens.add("Foram encontradas as Folhas Normal ou Complementar EFETIVADAS para o servidor na competência: "
                                + tercoFerias.getMes() + "/" + tercoFerias.getAno() + ". " +
                                "Os períodos aquisitivos do servidor com lançamento de 1/3 de férias automático não poderão ser regerados. " +
                                "Por favor, entre em contato com o suporte técnico para mais detalhes.");
                        } else {
                            ve.adicionarMensagemDeOperacaoNaoPermitida("Foram encontradas as Folhas Normal ou Complementar EM ABERTO para o servidor com " +
                                "lançamento de 1/3 de férias automático na competência: " + tercoFerias.getMes() + "/" + tercoFerias.getAno() + ". " +
                                "Por favor, efetue a exclusão das fichas para poder incluir o lançamento da prorrogação. A inclusão da prorrogação afeta a " +
                                "geração de períodos aquisitivos que, por sua vez, afeta o cálculo de pagamento de 1/3 de férias automático. Uma vez incluída a prorrogação, a(s) ficha(s) financeira(s) deverão ser reprocessadas.");
                        }
                    }
                }
            } else {
                periodosParaExclusao.put(pa, null);
            }
        }
        List<BaseCargo> bases = Lists.newArrayList();
        if (selecionado.getProvimentoFP().getVinculoFP() != null && selecionado.getProvimentoFP().getVinculoFP().getCargo() != null) {
            bases = baseCargoFacade.buscarBasesDoCargo(selecionado.getProvimentoFP().getVinculoFP().getCargo(), UtilRH.getDataOperacao());
        }
        if (bases == null || bases.isEmpty()) {
            temFolhaEfetivadaTercoOuCargoSemBasePA = Boolean.TRUE;
            mensagens.add("Ausência de Base de Período Aquisitivo vinculado ao cargo. Períodos aquisitivos não foram atualizados.");
        }
        ve.lancarException();
        return periodosParaExclusao;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoParteEPropositoAtoLegal(parte, PropositoAtoLegal.ATO_DE_PESSOAL, "numero", "nome");
    }

    private void validarProrrogacao() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getProvimentoFP().getVinculoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado!");
            throw ve;
        }
        Util.validarCampos(selecionado);
    }

    private void validarProvimentoFPProrrogacao() {
        if (!tipoProvimentoFacade.existeTipoProvimentoPorCodigo(TipoProvimento.PRORROGACAO_CONTRATO)) {
            String link = "<a href=" + FacesUtil.getRequestContextPath() + "/tipoprovimento/novo/ target='blank' style='font-weight: bold !important; color: #497692; font-size:12px'>Clique aqui para cadastrar</a>";
            FacesUtil.addOperacaoNaoPermitida("Não foi encontrado <b>tipo de provimento</b> para prorrogação de contrado. Faça o cadastro do mesmo antes de cadastrar a prorrogação. " + link);
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "listar");
        }
    }

    private void atribuirDadosIniciais() {
        selecionado.getProvimentoFP().setDataProvimento(UtilRH.getDataOperacao());
        selecionado.getProvimentoFP().setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.PRORROGACAO_CONTRATO));
        temFolhaEfetivadaTercoOuCargoSemBasePA = Boolean.FALSE;
        mensagens = Lists.newArrayList();
    }

    public Date getDataValidacao() {
        VinculoFP vinculoFP = selecionado.getProvimentoFP().getVinculoFP();
        if (isOperacaoNovo()) {
            return vinculoFP.temFinalVigencia() ? vinculoFP.getFinalVigencia() : selecionado.getProvimentoFP().getDataProvimento();
        }
        return vinculoFP.temHistorico() && vinculoFP.getVinculoFPHist().getFinalVigencia() != null ? vinculoFP.getVinculoFPHist().getFinalVigencia() : selecionado.getProvimentoFP().getDataProvimento();
    }

}
