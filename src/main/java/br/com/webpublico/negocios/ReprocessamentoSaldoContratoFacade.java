package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.administrativo.dao.JdbcReprocessamentoContrato;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ReprocessamentoSaldoContratoFacade {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;
    @EJB
    private SolicitacaoEmpenhoEstornoFacade solicitacaoEmpenhoEstornoFacade;
    @EJB
    private SaldoItemContratoFacade saldoItemContratoFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    private JdbcReprocessamentoContrato jdbcReprocessamentoContrato;

    @PostConstruct
    public void init() {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        jdbcReprocessamentoContrato = (JdbcReprocessamentoContrato) ap.getBean("jdbcReprocessamentoContrato");
    }

    public void reprocessarUnicoContrato(Contrato contrato) {
        AssistenteReprocessamentoContrato assistente = new AssistenteReprocessamentoContrato();
        assistente.setAssistenteBarraProgresso(new AssistenteBarraProgresso());
        assistente.setContrato(contrato);
        reprocessarContratos(assistente, contrato.getDataLancamento());
    }

    public void reprocessarUnicoContratoAlteracaoContratual(Contrato contrato, AlteracaoContratual alteracaoContratual) {
        AssistenteReprocessamentoContrato assistente = new AssistenteReprocessamentoContrato();
        assistente.setAssistenteBarraProgresso(new AssistenteBarraProgresso());
        assistente.setContrato(contrato);
        assistente.setAlteracaoContratual(alteracaoContratual);
        reprocessarContratos(assistente, alteracaoContratual.getDataLancamento());
    }

    @Asynchronous
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteReprocessamentoContrato> reprocessar(AssistenteReprocessamentoContrato assistente) {
        reprocessarContratos(assistente, assistente.getDataOperacao());
        assistente.getAssistenteBarraProgresso().setDescricaoProcesso("Finalizando Processo...");
        assistente.getAssistenteBarraProgresso().zerarContadoresProcesso();
        return new AsyncResult<>(assistente);
    }

    public void reprocessarContratos(AssistenteReprocessamentoContrato assistente, Date dataOperacao) {
        assistente.getAssistenteBarraProgresso().zerarContadoresProcesso();
        assistente.getAssistenteBarraProgresso().setDescricaoProcesso("Recuperando Contrato...");
        List<Contrato> contratos = buscarContratos(assistente);
        assistente.getAssistenteBarraProgresso().setTotal(contratos.size());

        gerarSaldoAndMovimentoItemContrato(assistente.getAssistenteBarraProgresso(), dataOperacao, contratos);
    }

    public void gerarSaldoAndMovimentoItemContrato(AssistenteBarraProgresso assistente, Date dataOperacao, List<Contrato> contratos) {
        for (Contrato contrato : contratos) {
            jdbcReprocessamentoContrato.deleteSaldoAndMovimento(contrato);
            reiniciarValoresContrato(contrato);

            List<ItemContrato> itens = contratoFacade.buscarItensContrato(contrato);
            gerarMovimentosInclusaoContrato(itens);

            List<ReprocessamentoContrato> movimentos = buscarMovimentosContrato(contrato);
            for (ReprocessamentoContrato movimento : movimentos) {
                switch (movimento.getTipoMovimentoContrato()) {
                    case EXECUCAO:
                        gerarMovimentoExecucaoContrato(contrato, movimento, dataOperacao);
                        break;
                    case ESTORNO_EXECUCAO:
                        gerarMovimentoEstornoExecucaoContrato(contrato, movimento, dataOperacao);
                        break;
                    case ADITIVO:
                    case APOSTILAMENTO:
                        alteracaoContratualFacade.criarMovimentoItemContrato(movimento.getMovimentosAlteracaoContratual(), dataOperacao);
                        alteracaoContratualFacade.movimentarOperacaoValorAndPrazoContrato(contrato, movimento.getMovimentosAlteracaoContratual());
                        break;
                }
            }
            contrato.setReprocessado(true);
            jdbcReprocessamentoContrato.updateContrato(contrato);
            assistente.conta();
        }
    }

    private List<Contrato> buscarContratos(AssistenteReprocessamentoContrato assistente) {
        if (assistente.getContrato() == null) {
            return contratoFacade.buscarFiltrandoPorSituacao("", SituacaoContrato.APROVADO, SituacaoContrato.DEFERIDO);
        }
        return Lists.newArrayList(assistente.getContrato());
    }

    private void gerarMovimentoExecucaoContrato(Contrato contrato, ReprocessamentoContrato execucao, Date dataOperacao) {
        contrato.setSaldoAtualContrato(contrato.getSaldoAtualContrato().subtract(execucao.getValor()));

        List<ExecucaoContratoItem> itensExecucao = execucaoContratoFacade.buscarItensExecucao(execucao.getIdMovimento());
        for (ExecucaoContratoItem itemExecucao : itensExecucao) {
            saldoItemContratoFacade.gerarSaldoExecucao(itemExecucao, dataOperacao);
        }
    }

    private void gerarMovimentoEstornoExecucaoContrato(Contrato contrato, ReprocessamentoContrato estornoExecucao, Date dataOperacao) {
        List<ExecucaoContratoEmpenhoEstornoItem> itensEstorno = solicitacaoEmpenhoEstornoFacade.buscarExecucaoContratoEstornoItem(estornoExecucao.getIdMovimento());
        if (itensEstorno != null && !itensEstorno.isEmpty()) {
            for (ExecucaoContratoEmpenhoEstornoItem itemEstorno : itensEstorno) {
                saldoItemContratoFacade.gerarSaldoExecucaoEstorno(itemEstorno, dataOperacao);

                SolicitacaoEmpenhoEstorno solEmpEst = itemEstorno.getExecucaoContratoEmpenhoEst().getSolicitacaoEmpenhoEstorno();
                if (solEmpEst.hasSolicitacaoEstornoEmpenhoEstornada() || solEmpEst.isSolicitacaoEstornoPorSolicitacaoEmpenho()) {
                    contrato.setSaldoAtualContrato(contrato.getSaldoAtualContrato().add(itemEstorno.getValorTotal()));
                }
            }
        }
    }

    private void gerarMovimentosInclusaoContrato(List<ItemContrato> itens) {
        for (ItemContrato itemContrato : itens) {
            saldoItemContratoFacade.gerarSaldoInicialContrato(itemContrato);
        }
    }

    private void reiniciarValoresContrato(Contrato contrato) {
        contrato.setSaldoAtualContrato(contrato.getValorTotal());
        contrato.setValorAtualContrato(contrato.getValorTotal());
        contrato.setVariacaoAtualContrato(BigDecimal.ZERO);
        contrato.setTerminoVigenciaAtual(contrato.getTerminoVigencia());
        contrato.setTerminoExecucaoAtual(contrato.getTerminoExecucao());
    }

    public List<ReprocessamentoContrato> buscarMovimentosContrato(Contrato contrato) {
        String sql = " " +
            " select  " +
            " dataMovimento  as dataMovimento, " +
            " origem         as origem, " +
            " idMovimento    as idMovimento," +
            " valor          as valor " +
            " from( " +
            "   select  " +
            "       ex.datalancamento as dataMovimento, " +
            "       'EXECUCAO'        as origem, " +
            "       ex.id             as idMovimento," +
            "       ex.valor          as valor    " +
            "   from execucaocontrato ex " +
            "       inner join contrato c on c.id = ex.contrato_id " +
            "  where c.id = :idContrato " +
            " union all " +
            "  select est.datalancamento as dataMovimento, " +
            "        'ESTORNO_EXECUCAO' as origem, " +
            "         est.id as idMovimento, " +
            "         0 as valor " +
            "      from execucaocontratoestorno est " +
            "               inner join execucaocontrato ex on ex.id = est.execucaocontrato_id " +
            "               inner join contrato c on c.id = ex.contrato_id " +
            "      where c.id = :idContrato " +
            " union all" +
            "   select  " +
            "       ac.datalancamento as dataMovimento, " +
            "       'ADITIVO'         as origem, " +
            "       ac.id             as idMovimento, " +
            "       0                 as valor    " +
            "   from alteracaocontratual ac " +
            "     inner join alteracaocontratualcont acc on acc.alteracaocontratual_id = ac.id   " +
            "     inner join contrato c on c.id = acc.contrato_id " +
            "  where c.id = :idContrato " +
            "   and ac.situacao <> :em_elaboracao " +
            "   and ac.tipoalteracaocontratual = :aditivo " +
            " union all " +
            "   select  " +
            "       ac.datalancamento as dataMovimento, " +
            "       'APOSTILAMENTO'   as origem, " +
            "       ac.id             as idMovimento, " +
            "       0                 as valor    " +
            "   from alteracaocontratual ac " +
            "     inner join alteracaocontratualcont acc on acc.alteracaocontratual_id = ac.id   " +
            "     inner join contrato c on c.id = acc.contrato_id " +
            "  where c.id = :idContrato " +
            "   and ac.situacao <> :em_elaboracao " +
            "   and ac.tipoalteracaocontratual = :apostilamento " +
            " ) " +
            " order by dataMovimento, idMovimento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("em_elaboracao", SituacaoContrato.EM_ELABORACAO.name());
        q.setParameter("aditivo", TipoAlteracaoContratual.ADITIVO.name());
        q.setParameter("apostilamento", TipoAlteracaoContratual.APOSTILAMENTO.name());
        List<ReprocessamentoContrato> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            ReprocessamentoContrato mov = new ReprocessamentoContrato();
            mov.setDataMovimento((Date) obj[0]);
            mov.setTipoMovimentoContrato(ReprocessamentoContrato.TipoMovimentoContrato.valueOf((String) obj[1]));
            mov.setIdMovimento(((BigDecimal) obj[2]).longValue());
            mov.setValor((BigDecimal) obj[3]);
            if (mov.getTipoMovimentoContrato().isVariacaoContratual()) {
                if (mov.getIdMovimento() != null) {
                    mov.setMovimentosAlteracaoContratual(alteracaoContratualFacade.buscarMovimentosAndItens(mov.getIdMovimento()));
                }
            }
            toReturn.add(mov);
        }
        return toReturn;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public AssistenteReprocessamentoContrato buscarExecucoesContratoCorrecaoOrigem(AssistenteReprocessamentoContrato selecionado) {
        AssistenteBarraProgresso assistente = selecionado.getAssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Buscando execuções contrato e suas origens...");
        assistente.zerarContadoresProcesso();

        String sql = " select distinct c.* from contrato c " +
            "           inner join execucaocontrato ex on ex.contrato_id = c.id " +
            "           where exists (select 1 from execucaocontrato ec " +
            "                         where ec.contrato_id = c.id " +
            "                              and ec.reprocessada = :reprocessada) ";
        sql += selecionado.getContrato() != null ? " and c.id = :idContrato " : "";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("reprocessada", selecionado.getExecucaoReprocessada());
        if (selecionado.getContrato() != null) {
            q.setParameter("idContrato", selecionado.getContrato().getId());
        }
        q.setMaxResults(selecionado.getQuantidadeConsulta());
        List<Contrato> resultado = q.getResultList();
        assistente.setTotal(resultado.size());
        List<ContratoCorrecaoVO> retorno = Lists.newArrayList();

        resultado.forEach(cont -> {
            ContratoCorrecaoVO contVO = new ContratoCorrecaoVO();
            contVO.setContrato(cont);
            contVO.setExecucoesCorrecao(buscarExecucoesContrato(cont, selecionado.getExecucaoReprocessada()));
            contVO.setOrigensExecucao(buscarExecucoesContratoOrigens(cont));
            retorno.add(contVO);
            assistente.conta();
        });
        selecionado.setContratosCorrecoes(Lists.newArrayList(retorno));

        classificarOrigemExecucaoCorreta(selecionado);
        atribuirExecucaoReprocessadaParaContratosSemAlteracaoContratual(selecionado);
        return selecionado;
    }

    private void atribuirExecucaoReprocessadaParaContratosSemAlteracaoContratual(AssistenteReprocessamentoContrato selecionado) {
        selecionado.getContratosCorrecoes()
            .stream()
            .filter(contVO -> contVO.getExecucoesCorrecao().size() == 1 && contVO.getOrigensExecucao().size() == 1)
            .forEach(contVO -> {
                List<AlteracaoContratual> alteracoesCont = alteracaoContratualFacade.buscarAlteracoesContratuaisPorContrato(contVO.getContrato());

                if (Util.isListNullOrEmpty(alteracoesCont)) {
                    ExecucaoContrato execucao = contVO.getExecucoesCorrecao().get(0).getExecucaoContrato();
                    execucao.setReprocessada(true);
                    jdbcReprocessamentoContrato.updateExecucaoContrato(execucao);
                }
            });
    }

    public void separarOrigemExecucaoCorreta(ContratoCorrecaoVO contVO) {
        contVO.getExecucoesCorrecao().stream().filter(ExecucaoContratoCorrecaoVO::getOrigemCorreta).forEach(execVO -> contVO.getExecucoesCorrigidas().add(execVO));
        contVO.getExecucoesCorrecao().removeIf(ExecucaoContratoCorrecaoVO::getOrigemCorreta);
        contVO.getOrigensExecucao().removeIf(ExecucaoContratoCorrecaoOrigemVO::getOrigemCorreta);
    }

    public ContratoCorrecaoVO marcarExecucaoComoReprocessada(ContratoCorrecaoVO contVO) {
        for (ExecucaoContratoCorrecaoVO execVO : contVO.getExecucoesCorrecao()) {
            ExecucaoContrato execucao = execVO.getExecucaoContrato();
            execucao.setReprocessada(true);
            jdbcReprocessamentoContrato.updateExecucaoContrato(execucao);
        }

        for (ExecucaoContratoCorrecaoVO execVO : contVO.getExecucoesCorrigidas()) {
            ExecucaoContrato execucao = execVO.getExecucaoContrato();
            execucao.setReprocessada(true);
            jdbcReprocessamentoContrato.updateExecucaoContrato(execucao);
        }
        return contVO;
    }


    public List<ExecucaoContratoCorrecaoVO> buscarExecucoesContrato(Contrato contrato, Boolean reprocesssada) {
        String sql = " select ex.* from execucaocontrato ex " +
            "           where ex.reprocessada = :reprocessada ";
        sql += contrato != null ? " and ex.contrato_id = :idContrato " : "";
        sql += "       order by ex.numero ";
        Query q = em.createNativeQuery(sql, ExecucaoContrato.class);
        q.setParameter("reprocessada", reprocesssada);
        if (contrato != null) {
            q.setParameter("idContrato", contrato.getId());
        }
        List<ExecucaoContrato> resultado = q.getResultList();
        List<ExecucaoContratoCorrecaoVO> retorno = Lists.newArrayList();

        resultado.forEach(exec -> {
            ExecucaoContratoCorrecaoVO execVO = new ExecucaoContratoCorrecaoVO();
            execVO.setExecucaoContrato(exec);
            execVO.setValorEstornado(execucaoContratoFacade.getValorEstornado(exec));
            retorno.add(execVO);
        });
        return retorno;
    }

    public List<ExecucaoContratoCorrecaoOrigemVO> buscarExecucoesContratoOrigens(Contrato contrato) {
        String sql = " " +
            " select * from (" +
            "  select distinct" +
            "            ac.id                                             as id, " +
            "            ac.datalancamento                                 as data, " +
            "            ac.tipoalteracaocontratual                        as tipo, " +
            "            ac.numero  || ' - ' || ac.numerotermo || '/' || ex.ano as descricao, " +
            "                coalesce((select sum(mov.valor) from movimentoalteracaocont mov " +
            "                where mov.alteracaocontratual_id = ac.id),0) as valor, " +
            "                movac.operacao                                as operacao," +
            "                ac.numero " +
            "           from alteracaocontratual ac " +
            "             inner join exercicio ex on ex.id = ac.exercicio_id " +
            "             inner join movimentoalteracaocont movac on movac.alteracaocontratual_id = ac.id " +
            "          where ac.contrato_id = :idContrato " +
            "                   and ac.situacao <> :emElaboracao" +
            "               and movac.operacao in (:operacoes))" +
            "  order by numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("emElaboracao", SituacaoContrato.EM_ELABORACAO.name());
        q.setParameter("operacoes", Util.getListOfEnumName(OperacaoMovimentoAlteracaoContratual.getOperacoesValor()));
        List<Object[]> resultado = q.getResultList();

        List<ExecucaoContratoCorrecaoOrigemVO> retorno = Lists.newArrayList();

        ExecucaoContratoCorrecaoOrigemVO origemContratoVO = new ExecucaoContratoCorrecaoOrigemVO();
        origemContratoVO.setIdOrigem(contrato.getId());
        origemContratoVO.setDataLancamento(contrato.getDataLancamento());
        origemContratoVO.setOrigem(OrigemSaldoItemContrato.CONTRATO);
        origemContratoVO.setDescricaoOrigem(contrato.getNumeroAnoTermo());
        origemContratoVO.setValor(contrato.getValorTotal());
        origemContratoVO.setOperacaoOrigem(OperacaoSaldoItemContrato.PRE_EXECUCAO);
        retorno.add(origemContratoVO);

        resultado.forEach(alt -> {
            ExecucaoContratoCorrecaoOrigemVO origemVO = new ExecucaoContratoCorrecaoOrigemVO();
            origemVO.setIdOrigem(((BigDecimal) alt[0]).longValue());
            origemVO.setDataLancamento((Date) alt[1]);
            origemVO.setOrigem(OrigemSaldoItemContrato.valueOf((String) alt[2]));
            origemVO.setDescricaoOrigem((String) alt[3]);
            origemVO.setValor((BigDecimal) alt[4]);
            boolean isOperacaoPre = OperacaoMovimentoAlteracaoContratual.getOperacoesPreExecucao().contains(OperacaoMovimentoAlteracaoContratual.valueOf((String) alt[5]));
            origemVO.setOperacaoOrigem(isOperacaoPre ? OperacaoSaldoItemContrato.PRE_EXECUCAO : OperacaoSaldoItemContrato.POS_EXECUCAO);
            retorno.add(origemVO);
        });
        return retorno;
    }

    public void classificarOrigemExecucaoCorreta(AssistenteReprocessamentoContrato assistente) {
        assistente.getContratosCorrecoes().forEach(contVO -> {
            contVO.getExecucoesCorrecao().forEach(execVO -> {
                ExecucaoContrato execucao = execVO.getExecucaoContrato();
                contVO.getOrigensExecucao().stream()
                    .filter(origemVO -> execucao.getIdOrigem().equals(origemVO.getIdOrigem()) && execucao.getValor().equals(origemVO.getValor()))
                    .forEach(origemVO -> {
                        origemVO.setOrigemCorreta(true);
                        execVO.setOrigemCorreta(true);
                        execVO.setOrigemRelacionadaVO(origemVO);
                    });
            });
        });

        assistente.getContratosCorrecoes().stream()
            .filter(contVO -> !Util.isListNullOrEmpty(contVO.getOrigensExecucao()) && contVO.getOrigensExecucao().size() == 1)
            .forEach(contVO -> {

                contVO.getOrigensExecucao().stream()
                    .filter(origemVO -> origemVO.getValor().compareTo(contVO.getValorTotalExecucoes(contVO)) == 0)
                    .forEach(origemVO -> {
                        origemVO.setOrigemCorreta(true);

                        contVO.getExecucoesCorrecao().forEach(execVO -> {
                            execVO.setOrigemCorreta(true);
                            execVO.setOrigemRelacionadaVO(origemVO);
                        });
                    });
            });
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }
}
