package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * @Author peixe on 22/10/2015  09:46.
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Configuração RH")
@Etiqueta("Configurações Folha De Pagamento")
public class ConfiguracaoFP extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EventoFP descontoConsignacao;
    @ManyToOne
    private EventoFP rpps;
    @ManyToOne
    private EventoFP adiantamento13Salario;
    @ManyToOne
    private EventoFP contribuicaoSindical;
    @ManyToOne
    private BaseFP baseMargemConsignavel;
    @ManyToOne
    private BaseFP baseMargemEuConsigoMais;
    private Integer qtdeMinimaDiasEuConsigMais;
    @ManyToOne
    private BaseFP baseImpostoRenda;
    @ManyToOne
    private EventoFP provisao13Salario;
    @ManyToOne
    private EventoFP provisaoFerias;

    private BigDecimal percentualMargemDisponivel;
    private BigDecimal percentualMargemEmprestimo;


    public ConfiguracaoFP() {
    }

    public BaseFP getBaseMargemConsignavel() {
        return baseMargemConsignavel;
    }

    public void setBaseMargemConsignavel(BaseFP baseMargemConsignavel) {
        this.baseMargemConsignavel = baseMargemConsignavel;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EventoFP getDescontoConsignacao() {
        return descontoConsignacao;
    }

    public void setDescontoConsignacao(EventoFP descontoConsignacao) {
        this.descontoConsignacao = descontoConsignacao;
    }

    public EventoFP getAdiantamento13Salario() {
        return adiantamento13Salario;
    }

    public void setAdiantamento13Salario(EventoFP adiantamento13Salario) {
        this.adiantamento13Salario = adiantamento13Salario;
    }

    public EventoFP getRpps() {
        return rpps;
    }

    public void setRpps(EventoFP rpps) {
        this.rpps = rpps;
    }

    public EventoFP getContribuicaoSindical() {
        return contribuicaoSindical;
    }

    public void setContribuicaoSindical(EventoFP contribuicaoSindical) {
        this.contribuicaoSindical = contribuicaoSindical;
    }

    public BigDecimal getPercentualMargemDisponivel() {
        return percentualMargemDisponivel;
    }

    public void setPercentualMargemDisponivel(BigDecimal percentualMargemDisponivel) {
        this.percentualMargemDisponivel = percentualMargemDisponivel;
    }

    public BigDecimal getPercentualMargemEmprestimo() {
        return percentualMargemEmprestimo;
    }

    public void setPercentualMargemEmprestimo(BigDecimal percentualMargemEmprestimo) {
        this.percentualMargemEmprestimo = percentualMargemEmprestimo;
    }

    public BaseFP getBaseImpostoRenda() {
        return baseImpostoRenda;
    }

    public void setBaseImpostoRenda(BaseFP baseImpostoRenda) {
        this.baseImpostoRenda = baseImpostoRenda;
    }

    public BaseFP getBaseMargemEuConsigoMais() {
        return baseMargemEuConsigoMais;
    }

    public void setBaseMargemEuConsigoMais(BaseFP baseMargemEuConsigoMais) {
        this.baseMargemEuConsigoMais = baseMargemEuConsigoMais;
    }

    public Integer getQtdeMinimaDiasEuConsigMais() {
        return qtdeMinimaDiasEuConsigMais;
    }

    public void setQtdeMinimaDiasEuConsigMais(Integer qtdeMinimaDiasEuConsigMais) {
        this.qtdeMinimaDiasEuConsigMais = qtdeMinimaDiasEuConsigMais;
    }

    public EventoFP getProvisao13Salario() {
        return provisao13Salario;
    }

    public void setProvisao13Salario(EventoFP provisao13Salario) {
        this.provisao13Salario = provisao13Salario;
    }

    public EventoFP getProvisaoFerias() {
        return provisaoFerias;
    }

    public void setProvisaoFerias(EventoFP provisaoFerias) {
        this.provisaoFerias = provisaoFerias;
    }

    @Override
    public String toString() {
        if (descontoConsignacao != null) {
            return descontoConsignacao.toString();
        }
        return super.toString();
    }
}
