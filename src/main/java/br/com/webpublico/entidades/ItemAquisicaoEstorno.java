package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Created by mga on 23/04/2018.
 */

@Entity
@Audited
@Etiqueta("Item Aquisição")
public class ItemAquisicaoEstorno extends EventoBem {

    @ManyToOne
    @Etiqueta("Estorno de Aquisição")
    @Obrigatorio
    private AquisicaoEstorno aquisicaoEstorno;

    @ManyToOne
    @Etiqueta("Item Aquisição")
    @Obrigatorio
    private ItemAquisicao itemAquisicao;


    public ItemAquisicaoEstorno() {
        super(TipoEventoBem.ITEMAQUISICAO_ESTORNO, TipoOperacao.CREDITO);
    }

    public AquisicaoEstorno getAquisicaoEstorno() {
        return aquisicaoEstorno;
    }

    public void setAquisicaoEstorno(AquisicaoEstorno aquisicaoEstorno) {
        this.aquisicaoEstorno = aquisicaoEstorno;
    }

    public ItemAquisicao getItemAquisicao() {
        return itemAquisicao;
    }

    public void setItemAquisicao(ItemAquisicao itemAquisicao) {
        this.itemAquisicao = itemAquisicao;
    }
}
