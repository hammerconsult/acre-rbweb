package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoProcesso;
import br.com.webpublico.enums.TipoProcessoTramite;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@GrupoDiagrama(nome = "Protocolo")
@Etiqueta("Protocolo/Processo")
@Entity
@Audited
public class Processo extends SuperEntidade implements Comparable<Processo> {

    private static final long serialVersionUID = 1L;
    public static String PROTOCOLO_CRIADO = "Protocolo Criado";
    public static String PROTOCOLO_ACEITO = "Protocolo Aceito";
    public static String PROTOCOLO_ALTERADO = "Protocolo Alterado";
    public static String PROTOCOLO_DESMENBRADO = "Protocolo Desmembrado";
    public static String PROTOCOLO_INCORPORADO = "Protocolo Incorporado";
    public static String PROTOCOLO_ENCAMINHADO = "Protocolo Encaminhado";
    public static String PROTOCOLO_ENCERRADO = "Protocolo Encerrado";
    public static String PROTOCOLO_ARQUIVADO = "Protocolo Arquivado";
    public static String PROTOCOLO_CANCELADO = "Protocolo Cancelado";
    public static String PROTOCOLO_REABERTO = "Protocolo Reaberto";

    public static String ANEXO_ADICIONADO = "Anexo Adicionado";
    public static String ANEXO_REMOVIDO = "Anexo Removido";

    @Invisivel
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    List<ProcessoEnglobado> processosEnglobados;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código")
    private Long id;

    @Tabelavel(ordemApresentacao = 1)
    @Etiqueta("Número")
    private Integer numero;

    @Tabelavel(ordemApresentacao = 2)
    @Etiqueta("Ano")
    private Integer ano;

    @Tabelavel(ordemApresentacao = 3)
    @Etiqueta("Data")
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;

    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;

    @ManyToOne
    @Etiqueta("Autor/Requerente")
    private Pessoa pessoa;

    @Etiqueta("Tipo de Processo")
    @Enumerated(EnumType.STRING)
    private TipoProcessoTramite tipoProcessoTramite;

    @ManyToOne
    @Etiqueta("Subassunto - Assunto")
    private SubAssunto subAssunto;

    @Tabelavel(ordemApresentacao = 5)
    @Etiqueta("Assunto")
    @Transient
    private String descricaoAssunto;

    @Tabelavel(ordemApresentacao = 7)
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoProcesso situacao;

    @Invisivel
    @Etiqueta("Trâmites")
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy(value = "indice")
    private List<Tramite> tramites;

    private Integer senha;
    @Invisivel
    @Etiqueta("Documentos")
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentoProcesso> documentoProcesso;

    @Etiqueta("Arquivos")
    @Invisivel
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoArquivo> arquivos;

    @Etiqueta("Observações")
    private String observacoes;

    @Etiqueta("Resumo/Conteúdo:")
    private String objetivo;

    private String assunto;
    private Boolean protocolo;
    private Boolean encaminhamentoMultiplo;
    @ManyToOne
    private UsuarioSistema responsavelCadastro;

    @Etiqueta("Mudança de situação")
    private String motivo;
    private Boolean confidencial;
    private String numeroProcessoAntigo;
    private String numeroProcessoSAJ;

    @ManyToOne
    private Tramite tramiteFinalizador;

    @ManyToOne
    private Processo processoSuperior;

