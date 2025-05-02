package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.ModalidadeEmissao;
import br.com.webpublico.nfse.enums.SituacaoNota;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RelatorioNotasFiscaisDTO {

    private Long id;
    private String numero;
    private String numeroRps;
    private Date emissao;
    private String nomeRazaoSocialPrestador;
    private String cpfCnpjPrestador;
    private String nomeRazaoSocialTomador;
    private String cpfCnpjTomador;
    private SituacaoNota situacao;
    private Exigibilidade naturezaOperacao;
    private String modalidade;
    private String codigoServico;
    private String nomeServico;
    private String discriminacao;
    private Integer quantidade;
    private BigDecimal totalServicos;
    private BigDecimal totalDescontos;
    private BigDecimal totalDeducoes;
    private BigDecimal totalRetencoes;
    private BigDecimal baseCalculo;
    private BigDecimal aliquota;
    private Boolean issRetido;
    private BigDecimal issCalculado;
    private BigDecimal issPagar;
    private ClassificacaoAtividade classificacao;
    private Date dataEmissaoLote;
    private String numeroLote;
    private String contribuinteLote;
    private Date competencia;

    public RelatorioNotasFiscaisDTO() {
        quantidade = 0;
        totalServicos = BigDecimal.ZERO;
        totalDescontos = BigDecimal.ZERO;
        totalDeducoes = BigDecimal.ZERO;
        totalDescontos = BigDecimal.ZERO;
        totalRetencoes = BigDecimal.ZERO;
        baseCalculo = BigDecimal.ZERO;
        aliquota = BigDecimal.ZERO;
        issCalculado = BigDecimal.ZERO;
        issPagar = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNumeroRps() {
        return numeroRps;
    }

    public void setNumeroRps(String numeroRps) {
        this.numeroRps = numeroRps;
    }

    public Date getEmissao() {
        return emissao;
    }

    public void setEmissao(Date emissao) {
        this.emissao = emissao;
    }

    public String getNomeRazaoSocialPrestador() {
        return nomeRazaoSocialPrestador;
    }

    public void setNomeRazaoSocialPrestador(String nomeRazaoSocialPrestador) {
        this.nomeRazaoSocialPrestador = nomeRazaoSocialPrestador;
    }

    public String getCpfCnpjPrestador() {
        return cpfCnpjPrestador;
    }

    public void setCpfCnpjPrestador(String cpfCnpjPrestador) {
        this.cpfCnpjPrestador = cpfCnpjPrestador;
    }

    public String getNomeRazaoSocialTomador() {
        return nomeRazaoSocialTomador;
    }

    public void setNomeRazaoSocialTomador(String nomeRazaoSocialTomador) {
        this.nomeRazaoSocialTomador = nomeRazaoSocialTomador;
    }

    public String getCpfCnpjTomador() {
        return cpfCnpjTomador;
    }

    public void setCpfCnpjTomador(String cpfCnpjTomador) {
        this.cpfCnpjTomador = cpfCnpjTomador;
    }

    public SituacaoNota getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoNota situacao) {
        this.situacao = situacao;
    }

    public Exigibilidade getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(Exigibilidade naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public String getModalidade() {
        return modalidade;
    }

    public void setModalidade(String modalidade) {
        this.modalidade = modalidade;
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

    public String getDiscriminacao() {
        return discriminacao;
    }

    public void setDiscriminacao(String discriminacao) {
        this.discriminacao = discriminacao;
    }

    public Integer getQuantidade() {
        return quantidade != null ? quantidade : 0;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos != null ? totalServicos : BigDecimal.ZERO;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getTotalDescontos() {
        return totalDescontos != null ? totalDescontos : BigDecimal.ZERO;
    }

    public void setTotalDescontos(BigDecimal totalDescontos) {
        this.totalDescontos = totalDescontos;
    }

    public BigDecimal getTotalRetencoes() {
        return totalRetencoes != null ? totalRetencoes : BigDecimal.ZERO;
    }

    public void setTotalRetencoes(BigDecimal totalRetencoes) {
        this.totalRetencoes = totalRetencoes;
    }

    public BigDecimal getTotalDeducoes() {
        return totalDeducoes != null ? totalDeducoes : BigDecimal.ZERO;
    }

    public void setTotalDeducoes(BigDecimal totalDeducoes) {
        this.totalDeducoes = totalDeducoes;
    }

    public BigDecimal getTotalDescontosDeducoes() {
        BigDecimal totalDescontosDeducoes = BigDecimal.ZERO;
        if (totalDescontos != null)
            totalDescontosDeducoes = totalDescontosDeducoes.add(totalDescontos);
        if (totalDeducoes != null)
            totalDescontosDeducoes = totalDescontosDeducoes.add(totalDeducoes);
        return totalDescontosDeducoes;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo != null ? baseCalculo : BigDecimal.ZERO;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
    }

    public BigDecimal getAliquota() {
        return aliquota == null ? BigDecimal.ZERO : aliquota;
    }

    public BigDecimal getAliquotaDivididaPorCem() {
        if (getAliquota().compareTo(BigDecimal.ZERO) == 0) {
            return getAliquota();
        }
        return getAliquota().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public Boolean getIssRetido() {
        return issRetido != null ? issRetido : Boolean.FALSE;
    }

    public void setIssRetido(Boolean issRetido) {
        this.issRetido = issRetido;
    }

    public BigDecimal getIssCalculado() {
        return issCalculado != null ? issCalculado : BigDecimal.ZERO;
    }

    public void setIssCalculado(BigDecimal issCalculado) {
        this.issCalculado = issCalculado;
    }

    public BigDecimal getIssPagar() {
        return issPagar != null ? issPagar : BigDecimal.ZERO;
    }

    public void setIssPagar(BigDecimal issPagar) {
        this.issPagar = issPagar;
    }

    public ClassificacaoAtividade getClassificacao() {
        return classificacao;
    }

    public void setClassificacao(ClassificacaoAtividade classificacao) {
        this.classificacao = classificacao;
    }

    public Date getDataEmissaoLote() {
        return dataEmissaoLote;
    }

    public void setDataEmissaoLote(Date dataEmissaoLote) {
        this.dataEmissaoLote = dataEmissaoLote;
    }

    public String getNumeroLote() {
        return numeroLote;
    }

    public void setNumeroLote(String numeroLote) {
        this.numeroLote = numeroLote;
    }

    public String getContribuinteLote() {
        return contribuinteLote;
    }

    public void setContribuinteLote(String contribuinteLote) {
        this.contribuinteLote = contribuinteLote;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public static List<RelatorioNotasFiscaisDTO> popular(List<Object[]> lista) {
        List<RelatorioNotasFiscaisDTO> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            RelatorioNotasFiscaisDTO dto = new RelatorioNotasFiscaisDTO();
            popular(obj, dto);
            retorno.add(dto);
        }
        return retorno;
    }

    public static void popular(Object[] obj, RelatorioNotasFiscaisDTO dto) {
        AtomicInteger index = new AtomicInteger(0);
        dto.setId(((Number) obj[index.getAndIncrement()]).longValue());
        dto.setNumero(obj[index.get()] != null ? obj[index.get()].toString() : "");
        index.incrementAndGet();
        dto.setEmissao((Date) obj[index.getAndIncrement()]);
        dto.setNumeroRps((String) obj[index.getAndIncrement()]);
        dto.setNumeroLote((String) obj[index.getAndIncrement()]);
        dto.setDataEmissaoLote((Date) obj[index.getAndIncrement()]);
        dto.setNomeRazaoSocialPrestador((String) obj[index.getAndIncrement()]);
        dto.setCpfCnpjPrestador((String) obj[index.getAndIncrement()]);
        dto.setNomeRazaoSocialTomador((String) obj[index.getAndIncrement()]);
        dto.setCpfCnpjTomador((String) obj[index.getAndIncrement()]);
        dto.setSituacao(obj[index.get()] != null ? SituacaoNota.valueOf((String) obj[index.get()]) : null);
        index.getAndIncrement();
        dto.setModalidade(obj[index.get()] != null ? ModalidadeEmissao.valueOf((String) obj[index.get()]).getDescricao() : null);
        index.getAndIncrement();
        dto.setDiscriminacao((String) obj[index.getAndIncrement()]);
        dto.setIssRetido(obj[index.get()] != null && ((Number) obj[index.get()]).intValue() == 1);
        index.getAndIncrement();
        dto.setTotalServicos((BigDecimal) obj[index.getAndIncrement()]);
        dto.setTotalDeducoes((BigDecimal) obj[index.getAndIncrement()]);
        dto.setTotalDescontos((BigDecimal) obj[index.getAndIncrement()]);
        dto.setTotalRetencoes((BigDecimal) obj[index.getAndIncrement()]);
        dto.setBaseCalculo((BigDecimal) obj[index.getAndIncrement()]);
        dto.setQuantidade(((Number) obj[index.getAndIncrement()]).intValue());
        dto.setAliquota((BigDecimal) obj[index.getAndIncrement()]);
        dto.setIssCalculado((BigDecimal) obj[index.getAndIncrement()]);
        dto.setIssPagar((BigDecimal) obj[index.getAndIncrement()]);
        dto.setNaturezaOperacao(obj[index.get()] != null ? Exigibilidade.valueOf((String) obj[index.get()]) : null);
        index.getAndIncrement();
        dto.setCompetencia((Date) obj[index.getAndIncrement()]);
        dto.setClassificacao(obj[index.get()] != null ? ClassificacaoAtividade.valueOf((String) obj[index.get()]) : null);
        index.getAndIncrement();
        dto.setCodigoServico((String) obj[index.getAndIncrement()]);
        dto.setNomeServico((String) obj[index.getAndIncrement()]);
    }
}
