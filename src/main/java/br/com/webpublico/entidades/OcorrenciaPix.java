package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.OcorrenciasPixDTO;

import javax.persistence.*;
import java.util.Date;

@Entity
public class OcorrenciaPix extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long dam_id;

    private String codigo;
    private String versao;
    private String mensagem;
    private String ocorrencia;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dataOcorrencia;

    public OcorrenciaPix() {
    }

    public OcorrenciaPix(OcorrenciasPixDTO ocorrencia) {
        this.codigo = ocorrencia.getCodigo();
        this.versao = ocorrencia.getVersao();
        this.mensagem = ocorrencia.getMensagem();
        this.ocorrencia = ocorrencia.getOcorrencia();
        this.dataOcorrencia = new Date();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDam_id() {
        return dam_id;
    }

    public void setDam_id(Long dam_id) {
        this.dam_id = dam_id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String versao) {
        this.versao = versao;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getOcorrencia() {
        return ocorrencia;
    }

    public void setOcorrencia(String ocorrencia) {
        this.ocorrencia = ocorrencia;
    }

    public Date getDataOcorrencia() {
        return dataOcorrencia;
    }

    public void setDataOcorrencia(Date dataOcorrencia) {
        this.dataOcorrencia = dataOcorrencia;
    }
}
