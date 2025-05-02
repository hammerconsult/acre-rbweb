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
 * @author fabio
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Fiscalizacao")
public class ProcessoCalculoFiscal extends ProcessoCalculo implements Serializable {
    private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "processoCalculoFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoFiscalizacao> calculosFiscalizacao;

    public ProcessoCalculoFiscal() {
        calculosFiscalizacao = new ArrayList<CalculoFiscalizacao>();
    }

    public List<CalculoFiscalizacao> getCalculosFiscalizacao() {
        return calculosFiscalizacao;
    }

    public void setCalculosFiscalizacao(List<CalculoFiscalizacao> calculosFiscalizacao) {
        this.calculosFiscalizacao = calculosFiscalizacao;
    }

    @Override
    public List<CalculoFiscalizacao> getCalculos() {
      return calculosFiscalizacao;
    }

}
