/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoSolicitacao;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.tributario.SolicitacaoDocumentoOficialDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Certidao")
@Etiqueta("Solicitação de Documento Oficial")
public class SolicitacaoDoctoOficial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigo;
    @Transient
    @Tabelavel
    @Etiqueta("Código")
    private String codigoComSequencia;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Data")
    private Date dataSolicitacao;
    @ManyToOne(fetch = FetchType.LAZY)
    private CadastroImobiliario cadastroImobiliario;
    @ManyToOne(fetch = FetchType.LAZY)
    private CadastroEconomico cadastroEconomico;
    @ManyToOne(fetch = FetchType.LAZY)
    private CadastroRural cadastroRural;
    @ManyToOne(fetch = FetchType.LAZY)
    private PessoaFisica pessoaFisica;
    @ManyToOne(fetch = FetchType.LAZY)
    private PessoaJuridica pessoaJuridica;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Documento")
    private TipoDoctoOficial tipoDoctoOficial;
    @ManyToOne
    private Finalidade finalidade;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "solicitacaoDoctoOficial")
    private List<ValorAtributoSolicitacao> valoresAtributosSolicitacao;
    @Etiqueta("N° Documento Oficial")
    @Tabelavel
    @OneToOne(cascade = CascadeType.ALL)
    private DocumentoOficial documentoOficial;
    private Boolean emitido;
    private BigDecimal valorNormalVencido;
    private BigDecimal valorNormalVencer;
    private BigDecimal valorParcelamentoVencido;
    private BigDecimal valorParcelamentoVencer;
    private BigDecimal valorNormalVencidoAcres;
    private BigDecimal valorParcelamentoVencidoAcres;
    private String observacao;
    @Transient
    private Long criadoEm;
    @Transient
    @Tabelavel
    @Etiqueta("Tipo de Cadastro")
    private TipoCadastroTributario tipoCadastro;
    @Transient
    @Tabelavel
    @Etiqueta("Cadastro")
    private String cadastroTabelavel;
    @Transient
    @Tabelavel
    @Etiqueta("Contribuinte")
    private String nomeCadastroTabelavel;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "solicitacaoDoctoOficial")
    private List<ImpressaoDoctoOficial> impressaoDoctoOficial;
    @Transient
    @Monetario
    @Tabelavel
    @Etiqueta("Valor Emissão")
    private BigDecimal valorEmissaoTabelavel;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Situação")
    private SituacaoSolicitacao situacaoSolicitacao;
    @Temporal(TemporalType.DATE)
    private Date dataCancelamento;
    private String motivoCancelamento;
    private Long sequencia;


    public SolicitacaoDoctoOficial() {
        criadoEm = System.nanoTime();
        this.valoresAtributosSolicitacao = new ArrayList<ValorAtributoSolicitacao>();
        this.impressaoDoctoOficial = new ArrayList<>();
        this.valorNormalVencido = new BigDecimal(BigInteger.ZERO);
        this.valorNormalVencer = new BigDecimal(BigInteger.ZERO);
        this.valorParcelamentoVencido = new BigDecimal(BigInteger.ZERO);
        this.valorParcelamentoVencer = new BigDecimal(BigInteger.ZERO);
        this.valorNormalVencidoAcres = new BigDecimal(BigInteger.ZERO);
        this.valorParcelamentoVencidoAcres = new BigDecimal(BigInteger.ZERO);
        this.sequencia = 0L;
        this.situacaoSolicitacao = SituacaoSolicitacao.ABERTO;
    }

    public SituacaoSolicitacao getSituacaoSolicitacao() {
        return situacaoSolicitacao;
    }

    public void setSituacaoSolicitacao(SituacaoSolicitacao situacaoSolicitacao) {
        this.situacaoSolicitacao = situacaoSolicitacao;
    }

    public Boolean getEmitido() {
        return emitido;
    }

    public void setEmitido(Boolean emitido) {
        this.emitido = emitido;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public CadastroRural getCadastroRural() {
        return cadastroRural;
    }

    public void setCadastroRural(CadastroRural cadastroRural) {
        this.cadastroRural = cadastroRural;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Finalidade getFinalidade() {
        return finalidade;
    }

    public void setFinalidade(Finalidade finalidade) {
        this.finalidade = finalidade;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ImpressaoDoctoOficial> getImpressaoDoctoOficial() {
        return impressaoDoctoOficial;
    }

    public void setImpressaoDoctoOficial(List<ImpressaoDoctoOficial> impressaoDoctoOficial) {
        this.impressaoDoctoOficial = impressaoDoctoOficial;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public DocumentoOficial getDocumentoOficial() {
        return documentoOficial;
    }

    public void setDocumentoOficial(DocumentoOficial documentoOficial) {
        this.documentoOficial = documentoOficial;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }

    public List<ValorAtributoSolicitacao> getValoresAtributosSolicitacao() {
        return valoresAtributosSolicitacao;
    }

    public void setValoresAtributosSolicitacao(List<ValorAtributoSolicitacao> valoresAtributosSolicitacao) {
        this.valoresAtributosSolicitacao = valoresAtributosSolicitacao;
    }


    public void setTipoCadastro(TipoCadastroTributario tipoCadastro) {
        this.tipoCadastro = tipoCadastro;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.SolicitacaoDoctoOficial[ id=" + id + " ]";
    }

    public String getCodigoComSequencia() {
        if (sequencia > 0) {
            codigoComSequencia = codigo + "-" + sequencia;
        } else {
            codigoComSequencia = codigo + "";
        }
        return codigoComSequencia;
    }

    public void setCodigoComSequencia(String codigoComSequencia) {
        this.codigoComSequencia = codigoComSequencia;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(Date dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public String getNomeCadastroTabelavel() {
        if (cadastroEconomico != null) {
            return this.getCadastroEconomico().getInscricaoCadastral();
        }
        if (cadastroRural != null) {
            return String.valueOf(cadastroRural.getCodigo());
        }
        if (cadastroImobiliario != null) {
            return cadastroImobiliario.getInscricaoCadastral();
        }
        if (pessoaFisica != null) {
            return pessoaFisica.getNomeCpfCnpj();
        }
        if (pessoaJuridica != null) {
            return pessoaJuridica.getNomeCpfCnpj();
        }
        return "";
    }

    public void setNomeCadastroTabelavel(String nomeCadastroTabelavel) {
        this.nomeCadastroTabelavel = nomeCadastroTabelavel;
    }

    public String getTipoCadastro() {
        if (cadastroEconomico != null) {
            return TipoCadastroTributario.ECONOMICO.getDescricao();
        }
        if (cadastroRural != null) {
            return TipoCadastroTributario.RURAL.getDescricao();
        }
        if (cadastroImobiliario != null) {
            return TipoCadastroTributario.IMOBILIARIO.getDescricao();
        }
        if (pessoaFisica != null || pessoaJuridica != null) {
            return TipoCadastroTributario.PESSOA.getDescricao();
        }
        return "";
    }

    public TipoCadastroDoctoOficial getTipoCadastroDoctoOficialEnumValue() {
        if (cadastroEconomico != null) {
            return TipoCadastroDoctoOficial.CADASTROECONOMICO;
        }
        if (cadastroRural != null) {
            return TipoCadastroDoctoOficial.CADASTRORURAL;
        }
        if (cadastroImobiliario != null) {
            return TipoCadastroDoctoOficial.CADASTROIMOBILIARIO;
        }
        if (pessoaFisica != null) {
            return TipoCadastroDoctoOficial.PESSOAFISICA;
        }
        if (pessoaJuridica != null) {
            return TipoCadastroDoctoOficial.PESSOAJURIDICA;
        }
        return null;
    }

    public BigDecimal getValorNormalVencer() {
        return valorNormalVencer;
    }

    public void setValorNormalVencer(BigDecimal valorNormalVencer) {
        this.valorNormalVencer = valorNormalVencer;
    }

    public BigDecimal getValorNormalVencido() {
        return valorNormalVencido;
    }

    public void setValorNormalVencido(BigDecimal valorNormalVencido) {
        this.valorNormalVencido = valorNormalVencido;
    }

    public BigDecimal getValorNormalVencidoAcres() {
        return valorNormalVencidoAcres;
    }

    public void setValorNormalVencidoAcres(BigDecimal valorNormalVencidoAcres) {
        this.valorNormalVencidoAcres = valorNormalVencidoAcres;
    }

    public BigDecimal getValorParcelamentoVencer() {
        return valorParcelamentoVencer;
    }

    public void setValorParcelamentoVencer(BigDecimal valorParcelamentoVencer) {
        this.valorParcelamentoVencer = valorParcelamentoVencer;
    }

    public BigDecimal getValorParcelamentoVencido() {
        return valorParcelamentoVencido;
    }

    public void setValorParcelamentoVencido(BigDecimal valorParcelamentoVencido) {
        this.valorParcelamentoVencido = valorParcelamentoVencido;
    }

    public BigDecimal getValorParcelamentoVencidoAcres() {
        return valorParcelamentoVencidoAcres;
    }

    public void setValorParcelamentoVencidoAcres(BigDecimal valorParcelamentoVencidoAcres) {
        this.valorParcelamentoVencidoAcres = valorParcelamentoVencidoAcres;
    }

    public Cadastro getCadastro() {
        return cadastroEconomico != null ? cadastroEconomico
            : cadastroImobiliario != null ? cadastroImobiliario
            : cadastroRural != null ? cadastroRural
            : null;
    }

    public Pessoa getPessoa() {
        return pessoaFisica != null ? pessoaFisica : pessoaJuridica;
    }

    public BigDecimal getValorEmissaoTabelavel() {
        return valorEmissaoTabelavel;
    }

    public void setValorEmissaoTabelavel(BigDecimal valorEmissaoTabelavel) {
        this.valorEmissaoTabelavel = valorEmissaoTabelavel;
    }

    public String getCadastroTabelavel() {
        return cadastroTabelavel;
    }

    public void setCadastroTabelavel(String cadastroTabelavel) {
        this.cadastroTabelavel = cadastroTabelavel;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public void setSequencia(Long sequencia) {
        this.sequencia = sequencia;
    }

    public SolicitacaoDocumentoOficialDTO toDto() {
        SolicitacaoDocumentoOficialDTO dto = new SolicitacaoDocumentoOficialDTO();
        dto.setId(getId());
        if (getCadastro() != null) {
            dto.setCadastro(getCadastro().getNumeroCadastro());
        }
        dto.setSituacao(getSituacaoSolicitacao().name());
        dto.setTipoCadastro(getTipoCadastro());
        dto.setNumeroDocumento(getDocumentoOficial().getNumero());
        dto.setDataEmissao(getDataSolicitacao());
        return dto;
    }
}
