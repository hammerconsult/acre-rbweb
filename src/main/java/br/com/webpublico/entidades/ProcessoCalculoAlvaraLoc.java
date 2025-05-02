/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Heinz/Fábio
 */

@Entity

@Audited
@GrupoDiagrama(nome = "Alvara")
public class ProcessoCalculoAlvaraLoc extends ProcessoCalculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany(mappedBy = "processoCalculoAlvaraLoc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoAlvaraLocalizacao> calculosAlvaraLocalizacao;

    public ProcessoCalculoAlvaraLoc() {
        calculosAlvaraLocalizacao = new ArrayList<CalculoAlvaraLocalizacao>();
    }

    public List<CalculoAlvaraLocalizacao> getCalculosAlvaraLocalizacao() {
        return calculosAlvaraLocalizacao;
    }

    public void setCalculosAlvaraLocalizacao(List<CalculoAlvaraLocalizacao> calculosAlvaraLocalizacao) {
        this.calculosAlvaraLocalizacao = calculosAlvaraLocalizacao;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
       return calculosAlvaraLocalizacao;
    }
}
