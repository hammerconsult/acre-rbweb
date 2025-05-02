/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@GrupoDiagrama(nome = "RBTrans")
@Entity
@Audited
@Etiqueta("Tarifa de Outorga")
public class TarifaOutorga implements Serializable, ValidadorVigencia {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Pesquisavel
    @Etiqueta("Código")
    @Tabelavel
    private Long codigo;
    @Pesquisavel
    @Column(length = 70)
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Pesquisavel
    @Monetario
    @Obrigatorio
    @Etiqueta("Valor")
    @Tabelavel
    private BigDecimal valor;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Índice Econômico")
    @Tabelavel
    @Pesquisavel
    private IndiceEconomico indiceEconomico;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Início de Vigência")
    @Temporal(TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Fim de Vigência")
    @Temporal(TemporalType.DATE)
    private Date fimVigencia;

    public TarifaOutorga() {
    }

    public TarifaOutorga(String descricao) {
        this.descricao = descricao;
    }

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
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

    public IndiceEconomico getIndiceEconomico() {
        return indiceEconomico;
    }

    public void setIndiceEconomico(IndiceEconomico indiceEconomico) {
        this.indiceEconomico = indiceEconomico;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof TarifaOutorga)) {
            return false;
        }
        TarifaOutorga other = (TarifaOutorga) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");
        if (codigo != null) {
            sb.append(codigo).append(" - ");
        }
        if (descricao != null) {
            sb.append(descricao);
        }

        return sb.toString().trim();
    }
}
