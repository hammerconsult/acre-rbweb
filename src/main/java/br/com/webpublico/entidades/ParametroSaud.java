package br.com.webpublico.entidades;

import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class ParametroSaud extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @ManyToOne
    private UsuarioSistema usuarioRegistro;
    @OneToMany(mappedBy = "parametroSaud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametroSaudDocumento> documentos;

    public ParametroSaud() {
        super();
        this.dataRegistro = new Date();
        this.documentos = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UsuarioSistema getUsuarioRegistro() {
        return usuarioRegistro;
    }

    public void setUsuarioRegistro(UsuarioSistema usuarioRegistro) {
        this.usuarioRegistro = usuarioRegistro;
    }

    public List<ParametroSaudDocumento> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<ParametroSaudDocumento> documentos) {
        this.documentos = documentos;
    }
}
