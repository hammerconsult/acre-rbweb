package br.com.webpublico.nfse.domain.dtos;

import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioServicosDeclaradosDetailDTO {

    private String codigoServico;
    private String nomeServico;
    private String descricao;
    private BigDecimal valorServico;
    private BigDecimal quantidade;
    private BigDecimal aliquota;
    private BigDecimal iss;
    private BigDecimal deducoes;
    private BigDecimal baseCalculo;


    public RelatorioServicosDeclaradosDetailDTO() {
    }

    public String getCodigoServico() {
        return codigoServico;
    }

    public void setCodigoServico(String codigoServico) {
        this.codigoServico = codigoServico;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorServico() {
        return valorServico;
    }

    public void setValorServico(BigDecimal valorServico) {
        this.valorServico = valorServico;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public static List<RelatorioServicosDeclaradosDetailDTO> popularDTO(List<Object[]> registros) {
        List<RelatorioServicosDeclaradosDetailDTO> dtos = Lists.newArrayList();
        if (registros != null && !registros.isEmpty()) {
            for (Object[] registro : registros) {
                RelatorioServicosDeclaradosDetailDTO dto = new RelatorioServicosDeclaradosDetailDTO();
                dto.setCodigoServico((String) registro[0]);
                dto.setNomeServico((String) registro[1]);
                dto.setDescricao((String) registro[2]);
                dto.setValorServico((BigDecimal) registro[3]);
                dto.setDeducoes((BigDecimal) registro[4]);
                dto.setBaseCalculo((BigDecimal) registro[5]);
                dto.setQuantidade((BigDecimal) registro[6]);
                dto.setAliquota((BigDecimal) registro[7]);
                dto.setIss((BigDecimal) registro[8]);
                dtos.add(dto);
            }
        }
        return dtos;
    }
}
