package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 18/09/14
 * Time: 09:26
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "ITEMMACOTECPRECITEMPROC")
@Etiqueta("Item Mapa Comparativo Técnica e Preço Item Processo")
public class ItemMapaComparativoTecnicaPrecoItemProcesso extends SuperEntidade implements ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Item Mapa Comparativo Técnica e Preço")
    @OneToOne
    private ItemMapaComparativoTecnicaPreco itemMapaComTecnicaPreco;

    @Etiqueta("Item Proposta Fornecedor")
    @OneToOne
    private ItemPropostaFornecedor itemPropostaVencedor;

    @Etiqueta("Item Processo de Compra")
    @OneToOne
    private ItemProcessoDeCompra itemProcessoDeCompra;

    public ItemMapaComparativoTecnicaPrecoItemProcesso() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemMapaComparativoTecnicaPreco getItemMapaComTecnicaPreco() {
        return itemMapaComTecnicaPreco;
    }

    public void setItemMapaComTecnicaPreco(ItemMapaComparativoTecnicaPreco itemMapaComTecnicaPreco) {
        this.itemMapaComTecnicaPreco = itemMapaComTecnicaPreco;
    }

    public ItemPropostaFornecedor getItemPropostaVencedor() {
        return itemPropostaVencedor;
    }

    public void setItemPropostaVencedor(ItemPropostaFornecedor itemPropostaVencedor) {
        this.itemPropostaVencedor = itemPropostaVencedor;
    }

    public ItemProcessoDeCompra getItemProcessoDeCompra() {
        return itemProcessoDeCompra;
    }

    public void setItemProcessoDeCompra(ItemProcessoDeCompra itemProcessoDeCompra) {
        this.itemProcessoDeCompra = itemProcessoDeCompra;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }
}
