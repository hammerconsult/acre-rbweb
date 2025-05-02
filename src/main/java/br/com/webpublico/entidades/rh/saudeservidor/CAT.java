package br.com.webpublico.entidades.rh.saudeservidor;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.esocial.IniciativaCAT;
import br.com.webpublico.enums.rh.esocial.LateralidadeParteAtingida;
import br.com.webpublico.enums.rh.esocial.OrgaoClasse;
import br.com.webpublico.esocial.comunicacao.enums.TipoInscricaoESocial;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @Author peixe on 12/09/2016  17:30.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Saúde do Servidor")
@Etiqueta("Comunicado de Acidente de Trabalho - CAT")
public class CAT extends SuperEntidade implements IHistoricoEsocial {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @ManyToOne
    @Etiqueta("Colaborador")
    @Pesquisavel
    @Obrigatorio
    private PessoaFisica colaborador;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data/Hora Ocorrência")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private Date ocorridoEm;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Acidente")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private TipoAcidenteCAT tipoAcidente;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo da CAT")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
    private TipoCAT tipoCat;
    @Etiqueta("Houve Óbito")
    @Pesquisavel
    private Boolean houveObito;
    @Etiqueta("Houve Comunicação à Autoridade Policial")
    private Boolean houveComunicacaoPolicial;
    @Etiqueta("Origem da CAT")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    private OrigemCAT origemCAT;

    @Etiqueta("Local do Acidente")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private LocalAcidente localAcidente;

    @Etiqueta("Tipo de Logradouro")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private TipoLogradouroEnderecoCorreio tipoLogradouro;

    @Obrigatorio
    @Etiqueta("Descrição do logradouro")
    private String descricaoLogradouro;

    @Obrigatorio
    @Etiqueta("Número do logradouro")
    private String numeroLogradouro;

    @Obrigatorio
    @Etiqueta("Complemento do logradouro")
    private String complementoLogradouro;

    @Obrigatorio
    @Etiqueta("Nome do bairro/distrito")
    private String bairroDistritoLogradouro;

    @Obrigatorio
    @Etiqueta("Código de Endereçamento Postal - CEP")
    private String cepLogradouro;

    @Obrigatorio
    @Etiqueta("Código do município")
    private String codigoMunicipio;

    @Obrigatorio
    @Etiqueta("Sigla da Unidade da Federação")
    @ManyToOne
    private UF uf;

    @Obrigatorio
    @Etiqueta("Sigla da UF do órgão de classe")
    @ManyToOne
    private UF ufOrgaoClasse;

    @Obrigatorio
    @Etiqueta("Código do país")
    private String codigoPais;


    @Obrigatorio
    @Etiqueta("Código de Endereçamento Postal")
    private String codigoEnderecamentoPostal;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de inscrição do local onde ocorreu o acidente ou a doença ocupacional ")
    private TipoInscricaoESocial tipoInscricaoAcidenteDoenca;

    @Obrigatorio
    @Etiqueta("Número de inscrição do estabelecimento")
    private String numeroInscEstabelecimento;

    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Etiqueta(" Lateralidade da(s) parte(s) atingida(s)")
    private LateralidadeParteAtingida lateralidadeParteAtingida;

    @Obrigatorio
    @Etiqueta("Descrição do Local do Acidente")
    @Pesquisavel
    private String descricaoLocalAcidente;
    @Pesquisavel
    @Etiqueta("Lesão")
    @Enumerated(EnumType.STRING)
    private Lesoes lesao;
    @Pesquisavel
    @Etiqueta("Observaçoes")
    private String observacoes;

    //Dados do atendimento
    @Pesquisavel
    @Etiqueta("Data do Atendimento")
    @Obrigatorio
    @Temporal(TemporalType.TIMESTAMP)
    private Date atendidoEm;
    @Etiqueta("Houve Internação")
    @Tabelavel
    private Boolean houveInternacao;
    @Etiqueta("Duração Provável do Tratamento (em dias)")
    @Obrigatorio
    private Integer duracao;
    @Etiqueta("Tipo de Duração")
    @Enumerated(EnumType.STRING)
    private TipoPrazo tipoPrazo;
    @Etiqueta("Houve Afastamento Durante o Tratamento")
    private Boolean afastaDuranteTratamento;
    //TODO verificar possibilidade de criacao do cadastro de *indicação da Lesao*
    @Etiqueta("Descrição da Lesão")
    private String descricaoLesao;
    @ManyToOne
    @Obrigatorio
    private CID cid;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Médico")
    private Medico medico;
    @Temporal(TemporalType.DATE)
    private Date inicioAfastamento;
    @Temporal(TemporalType.DATE)
    private Date fimAfastamento;
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Parte do Corpo")
    private ParteCorpo parteCorpo;
    @ManyToOne
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Agente Causador do Acidente de Trabalho")
    private AgenteAcidenteTrabalho agenteAcidenteTrabalho;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Agente Causador de Doença Profissional")
    private AgenteDoencaProfissional agenteDoencaProfissional;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação Geradora do Acidente")
    private SituacaoAcidente situacaoAcidente;

    @Temporal(TemporalType.DATE)
    private Date dataObito;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Hora do atendimento")
    private Date horaAtendimento;

    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Horas trabalhadas antes da ocorrência do acidente")
    private Date horasTrabalhadasAntesAcidente;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Natureza da Lesão")
    private NaturezaLesao naturezaLesao;

    @Etiqueta("Diagnóstico provável")
    @Obrigatorio
    private String diagnosticoProvavel;

    @Etiqueta("Observação (Atestado)")
    @Obrigatorio
    private String observacaoAtestado;

    @Etiqueta("Número do recibo da última CAT referente ao mesmo acidente/doença relacionada ao trabalho")
    private String numeroReciboUltimaCAT;

    @Obrigatorio
    @Etiqueta("Órgão de classe")
    @Enumerated(EnumType.STRING)
    private OrgaoClasse orgaoClasse;

    @Obrigatorio
    @Etiqueta("Iniciativa da CAT")
    @Enumerated(EnumType.STRING)
    private IniciativaCAT iniciativaCAT;

    public CAT() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PessoaFisica getColaborador() {
        return colaborador;
    }

    public void setColaborador(PessoaFisica colaborador) {
        this.colaborador = colaborador;
    }

    public Date getOcorridoEm() {
        return ocorridoEm;
    }

    public void setOcorridoEm(Date ocorridoEm) {
        this.ocorridoEm = ocorridoEm;
    }

    public TipoAcidenteCAT getTipoAcidente() {
        return tipoAcidente;
    }

    public void setTipoAcidente(TipoAcidenteCAT tipoAcidente) {
        this.tipoAcidente = tipoAcidente;
    }

    public TipoCAT getTipoCat() {
        return tipoCat;
    }

    public void setTipoCat(TipoCAT tipoCat) {
        this.tipoCat = tipoCat;
    }

    public Boolean getHouveObito() {
        return houveObito;
    }

    public void setHouveObito(Boolean houveObito) {
        this.houveObito = houveObito;
    }

    public Boolean getHouveComunicacaoPolicial() {
        return houveComunicacaoPolicial;
    }

    public void setHouveComunicacaoPolicial(Boolean houveComunicacaoPolicial) {
        this.houveComunicacaoPolicial = houveComunicacaoPolicial;
    }

    public OrigemCAT getOrigemCAT() {
        return origemCAT;
    }

    public void setOrigemCAT(OrigemCAT origemCAT) {
        this.origemCAT = origemCAT;
    }

    public LocalAcidente getLocalAcidente() {
        return localAcidente;
    }

    public void setLocalAcidente(LocalAcidente localAcidente) {
        this.localAcidente = localAcidente;
    }

    public String getDescricaoLocalAcidente() {
        return descricaoLocalAcidente;
    }

    public void setDescricaoLocalAcidente(String descricaoLocalAcidente) {
        this.descricaoLocalAcidente = descricaoLocalAcidente;
    }

    public Lesoes getLesao() {
        return lesao;
    }

    public void setLesao(Lesoes lesao) {
        this.lesao = lesao;
    }

    public Date getAtendidoEm() {
        return atendidoEm;
    }

    public void setAtendidoEm(Date atendidoEm) {
        this.atendidoEm = atendidoEm;
    }

    public Boolean getHouveInternacao() {
        return houveInternacao;
    }

    public void setHouveInternacao(Boolean houveInternacao) {
        this.houveInternacao = houveInternacao;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public TipoPrazo getTipoPrazo() {
        return tipoPrazo;
    }

    public void setTipoPrazo(TipoPrazo tipoPrazo) {
        this.tipoPrazo = tipoPrazo;
    }

    public Boolean getAfastaDuranteTratamento() {
        return afastaDuranteTratamento;
    }

    public void setAfastaDuranteTratamento(Boolean afastaDuranteTratamento) {
        this.afastaDuranteTratamento = afastaDuranteTratamento;
    }

    public String getDescricaoLesao() {
        return descricaoLesao;
    }

    public void setDescricaoLesao(String descricaoLesao) {
        this.descricaoLesao = descricaoLesao;
    }

    public CID getCid() {
        return cid;
    }

    public void setCid(CID cid) {
        this.cid = cid;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public Date getInicioAfastamento() {
        return inicioAfastamento;
    }

    public void setInicioAfastamento(Date inicioAfastamento) {
        this.inicioAfastamento = inicioAfastamento;
    }

    public Date getFimAfastamento() {
        return fimAfastamento;
    }

    public void setFimAfastamento(Date fimAfastamento) {
        this.fimAfastamento = fimAfastamento;
    }

    public ParteCorpo getParteCorpo() {
        return parteCorpo;
    }

    public void setParteCorpo(ParteCorpo parteCorpo) {
        this.parteCorpo = parteCorpo;
    }

    public AgenteAcidenteTrabalho getAgenteAcidenteTrabalho() {
        return agenteAcidenteTrabalho;
    }

    public void setAgenteAcidenteTrabalho(AgenteAcidenteTrabalho agenteAcidenteTrabalho) {
        this.agenteAcidenteTrabalho = agenteAcidenteTrabalho;
    }

    public AgenteDoencaProfissional getAgenteDoencaProfissional() {
        return agenteDoencaProfissional;
    }

    public void setAgenteDoencaProfissional(AgenteDoencaProfissional agenteDoencaProfissional) {
        this.agenteDoencaProfissional = agenteDoencaProfissional;
    }

    public SituacaoAcidente getSituacaoAcidente() {
        return situacaoAcidente;
    }

    public void setSituacaoAcidente(SituacaoAcidente situacaoAcidente) {
        this.situacaoAcidente = situacaoAcidente;
    }

    public Date getDataObito() {
        return dataObito;
    }

    public void setDataObito(Date dataObito) {
        this.dataObito = dataObito;
    }

    public TipoLogradouroEnderecoCorreio getTipoLogradouro() {
        return tipoLogradouro;
    }

    public void setTipoLogradouro(TipoLogradouroEnderecoCorreio tipoLogradouro) {
        this.tipoLogradouro = tipoLogradouro;
    }

    public String getDescricaoLogradouro() {
        return descricaoLogradouro;
    }

    public void setDescricaoLogradouro(String descricaoLogradouro) {
        this.descricaoLogradouro = descricaoLogradouro;
    }

    public String getNumeroLogradouro() {
        return numeroLogradouro;
    }

    public void setNumeroLogradouro(String numeroLogradouro) {
        this.numeroLogradouro = numeroLogradouro;
    }

    public String getComplementoLogradouro() {
        return complementoLogradouro;
    }

    public void setComplementoLogradouro(String complementoLogradouro) {
        this.complementoLogradouro = complementoLogradouro;
    }

    public String getBairroDistritoLogradouro() {
        return bairroDistritoLogradouro;
    }

    public void setBairroDistritoLogradouro(String bairroDistritoLogradouro) {
        this.bairroDistritoLogradouro = bairroDistritoLogradouro;
    }

    public String getCepLogradouro() {
        return cepLogradouro;
    }

    public void setCepLogradouro(String cepLogradouro) {
        this.cepLogradouro = cepLogradouro;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public UF getUf() {
        return uf;
    }

    public void setUf(UF uf) {
        this.uf = uf;
    }

    public String getCodigoEnderecamentoPostal() {
        return codigoEnderecamentoPostal;
    }

    public void setCodigoEnderecamentoPostal(String codigoEnderecamentoPostal) {
        this.codigoEnderecamentoPostal = codigoEnderecamentoPostal;
    }

    public String getCodigoPais() {
        return codigoPais;
    }

    public void setCodigoPais(String codigoPais) {
        this.codigoPais = codigoPais;
    }

    public TipoInscricaoESocial getTipoInscricaoAcidenteDoenca() {
        return tipoInscricaoAcidenteDoenca;
    }

    public void setTipoInscricaoAcidenteDoenca(TipoInscricaoESocial tipoInscricaoAcidenteDoenca) {
        this.tipoInscricaoAcidenteDoenca = tipoInscricaoAcidenteDoenca;
    }

    public String getNumeroInscEstabelecimento() {
        return numeroInscEstabelecimento;
    }

    public void setNumeroInscEstabelecimento(String numeroInscEstabelecimento) {
        this.numeroInscEstabelecimento = numeroInscEstabelecimento;
    }

    public LateralidadeParteAtingida getLateralidadeParteAtingida() {
        return lateralidadeParteAtingida;
    }

    public void setLateralidadeParteAtingida(LateralidadeParteAtingida lateralidadeParteAtingida) {
        this.lateralidadeParteAtingida = lateralidadeParteAtingida;
    }

    public Date getHoraAtendimento() {
        return horaAtendimento;
    }

    public void setHoraAtendimento(Date horaAtendimento) {
        this.horaAtendimento = horaAtendimento;
    }

    public NaturezaLesao getNaturezaLesao() {
        return naturezaLesao;
    }

    public void setNaturezaLesao(NaturezaLesao naturezaLesao) {
        this.naturezaLesao = naturezaLesao;
    }

    public String getDiagnosticoProvavel() {
        return diagnosticoProvavel;
    }

    public void setDiagnosticoProvavel(String diagnosticoProvavel) {
        this.diagnosticoProvavel = diagnosticoProvavel;
    }

    public String getObservacaoAtestado() {
        return observacaoAtestado;
    }

    public void setObservacaoAtestado(String observacaoAtestado) {
        this.observacaoAtestado = observacaoAtestado;
    }

    public OrgaoClasse getOrgaoClasse() {
        return orgaoClasse;
    }

    public void setOrgaoClasse(OrgaoClasse orgaoClasse) {
        this.orgaoClasse = orgaoClasse;
    }

    public UF getUfOrgaoClasse() {
        return ufOrgaoClasse;
    }

    public void setUfOrgaoClasse(UF ufOrgaoClasse) {
        this.ufOrgaoClasse = ufOrgaoClasse;
    }

    public String getNumeroReciboUltimaCAT() {
        return numeroReciboUltimaCAT;
    }

    public void setNumeroReciboUltimaCAT(String numeroReciboUltimaCAT) {
        this.numeroReciboUltimaCAT = numeroReciboUltimaCAT;
    }

    public Date getHorasTrabalhadasAntesAcidente() {
        return horasTrabalhadasAntesAcidente;
    }

    public void setHorasTrabalhadasAntesAcidente(Date horasTrabalhadasAntesAcidente) {
        this.horasTrabalhadasAntesAcidente = horasTrabalhadasAntesAcidente;
    }

    public IniciativaCAT getIniciativaCAT() {
        return iniciativaCAT;
    }

    public void setIniciativaCAT(IniciativaCAT iniciativaCAT) {
        this.iniciativaCAT = iniciativaCAT;
    }

    @Override
    public String getDescricaoCompleta() {
        return this.colaborador.toString();
    }

    @Override
    public String getIdentificador() {
        return this.getId().toString();
    }
}
