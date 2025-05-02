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
import java.util.ArrayList;
import java.util.List;

/**
 * @author Leonardo
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Fiscalizacao")
public class ProcessoCalculoMultaFiscal extends ProcessoCalculo {

    private static final long serialVersionUID = 1L;
    @OneToMany(mappedBy = "processoCalculoMultaFiscal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoMultaFiscalizacao> calculoMultaFiscalizacao;

    public ProcessoCalculoMultaFiscal() {
        calculoMultaFiscalizacao = new ArrayList<CalculoMultaFiscalizacao>();
    }

    public List<CalculoMultaFiscalizacao> getCalculoMultaFiscalizacao() {
        return calculoMultaFiscalizacao;
    }

    public void setCalculoMultaFiscalizacao(List<CalculoMultaFiscalizacao> calculoMultaFiscalizacao) {
        this.calculoMultaFiscalizacao = calculoMultaFiscalizacao;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return calculoMultaFiscalizacao;
    }
}
