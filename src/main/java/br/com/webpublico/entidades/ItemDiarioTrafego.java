package br.com.webpublico.entidades;

import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 15/10/14
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ItemDiarioTrafego extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DiarioTrafego diarioTrafego;
    @Obrigatorio
    @Etiqueta("Motorista")
    @ManyToOne
    private Motorista motorista;
    @Obrigatorio
    @Etiqueta("Data de Lançamento")
    @Temporal(TemporalType.DATE)
    private Date dataLancamento;
    @Obrigatorio
    @Etiqueta("Hora de Saída")
    @Temporal(TemporalType.TIME)
    private Date horaSaida;
    @Obrigatorio
    @Etiqueta("Km na Saída")
    private BigDecimal kmSaida;
    @Obrigatorio
    @Etiqueta("Local na Saída")
    private String localSaida;
    @Etiqueta("Local na Chegada")
    private String localChegada;
    @Etiqueta("Hora de Chegada")
    @Temporal(TemporalType.TIME)
    private Date horaChegada;
    @Etiqueta("Km de Chegada")
    private BigDecimal kmChegada;
    @Obrigatorio
    @Etiqueta("Responsável")
    @ManyToOne
    private Pessoa responsavel;
    private Boolean houveRetorno;
    @ManyToOne
    private ReservaObjetoFrota reservaObjetoFrota;
    @Obrigatorio
    @Etiqueta("Data de Chegada")
    private Date dataChegada;

    public ItemDiarioTrafego() {
        super();
        houveRetorno = Boolean.FALSE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DiarioTrafego getDiarioTrafego() {
        return diarioTrafego;
    }

    public void setDiarioTrafego(DiarioTrafego diarioTrafego) {
        this.diarioTrafego = diarioTrafego;
    }

    public Motorista getMotorista() {
        return motorista;
    }

    public void setMotorista(Motorista motorista) {
        this.motorista = motorista;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Date getHoraSaida() {
        return horaSaida;
    }

    public void setHoraSaida(Date horaSaida) {
        this.horaSaida = horaSaida;
    }

    public BigDecimal getKmSaida() {
        return kmSaida;
    }

    public void setKmSaida(BigDecimal kmSaida) {
        this.kmSaida = kmSaida;
    }

    public String getLocalSaida() {
        return localSaida;
    }

    public void setLocalSaida(String localSaida) {
        this.localSaida = localSaida;
    }

    public String getLocalChegada() {
        return localChegada;
    }

    public void setLocalChegada(String localChegada) {
        this.localChegada = localChegada;
    }

    public Date getHoraChegada() {
        return horaChegada;
    }

    public void setHoraChegada(Date horaChegada) {
        this.horaChegada = horaChegada;
    }

    public BigDecimal getKmChegada() {
        return kmChegada;
    }

    public void setKmChegada(BigDecimal kmChegada) {
        this.kmChegada = kmChegada;
    }

    public Pessoa getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(Pessoa responsavel) {
        this.responsavel = responsavel;
    }

    public Boolean getHouveRetorno() {
        return houveRetorno;
    }

    public void setHouveRetorno(Boolean houveRetorno) {
        this.houveRetorno = houveRetorno;
    }

    public Date getDataChegada() {
        return dataChegada;
    }

    public void setDataChegada(Date dataChegada) {
        this.dataChegada = dataChegada;
    }

    public ReservaObjetoFrota getReservaObjetoFrota() {
        return reservaObjetoFrota;
    }

    public void setReservaObjetoFrota(ReservaObjetoFrota reservaObjetoFrota) {
        this.reservaObjetoFrota = reservaObjetoFrota;
    }

    public static ItemDiarioTrafego clonar(ItemDiarioTrafego itemDiarioTrafego) {
        ItemDiarioTrafego clone = new ItemDiarioTrafego();
        clone.setId(null);
        clone.setDataLancamento(itemDiarioTrafego.getDataLancamento());
        clone.setDiarioTrafego(itemDiarioTrafego.getDiarioTrafego());
        clone.setResponsavel(itemDiarioTrafego.getResponsavel());
        clone.setMotorista(itemDiarioTrafego.getMotorista());
        clone.setHoraSaida(itemDiarioTrafego.getHoraSaida());
        clone.setKmSaida(itemDiarioTrafego.getKmSaida());
        clone.setLocalSaida(itemDiarioTrafego.getLocalSaida());
        clone.setHouveRetorno(itemDiarioTrafego.getHouveRetorno());
        clone.setHoraChegada(itemDiarioTrafego.getHoraChegada());
        clone.setKmChegada(itemDiarioTrafego.getKmChegada());
        clone.setLocalChegada(itemDiarioTrafego.getLocalChegada());
        clone.setReservaObjetoFrota(itemDiarioTrafego.getReservaObjetoFrota());
        clone.setDataChegada(itemDiarioTrafego.getDataChegada());
        return clone;
    }
}
