package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class TotalizadorCompetenciaRelatorioNotasFiscaisDTO implements Comparable<TotalizadorCompetenciaRelatorioNotasFiscaisDTO> {

    private Integer ano;
    private Integer mes;
    private Integer quantidadeNotas;
    private BigDecimal totalServicos;
    private BigDecimal totalDescontos;
    private BigDecimal totalDeducoes;
    private BigDecimal baseCalculoTotal;
    private BigDecimal aliquotaIssRecolher;
    private BigDecimal totalBaseCalculoIssRecolher;
    private BigDecimal totalIssRecolher;
    private BigDecimal totalIssRecolherPago;
    private BigDecimal aliquotaRetencaoIss;
    private BigDecimal totalBaseCalculoRetencaoIss;
    private BigDecimal totalRetencaoIss;
    private BigDecimal totalRetencaoIssPago;
    private BigDecimal aliquotaRetencaoSimples;
    private BigDecimal totalBaseCalculoRetencaoSimples;
    private BigDecimal totalRetencaoSimples;
    private BigDecimal totalRetencaoSimplesPago;
    private BigDecimal aliquotaSimplesNacional;
    private BigDecimal totalBaseCalculoSimplesNacional;
    private BigDecimal totalSimplesNacional;
    private BigDecimal aliquotaForaMunicipio;
    private BigDecimal totalBaseCalculoForaMunicipio;
    private BigDecimal totalForaMunicipio;

    public TotalizadorCompetenciaRelatorioNotasFiscaisDTO() {
        quantidadeNotas = 0;
        totalServicos = BigDecimal.ZERO;
        totalDescontos = BigDecimal.ZERO;
        totalDeducoes = BigDecimal.ZERO;
        baseCalculoTotal = BigDecimal.ZERO;
        aliquotaIssRecolher = BigDecimal.ZERO;
        totalBaseCalculoIssRecolher = BigDecimal.ZERO;
        totalIssRecolher = BigDecimal.ZERO;
        totalIssRecolherPago = BigDecimal.ZERO;
        aliquotaRetencaoIss = BigDecimal.ZERO;
        totalBaseCalculoRetencaoIss = BigDecimal.ZERO;
        totalRetencaoIss = BigDecimal.ZERO;
        totalRetencaoIssPago = BigDecimal.ZERO;
        aliquotaRetencaoSimples = BigDecimal.ZERO;
        totalBaseCalculoRetencaoSimples = BigDecimal.ZERO;
        totalRetencaoSimples = BigDecimal.ZERO;
        totalRetencaoSimplesPago = BigDecimal.ZERO;
        aliquotaSimplesNacional = BigDecimal.ZERO;
        totalBaseCalculoSimplesNacional = BigDecimal.ZERO;
        totalSimplesNacional = BigDecimal.ZERO;
        aliquotaForaMunicipio = BigDecimal.ZERO;
        totalBaseCalculoForaMunicipio = BigDecimal.ZERO;
        totalForaMunicipio = BigDecimal.ZERO;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getQuantidadeNotas() {
        return quantidadeNotas;
    }

    public void setQuantidadeNotas(Integer quantidadeNotas) {
        this.quantidadeNotas = quantidadeNotas;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getTotalDescontos() {
        return totalDescontos;
    }

    public void setTotalDescontos(BigDecimal totalDescontos) {
        this.totalDescontos = totalDescontos;
    }

    public BigDecimal getTotalDeducoes() {
        return totalDeducoes;
    }

    public void setTotalDeducoes(BigDecimal totalDeducoes) {
        this.totalDeducoes = totalDeducoes;
    }

    public BigDecimal getBaseCalculoTotal() {
        return baseCalculoTotal;
    }

    public void setBaseCalculoTotal(BigDecimal baseCalculoTotal) {
        this.baseCalculoTotal = baseCalculoTotal;
    }

    public BigDecimal getAliquotaIssRecolher() {
        return aliquotaIssRecolher;
    }

    public void setAliquotaIssRecolher(BigDecimal aliquotaIssRecolher) {
        this.aliquotaIssRecolher = aliquotaIssRecolher;
    }

    public BigDecimal getTotalBaseCalculoIssRecolher() {
        return totalBaseCalculoIssRecolher;
    }

    public void setTotalBaseCalculoIssRecolher(BigDecimal totalBaseCalculoIssRecolher) {
        this.totalBaseCalculoIssRecolher = totalBaseCalculoIssRecolher;
    }

    public BigDecimal getTotalIssRecolher() {
        return totalIssRecolher;
    }

    public void setTotalIssRecolher(BigDecimal totalIssRecolher) {
        this.totalIssRecolher = totalIssRecolher;
    }

    public BigDecimal getTotalIssRecolherPago() {
        return totalIssRecolherPago;
    }

    public void setTotalIssRecolherPago(BigDecimal totalIssRecolherPago) {
        this.totalIssRecolherPago = totalIssRecolherPago;
    }

    public BigDecimal getAliquotaRetencaoIss() {
        return aliquotaRetencaoIss;
    }

    public void setAliquotaRetencaoIss(BigDecimal aliquotaRetencaoIss) {
        this.aliquotaRetencaoIss = aliquotaRetencaoIss;
    }

    public BigDecimal getTotalBaseCalculoRetencaoIss() {
        return totalBaseCalculoRetencaoIss;
    }

    public void setTotalBaseCalculoRetencaoIss(BigDecimal totalBaseCalculoRetencaoIss) {
        this.totalBaseCalculoRetencaoIss = totalBaseCalculoRetencaoIss;
    }

    public BigDecimal getTotalRetencaoIss() {
        return totalRetencaoIss;
    }

    public void setTotalRetencaoIss(BigDecimal totalRetencaoIss) {
        this.totalRetencaoIss = totalRetencaoIss;
    }

    public BigDecimal getTotalRetencaoIssPago() {
        return totalRetencaoIssPago;
    }

    public void setTotalRetencaoIssPago(BigDecimal totalRetencaoIssPago) {
        this.totalRetencaoIssPago = totalRetencaoIssPago;
    }

    public BigDecimal getAliquotaRetencaoSimples() {
        return aliquotaRetencaoSimples;
    }

    public void setAliquotaRetencaoSimples(BigDecimal aliquotaRetencaoSimples) {
        this.aliquotaRetencaoSimples = aliquotaRetencaoSimples;
    }

    public BigDecimal getTotalBaseCalculoRetencaoSimples() {
        return totalBaseCalculoRetencaoSimples;
    }

    public void setTotalBaseCalculoRetencaoSimples(BigDecimal totalBaseCalculoRetencaoSimples) {
        this.totalBaseCalculoRetencaoSimples = totalBaseCalculoRetencaoSimples;
    }

    public BigDecimal getTotalRetencaoSimples() {
        return totalRetencaoSimples;
    }

    public void setTotalRetencaoSimples(BigDecimal totalRetencaoSimples) {
        this.totalRetencaoSimples = totalRetencaoSimples;
    }

    public BigDecimal getTotalRetencaoSimplesPago() {
        return totalRetencaoSimplesPago;
    }

    public void setTotalRetencaoSimplesPago(BigDecimal totalRetencaoSimplesPago) {
        this.totalRetencaoSimplesPago = totalRetencaoSimplesPago;
    }

    public BigDecimal getAliquotaSimplesNacional() {
        return aliquotaSimplesNacional;
    }

    public void setAliquotaSimplesNacional(BigDecimal aliquotaSimplesNacional) {
        this.aliquotaSimplesNacional = aliquotaSimplesNacional;
    }

    public BigDecimal getTotalBaseCalculoSimplesNacional() {
        return totalBaseCalculoSimplesNacional;
    }

    public void setTotalBaseCalculoSimplesNacional(BigDecimal totalBaseCalculoSimplesNacional) {
        this.totalBaseCalculoSimplesNacional = totalBaseCalculoSimplesNacional;
    }

    public BigDecimal getTotalSimplesNacional() {
        return totalSimplesNacional;
    }

    public void setTotalSimplesNacional(BigDecimal totalSimplesNacional) {
        this.totalSimplesNacional = totalSimplesNacional;
    }

    public BigDecimal getAliquotaForaMunicipio() {
        return aliquotaForaMunicipio;
    }

    public void setAliquotaForaMunicipio(BigDecimal aliquotaForaMunicipio) {
        this.aliquotaForaMunicipio = aliquotaForaMunicipio;
    }

    public BigDecimal getTotalBaseCalculoForaMunicipio() {
        return totalBaseCalculoForaMunicipio;
    }

    public void setTotalBaseCalculoForaMunicipio(BigDecimal totalBaseCalculoForaMunicipio) {
        this.totalBaseCalculoForaMunicipio = totalBaseCalculoForaMunicipio;
    }

    public BigDecimal getTotalForaMunicipio() {
        return totalForaMunicipio;
    }

    public void setTotalForaMunicipio(BigDecimal totalForaMunicipio) {
        this.totalForaMunicipio = totalForaMunicipio;
    }

    public BigDecimal getTotalDescontosDeducoes() {
        BigDecimal totalDescontosDeducoes = BigDecimal.ZERO;
        if (totalDescontos != null) {
            totalDescontosDeducoes = totalDescontosDeducoes.add(totalDescontos);
        }
        if (totalDeducoes != null) {
            totalDescontosDeducoes = totalDescontosDeducoes.add(totalDeducoes);
        }
        return totalDescontosDeducoes;
    }

    public static List<TotalizadorCompetenciaRelatorioNotasFiscaisDTO> totalizar(FiltroNotaFiscal filtroNotaFiscal,
                                                                                 List<RelatorioNotasFiscaisDTO> notasFiscais) {
        Calendar periodoInicial = filtroNotaFiscal.getPeriodoInicial();
        Calendar periodoFinal = filtroNotaFiscal.getPeriodoFinal();
        List<TotalizadorCompetenciaRelatorioNotasFiscaisDTO> totalizadores = Lists.newArrayList();
        while (periodoInicial.before(periodoFinal)) {
            TotalizadorCompetenciaRelatorioNotasFiscaisDTO totalizador = new TotalizadorCompetenciaRelatorioNotasFiscaisDTO();
            totalizador.setAno(periodoInicial.get(Calendar.YEAR));
            totalizador.setMes(periodoInicial.get(Calendar.MONTH) + 1);
            totalizadores.add(totalizador);
            periodoInicial.add(Calendar.MONTH, 1);
        }
        for (RelatorioNotasFiscaisDTO notaFiscal : notasFiscais) {
            TotalizadorCompetenciaRelatorioNotasFiscaisDTO totalizador =
                TotalizadorCompetenciaRelatorioNotasFiscaisDTO.getTotalizador(totalizadores,
                    DataUtil.getAno(notaFiscal.getCompetencia()), DataUtil.getMes(notaFiscal.getCompetencia()));
            TotalizadorCompetenciaRelatorioNotasFiscaisDTO.add(notaFiscal, totalizador);
        }
        Collections.sort(totalizadores);
        return totalizadores;
    }

    public static void add(RelatorioNotasFiscaisDTO notaFiscal, TotalizadorCompetenciaRelatorioNotasFiscaisDTO dto) {
        switch (notaFiscal.getNaturezaOperacao()) {
            case TRIBUTACAO_MUNICIPAL: {
                dto.setQuantidadeNotas(dto.getQuantidadeNotas() + 1);
                dto.setTotalServicos(dto.getTotalServicos().add(notaFiscal.getTotalServicos()));
                dto.setTotalDescontos(dto.getTotalDescontos().add(notaFiscal.getTotalDescontos()));
                dto.setTotalDeducoes(dto.getTotalDeducoes().add(notaFiscal.getTotalDeducoes()));
                dto.setBaseCalculoTotal(dto.getBaseCalculoTotal().add(notaFiscal.getBaseCalculo()));
                dto.setTotalBaseCalculoIssRecolher(dto.getTotalBaseCalculoIssRecolher().add(notaFiscal.getBaseCalculo()));
                dto.setTotalIssRecolher(dto.getTotalIssRecolher().add(notaFiscal.getIssCalculado()));
                dto.setAliquotaIssRecolher(calcularAliquota(dto.getTotalBaseCalculoIssRecolher(),
                    dto.getTotalIssRecolher()));
                if (SituacaoNota.PAGA.equals(notaFiscal.getSituacao())) {
                    dto.setTotalIssRecolherPago(dto.getTotalIssRecolherPago().add(notaFiscal.getIssCalculado()));
                }
                break;
            }
            case RETENCAO: {
                dto.setQuantidadeNotas(dto.getQuantidadeNotas() + 1);
                dto.setTotalServicos(dto.getTotalServicos().add(notaFiscal.getTotalServicos()));
                dto.setTotalDescontos(dto.getTotalDescontos().add(notaFiscal.getTotalDescontos()));
                dto.setTotalDeducoes(dto.getTotalDeducoes().add(notaFiscal.getTotalDeducoes()));
                dto.setBaseCalculoTotal(dto.getBaseCalculoTotal().add(notaFiscal.getBaseCalculo()));
                dto.setTotalBaseCalculoRetencaoIss(dto.getTotalBaseCalculoRetencaoIss().add(notaFiscal.getBaseCalculo()));
                dto.setTotalRetencaoIss(dto.getTotalRetencaoIss().add(notaFiscal.getIssCalculado()));
                dto.setAliquotaRetencaoIss(calcularAliquota(dto.getTotalBaseCalculoRetencaoIss(),
                    dto.getTotalRetencaoIss()));
                if (SituacaoNota.PAGA.equals(notaFiscal.getSituacao())) {
                    dto.setTotalRetencaoIssPago(dto.getTotalRetencaoIssPago().add(notaFiscal.getIssCalculado()));
                }
                break;
            }
            case RETENCAO_SIMPLES_NACIONAL: {
                dto.setQuantidadeNotas(dto.getQuantidadeNotas() + 1);
                dto.setTotalServicos(dto.getTotalServicos().add(notaFiscal.getTotalServicos()));
                dto.setTotalDescontos(dto.getTotalDescontos().add(notaFiscal.getTotalDescontos()));
                dto.setTotalDeducoes(dto.getTotalDeducoes().add(notaFiscal.getTotalDeducoes()));
                dto.setBaseCalculoTotal(dto.getBaseCalculoTotal().add(notaFiscal.getBaseCalculo()));
                dto.setTotalBaseCalculoRetencaoSimples(dto.getTotalBaseCalculoRetencaoSimples().add(notaFiscal.getBaseCalculo()));
                dto.setTotalRetencaoSimples(dto.getTotalRetencaoSimples().add(notaFiscal.getIssCalculado()));
                dto.setAliquotaRetencaoSimples(calcularAliquota(dto.getTotalBaseCalculoRetencaoSimples(),
                    dto.getTotalRetencaoSimples()));
                if (SituacaoNota.PAGA.equals(notaFiscal.getSituacao())) {
                    dto.setTotalRetencaoSimplesPago(dto.getTotalRetencaoSimplesPago().add(notaFiscal.getIssCalculado()));
                }
                break;
            }
            case SIMPLES_NACIONAL: {
                dto.setQuantidadeNotas(dto.getQuantidadeNotas() + 1);
                dto.setTotalServicos(dto.getTotalServicos().add(notaFiscal.getTotalServicos()));
                dto.setTotalDescontos(dto.getTotalDescontos().add(notaFiscal.getTotalDescontos()));
                dto.setTotalDeducoes(dto.getTotalDeducoes().add(notaFiscal.getTotalDeducoes()));
                dto.setBaseCalculoTotal(dto.getBaseCalculoTotal().add(notaFiscal.getBaseCalculo()));
                dto.setTotalBaseCalculoSimplesNacional(dto.getTotalBaseCalculoSimplesNacional().add(notaFiscal.getBaseCalculo()));
                dto.setTotalSimplesNacional(dto.getTotalSimplesNacional().add(notaFiscal.getIssCalculado()));
                dto.setAliquotaSimplesNacional(calcularAliquota(dto.getTotalBaseCalculoSimplesNacional(),
                    dto.getTotalSimplesNacional()));
                break;
            }
            case TRIBUTACAO_FORA_MUNICIPIO: {
                dto.setQuantidadeNotas(dto.getQuantidadeNotas() + 1);
                dto.setTotalServicos(dto.getTotalServicos().add(notaFiscal.getTotalServicos()));
                dto.setTotalDescontos(dto.getTotalDescontos().add(notaFiscal.getTotalDescontos()));
                dto.setTotalDeducoes(dto.getTotalDeducoes().add(notaFiscal.getTotalDeducoes()));
                dto.setTotalBaseCalculoForaMunicipio(dto.getTotalBaseCalculoForaMunicipio().add(notaFiscal.getBaseCalculo()));
                dto.setTotalForaMunicipio(dto.getTotalForaMunicipio().add(notaFiscal.getIssCalculado()));
                dto.setAliquotaForaMunicipio(calcularAliquota(dto.getTotalBaseCalculoForaMunicipio(),
                    dto.getTotalForaMunicipio()));
                break;
            }
        }
    }

    public static BigDecimal calcularAliquota(BigDecimal valorTotal, BigDecimal valorCalculado) {
        if (valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) > 0 &&
            valorCalculado != null && valorCalculado.compareTo(BigDecimal.ZERO) > 0) {
            return valorCalculado
                .divide(valorTotal, 8, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);
        }
        return BigDecimal.ZERO;
    }

    public void somar(TotalizadorCompetenciaRelatorioNotasFiscaisDTO dto) {
        this.setQuantidadeNotas(this.getQuantidadeNotas() + dto.getQuantidadeNotas());
        this.setTotalServicos(this.getTotalServicos().add(dto.getTotalServicos()));
        this.setTotalDescontos(this.getTotalDescontos().add(dto.getTotalDescontos()));
        this.setTotalDeducoes(this.getTotalDeducoes().add(dto.getTotalDeducoes()));
        this.setTotalDeducoes(this.getTotalDeducoes().add(dto.getTotalDeducoes()));
        this.setBaseCalculoTotal(this.getBaseCalculoTotal().add(dto.getBaseCalculoTotal()));
        this.setAliquotaIssRecolher(dto.getAliquotaIssRecolher());
        this.setTotalBaseCalculoIssRecolher(this.getTotalBaseCalculoIssRecolher().add(dto.getTotalBaseCalculoIssRecolher()));
        this.setTotalIssRecolher(this.getTotalIssRecolher().add(dto.getTotalIssRecolher()));
        this.setTotalIssRecolherPago(this.getTotalIssRecolherPago().add(dto.getTotalIssRecolherPago()));
        this.setAliquotaRetencaoIss(dto.getAliquotaRetencaoIss());
        this.setTotalBaseCalculoRetencaoIss(this.getTotalBaseCalculoRetencaoIss().add(dto.getTotalBaseCalculoRetencaoIss()));
        this.setTotalRetencaoIss(this.getTotalRetencaoIss().add(dto.getTotalRetencaoIss()));
        this.setTotalRetencaoIssPago(this.getTotalRetencaoIssPago().add(dto.getTotalRetencaoIssPago()));
        this.setAliquotaSimplesNacional(dto.getAliquotaSimplesNacional());
        this.setTotalBaseCalculoSimplesNacional(this.getTotalBaseCalculoSimplesNacional().add(dto.getTotalBaseCalculoSimplesNacional()));
        this.setTotalSimplesNacional(this.getTotalSimplesNacional().add(dto.getTotalSimplesNacional()));
        this.setAliquotaRetencaoSimples(dto.getAliquotaRetencaoSimples());
        this.setTotalBaseCalculoRetencaoSimples(this.getTotalBaseCalculoRetencaoSimples().add(dto.getTotalBaseCalculoRetencaoSimples()));
        this.setTotalRetencaoSimples(this.getTotalRetencaoSimples().add(dto.getTotalRetencaoSimples()));
        this.setTotalRetencaoSimplesPago(this.getTotalRetencaoSimplesPago().add(dto.getTotalRetencaoSimplesPago()));
        this.setAliquotaForaMunicipio(dto.getAliquotaForaMunicipio());
        this.setTotalBaseCalculoForaMunicipio(this.getTotalBaseCalculoForaMunicipio().add(dto.getTotalBaseCalculoForaMunicipio()));
        this.setTotalForaMunicipio(this.getTotalForaMunicipio().add(dto.getTotalForaMunicipio()));
    }

    public static TotalizadorCompetenciaRelatorioNotasFiscaisDTO getTotalizador(List<TotalizadorCompetenciaRelatorioNotasFiscaisDTO> totalizadores,
                                                                                Integer ano, Integer mes) {
        TotalizadorCompetenciaRelatorioNotasFiscaisDTO retorno = null;
        for (TotalizadorCompetenciaRelatorioNotasFiscaisDTO totalizador : totalizadores) {
            if (totalizador.getAno().compareTo(ano) == 0 && totalizador.getMes().compareTo(mes) == 0) {
                retorno = totalizador;
                break;
            }
        }
        if (retorno == null) {
            retorno = new TotalizadorCompetenciaRelatorioNotasFiscaisDTO();
            retorno.setAno(ano);
            retorno.setMes(mes);
            totalizadores.add(retorno);
        }
        return retorno;
    }


    @Override
    public int compareTo(TotalizadorCompetenciaRelatorioNotasFiscaisDTO o) {
        int i = o.getAno().compareTo(getAno());
        if (i == 0) {
            i = getMes().compareTo(o.getMes());
        }
        return i;
    }


}
