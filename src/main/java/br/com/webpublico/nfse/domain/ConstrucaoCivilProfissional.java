package br.com.webpublico.nfse.domain;

import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.nfse.domain.dtos.ConstrucaoCivilProfissionalNfseDTO;
import br.com.webpublico.nfse.domain.dtos.NfseEntity;
import br.com.webpublico.nfse.enums.TipoProfissional;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;


/**
 * A ConstrucaoCivil.
 */
@Entity
@Audited
@Table(name = "CONSTRUCAOCIVILPROF")
public class ConstrucaoCivilProfissional extends SuperEntidade implements Serializable, NfseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ConstrucaoCivil construcaoCivil;

    @ManyToOne
    private Pessoa profissional;

    @Enumerated(EnumType.STRING)
    private TipoProfissional tipoProfissional;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConstrucaoCivil getConstrucaoCivil() {
        return construcaoCivil;
    }

    public void setConstrucaoCivil(ConstrucaoCivil construcaoCivil) {
        this.construcaoCivil = construcaoCivil;
    }

    public Pessoa getProfissional() {
        return profissional;
    }

    public void setProfissional(Pessoa profissional) {
        this.profissional = profissional;
    }

    public TipoProfissional getTipoProfissional() {
        return tipoProfissional;
    }

    public void setTipoProfissional(TipoProfissional tipoProfissional) {
        this.tipoProfissional = tipoProfissional;
    }

    @Override
    public ConstrucaoCivilProfissionalNfseDTO toNfseDto() {
        ConstrucaoCivilProfissionalNfseDTO dto = new ConstrucaoCivilProfissionalNfseDTO();
        dto.setId(id);
        dto.setProfissional(profissional.toNfseDto());
        dto.setTipoProfissional(tipoProfissional.getDto());
        return dto;
    }
}
