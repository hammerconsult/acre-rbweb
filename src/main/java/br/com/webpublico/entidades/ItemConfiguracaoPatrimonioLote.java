package br.com.webpublico.entidades;

import br.com.webpublico.enums.PatrimonioDoLote;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Table(name = "ITEMCONFPATRIMONIOLOTE")
public class ItemConfiguracaoPatrimonioLote extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private PatrimonioDoLote patrimonioDoLote;
    @ManyToOne
    private ConfiguracaoPatrimonioLote configuracaoPatrimonioLote;
    @ManyToOne
    private ValorPossivel valorPossivel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PatrimonioDoLote getPatrimonioDoLote() {
        return patrimonioDoLote;
    }

    public void setPatrimonioDoLote(PatrimonioDoLote patrimonioDoLote) {
        this.patrimonioDoLote = patrimonioDoLote;
    }

    public ConfiguracaoPatrimonioLote getConfiguracaoPatrimonioLote() {
        return configuracaoPatrimonioLote;
    }

    public void setConfiguracaoPatrimonioLote(ConfiguracaoPatrimonioLote configuracaoPatrimonioLote) {
        this.configuracaoPatrimonioLote = configuracaoPatrimonioLote;
    }

    public ValorPossivel getValorPossivel() {
        return valorPossivel;
    }

    public void setValorPossivel(ValorPossivel valorPossivel) {
        this.valorPossivel = valorPossivel;
    }
}
