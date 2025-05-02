package br.com.webpublico.entidades.rh.creditodesalario.caixa;

import br.com.webpublico.entidades.DetentorArquivoComposicao;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.enums.TipoFolhaDePagamento;
import br.com.webpublico.enums.rh.creditosalario.BancoCreditoSalario;
import br.com.webpublico.enums.rh.creditosalario.FormaLancamentoCreditoSalario;
import br.com.webpublico.enums.rh.creditosalario.TipoOperacaoCreditoSalario;
import br.com.webpublico.enums.rh.creditosalario.TipoServicoCreditoSalario;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.PossuidorArquivo;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "RH")
@Etiqueta("Arquivo - Retorno Crédito de Salário")
public class RetornoCreditoSalario extends SuperEntidade implements PossuidorArquivo {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Etiqueta("Nome do Arquivo")
    private String nomeArquivo;

    @Etiqueta("Convenio")
    private String numeroConvenio;

    @Etiqueta("Sequência do Arquivo")
    private Long seguencial;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Mês do Arquivo")
    @Enumerated(EnumType.STRING)
    private Mes mes;

    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Folha de Pagamento")
    private TipoFolhaDePagamento tipoFolhaDePagamento;

    @Tabelavel
    @Pesquisavel
    @Etiqueta("Ano do Arquivo")
    @ManyToOne
    private Exercicio exercicio;

    @Temporal(TemporalType.TIMESTAMP)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data de Importação")
    private Date dataRegistro;

    @ManyToOne(cascade = CascadeType.ALL)
    private DetentorArquivoComposicao detentorArquivoComposicao;

    @OneToMany(mappedBy = "retornoCreditoSalario", cascade = CascadeType.ALL, orphanRemoval = true)
    @Invisivel
    private List<ItemRetornoCreditoSalario> itemRetornoCreditoSalario;

    private String ocorrenciasHeaderArquivo;
    private String ocorrenciasHeaderLote;
    private String ocorrenciasTraillerLote;
    @Enumerated(EnumType.STRING)
    private BancoCreditoSalario bancoCreditoSalario;
    private Integer versaoFolha;
    private Integer loteServicoHeader;
    @Enumerated(EnumType.STRING)
    private TipoOperacaoCreditoSalario tipoOperacao;
    @Enumerated(EnumType.STRING)
    private TipoServicoCreditoSalario tipoServico;
    @Enumerated(EnumType.STRING)
    private FormaLancamentoCreditoSalario formaLancamento;
    private Integer loteServicoTrailler;
    private Integer quantidadeRegistroLote;
    private Integer quantidadeLotes;
    private Integer quantidadeRegistros;
    private BigDecimal totalValores;

