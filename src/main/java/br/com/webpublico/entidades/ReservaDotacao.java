/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OrigemReservaFonte;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author venon
 */
@Entity
@Audited
@GrupoDiagrama(nome = "ReservaDeDotacao")
@Etiqueta("Reserva de Dotação")

public class ReservaDotacao extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data da Reserva")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    private Date dataReserva;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    @Etiqueta(value = "Valor (R$)")
    private BigDecimal valor;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private OrigemReservaFonte origemReservaFonte;
    @Tabelavel
    @Etiqueta("Ato Legal")
    @ManyToOne
    @Pesquisavel
    private AtoLegal atoLegal;
    @Tabelavel
    @Etiqueta("Fonte de Despesa Orçamentária")
    @ManyToOne
    @Pesquisavel
    private FonteDespesaORC fonteDespesaORC;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Liberado")
    private Boolean liberado;

    public ReservaDotacao() {
        super();
        valor = new BigDecimal(BigInteger.ZERO);
        liberado = false;
    }

    public ReservaDotacao(Date dataReserva, BigDecimal valor, AtoLegal atoLegal, FonteDespesaORC fonteDespesaORC, Boolean liberado) {
        this.dataReserva = dataReserva;
        this.valor = valor;
        this.atoLegal = atoLegal;
        this.fonteDespesaORC = fonteDespesaORC;
        this.liberado = liberado;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public Boolean getLiberado() {
        return liberado;
    }

    public void setLiberado(Boolean liberado) {
        this.liberado = liberado;
    }

    public OrigemReservaFonte getOrigemReservaFonte() {
        return origemReservaFonte;
    }

    public void setOrigemReservaFonte(OrigemReservaFonte origemReservaFonte) {
        this.origemReservaFonte = origemReservaFonte;
    }

    @Override
    public String toString() {
        return "Data: " + dataReserva + " - Valor: " + valor;
    }
}
