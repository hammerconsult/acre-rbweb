package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoCalculoISS;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Audited
public class CalculoISSEstorno implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Tipo do Cálculo")
    private TipoCalculoISS tipoCalculoISS;
    @Temporal(TemporalType.DATE)
    private Date dataEstorno;
    private String motivoEstorno;
    @ManyToOne
    private UsuarioSistema usuarioEstorno;
    @OneToMany(mappedBy = "calculoISSEstorno", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CalculoISS> calculos;

    public CalculoISSEstorno() {
        this.dataEstorno = new Date();
        this.calculos = Lists.newArrayList();
    }

    public List<CalculoISS> getCalculos() {
        return calculos;
    }

    public void setCalculos(List<CalculoISS> calculos) {
        this.calculos = calculos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public UsuarioSistema getUsuarioEstorno() {
        return usuarioEstorno;
    }

    public void setUsuarioEstorno(UsuarioSistema usuarioEstorno) {
        this.usuarioEstorno = usuarioEstorno;
    }

    public TipoCalculoISS getTipoCalculoISS() {
        return tipoCalculoISS;
    }

    public void setTipoCalculoISS(TipoCalculoISS tipoCalculoISS) {
        this.tipoCalculoISS = tipoCalculoISS;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CalculoISSEstorno)) {
            return false;
        }
        CalculoISSEstorno other = (CalculoISSEstorno) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.CalculoISSEstorno[ id=" + id + " ]";
    }
}
