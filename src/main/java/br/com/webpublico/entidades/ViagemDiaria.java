package br.com.webpublico.entidades;

import br.com.webpublico.enums.MeioDeTransporte;
import br.com.webpublico.enums.TipoViagem;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Viagens da Diária")
public class ViagemDiaria extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private PropostaConcessaoDiaria propostaConcessaoDiaria;
    @Obrigatorio
    @Etiqueta("Tipo de Viagem")
    @Enumerated(EnumType.STRING)
    private TipoViagem tipoViagem;
    @Obrigatorio
    @Etiqueta("Itinerário")
    private String itinerario;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Meio de Transporte")
    @Obrigatorio
    private MeioDeTransporte meioDeTransporte;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Inicial da Saída")
    private Date dataInicialSaida;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Final da Saída")
    private Date dataFinalSaida;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Inicial de Retorno")
    private Date dataInicialRetorno;
    @Obrigatorio
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Final de Retorno")
    private Date dataFinalRetorno;
    @Obrigatorio
    @Etiqueta("Hora Inicial de Saída")
    private String horaInicialSaida;
    @Obrigatorio
    @Etiqueta("Minuto Inicial de Saída")
    private String minutoInicialSaida;
    @Obrigatorio
    @Etiqueta("Hora Final de Saída")
    private String horaFinalSaida;
    @Obrigatorio
    @Etiqueta("Minuto Final de Saída")
    private String minutoFinalSaida;
    @Obrigatorio
    @Etiqueta("Hora Inicial de Chegada")
    private String horaInicialRetorno;
    @Obrigatorio
    @Etiqueta("Minuto Inicial de Chegada")
    private String minutoInicialRetorno;
    @Obrigatorio
    @Etiqueta("Hora Final de Chegada")
    private String horaFinalRetorno;
    @Obrigatorio
    @Etiqueta("Minuto Final de Chegada")
    private String minutoFinalRetorno;
    private BigDecimal totalValorMeia;
    private BigDecimal totalIntegral;
    private Double quantidadeDiarias;

    @Deprecated // Será apagado posteriormente
    private Boolean transladoComPernoite;

    public ViagemDiaria() {
        super();
        horaInicialSaida = "00";
        minutoInicialSaida = "00";
        horaInicialRetorno = "00";
        minutoInicialRetorno = "00";
        horaFinalSaida = "00";
        minutoFinalSaida = "00";
        horaFinalRetorno = "00";
        minutoFinalRetorno = "00";
        transladoComPernoite = Boolean.FALSE;
        totalValorMeia = BigDecimal.ZERO;
        totalIntegral = BigDecimal.ZERO;
        quantidadeDiarias = 0D;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PropostaConcessaoDiaria getPropostaConcessaoDiaria() {
        return propostaConcessaoDiaria;
    }

    public void setPropostaConcessaoDiaria(PropostaConcessaoDiaria propostaConcessaoDiaria) {
        this.propostaConcessaoDiaria = propostaConcessaoDiaria;
    }

    public TipoViagem getTipoViagem() {
        return tipoViagem;
    }

    public void setTipoViagem(TipoViagem tipoViagem) {
        this.tipoViagem = tipoViagem;
    }

    public String getItinerario() {
        return itinerario;
    }

    public void setItinerario(String itinerario) {
        this.itinerario = itinerario;
    }

    public MeioDeTransporte getMeioDeTransporte() {
        return meioDeTransporte;
    }

    public void setMeioDeTransporte(MeioDeTransporte meioDeTransporte) {
        this.meioDeTransporte = meioDeTransporte;
    }

    public String getHoraInicialSaida() {
        return horaInicialSaida;
    }

    public void setHoraInicialSaida(String horaSaida) {
        this.horaInicialSaida = horaSaida;
    }

    public String getMinutoInicialSaida() {
        return minutoInicialSaida;
    }

    public void setMinutoInicialSaida(String minutoSaida) {
        this.minutoInicialSaida = minutoSaida;
    }

    public String getHoraInicialRetorno() {
        return horaInicialRetorno;
    }

    public void setHoraInicialRetorno(String horaChegada) {
        this.horaInicialRetorno = horaChegada;
    }

    public String getMinutoInicialRetorno() {
        return minutoInicialRetorno;
    }

    public void setMinutoInicialRetorno(String minutoChegada) {
        this.minutoInicialRetorno = minutoChegada;
    }

    @Deprecated
    public Boolean getTransladoComPernoite() {
        return transladoComPernoite;
    }

    @Deprecated
    public void setTransladoComPernoite(Boolean transladoComPernoite) {
        this.transladoComPernoite = transladoComPernoite;
    }

    public Date getDataInicialSaida() {
        return dataInicialSaida;
    }

    public void setDataInicialSaida(Date inicio) {
        this.dataInicialSaida = inicio;
    }

    public Date getDataInicialRetorno() {
        return dataInicialRetorno;
    }

    public void setDataInicialRetorno(Date fim) {
        this.dataInicialRetorno = fim;
    }

    public boolean isDiariaInternacional() {
        return TipoViagem.INTERNACIONAL.equals(this.getTipoViagem());
    }

    public boolean isDiariaInterestadual() {
        return TipoViagem.INTERESTADUAL.equals(this.getTipoViagem());
    }

    public boolean isDiariaEstadual() {
        return TipoViagem.ESTADUAL.equals(this.getTipoViagem());
    }

    public BigDecimal getTotalValorMeia() {
        return totalValorMeia;
    }

    public void setTotalValorMeia(BigDecimal totalValorMeia) {
        this.totalValorMeia = totalValorMeia;
    }

    public BigDecimal getTotalIntegral() {
        return totalIntegral;
    }

    public void setTotalIntegral(BigDecimal totalIntegral) {
        this.totalIntegral = totalIntegral;
    }

    public Integer getQuantidadeDeDias() {
        return BigDecimal.valueOf(quantidadeDiarias - (quantidadeDiarias % 1D)).intValue();
    }

    public String getCaracterMeiaDiaria() {
        return (quantidadeDiarias % 1D) == 0.5D ? "\u00BD" : "0";
    }

    public boolean hasMeiaDiaria() {
        return (quantidadeDiarias % 1D) == 0.5D;
    }

    public String getDiasFormatado() {
        return getQuantidadeDeDias() + ((quantidadeDiarias % 1D) == 0.5D ? " " + getCaracterMeiaDiaria() : "");
    }

    public Double getQuantidadeDiarias() {
        return quantidadeDiarias;
    }

    public void setQuantidadeDiarias(Double quantidadeDiarias) {
        this.quantidadeDiarias = quantidadeDiarias;
    }

    public BigDecimal getValorTotal() {
        return totalIntegral.add(totalValorMeia);
    }

    public Date getDataFinalSaida() {
        return dataFinalSaida;
    }

    public void setDataFinalSaida(Date dataFinalSaida) {
        this.dataFinalSaida = dataFinalSaida;
    }

    public Date getDataFinalRetorno() {
        return dataFinalRetorno;
    }

    public void setDataFinalRetorno(Date dataFinalRetorno) {
        this.dataFinalRetorno = dataFinalRetorno;
    }

    public String getHoraFinalSaida() {
        return horaFinalSaida;
    }

    public void setHoraFinalSaida(String horaFinalSaida) {
        this.horaFinalSaida = horaFinalSaida;
    }

    public String getMinutoFinalSaida() {
        return minutoFinalSaida;
    }

    public void setMinutoFinalSaida(String minutoFinalSaida) {
        this.minutoFinalSaida = minutoFinalSaida;
    }

    public String getHoraFinalRetorno() {
        return horaFinalRetorno;
    }

    public void setHoraFinalRetorno(String horaFinalRetorno) {
        this.horaFinalRetorno = horaFinalRetorno;
    }

    public String getMinutoFinalRetorno() {
        return minutoFinalRetorno;
    }

    public void setMinutoFinalRetorno(String minutoFinalRetorno) {
        this.minutoFinalRetorno = minutoFinalRetorno;
    }
}
