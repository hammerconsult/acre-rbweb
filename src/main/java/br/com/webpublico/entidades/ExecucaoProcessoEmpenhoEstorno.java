package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Licitações")
@Table(name = "EXECUCAOPROCESSOEMPENHOEST")
public class ExecucaoProcessoEmpenhoEstorno extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Processo Estorno")
    private ExecucaoProcessoEstorno execucaoProcessoEstorno;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Solicitação Empenho Estorno")
    private SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno;

    @Etiqueta("Valor")
    private BigDecimal valor;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "execucaoProcessoEmpenhoEst", orphanRemoval = true)
    private List<ExecucaoProcessoEmpenhoEstornoItem> itens;

    public ExecucaoProcessoEmpenhoEstorno() {
        itens = Lists.newArrayList();
        valor = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoProcessoEstorno getExecucaoProcessoEstorno() {
        return execucaoProcessoEstorno;
    }

    public void setExecucaoProcessoEstorno(ExecucaoProcessoEstorno execucaoProcessoEstorno) {
        this.execucaoProcessoEstorno = execucaoProcessoEstorno;
    }

    public SolicitacaoEmpenhoEstorno getSolicitacaoEmpenhoEstorno() {
        return solicitacaoEmpenhoEstorno;
    }

    public void setSolicitacaoEmpenhoEstorno(SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno) {
        this.solicitacaoEmpenhoEstorno = solicitacaoEmpenhoEstorno;
    }

    public List<ExecucaoProcessoEmpenhoEstornoItem> getItens() {
        return itens;
    }

    public void setItens(List<ExecucaoProcessoEmpenhoEstornoItem> itens) {
        this.itens = itens;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getValorTotalItens() {
        BigDecimal total = BigDecimal.ZERO;
        for (ExecucaoProcessoEmpenhoEstornoItem item : getItens()) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }

    public BigDecimal getValorTotalItensSelecionado() {
        BigDecimal total = BigDecimal.ZERO;
        for (ExecucaoProcessoEmpenhoEstornoItem item : getItens()) {
            if (item.getSelecionado()) {
                total = total.add(item.getValorTotal());
            }
        }
        return total;
    }
}
