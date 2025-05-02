package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.cadastrofuncional.TempoContratoFP;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.SituacaoPessoaPortal;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.pessoa.dto.EnderecoCorreioDTO;
import br.com.webpublico.pessoa.dto.PessoaFisicaDTO;
import br.com.webpublico.pessoa.dto.TelefoneDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by peixe on 29/08/17.
 */
@Entity
@Etiqueta("Pessoa Física Portal")
public class PessoaFisicaPortal extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Pessoa")
    private PessoaFisica pessoaFisica;
    @Tabelavel
    @Pesquisavel
    private String cpf;
    @Tabelavel
    @Pesquisavel
    private String nome;
    private String nomeAbreviado;
    private String nomeTratamento;
    private String nomeSocial;
    @Etiqueta("Data de Nascimento")
    @Temporal(TemporalType.DATE)
    private Date dataNascimento;
    @Enumerated(EnumType.STRING)
    private Sexo sexo;
    private String email;
    private String homePage;
    @ManyToOne
    private NivelEscolaridade nivelEsolaridade;
    @ManyToOne
    private Profissao profissao;
    private String mae;
    private String pai;
    @ManyToOne
    private Nacionalidade nacionalidadeMae;
    @ManyToOne
    private Nacionalidade nacionalidadePai;
    @Enumerated(EnumType.STRING)
    private RacaCor racaCor;
    @Enumerated(EnumType.STRING)
    private TipoDeficiencia tipoDeficiencia;
    @ManyToOne
    private CID cid;
    private Date deficienteDesde;
    @Enumerated(EnumType.STRING)
    private TipoSanguineo tipoSanguineo;
    private Boolean doadorSanguineo;
    @Enumerated(EnumType.STRING)
    private EstadoCivil estadoCivil;
    @ManyToOne
    private Cidade naturalidade;
    @ManyToOne
    private Nacionalidade nacionalidade;
    private Integer anoChegada;

    @ManyToOne(cascade = CascadeType.ALL)
    private RGPortal rg;
    @ManyToOne(cascade = CascadeType.ALL)
    private TituloEleitorPortal tituloEleitor;
    @ManyToOne(cascade = CascadeType.ALL)
    private CarteiraTrabalhoPortal carteiraTrabalho;
    @ManyToOne(cascade = CascadeType.ALL)
    private CertidaoNascimentoPortal certidaoNascimento;
    @ManyToOne(cascade = CascadeType.ALL)
    private CertidaoCasamentoPortal certidaoCasamento;
    @ManyToOne(cascade = CascadeType.ALL)
    private SituacaoMilitarPortal situacaoMilitar;
    @Tabelavel
    @Etiqueta("Data de Aprovação")
    @Temporal(TemporalType.TIMESTAMP)
    @Pesquisavel
    private Date liberadoEm;
    @Tabelavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoPessoaPortal status;
    @ManyToOne
    private UsuarioSistema usuario;
    @Etiqueta("Condição de Ingresso")
    @Enumerated(EnumType.STRING)
    private TipoCondicaoIngresso tipoCondicaoIngresso;
    private Boolean casadoBrasileiro;
    private Boolean filhoBrasileiro;
    private Boolean localEOrgaoCorreto;
    private Boolean acumulaCargo;
    private String orgao;
    private String localTrabalho;
    @Etiqueta("Data de Atualização")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAtualizacao;

    @OneToMany(mappedBy = "pessoaFisicaPortal", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CarteiraHabilitacaoPortal> habilitacao;
    @OneToMany(mappedBy = "pessoaFisicaPortal", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<TelefonePortal> telefones;
    @OneToMany(mappedBy = "pessoaFisicaPortal", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EnderecoCorreioPortal> enderecos;
    @OneToMany(mappedBy = "pessoaFisicaPortal", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<FormacaoPessoaPortal> formacoesPessoa;
    @OneToMany(mappedBy = "pessoaFisicaPortal", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ConselhoClasseOrdemPortal> conselhos;
    @OneToMany(mappedBy = "pessoaFisicaPortal", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<HabilidadePortal> habilidades;
    @OneToMany(mappedBy = "pessoaFisicaPortal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TempoContratoFP> itemTempoContratoFPPessoaPortal;
    @OneToMany(mappedBy = "pessoaFisicaPortal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DependentePortal> dependentes;

    public PessoaFisicaPortal() {
        status = SituacaoPessoaPortal.AGUARDANDO_LIBERACAO;
    }

    public static PessoaFisicaPortal toPessoFisicaPortal(PessoaFisicaDTO dto) {

        PessoaFisicaPortal pessoa = new PessoaFisicaPortal();
        pessoa.setNome(dto.getNome());
        pessoa.setNomeAbreviado(dto.getNomeAbreviado());
        pessoa.setNomeTratamento(dto.getNomeTratamento());
        pessoa.setNomeSocial(dto.getNomeSocial());
        pessoa.setCpf(dto.getCpf());
        pessoa.setLocalEOrgaoCorreto(dto.getLocalEOrgaoCorreto());
        pessoa.setDataNascimento(dto.getDataNascimento());
        pessoa.setSexo(Sexo.valueOf(dto.getSexo().name()));
        pessoa.setPai(dto.getPai());
        pessoa.setNacionalidadePai(dto.getNacionalidadePai() != null ? Nacionalidade.dtoToNacionalidade(dto.getNacionalidadePai()) : null);
        pessoa.setMae(dto.getMae());
        pessoa.setProfissao(dto.getProfissao() != null ? Profissao.dtoToProfissao(dto.getProfissao()) : null);
        pessoa.setNacionalidadeMae(dto.getNacionalidadeMae() != null ? Nacionalidade.dtoToNacionalidade(dto.getNacionalidadeMae()) : null);
        pessoa.setEmail(dto.getEmail());
        pessoa.setHomePage(dto.getHomePage());
        pessoa.setRacaCor(RacaCor.valueOf(dto.getRacaCor().name()));
        pessoa.setDeficienteDesde(dto.getDeficienteDesde());
        pessoa.setTipoDeficiencia(dto.getTipoDeficiencia() != null ? TipoDeficiencia.valueOf(dto.getTipoDeficiencia().name()) : null);
        pessoa.setTipoSanguineo(dto.getTipoSanguineo() != null ? TipoSanguineo.valueOf(dto.getTipoSanguineo().name()) : null);
        pessoa.setDoadorSanguineo(dto.getDoadorSanguineo());
        pessoa.setNaturalidade(dto.getNaturalidade() != null ? Cidade.dtoToCidade(dto.getNaturalidade()) : null);
        pessoa.setNacionalidade(dto.getNacionalidade() != null ? Nacionalidade.dtoToNacionalidade(dto.getNacionalidade()) : null);
        pessoa.setAnoChegada(dto.getAnoChegada());
        pessoa.setNivelEsolaridade(dto.getNivelEsolaridade() != null ? NivelEscolaridade.dtoToNivelEscolaridade(dto.getNivelEsolaridade()) : null);
        pessoa.setEstadoCivil(dto.getEstadoCivil() != null ? EstadoCivil.valueOf(dto.getEstadoCivil().name()) : null);
        pessoa.setTipoCondicaoIngresso(dto.getTipoCondicaoIngresso() != null ? TipoCondicaoIngresso.valueOf(dto.getTipoCondicaoIngresso().name()) : null);
        pessoa.setCasadoBrasileiro(dto.getCasadoBrasileiro());
        pessoa.setFilhoBrasileiro(dto.getFilhoBrasileiro());
        pessoa.setAcumulaCargo(dto.getAcumulaCargo());
        pessoa.setOrgao(dto.getOrgao());
        pessoa.setLocalTrabalho(dto.getLocalTrabalho());
        pessoa.setDataAtualizacao(SistemaFacade.getDataCorrente());

        pessoa.setRg(RGPortal.dtoToRgPortal(dto.getRg()));
        pessoa.setTituloEleitor(TituloEleitorPortal.dtoToTituloEleitorPortal(dto.getTituloEleitor()));
        pessoa.setSituacaoMilitar(dto.getSituacaoMilitar() != null ? SituacaoMilitarPortal.dtoToSituacaoMilitarPortal(dto.getSituacaoMilitar()) : null);
        pessoa.setCertidaoNascimento(CertidaoNascimentoPortal.dtoToCertidaoNascimentoPortal(dto.getCertidaoNascimento()));
        pessoa.setCertidaoCasamento(CertidaoCasamentoPortal.dtoToCertidaoCasamentoPortal(dto.getCertidaoCasamento()));
        pessoa.setCarteiraTrabalho(CarteiraTrabalhoPortal.dtoToCarteiraTrabalhoPortal(dto.getCarteiraTrabalho()));

        pessoa.setFormacoesPessoa(FormacaoPessoaPortal.dtoToFormacoesPortal(dto.getFormacoesPessoa(), pessoa));
        pessoa.setTelefones(TelefonePortal.dtoToTelefones(dto.getTelefones(), pessoa, null));
        pessoa.setEnderecos(EnderecoCorreioPortal.dtoToEnderecos(dto.getEnderecos(), pessoa, null));
        pessoa.setHabilidades(HabilidadePortal.dtoToHabilidadesPortal(dto.getHabilidades(), pessoa));
        pessoa.setHabilitacao(CarteiraHabilitacaoPortal.dtoToHabilitacoes(dto.getHabilitacao(), pessoa));
        pessoa.setConselhos(ConselhoClasseOrdemPortal.dtoToConselhos(dto.getConselhos(), pessoa));
        if (dto.getItemTempoAnteriorContratoFPDTO() != null) {
            pessoa.setItemTempoContratoFPPessoaPortal(TempoContratoFP.dtoTOTempoContratoFPList(dto.getItemTempoAnteriorContratoFPDTO(), pessoa));
        } else {
            pessoa.setItemTempoContratoFPPessoaPortal(Lists.<TempoContratoFP>newArrayList());
        }
        pessoa.setDependentes(Lists.<DependentePortal>newArrayList());
        return pessoa;
    }

    public static PessoaFisicaDTO toPessoaFisicaDTO(PessoaFisicaPortal pessoa) {

        PessoaFisicaDTO dto = new PessoaFisicaDTO();
        dto.setId(pessoa.getPessoaFisica().getId());
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
        dto.setDeficienteDesde(pessoa.getDeficienteDesde());
        dto.setCid(pessoa.getCid() != null ? CID.toCidDTO(pessoa.getCid()) : null);
        dto.setDoadorSanguineo(pessoa.getDoadorSanguineo());
        dto.setTipoSanguineo(pessoa.getTipoSanguineo() != null ? br.com.webpublico.pessoa.enumeration.TipoSanguineo.valueOf(pessoa.getTipoSanguineo().name()) : null);
        dto.setEstadoCivil(pessoa.getEstadoCivil() != null ? br.com.webpublico.pessoa.enumeration.EstadoCivil.valueOf(pessoa.getEstadoCivil().name()) : null);
        dto.setNaturalidade(Cidade.toCidadeDTO(pessoa.getNaturalidade()));
        dto.setNacionalidade(Nacionalidade.toNacionalidadeDTO(pessoa.getNacionalidade()));
        dto.setAnoChegada(pessoa.getAnoChegada());
        dto.setNivelEsolaridade(NivelEscolaridade.toNivelEscolaridadeDTO(pessoa.getNivelEsolaridade()));
        dto.setRg(RG.toRGPortalDTO(pessoa.getRg()));
        dto.setAcumulaCargo(pessoa.getAcumulaCargo());
        dto.setOrgao(pessoa.getOrgao());
        dto.setLocalTrabalho(pessoa.getLocalTrabalho());

        dto.setTipoCondicaoIngresso(pessoa.getTipoCondicaoIngresso() != null ? br.com.webpublico.pessoa.enumeration.TipoCondicaoIngresso.valueOf(pessoa.getTipoCondicaoIngresso().name()) : null);
        dto.setCasadoBrasileiro(pessoa.getCasadoBrasileiro());
        dto.setFilhoBrasileiro(pessoa.getFilhoBrasileiro());
        dto.setTituloEleitor(TituloEleitor.toTituloEleitorPortalDTO(pessoa.getTituloEleitor()));
        dto.setHabilitacao(pessoa.getHabilitacao() != null ? Habilitacao.toHabilitacoesPortalDTO(pessoa.getHabilitacao()) : null);
        dto.setCarteiraTrabalho(CarteiraTrabalho.toCarteiraTrabalhoPortalDTO(pessoa.getCarteiraTrabalho()));
        dto.setSituacaoMilitar(pessoa.getSituacaoMilitar() != null ? SituacaoMilitar.toSituacaoMilitarPortalDTO(pessoa.getSituacaoMilitar()) : null);
        dto.setCertidaoNascimento(CertidaoNascimento.toCertidaoNascimentoPortalDTO(pessoa.getCertidaoNascimento()));
        dto.setCertidaoCasamento(CertidaoCasamento.toCertidaoCasamentoPortalDTO(pessoa.getCertidaoCasamento()));
        dto.setTelefones(pessoa.getTelefones() != null ? Telefone.toTelefonesPortalDTO(pessoa.getTelefones()) : Lists.<TelefoneDTO>newArrayList());
        dto.setEnderecos(pessoa.getEnderecos() != null ? EnderecoCorreio.toEnderecoCorreioPortalDTOs(pessoa.getEnderecos()) : Lists.<EnderecoCorreioDTO>newArrayList());
        dto.setConselhos(ConselhoClasseContratoFP.toConselhoClasseOrdemPortalDTOs(pessoa.getConselhos()));
        dto.setFormacoesPessoa(MatriculaFormacao.toFormacoesPortalDTOs(pessoa.getFormacoesPessoa()));
        List<Habilidade> habilidades = getHabilidadesPessoa(pessoa.getHabilidades());
        dto.setHabilidades(Habilidade.toHabilidadesDTO(habilidades));
        dto.setLocalEOrgaoCorreto(pessoa.getLocalEOrgaoCorreto());
        dto.setItemTempoAnteriorContratoFPDTO(TempoContratoFP.tempoContratoFPListToDto(pessoa.getItemTempoContratoFPPessoaPortal()));
        return dto;
    }

    private static List<Habilidade> getHabilidadesPessoa(List<HabilidadePortal> lista) {
        List<Habilidade> habilidades = Lists.newLinkedList();
        for (HabilidadePortal habilidade : lista) {
            if (!habilidades.contains(habilidade.getHabilidade())) {
                habilidades.add(habilidade.getHabilidade());
            }
        }

        return habilidades;
    }

    public PessoaFisica getPessoaFisica() {
        return pessoaFisica;
    }

    public void setPessoaFisica(PessoaFisica pessoaFisica) {
        this.pessoaFisica = pessoaFisica;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNomeAbreviado() {
        return nomeAbreviado;
    }

    public void setNomeAbreviado(String nomeAbreviado) {
        this.nomeAbreviado = nomeAbreviado;
    }

    public String getNomeTratamento() {
        return nomeTratamento;
    }

    public void setNomeTratamento(String nomeTratamento) {
        this.nomeTratamento = nomeTratamento;
    }

    public String getNomeSocial() {
        return nomeSocial;
    }

    public void setNomeSocial(String nomeSocial) {
        this.nomeSocial = nomeSocial;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Sexo getSexo() {
        return sexo;
    }

    public void setSexo(Sexo sexo) {
        this.sexo = sexo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public NivelEscolaridade getNivelEsolaridade() {
        return nivelEsolaridade;
    }

    public void setNivelEsolaridade(NivelEscolaridade nivelEsolaridade) {
        this.nivelEsolaridade = nivelEsolaridade;
    }

    public Profissao getProfissao() {
        return profissao;
    }

    public void setProfissao(Profissao profissao) {
        this.profissao = profissao;
    }

    public String getMae() {
        return mae;
    }

    public void setMae(String mae) {
        this.mae = mae;
    }

    public String getPai() {
        return pai;
    }

    public void setPai(String pai) {
        this.pai = pai;
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

    public Date getLiberadoEm() {
        return liberadoEm;
    }

    public void setLiberadoEm(Date liberadoEm) {
        this.liberadoEm = liberadoEm;
    }

    public RacaCor getRacaCor() {
        return racaCor;
    }

    public void setRacaCor(RacaCor racaCor) {
        this.racaCor = racaCor;
    }

    public TipoDeficiencia getTipoDeficiencia() {
        return tipoDeficiencia;
    }

    public void setTipoDeficiencia(TipoDeficiencia tipoDeficiencia) {
        this.tipoDeficiencia = tipoDeficiencia;
    }

    public CID getCid() {
        return cid;
    }

    public void setCid(CID cid) {
        this.cid = cid;
    }

    public Date getDeficienteDesde() {
        return deficienteDesde;
    }

    public void setDeficienteDesde(Date deficienteDesde) {
        this.deficienteDesde = deficienteDesde;
    }

    public TipoSanguineo getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(TipoSanguineo tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public Boolean getDoadorSanguineo() {
        return doadorSanguineo;
    }

    public void setDoadorSanguineo(Boolean doadorSanguineo) {
        this.doadorSanguineo = doadorSanguineo;
    }

    public EstadoCivil getEstadoCivil() {
        return estadoCivil;
    }

    public void setEstadoCivil(EstadoCivil estadoCivil) {
        this.estadoCivil = estadoCivil;
    }

    public Cidade getNaturalidade() {
        return naturalidade;
    }

    public void setNaturalidade(Cidade naturalidade) {
        this.naturalidade = naturalidade;
    }

    public Nacionalidade getNacionalidade() {
        return nacionalidade;
    }

    public void setNacionalidade(Nacionalidade nacionalidade) {
        this.nacionalidade = nacionalidade;
    }

    public Integer getAnoChegada() {
        return anoChegada;
    }

    public void setAnoChegada(Integer anoChegada) {
        this.anoChegada = anoChegada;
    }

    public RGPortal getRg() {
        return rg;
    }

    public void setRg(RGPortal rg) {
        this.rg = rg;
    }

    public TituloEleitorPortal getTituloEleitor() {
        return tituloEleitor;
    }

    public void setTituloEleitor(TituloEleitorPortal tituloEleitor) {
        this.tituloEleitor = tituloEleitor;
    }

    public CarteiraTrabalhoPortal getCarteiraTrabalho() {
        return carteiraTrabalho;
    }

    public void setCarteiraTrabalho(CarteiraTrabalhoPortal carteiraTrabalho) {
        this.carteiraTrabalho = carteiraTrabalho;
    }

    public SituacaoPessoaPortal getStatus() {
        return status;
    }

    public void setStatus(SituacaoPessoaPortal status) {
        this.status = status;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public CertidaoNascimentoPortal getCertidaoNascimento() {
        return certidaoNascimento;
    }

    public void setCertidaoNascimento(CertidaoNascimentoPortal certidaoNascimento) {
        this.certidaoNascimento = certidaoNascimento;
    }

    public Boolean getLocalEOrgaoCorreto() {
        return localEOrgaoCorreto;
    }

    public void setLocalEOrgaoCorreto(Boolean localEOrgaoCorreto) {
        this.localEOrgaoCorreto = localEOrgaoCorreto;
    }

    public CertidaoCasamentoPortal getCertidaoCasamento() {
        return certidaoCasamento;
    }

    public void setCertidaoCasamento(CertidaoCasamentoPortal certidaoCasamento) {
        this.certidaoCasamento = certidaoCasamento;
    }

    public SituacaoMilitarPortal getSituacaoMilitar() {
        return situacaoMilitar;
    }

    public void setSituacaoMilitar(SituacaoMilitarPortal situacaoMilitar) {
        this.situacaoMilitar = situacaoMilitar;
    }

    public List<CarteiraHabilitacaoPortal> getHabilitacao() {
        return habilitacao;
    }

    public void setHabilitacao(List<CarteiraHabilitacaoPortal> habilitacao) {
        this.habilitacao = habilitacao;
    }

    public List<TelefonePortal> getTelefones() {
        return telefones;
    }

    public void setTelefones(List<TelefonePortal> telefones) {
        this.telefones = telefones;
    }

    public List<EnderecoCorreioPortal> getEnderecos() {
        return enderecos;
    }

    public void setEnderecos(List<EnderecoCorreioPortal> enderecos) {
        this.enderecos = enderecos;
    }

    public List<FormacaoPessoaPortal> getFormacoesPessoa() {
        return formacoesPessoa;
    }

    public void setFormacoesPessoa(List<FormacaoPessoaPortal> formacoesPessoa) {
        this.formacoesPessoa = formacoesPessoa;
    }

    public List<ConselhoClasseOrdemPortal> getConselhos() {
        return conselhos;
    }

    public void setConselhos(List<ConselhoClasseOrdemPortal> conselhos) {
        this.conselhos = conselhos;
    }

    public List<HabilidadePortal> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<HabilidadePortal> habilidades) {
        this.habilidades = habilidades;
    }

    public TipoCondicaoIngresso getTipoCondicaoIngresso() {
        return tipoCondicaoIngresso;
    }

    public void setTipoCondicaoIngresso(TipoCondicaoIngresso tipoCondicaoIngresso) {
        this.tipoCondicaoIngresso = tipoCondicaoIngresso;
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

    public List<TempoContratoFP> getItemTempoContratoFPPessoaPortal() {
        return itemTempoContratoFPPessoaPortal;
    }

    public void setItemTempoContratoFPPessoaPortal(List<TempoContratoFP> itemTempoContratoFPPessoaPortal) {
        this.itemTempoContratoFPPessoaPortal = itemTempoContratoFPPessoaPortal;
    }

    public List<DependentePortal> getDependentes() {
        return dependentes;
    }

    public void setDependentes(List<DependentePortal> dependentes) {
        this.dependentes = dependentes;
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

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }
}
