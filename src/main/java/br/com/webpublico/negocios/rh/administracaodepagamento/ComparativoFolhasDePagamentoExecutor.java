package br.com.webpublico.negocios.rh.administracaodepagamento;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.rh.comparativofolhasdepagamento.ComparativoFolhasDePagamento;
import br.com.webpublico.entidadesauxiliares.rh.comparativofolhasdepagamento.ItemComparativoFolhasDePagamento;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ComparativoFolhasDePagamentoExecutor implements Callable<AssistenteBarraProgresso> {
    private final Logger logger = LoggerFactory.getLogger(ComparativoFolhasDePagamentoExecutor.class);

    private final FolhaDePagamentoFacade folhaDePagamentoFacade;
    private final VinculoFPFacade vinculoFPFacade;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Integer ano1;
    private Integer mes1;
    private TipoFolhaDePagamento tipoFolhaDePagamento1;
    private Integer ano2;
    private Integer mes2;
    private TipoFolhaDePagamento tipoFolhaDePagamento2;
    private String filtroRecursoFP;
    private Boolean semRetroacao;
    private Boolean exclusivoFolha1;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public ComparativoFolhasDePagamentoExecutor(FolhaDePagamentoFacade folhaDePagamentoFacade, VinculoFPFacade vinculoFPFacade) {
        this.folhaDePagamentoFacade = folhaDePagamentoFacade;
        this.vinculoFPFacade = vinculoFPFacade;
    }

    public Future<AssistenteBarraProgresso> execute(AssistenteBarraProgresso assistenteBarraProgresso, Integer ano1, Integer mes1,
                                                    TipoFolhaDePagamento tipoFolhaDePagamento1, Integer ano2, Integer mes2,
                                                    TipoFolhaDePagamento tipoFolhaDePagamento2, String filtroRecursoFP,
                                                    Boolean semRetroacao, Boolean exclusivoFolha1, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
        this.ano1 = ano1;
        this.mes1 = mes1;
        this.tipoFolhaDePagamento1 = tipoFolhaDePagamento1;
        this.ano2 = ano2;
        this.mes2 = mes2;
        this.tipoFolhaDePagamento2 = tipoFolhaDePagamento2;
        this.filtroRecursoFP = filtroRecursoFP;
        this.semRetroacao = semRetroacao;
        this.exclusivoFolha1 = exclusivoFolha1;
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        return executorService.submit(this);
    }

    public AssistenteBarraProgresso call() throws Exception {
        try {
            return compararFolhasDePagamento();
        } catch (Exception ex) {
            logger.error("Erro ao Comparar de Folhas de Pagamento {}", ex);
        }
        return null;
    }

    public AssistenteBarraProgresso compararFolhasDePagamento() {
        List<Object[]> folhas = folhaDePagamentoFacade.buscarFolhasParaComparacao(ano1, mes1, tipoFolhaDePagamento1, ano2, mes2, tipoFolhaDePagamento2, filtroRecursoFP, semRetroacao, exclusivoFolha1, hierarquiaOrganizacional);
        assistenteBarraProgresso.setTotal(folhas.size());
        ComparativoFolhasDePagamento comparativoFolhasDePagamento = new ComparativoFolhasDePagamento();
        List<ItemComparativoFolhasDePagamento> itensComparativo = Lists.newArrayList();
        BigDecimal totalVantagem1 = BigDecimal.ZERO;
        BigDecimal totalVantagem2 = BigDecimal.ZERO;
        BigDecimal totalDesconto1 = BigDecimal.ZERO;
        BigDecimal totalDesconto2 = BigDecimal.ZERO;
        BigDecimal totalLiquido1 = BigDecimal.ZERO;
        BigDecimal totalLiquido2 = BigDecimal.ZERO;
        for (Object[] obj : folhas) {
            ItemComparativoFolhasDePagamento item = new ItemComparativoFolhasDePagamento();
            item.setVinculoFP(vinculoFPFacade.recuperarSimples(((BigDecimal) obj[0]).longValue()));
            item.setVantagem1((BigDecimal) obj[1]);
            item.setDesconto1((BigDecimal) obj[2]);
            item.setVantagem2((BigDecimal) obj[3]);
            item.setDesconto2((BigDecimal) obj[4]);
            item.setLiquido1(item.getVantagem1().subtract(item.getDesconto1()));
            item.setLiquido2(item.getVantagem2().subtract(item.getDesconto2()));
            item.setVantagemDiferenca(item.getVantagem1().subtract(item.getVantagem2()));
            item.setDescontoDiferenca(item.getDesconto1().subtract(item.getDesconto2()));
            item.setLiquidoDiferenca(item.getLiquido1().subtract(item.getLiquido2()));
            itensComparativo.add(item);
            totalVantagem1 = totalVantagem1.add(item.getVantagem1());
            totalVantagem2 = totalVantagem2.add(item.getVantagem2());
            totalDesconto1 = totalDesconto1.add(item.getDesconto1());
            totalDesconto2 = totalDesconto2.add(item.getDesconto2());
            totalLiquido1 = totalLiquido1.add(item.getLiquido1());
            totalLiquido2 = totalLiquido2.add(item.getLiquido2());
            assistenteBarraProgresso.conta();
        }
        comparativoFolhasDePagamento.setItens(itensComparativo);
        comparativoFolhasDePagamento.setTotalVantagem1(totalVantagem1);
        comparativoFolhasDePagamento.setTotalVantagem2(totalVantagem2);
        comparativoFolhasDePagamento.setTotalDesconto1(totalDesconto1);
        comparativoFolhasDePagamento.setTotalDesconto2(totalDesconto2);
        comparativoFolhasDePagamento.setTotalLiquido1(totalLiquido1);
        comparativoFolhasDePagamento.setTotalLiquido2(totalLiquido2);
        comparativoFolhasDePagamento.setTotalVantagemDiferenca(totalVantagem1.subtract(totalVantagem2));
        comparativoFolhasDePagamento.setTotalDescontoDiferenca(totalDesconto1.subtract(totalDesconto2));
        comparativoFolhasDePagamento.setTotalLiquidoDiferenca(totalLiquido1.subtract(totalLiquido2));
        assistenteBarraProgresso.setSelecionado(comparativoFolhasDePagamento);
        return assistenteBarraProgresso;
    }
}
