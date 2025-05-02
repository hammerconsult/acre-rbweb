package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;

@Entity
@Audited
public class ParametroETR extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToMany(mappedBy = "parametroETR", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametroETRFormulario> parametroETRFormularioList;
    @Etiqueta("CNAE")
    @Obrigatorio
    @ManyToOne
    private CNAE cnae;
    @Etiqueta("Validade (Em anos)")
    @Obrigatorio
    private Integer validade;
    @ManyToOne
    @JoinColumn(name = "tipo_docto_oficial_alvara_id")
    private TipoDoctoOficial tipoDoctoOficialAlvara;
    @ManyToOne
    @JoinColumn(name = "tipo_docto_oficial_dispensa_id")
    private TipoDoctoOficial tipoDoctoOficialDispensa;
    private String email;
    @OneToMany(mappedBy = "parametroETR", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ParametroETRAceite> aceiteList;

    public ParametroETR() {
        super();
        parametroETRFormularioList = Lists.newArrayList();
        aceiteList = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ParametroETRFormulario> getParametroETRFormularioList() {
        if (parametroETRFormularioList != null) {
            Collections.sort(parametroETRFormularioList);
        }
        return parametroETRFormularioList;
    }

    public void setParametroETRFormularioList(List<ParametroETRFormulario> parametroETRFormularioList) {
        this.parametroETRFormularioList = parametroETRFormularioList;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }

    public Integer getValidade() {
        return validade;
    }

    public void setValidade(Integer validade) {
        this.validade = validade;
    }

    public TipoDoctoOficial getTipoDoctoOficialAlvara() {
        return tipoDoctoOficialAlvara;
    }

    public void setTipoDoctoOficialAlvara(TipoDoctoOficial tipoDoctoOficialAlvara) {
        this.tipoDoctoOficialAlvara = tipoDoctoOficialAlvara;
    }

    public TipoDoctoOficial getTipoDoctoOficialDispensa() {
        return tipoDoctoOficialDispensa;
    }

    public void setTipoDoctoOficialDispensa(TipoDoctoOficial tipoDoctoOficialDispensa) {
        this.tipoDoctoOficialDispensa = tipoDoctoOficialDispensa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<ParametroETRAceite> getAceiteList() {
        return aceiteList;
    }

    public void setAceiteList(List<ParametroETRAceite> aceiteList) {
        this.aceiteList = aceiteList;
    }

    public boolean hasUnidadeAdministrativa(UnidadeOrganizacional unidadeOrganizacional) {
        if (aceiteList != null) {
            for (ParametroETRAceite parametroETRAceite : aceiteList) {
                if (parametroETRAceite.getUnidadeOrganizacional().getId().equals(unidadeOrganizacional.getId()))
                    return true;
            }
        }
        return false;
    }
}
