/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author claudio
 */
@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity

@Audited
@Etiqueta("Penhora")
public class Penhora implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private static final long serialVersionUID = 1L;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Cadastro Imobiliário")
    private CadastroImobiliario cadastroImobiliario;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Sequencia")
    private Integer sequencia;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Número do Processo")
    private String numeroProcesso;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Autor")
    private Pessoa autor;
    private String motivo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Bloqueia B.C.I.")
    private Boolean bloqueio; //com bloqueio ou sem bloqueio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Baixa")
    private Date dataBaixa;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data da Penhora")
    private Date dataPenhora;
    @Transient
    private Long criadoEm;
    private String numeroProcessoBaixa;
    private String numeroProtocolo;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataProtocolo;
    @OneToMany(mappedBy = "penhora", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArquivoPenhora> arquivos;

    public Penhora() {
        this.criadoEm = System.nanoTime();
        arquivos = new ArrayList<ArquivoPenhora>();
    }

    public Pessoa getAutor() {
        return autor;
    }

    public void setAutor(Pessoa autor) {
        this.autor = autor;
    }

    public Boolean getBloqueio() {
        return bloqueio;
    }

    public void setBloqueio(Boolean bloqueio) {
        this.bloqueio = bloqueio;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Date getDataBaixa() {
        return dataBaixa;
    }

    public void setDataBaixa(Date dataBaixa) {
        this.dataBaixa = dataBaixa;
    }

    public Date getDataPenhora() {
        return dataPenhora;
    }

    public void setDataPenhora(Date dataPenhora) {
        this.dataPenhora = dataPenhora;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(String numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public String getNumeroProcessoBaixa() {
        return numeroProcessoBaixa;
    }

    public void setNumeroProcessoBaixa(String numeroProcessoBaixa) {
        this.numeroProcessoBaixa = numeroProcessoBaixa;
    }

    public Integer getSequencia() {
        return sequencia;
    }

    public void setSequencia(Integer sequencia) {
        this.sequencia = sequencia;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataProtocolo() {
        return dataProtocolo;
    }

    public void setDataProtocolo(Date dataProtocolo) {
        this.dataProtocolo = dataProtocolo;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public List<ArquivoPenhora> getArquivos() {
        return arquivos;
    }

    public void setArquivos(List<ArquivoPenhora> arquivos) {
        this.arquivos = arquivos;
    }

    public String getDescricaoDaPenhora() {
        String descricao = "";
        String descricaoBloqueio = "";
        if (bloqueio) {
            descricaoBloqueio += "COM BLOQUEIO";
        } else {
            descricaoBloqueio += "SEM BLOQUEIO";
        }
        if (dataBaixa == null) {
            descricao += "PENHORA ";
        } else {
            descricao += "BAIXA DE PENHORA ";
        }
        return descricao + descricaoBloqueio;
    }

    @Override
    public boolean equals(Object obj) {

        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {

        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
