package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
@Etiqueta("Histórico de Situações do DAM")
public class HistoricoSituacaoDAM extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação DAM")
    private DAM.Situacao situacaoDAM;
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data Situação")
    private Date dataSituacao;
    @ManyToOne
    @Etiqueta("Usuário Situação")
    private UsuarioSistema usuarioSituacao;
    @ManyToOne
    @Etiqueta("DAM")
    private DAM dam;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DAM.Situacao getSituacaoDAM() {
        return situacaoDAM;
    }

    public void setSituacaoDAM(DAM.Situacao situacaoDAM) {
        this.situacaoDAM = situacaoDAM;
    }

    public Date getDataSituacao() {
        return dataSituacao;
    }

    public void setDataSituacao(Date dataSituacao) {
        this.dataSituacao = dataSituacao;
    }

    public UsuarioSistema getUsuarioSituacao() {
        return usuarioSituacao;
    }

    public void setUsuarioSituacao(UsuarioSistema usuarioSituacao) {
        this.usuarioSituacao = usuarioSituacao;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }
}
