package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.nfse.domain.dtos.enums.SituacaoDeclaracaoNfseDTO;
import br.com.webpublico.nfse.enums.ModalidadeEmissao;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class NotaFiscalSearchDTO implements Serializable {

    private Long id;
    private Long idDeclaracaoPrestacaoServico;
    private Long idPrestador;
    private Long idTomador;
    private Long idRps;
    private String codigoRps;
    private Long numero;
    private Date emissao;
    private Date competencia;
    private String nomeTomador;
    private String nomePrestador;
    private String cpfCnpjTomador;
    private String cpfCnpjPrestador;
    private String situacao;
    private String modalidade;
    private Boolean issRetido;
    private BigDecimal totalServicos;
    private BigDecimal desconto;
    private BigDecimal deducoes;
    private BigDecimal aliquota;
    private BigDecimal valorLiquido;
    private BigDecimal baseCalculo;
    private BigDecimal iss;
    private BigDecimal issCalculado;
    private Boolean autorizarCancelamento;
    private String tipoDocumentoNfse;
    private String naturezaOperacao;
    private String discriminacaoServico;
    private String serieRps;
    private String tipoRps;
    private Boolean declarada;
    private String codigoVerificacao;

    public NotaFiscalSearchDTO() {
    }

    public NotaFiscalSearchDTO(NotaFiscalNfseDTO notaFiscal) {
        this.setId(notaFiscal.getId());
        this.setCodigoVerificacao(notaFiscal.getCodigoVerificacao());
        this.setNumero(notaFiscal.getNumero());
        this.setEmissao(notaFiscal.getEmissao());
        this.setNomeTomador(notaFiscal.getTomador().getDadosPessoais().getNomeRazaoSocial());
        this.setNomePrestador(notaFiscal.getPrestador().getPessoa().getDadosPessoais().getNomeRazaoSocial());
        this.setCpfCnpjTomador(notaFiscal.getTomador().getDadosPessoais().getCpfCnpj());
        this.setIdPrestador(notaFiscal.getPrestador().getId());
        this.setSituacao(notaFiscal.getSituacao().name());
        this.setModalidade(notaFiscal.getModalidade().name());
        this.setIssCalculado(notaFiscal.getIssCalculado());
        this.setDiscriminacaoServico(notaFiscal.getDescriminacaoServico());
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdDeclaracaoPrestacaoServico() {
        return idDeclaracaoPrestacaoServico;
    }

    public void setIdDeclaracaoPrestacaoServico(Long idDeclaracaoPrestacaoServico) {
        this.idDeclaracaoPrestacaoServico = idDeclaracaoPrestacaoServico;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public String getNomeTomador() {
        return nomeTomador;
    }

    public void setNomeTomador(String nomeTomador) {
        this.nomeTomador = nomeTomador;
    }

    public String getCpfCnpjTomador() {
        return cpfCnpjTomador;
    }

    public void setCpfCnpjTomador(String cpfCnpjTomador) {
        this.cpfCnpjTomador = cpfCnpjTomador;
    }

    public String getCpfCnpjNomeTomador() {
        if (ModalidadeEmissao.NAO_IDENTIFICADO.name().equals(modalidade)) {
            return "Não Identificado";
        } else if (ModalidadeEmissao.SEM_CPF.name().equals(modalidade)) {
            return nomeTomador;
        } else {
            return String.format("%s - %s", cpfCnpjTomador, nomeTomador);
        }
    }

    public Long getIdPrestador() {
        return idPrestador;
    }

    public void setIdPrestador(Long idPrestador) {
        this.idPrestador = idPrestador;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public SituacaoDeclaracaoNfseDTO getSituacaoEnum() {
        return SituacaoDeclaracaoNfseDTO.valueOf(situacao);
    }

    public String getNomePrestador() {
        return nomePrestador;
    }

    public void setNomePrestador(String nomePrestador) {
        this.nomePrestador = nomePrestador;
    }

    public String getCpfCnpjPrestador() {
        return cpfCnpjPrestador;
    }

    public void setCpfCnpjPrestador(String cpfCnpjPrestador) {
        this.cpfCnpjPrestador = cpfCnpjPrestador;
    }

    public String getCpfCnpjNomePrestador() {
        return String.format("%s - %s", cpfCnpjPrestador, nomePrestador);
    }

    public BigDecimal getTotalServicos() {
        return totalServicos;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getIss() {
        return iss;
    }

    public void setIss(BigDecimal iss) {
        this.iss = iss;
    }

    public BigDecimal getIssCalculado() {
        return issCalculado;
    }

    public void setIssCalculado(BigDecimal issCalculado) {
        this.issCalculado = issCalculado;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
    }

    public Boolean getIssRetido() {
        return issRetido;
    }

    public void setIssRetido(Boolean issRetido) {
        this.issRetido = issRetido;
    }

    public BigDecimal getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(BigDecimal deducoes) {
        this.deducoes = deducoes;
    }

    public Long getIdTomador() {
        return idTomador;
    }

    public void setIdTomador(Long idTomador) {
        this.idTomador = idTomador;
    }

    public Long getIdRps() {
        return idRps;
    }

    public void setIdRps(Long idRps) {
        this.idRps = idRps;
    }

    public String getCodigoRps() {
        return codigoRps;
    }

    public void setCodigoRps(String codigoRps) {
        this.codigoRps = codigoRps;
    }

    public Boolean getAutorizarCancelamento() {
        return autorizarCancelamento;
    }

    public void setAutorizarCancelamento(Boolean autorizarCancelamento) {
        this.autorizarCancelamento = autorizarCancelamento;
    }

    public String getTipoDocumentoNfse() {
        return tipoDocumentoNfse;
    }

    public void setTipoDocumentoNfse(String tipoDocumentoNfse) {
        this.tipoDocumentoNfse = tipoDocumentoNfse;
    }

    public String getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(String naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public String getDiscriminacaoServico() {
        return discriminacaoServico;
    }

    public void setDiscriminacaoServico(String discriminacaoServico) {
        this.discriminacaoServico = discriminacaoServico;
    }

    public String getSerieRps() {
        return serieRps;
    }

    public void setSerieRps(String serieRps) {
        this.serieRps = serieRps;
    }

    public String getTipoRps() {
        return tipoRps;
    }

    public void setTipoRps(String tipoRps) {
        this.tipoRps = tipoRps;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public Boolean getDeclarada() {
        return declarada;
    }

    public void setDeclarada(Boolean declarada) {
        this.declarada = declarada;
    }

    public void setCodigoVerificacao(String codigoVerificacao) {
        this.codigoVerificacao = codigoVerificacao;
    }

    public String getCodigoVerificacao() {
        return codigoVerificacao;
    }

    public static List<NotaFiscalSearchDTO> toListNotaFiscalSearchDTO(List<Object[]> resultList) {
        List<NotaFiscalSearchDTO> dtos = Lists.newArrayList();
        for (Object[] obj : resultList) {
            dtos.add(toNotaFiscalSearchDTO(obj));
        }
        return dtos;
    }

    public static NotaFiscalSearchDTO toNotaFiscalSearchDTO(Object[] obj) {
        NotaFiscalSearchDTO dto = new NotaFiscalSearchDTO();
        dto.setId(((Number) obj[0]).longValue());
        dto.setIdDeclaracaoPrestacaoServico(((Number) obj[1]).longValue());
        dto.setIdPrestador(obj[2] != null ? ((Number) obj[2]).longValue() : null);
        dto.setIdTomador(obj[3] != null ? ((Number) obj[3]).longValue() : null);
        dto.setIdRps(obj[4] != null ? ((Number) obj[4]).longValue() : null);
        dto.setCodigoRps((String) obj[5]);
        dto.setNumero(obj[6] != null ? ((Number) obj[6]).longValue() : null);
        dto.setEmissao(obj[7] != null ? ((Date) obj[7]) : null);
        dto.setCompetencia(obj[8] != null ? ((Date) obj[8]) : null);
        dto.setNomeTomador((String) obj[9]);
        dto.setNomePrestador((String) obj[10]);
        dto.setCpfCnpjTomador((String) obj[11]);
        dto.setCpfCnpjPrestador((String) obj[12]);
        dto.setSituacao((String) obj[13]);
        dto.setModalidade((String) obj[14]);
        dto.setIssRetido(((Number) obj[15]).intValue() == 1);
        dto.setTotalServicos((BigDecimal) obj[16]);
        dto.setDesconto((BigDecimal) obj[17]);
        dto.setDeducoes((BigDecimal) obj[18]);
        dto.setAliquota((BigDecimal) obj[19]);
        dto.setValorLiquido((BigDecimal) obj[20]);
        dto.setBaseCalculo((BigDecimal) obj[21]);
        dto.setIss((BigDecimal) obj[22]);
        dto.setIssCalculado((BigDecimal) obj[23]);
        dto.setTipoDocumentoNfse((String) obj[24]);
        dto.setNaturezaOperacao((String) obj[25]);
        dto.setDiscriminacaoServico((String) obj[26]);
        dto.setSerieRps((String) obj[27]);
        dto.setTipoRps((String) obj[28]);
        dto.setCodigoVerificacao((String) obj[29]);
        return dto;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotaFiscalSearchDTO that = (NotaFiscalSearchDTO) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(idDeclaracaoPrestacaoServico, that.idDeclaracaoPrestacaoServico) &&
            Objects.equals(idPrestador, that.idPrestador) &&
            Objects.equals(idTomador, that.idTomador) &&
            Objects.equals(idRps, that.idRps) &&
            Objects.equals(codigoRps, that.codigoRps) &&
            Objects.equals(numero, that.numero) &&
            Objects.equals(emissao, that.emissao) &&
            Objects.equals(competencia, that.competencia) &&
            Objects.equals(nomeTomador, that.nomeTomador) &&
            Objects.equals(nomePrestador, that.nomePrestador) &&
            Objects.equals(cpfCnpjTomador, that.cpfCnpjTomador) &&
            Objects.equals(cpfCnpjPrestador, that.cpfCnpjPrestador) &&
            Objects.equals(situacao, that.situacao) &&
            Objects.equals(modalidade, that.modalidade) &&
            Objects.equals(issRetido, that.issRetido) &&
            Objects.equals(totalServicos, that.totalServicos) &&
            Objects.equals(desconto, that.desconto) &&
            Objects.equals(deducoes, that.deducoes) &&
            Objects.equals(aliquota, that.aliquota) &&
            Objects.equals(valorLiquido, that.valorLiquido) &&
            Objects.equals(baseCalculo, that.baseCalculo) &&
            Objects.equals(iss, that.iss) &&
            Objects.equals(issCalculado, that.issCalculado) &&
            Objects.equals(autorizarCancelamento, that.autorizarCancelamento) &&
            Objects.equals(tipoDocumentoNfse, that.tipoDocumentoNfse) &&
            Objects.equals(naturezaOperacao, that.naturezaOperacao) &&
            Objects.equals(discriminacaoServico, that.discriminacaoServico) &&
            Objects.equals(serieRps, that.serieRps) &&
            Objects.equals(tipoRps, that.tipoRps) &&
            Objects.equals(codigoVerificacao, that.codigoVerificacao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idDeclaracaoPrestacaoServico, idPrestador, idTomador, idRps, codigoRps, numero,
            emissao, competencia, nomeTomador, nomePrestador, cpfCnpjTomador, cpfCnpjPrestador, situacao, modalidade,
            issRetido, totalServicos, desconto, deducoes, aliquota, valorLiquido, baseCalculo, iss, issCalculado,
            autorizarCancelamento, tipoDocumentoNfse, naturezaOperacao, discriminacaoServico, serieRps, tipoRps,
            codigoVerificacao);
    }

    @Override
    public String toString() {
        return "NotaFiscalSearchDTO{" +
            "id=" + id +
            ", idPrestador=" + idPrestador +
            ", numero=" + numero +
            ", emissao=" + emissao +
            ", nomeTomador='" + nomeTomador + '\'' +
            ", nomePrestador='" + nomePrestador + '\'' +
            ", cpfCnpjTomador='" + cpfCnpjTomador + '\'' +
            ", situacao='" + situacao + '\'' +
            '}';
    }
}
