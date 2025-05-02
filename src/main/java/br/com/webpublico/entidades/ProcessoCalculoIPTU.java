/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.ReferenciaImposto;
import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.enums.UtilizacaoImovel;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author Terminal-2
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Tributario")
@Etiqueta("Processo de Cálculo de IPTU")
public class ProcessoCalculoIPTU extends ProcessoCalculo implements Serializable {

    private static final long serialVersionUID = 1L;
    @OneToMany(mappedBy = "processoCalculoIPTU", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<CalculoIPTU> calculosIPTU;
    private String log;
    @Etiqueta("Referência")
    @Enumerated(EnumType.STRING)
    private ReferenciaImposto referenciaImposto;
    @Etiqueta("Tipo de Imóvel")
    @Enumerated(EnumType.STRING)
    private TipoImovel tipoImovel;
    @Etiqueta("Utilização do Imóvel")
    @Enumerated(EnumType.STRING)
    private UtilizacaoImovel utilizacaoImovel;
    @Etiqueta("Valor Min. I.Predial")
    private BigDecimal valorMinimoImpostoPredial;
    @Etiqueta("Valor Min. I.Territorial")
    private BigDecimal valorMinimoImpostoTerritorial;
    @Etiqueta("Desconto Venal Terreno")
    private BigDecimal descontoValoVenalTerreno;
    @Etiqueta("Desconto Venal Construcao")
    private BigDecimal descontoValoVenalConstrucao;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Cadastro Inicial")
    private String cadastroInical;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Cadastro Final")
    private String cadastroFinal;
    @Transient
    private Integer ordem;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Configuração de IPTU")
    private ConfiguracaoEventoIPTU configuracaoEventoIPTU;
    @Transient
    private Efetivado efetivado;
    @Transient
    private Integer quantidadeCalculos;

    public ProcessoCalculoIPTU() {
        calculosIPTU = Lists.newArrayList();
    }

    public ProcessoCalculoIPTU(ProcessoCalculoIPTU processoCalculoIPTU, Long id) {
        setId(id);
        cadastroInical = processoCalculoIPTU.getCadastroInical();
        cadastroFinal = processoCalculoIPTU.getCadastroFinal();
        setDivida(processoCalculoIPTU.getDivida());
        setDataLancamento(processoCalculoIPTU.getDataLancamento());
        setDescricao(processoCalculoIPTU.getDescricao());
    }

    public String getDescricaoCurta() {

        return getDescricao();
    }

    public ConfiguracaoEventoIPTU getConfiguracaoEventoIPTU() {
        return configuracaoEventoIPTU;
    }

    public void setConfiguracaoEventoIPTU(ConfiguracaoEventoIPTU configuracaoEventoIPTU) {
        this.configuracaoEventoIPTU = configuracaoEventoIPTU;
    }

    @Override
    public List<? extends Calculo> getCalculos() {
        return calculosIPTU;
    }

    public String getCadastroInicial() {
        return cadastroInical;
    }

    public void setCadastroInicial(String cadastroInicial) {
        this.cadastroInical = cadastroInicial;
    }

    public String getCadastroFinal() {
        return cadastroFinal;
    }

    public void setCadastroFinal(String cadastroFinal) {
        this.cadastroFinal = cadastroFinal;
    }

    public ReferenciaImposto getReferenciaImposto() {
        return referenciaImposto;
    }

    public void setReferenciaImposto(ReferenciaImposto referenciaImposto) {
        this.referenciaImposto = referenciaImposto;
    }

    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovel tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public UtilizacaoImovel getUtilizacaoImovel() {
        return utilizacaoImovel;
    }

    public void setUtilizacaoImovel(UtilizacaoImovel utilizacaoImovel) {
        this.utilizacaoImovel = utilizacaoImovel;
    }

    public BigDecimal getValorMinimoImpostoPredial() {
        return valorMinimoImpostoPredial;
    }

    public void setValorMinimoImpostoPredial(BigDecimal valorMinimoImpostoPredial) {
        this.valorMinimoImpostoPredial = valorMinimoImpostoPredial;
    }

    public BigDecimal getValorMinimoImpostoTerritorial() {
        return valorMinimoImpostoTerritorial;
    }

    public void setValorMinimoImpostoTerritorial(BigDecimal valorMinimoImpostoTerritorial) {
        this.valorMinimoImpostoTerritorial = valorMinimoImpostoTerritorial;
    }

    public BigDecimal getDescontoSobreValorVenalTerreno() {
        return descontoValoVenalTerreno;
    }

    public void setDescontoSobreValorVenalTerreno(BigDecimal descontoSobreValorVenalTerreno) {
        this.descontoValoVenalTerreno = descontoSobreValorVenalTerreno;
    }

    public BigDecimal getDescontoSobreValorVenalConstrucao() {
        return descontoValoVenalConstrucao;
    }

    public void setDescontoSobreValorVenalConstrucao(BigDecimal descontoSobreValorVenalConstrucao) {
        this.descontoValoVenalConstrucao = descontoSobreValorVenalConstrucao;
    }

    public String getCadastroInical() {
        return cadastroInical;
    }

    public void setCadastroInical(String cadastroInical) {
        this.cadastroInical = cadastroInical;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public List<CalculoIPTU> getCalculosIPTU() {
        return calculosIPTU;
    }

    public void setCalculosIPTU(List<CalculoIPTU> calculosIPTU) {
        this.calculosIPTU = calculosIPTU;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Efetivado getEfetivado() {
        return efetivado;
    }

    public void setEfetivado(Efetivado efetivado) {
        this.efetivado = efetivado;
    }

    public Integer getQuantidadeCalculos() {
        return quantidadeCalculos;
    }

    public void setQuantidadeCalculos(Integer quantidadeCalculos) {
        this.quantidadeCalculos = quantidadeCalculos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(getId()).append(" - ");

        if (getDescricao() != null) {
            sb.append(getDescricao());
        }
        if (this.cadastroInical != null && this.cadastroFinal != null && cadastroInical.equals(cadastroFinal)) {
            sb.append(" - Cad.: ").append(this.cadastroInical);
        } else {
            if (this.cadastroInical != null) {
                sb.append(" - de: ").append(this.cadastroInical);
            }
            if (this.cadastroFinal != null) {
                sb.append(" - até: ").append(this.cadastroFinal);
            }
        }
        if (getExercicio() != null) {
            sb.append(" - ").append(getExercicio().getAno());
        }
        return sb.toString();    //To change body of overridden methods use File | Settings | File Templates.

    }

    public static enum Efetivado {
        PARCIAL, TOTAL;
    }
}
