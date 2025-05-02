package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Gustavo
 */
@Entity
@Audited
public class ExceptionLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Tabelavel
    @Pesquisavel
    private Long id;
    @Tabelavel
    @Pesquisavel
    private String tipoException;

    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    private Date dataRegistro;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private UsuarioSistema usuarioLogado;
    @Tabelavel
    @Pesquisavel
    private String ip;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    private UnidadeOrganizacional unidadeOrganizacionalLogada;
    @Tabelavel
    @Pesquisavel
    private String causedBy;
    @Tabelavel
    @Pesquisavel
    private String pagina;
    private String stackTrace;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoException() {
        return tipoException;
    }

    public void setTipoException(String tipoException) {
        this.tipoException = tipoException;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public UsuarioSistema getUsuarioLogado() {
        return usuarioLogado;
    }

    public void setUsuarioLogado(UsuarioSistema usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalLogada() {
        return unidadeOrganizacionalLogada;
    }

    public void setUnidadeOrganizacionalLogada(UnidadeOrganizacional unidadeOrganizacionalLogada) {
        this.unidadeOrganizacionalLogada = unidadeOrganizacionalLogada;
    }

    public String getCausedBy() {
        return causedBy;
    }

    public void setCausedBy(String causedBy) {
        this.causedBy = causedBy;
    }

    public String getPagina() {
        return pagina;
    }

    public void setPagina(String pagina) {
        this.pagina = pagina;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ExceptionLog)) {
            return false;
        }
        ExceptionLog other = (ExceptionLog) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return "ExceptionLog{" +
            "id=" + id +
            ", tipoException='" + tipoException + '\'' +
            ", dataRegistro=" + dataRegistro +
            ", usuarioLogado=" + usuarioLogado +
            ", ip='" + ip + '\'' +
            ", unidadeOrganizacionalLogada=" + unidadeOrganizacionalLogada +
            ", causedBy='" + causedBy + '\'' +
            ", pagina='" + pagina + '\'' +
            ", stackTrace='" + stackTrace + '\'' +
            '}';
    }
}