    @Invisivel
    @OneToMany(mappedBy = "processoSuperior", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Processo> processosSubordinados;

    @Etiqueta("Cadastrado pela")
    @ManyToOne //representa na tela o Cadastrado pela
    private UnidadeOrganizacional uoCadastro;

    @Transient
    @Tabelavel(ordemApresentacao = 6)
    @Etiqueta("Unidade de Cadastro")
    private String descricaoUnidadeCadastro;

    @Transient
    @Tabelavel(ordemApresentacao = 9)
    @Etiqueta("Unidade de Destino")
    private String unidadeAtual;

    @Transient
    @Tabelavel(ordemApresentacao = 4)
    @Etiqueta("Solicitante")
    private String nomePessoa;

    private int sequencia;
    @Invisivel
    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoHistorico> historicos;

    @Transient
    private String nomeAutorRequerente;
    @Transient
    private String nomeAssuntoProcesso;
    @Version
    private Long versao;

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProcessoRota> rotas;

    @Etiqueta("Motivo de Cancelamento")
    private String motivoCancelamento;

    @Etiqueta("Data de Cancelamento")
    private Date dataCancelamento;

    public Processo() {
        protocolo = Boolean.FALSE;
        confidencial = Boolean.FALSE;
        encaminhamentoMultiplo = Boolean.FALSE;
        tramites = new ArrayList<Tramite>();
        arquivos = new ArrayList<ProcessoArquivo>();
        documentoProcesso = new ArrayList<DocumentoProcesso>();
        processosSubordinados = new ArrayList<Processo>();
        processosEnglobados = new ArrayList<ProcessoEnglobado>();
        tipoProcessoTramite = TipoProcessoTramite.INTERNO;
        historicos = Lists.newArrayList();
        sequencia = 1;
        rotas = new ArrayList<>();
    }

    public Processo(Long id, Integer numero, Integer ano, Date dataRegistro, Pessoa pessoa,
                    String assunto, TipoProcessoTramite tipoProcessoTramite, SituacaoProcesso situacao,
                    Tramite ultimoTramite, UnidadeOrganizacional unidadeOrganizacional, ProcessoHistorico ultimoHistorico) {
        this(id, numero, ano, dataRegistro, pessoa, assunto, tipoProcessoTramite, situacao);
        this.setUoCadastro(unidadeOrganizacional);
        this.sequencia = 1;
        this.tramites = Lists.newArrayList(ultimoTramite);
        this.historicos = Lists.newArrayList(ultimoHistorico);
    }

    public Processo(Long id, Integer numero, Integer ano, Date dataRegistro, Pessoa pessoa, String assunto, TipoProcessoTramite tipoProcessoTramite, SituacaoProcesso situacao) {
        tramites = new ArrayList<Tramite>();
        historicos = Lists.newArrayList();
        arquivos = new ArrayList<ProcessoArquivo>();
        documentoProcesso = new ArrayList<DocumentoProcesso>();
        processosSubordinados = new ArrayList<Processo>();
        processosEnglobados = new ArrayList<ProcessoEnglobado>();
        sequencia = 1;

        this.id = id;
        this.numero = numero;
        this.ano = ano;
        this.dataRegistro = dataRegistro;
        this.pessoa = pessoa;
        this.assunto = assunto;
        this.descricaoAssunto = assunto;
        this.tipoProcessoTramite = tipoProcessoTramite;
        this.situacao = situacao;
    }

    public Processo(Long id, Integer numero, Integer ano, Date dataRegistro, Pessoa pessoa, SubAssunto subAssunto, TipoProcessoTramite tipoProcessoTramite, SituacaoProcesso situacao, String unidadeAtual) {
        this(id, numero, ano, dataRegistro, pessoa, subAssunto, tipoProcessoTramite, situacao);
        this.unidadeAtual = unidadeAtual;
        sequencia = 1;
    }

    public Processo(Long id, Integer numero, Integer ano, Date dataRegistro, Pessoa pessoa, SubAssunto subAssunto, TipoProcessoTramite tipoProcessoTramite, SituacaoProcesso situacao) {
        tramites = new ArrayList<Tramite>();
        historicos = Lists.newArrayList();
        arquivos = new ArrayList<ProcessoArquivo>();
        documentoProcesso = new ArrayList<DocumentoProcesso>();
        processosSubordinados = new ArrayList<Processo>();
        processosEnglobados = new ArrayList<ProcessoEnglobado>();
        sequencia = 1;

        this.id = id;
        this.numero = numero;
        this.ano = ano;
        this.dataRegistro = dataRegistro;
        this.pessoa = pessoa;
        this.subAssunto = subAssunto;
        this.descricaoAssunto = subAssunto.getDescricao();
        this.tipoProcessoTramite = tipoProcessoTramite;
        this.situacao = situacao;
    }

    public Boolean getEncaminhamentoMultiplo() {
        return encaminhamentoMultiplo;
    }

    public void setEncaminhamentoMultiplo(Boolean encaminhamentoMultiplo) {
        this.encaminhamentoMultiplo = encaminhamentoMultiplo;
    }

    public UsuarioSistema getResponsavelCadastro() {
        return responsavelCadastro;
    }

    public void setResponsavelCadastro(UsuarioSistema responsavelCadastro) {
        this.responsavelCadastro = responsavelCadastro;
    }

    public Boolean getProtocolo() {
        return protocolo != null ? protocolo : false;
    }

    public void setProtocolo(Boolean protocolo) {
        this.protocolo = protocolo;
    }

    public String getAssunto() {
        return assunto;
    }

    public void setAssunto(String assunto) {
        if (assunto != null) {
            this.assunto = assunto.toUpperCase();
        }
    }

    public UnidadeOrganizacional getUoCadastro() {
        return uoCadastro;
    }

    public void setUoCadastro(UnidadeOrganizacional uoCadastro) {
        this.uoCadastro = uoCadastro;
    }

    public String getObjetivo() {
        return objetivo;
    }

    public void setObjetivo(String objetivo) {
        this.objetivo = objetivo;
    }

    public Integer getSenha() {
        return senha;
    }

    public void setSenha(Integer senha) {
        this.senha = senha;
    }

    public List<Tramite> getTramites() {
        if (tramites != null) {
            Collections.sort(tramites);
        }
        return tramites;
    }

    public void setTramites(List<Tramite> tramites) {
        this.tramites = tramites;
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

    public SituacaoProcesso getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcesso situacao) {
        this.situacao = situacao;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public SubAssunto getSubAssunto() {
        return subAssunto;
    }

    public void setSubAssunto(SubAssunto SubAssunto) {
        this.subAssunto = SubAssunto;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa Pessoa) {
        this.pessoa = Pessoa;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public List<DocumentoProcesso> getDocumentoProcesso() {
        return documentoProcesso;
    }

    public void setDocumentoProcesso(List<DocumentoProcesso> documentoProcesso) {
        this.documentoProcesso = documentoProcesso;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    @Override
    public String toString() {
        if (protocolo) {
            return "Protocolo: " + numero + "/" + ano;
        } else {
            return "Processo: " + numero + "/" + ano;
        }
    }

    public List<ProcessoArquivo> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ProcessoArquivo> arquivos) {
        this.arquivos = arquivos;
    }

    public Boolean getConfidencial() {
        return confidencial;
    }

    public void setConfidencial(Boolean confidencial) {
        this.confidencial = confidencial;
    }

    public String getNumeroProcessoAntigo() {
        return numeroProcessoAntigo;
    }

    public void setNumeroProcessoAntigo(String numeroProcessoAntigo) {
        this.numeroProcessoAntigo = numeroProcessoAntigo;
    }

    public String getNumeroProcessoSAJ() {
        return numeroProcessoSAJ;
    }

    public void setNumeroProcessoSAJ(String numeroProcessoSAJ) {
        this.numeroProcessoSAJ = numeroProcessoSAJ;
    }

    public TipoProcessoTramite getTipoProcessoTramite() {
        return tipoProcessoTramite;
    }

    public void setTipoProcessoTramite(TipoProcessoTramite tipoProcessoTramite) {
        this.tipoProcessoTramite = tipoProcessoTramite;
    }

    public Tramite getTramiteFinalizador() {
        return tramiteFinalizador;
    }

    public void setTramiteFinalizador(Tramite tramiteFinalizador) {
        this.tramiteFinalizador = tramiteFinalizador;
    }

    public Processo getProcessoSuperior() {
        return processoSuperior;
    }

    public void setProcessoSuperior(Processo processoSuperior) {
        this.processoSuperior = processoSuperior;
    }

    public List<Processo> getProcessosSubordinados() {
        return processosSubordinados;
    }

    public void setProcessosSubordinados(List<Processo> processosSubordinados) {
        this.processosSubordinados = processosSubordinados;
    }

    public String getDescricaoAssunto() {
        return descricaoAssunto;
    }

    public void setDescricaoAssunto(String descricaoAssunto) {
        this.descricaoAssunto = descricaoAssunto;
    }

    @Override
    public int compareTo(Processo o) {
        return o.getId().compareTo(this.id);
    }

    public String getUnidadeAtual() {
        return unidadeAtual;
    }

    public void setUnidadeAtual(String unidadeAtual) {
        this.unidadeAtual = unidadeAtual;
    }

    public int getSequencia() {
        return sequencia;
    }

    public void setSequencia(int sequencia) {
        this.sequencia = sequencia;
    }

    public String getNomeAutorRequerente() {
        if (pessoa != null) {
            return pessoa.getNome();
        }
        return nomeAutorRequerente;
    }

    public void setNomeAutorRequerente(String nomeAutorRequerente) {
        this.nomeAutorRequerente = nomeAutorRequerente;
    }

    public String getNomeAssuntoProcesso() {
        if (subAssunto != null) {
            return subAssunto.getNome() + " - " + subAssunto.getAssunto().getNome();
        }
        return nomeAssuntoProcesso;
    }

    public void setNomeAssuntoProcesso(String nomeAssuntoProcesso) {
        this.nomeAssuntoProcesso = nomeAssuntoProcesso;
    }

    public List<ProcessoEnglobado> getProcessosEnglobados() {
        return processosEnglobados;
    }

    public void setProcessosEnglobados(List<ProcessoEnglobado> processosEnglobados) {
        this.processosEnglobados = processosEnglobados;
    }

    public String getDescricaoUnidadeCadastro() {
        return descricaoUnidadeCadastro;
    }

    public void setDescricaoUnidadeCadastro(String descricaoUnidadeCadastro) {
        this.descricaoUnidadeCadastro = descricaoUnidadeCadastro;
    }

    public Integer buscarMaiorIndiceDoTramite() {
        Integer maior = -1;
        for (Tramite tramite : tramites) {
            if (tramite.getIndice() > maior) {
                maior = tramite.getIndice();
            }
        }
        return maior;
    }

    public Tramite buscarUltimoTramite() {
        Tramite tramiteMaior = null;
        for (Tramite tramite : tramites) {
            if (tramiteMaior == null || tramite.getIndice() > tramiteMaior.getIndice()) {
                tramiteMaior = tramite;
            }
        }
        return tramiteMaior;
    }

    public String getNomePessoa() {
        return nomePessoa;
    }

    public void setNomePessoa(String nomePessoa) {
        this.nomePessoa = nomePessoa;
    }

    public Long getVersao() {
        return versao;
    }

    public void setVersao(Long versao) {
        this.versao = versao;
    }

    public List<ProcessoHistorico> getHistoricos() {
        return historicos;
    }

    public void setHistoricos(List<ProcessoHistorico> historicos) {
        this.historicos = historicos;
    }

    public List<ProcessoHistorico> getHistoricosOrdenado() {
        List<ProcessoHistorico> proc = getHistoricos();
        if (proc != null) {
            Collections.sort(proc);
        }
        return proc;
    }

    public ProcessoHistorico criarHistoricoProcesso(String descricao, UnidadeOrganizacional unidade, UsuarioSistema usuarioSistema) {
        return new ProcessoHistorico(usuarioSistema, getSituacao(), descricao, this, unidade);
    }

    public ProcessoHistorico criarHistoricoProcessoExterno(String descricao, String unidadeExterna, UsuarioSistema usuarioSistema) {
        return new ProcessoHistorico(usuarioSistema, getSituacao(), descricao, this, unidadeExterna);
    }

    public boolean isProcessoInterno(){
        return TipoProcessoTramite.INTERNO.equals(this.tipoProcessoTramite);
    }

    public boolean isProcessoExterno(){
        return TipoProcessoTramite.EXTERNO.equals(this.tipoProcessoTramite);
    }

    public List<ProcessoRota> getRotas() {
        return rotas;
    }

    public void setRotas(List<ProcessoRota> rotas) {
        this.rotas = rotas;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public Date getDataCancelamento() {
        return dataCancelamento;
    }

    public void setDataCancelamento(Date dataCancelamento) {
        this.dataCancelamento = dataCancelamento;
    }

}
