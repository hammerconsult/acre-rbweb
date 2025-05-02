package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Audited
public class LotacaoVistoriadoraConfigTribRendas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private LotacaoVistoriadora lotacaoVistoriadora;
    @ManyToOne
    private ConfiguracaoTributario configuracaoTributario;

    public LotacaoVistoriadoraConfigTribRendas() {
    }

    public LotacaoVistoriadoraConfigTribRendas(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LotacaoVistoriadora getLotacaoVistoriadora() {
        return lotacaoVistoriadora;
    }

    public void setLotacaoVistoriadora(LotacaoVistoriadora lotacaoVistoriadora) {
        this.lotacaoVistoriadora = lotacaoVistoriadora;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }
}
