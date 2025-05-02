package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.enums.SituacaoNota;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TotalizadorRelatorioNotasFiscaisDTO implements Comparable<TotalizadorRelatorioNotasFiscaisDTO> {

    private Integer ano;
    private Integer mes;
    private String codigoServico;
    private String descricaoServico;
    private String naturezaOperacao;
    private Integer quantidadeNotas;
    private BigDecimal totalServicos;
    private BigDecimal totalDescontos;
    private BigDecimal totalDeducoes;
    private BigDecimal totalRetencoes;
    private BigDecimal totalBaseCalculo;
    private BigDecimal aliquota;
    private BigDecimal totalIssCalculado;
    private BigDecimal totalIssPago;

    public TotalizadorRelatorioNotasFiscaisDTO() {
        quantidadeNotas = 0;
        totalServicos = BigDecimal.ZERO;
        totalDescontos = BigDecimal.ZERO;
        totalDeducoes = BigDecimal.ZERO;
        totalRetencoes = BigDecimal.ZERO;
        totalBaseCalculo = BigDecimal.ZERO;
        aliquota = BigDecimal.ZERO;
        totalIssCalculado = BigDecimal.ZERO;
        totalIssPago = BigDecimal.ZERO;
    }


    private static void totalizar(RelatorioNotasFiscaisDTO notaFiscal, TotalizadorRelatorioNotasFiscaisDTO dto) {
        dto.setQuantidadeNotas(dto.getQuantidadeNotas() + 1);
        dto.setTotalServicos(dto.getTotalServicos().add(notaFiscal.getTotalServicos()));
        dto.setTotalDescontos(dto.getTotalDescontos().add(notaFiscal.getTotalDescontos()));
        dto.setTotalDeducoes(dto.getTotalDeducoes().add(notaFiscal.getTotalDeducoes()));
        dto.setTotalRetencoes(dto.getTotalRetencoes().add(notaFiscal.getTotalRetencoes()));
        dto.setTotalBaseCalculo(dto.getTotalBaseCalculo().add(notaFiscal.getBaseCalculo()));
        dto.setAliquota(notaFiscal.getAliquota());
        dto.setTotalIssCalculado(dto.getTotalIssCalculado().add(notaFiscal.getIssCalculado()));
        if (SituacaoNota.PAGA.equals(notaFiscal.getSituacao()))
            dto.setTotalIssPago(dto.getTotalIssPago().add(notaFiscal.getIssCalculado()));
    }

    public static List<TotalizadorRelatorioNotasFiscaisDTO> totalizarPorNatureza(FiltroNotaFiscal filtroNotaFiscal, List<RelatorioNotasFiscaisDTO> notasFiscais) {
        List<TotalizadorRelatorioNotasFiscaisDTO> totalizadores = Lists.newArrayList();
        if (notasFiscais != null && !notasFiscais.isEmpty()) {
            for (RelatorioNotasFiscaisDTO notaFiscal : notasFiscais) {
                TotalizadorRelatorioNotasFiscaisDTO dto = null;
                if (totalizadores != null && !totalizadores.isEmpty()) {
                    for (TotalizadorRelatorioNotasFiscaisDTO totalizador : totalizadores) {
                        if (totalizador.getNaturezaOperacao().equals(notaFiscal.getNaturezaOperacao().getDescricao())) {
                            dto = totalizador;
                            break;
                        }
                    }
                }
                if (dto == null) {
                    dto = new TotalizadorRelatorioNotasFiscaisDTO();
                    dto.setNaturezaOperacao(notaFiscal.getNaturezaOperacao().getDescricao());
                    totalizadores.add(dto);
                }
                totalizar(notaFiscal, dto);
            }
        }
        Collections.sort(totalizadores, new Comparator<TotalizadorRelatorioNotasFiscaisDTO>() {
            @Override
            public int compare(TotalizadorRelatorioNotasFiscaisDTO o1, TotalizadorRelatorioNotasFiscaisDTO o2) {
                return o1.getNaturezaOperacao().compareTo(o2.getNaturezaOperacao());
            }
        });
        return totalizadores;
    }

    public static List<TotalizadorRelatorioNotasFiscaisDTO> totalizarPorCompetenciaNatureza(FiltroNotaFiscal filtroNotaFiscal,
                                                                                            List<RelatorioNotasFiscaisDTO> notasFiscais) {
        List<TotalizadorRelatorioNotasFiscaisDTO> totalizadores = Lists.newArrayList();
        if (notasFiscais != null && !notasFiscais.isEmpty()) {
            for (RelatorioNotasFiscaisDTO notaFiscal : notasFiscais) {
                TotalizadorRelatorioNotasFiscaisDTO dto = null;
                if (totalizadores != null && !totalizadores.isEmpty()) {
                    for (TotalizadorRelatorioNotasFiscaisDTO totalizador : totalizadores) {
                        if (totalizador.getAno().equals(DataUtil.getAno(notaFiscal.getCompetencia())) &&
                            totalizador.getMes().equals(DataUtil.getMes(notaFiscal.getCompetencia())) &&
                            totalizador.getNaturezaOperacao().equals(notaFiscal.getNaturezaOperacao().getDescricao())) {
                            dto = totalizador;
                            break;
                        }
                    }
                }
                if (dto == null) {
                    dto = new TotalizadorRelatorioNotasFiscaisDTO();
                    dto.setAno(DataUtil.getAno(notaFiscal.getCompetencia()));
                    dto.setMes(DataUtil.getMes(notaFiscal.getCompetencia()));
                    dto.setNaturezaOperacao(notaFiscal.getNaturezaOperacao().getDescricao());
                    totalizadores.add(dto);
                }
                totalizar(notaFiscal, dto);
            }
            Collections.sort(totalizadores, new Comparator<TotalizadorRelatorioNotasFiscaisDTO>() {
                @Override
                public int compare(TotalizadorRelatorioNotasFiscaisDTO o1, TotalizadorRelatorioNotasFiscaisDTO o2) {
                    int i = o2.getAno().compareTo(o1.getAno());
                    if (i != 0) return i;
                    i = o1.getMes().compareTo(o2.getMes());
                    if (i != 0) return i;
                    return o1.getNaturezaOperacao().compareTo(o2.getNaturezaOperacao());
                }
            });
        }
        return totalizadores;
    }

    public static List<TotalizadorRelatorioNotasFiscaisDTO> totalizarPorServicoNatureza(FiltroNotaFiscal filtroNotaFiscal,
                                                                                        List<RelatorioNotasFiscaisDTO> notasFiscais) {
        List<TotalizadorRelatorioNotasFiscaisDTO> totalizadores = Lists.newArrayList();
        if (notasFiscais != null && !notasFiscais.isEmpty()) {
            for (RelatorioNotasFiscaisDTO notaFiscal : notasFiscais) {
                TotalizadorRelatorioNotasFiscaisDTO dto = null;
                if (totalizadores != null && !totalizadores.isEmpty()) {
                    for (TotalizadorRelatorioNotasFiscaisDTO totalizador : totalizadores) {
                        if (totalizador.getCodigoServico().equals(notaFiscal.getCodigoServico()) &&
                            totalizador.getNaturezaOperacao().equals(notaFiscal.getNaturezaOperacao().getDescricao())) {
                            dto = totalizador;
                            break;
                        }
                    }
                }
                if (dto == null) {
                    dto = new TotalizadorRelatorioNotasFiscaisDTO();
                    dto.setCodigoServico(notaFiscal.getCodigoServico());
                    dto.setDescricaoServico(notaFiscal.getNomeServico());
                    dto.setNaturezaOperacao(notaFiscal.getNaturezaOperacao().getDescricao());
                    totalizadores.add(dto);
                }
                totalizar(notaFiscal, dto);
            }
        }
        return totalizadores;
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

    public String getCodigoServico() {
        return codigoServico;
    }

    public void setCodigoServico(String codigoServico) {
        this.codigoServico = codigoServico;
    }

    public String getDescricaoServico() {
        return descricaoServico;
    }

    public void setDescricaoServico(String descricaoServico) {
        this.descricaoServico = descricaoServico;
    }

    public String getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(String naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
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

    public BigDecimal getTotalBaseCalculo() {
        return totalBaseCalculo;
    }

    public void setTotalBaseCalculo(BigDecimal totalBaseCalculo) {
        this.totalBaseCalculo = totalBaseCalculo;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
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

    public BigDecimal getTotalRetencoes() {
        return totalRetencoes;
    }

    public void setTotalRetencoes(BigDecimal totalRetencoes) {
        this.totalRetencoes = totalRetencoes;
    }

    public BigDecimal getTotalIssCalculado() {
        return totalIssCalculado;
    }

    public void setTotalIssCalculado(BigDecimal totalIssCalculado) {
        this.totalIssCalculado = totalIssCalculado;
    }

    public BigDecimal getTotalIssPago() {
        return totalIssPago;
    }

    public void setTotalIssPago(BigDecimal totalIssPago) {
        this.totalIssPago = totalIssPago;
    }

    public String getGrupo(FiltroNotaFiscal.TipoAgrupamento tipoAgrupamento) {
        switch (tipoAgrupamento) {
            case COMPETENCIA_NATUREZA_OPERACAO: {
                return "Competência: " + ano + "/" + mes;
            }
            case SERVICO_NATUREZA_OPERACAO: {
                return "Serviço: " + codigoServico;
            }
            case NATUREZA_OPERACAO: {
                return "Natureza da Operação";
            }
        }
        return null;
    }


    @Override
    public int compareTo(TotalizadorRelatorioNotasFiscaisDTO o) {
        int i = getAno().compareTo(o.getAno());
        if (i == 0) {
            i = getMes().compareTo(o.getMes());
        }
        if (i == 0) {
            i = getNaturezaOperacao().compareTo(o.getNaturezaOperacao());
        }
        return i;
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

    public static List<TotalizadorRelatorioNotasFiscaisDTO> criarTotalizadorNotasFiscais(FiltroNotaFiscal filtroNotaFiscal,
                                                                                         List<RelatorioNotasFiscaisDTO> notasFiscais) {
        switch (filtroNotaFiscal.getTipoAgrupamento()) {
            case NATUREZA_OPERACAO:
                return TotalizadorRelatorioNotasFiscaisDTO.totalizarPorNatureza(filtroNotaFiscal, notasFiscais);
            case COMPETENCIA_NATUREZA_OPERACAO:
                return TotalizadorRelatorioNotasFiscaisDTO.totalizarPorCompetenciaNatureza(filtroNotaFiscal, notasFiscais);
            case SERVICO_NATUREZA_OPERACAO:
                return TotalizadorRelatorioNotasFiscaisDTO.totalizarPorServicoNatureza(filtroNotaFiscal, notasFiscais);
        }
        return Lists.newArrayList();
    }
}
