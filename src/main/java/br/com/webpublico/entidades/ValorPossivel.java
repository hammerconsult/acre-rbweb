/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "AtributosDinamicos")
@Entity

@Audited
public class ValorPossivel extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Valor")
    private String valor;
    @Obrigatorio
    @Etiqueta("Código")
    @Tabelavel
    private Integer codigo;
    @Tabelavel
    @ManyToOne
    private Atributo atributo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    private boolean valorPadrao;
    private BigDecimal fator;

    public ValorPossivel() {
        super();
    }

    public ValorPossivel(String valor, Atributo atributo) {
        this();
        this.valor = valor;
        this.atributo = atributo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public boolean isValorPadrao() {
        return valorPadrao;
    }

    public void setValorPadrao(boolean valorPadrao) {
        this.valorPadrao = valorPadrao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {

        return codigo != null && valor != null ? codigo + " - " + valor : codigo != null ? codigo + "" : valor;
    }

    public BigDecimal getFator() {
        return fator;
    }

    public void setFator(BigDecimal fator) {
        this.fator = fator;
    }

    @Override
    public String toString() {
        return getDescricao();
    }

}
