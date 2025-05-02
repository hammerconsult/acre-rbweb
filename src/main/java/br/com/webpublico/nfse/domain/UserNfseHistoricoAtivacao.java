package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.util.DataUtil;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@GrupoDiagrama(nome = "NFSE")
@Entity
@Audited
public class UserNfseHistoricoAtivacao extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private UsuarioWeb usuarioNfse;
    private Boolean tipo;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataHistorico;
    @ManyToOne
    private UsuarioSistema usuarioSistema;

    public UserNfseHistoricoAtivacao() {
        this.dataHistorico = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioWeb getUsuarioNfse() {
        return usuarioNfse;
    }

    public void setUsuarioNfse(UsuarioWeb usuarioNfse) {
        this.usuarioNfse = usuarioNfse;
    }

    public Boolean getTipo() {
        return tipo;
    }

    public void setTipo(Boolean tipo) {
        this.tipo = tipo;
    }

    public Date getDataHistorico() {
        return dataHistorico;
    }

    public void setDataHistorico(Date dataHistorico) {
        this.dataHistorico = dataHistorico;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    @Override
    public String toString() {
        StringBuilder toString = new StringBuilder(DataUtil.getDataFormatada(dataHistorico, "dd/MM/yyyy HH:mm:ss"));
        toString.append(" - ").append(tipo ? "Ativação" : "Inativação");
        toString.append(" - ").append(usuarioSistema.getPessoaFisica().toString());
        return toString.toString();
    }
}
