package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 17/11/14
 * Time: 09:27
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class ViagemAbastecimento extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private Viagem viagem;

    @Obrigatorio
    @ManyToOne
    private Cidade cidade;

    @Obrigatorio
    private String fornecedor;

    @Obrigatorio
    private String combustivel;

    @Monetario
    @Obrigatorio
    private BigDecimal quantidade;

    @Monetario
    @Obrigatorio
    private BigDecimal quilometragemAtual;

    @Temporal(TemporalType.DATE)
    private Date dataAbastecimento;

    public ViagemAbastecimento() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Viagem getViagem() {
        return viagem;
    }

    public void setViagem(Viagem viagem) {
        this.viagem = viagem;
    }

    public Cidade getCidade() {
        return cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getCombustivel() {
        return combustivel;
    }

    public void setCombustivel(String combustivel) {
        this.combustivel = combustivel;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getQuilometragemAtual() {
        return quilometragemAtual;
    }

    public void setQuilometragemAtual(BigDecimal quilometragemAtual) {
        this.quilometragemAtual = quilometragemAtual;
    }

    public Date getDataAbastecimento() {
        return dataAbastecimento;
    }

    public void setDataAbastecimento(Date dataAbastecimento) {
        this.dataAbastecimento = dataAbastecimento;
    }
}
