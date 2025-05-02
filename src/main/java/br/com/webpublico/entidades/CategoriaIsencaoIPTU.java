package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoImovel;
import br.com.webpublico.enums.TipoLancamentoIsencaoIPTU;
import br.com.webpublico.enums.tributario.TipoCategoriaIsencaoIPTU;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Fabio
 */
@Etiqueta(value = "Categoria de Isenção do Impostos")
@GrupoDiagrama(nome = "Tributário")
@Audited
@Entity

public class CategoriaIsencaoIPTU implements Serializable {

    @Invisivel
    private static final long serialVersionUID = 1L;
    @Id
    @Invisivel
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Etiqueta("Código Interno")
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício Inicial")
    private Exercicio exercicioInicial;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Exercício Final")
    private Exercicio exercicioFinal;
    @Tabelavel
    @Etiqueta("Quantidade de Imóveis por Contribuinte")
    private Integer qtdeImoveisContribuinte;
    @Etiqueta("Área Limite do Terreno")
    @Tabelavel
    private BigDecimal areaLimiteTerreno;
    @Tabelavel
    @Etiqueta("Área Limite da Construção")
    private BigDecimal areaLimiteConstrucao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Tipo de Lançamento")
    private TipoLancamentoIsencaoIPTU tipoLancamentoIsencaoIPTU;
    @Etiqueta("Número da Lei")
    @Tabelavel
    private String numeroLei;
    @Etiqueta("Descrição da Lei")
    private String descricaoLei;
    @ManyToOne
    @Etiqueta("Tipo de Documento Oficial")
    private TipoDoctoOficial tipoDoctoOficial;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Categoria de Isenção")
    @Obrigatorio
    private TipoCategoriaIsencaoIPTU tipoCategoriaIsencaoIPTU;
    @Etiqueta("Valor Inicial do Último IPTU Ativo")
    private BigDecimal valorInicialUltimoIptuAtivo;
    @Etiqueta("Valor Final do Último IPTU Ativo")
    private BigDecimal valorFinalUltimoIptuAtivo;
    @Enumerated(EnumType.STRING)
    @Etiqueta("Tipo de Imóvel")
    private TipoImovel tipoImovel;
    @Etiqueta("Percentual")
    private BigDecimal percentual;
    @Transient
    @Invisivel
    private Long criadoEm;

    public CategoriaIsencaoIPTU() {
        criadoEm = System.nanoTime();
    }

    public BigDecimal getAreaLimiteConstrucao() {
        return areaLimiteConstrucao;
    }

    public void setAreaLimiteConstrucao(BigDecimal areaLimiteConstrucao) {
        this.areaLimiteConstrucao = areaLimiteConstrucao;
    }

    public BigDecimal getAreaLimiteTerreno() {
        return areaLimiteTerreno;
    }

    public void setAreaLimiteTerreno(BigDecimal areaLimiteTerreno) {
        this.areaLimiteTerreno = areaLimiteTerreno;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoLei() {
        return descricaoLei;
    }

    public void setDescricaoLei(String descricaoLei) {
        this.descricaoLei = descricaoLei;
    }

    public Exercicio getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Exercicio exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Exercicio getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Exercicio exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroLei() {
        return numeroLei;
    }

    public void setNumeroLei(String numeroLei) {
        this.numeroLei = numeroLei;
    }

    public Integer getQtdeImoveisContribuinte() {
        return qtdeImoveisContribuinte;
    }

    public void setQtdeImoveisContribuinte(Integer qtdeImoveisContribuinte) {
        this.qtdeImoveisContribuinte = qtdeImoveisContribuinte;
    }

    public TipoDoctoOficial getTipoDoctoOficial() {
        return tipoDoctoOficial;
    }

    public void setTipoDoctoOficial(TipoDoctoOficial tipoDoctoOficial) {
        this.tipoDoctoOficial = tipoDoctoOficial;
    }


    public TipoLancamentoIsencaoIPTU getTipoLancamentoIsencaoIPTU() {
        return tipoLancamentoIsencaoIPTU;
    }

    public void setTipoLancamentoIsencaoIPTU(TipoLancamentoIsencaoIPTU tipoLancamentoIsencaoIPTU) {
        this.tipoLancamentoIsencaoIPTU = tipoLancamentoIsencaoIPTU;
    }

    public TipoCategoriaIsencaoIPTU getTipoCategoriaIsencaoIPTU() {
        return tipoCategoriaIsencaoIPTU;
    }

    public void setTipoCategoriaIsencaoIPTU(TipoCategoriaIsencaoIPTU tipoCategoriaIsencaoIPTU) {
        this.tipoCategoriaIsencaoIPTU = tipoCategoriaIsencaoIPTU;
    }

    public BigDecimal getValorInicialUltimoIptuAtivo() {
        return valorInicialUltimoIptuAtivo;
    }

    public void setValorInicialUltimoIptuAtivo(BigDecimal valorInicialUltimoIptuAtivo) {
        this.valorInicialUltimoIptuAtivo = valorInicialUltimoIptuAtivo;
    }

    public BigDecimal getValorFinalUltimoIptuAtivo() {
        return valorFinalUltimoIptuAtivo;
    }

    public void setValorFinalUltimoIptuAtivo(BigDecimal valorFinalUltimoIptuAtivo) {
        this.valorFinalUltimoIptuAtivo = valorFinalUltimoIptuAtivo;
    }

    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovel tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        try {
            return codigo + " " + (descricao != null ? descricao : "") + " (" + exercicioInicial.getAno() + " - " + exercicioFinal.getAno() + ")";
        } catch (NullPointerException ex) {
            return "";
        }
    }
}
