/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author java
 */
@GrupoDiagrama(nome = "Valor Indicador Econômico")
@Entity

@Audited
@Etiqueta("Valor Indicador Econômico")
public class ValorIndicadorEconomico extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Valor")
    private Double valor;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Início de Vigência")
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Fim de Vigência")
    private Date fimVigencia;
    @ManyToOne
    private IndicadorEconomico indicadorEconomico;


    public ValorIndicadorEconomico() {
        this.criadoEm = System.nanoTime();
    }

    public ValorIndicadorEconomico(Double valor, Date inicioVigencia, Date fimVigencia, IndicadorEconomico indicadorEconomico) {
        this.valor = valor;
        this.inicioVigencia = inicioVigencia;
        this.fimVigencia = fimVigencia;
        this.indicadorEconomico = indicadorEconomico;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFimVigencia() {
        return fimVigencia;
    }

    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public IndicadorEconomico getIndicadorEconomico() {
        return indicadorEconomico;
    }

    public void setIndicadorEconomico(IndicadorEconomico indicadorEconomico) {
        this.indicadorEconomico = indicadorEconomico;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public String toString() {
        return valor.toString();
    }
}
