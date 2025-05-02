package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEventoBem;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 25/09/14
 * Time: 15:56
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Table(name = "ITEMEFETIESTORTRANSF")
@Etiqueta("Efetivação de Estorno de Transferência")
public class ItemEfetivacaoEstornoTransferencia extends EventoBem {

    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Efetivação de estorno")
    @ManyToOne
    private EfetivacaoEstornoTransferencia efetivacaoEstorno;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Item Solicitação de Estorno")
    @OneToOne
    private ItemEstornoTransferencia itemEstornoTransferencia;

    public ItemEfetivacaoEstornoTransferencia() {
        super(TipoEventoBem.ITEMEFETIVACAOESTORNOTRANSFERENCIA, TipoOperacao.CREDITO);
    }

    public EfetivacaoEstornoTransferencia getEfetivacaoEstorno() {
        return efetivacaoEstorno;
    }

    public void setEfetivacaoEstorno(EfetivacaoEstornoTransferencia efetivacaoEstorno) {
        this.efetivacaoEstorno = efetivacaoEstorno;
    }

    public ItemEstornoTransferencia getItemEstornoTransferencia() {
        return itemEstornoTransferencia;
    }

    public void setItemEstornoTransferencia(ItemEstornoTransferencia itemEstornoTransferencia) {
        this.itemEstornoTransferencia = itemEstornoTransferencia;
    }

    @Override
    public Boolean ehEstorno() {
        return true;
    }
}
