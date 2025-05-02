package br.com.webpublico.entidades;

import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.CaracterizadorDeBemImovel;
import br.com.webpublico.interfaces.CaracterizadorDeBemMovel;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC
 * Date: 04/12/13
 * Time: 10:01
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
@Etiqueta(value = "Estado do Bem", genero = "M")
public class EstadoBem extends SuperEntidade implements Cloneable {
    private static Logger logger = LoggerFactory.getLogger(EstadoBem.class);

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Identificação")
    private String identificacao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Grupo do Bem")
    @ManyToOne
    private GrupoBem grupoBem;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Grupo Objeto de Compra")
    @ManyToOne
    private GrupoObjetoCompra grupoObjetoCompra;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade Detentora Administrativa")
    @ManyToOne
    private UnidadeOrganizacional detentoraAdministrativa;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Unidade Detentora Orçamentária")
    @ManyToOne
    private UnidadeOrganizacional detentoraOrcamentaria;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Tipo de Aquisição")
    @Enumerated(EnumType.STRING)
    private TipoAquisicaoBem tipoAquisicaoBem;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Estado do Bem")
    @Enumerated(EnumType.STRING)
    private EstadoConservacaoBem estadoBem;

    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Situação de Conservação")
    @Enumerated(EnumType.STRING)
    private SituacaoConservacaoBem situacaoConservacaoBem;

    @Obrigatorio
    @Etiqueta("Tipo Grupo")
    @Enumerated(EnumType.STRING)
    private TipoGrupo tipoGrupo;

    @Monetario
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Valor Original")
    private BigDecimal valorOriginal;

    @Monetario
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Valor Acumulado da Amortização")
    private BigDecimal valorAcumuladoDaAmortizacao;

    @Monetario
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Valor Acumulado da Depreciação")
    private BigDecimal valorAcumuladoDaDepreciacao;

    @Monetario
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Valor Acumulado da Exaustão")
    private BigDecimal valorAcumuladoDaExaustao;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Valor Acumulado de Ajuste")
    private BigDecimal valorAcumuladoDeAjuste;

    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Valor Liquidado")
    private BigDecimal valorLiquidado;

    @Etiqueta("Localização")
    private String localizacao;

    public EstadoBem() {
        inicializarValoresAcumulados();
    }

    public EstadoBem(Long id) {
        setId(id);
    }

    public EstadoBem(CaracterizadorDeBemMovel cdbm) {
        super();
        this.valorOriginal = cdbm.getValorDoBem();
        this.identificacao = cdbm.getCodigoPatrimonio();
        this.estadoBem = cdbm.getEstadoConservacaoBem();
        this.situacaoConservacaoBem = cdbm.getSituacaoConservacaoBem();
        this.grupoBem = cdbm.getGrupoBem();
        this.grupoObjetoCompra = cdbm.getGrupoObjetoCompra();
        this.detentoraAdministrativa = cdbm.getUnidadeAdministrativa();
        this.detentoraOrcamentaria = cdbm.getUnidadeOrcamentaria();
        this.tipoAquisicaoBem = cdbm.getTipoAquisicaoBem();
        this.localizacao = cdbm.getLocalizacao();
        this.valorLiquidado = BigDecimal.ZERO;
        inicializarValoresAcumulados();
    }

    public EstadoBem(CaracterizadorDeBemImovel cdbm) {
        super();
        this.valorOriginal = cdbm.getValorDoBem();
        this.identificacao = cdbm.getCodigoPatrimonio();
        this.estadoBem = cdbm.getEstadoConservacaoBem();
        this.situacaoConservacaoBem = cdbm.getSituacaoConservacaoBem();
        this.grupoBem = cdbm.getGrupoBem();
        this.grupoObjetoCompra = cdbm.getGrupoObjetoCompra();
        this.detentoraAdministrativa = cdbm.getUnidadeAdministrativa();
        this.detentoraOrcamentaria = cdbm.getUnidadeOrcamentaria();
        this.tipoAquisicaoBem = cdbm.getTipoAquisicaoBem();
        this.localizacao = cdbm.getLocalizacao();
        inicializarValoresAcumulados();
    }

    private void inicializarValoresAcumulados() {
        this.valorAcumuladoDaAmortizacao = BigDecimal.ZERO;
        this.valorAcumuladoDaDepreciacao = BigDecimal.ZERO;
        this.valorAcumuladoDaExaustao = BigDecimal.ZERO;
        this.valorAcumuladoDeAjuste = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valor) {
        this.valorOriginal = valor;
    }

    public BigDecimal getValorAcumuladoDaAmortizacao() {
        return valorAcumuladoDaAmortizacao;
    }

    public void setValorAcumuladoDaAmortizacao(BigDecimal valorAcumuladoDaAmortizacao) {
        this.valorAcumuladoDaAmortizacao = valorAcumuladoDaAmortizacao;
    }

    public BigDecimal getValorAcumuladoDaDepreciacao() {
        return valorAcumuladoDaDepreciacao;
    }

    public void setValorAcumuladoDaDepreciacao(BigDecimal valorAcumuladoDaDepreciacao) {
        this.valorAcumuladoDaDepreciacao = valorAcumuladoDaDepreciacao;
    }

