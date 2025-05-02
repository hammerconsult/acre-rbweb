package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusEmenda;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Etiqueta("Emenda")
@Audited
public class HistoricoEmenda extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Emenda emenda;
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAlteracao;
    @Enumerated(EnumType.STRING)
    private StatusEmenda statusAnteriorCamara;
    @Enumerated(EnumType.STRING)
    private StatusEmenda statusAnteriorPrefeitura;
    @Enumerated(EnumType.STRING)
    private StatusEmenda statusNovoCamara;
    @Enumerated(EnumType.STRING)
    private StatusEmenda statusNovoPrefeitura;
    @Etiqueta("Motivo da Rejeição pela Prefeitura")
    private String motivoRejeicaoPrefeitura;
    @Etiqueta("Motivo da Rejeição pela Câmara")
    private String motivoRejeicaoCamara;

    public HistoricoEmenda() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Emenda getEmenda() {
        return emenda;
    }

    public void setEmenda(Emenda emenda) {
        this.emenda = emenda;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public Date getDataAlteracao() {
        return dataAlteracao;
    }

    public void setDataAlteracao(Date dataAlteracao) {
        this.dataAlteracao = dataAlteracao;
    }

    public StatusEmenda getStatusAnteriorCamara() {
        return statusAnteriorCamara;
    }

    public void setStatusAnteriorCamara(StatusEmenda statusAnteriorCamara) {
        this.statusAnteriorCamara = statusAnteriorCamara;
    }

    public StatusEmenda getStatusAnteriorPrefeitura() {
        return statusAnteriorPrefeitura;
    }

    public void setStatusAnteriorPrefeitura(StatusEmenda statusAnteriorPrefeitura) {
        this.statusAnteriorPrefeitura = statusAnteriorPrefeitura;
    }

    public StatusEmenda getStatusNovoCamara() {
        return statusNovoCamara;
    }

    public void setStatusNovoCamara(StatusEmenda statusNovoCamara) {
        this.statusNovoCamara = statusNovoCamara;
    }

    public StatusEmenda getStatusNovoPrefeitura() {
        return statusNovoPrefeitura;
    }

    public void setStatusNovoPrefeitura(StatusEmenda statusNovoPrefeitura) {
        this.statusNovoPrefeitura = statusNovoPrefeitura;
    }

    public String getMotivoRejeicaoPrefeitura() {
        return motivoRejeicaoPrefeitura;
    }

    public void setMotivoRejeicaoPrefeitura(String motivoRejeicaoPrefeitura) {
        this.motivoRejeicaoPrefeitura = motivoRejeicaoPrefeitura;
    }

    public String getMotivoRejeicaoCamara() {
        return motivoRejeicaoCamara;
    }

    public void setMotivoRejeicaoCamara(String motivoRejeicaoCamara) {
        this.motivoRejeicaoCamara = motivoRejeicaoCamara;
    }

    public String getMotivoRejeicao() {
        if (StatusEmenda.REJEITADO.equals(statusNovoPrefeitura)) {
            return motivoRejeicaoPrefeitura;
        } else if (StatusEmenda.REJEITADO.equals(statusNovoCamara)) {
            return motivoRejeicaoCamara;
        }
        return "";
    }
}
