package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 19/07/13
 * Time: 10:11
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Etiqueta("Laudo de Provimento de Reversão")
@GrupoDiagrama(nome = "Recursos Humanos")
@Audited

public class LaudoProvimentoReversao implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Laudo")
    private Date dataLaudo;
    @Etiqueta("Descrição do Laudo")
    private String descricao;
    @Etiqueta("Motivo")
    private String motivo;
    @OneToOne
    private Arquivo arquivo;
    @Transient
    private Long criadoEm;

    public LaudoProvimentoReversao() {
        this.criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLaudo() {
        return dataLaudo;
    }

    public void setDataLaudo(Date dataLaudo) {
        this.dataLaudo = dataLaudo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        if (descricao != null && !descricao.trim().isEmpty()) {
            return descricao;
        } else if (motivo != null && !motivo.trim().isEmpty()) {
            return motivo;
        }
        return "";
    }
}
