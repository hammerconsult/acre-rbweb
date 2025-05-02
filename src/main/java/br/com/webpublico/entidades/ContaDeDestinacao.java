/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Arthur
 */
@Entity
@Audited
@GrupoDiagrama(nome = "'Contabil")
@Etiqueta(value = "Conta de Destinação de Recurso")
public class ContaDeDestinacao extends Conta {

    @ManyToOne()
    @Pesquisavel
    @Obrigatorio
    @Etiqueta(value = "Fonte de Recurso")
    private FonteDeRecursos fonteDeRecursos;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataCriacao;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.ESQUERDA)
    @Pesquisavel
    @Etiqueta(value = "Código CO")
    private String codigoCO;
    @Transient
    private String codigoCOEmenda;

    public ContaDeDestinacao() {
        super();
        dataCriacao = new Date();
        super.setdType("ContaDeDestinacao");
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getCodigoCO() {
        return codigoCO;
    }

    public void setCodigoCO(String codigoCO) {
        this.codigoCO = codigoCO;
    }

    public String getCodigoCOEmenda() {
        return codigoCOEmenda;
    }

    public void setCodigoCOEmenda(String codigoCOEmenda) {
        this.codigoCOEmenda = codigoCOEmenda;
    }

    public String getCodigoCOSiconf() {
        return codigoCOEmenda != null ? codigoCOEmenda :codigoCO;
    }
}
