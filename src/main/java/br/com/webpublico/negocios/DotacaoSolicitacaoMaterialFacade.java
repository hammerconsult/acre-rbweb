/*
 * Codigo gerado automaticamente em Wed Apr 18 18:47:14 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AgrupadorReservaSolicitacaoCompraVO;
import br.com.webpublico.entidadesauxiliares.DotacaoSolicitacaoMaterialItemVO;
import br.com.webpublico.entidadesauxiliares.FiltroDotacaoProcessoCompraVO;
import br.com.webpublico.entidadesauxiliares.contabil.MovimentoDespesaORCVO;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.contabil.execucao.ExtratoMovimentoDespesaOrcFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Stateless
public class DotacaoSolicitacaoMaterialFacade extends AbstractFacade<DotacaoSolicitacaoMaterial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;
    @EJB
    private ConvenioDespesaFacade convenioDespesaFacade;
    @EJB
    private SolicitacaoMaterialFacade solicitacaoMaterialFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ReprocessamentoSaldoFonteDespesaOrcFacade reprocessamentoSaldoFonteDespesaOrcFacade;
    @EJB
    private ExtratoMovimentoDespesaOrcFacade extratoMovimentoDespesaOrcFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private ExecucaoProcessoFacade execucaoProcessoFacade;
    @EJB
    private DotacaoProcessoCompraFacade dotacaoProcessoCompraFacade;

    public DotacaoSolicitacaoMaterialFacade() {
        super(DotacaoSolicitacaoMaterial.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public DotacaoSolicitacaoMaterial recuperar(Object id) {
        DotacaoSolicitacaoMaterial entity = super.recuperar(id);
        Hibernate.initialize(entity.getItens());
        for (DotacaoSolicitacaoMaterialItem dotTipo : entity.getItens()) {
            Hibernate.initialize(dotTipo.getFontes());
        }
        return entity;
    }

    public DotacaoSolicitacaoMaterial salvarRetornando(DotacaoSolicitacaoMaterial entity, List<DotacaoSolicitacaoMaterialItemVO> reservas) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(DotacaoSolicitacaoMaterial.class, "codigo"));
        }
        criarDotacaoSolicitacaoMaterialItemFonteFromVO(entity, reservas);
        return em.merge(entity);
    }

    private void criarDotacaoSolicitacaoMaterialItemFonteFromVO(DotacaoSolicitacaoMaterial entity, List<DotacaoSolicitacaoMaterialItemVO> itens) {
        entity.setItens(Lists.newArrayList());
        itens.forEach(dotTipoVO -> {
            if (dotTipoVO.getValor().compareTo(BigDecimal.ZERO) > 0) {
                DotacaoSolicitacaoMaterialItem novoDotTipo;
                List<AgrupadorReservaSolicitacaoCompraVO> agrupadores = dotTipoVO.getAgrupadorFontes();
                novoDotTipo = new DotacaoSolicitacaoMaterialItem();
                novoDotTipo.setDotacaoSolicitacaoMaterial(entity);
                novoDotTipo.setValor(dotTipoVO.getValor());
                novoDotTipo.setTipoObjetoCompra(dotTipoVO.getTipoObjetoCompra());

                agrupadores.stream()
                    .flatMap(agrupador -> agrupador.getDotacoesFonte().stream())
                    .forEach(dotFonteVO -> {
                        DotacaoSolicitacaoMaterialItemFonte novaDotFonte = new DotacaoSolicitacaoMaterialItemFonte();
                        novaDotFonte.setDotacaoSolMatItem(novoDotTipo);
                        novaDotFonte.setIdOrigem(dotFonteVO.getIdOrigem() != null ? dotFonteVO.getIdOrigem() : entity.getId());
                        novaDotFonte.setDataLancamento(dotFonteVO.getDataLancamento());
                        novaDotFonte.setFonteDespesaORC(dotFonteVO.getFonteDespesaORC());
                        novaDotFonte.setExercicio(dotFonteVO.getExercicio());
                        novaDotFonte.setTipoOperacao(dotFonteVO.getTipoOperacao());
                        novaDotFonte.setTipoReserva(dotFonteVO.getTipoReserva());
                        novaDotFonte.setValor(dotFonteVO.getValorReservado());
                        novaDotFonte.setGeraReservaOrc(verificarGeraReservaOrcamentaria(entity, novaDotFonte));
                        Util.adicionarObjetoEmLista(novoDotTipo.getFontes(), novaDotFonte);
                    });
                Util.adicionarObjetoEmLista(entity.getItens(), novoDotTipo);
            }
        });
    }

    private boolean verificarGeraReservaOrcamentaria(DotacaoSolicitacaoMaterial entity, DotacaoSolicitacaoMaterialItemFonte dotFonte) {
        if (dotFonte.getTipoReserva().isSolicitacaoCompra()
            && !entity.getSolicitacaoMaterial().isExercicioProcessoDiferenteExercicioAtual(sistemaFacade.getDataOperacao())) {
            return true;
        }
        Date dataReferenciaReservaDotacao = configuracaoLicitacaoFacade.getDataReferenciaReservaDotacao();
        return dotFonte.getTipoReserva().isSolicitacaoCompra()
            && dataReferenciaReservaDotacao != null
            && Util.getDataHoraMinutoSegundoZerado(entity.getSolicitacaoMaterial().getDataSolicitacao()).before(Util.getDataHoraMinutoSegundoZerado(dataReferenciaReservaDotacao));
    }

    public SaldoFonteDespesaORCVO criarSaldoFonteDespesaORCVO(DotacaoSolicitacaoMaterialItemFonte fonte, TipoOperacaoORC tipoOperacaoORC) {
        return new SaldoFonteDespesaORCVO(
            fonte.getFonteDespesaORC(),
            OperacaoORC.RESERVADO_POR_LICITACAO,
            tipoOperacaoORC,
            fonte.getValor(),
            fonte.getDataLancamento(),
            fonte.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional(),
            fonte.getId().toString(),
            fonte.getClass().getSimpleName(),
            fonte.getDotacaoSolMatItem().getDotacaoSolicitacaoMaterial().getCodigo().toString(),
            getHistoricoDotacao(fonte.getFonteDespesaORC(), fonte.getDotacaoSolMatItem().getDotacaoSolicitacaoMaterial().getSolicitacaoMaterial().getUnidadeOrganizacional())
        );
    }

    private String getHistoricoDotacao(FonteDespesaORC fonteDespOrc, UnidadeOrganizacional unidadeAdm) {
        return " Unidade Administrativa: " + hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), unidadeAdm, TipoHierarquiaOrganizacional.ADMINISTRATIVA).toString() + " | " +
            " Elemento de Despesa: " + fonteDespOrc.getDespesaORC() + "/" + fonteDespOrc.getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " | " +
            " Fonte de Recurso: " + fonteDespOrc.getDescricaoFonteDeRecurso().trim();
    }

    private void validarSaldoFonteDespesaOrc(TipoOperacaoORC tipoOperacaoORC, FonteDespesaORC fonte, BigDecimal valor) {
        if (tipoOperacaoORC.isNormal()) {
            ValidacaoException ve = new ValidacaoException();
            SaldoFonteDespesaORC saldoFonteDespesaORC = saldoFonteDespesaORCFacade.recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(fonte,
                sistemaFacade.getDataOperacao(), fonte.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());

            if (valor.compareTo(saldoFonteDespesaORC.getSaldoAtual()) > 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O valor à reservar R$ " + Util.formataNumero(valor) + " para a fonte de recurso " +
                    fonte + " é superior ao seu saldo atual. Saldo da fonte de recurso R$ " + Util.formataNumero(saldoFonteDespesaORC.getSaldoAtual()));
            }
            ve.lancarException();
        }
    }

    public void reservarDotacaoSolicitacaoMaterial(DotacaoSolicitacaoMaterial entity, TipoOperacaoORC tipoOperacaoORC) {
        entity.getItens().stream()
            .flatMap(item -> item.getFontes().stream())
            .filter(DotacaoSolicitacaoMaterialItemFonte::getGeraReservaOrc)
            .map(fonte -> criarSaldoFonteDespesaORCVO(fonte, tipoOperacaoORC))
            .forEach(saldoVO -> saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(saldoVO));
    }

    public void validarExclusao(DotacaoSolicitacaoMaterial entity) {
        TipoStatusSolicitacao statusAtualDaSolicitacao = solicitacaoMaterialFacade.getStatusAtualDaSolicitacao(entity.getSolicitacaoMaterial());
        ValidacaoException ve = new ValidacaoException();
        if (TipoStatusSolicitacao.APROVADA.equals(statusAtualDaSolicitacao)
            && (!entity.getSolicitacaoMaterial().getModalidadeLicitacao().isDispensaLicitacao()
            && !entity.getSolicitacaoMaterial().getModalidadeLicitacao().isInexigibilidade())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não será possível excluir a reserva, pois a solicitação de compra já está aprovada.");
        }
        if (verificarVinculoComProcessoCompra(entity)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não será possível excluir a reserva, pois a solicitação de compra já está vinculada a um processo de compra.");
        }
        ve.lancarException();
    }

    public List<DotacaoSolicitacaoMaterial> recuperarDotacoesAPartirDaLicitacao(Licitacao licitacao) {
        String sql = " SELECT DOT.* "
            + "FROM FORMULARIOCOTACAO FORC "
            + "INNER JOIN COTACAO COT ON COT.FORMULARIOCOTACAO_ID = FORC.ID "
            + "INNER JOIN SOLICITACAOMATERIAL SM ON SM.COTACAO_ID = COT.ID "
            + "INNER JOIN DOTSOLMAT DOT ON DOT.SOLICITACAOMATERIAL_ID = SM.ID "
            + "INNER JOIN LOTESOLICITACAOMATERIAL LOTSOLMAT ON LOTSOLMAT.SOLICITACAOMATERIAL_ID = SM.ID "
            + "INNER JOIN ITEMSOLICITACAO ITSOL ON ITSOL.LOTESOLICITACAOMATERIAL_ID = LOTSOLMAT.ID "
            + "INNER JOIN ITEMPROCESSODECOMPRA ITPC ON ITPC.ITEMSOLICITACAOMATERIAL_ID = ITSOL.ID "
            + "INNER JOIN LOTEPROCESSODECOMPRA LOTEPC ON LOTEPC.ID = ITPC.LOTEPROCESSODECOMPRA_ID "
            + "INNER JOIN PROCESSODECOMPRA PROCOM ON PROCOM.ID = LOTEPC.PROCESSODECOMPRA_ID "
            + "INNER JOIN LICITACAO LIC ON LIC.PROCESSODECOMPRA_ID = PROCOM.ID "
            + "WHERE LIC.ID = :idLicitacao ";

        Query q = em.createNativeQuery(sql, DotacaoSolicitacaoMaterial.class);
        q.setParameter("idLicitacao", licitacao.getId());

        if (q.getResultList() != null || !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList();
    }

    public DotacaoSolicitacaoMaterial recuperarDotacaoAPartirDoProcessoDeCompra(ProcessoDeCompra processoDeCompra) {
        String sql = " SELECT DOT.* "
            + " from PROCESSODECOMPRA proc "
            + "INNER JOIN SOLICITACAOMATERIAL SM ON SM.id = proc.solicitacaomaterial_ID "
            + "INNER JOIN DOTSOLMAT DOT ON DOT.SOLICITACAOMATERIAL_ID = SM.ID "
            + "WHERE proc.ID = :idProcessoDeCompra ";
        Query q = em.createNativeQuery(sql, DotacaoSolicitacaoMaterial.class);
        q.setParameter("idProcessoDeCompra", processoDeCompra.getId());
        try {
            q.setMaxResults(1);
            return (DotacaoSolicitacaoMaterial) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public DotacaoSolicitacaoMaterial recuperarDotacaoProcessoLicitatorio(Long idProcesso) {
        String sql = " select dsm.* from dotsolmat dsm " +
            "           inner join solicitacaomaterial sol on sol.id = dsm.solicitacaomaterial_id " +
            "           inner join cotacao cot on cot.id = sol.cotacao_id " +
            "           inner join formulariocotacao fc on fc.id = cot.formulariocotacao_id " +
            "           left join intencaoregistropreco irp on irp.id = fc.intencaoregistropreco_id " +
            "           left join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
            "           left join licitacao lic on lic.processodecompra_id = pc.id " +
            "          left join dispensadelicitacao disp on disp.processodecompra_id = pc.id " +
            "         where (disp.id = :idProcesso or cot.id = :idProcesso or fc.id = :idProcesso or irp.id = :idProcesso or lic.id = :idProcesso or disp.id = :idProcesso) ";
        Query q = em.createNativeQuery(sql, DotacaoSolicitacaoMaterial.class);
        q.setParameter("idProcesso", idProcesso);
        try {
            q.setMaxResults(1);
            return (DotacaoSolicitacaoMaterial) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public DotacaoSolicitacaoMaterial recuperarDotacaoPorSolicitacaoCompra(SolicitacaoMaterial solicitacaoMaterial) {
        String sql = " SELECT DOT.* from DOTSOLMAT DOT WHERE dot.solicitacaomaterial_ID = :idSolicitacao ";
        Query q = em.createNativeQuery(sql, DotacaoSolicitacaoMaterial.class);
        q.setParameter("idSolicitacao", solicitacaoMaterial.getId());
        try {
            q.setMaxResults(1);
            return (DotacaoSolicitacaoMaterial) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public BigDecimal buscarValorReservadoPorProcesso(ProcessoDeCompra processoDeCompra, TipoObjetoCompra tipoObjetoCompra, Exercicio exercicio) {
        String sql = " " +
            "  select coalesce(sum(vl_reservado),0) from ( " +
            "       select case " +
            "              when fonte.TIPOOPERACAO = :operacaoNormal " +
            "                then coalesce(fonte.valor, 0) " +
            "                else coalesce(fonte.valor * -1, 0) " +
            "              end as vl_reservado " +
            "  from dotacaosolmatitemfonte fonte " +
            "    inner join dotacaosolmatitem item on item.id = fonte.dotacaosolmatitem_id " +
            "    inner join dotsolmat dot on dot.id = item.dotacaosolicitacaomaterial_id " +
            "    inner join solicitacaomaterial sm on sm.id = dot.solicitacaomaterial_id " +
            "    inner join processodecompra pc on pc.solicitacaomaterial_id = sm.id " +
            "  where pc.id = :idProcessoCompra " +
            "        and item.tipoObjetoCompra = :tipoObjetoCompra ";
        sql += exercicio != null ? " and fonte.exercicio_id = :idExercicio " : "";
        sql += ") ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcessoCompra", processoDeCompra.getId());
        q.setParameter("tipoObjetoCompra", tipoObjetoCompra.name());
        q.setParameter("operacaoNormal", TipoOperacaoORC.NORMAL.name());
        if (exercicio != null) {
            q.setParameter("idExercicio", exercicio.getId());
        }
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException ne) {
            return BigDecimal.ZERO;
        }
    }

    public List<DotacaoSolicitacaoMaterial> recuperarDotacoesAPartirDaDispensaLicitacao(DispensaDeLicitacao dispensaDeLicitacao) {
        String sql = " SELECT DOT.* "
            + "FROM FORMULARIOCOTACAO FORC "
            + "INNER JOIN COTACAO COT ON COT.FORMULARIOCOTACAO_ID = FORC.ID "
            + "INNER JOIN SOLICITACAOMATERIAL SM ON SM.COTACAO_ID = COT.ID "
            + "INNER JOIN DOTSOLMAT DOT ON DOT.SOLICITACAOMATERIAL_ID = SM.ID "
            + "INNER JOIN LOTESOLICITACAOMATERIAL LOTSOLMAT ON LOTSOLMAT.SOLICITACAOMATERIAL_ID = SM.ID "
            + "INNER JOIN ITEMSOLICITACAO ITSOL ON ITSOL.LOTESOLICITACAOMATERIAL_ID = LOTSOLMAT.ID "
            + "INNER JOIN ITEMPROCESSODECOMPRA ITPC ON ITPC.ITEMSOLICITACAOMATERIAL_ID = ITSOL.ID "
            + "INNER JOIN LOTEPROCESSODECOMPRA LOTEPC ON LOTEPC.ID = ITPC.LOTEPROCESSODECOMPRA_ID "
            + "INNER JOIN PROCESSODECOMPRA PROCOM ON PROCOM.ID = LOTEPC.PROCESSODECOMPRA_ID "
            + "INNER JOIN DISPENSADELICITACAO DIS ON DIS.PROCESSODECOMPRA_ID = PROCOM.ID "
            + "WHERE DIS.ID = :idDispensa ";

        Query q = em.createNativeQuery(sql, DotacaoSolicitacaoMaterial.class);
        q.setParameter("idDispensa", dispensaDeLicitacao.getId());

        if (q.getResultList() != null || !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList();
    }

    public boolean verificarVinculoComProcessoCompra(DotacaoSolicitacaoMaterial entity) {
        String sql = " select pc.* from processodecompra pc  " +
            "           inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id" +
            "           inner join dotsolmat dsm on dsm.solicitacaomaterial_id = sol.id " +
            "          where sol.id = :idSolicitacao  ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idSolicitacao", entity.getSolicitacaoMaterial().getId());
        query.setMaxResults(1);
        try {
            return query.getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    public boolean verificarVinculoComExecucao(DotacaoSolicitacaoMaterialItemFonte dotFonte) {
        String sql = " select ex.id from execucaocontrato ex " +
            "         inner join contrato c on c.id = ex.contrato_id " +
            "         inner join execucaocontratotipo extipo on extipo.execucaocontrato_id = ex.id " +
            "         inner join execucaocontratotipofonte exfonte on exfonte.execucaocontratotipo_id = extipo.id " +
            "         left join conlicitacao cl on cl.contrato_id = c.id " +
            "         left join licitacao l on cl.licitacao_id = l.id " +
            "         left join condispensalicitacao cdl on cdl.contrato_id = c.id " +
            "         left join dispensadelicitacao dl on cdl.dispensadelicitacao_id = dl.id " +
            "         inner join processodecompra pc on pc.id = coalesce(l.processodecompra_id, dl.processodecompra_id) " +
            "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
            "         inner join dotsolmat dsm on dsm.solicitacaomaterial_id = sol.id " +
            " where dsm.id = :idDotSolMat " +
            "       and exfonte.fontedespesaorc_id = :idFonte " +
            " union all " +
            " select ex.id from execucaoprocesso ex " +
            "         inner join execucaoprocessoreserva exr on exr.execucaoprocesso_id = ex.id " +
            "         inner join execucaoprocessofonte exf on exf.execucaoprocessoreserva_id = exr.id " +
            "         inner join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "         inner join dispensadelicitacao dl on dl.id = exdisp.dispensalicitacao_id " +
            "         inner join processodecompra pc on pc.id = dl.processodecompra_id " +
            "         inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
            "         inner join dotsolmat dsm on dsm.solicitacaomaterial_id = sol.id " +
            " where dsm.id = :idDotSolMat " +
            "       and exf.fontedespesaorc_id = :idFonte ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("idDotSolMat", dotFonte.getDotacaoSolMatItem().getDotacaoSolicitacaoMaterial().getId());
        query.setParameter("idFonte", dotFonte.getFonteDespesaORC().getId());
        return !Util.isListNullOrEmpty(query.getResultList());
    }

    public DotacaoSolicitacaoMaterialItemFonte recuperarDotacaoSolicitacaoMaterialFontePorFonteDespesaOrc(SolicitacaoMaterial solicitacaoMaterial, FonteDespesaORC fonteDespesaORC) {
        String sql = " " +
            "     select dsmif.* from dotsolmat dsm " +
            "       inner join solicitacaomaterial sm on sm.id = dsm.solicitacaomaterial_id " +
            "       inner join dotacaosolmatitem dsmi on dsmi.dotacaosolicitacaomaterial_id = dsm.id " +
            "       inner join dotacaosolmatitemfonte dsmif on dsmif.dotacaosolmatitem_id = dsmi.id " +
            "     where sm.id = :idSolicitacaoCompra " +
            "       and dsmif.tipooperacao = :tipoOperacao " +
            "       and dsmif.fontedespesaorc_id = :idFonteDespOrc ";
        Query q = em.createNativeQuery(sql, DotacaoSolicitacaoMaterialItemFonte.class);
        q.setParameter("idFonteDespOrc", fonteDespesaORC.getId());
        q.setParameter("idSolicitacaoCompra", solicitacaoMaterial.getId());
        q.setParameter("tipoOperacao", TipoOperacaoORC.NORMAL.name());
        try {
            return (DotacaoSolicitacaoMaterialItemFonte) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Dotação fonte solicitação de compra não encontrada para a fonte despesa " + fonteDespesaORC + ".");
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica("Encontrado mais de uma dotação fonte solicitação de compra para a fonte despesa " + fonteDespesaORC + ".");
        }
    }

    public List<MovimentoDespesaORCVO> buscarMovimentosDespesasPorFonte(FiltroDotacaoProcessoCompraVO filtro) {
        String sql = " select distinct movimento.tipooperacaoorc, " +
            "                movimento.operacaoorc, " +
            "                movimento.datamovimento, " +
            "                movimento.classeorigem, " +
            "                movimento.idorigem, " +
            "                do.codigo || ' - ' || cdesp.codigo       as despesa, " +
            "                cdest.codigo || ' - ' || cdest.descricao as fonte, " +
            "                movimento.historico, " +
            "                movimento.valor, " +
            "                movimento.numeromovimento, " +
            "                movimento.id " +
            " from ( " +
            "         select mov.* " + //recupera reservado por licitação (NORMAL e ESTORNO) do processo de compra
            "         from movimentodespesaorc mov " +
            "                  inner join dotacaosolmatitemfonte fonte on fonte.id = mov.idorigem " +
            "                  inner join dotacaosolmatitem item on item.id = fonte.dotacaosolmatitem_id " +
            "                  inner join dotsolmat dsm on dsm.id = item.dotacaosolicitacaomaterial_id " +
            "         where dsm.solicitacaomaterial_id = :idSolicitacao " +
            "         union all " +
            "         select mov.* " + //recupera reservado por licitação (NORMAL) da execução de contrato
            "         from movimentodespesaorc mov " +
            "                  inner join execucaocontratotipofonte fonte on fonte.id = mov.idorigem " +
            "                  inner join execucaocontratotipo item on item.id = fonte.execucaocontratotipo_id " +
            "                  inner join execucaocontrato ex on ex.id = item.execucaocontrato_id " +
            "                  inner join contrato c on c.id = ex.contrato_id " +
            "                  left join conlicitacao cl on cl.contrato_id = c.id " +
            "                  left join licitacao l on l.id = cl.licitacao_id " +
            "                  left join condispensalicitacao cdl on cdl.contrato_id = c.id " +
            "                  left join dispensadelicitacao dl on dl.id = cdl.dispensadelicitacao_id " +
            "                  inner join processodecompra pdc on pdc.id = coalesce(dl.processodecompra_id, l.processodecompra_id) " +
            "         where pdc.solicitacaomaterial_id = :idSolicitacao " +
            "         union all " +
            "         select mov.* " + //recupera reservado por licitação (NORMAL) da execução sem contrato
            "         from movimentodespesaorc mov " +
            "                  inner join execucaoprocessofonte fonte on fonte.id = mov.idorigem " +
            "                  inner join execucaoprocessoreserva item on item.id = fonte.execucaoprocessoreserva_id " +
            "                  inner join execucaoprocesso ex on ex.id = item.execucaoprocesso_id " +
            "                  left join execucaoprocessoata exata on exata.execucaoprocesso_id = ex.id " +
            "                  left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "                  left join licitacao l on l.id = ata.licitacao_id " +
            "                  left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = ex.id " +
            "                  left join dispensadelicitacao disp on disp.id = exdisp.dispensalicitacao_id " +
            "                  inner join processodecompra pdc on pdc.id = coalesce(l.processodecompra_id, disp.processodecompra_id) " +
            "         where pdc.solicitacaomaterial_id = :idSolicitacao " +
            "         union all " +
            "         select mov.* " + //recupera empenhado (NORMAL) quando o empenho é empenhado
            "         from movimentodespesaorc mov " +
            "                  left join empenho emp1 on '9' || emp1.id = mov.idorigem " +
            "                  left join empenho emp2 on emp2.id = mov.idorigem " +
            "                  inner join solicitacaoempenho sol on sol.empenho_id = coalesce(emp1.id, emp2.id) " +
            "                  left join execucaocontratoempenho ece on ece.solicitacaoempenho_id = sol.id " +
            "                  left join execucaocontrato ec on ec.id = ece.execucaocontrato_id " +
            "                  left join contrato c on c.id = ec.contrato_id " +
            "                  left join conlicitacao cl on cl.contrato_id = c.id " +
            "                  left join licitacao l on l.id = cl.licitacao_id " +
            "                  left join condispensalicitacao cdl on cdl.contrato_id = c.id " +
            "                  left join dispensadelicitacao dl on dl.id = cdl.dispensadelicitacao_id " +
            "                  left join execucaoprocesso exeproc on exeproc.id = coalesce(emp1.execucaoprocesso_id, emp2.execucaoprocesso_id) " +
            "                  left join execucaoprocessoata exata on exata.execucaoprocesso_id = exeproc.id " +
            "                  left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "                  left join licitacao licata on ata.licitacao_id = licata.id " +
            "                  left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = exeproc.id " +
            "                  left join dispensadelicitacao dispproc on dispproc.id = exdisp.dispensalicitacao_id " +
            "                  inner join processodecompra pdc on coalesce(dl.processodecompra_id, l.processodecompra_id, dispproc.processodecompra_id, licata.processodecompra_id) = pdc.id " +
            "         where pdc.solicitacaomaterial_id = :idSolicitacao " +
            "           and mov.operacaoorc = :operacaoEmpenhado " +
            "         union all " +
            "         select mov.* " + //recupera empenhado (ESTORNO) quando acontece o estorno do empenho
            "         from movimentodespesaorc mov " +
            "                  inner join empenhoestorno empest on empest.id = mov.idorigem " +
            "                  inner join empenho emp on emp.id = empest.empenho_id " +
            "                  inner join solicitacaoempenho sol on sol.empenho_id = emp.id " +
            "                  left join execucaocontratoempenho ece on ece.solicitacaoempenho_id = sol.id " +
            "                  left join execucaocontrato ec on ec.id = ece.execucaocontrato_id " +
            "                  left join contrato c on c.id = ec.contrato_id " +
            "                  left join conlicitacao cl on cl.contrato_id = c.id " +
            "                  left join licitacao l on l.id = cl.licitacao_id " +
            "                  left join condispensalicitacao cdl on cdl.contrato_id = c.id " +
            "                  left join dispensadelicitacao dl on dl.id = cdl.dispensadelicitacao_id " +
            "                  left join execucaoprocesso exeproc on exeproc.id = emp.execucaoprocesso_id " +
            "                  left join execucaoprocessoata exata on exata.execucaoprocesso_id = exeproc.id " +
            "                  left join ataregistropreco ata on ata.id = exata.ataregistropreco_id " +
            "                  left join licitacao licata on ata.licitacao_id = licata.id " +
            "                  left join execucaoprocessodispensa exdisp on exdisp.execucaoprocesso_id = exeproc.id " +
            "                  left join dispensadelicitacao dispproc on dispproc.id = exdisp.dispensalicitacao_id " +
            "                  inner join processodecompra pdc on coalesce(dl.processodecompra_id, l.processodecompra_id, dispproc.processodecompra_id, licata.processodecompra_id) = pdc.id " +
            "         where pdc.solicitacaomaterial_id = :idSolicitacao " +
            "           and mov.operacaoorc = :operacaoEmpenhado " +
            "         union all " +
            "         select mov.* " + //recupera reservado por licitação (ESTORNO) quando o valor original do contrato é suprimido
            "         from movimentodespesaorc mov " +
            "                  inner join alteracaocontratual ac on ac.id = mov.idorigem " +
            "                  inner join movimentoalteracaocont mac on mac.alteracaocontratual_id = ac.id " +
            "                  inner join contrato c on c.id = ac.contrato_id " +
            "                  left join conlicitacao cl on cl.contrato_id = c.id " +
            "                  left join licitacao l on l.id = cl.licitacao_id " +
            "                  left join condispensalicitacao cdl on cdl.contrato_id = c.id " +
            "                  left join dispensadelicitacao dl on dl.id = cdl.dispensadelicitacao_id " +
            "                  inner join processodecompra pdc on pdc.id = coalesce(dl.processodecompra_id, l.processodecompra_id) " +
            "         where pdc.solicitacaomaterial_id = :idSolicitacao " +
            "           and mac.gerarreserva = :geraReserva" +
            "     union all " +
            "      select mov.* " +
            "      from movimentodespesaorc mov " +
            "               inner join movimentoalteracaocont movac on movac.id = mov.idorigem " +
            "               inner join alteracaocontratual ac on ac.id = movac.alteracaocontratual_id " +
            "               left join alteracaocontratualcont acc on acc.alteracaocontratual_id = ac.id " +
            "               left join contrato c on c.id = acc.contrato_id " +
            "               left join conlicitacao cl on cl.contrato_id = c.id " +
            "               left join condispensalicitacao cdl on cdl.contrato_id = c.id " +
            "               left join alteracaocontratualata aca on aca.alteracaocontratual_id = ac.id " +
            "               left join ataregistropreco ata on ata.id = aca.ataregistropreco_id " +
            "               left join licitacao l on l.id = coalesce(cl.licitacao_id, ata.licitacao_id) " +
            "               left join dispensadelicitacao dl on dl.id = cdl.dispensadelicitacao_id " +
            "               inner join processodecompra pdc on pdc.id = coalesce(dl.processodecompra_id, l.processodecompra_id) " +
            "      where pdc.solicitacaomaterial_id = :idSolicitacao " +
            ") movimento " +
            "         inner join fontedespesaorc fdo on fdo.id = movimento.fontedespesaorc_id " +
            "         inner join despesaorc do on do.id = fdo.despesaorc_id " +
            "         inner join provisaoppadespesa ppd on ppd.id = do.provisaoppadespesa_id " +
            "         inner join conta cdesp on cdesp.id = ppd.contadedespesa_id " +
            "         inner join provisaoppafonte ppf on ppf.id = fdo.provisaoppafonte_id " +
            "         inner join conta cdest on cdest.id = ppf.destinacaoderecursos_id " +
            " where fdo.id = :idFonteDespesaOrc " +
            " order by movimento.datamovimento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", filtro.getSolicitacaoMaterial().getId());
        q.setParameter("idFonteDespesaOrc", filtro.getFonteDespesaORC().getId());
        q.setParameter("operacaoEmpenhado", OperacaoORC.EMPENHO.name());
        q.setParameter("geraReserva", Boolean.TRUE);
        List<Object[]> resultado = q.getResultList();
        List<MovimentoDespesaORCVO> retorno = Lists.newArrayList();

        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                MovimentoDespesaORCVO vo = new MovimentoDespesaORCVO();
                vo.setTipoOperacaoORC(TipoOperacaoORC.valueOf((String) obj[0]).getDescricao());
                vo.setOperacaoORC(OperacaoORC.valueOf((String) obj[1]));
                vo.setDataMovimento((Date) obj[2]);
                vo.setClasseOrigem((String) obj[3]);
                vo.setIdOrigem((String) obj[4]);
                vo.setDespesa((String) obj[5]);
                vo.setFonte((String) obj[6]);
                vo.setHistorico((String) obj[7]);
                vo.setValor((BigDecimal) obj[8]);
                vo.setNumeroMovimento((String) obj[9]);
                vo.setHref(extratoMovimentoDespesaOrcFacade.montarHrefMovimento(vo));
                retorno.add(vo);
            }
        }
        return retorno;
    }

    public List<DotacaoSolicitacaoMaterialItemFonte> buscarDotacaoSolicitacaoMaterialItemFontePorIdOrigem(Long idOrigem, ProcessoDeCompra processoCompra) {
        String sql = " " +
            "     select dsmif.* " +
            "     from dotsolmat dsm " +
            "       inner join dotacaosolmatitem dsmi on dsmi.dotacaosolicitacaomaterial_id = dsm.id " +
            "       inner join dotacaosolmatitemfonte dsmif on dsmif.dotacaosolmatitem_id = dsmi.id " +
            "       inner join solicitacaomaterial sol on sol.id = dsm.solicitacaomaterial_id " +
            "       inner join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
            "     where pc.id = :idProcesso " +
            "       and dsmif.idorigem = :idOrigem ";
        Query q = em.createNativeQuery(sql, DotacaoSolicitacaoMaterialItemFonte.class);
        q.setParameter("idProcesso", processoCompra.getId());
        q.setParameter("idOrigem", idOrigem);
        return q.getResultList();
    }

    public void removerDotacaoSolicitacaoCompraFonte(ProcessoDeCompra processoCompra, Long idOrigem) {
        if (processoCompra != null) {
            List<DotacaoSolicitacaoMaterialItemFonte> fontes = buscarDotacaoSolicitacaoMaterialItemFontePorIdOrigem(idOrigem, processoCompra);

            fontes.forEach(fonte -> {
                DotacaoSolicitacaoMaterialItem dotTipo = fonte.getDotacaoSolMatItem();
                dotTipo.setValor(dotTipo.getValor().subtract(fonte.getValor()));
                dotTipo.getFontes().remove(fonte);
                em.merge(dotTipo);
            });
        }
    }

    public void gerarReservaSolicitcaoCompraExecucaoContrato(ExecucaoContrato execucao) {
        ProcessoDeCompra processoCompra = execucao.getContrato().getObjetoAdequado().getProcessoDeCompra();
        if (processoCompra != null) {
            List<ExecucaoContratoTipoFonte> reservasExecucao = Lists.newArrayList();

            DotacaoSolicitacaoMaterial dotSolMat = recuperarDotacaoAPartirDoProcessoDeCompra(processoCompra);
            if (dotSolMat == null) {
                dotSolMat = criarDotacaoSolicitacaoCompra(processoCompra.getSolicitacaoMaterial());
                reservasExecucao.addAll(execucaoContratoFacade.buscarExecucaoContratoFontePorContrato(execucao.getContrato()));
            } else {
                execucao.getReservas().stream().flatMap(exTipo -> exTipo.getFontes().stream()).forEach(reservasExecucao::add);
            }

            Hibernate.initialize(dotSolMat.getItens());
            dotSolMat.getItens().forEach(dotItem -> {
                reservasExecucao.stream()
                    .filter(execFonte -> dotItem.getTipoObjetoCompra().equals(execFonte.getExecucaoContratoTipo().getTipoObjetoCompra()))
                    .filter(ExecucaoContratoTipoFonte::getGerarReserva)
                    .forEach(execFonte -> {
                        DotacaoSolicitacaoMaterialItemFonte novaDotFonte = new DotacaoSolicitacaoMaterialItemFonte();
                        novaDotFonte.setDotacaoSolMatItem(dotItem);
                        novaDotFonte.setIdOrigem(execucao.getId());
                        novaDotFonte.setTipoOperacao(TipoOperacaoORC.NORMAL);
                        novaDotFonte.setTipoReserva(TipoReservaSolicitacaoCompra.EXECUCAO_CONTRATO);
                        novaDotFonte.setFonteDespesaORC(execFonte.getFonteDespesaORC());
                        novaDotFonte.setExercicio(sistemaFacade.getExercicioCorrente());
                        novaDotFonte.setValor(execFonte.getValor());
                        novaDotFonte.setDataLancamento(execucao.getDataLancamento());
                        Util.adicionarObjetoEmLista(dotItem.getFontes(), novaDotFonte);

                        dotItem.setValor(dotItem.getValor().add(novaDotFonte.getValor()));
                    });
            });
            em.merge(dotSolMat);
        }
    }

    public void gerarReservaSolicitcaoCompraExecucaoProcesso(ExecucaoProcesso execucao) {
        List<ExecucaoProcessoFonte> reservasExecucao = Lists.newArrayList();

        DotacaoSolicitacaoMaterial dotSolMat = recuperarDotacaoAPartirDoProcessoDeCompra(execucao.getProcessoCompra());
        if (dotSolMat == null) {
            dotSolMat = criarDotacaoSolicitacaoCompra(execucao.getProcessoCompra().getSolicitacaoMaterial());
            reservasExecucao.addAll(execucaoProcessoFacade.buscarExecucaoFontePorProcessoCompra(execucao.getProcessoCompra()));
        } else {
            execucao.getReservas().stream().flatMap(exTipo -> exTipo.getFontes().stream()).forEach(reservasExecucao::add);
        }

        Hibernate.initialize(dotSolMat.getItens());
        dotSolMat.getItens().forEach(dotItem -> {
            reservasExecucao.stream()
                .filter(execFonte -> dotItem.getTipoObjetoCompra().equals(execFonte.getExecucaoProcessoReserva().getTipoObjetoCompra()))
                .filter(ExecucaoProcessoFonte::getGeraReserva)
                .forEach(execFonte -> {
                    DotacaoSolicitacaoMaterialItemFonte novaDotFonte = new DotacaoSolicitacaoMaterialItemFonte();
                    novaDotFonte.setDotacaoSolMatItem(dotItem);
                    novaDotFonte.setIdOrigem(execucao.getId());
                    novaDotFonte.setTipoOperacao(TipoOperacaoORC.NORMAL);
                    novaDotFonte.setTipoReserva(TipoReservaSolicitacaoCompra.EXECUCAO_PROCESSO);
                    novaDotFonte.setFonteDespesaORC(execFonte.getFonteDespesaORC());
                    novaDotFonte.setExercicio(sistemaFacade.getExercicioCorrente());
                    novaDotFonte.setValor(execFonte.getValor());
                    novaDotFonte.setDataLancamento(execucao.getDataLancamento());

                    Util.adicionarObjetoEmLista(dotItem.getFontes(), novaDotFonte);
                    dotItem.setValor(dotItem.getValor().add(novaDotFonte.getValor()));
                });
        });
        em.merge(dotSolMat);
    }

    public DotacaoSolicitacaoMaterial criarDotacaoSolicitacaoCompra(SolicitacaoMaterial solicitacaoCompra) {
        DotacaoSolicitacaoMaterial dotSolMat = new DotacaoSolicitacaoMaterial();
        dotSolMat.setRealizadaEm(solicitacaoCompra.getDataSolicitacao());
        dotSolMat.setCodigo(singletonGeradorCodigo.getProximoCodigo(DotacaoSolicitacaoMaterial.class, "codigo"));
        dotSolMat.setSolicitacaoMaterial(solicitacaoCompra);

        Map<TipoObjetoCompra, BigDecimal> mapTipoObjetoCompraValor = getMapTipoObjetoCompraValor(solicitacaoCompra);

        for (TipoObjetoCompra tipoObjCompra : mapTipoObjetoCompraValor.keySet()) {
            DotacaoSolicitacaoMaterialItem dotItem = new DotacaoSolicitacaoMaterialItem();
            dotItem.setDotacaoSolicitacaoMaterial(dotSolMat);
            dotItem.setTipoObjetoCompra(tipoObjCompra);
            dotItem.setValor(mapTipoObjetoCompraValor.get(tipoObjCompra));
            Util.adicionarObjetoEmLista(dotSolMat.getItens(), dotItem);
        }
        return dotSolMat;
    }

    public Map<TipoObjetoCompra, BigDecimal> getMapTipoObjetoCompraValor(SolicitacaoMaterial solicitacaoCompra) {
        List<ItemSolicitacao> itens = solicitacaoMaterialFacade.buscarItens(solicitacaoCompra);
        Map<TipoObjetoCompra, BigDecimal> mapTipoObjetoCompraValor = Maps.newHashMap();
        itens.forEach(itemSol -> {
            TipoObjetoCompra tipoObjetoCompra = itemSol.getItemCotacao().getObjetoCompra().getTipoObjetoCompra();
            mapTipoObjetoCompraValor.putIfAbsent(tipoObjetoCompra, BigDecimal.ZERO);
            mapTipoObjetoCompraValor.put(tipoObjetoCompra, mapTipoObjetoCompraValor.get(tipoObjetoCompra).add(itemSol.getPrecoTotal()));
        });
        return mapTipoObjetoCompraValor;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }

    public ReprocessamentoSaldoFonteDespesaOrcFacade getReprocessamentoSaldoFonteDespesaOrcFacade() {
        return reprocessamentoSaldoFonteDespesaOrcFacade;
    }

    public ConvenioDespesaFacade getConvenioDespesaFacade() {
        return convenioDespesaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public SolicitacaoMaterialFacade getSolicitacaoMaterialFacade() {
        return solicitacaoMaterialFacade;
    }

    public DotacaoProcessoCompraFacade getDotacaoProcessoCompraFacade() {
        return dotacaoProcessoCompraFacade;
    }
}
