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
 * Time: 09:42
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "ITEMMACOTECPRECLOTEPROC")
@Etiqueta("Item Mapa Comparativo Técnica e Preço Lote Processo")
public class ItemMapaComparativoTecnicaPrecoLoteProcesso extends SuperEntidade implements ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Item Mapa Comparativo Técnica e Preço")
    @OneToOne
    private ItemMapaComparativoTecnicaPreco itemMapaComTecnicaPreco;

    @Etiqueta("Lote Proposta Fornecedor")
    @OneToOne
    private LotePropostaFornecedor lotePropostaVencedor;

    @Etiqueta("Lote Processo de Compra")
    @OneToOne
    private LoteProcessoDeCompra loteProcessoDeCompra;

    public ItemMapaComparativoTecnicaPrecoLoteProcesso() {
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

    public LotePropostaFornecedor getLotePropostaVencedor() {
        return lotePropostaVencedor;
    }

    public void setLotePropostaVencedor(LotePropostaFornecedor lotePropostaVencedor) {
        this.lotePropostaVencedor = lotePropostaVencedor;
    }

    public LoteProcessoDeCompra getLoteProcessoDeCompra() {
        return loteProcessoDeCompra;
    }

    public void setLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        this.loteProcessoDeCompra = loteProcessoDeCompra;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }
}
