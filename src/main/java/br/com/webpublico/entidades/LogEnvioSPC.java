package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class LogEnvioSPC extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private ItemProcessoCobrancaSPC itemProcessoCobrancaSPC;
    @Temporal(TemporalType.DATE)
    private Date dataRegistro;
    @Enumerated(EnumType.STRING)
    private TipoLog tipoLog;
    private String log;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ItemProcessoCobrancaSPC getItemProcessoCobrancaSPC() {
        return itemProcessoCobrancaSPC;
    }

    public void setItemProcessoCobrancaSPC(ItemProcessoCobrancaSPC itemProcessoCobrancaSPC) {
        this.itemProcessoCobrancaSPC = itemProcessoCobrancaSPC;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public TipoLog getTipoLog() {
        return tipoLog;
    }

    public void setTipoLog(TipoLog tipoLog) {
        this.tipoLog = tipoLog;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public enum TipoLog {
        INCLUSAO("Inclusão"), EXCLUSAO("Exclusão");

        private final String descricao;


        TipoLog(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
