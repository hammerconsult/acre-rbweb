package br.com.webpublico.entidades.contabil.conciliacaocontabil;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Configuração de Conciliação Contábil - Categoria Orçamentária")
@Table(name = "CONFIGCONCCONTABILCATEGORC")
public class ConfigConciliacaoContabilCategoriaOrcamentaria extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfigConciliacaoContabil configConciliacaoContabil;
    @Enumerated(EnumType.STRING)
    private CategoriaOrcamentaria categoriaOrcamentaria;

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

    public CategoriaOrcamentaria getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(CategoriaOrcamentaria categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }
}
