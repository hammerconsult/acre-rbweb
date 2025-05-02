/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IHistoricoEsocial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author terminal3
 */
@GrupoDiagrama(nome = "Administrativo")
@Entity

@Audited
@Etiqueta("Entidade")
public class Entidade implements Serializable, IHistoricoEsocial {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Column(length = 70)
    @Etiqueta(value = "Nome")
    private String nome;
    /**
     * Arquivo é para a imagem do Brasão
     */
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    /**
     * Criar enum depois com os itens para FPAS e TCE
     */
    @Etiqueta("Código PAS")
    @Pesquisavel
    private Integer codigoFpas;
    @ManyToOne
    @Etiqueta("CNAE")
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    private CNAE cnae;
    @Tabelavel
    @Pesquisavel
    private Boolean ativa;
    /**
     * Pessoa jurídica que representa esta Unidade Organizacional. Será
     * relacionada quando a Unidade Organizacional possuir personalidade
     * jurídica própria.
     */
    @Obrigatorio
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Pessoa Jurídica")
    private PessoaJuridica pessoaJuridica;
    @OneToOne(mappedBy = "entidade")
    private UnidadeOrganizacional unidadeOrganizacional;
    //Atributo utilizado para a integração do tributário com o Contábil
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacionalOrc;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Natureza Jurídica")
    @Pesquisavel
    private NaturezaJuridicaEntidade naturezaJuridicaEntidade;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Pagamento da GPS")
    private PagamentoDaGPS pagamentoDaGPS;
    @Etiqueta("Código de Outras Entidades")
    @Tabelavel
    @Pesquisavel
    private String codigoOutrasEntidades;
    @Etiqueta("Código de Outras Entidades Suspenso")
    @Tabelavel
    @Pesquisavel
    private String codigoOutrasEntidadesSuspenso;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Símples")
    @Pesquisavel
    private Simples simples;
    @Etiqueta("Aliquota RAT")
    @Tabelavel
    @Pesquisavel
    private BigDecimal aliquotaRAT;
    @Pesquisavel
    @Etiqueta(value = "Sigla")
    private String sigla;
    @Etiqueta("Código Município")
    @Tabelavel
    @Pesquisavel
    private String codigoMunicipio;
    @Obrigatorio
    @Etiqueta("Ato Legal")
    @OneToOne
    @Tabelavel
    @Pesquisavel
    private AtoLegal atoLegal;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Natureza")
    @Enumerated(EnumType.STRING)
    private TipoEntidade tipoUnidade;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Tipo de Adminstração")
    @Enumerated(EnumType.STRING)
    private TipoAdministracao tipoAdministracao;
    @Pesquisavel
    @Etiqueta("Classificação")
    @Enumerated(EnumType.STRING)
    private ClassificacaoUO classificacaoUO;
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Esfera do Poder")
    private EsferaDoPoder esferaDoPoder;
    @Etiqueta("Poder/Órgão Siconfi")
    @Pesquisavel
    private String poderOrgaoSiconfi;

