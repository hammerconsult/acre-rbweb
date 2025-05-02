/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.nfse.domain.TributosFederais;
import br.com.webpublico.nfse.domain.UserNfseCadastroEconomico;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.pessoa.dto.CadastroEconomicoDTO;
import br.com.webpublico.pessoa.dto.EconomicoCnaeDTO;
import br.com.webpublico.pessoa.dto.ServicoDTO;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Jaime
 */
@GrupoDiagrama(nome = "CadastroEconomico")
@Entity
@Audited
@Etiqueta("Cadastro Econômico")
public class CadastroEconomico extends Cadastro implements Serializable, Comparable<CadastroEconomico>, NfseEntity, NfseDTO {

    private static final long serialVersionUID = 1L;
    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @Pesquisavel
    @Tabelavel
    @Etiqueta("CPF/CNPJ ou Nome/Razão Social")
    private Pessoa pessoa;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("C.M.C")
    private String inscricaoCadastral;
    @Tabelavel
    @Transient
    @Etiqueta("CNAEs")
    private List<EconomicoCNAE> economicoCNAEVigentes;
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EconomicoCNAE> economicoCNAE;
    @ManyToMany(cascade = CascadeType.ALL)
    private List<Servico> servico; //JOIN TABLE = cadastroeconomico_servico
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "CE_VALORATRIBUTOS")
    private Map<Atributo, ValorAtributo> atributos;
    @ManyToOne
    private EscritorioContabil escritorioContabil;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date abertura;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date encerramento;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date reativacao;
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TipoProcessoEconomico> tipoProcessoEconomico;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "CE_SITUACAOCADASTRAL")
    private List<SituacaoCadastroEconomico> situacaoCadastroEconomico;
    @ManyToOne
    private NaturezaJuridica naturezaJuridica;
    private String inscricaoAnterior;
    private Double areaPublicidade;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "CE_REGISTROJUNTAS")
    private List<RegistroJuntas> registroJuntas;
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SociedadeCadastroEconomico> sociedadeCadastroEconomico;
    @JoinTable(name = "CE_Arquivos")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Arquivo> arquivos;
    private Boolean cadastroPorOficio;
    private String numeroAlvara;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataAlvaraLocalizacao;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataAlvaraFuncionamento;
    @ManyToOne
    private TipoAutonomo tipoAutonomo;
    @Transient
    @Tabelavel
    @Etiqueta("Situação Atual")
    private SituacaoCadastroEconomico situacaoAtual;
    @OneToOne
    private AtoLegal oficio;
    private String protocoloOficio;
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BCECaracFuncionamento> listaBCECaracFuncionamento;
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlacaAutomovelCmc> placas;
    private String observacoes;
    @Enumerated(EnumType.STRING)
    private TipoImovelSituacao tipoImovelSituacao;
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IsencaoCadastroEconomico> isencoes;
    @Temporal(TemporalType.DATE)
    private Date dataCadastro;
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnderecoCadastroEconomico> listaEnderecoCadastroEconomico;
    @Transient
    private List<Alvara> alvaras;
    @ManyToOne
    private UsuarioSistema usuarioDoCadastro;
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnquadramentoFiscal> enquadramentos;
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotAudited
    private List<UserNfseCadastroEconomico> usuariosNota;
    private Boolean enviaEmailNfseTomador;
    private Boolean enviaEmailNfseContador;
    private Boolean enviaEmailCancelaNfseTomador;
    private Boolean enviaEmailCancelaNfseContador;
    private String nomeParaContato;
    private String telefoneParaContato;
    private String resumoSobreEmpresa;
    private String ultimaViabilidadeVinculada;
    private String ultimaViabilidadeEndereco;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date ultimaAnaliseEndereco;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date ultimaIntegracaoRedeSim;
    @ManyToOne(cascade = CascadeType.ALL)
    private TributosFederais tributosFederais;
    private String chaveAcesso;
    private Long numeroUltimaNotaFiscal;
    @Enumerated(EnumType.STRING)
    private ClassificacaoAtividade classificacaoAtividade;
    @NotAudited
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CadastroEconomicoImpressaoHist> cadastroEconomicoImpressaoHists;
    @NotAudited
    @Etiqueta("Enquadramentos Fiscais")
    @OneToMany(mappedBy = "cadastroEconomico", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EnquadramentoAmbiental> enquadramentosAmbientais;
    private String caracteristicasAdicionais;


    public CadastroEconomico() {
        arquivos = Lists.newLinkedList();
        alvaras = Lists.newLinkedList();
        economicoCNAE = Lists.newLinkedList();
        registroJuntas = Lists.newLinkedList();
        servico = Lists.newLinkedList();
        situacaoCadastroEconomico = Lists.newLinkedList();
        sociedadeCadastroEconomico = Lists.newLinkedList();
        tipoProcessoEconomico = Lists.newLinkedList();
        listaBCECaracFuncionamento = Lists.newLinkedList();
        placas = Lists.newLinkedList();
        isencoes = Lists.newLinkedList();
        abertura = new Date();
        listaEnderecoCadastroEconomico = Lists.newLinkedList();
        enquadramentos = Lists.newArrayList();
        usuariosNota = Lists.newArrayList();
        enquadramentosAmbientais = Lists.newArrayList();
    }

    public CadastroEconomico(Long id, String inscricaoCadastral, String migracaoChave) {
        this.setId(id);
        this.setInscricaoCadastral(inscricaoCadastral);
        this.setMigracaoChave(migracaoChave);
    }

    public CadastroEconomico(Long id) {
        setId(id);
    }

    public CadastroEconomico(Long id, Pessoa pessoa) {
        super();
        setId(id);
        this.pessoa = pessoa;
    }

    public CadastroEconomico(Long id, Pessoa pessoa, String cmc) {
        super();
        setId(id);
        this.pessoa = pessoa;
        this.inscricaoCadastral = cmc;
        this.isencoes = Lists.newArrayList();
    }

    public Date getUltimaIntegracaoRedeSim() {
        return ultimaIntegracaoRedeSim;
    }

    public void setUltimaIntegracaoRedeSim(Date ultimaIntegracaoRedeSim) {
        this.ultimaIntegracaoRedeSim = ultimaIntegracaoRedeSim;
    }

    public UsuarioSistema getUsuarioDoCadastro() {
        return usuarioDoCadastro;
    }

    public void setUsuarioDoCadastro(UsuarioSistema usuarioDoCadastro) {
        this.usuarioDoCadastro = usuarioDoCadastro;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public TipoImovelSituacao getTipoImovelSituacao() {
        return tipoImovelSituacao;
    }

    public void setTipoImovelSituacao(TipoImovelSituacao tipoImovelSituacao) {
        this.tipoImovelSituacao = tipoImovelSituacao;
    }

    public List<PlacaAutomovelCmc> getPlacas() {
        return placas;
    }

    public void setPlacas(List<PlacaAutomovelCmc> placas) {
        this.placas = placas;
    }

    public void addPlaca(PlacaAutomovelCmc placa) {
        placa.setCadastroEconomico(this);
        this.placas.add(placa);
    }

    public Date getAbertura() {
        return abertura;
    }

    public void setAbertura(Date abertura) {
        this.abertura = abertura;
    }

    public Map<Atributo, ValorAtributo> getAtributos() {
        return atributos;
    }

    public void setAtributos(Map<Atributo, ValorAtributo> atributos) {
        this.atributos = atributos;
    }

    public List<EconomicoCNAE> getEconomicoCNAE() {
        return economicoCNAE;
    }

    public void setEconomicoCNAE(List<EconomicoCNAE> economicoCNAE) {
        this.economicoCNAE = economicoCNAE;
    }

    public List<EconomicoCNAE> getEconomicoCNAEAtivos() {
        List<EconomicoCNAE> cnaesAtivos = Lists.newArrayList();
        if (economicoCNAE != null) {
            for (EconomicoCNAE ec : economicoCNAE) {
                if ((ec.getFim() == null || ec.getFim().after(new Date())) &&
                    CNAE.Situacao.ATIVO.equals(ec.getCnae().getSituacao())) {
                    cnaesAtivos.add(ec);
                }
            }
        }
        Collections.sort(cnaesAtivos);
        return cnaesAtivos;
    }

    public EconomicoCNAE getEconomicoCNAEPrincipal() {
        EconomicoCNAE primaria = null;
        for (EconomicoCNAE ec : economicoCNAE) {
            if ((ec.getFim() == null || ec.getFim().after(new Date())) &&
                CNAE.Situacao.ATIVO.equals(ec.getCnae().getSituacao()) &&
                EconomicoCNAE.TipoCnae.Primaria.equals(ec.getTipo())) {
                primaria = ec;
                break;
            }
        }
        return primaria;
    }

    public List<EconomicoCNAE> getEconomicoCNAEVigentes() {
        economicoCNAEVigentes = new ArrayList<EconomicoCNAE>();
        if (economicoCNAE != null) {
            for (EconomicoCNAE ec : economicoCNAE) {
                if (CNAE.Situacao.ATIVO.equals(ec.getCnae().getSituacao()) &&
                    (ec.getFim() == null || ec.getFim().after(new Date()))) {
                    economicoCNAEVigentes.add(ec);
                }
            }
        }
        return economicoCNAEVigentes;
    }

    public List<EconomicoCNAE> getEconomicoCNAESecundariosVigentes() {
        economicoCNAEVigentes = new ArrayList<EconomicoCNAE>();
        if (economicoCNAE != null) {
            for (EconomicoCNAE ec : economicoCNAE) {
                if (CNAE.Situacao.ATIVO.equals(ec.getCnae().getSituacao()) &&
                    (ec.getFim() == null || ec.getFim().after(new Date())) &&
                    EconomicoCNAE.TipoCnae.Secundaria.equals(ec.getTipo())) {
                    economicoCNAEVigentes.add(ec);
                }
            }
        }
        return economicoCNAEVigentes;
    }

    public void setEconomicoCNAEVigentes(List<EconomicoCNAE> economicoCNAEVigentes) {
        this.economicoCNAEVigentes = economicoCNAEVigentes;
    }

    public List<EconomicoCNAE> getEconomicoCNAEVigentesNaData(Date dataOperacao) {
        dataOperacao = dataOperacao == null ? new Date() : dataOperacao;
        economicoCNAEVigentes = new ArrayList<EconomicoCNAE>();
        if (economicoCNAE != null) {
            for (EconomicoCNAE ec : economicoCNAE) {
                if (ec.getFim() == null || ec.getFim().after(dataOperacao)) {
                    economicoCNAEVigentes.add(ec);
                }
            }
        }
        return economicoCNAEVigentes;
    }

    public Date getEncerramento() {
        return encerramento;
    }

    public void setEncerramento(Date encerramento) {
        this.encerramento = encerramento;
    }

    public EscritorioContabil getEscritorioContabil() {
        return escritorioContabil;
    }

    public void setEscritorioContabil(EscritorioContabil escritorioContabil) {
        this.escritorioContabil = escritorioContabil;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public List<TipoProcessoEconomico> getTipoProcessoEconomico() {
        return tipoProcessoEconomico;
    }

    public void setTipoProcessoEconomico(List<TipoProcessoEconomico> tipoProcessoEconomico) {
        this.tipoProcessoEconomico = tipoProcessoEconomico;
    }

    public Date getReativacao() {
        return reativacao;
    }

    public void setReativacao(Date reativacao) {
        this.reativacao = reativacao;
    }

    public List<Servico> getServico() {
        return servico;
    }

    public void setServico(List<Servico> servico) {
        this.servico = servico;
    }

    public List<SituacaoCadastroEconomico> getSituacaoCadastroEconomico() {
        return situacaoCadastroEconomico;
    }

    public void setSituacaoCadastroEconomico(List<SituacaoCadastroEconomico> situacaoCadastroEconomico) {
        this.situacaoCadastroEconomico = situacaoCadastroEconomico;
    }

    public Double getAreaPublicidade() {
        return areaPublicidade != null ? areaPublicidade : 0;
    }

    public void setAreaPublicidade(Double areaPublicidade) {
        this.areaPublicidade = areaPublicidade;
    }

    public String getInscricaoAnterior() {
        return inscricaoAnterior;
    }

    public void setInscricaoAnterior(String inscricaoAnterior) {
        this.inscricaoAnterior = inscricaoAnterior;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public List<RegistroJuntas> getRegistroJuntas() {
        return registroJuntas;
    }

    public void setRegistroJuntas(List<RegistroJuntas> registroJuntas) {
        this.registroJuntas = registroJuntas;
    }

    public List<SociedadeCadastroEconomico> getSociedadeCadastroEconomico() {
        return sociedadeCadastroEconomico;
    }

    public void setSociedadeCadastroEconomico(List<SociedadeCadastroEconomico> sociedadeCadastroEconomico) {
        this.sociedadeCadastroEconomico = sociedadeCadastroEconomico;
    }

    public List<Arquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<Arquivo> arquivos) {
        this.arquivos = arquivos;
    }

    public Boolean getCadastroPorOficio() {
        return cadastroPorOficio;
    }

    public void setCadastroPorOficio(Boolean cadastroPorOficio) {
        this.cadastroPorOficio = cadastroPorOficio;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public TipoAutonomo getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(TipoAutonomo tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public Date getDataAlvaraLocalizacao() {
        return dataAlvaraLocalizacao;
    }

    public void setDataAlvaraLocalizacao(Date dataAlvaraLocalizacao) {
        this.dataAlvaraLocalizacao = dataAlvaraLocalizacao;
    }

    public Date getDataAlvaraFuncionamento() {
        return dataAlvaraFuncionamento;
    }

    public void setDataAlvaraFuncionamento(Date dataAlvaraFuncionamento) {
        this.dataAlvaraFuncionamento = dataAlvaraFuncionamento;
    }

    public String getNumeroAlvara() {
        return numeroAlvara;
    }

    public void setNumeroAlvara(String numeroAlvara) {
        this.numeroAlvara = numeroAlvara;
    }

    public AtoLegal getOficio() {
        return oficio;
    }

    public void setOficio(AtoLegal oficio) {
        this.oficio = oficio;
    }

    public String getProtocoloOficio() {
        return protocoloOficio;
    }

    public void setProtocoloOficio(String protocoloOficio) {
        this.protocoloOficio = protocoloOficio;
    }

    public List<BCECaracFuncionamento> getListaBCECaracFuncionamento() {
        return listaBCECaracFuncionamento;
    }

    public void setListaBCECaracFuncionamento(List<BCECaracFuncionamento> listaBCECaracFuncionamento) {
        this.listaBCECaracFuncionamento = listaBCECaracFuncionamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public NaturezaJuridica getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public List<IsencaoCadastroEconomico> getIsencoes() {
        Collections.sort(isencoes);
        return isencoes;
    }

    public void setIsencoes(List<IsencaoCadastroEconomico> isencoes) {
        this.isencoes = isencoes;
    }

    public SituacaoCadastroEconomico getSituacaoAtual() {
        for (SituacaoCadastroEconomico s : situacaoCadastroEconomico) {
            if (s.getDataRegistro() != null) {
                if (situacaoAtual == null || situacaoAtual.getDataRegistro() == null || s.getDataRegistro().after(situacaoAtual.getDataRegistro())) {
                    situacaoAtual = s;
                }
            }
        }
        return situacaoAtual;
    }

    public void setSituacaoAtual(SituacaoCadastroEconomico situacaoAtual) {
        this.situacaoAtual = situacaoAtual;
    }

    public List<EnderecoCadastroEconomico> getListaEnderecoCadastroEconomico() {
        if (listaEnderecoCadastroEconomico == null) listaEnderecoCadastroEconomico = Lists.newArrayList();
        return listaEnderecoCadastroEconomico;
    }

    public void setListaEnderecoCadastroEconomico(List<EnderecoCadastroEconomico> listaEnderecoCadastroEconomico) {
        this.listaEnderecoCadastroEconomico = listaEnderecoCadastroEconomico;
    }

    public List<CadastroEconomicoImpressaoHist> getCadastroEconomicoImpressaoHists() {
        return cadastroEconomicoImpressaoHists;
    }

    public void setCadastroEconomicoImpressaoHists(List<CadastroEconomicoImpressaoHist> cadastroEconomicoImpressaoHists) {
        this.cadastroEconomicoImpressaoHists = cadastroEconomicoImpressaoHists;
    }

    public Servico getServicoPrincipal() {
        if (this.getServico().isEmpty()) {
            return null;
        } else {
            return this.getServico().get(0);
        }
    }

    public String getDescricao() {
        StringBuilder descricao = new StringBuilder("");
        if (inscricaoCadastral != null) {
            descricao.append(inscricaoCadastral);
        } else {
            descricao.append("Sem número de CMC");
        }
        if (pessoa != null) {
            if (pessoa.getNome() != null) {
                if (!descricao.toString().isEmpty()) {
                    descricao.append(" - ");
                }
                descricao.append(pessoa.getNome());
            }
            if (pessoa.getCpf_Cnpj() != null) {
                if (!descricao.toString().isEmpty()) {
                    descricao.append(" ");
                }
                descricao.append("(").append(pessoa.getCpf_Cnpj()).append(")");
            }
        }

        return descricao.toString();
    }

    public String getCmcNomeCpfCnpj() {
        StringBuilder descricao = new StringBuilder("");
        if (inscricaoCadastral != null) {
            descricao.append(inscricaoCadastral);
            descricao.append(" - ");
        }
        if (pessoa != null) {
            if (pessoa.getCpf_Cnpj() != null) {
                descricao.append(pessoa.getNome()).append(" (").append(pessoa.getCpf_Cnpj()).append(")");
            }
        }
        return descricao.toString();
    }

    public EnderecoCadastroEconomico getLocalizacao() {
        for (EnderecoCadastroEconomico enderecoCadastroEconomico : getListaEnderecoCadastroEconomico()) {
            if (enderecoCadastroEconomico.getTipoEndereco().equals(TipoEndereco.COMERCIAL)) {
                return enderecoCadastroEconomico;
            }
        }
        EnderecoCadastroEconomico enderecoCadastroEconomico = new EnderecoCadastroEconomico(TipoEndereco.COMERCIAL, this);
        listaEnderecoCadastroEconomico.add(enderecoCadastroEconomico);
        return enderecoCadastroEconomico;
    }

    public List<EnderecoCadastroEconomico> getEnderecosSecundarios() {
        return getListaEnderecoCadastroEconomico().stream().filter(ece -> TipoEndereco.CORRESPONDENCIA.equals(ece.getTipoEndereco())).collect(Collectors.toList());
    }

    public String getInscricaoCadastralSemDigito() {
        if (pessoa instanceof PessoaJuridica) {
            return inscricaoCadastral;
        }
        if (inscricaoCadastral != null) {
            if (inscricaoCadastral.length() >= 7) {
                return inscricaoCadastral.substring(0, 6);
            }
            return inscricaoCadastral;
        }
        return "";
    }

    public String getDigitoVerificadorInscricao() {
        if (pessoa instanceof PessoaJuridica) {
            return "";
        }
        if (inscricaoCadastral != null) {
            if (inscricaoCadastral.length() >= 7) {
                return inscricaoCadastral.substring(6, 7);
            }
            return "0";
        }
        return "";
    }

    public int getAnoDeAbertura() {
        if (abertura != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(abertura);
            return c.get(Calendar.YEAR);
        }
        return -1;
    }

    public List<Alvara> getAlvaras() {
        return alvaras;
    }

    public void setAlvaras(List<Alvara> alvaras) {
        this.alvaras = alvaras;
    }

    public List<EnquadramentoFiscal> getEnquadramentos() {
        return enquadramentos;
    }

    public void setEnquadramentos(List<EnquadramentoFiscal> enquadramentos) {
        this.enquadramentos = enquadramentos;
    }

    public EnquadramentoFiscal getEnquadramentoVigente() {
        if (enquadramentos != null) {
            for (EnquadramentoFiscal enquadramento : enquadramentos) {
                if (enquadramento.getFimVigencia() == null) {
                    return enquadramento;
                }
            }
        }
        return null;
    }

    public List<UserNfseCadastroEconomico> getUsuariosNota() {
        return usuariosNota;
    }

    public void setUsuariosNota(List<UserNfseCadastroEconomico> usuariosNota) {
        this.usuariosNota = usuariosNota;
    }

    public Boolean getEnviaEmailNfseTomador() {
        return enviaEmailNfseTomador != null ? enviaEmailNfseTomador : false;
    }

    public void setEnviaEmailNfseTomador(Boolean enviaEmailNfseTomador) {
        this.enviaEmailNfseTomador = enviaEmailNfseTomador;
    }

    public Boolean getEnviaEmailNfseContador() {
        return enviaEmailNfseContador != null ? enviaEmailNfseContador : false;
    }

    public void setEnviaEmailNfseContador(Boolean enviaEmailNfseContador) {
        this.enviaEmailNfseContador = enviaEmailNfseContador;
    }

    public Boolean getEnviaEmailCancelaNfseTomador() {
        return enviaEmailCancelaNfseTomador != null ? enviaEmailCancelaNfseTomador : false;
    }

    public void setEnviaEmailCancelaNfseTomador(Boolean enviaEmailCancelaNfseTomador) {
        this.enviaEmailCancelaNfseTomador = enviaEmailCancelaNfseTomador;
    }

    public Boolean getEnviaEmailCancelaNfseContador() {
        return enviaEmailCancelaNfseContador != null ? enviaEmailCancelaNfseContador : false;
    }

    public void setEnviaEmailCancelaNfseContador(Boolean enviaEmailCancelaNfseContador) {
        this.enviaEmailCancelaNfseContador = enviaEmailCancelaNfseContador;
    }

    public String getNomeParaContato() {
        return nomeParaContato;
    }

    public void setNomeParaContato(String nomeParaContato) {
        this.nomeParaContato = nomeParaContato;
    }

    public String getTelefoneParaContato() {
        return telefoneParaContato;
    }

    public void setTelefoneParaContato(String telefoneParaContato) {
        this.telefoneParaContato = telefoneParaContato;
    }

    public String getResumoSobreEmpresa() {
        return resumoSobreEmpresa;
    }

    public void setResumoSobreEmpresa(String resumoSobreEmpresa) {
        this.resumoSobreEmpresa = resumoSobreEmpresa;
    }

    public String getUltimaViabilidadeVinculada() {
        return ultimaViabilidadeVinculada;
    }

    public void setUltimaViabilidadeVinculada(String ultimaViabilidadeVinculada) {
        this.ultimaViabilidadeVinculada = ultimaViabilidadeVinculada;
    }

    public String getUltimaViabilidadeEndereco() {
        return ultimaViabilidadeEndereco;
    }

    public void setUltimaViabilidadeEndereco(String ultimaViabilidadeEndereco) {
        this.ultimaViabilidadeEndereco = ultimaViabilidadeEndereco;
    }

    public Date getUltimaAnaliseEndereco() {
        return ultimaAnaliseEndereco;
    }

    public void setUltimaAnaliseEndereco(Date ultimaAnaliseEndereco) {
        this.ultimaAnaliseEndereco = ultimaAnaliseEndereco;
    }

    public boolean isAutonomo() {
        return naturezaJuridica != null && naturezaJuridica.getAutonomo();
    }

    @Override
    public String toString() {
        return getCmcNomeCpfCnpj();
    }

    @Override
    public String getNumeroCadastro() {
        return this.inscricaoCadastral;
    }

    @Override
    public String getCadastroResponsavel() {
        return getInscricaoCadastral() + " - " + pessoa.toString();
    }

    public String getChaveAcesso() {
        return chaveAcesso;
    }

    public void setChaveAcesso(String chaveAcesso) {
        this.chaveAcesso = chaveAcesso;
    }

    public TributosFederais getTributosFederais() {
        return tributosFederais;
    }

    public void setTributosFederais(TributosFederais tributosFederais) {
        this.tributosFederais = tributosFederais;
    }

    public Long getNumeroUltimaNotaFiscal() {
        return numeroUltimaNotaFiscal;
    }

    public void setNumeroUltimaNotaFiscal(Long numeroUltimaNotaFiscal) {
        this.numeroUltimaNotaFiscal = numeroUltimaNotaFiscal;
    }

    @Override
    public int compareTo(CadastroEconomico o) {
        try {
            return o.getInscricaoCadastral().compareTo(this.inscricaoCadastral);
        } catch (Exception e) {
            return 0;
        }

    }


    @Override
    public PrestadorServicoNfseDTO toNfseDto() {
        PrestadorServicoNfseDTO dto = new PrestadorServicoNfseDTO(this.getId(),
            this.getInscricaoCadastral(), this.getPessoa().toNfseDto());

        dto.setEnviaEmailNfseTomador(getEnviaEmailNfseTomador());
        dto.setEnviaEmailNfseContador(getEnviaEmailNfseContador());
        dto.setEnviaEmailCancelaNfseTomador(getEnviaEmailCancelaNfseTomador());
        dto.setEnviaEmailCancelaNfseContador(getEnviaEmailCancelaNfseContador());
//        dto.setEnviaEmailNfseSocios(getEnviaEmailNfseSocios());
//        dto.setEnviaEmailCancelaNfseSocios(getEnviaEmailCancelaNfseSocios());

        dto.setNomeParaContato(getNomeParaContato());
        dto.setTelefoneParaContato(getTelefoneParaContato());
        dto.setResumoSobreEmpresa(getResumoSobreEmpresa());
//        dto.setDataCredenciamentoISSOnline(getDataCredenciamentoISSOnline());

        dto.setChaveAcesso(this.chaveAcesso);

        if (getEscritorioContabil() != null) {
            dto.setEscritorioContabil(getEscritorioContabil().toNfseDto());
        }
        if (getNaturezaJuridica() != null) {
            dto.setNaturezaJuridica(getNaturezaJuridica().toNfseDto());
        }
        for (SociedadeCadastroEconomico socio : this.getSociedadeCadastroEconomico()) {
            dto.getSocios().add(new SocioNfseDTO(socio.getProporcao(), socio.getSocio().toNfseDto()));
        }
        for (Servico servico : this.getServico()) {
            dto.getServicos().add(servico.toNfseDto());
        }
        for (EconomicoCNAE cnae : getEconomicoCNAE()) {
            dto.getCnaes().add(cnae.getCnae().toNfseDto());
        }

        dto.setEnquadramentoFiscal(new EnquadramentoFiscalNfseDTO());

        EnquadramentoFiscal situacaoVigente = this.getEnquadramentoVigente();
        if (situacaoVigente != null) {
            if (situacaoVigente.getPorte() != null) {
                dto.getEnquadramentoFiscal().setPorte(situacaoVigente.getPorte().getNfeDTO());
            }
            if (situacaoVigente.getTipoContribuinte() != null) {
                dto.getEnquadramentoFiscal().setTipoContribuinte(situacaoVigente.getTipoContribuinte().getNfeDTO());
            }

            if (situacaoVigente.getTipoNotaFiscalServico() != null) {
                dto.getEnquadramentoFiscal().setTipoNotaFiscal(situacaoVigente.getTipoNotaFiscalServico().toDto());
            }

            dto.getEnquadramentoFiscal().setSubstitutoTributario(situacaoVigente.getSubstitutoTributario());
            dto.getEnquadramentoFiscal().setEmitenteNfse(TipoNotaFiscalServico.ELETRONICA.equals(situacaoVigente.getTipoNotaFiscalServico()));
            dto.getEnquadramentoFiscal().setAtivo(SituacaoCadastralCadastroEconomico.ATIVA_REGULAR.equals(this.getSituacaoAtual().getSituacaoCadastral()));

            dto.setEmiteNfs(TipoNotaFiscalServico.ELETRONICA.equals(situacaoVigente.getTipoNotaFiscalServico()));
            dto.setInstituicaoFinanceira(RegimeEspecialTributacao.INSTITUICAO_FINANCEIRA.equals(situacaoVigente.getRegimeEspecialTributacao()));

            if (situacaoVigente.getRegimeTributario() != null)
                dto.getEnquadramentoFiscal().setRegimeTributario(situacaoVigente.getRegimeTributario().toDto());
            if (situacaoVigente.getTipoIssqn() != null)
                dto.getEnquadramentoFiscal().setTipoIssqn(situacaoVigente.getTipoIssqn().toDto());
            dto.getEnquadramentoFiscal().setSimplesNacional(TipoIssqn.SIMPLES_NACIONAL.equals(situacaoVigente.getTipoIssqn()));
        }
        return dto;
    }

    public PrestadorServicoNfseDTO toSimpleNfseDto() {
        PrestadorServicoNfseDTO dto = new PrestadorServicoNfseDTO(this.getId(),
            this.getInscricaoCadastral(), this.getPessoa().toSimpleNfseDto());
        return dto;
    }

    public List<JuntaComercialPessoaJuridica> getJuntasComerciais() {
        List<JuntaComercialPessoaJuridica> juntas = Lists.newArrayList();
        if (pessoa != null && pessoa instanceof PessoaJuridica) {
            juntas = ((PessoaJuridica) pessoa).getJuntaComercial();
        }
        return juntas;
    }

    public boolean validarAlteracaoCnaeCadastroEconomico() {
        if (SituacaoCadastralCadastroEconomico.BAIXADA.equals(getSituacaoAtual().getSituacaoCadastral())) {
            return true;
        }
        for (EconomicoCNAE economicoCNAE : getEconomicoCNAE()) {
            if (EconomicoCNAE.TipoCnae.Primaria.equals(economicoCNAE.getTipo())) {
                return true;
            }
        }
        return false;
    }

    public boolean hasServico(Servico servico) {
        if (servico == null) {
            return false;
        }
        for (Servico cmcServico : this.servico) {
            if (cmcServico.equals(servico)) {
                return true;
            }
        }
        return false;
    }

    public void adicionarServico(Servico servico) {
        if (this.servico == null) {
            this.servico = Lists.newArrayList();
        }
        this.servico.add(servico);
    }

    public void adicionarServicos(List<CNAEServico> servicos) {
        if (servicos != null && !servicos.isEmpty()) {
            for (CNAEServico cnaeServico : servicos) {
                if (!this.temServico(cnaeServico.getServico())) {
                    this.adicionarServico(cnaeServico.getServico());
                }
            }
        }
    }


    public CadastroEconomicoDTO toCadastroEconomicoDTO() {
        CadastroEconomicoDTO cadastroEconomicoDTO = new CadastroEconomicoDTO();
        cadastroEconomicoDTO.setInscricaoCadastral(getInscricaoCadastral());
        cadastroEconomicoDTO.setCnpj(getPessoa().getCpf_Cnpj());
        cadastroEconomicoDTO.setId(getId());
        List<EconomicoCnaeDTO> cnaeDTOS = Lists.newArrayList();
        for (EconomicoCNAE economicoCNAE : getEconomicoCNAEVigentes()) {
            cnaeDTOS.add(economicoCNAE.toEconomicoCnaeDTO());
        }
        cadastroEconomicoDTO.setCnaeDTOS(cnaeDTOS);
        if (getPessoa().isPessoaJuridica()) {
            cadastroEconomicoDTO.setPessoaJuridica(((PessoaJuridica) getPessoa()).toPessoaJuridicaDTO());
        } else {
            cadastroEconomicoDTO.setPessoaFisica(((PessoaFisica) getPessoa()).toPessoaFisicaDTO());
        }
        List<ServicoDTO> servicoDTOS = Lists.newArrayList();
        for (Servico servico : getServico()) {
            servicoDTOS.add(servico.toServicoDTO());
        }
        cadastroEconomicoDTO.setServicos(servicoDTOS);
        return cadastroEconomicoDTO;
    }

    public boolean temServico(Servico s) {
        if (this.servico == null || this.servico.isEmpty())
            return false;
        for (Servico item : this.servico) {
            if (item.getId().equals(s.getId())) {
                return true;
            }
        }
        return false;
    }

    public double getAreaUtilizacao() {
        return getListaEnderecoCadastroEconomico().stream().mapToDouble(EnderecoCadastroEconomico::getAreaUtilizacao).sum();
    }

    public boolean isPrestadorServico() {
        return this.classificacaoAtividade != null && this.classificacaoAtividade.isPrestacaoServico();
    }

    public ClassificacaoAtividade getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public List<EnquadramentoAmbiental> getEnquadramentosAmbientais() {
        return enquadramentosAmbientais;
    }

    public void setEnquadramentosAmbientais(List<EnquadramentoAmbiental> enquadramentosAmbientais) {
        this.enquadramentosAmbientais = enquadramentosAmbientais;
    }

    public String getCaracteristicasAdicionais() {
        return caracteristicasAdicionais;
    }

    public void setCaracteristicasAdicionais(String caracteristicasAdicionais) {
        this.caracteristicasAdicionais = caracteristicasAdicionais;
    }

    public EnderecoCadastroEconomico getEnderecoPrimario() {
        if (listaEnderecoCadastroEconomico != null && !listaEnderecoCadastroEconomico.isEmpty()) {
            return listaEnderecoCadastroEconomico.stream()
                .filter(ece -> TipoEndereco.COMERCIAL.equals(ece.getTipoEndereco()))
                .findFirst()
                .orElse(null);
        }
        return null;
    }

    public EnderecoCadastroEconomico getEnderecoSecundario() {
        List<EnderecoCadastroEconomico> enderecosSecundarios = getEnderecosSecundarios();
        if (enderecosSecundarios != null && !enderecosSecundarios.isEmpty()) {
            return enderecosSecundarios.stream().findFirst().get();
        }
        return null;
    }
}
