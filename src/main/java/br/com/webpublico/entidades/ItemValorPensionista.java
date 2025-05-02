/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.entidades.rh.configuracao.ReajusteMediaAposentadoria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.ItemValorPrevidencia;
import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.interfaces.ValidadorVigenciaFolha;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author boy
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ItemValorPensionista extends SuperEntidade implements ItemValorPrevidencia, ValidadorVigencia, ValidadorVigenciaFolha, Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Valor")
    private BigDecimal valor;
    @Etiqueta("Início da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Etiqueta("Final da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @ManyToOne
    @Etiqueta("Pensionista")
    private Pensionista pensionista;
    @Etiqueta("Principal Verba do Pensionista")
    @ManyToOne
    private EventoFP eventoFP;
    @ManyToOne
    private ReajusteMediaAposentadoria reajusteRecebido;
    @Etiqueta("Verba Principal")
    private Boolean verbaPrincipal;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;

    public ItemValorPensionista() {
        dataRegistro = new Date();
        valor = BigDecimal.ZERO;
        verbaPrincipal = false;
    }

    public ReajusteMediaAposentadoria getReajusteRecebido() {
        return reajusteRecebido;
    }

    public void setReajusteRecebido(ReajusteMediaAposentadoria reajusteRecebido) {
        this.reajusteRecebido = reajusteRecebido;
    }

    public Boolean getVerbaPrincipal() {
        return verbaPrincipal != null ? verbaPrincipal : false;
    }

    public void setVerbaPrincipal(Boolean verbaPrincipal) {
        this.verbaPrincipal = verbaPrincipal;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    @Override
    public VinculoFP getVinculoFP() {
        return pensionista;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    @Override
    public void setVinculoFP(VinculoFP vinculoFP) {
        if (vinculoFP instanceof Pensionista) {
            setPensionista((Pensionista) vinculoFP);
        }
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return this.finalVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.finalVigencia = data;
    }

    public Pensionista getPensionista() {
        return pensionista;
    }

    public void setPensionista(Pensionista pensionista) {
        this.pensionista = pensionista;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return DataUtil.getDataFormatada(inicioVigencia) + " - " + DataUtil.getDataFormatada(finalVigencia) + " valor: R$ " + valor + ", verba base: " + eventoFP;
    }

    public boolean temReajusteRecebido() {
        return reajusteRecebido != null;
    }
}
