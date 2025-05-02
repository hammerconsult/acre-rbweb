package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Created by mga on 08/03/2018.
 */

@Audited
@Entity
@Etiqueta("Item da Efetivação Ajuste de Bens Móveis")
@Table(name = "ITEMEFETIVACAOAJUSTEMOVEL")
public class ItemEfetivacaoAjusteBemMovel extends EventoBem {

    @ManyToOne
    @Etiqueta("Efetivação")
    private EfetivacaoAjusteBemMovel efetivacaoAjusteBemMovel;

    public ItemEfetivacaoAjusteBemMovel() {
        super(TipoEventoBem.EFETIVACAO_AJUSTE_BEM_MOVEL_ORIGINAL_DIMINUTIVO, TipoOperacao.NENHUMA_OPERACAO);
    }

    public ItemEfetivacaoAjusteBemMovel(TipoEventoBem tipoEventoBem, TipoOperacao tipoOperacao) {
        super(tipoEventoBem, tipoOperacao);
    }

    public EfetivacaoAjusteBemMovel getEfetivacaoAjusteBemMovel() {
        return efetivacaoAjusteBemMovel;
    }

    public void setEfetivacaoAjusteBemMovel(EfetivacaoAjusteBemMovel efetivacaoAjusteBemMovel) {
        this.efetivacaoAjusteBemMovel = efetivacaoAjusteBemMovel;
    }
}
