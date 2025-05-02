package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Arquivo;
import br.com.webpublico.enums.EsferaDoPoder;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by renato on 30/08/2016.
 */
@GrupoDiagrama(nome = "Contabil")
@Audited
@Entity
public class PrefeituraPortal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Nome")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String nome;
    @Obrigatorio
    @Etiqueta("Endereço")
    @Tabelavel
    @Pesquisavel
    private String endereco;
    @Obrigatorio
    @Etiqueta("Telefone de contato")
    @Tabelavel
    @Pesquisavel
    private String contato;
    @Obrigatorio
    @Etiqueta("Email da Tecnologia")
    @Tabelavel
    @Pesquisavel
    private String emailTecnologia;
    @Obrigatorio
    @Etiqueta("Telefone de contato da Tecnologia")
    @Tabelavel
    @Pesquisavel
    private String contatoTecnologia;
    @Obrigatorio
    @Etiqueta("Horário de Atendimento")
    @Tabelavel
    @Pesquisavel
    private String horarioAtendimento;
    @Obrigatorio
    @Etiqueta("Site")
    @Tabelavel
    @Pesquisavel
    private String site;
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Última atualização")
    @Tabelavel
    @Pesquisavel
    private Date ultimaAtualizacao;
    @Obrigatorio
    @Etiqueta("Página Inicial")
    @Tabelavel
    @Pesquisavel
    private String paginainicial;
    @Obrigatorio
    @Etiqueta("Botões Página Inicial")
    @Tabelavel
    @Pesquisavel
    private String botaoPaginainicial;
    @Etiqueta("Logo topo")
    @Tabelavel
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    @Obrigatorio
    private Arquivo logoTopo;
    @Etiqueta("IP")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String ip;

    @Etiqueta("urlExternoPortalTransparencia")
    @Tabelavel
    @Pesquisavel
    private String urlExternoPortalTransparencia;

    @Etiqueta("Identificador")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String identificador;
    private Boolean principal;

    @Etiqueta("URL acesso a informação")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String urlAcessoInformacao;
    @Etiqueta("URL E-SIC")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String urlESic;
    @Etiqueta("URL Ouvidoria")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String urlOuvidoria;
    @Etiqueta("Atendimento Presencial")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String atendimentoPresencial;

    @Etiqueta("Descrição E-Ouv")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String descricaoEouv;

    @Etiqueta("Nome Reduzido")
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    private String nomereduzido;

    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta(value = "Esfera do Poder")
    private EsferaDoPoder esferaDoPoder;
    private Boolean habilitarBannerCalamidade;
    @Etiqueta("Logo topo")
    @Tabelavel
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    @Obrigatorio
    private Arquivo organograma;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "prefeitura")
    private List<UnidadePrefeituraPortal> unidades;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "prefeitura")
    private List<UnidadeAdmPrefeituraPortal> unidadesAdm;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "prefeitura")
    private List<ModuloPrefeituraPortal> modulos;

    public PrefeituraPortal() {
        unidades = Lists.newArrayList();
        unidadesAdm = Lists.newArrayList();
        modulos = Lists.newArrayList();
        habilitarBannerCalamidade = false;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getHorarioAtendimento() {
        return horarioAtendimento;
    }

    public void setHorarioAtendimento(String horarioAtendimento) {
        this.horarioAtendimento = horarioAtendimento;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Date getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(Date ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public String getPaginainicial() {
        return paginainicial;
    }

    public void setPaginainicial(String paginainicial) {
        this.paginainicial = paginainicial;
    }

    public Arquivo getLogoTopo() {
        return logoTopo;
    }

    public void setLogoTopo(Arquivo logoTopo) {
        this.logoTopo = logoTopo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public List<UnidadePrefeituraPortal> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<UnidadePrefeituraPortal> unidades) {
        this.unidades = unidades;
    }

    public List<ModuloPrefeituraPortal> getModulos() {
        return modulos;
    }

    public void setModulos(List<ModuloPrefeituraPortal> modulos) {
        this.modulos = modulos;
    }

    public String getUrlAcessoInformacao() {
        return urlAcessoInformacao;
    }

    public void setUrlAcessoInformacao(String urlAcessoInformacao) {
        this.urlAcessoInformacao = urlAcessoInformacao;
    }

    public String getUrlESic() {
        return urlESic;
    }

    public void setUrlESic(String urlESic) {
        this.urlESic = urlESic;
    }

    public String getUrlOuvidoria() {
        return urlOuvidoria;
    }

    public void setUrlOuvidoria(String urlOuvidoria) {
        this.urlOuvidoria = urlOuvidoria;
    }

    public String getAtendimentoPresencial() {
        return atendimentoPresencial;
    }

    public void setAtendimentoPresencial(String atendimentoPresencial) {
        this.atendimentoPresencial = atendimentoPresencial;
    }

    public String getNomereduzido() {
        return nomereduzido;
    }

    public void setNomereduzido(String nomereduzido) {
        this.nomereduzido = nomereduzido;
    }

    public EsferaDoPoder getEsferaDoPoder() {
        return esferaDoPoder;
    }

    public void setEsferaDoPoder(EsferaDoPoder esferaDoPoder) {
        this.esferaDoPoder = esferaDoPoder;
    }

    public String getDescricaoEouv() {
        return descricaoEouv;
    }

    public void setDescricaoEouv(String descricaoEouv) {
        this.descricaoEouv = descricaoEouv;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public Boolean getHabilitarBannerCalamidade() {
        return habilitarBannerCalamidade == null ? Boolean.FALSE : habilitarBannerCalamidade;
    }

    public void setHabilitarBannerCalamidade(Boolean habilitarBannerCalamidade) {
        this.habilitarBannerCalamidade = habilitarBannerCalamidade;
    }

    public String getEmailTecnologia() {
        return emailTecnologia;
    }

    public void setEmailTecnologia(String emailTecnologia) {
        this.emailTecnologia = emailTecnologia;
    }

    public String getContatoTecnologia() {
        return contatoTecnologia;
    }

    public void setContatoTecnologia(String contatoTecnologia) {
        this.contatoTecnologia = contatoTecnologia;
    }

    public String getBotaoPaginainicial() {
        return botaoPaginainicial;
    }

    public void setBotaoPaginainicial(String botaoPaginainicial) {
        this.botaoPaginainicial = botaoPaginainicial;
    }

    public String getUrlExternoPortalTransparencia() {
        return urlExternoPortalTransparencia;
    }

    public void setUrlExternoPortalTransparencia(String urlExternoPortalTransparencia) {
        this.urlExternoPortalTransparencia = urlExternoPortalTransparencia;
    }

    public List<UnidadeAdmPrefeituraPortal> getUnidadesAdm() {
        return unidadesAdm;
    }

    public void setUnidadesAdm(List<UnidadeAdmPrefeituraPortal> unidadesAdm) {
        this.unidadesAdm = unidadesAdm;
    }

    public Arquivo getOrganograma() {
        return organograma;
    }

    public void setOrganograma(Arquivo organograma) {
        this.organograma = organograma;
    }

    @Override
    public String toString() {
        return nome;
    }
}
