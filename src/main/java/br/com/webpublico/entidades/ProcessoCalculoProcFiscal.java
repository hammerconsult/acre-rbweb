/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.envers.Audited;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonardo
 */
@Entity
@Audited
@GrupoDiagrama(nome = "fiscalizacaogeral")
@Etiqueta("Processo de Calculo do Processo de Fiscalizacao Geral")
public class ProcessoCalculoProcFiscal extends ProcessoCalculo implements Serializable {

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoProcFiscalizacao> calculos;

    public ProcessoCalculoProcFiscal() {
        calculos = new ArrayList<CalculoProcFiscalizacao>();
    }

    public List<CalculoProcFiscalizacao> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoProcFiscalizacao> calculos) {
        this.calculos = calculos;
    }
}
