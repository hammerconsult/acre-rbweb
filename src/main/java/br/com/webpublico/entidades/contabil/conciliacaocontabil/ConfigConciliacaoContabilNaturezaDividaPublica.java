package br.com.webpublico.entidades.contabil.conciliacaocontabil;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Etiqueta("Configuração de Conciliação Contábil - Natureza da Dívida Pública")
@Table(name = "CONFIGCONCCONTABILNATDIVPB")
public class ConfigConciliacaoContabilNaturezaDividaPublica extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConfigConciliacaoContabil configConciliacaoContabil;
    @Enumerated(EnumType.STRING)
    private NaturezaDividaPublica naturezaDividaPublica;

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

    public NaturezaDividaPublica getNaturezaDividaPublica() {
        return naturezaDividaPublica;
    }

    public void setNaturezaDividaPublica(NaturezaDividaPublica naturezaDividaPublica) {
        this.naturezaDividaPublica = naturezaDividaPublica;
    }
}
