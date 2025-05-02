package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.SituacaoLoteBaixa;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.tributario.services.ServiceConsultaDebitos;
import br.com.webpublico.singletons.SingletonConferenciaRelatorio;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Wellington on 24/11/2015.
 */
public abstract class AbstractRelacaoLancamentoTributarioFacade implements Serializable {

    public static int QUANTIDADE_REGISTROS_POR_PDF = 100000;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SingletonConferenciaRelatorio singletonConferenciaRelatorio;

    protected abstract Logger getLogger();

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public boolean getSituacoesDoValorDividaIguais(List<Object> objetos) {
        SituacaoParcela situacaoParcela = SituacaoParcela.fromDto(((AbstractVOConsultaLancamento) objetos.get(0)).getResultadoParcela().getSituacaoEnumValue());
        for (Object obj : objetos) {
            AbstractVOConsultaLancamento vo = (AbstractVOConsultaLancamento) obj;
            if (!situacaoParcela.equals(vo.getResultadoParcela().getSituacaoEnumValue())) {
                return false;
            }
        }

        return true;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<VOAgrupamentoPorSituacao> agruparPorSituacao(List<? extends AbstractVOConsultaLancamento> resultado, AbstractFiltroRelacaoLancamentoDebito filtro) {
        List<? extends AbstractVOConsultaLancamento> resultadoParaAgrupar = agruparParcelasPorValorDivida(resultado, filtro);
        Map<String, VOAgrupamentoPorSituacao> mapaTotalSituacao = new HashMap<>();

        for (AbstractVOConsultaLancamento vo : resultadoParaAgrupar) {
            VOAgrupamentoPorSituacao voAgrupamentoPorSituacao = mapaTotalSituacao.get(vo.getResultadoParcela().getSituacao());
            if (voAgrupamentoPorSituacao == null) {
                voAgrupamentoPorSituacao = new VOAgrupamentoPorSituacao();
            }
            voAgrupamentoPorSituacao.setSituacaoParcela(vo.getResultadoParcela().getSituacao());
            voAgrupamentoPorSituacao.setValorImposto(voAgrupamentoPorSituacao.getValorImposto().add(vo.getResultadoParcela().getValorImposto()));
            voAgrupamentoPorSituacao.setValorTaxa(voAgrupamentoPorSituacao.getValorTaxa().add(vo.getResultadoParcela().getValorTaxa()));
            voAgrupamentoPorSituacao.setValorJuros(voAgrupamentoPorSituacao.getValorJuros().add(vo.getResultadoParcela().getValorJuros()));
            voAgrupamentoPorSituacao.setValorMulta(voAgrupamentoPorSituacao.getValorMulta().add(vo.getResultadoParcela().getValorMulta()));
            voAgrupamentoPorSituacao.setValorCorrecao(voAgrupamentoPorSituacao.getValorCorrecao().add(vo.getResultadoParcela().getValorCorrecao()));
            voAgrupamentoPorSituacao.setValorHonorarios(voAgrupamentoPorSituacao.getValorHonorarios().add(vo.getResultadoParcela().getValorHonorarios()));
            voAgrupamentoPorSituacao.setValorDesconto(voAgrupamentoPorSituacao.getValorDesconto().add(vo.getResultadoParcela().getValorDesconto()));
            voAgrupamentoPorSituacao.setValorPago(voAgrupamentoPorSituacao.getValorPago().add(vo.getResultadoParcela().getValorPago()));
            if (BigDecimal.ZERO.compareTo(voAgrupamentoPorSituacao.getValorPago()) != 0) {
                voAgrupamentoPorSituacao.setValorTotal(voAgrupamentoPorSituacao.getValorTotal().add(vo.getResultadoParcela().getValorPago()));
            } else {
                voAgrupamentoPorSituacao.setValorTotal(voAgrupamentoPorSituacao.getValorTotal().add(vo.getResultadoParcela().getValorTotal()));
            }
            mapaTotalSituacao.put(vo.getResultadoParcela().getSituacao(), voAgrupamentoPorSituacao);
        }

        List<VOAgrupamentoPorSituacao> agrupado = Lists.newArrayList();
        for (String chave : mapaTotalSituacao.keySet()) {
            agrupado.add(mapaTotalSituacao.get(chave));
        }
        return agrupado;
    }


    protected AbstractFiltroRelacaoLancamentoDebito montarWhere(AbstractFiltroRelacaoLancamentoDebito abstractFiltroRelacaoLancamentoDebito) {
        abstractFiltroRelacaoLancamentoDebito.inicializarFiltro();

        abstractFiltroRelacaoLancamentoDebito.addFiltro("vw.emissao", ">=", "Data de Lançamento Inicial", abstractFiltroRelacaoLancamentoDebito.getDataLancamentoInicial());
        abstractFiltroRelacaoLancamentoDebito.addFiltro("vw.emissao", "<=", "Data de Lançamento Final", abstractFiltroRelacaoLancamentoDebito.getDataLancamentoFinal());

        abstractFiltroRelacaoLancamentoDebito.addFiltro("vw.exercicio", ">=", "Exercício Inicial", abstractFiltroRelacaoLancamentoDebito.getExercicioDividaInicial());
        abstractFiltroRelacaoLancamentoDebito.addFiltro("vw.exercicio", "<=", "Exercício Final", abstractFiltroRelacaoLancamentoDebito.getExercicioDividaFinal());

        abstractFiltroRelacaoLancamentoDebito.addFiltro("vw.vencimento", ">=", "Vencimento Inicial", abstractFiltroRelacaoLancamentoDebito.getDataVencimentoInicial());
        abstractFiltroRelacaoLancamentoDebito.addFiltro("vw.vencimento", "<=", "Vencimento Final", abstractFiltroRelacaoLancamentoDebito.getDataVencimentoFinal());

        abstractFiltroRelacaoLancamentoDebito.addFiltroIn("vw.id_divida", "Divida(s)", abstractFiltroRelacaoLancamentoDebito.inDividas(), abstractFiltroRelacaoLancamentoDebito.filtroDividas());

        if (abstractFiltroRelacaoLancamentoDebito.getDataPagamentoInicial() != null && abstractFiltroRelacaoLancamentoDebito.getDataPagamentoFinal() != null) {
            abstractFiltroRelacaoLancamentoDebito.addFiltroLivre("  ( exists (select 1 " +
                "      from lotebaixa lb " +
                "     inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id " +
                "     inner join itemlotebaixaparcela ilbp on ilbp.itemlotebaixa_id = ilb.id " +
                "     inner join itemdam idam on idam.id = ilbp.itemdam_id " +
                "   where idam.parcela_id = vw.id_parcela " +
                "     and lb.situacaolotebaixa in ('" + SituacaoLoteBaixa.BAIXADO.name() + "', '" + SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name() + "')" +
                "     and lb.datapagamento between to_date('" + Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoInicial()) + "' , 'dd/MM/yyyy') " +
                "                          and     to_date('" + Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoFinal()) + "' , 'dd/MM/yyyy') ) " +
                " or exists (" +
                "   select 1 \n" +
                "       from itemprocessodebito ipd\n" +
                "     inner join processodebito pd on pd.id = ipd.processodebito_id\n" +
                "    where pd.tipo = 'BAIXA' and pd.situacao = 'FINALIZADO' and ipd.parcela_id = vw.id_parcela " +
                "     and pd.datapagamento between to_date('" + Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoInicial()) + "' , 'dd/MM/yyyy') " +
                "                          and     to_date('" + Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoFinal()) + "' , 'dd/MM/yyyy') )" +
                " ) ");

            abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Data de Pagamento Inicial", Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoInicial()));
            abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Data de Pagamento Final", Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoFinal()));
        } else if (abstractFiltroRelacaoLancamentoDebito.getDataPagamentoInicial() != null) {
            abstractFiltroRelacaoLancamentoDebito.addFiltroLivre("  ( exists (select 1 " +
                "      from lotebaixa lb " +
                "     inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id " +
                "     inner join itemlotebaixaparcela ilbp on ilbp.itemlotebaixa_id = ilb.id " +
                "     inner join itemdam idam on idam.id = ilbp.itemdam_id " +
                "   where idam.parcela_id = vw.id_parcela " +
                "     and lb.situacaolotebaixa in ('" + SituacaoLoteBaixa.BAIXADO.name() + "', '" + SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name() + "')" +
                "     and lb.datapagamento >= to_date('" +
                Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoInicial()) +
                "' , 'dd/MM/yyyy') ) " +
                " or exists (" +
                "   select 1 \n" +
                "       from itemprocessodebito ipd\n" +
                "     inner join processodebito pd on pd.id = ipd.processodebito_id\n" +
                "    where pd.tipo = 'BAIXA' and pd.situacao = 'FINALIZADO' and ipd.parcela_id = vw.id_parcela " +
                "     and pd.datapagamento >= to_date('" + Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoInicial()) + "' , 'dd/MM/yyyy') )" +
                ") ");
            abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Data de Pagamento Inicial",
                Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoInicial()));
        } else if (abstractFiltroRelacaoLancamentoDebito.getDataPagamentoFinal() != null) {
            abstractFiltroRelacaoLancamentoDebito.addFiltroLivre(" ( exists (select 1 " +
                "      from lotebaixa lb " +
                "     inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id " +
                "     inner join itemlotebaixaparcela ilbp on ilbp.itemlotebaixa_id = ilb.id " +
                "     inner join itemdam idam on idam.id = ilbp.itemdam_id " +
                "   where idam.parcela_id = vw.id_parcela " +
                "     and lb.situacaolotebaixa in ('" + SituacaoLoteBaixa.BAIXADO.name() + "', '" + SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name() + "')" +
                "     and lb.datapagamento <= to_date('" +
                Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoFinal()) +
                "' , 'dd/MM/yyyy') ) " +
                " or exists (" +
                "   select 1 " +
                "       from itemprocessodebito ipd " +
                "     inner join processodebito pd on pd.id = ipd.processodebito_id " +
                "    where pd.tipo = 'BAIXA' and pd.situacao = 'FINALIZADO' and ipd.parcela_id = vw.id_parcela " +
                "     and pd.datapagamento <= to_date('" + Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoFinal()) + "' , 'dd/MM/yyyy') )" +
                ") ");
            abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Data de Pagamento Final", Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataPagamentoFinal()));
        }

        if (abstractFiltroRelacaoLancamentoDebito.getDataMovimentoInicial() != null && abstractFiltroRelacaoLancamentoDebito.getDataMovimentoFinal() != null) {
            abstractFiltroRelacaoLancamentoDebito.addFiltroExists("  (select 1 " +
                "      from lotebaixa lb " +
                "     inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id " +
                "     inner join itemlotebaixaparcela ilbp on ilbp.itemlotebaixa_id = ilb.id " +
                "     inner join itemdam idam on idam.id = ilbp.itemdam_id " +
                "   where idam.parcela_id = vw.id_parcela " +
                "     and lb.situacaolotebaixa in ('" + SituacaoLoteBaixa.BAIXADO.name() + "', '" + SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name() + "')" +
                "     and lb.datafinanciamento between to_date('" + Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataMovimentoInicial()) + "' , 'dd/MM/yyyy') " +
                "                              and     to_date('" + Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataMovimentoFinal()) + "' , 'dd/MM/yyyy') ) ");
            abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Data de Movimento Inicial", Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataMovimentoInicial()));
            abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Data de Movimento Final", Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataMovimentoFinal()));
        } else if (abstractFiltroRelacaoLancamentoDebito.getDataMovimentoInicial() != null) {
            abstractFiltroRelacaoLancamentoDebito.addFiltroExists("  (select 1 " +
                "      from lotebaixa lb " +
                "     inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id " +
                "     inner join itemlotebaixaparcela ilbp on ilbp.itemlotebaixa_id = ilb.id " +
                "     inner join itemdam idam on idam.id = ilbp.itemdam_id " +
                "   where idam.parcela_id = vw.id_parcela " +
                "     and lb.situacaolotebaixa in ('" + SituacaoLoteBaixa.BAIXADO.name() + "', '" + SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name() + "')" +
                "     and lb.datafinanciamento >= to_date('" +
                Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataMovimentoInicial()) +
                "' , 'dd/MM/yyyy')) ");
            abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Data de Movimento Inicial", Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataMovimentoInicial()));
        } else if (abstractFiltroRelacaoLancamentoDebito.getDataMovimentoFinal() != null) {
            abstractFiltroRelacaoLancamentoDebito.addFiltroExists("  (select 1 " +
                "      from lotebaixa lb " +
                "     inner join itemlotebaixa ilb on ilb.lotebaixa_id = lb.id " +
                "     inner join itemlotebaixaparcela ilbp on ilbp.itemlotebaixa_id = ilb.id " +
                "     inner join itemdam idam on idam.id = ilbp.itemdam_id " +
                "   where idam.parcela_id = vw.id_parcela " +
                "     and lb.situacaolotebaixa in ('" + SituacaoLoteBaixa.BAIXADO.name() + "', '" + SituacaoLoteBaixa.BAIXADO_INCONSITENTE.name() + "')" +
                "     and lb.datafinanciamento <= to_date('" +
                Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataMovimentoFinal()) +
                "' , 'dd/MM/yyyy')) ");
            abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Data de Movimento Final", Util.dateToString(abstractFiltroRelacaoLancamentoDebito.getDataMovimentoFinal()));
        }

        if (abstractFiltroRelacaoLancamentoDebito.getContribuintes() != null && abstractFiltroRelacaoLancamentoDebito.getContribuintes().size() > 0) {
            String juncao = "";
            String ids = "";
            String contribuintes = "";
            for (Pessoa pessoa : abstractFiltroRelacaoLancamentoDebito.getContribuintes()) {
                ids += juncao + pessoa.getId();
                contribuintes += juncao + pessoa;
                juncao = ", ";
            }
            abstractFiltroRelacaoLancamentoDebito.addFiltroIn("vw.id_pessoa", "Contribuintes", ids, contribuintes);
        } else {
            abstractFiltroRelacaoLancamentoDebito.addFiltroLivre("vw.id_pessoa = (select max(calcpes.pessoa_id) from CalculoPessoa calcpes where calcpes.calculo_id = vw.id_calculo)");
        }

        if (abstractFiltroRelacaoLancamentoDebito.getSituacoes() != null && abstractFiltroRelacaoLancamentoDebito.getSituacoes().length > 0) {
            if (abstractFiltroRelacaoLancamentoDebito.getSituacoes().length == SituacaoParcela.getValues().size()) {
                abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Situação(ões) de Pagamento", "Todas");
            } else {
                String juncao = "";
                String ids = "";
                String situacoes = "";
                for (SituacaoParcela situacaoParcela : abstractFiltroRelacaoLancamentoDebito.getSituacoes()) {
                    ids += juncao + "'" + situacaoParcela.name() + "'";
                    situacoes += juncao + situacaoParcela.getDescricao();
                    juncao = ", ";
                }
                abstractFiltroRelacaoLancamentoDebito.addFiltroIn("vw.situacaoparcela", "Situação(ões) de Pagamento", ids, situacoes);
            }
        }

        abstractFiltroRelacaoLancamentoDebito.addFiltro(" vw.valororiginal ", ">=", "Total Lançado Final", abstractFiltroRelacaoLancamentoDebito.getTotalLancadoInicial());
        abstractFiltroRelacaoLancamentoDebito.addFiltro(" vw.valororiginal ", "<=", "Total Lançado Final", abstractFiltroRelacaoLancamentoDebito.getTotalLancadoFinal());

        abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Tipo de Relatório", abstractFiltroRelacaoLancamentoDebito.getTipoRelatorio().toString());
        abstractFiltroRelacaoLancamentoDebito.addApenasFiltro("Totalizador de Lançamento", abstractFiltroRelacaoLancamentoDebito.getSomenteTotalizador() ? "Sim" : "Não");

        return abstractFiltroRelacaoLancamentoDebito;
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<? extends AbstractVOConsultaLancamento> agruparParcelasPorValorDivida(List<? extends AbstractVOConsultaLancamento> resultado, AbstractFiltroRelacaoLancamentoDebito filtro) {
        Map<Long, List<Object>> parcelaPorVd = Maps.newHashMap();
        for (AbstractVOConsultaLancamento vo : resultado) {
            if (!parcelaPorVd.containsKey(vo.getResultadoParcela().getIdValorDivida())) {
                parcelaPorVd.put(vo.getResultadoParcela().getIdValorDivida(), Lists.newArrayList());
            }
            parcelaPorVd.get(vo.getResultadoParcela().getIdValorDivida()).add(vo);
        }
        List<AbstractVOConsultaLancamento> resultadoParaAgrupar = Lists.newArrayList();
        for (Long idValorDivida : parcelaPorVd.keySet()) {
            List<AbstractVOConsultaLancamento> somar = Lists.newArrayList();
            AbstractVOConsultaLancamento vo = null;
            if (getSituacoesDoValorDividaIguais(parcelaPorVd.get(idValorDivida)) && SituacaoParcela.PAGO.equals(((AbstractVOConsultaLancamento) parcelaPorVd.get(idValorDivida).get(0)).getResultadoParcela().getSituacaoEnumValue())) {
                for (Object obj : parcelaPorVd.get(idValorDivida)) {
                    vo = (AbstractVOConsultaLancamento) obj;
                    somar.add(vo);
                }
            } else {
                for (Object obj : parcelaPorVd.get(idValorDivida)) {
                    vo = (AbstractVOConsultaLancamento) obj;
                    if (filtro.getTipoCobrancaTributario() == null) {
                        if (vo.getResultadoParcela().getCotaUnica()
                            && (!vo.getResultadoParcela().getVencido() || SituacaoParcela.PAGO.equals(vo.getResultadoParcela().getSituacaoEnumValue()))) {
                            somar.clear();
                            somar.add(vo);
                            break;
                        } else if (!vo.getResultadoParcela().getCotaUnica()) {
                            somar.add(vo);
                        }
                    } else {
                        somar.add(vo);
                    }
                }
            }
            resultadoParaAgrupar.addAll(somar);
            BigDecimal valorTotalValorDivida = BigDecimal.ZERO;
            for (AbstractVOConsultaLancamento voSomar : somar) {
                valorTotalValorDivida = valorTotalValorDivida.add(voSomar.getResultadoParcela().getValorImposto());
            }
        }

        return resultadoParaAgrupar;
    }

    public Long totalizarQuantidadeDeCadastros(List<? extends AbstractVOConsultaLancamento> resultado) {
        HashSet<Long> cadastros = new HashSet();
        for (AbstractVOConsultaLancamento vo : resultado) {
            cadastros.add(vo.getResultadoParcela().getIdCadastro() != null ? vo.getResultadoParcela().getIdCadastro() : vo.getResultadoParcela().getIdPessoa());
        }
        return new Long(cadastros.size());
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<JasperPrint> gerarJasper(String usuario, String caminhoReport, String caminhoImagem,
                                           UnidadeOrganizacional unidadeOrganizacional, String nomeDoRelatorio, String filtro, List parte, int bloco) throws IOException, JRException {
        getLogger().debug("GERANDO JASPER - BLOCO : " + bloco);
        return new AsyncResult<>(gerarBytesDoRelatorio(usuario, caminhoReport, caminhoImagem, nomeDoRelatorio, parte, bloco, filtro, unidadeOrganizacional));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<ByteArrayOutputStream> gerarRelatorio(List<JasperPrint> jaspers, String usuario, String caminhoReport, String caminhoImagem,
                                                        RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario relacaoLancamentoTributario,
                                                        AbstractFiltroRelacaoLancamentoDebito filtro, AssistenteRelacaoLancamentoTributario assistente,
                                                        UnidadeOrganizacional unidadeOrganizacional) throws IOException, JRException {
        ByteArrayOutputStream report = null;
        try {
            addTotalizador(usuario, caminhoReport, caminhoImagem, relacaoLancamentoTributario, filtro, assistente, jaspers, unidadeOrganizacional);

            AbstractReport abstractReport = AbstractReport.getAbstractReport();
            report = abstractReport.exportarJaspersParaPDF(jaspers);
            getLogger().debug("FINALIZOU EXPORT PARA PDF");
        } catch (Exception e) {
            getLogger().error("Erro ao gerar relatório [{}]", e);
        }
        return new AsyncResult<>(report);
    }

    public boolean isSomenteTotalizador(AssistenteRelacaoLancamentoTributario assistente) {
        return assistente.getSomenteTotalizador();
    }

    protected void addTotalizador(String usuario, String caminhoReport, String caminhoImagem, RelatorioRelacaoLancamentoTributario.RelacaoLancamentoTributario relacaoLancamentoTributario, AbstractFiltroRelacaoLancamentoDebito filtro, AssistenteRelacaoLancamentoTributario assistente, List jaspers, UnidadeOrganizacional unidadeOrganizacional) throws IOException, JRException {
        String nomeRelatorioAgrupadorPorSituacao;
        if (AbstractFiltroRelacaoLancamentoDebito.TipoRelatorio.RESUMIDO.equals(filtro.getTipoRelatorio())) {
            nomeRelatorioAgrupadorPorSituacao = "RelacaoLancamentoTributarioAgrupadorPorSituacaoResumido";
        } else {
            nomeRelatorioAgrupadorPorSituacao = "RelacaoLancamentoTributarioAgrupadorPorSituacaoDetalhado";
        }

        jaspers.add(gerarBytesDoTotalizador(usuario, caminhoReport, caminhoImagem, nomeRelatorioAgrupadorPorSituacao,
            assistente.getResultadoPorSituacao(), filtro.getFiltro().toString(), relacaoLancamentoTributario.getDescricao(),
            totalizarQuantidadeDeCadastros(assistente.getResultado()), new Long(assistente.getResultado().size()), unidadeOrganizacional));
    }

    private JasperPrint gerarBytesDoRelatorio(String usuario, String caminhoReport, String caminhoImagem,
                                              String nomeDoJasper, List resultado,
                                              int bloco, String filtro, UnidadeOrganizacional unidadeOrganizacional) throws IOException, JRException {
        AbstractReport report = AbstractReport.getAbstractReport();

        HashMap parameters = new HashMap();
        parameters.put("USUARIO", usuario);
        parameters.put("SECRETARIA", "Secretaria Municipal de Finanças");
        parameters.put("BRASAO", caminhoImagem);
        parameters.put("BLOCO", bloco);
        parameters.put("FILTROS", filtro);
        parameters.put("SUBREPORT_DIR", caminhoReport);

        return report.gerarBytesDeRelatorioComDadosEmCollectionView(caminhoReport, nomeDoJasper + ".jasper", parameters, new JRBeanCollectionDataSource(resultado), unidadeOrganizacional);
    }


    protected JasperPrint gerarBytesDoTotalizador(String usuario, String caminhoReport, String caminhoImagem,
                                                  String nomeDoJasper, List resultado,
                                                  String filtro, String tipoDivida, Long quantidadeDeCadastro,
                                                  Long quantidadeDeParcelas, UnidadeOrganizacional unidadeOrganizacional) throws IOException, JRException {
        AbstractReport report = AbstractReport.getAbstractReport();

        HashMap parameters = new HashMap();
        parameters.put("USUARIO", usuario);
        parameters.put("SECRETARIA", "Secretaria Municipal de Finanças");
        parameters.put("BRASAO", caminhoImagem);
        parameters.put("FILTROS", filtro);
        parameters.put("TIPO_DIVIDA", tipoDivida);
        parameters.put("QUANTIDADE_CADASTROS", quantidadeDeCadastro);
        parameters.put("QUANTIDADE_PARCELAS", quantidadeDeParcelas);
        parameters.put("SUBREPORT_DIR", caminhoReport);

        return report.gerarBytesDeRelatorioComDadosEmCollectionView(caminhoReport, nomeDoJasper + ".jasper", parameters, new JRBeanCollectionDataSource(resultado), unidadeOrganizacional);
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    public Future<? extends List<? extends AbstractVOConsultaLancamento>> calcularAcrescimo(List<? extends AbstractVOConsultaLancamento> resultado,
                                                                                            Date dataPagamentoInicial, Date dataPagamentoFinal) {
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        ServiceConsultaDebitos serviceConsultaDebitos = (ServiceConsultaDebitos) ap.getBean("serviceConsultaDebitos");
        serviceConsultaDebitos.calcularAcrescimos(resultado, dataPagamentoInicial, dataPagamentoFinal, null, null);
        return new AsyncResult<>(resultado);
    }

    public abstract Future<? extends List<? extends AbstractVOConsultaLancamento>> consultarLancamentos(AbstractFiltroRelacaoLancamentoDebito filtroRelacaoLancamentoDebito);

}
