package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeDetendorDocumentoLicitacao;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 01/08/14
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "REGISTROSOLMATEXT")
@Etiqueta("Adesão à Ata de Registro de Preço Externo")
public class RegistroSolicitacaoMaterialExterno extends SuperEntidade implements ValidadorEntidade, EntidadeDetendorDocumentoLicitacao {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Solicitação Registro de Preço")
    @Pesquisavel
    @Tabelavel
    @OneToOne
    private SolicitacaoMaterialExterno solicitacaoMaterialExterno;

    @Etiqueta("Número do Registro")
    @Pesquisavel
    @Tabelavel
    private Integer numeroRegistro;

    @Etiqueta("Exercício")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private Exercicio exercicioRegistro;

    @Etiqueta("Data de Registro da Carona")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date dataRegistroCarona;

    @Etiqueta("Órgão/Entidade/Fundo Participante")
    @Obrigatorio
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;

    @Transient
    @Tabelavel
    @Etiqueta("Órgão/Entidade/Fundo Participante")
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    @Etiqueta("Representante Legal Aderente")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    private ContratoFP contratoFP;

    @Etiqueta("Órgão/Entidade/Fundo Gerenciador")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private PessoaJuridica pessoaJuridica;

    @Etiqueta("Representante Legal Gerenciador")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    private PessoaFisica pessoaFisica;

    @Etiqueta("Número do Processo de Compra")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Integer numeroProcessoCompra;

    @Etiqueta("Modalidade da Carona")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private ModalidadeLicitacao modalidadeCarona;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    private TipoNaturezaDoProcedimentoLicitacao tipoModalidade;

    @Etiqueta("Número da Modalidade")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Integer numeroModalidade;

    @Etiqueta("Exercício da Modalidade")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @ManyToOne
    private Exercicio exercicioModalidade;

    @Etiqueta("Tipo de Avaliação")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Enumerated(EnumType.STRING)
    private TipoAvaliacaoLicitacao tipoAvaliacao;

    @Etiqueta("Tipo de Apuração")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Enumerated(EnumType.STRING)
    private TipoApuracaoLicitacao tipoApuracao;

    @Etiqueta("Objeto")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Length(maximo = 3000)
    private String objeto;

    @Etiqueta("Justificativa")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Length(maximo = 3000)
    private String justificativa;

    @Etiqueta("Número da Ata Carona")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Integer numeroAtaCarona;

    @Etiqueta("Data de Validade da Ata Carona")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Temporal(TemporalType.DATE)
    private Date dataValidadeAtaCarona;

    @Etiqueta("Data de Autorização da Carona")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Temporal(TemporalType.DATE)
    private Date dataAutorizacaoCarona;

    @ManyToOne
    @Etiqueta("Representante Legal Aderente PF")
    private PessoaFisica representanteAderentePF;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private DetentorDocumentoLicitacao detentorDocumentoLicitacao;

