package br.com.webpublico.entidades.contabil;

import br.com.webpublico.enums.TipoBalancete;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
public class MovimentoContabil  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOperacao;
    private String classeOrigem;
    private String idOrigem;
    private String operacao;
    @Enumerated(EnumType.STRING)
    private SituacaoMovimentoContabil situacao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getClasseOrigem() {
        return classeOrigem;
    }

    public void setClasseOrigem(String classeOrigem) {
        this.classeOrigem = classeOrigem;
    }

    public String getIdOrigem() {
        return idOrigem;
    }

    public void setIdOrigem(String idOrigem) {
        this.idOrigem = idOrigem;
    }

    public SituacaoMovimentoContabil getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoMovimentoContabil situacao) {
        this.situacao = situacao;
    }

    public String getOperacao() {
        return operacao;
    }

    public void setOperacao(String operacao) {
        this.operacao = operacao;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public enum SituacaoMovimentoContabil {
        PROCESSADO,
        NAO_PROCESSADO;
    }
}
