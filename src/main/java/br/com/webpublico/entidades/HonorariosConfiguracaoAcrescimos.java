package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.tributario.consultadebitos.dtos.DTOConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.dtos.EntidadeConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.dtos.HonorariosConfiguracaoAcrescimosDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Audited
@Table(name = "HONORARIOSCONFIGACRESCIMOS")
public class HonorariosConfiguracaoAcrescimos implements Serializable, Comparable, ValidadorVigencia, EntidadeConsultaDebitos {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private BigDecimal honorariosAdvocaticios;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    @ManyToOne
    private ConfiguracaoAcrescimos configuracaoAcrescimos;
    @Transient
    private Long criadoEm;

    public HonorariosConfiguracaoAcrescimos() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getHonorariosAdvocaticios() {
        return honorariosAdvocaticios;
    }

    public void setHonorariosAdvocaticios(BigDecimal honorariosAdvocaticios) {
        this.honorariosAdvocaticios = honorariosAdvocaticios;
    }

    public ConfiguracaoAcrescimos getConfiguracaoAcrescimos() {
        return configuracaoAcrescimos;
    }

    public void setConfiguracaoAcrescimos(ConfiguracaoAcrescimos configuracaoAcrescimos) {
        this.configuracaoAcrescimos = configuracaoAcrescimos;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HonorariosConfiguracaoAcrescimos other = (HonorariosConfiguracaoAcrescimos) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        if (this.honorariosAdvocaticios != other.honorariosAdvocaticios && (this.honorariosAdvocaticios == null || !this.honorariosAdvocaticios.equals(other.honorariosAdvocaticios))) {
            return false;
        }
        if (this.configuracaoAcrescimos != other.configuracaoAcrescimos && (this.configuracaoAcrescimos == null || !this.configuracaoAcrescimos.equals(other.configuracaoAcrescimos))) {
            return false;
        }
        if (this.criadoEm != other.criadoEm && (this.criadoEm == null || !this.criadoEm.equals(other.criadoEm))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object o) {
        return honorariosAdvocaticios.compareTo(((HonorariosConfiguracaoAcrescimos) o).getHonorariosAdvocaticios());
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return fimVigencia;
    }

    public Date getFimVigenciaNotNull() {
        if (fimVigencia != null) {
            return fimVigencia;
        }
        return new Date();
    }

    @Override
    public void setInicioVigencia(Date data) {
        this.inicioVigencia = data;
    }

    @Override
    public void setFimVigencia(Date data) {
        this.fimVigencia = data;
    }

    @Override
    public HonorariosConfiguracaoAcrescimosDTO toDto() {
        HonorariosConfiguracaoAcrescimosDTO dto = new HonorariosConfiguracaoAcrescimosDTO();
        dto.setId(getId());
        dto.setHonorariosAdvocaticios(getHonorariosAdvocaticios());
        dto.setInicioVigencia(getInicioVigencia());
        dto.setFimVigencia(getFimVigencia());
        return dto;


    }
}
