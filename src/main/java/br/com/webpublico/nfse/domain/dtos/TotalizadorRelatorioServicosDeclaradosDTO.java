package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TotalizadorRelatorioServicosDeclaradosDTO implements Comparable<TotalizadorRelatorioServicosDeclaradosDTO> {

    private Integer ano;
    private Integer mes;
    private String codigoServico;
    private String descricaoServico;
    private String naturezaOperacao;
    private Integer quantidadeNotas;
    private BigDecimal totalServicos;
    private BigDecimal totalRetencoes;
    private BigDecimal totalBaseCalculo;
    private BigDecimal totalIss;

    public TotalizadorRelatorioServicosDeclaradosDTO() {
        quantidadeNotas = 0;
        totalServicos = BigDecimal.ZERO;
        totalRetencoes = BigDecimal.ZERO;
        totalBaseCalculo = BigDecimal.ZERO;
        totalIss = BigDecimal.ZERO;
    }

    public static List<TotalizadorRelatorioServicosDeclaradosDTO> totalizarPorNatureza(List<RelatorioServicosDeclaradosDTO> servicosDeclaradosDTOS) {
        List<TotalizadorRelatorioServicosDeclaradosDTO> retorno = Lists.newArrayList();
        if (servicosDeclaradosDTOS != null && !servicosDeclaradosDTOS.isEmpty()) {
            for (RelatorioServicosDeclaradosDTO servicoDeclarado : servicosDeclaradosDTOS) {
                TotalizadorRelatorioServicosDeclaradosDTO dto = null;
                if (retorno != null && !retorno.isEmpty()) {
                    for (TotalizadorRelatorioServicosDeclaradosDTO totalizador : retorno) {
                        if (totalizador.getNaturezaOperacao().equals(servicoDeclarado.getNaturezaOperacao())) {
                            dto = totalizador;
                            break;
                        }
                    }
                }
                if (dto == null) {
                    dto = new TotalizadorRelatorioServicosDeclaradosDTO();
                    dto.setNaturezaOperacao(servicoDeclarado.getNaturezaOperacao());
                    retorno.add(dto);
                }
                dto.setQuantidadeNotas(dto.getQuantidadeNotas() + 1);
                dto.setTotalServicos(dto.getTotalServicos().add(servicoDeclarado.getTotalServicos()));
                dto.setTotalBaseCalculo(dto.getTotalBaseCalculo().add(servicoDeclarado.getBaseCalculo()));
                dto.setTotalRetencoes(dto.getTotalRetencoes().add(servicoDeclarado.getTotalDeducoes()));
                dto.setTotalIss(dto.getTotalIss().add(servicoDeclarado.getIssCalculo()));
            }
        }
        Collections.sort(retorno, new Comparator<TotalizadorRelatorioServicosDeclaradosDTO>() {
            @Override
            public int compare(TotalizadorRelatorioServicosDeclaradosDTO o1, TotalizadorRelatorioServicosDeclaradosDTO o2) {
                return o1.getNaturezaOperacao().compareTo(o2.getNaturezaOperacao());
            }
        });
        return retorno;
    }

    public static List<TotalizadorRelatorioServicosDeclaradosDTO> totalizarPorCompetenciaNatureza(List<RelatorioServicosDeclaradosDTO> servicosDeclaradosDTOS) {
        List<TotalizadorRelatorioServicosDeclaradosDTO> retorno = Lists.newArrayList();
        if (servicosDeclaradosDTOS != null && !servicosDeclaradosDTOS.isEmpty()) {
            for (RelatorioServicosDeclaradosDTO servicoDeclarado : servicosDeclaradosDTOS) {
                TotalizadorRelatorioServicosDeclaradosDTO dto = null;
                if (retorno != null && !retorno.isEmpty()) {
                    for (TotalizadorRelatorioServicosDeclaradosDTO totalizador : retorno) {
                        if (totalizador.getAno().equals(DataUtil.getAno(servicoDeclarado.getCompetencia())) &&
                            totalizador.getMes().equals(DataUtil.getMes(servicoDeclarado.getCompetencia())) &&
                            totalizador.getNaturezaOperacao().equals(servicoDeclarado.getNaturezaOperacao())) {
                            dto = totalizador;
                            break;
                        }
                    }
                }
                if (dto == null) {
                    dto = new TotalizadorRelatorioServicosDeclaradosDTO();
                    dto.setAno(DataUtil.getAno(servicoDeclarado.getCompetencia()));
                    dto.setMes(DataUtil.getMes(servicoDeclarado.getCompetencia()));
                    dto.setNaturezaOperacao(servicoDeclarado.getNaturezaOperacao());
                    retorno.add(dto);
                }
                dto.setQuantidadeNotas(dto.getQuantidadeNotas() + 1);
                dto.setTotalServicos(dto.getTotalServicos().add(servicoDeclarado.getTotalServicos()));
                dto.setTotalBaseCalculo(dto.getTotalBaseCalculo().add(servicoDeclarado.getBaseCalculo()));
                dto.setTotalRetencoes(dto.getTotalRetencoes().add(servicoDeclarado.getTotalDeducoes()));
                dto.setTotalIss(dto.getTotalIss().add(servicoDeclarado.getIssCalculo()));
            }
        }
        return retorno;
    }

    public static List<TotalizadorRelatorioServicosDeclaradosDTO> totalizarPorServicoNatureza(List<RelatorioServicosDeclaradosDTO> servicosDeclaradosDTOS) {
        List<TotalizadorRelatorioServicosDeclaradosDTO> retorno = Lists.newArrayList();
        if (servicosDeclaradosDTOS != null && !servicosDeclaradosDTOS.isEmpty()) {
            for (RelatorioServicosDeclaradosDTO servicoDeclaradoDTO : servicosDeclaradosDTOS) {
                for (RelatorioServicosDeclaradosDetailDTO item : servicoDeclaradoDTO.getItens()) {
                    TotalizadorRelatorioServicosDeclaradosDTO dto = null;
                    if (retorno != null && !retorno.isEmpty()) {
                        for (TotalizadorRelatorioServicosDeclaradosDTO totalizador : retorno) {
                            if (totalizador.getCodigoServico().equals(item.getCodigoServico()) &&
                                totalizador.getNaturezaOperacao().equals(servicoDeclaradoDTO.getNaturezaOperacao())) {
                                dto = totalizador;
                                break;
                            }
                        }
                    }
                    if (dto == null) {
                        dto = new TotalizadorRelatorioServicosDeclaradosDTO();
                        dto.setCodigoServico(item.getCodigoServico());
                        dto.setDescricaoServico(item.getNomeServico());
                        dto.setNaturezaOperacao(servicoDeclaradoDTO.getNaturezaOperacao());
                        retorno.add(dto);
                    }
                    dto.setQuantidadeNotas(dto.getQuantidadeNotas() + 1);
                    dto.setTotalServicos(dto.getTotalServicos().add(item.getValorServico()));
                    dto.setTotalBaseCalculo(dto.getTotalBaseCalculo().add(item.getBaseCalculo()));
                    dto.setTotalRetencoes(dto.getTotalRetencoes().add(item.getDeducoes()));
                    dto.setTotalIss(dto.getTotalIss().add(item.getIss()));
                }
            }
        }
        return retorno;
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

    public String getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(String naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public Integer getQuantidadeNotas() {
        return quantidadeNotas == null ? 0 : quantidadeNotas;
    }

    public void setQuantidadeNotas(Integer quantidadeNotas) {
        this.quantidadeNotas = quantidadeNotas;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos == null ? BigDecimal.ZERO : totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getTotalRetencoes() {
        return totalRetencoes == null ? BigDecimal.ZERO : totalRetencoes;
    }

    public void setTotalRetencoes(BigDecimal totalRetencoes) {
        this.totalRetencoes = totalRetencoes;
    }

    public BigDecimal getTotalBaseCalculo() {
        return totalBaseCalculo == null ? BigDecimal.ZERO : totalBaseCalculo;
    }

    public void setTotalBaseCalculo(BigDecimal totalBaseCalculo) {
        this.totalBaseCalculo = totalBaseCalculo;
    }

    public BigDecimal getTotalIss() {
        return totalIss == null ? BigDecimal.ZERO : totalIss;
    }

    public void setTotalIss(BigDecimal totalIss) {
        this.totalIss = totalIss;
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

    @Override
    public int compareTo(TotalizadorRelatorioServicosDeclaradosDTO o) {
        int i = getAno().compareTo(o.getAno());
        if (i == 0) {
            i = getMes().compareTo(o.getMes());
        }
        if (i == 0) {
            i = getNaturezaOperacao().compareTo(o.getNaturezaOperacao());
        }
        return i;
    }
}
