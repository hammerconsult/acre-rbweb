/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.cadastrofuncional.TempoContratoFPPessoa;
import br.com.webpublico.entidades.rh.portal.atualizacaocadastral.PessoaFisicaPortal;
import br.com.webpublico.entidadesauxiliares.FileUploadPrimeFaces;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.esocial.SituacaoQualificacaoCadastral;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.pessoa.dto.FormacaoPessoaDTO;
import br.com.webpublico.pessoa.dto.PessoaFisicaDTO;
import br.com.webpublico.util.OuvinteDeEntidade;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.UtilEntidades;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Jaime
 */
@GrupoDiagrama(nome = "CadastroUnico")
@Audited
@Entity
@EntityListeners(OuvinteDeEntidade.class)

@Inheritance(strategy = InheritanceType.JOINED)
@Etiqueta("Pessoa Física")
public class PessoaFisica extends Pessoa implements Serializable {

    @Obrigatorio
    @Tabelavel
    @Column(length = 70)
    @Pesquisavel
    @Etiqueta("Nome")
    private String nome;
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("CPF")
    private String cpf;
    @Column
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data de Nascimento")
    private Date dataNascimento;
    @Column(length = 70)
    @Tabelavel
    private String pai;
    @Column(length = 70)
    @Tabelavel
    private String mae;
    @Column
    @Enumerated(EnumType.STRING)
    @Etiqueta("Sexo")
    @Tabelavel(campoSelecionado = false)
    private Sexo sexo;
    @Etiqueta("Raça/Cor")
    @Column
    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    private RacaCor racaCor;
    @Column
    @Etiqueta("Deficiente Físico")
    @Tabelavel(campoSelecionado = false)
    private Boolean deficienteFisico;
    @Enumerated(EnumType.STRING)
    @Column
    @Etiqueta("Tipo Sanguíneo")
    @Tabelavel(campoSelecionado = false)
    private TipoSanguineo tipoSanguineo;
    @Column
    @Tabelavel(campoSelecionado = false)
    private Boolean doadorSangue;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Estado Civil")
    @Tabelavel(campoSelecionado = false)
    private EstadoCivil estadoCivil;
    @ManyToOne
    private Cidade naturalidade;
    @ManyToOne
    private NivelEscolaridade nivelEscolaridade;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "pessoaFisica")
    private List<DocumentoPessoal> documentosPessoais;
    @Column
    @Etiqueta("Tipo de Deficiência")
    @Enumerated(EnumType.STRING)
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private TipoDeficiencia tipoDeficiencia;
    private Integer anoChegada;
    @Etiqueta("Data da Invalidez")
    @Temporal(TemporalType.DATE)
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Date dataInvalidez;
    @Etiqueta("Nome Abreviado")
    private String nomeAbreviado;
    @Etiqueta("Nome Tratamento/Social")
    @Pesquisavel
    @Tabelavel
    private String nomeTratamento;
    @Etiqueta("Nacionalidade do Pai")
    @ManyToOne
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Nacionalidade nacionalidadePai;
    @Etiqueta("Nacionalidade da Mãe")
    @ManyToOne
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Nacionalidade nacionalidadeMae;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Qualificação Cadastral")
    private SituacaoQualificacaoCadastral situacaoQualificacaoCadastral;
    @Invisivel
    @OneToOne(mappedBy = "pessoaFisica", cascade = CascadeType.ALL)
    private PessoaFisicaCid pessoaFisicaCid;
    @Etiqueta("Conselho Classe Ordem")
    @OneToMany(mappedBy = "pessoaFisica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ConselhoClasseContratoFP> conselhoClasseContratos;
    @ManyToOne
    private EsferaGoverno esferaGoverno;
    @Transient
    private FileUploadPrimeFaces fileUploadPrimeFaces;
    @OneToMany(mappedBy = "pessoaFisica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Formações")
    private List<MatriculaFormacao> formacoes;
    @OneToMany(mappedBy = "pessoaFisica", cascade = CascadeType.ALL)
    @Etiqueta("Habilidades")
    private List<PessoaHabilidade> habilidades;
    @OneToMany(mappedBy = "responsavel", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Dependentes")
    private List<Dependente> dependentes;
    private Boolean viveUniaoEstavel;
    @OneToMany(mappedBy = "pessoaFisica", cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private List<TempoContratoFPPessoa> itemTempoContratoFPPessoa;
    @Transient
    private boolean fichaJaExcluidas;
    @Enumerated(EnumType.STRING)
    private TipoCondicaoIngresso tipoCondicaoIngresso;
    private Boolean casadoBrasileiro;
    private Boolean filhoBrasileiro;
    private Boolean cotaDeficiencia;
    private String observacaoCotaDeficiencia;
    private Boolean acumulaCargo;
    private String orgao;
    private String localTrabalho;
    @Transient
    private ConselhoClasseContratoFP conselhoClasseContratoFP;
    @Transient
    private String siglaConselhoOrdem;
    @Transient
    private String numDoc;

    public PessoaFisica() {
        super();
        this.documentosPessoais = new ArrayList<>();
        this.conselhoClasseContratos = new ArrayList<>();
        fileUploadPrimeFaces = new FileUploadPrimeFaces();
        dependentes = new ArrayList<>();
        formacoes = new ArrayList<>();
        habilidades = new ArrayList<>();
        situacaoQualificacaoCadastral = SituacaoQualificacaoCadastral.NAO_QUALIFICADO;
        casadoBrasileiro = false;
        filhoBrasileiro = false;
        cotaDeficiencia = false;
        itemTempoContratoFPPessoa = Lists.newArrayList();
    }

    public PessoaFisica(String email, String homePage, Nacionalidade nacionalidade, List<EnderecoCorreio> enderecos, List<Telefone> telefones, EscritorioContabil escritorioContabil, String nome, Date dataNascimento, Sexo sexo, String pai, String mae, RacaCor racaCor, Boolean deficienteFisico, TipoSanguineo tipoSanguineo, Boolean doadorSangue, EstadoCivil estadoCivil, Cidade naturalidade, NivelEscolaridade nivelEscolaridade, List<DocumentoPessoal> documentosPessoais, Date dataInvalidez, String nomeAbreviado, String nomeTratamento, String formacao, Nacionalidade nacionalidadePai, Nacionalidade nacionalidadeMae, String observacao, Boolean viveUniaoEstavel) {
        super(email, homePage, nacionalidade, enderecos, telefones, escritorioContabil, observacao);
        this.nome = nome;
        this.dataNascimento = dataNascimento;
        this.sexo = sexo;
        this.pai = pai;
        this.mae = mae;
        this.racaCor = racaCor;
        this.deficienteFisico = deficienteFisico;
        this.tipoSanguineo = tipoSanguineo;
        this.doadorSangue = doadorSangue;
        this.estadoCivil = estadoCivil;
        this.naturalidade = naturalidade;
        this.nivelEscolaridade = nivelEscolaridade;
        this.documentosPessoais = documentosPessoais;
        this.dataInvalidez = dataInvalidez;
        this.nomeAbreviado = nomeAbreviado;
        this.nomeTratamento = nomeTratamento;
        this.nacionalidadePai = nacionalidadePai;
        this.nacionalidadeMae = nacionalidadeMae;
        this.viveUniaoEstavel = viveUniaoEstavel;
    }

    public PessoaFisica(Long id, String nome) {
        super(id);
        this.nome = nome;
    }

    public PessoaFisica(Long id, String nome, String cpf) {
        super(id);
        this.cpf = cpf;
        this.nome = nome;
    }

    public PessoaFisica(Long id, String nome, String siglaConselhoOrdem, String numeroDoc) {
        super(id);
        this.nome = nome;
        this.siglaConselhoOrdem = siglaConselhoOrdem;
        this.numDoc = numeroDoc;

    }

    public static PessoaFisica dtoToPessoaFisica(PessoaFisicaDTO pessoaFisicaDTO) {
        PessoaFisica pf = new PessoaFisica();
        pf.setId(pessoaFisicaDTO.getId());
        return pf;
    }

    public static PessoaFisica pessoaPortalToPessoaFisica(PessoaFisica p, PessoaFisicaPortal pessoaPortal) {

        p.setNome(pessoaPortal.getNome());
        p.setNomeAbreviado(pessoaPortal.getNomeAbreviado());
        p.setNomeTratamento(pessoaPortal.getNomeTratamento());
        p.setCpf(pessoaPortal.getCpf());
        p.setDataNascimento(pessoaPortal.getDataNascimento());
        p.setSexo(pessoaPortal.getSexo());
        p.setPai(pessoaPortal.getPai());
        p.setNacionalidadePai(pessoaPortal.getNacionalidadePai());
        p.setMae(pessoaPortal.getMae());
        p.setNacionalidadeMae(pessoaPortal.getNacionalidadeMae());
        p.setEmail(pessoaPortal.getEmail());
        p.setHomePage(pessoaPortal.getHomePage() != null ? pessoaPortal.getHomePage() : "");
        p.setRacaCor(pessoaPortal.getRacaCor());
        p.setTipoDeficiencia(pessoaPortal.getTipoDeficiencia());
        p.setDataInvalidez(pessoaPortal.getDeficienteDesde());
        p.setPessoaFisicaCid((pessoaPortal.getCid() != null && pessoaPortal.getDeficienteDesde() != null) ? new PessoaFisicaCid(p, pessoaPortal.getCid(), pessoaPortal.getDeficienteDesde()) : null);
        p.setTipoSanguineo(pessoaPortal.getTipoSanguineo());
        p.setDoadorSangue(pessoaPortal.getDoadorSanguineo());
        p.setEstadoCivil(pessoaPortal.getEstadoCivil());
        p.setNaturalidade(pessoaPortal.getNaturalidade());
        p.setNacionalidade(pessoaPortal.getNacionalidade());
        p.setAnoChegada(pessoaPortal.getAnoChegada());
        p.setNivelEscolaridade(pessoaPortal.getNivelEsolaridade());
        p.setTipoCondicaoIngresso(pessoaPortal.getTipoCondicaoIngresso());
        p.setCasadoBrasileiro(pessoaPortal.getCasadoBrasileiro());
        p.setFilhoBrasileiro(pessoaPortal.getFilhoBrasileiro());
        p.setAcumulaCargo(pessoaPortal.getAcumulaCargo());
        p.setOrgao(pessoaPortal.getOrgao());
        p.setLocalTrabalho(pessoaPortal.getLocalTrabalho());
        return p;
    }

    public Boolean getCasadoBrasileiro() {
        return casadoBrasileiro == null ? false : casadoBrasileiro;
    }

    public void setCasadoBrasileiro(Boolean casadoBrasileiro) {
        this.casadoBrasileiro = casadoBrasileiro;
    }

    public Boolean getFilhoBrasileiro() {
        return filhoBrasileiro == null ? false : filhoBrasileiro;
    }

    public void setFilhoBrasileiro(Boolean filhoBrasileiro) {
        this.filhoBrasileiro = filhoBrasileiro;
    }

    public Boolean getCotaDeficiencia() {
        return cotaDeficiencia == null ? false : cotaDeficiencia;
    }

    public void setCotaDeficiencia(Boolean cotaDeficiencia) {
        this.cotaDeficiencia = cotaDeficiencia;
    }

    public String getObservacaoCotaDeficiencia() {
        return observacaoCotaDeficiencia;
    }

    public void setObservacaoCotaDeficiencia(String observacaoCotaDeficiencia) {
        this.observacaoCotaDeficiencia = observacaoCotaDeficiencia;
    }

    public FileUploadPrimeFaces getFileUploadPrimeFaces() {
        return fileUploadPrimeFaces;
    }

    public void setFileUploadPrimeFaces(FileUploadPrimeFaces fileUploadPrimeFaces) {
        this.fileUploadPrimeFaces = fileUploadPrimeFaces;
    }

    public List<TempoContratoFPPessoa> getItemTempoContratoFPPessoa() {
        return itemTempoContratoFPPessoa;
    }

    public void setItemTempoContratoFPPessoa(List<TempoContratoFPPessoa> itemTempoContratoFPPessoa) {
        this.itemTempoContratoFPPessoa = itemTempoContratoFPPessoa;
    }

    public List<ConselhoClasseContratoFP> getConselhoClasseContratos() {
        return conselhoClasseContratos;
    }

    public void setConselhoClasseContratos(List<ConselhoClasseContratoFP> conselhoClasseContrato) {
        this.conselhoClasseContratos = conselhoClasseContrato;
    }

    public TipoCondicaoIngresso getTipoCondicaoIngresso() {
        return tipoCondicaoIngresso;
    }

    public void setTipoCondicaoIngresso(TipoCondicaoIngresso tipoCondicaoIngresso) {
        this.tipoCondicaoIngresso = tipoCondicaoIngresso;
    }

    public PessoaFisicaCid getPessoaFisicaCid() {
        return pessoaFisicaCid;
    }

    public void setPessoaFisicaCid(PessoaFisicaCid pessoaFisicaCid) {
        this.pessoaFisicaCid = pessoaFisicaCid;
    }

    public SituacaoQualificacaoCadastral getSituacaoQualificacaoCadastral() {
        return situacaoQualificacaoCadastral;
    }

    public void setSituacaoQualificacaoCadastral(SituacaoQualificacaoCadastral situacaoQualificacaoCadastral) {
        this.situacaoQualificacaoCadastral = situacaoQualificacaoCadastral;
    }

    public Nacionalidade getNacionalidadeMae() {
        return nacionalidadeMae;
    }

    public void setNacionalidadeMae(Nacionalidade nacionalidadeMae) {
        this.nacionalidadeMae = nacionalidadeMae;
    }

    public Nacionalidade getNacionalidadePai() {
        return nacionalidadePai;
    }

    public void setNacionalidadePai(Nacionalidade nacionalidadePai) {
        this.nacionalidadePai = nacionalidadePai;
    }

    public String getNomeAbreviado() {
        return nomeAbreviado;
    }

    public void setNomeAbreviado(String nomeAbreviado) {
        this.nomeAbreviado = nomeAbreviado;
    }

    @Override
    public String getNomeTratamento() {
        return nomeTratamento != null ? nomeTratamento : "";
    }

    public void setNomeTratamento(String nomeTratamento) {
        this.nomeTratamento = nomeTratamento;
    }

    public Date getDataInvalidez() {
        return dataInvalidez;
    }

    public void setDataInvalidez(Date dataInvalidez) {
        this.dataInvalidez = dataInvalidez;
    }

    public String getCpf() {
        return UtilEntidades.formatarCpf(cpf);
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public List<MatriculaFormacao> getFormacoes() {
        return formacoes;
    }

    public void setFormacoes(List<MatriculaFormacao> formacoes) {
        this.formacoes = formacoes;
    }

    public List<PessoaHabilidade> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<PessoaHabilidade> habilidades) {
        this.habilidades = habilidades;
    }

    public String getDataNascimentoFormatada() {
        if (this.dataNascimento != null) {
            SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");
            return formatoData.format(this.dataNascimento);
        }
        return "";
    }

    public String getDataNascimentoFormatada(String mask) {
        if (this.dataNascimento != null) {
            SimpleDateFormat formatoData = new SimpleDateFormat(mask);
            return formatoData.format(this.dataNascimento);
        }
        return "";
    }

    public Boolean getDeficienteFisico() {
        return deficienteFisico != null ? deficienteFisico : false;
    }

    public void setDeficienteFisico(Boolean deficienteFisico) {
        this.deficienteFisico = deficienteFisico;
    }

    public Boolean getDoadorSangue() {
        return doadorSangue;
    }

    public void setDoadorSangue(Boolean doadorSangue) {
        this.doadorSangue = doadorSangue;
    }

    public List<DocumentoPessoal> getDocumentosPessoais() {
        return documentosPessoais;
    }

    public void setDocumentosPessoais(List<DocumentoPessoal> documentosPessoais) {
        this.documentosPessoais = documentosPessoais;
    }

    public String getMae() {
        return mae;
    }

    public void setMae(String mae) {
        this.mae = mae;
    }

    public Cidade getNaturalidade() {
        return naturalidade;
    }

    public void setNaturalidade(Cidade naturalidade) {
        this.naturalidade = naturalidade;
    }

    public NivelEscolaridade getNivelEscolaridade() {
        return nivelEscolaridade;
    }

    public void setNivelEscolaridade(NivelEscolaridade nivelEscolaridade) {
        this.nivelEscolaridade = nivelEscolaridade;
    }

    @Override
    public String getNome() {
        return nome != null ? nome : "";
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getNomeFantasia() {
        return nome != null ? nome : "";
    }

    public String getPrimeiroNome() {
        if (nome.contains(" ")) {
            String[] split = nome.split(" ");
            return StringUtil.primeiroCaracterMaiusculo(split[0].toLowerCase());
        }
        return nome;
    }

    public String getUltimoNome() {
        if (nome.contains(" ")) {
            String[] split = nome.split(" ");
            return StringUtil.primeiroCaracterMaiusculo(split[split.length - 1].toLowerCase());
        }
        return nome;
    }

    public String getPai() {
        return pai;
    }

    public void setPai(String pai) {
        this.pai = pai;
    }

    public RacaCor getRacaCor() {
        return racaCor;
    }

    public void setRacaCor(RacaCor racaCor) {
        this.racaCor = racaCor;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public TipoSanguineo getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(TipoSanguineo tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public TipoDeficiencia getTipoDeficiencia() {
        return tipoDeficiencia;
    }

    public void setTipoDeficiencia(TipoDeficiencia tipoDeficiencia) {
        this.tipoDeficiencia = tipoDeficiencia;
    }

    public List<Dependente> getDependentes() {
        return dependentes;
    }

    public void setDependentes(List<Dependente> dependentes) {
        this.dependentes = dependentes;
    }

    public List<Dependente> getDependentesAtivos() {
        List<Dependente> dependentesAtivos = Lists.newArrayList();
        if (dependentes != null) {
            for (Dependente dependente : dependentes) {
                if (dependente.getAtivo()) {
                    dependentesAtivos.add(dependente);
                }
            }
        }
        return dependentesAtivos;
    }

    @Override
    public String toString() {
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

        return sb.toString();
    }

    @Override
    public String getCpf_Cnpj() {
        return getCpf() != null ? getCpf() : " ";
    }

    @Override
    public String getRg_InscricaoEstadual() {
        if (getRg() != null) {
            return getRg().getNumero();
        }
        return "";
    }

    @Override
    public String getOrgaoExpedidor() {
        if (getRg() != null) {
            return getRg().getOrgaoEmissao();
        }
        return "";
    }

    public RG getRg() {
        if (this.documentosPessoais != null && !this.documentosPessoais.isEmpty()) {
            List<RG> rgs = Lists.newArrayList();
            for (DocumentoPessoal documentoPessoal : this.documentosPessoais) {
                if (documentoPessoal instanceof RG && ((RG) documentoPessoal).getNumero() != null) {
                    rgs.add((RG) documentoPessoal);
                }
            }
            Collections.sort(rgs, new Comparator<RG>() {
                @Override
                public int compare(RG o1, RG o2) {
                    if (o1 == null || o2 == null) {
                        return 1;
                    }
                    if (o1.getDataemissao() == null || o2.getDataemissao() == null) {
                        return 1;
                    }
                    if (o2.getDataemissao() == null) {
                        return 0;
                    }
                    return o2.getDataemissao().compareTo(o1.getDataemissao());
                }
            });
            if (!rgs.isEmpty()) {
                return rgs.get(0);
            }
        }


        return null;
    }

    public Emancipacao getEmancipacao() {
        if (this.documentosPessoais != null && !this.documentosPessoais.isEmpty()) {
            List<Emancipacao> emancipacoes = Lists.newArrayList();
            for (DocumentoPessoal documentoPessoal : this.documentosPessoais) {
                if (documentoPessoal instanceof Emancipacao
                    && documentoPessoal.getDetentorArquivoComposicao() != null
                    && documentoPessoal.getDetentorArquivoComposicao().getArquivoComposicao() != null) {
                    emancipacoes.add((Emancipacao) documentoPessoal);
                }
            }
            Collections.sort(emancipacoes, new Comparator<Emancipacao>() {
                @Override
                public int compare(Emancipacao o1, Emancipacao o2) {
                    if (o1 == null || o2 == null) {
                        return 1;
                    }
                    if (o1.getNumero() == null || o2.getNumero() == null) {
                        return 1;
                    }
                    if (o2.getNumero() == null) {
                        return 0;
                    }
                    return o2.getNumero().compareTo(o1.getNumero());
                }
            });
            if (!emancipacoes.isEmpty()) {
                return emancipacoes.get(0);
            }
        }
        return null;
    }

    public DocumentoNacionalIdentidade getDocumentoNacionalIdentidade() {
        if (this.documentosPessoais != null && !this.documentosPessoais.isEmpty()) {
            List<DocumentoNacionalIdentidade> docs = Lists.newArrayList();
            for (DocumentoPessoal documentoPessoal : this.documentosPessoais) {
                if (documentoPessoal instanceof DocumentoNacionalIdentidade && ((DocumentoNacionalIdentidade) documentoPessoal).getDni() != null) {
                    docs.add((DocumentoNacionalIdentidade) documentoPessoal);
                }
            }
            Collections.sort(docs, new Comparator<DocumentoNacionalIdentidade>() {
                @Override
                public int compare(DocumentoNacionalIdentidade o1, DocumentoNacionalIdentidade o2) {
                    if (o1 == null || o2 == null) {
                        return 1;
                    }
                    if (o1.getDataExpedicao() == null || o2.getDataExpedicao() == null) {
                        return 1;
                    }
                    if (o2.getDataExpedicao() == null) {
                        return 0;
                    }
                    return o2.getDataExpedicao().compareTo(o1.getDataExpedicao());
                }
            });
            if (!docs.isEmpty()) {
                return docs.get(0);
            }
        }
        return null;
    }

    public Habilitacao getHabilitacao() {
        if (this.documentosPessoais != null && !this.documentosPessoais.isEmpty()) {
            List<Habilitacao> habilitacaos = new ArrayList();
            for (DocumentoPessoal documentoPessoal : this.documentosPessoais) {
                if (documentoPessoal instanceof Habilitacao) {
                    habilitacaos.add((Habilitacao) documentoPessoal);
                }
            }
            if (habilitacaos.size() > 0) {
                Collections.sort(habilitacaos, new Comparator<Habilitacao>() {
                    @Override
                    public int compare(Habilitacao o1, Habilitacao o2) {
                        if (o1 == null || o2 == null) {
                            return 0;
                        }
                        if (o1.getValidade() == null || o2.getValidade() == null) {
                            return 0;
                        }
                        return o2.getValidade().compareTo(o1.getValidade());
                    }
                });
                return habilitacaos.get(0);
            }
        }
        return null;
    }

    public SituacaoMilitar getSituacaoMilitar() {
        if (this.documentosPessoais != null && !this.documentosPessoais.isEmpty()) {
            for (DocumentoPessoal documentoPessoal : this.documentosPessoais) {
                if (documentoPessoal instanceof SituacaoMilitar) {
                    return (SituacaoMilitar) documentoPessoal;
                }
            }
        }
        return null;
    }

    public Integer getAnoChegada() {
        return anoChegada;
    }

    public void setAnoChegada(Integer anoChegada) {
        this.anoChegada = anoChegada;
    }

    @Override
    public int compareTo(Pessoa o) {
        try {
            return this.nome.compareTo(o.getNome());
        } catch (Exception e) {
            return 0;
        }
    }

    public CertidaoNascimento getCertidaoNascimento() {
        if (this.documentosPessoais != null && !this.documentosPessoais.isEmpty()) {
            for (DocumentoPessoal documentoPessoal : this.documentosPessoais) {
                if (documentoPessoal instanceof CertidaoNascimento) {
                    return (CertidaoNascimento) documentoPessoal;
                }
            }
        }
        return null;
    }

    public Boolean possuiDeficienciaFisica() throws NullPointerException {
        if (this.tipoDeficiencia.equals(TipoDeficiencia.AUDITIVA)
            || this.tipoDeficiencia.equals(TipoDeficiencia.FISICA)
            || this.tipoDeficiencia.equals(TipoDeficiencia.MENTAL)
            || this.tipoDeficiencia.equals(TipoDeficiencia.MULTIPLA)
            || this.tipoDeficiencia.equals(TipoDeficiencia.REABILITADO)
            || this.tipoDeficiencia.equals(TipoDeficiencia.VISUAL)
            || this.tipoDeficiencia.equals(TipoDeficiencia.PSICOSSOCIAL)
            || this.tipoDeficiencia.equals(TipoDeficiencia.OUTROS)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public Habilitacao getCNH() {
        if (this.documentosPessoais != null && !this.documentosPessoais.isEmpty()) {
            for (DocumentoPessoal documentoPessoal : this.documentosPessoais) {
                if (documentoPessoal instanceof Habilitacao) {
                    return (Habilitacao) documentoPessoal;
                }
            }
        }
        return null;
    }

    public TituloEleitor getTituloEleitor() {
        if (this.documentosPessoais != null && !this.documentosPessoais.isEmpty()) {
            for (DocumentoPessoal documentoPessoal : this.documentosPessoais) {
                if (documentoPessoal instanceof TituloEleitor) {
                    return (TituloEleitor) documentoPessoal;
                }
            }
        }
        return null;
    }

    public CarteiraTrabalho getCarteiraDeTrabalho() {
        if (this.documentosPessoais != null && !this.documentosPessoais.isEmpty()) {
            for (DocumentoPessoal documentoPessoal : this.documentosPessoais) {
                if (documentoPessoal instanceof CarteiraTrabalho) {
                    return (CarteiraTrabalho) documentoPessoal;
                }
            }
        }
        return null;
    }

    public CertidaoCasamento getCertidaoCasamento() {
        if (this.documentosPessoais != null && !this.documentosPessoais.isEmpty()) {
            for (DocumentoPessoal documentoPessoal : this.documentosPessoais) {
                if (documentoPessoal instanceof CertidaoCasamento) {
                    return (CertidaoCasamento) documentoPessoal;
                }
            }
        }
        return null;
    }

    public EsferaGoverno getEsferaGoverno() {
        return esferaGoverno;
    }

    public void setEsferaGoverno(EsferaGoverno esferaGoverno) {
        this.esferaGoverno = esferaGoverno;
    }

    @Override
    public TipoEmpresa getTipoEmpresa() {
        return null;
    }

    public boolean temConjugeOrCompanheiro() {
        return isEstadoCivilCasado();
    }

    public boolean isEstadoCivilUniaoEstavel() {
        return viveUniaoEstavel != null ? viveUniaoEstavel : false;
    }

    public boolean isEstadoCivilCasado() {
        return EstadoCivil.CASADO.equals(estadoCivil);
    }

    public boolean isSexoMasculino() {
        return Sexo.MASCULINO.equals(sexo);
    }

    public boolean temNivelEscolaridade() {
        return nivelEscolaridade != null;
    }

    public PessoaFisicaDTO toPessoaFisicaDTO() {
        return toPessoaFisicaDTO(null);
    }

    public PessoaFisicaDTO toPessoaFisicaDTO(HierarquiaOrganizacional ho) {
        PessoaFisica pessoa = this;

        PessoaFisicaDTO dto = new PessoaFisicaDTO();
        dto.setId(pessoa.getId());
        dto.setNome(pessoa.getNome());
        dto.setNomeAbreviado(pessoa.getNomeAbreviado());
        dto.setNomeTratamento(pessoa.getNomeTratamento());
        dto.setCpf(pessoa.getCpf());
        dto.setDataNascimento(pessoa.getDataNascimento());
        dto.setSexo(pessoa.getSexo() != null ? br.com.webpublico.pessoa.enumeration.Sexo.valueOf(pessoa.getSexo().name()) : null);
        dto.setPai(pessoa.getPai());
        dto.setNacionalidadePai(Nacionalidade.toNacionalidadeDTO(pessoa.getNacionalidadePai()));
        dto.setMae(pessoa.getMae());
        dto.setNacionalidadeMae(Nacionalidade.toNacionalidadeDTO(pessoa.getNacionalidadeMae()));
        dto.setEmail(pessoa.getEmail());
        dto.setHomePage(pessoa.getHomePage());
        dto.setRacaCor(pessoa.getRacaCor() != null ? br.com.webpublico.pessoa.enumeration.RacaCor.valueOf(pessoa.getRacaCor().name()) : null);
        dto.setTipoDeficiencia(pessoa.getTipoDeficiencia() != null ? br.com.webpublico.pessoa.enumeration.TipoDeficiencia.valueOf(pessoa.getTipoDeficiencia().name()) : null);
        dto.setDeficienteDesde(pessoa.getDataInvalidez());
        dto.setCid(pessoa.getPessoaFisicaCid() != null ? CID.toCidDTO(pessoa.getPessoaFisicaCid().getCid()) : null);
        dto.setTipoSanguineo(pessoa.getTipoSanguineo() != null ? br.com.webpublico.pessoa.enumeration.TipoSanguineo.valueOf(pessoa.getTipoSanguineo().name()) : null);
        dto.setDoadorSanguineo(pessoa.getDoadorSangue());
        dto.setEstadoCivil(pessoa.getEstadoCivil() != null ? br.com.webpublico.pessoa.enumeration.EstadoCivil.valueOf(pessoa.getEstadoCivil().name()) : null);
        dto.setNaturalidade(Cidade.toCidadeDTO(pessoa.getNaturalidade()));
        dto.setNacionalidade(Nacionalidade.toNacionalidadeDTO(pessoa.getNacionalidade()));
        dto.setAnoChegada(pessoa.getAnoChegada());
        dto.setNivelEsolaridade(NivelEscolaridade.toNivelEscolaridadeDTO(pessoa.getNivelEscolaridade()));

        dto.setAcumulaCargo(pessoa.getAcumulaCargo());
        dto.setOrgao(pessoa.getOrgao());
        dto.setLocalTrabalho(pessoa.getLocalTrabalho());
        dto.setRg(RG.toRGDTO(pessoa.getRg()));
        dto.setTituloEleitor(TituloEleitor.toTituloEleitorDTO(pessoa.getTituloEleitor()));
        dto.setHabilitacao(pessoa.getHabilitacao() != null ? Habilitacao.toHabilitacoesDTO(Arrays.asList(pessoa.getHabilitacao())) : null);
        dto.setCarteiraTrabalho(CarteiraTrabalho.toCarteiraTrabalhoDTO(pessoa.getCarteiraDeTrabalho()));
        dto.setSituacaoMilitar(pessoa.getSituacaoMilitar() != null ? SituacaoMilitar.toSituacaoMilitarDTO(pessoa.getSituacaoMilitar()) : null);
        dto.setCertidaoNascimento(CertidaoNascimento.toCertidaoNascimentoDTO(pessoa.getCertidaoNascimento()));
        dto.setCertidaoCasamento(CertidaoCasamento.toCertidaoCasamento(pessoa.getCertidaoCasamento()));
        dto.setTelefones(Telefone.toTelefones(pessoa.getTelefones()));
        dto.setEnderecos(EnderecoCorreio.toEnderecoCorreioDTOs(pessoa.getEnderecos()));
        dto.setConselhos(ConselhoClasseContratoFP.toConselhoClasseOrdemDTOs(pessoa.getConselhoClasseContratos()));
        if (pessoa.getFormacoes() == null || pessoa.getFormacoes().isEmpty()) {
            dto.setFormacoesPessoa(Lists.<FormacaoPessoaDTO>newArrayList());
        } else {
            for (MatriculaFormacao formacao : pessoa.getFormacoes()) {
                if (formacao.getFormacao() != null) {
                    dto.setFormacoesPessoa(MatriculaFormacao.toFormacoesDTOs(pessoa.getFormacoes()));
                }
            }
        }

        List<Habilidade> habilidades = getHabilidadesPessoa(pessoa.getHabilidades());
        dto.setHabilidades(Habilidade.toHabilidadesDTO(habilidades));
        List<Dependente> dependentes = Lists.newArrayList();
        for (Dependente dependente : pessoa.getDependentes()) {
            if (dependente.getAtivo()) {
                dependentes.add(dependente);
            }
        }
        dto.setDependentes(Dependente.toDependentesDTO(dependentes));
        dto.setContasBancarias(ContaCorrenteBancPessoa.toContaCorrenteBancariaDTOs(pessoa.getContaCorrenteBancPessoas()));
        dto.setHierarquiaOrganizacionalDTO(HierarquiaOrganizacional.toHierarquiaOrganizacionalDTO(ho));
        return dto;
    }

    public br.com.webpublico.tributario.dto.PessoaFisicaDTO toPessoaFisicaDTOTributario() {
        PessoaFisica pessoa = this;

        br.com.webpublico.tributario.dto.PessoaFisicaDTO dto = new br.com.webpublico.tributario.dto.PessoaFisicaDTO();
        dto.setId(pessoa.getId());
        dto.setNome(pessoa.getNome());
        dto.setCpf(pessoa.getCpf());
        dto.setDataNascimento(pessoa.getDataNascimento());
        dto.setSexo(pessoa.getSexo() != null ? br.com.webpublico.tributario.enumeration.Sexo.valueOf(pessoa.getSexo().name()) : null);
        dto.setMae(pessoa.getMae());
        dto.setEmail(pessoa.getEmail());
        dto.setHomePage(pessoa.getHomePage());
        dto.setEnderecos(EnderecoCorreio.toEnderecoCorreioDTOsTributario(pessoa.getEnderecos()));
        return dto;
    }

    private List<Habilidade> getHabilidadesPessoa(List<PessoaHabilidade> lista) {
        List<Habilidade> habilidades = Lists.newLinkedList();
        for (PessoaHabilidade habilidade : lista) {
            if (!habilidades.contains(habilidade.getHabilidade())) {
                habilidades.add(habilidade.getHabilidade());
            }
        }

        return habilidades;
    }

    public String getCpfSemFormatacao() {
        if (cpf != null) {
            return StringUtil.removeCaracteresEspeciaisSemEspaco(cpf);
        }
        return cpf;
    }

    public boolean isFichaJaExcluidas() {
        return fichaJaExcluidas;
    }

    public void setFichaJaExcluidas(boolean fichaJaExcluidas) {
        this.fichaJaExcluidas = fichaJaExcluidas;
    }

    public Boolean getViveUniaoEstavel() {
        return viveUniaoEstavel;
    }

    public void setViveUniaoEstavel(Boolean viveUniaoEstavel) {
        this.viveUniaoEstavel = viveUniaoEstavel;
    }

    public Boolean getAcumulaCargo() {
        return acumulaCargo != null ? acumulaCargo : Boolean.FALSE;
    }

    public void setAcumulaCargo(Boolean acumulaCargo) {
        this.acumulaCargo = acumulaCargo;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getLocalTrabalho() {
        return localTrabalho;
    }

    public void setLocalTrabalho(String localTrabalho) {
        this.localTrabalho = localTrabalho;
    }

    public ConselhoClasseContratoFP getConselhoClasseContratoFP() {
        return conselhoClasseContratoFP;
    }

    public void setConselhoClasseContratoFP(ConselhoClasseContratoFP conselhoClasseContratoFP) {
        this.conselhoClasseContratoFP = conselhoClasseContratoFP;
    }

    public String getSiglaConselhoOrdem() {
        return siglaConselhoOrdem;
    }

    public void setSiglaConselhoOrdem(String siglaConselhoOrdem) {
        this.siglaConselhoOrdem = siglaConselhoOrdem;
    }

    public String getNumDoc() {
        return numDoc;
    }

    public void setNumDoc(String numDoc) {
        this.numDoc = numDoc;
    }
}
