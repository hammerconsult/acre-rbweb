package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoIncidenciaAcrescimo;
import br.com.webpublico.tributario.consultadebitos.dtos.DTOConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.dtos.EntidadeConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.dtos.IncidenciaAcrescimoDTO;
import br.com.webpublico.tributario.consultadebitos.enums.EnumConsultaDebitos;
import br.com.webpublico.tributario.consultadebitos.enums.TipoAcrescimoDTO;
import br.com.webpublico.tributario.consultadebitos.enums.TipoIncidenciaAcrescimoDTO;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class IncidenciaAcrescimo implements Serializable, EntidadeConsultaDebitos {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoIncidenciaAcrescimo incidencia;
    @Enumerated(EnumType.STRING)
    private TipoAcrescimo tipoAcrescimo;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private ConfiguracaoAcrescimos configuracaoAcrescimos;

    public IncidenciaAcrescimo() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoIncidenciaAcrescimo getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(TipoIncidenciaAcrescimo incidencia) {
        this.incidencia = incidencia;
    }

    public TipoAcrescimo getTipoAcrescimo() {
        return tipoAcrescimo;
    }

    public void setTipoAcrescimo(TipoAcrescimo tipoAcrescimo) {
        this.tipoAcrescimo = tipoAcrescimo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public ConfiguracaoAcrescimos getConfiguracaoAcrescimos() {
        return configuracaoAcrescimos;
    }

    public void setConfiguracaoAcrescimos(ConfiguracaoAcrescimos configuracaoAcrescimos) {
        this.configuracaoAcrescimos = configuracaoAcrescimos;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public IncidenciaAcrescimoDTO toDto() {
        IncidenciaAcrescimoDTO dto = new IncidenciaAcrescimoDTO();
        dto.setId(getId());
        dto.setIncidencia(getIncidencia().toDto());
        dto.setTipoAcrescimo(getTipoAcrescimo().toDto());
        return dto;
    }

    public enum TipoAcrescimo implements EnumConsultaDebitos {
        JUROS(TipoAcrescimoDTO.JUROS), MULTA(TipoAcrescimoDTO.MULTA), CORRECAO(TipoAcrescimoDTO.CORRECAO);

        private TipoAcrescimoDTO dto;

        TipoAcrescimo(TipoAcrescimoDTO dto) {
            this.dto = dto;
        }

        @Override
        public TipoAcrescimoDTO toDto() {
            return dto;
        }
    }
}
