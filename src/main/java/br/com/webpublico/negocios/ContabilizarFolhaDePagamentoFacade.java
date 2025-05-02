/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.EmpenhoFP;
import br.com.webpublico.entidadesauxiliares.RetencaoPgtoFP;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoDocPagto;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author everton
 */
@Stateless
public class ContabilizarFolhaDePagamentoFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FolhaDePagamentoFacade folhaDePagamentoFacade;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;
    public static final String HISTORICO_CONTABIL_EMPENHO = "736";
    public static final String HISTORICO_CONTABIL_LIQUIDACAO = "736";
    public static final String HISTORICO_CONTABIL_PAGAMENTO = "736";
    public static final String HISTORICO_CONTABIL_RETENCAO = "736";

    public List<EmpenhoFP> listaContabilizacaoFolhaDePagamento(FolhaDePagamento folhaDePagamento, Date dataContabilizacao) {
        StringBuilder sql = new StringBuilder();

        sql.append("select new br.com.webpublico.entidadesauxiliares.EmpenhoFP(folha, recurso, despesaOrc, provPPADesp.unidadeOrganizacional, fonteRecurso.fonteDespesaORC, fonteRecurso.contaFinanceira, fonteEvento.credor, fonteEvento.classeCredor, fonteEvento.desdobramento,");
        sql.append("                                                                   sum(case when(fonteEvento.operacaoFormula = '").append(OperacaoFormula.ADICAO.name()).append("') then itemFicha.valor else -itemFicha.valor end))");
        sql.append("  from RecursoDoVinculoFP recursoVinc, ItemFichaFinanceiraFP itemFicha");
        sql.append(" inner join recursoVinc.recursoFP recurso");
        sql.append(" inner join recurso.despesaORC despesaOrc");
        sql.append(" inner join recurso.fontesRecursoFPs fonteRecurso");
        sql.append(" inner join despesaOrc.provisaoPPADespesa provPPADesp");
        sql.append(" inner join despesaOrc.exercicio exercicio");
        sql.append(" inner join fonteRecurso.fonteEventoFPs fonteEvento");
        sql.append(" inner join itemFicha.fichaFinanceiraFP ficha");
        sql.append(" inner join ficha.folhaDePagamento folha");
        sql.append(" inner join folha.competenciaFP competencia");
        sql.append(" where recursoVinc.vinculoFP = ficha.vinculoFP");
        sql.append("   and fonteEvento.eventoFP = itemFicha.eventoFP");
        sql.append("   and exercicio = competencia.exercicio");
        sql.append("   and folha = :folha");
        sql.append("   and recurso.inicioVigencia <= :data and coalesce(recurso.finalVigencia, :data) >= :data");
        sql.append("   and recursoVinc.inicioVigencia <= :data and coalesce(recursoVinc.finalVigencia, :data) >= :data");
        sql.append("   and fonteEvento.contaExtraorcamentaria is null");
        sql.append(" group by folha, recurso, despesaOrc, provPPADesp.unidadeOrganizacional, fonteRecurso.fonteDespesaORC, fonteRecurso.contaFinanceira, fonteEvento.credor, fonteEvento.classeCredor, fonteEvento.desdobramento");

        Query q = em.createQuery(sql.toString());
        q.setParameter("folha", folhaDePagamento);
        q.setParameter("data", dataContabilizacao);
        return q.getResultList();
    }

    public List<RetencaoPgtoFP> listaRetencoesEmpenho(EmpenhoFP empenhoFP) {
        StringBuilder sql = new StringBuilder();

        sql.append("select new br.com.webpublico.entidadesauxiliares.RetencaoPgtoFP(fonteEvento.contaExtraorcamentaria, fonteEvento.subConta,");
        sql.append("                                                                        sum(case when(fonteEvento.operacaoFormula = '").append(OperacaoFormula.ADICAO.name()).append("') then itemFicha.valor else -itemFicha.valor end))");
        sql.append("  from RecursoDoVinculoFP recursoVinc, ItemFichaFinanceiraFP itemFicha");
        sql.append(" inner join recursoVinc.recursoFP recurso");
        sql.append(" inner join recurso.despesaORC despesaOrc");
        sql.append(" inner join recurso.fontesRecursoFPs fonteRecurso");
        sql.append(" inner join despesaOrc.provisaoPPADespesa provPPADesp");
        sql.append(" inner join despesaOrc.exercicio exercicio");
        sql.append(" inner join fonteRecurso.fonteEventoFPs fonteEvento");
        sql.append(" inner join itemFicha.fichaFinanceiraFP ficha");
        sql.append(" inner join ficha.folhaDePagamento folha");
        sql.append(" inner join folha.competenciaFP competencia");
        sql.append(" where recursoVinc.vinculoFP = ficha.vinculoFP");
        sql.append("   and fonteEvento.eventoFP = itemFicha.eventoFP");
        sql.append("   and exercicio = competencia.exercicio");
        sql.append("   and folha = :folha");
        sql.append("   and recurso = :recurso");
        sql.append("   and despesaOrc = :despesaOrc");
        sql.append("   and provPPADesp.unidadeOrganizacional = :unidadeOrganizacional");
        sql.append("   and fonteRecurso.fonteDespesaORC = :fonteDespesaORC");
        sql.append("   and fonteRecurso.contaFinanceira = :contaFinanceira");
        sql.append("   and fonteEvento.credor = :credor");
        sql.append("   and fonteEvento.classeCredor = :classeCredor");
        sql.append("   and fonteEvento.desdobramento = :desdobramento");
        sql.append("   and recurso.inicioVigencia <= :data and coalesce(recurso.finalVigencia, :data) >= :data");
        sql.append("   and recursoVinc.inicioVigencia <= :data and coalesce(recursoVinc.finalVigencia, :data) >= :data");
        sql.append("   and fonteEvento.contaExtraorcamentaria is not null");
        sql.append("   and fonteEvento.subConta is not null");
        sql.append(" group by fonteEvento.contaExtraorcamentaria, fonteEvento.subConta");

        Query q = em.createQuery(sql.toString());
        q.setParameter("folha", empenhoFP.getEmpenho().getFolhaDePagamento());
        q.setParameter("recurso", empenhoFP.getRecursoFP());
        q.setParameter("despesaOrc", empenhoFP.getEmpenho().getDespesaORC());
        q.setParameter("unidadeOrganizacional", empenhoFP.getEmpenho().getUnidadeOrganizacional());
        q.setParameter("fonteDespesaORC", empenhoFP.getEmpenho().getFonteDespesaORC());
        q.setParameter("contaFinanceira", empenhoFP.getContaFinanceira());
        q.setParameter("credor", empenhoFP.getEmpenho().getFornecedor());
        q.setParameter("classeCredor", empenhoFP.getEmpenho().getClasseCredor());
        q.setParameter("desdobramento", empenhoFP.getDesdobramento());
        q.setParameter("data", empenhoFP.getEmpenho().getDataEmpenho());
        return q.getResultList();
    }

    private List<EmpenhoFP> gerarEmpenhos(FolhaDePagamento folhaDePagamento, Date dataContabilizacao) throws Exception {
        List<EmpenhoFP> listaEmpenhos = listaContabilizacaoFolhaDePagamento(folhaDePagamento, dataContabilizacao);
        HistoricoContabil historicoContabil = historicoContabilFacade.listaFiltrando(HISTORICO_CONTABIL_EMPENHO, "codigo").get(0);

        for (EmpenhoFP empenhoFP : listaEmpenhos) {
            empenhoFP.getEmpenho().setNumero("0");
            empenhoFP.getEmpenho().setDataEmpenho(dataContabilizacao);
            empenhoFP.getEmpenho().setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            empenhoFP.getEmpenho().setHistoricoContabil(historicoContabil); // ???
            empenhoFP.getEmpenho().setComplementoHistorico(empenhoFP.getEmpenho().getComplementoContabil());

            empenhoFacade.validarEmpenho(empenhoFP.getEmpenho());
        }

        for (EmpenhoFP empenhoFP : listaEmpenhos) {
            BigDecimal numeroEmpenho = new BigDecimal(empenhoFacade.retornaUltimoNumeroEmpenho(empenhoFP.getEmpenho().getUnidadeOrganizacional())).add(BigDecimal.ONE);
            empenhoFP.getEmpenho().setNumero(numeroEmpenho.toString());
            empenhoFP.getEmpenho().setComplementoHistorico(empenhoFP.getEmpenho().getComplementoContabil()
                    .concat(" ")
                    .concat(empenhoFP.getRecursoFP().getDescricao())
                    .concat(" FOLHA DE PAGAMENTO ")
                    .concat(folhaDePagamento.getMes().getDescricao())
                    .concat("/")
                    .concat(folhaDePagamento.getAno().toString()));

            empenhoFacade.salvarEmpenho(empenhoFP.getEmpenho());
        }

        if (listaEmpenhos.isEmpty()) {
            throw new Exception("Não foi possível gerar os empenhos!");
        }

        return listaEmpenhos;
    }

    //HistoricoContabil historicoContabil = null;
    private void gerarLiquidacoes(List<EmpenhoFP> listaEmpenhos) throws Exception {
        HistoricoContabil historicoContabil = historicoContabilFacade.listaFiltrando(HISTORICO_CONTABIL_LIQUIDACAO, "codigo").get(0);
        TipoDocumentoFiscal tipoDocumentoFiscal = tipoDocumentoFiscalFacade.listaFiltrando("FP", "codigo").get(0);

        for (EmpenhoFP empenhoFP : listaEmpenhos) {

            Liquidacao liquidacao = new Liquidacao();

            LiquidacaoDoctoFiscal docto = new LiquidacaoDoctoFiscal();
            docto.getDoctoFiscalLiquidacao().setDataDocto(empenhoFP.getEmpenho().getDataEmpenho());
            docto.getDoctoFiscalLiquidacao().setValor(empenhoFP.getEmpenho().getValor());
            docto.getDoctoFiscalLiquidacao().setTotal(empenhoFP.getEmpenho().getValor());
            docto.getDoctoFiscalLiquidacao().setTipoDocumentoFiscal(tipoDocumentoFiscal);
            docto.getDoctoFiscalLiquidacao().setNumero("0");
            //docto.setLiquidacao(liquidacao);

            Desdobramento desdobramento = new Desdobramento();
            desdobramento.setConta(empenhoFP.getDesdobramento());
            desdobramento.setValor(empenhoFP.getEmpenho().getValor());
            //desdobramento.setLiquidacao(liquidacao);

            liquidacao.getDoctoFiscais().add(docto);
            liquidacao.getDesdobramentos().add(desdobramento);

            BigDecimal numeroLiquidacao = new BigDecimal(liquidacaoFacade.retornaUltimoNumeroLiquidacao(empenhoFP.getEmpenho().getUnidadeOrganizacional())).add(BigDecimal.ONE);
            liquidacao.setNumero(numeroLiquidacao.toString());
            liquidacao.setEmpenho(empenhoFP.getEmpenho());
            liquidacao.setDataLiquidacao(empenhoFP.getEmpenho().getDataEmpenho());
//            liquidacao.setHistoricoContabil(historicoContabil); // ???
            liquidacao.setExercicio(empenhoFP.getEmpenho().getExercicio());
            liquidacao.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
            liquidacao.setValor(empenhoFP.getEmpenho().getValor());
            liquidacao.setSaldo(empenhoFP.getEmpenho().getValor());
            liquidacao.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            liquidacao.setDataRegistro(new Date());
            liquidacao.setComplemento(empenhoFP.getEmpenho().getComplementoHistorico()
                    .concat(" Liquidação nº ")
                    .concat(liquidacao.getNumero())
                    .concat(" ")
                    .concat(empenhoFP.getEmpenho().getFolhaDePagamento().getMes().getDescricao())
                    .concat("/")
                    .concat(empenhoFP.getEmpenho().getFolhaDePagamento().getAno().toString())
                    .concat(" - ")
                    .concat(empenhoFP.getRecursoFP().getDescricao()));

            empenhoFP.setLiquidacao(liquidacao);
            liquidacaoFacade.salvarNovaLiquidacao(liquidacao, Lists.newArrayList());
        }
    }

    private void gerarPagamentos(List<EmpenhoFP> listaEmpenhos) throws Exception {
        HistoricoContabil historicoContabil = historicoContabilFacade.listaFiltrando(HISTORICO_CONTABIL_PAGAMENTO, "codigo").get(0);

        for (EmpenhoFP empenhoFP : listaEmpenhos) {
            Pagamento pagamento = new Pagamento();
            empenhoFP.setPagamento(pagamento);

            BigDecimal numeroPagamento = new BigDecimal(pagamentoFacade.retornaUltimoNumeroPagamento(empenhoFP.getEmpenho().getUnidadeOrganizacional()));
            pagamento.setNumeroPagamento(numeroPagamento.toString());
            pagamento.setLiquidacao(empenhoFP.getLiquidacao());
            pagamento.setValor(empenhoFP.getLiquidacao().getValor());
            pagamento.setDataPagamento(empenhoFP.getLiquidacao().getDataLiquidacao());
            pagamento.setPrevistoPara(empenhoFP.getLiquidacao().getDataLiquidacao());
            pagamento.setExercicio(empenhoFP.getLiquidacao().getExercicio());
            pagamento.setCategoriaOrcamentaria(CategoriaOrcamentaria.NORMAL);
            pagamento.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
            pagamento.setHistoricoContabil(historicoContabil); // ???
            pagamento.setComplementoHistorico(empenhoFP.getLiquidacao().getComplemento()
                    .concat(" Pagamento nº ")
                    .concat(pagamento.getNumeroPagamento())
                    .concat(" ")
                    .concat(empenhoFP.getEmpenho().getFolhaDePagamento().getMes().getDescricao())
                    .concat("/")
                    .concat(empenhoFP.getEmpenho().getFolhaDePagamento().getAno().toString())
                    .concat(" - ")
                    .concat(empenhoFP.getRecursoFP().getDescricao()));

            //Conta Financeira que vai ser debitado o pagamento.
            pagamento.setSubConta(empenhoFP.getContaFinanceira());
            pagamento.setTipoDocPagto(TipoDocPagto.ORDEMPAGAMENTO);
            BigDecimal valorDescontado = pagamento.getValor().subtract(gerarRentencoes(empenhoFP));
            pagamento.setValorFinal(valorDescontado);

            pagamentoFacade.salvarNovoPagto(pagamento);
        }
    }

    private BigDecimal gerarRentencoes(EmpenhoFP empenhoFP) {
        BigDecimal totalRetido = BigDecimal.ZERO;
//        HistoricoContabil historicoContabil = historicoContabilFacade.listaFiltrando(HISTORICO_CONTABIL_RETENCAO, "codigo").get(0);
        List<RetencaoPgtoFP> listaRetencao = listaRetencoesEmpenho(empenhoFP);

        for (RetencaoPgtoFP retencaoPgtoFP : listaRetencao) {
            RetencaoPgto retencao = new RetencaoPgto();
            retencao.setPagamento(empenhoFP.getPagamento());
            retencao.setContaExtraorcamentaria(retencaoPgtoFP.getContaExtraorcamentaria());
            //Conta Financeira onde vai ser Creditado o valor de cada retenção.
            retencao.setSubConta(retencaoPgtoFP.getSubConta());
            retencao.setValor(retencaoPgtoFP.getValor());
            retencao.setSaldo(retencaoPgtoFP.getValor());
            retencao.setDataRetencao(empenhoFP.getPagamento().getDataPagamento());
            retencao.setUnidadeOrganizacional(empenhoFP.getEmpenho().getUnidadeOrganizacional());
            retencao.setUsuarioSistema(empenhoFP.getPagamento().getUsuarioSistema());
//            retencao.setHistoricoContabil(historicoContabil); //???????
            totalRetido = totalRetido.add(retencao.getValor());
            empenhoFP.getPagamento().getRetencaoPgtos().add(retencao);
        }
        return totalRetido;
    }

    public void contabilizar(FolhaDePagamento folhaDePagamento, Date dataContabilizacao) throws Exception {
        List<EmpenhoFP> listaEmpenhos = gerarEmpenhos(folhaDePagamento, dataContabilizacao);
        gerarLiquidacoes(listaEmpenhos);
        gerarPagamentos(listaEmpenhos);
        folhaDePagamentoFacade.efetivarFolhaDePagamento(folhaDePagamento, dataContabilizacao);
    }
}
