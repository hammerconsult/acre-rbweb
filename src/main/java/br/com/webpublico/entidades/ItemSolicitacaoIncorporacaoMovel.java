package br.com.webpublico.entidades;

import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoConservacaoBem;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.enums.TipoAquisicaoBem;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CaracterizadorDeBemMovel;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 28/01/2016.
 */
@Entity
@Audited
@Table(name = "ITEMSOLICINCORPORACAOMOVEL")
public class ItemSolicitacaoIncorporacaoMovel extends SuperEntidade implements CaracterizadorDeBemMovel {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Registro Patrimonial")
    @Obrigatorio
    private String codigoPatrimonio;

    @Pesquisavel
    @Etiqueta("Registro Anterior")
    private String codigoAnterior;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Item Patrimonial")
    @ManyToOne
    private ObjetoCompra item;

    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Descrição do Bem")
    private String descricaoBem;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Marca")
    private String marca;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Modelo")
    private String modelo;

    @Pesquisavel
    @Tabelavel
    @Etiqueta("Localização")
    private String localizacao;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Estado de Conservação")
    @Enumerated(EnumType.STRING)
    private EstadoConservacaoBem estadoConservacaoBem;

    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Situação de Conservação")
    @Enumerated(EnumType.STRING)
    private SituacaoConservacaoBem situacaoConservacaoBem;

    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    @UFM
    @Etiqueta("Valor do Bem (R$)")
    private BigDecimal valorBem;

    @Transient
    private GrupoBem grupoBem;

    @ManyToOne(cascade = CascadeType.ALL)
    private Seguradora seguradora;

    @ManyToOne(cascade = CascadeType.ALL)
    private Garantia garantia;

    private BigDecimal quantidade = BigDecimal.ONE;

    @ManyToOne
    private SolicitacaoIncorporacaoMovel solicitacao;

    public ItemSolicitacaoIncorporacaoMovel() {
        super();
    }

    public ItemSolicitacaoIncorporacaoMovel(SolicitacaoIncorporacaoMovel solicitacao) {
        super();
        this.solicitacao = solicitacao;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getCodigoPatrimonio() {
        return codigoPatrimonio;
    }

    public void setCodigoPatrimonio(String codigoPatrimonio) {
        this.codigoPatrimonio = codigoPatrimonio;
    }

    public String getCodigoAnterior() {
        return codigoAnterior;
    }

    public void setCodigoAnterior(String codigoAnterior) {
        this.codigoAnterior = codigoAnterior;
    }

    public ObjetoCompra getItem() {
        return item;
    }

    public void setItem(ObjetoCompra item) {
        this.item = item;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    @Override
    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    @Override
    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    @Override
    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }


    @Override
    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    @Override
    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    public BigDecimal getValorBem() {
        return valorBem;
    }

    public void setValorBem(BigDecimal valorBem) {
        this.valorBem = valorBem;
    }

    public SolicitacaoIncorporacaoMovel getSolicitacao() {
        return solicitacao;
    }

    public void setSolicitacao(SolicitacaoIncorporacaoMovel solicitacao) {
        this.solicitacao = solicitacao;
    }

    @Override
    public GrupoBem getGrupoBem() {
        return grupoBem;
    }


    @Override
    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public void validarNegocio() throws ValidacaoException {
        ValidacaoException ve = new ValidacaoException();

        if (valorBem == null || valorBem.compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemError(SummaryMessages.OPERACAO_NAO_PERMITIDA, "O valor do bem deve ser positivo.");
        }

        if (this.item == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Item Patrimonial é obrigatório!");
        }
        if (this.descricaoBem == null || this.descricaoBem.trim().isEmpty()) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Descrição do Bem é obrigatório!");
        }

        if (this.estadoConservacaoBem == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Estado de Conservação é obrigatório!");
        }

        if (this.situacaoConservacaoBem == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Situação de Conservação é obrigatório!");
        }

        if (this.valorBem == null) {
            ve.adicionarMensagemError(SummaryMessages.CAMPO_OBRIGATORIO, "O campo Valor do Bem (R$) é obrigatório!");
        }

        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public Boolean getVazioOuNullCodigoAnterior() {
        return codigoAnterior == null ? Boolean.TRUE : codigoAnterior.isEmpty();
    }

    @Override
    public Date getDataDaOperacao() {
        return solicitacao.getDataSolicitacao();
    }

    @Override
    public Date getDataLancamento() {
        return solicitacao.getDataAquisicao();
    }

    @Override
    public BigDecimal getValorDoBem() {
        return valorBem;
    }

    @Override
    public String getDescricaoDoBem() {
        return descricaoBem;
    }

    @Override
    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return solicitacao.getUnidadeAdministrativa();
    }

    @Override
    public UnidadeOrganizacional getUnidadeOrcamentaria() {
        return solicitacao.getUnidadeOrcamentaria();
    }

    @Override
    public ObjetoCompra getObjetoCompra() {
        return item;
    }

    @Override
    public String getRegistroAnterior() {
        return codigoAnterior;
    }

    @Override
    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return item.getGrupoObjetoCompra();
    }

    @Override
    public String getObservacao() {
        return solicitacao.getObservacao();
    }

    @Override
    public DetentorOrigemRecurso getDetentorOrigemRecurso() {
        return solicitacao.getDetentorOrigemRecurso();
    }

    @Override
    public Pessoa getFornecedor() {
        return solicitacao.getFornecedor();
    }

    @Override
    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return solicitacao.getTipoAquisicaoBem();
    }

    public Seguradora getSeguradora() {
        return seguradora;
    }

    public void setSeguradora(Seguradora seguradora) {
        this.seguradora = seguradora;
    }

    public Garantia getGarantia() {
        return garantia;
    }

    public void setGarantia(Garantia garantia) {
        this.garantia = garantia;
    }

    @Override
    public List<BemNotaFiscal> getNotasFiscais() {
        List<BemNotaFiscal> notasFiscais = Lists.newArrayList();
        if (solicitacao.getDataNotaFiscal() != null || !StringUtil.tratarCampoVazio(solicitacao.getNotaFiscal()).isEmpty()) {
            BemNotaFiscal bemNotaFiscal = new BemNotaFiscal();
            bemNotaFiscal.setDataNotaFiscal(solicitacao.getDataNotaFiscal());
            bemNotaFiscal.setNumeroNotaFiscal(solicitacao.getNotaFiscal());
            if (solicitacao.getDataNotaEmpenho() != null || (solicitacao.getNotaEmpenho() != null && solicitacao.getNotaEmpenho() > 0)) {
                BemNotaFiscalEmpenho bemNotaFiscalEmpenho = new BemNotaFiscalEmpenho();
                bemNotaFiscalEmpenho.setBemNotaFiscal(bemNotaFiscal);
                bemNotaFiscalEmpenho.setNumeroEmpenho(solicitacao.getNotaEmpenho().toString());
                bemNotaFiscalEmpenho.setDataEmpenho(solicitacao.getDataNotaEmpenho());
                bemNotaFiscal.getEmpenhos().add(bemNotaFiscalEmpenho);
            }
            notasFiscais.add(bemNotaFiscal);
        }
        return notasFiscais;
    }
}
