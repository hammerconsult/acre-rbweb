package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.ConsultaDebitoFacade;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

public class ImprimeDemonstrativoDebitos extends AbstractReport {

    private HashMap parameters;
    private String nomeRelatorio;
    private List<ResultadoParcela> resultados;
    private List<TotalSituacao> totalPorSituacao;

    public ImprimeDemonstrativoDebitos(String nomeRelatorio, LinkedList<ResultadoParcela> resultadosParcela) {
        super();
        this.nomeRelatorio = nomeRelatorio;
        this.resultados = resultadosParcela;
        this.totalPorSituacao = Lists.newArrayList();
    }

    public void adicionarParametro(String nome, Object valor) {
        if (parameters == null) {
            parameters = new HashMap();
        }
        parameters.put(nome, valor);
    }

    public void imprimeRelatorio() throws IOException, JRException {
        gerarRelatorioComDadosEmCollectionView(nomeRelatorio, parameters, new JRBeanCollectionDataSource(resultados));
    }

    public List<TotalSituacao> getTotalPorSituacao() {
        return totalPorSituacao;
    }

    public void setTotalPorSituacao(List<TotalSituacao> totalPorSituacao) {
        this.totalPorSituacao = totalPorSituacao;
    }

    public void montarMapa() {
        Map<Long, List<ResultadoParcela>> parcelaPorVd = Maps.newHashMap();
        for (ResultadoParcela resultado : resultados) {
            if (!parcelaPorVd.containsKey(resultado.getIdValorDivida())) {
                parcelaPorVd.put(resultado.getIdValorDivida(), new ArrayList<ResultadoParcela>());
            }
            parcelaPorVd.get(resultado.getIdValorDivida()).add(resultado);
        }

        List<ResultadoParcela> parcelasSoma = Lists.newArrayList();
        for (Long idValorDivida : parcelaPorVd.keySet()) {
            List<ResultadoParcela> parcelasDaDivida = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : parcelaPorVd.get(idValorDivida)) {
                if (resultadoParcela.getCotaUnica()
                    && !resultadoParcela.getVencido()
                    && (resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.EM_ABERTO.name())
                    || resultadoParcela.getSituacaoNameEnum().equals(SituacaoParcela.PAGO.name()))
                    ) {
                    parcelasDaDivida.clear();
                    parcelasDaDivida.add(resultadoParcela);
                    break;
                } else {
                    parcelasDaDivida.add(resultadoParcela);
                }
            }
            parcelasSoma.addAll(parcelasDaDivida);
        }

        Map<String, BigDecimal> totaisSituacao = Maps.newHashMap();
        for (ResultadoParcela resultado : parcelasSoma) {
            BigDecimal total = BigDecimal.ZERO;
            String situacaoParaProcessamento = resultado.getSituacaoParaProcessamento(new Date());

            if (!totaisSituacao.containsKey(situacaoParaProcessamento)) {
                totaisSituacao.put(situacaoParaProcessamento, BigDecimal.ZERO);
            }
            if (resultado.isPago()) {
                total = totaisSituacao.get(situacaoParaProcessamento).add(resultado.getValorPago());
            } else {
                if ((!resultado.getVencido() && resultado.getCotaUnica()) || (!resultado.getCotaUnica()) || (resultado.getCotaUnica() && (resultado.isInscrito() || resultado.isCancelado()))) {
                    total = totaisSituacao.get(situacaoParaProcessamento).add(resultado.getValorTotal());
                }
            }
            totaisSituacao.put(situacaoParaProcessamento, total);
        }

        for (String situacao : totaisSituacao.keySet()) {
            TotalSituacao total = new TotalSituacao();
            total.setSituacao(situacao);
            total.setValor(totaisSituacao.get(situacao));
            totalPorSituacao.add(total);
        }
    }
}
