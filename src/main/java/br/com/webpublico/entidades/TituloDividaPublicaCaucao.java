/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Contratos")
@Entity
@Audited
public class TituloDividaPublicaCaucao extends CaucaoContrato {

    @Tabelavel(ordemApresentacao = 4)
    @Etiqueta("Número da Escritura")
    private Integer numeroEscritura;
    @Tabelavel(ordemApresentacao = 5)
    @Etiqueta("Número do Registro SCL")
    private Integer numeroRegistroSCL;
    @Etiqueta("Número da Custódia")
    @Tabelavel(ordemApresentacao = 6)
    private Integer numeroCustodia;
    @Etiqueta("Validade")
    @Tabelavel(ordemApresentacao = 7)
    @Temporal(TemporalType.DATE)
    private Date validade;

    public Integer getNumeroEscritura() {
        return numeroEscritura;
    }

    public Date getValidade() {
        return validade;
    }

    public void setValidade(Date validade) {
        this.validade = validade;
    }

    public void setNumeroEscritura(Integer numeroEscritura) {
        this.numeroEscritura = numeroEscritura;
    }

    public Integer getNumeroRegistroSCL() {
        return numeroRegistroSCL;
    }

    public void setNumeroRegistroSCL(Integer numeroRegistroSCL) {
        this.numeroRegistroSCL = numeroRegistroSCL;
    }

    public Integer getNumeroCustodia() {
        return numeroCustodia;
    }

    public void setNumeroCustodia(Integer numeroCustodia) {
        this.numeroCustodia = numeroCustodia;
    }
}
