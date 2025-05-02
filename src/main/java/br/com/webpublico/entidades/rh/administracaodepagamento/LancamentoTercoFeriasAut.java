package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.administracaopagamento.IdentificadorLancamentoTerco;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.ColunaAuditoriaRH;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
public class LancamentoTercoFeriasAut extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Período Aquisitivo")
    @ColunaAuditoriaRH(posicao = 5)
    @ManyToOne
    private PeriodoAquisitivoFL periodoAquisitivoFL;
    private Integer saldoPeriodoAquisitivo;
    @Etiqueta("Mês")
    @ColunaAuditoriaRH(posicao = 2)
    private Integer mes;
    @Etiqueta("Ano")
    @ColunaAuditoriaRH(posicao = 3)
    private Integer ano;
    @Etiqueta("Servidor(a)")
    @ColunaAuditoriaRH()
    @ManyToOne
    private ContratoFP contratoFP;
    @Etiqueta("Identificador de Lançamento")
    @ColunaAuditoriaRH(posicao = 4)
    @Enumerated(EnumType.STRING)
    private IdentificadorLancamentoTerco identificadorLancamento;

    public LancamentoTercoFeriasAut() {
    }

    public LancamentoTercoFeriasAut(PeriodoAquisitivoFL periodoAquisitivoFL, Integer saldoPeriodoAquisitivo, Integer mes, Integer ano, ContratoFP contratoFP, IdentificadorLancamentoTerco identificadorLancamento) {
        this.periodoAquisitivoFL = periodoAquisitivoFL;
        this.saldoPeriodoAquisitivo = saldoPeriodoAquisitivo;
        this.mes = mes;
        this.ano = ano;
        this.contratoFP = contratoFP;
        this.identificadorLancamento = identificadorLancamento;
    }

    public PeriodoAquisitivoFL getPeriodoAquisitivoFL() {
        return periodoAquisitivoFL;
    }

    public void setPeriodoAquisitivoFL(PeriodoAquisitivoFL periodoAquisitivoFL) {
        this.periodoAquisitivoFL = periodoAquisitivoFL;
    }

    public Integer getSaldoPeriodoAquisitivo() {
        return saldoPeriodoAquisitivo;
    }

    public void setSaldoPeriodoAquisitivo(Integer saldoPeriodoAquisitivo) {
        this.saldoPeriodoAquisitivo = saldoPeriodoAquisitivo;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public IdentificadorLancamentoTerco getIdentificadorLancamento() {
        return identificadorLancamento;
    }

    public void setIdentificadorLancamento(IdentificadorLancamentoTerco identificadorLancamento) {
        this.identificadorLancamento = identificadorLancamento;
    }
}