    @Etiqueta("Publicações")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "registroSolMatExterno")
    private List<RegistroSolicitacaoMaterialExternoPublicacao> publicacoes;

    @Etiqueta("Fornecedores")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "registroSolMatExterno")
    private List<RegistroSolicitacaoMaterialExternoFornecedor> fornecedores;

    public RegistroSolicitacaoMaterialExterno() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public SolicitacaoMaterialExterno getSolicitacaoMaterialExterno() {
        return solicitacaoMaterialExterno;
    }

    public void setSolicitacaoMaterialExterno(SolicitacaoMaterialExterno solicitacaoMaterialExterno) {
        this.solicitacaoMaterialExterno = solicitacaoMaterialExterno;
    }

    public Integer getNumeroRegistro() {
        return numeroRegistro;
    }

    public void setNumeroRegistro(Integer numeroRegistro) {
        this.numeroRegistro = numeroRegistro;
    }

    public Exercicio getExercicioRegistro() {
        return exercicioRegistro;
    }

    public void setExercicioRegistro(Exercicio exercicioRegistro) {
        this.exercicioRegistro = exercicioRegistro;
    }

    public Date getDataRegistroCarona() {
        return dataRegistroCarona;
    }

    public void setDataRegistroCarona(Date dataRegistroCarona) {
        this.dataRegistroCarona = dataRegistroCarona;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        if (contratoFP != null) {
            setRepresentanteAderentePF(contratoFP.getMatriculaFP().getPessoa());
        }
        this.contratoFP = contratoFP;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    public Integer getNumeroProcessoCompra() {
        return numeroProcessoCompra;
    }

    public void setNumeroProcessoCompra(Integer numeroProcessoCompra) {
        this.numeroProcessoCompra = numeroProcessoCompra;
    }

    public ModalidadeLicitacao getModalidadeCarona() {
        return modalidadeCarona;
    }

    public void setModalidadeCarona(ModalidadeLicitacao modalidadeCarona) {
        this.modalidadeCarona = modalidadeCarona;
    }

    public Integer getNumeroModalidade() {
        return numeroModalidade;
    }

    public void setNumeroModalidade(Integer numeroModalidade) {
        this.numeroModalidade = numeroModalidade;
    }

    public Exercicio getExercicioModalidade() {
        return exercicioModalidade;
    }

    public void setExercicioModalidade(Exercicio exercicioModalidade) {
        this.exercicioModalidade = exercicioModalidade;
    }

    public TipoAvaliacaoLicitacao getTipoAvaliacao() {
        return tipoAvaliacao;
    }

    public void setTipoAvaliacao(TipoAvaliacaoLicitacao tipoAvaliacao) {
        this.tipoAvaliacao = tipoAvaliacao;
    }

    public TipoApuracaoLicitacao getTipoApuracao() {
        return tipoApuracao;
    }

    public void setTipoApuracao(TipoApuracaoLicitacao tipoApuracao) {
        this.tipoApuracao = tipoApuracao;
    }

    public String getObjeto() {
        return objeto;
    }

    public void setObjeto(String objeto) {
        this.objeto = objeto;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public Integer getNumeroAtaCarona() {
        return numeroAtaCarona;
    }

    public void setNumeroAtaCarona(Integer numeroAtaCarona) {
        this.numeroAtaCarona = numeroAtaCarona;
    }

    public Date getDataValidadeAtaCarona() {
        return dataValidadeAtaCarona;
    }

    public void setDataValidadeAtaCarona(Date dataValidadeAtaCarona) {
        this.dataValidadeAtaCarona = dataValidadeAtaCarona;
    }

    public Date getDataAutorizacaoCarona() {
        return dataAutorizacaoCarona;
    }

    public void setDataAutorizacaoCarona(Date dataAutorizacaoCarona) {
        this.dataAutorizacaoCarona = dataAutorizacaoCarona;
    }

    public PessoaFisica getRepresentanteAderentePF() {
        return representanteAderentePF;
    }

    public void setRepresentanteAderentePF(PessoaFisica representanteAderentePF) {
        this.representanteAderentePF = representanteAderentePF;
    }

    @Override
    public DetentorDocumentoLicitacao getDetentorDocumentoLicitacao() {
        return detentorDocumentoLicitacao;
    }

    @Override
    public void setDetentorDocumentoLicitacao(DetentorDocumentoLicitacao detentorDocumentoLicitacao) {
        this.detentorDocumentoLicitacao = detentorDocumentoLicitacao;
    }

    @Override
    public TipoMovimentoProcessoLicitatorio getTipoAnexo() {
        return TipoMovimentoProcessoLicitatorio.ADESAO_EXTERNA;
    }

    public List<RegistroSolicitacaoMaterialExternoPublicacao> getPublicacoes() {
        return publicacoes;
    }

    public void setPublicacoes(List<RegistroSolicitacaoMaterialExternoPublicacao> publicacoes) {
        this.publicacoes = publicacoes;
    }

    public List<RegistroSolicitacaoMaterialExternoFornecedor> getFornecedores() {
        return fornecedores;
    }

    public void setFornecedores(List<RegistroSolicitacaoMaterialExternoFornecedor> fornecedores) {
        this.fornecedores = fornecedores;
    }

    public TipoNaturezaDoProcedimentoLicitacao getTipoModalidade() {
        return tipoModalidade;
    }

    public void setTipoModalidade(TipoNaturezaDoProcedimentoLicitacao tipoModalidade) {
        this.tipoModalidade = tipoModalidade;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.unidadeOrganizacional = null;
        if (hierarquiaOrganizacional != null) {
            this.unidadeOrganizacional = hierarquiaOrganizacional.getSubordinada();
        }
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public String toString() {
        try {
            return toStringAutoComplete();
        } catch (NullPointerException ne) {
            return "";
        }
    }

    public boolean registroTemFornecedorAdicionado() {
        return getFornecedores() != null && !getFornecedores().isEmpty();
    }

    public String toStringAutoComplete() {
        return "Nº - " + numeroRegistro + " / " + exercicioRegistro.getAno() + " - " + objeto;
    }
}
