package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoDesconto;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author fabio
 */
@Entity

@Audited
@Etiqueta("Parâmetros de Fiscalização")
@GrupoDiagrama(nome = "Fiscalizacao")
public class ParametroFiscalizacao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    @Obrigatorio
    private Exercicio exercicio;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Tipo de Documento para Termo de Início")
    private TipoDoctoOficial tipoDoctoTermoInicio;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Tipo de Documento para Termo de Finalização")
    private TipoDoctoOficial tipoDoctoTermoFim;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Tipo de Documento para Termo de Homologação")
    private TipoDoctoOficial tipoDoctoTermoHomologacao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Tipo de Documento para o Auto de Infração")
    private TipoDoctoOficial tipoDoctoAutoInfracao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Tipo de Documento para a Ordem de Serviço")
    private TipoDoctoOficial tipoDoctoOrdemServico;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Tipo de Documento para o Relatório Fiscal")
    private TipoDoctoOficial tipoDoctoRelatorioFiscal;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Secretaria")
    @Obrigatorio
    private Entidade secretaria;
    @Obrigatorio
    @Etiqueta("Nome do Secretário")
    private String nomeSecretario;
    private BigDecimal descontoMultaTrintaDias;
    private BigDecimal descontoLevantamento;
    private BigDecimal descontoDeMulta;
    @Enumerated(EnumType.STRING)
    private TipoDesconto tipoDesconto;
    @ManyToOne
    private Pessoa diretor;
    @Obrigatorio
    @Etiqueta("Vencimento do Auto de Infração(em dias)")
    private Integer vencimentoAutoInfracao;
    @Obrigatorio
    @Etiqueta("Vencimento do Auto de Infração com revelia(em dias)")
    private Integer vencimentoAutoInfracaoRevelia;
    @Obrigatorio
    @Etiqueta("Prazo de Fiscalização")
    private Integer prazoFiscalizacao;
    @Obrigatorio
    @Etiqueta("Prazo para Apresentação de Documentos(em dias)")
    private Integer prazoApresentacaoDocto;
    private Integer ultimoNumLevantamento;
    private Integer ultimoNumAutoInfracao;
    private Integer ultimoNumProgramacao;
    private Integer ultimoNumTermoInicio;
    private Integer ultimoNumTermoFim;
    private Integer ultimoNumOrdemServico;
    private Integer ultimoNumHomologacao;
    private Integer diaVencimentoISS;
    private Double descontoAteVencimento;
    private String mensagemDam;
    @Etiqueta("Dívidas ISSQN")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "parametroFiscalizacao")
    private List<ParametroFiscalizacaoDivida> dividasIssqn;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date competencia;

    @Transient
    private Long criadoEm;

    public BigDecimal getDescontoDeMulta() {
        return descontoDeMulta;
    }

    public void setDescontoDeMulta(BigDecimal descontoDeMulta) {
        this.descontoDeMulta = descontoDeMulta;
    }

    public BigDecimal getDescontoLevantamento() {
        return descontoLevantamento;
    }

    public void setDescontoLevantamento(BigDecimal descontoLevantamento) {
        this.descontoLevantamento = descontoLevantamento;
    }

    public BigDecimal getDescontoMultaTrintaDias() {
        return descontoMultaTrintaDias;
    }

    public void setDescontoMultaTrintaDias(BigDecimal descontoMultaTrintaDias) {
        this.descontoMultaTrintaDias = descontoMultaTrintaDias;
    }


    public TipoDesconto getTipoDesconto() {
        return tipoDesconto;
    }

    public void setTipoDesconto(TipoDesconto tipoDesconto) {
        this.tipoDesconto = tipoDesconto;
    }


    public ParametroFiscalizacao() {
        criadoEm = System.nanoTime();
        setDividasIssqn(new ArrayList<ParametroFiscalizacaoDivida>());
    }

    public Integer getDiaVencimentoISS() {
        return diaVencimentoISS;
    }

    public void setDiaVencimentoISS(Integer diaVencimentoISS) {
        this.diaVencimentoISS = diaVencimentoISS;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoDoctoOficial getTipoDoctoAutoInfracao() {
        return tipoDoctoAutoInfracao;
    }

    public void setTipoDoctoAutoInfracao(TipoDoctoOficial tipoDoctoAutoInfracao) {
        this.tipoDoctoAutoInfracao = tipoDoctoAutoInfracao;
    }

    public TipoDoctoOficial getTipoDoctoTermoFim() {
        return tipoDoctoTermoFim;
    }

    public void setTipoDoctoTermoFim(TipoDoctoOficial tipoDoctoTermoFim) {
        this.tipoDoctoTermoFim = tipoDoctoTermoFim;
    }

    public TipoDoctoOficial getTipoDoctoTermoHomologacao() {
        return tipoDoctoTermoHomologacao;
    }

    public void setTipoDoctoTermoHomologacao(TipoDoctoOficial tipoDoctoTermoHomologacao) {
        this.tipoDoctoTermoHomologacao = tipoDoctoTermoHomologacao;
    }

    public TipoDoctoOficial getTipoDoctoTermoInicio() {
        return tipoDoctoTermoInicio;
    }

    public void setTipoDoctoTermoInicio(TipoDoctoOficial tipoDoctoTermoInicio) {
        this.tipoDoctoTermoInicio = tipoDoctoTermoInicio;
    }

    public String getNomeSecretario() {
        return nomeSecretario;
    }

    public void setNomeSecretario(String nomeSecretario) {
        this.nomeSecretario = nomeSecretario;
    }

    public Integer getPrazoApresentacaoDocto() {
        return prazoApresentacaoDocto;
    }

    public void setPrazoApresentacaoDocto(Integer prazoApresentacaoDocto) {
        this.prazoApresentacaoDocto = prazoApresentacaoDocto;
    }

    public Integer getPrazoFiscalizacao() {
        return prazoFiscalizacao;
    }

    public void setPrazoFiscalizacao(Integer prazoFiscalizacao) {
        this.prazoFiscalizacao = prazoFiscalizacao;
    }

    public Entidade getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(Entidade secretaria) {
        this.secretaria = secretaria;
    }

    public Integer getUltimoNumAutoInfracao() {
        return ultimoNumAutoInfracao;
    }

    public void setUltimoNumAutoInfracao(Integer ultimoNumAutoInfracao) {
        this.ultimoNumAutoInfracao = ultimoNumAutoInfracao;
    }

    public Integer getUltimoNumHomologacao() {
        return ultimoNumHomologacao;
    }

    public void setUltimoNumHomologacao(Integer ultimoNumHomologacao) {
        this.ultimoNumHomologacao = ultimoNumHomologacao;
    }

    public Integer getUltimoNumLevantamento() {
        return ultimoNumLevantamento;
    }

    public void setUltimoNumLevantamento(Integer ultimoNumLevantamento) {
        this.ultimoNumLevantamento = ultimoNumLevantamento;
    }

    public Integer getUltimoNumOrdemServico() {
        return ultimoNumOrdemServico;
    }

    public void setUltimoNumOrdemServico(Integer ultimoNumOrdemServico) {
        this.ultimoNumOrdemServico = ultimoNumOrdemServico;
    }

    public Integer getUltimoNumProgramacao() {
        return ultimoNumProgramacao;
    }

    public void setUltimoNumProgramacao(Integer ultimoNumProgramacao) {
        this.ultimoNumProgramacao = ultimoNumProgramacao;
    }

    public Integer getUltimoNumTermoFim() {
        return ultimoNumTermoFim;
    }

    public void setUltimoNumTermoFim(Integer ultimoNumTermoFim) {
        this.ultimoNumTermoFim = ultimoNumTermoFim;
    }

    public Integer getUltimoNumTermoInicio() {
        return ultimoNumTermoInicio;
    }

    public void setUltimoNumTermoInicio(Integer ultimoNumTermoInicio) {
        this.ultimoNumTermoInicio = ultimoNumTermoInicio;
    }

    public Integer getVencimentoAutoInfracao() {
        return vencimentoAutoInfracao;
    }

    public void setVencimentoAutoInfracao(Integer vencimentoAutoInfracao) {
        this.vencimentoAutoInfracao = vencimentoAutoInfracao;
    }

    public TipoDoctoOficial getTipoDoctoOrdemServico() {
        return tipoDoctoOrdemServico;
    }

    public void setTipoDoctoOrdemServico(TipoDoctoOficial tipoDoctoOrdemServico) {
        this.tipoDoctoOrdemServico = tipoDoctoOrdemServico;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoDoctoOficial getTipoDoctoRelatorioFiscal() {
        return tipoDoctoRelatorioFiscal;
    }

    public void setTipoDoctoRelatorioFiscal(TipoDoctoOficial tipoDoctoRelatorioFiscal) {
        this.tipoDoctoRelatorioFiscal = tipoDoctoRelatorioFiscal;
    }

    public Pessoa getDiretor() {
        return diretor;
    }

    public void setDiretor(Pessoa diretor) {
        this.diretor = diretor;
    }

    public Double getDescontoAteVencimento() {
        return descontoAteVencimento;
    }

    public void setDescontoAteVencimento(Double descontoAteVencimento) {
        this.descontoAteVencimento = descontoAteVencimento;
    }

    public String getMensagemDam() {
        return mensagemDam;
    }

    public void setMensagemDam(String mensagemDam) {
        this.mensagemDam = mensagemDam;
    }

    public Integer getVencimentoAutoInfracaoRevelia() {
        return vencimentoAutoInfracaoRevelia;
    }

    public void setVencimentoAutoInfracaoRevelia(Integer vencimentoAutoInfracaoRevelia) {
        this.vencimentoAutoInfracaoRevelia = vencimentoAutoInfracaoRevelia;
    }

    public List<ParametroFiscalizacaoDivida> getDividasIssqn() {
        return dividasIssqn;
    }

    public void setDividasIssqn(List<ParametroFiscalizacaoDivida> dividasIssqn) {
        this.dividasIssqn = dividasIssqn;
    }

    public Date getCompetencia() {
        return competencia;
    }

    public void setCompetencia(Date competencia) {
        this.competencia = competencia;
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
        return "br.com.webpublico.entidades.ParametroFiscalizacao[ id=" + id + " ]";
    }

}
