/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemReservaFonte;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author venon
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Orcamentario")

public class ReservaFonteDespesaOrc extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Tabelavel
    @Obrigatorio
    @Etiqueta("Data da Reserva")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataReserva;

    @Obrigatorio
    @Tabelavel
    @Etiqueta("Valor")
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Etiqueta("Origem da Reserva")
    private OrigemReservaFonte origemReservaFonte;

    @ManyToOne
    @Etiqueta("Fonte de Recurso")
    private FonteDespesaORC fonteDespesaORC;

    public ReservaFonteDespesaOrc() {
        valor = BigDecimal.ZERO;
    }

    public ReservaFonteDespesaOrc(Date dataReserva, BigDecimal valor, OrigemReservaFonte origemReservaFonte, FonteDespesaORC fonteDespesaORC) {
        this.dataReserva = dataReserva;
        this.valor = valor;
        this.origemReservaFonte = origemReservaFonte;
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(Date dataReserva) {
        this.dataReserva = dataReserva;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public OrigemReservaFonte getOrigemReservaFonte() {
        return origemReservaFonte;
    }

    public void setOrigemReservaFonte(OrigemReservaFonte origemReservaFonte) {
        this.origemReservaFonte = origemReservaFonte;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Data: " + dataReserva + " - " + valor;
    }

}
