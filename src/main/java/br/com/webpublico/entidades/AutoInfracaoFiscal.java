/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAutoInfracao;
import br.com.webpublico.enums.TipoDoctoAcaoFiscal;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.DocumentoFiscalEmail;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
@Entity
@Etiqueta("Auto de Infração")

@Audited
public class AutoInfracaoFiscal extends SuperEntidade implements Serializable, DocumentoFiscalEmail, Comparable<AutoInfracaoFiscal> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número")
    private Integer numero;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano")
    private Integer ano;
    @ManyToOne
    @Etiqueta("Doc. Ação Fiscal")
    @Pesquisavel
    private DoctoAcaoFiscal doctoAcaoFiscal;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Situação")
    @Pesquisavel
    private SituacaoAutoInfracao situacaoAutoInfracao;

    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Vencimento")
    @Pesquisavel
    @Tabelavel
    private Date vencimento;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data Ciência/Revelia")
    @Pesquisavel
    @Tabelavel
    private Date dataCienciaRevelia;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataEstorno;
    private String motivoEstorno;
    @Transient
    private Long criadoEm;
    @Transient
    @Etiqueta("Número Programação")
    @Tabelavel
    private String numeroProgramacao;
    @Transient
    @Etiqueta("Número Ordem Serviço")
    @Tabelavel
    private String numeroOrdemServico;
    @Transient
    @Etiqueta("C.M.C")
    @Tabelavel
    private String cmc;
    @Transient
    @Etiqueta("Razão Social")
    @Tabelavel
    private String razaoSocial;
    @Transient
    @Etiqueta("CNPJ")
    @Tabelavel
    private String cnpj;
    @Transient
    @Etiqueta("Período Fiscalização")
    @Tabelavel
    private String periodoFiscalizacao;
    @ManyToOne
    private RegistroLancamentoContabil registro;
    private String fundamentacao;
    private String historicoFiscal;
    @Tabelavel
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valorAutoInfracao;
    private BigDecimal multa;
    private BigDecimal juros;
    private BigDecimal correcao;
    private String observacaoRevelia;
    @ManyToOne
    private CadastroEconomico cadastroEconomico;

    public AutoInfracaoFiscal() {
        this.criadoEm = System.nanoTime();
    }

    public AutoInfracaoFiscal(AutoInfracaoFiscal autoInfracaoFiscal) {
        this.id = autoInfracaoFiscal.getId();
        this.numero = autoInfracaoFiscal.getNumero();
        this.ano = autoInfracaoFiscal.getAno();
        this.doctoAcaoFiscal = autoInfracaoFiscal.getDoctoAcaoFiscal();
        this.situacaoAutoInfracao = autoInfracaoFiscal.getSituacaoAutoInfracao();
        this.valorAutoInfracao = autoInfracaoFiscal.getValorAutoInfracao();
        this.vencimento = autoInfracaoFiscal.getVencimento();
        this.dataCienciaRevelia = autoInfracaoFiscal.getDataCienciaRevelia();
        this.dataEstorno = autoInfracaoFiscal.getDataEstorno();
        this.motivoEstorno = autoInfracaoFiscal.getMotivoEstorno();
        this.criadoEm = System.nanoTime();
        try {
            this.numeroProgramacao = this.doctoAcaoFiscal.getAcaoFiscal().getProgramacaoFiscal().getNumero().toString();
        } catch (Exception e) {
        }

        try {
            this.numeroOrdemServico = this.doctoAcaoFiscal.getAcaoFiscal().getOrdemServico().toString();
        } catch (Exception e) {
        }

        try {
            this.numeroProgramacao = this.doctoAcaoFiscal.getAcaoFiscal().getProgramacaoFiscal().getNumero().toString();
        } catch (Exception e) {
        }

        try {
            this.numeroOrdemServico = this.doctoAcaoFiscal.getAcaoFiscal().getOrdemServico().toString();
        } catch (Exception e) {
        }

        try {
            this.cmc = this.doctoAcaoFiscal.getAcaoFiscal().getCadastroEconomico().getInscricaoCadastral();
        } catch (Exception e) {
        }

        try {
            this.razaoSocial = this.doctoAcaoFiscal.getAcaoFiscal().getCadastroEconomico().getPessoa().getNome();
        } catch (Exception e) {
        }

        try {
            this.cnpj = this.doctoAcaoFiscal.getAcaoFiscal().getCadastroEconomico().getPessoa().getCpf_Cnpj();
        } catch (Exception e) {
        }

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        String s = "";
        try {
            s += formato.format(doctoAcaoFiscal.getAcaoFiscal().getDataInicial());
        } catch (Exception e) {
        }

        try {
            s += " à " + formato.format(doctoAcaoFiscal.getAcaoFiscal().getDataFinal());
        } catch (Exception e) {
        }

        this.periodoFiscalizacao = s;
    }

    public String getFundamentacao() {
        return fundamentacao;
    }

    public void setFundamentacao(String fundamentacao) {
        this.fundamentacao = fundamentacao;
    }

    public RegistroLancamentoContabil getRegistro() {
        return registro;
    }

    public void setRegistro(RegistroLancamentoContabil registro) {
        this.registro = registro;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public DoctoAcaoFiscal getDoctoAcaoFiscal() {
        return doctoAcaoFiscal;
    }

    public void setDoctoAcaoFiscal(DoctoAcaoFiscal doctoAcaoFiscal) {
        this.doctoAcaoFiscal = doctoAcaoFiscal;
    }

    @Override
    public String getNome() {
        return TipoDoctoAcaoFiscal.AUTOINFRACAO.getDescricao();
    }

    @Override
    public DoctoAcaoFiscal getDocumento() {
        return this.getDoctoAcaoFiscal();
    }

    @Override
    public String getIdentificacao() {
        return this.numero + "/" + this.ano;
    }

    @Override
    public TipoDoctoAcaoFiscal getTipoDocumento() {
        return this.getDoctoAcaoFiscal().getTipoDoctoAcaoFiscal();
    }

    public Integer getNumero() {
        return this.numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public SituacaoAutoInfracao getSituacaoAutoInfracao() {
        return situacaoAutoInfracao;
    }

    public void setSituacaoAutoInfracao(SituacaoAutoInfracao situacaoAutoInfracao) {
        this.situacaoAutoInfracao = situacaoAutoInfracao;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public BigDecimal getValorTotal() {
        return getValorAutoInfracao().add(getMulta()).add(getJuros()).add(getCorrecao());
    }

    public BigDecimal getValorAutoInfracao() {
        return valorAutoInfracao != null ? valorAutoInfracao : BigDecimal.ZERO;
    }

    public void setValorAutoInfracao(BigDecimal valorAutoInfracao) {
        this.valorAutoInfracao = valorAutoInfracao;
    }

    public BigDecimal getMulta() {
        return multa != null ? multa : BigDecimal.ZERO;
    }

    public void setMulta(BigDecimal multa) {
        this.multa = multa;
    }

    public BigDecimal getJuros() {
        return juros != null ? juros : BigDecimal.ZERO;
    }

    public void setJuros(BigDecimal juros) {
        this.juros = juros;
    }

    public BigDecimal getCorrecao() {
        return correcao != null ? correcao : BigDecimal.ZERO;
    }

    public void setCorrecao(BigDecimal correcao) {
        this.correcao = correcao;
    }

    public BigDecimal getValorAutoInfracaoCorrigido() {
        return getValorAutoInfracao().add(getCorrecao());
    }

    public Date getVencimento() {
        return vencimento;
    }

    public void setVencimento(Date vencimento) {
        this.vencimento = vencimento;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public Date getDataCienciaRevelia() {
        return dataCienciaRevelia;
    }

    public void setDataCienciaRevelia(Date dataCienciaRevelia) {
        this.dataCienciaRevelia = dataCienciaRevelia;
    }

    public String getNumeroProgramacao() {
        return numeroProgramacao;
    }

    public void setNumeroProgramacao(String numeroProgramacao) {
        this.numeroProgramacao = numeroProgramacao;
    }

    public String getNumeroOrdemServico() {
        return numeroOrdemServico;
    }

    public void setNumeroOrdemServico(String numeroOrdemServico) {
        this.numeroOrdemServico = numeroOrdemServico;
    }

    public String getCmc() {
        return cmc;
    }

    public void setCmc(String cmc) {
        this.cmc = cmc;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getPeriodoFiscalizacao() {
        return periodoFiscalizacao;
    }

    public void setPeriodoFiscalizacao(String periodoFiscalizacao) {
        this.periodoFiscalizacao = periodoFiscalizacao;
    }

    public String getObservacaoRevelia() {
        return observacaoRevelia != null ? observacaoRevelia : "";
    }

    public void setObservacaoRevelia(String observacaoRevelia) {
        this.observacaoRevelia = observacaoRevelia;
    }

    public String getHistoricoFiscal() {
        return historicoFiscal;
    }

    public void setHistoricoFiscal(String historicoFiscal) {
        this.historicoFiscal = historicoFiscal;
    }

    @Override
    public String toString() {
        return this.numero.toString();
    }

    public Boolean isSituacaoCienciaRevelia() {
        return SituacaoAutoInfracao.CIENCIA.equals(this.situacaoAutoInfracao) || SituacaoAutoInfracao.REVELIA.equals(this.situacaoAutoInfracao);
    }

    public Boolean isSituacaoRevelia() {
        return SituacaoAutoInfracao.REVELIA.equals(this.situacaoAutoInfracao);
    }

    public boolean isSituacaoCancelado() {
        return SituacaoAutoInfracao.CANCELADO.equals(this.situacaoAutoInfracao);
    }

    public boolean isSituacaoEstornado() {
        return SituacaoAutoInfracao.ESTORNADO.equals(this.situacaoAutoInfracao);
    }

    @Override
    public int compareTo(AutoInfracaoFiscal o) {
        try {
            int i =  this.getAno().compareTo(o.getAno());
            if (i == 0) {
                i =  this.getNumero().compareTo(o.getNumero());
            }
            if (i == 0) {
                i = this.getId().compareTo(o.getId());
            }
            return i;
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }
}
