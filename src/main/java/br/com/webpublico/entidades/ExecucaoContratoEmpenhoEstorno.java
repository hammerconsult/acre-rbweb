package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Monetario;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity

@Audited
@GrupoDiagrama(nome = "Contratos")
@Etiqueta("Execução Contrato Empenho Estorno")
@Table(name = "EXECUCAOCONTRATOEMPENHOEST")
public class ExecucaoContratoEmpenhoEstorno extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Contrato Estorno")
    private ExecucaoContratoEstorno execucaoContratoEstorno;

    @Monetario
    @Etiqueta("Valor")
    private BigDecimal valor;

    @ManyToOne(cascade = CascadeType.ALL)
    @Etiqueta("Solicitação Empenho Estorno")
    private SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno;

    @Etiqueta("Solicitações Liquidação Estorno")
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "execucaoContratoEmpenhoEst", orphanRemoval = true)
    private List<ExecucaoContratoLiquidacaoEstorno> solicitacoesLiquidacaoEstorno;

    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "execucaoContratoEmpenhoEst", orphanRemoval = true)
    private List<ExecucaoContratoEmpenhoEstornoItem> itens;

    public ExecucaoContratoEmpenhoEstorno() {
        itens = Lists.newArrayList();
        solicitacoesLiquidacaoEstorno = Lists.newArrayList();
        valor = BigDecimal.ZERO;
    }

    public List<ExecucaoContratoLiquidacaoEstorno> getSolicitacoesLiquidacaoEstorno() {
        return solicitacoesLiquidacaoEstorno;
    }

    public void setSolicitacoesLiquidacaoEstorno(List<ExecucaoContratoLiquidacaoEstorno> solicitacoesLiquidacaoEstorno) {
        this.solicitacoesLiquidacaoEstorno = solicitacoesLiquidacaoEstorno;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoContratoEstorno getExecucaoContratoEstorno() {
        return execucaoContratoEstorno;
    }

    public void setExecucaoContratoEstorno(ExecucaoContratoEstorno execucaoContratoEstorno) {
        this.execucaoContratoEstorno = execucaoContratoEstorno;
    }

    public SolicitacaoEmpenhoEstorno getSolicitacaoEmpenhoEstorno() {
        return solicitacaoEmpenhoEstorno;
    }

    public void setSolicitacaoEmpenhoEstorno(SolicitacaoEmpenhoEstorno solicitacaoEmpenhoEstorno) {
        this.solicitacaoEmpenhoEstorno = solicitacaoEmpenhoEstorno;
    }

    public List<ExecucaoContratoEmpenhoEstornoItem> getItens() {
        return itens;
    }

    public void setItens(List<ExecucaoContratoEmpenhoEstornoItem> itens) {
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
        for (ExecucaoContratoEmpenhoEstornoItem item : getItens()) {
            total = total.add(item.getValorTotal());
        }
        return total;
    }
}