    private BigDecimal fap;
    private BigDecimal aliquotaRatAjustada;
    @OneToMany(mappedBy = "entidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PrevidenciaEntidade> previdenciaEntidades;
    private BigDecimal terceiros;
    private String codigoPatrocinadora;
    private String cnpb;

    public Entidade() {
        this.ativa = Boolean.TRUE;
        setPrevidenciaEntidades(new ArrayList<PrevidenciaEntidade>());
    }

    public Entidade(PessoaJuridica pessoaJuridica, String nome, String email, String homePage, Nacionalidade nacionalidade, List<Telefone> telefones, EscritorioContabil escritorioContabil, String razaoSocial, String nomeReduzido, String nomeFantasia, String cnpj, String inscricaoEstadual, String nomeEntidade, Arquivo arquivo, Integer codigoFpas, CNAE cnae, Boolean ativa, String codigoMunicipio) {
        this.nome = nome;
        this.arquivo = arquivo;
        this.codigoFpas = codigoFpas;
        this.cnae = cnae;
        this.ativa = ativa;
        this.codigoMunicipio = codigoMunicipio;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public String getCodigoMunicipio() {
        return codigoMunicipio;
    }

    public void setCodigoMunicipio(String codigoMunicipio) {
        this.codigoMunicipio = codigoMunicipio;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public Integer getCodigoFpas() {
        return codigoFpas;
    }

    public void setCodigoFpas(Integer codigoFpas) {
        this.codigoFpas = codigoFpas;
    }

    public PessoaJuridica getPessoaJuridica() {
        return pessoaJuridica;
    }

    public void setPessoaJuridica(PessoaJuridica pessoaJuridica) {
        this.pessoaJuridica = pessoaJuridica;
    }

    @Override
    public String toString() {
        return getNome();
    }

    public Boolean isAtiva() {
        return ativa;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public PagamentoDaGPS getPagamentoDaGPS() {
        return pagamentoDaGPS;
    }

    public void setPagamentoDaGPS(PagamentoDaGPS pagamentoDaGPS) {
        this.pagamentoDaGPS = pagamentoDaGPS;
    }

    public String getCodigoOutrasEntidades() {
        return codigoOutrasEntidades;
    }

    public void setCodigoOutrasEntidades(String codigoOutrasEntidades) {
        this.codigoOutrasEntidades = codigoOutrasEntidades;
    }

    public BigDecimal getAliquotaRAT() {
        return aliquotaRAT;
    }

    public void setAliquotaRAT(BigDecimal aliquotaRAT) {
        this.aliquotaRAT = aliquotaRAT;
    }

    public Simples getSimples() {
        return simples;
    }

    public void setSimples(Simples simples) {
        this.simples = simples;
    }

    public NaturezaJuridicaEntidade getNaturezaJuridicaEntidade() {
        return naturezaJuridicaEntidade;
    }

    public void setNaturezaJuridicaEntidade(NaturezaJuridicaEntidade naturezaJuridicaEntidade) {
        this.naturezaJuridicaEntidade = naturezaJuridicaEntidade;
    }

    public TipoEntidade getTipoUnidade() {
        return tipoUnidade;
    }

    public void setTipoUnidade(TipoEntidade tipoUnidade) {
        this.tipoUnidade = tipoUnidade;
    }

    public TipoAdministracao getTipoAdministracao() {
        return tipoAdministracao;
    }

    public void setTipoAdministracao(TipoAdministracao tipoAdministracao) {
        this.tipoAdministracao = tipoAdministracao;
    }

    public ClassificacaoUO getClassificacaoUO() {
        return classificacaoUO;
    }

    public void setClassificacaoUO(ClassificacaoUO classificacaoUO) {
        this.classificacaoUO = classificacaoUO;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrc() {
        return unidadeOrganizacionalOrc;
    }

    public void setUnidadeOrganizacionalOrc(UnidadeOrganizacional unidadeOrganizacionalOrc) {
        this.unidadeOrganizacionalOrc = unidadeOrganizacionalOrc;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entidade other = (Entidade) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    public boolean isTemAliquotaRAT() {
        return aliquotaRAT != null;
    }

    public String getPoderOrgaoSiconfi() {
        return poderOrgaoSiconfi;
    }

    public void setPoderOrgaoSiconfi(String poderOrgaoSiconfi) {
        this.poderOrgaoSiconfi = poderOrgaoSiconfi;
    }

    public BigDecimal getFap() {
        return fap;
    }

    public void setFap(BigDecimal fap) {
        this.fap = fap;
    }

    public BigDecimal getAliquotaRatAjustada() {
        return aliquotaRatAjustada;
    }

    public void setAliquotaRatAjustada(BigDecimal aliquotaRatAjustada) {
        this.aliquotaRatAjustada = aliquotaRatAjustada;
    }

    public String getCodigoOutrasEntidadesSuspenso() {
        return codigoOutrasEntidadesSuspenso;
    }

    public void setCodigoOutrasEntidadesSuspenso(String codigoOutrasEntidadesSuspenso) {
        this.codigoOutrasEntidadesSuspenso = codigoOutrasEntidadesSuspenso;
    }

    public List<PrevidenciaEntidade> getPrevidenciaEntidades() {
        return previdenciaEntidades;
    }

    public void setPrevidenciaEntidades(List<PrevidenciaEntidade> previdenciaEntidades) {
        this.previdenciaEntidades = previdenciaEntidades;
    }

    public BigDecimal getTerceiros() {
        return terceiros;
    }

    public void setTerceiros(BigDecimal terceiros) {
        this.terceiros = terceiros;
    }

    public String getCodigoPatrocinadora() {
        return codigoPatrocinadora;
    }

    public void setCodigoPatrocinadora(String codigoPatrocinadora) {
        this.codigoPatrocinadora = codigoPatrocinadora;
    }

    public String getCnpb() {
        return cnpb;
    }

    public void setCnpb(String cnpb) {
        this.cnpb = cnpb;
    }

    @Override
    public String getDescricaoCompleta() {
        return this.toString();
    }

    @Override
    public String getIdentificador() {
        return this.getId().toString();
    }

    public String getNomeEntidadePessoaJuridica(){
        return getNome() + " - " + getPessoaJuridica();
    }
}
