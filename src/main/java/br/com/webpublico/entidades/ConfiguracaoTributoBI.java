package br.com.webpublico.entidades;

import br.com.webpublico.enums.tributario.TipoTributoBI;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ConfiguracaoTributoBI extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Tributo tributo;
    @Enumerated(EnumType.STRING)
    private TipoTributoBI tipoTributoBI;

    public ConfiguracaoTributoBI() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Tributo getTributo() {
        return tributo;
    }

    public void setTributo(Tributo tributo) {
        this.tributo = tributo;
    }

    public TipoTributoBI getTipoTributoBI() {
        return tipoTributoBI;
    }

    public void setTipoTributoBI(TipoTributoBI tipoTributoBI) {
        this.tipoTributoBI = tipoTributoBI;
    }
}
