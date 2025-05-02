package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEmissaoMarcaFogo;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class TaxaMarcaFogo extends SuperEntidade {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ParametroMarcaFogo parametroMarcaFogo;

    @Obrigatorio
    @Etiqueta("Tipo de Emissão")
    @Enumerated(EnumType.STRING)
    private TipoEmissaoMarcaFogo tipoEmissao;

    @Obrigatorio
    @Etiqueta("Tributo")
    @ManyToOne
    private TributoTaxaDividasDiversas tributo;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ParametroMarcaFogo getParametroMarcaFogo() {
        return parametroMarcaFogo;
    }

    public void setParametroMarcaFogo(ParametroMarcaFogo parametroMarcaFogo) {
        this.parametroMarcaFogo = parametroMarcaFogo;
    }

    public TipoEmissaoMarcaFogo getTipoEmissao() {
        return tipoEmissao;
    }

    public void setTipoEmissao(TipoEmissaoMarcaFogo tipoEmissao) {
        this.tipoEmissao = tipoEmissao;
    }

    public TributoTaxaDividasDiversas getTributo() {
        return tributo;
    }

    public void setTributo(TributoTaxaDividasDiversas tributo) {
        this.tributo = tributo;
    }

}
