package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.RecursoFP;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.TipoCedenciaContratoFP;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;

/**
 * Created by renat on 01/06/2016.
 */
@Entity
@Etiqueta("Entidade - Portal da Transparência")
public class ServidorPortal extends EntidadePortalTransparencia {

    @ManyToOne
    private VinculoFP vinculo;
    @ManyToOne
    private UnidadeOrganizacional lotacao;
    @ManyToOne
    private RecursoFP recurso;
    @Enumerated(EnumType.STRING)
    private TipoFolhaDePagamento tipoFolhaDePagamento;
    private BigDecimal vencimentoBase;
    private BigDecimal outrasVerbas;
    private BigDecimal salarioBruto;
    private BigDecimal descontos;
    private BigDecimal salarioLiquido;
    private Integer mes;
    @ManyToOne
    private Exercicio exercicio;
    private BigDecimal impostoRenda;
    private BigDecimal previdencia;
    private BigDecimal abateTeto;
    private BigDecimal outrosDescontos;
    @Enumerated(EnumType.STRING)
    private TipoCedenciaContratoFP tipoCedenciaContratoFP;

    public ServidorPortal() {
        super.setTipo(TipoObjetoPortalTransparencia.SERVIDOR);
    }

    public ServidorPortal(VinculoFP vinculo, TipoFolhaDePagamento tipoFolhaDePagamento, UnidadeOrganizacional lotacao, RecursoFP recurso, BigDecimal vencimentoBase, BigDecimal outrasVerbas, BigDecimal salarioBruto, BigDecimal descontos, BigDecimal salarioLiquido, Integer mes, Exercicio exercicio, BigDecimal impostoRenda, BigDecimal previdencia, BigDecimal abateTeto, BigDecimal outrosDescontos) {
        this.vinculo = vinculo;
        this.lotacao = lotacao;
        this.recurso = recurso;
        this.vencimentoBase = vencimentoBase;
        this.outrasVerbas = outrasVerbas;
        this.salarioBruto = salarioBruto;
        this.descontos = descontos;
        this.salarioLiquido = salarioLiquido;
        this.mes = mes;
        this.exercicio = exercicio;
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;

        this.impostoRenda = impostoRenda;
        this.previdencia = previdencia;
        this.abateTeto = abateTeto;
        this.outrosDescontos = outrosDescontos;

        if (this.vencimentoBase == null) {
            this.vencimentoBase = BigDecimal.ZERO;
        }
        if (this.outrasVerbas == null) {
            this.outrasVerbas = BigDecimal.ZERO;
        }
        if (this.salarioBruto == null) {
            this.salarioBruto = BigDecimal.ZERO;
        }
        if (this.descontos == null) {
            this.descontos = BigDecimal.ZERO;
        }
        if (this.salarioLiquido == null) {
            this.salarioLiquido = BigDecimal.ZERO;
        }
        super.setTipo(TipoObjetoPortalTransparencia.SERVIDOR);
    }

    public BigDecimal getImpostoRenda() {
        return impostoRenda;
    }

    public void setImpostoRenda(BigDecimal impostoRenda) {
        this.impostoRenda = impostoRenda;
    }

    public BigDecimal getPrevidencia() {
        return previdencia;
    }

    public void setPrevidencia(BigDecimal previdencia) {
        this.previdencia = previdencia;
    }

    public BigDecimal getAbateTeto() {
        return abateTeto;
    }

    public void setAbateTeto(BigDecimal abateTeto) {
        this.abateTeto = abateTeto;
    }

    public BigDecimal getOutrosDescontos() {
        return outrosDescontos;
    }

    public void setOutrosDescontos(BigDecimal outrosDescontos) {
        this.outrosDescontos = outrosDescontos;
    }

    public BigDecimal getVencimentoBase() {
        return vencimentoBase;
    }

    public void setVencimentoBase(BigDecimal vencimentoBase) {
        this.vencimentoBase = vencimentoBase;
    }

    public BigDecimal getOutrasVerbas() {
        return outrasVerbas;
    }

    public void setOutrasVerbas(BigDecimal outrasVerbas) {
        this.outrasVerbas = outrasVerbas;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public VinculoFP getVinculo() {
        return vinculo;
    }

    public void setVinculo(VinculoFP vinculo) {
        this.vinculo = vinculo;
    }

    public UnidadeOrganizacional getLotacao() {
        return lotacao;
    }

    public void setLotacao(UnidadeOrganizacional lotacao) {
        this.lotacao = lotacao;
    }

    public RecursoFP getRecurso() {
        return recurso;
    }

    public void setRecurso(RecursoFP recurso) {
        this.recurso = recurso;
    }

    public BigDecimal getSalarioBruto() {
        return salarioBruto;
    }

    public void setSalarioBruto(BigDecimal salarioBruto) {
        this.salarioBruto = salarioBruto;
    }

    public BigDecimal getDescontos() {
        return descontos;
    }

    public void setDescontos(BigDecimal descontos) {
        this.descontos = descontos;
    }

    public BigDecimal getSalarioLiquido() {
        return salarioLiquido;
    }

    public void setSalarioLiquido(BigDecimal salarioLiquido) {
        this.salarioLiquido = salarioLiquido;
    }

    public TipoCedenciaContratoFP getTipoCedenciaContratoFP() {
        return tipoCedenciaContratoFP;
    }

    public void setTipoCedenciaContratoFP(TipoCedenciaContratoFP tipoCedenciaContratoFP) {
        this.tipoCedenciaContratoFP = tipoCedenciaContratoFP;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    @Override
    public String toString() {
        return "ServidorPortal{" +
            "vinculo=" + vinculo +
            ", lotacao=" + lotacao +
            ", recurso=" + recurso +
            ", vencimentoBase=" + vencimentoBase +
            ", outrasVerbas=" + outrasVerbas +
            ", salarioBruto=" + salarioBruto +
            ", descontos=" + descontos +
            ", salarioLiquido=" + salarioLiquido +
            ", mes=" + mes +
            ", exercicio=" + exercicio +
            ", impostoRenda=" + impostoRenda +
            ", previdencia=" + previdencia +
            ", abateTeto=" + abateTeto +
            ", outrosDescontos=" + outrosDescontos +
            ", tipoCedenciaContratoFP=" + tipoCedenciaContratoFP +
            '}';
    }
}
