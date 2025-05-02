package br.com.webpublico.negocios.contabil.execucao;


import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ExtratoMovimentoDespesaORC;
import br.com.webpublico.entidadesauxiliares.contabil.MovimentoDespesaORCVO;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaOcamentariaVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class ExtratoMovimentoDespesaOrcFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private ProvisaoPPADespesaFacade provisaoPPADespesaFacade;
    @EJB
    private AlteracaoORCFacade alteracaoORCFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public void recuparMovimento(ExtratoMovimentoDespesaORC selecionado) {
        try {
            selecionado.limparCampos();
            selecionado.getBarraProgressoItens().inicializa();
            selecionado.getBarraProgressoItens().addMensagemNegrito("Iniciando....");
            buscarSaldoMovimentoDespesaOrc(selecionado);
            buscarValoresFatosGerados(selecionado);
        } catch (Exception e) {
            selecionado.getBarraProgressoItens().addMensagemNegrito("Error: " + e.getMessage());
        } finally {
            selecionado.getBarraProgressoItens().finaliza();
            selecionado.getBarraProgressoItens().addMensagemNegrito("Finalizado....");
        }
    }

    public void buscarValoresFatosGerados(ExtratoMovimentoDespesaORC selecionado) {
        selecionado.getBarraProgressoItens().addMensagemNegrito("Recuperando valores dos fatos geradores....");
        selecionado.setDotacao(provisaoPPADespesaFacade.buscarValorPorFonteDespesaOrc(selecionado.getFonteDespesaORC()));
        selecionado.setAlteracao(alteracaoORCFacade.buscarValorPorFonteDespesaOrc(selecionado.getFonteDespesaORC(), selecionado.getDataInicial(), selecionado.getDataFinal()));
        selecionado.setReservado(solicitacaoEmpenhoFacade.buscarValorReservadorPorFonteDespesaOrc(selecionado.getFonteDespesaORC(), selecionado.getDataInicial(), selecionado.getDataFinal()));
        selecionado.setReservadoPorLicitacao(solicitacaoEmpenhoFacade.buscarValorReservadorPorLicitacaoPorFonteDespesaOrc(selecionado.getFonteDespesaORC(), selecionado.getDataInicial(), selecionado.getDataFinal()));
        selecionado.setEmpenhado(empenhoFacade.buscarValorPorFonteDespesaOrc(selecionado.getFonteDespesaORC(), selecionado.getDataInicial(), selecionado.getDataFinal()));
        selecionado.setLiquidado(liquidacaoFacade.buscarValorPorFonteDespesaOrc(selecionado.getFonteDespesaORC(), selecionado.getDataInicial(), selecionado.getDataFinal()));
        selecionado.setPago(pagamentoFacade.buscarValorPorFonteDespesaOrc(selecionado.getFonteDespesaORC(), selecionado.getDataInicial(), selecionado.getDataFinal()));
    }

    public void buscarUltimoMovimentoDespesaOrc(ExtratoMovimentoDespesaORC selecionado) {
        Date data = selecionado.getDataFinal();
        UnidadeOrganizacional subordinada = selecionado.getHierarquiaOrganizacional().getSubordinada();
        selecionado.getBarraProgressoItens().addMensagemNegrito("Recuperando informações do dia " + DataUtil.getDataFormatada(data) + "....");
        SaldoFonteDespesaOcamentariaVO saldo = recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(selecionado.getFonteDespesaORC(), data, subordinada, false);
        if (saldo != null) {
            selecionado.getSaldos().add(saldo);
        }
    }

    public void buscarSaldoMovimentoDespesaOrc(ExtratoMovimentoDespesaORC selecionado) {
        Date dataFinal = selecionado.getDataFinal();
        Date dataInicial = selecionado.getDataInicial();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataInicial);
        UnidadeOrganizacional subordinada = selecionado.getHierarquiaOrganizacional().getSubordinada();

        Long dias = DataUtil.diferencaDiasInteiraComTimezone(dataInicial, dataFinal);
        selecionado.getBarraProgressoItens().setTotal(dias.intValue());

        for (int i = 0; i <= dias; i++) {
            Date data = calendar.getTime();

            selecionado.getBarraProgressoItens().addMensagemNegrito("Recuperando informações do dia " + DataUtil.getDataFormatada(data) + "....");
            SaldoFonteDespesaOcamentariaVO saldo = recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(selecionado.getFonteDespesaORC(), data, subordinada, true);
            if (saldo != null) {
                selecionado.getSaldos().add(saldo);
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            selecionado.getBarraProgressoItens().setProcessados(selecionado.getBarraProgressoItens().getProcessados() + 1);
        }
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public SaldoFonteDespesaOcamentariaVO recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(FonteDespesaORC fonteDespesaOrc, Date data, UnidadeOrganizacional unidadeOrganizacional, Boolean igualData) {
        String sql = "select f.dotacao, f.reservado, f.reservadoPorLicitacao, f.alteracao, f.suplementado, f.reduzido, f.empenhado, f.liquidado, f.pago, f.dataSaldo "
            + " from SaldoFonteDespesaORC f"
            + " WHERE f.fonteDespesaORC_id = :fonte "
            + " AND f.unidadeOrganizacional_id = :unidade"
            + " AND trunc(f.dataSaldo) " + (igualData ? " = " : " <= ") + " to_date(:data,'dd/MM/yyyy')"
            + " ORDER BY trunc(f.dataSaldo) desc";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fonte", fonteDespesaOrc.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            Object[] obj = resultado.get(0);
            SaldoFonteDespesaOcamentariaVO saldoVo = new SaldoFonteDespesaOcamentariaVO();
            saldoVo.setDotacao((BigDecimal) obj[0]);
            saldoVo.setReservado((BigDecimal) obj[1]);
            saldoVo.setReservadoPorLicitacao((BigDecimal) obj[2]);
            saldoVo.setAlteracao((BigDecimal) obj[3]);
            saldoVo.setSuplementado((BigDecimal) obj[4]);
            saldoVo.setReduzido((BigDecimal) obj[5]);
            saldoVo.setEmpenhado((BigDecimal) obj[6]);
            saldoVo.setLiquidado((BigDecimal) obj[7]);
            saldoVo.setPago((BigDecimal) obj[8]);
            saldoVo.setDataSaldo((Date) obj[9]);
            saldoVo.setMovimentos(buscarMovimentos(fonteDespesaOrc, data, unidadeOrganizacional));
            return saldoVo;
        }
        return null;
    }

    public List<MovimentoDespesaORCVO> buscarMovimentos(FonteDespesaORC fonteDespesaORC, Date data, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " select f.valor, f.operacaoORC, f.tipoOperacaoORC, f.idOrigem, f.classeOrigem, f.numeroMovimento, f.historico "
            + " from MovimentoDespesaORC f "
            + " where f.fonteDespesaORC_id = :fonte"
            + " and f.unidadeOrganizacional_id = :unidade"
            + " and trunc(f.dataMovimento) = to_date(:data, 'dd/MM/yyyy')"
            + " ORDER BY trunc(f.dataMovimento), f.id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("fonte", fonteDespesaORC.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        List<Object[]> resultado = q.getResultList();
        List<MovimentoDespesaORCVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            resultado.forEach(obj -> {
                MovimentoDespesaORCVO mov = new MovimentoDespesaORCVO();
                mov.setValor((BigDecimal) obj[0]);
                mov.setOperacaoORC(obj[1] != null ? OperacaoORC.valueOf((String) obj[1]) : null);
                mov.setTipoOperacaoORC(obj[2] != null ? TipoOperacaoORC.valueOf((String) obj[2]).getDescricao() : "");
                mov.setIdOrigem((String) obj[3]);
                mov.setClasseOrigem((String) obj[4]);
                mov.setNumeroMovimento((String) obj[5]);
                mov.setHistorico((String) obj[6]);
                mov.setHref(montarHrefMovimento(mov));
                retorno.add(mov);
            });
        }
        return retorno;
    }

    public String montarHrefMovimento(MovimentoDespesaORCVO mov) {
        if (Empenho.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return "empenho/" + (isCategoriaOrcamentariaResto(Long.parseLong(mov.getIdOrigem()), mov.getClasseOrigem()) ? "resto-a-pagar/" : "") + "ver/" + mov.getIdOrigem() + "/";
        }
        if (EmpenhoEstorno.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return (isCategoriaOrcamentariaResto(Long.parseLong(mov.getIdOrigem()), mov.getClasseOrigem()) ? "cancelamento-empenho-restos-a-pagar/" : "estorno-empenho/") + "ver/" + mov.getIdOrigem() + "/";
        }
        if (Liquidacao.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return "liquidacao/" + (isCategoriaOrcamentariaResto(Long.parseLong(mov.getIdOrigem()), mov.getClasseOrigem()) ? "resto-a-pagar/" : "") + "ver/" + mov.getIdOrigem() + "/";
        }
        if (LiquidacaoEstorno.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return "liquidacao-estorno/" + (isCategoriaOrcamentariaResto(Long.parseLong(mov.getIdOrigem()), mov.getClasseOrigem()) ? "resto-a-pagar/" : "") + "ver/" + mov.getIdOrigem() + "/";
        }
        if (Pagamento.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return "pagamento/" + (isCategoriaOrcamentariaResto(Long.parseLong(mov.getIdOrigem()), mov.getClasseOrigem()) ? "resto-a-pagar/" : "") + "ver/" + mov.getIdOrigem() + "/";
        }
        if (PagamentoEstorno.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return "pagamento-estorno/" + (isCategoriaOrcamentariaResto(Long.parseLong(mov.getIdOrigem()), mov.getClasseOrigem()) ? "resto-a-pagar/" : "") + "ver/" + mov.getIdOrigem() + "/";
        }
        if (SolicitacaoEmpenho.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return "solicitacao-empenho/ver/" + mov.getIdOrigem() + "/";
        }
        if (PropostaConcessaoDiaria.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return (isSuprimentoFundo(Long.parseLong(mov.getIdOrigem())) ? "suprimento-fundos/ver/" : "diaria/ver/") + mov.getIdOrigem() + "/";
        }
        if (Contrato.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return "contrato-adm/ver/" + mov.getIdOrigem() + "/";
        }
        if (CancelamentoReservaDotacao.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return "cancelamento-reserva-dotacao/ver/" + mov.getIdOrigem() + "/";
        }
        if (ExecucaoContratoTipoFonte.class.getSimpleName().equals(mov.getClasseOrigem())) {
            Long idExecucao = buscarIdExecucaoContratoPorIdExecucaoFonte(Long.parseLong(mov.getIdOrigem()));
            if (idExecucao != null) {
                return "execucao-contrato-adm/ver/" + idExecucao + "/";
            }
        }
        if (ExecucaoContratoEmpenhoEstorno.class.getSimpleName().equals(mov.getClasseOrigem())) {
            Long idExecucao = buscarIdExecucaoContratoEstorno(Long.parseLong(mov.getIdOrigem()));
            if (idExecucao != null) {
                return "execucao-contrato-estorno/ver/" + idExecucao + "/";
            }
        }
        if (ExecucaoProcessoFonte.class.getSimpleName().equals(mov.getClasseOrigem())) {
            Long idExecucao = buscarIdExecucaoProcessoPorIdExecucaoFonte(Long.parseLong(mov.getIdOrigem()));
            if (idExecucao != null) {
                return "execucao-sem-contrato/ver/" + idExecucao + "/";
            }
        }
        if (ExecucaoProcessoEmpenhoEstorno.class.getSimpleName().equals(mov.getClasseOrigem())) {
            Long idExecucao = buscarIdExecucaoProcessoEstorno(Long.parseLong(mov.getIdOrigem()));
            if (idExecucao != null) {
                return "execucao-sem-contrato-estorno/ver/" + idExecucao + "/";
            }
        }
        if (ReconhecimentoDivida.class.getSimpleName().equals(mov.getClasseOrigem())) {
            return "reconhecimento-divida/ver/" + mov.getIdOrigem() + "/";
        }
        if (ProvisaoPPAFonte.class.getSimpleName().equals(mov.getClasseOrigem())) {
            Long idSubAcaoPPA = buscarIdSubAcaoPPAPorIdPorvisaoPPAFonte(Long.parseLong(mov.getIdOrigem()));
            if (idSubAcaoPPA != null) {
                return "provisao-ppa-despesa/passo-a-passo/ver/" + idSubAcaoPPA + "/";
            }
        }
        if (AnulacaoORC.class.getSimpleName().equals(mov.getClasseOrigem()) || SuplementacaoORC.class.getSimpleName().equals(mov.getClasseOrigem())) {
            Long idAlteracaoOrc = buscarIdAlteracaoOrc(Long.parseLong(mov.getIdOrigem()));
            if (idAlteracaoOrc != null) {
                return "alteracao-orcamentaria/ver/" + idAlteracaoOrc + "/";
            }
        }
        if (ReservaFonteDespesaOrc.class.getSimpleName().equals(mov.getClasseOrigem())) {
            Long idReservaDotacao = buscarIdReservaDotacaoPorIdReservaFonte(Long.parseLong(mov.getIdOrigem()));
            if (idReservaDotacao != null) {
                return "reserva-dotacao/ver/" + idReservaDotacao + "/";
            }
        }
        if (DotacaoSolicitacaoMaterialItemFonte.class.getSimpleName().equals(mov.getClasseOrigem())) {
            Long idDotacaoSolicitacaoMaterial = buscarIdDotacaoSolicitacaoMaterialPorDotSolMatFonte(Long.parseLong(mov.getIdOrigem()));
            if (idDotacaoSolicitacaoMaterial != null) {
                return "dotacao-de-solicitacao-de-compra/ver/" + idDotacaoSolicitacaoMaterial + "/";
            }
        }
        if (MovimentoAlteracaoContratual.class.getSimpleName().equals(mov.getClasseOrigem())) {
            boolean isAditivo = isAditivo(Long.parseLong(mov.getIdOrigem()));
            String url = isAditivo ? "aditivo-contrato/" : "apostilamento-contrato/";

            Long idAlteracaoContratual = buscarIdAlteracaoContratual(Long.parseLong(mov.getIdOrigem()));
            return url + "ver/" + idAlteracaoContratual + "/";
        }
        return null;
    }

    public boolean isAditivo(Long idEntidade) {
        if (idEntidade != null) {
            String sql = " select ac.* from alteracaocontratual ac " +
                "           inner join movimentoalteracaocont mov on mov.alteracaocontratual_id = ac.id " +
                "           where mov.id = :idEntidade " +
                "           and ac.tipoalteracaocontratual = :tipoAditivo ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idEntidade", idEntidade);
            q.setParameter("tipoAditivo", TipoAlteracaoContratual.ADITIVO.name());
            return !q.getResultList().isEmpty();
        }
        return false;
    }

    public boolean isCategoriaOrcamentariaResto(Long idEntidade, String nomeEntidade) {
        if (idEntidade != null && nomeEntidade != null) {
            String sql = "select * from " + nomeEntidade.toUpperCase().trim() + " where id = :idEntidade and categoriaOrcamentaria = :resto ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idEntidade", idEntidade);
            q.setParameter("resto", CategoriaOrcamentaria.RESTO.name());
            return !q.getResultList().isEmpty();
        }
        return false;
    }

    public Long buscarIdAlteracaoOrc(Long idAnulacaoOrSuplementacao) {
        String sql = " select alt.id from alteracaoorc alt " +
            " left join AnulacaoORC anu on anu.alteracaoorc_id = alt.id " +
            " left join SuplementacaoORC sup on sup.alteracaoorc_id = alt.id " +
            " where sup.id = :idAnulacaoOrSuplementacao or anu.ID = :idAnulacaoOrSuplementacao " +
            " order by alt.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAnulacaoOrSuplementacao", idAnulacaoOrSuplementacao);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0).longValue();
        }
        return null;
    }

    public Long buscarIdSubAcaoPPAPorIdPorvisaoPPAFonte(Long idProvFonte) {
        String sql = " select prov.subAcaoPPA_id " +
            " from ProvisaoPPAFonte provFonte " +
            "   inner join provisaoppadespesa prov on prov.ID = provFonte.PROVISAOPPADESPESA_ID " +
            " where provFonte.id = :idProvFonte" +
            " order by prov.subAcaoPPA_id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProvFonte", idProvFonte);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0).longValue();
        }
        return null;
    }

    public boolean isSuprimentoFundo(Long idProposta) {
        String sql = " select * from PROPOSTACONCESSAODIARIA where id = :idProposta and tipoProposta = :suprimentoFundo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProposta", idProposta);
        q.setParameter("suprimentoFundo", TipoProposta.SUPRIMENTO_FUNDO.name());
        List<Object[]> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }

    public Long buscarIdExecucaoContratoEstorno(Long idExecucaoEstorno) {
        String sql = " " +
            " select exest.id from execucaocontratoempenhoest exest " +
            " where exest.execucaocontratoestorno_id = :idExecucaoEstorno " +
            " order by exest.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucaoEstorno", idExecucaoEstorno);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0).longValue();
        }
        return null;
    }

    public Long buscarIdExecucaoProcessoEstorno(Long idExecucaoEstorno) {
        String sql = " " +
            " select exest.id from execucaoprocessoempenhoest exest " +
            " where exest.execucaoprocessoestorno_id = :idExecucaoEstorno " +
            " order by exest.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucaoEstorno", idExecucaoEstorno);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0).longValue();
        }
        return null;
    }

    public Long buscarIdExecucaoContratoPorIdExecucaoFonte(Long idExecucaoFonte) {
        String sql = " " +
            " select ex.id from execucaocontrato ex " +
            "   inner join execucaocontratotipo exres on exres.execucaocontrato_id = ex.id " +
            "   inner join execucaocontratotipofonte exfonte on exfonte.execucaocontratotipo_id = exres.id " +
            " where exfonte.id = :idExecucaoFonte " +
            " order by ex.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucaoFonte", idExecucaoFonte);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0).longValue();
        }
        return null;
    }

    public Long buscarIdExecucaoProcessoPorIdExecucaoFonte(Long idExecucaoFonte) {
        String sql = " " +
            " select ex.id from execucaoprocesso ex " +
            "   inner join execucaoprocessoreserva exres on exres.execucaoprocesso_id = ex.id " +
            "   inner join execucaoprocessofonte exfonte on exfonte.execucaoprocessoreserva_id = exres.id " +
            " where exfonte.id = :idExecucaoFonte " +
            " order by ex.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExecucaoFonte", idExecucaoFonte);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0).longValue();
        }
        return null;
    }

    public Long buscarIdReservaDotacaoPorIdReservaFonte(Long idReservaFonte) {
        String sql = " select RESERVADOTACAO_ID from ReservaDotacaoReservaFonte where RESERVAFONTEDESPESAORC_ID = :idReservaFonte order by RESERVADOTACAO_ID desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idReservaFonte", idReservaFonte);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0).longValue();
        }
        return null;
    }

    public Long buscarIdDotacaoSolicitacaoMaterialPorDotSolMatFonte(Long idDotSolMatFonte) {
        String sql = " select item.DOTACAOSOLICITACAOMATERIAL_ID " +
            " from DOTACAOSOLMATITEMFONTE fonte " +
            "    inner join DOTACAOSOLMATITEM item on item.ID = fonte.DOTACAOSOLMATITEM_ID " +
            "    inner join DOTSOLMAT dotacao on dotacao.ID = item.DOTACAOSOLICITACAOMATERIAL_ID " +
            " where fonte.ID = :idDotSolMatFonte order by item.DOTACAOSOLICITACAOMATERIAL_ID desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDotSolMatFonte", idDotSolMatFonte);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0).longValue();
        }
        return null;
    }

    public Long buscarIdAlteracaoContratual(Long idMovimentoAc) {
        String sql = " select mov.alteracaocontratual_id from movimentoalteracaocont mov where mov.id = :idEntidade ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEntidade", idMovimentoAc);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0).longValue();
        }
        return null;
    }
}
