package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.rh.administracaopagamento.TipoVinculoAnaliseConformidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Table(name = "VINCULOANALISECONFFP")
public class VinculoAnaliseConformidadeFP extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AnaliseConformidadeFP analiseConformidadeFP;
    @ManyToOne
    private VinculoFP vinculoFP;
    @Enumerated(EnumType.STRING)
    private TipoVinculoAnaliseConformidade tipoVinculo;

    @ManyToOne
    private UsuarioSistema responsavelAuditoria;

    public VinculoAnaliseConformidadeFP() {
    }

    public VinculoAnaliseConformidadeFP(AnaliseConformidadeFP analiseConformidadeFP, VinculoFP vinculoFP, TipoVinculoAnaliseConformidade tipoVinculo) {
        this.analiseConformidadeFP = analiseConformidadeFP;
        this.vinculoFP = vinculoFP;
        this.tipoVinculo = tipoVinculo;
    }

    public AnaliseConformidadeFP getAnaliseConformidadeFP() {
        return analiseConformidadeFP;
    }

    public void setAnaliseConformidadeFP(AnaliseConformidadeFP analiseConformidadeFP) {
        this.analiseConformidadeFP = analiseConformidadeFP;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public TipoVinculoAnaliseConformidade getTipoVinculo() {
        return tipoVinculo;
    }

    public void setTipoVinculo(TipoVinculoAnaliseConformidade tipoVinculo) {
        this.tipoVinculo = tipoVinculo;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VinculoAnaliseConformidadeFP vinculoAnaliseConformidadeFP = (VinculoAnaliseConformidadeFP) o;
        return tipoVinculo.equals(vinculoAnaliseConformidadeFP.tipoVinculo) && vinculoFP.equals(vinculoAnaliseConformidadeFP.vinculoFP)
            && analiseConformidadeFP.equals(vinculoAnaliseConformidadeFP.analiseConformidadeFP);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tipoVinculo, vinculoFP, analiseConformidadeFP);
    }

    public UsuarioSistema getresponsavelAuditoria() {
        return responsavelAuditoria;
    }

    public void setresponsavelAuditoria(UsuarioSistema responsavelAuditoria_id) {
        this.responsavelAuditoria = responsavelAuditoria_id;
    }
}
