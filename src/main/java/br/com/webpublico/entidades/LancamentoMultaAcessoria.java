/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Alex
 * @since 30/03/2017 14:30
 */
@Etiqueta("Lançamento de Multa Acessória")
@Entity
@Audited
public class LancamentoMultaAcessoria extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Etiqueta("Data do Lançamento")
    private Date dataLancamento;
    @Obrigatorio
    @OneToMany(mappedBy = "lancamentoMultaAcessoria", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemLancamentoMultaAcessoria> itemLancamentoMultaAcessorias;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Cadastro Econômico")
    private CadastroEconomico cadastroEconomico;
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    @Etiqueta("Pessoa")
    private Pessoa pessoa;
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Monetario
    @Etiqueta("Total")
    private BigDecimal valorTotal;
    @OneToOne(cascade = CascadeType.ALL)
    private ProcessoCalculoMultaAcessoria processoMultaAcessoria;

    public LancamentoMultaAcessoria() {
        itemLancamentoMultaAcessorias = new ArrayList<>();

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public List<ItemLancamentoMultaAcessoria> getItemLancamentoMultaAcessoria() {
        return itemLancamentoMultaAcessorias;
    }

    public void setItemLancamentoMultaAcessoria(List<ItemLancamentoMultaAcessoria> itemLancamentoMultaAcessoria) {
        this.itemLancamentoMultaAcessorias = itemLancamentoMultaAcessoria;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public String getValorTotalFormatado() {
        if (valorTotal == null) {
            return "0,00";
        }
        return Util.formataNumero(valorTotal);
    }

    public List<ItemLancamentoMultaAcessoria> getItemLancamentoMultaAcessorias() {
        return itemLancamentoMultaAcessorias;
    }

    public void setItemLancamentoMultaAcessorias(List<ItemLancamentoMultaAcessoria> itemLancamentoMultaAcessorias) {
        this.itemLancamentoMultaAcessorias = itemLancamentoMultaAcessorias;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public String cadastroOrPessoa() {
        if (this.cadastroEconomico != null) {
            return cadastroEconomico.toString();
        } else {
            return pessoa.toString();
        }
    }

    public ProcessoCalculoMultaAcessoria getProcessoMultaAcessoria() {
        return processoMultaAcessoria;
    }

    public void setProcessoMultaAcessoria(ProcessoCalculoMultaAcessoria processoMultaAcessoria) {
        this.processoMultaAcessoria = processoMultaAcessoria;
    }
}
