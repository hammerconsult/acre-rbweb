/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.CamposPesquisa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import org.hibernate.envers.Audited;

/**
 *
 * @author Peixe
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Itens Lote Processamento")
public class ItensLoteProcessamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LoteProcessamento loteProcessamento;
    @Enumerated(EnumType.STRING)
    private CamposPesquisa campo;
    private String tipo;
    private String valor;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataInicio;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataFim;
    @Transient
    private long criadoEm;

    public ItensLoteProcessamento() {
        criadoEm = System.nanoTime();
    }

    public ItensLoteProcessamento(String tipo, String valor) {
        super();
        this.tipo = tipo;
        this.valor = valor;

    }

    public ItensLoteProcessamento(CamposPesquisa campo, String tipo, String valor) {
        super();
        this.campo = campo;
        this.tipo = tipo;
        this.valor = valor;
    }

    public long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LoteProcessamento getLoteProcessamento() {
        return loteProcessamento;
    }

    public void setLoteProcessamento(LoteProcessamento loteProcessamento) {
        this.loteProcessamento = loteProcessamento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

//    public TipoOperador getJuncao() {
//        return operador;
//    }
//
//    public void setOperador(TipoOperador operador) {
//        this.operador = operador;
//    }
    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public CamposPesquisa getCampo() {
        return campo;
    }

    public void setCampo(CamposPesquisa campo) {
        this.campo = campo;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return tipo + " - " + valor;
    }

    public enum TipoOperador {

        AND, OR;
    }
}