    public BigDecimal getValorAcumuladoDaExaustao() {
        return valorAcumuladoDaExaustao;
    }

    public void setValorAcumuladoDaExaustao(BigDecimal valorAcumuladoDaExaustao) {
        this.valorAcumuladoDaExaustao = valorAcumuladoDaExaustao;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public UnidadeOrganizacional getDetentoraAdministrativa() {
        return detentoraAdministrativa;
    }

    public void setDetentoraAdministrativa(UnidadeOrganizacional detentoraAdministrativa) {
        this.detentoraAdministrativa = detentoraAdministrativa;
    }

    public UnidadeOrganizacional getDetentoraOrcamentaria() {
        return detentoraOrcamentaria;
    }

    public void setDetentoraOrcamentaria(UnidadeOrganizacional detentoraOrcamentaria) {
        this.detentoraOrcamentaria = detentoraOrcamentaria;
    }

    public EstadoConservacaoBem getEstadoBem() {
        return estadoBem;
    }

    public void setEstadoBem(EstadoConservacaoBem estadoBem) {
        this.estadoBem = estadoBem;
    }

    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public BigDecimal getValorAcumuladoDeAjuste() {
        if (valorAcumuladoDeAjuste == null) {
            valorAcumuladoDeAjuste = BigDecimal.ZERO;
        }
        return valorAcumuladoDeAjuste;
    }

    public void setValorAcumuladoDeAjuste(BigDecimal valorAcumuladoDeAjuste) {
        this.valorAcumuladoDeAjuste = valorAcumuladoDeAjuste;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public void atualizarValorAcumuladoDaReducao(TipoReducaoValorBem tipoReducaoValorBem, BigDecimal novoValorAcumuladoDaReducao) {
        switch (tipoReducaoValorBem) {
            case AMORTIZACAO:
                valorAcumuladoDaAmortizacao = novoValorAcumuladoDaReducao;
                break;
            case DEPRECIACAO:
                valorAcumuladoDaDepreciacao = novoValorAcumuladoDaReducao;
                break;
            case EXAUSTAO:
                valorAcumuladoDaExaustao = novoValorAcumuladoDaReducao;
                break;
            default:
                throw new ExcecaoNegocioGenerica("Nenhum valor acumulado encontrado para o tipo de redução " + tipoReducaoValorBem.getDescricao());
        }
    }

    public BigDecimal getValorLiquido() {
        BigDecimal valorLiquido = BigDecimal.ZERO;
        valorLiquido = valorLiquido.add(this.getValorOriginal() != null ? this.getValorOriginal() : BigDecimal.ZERO);

        valorLiquido = valorLiquido.subtract(this.getValorAcumuladoDaAmortizacao() != null ? this.getValorAcumuladoDaAmortizacao() : BigDecimal.ZERO);
        valorLiquido = valorLiquido.subtract(this.getValorAcumuladoDaDepreciacao() != null ? this.getValorAcumuladoDaDepreciacao() : BigDecimal.ZERO);
        valorLiquido = valorLiquido.subtract(this.getValorAcumuladoDaExaustao() != null ? this.getValorAcumuladoDaExaustao() : BigDecimal.ZERO);

        valorLiquido = valorLiquido.subtract(this.getValorAcumuladoDeAjuste() != null ? this.getValorAcumuladoDeAjuste() : BigDecimal.ZERO);

        return valorLiquido;
    }

    public BigDecimal getValorDosAjustes() {
        BigDecimal ajustes = BigDecimal.ZERO;

        ajustes = ajustes.add(this.getValorAcumuladoDaAmortizacao() != null ? this.getValorAcumuladoDaAmortizacao() : BigDecimal.ZERO);
        ajustes = ajustes.add(this.getValorAcumuladoDaDepreciacao() != null ? this.getValorAcumuladoDaDepreciacao() : BigDecimal.ZERO);
        ajustes = ajustes.add(this.getValorAcumuladoDaExaustao() != null ? this.getValorAcumuladoDaExaustao() : BigDecimal.ZERO);
        ajustes = ajustes.add(this.getValorAcumuladoDeAjuste() != null ? this.getValorAcumuladoDeAjuste() : BigDecimal.ZERO);

        return ajustes;
    }

    @Override
    public EstadoBem clone() {
        EstadoBem novoEstadadoBem = null;
        try {
            novoEstadadoBem = (EstadoBem) super.clone();
            novoEstadadoBem.setId(null);
        } catch (CloneNotSupportedException e) {
            logger.error(e.getMessage());
        }
        return novoEstadadoBem;
    }

    public boolean hasValorAcumuladoDepreciacao() {
        return valorAcumuladoDaDepreciacao != null && valorAcumuladoDaDepreciacao.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorAcumuladoAmortizacao() {
        return valorAcumuladoDaAmortizacao != null && valorAcumuladoDaAmortizacao.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorAcumuladoAjuste() {
        return valorAcumuladoDeAjuste != null && valorAcumuladoDeAjuste.compareTo(BigDecimal.ZERO) > 0;
    }


    public BigDecimal getValorLiquidado() {
        return valorLiquidado;
    }

    public void setValorLiquidado(BigDecimal valorLiquidado) {
        this.valorLiquidado = valorLiquidado;
    }
}
