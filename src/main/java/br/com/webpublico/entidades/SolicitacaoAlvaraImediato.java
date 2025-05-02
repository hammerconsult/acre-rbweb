package br.com.webpublico.entidades;

import br.com.webpublico.entidades.comum.RespostaFormulario;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ItemServicoConstrucao;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ResponsavelServico;
import br.com.webpublico.enums.SituacaoAlvaraImediato;
import br.com.webpublico.enums.TipoSolicitacaoAlvaraImediato;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@Etiqueta("Solicitação de Alvará Imediato")
public class SolicitacaoAlvaraImediato extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UsuarioWeb usuarioWeb;
    @ManyToOne
    private Exercicio exercicio;
    private Long codigo;
    @Temporal(TemporalType.DATE)
    private Date dataSolicitacao;
    private String numeroProtocolo;
    private Integer anoProtocolo;
    @Enumerated(EnumType.STRING)
    private SituacaoAlvaraImediato situacao;
    @Temporal(TemporalType.DATE)
    private Date dataEfetivacao;
    @ManyToOne
    private UsuarioSistema usuarioEfetivacao;
    @ManyToOne
    private CadastroImobiliario cadastroImobiliario;
    @Enumerated(EnumType.STRING)
    private TipoSolicitacaoAlvaraImediato tipoSolicitacao;
    @Temporal(TemporalType.DATE)
    private Date dataInicio;
    @Temporal(TemporalType.DATE)
    private Date dataConclusao;
    private String cei;
    @ManyToOne
    private ItemServicoConstrucao tipoObra;
    private BigDecimal areaTerreno;
    private BigDecimal areaExistente;
    private BigDecimal areaTotal;
    private BigDecimal areaConstruir;
    private BigDecimal areaDemolir;
    private Integer numeroPavimentos;
    private String descricaoArea;
    private String tipoImovel;
    private String utilizacaoImovel;
    @ManyToOne
    private ResponsavelServico autorProjeto;
    private String artRrtAutorProjeto;
    @ManyToOne
    private ResponsavelServico responsavelExecucao;
    private String artRrtResponsavelExecucao;
    @ManyToOne
    private Zona zona;
    @ManyToOne
    private ClassificacaoUso classificacaoUso;
    @ManyToOne
    private ClassificacaoUsoItem classificacaoUsoItem;
    @ManyToOne
    private HierarquiaVia hierarquiaVia;
    @OneToOne
    private RespostaFormulario respostaFormulario;
    @OneToMany(mappedBy = "solicitacao", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("dataRegistro")
    private List<SolicitacaoAlvaraImediatoHistorico> historicos;
    @OneToOne(cascade = CascadeType.ALL)
    private ProcRegularizaConstrucao procRegularizaConstrucao;
    private String justificativa;
    @OneToMany(mappedBy = "solicitacao")
    private List<ExigenciaAlvaraImediato> exigenciasAlvaraImediato;
    private String email;
    private Boolean alvaraEnviadoPorEmail;
    @ManyToOne
    private UsuarioSistema analistaResponsavel;
    private String numeroProtocoloDeferimento;
    private String anoProtocoloDeferimento;
    private String observacao;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioWeb getUsuarioWeb() {
        return usuarioWeb;
    }

    public void setUsuarioWeb(UsuarioWeb usuarioWeb) {
        this.usuarioWeb = usuarioWeb;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public Integer getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(Integer anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public SituacaoAlvaraImediato getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoAlvaraImediato situacao) {
        this.situacao = situacao;
    }

    public Date getDataEfetivacao() {
        return dataEfetivacao;
    }

    public void setDataEfetivacao(Date dataEfetivacao) {
        this.dataEfetivacao = dataEfetivacao;
    }

    public UsuarioSistema getUsuarioEfetivacao() {
        return usuarioEfetivacao;
    }

    public void setUsuarioEfetivacao(UsuarioSistema usuarioEfetivacao) {
        this.usuarioEfetivacao = usuarioEfetivacao;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public TipoSolicitacaoAlvaraImediato getTipoSolicitacao() {
        return tipoSolicitacao;
    }

    public void setTipoSolicitacao(TipoSolicitacaoAlvaraImediato tipoSolicitacao) {
        this.tipoSolicitacao = tipoSolicitacao;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(Date dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getCei() {
        return cei;
    }

    public void setCei(String cei) {
        this.cei = cei;
    }

    public ItemServicoConstrucao getTipoObra() {
        return tipoObra;
    }

    public void setTipoObra(ItemServicoConstrucao tipoObra) {
        this.tipoObra = tipoObra;
    }

    public BigDecimal getAreaTerreno() {
        return areaTerreno;
    }

    public void setAreaTerreno(BigDecimal areaTerreno) {
        this.areaTerreno = areaTerreno;
    }

    public BigDecimal getAreaExistente() {
        return areaExistente;
    }

    public void setAreaExistente(BigDecimal areaExistente) {
        this.areaExistente = areaExistente;
    }

    public BigDecimal getAreaTotal() {
        return areaTotal;
    }

    public void setAreaTotal(BigDecimal areaTotal) {
        this.areaTotal = areaTotal;
    }

    public BigDecimal getAreaConstruir() {
        return areaConstruir;
    }

    public void setAreaConstruir(BigDecimal areaConstruir) {
        this.areaConstruir = areaConstruir;
    }

    public BigDecimal getAreaDemolir() {
        return areaDemolir;
    }

    public void setAreaDemolir(BigDecimal areaDemolir) {
        this.areaDemolir = areaDemolir;
    }

    public Integer getNumeroPavimentos() {
        return numeroPavimentos;
    }

    public void setNumeroPavimentos(Integer numeroPavimentos) {
        this.numeroPavimentos = numeroPavimentos;
    }

    public String getDescricaoArea() {
        return descricaoArea;
    }

    public void setDescricaoArea(String descricaoArea) {
        this.descricaoArea = descricaoArea;
    }

    public String getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(String tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public String getUtilizacaoImovel() {
        return utilizacaoImovel;
    }

    public void setUtilizacaoImovel(String utilizacaoImovel) {
        this.utilizacaoImovel = utilizacaoImovel;
    }

    public ResponsavelServico getAutorProjeto() {
        return autorProjeto;
    }

    public void setAutorProjeto(ResponsavelServico autorProjeto) {
        this.autorProjeto = autorProjeto;
    }

    public String getArtRrtAutorProjeto() {
        return artRrtAutorProjeto;
    }

    public void setArtRrtAutorProjeto(String artRrtAutorProjeto) {
        this.artRrtAutorProjeto = artRrtAutorProjeto;
    }

    public ResponsavelServico getResponsavelExecucao() {
        return responsavelExecucao;
    }

    public void setResponsavelExecucao(ResponsavelServico responsavelExecucao) {
        this.responsavelExecucao = responsavelExecucao;
    }

    public String getArtRrtResponsavelExecucao() {
        return artRrtResponsavelExecucao;
    }

    public void setArtRrtResponsavelExecucao(String artRrtResponsavelExecucao) {
        this.artRrtResponsavelExecucao = artRrtResponsavelExecucao;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public ClassificacaoUso getClassificacaoUso() {
        return classificacaoUso;
    }

    public void setClassificacaoUso(ClassificacaoUso classificacaoUso) {
        this.classificacaoUso = classificacaoUso;
    }

    public ClassificacaoUsoItem getClassificacaoUsoItem() {
        return classificacaoUsoItem;
    }

    public void setClassificacaoUsoItem(ClassificacaoUsoItem classificacaoUsoItem) {
        this.classificacaoUsoItem = classificacaoUsoItem;
    }

    public HierarquiaVia getHierarquiaVia() {
        return hierarquiaVia;
    }

    public void setHierarquiaVia(HierarquiaVia hierarquiaVia) {
        this.hierarquiaVia = hierarquiaVia;
    }

    public RespostaFormulario getRespostaFormulario() {
        return respostaFormulario;
    }

    public void setRespostaFormulario(RespostaFormulario respostaFormulario) {
        this.respostaFormulario = respostaFormulario;
    }

    public List<SolicitacaoAlvaraImediatoHistorico> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<SolicitacaoAlvaraImediatoHistorico> historicos) {
        this.historicos = historicos;
    }

    public ProcRegularizaConstrucao getProcRegularizaConstrucao() {
        return procRegularizaConstrucao;
    }

    public void setProcRegularizaConstrucao(ProcRegularizaConstrucao procRegularizaConstrucao) {
        this.procRegularizaConstrucao = procRegularizaConstrucao;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public List<ExigenciaAlvaraImediato> getExigenciasAlvaraImediato() {
        return exigenciasAlvaraImediato;
    }

    public void setExigenciasAlvaraImediato(List<ExigenciaAlvaraImediato> exigenciasAlvaraImediato) {
        this.exigenciasAlvaraImediato = exigenciasAlvaraImediato;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAlvaraEnviadoPorEmail() {
        return alvaraEnviadoPorEmail;
    }

    public void setAlvaraEnviadoPorEmail(Boolean alvaraEnviadoPorEmail) {
        this.alvaraEnviadoPorEmail = alvaraEnviadoPorEmail;
    }

    public UsuarioSistema getAnalistaResponsavel() {
        return analistaResponsavel;
    }

    public void setAnalistaResponsavel(UsuarioSistema analistaResponsavel) {
        this.analistaResponsavel = analistaResponsavel;
    }

    public String getNumeroProtocoloDeferimento() {
        return numeroProtocoloDeferimento;
    }

    public void setNumeroProtocoloDeferimento(String numeroProtocoloDeferimento) {
        this.numeroProtocoloDeferimento = numeroProtocoloDeferimento;
    }

    public String getAnoProtocoloDeferimento() {
        return anoProtocoloDeferimento;
    }

    public void setAnoProtocoloDeferimento(String anoProtocoloDeferimento) {
        this.anoProtocoloDeferimento = anoProtocoloDeferimento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public void adicionarHistorico(Date dataRegistro, SituacaoAlvaraImediato situacao) {
        if (historicos == null) {
            historicos = Lists.newArrayList();
        }
        SolicitacaoAlvaraImediatoHistorico historico = new SolicitacaoAlvaraImediatoHistorico();
        historico.setSolicitacao(this);
        historico.setDataRegistro(dataRegistro);
        historico.setSituacao(situacao);
        this.situacao = situacao;
        this.historicos.add(historico);
    }

    public Integer getUltimoSequencialExigencia() {
        if (exigenciasAlvaraImediato == null || exigenciasAlvaraImediato.isEmpty()) {
            return 0;
        }
        return exigenciasAlvaraImediato.stream()
            .map(ExigenciaAlvaraImediato::getSequencial)
            .max(Integer::compare)
            .get();
    }
}
