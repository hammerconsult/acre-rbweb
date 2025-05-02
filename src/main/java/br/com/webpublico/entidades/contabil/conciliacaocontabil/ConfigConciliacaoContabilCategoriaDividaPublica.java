package br.com.webpublico.entidades.contabil.conciliacaocontabil;

import br.com.webpublico.entidades.CategoriaDividaPublica;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Configuração de Conciliação Contábil - Categoria da Dívida Pública")
@Table(name = "CONFIGCONCCONTABILCATDIVPB")
public class ConfigConciliacaoContabilCategoriaDividaPublica extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfigConciliacaoContabil configConciliacaoContabil;
    @ManyToOne
    private CategoriaDividaPublica categoriaDividaPublica;

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

    public CategoriaDividaPublica getCategoriaDividaPublica() {
        return categoriaDividaPublica;
    }

    public void setCategoriaDividaPublica(CategoriaDividaPublica categoriaDividaPublica) {
        this.categoriaDividaPublica = categoriaDividaPublica;
    }
}
