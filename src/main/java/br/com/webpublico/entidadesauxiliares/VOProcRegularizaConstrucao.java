package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.AlvaraConstrucao;
import br.com.webpublico.entidades.ProcRegularizaConstrucao;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class VOProcRegularizaConstrucao {

    private Long id;
    private Integer exercicio;
    private Long codigo;
    private Date dataCriacao;
    private String numeroProtocolo;
    private String usuarioIncluiu;
    private String observacao;
    private VOCadastroImobiliario cadastroImobiliario;
    private Date dataInicioObra;
    private Date dataFimObra;
    private String matriculaINSS;
    private String responsavelProjeto;
    private String responsavelExecucao;
    private String situacao;
    private List<VOAlvaraConstrucao> alvaras;
    private VOAlvaraConstrucao alvara;

    public VOProcRegularizaConstrucao(ProcRegularizaConstrucao procRegularizaConstrucao) {
        this.codigo = procRegularizaConstrucao.getCodigo();
        this.id = procRegularizaConstrucao.getId();
        this.exercicio = procRegularizaConstrucao.getExercicio().getAno();
        this.dataCriacao = procRegularizaConstrucao.getDataCriacao();
        if (!Strings.isNullOrEmpty(procRegularizaConstrucao.getNumeroProtocolo())) {
            this.numeroProtocolo = procRegularizaConstrucao.getNumeroProtocolo() + "/" + procRegularizaConstrucao.getAnoProtocolo();
        } else {
            this.numeroProtocolo = "";
        }
        this.usuarioIncluiu = procRegularizaConstrucao.getUsuarioIncluiu().getLogin();
        this.observacao = procRegularizaConstrucao.getObservacao();
        this.cadastroImobiliario = new VOCadastroImobiliario(procRegularizaConstrucao.getCadastroImobiliario());
        this.dataInicioObra = procRegularizaConstrucao.getDataInicioObra();
        this.dataFimObra = procRegularizaConstrucao.getDataFimObra();
        this.matriculaINSS = procRegularizaConstrucao.getMatriculaINSS();
        this.responsavelProjeto = procRegularizaConstrucao.getResponsavelProjeto().getCauCrea() + " - " + procRegularizaConstrucao.getResponsavelProjeto().getPessoa().getNome();
        this.responsavelExecucao = procRegularizaConstrucao.getResponsavelExecucao().getCauCrea() + " - " + procRegularizaConstrucao.getResponsavelExecucao().getPessoa().getNome();
        this.situacao = procRegularizaConstrucao.getSituacao().getDescricao();
        this.alvara = new VOAlvaraConstrucao(procRegularizaConstrucao.getUltimoAlvara(), this);
        this.alvaras = Lists.newArrayList();
        for (AlvaraConstrucao alvaraConstrucao : procRegularizaConstrucao.getAlvarasDeConstrucao()) {
            this.alvaras.add(new VOAlvaraConstrucao(alvaraConstrucao, this));
        }
        if (this.numeroProtocolo == null) {
            this.numeroProtocolo = "";
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getExercicio() {
        return exercicio;
    }

    public void setExercicio(Integer exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getNumeroProtocolo() {
        return numeroProtocolo;
    }

    public void setNumeroProtocolo(String numeroProtocolo) {
        this.numeroProtocolo = numeroProtocolo;
    }

    public String getUsuarioIncluiu() {
        return usuarioIncluiu;
    }

    public void setUsuarioIncluiu(String usuarioIncluiu) {
        this.usuarioIncluiu = usuarioIncluiu;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public VOCadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(VOCadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Date getDataInicioObra() {
        return dataInicioObra;
    }

    public void setDataInicioObra(Date dataInicioObra) {
        this.dataInicioObra = dataInicioObra;
    }

    public Date getDataFimObra() {
        return dataFimObra;
    }

    public void setDataFimObra(Date dataFimObra) {
        this.dataFimObra = dataFimObra;
    }

    public String getMatriculaINSS() {
        return matriculaINSS;
    }

    public void setMatriculaINSS(String matriculaINSS) {
        this.matriculaINSS = matriculaINSS;
    }

    public String getResponsavelProjeto() {
        return responsavelProjeto;
    }

    public void setResponsavelProjeto(String responsavelProjeto) {
        this.responsavelProjeto = responsavelProjeto;
    }

    public String getResponsavelExecucao() {
        return responsavelExecucao;
    }

    public void setResponsavelExecucao(String responsavelExecucao) {
        this.responsavelExecucao = responsavelExecucao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public VOAlvaraConstrucao getAlvara() {
        return alvara;
    }

    public void setAlvara(VOAlvaraConstrucao alvara) {
        this.alvara = alvara;
    }

    public List<VOAlvaraConstrucao> getAlvaras() {
        return alvaras;
    }

    public void setAlvaras(List<VOAlvaraConstrucao> alvaras) {
        this.alvaras = alvaras;
    }
}
