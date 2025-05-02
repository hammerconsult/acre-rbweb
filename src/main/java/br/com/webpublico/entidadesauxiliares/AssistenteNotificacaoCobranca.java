package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.NotificacaoCobrancaAdmin;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by tharlyson on 27/11/19.
 */
public class AssistenteNotificacaoCobranca {

    private NotificacaoCobrancaAdmin selecionado;
    private List<ResultadoParcela> resultadosParcela;
    private Boolean finalizarProcesso;
    private String ip;
    private UsuarioSistema usuarioSistema;
    private Date dataOperacao;
    private Exercicio exercicio;
    private Boolean emitirNotificacao;
    private AssistenteBarraProgresso assistenteBarraProgresso;


    public AssistenteNotificacaoCobranca(NotificacaoCobrancaAdmin selecionado) {
        this.selecionado = selecionado;
        this.resultadosParcela = Lists.newArrayList();
        this.finalizarProcesso = false;
        this.emitirNotificacao = false;
    }

    public Boolean getEmitirNotificacao() {
        return emitirNotificacao;
    }

    public void setEmitirNotificacao(Boolean emitirNotificacao) {
        this.emitirNotificacao = emitirNotificacao;
    }

    public Boolean getFinalizarProcesso() {
        return finalizarProcesso;
    }

    public void setFinalizarProcesso(Boolean finalizarProcesso) {
        this.finalizarProcesso = finalizarProcesso;
    }

    public NotificacaoCobrancaAdmin getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(NotificacaoCobrancaAdmin selecionado) {
        this.selecionado = selecionado;
    }

    public List<ResultadoParcela> getResultadosParcela() {
        return resultadosParcela;
    }

    public void setResultadosParcela(List<ResultadoParcela> resultadosParcela) {
        this.resultadosParcela = resultadosParcela;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }
}
