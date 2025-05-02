package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;

@Entity
@Audited
public class RequerimentoLicenciamentoETRAceite extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private RequerimentoLicenciamentoETR requerimento;
    @ManyToOne
    private UnidadeOrganizacional unidadeAceite;
    @ManyToOne
    private UsuarioSistema usuarioAceite;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataAceite;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RequerimentoLicenciamentoETR getRequerimento() {
        return requerimento;
    }

    public void setRequerimento(RequerimentoLicenciamentoETR requerimento) {
        this.requerimento = requerimento;
    }

    public UnidadeOrganizacional getUnidadeAceite() {
        return unidadeAceite;
    }

    public void setUnidadeAceite(UnidadeOrganizacional unidadeAceite) {
        this.unidadeAceite = unidadeAceite;
    }

    public UsuarioSistema getUsuarioAceite() {
        return usuarioAceite;
    }

    public void setUsuarioAceite(UsuarioSistema usuarioAceite) {
        this.usuarioAceite = usuarioAceite;
    }

    public Date getDataAceite() {
        return dataAceite;
    }

    public void setDataAceite(Date dataAceite) {
        this.dataAceite = dataAceite;
    }
}
