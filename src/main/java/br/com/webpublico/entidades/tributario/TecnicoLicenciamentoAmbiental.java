package br.com.webpublico.entidades.tributario;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
public class TecnicoLicenciamentoAmbiental extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long sequencial;
    @Etiqueta("Pessoa")
    @Obrigatorio
    @ManyToOne
    private UsuarioSistema usuario;
    private Boolean ativo;

    public TecnicoLicenciamentoAmbiental() {
        super();
        this.ativo = Boolean.TRUE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSequencial() {
        return sequencial;
    }

    public void setSequencial(Long sequencial) {
        this.sequencial = sequencial;
    }

    public UsuarioSistema getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioSistema usuario) {
        this.usuario = usuario;
    }

    public String getMensagemSequencial() {
        if (id == null) {
            return "Gerado automaticamente ao salvar.";
        } else {
            return sequencial.toString();
        }
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    @Override
    public String toString() {
        return usuario.toString();
    }
}
