package br.com.webpublico.entidades.contabil.conciliacaocontabil;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.NaturezaTipoGrupoMaterial;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Configuração de Conciliação Contábil - Natureza do Tipo de Grupo de Material")
@Table(name = "CONFIGCONCCONTABILNATTPGM")
public class ConfigConciliacaoContabilNaturezaTipoGrupoMaterial extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfigConciliacaoContabil configConciliacaoContabil;
    @Enumerated(EnumType.STRING)
    private NaturezaTipoGrupoMaterial naturezaTipoGrupoMaterial;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfigConciliacaoContabil getConfigConciliacaoContabil() {
        return configConciliacaoContabil;
    }

    public void setConfigConciliacaoContabil(ConfigConciliacaoContabil configConciliacaoContabil) {
        this.configConciliacaoContabil = configConciliacaoContabil;
    }

    public NaturezaTipoGrupoMaterial getNaturezaTipoGrupoMaterial() {
        return naturezaTipoGrupoMaterial;
    }

    public void setNaturezaTipoGrupoMaterial(NaturezaTipoGrupoMaterial naturezaTipoGrupoMaterial) {
        this.naturezaTipoGrupoMaterial = naturezaTipoGrupoMaterial;
    }
}
