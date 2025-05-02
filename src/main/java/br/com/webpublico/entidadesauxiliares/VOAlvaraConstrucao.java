package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AlvaraConstrucao;
import br.com.webpublico.entidades.Habitese;
import br.com.webpublico.entidades.ServicosAlvaraConstrucao;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class VOAlvaraConstrucao {

    private Long id;
    private Long codigo;
    private Integer exercicio;
    private String numeroProtocolo;
    private String anoProtocolo;
    private String usuarioIncluiu;
    private Date dataExpedicao;
    private Date dataVencimentoCartaz;
    private Date dataVencimentoDebito;
    private String situacao;
    private VOProcRegularizaConstrucao procRegularizaConstrucao;
    private List<VOServicosAlvaraConstrucao> servicos;
    private String responsavelServico;
    private VOCalculoAlvara voCalculoAlvara;
    private List<VOHabitese> habitese;

    public VOAlvaraConstrucao(AlvaraConstrucao alvaraConstrucao) {
        this(alvaraConstrucao, null);
    }

    public VOAlvaraConstrucao(AlvaraConstrucao alvaraConstrucao, VOProcRegularizaConstrucao procRegularizaConstrucao) {
        this.id = alvaraConstrucao.getId();
        this.codigo = alvaraConstrucao.getCodigo();
        this.exercicio = alvaraConstrucao.getExercicio().getAno();
        if (!Strings.isNullOrEmpty(alvaraConstrucao.getNumeroProtocolo())) {
            this.numeroProtocolo = alvaraConstrucao.getNumeroProtocolo() + "/" + alvaraConstrucao.getAnoProtocolo();
        } else {
            this.numeroProtocolo = "";
        }
        this.usuarioIncluiu = alvaraConstrucao.getUsuarioIncluiu().getLogin();
        this.dataExpedicao = alvaraConstrucao.getDataExpedicao();
        this.dataVencimentoCartaz = alvaraConstrucao.getDataVencimentoCartaz();
        this.dataVencimentoDebito = alvaraConstrucao.getDataVencimentoDebito();
        this.situacao = alvaraConstrucao.getSituacao().getDescricao();
        this.servicos = Lists.newArrayList();
        for (ServicosAlvaraConstrucao servico : alvaraConstrucao.getServicos()) {
            this.servicos.add(new VOServicosAlvaraConstrucao(servico));
        }
        this.responsavelServico = alvaraConstrucao.getResponsavelServico().getCauCrea() + " - " + alvaraConstrucao.getResponsavelServico().getPessoa().getNome();
        if (alvaraConstrucao.getProcessoCalcAlvaConstVisto() != null) {
            this.voCalculoAlvara = new VOCalculoAlvara(alvaraConstrucao.getProcessoCalcAlvaConstHabi().getCalculosAlvaraConstrucaoHabitese().get(0));
        }
        this.habitese = Lists.newArrayList();
        for (Habitese habitese : alvaraConstrucao.getHabiteses()) {
            this.habitese.add(new VOHabitese(habitese, this));
        }
        this.procRegularizaConstrucao = procRegularizaConstrucao == null ? new VOProcRegularizaConstrucao(alvaraConstrucao.getProcRegularizaConstrucao()) : procRegularizaConstrucao;
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

    public VOProcRegularizaConstrucao getProcRegularizaConstrucao() {
        return procRegularizaConstrucao;
    }

    public void setProcRegularizaConstrucao(VOProcRegularizaConstrucao procRegularizaConstrucao) {
        this.procRegularizaConstrucao = procRegularizaConstrucao;
    }

    public String getResponsavelServico() {
        return responsavelServico;
    }

    public void setResponsavelServico(String responsavelServico) {
        this.responsavelServico = responsavelServico;
    }

    public List<VOServicosAlvaraConstrucao> getServicos() {
        return servicos;
    }

    public void setServicos(List<VOServicosAlvaraConstrucao> servicos) {
        this.servicos = servicos;
    }

    public VOCalculoAlvara getVoCalculoAlvara() {
        return voCalculoAlvara;
    }

    public void setVoCalculoAlvara(VOCalculoAlvara voCalculoAlvara) {
        this.voCalculoAlvara = voCalculoAlvara;
    }

    public List<VOHabitese> getHabitese() {
        return habitese;
    }

    public void setHabitese(List<VOHabitese> habitese) {
        this.habitese = habitese;
    }
}
