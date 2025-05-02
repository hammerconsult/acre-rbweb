package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.ValidadorVigencia;
import br.com.webpublico.tributario.consultadebitos.dtos.*;
import br.com.webpublico.util.IdentidadeDaEntidade;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author Fabio
 */
@Entity

@Audited
public class MultaConfiguracaoAcrescimo implements Serializable, ValidadorVigencia, EntidadeConsultaDebitos {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfiguracaoAcrescimos configuracaoAcrescimos;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date inicioVigencia;
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date fimVigencia;
    private Boolean somarMultaOriginal;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "multa", orphanRemoval = true)
    private List<IncidenciaAcrescimoMulta> incidencias;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "multa")
    private List<ItemConfiguracaoAcrescimos> diasAtraso;
    @Transient
    private Long criadoEm;

    public MultaConfiguracaoAcrescimo() {
        diasAtraso = Lists.newArrayList();
        incidencias = Lists.newArrayList();
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoAcrescimos getConfiguracaoAcrescimos() {
        return configuracaoAcrescimos;
    }

    public void setConfiguracaoAcrescimos(ConfiguracaoAcrescimos configuracaoAcrescimos) {
        this.configuracaoAcrescimos = configuracaoAcrescimos;
    }

    @Override
    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    @Override
    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public Date getFimVigencia() {
        return fimVigencia;
    }

    @Override
    public void setFimVigencia(Date fimVigencia) {
        this.fimVigencia = fimVigencia;
    }

    public Date getFimVigenciaNotNull() {
        if (fimVigencia != null) {
            return fimVigencia;
        }
        return new Date();
    }

    public Boolean getSomarMultaOriginal() {
        return somarMultaOriginal != null ? somarMultaOriginal : false;
    }

    public void setSomarMultaOriginal(Boolean somarMultaOriginal) {
        this.somarMultaOriginal = somarMultaOriginal;
    }

    public List<IncidenciaAcrescimoMulta> getIncidencias() {
        return incidencias;
    }

    public void setIncidencias(List<IncidenciaAcrescimoMulta> incidencias) {
        this.incidencias = incidencias;
    }

    public List<ItemConfiguracaoAcrescimos> getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(List<ItemConfiguracaoAcrescimos> diasAtraso) {
        this.diasAtraso = diasAtraso;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public MultaConfiguracaoAcrescimoDTO toDto() {
        MultaConfiguracaoAcrescimoDTO dto = new MultaConfiguracaoAcrescimoDTO();
        dto.setId(getId());
        dto.setSomarMultaOriginal(getSomarMultaOriginal());
        dto.setInicioVigencia(getInicioVigencia());
        dto.setFimVigencia(getFimVigenciaNotNull());
        dto.setIncidencias(Lists.<IncidenciaAcrescimoMultaDTO>newArrayList());
        for (IncidenciaAcrescimoMulta incidencia : incidencias) {
            IncidenciaAcrescimoMultaDTO incidenciaDTO = new IncidenciaAcrescimoMultaDTO();
            incidenciaDTO.setId(incidencia.getId());
            incidenciaDTO.setIncidencia(incidencia.getIncidencia().toDto());
            dto.getIncidencias().add(incidenciaDTO);
        }
        dto.setDiasAtraso(Lists.<ItemConfiguracaoAcrescimosDTO>newArrayList());
        for (ItemConfiguracaoAcrescimos itemConfiguracaoAcrescimos : diasAtraso) {
            ItemConfiguracaoAcrescimosDTO itemDTO = new ItemConfiguracaoAcrescimosDTO();
            itemDTO.setValor(itemConfiguracaoAcrescimos.getValor());
            itemDTO.setQntDias(itemConfiguracaoAcrescimos.getQntDias());
            itemDTO.setId(itemConfiguracaoAcrescimos.getId());
            dto.getDiasAtraso().add(itemDTO);
        }
        return dto;
    }
}
