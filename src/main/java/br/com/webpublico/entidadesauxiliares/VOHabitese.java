package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Habitese;
import com.google.common.base.Strings;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class VOHabitese {

    private Long id;
    private Long codigo;
    private Integer exercicio;
    private Date dataExpedicaoTermo;
    private Date dataVencimentoISS;
    private Date dataLancamento;
    private String numeroProtocolo;
    private String situacao;
    private VOCalculoAlvara calculo;
    private String usuarioSistema;
    private VOCaracteristicaConstrucaoHabitese caracteristica;
    private VOAlvaraConstrucao alvara;
    private List<VODeducaoHabitese> deducoes;
    private BigDecimal baseDeCalculoISSQN;
    private BigDecimal aliquota;
    private BigDecimal valorAliquotaCalculado;

    public VOHabitese(Habitese habitese) {
        this(habitese, null);
    }

    public VOHabitese(Habitese habitese, VOAlvaraConstrucao alvaraConstrucao) {
        this.id = habitese.getId();
        this.codigo = habitese.getCodigo();
        this.exercicio = habitese.getExercicio().getAno();
        this.dataExpedicaoTermo = habitese.getDataExpedicaoTermo();
        this.dataVencimentoISS = habitese.getDataVencimentoISS();
        this.situacao = habitese.getSituacao().getDescricao();
        if (!Strings.isNullOrEmpty(habitese.getNumeroProtocolo())) {
            this.numeroProtocolo = habitese.getNumeroProtocolo() + "/" + habitese.getAnoProtocolo();
        } else {
            this.numeroProtocolo = "";
        }
        if (habitese.getProcessoCalcAlvaConstHabi() != null) {
            this.calculo = new VOCalculoAlvara(habitese.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese().get(0));
        }
        this.usuarioSistema = habitese.getUsuarioSistema().getLogin();
        this.dataLancamento = habitese.getDataLancamento();
        this.caracteristica = new VOCaracteristicaConstrucaoHabitese(habitese.getCaracteristica());
        this.alvara = alvaraConstrucao == null ? new VOAlvaraConstrucao(habitese.getAlvaraConstrucao()) : alvaraConstrucao;
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

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Date getDataExpedicaoTermo() {
        return dataExpedicaoTermo;
    }

    public void setDataExpedicaoTermo(Date dataExpedicaoTermo) {
        this.dataExpedicaoTermo = dataExpedicaoTermo;
    }

    public Date getDataVencimentoISS() {
        return dataVencimentoISS;
    }

    public void setDataVencimentoISS(Date dataVencimentoISS) {
        this.dataVencimentoISS = dataVencimentoISS;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public VOCalculoAlvara getCalculo() {
        return calculo;
    }

    public void setCalculo(VOCalculoAlvara calculo) {
        this.calculo = calculo;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(String usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public VOCaracteristicaConstrucaoHabitese getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(VOCaracteristicaConstrucaoHabitese caracteristica) {
        this.caracteristica = caracteristica;
    }

    public VOAlvaraConstrucao getAlvara() {
        return alvara;
    }

    public void setAlvara(VOAlvaraConstrucao alvara) {
        this.alvara = alvara;
    }

    public List<VODeducaoHabitese> getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(List<VODeducaoHabitese> deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getBaseDeCalculoISSQN() {
        return baseDeCalculoISSQN;
    }

    public void setBaseDeCalculoISSQN(BigDecimal baseDeCalculoISSQN) {
        this.baseDeCalculoISSQN = baseDeCalculoISSQN;
    }

    public BigDecimal getAliquota() {
        return aliquota;
    }

    public void setAliquota(BigDecimal aliquota) {
        this.aliquota = aliquota;
    }

    public BigDecimal getValorAliquotaCalculado() {
        return valorAliquotaCalculado;
    }

    public void setValorAliquotaCalculado(BigDecimal valorAliquotaCalculado) {
        this.valorAliquotaCalculado = valorAliquotaCalculado;
    }
}
