package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 04/08/14
 * Time: 09:39
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "REGSOLMATEXTFORITEMSOL")
@Etiqueta("Registro Solicitacao Material Externo Fornecedor Item Solicitação Externo")
public class RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno  extends SuperEntidade implements Serializable, ValidadorEntidade, Comparable<RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Registro Solicitação Material Externo Fornecedor")
    @ManyToOne
    private RegistroSolicitacaoMaterialExternoFornecedor regSolMatExtFornecedor;

    @Etiqueta("Item Solicitação Externo")
    @ManyToOne(cascade = CascadeType.MERGE)
    private ItemSolicitacaoExterno itemSolicitacaoExterno;

    @Etiqueta("Modelo")
    @Transient
    private String modelo;

    @Etiqueta("Descrição do Produto")
    @Transient
    private String descricaoProduto;

    public RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RegistroSolicitacaoMaterialExternoFornecedor getRegSolMatExtFornecedor() {
        return regSolMatExtFornecedor;
    }

    public void setRegSolMatExtFornecedor(RegistroSolicitacaoMaterialExternoFornecedor regSolMatExtFornecedor) {
        this.regSolMatExtFornecedor = regSolMatExtFornecedor;
    }

    public ItemSolicitacaoExterno getItemSolicitacaoExterno() {
        return itemSolicitacaoExterno;
    }

    public void setItemSolicitacaoExterno(ItemSolicitacaoExterno itemSolicitacaoExterno) {
        this.itemSolicitacaoExterno = itemSolicitacaoExterno;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    @Override
    public int compareTo(RegistroSolicitacaoMaterialExternoFornecedorItemSolicitacaoExterno o) {
        try {
            return getItemSolicitacaoExterno().getNumero().compareTo(o.getItemSolicitacaoExterno().getNumero());
        } catch (NullPointerException e) {
            return 0;
        }
    }
}
