package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Audited
public class PermissaoUsoZoneamento extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;
    @Obrigatorio
    @Etiqueta("Zona")
    @ManyToOne
    private Zona zona;
    @ManyToOne
    private ClassificacaoUso classificacaoUso;
    @ManyToOne
    private ClassificacaoUsoItem classificacaoUsoItem;
    @ManyToOne
    private HierarquiaVia hierarquiaVia;
    private Boolean permitido;

    public PermissaoUsoZoneamento() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Zona getZona() {
        return zona;
    }

    public void setZona(Zona zona) {
        this.zona = zona;
    }

    public ClassificacaoUso getClassificacaoUso() {
        return classificacaoUso;
    }

    public void setClassificacaoUso(ClassificacaoUso classificacaoUso) {
        this.classificacaoUso = classificacaoUso;
    }

    public ClassificacaoUsoItem getClassificacaoUsoItem() {
        return classificacaoUsoItem;
    }

    public void setClassificacaoUsoItem(ClassificacaoUsoItem classificacaoUsoItem) {
        this.classificacaoUsoItem = classificacaoUsoItem;
    }

    public HierarquiaVia getHierarquiaVia() {
        return hierarquiaVia;
    }

    public void setHierarquiaVia(HierarquiaVia hierarquiaVia) {
        this.hierarquiaVia = hierarquiaVia;
    }

    public Boolean getPermitido() {
        return permitido != null ? permitido : Boolean.FALSE;
    }

    public void setPermitido(Boolean permitido) {
        this.permitido = permitido;
    }
}
