package br.com.webpublico.entidades.rh.portal.atualizacaocadastral;

import br.com.webpublico.entidades.TipoDependente;
import br.com.webpublico.pessoa.dto.DependenteVinculoFPDTO;
import br.com.webpublico.pessoa.enumeration.TipoAlteracaoPortal;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by mga on 21/06/19.
 */
@Entity
public class DependenteVinculoPortal implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início da Vigência")
    private Date inicioVigencia;

    @Temporal(TemporalType.DATE)
    @Etiqueta("Fim da Vigência")
    private Date finalVigencia;

    @ManyToOne
    private DependentePortal dependentePortal;

    @Etiqueta("Tipo de Dependente")
    @ManyToOne
    private TipoDependente tipoDependente;

    @Enumerated(EnumType.STRING)
    private TipoAlteracaoPortal tipoAlteracaoPortal;

    public DependenteVinculoPortal() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public DependentePortal getDependentePortal() {
        return dependentePortal;
    }

    public void setDependentePortal(DependentePortal dependentePortal) {
        this.dependentePortal = dependentePortal;
    }

    public TipoDependente getTipoDependente() {
        return tipoDependente;
    }

    public void setTipoDependente(TipoDependente tipoDependente) {
        this.tipoDependente = tipoDependente;
    }

    public static DependenteVinculoPortal dtoToDependenteVinculoPortal(DependenteVinculoFPDTO dto) {
        DependenteVinculoPortal dep = new DependenteVinculoPortal();
        dep.setId(dto.getId());
        dep.setInicioVigencia(dto.getInicioVigencia());
        dep.setFinalVigencia(dto.getFinalVigencia());
        dep.setTipoAlteracaoPortal(dto.getTipoAlteracaoPortal());
        return dep;
    }

    public TipoAlteracaoPortal getTipoAlteracaoPortal() {
        return tipoAlteracaoPortal;
    }

    public void setTipoAlteracaoPortal(TipoAlteracaoPortal tipoAlteracaoPortal) {
        this.tipoAlteracaoPortal = tipoAlteracaoPortal;
    }
}
