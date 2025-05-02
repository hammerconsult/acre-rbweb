package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ItemInconsistenciaIntegracaoTribCont;
import br.com.webpublico.enums.OperacaoReceita;
import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.enums.TipoIntegracao;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.BloqueioMovimentoContabilException;
import br.com.webpublico.exception.SemUnidadeException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@ManagedBean(name = "inconsistenciaIntegracaoTribContControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaInconsistenciaTribCont",
        pattern = "/integracao-tributario-contabil/#{inconsistenciaIntegracaoTribContControlador.idLoteBaixa}/",
        viewId = "/faces/tributario/arrecadacao/integracao/edita.xhtml")
})
public class InconsistenciaIntegracaoTribContControlador extends PrettyControlador<IntegracaoTributarioContabil> implements Serializable, CRUD {

    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private IntegracaoTributarioContabilFacade integracaoTributarioContabilFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private Long idLoteBaixa;
    private LoteBaixa loteBaixa;
    private List<ItemInconsistenciaIntegracaoTribCont> integracoesTributarias;
    private List<ItemInconsistenciaIntegracaoTribCont> integracoesDATributarias;
    private List<ItemInconsistenciaIntegracaoTribCont> integracoesCRTributarias;

    private Date dataContabil;

    @Override
    public AbstractFacade getFacede() {
        return loteBaixaFacade;
    }

    public InconsistenciaIntegracaoTribContControlador() {
        super(IntegracaoTributarioContabil.class);
    }

    public Long getIdLoteBaixa() {
        return idLoteBaixa;
    }

