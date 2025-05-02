/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusPeriodoAquisitivo;
import br.com.webpublico.geradores.CorEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * @author peixe
 */
@Entity

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@CorEntidade(value = "#9400D3")
@Etiqueta("Período Aquisitivo Férias Licença")
public class PeriodoAquisitivoFL implements Serializable, Comparable<PeriodoAquisitivoFL> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Quantidade de Dias")
    private Integer quantidadeDias;
    @Tabelavel
    private Integer saldoDeDireito;
    @Tabelavel
    @Etiqueta("Base Cargo")
    @Obrigatorio
    @ManyToOne
    private BaseCargo baseCargo;
    @Tabelavel
    @Etiqueta("Inicio da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Tabelavel
    @Etiqueta("Final da Vigência")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date finalVigencia;
    @ManyToOne
    private ContratoFP contratoFP;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private StatusPeriodoAquisitivo status;
    @Tabelavel
    private Integer saldo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataAtualizacao;
    @OneToOne(mappedBy = "periodoAquisitivoFL")
    private SugestaoFerias sugestaoFerias;
    private String observacao;
    @Transient
    @Invisivel
    private Long criadoEm;

    public PeriodoAquisitivoFL() {
        this.criadoEm = System.nanoTime();
    }

    public PeriodoAquisitivoFL(Integer quantidadeDias, BaseCargo baseCargo, Date inicioVigencia, Date finalVigencia, ContratoFP contratoFP) {
        this.criadoEm = System.nanoTime();
        this.quantidadeDias = quantidadeDias;
        this.baseCargo = baseCargo;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.contratoFP = contratoFP;
    }

    public PeriodoAquisitivoFL(Integer quantidadeDias, BaseCargo baseCargo, Date inicioVigencia, Date finalVigencia, ContratoFP contratoFP, Integer saldoDeDireito) {
        this.criadoEm = System.nanoTime();
        this.quantidadeDias = quantidadeDias;
        this.baseCargo = baseCargo;
        this.inicioVigencia = inicioVigencia;
        this.finalVigencia = finalVigencia;
        this.contratoFP = contratoFP;
        this.saldoDeDireito = saldoDeDireito == null ? 0 : saldoDeDireito;
        this.saldo = saldoDeDireito;
        this.status = StatusPeriodoAquisitivo.NAO_CONCEDIDO;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public SugestaoFerias getSugestaoFerias() {
        return sugestaoFerias;
    }

    public void setSugestaoFerias(SugestaoFerias sugestaoFerias) {
        this.sugestaoFerias = sugestaoFerias;
    }

    public Long getId() {
        return id;
    }

    public Integer getSaldo() {
        return saldo;
    }

    public void setSaldo(Integer saldo) {
        this.saldo = saldo;
    }

    public BaseCargo getBaseCargo() {
        return baseCargo;
    }

    public StatusPeriodoAquisitivo getStatus() {
        return status;
    }

    public void setStatus(StatusPeriodoAquisitivo status) {
        this.status = status;
    }

    public void setBaseCargo(BaseCargo baseCargo) {
        this.baseCargo = baseCargo;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Integer getQuantidadeDias() {
        return quantidadeDias;
    }

    public void setQuantidadeDias(Integer quantidadeDias) {
        this.quantidadeDias = quantidadeDias;
    }

    public Integer getSaldoDeDireito() {
        return saldoDeDireito;
    }

    public void setSaldoDeDireito(Integer saldoDeDireito) {
        this.saldoDeDireito = saldoDeDireito;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(Date dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public DateTime getFinalVigenciaJoda() {
        if (finalVigencia != null) {
            return new DateTime(finalVigencia);
        }
        return null;
    }

    public DateTime getInicioVigenciaJoda() {
        if (inicioVigencia != null) {
            return new DateTime(inicioVigencia);
        }
        return null;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    public String getSaldoDeDireitoToString() {
        if (saldoDeDireito == null || saldoDeDireito == 0) {
            return "00";
        }
        if (saldoDeDireito < 10) {
            return "0" + saldo;
        }
        return "" + saldoDeDireito;
    }

    @Override
    public String toString() {
        return this.contratoFP + " - " + DataUtil.getDataFormatada(this.getInicioVigencia()) + " - " + DataUtil.getDataFormatada(this.getFinalVigencia()) + " - Saldo de Direito: " + this.saldoDeDireito;
    }

    public String toStringAlternativo() {
        return DataUtil.getDataFormatada(this.getInicioVigencia()) + " - " + DataUtil.getDataFormatada(this.getFinalVigencia()) + " - Saldo de Direito: " + this.saldoDeDireito;
    }


    public String getToStringAlternativo() {
        return DataUtil.getDataFormatada(this.getInicioVigencia()) + " - " + DataUtil.getDataFormatada(this.getFinalVigencia()) + " - Saldo de Direito: " + this.saldoDeDireito;
    }

    public String getToStringAlternativoPeriodo() {
        return DataUtil.getDataFormatada(this.getInicioVigencia()) + " - " + DataUtil.getDataFormatada(this.getFinalVigencia());
    }

    public boolean isStatusConcedido() {
        return StatusPeriodoAquisitivo.CONCEDIDO.equals(status);
    }

    @Override
    public int compareTo(PeriodoAquisitivoFL o) {
        return Comparators.INICIOVIGENCIAASC.compare(this, o);
    }

    public boolean temSugestaoFerias() {
        return sugestaoFerias != null;
    }

    public boolean temSugestaoFeriasAprovada() {
        try {
            if (sugestaoFerias.estaAprovada()) {
                return true;
            }
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public boolean temSaldoDeDireito() {
        try {
            return saldoDeDireito.compareTo(new Integer(0)) > 0;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public static class Comparators {

        public static Comparator<PeriodoAquisitivoFL> INICIOVIGENCIAASC = new Comparator<PeriodoAquisitivoFL>() {
            @Override
            public int compare(PeriodoAquisitivoFL o1, PeriodoAquisitivoFL o2) {
                return o1.inicioVigencia.compareTo(o2.inicioVigencia);
            }
        };
        public static Comparator<PeriodoAquisitivoFL> INICIOVIGENCIADESC = new Comparator<PeriodoAquisitivoFL>() {
            @Override
            public int compare(PeriodoAquisitivoFL o1, PeriodoAquisitivoFL o2) {
                return o2.inicioVigencia.compareTo(o1.inicioVigencia);
            }
        };

//        public static Comparator<PeriodoAquisitivoFL> NAMEANDAGE = new Comparator<PeriodoAquisitivoFL>() {
//            @Override
//            public int compare(PeriodoAquisitivoFL o1, PeriodoAquisitivoFL o2) {
//                int i = o1.name.compareTo(o2.name);
//                if (i == 0) {
//                    i = o1.age - o2.age;
//                }
//                return i;
//            }
//        };
    }

}
