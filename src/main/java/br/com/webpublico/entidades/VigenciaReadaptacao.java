package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Carnage
 * Date: 06/08/13
 * Time: 09:56
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Cacheable
@Audited
@Etiqueta("Vigência da Readaptação")
public class VigenciaReadaptacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da avaliação")
    private Date inicioVigencia;
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final de Vigência")
    private Date finalVigencia;
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Reavaliação")
    private Date dataReavaliacao;
    @Pesquisavel
    @Etiqueta("Número do Laudo")
    private String numeroLaudo;
    @Pesquisavel
    @Etiqueta("Descrição do Laudo")
    private String descricaoLaudo;
    @ManyToOne(cascade = CascadeType.ALL)
    private Arquivo arquivoLaudo;
    @ManyToOne
    private Readaptacao readaptacao;
    @Transient
    private Long criadoEm;

    public VigenciaReadaptacao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public String getNumeroLaudo() {
        return numeroLaudo;
    }

    public void setNumeroLaudo(String numeroLaudo) {
        this.numeroLaudo = numeroLaudo;
    }

    public String getDescricaoLaudo() {
        return descricaoLaudo;
    }

    public void setDescricaoLaudo(String descricaoLaudo) {
        this.descricaoLaudo = descricaoLaudo;
    }

    public Arquivo getArquivoLaudo() {
        return arquivoLaudo;
    }

    public void setArquivoLaudo(Arquivo arquivoLaudo) {
        this.arquivoLaudo = arquivoLaudo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Readaptacao getReadaptacao() {
        return readaptacao;
    }

    public void setReadaptacao(Readaptacao readaptacao) {
        this.readaptacao = readaptacao;
    }

    public Date getDataReavaliacao() {
        return dataReavaliacao;
    }

    public void setDataReavaliacao(Date dataReavaliacao) {
        this.dataReavaliacao = dataReavaliacao;
    }

    @Override
    public String toString() {
        return inicioVigencia + " a " + finalVigencia;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }
}
