package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.controle.PaginacaoControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ClassificacaoAtividade;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.interfaces.AssistentePaginacao;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by HardRock on 11/04/2017.
 */
public class AuditoriaCadastroAssistente implements AssistentePaginacao {

    private List<AuditoriaCadastro> auditoriaCadastros;
    private String mensagem;
    private CadastroImobiliario bciRevisaoAtual;
    private CadastroImobiliario bciRevisaoAnterior;
    private CadastroEconomico bceRevisaoAtual;
    private CadastroEconomico bceRevisaoAnterior;
    private PessoaFisica pessoaFisicaAtual;
    private PessoaFisica pessoaFisicaAnterior;
    private PessoaJuridica pessoaJuridicaAtual;
    private PessoaJuridica pessoaJuridicaAnterior;
    private String styleDePara;
    private List<Propriedade> propriedadesHistoricoAtual;
    private List<Propriedade> propriedadesHistoricoAnterior;
    private RG rgAnterior;
    private RG rgAtual;

    //    Paginação
    private Integer totalRegistro;
    private Integer indexPagina;
    private Integer tamanhoListaPorPagina;
    private Double totalRegistroPorPagina;
    private List<PaginacaoControlador.Pagina> reiniciarPaginacao;


    //    Filtros geral
    private TipoCadastroTributario tipoCadastroTributario;
    private Date dataInicial;
    private Date dataFinal;
    private UsuarioSistema usuarioSistema;
    private TipoRelatorio tipoRelatorio;
    private TipoMovimento tipoMovimento;

    //    Filtros cadastro imobiliário
    private String setorInicial;
    private String setorFinal;
    private String quadraInicial;
    private String quadraFinal;
    private String loteInicial;
    private String loteFinal;
    private Logradouro logradouro;
    private Bairro bairro;
    private String inscricaoBciInicial;
    private String inscricaoBciFinal;
    private Propriedade proprietario;
    private Compromissario compromissario;

    //    Filtros cadastro econômico
    private ClassificacaoAtividade classificacaoAtividade;
    private NaturezaJuridica naturezaJuridica;
    private GrauDeRiscoDTO grauDeRisco;
    private TipoAutonomo tipoAutonomo;
    private String inscricaoBceInicial;
    private String inscricaoBceFinal;
    private Boolean mei;

    //    Filtro contribuinte
    private Pessoa pessoa;

    public AuditoriaCadastroAssistente(List<AuditoriaCadastro> auditoriaCadastros) {
        this.auditoriaCadastros = auditoriaCadastros;
    }

    public AuditoriaCadastroAssistente() {
        propriedadesHistoricoAtual = Lists.newArrayList();
        propriedadesHistoricoAnterior = Lists.newArrayList();
        iniciarAtributosPaginacao();
    }

    public void iniciarAtributosPaginacao() {
        indexPagina = 0;
        totalRegistro = 0;
        tamanhoListaPorPagina = 0;
        totalRegistroPorPagina = 10.0;
    }

    public List<AuditoriaCadastro> getAuditoriaCadastros() {
        return auditoriaCadastros;
    }

