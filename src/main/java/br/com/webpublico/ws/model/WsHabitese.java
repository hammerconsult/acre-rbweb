package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.DeducaoHabitese;
import br.com.webpublico.entidades.Habitese;
import br.com.webpublico.entidades.ServicosAlvaraConstrucao;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class WsHabitese implements Serializable {

    private Long id;
    private Long codigo;
    private Integer exercicio;
    private String usuarioIncluiu;
    private Date dataExpedicaoTermo;
    private Date dataVencimentoISS;
    private Date dataLancamento;
    private String numeroProtocolo;
    private String anoProtocolo;
    private String situacao;
    private WsAlvaraConstrucao alvaraConstrucao;
    private WsCaracteristicaConstrucaoHabitese caracteristica;
    private List<WsDeducaoHabitese> deducoes;
    private List<WsServicosAlvaraConstrucao> servicos;
    private BigDecimal baseCalculoISSQN;
    private BigDecimal valorAliquota;
    private BigDecimal valorAliquotaCalculado;

    public WsHabitese() {

    }

    public WsHabitese(Habitese habitese) {
        this(habitese, null);
    }

    public WsHabitese(Habitese habitese, WsAlvaraConstrucao wsAlvaraConstrucao) {
        this.id = habitese.getId();
        this.exercicio = habitese.getExercicio().getAno();
        this.codigo = habitese.getCodigo();
        if (!Strings.isNullOrEmpty(habitese.getAnoProtocolo())) {
            this.numeroProtocolo = habitese.getNumeroProtocolo();
            this.anoProtocolo = habitese.getAnoProtocolo();
        } else {
            this.numeroProtocolo = "";
            this.anoProtocolo = "";
        }
        this.usuarioIncluiu = habitese.getUsuarioSistema().getLogin();
        this.dataExpedicaoTermo = habitese.getDataExpedicaoTermo();
        this.dataVencimentoISS = habitese.getDataVencimentoISS();
        this.dataLancamento = habitese.getDataLancamento();
        this.situacao = habitese.getSituacao().getDescricao();
        this.alvaraConstrucao = wsAlvaraConstrucao == null ? new WsAlvaraConstrucao(habitese.getAlvaraConstrucao()) : wsAlvaraConstrucao;
        this.caracteristica = new WsCaracteristicaConstrucaoHabitese(habitese.getCaracteristica());
        this.deducoes = Lists.newArrayList();
        this.servicos = Lists.newArrayList();
        for (DeducaoHabitese deducaoHabitese : habitese.getDeducoes()) {
            this.deducoes.add(new WsDeducaoHabitese(deducaoHabitese));
        }
        for (ServicosAlvaraConstrucao servico : habitese.getCaracteristica().getServicos()) {
            this.servicos.add(new WsServicosAlvaraConstrucao(servico));
        }
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

    public String getUsuarioIncluiu() {
        return usuarioIncluiu;
    }

    public void setUsuarioIncluiu(String usuarioIncluiu) {
        this.usuarioIncluiu = usuarioIncluiu;
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

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getAnoProtocolo() {
        return anoProtocolo;
    }

    public void setAnoProtocolo(String anoProtocolo) {
        this.anoProtocolo = anoProtocolo;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public WsAlvaraConstrucao getAlvaraConstrucao() {
        return alvaraConstrucao;
    }

    public void setAlvaraConstrucao(WsAlvaraConstrucao alvaraConstrucao) {
        this.alvaraConstrucao = alvaraConstrucao;
    }

    public WsCaracteristicaConstrucaoHabitese getCaracteristica() {
        return caracteristica;
    }

    public void setCaracteristica(WsCaracteristicaConstrucaoHabitese caracteristica) {
        this.caracteristica = caracteristica;
    }

    public List<WsServicosAlvaraConstrucao> getServicos() {
        return servicos;
    }

    public void setServicos(List<WsServicosAlvaraConstrucao> servicos) {
        this.servicos = servicos;
    }

    public List<WsDeducaoHabitese> getDeducoes() {
        return deducoes;
    }

    public void setDeducoes(List<WsDeducaoHabitese> deducoes) {
        this.deducoes = deducoes;
    }

    public BigDecimal getBaseCalculoISSQN() {
        return baseCalculoISSQN;
    }

    public void setBaseCalculoISSQN(BigDecimal baseCalculoISSQN) {
        this.baseCalculoISSQN = baseCalculoISSQN;
    }

    public BigDecimal getValorAliquota() {
        return valorAliquota;
    }

    public void setValorAliquota(BigDecimal valorAliquota) {
        this.valorAliquota = valorAliquota;
    }

    public BigDecimal getValorAliquotaCalculado() {
        return valorAliquotaCalculado;
    }

    public void setValorAliquotaCalculado(BigDecimal valorAliquotaCalculado) {
        this.valorAliquotaCalculado = valorAliquotaCalculado;
    }

}
