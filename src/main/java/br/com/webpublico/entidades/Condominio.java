package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCondominio;
import br.com.webpublico.enums.TipoOcupacaoCondominio;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author daniel
 */
@Entity
@Etiqueta("Condomínio")
@Audited
@GrupoDiagrama(nome = "CadastroImobiliario")
public class Condominio implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    private String descricao;
    @Temporal(TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano da construção")
    private Date anoConstrucao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Habite-se")
    private String numeroHabiteSe;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Projeto")
    private String numeroProjeto;
    private String inscricaoImobiliaria;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo do Condomínio")
    @Enumerated(EnumType.STRING)
    private TipoCondominio tipoCondominio;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ocupação")
    private TipoOcupacaoCondominio tipoOcupacaoCondominio;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "condominio")
    private List<LoteCondominio> lotes;
    private Long codigo;
    private Long codigoManual;
    @Pesquisavel
    @Etiqueta("Número")
    private String numero;
    @Pesquisavel
    @Etiqueta("Complemento")
    private String complemento;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Logradouro e Bairro")
    private LogradouroBairro logradouroBairro;
    @Transient
    private Setor setor;
    @Transient
    private Quadra quadra;
    @Transient
    private LoteCondominio loteCondominio;
    @Transient
    private List<Testada> listaTestadas;
    @Transient
    private Logradouro logradouro;
    @Transient
    private Bairro bairro;
    @Transient
    private String cep;
    @Transient
    private String complementoEndereco;
    @Transient
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private Long codigoCondominio;

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }


    public Condominio() {
        lotes = new ArrayList<LoteCondominio>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getAnoConstrucao() {
        return anoConstrucao;
    }

    public void setAnoConstrucao(Date anoConstrucao) {
        this.anoConstrucao = anoConstrucao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getInscricaoImobiliaria() {
        return inscricaoImobiliaria;
    }

    public void setInscricaoImobiliaria(String inscricaoImobiliaria) {
        this.inscricaoImobiliaria = inscricaoImobiliaria;
    }

    public String getNumeroHabiteSe() {
        return numeroHabiteSe;
    }

    public void setNumeroHabiteSe(String numeroHabiteSe) {
        this.numeroHabiteSe = numeroHabiteSe;
    }

    public String getNumeroProjeto() {
        return numeroProjeto;
    }

    public void setNumeroProjeto(String numeroProjeto) {
        this.numeroProjeto = numeroProjeto;
    }

    public TipoCondominio getTipoCondominio() {
        return tipoCondominio;
    }

    public void setTipoCondominio(TipoCondominio tipoCondominio) {
        this.tipoCondominio = tipoCondominio;
    }

    public TipoOcupacaoCondominio getTipoOcupacaoCondominio() {
        return tipoOcupacaoCondominio;
    }

    public void setTipoOcupacaoCondominio(TipoOcupacaoCondominio tipoOcupacaoCondominio) {
        this.tipoOcupacaoCondominio = tipoOcupacaoCondominio;
    }

    public List<LoteCondominio> getLotes() {
        return lotes;
    }

    public void setLotes(List<LoteCondominio> lotes) {
        this.lotes = lotes;
    }

    public Long getCodigo() {
        return this.codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getCodigoManual() {
        return codigoManual;
    }

    public void setCodigoManual(Long codigoManual) {
        this.codigoManual = codigoManual;
    }

    public LogradouroBairro getLogradouroBairro() {
        return logradouroBairro;
    }

    public void setLogradouroBairro(LogradouroBairro logradouroBairro) {
        this.logradouroBairro = logradouroBairro;
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

    public LoteCondominio getLoteCondominio() {
        return loteCondominio;
    }

    public void setLoteCondominio(LoteCondominio loteCondominio) {
        this.loteCondominio = loteCondominio;
    }

    public List<Testada> getListaTestadas() {
        return listaTestadas;
    }

    public void setListaTestadas(List<Testada> listaTestadas) {
        this.listaTestadas = listaTestadas;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }


    public String getComplementoEndereco() {
        return complementoEndereco;
    }

    public void setComplementoEndereco(String complementoEndereco) {
        this.complementoEndereco = complementoEndereco;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Condominio)) {
            return false;
        }
        Condominio other = (Condominio) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.Condominio[ id=" + id + " ]";
    }

    public Long getCodigoCondominio() {
        return codigoCondominio != null ? codigoCondominio : (codigo != null ? codigo : codigoManual);
    }

    public void setCodigoCondominio(Long codigoCondominio) {
        this.codigoCondominio = codigoCondominio;
    }
}
