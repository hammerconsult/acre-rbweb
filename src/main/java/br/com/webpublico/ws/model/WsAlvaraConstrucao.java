package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.AlvaraConstrucao;
import br.com.webpublico.entidades.Habitese;
import br.com.webpublico.entidades.ServicosAlvaraConstrucao;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class WsAlvaraConstrucao implements Serializable {

    private Long id;
    private Long codigo;
    private Integer exercicio;
    private String numeroProtocolo;
    private String anoProtocolo;
    private String usuarioIncluiu;
    private String construcao;
    private Date dataExpedicao;
    private Date dataVencimentoCartaz;
    private Date dataVencimentoDebito;
    private String situacao;
    private WsProcRegularizaConstrucao procRegularizaConstrucao;
    private List<WsServicosAlvaraConstrucao> servicos;
    private String responsavelServico;
    private List<WsHabitese> habiteses;

    public WsAlvaraConstrucao() {

    }

    public WsAlvaraConstrucao(AlvaraConstrucao alvara) {
        this(alvara, null);
    }

    public WsAlvaraConstrucao(AlvaraConstrucao alvara, WsProcRegularizaConstrucao wsProcRegularizaConstrucao) {
        this.id = alvara.getId();
        this.exercicio = alvara.getExercicio().getAno();
        this.codigo = alvara.getCodigo();
        if (!Strings.isNullOrEmpty(alvara.getAnoProtocolo())) {
            this.numeroProtocolo = alvara.getNumeroProtocolo();
            this.anoProtocolo = alvara.getAnoProtocolo();
        } else {
            this.numeroProtocolo = "";
            this.anoProtocolo = "";
        }
        this.usuarioIncluiu = alvara.getUsuarioIncluiu().getLogin();
        this.dataExpedicao = alvara.getDataExpedicao();
        this.dataVencimentoCartaz = alvara.getDataVencimentoCartaz();
        this.dataVencimentoDebito = alvara.getDataVencimentoDebito();
        this.situacao = alvara.getSituacao().getDescricao();
        this.procRegularizaConstrucao = wsProcRegularizaConstrucao == null ? new WsProcRegularizaConstrucao(alvara.getProcRegularizaConstrucao()) : wsProcRegularizaConstrucao;
        this.servicos = Lists.newArrayList();
        for (ServicosAlvaraConstrucao servicosAlvaraConstrucao : alvara.getServicos()) {
            this.servicos.add(new WsServicosAlvaraConstrucao(servicosAlvaraConstrucao));
        }
        this.responsavelServico = alvara.getResponsavelServico() != null ? alvara.getResponsavelServico().toString() : "";
        this.construcao = (alvara.getConstrucaoAlvara() != null && alvara.getConstrucaoAlvara().getConstrucao() != null) ?
            alvara.getConstrucaoAlvara().getConstrucao().toString() : "";
        this.habiteses = Lists.newArrayList();
        for (Habitese habitese : alvara.getHabiteses()) {
            this.habiteses.add(new WsHabitese(habitese, this));
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

    public String getUsuarioIncluiu() {
        return usuarioIncluiu;
    }

    public void setUsuarioIncluiu(String usuarioIncluiu) {
        this.usuarioIncluiu = usuarioIncluiu;
    }

    public Date getDataExpedicao() {
        return dataExpedicao;
    }

    public void setDataExpedicao(Date dataExpedicao) {
        this.dataExpedicao = dataExpedicao;
    }

    public Date getDataVencimentoCartaz() {
        return dataVencimentoCartaz;
    }

    public void setDataVencimentoCartaz(Date dataVencimentoCartaz) {
        this.dataVencimentoCartaz = dataVencimentoCartaz;
    }

    public Date getDataVencimentoDebito() {
        return dataVencimentoDebito;
    }

    public void setDataVencimentoDebito(Date dataVencimentoDebito) {
        this.dataVencimentoDebito = dataVencimentoDebito;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public WsProcRegularizaConstrucao getProcRegularizaConstrucao() {
        return procRegularizaConstrucao;
    }

    public void setProcRegularizaConstrucao(WsProcRegularizaConstrucao procRegularizaConstrucao) {
        this.procRegularizaConstrucao = procRegularizaConstrucao;
    }

    public List<WsServicosAlvaraConstrucao> getServicos() {
        return servicos;
    }

    public void setServicos(List<WsServicosAlvaraConstrucao> servicos) {
        this.servicos = servicos;
    }

    public String getResponsavelServico() {
        return responsavelServico;
    }

    public void setResponsavelServico(String responsavelServico) {
        this.responsavelServico = responsavelServico;
    }

    public List<WsHabitese> getHabiteses() {
        return habiteses;
    }

    public void setHabiteses(List<WsHabitese> habiteses) {
        this.habiteses = habiteses;
    }

    public String getConstrucao() {
        return construcao;
    }

    public void setConstrucao(String construcao) {
        this.construcao = construcao;
    }

}
