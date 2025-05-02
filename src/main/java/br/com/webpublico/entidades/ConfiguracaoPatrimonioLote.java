package br.com.webpublico.entidades;

import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Audited
public class ConfiguracaoPatrimonioLote extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @OneToOne
    private ConfiguracaoTributario configuracaoTributario;
    @ManyToOne
    private Atributo atributo;
    @OneToMany(mappedBy = "configuracaoPatrimonioLote", cascade = CascadeType.ALL)
    private List<ItemConfiguracaoPatrimonioLote> itensConfiguracaoPatrimonioLote;

    public ConfiguracaoPatrimonioLote() {
        itensConfiguracaoPatrimonioLote = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConfiguracaoTributario getConfiguracaoTributario() {
        return configuracaoTributario;
    }

    public void setConfiguracaoTributario(ConfiguracaoTributario configuracaoTributario) {
        this.configuracaoTributario = configuracaoTributario;
    }

    public Atributo getAtributo() {
        return atributo;
    }

    public void setAtributo(Atributo atributo) {
        this.atributo = atributo;
    }

    public List<ItemConfiguracaoPatrimonioLote> getItensConfiguracaoPatrimonioLote() {
        return itensConfiguracaoPatrimonioLote;
    }

    public void setItensConfiguracaoPatrimonioLote(List<ItemConfiguracaoPatrimonioLote> itensConfiguracaoPatrimonioLote) {
        this.itensConfiguracaoPatrimonioLote = itensConfiguracaoPatrimonioLote;
    }
}
