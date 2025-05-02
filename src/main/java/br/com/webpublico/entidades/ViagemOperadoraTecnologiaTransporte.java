package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Monetario;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author octavio
 */
@Table(name = "VIAGEMOTT")
@GrupoDiagrama(nome = "RBTrans")
@Etiqueta("Viagem da Operadora de Tecnologia de Transporte")
@Entity
@Audited
public class ViagemOperadoraTecnologiaTransporte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Operadora de Transporte")
    @Tabelavel
    @ManyToOne
    private OperadoraTecnologiaTransporte operadoraTecTransporte;
    @Etiqueta("Data do chamado")
    @Tabelavel
    @Pesquisavel
    @Temporal(TemporalType.DATE)
    private Date dataChamadas;
    @Pesquisavel
    @Etiqueta("Quantidade de chamados")
    private Long qtdChamadas;
    @Pesquisavel
    @Etiqueta("Quantidade de cancelamentos")
    private Long qtdCancelamento;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Quantidade de corridas")
    private Long qtdCorridas;
    @Pesquisavel
    @Etiqueta("Tempo total de chamados em segundos")
    private Long tempoTotalChamadoSegundos;
    @Pesquisavel
    @Etiqueta("Tempo total de corridas em segundos")
    private Long tempoTotalCorridaSegundos;
    @Pesquisavel
    @Etiqueta("Distância percorrida")
    private Long distanciaPercorrida;
    @Pesquisavel
    @Etiqueta("Quantidade de corridas masculinas")
    private Long qtdCorridaMasculinas;
    @Pesquisavel
    @Etiqueta("Quantidade de corridas femininas")
    private Long qtdCorridaFemininas;
    @Pesquisavel
    @Etiqueta("Quantidade de corridas por veículo poluente")
    private Long qtdCorridaVeicPoluentes;
    @Pesquisavel
    @Etiqueta("Quantidade de corridas por veículo não poluente")
    private Long qtdCorridaVeicNaoPoluentes;
    @Pesquisavel
    @Tabelavel
    @Monetario
    @Etiqueta("Valor total")
    private BigDecimal valorTotalCorridas;
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor total líquido")
    private BigDecimal valorTotalLiquidoCobrado;
    @Pesquisavel
    @Monetario
    @Etiqueta("Valor total do desconto")
    private BigDecimal valorTotalDesconto;
    @Pesquisavel
    @Etiqueta("Avaliações")
    private String avaliacoes;
    @Pesquisavel
    @Etiqueta("Quantidade de corridas por hora")
    private String qtdCorridaPorHora;
    @Pesquisavel
    @Etiqueta("Quantidade de carros por hora")
    private String qtdCarrosPorHora;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OperadoraTecnologiaTransporte getOperadoraTecTransporte() {
        return operadoraTecTransporte;
    }

    public void setOperadoraTecTransporte(OperadoraTecnologiaTransporte operadoraTecTransporte) {
        this.operadoraTecTransporte = operadoraTecTransporte;
    }

    public Date getDataChamadas() {
        return dataChamadas;
    }

    public void setDataChamadas(Date dataChamadas) {
        this.dataChamadas = dataChamadas;
    }

    public Long getQtdChamadas() {
        return qtdChamadas;
    }

    public void setQtdChamadas(Long qtdChamadas) {
        this.qtdChamadas = qtdChamadas;
    }

    public Long getQtdCancelamento() {
        return qtdCancelamento;
    }

    public void setQtdCancelamento(Long qtdCancelamento) {
        this.qtdCancelamento = qtdCancelamento;
    }

    public Long getQtdCorridas() {
        return qtdCorridas;
    }

    public void setQtdCorridas(Long qtdCorridas) {
        this.qtdCorridas = qtdCorridas;
    }

    public Long getTempoTotalChamadoSegundos() {
        return tempoTotalChamadoSegundos;
    }

    public void setTempoTotalChamadoSegundos(Long tempoTotalChamadoSegundos) {
        this.tempoTotalChamadoSegundos = tempoTotalChamadoSegundos;
    }

    public Long getTempoTotalCorridaSegundos() {
        return tempoTotalCorridaSegundos;
    }

    public void setTempoTotalCorridaSegundos(Long tempoTotalCorridaSegundos) {
        this.tempoTotalCorridaSegundos = tempoTotalCorridaSegundos;
    }

    public Long getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public void setDistanciaPercorrida(Long distanciaPercorrida) {
        this.distanciaPercorrida = distanciaPercorrida;
    }

    public Long getQtdCorridaMasculinas() {
        return qtdCorridaMasculinas;
    }

    public void setQtdCorridaMasculinas(Long qtdCorridaMasculinas) {
        this.qtdCorridaMasculinas = qtdCorridaMasculinas;
    }

    public Long getQtdCorridaFemininas() {
        return qtdCorridaFemininas;
    }

    public void setQtdCorridaFemininas(Long qtdCorridaFemininas) {
        this.qtdCorridaFemininas = qtdCorridaFemininas;
    }

    public Long getQtdCorridaVeicPoluentes() {
        return qtdCorridaVeicPoluentes;
    }

    public void setQtdCorridaVeicPoluentes(Long qtdCorridaVeicPoluentes) {
        this.qtdCorridaVeicPoluentes = qtdCorridaVeicPoluentes;
    }

    public Long getQtdCorridaVeicNaoPoluentes() {
        return qtdCorridaVeicNaoPoluentes;
    }

    public void setQtdCorridaVeicNaoPoluentes(Long qtdCorridaVeicNaoPoluentes) {
        this.qtdCorridaVeicNaoPoluentes = qtdCorridaVeicNaoPoluentes;
    }

    public BigDecimal getValorTotalCorridas() {
        return valorTotalCorridas;
    }

    public void setValorTotalCorridas(BigDecimal valorTotalCorridas) {
        this.valorTotalCorridas = valorTotalCorridas;
    }

    public BigDecimal getValorTotalLiquidoCobrado() {
        return valorTotalLiquidoCobrado;
    }

    public void setValorTotalLiquidoCobrado(BigDecimal valorTotalLiquidoCobrado) {
        this.valorTotalLiquidoCobrado = valorTotalLiquidoCobrado;
    }

    public BigDecimal getValorTotalDesconto() {
        return valorTotalDesconto;
    }

    public void setValorTotalDesconto(BigDecimal valorTotalDesconto) {
        this.valorTotalDesconto = valorTotalDesconto;
    }

    public String getAvaliacoes() {
        return avaliacoes;
    }

    public void setAvaliacoes(String avaliacoes) {
        this.avaliacoes = avaliacoes;
    }

    public String getQtdCorridaPorHora() {
        return qtdCorridaPorHora;
    }

    public void setQtdCorridaPorHora(String qtdCorridaPorHora) {
        this.qtdCorridaPorHora = qtdCorridaPorHora;
    }

    public String getQtdCarrosPorHora() {
        return qtdCarrosPorHora;
    }

    public void setQtdCarrosPorHora(String qtdCarrosPorHora) {
        this.qtdCarrosPorHora = qtdCarrosPorHora;
    }
}