    public void setAuditoriaCadastros(List<AuditoriaCadastro> auditoriaCadastros) {
        this.auditoriaCadastros = auditoriaCadastros;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public CadastroImobiliario getBciRevisaoAtual() {
        return bciRevisaoAtual;
    }

    public void setBciRevisaoAtual(CadastroImobiliario bciRevisaoAtual) {
        this.bciRevisaoAtual = bciRevisaoAtual;
    }

    public CadastroImobiliario getBciRevisaoAnterior() {
        return bciRevisaoAnterior;
    }

    public void setBciRevisaoAnterior(CadastroImobiliario bciRevisaoAnterior) {
        this.bciRevisaoAnterior = bciRevisaoAnterior;
    }

    public CadastroEconomico getBceRevisaoAtual() {
        return bceRevisaoAtual;
    }

    public void setBceRevisaoAtual(CadastroEconomico bceRevisaoAtual) {
        this.bceRevisaoAtual = bceRevisaoAtual;
    }

    public CadastroEconomico getBceRevisaoAnterior() {
        return bceRevisaoAnterior;
    }

    public void setBceRevisaoAnterior(CadastroEconomico bceRevisaoAnterior) {
        this.bceRevisaoAnterior = bceRevisaoAnterior;
    }

    public PessoaFisica getPessoaFisicaAtual() {
        return pessoaFisicaAtual;
    }

    public void setPessoaFisicaAtual(PessoaFisica pessoaFisicaAtual) {
        this.pessoaFisicaAtual = pessoaFisicaAtual;
    }

    public PessoaFisica getPessoaFisicaAnterior() {
        return pessoaFisicaAnterior;
    }

    public void setPessoaFisicaAnterior(PessoaFisica pessoaFisicaAnterior) {
        this.pessoaFisicaAnterior = pessoaFisicaAnterior;
    }

    public PessoaJuridica getPessoaJuridicaAtual() {
        return pessoaJuridicaAtual;
    }

    public void setPessoaJuridicaAtual(PessoaJuridica pessoaJuridicaAtual) {
        this.pessoaJuridicaAtual = pessoaJuridicaAtual;
    }

    public PessoaJuridica getPessoaJuridicaAnterior() {
        return pessoaJuridicaAnterior;
    }

    public void setPessoaJuridicaAnterior(PessoaJuridica pessoaJuridicaAnterior) {
        this.pessoaJuridicaAnterior = pessoaJuridicaAnterior;
    }

    public String getStyleDePara() {
        return styleDePara;
    }

    public void setStyleDePara(String styleDePara) {
        this.styleDePara = styleDePara;
    }

    public List<Propriedade> getPropriedadesHistoricoAtual() {
        return propriedadesHistoricoAtual;
    }

    public void setPropriedadesHistoricoAtual(List<Propriedade> propriedadesHistoricoAtual) {
        this.propriedadesHistoricoAtual = propriedadesHistoricoAtual;
    }

    public List<Propriedade> getPropriedadesHistoricoAnterior() {
        return propriedadesHistoricoAnterior;
    }

    public void setPropriedadesHistoricoAnterior(List<Propriedade> propriedadesHistoricoAnterior) {
        this.propriedadesHistoricoAnterior = propriedadesHistoricoAnterior;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public TipoMovimento getTipoMovimento() {
        return tipoMovimento;
    }

    public void setTipoMovimento(TipoMovimento tipoMovimento) {
        this.tipoMovimento = tipoMovimento;
    }

    public String getSetorInicial() {
        return setorInicial;
    }

    public void setSetorInicial(String setorInicial) {
        this.setorInicial = setorInicial;
    }

    public String getSetorFinal() {
        return setorFinal;
    }

    public void setSetorFinal(String setorFinal) {
        this.setorFinal = setorFinal;
    }

    public String getQuadraInicial() {
        return quadraInicial;
    }

    public void setQuadraInicial(String quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public String getQuadraFinal() {
        return quadraFinal;
    }

    public void setQuadraFinal(String quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public String getLoteInicial() {
        return loteInicial;
    }

    public void setLoteInicial(String loteInicial) {
        this.loteInicial = loteInicial;
    }

    public String getLoteFinal() {
        return loteFinal;
    }

    public void setLoteFinal(String loteFinal) {
        this.loteFinal = loteFinal;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public String getInscricaoBciInicial() {
        return inscricaoBciInicial;
    }

    public void setInscricaoBciInicial(String inscricaoBciInicial) {
        this.inscricaoBciInicial = inscricaoBciInicial;
    }

    public String getInscricaoBciFinal() {
        return inscricaoBciFinal;
    }

    public void setInscricaoBciFinal(String inscricaoBciFinal) {
        this.inscricaoBciFinal = inscricaoBciFinal;
    }

    public Propriedade getProprietario() {
        return proprietario;
    }

    public void setProprietario(Propriedade proprietario) {
        this.proprietario = proprietario;
    }

    public Compromissario getCompromissario() {
        return compromissario;
    }

    public void setCompromissario(Compromissario compromissario) {
        this.compromissario = compromissario;
    }

    public ClassificacaoAtividade getClassificacaoAtividade() {
        return classificacaoAtividade;
    }

    public void setClassificacaoAtividade(ClassificacaoAtividade classificacaoAtividade) {
        this.classificacaoAtividade = classificacaoAtividade;
    }

    public NaturezaJuridica getNaturezaJuridica() {
        return naturezaJuridica;
    }

    public void setNaturezaJuridica(NaturezaJuridica naturezaJuridica) {
        this.naturezaJuridica = naturezaJuridica;
    }

    public GrauDeRiscoDTO getGrauDeRisco() {
        return grauDeRisco;
    }

    public void setGrauDeRisco(GrauDeRiscoDTO grauDeRisco) {
        this.grauDeRisco = grauDeRisco;
    }

    public TipoAutonomo getTipoAutonomo() {
        return tipoAutonomo;
    }

    public void setTipoAutonomo(TipoAutonomo tipoAutonomo) {
        this.tipoAutonomo = tipoAutonomo;
    }

    public String getInscricaoBceInicial() {
        return inscricaoBceInicial;
    }

    public void setInscricaoBceInicial(String inscricaoBceInicial) {
        this.inscricaoBceInicial = inscricaoBceInicial;
    }

    public String getInscricaoBceFinal() {
        return inscricaoBceFinal;
    }

    public void setInscricaoBceFinal(String inscricaoBceFinal) {
        this.inscricaoBceFinal = inscricaoBceFinal;
    }

    public Boolean getMei() {
        return mei != null ? mei : false;
    }

    public void setMei(Boolean mei) {
        this.mei = mei;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
    }

    public RG getRgAnterior() {
        return rgAnterior;
    }

    public void setRgAnterior(RG rgAnterior) {
        this.rgAnterior = rgAnterior;
    }

    public RG getRgAtual() {
        return rgAtual;
    }

    public void setRgAtual(RG rgAtual) {
        this.rgAtual = rgAtual;
    }

    @Override
    public Integer getTotalRegistro() {
        return totalRegistro;
    }

    @Override
    public void setTotalRegistro(Integer totalRegistro) {
        this.totalRegistro = totalRegistro;
    }

    @Override
    public Double getTotalRegistroPorPagina() {
        return totalRegistroPorPagina;
    }

    @Override
    public Integer getIndexPagina() {
        return indexPagina;
    }

    @Override
    public void setIndexPagina(Integer indexPagina) {
        this.indexPagina = indexPagina;
    }

    @Override
    public Integer getTamanhoListaPorPagina() {
        return tamanhoListaPorPagina;
    }

    @Override
    public void setTamanhoListaPorPagina(Integer tamanhoListaPorPagina) {
        this.tamanhoListaPorPagina = tamanhoListaPorPagina;
    }

    public enum TipoRelatorio {
        ANALITICO("Analítico"),
        SINTETICO("Sintético");
        private String descricao;

        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public enum TipoMovimento {
        INCLUSAO("Inclusão", 0),
        ALTERACAO("Alteração", 1),
        EXCLUISAO("Exclusão", 2);

        private String descricao;
        private Integer value;

        TipoMovimento(String descricao, Integer value) {
            this.descricao = descricao;
            this.value = value;
        }

        public String getDescricao() {
            return descricao;
        }

        public Integer getValue() {
            return value;
        }
    }

    public void novo() {
        iniciarAtributosPaginacao();
        iniciarVariaveisGeral();
        iniciarVariaveisCadastroImobiliario();
        iniciarVariaveisCadastroEconomico();
        iniciarVariaveisCadastroContribuinte();
    }

    public boolean isCadastroEconomico() {
        return this.tipoCadastroTributario != null && TipoCadastroTributario.ECONOMICO.equals(this.tipoCadastroTributario);
    }

    public boolean isCadastroImobiliario() {
        return this.tipoCadastroTributario != null && TipoCadastroTributario.IMOBILIARIO.equals(this.tipoCadastroTributario);
    }

    public boolean isCadastroContribuinte() {
        return this.tipoCadastroTributario != null && TipoCadastroTributario.PESSOA.equals(this.tipoCadastroTributario);
    }

    public boolean isRelatorioAnalitico() {
        return this.tipoRelatorio != null && TipoRelatorio.ANALITICO.equals(this.tipoRelatorio);
    }

    public boolean isRelatorioSintetico() {
        return this.tipoRelatorio != null && TipoRelatorio.SINTETICO.equals(this.tipoRelatorio);
    }

    public void iniciarVariaveisCadastroEconomico() {
        this.classificacaoAtividade = null;
        this.naturezaJuridica = null;
        this.grauDeRisco = null;
        this.tipoAutonomo = null;
    }

    public void iniciarVariaveisCadastroImobiliario() {
        this.setorInicial = "";
        this.setorFinal = "";
        this.loteInicial = "";
        this.loteFinal = "";
        this.logradouro = null;
        this.bairro = null;
        this.inscricaoBciInicial = "";
        this.inscricaoBciFinal = "";
        this.inscricaoBceInicial = "";
        this.inscricaoBceFinal = "";
        this.proprietario = null;
        this.compromissario = null;
    }

    public void iniciarVariaveisCadastroContribuinte() {
        this.pessoa = null;
    }

    public void iniciarVariaveisGeral() {
        this.mensagem = "";
        this.dataInicial = null;
        this.dataFinal = new Date();
        this.usuarioSistema = null;
        this.tipoMovimento = null;
        this.tipoCadastroTributario = null;
        if (this.auditoriaCadastros != null) {
            auditoriaCadastros.clear();
        }
    }
}