    public void setIdLoteBaixa(Long idLoteBaixa) {
        this.idLoteBaixa = idLoteBaixa;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public void setLoteBaixa(LoteBaixa loteBaixa) {
        this.loteBaixa = loteBaixa;
    }

    public List<ItemInconsistenciaIntegracaoTribCont> getIntegracoes() {
        return integracoesTributarias;
    }

    public void setIntegracoes(List<ItemInconsistenciaIntegracaoTribCont> integracoes) {
        this.integracoesTributarias = integracoes;
    }

    public List<ItemInconsistenciaIntegracaoTribCont> getIntegracoesDA() {
        return integracoesDATributarias;
    }

    public void setIntegracoesDA(List<ItemInconsistenciaIntegracaoTribCont> integracoesDA) {
        this.integracoesDATributarias = integracoesDA;
    }

    public List<ItemInconsistenciaIntegracaoTribCont> getIntegracoesCR() {
        return integracoesCRTributarias;
    }

    public void setIntegracoesCR(List<ItemInconsistenciaIntegracaoTribCont> integracoesCRTributarias) {
        this.integracoesCRTributarias = integracoesCRTributarias;
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public Date getDataContabil() {
        return dataContabil;
    }

    public void setDataContabil(Date dataContabil) {
        this.dataContabil = dataContabil;
    }

    @URLAction(mappingId = "novaInconsistenciaTribCont", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        if (idLoteBaixa != null) {
            try {
                loteBaixa = loteBaixaFacade.recuperarIntegracao(idLoteBaixa);
                validarDatasPeriodoFase();
                List<ItemInconsistenciaIntegracaoTribCont> integracoesContabeis = loteBaixaFacade.buscarIntegracoesNaContabilidade(loteBaixa);
                List<ItemInconsistenciaIntegracaoTribCont> integracoesCRContabeis = loteBaixaFacade.buscarIntegracoesCreditoReceber(loteBaixa);
                List<ItemInconsistenciaIntegracaoTribCont> integracoesDAContabeis = loteBaixaFacade.buscarIntegracoesDividaAtiva(loteBaixa);

                integracoesTributarias = loteBaixaFacade.criarItensParaInconsistenciaDaIntegracao(loteBaixa);
                integracoesCRTributarias = separarIntegracoesCreditoReceberTributarias(integracoesTributarias);
                integracoesDATributarias = separarIntegracoesDividaAtivaTributarias(integracoesTributarias);

                agruparContasDeOperacoesDeIntegracaoDiferentes(integracoesTributarias);

                buscarValoresContabeis(integracoesTributarias, integracoesContabeis);
                buscarValoresContabeis(integracoesCRTributarias, integracoesCRContabeis);
                buscarValoresContabeis(integracoesDATributarias, integracoesDAContabeis);

                adicionarContasContabeisNaoExistentesNoTributario(integracoesTributarias, integracoesContabeis);
                adicionarContasContabeisNaoExistentesNoTributario(integracoesCRTributarias, integracoesCRContabeis);
                adicionarContasContabeisNaoExistentesNoTributario(integracoesDATributarias, integracoesDAContabeis);

                removerContasZeradas(integracoesTributarias);
                removerContasZeradas(integracoesCRTributarias);
                removerContasZeradas(integracoesDATributarias);

                Collections.sort(integracoesTributarias);
                Collections.sort(integracoesCRTributarias);
                Collections.sort(integracoesDATributarias);
            } catch (ValidacaoException ex) {
                FacesUtil.printAllFacesMessages(ex.getMensagens());
                FacesUtil.redirecionamentoInterno("/tributario/arrecadacao/ver/" + idLoteBaixa + "/");
            }
        }
    }

    private void agruparContasDeOperacoesDeIntegracaoDiferentes(List<ItemInconsistenciaIntegracaoTribCont> integracoesTributarias) {
        List<ItemInconsistenciaIntegracaoTribCont> lista = Lists.newArrayList();
        List<ItemInconsistenciaIntegracaoTribCont> paraRemover = Lists.newArrayList();
        for (ItemInconsistenciaIntegracaoTribCont integracaoTributaria : integracoesTributarias) {
            boolean temItem = false;
            ItemInconsistenciaIntegracaoTribCont repetido = null;
            for (ItemInconsistenciaIntegracaoTribCont item : lista) {
                if (item.getContaReceita().equals(integracaoTributaria.getContaReceita()) &&
                    item.getOperacaoReceitaRealizada().equals(integracaoTributaria.getOperacaoReceitaRealizada())) {
                    temItem = true;
                    repetido = item;
                    break;
                }
            }
            if (!temItem) {
                lista.add(integracaoTributaria);
            } else {
                repetido.setValorTributario(repetido.getValorTributario().add(integracaoTributaria.getValorTributario()));
                paraRemover.add(integracaoTributaria);
            }
        }
        for (ItemInconsistenciaIntegracaoTribCont remover : paraRemover) {
            integracoesTributarias.remove(remover);
        }
    }

    private List<ItemInconsistenciaIntegracaoTribCont> separarIntegracoesDividaAtivaTributarias(List<ItemInconsistenciaIntegracaoTribCont> integracoes) {
        List<ItemInconsistenciaIntegracaoTribCont> dividasAtivas = Lists.newArrayList();
        for (ItemInconsistenciaIntegracaoTribCont integracao : integracoes) {
            if (OperacaoReceita.RECEITA_DIVIDA_ATIVA_BRUTA.equals(integracao.getOperacaoReceitaRealizada())) {
                dividasAtivas.add(duplicarItemInconsistenciaIntegracao(integracao));
            }
        }
        return dividasAtivas;
    }

    private ItemInconsistenciaIntegracaoTribCont duplicarItemInconsistenciaIntegracao(ItemInconsistenciaIntegracaoTribCont integracao) {
        ItemInconsistenciaIntegracaoTribCont item = new ItemInconsistenciaIntegracaoTribCont();
        item.setDataPagamento(integracao.getDataPagamento());
        item.setDataCredito(integracao.getDataCredito());
        item.setEntidade(integracao.getEntidade());
        item.setContaReceita(integracao.getContaReceita());
        item.setOperacaoReceitaRealizada(integracao.getOperacaoReceitaRealizada());
        item.setFonteDeRecursos(integracao.getFonteDeRecursos());
        item.setOperacaoIntegracao(integracao.getOperacaoIntegracao());
        item.setValorTributario(integracao.getValorTributario());
        item.setValorContabil(BigDecimal.ZERO);
        return item;
    }

    private List<ItemInconsistenciaIntegracaoTribCont> separarIntegracoesCreditoReceberTributarias(List<ItemInconsistenciaIntegracaoTribCont> integracoes) {
        List<ItemInconsistenciaIntegracaoTribCont> creditosReceber = Lists.newArrayList();
        for (ItemInconsistenciaIntegracaoTribCont integracao : integracoes) {
            if (OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA.equals(integracao.getOperacaoReceitaRealizada())) {
                creditosReceber.add(duplicarItemInconsistenciaIntegracao(integracao));
            }
        }
        return creditosReceber;
    }

    public void validarDatasPeriodoFase() {
        ValidacaoException ve = new ValidacaoException();
        if (faseFacade.temBloqueioFaseParaRecurso("/tributario/arrecadacao/integracao/edita.xhtml", loteBaixa.getDataFinanciamento(),
            getSistemaControlador().getUnidadeOrganizacionalOrcamentoCorrente(),
            getSistemaControlador().getExercicioCorrente())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esse lote tem como data de movimento " + Util.dateToString(loteBaixa.getDataFinanciamento()) + ", que está fora do período fase!");
        }
        ve.lancarException();
    }

    private void buscarValoresContabeis(List<ItemInconsistenciaIntegracaoTribCont> integracoesTr, List<ItemInconsistenciaIntegracaoTribCont> integracoesCb) {
        for (ItemInconsistenciaIntegracaoTribCont tributaria : integracoesTr) {
            BigDecimal valorContabil = getValorContabilidade(tributaria, integracoesCb);
            tributaria.setValorContabil(valorContabil);
        }
    }

    private void adicionarContasContabeisNaoExistentesNoTributario(List<ItemInconsistenciaIntegracaoTribCont> integracoesTr, List<ItemInconsistenciaIntegracaoTribCont> integracoesCb) {
        for (ItemInconsistenciaIntegracaoTribCont contabil : integracoesCb) {
            if (!integracoesTr.contains(contabil)) {
                integracoesTr.add(contabil);
            }
        }
    }

    private void removerContasZeradas(List<ItemInconsistenciaIntegracaoTribCont> integracoesTr) {
        List<ItemInconsistenciaIntegracaoTribCont> aRemover = Lists.newArrayList();
        for (ItemInconsistenciaIntegracaoTribCont item : integracoesTr) {
            if (item.getValorTributario().compareTo(BigDecimal.ZERO) == 0
                && item.getValorContabil().compareTo(BigDecimal.ZERO) == 0) {
                aRemover.add(item);
            }
        }

        for (ItemInconsistenciaIntegracaoTribCont remover : aRemover) {
            integracoesTr.remove(remover);
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/integracao-tributario-contabil/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void salvar() {

    }

    public BigDecimal getTotalValorTributario() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemInconsistenciaIntegracaoTribCont integracao : integracoesTributarias) {
            total = total.add(integracao.getValorTributario());
        }
        return total;
    }

    public BigDecimal getTotalValorTributarioCR() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemInconsistenciaIntegracaoTribCont integracao : integracoesCRTributarias) {
            total = total.add(integracao.getValorTributario());
        }
        return total;
    }

