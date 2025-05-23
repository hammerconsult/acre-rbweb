/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.entidadesauxiliares.ListenerDadosCmc;
import br.com.webpublico.entidadesauxiliares.ListenerGaranteContaBancariaPrincipal;
import br.com.webpublico.entidadesauxiliares.ListenerGaranteEnderecoPrincipal;
import br.com.webpublico.entidadesauxiliares.ListenerGaranteTelefonePrincipal;
import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.nfse.domain.dtos.DadosPessoaisNfseDTO;
import br.com.webpublico.nfse.domain.dtos.MunicipioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.domain.dtos.PessoaNfseDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.Years;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

/**
 * @author jaime
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Entity

@Audited
@Inheritance(strategy = InheritanceType.JOINED)
@EntityListeners(value = {ListenerGaranteEnderecoPrincipal.class, ListenerGaranteTelefonePrincipal.class, ListenerGaranteContaBancariaPrincipal.class, ListenerDadosCmc.class})
public abstract class Pessoa extends SuperEntidadeDetectaAlteracao implements Serializable, Comparable<Pessoa>, NfseEntity, PossuidorArquivo {

    static final Logger log = LoggerFactory.getLogger(Pessoa.class);
    private static final long serialVersionUID = 1L;
    @Transient
    @Invisivel
    public Long criadoEm;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    @Column(name = "id")
    private Long id;
    @Column(length = 45, name = "email")
    @Pesquisavel
    private String email;
    @Column(length = 50, name = "homePage")
    @Etiqueta("Home Page")
    private String homePage;
    @ManyToOne
    @JoinColumn(name = "nacionalidade_id")
    private Nacionalidade nacionalidade;
    //JOIN TABLE = pessoa_enderecocorreio
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnderecoCorreio> enderecoscorreio;
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Telefone> telefones;
    @OneToMany(mappedBy = "pessoa")
    @Invisivel
    private List<Propriedade> propriedades;
    @OneToOne
    @Etiqueta("Conta Corrente dos Contribuintes")
    @Invisivel
    @JoinColumn(name = "contacorrentecontribuinte_id")
    private ContaCorrenteContribuinte contaCorrenteContribuinte;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Column(name = "dataregistro")
    private Date dataRegistro;
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContaCorrenteBancPessoa> contaCorrenteBancPessoas;
    @ElementCollection(targetClass = PerfilEnum.class)
    @CollectionTable(name = "PESSOA_PERFIL", joinColumns =
    @JoinColumn(name = "ID_PESSOA"))
    @Column(name = "PERFIL", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<PerfilEnum> perfis = new ArrayList<PerfilEnum>();
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação Cadastral")
    @Tabelavel
    private SituacaoCadastralPessoa situacaoCadastralPessoa;
    @Transient
    private String cpf_cnpj_temp;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClasseCredorPessoa> classeCredorPessoas;
    private Boolean bloqueado;
    private String motivo;
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PessoaClassificacaoCredor> classificacaoCredores;
    private String migracaoChave;
    @Enumerated(EnumType.STRING)
    private ClassePessoa classePessoa;
    @ManyToOne
    @Pesquisavel
    private UnidadeExterna unidadeExterna;
    private String observacao;
    @ManyToOne
    private EnderecoCorreio enderecoPrincipal;
    @ManyToOne
    private Telefone telefonePrincipal;
    @ManyToOne
    private ContaCorrenteBancPessoa contaCorrentePrincipal;
    @ManyToOne
    private Profissao profissao;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pessoa")
    private List<DadosRetificacaoPessoaCda> dadosRetificacao;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pessoa")
    private List<UsuarioWeb> usuariosWeb;
    @Transient
    @Invisivel
    private List<ItemPropostaFornecedor> itensPropostaFornecedor;
    @OneToOne(cascade = CascadeType.ALL)
    @Invisivel
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @OneToOne
    private Arquivo arquivo;
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RepresentanteLegalPessoa> representantesLegal;
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<HistoricoSituacaoPessoa> historicoSituacoesPessoa;
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PessoaCNAE> pessoaCNAE;
    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PessoaHorarioFuncionamento> horariosFuncionamento;
    private String codigoCnaeBI;
    private String key;

    private Boolean receitaBrutaCPRB;

    public Pessoa() {
        criadoEm = System.nanoTime();
        dataRegistro = new Date();
        bloqueado = Boolean.FALSE;
        classePessoa = ClassePessoa.EXTRA;
        classeCredorPessoas = Lists.newArrayList();
        classificacaoCredores = Lists.newArrayList();
        enderecoscorreio = Lists.newArrayList();
        dadosRetificacao = Lists.newArrayList();
        itensPropostaFornecedor = Lists.newArrayList();
        telefones = Lists.newArrayList();
        this.representantesLegal = Lists.newArrayList();
        this.usuariosWeb = Lists.newArrayList();
        this.historicoSituacoesPessoa = Lists.newArrayList();
        this.pessoaCNAE = Lists.newArrayList();
        this.horariosFuncionamento = Lists.newArrayList();
    }

    public Pessoa(String email, String homePage, Nacionalidade nacionalidade, List<EnderecoCorreio> enderecos, List<Telefone> telefones, EscritorioContabil escritorioContabil, String observacao) {
        this.email = email;
        this.homePage = homePage;
        this.nacionalidade = nacionalidade;
        this.enderecoscorreio = enderecos;
        this.telefones = telefones;
        this.observacao = observacao;
    }

    public Pessoa(Long id) {
        this.id = id;
    }

    public List<PessoaCNAE> getPessoaCNAE() {
        return pessoaCNAE;
    }

    public List<PessoaCNAE> getPessoaCNAEAtivos() {
        List<PessoaCNAE> cnaesAtivos = Lists.newArrayList();
        for (PessoaCNAE pessoaCNAE : getPessoaCNAE()) {
            if (pessoaCNAE.getCnae() != null) {
                if ((pessoaCNAE.getFim() == null || pessoaCNAE.getFim().after(new Date())) &&
                    CNAE.Situacao.ATIVO.equals(pessoaCNAE.getCnae().getSituacao())) {
                    cnaesAtivos.add(pessoaCNAE);
                }
            }
        }
        Collections.sort(cnaesAtivos);
        return cnaesAtivos;
    }

    public List<PessoaCNAE> getPessoaCNAEInativos() {
        List<PessoaCNAE> cnaesInativos = Lists.newArrayList();
        for (PessoaCNAE pessoaCNAE : getPessoaCNAE()) {
            if (pessoaCNAE.getCnae() != null) {
                if ((pessoaCNAE.getFim() != null && pessoaCNAE.getFim().before(new Date())) ||
                    CNAE.Situacao.INATIVO.equals(pessoaCNAE.getCnae().getSituacao())) {
                    cnaesInativos.add(pessoaCNAE);
                }
            }
        }
        Collections.sort(cnaesInativos);
        return cnaesInativos;
    }

    public void setPessoaCNAE(List<PessoaCNAE> pessoaCNAE) {
        this.pessoaCNAE = pessoaCNAE;
    }

    public List<ItemPropostaFornecedor> getItensPropostaFornecedor() {
        if (itensPropostaFornecedor != null) {
            Collections.sort(itensPropostaFornecedor, new Comparator<ItemPropostaFornecedor>() {
                @Override
                public int compare(ItemPropostaFornecedor o1, ItemPropostaFornecedor o2) {
                    if (o1.getItemProcessoDeCompra() != null) {
                        int i = o1.getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero().compareTo(
                            o2.getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero());
                        if (i == 0) {
                            i = o1.getItemProcessoDeCompra().getNumero().compareTo(
                                o2.getItemProcessoDeCompra().getNumero());
                        }
                        return i;
                    } else {
                        return o1.getLotePropostaFornecedor().getLoteProcessoDeCompra().getNumero().compareTo(
                            o2.getLotePropostaFornecedor().getLoteProcessoDeCompra().getNumero());
                    }
                }
            });
        }
        return itensPropostaFornecedor;
    }

    public void setItensPropostaFornecedor(List<ItemPropostaFornecedor> itensPropostaFornecedor) {
        this.itensPropostaFornecedor = itensPropostaFornecedor;
    }

    public abstract String getNome();

    public abstract String getNomeTratamento();

    public abstract String getNomeFantasia();

    public abstract String getCpf_Cnpj();

    public abstract String getRg_InscricaoEstadual();

    public abstract String getOrgaoExpedidor();

    public String getNomeAutoComplete() {
        StringBuilder sb = new StringBuilder();
        if (!Strings.isNullOrEmpty(getCpf_Cnpj())) {
            sb.append(getCpf_Cnpj().trim());
        }
        if (!Strings.isNullOrEmpty(getNome())) {
            if (!Strings.isNullOrEmpty(sb.toString()))
                sb.append(" - ");
            sb.append(getNome().trim());
        }
        if (!Strings.isNullOrEmpty(getNomeTratamento())) {
            sb.append(" (").append(getNomeTratamento().trim()).append(")");
        }

        if (getSituacaoCadastralPessoa() != null && !Strings.isNullOrEmpty(getSituacaoCadastralPessoa().getDescricao())) {
            sb.append(" - ").append(getSituacaoCadastralPessoa().getDescricao().trim());
        }
        return sb.toString();
    }

    public String getNomeAutoCompleteComId() {
        return getId() + " - " + getCpf_Cnpj() + " - " + getNome() + " (" + getNomeTratamento() + ") " + " - " + getSituacaoCadastralPessoa().getDescricao();
    }

    public List<PessoaClassificacaoCredor> getClassificacaoCredores() {
        return classificacaoCredores;
    }

    public void setClassificacaoCredores(List<PessoaClassificacaoCredor> classificacaoCredores) {
        this.classificacaoCredores = classificacaoCredores;
    }

    public List<EnderecoCorreio> getEnderecoscorreio() {
        return enderecoscorreio;
    }

    public void setEnderecoscorreio(List<EnderecoCorreio> enderecoscorreio) {
        this.enderecoscorreio = enderecoscorreio;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Profissao getProfissao() {
        return profissao;
    }

    public void setProfissao(Profissao profissao) {
        this.profissao = profissao;
    }

    public Boolean getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(Boolean bloqueado) {
        this.bloqueado = bloqueado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public ContaCorrenteContribuinte getContaCorrenteContribuinte() {
        return contaCorrenteContribuinte;
    }

    public void setContaCorrenteContribuinte(ContaCorrenteContribuinte contaCorrenteContribuinte) {
        this.contaCorrenteContribuinte = contaCorrenteContribuinte;
    }

    public String getEmail() {
        if (email != null) {
            return email.toLowerCase();
        }
        return email;
    }

    public void setEmail(String email) {
        if (email != null) {
            this.email = email.toLowerCase();
        } else {
            this.email = email;
        }
    }

    public List<EnderecoCorreio> getEnderecos() {
        return enderecoscorreio;
    }

    public void setEnderecos(List<EnderecoCorreio> enderecos) {
        this.enderecoscorreio = enderecos;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        if (!Strings.isNullOrEmpty(homePage)) {
            this.homePage = homePage.toLowerCase();
        } else {
            this.homePage = homePage;
        }
    }

    public Nacionalidade getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(Nacionalidade nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public List<Propriedade> getPropriedades() {
        return propriedades;
    }

    public void setPropriedades(List<Propriedade> propriedades) {
        this.propriedades = propriedades;
    }

    public List<Telefone> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<Telefone> telefones) {
        this.telefones = telefones;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<PerfilEnum> getPerfis() {
        return perfis;
    }

    public void setPerfis(List<PerfilEnum> perfis) {
        this.perfis = perfis;
    }

    public List<ClasseCredorPessoa> getClasseCredorPessoas() {
        return classeCredorPessoas;
    }

    public void setClasseCredorPessoas(List<ClasseCredorPessoa> classeCredorPessoas) {
        this.classeCredorPessoas = classeCredorPessoas;
    }

    public List<ContaCorrenteBancPessoa> getContaCorrenteBancPessoas() {
        return contaCorrenteBancPessoas;
    }

    public void setContaCorrenteBancPessoas(List<ContaCorrenteBancPessoa> contaCorrenteBancPessoas) {
        this.contaCorrenteBancPessoas = contaCorrenteBancPessoas;
    }

    @Override
    public Long getCriadoEm() {
        return criadoEm;
    }

    @Override
    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public SituacaoCadastralPessoa getSituacaoCadastralPessoa() {
        return situacaoCadastralPessoa;
    }

    public void setSituacaoCadastralPessoa(SituacaoCadastralPessoa situacaoCadastralPessoa) {
        this.situacaoCadastralPessoa = situacaoCadastralPessoa;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getMigracaoChave() {
        return migracaoChave;
    }

    public void setMigracaoChave(String migracaoChave) {
        this.migracaoChave = migracaoChave;
    }

    public EnderecoCorreio getEnderecoPrincipal() {
        return enderecoPrincipal;
    }

    public void setEnderecoPrincipal(EnderecoCorreio enderecoPrincipal) {
        this.enderecoPrincipal = enderecoPrincipal;
    }

    public void setTelefonePrincipal(Telefone telefonePrincipal) {
        this.telefonePrincipal = telefonePrincipal;
    }

    public Telefone getTelefonePrincipal() {
        return telefonePrincipal;
    }

    public List<DadosRetificacaoPessoaCda> getDadosRetificacao() {
        return dadosRetificacao;
    }

    public void setDadosRetificacao(List<DadosRetificacaoPessoaCda> dadosRetificacao) {
        this.dadosRetificacao = dadosRetificacao;
    }

    public List<UsuarioWeb> getUsuariosWeb() {
        return usuariosWeb;
    }

    public void setUsuariosWeb(List<UsuarioWeb> usuariosWeb) {
        this.usuariosWeb = usuariosWeb;
    }

    public DadosRetificacaoPessoaCda getDadosRetificacaoPessoaCdaVigente() {
        if (!dadosRetificacao.isEmpty()) {
            Collections.sort(dadosRetificacao, new Comparator<DadosRetificacaoPessoaCda>() {
                @Override
                public int compare(DadosRetificacaoPessoaCda o1, DadosRetificacaoPessoaCda o2) {
                    return o1.getSequencia().compareTo(o2.getSequencia());
                }
            });

            return dadosRetificacao.get(dadosRetificacao.size() - 1);
        }
        return null;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public String getCpf_cnpj_temp() {
        return getCpf_Cnpj();
    }

    public String getTipoPessoa() {
        return (this instanceof PessoaFisica) ? "Pessoa Física" : "Pessoa Jurídica";
    }

    public String getNomeCpfCnpj() {
        if (getCpf_Cnpj() != null) {
            return getNome() + " (" + getCpf_Cnpj() + ") ";
        }
        return getNome();
    }

    public ClassePessoa getClassePessoa() {
        return classePessoa;
    }

    public void setClassePessoa(ClassePessoa classePessoa) {
        this.classePessoa = classePessoa;
    }

    public UnidadeExterna getUnidadeExterna() {
        return unidadeExterna;
    }

    public void setUnidadeExterna(UnidadeExterna unidadeExterna) {
        this.unidadeExterna = unidadeExterna;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public ContaCorrenteBancPessoa getContaCorrentePrincipal() {
        return contaCorrentePrincipal;
    }

    public void setContaCorrentePrincipal(ContaCorrenteBancPessoa contaCorrentePrincipal) {
        this.contaCorrentePrincipal = contaCorrentePrincipal;
    }

    public Telefone getFax() {
        try {
            for (Telefone telefone : getTelefones()) {
                if (telefone.isFax()) {
                    return telefone;
                }
            }
            return null;
        } catch (NullPointerException npe) {
            throw npe;
        }
    }

    public static Integer recuperaIdade(Date dataNascimento) {
        DateTime start = new DateTime(dataNascimento);
        DateTime end = new DateTime();

        return Years.yearsBetween(start, end).getYears();
    }

    public EnderecoCorreio getEnderecoCompletoOuCorrespondencia() {
        for (EnderecoCorreio endereco : enderecoscorreio) {
            if (temLogradouro(endereco) && temNumeroCorreio(endereco) && temLocalidade(endereco)
                && temUf(endereco) && temCep(endereco) && temBairro(endereco)) {
                return endereco;
            }
        }
        return getEnderecoCorrespondencia();
    }

    private boolean temCep(EnderecoCorreio endereco) {
        return endereco.getCep() != null && !endereco.getCep().trim().isEmpty();
    }

    private boolean temUf(EnderecoCorreio endereco) {
        return endereco.getUf() != null && !endereco.getUf().trim().isEmpty();
    }

    private boolean temLocalidade(EnderecoCorreio endereco) {
        return endereco.getLocalidade() != null && !endereco.getLocalidade().trim().isEmpty();
    }

    private boolean temBairro(EnderecoCorreio endereco) {
        return endereco.getBairro() != null && !endereco.getBairro().trim().isEmpty();
    }

    private boolean temNumeroCorreio(EnderecoCorreio endereco) {
        return endereco.getNumero() != null && !endereco.getNumero().trim().isEmpty();
    }

    private boolean temLogradouro(EnderecoCorreio endereco) {
        return endereco.getLogradouro() != null && !endereco.getLogradouro().trim().isEmpty();
    }

    public EnderecoCorreio getEnderecoCorrespondencia() {
        EnderecoCorreio correspondencia = null;
        for (EnderecoCorreio enderecoCorreio : enderecoscorreio) {
            if (enderecoCorreio.getPrincipal()) {
                return enderecoCorreio;
            } else if (TipoEndereco.CORRESPONDENCIA.equals(enderecoCorreio.getTipoEndereco())) {
                correspondencia = enderecoCorreio;
            }
        }
        if (correspondencia == null && enderecoscorreio != null && !enderecoscorreio.isEmpty()) {
            correspondencia = enderecoscorreio.get(0);
        }
        return correspondencia;
    }

    public Telefone getTelefonePrincipalAdicionado() {
        Telefone telefonePrincipal = null;
        if (this.getTelefones() != null && !this.getTelefones().isEmpty()) {
            for (Telefone telefone : this.getTelefones()) {
                if (telefone.getPrincipal()) {
                    telefonePrincipal = telefone;
                }
            }
        }
        if (telefonePrincipal == null && this.getTelefones() != null && !this.getTelefones().isEmpty()) {
            telefonePrincipal = this.getTelefones().get(0);
        }
        return telefonePrincipal;
    }

    public ContaCorrenteBancPessoa getContaBancariaPrincipalAdicionada() {
        if (contaCorrenteBancPessoas != null && !contaCorrenteBancPessoas.isEmpty()) {
            for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : contaCorrenteBancPessoas) {
                if (contaCorrenteBancPessoa.getPrincipal()) {
                    return contaCorrenteBancPessoa;
                }
            }
        }
        if (contaCorrenteBancPessoas != null && !contaCorrenteBancPessoas.isEmpty()) {
            for (ContaCorrenteBancPessoa contaCorrenteBancPessoa : contaCorrenteBancPessoas) {
                if (SituacaoConta.ATIVO.equals(contaCorrenteBancPessoa.getSituacao())) {
                    contaCorrenteBancPessoa.setPrincipal(true);
                    return contaCorrenteBancPessoa;
                }
            }
        }
        return null;
    }

    public EnderecoCorreio getEnderecoDomicilioFiscal() {
        EnderecoCorreio domicilioFiscal = null;
        for (EnderecoCorreio enderecoCorreio : enderecoscorreio) {
            if (TipoEndereco.DOMICILIO_FISCAL.equals(enderecoCorreio.getTipoEndereco())) {
                return enderecoCorreio;
            } else if (enderecoCorreio.getPrincipal()) {
                domicilioFiscal = enderecoCorreio;
            }
        }

        if (domicilioFiscal == null) {
            return getEnderecoCorrespondencia();
        }
        return domicilioFiscal;
    }

    public Telefone getTelefonePorTipo(TipoTelefone tipoTelefone) {
        try {
            for (Telefone telefone : getTelefones()) {
                if (tipoTelefone.equals(telefone.getTipoFone())) {
                    return telefone;
                }
            }
            return null;
        } catch (NullPointerException npe) {
            throw npe;
        }
    }

    public Telefone getTelefoneValido() {
        try {
            if (getTelefones().isEmpty()) {
                return null;
            }
            for (Telefone telefone : getTelefones()) {
                if (telefone.getPrincipal()) {
                    return telefone;
                }
            }
            for (Telefone telefone : getTelefones()) {
                if (!telefone.getTelefone().equals("") && telefone.getTipoFone().equals(TipoTelefone.RESIDENCIAL)) {
                    return telefone;
                }
            }
            for (Telefone telefone : getTelefones()) {
                if (!telefone.getTelefone().equals("") && telefone.getTipoFone().equals(TipoTelefone.CELULAR)) {
                    return telefone;
                }
            }
            for (Telefone telefone : getTelefones()) {
                if (!telefone.getTelefone().equals("") && telefone.getTipoFone().equals(TipoTelefone.COMERCIAL)) {
                    return telefone;
                }
            }
            return null;
        } catch (NullPointerException npe) {
            throw npe;
        }
    }

    public TipoEmpresa getTipoEmpresa() {
        return TipoEmpresa.INDEFINIDO;
    }

    public boolean isPessoaJuridica() {
        return Util.checkInstaceof(this, PessoaJuridica.class);
    }

    public boolean isPessoaFisica() {
        return this instanceof PessoaFisica;
    }

    public PessoaJuridica getAsPessoaJuridica() {
        return (PessoaJuridica) this;
    }

    public PessoaFisica getAsPessoaFisica() {
        return (PessoaFisica) this;
    }

    public boolean isMicroEmpresa() {
        return TipoEmpresa.MICRO.equals(getTipoEmpresa());
    }

    public boolean isPequenaEmpresa() {
        return TipoEmpresa.PEQUENA.equals(getTipoEmpresa());
    }

    public boolean isTipoEmpresaIndefinido() {
        return TipoEmpresa.INDEFINIDO.equals(getTipoEmpresa());
    }

    public boolean isSituacaoCadastralAguardandoConfirmacaoCadastro() {
        return SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO.equals(situacaoCadastralPessoa);
    }

    public boolean isSituacaoCadastralAtivo() {
        return SituacaoCadastralPessoa.ATIVO.equals(situacaoCadastralPessoa);
    }

    public boolean hasEmail() {
        return email != null && !email.trim().isEmpty();
    }

    public String getCodigoCnaeBI() {
        return codigoCnaeBI;
    }

    public void setCodigoCnaeBI(String codigoCnaeBI) {
        this.codigoCnaeBI = codigoCnaeBI;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Boolean getReceitaBrutaCPRB() {
        if (receitaBrutaCPRB == null) {
            receitaBrutaCPRB = Boolean.FALSE;
        }
        return receitaBrutaCPRB;
    }

    public void setReceitaBrutaCPRB(Boolean receitaBrutaCPRB) {
        this.receitaBrutaCPRB = receitaBrutaCPRB;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    @Override
    public PessoaNfseDTO toNfseDto() {
        DadosPessoaisNfseDTO dadosPessoais = new DadosPessoaisNfseDTO();
        dadosPessoais.setIdPessoa(getId());
        dadosPessoais.setCpfCnpj(getCpf_Cnpj());
        dadosPessoais.setNomeRazaoSocial(getNome());
        dadosPessoais.setEmail(getEmail());
        dadosPessoais.setSite(getHomePage());
        try {
            dadosPessoais.setInscricaoEstadualRg(getRg_InscricaoEstadual());
            Telefone fone = getTelefonePorTipo(TipoTelefone.FIXO);
            if (fone != null) {
                dadosPessoais.setTelefone(fone.getTelefone());
            }
            fone = getTelefonePorTipo(TipoTelefone.CELULAR);
            if (fone != null) {
                dadosPessoais.setCelular(fone.getTelefone());
            }
            EnderecoCorreio enderecoCorreio = getEnderecoPorPrioridade();
            if (enderecoCorreio != null) {
                dadosPessoais.setCep(enderecoCorreio.getCep());
                dadosPessoais.setLogradouro(enderecoCorreio.getLogradouro());
                dadosPessoais.setNumero(enderecoCorreio.getNumero());
                dadosPessoais.setComplemento(enderecoCorreio.getComplemento());
                dadosPessoais.setBairro(enderecoCorreio.getBairro());
                if (!Strings.isNullOrEmpty(enderecoCorreio.getLocalidade()) && !Strings.isNullOrEmpty(enderecoCorreio.getUf()))
                    dadosPessoais.setMunicipio(new MunicipioNfseDTO(enderecoCorreio.getLocalidade(), enderecoCorreio.getUf()));
            }
            if (this instanceof PessoaJuridica) {
                dadosPessoais.setInscricaoMunicipal(((PessoaJuridica) this).getInscricaoMunicipal());
            }
        } catch (Exception e) {
            log.debug("Não foi possível criar as dependencias LAzy da PessoaNfseDTO");
        }


        return new PessoaNfseDTO(getId(), dadosPessoais);
    }

    public PessoaNfseDTO toSimpleNfseDto() {
        DadosPessoaisNfseDTO dadosPessoais = new DadosPessoaisNfseDTO();
        dadosPessoais.setIdPessoa(getId());
        dadosPessoais.setCpfCnpj(getCpf_Cnpj());
        dadosPessoais.setNomeRazaoSocial(getNome());
        dadosPessoais.setEmail(getEmail());
        return new PessoaNfseDTO(getId(), dadosPessoais);
    }

    public List<RepresentanteLegalPessoa> getRepresentantesLegal() {
        return representantesLegal;
    }

    public void setRepresentantesLegal(List<RepresentanteLegalPessoa> representantesLegal) {
        this.representantesLegal = representantesLegal;
    }

    public List<HistoricoSituacaoPessoa> getHistoricoSituacoesPessoa() {
        return historicoSituacoesPessoa;
    }

    public void setHistoricoSituacoesPessoa(List<HistoricoSituacaoPessoa> historicoSituacoesPessoa) {
        this.historicoSituacoesPessoa = historicoSituacoesPessoa;
    }

    public void adicionarHistoricoSituacaoCadastral() {
        if (this.getSituacaoCadastralPessoa() != null) {
            HistoricoSituacaoPessoa historicoSituacao = new HistoricoSituacaoPessoa();
            historicoSituacao.setPessoa(this);
            historicoSituacao.setSituacaoCadastralPessoa(this.getSituacaoCadastralPessoa());
            historicoSituacao.setDataSituacao(new Date());
            this.getHistoricoSituacoesPessoa().add(historicoSituacao);
        }
    }

    public List<PessoaHorarioFuncionamento> getHorariosFuncionamento() {
        return horariosFuncionamento;
    }

    public void setHorariosFuncionamento(List<PessoaHorarioFuncionamento> horariosFuncionamento) {
        this.horariosFuncionamento = horariosFuncionamento;
    }

    public List<PessoaHorarioFuncionamento> getHorariosFuncionamentoAtivos() {
        List<PessoaHorarioFuncionamento> ativos = Lists.newArrayList();
        for (PessoaHorarioFuncionamento pessoaHorarioFuncionamento : this.getHorariosFuncionamento()) {
            if (pessoaHorarioFuncionamento.getAtivo()) {
                ativos.add(pessoaHorarioFuncionamento);
            }
        }
        return ativos;
    }

    public EnderecoCorreio getEnderecoPorPrioridade() {
        if (getEnderecoPrincipal() != null) {
            return getEnderecoPrincipal();
        }
        if (enderecoscorreio != null && !enderecoscorreio.isEmpty()) {
            EnderecoCorreio endereco = getEnderecoMarcadoPrincipal();
            if (endereco != null) {
                return endereco;
            }
            endereco = buscarEndereco(enderecoscorreio, TipoEndereco.RESIDENCIAL);
            if (endereco != null) {
                return endereco;
            }
            endereco = buscarEndereco(enderecoscorreio, TipoEndereco.CORRESPONDENCIA);
            if (endereco != null) {
                return endereco;
            }
            endereco = buscarEndereco(enderecoscorreio, TipoEndereco.COMERCIAL);
            if (endereco != null) {
                return endereco;
            }
            endereco = buscarEndereco(enderecoscorreio, null);
            if (endereco != null) {
                return endereco;
            }

        }
        return null;
    }

    public EnderecoCorreio getEnderecoMarcadoPrincipal() {
        if (enderecoscorreio != null) {
            return enderecoscorreio.stream()
                .filter(EnderecoCorreio::getPrincipal)
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    public EnderecoCorreio buscarEndereco(List<EnderecoCorreio> enderecos, TipoEndereco tipo) {
        for (EnderecoCorreio endereco : enderecos) {
            if (tipo == null) {
                return endereco;
            }
            if (tipo.equals(endereco.getTipoEndereco())) {
                return endereco;
            }
        }
        return null;
    }
}




