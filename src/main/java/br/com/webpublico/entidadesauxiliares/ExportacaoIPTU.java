package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoEndereco;
import br.com.webpublico.enums.tributario.TipoEnderecoExportacaoIPTU;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import java.math.BigDecimal;
import java.util.List;

public class ExportacaoIPTU {

    private String inscricaoInicial;
    private String inscricaoFinal;
    private Exercicio exercicio;
    private TipoImovelExportacao tipoImovel;
    private TipoEnderecoExportacaoIPTU tipoEndereco;
    private OpcaoFiltroExportacao opcaoFiltro;
    private BigDecimal valorInicial;
    private BigDecimal valorFinal;
    private Boolean geraParcelasJaImpressas;
    private Setor setor;
    private Lote lote;
    private Quadra quadra;
    private Bairro bairro;
    private Logradouro logradouro;
    private List<Setor> setores;
    private List<Lote> lotes;
    private List<Quadra> quadras;
    private List<Bairro> bairros;
    private List<Logradouro> logradouros;
    private List<VOCadastroImobiliario> cadastros;
    private List<VOCadastroImobiliario> cadastrosIgnorados;
    private Boolean cadastrosNumeroInvalido;
    private Boolean cadastrosCepInvalido;
    private Boolean novoSequencial;
    private Integer quantidadeParcelasInicial;
    private Integer quantidadeParcelasFinal;

    public ExportacaoIPTU() {
        setores = Lists.newArrayList();
        lotes = Lists.newArrayList();
        quadras = Lists.newArrayList();
        bairros = Lists.newArrayList();
        logradouros = Lists.newArrayList();
        cadastros = Lists.newArrayList();
        cadastrosIgnorados = Lists.newArrayList();
        cadastrosNumeroInvalido = Boolean.FALSE;
        cadastrosCepInvalido = Boolean.FALSE;
        novoSequencial = Boolean.FALSE;
        quantidadeParcelasInicial = 1;
        quantidadeParcelasFinal = 99;
    }

    public String getFiltros() {
        String filtro = "";
        if (inscricaoInicial != null) {
            filtro += " Inscrição Inicial: " + inscricaoInicial;
        }
        if (inscricaoFinal != null) {
            filtro += " Inscrição Final: " + inscricaoFinal;
        }
        if (exercicio != null) {
            filtro += " Exercício: " + exercicio;
        }
        if (tipoImovel != null) {
            filtro += " Imóveis: " + tipoImovel.getDescricao();
        }
        if (valorInicial != null) {
            filtro += " Valor Inicial do IPTU: " + Util.formataValor(valorInicial);
        }
        if (valorFinal != null) {
            filtro += " Valor Final do IPTU: " + Util.formataValor(valorFinal);
        }
        return filtro;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoImovelExportacao getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(TipoImovelExportacao tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public OpcaoFiltroExportacao getOpcaoFiltro() {
        return opcaoFiltro;
    }

    public void setOpcaoFiltro(OpcaoFiltroExportacao opcaoFiltro) {
        this.opcaoFiltro = opcaoFiltro;
    }

    public BigDecimal getValorInicial() {
        return valorInicial;
    }

    public void setValorInicial(BigDecimal valorInicial) {
        this.valorInicial = valorInicial;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Boolean getGeraParcelasJaImpressas() {
        return geraParcelasJaImpressas;
    }

    public void setGeraParcelasJaImpressas(Boolean geraParcelasJaImpressas) {
        this.geraParcelasJaImpressas = geraParcelasJaImpressas;
    }

    public Boolean getNovoSequencial() {
        return novoSequencial;
    }

    public void setNovoSequencial(Boolean novoSequencial) {
        this.novoSequencial = novoSequencial;
    }

    public enum TipoImovelExportacao {
        PREDIAL("Predial"),
        TERRITORIAL("Territorial"),
        AMBOS("Predial e Territorial");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private TipoImovelExportacao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum OpcaoFiltroExportacao {

        VALOR("Valor do I.P.T.U.");
        private String descricao;

        public String getDescricao() {
            return descricao;
        }

        private OpcaoFiltroExportacao(String descricao) {
            this.descricao = descricao;
        }
    }

    public Setor getSetor() {
        return setor;
    }

    public void setSetor(Setor setor) {
        this.setor = setor;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public Quadra getQuadra() {
        return quadra;
    }

    public void setQuadra(Quadra quadra) {
        this.quadra = quadra;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public List<Setor> getSetores() {
        return setores;
    }

    public void setSetores(List<Setor> setores) {
        this.setores = setores;
    }

    public List<Lote> getLotes() {
        return lotes;
    }

    public void setLotes(List<Lote> lotes) {
        this.lotes = lotes;
    }

    public List<Quadra> getQuadras() {
        return quadras;
    }

    public void setQuadras(List<Quadra> quadras) {
        this.quadras = quadras;
    }

    public List<Bairro> getBairros() {
        return bairros;
    }

    public void setBairros(List<Bairro> bairros) {
        this.bairros = bairros;
    }

    public List<Logradouro> getLogradouros() {
        return logradouros;
    }

    public void setLogradouros(List<Logradouro> logradouros) {
        this.logradouros = logradouros;
    }

    public List<VOCadastroImobiliario> getCadastros() {
        return cadastros;
    }

    public void setCadastros(List<VOCadastroImobiliario> cadastros) {
        this.cadastros = cadastros;
    }

    public List<VOCadastroImobiliario> getCadastrosIgnorados() {
        return cadastrosIgnorados;
    }

    public void setCadastrosIgnorados(List<VOCadastroImobiliario> cadastrosIgnorados) {
        this.cadastrosIgnorados = cadastrosIgnorados;
    }

    public Boolean getCadastrosNumeroInvalido() {
        return cadastrosNumeroInvalido;
    }

    public void setCadastrosNumeroInvalido(Boolean cadastrosNumeroInvalido) {
        this.cadastrosNumeroInvalido = cadastrosNumeroInvalido;
    }

    public TipoEnderecoExportacaoIPTU getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEnderecoExportacaoIPTU tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public Boolean getCadastrosCepInvalido() {
        return cadastrosCepInvalido;
    }

    public void setCadastrosCepInvalido(Boolean cadastrosCepInvalido) {
        this.cadastrosCepInvalido = cadastrosCepInvalido;
    }

    public Integer getQuantidadeParcelasInicial() {
        return quantidadeParcelasInicial;
    }

    public void setQuantidadeParcelasInicial(Integer quantidadeParcelasInicial) {
        this.quantidadeParcelasInicial = quantidadeParcelasInicial;
    }

    public Integer getQuantidadeParcelasFinal() {
        return quantidadeParcelasFinal;
    }

    public void setQuantidadeParcelasFinal(Integer quantidadeParcelasFinal) {
        this.quantidadeParcelasFinal = quantidadeParcelasFinal;
    }
}
