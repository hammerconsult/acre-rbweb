/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * @author venon
 */
@Entity

@Audited
public class OCCConta extends OrigemContaContabil implements Serializable {

    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Conta")
    private Conta contaOrigem;

    public OCCConta() {
    }

    public OCCConta(Conta contaOrigem, TagOCC tagOCC, ContaContabil contaContabil, Date inicioVigencia, Date fimVigencia, Boolean reprocessar, OrigemContaContabil origem) {
        super(tagOCC, contaContabil, inicioVigencia, fimVigencia, reprocessar, origem);
        this.contaOrigem = contaOrigem;
    }

    public Conta getContaOrigem() {
        return contaOrigem;
    }

    public void setContaOrigem(Conta contaOrigem) {
        this.contaOrigem = contaOrigem;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.contaOrigem);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OCCConta other = (OCCConta) obj;
        if (!Objects.equals(this.contaOrigem, other.contaOrigem)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " Conta: " + contaOrigem;
    }
}