    public RetornoCreditoSalario() {
        itemRetornoCreditoSalario = Lists.newArrayList();
        detentorArquivoComposicao = new DetentorArquivoComposicao();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public String getNumeroConvenio() {
        return numeroConvenio;
    }

    public void setNumeroConvenio(String numeroConvenio) {
        this.numeroConvenio = numeroConvenio;
    }

    public Long getSeguencial() {
        return seguencial;
    }

    public void setSeguencial(Long seguencial) {
        this.seguencial = seguencial;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMes() {
        return mes;
    }

    public TipoFolhaDePagamento getTipoFolhaDePagamento() {
        return tipoFolhaDePagamento;
    }

    public void setTipoFolhaDePagamento(TipoFolhaDePagamento tipoFolhaDePagamento) {
        this.tipoFolhaDePagamento = tipoFolhaDePagamento;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getOcorrenciasHeaderArquivo() {
        return ocorrenciasHeaderArquivo;
    }

    public void setOcorrenciasHeaderArquivo(String ocorrenciasHeaderArquivo) {
        this.ocorrenciasHeaderArquivo = ocorrenciasHeaderArquivo;
    }

    public String getOcorrenciasHeaderLote() {
        return ocorrenciasHeaderLote;
    }

    public void setOcorrenciasHeaderLote(String ocorrenciasHeaderLote) {
        this.ocorrenciasHeaderLote = ocorrenciasHeaderLote;
    }

    public String getOcorrenciasTraillerLote() {
        return ocorrenciasTraillerLote;
    }

    public void setOcorrenciasTraillerLote(String ocorrenciasTraillerLote) {
        this.ocorrenciasTraillerLote = ocorrenciasTraillerLote;
    }

    public BancoCreditoSalario getBancoCreditoSalario() {
        return bancoCreditoSalario;
    }

    public void setBancoCreditoSalario(BancoCreditoSalario bancoCreditoSalario) {
        this.bancoCreditoSalario = bancoCreditoSalario;
    }

    public Integer getVersaoFolha() {
        return versaoFolha;
    }

    public void setVersaoFolha(Integer versaoFolha) {
        this.versaoFolha = versaoFolha;
    }

    public Integer getLoteServicoHeader() {
        return loteServicoHeader;
    }

    public void setLoteServicoHeader(Integer loteServicoHeader) {
        this.loteServicoHeader = loteServicoHeader;
    }

    public TipoOperacaoCreditoSalario getTipoOperacao() {
        return tipoOperacao;
    }

    public void setTipoOperacao(TipoOperacaoCreditoSalario tipoOperacao) {
        this.tipoOperacao = tipoOperacao;
    }

    public TipoServicoCreditoSalario getTipoServico() {
        return tipoServico;
    }

    public void setTipoServico(TipoServicoCreditoSalario tipoServico) {
        this.tipoServico = tipoServico;
    }

    public FormaLancamentoCreditoSalario getFormaLancamento() {
        return formaLancamento;
    }

    public void setFormaLancamento(FormaLancamentoCreditoSalario formaLancamento) {
        this.formaLancamento = formaLancamento;
    }

    public Integer getLoteServicoTrailler() {
        return loteServicoTrailler;
    }

    public void setLoteServicoTrailler(Integer loteServicoTrailler) {
        this.loteServicoTrailler = loteServicoTrailler;
    }

    public Integer getQuantidadeRegistroLote() {
        return quantidadeRegistroLote;
    }

    public void setQuantidadeRegistroLote(Integer quantidadeRegistroLote) {
        this.quantidadeRegistroLote = quantidadeRegistroLote;
    }

    public Integer getQuantidadeLotes() {
        return quantidadeLotes;
    }

    public void setQuantidadeLotes(Integer quantidadeLotes) {
        this.quantidadeLotes = quantidadeLotes;
    }

    public Integer getQuantidadeRegistros() {
        return quantidadeRegistros;
    }

    public void setQuantidadeRegistros(Integer quantidadeRegistros) {
        this.quantidadeRegistros = quantidadeRegistros;
    }

    public BigDecimal getTotalValores() {
        return totalValores;
    }

    public void setTotalValores(BigDecimal totalValores) {
        this.totalValores = totalValores;
    }

    @Override
    public DetentorArquivoComposicao getDetentorArquivoComposicao() {
        return detentorArquivoComposicao;
    }

    @Override
    public void setDetentorArquivoComposicao(DetentorArquivoComposicao detentorArquivoComposicao) {
        this.detentorArquivoComposicao = detentorArquivoComposicao;
    }

    public List<ItemRetornoCreditoSalario> getItemRetornoCreditoSalario() {
        return itemRetornoCreditoSalario;
    }

    public void setItemRetornoCreditoSalario(List<ItemRetornoCreditoSalario> itemRetornoCreditoSalario) {
        this.itemRetornoCreditoSalario = itemRetornoCreditoSalario;
    }

    public ItemRetornoCreditoSalario getItemPorIndice(int indice) {
        for (ItemRetornoCreditoSalario item : itemRetornoCreditoSalario) {
            if (item.getIndice() == indice) {
                return item;
            }
        }
        throw new ExcecaoNegocioGenerica("Item não encontrado com o indice: " + indice);
    }
}



