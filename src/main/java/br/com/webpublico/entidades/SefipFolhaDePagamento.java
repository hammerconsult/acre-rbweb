/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Recursos Humanos")
@Etiqueta("SEFIP")
@Entity
@Audited
public class SefipFolhaDePagamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("SEFIP")
    @ManyToOne
    private Sefip sefip;
    @Etiqueta("Folha de Pagamento")
    @ManyToOne
    private FolhaDePagamento folhaDePagamento;
    @Transient
    @Invisivel
    private Long criadoEm;

    public SefipFolhaDePagamento() {
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public Sefip getSefip() {
        return sefip;
    }

    public void setSefip(Sefip sefip) {
        this.sefip = sefip;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return ""+ folhaDePagamento;
    }
}
