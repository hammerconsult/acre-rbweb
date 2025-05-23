/*
 * Codigo gerado automaticamente em Wed Dec 14 09:49:45 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.LiquidacaoEstornoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DocumentoFiscalIntegracao;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

@Stateless
public class LiquidacaoEstornoFacade extends SuperFacadeContabil<LiquidacaoEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private DoctoFiscalLiquidacaoFacade doctoFiscalLiquidacaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ConfigLiquidacaoFacade configLiquidacaoFacade;
    @EJB
    private ConfigLiquidacaoResPagarFacade configLiquidacaoResPagarFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private SaldoGrupoMaterialFacade saldoGrupoMaterialFacade;
    @EJB
    private SaldoGrupoBemMovelFacade saldoGrupoBemFacade;
    @EJB
    private SaldoGrupoBemImovelFacade saldoGrupoBemImovelFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private ObrigacaoAPagarFacade obrigacaoAPagarFacade;
    @EJB
    private DesdobramentoObrigacaoAPagarFacade desdobramentoObrigacaoAPagarFacade;
    @EJB
    private SolicitacaoLiquidacaoEstornoFacade solicitacaoLiquidacaoFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;
    @EJB
    private BensEstoqueFacade bensEstoqueFacade;
    @EJB
    private ConfigGrupoMaterialFacade configGrupoMaterialFacade;
    @EJB
    private ConfigDespesaBensFacade configDespesaBensFacade;
    @EJB
    private IntegradorDocumentoFiscalLiquidacaoFacade integradorDocumentoFiscalLiquidacaoFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;

    public LiquidacaoEstornoFacade() {
        super(LiquidacaoEstorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public LiquidacaoEstorno recuperar(Object id) {
        LiquidacaoEstorno liquidacaoEstorno = em.find(LiquidacaoEstorno.class, id);
        liquidacaoEstorno.getDesdobramentos().size();
        liquidacaoEstorno.getDocumentosFiscais().size();
        for (LiquidacaoEstornoDoctoFiscal documento : liquidacaoEstorno.getDocumentosFiscais()) {
            documento.getTransferenciasBens().size();
        }
        return liquidacaoEstorno;
    }

    public List<LiquidacaoEstorno> listaPorEmpenho(Empenho e) {
        String sql = "SELECT le.* FROM liquidacaoestorno le "
            + "INNER JOIN liquidacao li ON li.id = le.liquidacao_id "
            + "WHERE li.empenho_id = :param";
        Query q = em.createNativeQuery(sql, LiquidacaoEstorno.class);
        q.setParameter("param", e.getId());
        return (List<LiquidacaoEstorno>) q.getResultList();
    }

    public List<LiquidacaoEstorno> listaPorLiquidacao(Liquidacao li) {
        String sql = " SELECT LE.* FROM LIQUIDACAOESTORNO LE " +
            " WHERE LE.LIQUIDACAO_ID = :param ";
        Query q = em.createNativeQuery(sql, LiquidacaoEstorno.class);
        q.setParameter("param", li.getId());
        return q.getResultList();
    }

    public List<LiquidacaoEstorno> buscarLiquidacaoEstornoPorUnidadesExercicio(List<HierarquiaOrganizacional> listaUnidades, Exercicio exercicio, CategoriaOrcamentaria categoria) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }
        String sql = "SELECT est "
            + " FROM LiquidacaoEstorno est"
            + " WHERE  est.liquidacao.exercicio.id = :exercicio"
            + " and est.unidadeOrganizacional in ( :unidades )" +
            " and est.categoriaOrcamentaria = :categoria";
        Query q = em.createQuery(sql, LiquidacaoEstorno.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("unidades", unidades);
        q.setParameter("categoria", categoria);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    private void geraNumeroEstornoLiquidacao(LiquidacaoEstorno entity) {
        entity.setNumero(singletonGeradorCodigoContabil.getNumeroLiquidacao(entity.getLiquidacao().getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataEstorno()));
    }

    private void gerarMovimentoDespesaORC(LiquidacaoEstorno entity) {
        if (entity.isLiquidacaoEstornoCategoriaNormal()) {
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                entity.getLiquidacao().getEmpenho().getFonteDespesaORC(),
                OperacaoORC.LIQUIDACAO,
                TipoOperacaoORC.ESTORNO,
                entity.getValor(),
                entity.getDataEstorno(),
                entity.getUnidadeOrganizacional(),
                entity.getId().toString(),
                entity.getClass().getSimpleName(),
                entity.getNumero(),
                entity.getHistoricoNota());
            MovimentoDespesaORC mdorc = saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
            entity.setMovimentoDespesaORC(mdorc);
        }
    }

    private void gerarSaldoGrupoPatrimonialAndGrupoMaterial(LiquidacaoEstorno entity) {
        Empenho empenho = entity.getLiquidacao().getEmpenho();
        switch (empenho.getTipoContaDespesa()) {
            case BEM_MOVEL:
                gerarSaldoBensMoveis(entity);
                break;
            case BEM_IMOVEL:
                gerarSaldoBensImoveis(entity);
                break;
            case BEM_ESTOQUE:
                gerarSaldoBensEstoque(entity);
                break;
        }
    }

    private void gerarSaldoBensMoveis(LiquidacaoEstorno entity) {

        for (DesdobramentoLiquidacaoEstorno desdobramento : entity.getDesdobramentos()) {
            List<HashMap> gruposBens = grupoBemFacade.recuperaGrupoBemETipoGrupoBemPorContaDespesa(desdobramento.getConta(), entity.getDataEstorno());
            for (HashMap has : gruposBens) {
                GrupoBem grupoBem = (GrupoBem) has.get(1);
                TipoGrupo tipoGrupoBem = (TipoGrupo) has.get(2);
                saldoGrupoBemFacade.geraSaldoGrupoBemMoveis(
                    entity.getUnidadeOrganizacional(),
                    grupoBem,
                    desdobramento.getValor(),
                    tipoGrupoBem,
                    entity.getDataEstorno(),
                    TipoOperacaoBensMoveis.AQUISICAO_BENS_MOVEIS,
                    TipoLancamento.ESTORNO,
                    TipoOperacao.DEBITO,
                    true);
            }
        }
    }

    private void gerarSaldoBensImoveis(LiquidacaoEstorno entity) {

        for (DesdobramentoLiquidacaoEstorno desdobramento : entity.getDesdobramentos()) {
            List<HashMap> gruposBens = grupoBemFacade.recuperaGrupoBemETipoGrupoBemPorContaDespesa(desdobramento.getConta(), entity.getDataEstorno());
            for (HashMap has : gruposBens) {
                GrupoBem grupoBem = (GrupoBem) has.get(1);
                TipoGrupo tipoGrupoBem = (TipoGrupo) has.get(2);
                saldoGrupoBemImovelFacade.geraSaldoGrupoBemImoveis(
                    entity.getUnidadeOrganizacional(),
                    grupoBem,
                    desdobramento.getValor(),
                    tipoGrupoBem,
                    entity.getDataEstorno(),
                    TipoOperacaoBensImoveis.AQUISICAO_BENS_IMOVEIS,
                    TipoLancamento.ESTORNO,
                    TipoOperacao.DEBITO,
                    true);
            }
        }
    }


    private void gerarSaldoBensEstoque(LiquidacaoEstorno entity) {
        for (DesdobramentoLiquidacaoEstorno desdobramento : entity.getDesdobramentos()) {
            List<HashMap> gruposBens = grupoMaterialFacade.recuperaGrupoMateriaETipoEstoquePorContaDespesa(desdobramento.getConta(), entity.getDataEstorno());
            for (HashMap has : gruposBens) {
                GrupoMaterial grupoMaterial = (GrupoMaterial) has.get(1);
                TipoEstoque tipoEstoque = (TipoEstoque) has.get(2);
                saldoGrupoMaterialFacade.geraSaldoGrupoMaterial(
                    entity.getUnidadeOrganizacional(),
                    grupoMaterial,
                    desdobramento.getValor(),
                    tipoEstoque,
                    entity.getDataEstorno(),
                    TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE,
                    TipoLancamento.ESTORNO,
                    TipoOperacao.DEBITO,
                    entity.getId(),
                    true);
                gerarBensEstoqueEmpenhoReajustePosExecucao(entity, desdobramento, grupoMaterial, tipoEstoque);
            }
        }
    }

    private void gerarBensEstoqueEmpenhoReajustePosExecucao(LiquidacaoEstorno liquidacaoEstorno, DesdobramentoLiquidacaoEstorno desdobramento, GrupoMaterial grupoMaterial, TipoEstoque tipoEstoque) {
        if (empenhoFacade.getExecucaoContratoFacade().isEmpenhoReajustePosExecucao(liquidacaoEstorno.getLiquidacao().getEmpenho())) {
            BensEstoque bensEstoque = new BensEstoque();
            bensEstoque.setDataBem(liquidacaoEstorno.getDataEstorno());
            bensEstoque.setExercicio(liquidacaoEstorno.getExercicio());
            bensEstoque.setOperacoesBensEstoque(TipoOperacaoBensEstoque.BAIXA_BENS_ESTOQUE_POR_CONSUMO);
            bensEstoque.setUnidadeOrganizacional(liquidacaoEstorno.getUnidadeOrganizacional());
            bensEstoque.setTipoBaixaBens(bensEstoqueFacade.getTipoBaixaBensFacade().recuperarTipoIngressoBaixa(TipoBem.ESTOQUE, TipoIngressoBaixa.CONSUMO));
            bensEstoque.setGrupoMaterial(grupoMaterial);
            bensEstoque.setTipoEstoque(tipoEstoque);
            bensEstoque.setTipoLancamento(TipoLancamento.ESTORNO);
            bensEstoque.setHistorico(liquidacaoEstorno.getComplementoHistorico());
            bensEstoque.setValor(desdobramento.getValor());
            bensEstoqueFacade.salvarBensEstoque(bensEstoque);
        }
    }

    @Override
    public void salvar(LiquidacaoEstorno entity) {
        entity.gerarHistoricos();
        em.merge(entity);
        empenhoFacade.getPortalTransparenciaNovoFacade().salvarPortal(new LiquidacaoEstornoPortal(entity));
    }

    /**
     * Não alterar a ordem de execução dos métodos;
     */
    public LiquidacaoEstorno salvarNovoEstorno(LiquidacaoEstorno entity, SolicitacaoLiquidacaoEstorno solicitacaoEstorno, List<DocumentoFiscalIntegracao> documentosIntegracao) {
        if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
            throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        } else {
            if (singletonConcorrenciaContabil.isDisponivel(entity.getLiquidacao())) {
                singletonConcorrenciaContabil.bloquear(entity.getLiquidacao());
                validaEstorno(entity);
                movimentarLiquidacao(entity, documentosIntegracao);
                if (entity.getId() == null) {
                    geraNumeroEstornoLiquidacao(entity);
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                gerarMovimentoDespesaORC(entity);
                gerarSaldoGrupoPatrimonialAndGrupoMaterial(entity);
                atribuirEstornoLiquidacaoNaSolicitacaoEstorno(entity, solicitacaoEstorno);
                contabilzar(entity);
                entity = integradorDocumentoFiscalLiquidacaoFacade.gerarEstornoTransferenciasDeBensEmGruposDesdobradosDiferentes(entity);
                empenhoFacade.getPortalTransparenciaNovoFacade().salvarPortal(new LiquidacaoEstornoPortal(entity));
                empenhoFacade.getSingletonConcorrenciaContabil().desbloquear(entity.getLiquidacao());
            } else {
                throw new ExcecaoNegocioGenerica("A Liquidação " + entity.getLiquidacao() + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente o Empenho.");
            }
        }
        return entity;
    }

    private void atribuirEstornoLiquidacaoNaSolicitacaoEstorno(LiquidacaoEstorno entity, SolicitacaoLiquidacaoEstorno solicitacaoEstorno) {
        if (solicitacaoEstorno != null) {
            solicitacaoEstorno.setLiquidacaoEstorno(entity);
            em.merge(solicitacaoEstorno);
        }
    }

    private void validaEstorno(LiquidacaoEstorno entity) {
        hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEstorno());
        if (entity.getDesdobramentos() == null || entity.getDesdobramentos().isEmpty()) {
            throw new ExcecaoNegocioGenerica("Para salvar é obrigatório adicionar um detalhamento.");
        }
        if (entity.getValor().compareTo(new BigDecimal(BigInteger.ZERO)) <= 0) {
            throw new ExcecaoNegocioGenerica(" O campo valor deve ser maior que zero(0).");
        }
        if (entity.getLiquidacao() != null) {
            if (entity.getLiquidacao().getSaldo().compareTo(entity.getValor()) < 0) {
                throw new ExcecaoNegocioGenerica(" O valor a ser estornado de <b> " + Util.formataValor(entity.getValor()) + "</b> é maior que o saldo de <b> " + Util.formataValor(entity.getLiquidacao().getSaldo()) + " </b> disponível na liquidação.");
            }
            if (DataUtil.dataSemHorario(entity.getDataEstorno()).before(DataUtil.dataSemHorario(entity.getLiquidacao().getDataLiquidacao()))) {
                throw new ExcecaoNegocioGenerica(" A data do estorno de liquidação (" + DataUtil.getDataFormatada(entity.getDataEstorno()) + ") deve ser maior ou igual a data da liquidação. Data da liquidação: <b>" + DataUtil.getDataFormatada(entity.getLiquidacao().getDataLiquidacao()) + "</b>.");
            }
        }
    }

    private void movimentarLiquidacao(LiquidacaoEstorno entity, List<DocumentoFiscalIntegracao> documentosIntegracao) {
        if (entity.getLiquidacao() != null) {
            Liquidacao liquidacao = liquidacaoFacade.recuperar(entity.getLiquidacao().getId());
            if (liquidacao.getSaldo().compareTo(entity.getValor()) < 0) {
                throw new ValidacaoException("O movimento já foi salvo por outro usuário! Atualize a página e tente novamente.");
            }
            liquidacao.setSaldo(liquidacao.getSaldo().subtract(entity.getValor()));
            liquidacao.setEmpenho(movimentarEmpenho(entity));

            liquidacao = movimentarSaldoObrigacaoPagar(liquidacao, entity);
            liquidacao = movimentarSaldoLiquidacao(liquidacao, entity);

            integradorDocumentoFiscalLiquidacaoFacade.estornarDocumentoFiscalIntegracao(liquidacao, entity, documentosIntegracao);

            liquidacao.setEmpenho(em.merge(liquidacao.getEmpenho()));
            liquidacao = em.merge(liquidacao);
            entity.setLiquidacao(liquidacao);
        }
    }

    private Empenho movimentarEmpenho(LiquidacaoEstorno entity) {
        Empenho empenho = empenhoFacade.recuperar(entity.getLiquidacao().getEmpenho().getId());
        empenho.setSaldo(entity.getLiquidacao().getEmpenho().getSaldo().add(entity.getValor()));
        if (empenho.isEmpenhoProcessado() && !empenho.getDesdobramentos().isEmpty()) {
            for (DesdobramentoEmpenho desdobramentoEmpenho : empenho.getDesdobramentos()) {
                for (DesdobramentoLiquidacaoEstorno desdobramentoEstorno : entity.getDesdobramentos()) {
                    if (desdobramentoEmpenho.getConta() != null && desdobramentoEstorno.getConta() != null &&
                        desdobramentoEmpenho.getConta().getCodigo().equals(desdobramentoEstorno.getConta().getCodigo())) {
                        desdobramentoEmpenho.setSaldo(desdobramentoEmpenho.getSaldo().add(desdobramentoEstorno.getValor()));
                    }
                }
            }
        }
        return empenho;
    }

    private Liquidacao movimentarSaldoLiquidacao(Liquidacao liquidacao, LiquidacaoEstorno entity) {

        if (liquidacao.isLiquidacaoPossuiObrigacoesPagar()) {
            movimentarSaldoDetalhamentoLiquidacaoPorObrigacaoPagar(liquidacao, entity);
        } else {
            movimentarSaldoDetalhamentoLiquidacao(liquidacao, entity);
        }
        movimentarSaldoDocumentoFiscalLiquidacao(liquidacao, entity);
        return liquidacao;
    }

    private void movimentarSaldoDetalhamentoLiquidacao(Liquidacao liquidacao, LiquidacaoEstorno entity) {
        for (DesdobramentoLiquidacaoEstorno desdEstorno : entity.getDesdobramentos()) {
            for (Desdobramento desdLiquidacao : liquidacao.getDesdobramentos()) {
                if (desdEstorno.getConta().equals(desdLiquidacao.getConta())) {
                    desdLiquidacao.setSaldo(desdLiquidacao.getSaldo().subtract(desdEstorno.getValor()));
                    if (desdLiquidacao.getDesdobramentoEmpenho() != null) {
                        desdLiquidacao.getDesdobramentoEmpenho().setSaldo(desdLiquidacao.getDesdobramentoEmpenho().getSaldo().add(desdEstorno.getValor()));
                    }
                }
            }
        }
    }

    private void movimentarSaldoDetalhamentoLiquidacaoPorObrigacaoPagar(Liquidacao liquidacao, LiquidacaoEstorno entity) {
        for (DesdobramentoLiquidacaoEstorno desdEstorno : entity.getDesdobramentos()) {
            for (Desdobramento desdLiquidacao : liquidacao.getDesdobramentos()) {
                if (desdEstorno.getConta().equals(desdLiquidacao.getConta())
                    && desdEstorno.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(desdLiquidacao.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar())) {
                    desdLiquidacao.setSaldo(desdLiquidacao.getSaldo().subtract(desdEstorno.getValor()));
                    if (desdLiquidacao.getDesdobramentoEmpenho() != null) {
                        desdLiquidacao.getDesdobramentoEmpenho().setSaldo(desdLiquidacao.getDesdobramentoEmpenho().getSaldo().add(desdEstorno.getValor()));
                    }
                }
            }
        }
    }

    private void movimentarSaldoDocumentoFiscalLiquidacao(Liquidacao liquidacao, LiquidacaoEstorno entity) {
        for (LiquidacaoEstornoDoctoFiscal doctoEstorno : entity.getDocumentosFiscais()) {
            for (LiquidacaoDoctoFiscal doctoLiquidacao : liquidacao.getDoctoFiscais()) {
                if (doctoEstorno.getDocumentoFiscal().equals(doctoLiquidacao.getDoctoFiscalLiquidacao())) {
                    doctoLiquidacao.setSaldo(doctoLiquidacao.getSaldo().subtract(doctoEstorno.getValor()));
                }
            }
        }
    }

    private Liquidacao movimentarSaldoObrigacaoPagar(Liquidacao liquidacao, LiquidacaoEstorno entity) {
        if (liquidacao.isLiquidacaoPossuiObrigacoesPagar()) {
            for (LiquidacaoObrigacaoPagar obrigadacaoLiquidacao : liquidacao.getObrigacoesPagar()) {
                ObrigacaoAPagar obrigacaoPagar = obrigacaoAPagarFacade.recuperar(obrigadacaoLiquidacao.getObrigacaoAPagar().getId());

                if (obrigacaoPagar.isObrigacaoPagarAntesEmpenho()) {
                    obrigacaoPagar = movimentarSaldoDesdobramentoObrigacaoPagarAntesEmpenho(entity, liquidacao, obrigacaoPagar);
                } else {
                    obrigacaoPagar = movimentarSaldoDesdobramentoObrigacaoPagarDepoisEmpenho(entity, liquidacao, obrigacaoPagar);
                }
                obrigacaoPagar = movimentarSaldoDocumentoFiscalObrigacaoPagar(entity, obrigacaoPagar);
                em.merge(obrigacaoPagar);
            }
        }
        return liquidacao;
    }

    private ObrigacaoAPagar movimentarSaldoDocumentoFiscalObrigacaoPagar(LiquidacaoEstorno entity, ObrigacaoAPagar obrigacaoPagar) {
        for (LiquidacaoEstornoDoctoFiscal doctoLiquidacao : entity.getDocumentosFiscais()) {
            for (ObrigacaoAPagarDoctoFiscal doctoObrigacao : obrigacaoPagar.getDocumentosFiscais()) {
                if (doctoLiquidacao.getDocumentoFiscal().equals(doctoObrigacao.getDocumentoFiscal())) {
                    doctoObrigacao.setSaldo(doctoObrigacao.getSaldo().add(doctoLiquidacao.getValor()));
                }
            }
        }
        return obrigacaoPagar;
    }


    private ObrigacaoAPagar movimentarSaldoDesdobramentoObrigacaoPagarAntesEmpenho(LiquidacaoEstorno entity, Liquidacao liquidacao, ObrigacaoAPagar obrigacaoPagar) {

        for (DesdobramentoLiquidacaoEstorno desdLiquidacao : entity.getDesdobramentos()) {
            Empenho empenho = empenhoFacade.recuperar(liquidacao.getEmpenho().getId());

            for (DesdobramentoEmpenho desdEmpenho : empenho.getDesdobramentos()) {
                if (desdLiquidacao.getConta().equals(desdEmpenho.getConta())
                    && desdLiquidacao.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoPagar)
                    && desdEmpenho.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoPagar)) {

                    desdEmpenho.setSaldo(desdEmpenho.getSaldo().add(desdLiquidacao.getValor()));
                    obrigacaoPagar.setSaldo(obrigacaoPagar.getSaldo().add(desdLiquidacao.getValor()));
                    liquidacao.getEmpenho().setSaldoObrigacaoPagarAntesEmp(liquidacao.getEmpenho().getSaldoObrigacaoPagarAntesEmp().add(desdLiquidacao.getValor()));
                }
            }
        }
        return obrigacaoPagar;
    }

    private ObrigacaoAPagar movimentarSaldoDesdobramentoObrigacaoPagarDepoisEmpenho(LiquidacaoEstorno entity, Liquidacao liquidacao, ObrigacaoAPagar obrigacaoPagar) {
        for (DesdobramentoLiquidacaoEstorno desdLiquidacao : entity.getDesdobramentos()) {
            for (DesdobramentoObrigacaoPagar desdObrigacao : obrigacaoPagar.getDesdobramentos()) {
                if (desdLiquidacao.getDesdobramentoObrigacaoPagar().getObrigacaoAPagar().equals(obrigacaoPagar)
                    && desdLiquidacao.getConta().equals(desdObrigacao.getConta())) {
                    desdObrigacao.setSaldo(desdObrigacao.getSaldo().add(desdLiquidacao.getValor()));
                    obrigacaoPagar.setSaldo(obrigacaoPagar.getSaldo().add(desdLiquidacao.getValor()));
                    liquidacao.getEmpenho().setSaldoObrigacaoPagarDepoisEmp(liquidacao.getEmpenho().getSaldoObrigacaoPagarDepoisEmp().add(desdLiquidacao.getValor()));
                }
            }
        }
        return obrigacaoPagar;
    }

    //    Não remover, aguardando regra para ser utilizado
    private Liquidacao desvincularLiquidacaoDoctoFiscalGrupoMaterial(Liquidacao liquidacao) {
        if (liquidacao.getEmpenho().getTipoContaDespesa().isEstoque() && liquidacao.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            for (LiquidacaoDoctoFiscal doc : liquidacao.getDoctoFiscais()) {
                doc.setLiquidacao(null);
            }
        }
        return liquidacao;
    }

    public EventoContabil buscarEventoContabil(DesdobramentoLiquidacaoEstorno desdobramento) {
        EventoContabil eventoContabil = null;
        if (desdobramento.getLiquidacaoEstorno().isLiquidacaoEstornoCategoriaNormal()) {
            ConfigLiquidacao configLiquidacao = configLiquidacaoFacade.buscarCDELiquidacao(
                desdobramento.getConta(),
                TipoLancamento.ESTORNO,
                desdobramento.getLiquidacaoEstorno().getDataEstorno(),
                desdobramento.getLiquidacaoEstorno().getLiquidacao().getEmpenho().getSubTipoDespesa(),
                desdobramento.getLiquidacaoEstorno().getLiquidacao().getTipoReconhecimento());
            if (configLiquidacao != null) {
                eventoContabil = configLiquidacao.getEventoContabil();
            }
        } else {
            if (desdobramento.getLiquidacaoEstorno().getLiquidacao().isLiquidacaoRestoNaoProcessada()) {
                ConfigLiquidacaoResPagar configLiquidacaoRestoPagar = configLiquidacaoResPagarFacade.recuperaEventoPorContaDespesa(
                    desdobramento.getConta(),
                    TipoLancamento.ESTORNO,
                    desdobramento.getLiquidacaoEstorno().getDataEstorno(),
                    desdobramento.getLiquidacaoEstorno().getLiquidacao().getEmpenho().getSubTipoDespesa(),
                    desdobramento.getLiquidacaoEstorno().getLiquidacao().getTipoReconhecimento());
                if (configLiquidacaoRestoPagar != null) {
                    eventoContabil = configLiquidacaoRestoPagar.getEventoContabil();
                }
            }
        }
        return eventoContabil;
    }

    private void contabilzar(LiquidacaoEstorno entity) {
        for (DesdobramentoLiquidacaoEstorno desdobramento : entity.getDesdobramentos()) {
            if (entity.isLiquidacaoEstornoCategoriaNormal()) {
                contabilizarLiquidacaoEstorno(desdobramento);
            } else {
                contabilizarLiquidacaoEstornoResto(desdobramento);
            }
        }
    }

    private void contabilizarLiquidacaoEstornoResto(DesdobramentoLiquidacaoEstorno desdobramento) {
        if (desdobramento.getLiquidacaoEstorno().getLiquidacao().isLiquidacaoRestoNaoProcessada()) {
            if (!Hibernate.isInitialized(desdobramento.getLiquidacaoEstorno().getDesdobramentos())) {
                desdobramento.setLiquidacaoEstorno(recuperar(desdobramento.getLiquidacaoEstorno().getId()));
            }
            EventoContabil eventoContabil = buscarEventoContabil(desdobramento);
            if (eventoContabil != null) {
                desdobramento.setEventoContabil(eventoContabil);
            }
            desdobramento.getLiquidacaoEstorno().gerarHistoricos();
            contabilizacaoFacade.contabilizar(getParametroEvento(desdobramento));
        }
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        DesdobramentoLiquidacaoEstorno obj1 = (DesdobramentoLiquidacaoEstorno) entidadeContabil;
        if (obj1.getLiquidacaoEstorno().getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.NORMAL)) {
            contabilizarLiquidacaoEstorno(obj1);
        } else {
            contabilizarLiquidacaoEstornoResto(obj1);
        }
    }

    private void contabilizarLiquidacaoEstorno(DesdobramentoLiquidacaoEstorno desdobramento) {
        if (!Hibernate.isInitialized(desdobramento.getLiquidacaoEstorno().getDesdobramentos())) {
            desdobramento.setLiquidacaoEstorno(recuperar(desdobramento.getLiquidacaoEstorno().getId()));
        }
        EventoContabil eventoContabil = buscarEventoContabil(desdobramento);
        if (eventoContabil != null) {
            desdobramento.getLiquidacaoEstorno().gerarHistoricos();
            desdobramento.setEventoContabil(eventoContabil);
        }
        contabilizacaoFacade.contabilizar(getParametroEvento(desdobramento));
    }

    private List<ObjetoParametro> criarObjetosParametros(DesdobramentoLiquidacaoEstorno desdobramento, ItemParametroEvento item) {
        List<ObjetoParametro> objetos = new ArrayList<ObjetoParametro>();
        objetos.add(new ObjetoParametro(desdobramento.getConta().getId().toString(), ContaDespesa.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(desdobramento.getLiquidacaoEstorno().getLiquidacao().getEmpenho().getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
        if (desdobramento.getLiquidacaoEstorno().isLiquidacaoEstornoCategoriaNormal() && desdobramento.getLiquidacaoEstorno().getLiquidacao().getEmpenho().getDividaPublica() != null) {
            objetos.add(new ObjetoParametro(desdobramento.getLiquidacaoEstorno().getLiquidacao().getEmpenho().getDividaPublica().getCategoriaDividaPublica().getId().toString(), CategoriaDividaPublica.class.getSimpleName(), item));
        }
        if (DesdobramentoLiquidacaoEstorno.class.isAnnotationPresent(Entity.class)) {
            Entity entity = DesdobramentoLiquidacaoEstorno.class.getAnnotation(Entity.class);
            objetos.add(new ObjetoParametro(desdobramento.getId().toString(), entity.name(), item));
        } else {
            objetos.add(new ObjetoParametro(desdobramento.getId().toString(), DesdobramentoLiquidacaoEstorno.class.getSimpleName(), item));
        }
        return objetos;
    }

    private ItemParametroEvento criarItemParametroEvento(BigDecimal valor, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(valor);
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    public ParametroEvento getParametroEvento(EntidadeContabil entidadeContabil) {
        try {
            DesdobramentoLiquidacaoEstorno desdobramento = (DesdobramentoLiquidacaoEstorno) entidadeContabil;
            ParametroEvento parametroEvento = criarParametroEvento(desdobramento);
            ItemParametroEvento item = criarItemParametroEvento(desdobramento.getValor(), parametroEvento);
            List<ObjetoParametro> listaObj = criarObjetosParametros(desdobramento, item);
            item.setObjetoParametros(listaObj);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    private ParametroEvento criarParametroEvento(DesdobramentoLiquidacaoEstorno desdobramento) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(desdobramento.getLiquidacaoEstorno().getHistoricoRazao());
        parametroEvento.setDataEvento(desdobramento.getLiquidacaoEstorno().getDataEstorno());
        parametroEvento.setUnidadeOrganizacional(desdobramento.getLiquidacaoEstorno().getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(desdobramento.getEventoContabil());
        parametroEvento.setExercicio(desdobramento.getConta().getExercicio());
        parametroEvento.setClasseOrigem(desdobramento.getClass().getSimpleName());
        parametroEvento.setIdOrigem(desdobramento.getId().toString());
        return parametroEvento;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ObrigacaoAPagarFacade getObrigacaoAPagarFacade() {
        return obrigacaoAPagarFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public LiquidacaoFacade getLiquidacaoFacade() {
        return liquidacaoFacade;
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public ContabilizacaoFacade getContabilizacaoFacade() {
        return contabilizacaoFacade;
    }


    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    public DoctoFiscalLiquidacaoFacade getDoctoFiscalLiquidacaoFacade() {
        return doctoFiscalLiquidacaoFacade;
    }

    public DesdobramentoObrigacaoAPagarFacade getDesdobramentoObrigacaoAPagarFacade() {
        return desdobramentoObrigacaoAPagarFacade;
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(DesdobramentoLiquidacaoEstorno.class, filtros);
        consulta.incluirJoinsComplementar(" inner join liquidacaoestorno est on est.id = obj.liquidacaoestorno_id ");
        consulta.incluirJoinsComplementar(" inner join liquidacao liq on est.liquidacao_id = liq.id ");
        consulta.incluirJoinsComplementar(" inner join empenho emp on liq.empenho_id = emp.id ");
        consulta.incluirJoinsOrcamentoDespesa(" emp.fontedespesaorc_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(est.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(est.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "est.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "est.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "emp.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "emp.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "est.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "est.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "est.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_DESPESA, "obj.conta_id");
        return Arrays.asList(consulta);
    }

    public SolicitacaoLiquidacaoEstornoFacade getSolicitacaoLiquidacaoFacade() {
        return solicitacaoLiquidacaoFacade;
    }

    public List<NotaExecucaoOrcamentaria> buscarNotaLiquidacaoEstorno(String condicao, CategoriaOrcamentaria categoriaOrcamentaria) {
        String sql = " SELECT " +
            "    NOTA.numero||'/'||exerc.ano as numero, " +
            "    emp.numero || '/'||exerc.ano as numeroEmpenho, " +
            "    nota.dataestorno as data_nota, " +
            "    coalesce(nota.historiconota, 'Sem histórico') as historico_nota, " +
            "    emp.tipoempenho as tipo, " +
            "    TRIM(vworg.CODIGO) AS CD_ORGAO, " +
            "    TRIM(vworg.DESCRICAO) AS DESC_ORGAO, " +
            "    TRIM(vw.CODIGO) AS CD_UNIDADE, " +
            "    TRIM(vw.DESCRICAO) AS DESC_UNIDADE, " +
            "    f.codigo || '.' || sf.codigo || '.' || p.codigo || '.' || tpa.codigo || a.codigo || '.' || sub.codigo as CD_PROG_TRABALHO, " +
            "    ct_desp.codigo as elemento, " +
            "    ct_desp.descricao as especificao_despesa, " +
            "    coalesce(pf.nome, pj.razaosocial)as nome_pessoa, " +
            "    formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "    fonte_r.codigo || ' - ' || trim(fonte_r.descricao)  || ' (' || cdest.codigo || ')' as desc_destinacao, " +
            "    NOTA.valor as valor, " +
            "    frt_extenso_monetario(NOTA.valor)||'  ***********************' as valor_extenso, " +
            "    coalesce(endereco.logradouro, 'Cidade não cadastrada para essa pessoa') as logradouro, " +
            "    coalesce(endereco.localidade, 'Cidade não cadastrada para essa pessoa') as localidade, " +
            "    A.DESCRICAO AS DESC_ACAO, " +
            "    coalesce(lic.numero, disp.numeroDaDispensa, regext.numeromodalidade)  || ' - Processo Licitatório ' || coalesce(lic.numerolicitacao, disp.numerodadispensa, regext.numeroregistro) || ' - ' || to_char(coalesce(lic.emitidaem, disp.datadadispensa, regext.dataregistrocarona),'dd/MM/YYYY') as modalidade, " +
            "    emp.MODALIDADELICITACAO as MODALIDADELICITACAO, " +
            "    classe.codigo || ' - ' || classe.descricao as desc_classepessoa, " +
            (categoriaOrcamentaria.isResto()
                ? " COALESCE(EMP.SALDODISPONIVEL,0) AS SALDO_ANTERIOR, COALESCE(EMP.SALDODISPONIVEL,0)- EMP.VALOR AS SALDO_POSTERIOR, "
                : " COALESCE(LIQ.VALOR ,0) AS VALOR_LIQUIDADO, COALESCE(LIQ.saldo, 0) AS SALDO_LIQUIDAR, "
            ) +
            "    COALESCE(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "    COALESCE(ENDERECO.UF,'sem UF ') AS UF, " +
            "    COALESCE(SUBSTR(ENDERECO.CEP,1,5)||'-'||SUBSTR(ENDERECO.CEP,6,3),'sem CEP') AS CEP," +
            "    vw.subordinada_id as idUnidadeOrcamentaria " +
            " FROM liquidacaoestorno NOTA " +
            " inner join liquidacao liq on nota.liquidacao_id = liq.id " +
            " inner JOIN EMPENHO EMP ON LIQ.EMPENHO_ID = EMP.ID " +
            (categoriaOrcamentaria.isResto() ? " inner join empenho emp_original on emp.empenho_id = emp_original.id " : "") +
            " inner JOIN fontedespesaorc FONTE ON EMP.fontedespesaorc_id = fonte.id " +
            " inner JOIN despesaorc DESPESA ON fonte.despesaorc_id = despesa.id " +
            " inner JOIN provisaoppadespesa PROVISAO ON despesa.provisaoppadespesa_id= provisao.id " +
            " inner join provisaoppafonte ppf on ppf.id = fonte.PROVISAOPPAFONTE_ID " +
            " inner JOIN SUBACAOPPA SUB ON SUB.ID = provisao.subacaoppa_id " +
            " inner JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            " inner JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            " inner JOIN programappa P ON P.ID = A.programa_id " +
            " inner JOIN FUNCAO F ON F.ID = A.funcao_id " +
            " inner JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            " inner JOIN CONTA CT_DESP ON provisao.contadedespesa_id = CT_DESP.ID " +
            " inner join exercicio exerc on liq.exercicio_id = exerc.id " +
            " inner join vwhierarquiaorcamentaria vw on emp.unidadeorganizacional_id  = vw.subordinada_id " +
            " inner join vwhierarquiaorcamentaria vworg on vw.superior_id  = vworg.subordinada_id " +
            " left JOIN PESSOA pes ON emp.fornecedor_id = pes.id " +
            " left join pessoafisica pf on pes.id = pf.id " +
            " left join pessoajuridica pj on pes.id = pj.id " +
            " left join enderecocorreio endereco on pes.enderecoprincipal_id = endereco.id " +
            " left join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id " +
            " left join conta cdest on cdest.id = cd.id " +
            " left join fontederecursos fonte_r on fonte_r.id = cd.fontederecursos_id " +
            (categoriaOrcamentaria.isResto() ? "  and fonte_r.exercicio_id = emp_original.exercicio_id " : "  and fonte_r.exercicio_id = exerc.id ") +
            " left join contrato contrato on emp.contrato_id = contrato.id " +
            " left join conlicitacao conlic on conlic.contrato_id = contrato.id " +
            " left join licitacao lic on lic.id = conlic.licitacao_id " +
            " left join condispensalicitacao conDisp on condisp.contrato_id = contrato.id " +
            " left join dispensadelicitacao disp on disp.id = condisp.dispensadelicitacao_id " +
            " left join CONREGPRECOEXTERNO conReg on conreg.contrato_id = contrato.id " +
            " left join REGISTROSOLMATEXT regExt on regExt.id = conreg.regsolmatext_id " +
            " left join classecredor classe on emp.classecredor_id = classe.id " +
            " WHERE trunc(nota.dataestorno) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(nota.dataestorno)) " +
            "   AND trunc(nota.dataestorno) BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, trunc(nota.dataestorno)) " +
            "   AND nota.categoriaOrcamentaria = :categoriaOrcamentaria " +
            condicao;
        Query q = em.createNativeQuery(sql);
        q.setParameter("categoriaOrcamentaria", categoriaOrcamentaria.name());
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nle = new NotaExecucaoOrcamentaria();
                nle.setNumeroDocumento((String) obj[0]);
                nle.setNumeroEmpenho((String) obj[1]);
                nle.setDataEstorno(DataUtil.getDataFormatada((Date) obj[2]));
                nle.setHistorico((String) obj[3]);
                nle.setTipoEmpenho((String) obj[4]);
                nle.setCodigoOrgao((String) obj[5]);
                nle.setDescricaoOrgao((String) obj[6]);
                nle.setCodigoUnidade((String) obj[7]);
                nle.setDescricaoUnidade((String) obj[8]);
                nle.setCodigoProgramaTrabalho((String) obj[9]);
                nle.setNaturezaDespesa((String) obj[10]);
                nle.setEspecificacaoDespesa((String) obj[11]);
                nle.setNomePessoa((String) obj[12]);
                nle.setCpfCnpj((String) obj[13]);
                nle.setDescricaoDestinacao((String) obj[14]);
                nle.setValorEstorno((BigDecimal) obj[15]);
                nle.setValorPorExtenso((String) obj[16]);
                nle.setLogradouro((String) obj[17]);
                nle.setCidade((String) obj[18]);
                nle.setDescricaoProjetoAtividade((String) obj[19]);
                nle.setModalidadeLicitacao(obj[21] != null ? ModalidadeLicitacaoEmpenho.valueOf((String) obj[21]).getDescricao() + " " + (String) obj[20] : "");
                nle.setClassePessoa((String) obj[22]);
                if (categoriaOrcamentaria.isResto()) {
                    nle.setSaldoAnterior((BigDecimal) obj[23]);
                } else {
                    nle.setValor((BigDecimal) obj[23]);
                }
                nle.setSaldoAtual((BigDecimal) obj[24]);
                nle.setBairro((String) obj[25]);
                nle.setUf((String) obj[26]);
                nle.setCep((String) obj[27]);
                nle.setIdUnidadeOrcamentaria((BigDecimal) obj[28]);
                retorno.add(nle);
            }
        }
        return retorno;
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public ConfigGrupoMaterialFacade getConfigGrupoMaterialFacade() {
        return configGrupoMaterialFacade;
    }

    public ConfigDespesaBensFacade getConfigDespesaBensFacade() {
        return configDespesaBensFacade;
    }

    public IntegradorDocumentoFiscalLiquidacaoFacade getIntegradorDocumentoFiscalLiquidacaoFacade() {
        return integradorDocumentoFiscalLiquidacaoFacade;
    }

    public EntradaMaterialFacade getEntradaMaterialFacade() {
        return entradaMaterialFacade;
    }

    public void recuperarTransferenciasLiquidacaoDoctoFiscal(LiquidacaoEstorno entity) {
        entity.getDocumentosFiscais().forEach(ldf -> {
            ldf.getTransferenciasBens().forEach(transferencia -> {
                switch (transferencia.getTipoContaDespesa()) {
                    case BEM_MOVEL:
                        transferencia.setTransfBensMoveis(em.find(TransfBensMoveis.class, transferencia.getIdTransferencia()));
                        break;

                    case BEM_ESTOQUE:
                        transferencia.setTransferenciaBensEstoque(em.find(TransferenciaBensEstoque.class, transferencia.getIdTransferencia()));
                        break;
                }
            });
        });
    }
}
