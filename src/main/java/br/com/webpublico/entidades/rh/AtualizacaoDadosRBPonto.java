package br.com.webpublico.entidades.rh;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.rh.StatusIntegracaoRBPonto;
import br.com.webpublico.enums.rh.TipoInformacaoEnvioRBPonto;
import br.com.webpublico.enums.rh.TipoOperacaoIntegracaoRBPonto;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Audited
@Entity
@Etiqueta("Atualização Dados RBPonto")
public class AtualizacaoDadosRBPonto extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private Long identificador;
    private String requisicao;
    private String resposta;
    private String log;
    @Enumerated(EnumType.STRING)
    private TipoInformacaoEnvioRBPonto tipoInformacao;
    @Enumerated(EnumType.STRING)
    private StatusIntegracaoRBPonto statusIntegracao;
    @Enumerated(EnumType.STRING)
    private TipoOperacaoIntegracaoRBPonto tipoOperacao;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataExecucao;
    @Override
    public Long getId() {
        return id;
    }

    public AtualizacaoDadosRBPonto() {
        this.dataExecucao = new Date();
    }

    public AtualizacaoDadosRBPonto(Long idRbWeb, TipoInformacaoEnvioRBPonto tipoInformacao, TipoOperacaoIntegracaoRBPonto tipoOperacao) {
        this.dataExecucao = new Date();
        this.identificador = idRbWeb;
        this.tipoInformacao = tipoInformacao;
        this.tipoOperacao = tipoOperacao;
    }

    public TipoInformacaoEnvioRBPonto getTipoInformacao() {
        return tipoInformacao;
    }

    public void setTipoInformacaoEnvioRBPonto(TipoInformacaoEnvioRBPonto tipoInformacao) {
        this.tipoInformacao = tipoInformacao;
    }

    public Long getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Long identificador) {
        this.identificador = identificador;
    }

    public String getRequisicao() {
        return requisicao;
    }

    public void setRequisicao(String requisicao) {
        this.requisicao = requisicao;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public void setTipoInformacao(TipoInformacaoEnvioRBPonto tipoInformacao) {
        this.tipoInformacao = tipoInformacao;
    }

    public StatusIntegracaoRBPonto getStatusIntegracao() {
        return statusIntegracao;
    }

    public void setStatusIntegracao(StatusIntegracaoRBPonto statusIntegracao) {
        this.statusIntegracao = statusIntegracao;
    }

    public TipoOperacaoIntegracaoRBPonto getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoIntegracaoRBPonto tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataExecucao() {
        return dataExecucao;
    }

    public void setDataExecucao(Date data) {
        this.dataExecucao = data;
    }
}
