package br.com.webpublico.entidades;

import br.com.webpublico.enums.MotivoInativacaoImovel;
import br.com.webpublico.enums.TipoMatricula;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.nfse.domain.dtos.CadastroImobiliarioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity
@Audited
@Etiqueta("Cadastro Imobiliário")
public class CadastroImobiliario extends Cadastro implements Comparable<CadastroImobiliario>, Serializable, NfseEntity, PossuidorArquivo {

    private static final Logger logger = LoggerFactory.getLogger(CadastroImobiliario.class);

    private static final long serialVersionUID = 1L;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    @Obrigatorio
    private String codigo;
    @Tabelavel
    @Etiqueta("Inscrição Cadastral")
    @Pesquisavel
    private String inscricaoCadastral;
    @Etiqueta("Proprietários")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "imovel", orphanRemoval = true)
    private List<Propriedade> propriedade;
    @Etiqueta("Proprietários no Cartório")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "imovel", orphanRemoval = true)
    private List<PropriedadeCartorio> propriedadeCartorio;
    @Tabelavel
    @Etiqueta("Proprietários")
    @Transient
    private List<Propriedade> propriedades;
    @Tabelavel
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Lote")
    private Lote lote;
    @Transient
    @Etiqueta("Bairro")
    @Tabelavel
    private Bairro bairro;
    @Transient
    @Etiqueta("Logradouro")
    @Tabelavel
    private Logradouro logradouro;
    @Tabelavel
    @Etiqueta("Número")
    private String numero;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Complemento (BCI)")
    private String complementoEndereco;
    @ManyToOne
    private Cartorio cartorio;
    private String matriculaRegistro;
    private Integer livroRegistro;
    private Integer folhaRegistro;
    @ManyToOne
    private AtoLegal atoLegalIsencao;
    @OneToOne(cascade = CascadeType.ALL)
    private Texto observacoes;
    @OneToOne()
    private Processo processo;
    @OneToMany(mappedBy = "imovel", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Construcao> construcoes;
    @Tabelavel
    @Transient
    @Etiqueta("Construções")
    private List<Construcao> construcoesAtivas;
    @OneToMany(mappedBy = "cadastroImobiliario", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CaracteristicasBci> caracteristicasBci;
    @Transient
    private Map<Atributo, ValorAtributo> atributos;
    @ManyToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;
    @Pesquisavel(labelBooleanVerdadeiro = "Ativo", labelBooleanFalso = "Inativo")
    @Tabelavel
    @Etiqueta("Ativo")
    private Boolean ativo;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataInativacao;
    @Enumerated(EnumType.STRING)
    private MotivoInativacaoImovel motivoInativacao;
    private BigDecimal areaTotalConstruida;
    @ManyToOne
    private TipoIsencao tipoIsencao;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataIsencao;
    @OneToMany(mappedBy = "cadastroImobiliario", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<CadastroImobInscrAnt> inscricoesAnterioes;
    @OneToMany(mappedBy = "cadastroImobiliario", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<IsencaoCadastroImobiliario> isencoes;
    @Transient
    private Long criadoEm;
    @Transient
    private Integer quantidadeImoveisDoProprietario;
    private Boolean imune;
    private Boolean gerarNovoCodigoAutenticidade;
    @Transient
    private Long idMaiorProprietario;
    @Enumerated(EnumType.STRING)
    private TipoMatricula tipoMatricula;
    @Temporal(TemporalType.DATE)
    private Date dataRegistro;
    @Temporal(TemporalType.DATE)
    private Date dataTitulo;
    private String numeroDoTitulo;
    @OneToMany(mappedBy = "cadastroImobiliario")
    private List<EventoCalculoBCI> eventosCalculoBCI;
    @Transient
    private Map<GrupoAtributo, List<EventoCalculoBCI>> mapEventoCalculoBCIPorGrupoAtributo;
    @OneToMany(mappedBy = "cadastroImobiliario", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<EnderecoCadastroImobiliario> enderecosCadastroImobiliario;
    @Transient
    private List<EventoCalculoConstrucao> eventosCalculoConstrucoes;
    @Transient
    private BigDecimal areaLote;
    @ManyToOne
    private UnidadeExterna unidadeExterna;
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    private String identificacaoPatrimonial;
    private Long autonoma;
    @Transient
    private String motivoParaNaoCalcularIPTU;
    @OneToMany(mappedBy = "cadastroImobiliario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Compromissario> listaCompromissarios;
    @NotAudited
    @OneToMany(mappedBy = "cadastroImobiliario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcRegularizaConstrucao> processosDeRegularizacao;
    @OneToMany(mappedBy = "cadastroImobiliario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CadastroImobiliarioImpressaoHist> cadastroImobiliarioImpressaoHists;

    private String coordenadasSIT;

    @Transient
    private List<AlvaraConstrucao> alvarasContrucao;
    @Transient
    private List<Habitese> habiteses;

    public CadastroImobiliario(Long id, String codigo, String inscricaoCadastral, Lote lote, BigDecimal quantidadeImoveisDoProprietario) {
        this.setId(id);
        this.codigo = codigo;
        this.inscricaoCadastral = inscricaoCadastral;
        this.lote = lote;
        this.quantidadeImoveisDoProprietario = quantidadeImoveisDoProprietario.intValue();
        imune = false;
    }

    public CadastroImobiliario(Long id, String codigo, String inscricaoCadastral, Lote lote) {
        this.setId(id);
        this.codigo = codigo;
        this.inscricaoCadastral = inscricaoCadastral;
        this.lote = lote;
        imune = false;
    }

    public CadastroImobiliario(Long id, String inscricaoCadastral) {
        this.setId(id);
        this.inscricaoCadastral = inscricaoCadastral;
        imune = false;
    }

    public CadastroImobiliario(String codigo, List<Construcao> construcoes, List<Propriedade> propriedade, List<IsencaoCadastroImobiliario> isencoes) {
        this.codigo = codigo;
        this.construcoes = construcoes;
        this.propriedade = propriedade;
        this.isencoes = isencoes;
        imune = false;
    }

    public CadastroImobiliario() {
        setHistoricos(new ArrayList<Historico>());
        criadoEm = System.nanoTime();
        areaTotalConstruida = BigDecimal.ZERO;
        isencoes = new ArrayList<>();
//        lote = new Lote();
        imune = false;
        ativo = true;
        propriedade = Lists.newLinkedList();
        propriedadeCartorio = Lists.newLinkedList();
        inscricoesAnterioes = Lists.newLinkedList();
        atributos = new HashMap<>();
        construcoes = Lists.newLinkedList();
        eventosCalculoBCI = Lists.newLinkedList();
        eventosCalculoConstrucoes = Lists.newLinkedList();
        listaCompromissarios = Lists.newArrayList();
    }

    public CadastroImobiliario(Long id) {
        setId(id);
    }

    public List<Propriedade> getPropriedades() {
        return propriedades;
    }

    public void setPropriedades(List<Propriedade> propriedades) {
        this.propriedades = propriedades;
    }

    public String getMotivoParaNaoCalcularIPTU() {
        return motivoParaNaoCalcularIPTU;
    }

    public void setMotivoParaNaoCalcularIPTU(String motivoParaNaoCalcularIPTU) {
        this.motivoParaNaoCalcularIPTU = motivoParaNaoCalcularIPTU;
    }

    public List<PropriedadeCartorio> getPropriedadeCartorio() {
        return propriedadeCartorio;
    }

    public void setPropriedadeCartorio(List<PropriedadeCartorio> propriedadeCartorio) {
        this.propriedadeCartorio = propriedadeCartorio;
    }

    public BigDecimal getAreaLote() {
        return areaLote;
    }

    public void setAreaLote(BigDecimal areaLote) {
        this.areaLote = areaLote;
    }

    public String getNumeroDoTitulo() {
        return numeroDoTitulo;
    }

    public void setNumeroDoTitulo(String numeroDoTitulo) {
        this.numeroDoTitulo = numeroDoTitulo;
    }

    public Date getDataTitulo() {
        return dataTitulo;
    }

    public void setDataTitulo(Date dataTitulo) {
        this.dataTitulo = dataTitulo;
    }

    public TipoMatricula getTipoMatricula() {
        return tipoMatricula;
    }

    public void setTipoMatricula(TipoMatricula tipoMatricula) {
        this.tipoMatricula = tipoMatricula;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public boolean isImune() {
        return imune != null ? imune : false;
    }

    public void setImune(boolean imune) {
        this.imune = imune;
    }

    public Integer getQuantidadeImoveisDoProprietario() {
        return quantidadeImoveisDoProprietario;
    }

    public void setQuantidadeImoveisDoProprietario(Integer quantidadeImoveisDoProprietario) {
        this.quantidadeImoveisDoProprietario = quantidadeImoveisDoProprietario;
    }

    public List<IsencaoCadastroImobiliario> getIsencoes() {
        Collections.sort(isencoes);
        return isencoes;
    }

    public void setIsencoes(List<IsencaoCadastroImobiliario> isencoes) {
        this.isencoes = isencoes;
    }

    public List<Propriedade> getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(List<Propriedade> propriedade) {
        this.propriedade = propriedade;
    }

    public Propriedade getPropriedadePrincipal() {
        Propriedade propriedadePrincipal = null;
        for (Propriedade p : getPropriedadeVigente()) {
            if (propriedadePrincipal == null) {
                propriedadePrincipal = p;
            } else {
                if (propriedadePrincipal.getProporcao().compareTo(p.getProporcao()) < 0) {
                    propriedadePrincipal = p;
                }
            }
        }
        return propriedadePrincipal;
    }


    public List<Propriedade> getPropriedadeVigente(Date dataOperacao) {
        dataOperacao = dataOperacao == null ? new Date() : dataOperacao;
        List<Propriedade> vigentes = Lists.newArrayList();
        if (propriedade != null) {
            propriedade.sort((p1, p2) -> {
                if (p1.getProporcao() != null && p2.getProporcao() != null && p1.getProporcao().equals(p2.getProporcao())) {
                    return p1.getPessoa() != null && p2.getPessoa() != null ?
                        p1.getPessoa().getNome().compareTo(p2.getPessoa().getNome()) : 0;
                } else {
                    return Double.compare(p2.getProporcao(), p1.getProporcao());
                }
            });
            try {
                for (Propriedade atual : propriedade) {
                    if (atual.getFinalVigencia() == null || atual.getFinalVigencia().after(dataOperacao)) {
                        vigentes.add(atual);
                    }
                }
            } catch (java.lang.NullPointerException ex) {
            }
        }
        return vigentes;
    }

    public List<Propriedade> getPropriedadeVigente() {
        return getPropriedadeVigente(null);
    }

    public List<Propriedade> getPropriedadeHistorico() {
        return getPropriedadeHistorico(null);
    }

    public List<Propriedade> getPropriedadeHistorico(Date dataOperacao) {
        dataOperacao = dataOperacao == null ? new Date() : dataOperacao;
        List<Propriedade> vigentes = new ArrayList<>();
        for (Propriedade atual : propriedade) {
            if (atual.getFinalVigencia() != null && atual.getFinalVigencia().before(dataOperacao)) {
                vigentes.add(atual);
            }
        }
        return vigentes;
    }

    public List<Compromissario> getCompromissarioVigente() {
        return getCompromissarioVigente(null);
    }

    public List<Compromissario> getCompromissarioVigente(Date dataOperacao) {
        dataOperacao = dataOperacao == null ? new Date() : dataOperacao;
        List<Compromissario> vigentes = new ArrayList<>();
        if (listaCompromissarios != null) {
            try {
                listaCompromissarios.sort(Comparator.comparing(c -> c.getCompromissario().getNome()));
                for (Compromissario atual : listaCompromissarios) {
                    if (atual.getFimVigencia() == null || DataUtil.dataSemHorario(atual.getFimVigencia()).compareTo(DataUtil.dataSemHorario(dataOperacao)) >= 0) {
                        vigentes.add(atual);
                    }
                }
            } catch (Exception ex) {
            }
        }
        return vigentes;
    }

    public List<Compromissario> getCompromissariosHistorico() {
        return getCompromissariosHistorico(null);
    }

    public List<Compromissario> getCompromissariosHistorico(Date dataOperacao) {
        dataOperacao = dataOperacao == null ? new Date() : dataOperacao;
        List<Compromissario> vigentes = new ArrayList<>();
        if (listaCompromissarios != null) {
            for (Compromissario atual : listaCompromissarios) {
                if (atual.getFimVigencia() != null && atual.getFimVigencia().before(dataOperacao)) {
                    vigentes.add(atual);
                }
            }
        }
        return vigentes;
    }

    public Propriedade getPropriedade(Pessoa pessoa) {
        if (propriedade != null) {
            try {
                for (Propriedade atual : propriedade) {
                    if ((atual.getPessoa().equals(pessoa)) && (atual.getFinalVigencia() == null || atual.getFinalVigencia().after(new Date()))) {
                        return atual;
                    }
                }
            } catch (java.lang.NullPointerException ex) {
            }
        }
        return null;
    }

    public List<Propriedade> getPropriedadeVigenteNaData(Date data) {
        List<Propriedade> vigentes = new ArrayList<>();
        try {
            for (Propriedade prop : propriedade) {
                if ((prop.getFinalVigencia() == null || prop.getFinalVigencia().after(data)) && prop.getInicioVigencia().before(data)) {
                    vigentes.add(prop);
                }
            }
        } catch (java.lang.NullPointerException ex) {
        }
        return vigentes;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public Map<Atributo, ValorAtributo> getAtributos() {
        if (atributos == null) {
            popularAtributos(Lists.newArrayList());
        }
        return atributos;
    }

    public void popularAtributos(List<Atributo> atributosPorClasse) {
        atributos = new HashMap();
        if (caracteristicasBci != null) {
            for (CaracteristicasBci carac : caracteristicasBci) {
                atributos.put(carac.getAtributo(), carac.getValorAtributo());
            }
        }
        if (atributosPorClasse != null) {
            for (Atributo atributo : atributosPorClasse) {
                if (!atributos.containsKey(atributo)) {
                    atributos.put(atributo, new ValorAtributo(atributo));
                }
            }
        }
    }

    public void setAtributos(Map<Atributo, ValorAtributo> atributos) {
        this.atributos = atributos;
    }

    public Cartorio getCartorio() {
        return cartorio;
    }

    public void setCartorio(Cartorio cartorio) {
        this.cartorio = cartorio;
    }

    public List<Construcao> getConstrucoesAtivas() {
        List<Construcao> retorno = Lists.newArrayList();
        for (Construcao construcao : construcoes) {
            if (!construcao.getCancelada()) {
                retorno.add(construcao);
            }
        }
        return retorno;
    }

    public void setConstrucoesAtivas(List<Construcao> construcoesAtivas) {
        this.construcoesAtivas = construcoesAtivas;
    }

    public List<Construcao> getConstrucoes() {
        return construcoes;
    }

    public void setConstrucoes(List<Construcao> construcoes) {
        this.construcoes = construcoes;
    }

    public Integer getFolhaRegistro() {
        return folhaRegistro;
    }

    public void setFolhaRegistro(Integer folhaRegistro) {
        this.folhaRegistro = folhaRegistro;
    }

    public AtoLegal getAtoLegalIsencao() {
        return atoLegalIsencao;
    }

    public void setAtoLegalIsencao(AtoLegal atoLegalIsencao) {
        this.atoLegalIsencao = atoLegalIsencao;
    }

    public Integer getLivroRegistro() {
        return livroRegistro;
    }

    public void setLivroRegistro(Integer livroRegistro) {
        this.livroRegistro = livroRegistro;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public String getMatriculaRegistro() {
        return matriculaRegistro;
    }

    public void setMatriculaRegistro(String matriculaRegistro) {
        this.matriculaRegistro = matriculaRegistro;
    }

    public Texto getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(Texto observacoes) {
        this.observacoes = observacoes;
    }

    public Processo getProcesso() {
        return processo;
    }

    public void setProcesso(Processo processo) {
        this.processo = processo;
    }

    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Date getDataInativacao() {
        return dataInativacao;
    }

    public void setDataInativacao(Date dataInativacao) {
        this.dataInativacao = dataInativacao;
    }

    public MotivoInativacaoImovel getMotivoInativacao() {
        return motivoInativacao;
    }

    public void setMotivoInativacao(MotivoInativacaoImovel motivoInativacao) {
        this.motivoInativacao = motivoInativacao;
    }

    @Override
    public String toString() {
        return codigo;
    }

    @Override
    public int compareTo(CadastroImobiliario o) {
        try {
            int var = this.codigo.compareTo(o.getCodigo());
            if (var == 0) {
                return this.codigo.compareTo(o.getCodigo());
            } else {
                return var;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public String getNumeroCadastro() {
        return this.inscricaoCadastral;
    }

    @Override
    public String getCadastroResponsavel() {
        return getInscricaoCadastral();
    }

    public BigDecimal getAreaTotalConstruida() {
        return areaTotalConstruida;
    }

    public void setAreaTotalConstruida(BigDecimal areaTotalConstruida) {
        this.areaTotalConstruida = areaTotalConstruida;
    }

    public Date getDataIsencao() {
        return dataIsencao;
    }

    public void setDataIsencao(Date dataIsencao) {
        this.dataIsencao = dataIsencao;
    }

    public TipoIsencao getTipoIsencao() {
        return tipoIsencao;
    }

    public void setTipoIsencao(TipoIsencao tipoIsencao) {
        this.tipoIsencao = tipoIsencao;
    }

    public List<CadastroImobInscrAnt> getInscricoesAnterioes() {
        return inscricoesAnterioes;
    }

    public void setInscricoesAnterioes(List<CadastroImobInscrAnt> inscricoesAnterioes) {
        this.inscricoesAnterioes = inscricoesAnterioes;
    }

    public boolean isIsento() {
        return this.tipoIsencao != null;
    }

    public String getComplementoEndereco() {
        return complementoEndereco;
    }

    public void setComplementoEndereco(String complementoEndereco) {
        this.complementoEndereco = complementoEndereco;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public Long getIdMaiorProprietario() {
        return idMaiorProprietario;
    }

    public void setIdMaiorProprietario(Long idMaiorProprietario) {
        this.idMaiorProprietario = idMaiorProprietario;
    }

    public List<EventoCalculoBCI> getEventosCalculoBCI() {
        return eventosCalculoBCI;
    }

    public void setEventosCalculoBCI(List<EventoCalculoBCI> eventosCalculoBCI) {
        this.eventosCalculoBCI = eventosCalculoBCI;
    }

    public List<GrupoAtributo> initEventosCalculadosBCI() {
        if (mapEventoCalculoBCIPorGrupoAtributo == null) {
            mapEventoCalculoBCIPorGrupoAtributo = Maps.newHashMap();
            for (EventoCalculoBCI evento : eventosCalculoBCI) {
                mapEventoCalculoBCIPorGrupoAtributo.computeIfAbsent(evento.getEventoCalculo().getGrupoAtributo(), k -> Lists.newArrayList());
                mapEventoCalculoBCIPorGrupoAtributo.get(evento.getEventoCalculo().getGrupoAtributo()).add(evento);
            }
        }
        return mapEventoCalculoBCIPorGrupoAtributo.keySet().stream()
            .sorted(Comparator.comparing(GrupoAtributo::getCodigo))
            .collect(Collectors.toList());
    }

    public Map<GrupoAtributo, List<EventoCalculoBCI>> getMapEventoCalculoBCIPorGrupoAtributo() {
        return mapEventoCalculoBCIPorGrupoAtributo;
    }

    public List<EventoCalculoConstrucao> getEventosCalculoConstrucoes() {
        return eventosCalculoConstrucoes;
    }

    public List<EventoCalculoConstrucao> getEventosCalculoDaConstrucao(Construcao construcao) {
        List<EventoCalculoConstrucao> eventosDaConstrucao = Lists.newArrayList();
        for (EventoCalculoConstrucao eventoCalculo : eventosCalculoConstrucoes) {
            if (eventoCalculo.getConstrucao().equals(construcao)) {
                eventosDaConstrucao.add(eventoCalculo);
            }
        }
        return eventosDaConstrucao;
    }

    public List<EnderecoCadastroImobiliario> getEnderecosCadastroImobiliario() {
        return enderecosCadastroImobiliario;
    }

    public void setEnderecosCadastroImobiliario(List<EnderecoCadastroImobiliario> enderecosCadastroImobiliario) {
        this.enderecosCadastroImobiliario = enderecosCadastroImobiliario;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public UnidadeExterna getUnidadeExterna() {
        return unidadeExterna;
    }

    public void setUnidadeExterna(UnidadeExterna unidadeExterna) {
        this.unidadeExterna = unidadeExterna;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public String getIdentificacaoPatrimonial() {
        return identificacaoPatrimonial;
    }

    public void setIdentificacaoPatrimonial(String identificacaoPatrimonial) {
        this.identificacaoPatrimonial = identificacaoPatrimonial;
    }

    public String getDescricaoProprietarios() {
        String s = "";
        for (Propriedade p : getPropriedade()) {
            if ((p.getFinalVigencia() == null) || p.getFinalVigencia().getTime() >= new Date().getTime()) {
                if (p.getPessoa() != null) {
                    s += p.getPessoa().getNome() + " " + p.getPessoa().getCpf_Cnpj() + "\n";
                }
            }
        }
        return s;
    }

    public String getEnderecoCompleto() {
        return new EnderecoCadastroStrings(lote.getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro().getNome(), numero, complementoEndereco, lote.getTestadaPrincipal().getFace().getLogradouroBairro().getBairro().getDescricao(), lote.getTestadaPrincipal().getFace().getCep(), lote.getSetor().getCodigo(), lote.getQuadra().getCodigo(), lote.getCodigoLote(), lote.getComplemento()).getEnderecoCompleto().replaceAll("Endereço do Imóvel: ", "");
    }

    public String getEnderecoCompletoResumido() {
        return new EnderecoCadastroStrings(lote.getTestadaPrincipal().getFace().getLogradouroBairro().getLogradouro().getNome(), numero, null, lote.getTestadaPrincipal().getFace().getLogradouroBairro().getBairro().getDescricao(), null, null, null, null, null).getEnderecoCompleto().replaceAll("Endereço do Imóvel: ", "");
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    public IsencaoCadastroImobiliario getIsencaoVigente() {
        for (IsencaoCadastroImobiliario isencao : isencoes) {
            if (!isencao.getVencido()) {
                return isencao;
            }
        }
        return null;
    }

    public BigDecimal getValorVenal() {
        for (EventoCalculoBCI evento : eventosCalculoBCI) {
            if (evento.getEventoCalculo().getIdentificacao().equals(EventoConfiguradoBCI.Identificacao.VALOR_VENAL)) {
                return evento.getValor();
            }
        }
        return BigDecimal.ZERO;
    }

    public List<Construcao> getContrucaoPorCodigo(Integer codigo) {
        List<Construcao> construcoesRetorno = Lists.newArrayList();
        if (codigo != null) {
            Collections.sort(construcoes);
            for (Construcao construcao : construcoes) {
                if (construcao.getCodigo().equals(codigo)) {
                    Util.adicionarObjetoEmLista(construcoesRetorno, construcao);
                }
            }
        }
        return construcoesRetorno;
    }

    public void cancelaTodasConstrucoes() {
        for (Construcao construcao : getConstrucoesAtivas()) {
            construcao.setCancelada(true);
        }
    }

    public boolean hasProprietarioAnterior(Pessoa pessoa) {
        if (propriedade != null) {
            for (Propriedade propriedade : propriedade) {
                if (propriedade.getFinalVigencia() != null && propriedade.getPessoa().getId().equals(pessoa.getId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static class EnderecoCadastroStrings {
        String logradouro;
        String numero;
        String complemento;
        String bairro;
        String cep;
        String setor;
        String quadra;
        String lote;
        String complementoLote;
        String loteamento;

        public EnderecoCadastroStrings() {

        }

        public EnderecoCadastroStrings(String logradouro, String numero, String complemento, String bairro, String cep, String setor, String quadra, String lote, String complementoLote) {
            this.logradouro = logradouro;
            this.numero = numero;
            this.complemento = complemento;
            this.bairro = bairro;
            this.cep = cep;
            this.setor = setor;
            this.quadra = quadra;
            this.lote = lote;
            this.complementoLote = complementoLote;
        }

        public void setLogradouro(String logradouro) {
            this.logradouro = logradouro;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public void setComplemento(String complemento) {
            this.complemento = complemento;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public void setLoteamento(String loteamento) {
            this.loteamento = loteamento;
        }

        public void setSetor(String setor) {
            this.setor = setor;
        }

        public void setQuadra(String quadra) {
            this.quadra = quadra;
        }

        public void setLote(String lote) {
            this.lote = lote;
        }

        public void setCep(String cep) {
            this.cep = cep;
        }

        public void setComplementoLote(String complementoLote) {
            this.complementoLote = complementoLote;
        }

        public String getEnderecoCompleto() {
            StringBuilder sb = new StringBuilder();
            sb.append("Endereço do Imóvel: ");
            if (logradouro != null) {
                sb.append(logradouro).append(", ");
            }
            if (numero != null) {
                sb.append(numero).append(", ");
            }
            if (complemento != null) {
                sb.append(complemento).append(", ");
            }
            if (bairro != null) {
                sb.append(bairro).append(", ");
            }
            if (cep != null) {
                sb.append(cep).append(", ");
            }
            if (setor != null) {
                sb.append("Setor ").append(setor).append(", ");
            }
            if (quadra != null) {
                sb.append("Quadra ").append(quadra).append(", ");
            }
            if (lote != null) {
                sb.append("Lote ").append(lote);
                if (complementoLote != null) {
                    sb.append(" - ").append(complementoLote);
                }
                sb.append(", ");
            }
            if (loteamento != null) {
                sb.append("Loteamento: ").append(loteamento);
            }
            if (sb.toString().endsWith(", ")) {
                sb.replace(sb.toString().length() - 2, sb.toString().length(), "");
            }
            return sb.toString();
        }
    }

    public List<CadastroImobiliarioImpressaoHist> getCadastroImobiliarioImpressaoHists() {
        return cadastroImobiliarioImpressaoHists;
    }

    public void setCadastroImobiliarioImpressaoHists(List<CadastroImobiliarioImpressaoHist> cadastroImobiliarioImpressaoHists) {
        this.cadastroImobiliarioImpressaoHists = cadastroImobiliarioImpressaoHists;
    }

    public Long getAutonoma() {
        return autonoma == null ? 1 : autonoma;
    }

    public void setAutonoma(Long autonoma) {
        this.autonoma = autonoma;
    }

    public List<CaracteristicasBci> getCaracteristicasBci() {
        return caracteristicasBci;
    }

    public void setCaracteristicasBci(List<CaracteristicasBci> caracteristicasBci) {
        this.caracteristicasBci = caracteristicasBci;
    }

    public void popularCaracteristicas() {
        if (atributos != null) {
            if (caracteristicasBci == null) {
                caracteristicasBci = new ArrayList();
            }
            CaracteristicasBci caracteristica;
            boolean registrado = false;
            for (Atributo atributo : atributos.keySet()) {
                registrado = false;
                for (CaracteristicasBci c : this.caracteristicasBci) {
                    if (c.getAtributo().equals(atributo)) {
                        c.setValorAtributo(atributos.get(atributo));
                        registrado = true;
                        break;
                    }
                }
                if (registrado) {
                    continue;
                }
                caracteristica = new CaracteristicasBci();
                caracteristica.setAtributo(atributo);
                caracteristica.setValorAtributo(atributos.get(atributo));
                caracteristica.setCadastroImobiliario(this);
                this.caracteristicasBci.add(caracteristica);
            }
        }
    }

    public List<Compromissario> getListaCompromissarios() {
        if (listaCompromissarios == null) {
            return Lists.newArrayList();
        }
        return listaCompromissarios;
    }

    public void setListaCompromissarios(List<Compromissario> listaCompromissarios) {
        this.listaCompromissarios = listaCompromissarios;
    }

    public Boolean getGerarNovoCodigoAutenticidade() {
        return gerarNovoCodigoAutenticidade != null ? gerarNovoCodigoAutenticidade : false;
    }

    public void setGerarNovoCodigoAutenticidade(Boolean gerarNovoCodigoAtenticidade) {
        this.gerarNovoCodigoAutenticidade = gerarNovoCodigoAtenticidade;
    }

    public String getCoordenadasSIT() {
        return coordenadasSIT;
    }

    public void setCoordenadasSIT(String coordenadasSIT) {
        this.coordenadasSIT = coordenadasSIT;
    }

    @Override
    public CadastroImobiliarioNfseDTO toNfseDto() {
        CadastroImobiliarioNfseDTO dto = new CadastroImobiliarioNfseDTO();
        dto.setId(getId());
        dto.setInscricaoCadastral(inscricaoCadastral);
        return dto;
    }

    public List<ProcRegularizaConstrucao> getProcessosDeRegularizacao() {
        return processosDeRegularizacao;
    }

    public void setProcessosDeRegularizacao(List<ProcRegularizaConstrucao> processosDeRegularizacao) {
        this.processosDeRegularizacao = processosDeRegularizacao;
    }

    public List<AlvaraConstrucao> getAlvarasContrucao() {
        return alvarasContrucao;
    }

    public void setAlvarasContrucao(List<AlvaraConstrucao> alvarasContrucao) {
        this.alvarasContrucao = alvarasContrucao;
    }

    public List<Habitese> getHabiteses() {
        return habiteses;
    }

    public void setHabiteses(List<Habitese> habiteses) {
        this.habiteses = habiteses;
    }
}
