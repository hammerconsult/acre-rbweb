/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Munif
 */
@Entity
@Audited
public class PropriedadeOriginalITBI extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Propriedade propriedade;
    @ManyToOne
    private PropriedadeRural propriedadeRural;
    @ManyToOne
    private ProcessoCalculoITBI processoCalculoITBI;

    public ProcessoCalculoITBI getProcessoCalculoITBI() {
        return processoCalculoITBI;
    }

    public void setProcessoCalculoITBI(ProcessoCalculoITBI processoCalculoITBI) {
        this.processoCalculoITBI = processoCalculoITBI;
    }

    public Propriedade getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(Propriedade propriedade) {
        this.propriedade = propriedade;
    }

    public PropriedadeRural getPropriedadeRural() {
        return propriedadeRural;
    }

    public void setPropriedadeRural(PropriedadeRural propriedadeRural) {
        this.propriedadeRural = propriedadeRural;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}