    public BigDecimal getTotalValorTributarioDA() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemInconsistenciaIntegracaoTribCont integracao : integracoesDATributarias) {
            total = total.add(integracao.getValorTributario());
        }
        return total;
    }

    public BigDecimal getTotalValorContabil() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemInconsistenciaIntegracaoTribCont integracao : integracoesTributarias) {
            total = total.add(integracao.getValorContabil());
        }
        return total;
    }

    public BigDecimal getTotalValorContabilCR() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemInconsistenciaIntegracaoTribCont integracao : integracoesCRTributarias) {
            total = total.add(integracao.getValorContabil());
        }
        return total;
    }

    public BigDecimal getTotalValorContabilDA() {
        BigDecimal total = BigDecimal.ZERO;
        for (ItemInconsistenciaIntegracaoTribCont integracao : integracoesDATributarias) {
            total = total.add(integracao.getValorContabil());
        }
        return total;
    }

    public void corrigirIntegracao() {
        try {
            LoteBaixa loteBaixa = loteBaixaFacade.recuperarIntegracao(this.loteBaixa.getId());
            ConfiguracaoContabil configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
            Preconditions.checkNotNull(configuracaoContabil.getClasseTribContReceitaRea(), "A Classe Credor configurada para Integração Contábil/Tributário para Receita Realizada não foi informada.");

            for (ItemInconsistenciaIntegracaoTribCont itemIntegracao : integracoesTributarias) {
                if (loteBaixa.isEstornado()) {
                    if (itemIntegracao.getValorContabil().compareTo(BigDecimal.ZERO) > 0) {
                        integrarInconsistencia(itemIntegracao, TipoIntegracao.ESTORNO_ARRECADACAO, itemIntegracao.getValorContabil());
                    } else if (itemIntegracao.getValorContabil().compareTo(BigDecimal.ZERO) < 0) {
                        integrarInconsistencia(itemIntegracao, TipoIntegracao.ARRECADACAO, itemIntegracao.getValorContabil());
                    }
                } else {
                    if (itemIntegracao.getDiferenca().compareTo(BigDecimal.ZERO) > 0) {
                        integrarInconsistencia(itemIntegracao, TipoIntegracao.ARRECADACAO, itemIntegracao.getDiferenca());
                    } else if (itemIntegracao.getDiferenca().compareTo(BigDecimal.ZERO) < 0) {
                        integrarInconsistencia(itemIntegracao, TipoIntegracao.ESTORNO_ARRECADACAO, itemIntegracao.getDiferenca());
                    }
                }
            }
            List<CreditoReceber> creditosReceber = Lists.newArrayList();
            for (ItemInconsistenciaIntegracaoTribCont itemIntegracao : integracoesCRTributarias) {
                if (loteBaixa.isEstornado()) {
                    if (itemIntegracao.getValorContabil().compareTo(BigDecimal.ZERO) > 0) {
                        creditosReceber.add(integrarInconsistenciaCreditoReceber(itemIntegracao, TipoLancamento.ESTORNO, configuracaoContabil, itemIntegracao.getValorContabil()));
                    } else if (itemIntegracao.getValorContabil().compareTo(BigDecimal.ZERO) < 0) {
                        creditosReceber.add(integrarInconsistenciaCreditoReceber(itemIntegracao, TipoLancamento.NORMAL, configuracaoContabil, itemIntegracao.getValorContabil()));
                    }
                } else {
                    if (itemIntegracao.getDiferenca().compareTo(BigDecimal.ZERO) > 0) {
                        creditosReceber.add(integrarInconsistenciaCreditoReceber(itemIntegracao, TipoLancamento.NORMAL, configuracaoContabil, itemIntegracao.getDiferenca()));
                    } else if (itemIntegracao.getDiferenca().compareTo(BigDecimal.ZERO) < 0) {
                        creditosReceber.add(integrarInconsistenciaCreditoReceber(itemIntegracao, TipoLancamento.ESTORNO, configuracaoContabil, itemIntegracao.getDiferenca()));
                    }
                }
            }
            List<DividaAtivaContabil> dividasAtivas = Lists.newArrayList();
            for (ItemInconsistenciaIntegracaoTribCont itemIntegracao : integracoesDATributarias) {
                if (loteBaixa.isEstornado()) {
                    if (itemIntegracao.getValorContabil().compareTo(BigDecimal.ZERO) > 0) {
                        dividasAtivas.add(integrarInconsistenciaDividaAtiva(itemIntegracao, TipoLancamento.ESTORNO, configuracaoContabil, itemIntegracao.getValorContabil()));
                    } else if (itemIntegracao.getValorContabil().compareTo(BigDecimal.ZERO) < 0) {
                        dividasAtivas.add(integrarInconsistenciaDividaAtiva(itemIntegracao, TipoLancamento.NORMAL, configuracaoContabil, itemIntegracao.getValorContabil()));
                    }
                } else {
                    if (itemIntegracao.getDiferenca().compareTo(BigDecimal.ZERO) > 0) {
                        dividasAtivas.add(integrarInconsistenciaDividaAtiva(itemIntegracao, TipoLancamento.NORMAL, configuracaoContabil, itemIntegracao.getDiferenca()));
                    } else if (itemIntegracao.getDiferenca().compareTo(BigDecimal.ZERO) < 0) {
                        dividasAtivas.add(integrarInconsistenciaDividaAtiva(itemIntegracao, TipoLancamento.ESTORNO, configuracaoContabil, itemIntegracao.getDiferenca()));
                    }
                }
            }

            if (!creditosReceber.isEmpty()) {
                integracaoTributarioContabilFacade.importarReceitaCreditoReceber(creditosReceber, new AssistenteBarraProgresso(creditosReceber.size()), false);
            }
            if (!dividasAtivas.isEmpty()) {
                integracaoTributarioContabilFacade.importarReceitasDividaAtiva(dividasAtivas, new AssistenteBarraProgresso(dividasAtivas.size()), false);
            }
            loteBaixa = loteBaixaFacade.recuperarIntegracao(this.loteBaixa.getId());
            loteBaixa.setIntegraContasAcrecimos(true);
            if (SituacaoLoteBaixa.ESTORNADO.equals(loteBaixa.getSituacaoLoteBaixa())) {
                loteBaixa.setIntegracaoEstorno(true);
            } else {
                loteBaixa.setIntegracaoArrecadacao(true);
            }
            this.loteBaixa = loteBaixaFacade.salvaRetornando(loteBaixa);
            novo();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (BloqueioMovimentoContabilException ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (SemUnidadeException ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    private void integrarInconsistencia(ItemInconsistenciaIntegracaoTribCont itemIntegracao, TipoIntegracao tipoIntegracao, BigDecimal valorIntegracao) throws SemUnidadeException, BloqueioMovimentoContabilException {
        try {
            if (valorIntegracao.compareTo(BigDecimal.ZERO) < 0) {
                valorIntegracao = valorIntegracao.multiply(new BigDecimal(-1));
            }

            Date dataPagamento = loteBaixa.getDataPagamento();
            Date dataCredito = loteBaixa.getDataFinanciamento();
            if (dataContabil != null) {
                dataPagamento = dataContabil;
                dataCredito = dataContabil;
            }
            if (valorIntegracao.compareTo(BigDecimal.ZERO) != 0) {
                loteBaixaFacade.criarIntegracaoComAContabilidade(loteBaixa,
                    dataPagamento,
                    dataCredito,
                    itemIntegracao.getEntidade().getId(),
                    valorIntegracao,
                    getSistemaControlador().getExercicioCorrente(),
                    itemIntegracao.getContaReceita(),
                    tipoIntegracao,
                    itemIntegracao.getOperacaoReceitaRealizada(),
                    itemIntegracao.getFonteDeRecursos(),
                    itemIntegracao.getOperacaoIntegracao(),
                    itemIntegracao);
            }
        } catch (SemUnidadeException | BloqueioMovimentoContabilException | ExcecaoNegocioGenerica ex) {
            throw ex;
        }
    }

    private CreditoReceber integrarInconsistenciaCreditoReceber(ItemInconsistenciaIntegracaoTribCont itemIntegracao, TipoLancamento tipoLancamento, ConfiguracaoContabil configuracaoContabil, BigDecimal valorIntegracao) throws SemUnidadeException, BloqueioMovimentoContabilException {
        try {
            if (valorIntegracao.compareTo(BigDecimal.ZERO) < 0) {
                valorIntegracao = valorIntegracao.multiply(new BigDecimal(-1));
            }

            Date dataCredito = loteBaixa.getDataPagamento();
            if (dataContabil != null) {
                dataCredito = dataContabil;
            }

            return loteBaixaFacade.criarIntegracaoCreditoReceberComAContabilidade(loteBaixa,
                dataCredito,
                itemIntegracao.getEntidade(),
                valorIntegracao,
                getSistemaControlador().getExercicioCorrente(),
                itemIntegracao.getContaReceita(),
                tipoLancamento, configuracaoContabil,
                itemIntegracao.getFonteDeRecursos());
        } catch (SemUnidadeException | BloqueioMovimentoContabilException | ExcecaoNegocioGenerica ex) {
            throw ex;
        }
    }

    private DividaAtivaContabil integrarInconsistenciaDividaAtiva(ItemInconsistenciaIntegracaoTribCont itemIntegracao, TipoLancamento tipoLancamento, ConfiguracaoContabil configuracaoContabil, BigDecimal valorIntegracao) throws SemUnidadeException, BloqueioMovimentoContabilException {
        try {
            if (valorIntegracao.compareTo(BigDecimal.ZERO) < 0) {
                valorIntegracao = valorIntegracao.multiply(new BigDecimal(-1));
            }

            Date dataCredito = loteBaixa.getDataPagamento();
            if (dataContabil != null) {
                dataCredito = dataContabil;
            }

            return loteBaixaFacade.criarIntegracaoDividaAtivaComAContabilidade(loteBaixa,
                dataCredito,
                itemIntegracao.getEntidade(),
                valorIntegracao,
                getSistemaControlador().getExercicioCorrente(),
                itemIntegracao.getContaReceita(),
                tipoLancamento, configuracaoContabil,
                itemIntegracao.getFonteDeRecursos());
        } catch (SemUnidadeException | BloqueioMovimentoContabilException | ExcecaoNegocioGenerica ex) {
            throw ex;
        }
    }

    public void corrigirDAMs() {
        List<DAM> dams = Lists.newArrayList();
        for (ItemLoteBaixa itemLoteBaixa : loteBaixa.getItemLoteBaixa()) {
            dams.add(itemLoteBaixa.getDam());
        }
        loteBaixaFacade.corrigirDAMsParcelamento(dams);
        novo();
    }

    private BigDecimal getValorContabilidade(ItemInconsistenciaIntegracaoTribCont tributaria, List<ItemInconsistenciaIntegracaoTribCont> itens) {
        List<ItemInconsistenciaIntegracaoTribCont> contabeis = Lists.newArrayList();
        for (ItemInconsistenciaIntegracaoTribCont contabil : itens) {
            if (contabil.getContaReceita().getCodigo().equals(tributaria.getContaReceita().getCodigo()) &&
                contabil.getOperacaoReceitaRealizada().equals(tributaria.getOperacaoReceitaRealizada())) {
                contabeis.add(contabil);
            }
        }
        if (!contabeis.isEmpty()) {
            BigDecimal valorContabil = BigDecimal.ZERO;
            for (ItemInconsistenciaIntegracaoTribCont contabil : contabeis) {
                valorContabil = valorContabil.add(contabil.getValorContabil());
                itens.remove(contabil);
            }
            return valorContabil;
        }
        return BigDecimal.ZERO;
    }

    public boolean validarSituacaoLote() {
        if (loteBaixa != null) {
            return (!SituacaoLoteBaixa.ESTORNADO.equals(loteBaixa.getSituacaoLoteBaixa()) && !SituacaoLoteBaixa.EM_ABERTO.equals(loteBaixa.getSituacaoLoteBaixa()));
        }
        return false;
    }

    public boolean validarValorContabil() {
        return getTotalValorContabil().compareTo(BigDecimal.ZERO) != 0;
    }

    public void voltar() {
        FacesUtil.redirecionamentoInterno("/tributario/arrecadacao/ver/" + idLoteBaixa + "/");
    }

    public void geraImprimeMapaArrecadacao() {
        loteBaixaFacade.gerarImprimirMapaArrecadacao(loteBaixa, null, loteBaixaFacade.arquivoDoLote(loteBaixa));
    }

    public void reprocessarIntegracao() {
        if (verificaContasDoLote(loteBaixa)) {
            if (podeApagarIntegracao(loteBaixa)) {
                try {
                    loteBaixaFacade.getArrecadacaoFacade().apagarIntegracaoECria(loteBaixa);
                    FacesUtil.addInfo("Sucesso!", "Reprocessamento concluído!");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    FacesUtil.addOperacaoNaoPermitida("Erro Interno do Servidor! " + e.getMessage());
                }
            }
        }
    }

    public boolean verificaContasDoLote(LoteBaixa loteBaixa) {
        List<Exception> exceptions = loteBaixaFacade.verificaContasDoLote(loteBaixa);
        if (exceptions.isEmpty()) {
            return true;
        } else {
            for (Exception e : exceptions) {
                FacesUtil.addOperacaoNaoPermitida(e.getMessage());
            }
            return false;
        }
    }

    private boolean podeApagarIntegracao(LoteBaixa loteBaixa) {
        boolean retorno = loteBaixaFacade.podeApagarIntegracao(loteBaixa);
        if (!retorno) {
            FacesUtil.addOperacaoNaoPermitida("Já foi gerado uma Receita Realizada para esse lote! Não é permitido o reprocessamento!");
        }
        return retorno;
    }
}
