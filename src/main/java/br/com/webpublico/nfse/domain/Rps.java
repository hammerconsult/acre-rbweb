package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.RegimeTributario;
import br.com.webpublico.nfse.domain.dtos.ItemDeclaracaoServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.RpsNfseDTO;
import br.com.webpublico.nfse.enums.Exigibilidade;
import br.com.webpublico.nfse.enums.TipoRps;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Etiqueta(value = "RPS")
public class Rps extends SuperEntidade implements NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta(value = "Tipo")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoRps tipoRps;
    @Tabelavel
    @Etiqueta(value = "Número")
    @Pesquisavel
    private String numero;
    @Tabelavel
    @Etiqueta(value = "Série")
    @Pesquisavel
    private String serie;
    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Etiqueta(value = "Data")
    @Pesquisavel
    private Date dataEmissao;
    @ManyToOne
    @Tabelavel
    @Etiqueta(value = "Prestador")
    @Pesquisavel
    private CadastroEconomico prestador;
    @Tabelavel
    @Etiqueta(value = "Natureza da Operação")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private Exigibilidade naturezaOperacao;
    private Boolean optanteSimplesNacional;
    @Tabelavel
    @Etiqueta(value = "Competência")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date competencia;
    @Tabelavel
    @Etiqueta(value = "Dados do Tomador")
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL, optional=false)
    private DadosPessoaisNfse dadosPessoaisPrestador;
    @ManyToOne(cascade = CascadeType.ALL, optional=false)
    private DadosPessoaisNfse dadosPessoaisTomador;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "rps")
    private List<ItemRPS> itens;
    @ManyToOne(cascade = CascadeType.ALL)
    private TributosFederais tributosFederais;
    @Pesquisavel
    private Boolean issRetido;
    @Pesquisavel
    private Boolean incentivoFiscal;
    @Pesquisavel
    private BigDecimal totalServicos;
    @Pesquisavel
    private BigDecimal deducoesLegais;
    @Pesquisavel
    private BigDecimal descontosIncondicionais;
    @Pesquisavel
    private BigDecimal descontosCondicionais;
    @Pesquisavel
    private BigDecimal retencoesFederais;
    @Pesquisavel
    private BigDecimal baseCalculo;
    @Pesquisavel
    private BigDecimal issCalculado;
    @Pesquisavel
    private BigDecimal issPagar;
    @Pesquisavel
    private BigDecimal valorLiquido;
    @ManyToOne
    @Tabelavel
    @Etiqueta(value = "Lote RPS")
    @Pesquisavel
    private LoteRPS loteRPS;
    private String descriminacaoServico;
    @ManyToOne(cascade = CascadeType.ALL)
    private ConstrucaoCivil construcaoCivil;
    @Enumerated(EnumType.STRING)
    private RegimeTributario regimeTributario;
    private Boolean processado;


    public Rps() {
        this.itens = Lists.newArrayList();
        this.totalServicos = BigDecimal.ZERO;
        this.deducoesLegais = BigDecimal.ZERO;
        this.descontosIncondicionais = BigDecimal.ZERO;
        this.descontosCondicionais = BigDecimal.ZERO;
        this.baseCalculo = BigDecimal.ZERO;
        this.issCalculado = BigDecimal.ZERO;
        this.issPagar = BigDecimal.ZERO;
        this.valorLiquido = BigDecimal.ZERO;
        this.retencoesFederais = BigDecimal.ZERO;
        this.processado = Boolean.FALSE;
    }

    public Rps(Long id, String numero, String serie, Date dataEmissao, CadastroEconomico prestador) {
        this.id = id;
        this.numero = numero;
        this.serie = serie;
        this.dataEmissao = dataEmissao;
        this.prestador = prestador;
    }

    public Rps(String numero, String serie, Date dataEmissao, CadastroEconomico prestador, DeclaracaoPrestacaoServico declaracaoPrestacaoServico) {
        this.numero = numero;
        this.serie = serie;
        this.dataEmissao = dataEmissao;
        this.prestador = prestador;
        this.naturezaOperacao = declaracaoPrestacaoServico.getNaturezaOperacao();
        this.optanteSimplesNacional = declaracaoPrestacaoServico.getOptanteSimplesNacional();
        this.competencia = declaracaoPrestacaoServico.getCompetencia();
        if (declaracaoPrestacaoServico.getDadosPessoaisPrestador() != null)
            this.dadosPessoaisPrestador = declaracaoPrestacaoServico.getDadosPessoaisPrestador().clone();
        if (declaracaoPrestacaoServico.getDadosPessoaisTomador() != null)
            this.dadosPessoaisTomador = declaracaoPrestacaoServico.getDadosPessoaisTomador().clone();
        if (declaracaoPrestacaoServico.getTributosFederais() != null)
            this.tributosFederais = declaracaoPrestacaoServico.getTributosFederais().clone();
        this.issRetido = declaracaoPrestacaoServico.getIssRetido();
        this.totalServicos = declaracaoPrestacaoServico.getTotalServicos();
        this.deducoesLegais = declaracaoPrestacaoServico.getDeducoesLegais();
        this.descontosIncondicionais = declaracaoPrestacaoServico.getDescontosIncondicionais();
        this.descontosCondicionais = declaracaoPrestacaoServico.getDescontosCondicionais();
        this.retencoesFederais = declaracaoPrestacaoServico.getRetencoesFederais();
        this.baseCalculo = declaracaoPrestacaoServico.getBaseCalculo();
        this.issCalculado = declaracaoPrestacaoServico.getIssCalculado();
        this.issPagar = declaracaoPrestacaoServico.getIssPagar();
        this.valorLiquido = declaracaoPrestacaoServico.getValorLiquido();
        this.itens = Lists.newArrayList();
        for (ItemDeclaracaoServico itemDeclaracaoServico : declaracaoPrestacaoServico.getItens()) {
            this.itens.add(new ItemRPS(this, itemDeclaracaoServico));
        }
    }

    public static Rps toEntity(RpsNfseDTO dto) {
        return new Rps(dto.getId(), dto.getNumero(), dto.getSerie(), dto.getDataEmissao(), new CadastroEconomico(dto.getPrestador().getId()));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoRps getTipoRps() {
        return tipoRps;
    }

    public void setTipoRps(TipoRps tipoRps) {
        this.tipoRps = tipoRps;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public Date getDataEmissao() {
        return dataEmissao;
    }

    public void setDataEmissao(Date dataEmissao) {
        this.dataEmissao = dataEmissao;
    }

    public CadastroEconomico getPrestador() {
        return prestador;
    }

    public void setPrestador(CadastroEconomico prestador) {
        this.prestador = prestador;
    }

    public Exigibilidade getNaturezaOperacao() {
        return naturezaOperacao;
    }

    public void setNaturezaOperacao(Exigibilidade naturezaOperacao) {
        this.naturezaOperacao = naturezaOperacao;
    }

    public Boolean getOptanteSimplesNacional() {
        return optanteSimplesNacional != null ? optanteSimplesNacional : false;
    }

    public void setOptanteSimplesNacional(Boolean optanteSimplesNacional) {
        this.optanteSimplesNacional = optanteSimplesNacional;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
    }

    public DadosPessoaisNfse getDadosPessoaisPrestador() {
        return dadosPessoaisPrestador;
    }

    public void setDadosPessoaisPrestador(DadosPessoaisNfse dadosPessoaisPrestador) {
        this.dadosPessoaisPrestador = dadosPessoaisPrestador;
    }

    public DadosPessoaisNfse getDadosPessoaisTomador() {
        return dadosPessoaisTomador;
    }

    public void setDadosPessoaisTomador(DadosPessoaisNfse dadosPessoaisTomador) {
        this.dadosPessoaisTomador = dadosPessoaisTomador;
    }

    public List<ItemRPS> getItens() {
        return itens;
    }

    public void setItens(List<ItemRPS> itens) {
        this.itens = itens;
    }

    public TributosFederais getTributosFederais() {
        return tributosFederais;
    }

    public void setTributosFederais(TributosFederais tributosFederais) {
        this.tributosFederais = tributosFederais;
    }

    public Boolean getIssRetido() {
        return issRetido != null ? issRetido : false;
    }

    public void setIssRetido(Boolean issRetido) {
        this.issRetido = issRetido;
    }

    public BigDecimal getTotalServicos() {
        return totalServicos != null ? totalServicos : BigDecimal.ZERO;
    }

    public void setTotalServicos(BigDecimal totalServicos) {
        this.totalServicos = totalServicos;
    }

    public BigDecimal getDeducoesLegais() {
        return deducoesLegais != null ? deducoesLegais : BigDecimal.ZERO;
    }

    public void setDeducoesLegais(BigDecimal deducoesLegais) {
        this.deducoesLegais = deducoesLegais;
    }

    public BigDecimal getDescontosIncondicionais() {
        return descontosIncondicionais != null ? descontosIncondicionais : BigDecimal.ZERO;
    }

    public void setDescontosIncondicionais(BigDecimal descontosIncondicionais) {
        this.descontosIncondicionais = descontosIncondicionais;
    }

    public BigDecimal getDescontosCondicionais() {
        return descontosCondicionais != null ? descontosCondicionais : BigDecimal.ZERO;
    }

    public void setDescontosCondicionais(BigDecimal descontosCondicionais) {
        this.descontosCondicionais = descontosCondicionais;
    }

    public BigDecimal getRetencoesFederais() {
        return retencoesFederais != null ? retencoesFederais : BigDecimal.ZERO;
    }

    public void setRetencoesFederais(BigDecimal retencoesFederais) {
        this.retencoesFederais = retencoesFederais;
    }

    public BigDecimal getBaseCalculo() {
        return baseCalculo != null ? baseCalculo : BigDecimal.ZERO;
    }

    public void setBaseCalculo(BigDecimal baseCalculo) {
        this.baseCalculo = baseCalculo;
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

    public BigDecimal getValorLiquido() {
        return valorLiquido != null ? valorLiquido : BigDecimal.ZERO;
    }

    public void setValorLiquido(BigDecimal valorLiquido) {
        this.valorLiquido = valorLiquido;
    }

    public LoteRPS getLoteRPS() {
        return loteRPS;
    }

    public void setLoteRPS(LoteRPS loteRPS) {
        this.loteRPS = loteRPS;
    }

    public ConstrucaoCivil getConstrucaoCivil() {
        return construcaoCivil;
    }

    public void setConstrucaoCivil(ConstrucaoCivil construcaoCivil) {
        this.construcaoCivil = construcaoCivil;
    }

    public RegimeTributario getRegimeTributario() {
        return regimeTributario;
    }

    public void setRegimeTributario(RegimeTributario regimeTributario) {
        this.regimeTributario = regimeTributario;
    }

    @Override
    public String toString() {
        return "Rps{" +
            "id=" + id +
            ", numero='" + numero + '\'' +
            ", serie='" + serie + '\'' +
            ", dataEmissao=" + dataEmissao +
            ", prestador=" + prestador +
            '}';
    }

    @Override
    public RpsNfseDTO toNfseDto() {
        RpsNfseDTO dto = new RpsNfseDTO();
        dto.setId(this.id);
        if (this.tipoRps != null)
            dto.setTipoRps(this.tipoRps.toDto());
        dto.setNumero(numero);
        dto.setSerie(serie);
        if (dataEmissao != null)
            dto.setDataEmissao(DataUtil.dataSemHorario(dataEmissao));
        if (prestador != null)
            dto.setPrestador(prestador.toSimpleNfseDto());
        if (naturezaOperacao != null)
            dto.setNaturezaOperacao(naturezaOperacao.toDto());
        dto.setOptanteSimplesNacional(optanteSimplesNacional);
        if (competencia != null)
            dto.setCompetencia(DataUtil.dataSemHorario(competencia));
        if (dadosPessoaisPrestador != null)
            dto.setDadosPessoaisPrestador(dadosPessoaisPrestador.toNfseDto());
        if (dadosPessoaisTomador != null)
            dto.setDadosPessoaisTomador(dadosPessoaisTomador.toNfseDto());
        if (tributosFederais != null)
            dto.setTributosFederais(tributosFederais.toNfseDto());
        dto.setIssRetido(issRetido);
        dto.setTotalServicos(totalServicos);
        dto.setDeducoesLegais(deducoesLegais);
        dto.setDescontosIncondicionais(descontosIncondicionais);
        dto.setDescontosCondicionais(descontosCondicionais);
        dto.setRetencoesFederais(retencoesFederais);
        dto.setBaseCalculo(baseCalculo);
        dto.setIssCalculado(issCalculado);
        dto.setIssPagar(issPagar);
        dto.setValorLiquido(valorLiquido);
        dto.setItens(Lists.<ItemDeclaracaoServicoNfseDTO>newArrayList());
        for (ItemRPS item : this.getItens()) {
            dto.getItens().add(item.toNfseDto());
        }

        return dto;
    }

    public String getDescriminacaoServico() {
        return descriminacaoServico;
    }

    public void setDescriminacaoServico(String descriminacaoServico) {
        this.descriminacaoServico = descriminacaoServico;
    }

    public Boolean getIncentivoFiscal() {
        return incentivoFiscal;
    }

    public void setIncentivoFiscal(Boolean incentivoFiscal) {
        this.incentivoFiscal = incentivoFiscal;
    }

    public Boolean getProcessado() {
        return processado;
    }

    public void setProcessado(Boolean processado) {
        this.processado = processado;
    }
}
