package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

@Entity
@Audited
public class ZonaFiscal extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    private Long codigo;
    @Etiqueta("Domínio GE")
    @Obrigatorio
    private Integer dominioGe;
    @Etiqueta("Índice")
    @Obrigatorio
    private BigDecimal indice;
    @Etiqueta("VUT – Valor Unitário de Terreno (Valor de M²)")
    @Obrigatorio
    private BigDecimal valorUnitarioTerreno;

    public ZonaFiscal() {
        super();
        indice = BigDecimal.ZERO;
        valorUnitarioTerreno = BigDecimal.ZERO;
    }

    public ZonaFiscal(Long id, BigDecimal indice, BigDecimal valorUnitarioTerreno) {
        this.id = id;
        this.indice = indice;
        this.valorUnitarioTerreno = valorUnitarioTerreno;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Integer getDominioGe() {
        return dominioGe;
    }

    public void setDominioGe(Integer dominioGe) {
        this.dominioGe = dominioGe;
    }

    public BigDecimal getIndice() {
        return indice;
    }

    public void setIndice(BigDecimal indice) {
        this.indice = indice;
    }

    public Double getValorUnitarioTerreno() {
        if (valorUnitarioTerreno != null) {
            return valorUnitarioTerreno.doubleValue();
        }
        return null;
    }

    public void setValorUnitarioTerreno(BigDecimal valorUnitarioTerreno) {
        this.valorUnitarioTerreno = valorUnitarioTerreno;
    }

    @Override
    public String toString() {
        return codigo + "";
    }
}
