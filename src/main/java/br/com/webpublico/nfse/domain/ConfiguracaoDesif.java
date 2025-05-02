package br.com.webpublico.nfse.domain;


import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ConfiguracaoDesif extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    @Etiqueta("Tipo de Manual")
    private TipoManual tipoManual;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoManual getTipoManual() {
        return tipoManual;
    }

    public void setTipoManual(TipoManual tipoManual) {
        this.tipoManual = tipoManual;
    }
}
