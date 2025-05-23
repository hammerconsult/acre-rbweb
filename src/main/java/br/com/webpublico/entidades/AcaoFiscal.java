package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoAcaoFiscal;
import br.com.webpublico.enums.SituacaoLancamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */
@GrupoDiagrama(nome = "Fiscalizacao")
@Entity

@Audited
@Etiqueta("Ação Fiscal")
public class AcaoFiscal extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "acaoFiscal", orphanRemoval = true)
    private List<SituacoesAcaoFiscal> situacoesAcaoFiscal;
    private String conclusao;
    @Tabelavel
    @Etiqueta(value = "N° Ordem de Serviço")
    @Pesquisavel
    private Long ordemServico;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Programação")
    @ManyToOne
    private ProgramacaoFiscal programacaoFiscal;
    @Pesquisavel
    @Etiqueta("C.M.C.")
    @Tabelavel
    @ManyToOne
    private CadastroEconomico cadastroEconomico;
    @Transient
    @Tabelavel
    @Etiqueta(value = "Pessoa")
    private Pessoa pessoaParaLista;
    @Transient
    @Tabelavel
    @Etiqueta(value = "CPF/CNPJ")
    private String cpfCnpjLista;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataInicial;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFinal;
    @Tabelavel
    @Etiqueta(value = "Data Inicial do Levant.")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLevantamentoInicial;
    @Tabelavel
    @Etiqueta(value = "Data Final do Levant.")
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataLevantamentoFinal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "acaoFiscal", orphanRemoval = true)
    @Tabelavel
    @Etiqueta(value = "Fiscais Designados")
    private List<FiscalDesignado> fiscalDesignados;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "acaoFiscal", orphanRemoval = true)
    private List<LancamentoDoctoFiscal> lancamentoDoctoFiscal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "acaoFiscal", orphanRemoval = true)
    private List<LevantamentoContabil> levantamentosContabeis;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "acaoFiscal", orphanRemoval = true)
    private List<RegistroLancamentoContabil> lancamentosContabeis;
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoAcaoFiscal situacaoAcaoFiscal;
    @Enumerated(EnumType.STRING)
    private SituacaoLancamento situacaoLancamento;
    private Integer ano;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "acaoFiscal", orphanRemoval = true)
    private List<DoctoAcaoFiscal> doctosAcaoFiscal;
    private Long numeroHomologacao;
    @Temporal(TemporalType.DATE)
    private Date dataTermoFiscalizacao;
    @Tabelavel
    @Etiqueta("Número Termo")
    private Integer numeroTermoFiscalizacao;
    private Long numeroLevantamento;
    @Transient
    private Long criadoEm;
    @Transient
    private boolean permissaoUsuario;
    private String migracaochave;
    @OneToMany(mappedBy = "acaoFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoAcaoFiscal> arquivos;
    @Temporal(TemporalType.DATE)
    private Date dataArbitramento;
    private BigDecimal ufmArbitramento;

    public AcaoFiscal() {
        inicializa();
    }

    public AcaoFiscal(AcaoFiscal acao) {
        this.ordemServico = acao.getOrdemServico();
        this.programacaoFiscal = acao.getProgramacaoFiscal();
        this.dataInicial = acao.getDataInicial();
        this.dataFinal = acao.getDataFinal();
        this.situacaoAcaoFiscal = acao.getSituacaoAcaoFiscal();
        this.dataLevantamentoInicial = acao.getDataLevantamentoInicial();
        this.dataLevantamentoFinal = acao.getDataLevantamentoFinal();
        this.fiscalDesignados = acao.getFiscalDesignados();
        this.cadastroEconomico = acao.getCadastroEconomico();
        this.pessoaParaLista = cadastroEconomico.getPessoa();
        if (cadastroEconomico.getPessoa() != null) {
            this.cpfCnpjLista = cadastroEconomico.getPessoa().getCpf_Cnpj();
        }
        this.id = acao.getId();
        inicializa();
    }

    public List<RegistroLancamentoContabil> getLancamentosContabeis() {
        return lancamentosContabeis;
    }

    public List<RegistroLancamentoContabil> getLancamentosContabeisAtivos() {
        List<RegistroLancamentoContabil> ativos = Lists.newArrayList();
        for (RegistroLancamentoContabil reg : lancamentosContabeis) {
            if (!reg.getSituacao().equals(RegistroLancamentoContabil.Situacao.CANCELADO) &&
                !reg.getSituacao().equals(RegistroLancamentoContabil.Situacao.ESTORNADO)) {
                ativos.add(reg);
            }
        }
        return ativos;
    }

    public void setLancamentosContabeis(List<RegistroLancamentoContabil> lancamentosContabeis) {
        this.lancamentosContabeis = lancamentosContabeis;
    }

    public Pessoa getPessoaParaLista() {
        return pessoaParaLista;
    }

    public void setPessoaParaLista(Pessoa pessoaParaLista) {
        this.pessoaParaLista = pessoaParaLista;
    }

    public String getMigracaochave() {
        return migracaochave;
    }

    public void setMigracaochave(String migracaochave) {
        this.migracaochave = migracaochave;
    }

    public Long getNumeroHomologacao() {
        return numeroHomologacao;
    }

    public void setNumeroHomologacao(Long numeroHomologacao) {
        this.numeroHomologacao = numeroHomologacao;
    }

    public Long getNumeroLevantamento() {
        return numeroLevantamento;
    }

    public void setNumeroLevantamento(Long numeroLevantamento) {
        this.numeroLevantamento = numeroLevantamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SituacaoLancamento getSituacaoLancamento() {
        return situacaoLancamento;
    }

    public void setSituacaoLancamento(SituacaoLancamento situacaoLancamento) {
        this.situacaoLancamento = situacaoLancamento;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<SituacoesAcaoFiscal> getSituacoesAcaoFiscal() {
        return situacoesAcaoFiscal;
    }

    public void setSituacoesAcaoFiscal(List<SituacoesAcaoFiscal> situacoesAcaoFiscal) {
        this.situacoesAcaoFiscal = situacoesAcaoFiscal;
    }

    public Date getDataTermoFiscalizacao() {
        return dataTermoFiscalizacao;
    }

    public void setDataTermoFiscalizacao(Date dataTermoFiscalizacao) {
        this.dataTermoFiscalizacao = dataTermoFiscalizacao;
    }

    public Integer getNumeroTermoFiscalizacao() {
        return numeroTermoFiscalizacao;
    }

    public void setNumeroTermoFiscalizacao(Integer numeroTermoFiscalizacao) {
        this.numeroTermoFiscalizacao = numeroTermoFiscalizacao;
    }

    public List<FiscalDesignado> getFiscalDesignados() {
        return fiscalDesignados;
    }

    public void setFiscalDesignados(List<FiscalDesignado> fiscalDesignados) {
        this.fiscalDesignados = fiscalDesignados;
    }

    public String getConclusao() {
        return conclusao != null ? conclusao : "";
    }

    public void setConclusao(String conclusao) {
        this.conclusao = conclusao;
    }

    public List<DoctoAcaoFiscal> getDoctosAcaoFiscal() {
        return doctosAcaoFiscal;
    }

    public void setDoctosAcaoFiscal(List<DoctoAcaoFiscal> doctosAcaoFiscal) {
        this.doctosAcaoFiscal = doctosAcaoFiscal;
    }

    public Long getOrdemServico() {
        return ordemServico;
    }

    public void setOrdemServico(Long ordemServico) {
        this.ordemServico = ordemServico;
    }

    public ProgramacaoFiscal getProgramacaoFiscal() {
        return programacaoFiscal;
    }

    public void setProgramacaoFiscal(ProgramacaoFiscal programacaoFiscal) {
        this.programacaoFiscal = programacaoFiscal;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public boolean isPermissaoUsuario() {
        return permissaoUsuario;
    }

    public void setPermissaoUsuario(boolean permissaoUsuario) {
        this.permissaoUsuario = permissaoUsuario;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public SituacaoAcaoFiscal getSituacaoAcaoFiscal() {
        return situacaoAcaoFiscal;
    }

    public void setSituacaoAcaoFiscal(SituacaoAcaoFiscal situacaoAcaoFiscal) {
        this.situacaoAcaoFiscal = situacaoAcaoFiscal;
    }

    public List<LancamentoDoctoFiscal> getLancamentoDoctoFiscal() {
        return lancamentoDoctoFiscal;
    }

    public void setLancamentoDoctoFiscal(List<LancamentoDoctoFiscal> lancamentoDoctoFiscal) {
        this.lancamentoDoctoFiscal = lancamentoDoctoFiscal;
    }

    public List<LevantamentoContabil> getLevantamentosContabeis() {
        return levantamentosContabeis;
    }

    public void setLevantamentosContabeis(List<LevantamentoContabil> levantamentosContabeis) {
        this.levantamentosContabeis = levantamentosContabeis;
    }

    public Date getDataLevantamentoFinal() {
        return dataLevantamentoFinal;
    }

    public void setDataLevantamentoFinal(Date dataLevantamentoFinal) {
        this.dataLevantamentoFinal = dataLevantamentoFinal;
    }

    public Date getDataLevantamentoInicial() {
        return dataLevantamentoInicial;
    }

    public void setDataLevantamentoInicial(Date dataLevantamentoInicial) {
        this.dataLevantamentoInicial = dataLevantamentoInicial;
    }

    public String getInicio() {
        return new SimpleDateFormat("dd/MM/yyyy").format(dataLevantamentoInicial);
    }

    public String getFim() {
        return new SimpleDateFormat("dd/MM/yyyy").format(dataLevantamentoFinal);
    }

    public String getCpfCnpjLista() {
        return cpfCnpjLista;
    }

    public void setCpfCnpjLista(String cpfCnpjLista) {
        this.cpfCnpjLista = cpfCnpjLista;
    }

    public List<ArquivoAcaoFiscal> getArquivos() {
        return arquivos;
    }

    public Date getDataArbitramento() {
        return dataArbitramento;
    }

    public void setDataArbitramento(Date dataArbitramento) {
        this.dataArbitramento = dataArbitramento;
    }

    public BigDecimal getUfmArbitramento() {
        return ufmArbitramento;
    }

    public void setUfmArbitramento(BigDecimal ufmArbitramento) {
        this.ufmArbitramento = ufmArbitramento;
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
        try {
            return cadastroEconomico.getInscricaoCadastral();
        } catch (Exception e) {
            return "Ação Fiscal: Cadastro não informado";
        }
    }

    private void inicializa() {
        criadoEm = System.nanoTime();
        situacoesAcaoFiscal = Lists.newLinkedList();
        fiscalDesignados = Lists.newLinkedList();
        doctosAcaoFiscal = Lists.newLinkedList();
        lancamentoDoctoFiscal = Lists.newLinkedList();
        lancamentosContabeis = Lists.newLinkedList();
        arquivos = Lists.newLinkedList();
        if (cadastroEconomico != null) {
            this.pessoaParaLista = cadastroEconomico.getPessoa();
            if (cadastroEconomico.getPessoa() != null) {
                this.cpfCnpjLista = cadastroEconomico.getPessoa().getCpf_Cnpj();
            }
        }
    }
}
