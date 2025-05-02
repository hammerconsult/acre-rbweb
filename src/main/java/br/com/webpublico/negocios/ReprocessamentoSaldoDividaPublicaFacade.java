package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.entidadesauxiliares.SaldoDividaPublicaReprocessamento;
import br.com.webpublico.enums.Intervalo;
import br.com.webpublico.enums.OperacaoDiarioDividaPublica;
import br.com.webpublico.enums.OperacaoMovimentoDividaPublica;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoHistoricoFacade;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mateus on 02/02/2015.
 */
@Stateless
public class ReprocessamentoSaldoDividaPublicaFacade {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private SaldoDividaPublicaFacade saldoDividaPublicaFacade;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ReprocessamentoHistoricoFacade reprocessamentoHistoricoFacade;

    public ReprocessamentoSaldoDividaPublicaFacade() {
    }

    public SaldoDividaPublicaFacade getSaldoDividaPublicaFacade() {
        return saldoDividaPublicaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteReprocessamento> reprocessar(AssistenteReprocessamento assistente) {
        AsyncResult<AssistenteReprocessamento> retorno = new AsyncResult<>(assistente);
        assistente.setQueryReprocessamento(QueryReprocessamentoService.getService().getReprocessamentoDividaPublica());
        assistente.getAssistenteBarraProgresso().inicializa();
        if (!assistente.getQueryReprocessamento().isEmpty()) {
            excluirSaldo(assistente);
            List<SaldoDividaPublicaReprocessamento> itens = recuperarItens(assistente);
            assistente.getAssistenteBarraProgresso().inicializa();
            assistente.getAssistenteBarraProgresso().setTotal(itens.size());
            assistente.getHistorico().setTotal(itens.size());
            gerarSaldoDividaPublica(assistente, itens);
        }
        assistente.finalizar();
        salvarHistorico(assistente.getHistorico());
        return retorno;
    }

    void excluirSaldo(AssistenteReprocessamento assistente) {
        if (assistente.getExcluirSaldos()) {
            assistente.getAssistenteBarraProgresso().setMensagem("EXCLUINDO SALDOS");
            saldoDividaPublicaFacade.excluirSaldosPeriodo(assistente.getDataInicial(), assistente.getDataFinal());
        }
    }

    public void gerarSaldoDividaPublica(AssistenteReprocessamento assistente, List<SaldoDividaPublicaReprocessamento> itens) {
        String objetosUtilizados = "";
        for (SaldoDividaPublicaReprocessamento item : itens) {
            try {
                objetosUtilizados = "Unidade: " + item.getUnidadeOrganizacional() +
                    "; Operação Movimento: " + item.getOperacaoMovimentoDividaPublica() +
                    "; Operação Diário: " + item.getOperacaoDiarioDividaPublica() +
                    "; Conta de Destinação: " + item.getContaDeDestinacao();
                assistente.getAssistenteBarraProgresso().setMensagem("GERANDO SALDO DÍVIDA PÚBLICA PARA O DIA " + DataUtil.getDataFormatada(item.getData()));
                saldoDividaPublicaFacade.gerarMovimento(item.getData(),
                    item.getValor(),
                    item.getUnidadeOrganizacional(),
                    item.getDividaPublica(),
                    item.getOperacaoMovimentoDividaPublica(),
                    item.getEstorno(),
                    item.getOperacaoDiarioDividaPublica(),
                    item.getIntervalo(),
                    item.getContaDeDestinacao(),
                    false);
                assistente.adicionarHistoricoLogSemErro(item.getDividaPublica(), objetosUtilizados);
                assistente.historicoContaSemErro();
            } catch (OptimisticLockException optex) {
                throw new ExcecaoNegocioGenerica("Saldo financeiro está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
            } catch (Exception ex) {
                assistente.adicionarReprocessandoErroLog(ex.getMessage(), item.getDividaPublica(), objetosUtilizados);
                assistente.historicoContaComErro();
            } finally {
                assistente.historicoConta();
            }
        }
    }

    public List<SaldoDividaPublicaReprocessamento> recuperarItens(AssistenteReprocessamento assistente) {
        assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO MOVIMENTOS");
        StringBuilder sql = new StringBuilder();
        sql.append(assistente.getQueryReprocessamento()
            .replace("$ContasDespesaPelaConfiguracao", buscarContasDespesaPelaConfiguracaoContabil())
            .replace("$ContasReceitaPelaConfiguracao", buscarContasReceitaPelaConfiguracaoContabil()));
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        List<Object[]> resultado = q.getResultList();
        List<SaldoDividaPublicaReprocessamento> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                SaldoDividaPublicaReprocessamento item = new SaldoDividaPublicaReprocessamento();
                item.setData((Date) obj[0]);
                item.setValor((BigDecimal) obj[1]);
                item.setUnidadeOrganizacional(unidadeOrganizacionalFacade.getUnidadePelaSubordinada(((BigDecimal) obj[2]).longValue(), item.getData(), item.getData()));
                item.setDividaPublica(dividaPublicaFacade.recuperar(((BigDecimal) obj[3]).longValue()));
                item.setOperacaoMovimentoDividaPublica(OperacaoMovimentoDividaPublica.valueOf((String) obj[4]));
                item.setEstorno(((BigDecimal) obj[5]).compareTo(BigDecimal.ONE) == 0);
                item.setOperacaoDiarioDividaPublica(OperacaoDiarioDividaPublica.valueOf((String) obj[6]));
                item.setIntervalo(Intervalo.valueOf((String) obj[7]));
                item.setContaDeDestinacao((ContaDeDestinacao) contaFacade.recuperar(((BigDecimal) obj[8]).longValue()));
                retorno.add(item);
            }
        }
        return retorno;
    }

    private String buscarContasDespesaPelaConfiguracaoContabil() {
        String clausula = "";
        ConfiguracaoContabil configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
        for (ConfiguracaoContabilContaDespesa configuracaoContabilContaDespesa : configuracaoContabil.getContasDespesa()) {
            clausula += " c.codigo like '" + configuracaoContabilContaDespesa.getContaDespesa().getCodigoSemZerosAoFinal() + "%' or ";
        }
        clausula = clausula.substring(0, clausula.length() - 3);
        return !clausula.isEmpty() ? "(" + clausula + ")" : "";
    }

    private String buscarContasReceitaPelaConfiguracaoContabil() {
        String clausula = "";
        ConfiguracaoContabil configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
        for (ConfiguracaoContabilContaReceita configuracaoContabilContaReceita : configuracaoContabil.getContasReceita()) {
            clausula += " c.codigo like '" + configuracaoContabilContaReceita.getContaReceita().getCodigoSemZerosAoFinal() + "%' or ";
        }
        clausula = clausula.substring(0, clausula.length() - 3);
        return !clausula.isEmpty() ? "(" + clausula + ")" : "";
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }

    public ReprocessamentoHistoricoFacade getReprocessamentoHistoricoFacade() {
        return reprocessamentoHistoricoFacade;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void salvarHistorico(ReprocessamentoHistorico historico) {
        reprocessamentoHistoricoFacade.salvar(historico);
    }
}
