/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.hibernate.envers.Audited;

/**
 * @author venon
 */
@Entity

@Audited
public class OCCBanco extends OrigemContaContabil implements Serializable {

    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Conta Financeira")
    private SubConta subConta;

    public OCCBanco() {
    }

    public OCCBanco(SubConta subConta, TagOCC tagOCC, ContaContabil contaContabil, Date inicioVigencia, Date fimVigencia, Boolean reprocessar, OrigemContaContabil origem) {
        super(tagOCC, contaContabil, inicioVigencia, fimVigencia, reprocessar, origem);
        this.subConta = subConta;
    }

    public SubConta getSubConta() {
        return subConta;
    }

    public void setSubConta(SubConta subConta) {
        this.subConta = subConta;
    }


    @Override
    public String toString() {
        return super.toString() + " Conta: " + subConta;
    }

}
