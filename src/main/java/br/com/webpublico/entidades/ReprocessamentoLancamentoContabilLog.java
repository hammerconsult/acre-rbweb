package br.com.webpublico.entidades;

import br.com.webpublico.entidades.EventoContabil;
import br.com.webpublico.entidades.ReprocessamentoContabilHistorico;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 30/12/13
 * Time: 11:23
 * To change this template use File | Settings | File Templates.
 */

@Audited
@Entity
@Etiqueta("Reprocessamento Contábil Log")
@Table(name = "REPROCESSCONTABILLOG")
public class ReprocessamentoLancamentoContabilLog implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String mensagem;
    private String erro;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @ManyToOne
    private EventoContabil eventoContabil;
    @ManyToOne
    private ReprocessamentoContabilHistorico reprocesscontabilhistorico;
    private Boolean logDeErro;
    private String classeObjeto;
    private Long idObjeto;
    @Transient
    private Long criadoEm;


    public ReprocessamentoLancamentoContabilLog() {
        criadoEm = System.nanoTime();
    }

    public ReprocessamentoLancamentoContabilLog(Date data, String mensagem, String erro, EventoContabil eventoContabil, Boolean logDeErro, ReprocessamentoContabilHistorico reprocessamentoContabilHistorico, Long idObjeto, String classeOIbjeto) {
        this.mensagem = mensagem;
        this.erro = erro;
        this.eventoContabil = eventoContabil;
        this.logDeErro = logDeErro;
        this.data = data;
        this.reprocesscontabilhistorico = reprocessamentoContabilHistorico;
        this.idObjeto = idObjeto;
        this.classeObjeto = classeOIbjeto;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public Boolean getLogDeErro() {
        return logDeErro;
    }

    public void setLogDeErro(Boolean logDeErro) {
        this.logDeErro = logDeErro;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    private String getDataFormatadaDiaHora() {
        return "<b>" + DataUtil.getDataFormatadaDiaHora(data) + " - </b> ";
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReprocessamentoContabilHistorico getReprocessamentoContabilHistorico() {
        return reprocesscontabilhistorico;
    }

    public void setReprocessamentoContabilHistorico(ReprocessamentoContabilHistorico reprocessamentoContabilHistorico) {
        this.reprocesscontabilhistorico = reprocessamentoContabilHistorico;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ReprocessamentoContabilHistorico getReprocesscontabilhistorico() {
        return reprocesscontabilhistorico;
    }

    public void setReprocesscontabilhistorico(ReprocessamentoContabilHistorico reprocesscontabilhistorico) {
        this.reprocesscontabilhistorico = reprocesscontabilhistorico;
    }

    public String getClasseObjeto() {
        return classeObjeto;
    }

    public void setClasseObjeto(String classeObjeto) {
        this.classeObjeto = classeObjeto;
    }

    public Long getIdObjeto() {
        return idObjeto;
    }

    public void setIdObjeto(Long idObjeto) {
        this.idObjeto = idObjeto;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        return getLinhaLog();
    }

    public String getLinhaLog() {
        String br = "</br>";
        if (this.getLogDeErro()) {
            return getDataFormatadaDiaHora() + mensagem + erro + br;
        }
        return getDataFormatadaDiaHora() + mensagem + br;
    }
}
