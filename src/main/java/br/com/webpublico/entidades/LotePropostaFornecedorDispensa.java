package br.com.webpublico.entidades;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ValidadorEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 18/07/14
 * Time: 11:12
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited

@Table(name = "LOTEPROPOSTAFORNEDISP")
@Etiqueta("Lote Proposta Fornecedor Dispensa")
public class LotePropostaFornecedorDispensa extends SuperEntidade implements ValidadorEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Proposta Fornecedor Dispensa")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private PropostaFornecedorDispensa propostaFornecedorDispensa;

    @Etiqueta("Lote Processo de Compra")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private LoteProcessoDeCompra loteProcessoDeCompra;

    @Etiqueta("Valor")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    private BigDecimal valor;

    @Etiqueta("Itens da Proposta")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "lotePropostaFornecedorDispensa", orphanRemoval = true)
    private List<ItemPropostaFornecedorDispensa> itensProposta;


    public LotePropostaFornecedorDispensa() {
        super();
        this.itensProposta = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PropostaFornecedorDispensa getPropostaFornecedorDispensa() {
        return propostaFornecedorDispensa;
    }

    public void setPropostaFornecedorDispensa(PropostaFornecedorDispensa propostaFornecedorDispensa) {
        this.propostaFornecedorDispensa = propostaFornecedorDispensa;
    }

    public LoteProcessoDeCompra getLoteProcessoDeCompra() {
        return loteProcessoDeCompra;
    }

    public void setLoteProcessoDeCompra(LoteProcessoDeCompra loteProcessoDeCompra) {
        this.loteProcessoDeCompra = loteProcessoDeCompra;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public List<ItemPropostaFornecedorDispensa> getItensProposta() {
        return itensProposta;
    }

    public void setItensProposta(List<ItemPropostaFornecedorDispensa> itensProposta) {
        this.itensProposta = itensProposta;
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ItemPropostaFornecedorDispensa item : getItensProposta()) {
            valorTotal = valorTotal.add(item.getPrecoTotal());
        }
        return valorTotal;
    }

    @Override
    public void validarConfirmacao() throws ValidacaoException {
        return;
    }

    public boolean isLotePropostaSemItem() {
        return this.getItensProposta() == null || this.getItensProposta().isEmpty();
    }
}
