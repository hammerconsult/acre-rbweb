/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoArquivo;
import br.com.webpublico.enums.TipoArquivoBancarioTributario;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author gustavo
 */
@Entity
@Audited
public class ArquivoLoteBaixa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Situação")
    private SituacaoArquivo situacaoArquivo;
    @ManyToOne
    private Arquivo arquivo;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Válido")
    private Boolean valido;
    private String hashMd5;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "arquivoLoteBaixa")
    private List<LoteBaixaDoArquivo> lotes;
    @Enumerated(EnumType.STRING)
    private TipoArquivoBancarioTributario tipoArquivoBancarioTributario;
    @Transient
    private BancoContaConfTributario conta;
    @Transient
    private SubConta transientSubConta;
    @Transient
    private Banco transientBanco;
    @Transient
    private Long criadoEm;

    public ArquivoLoteBaixa() {
        this.valido = false;
        this.lotes = Lists.newArrayList();
        this.criadoEm = System.nanoTime();
    }


    public TipoArquivoBancarioTributario getTipoArquivoBancarioTributario() {
        return tipoArquivoBancarioTributario;
    }

    public void setTipoArquivoBancarioTributario(TipoArquivoBancarioTributario tipoArquivoBancarioTributario) {
        this.tipoArquivoBancarioTributario = tipoArquivoBancarioTributario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setHashMd5(String hashMd5) {
        this.hashMd5 = hashMd5;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public List<LoteBaixaDoArquivo> getLotes() {
        return lotes;
    }

    public void setLotes(List<LoteBaixaDoArquivo> lotes) {
        this.lotes = lotes;
    }

    public SituacaoArquivo getSituacaoArquivo() {
        return situacaoArquivo;
    }

    public void setSituacaoArquivo(SituacaoArquivo situacaoArquivo) {
        this.situacaoArquivo = situacaoArquivo;
    }

    public Boolean getValido() {
        return valido;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public String getHashMd5() {
        return hashMd5;
    }

    public BancoContaConfTributario getConta() {
        return conta;
    }

    public void setConta(BancoContaConfTributario conta) {
        this.conta = conta;
    }

    public SubConta getTransientSubConta() {
        return transientSubConta;
    }

    public void setTransientSubConta(SubConta transientSubConta) {
        this.transientSubConta = transientSubConta;
    }

    public Banco getTransientBanco() {
        return transientBanco;
    }

    public void setTransientBanco(Banco transientBanco) {
        this.transientBanco = transientBanco;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ArquivoLoteBaixa[ id=" + id + " ]";
    }

    public boolean isDaf607() {
        return TipoArquivoBancarioTributario.DAF607.equals(tipoArquivoBancarioTributario);
    }
}
