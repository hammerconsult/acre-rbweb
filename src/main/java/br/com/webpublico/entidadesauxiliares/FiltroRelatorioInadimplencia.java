package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.tributario.enumeration.GrauDeRiscoDTO;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created by HardRock on 12/05/2017.
 */
public class FiltroRelatorioInadimplencia implements Comparable<FiltroRelatorioInadimplencia> {

    private static final Logger log = LoggerFactory.getLogger(FiltroMaioresDevedores.class);
    private Date dataRelatorio;
    private Exercicio exercicioDivida;
    private Date vencimentoInicial;
    private Date vencimentoFinal;
    private Date lancamentoInicial;
    private Date lancamentoFinal;
    private TipoCadastroTributario tipoCadastroTributario;
    private Integer quantidadeInadimplencias;
    private String texto;
    private Ordenacao ordenacao;
    private OrdenacaoInadimplencia ordenacaoInadimplencia;
    private TipoDebito tipoDebito;
    private Divida divida;
    private List<Divida> listaDividas;
    private Boolean valoresAtualizados;
    private Boolean detalhado;
    private String filtros;

    private TipoPessoa tipoPessoa;
    private Pessoa pessoa;

    private String inscricaoBciInicial;
    private String inscricaoBciFinal;
    private TipoImovel tipoImovelBci;
    private Bairro bairroBci;
    private Logradouro logradouroBci;
    private String setorBciInicial;
    private String setorBciFinal;
    private String quadraBciInicial;
    private String quadraBciFinal;
    private String loteBciInicial;
    private String loteBciFinal;

    private String cmcInicial;
    private String cmcFinal;
    private Bairro bairroCmc;
    private Logradouro logradouroCmc;
    private ClassificacaoAtividade classificacaoAtividade;
    private NaturezaJuridica naturezaJuridica;
    private GrauDeRiscoDTO grauDeRisco;
    private TipoAutonomo tipoAutonomo;

    private String inscricaoRuralInicial;
    private String inscricaoRuralFinal;
    private String localizacaoLote;
    private String numeroIncra;

    public FiltroRelatorioInadimplencia() {
        listaDividas = Lists.newArrayList();
        ordenacao = Ordenacao.DECRESCENTE;
        quantidadeInadimplencias = 20;
        tipoPessoa = null;
        valoresAtualizados = true;
        detalhado = false;
        dataRelatorio = new Date();
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public Date getDataRelatorio() {
        return dataRelatorio;
    }

    public static Logger getLog() {
        return log;
    }

    public Exercicio getExercicioDivida() {
        return exercicioDivida;
    }

    public void setExercicioDivida(Exercicio exercicioDivida) {
        this.exercicioDivida = exercicioDivida;
    }

    public Date getVencimentoInicial() {
        return vencimentoInicial;
    }

    public void setVencimentoInicial(Date vencimentoInicial) {
        this.vencimentoInicial = vencimentoInicial;
    }

    public Date getVencimentoFinal() {
        return vencimentoFinal;
    }

    public void setVencimentoFinal(Date vencimentoFinal) {
        this.vencimentoFinal = vencimentoFinal;
    }

    public Date getLancamentoInicial() {
        return lancamentoInicial;
    }

    public void setLancamentoInicial(Date lancamentoInicial) {
        this.lancamentoInicial = lancamentoInicial;
    }

    public Date getLancamentoFinal() {
        return lancamentoFinal;
    }

    public void setLancamentoFinal(Date lancamentoFinal) {
        this.lancamentoFinal = lancamentoFinal;
    }

    public TipoCadastroTributario getTipoCadastroTributario() {
        return tipoCadastroTributario;
    }

    public void setTipoCadastroTributario(TipoCadastroTributario tipoCadastroTributario) {
        this.tipoCadastroTributario = tipoCadastroTributario;
    }

    public Integer getQuantidadeInadimplencias() {
        return quantidadeInadimplencias;
    }

    public void setQuantidadeInadimplencias(Integer quantidadeInadimplencias) {
        this.quantidadeInadimplencias = quantidadeInadimplencias;
    }

    public Ordenacao getOrdenacao() {
        return ordenacao;
    }

    public void setOrdenacao(Ordenacao ordenacao) {
        this.ordenacao = ordenacao;
    }

    public OrdenacaoInadimplencia getOrdenacaoInadimplencia() {
        return ordenacaoInadimplencia;
    }

    public void setOrdenacaoInadimplencia(OrdenacaoInadimplencia ordenacaoInadimplencia) {
        this.ordenacaoInadimplencia = ordenacaoInadimplencia;
    }

    public TipoDebito getTipoDebito() {
        return tipoDebito;
    }

    public void setTipoDebito(TipoDebito tipoDebito) {
        this.tipoDebito = tipoDebito;
    }

    public List<Divida> getListaDividas() {
        return listaDividas;
    }

    public List<Long> getListaIdDividas() {
        List<Long> retorno = Lists.newArrayList();
        if (listaDividas != null) {
            for (Divida divida : listaDividas) {
                retorno.add(divida.getId());
            }
        }
        return retorno;
    }

    public void setListaDividas(List<Divida> listaDividas) {
        this.listaDividas = listaDividas;
    }

    public Boolean getValoresAtualizados() {
        return valoresAtualizados;
    }

    public void setValoresAtualizados(Boolean valoresAtualizados) {
        this.valoresAtualizados = valoresAtualizados;
    }

    public Boolean getDetalhado() {
        return detalhado;
    }

    public void setDetalhado(Boolean detalhado) {
        this.detalhado = detalhado;
    }

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public Pessoa getPessoa() {
        return pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
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

    public TipoImovel getTipoImovelBci() {
        return tipoImovelBci;
    }

    public void setTipoImovelBci(TipoImovel tipoImovelBci) {
        this.tipoImovelBci = tipoImovelBci;
    }

    public Bairro getBairroBci() {
        return bairroBci;
    }

    public void setBairroBci(Bairro bairroBci) {
        this.bairroBci = bairroBci;
    }

    public Logradouro getLogradouroBci() {
        return logradouroBci;
    }

    public void setLogradouroBci(Logradouro logradouroBci) {
        this.logradouroBci = logradouroBci;
    }

    public String getSetorBciInicial() {
        return setorBciInicial;
    }

    public void setSetorBciInicial(String setorBciInicial) {
        this.setorBciInicial = setorBciInicial;
    }

    public String getSetorBciFinal() {
        return setorBciFinal;
    }

    public void setSetorBciFinal(String setorBciFinal) {
        this.setorBciFinal = setorBciFinal;
    }

    public String getQuadraBciInicial() {
        return quadraBciInicial;
    }

    public void setQuadraBciInicial(String quadraBciInicial) {
        this.quadraBciInicial = quadraBciInicial;
    }

    public String getQuadraBciFinal() {
        return quadraBciFinal;
    }

    public void setQuadraBciFinal(String quadraBciFinal) {
        this.quadraBciFinal = quadraBciFinal;
    }

    public String getLoteBciInicial() {
        return loteBciInicial;
    }

    public void setLoteBciInicial(String loteBciInicial) {
        this.loteBciInicial = loteBciInicial;
    }

    public String getLoteBciFinal() {
        return loteBciFinal;
    }

    public void setLoteBciFinal(String loteBciFinal) {
        this.loteBciFinal = loteBciFinal;
    }

    public String getCmcInicial() {
        return cmcInicial;
    }

    public void setCmcInicial(String cmcInicial) {
        this.cmcInicial = cmcInicial;
    }

    public String getCmcFinal() {
        return cmcFinal;
    }

    public void setCmcFinal(String cmcFinal) {
        this.cmcFinal = cmcFinal;
    }

    public Bairro getBairroCmc() {
        return bairroCmc;
    }

    public void setBairroCmc(Bairro bairroCmc) {
        this.bairroCmc = bairroCmc;
    }

    public Logradouro getLogradouroCmc() {
        return logradouroCmc;
    }

    public void setLogradouroCmc(Logradouro logradouroCmc) {
        this.logradouroCmc = logradouroCmc;
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

    public String getInscricaoRuralInicial() {
        return inscricaoRuralInicial;
    }

    public void setInscricaoRuralInicial(String inscricaoRuralInicial) {
        this.inscricaoRuralInicial = inscricaoRuralInicial;
    }

    public String getInscricaoRuralFinal() {
        return inscricaoRuralFinal;
    }

    public void setInscricaoRuralFinal(String inscricaoRuralFinal) {
        this.inscricaoRuralFinal = inscricaoRuralFinal;
    }

    public String getLocalizacaoLote() {
        return localizacaoLote;
    }

    public void setLocalizacaoLote(String localizacaoLote) {
        this.localizacaoLote = localizacaoLote;
    }

    public String getNumeroIncra() {
        return numeroIncra;
    }

    public void setNumeroIncra(String numeroIncra) {
        this.numeroIncra = numeroIncra;
    }

    @Override
    public String toString() {
        return "FiltroMaioresDevedores{" +
            "dataRelatorio=" + dataRelatorio +
            ", vencimentoInicial=" + vencimentoInicial +
            ", vencimentoFinal=" + vencimentoFinal +
            ", qtdeInadimplencias=" + quantidadeInadimplencias +
            ", tipoPessoa=" + tipoPessoa +
            ", ordenacao=" + ordenacao +
            ", listaDividas=" + listaDividas +
            ", valoresAtualizados=" + valoresAtualizados +
            ", detalhado=" + detalhado +
            '}';
    }

    @Override
    public int compareTo(FiltroRelatorioInadimplencia o) {
        return o.getDataRelatorio().compareTo(this.dataRelatorio);
    }

    public enum TipoDebito {
        DO_EXERCICIO("Do Exercício", true),
        DIVIDA_ATIVA("Dívida Ativa", true),
        DIVIDA_ATIVA_AJUIZADA("Dívida Ativa Ajuizada", true),
        PARCELAMENTO_DIVIDA_ATIVA("Parcelamentos originados da Dívida Ativa", true);
        private String descricao;
        private boolean visivel;

        TipoDebito(String descricao, boolean visivel) {
            this.descricao = descricao;
            this.visivel = visivel;
        }

        public String getDescricao() {
            return descricao;
        }

        public boolean isVisivel() {
            return visivel;
        }
    }

    public enum OrdenacaoInadimplencia {
        TIPO_CADASTRO("Tipo de Cadastro"),
        DIVIDA("Dívida"),
        VALOR("Valor");
        private String descricao;

        OrdenacaoInadimplencia(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public void novoRelatorio() {
        iniciarVariaveisGeral();
        iniciarVariaveisCadastroContribuinte();
        iniciarVariaveisCadastroImobiliario();
        iniciarVariaveisCadastroEconomico();
        iniciarVariaveisCadastroRural();
    }

    public void iniciarVariaveisGeral() {
        DateTime hoje = DateTime.now();
        vencimentoInicial = hoje.minusDays(30).toDate();
        vencimentoFinal = hoje.toDate();
        lancamentoInicial = hoje.minusDays(30).toDate();
        lancamentoFinal = hoje.toDate();
        exercicioDivida = null;
        if (listaDividas != null) {
            listaDividas.clear();
        }
        divida = null;
        filtros = "";
        texto = "";
    }

    public void iniciarVariaveisCadastroContribuinte() {
        this.tipoPessoa = null;
        this.pessoa = null;
    }

    public void iniciarVariaveisCadastroImobiliario() {
        this.inscricaoBciInicial = "";
        this.inscricaoBciFinal = "";
        this.bairroBci = null;
        this.logradouroBci = null;
        this.setorBciInicial = "";
        this.setorBciFinal = "";
        this.quadraBciInicial = "";
        this.quadraBciFinal = "";
        this.loteBciInicial = "";
        this.loteBciFinal = "";
    }

    public void iniciarVariaveisCadastroEconomico() {
        this.cmcInicial = "";
        this.cmcFinal = "";
        this.bairroCmc = null;
        this.logradouroCmc = null;
        this.classificacaoAtividade = null;
        this.tipoAutonomo = null;
        this.grauDeRisco = null;
        this.naturezaJuridica = null;
    }

    public void iniciarVariaveisCadastroRural() {
        this.inscricaoRuralInicial = "";
        this.inscricaoRuralFinal = "";
        this.localizacaoLote = "";
        this.numeroIncra = "";
    }

    public boolean isCadastroContribuinte() {
        return tipoCadastroTributario != null && TipoCadastroTributario.PESSOA.equals(tipoCadastroTributario);
    }

    public boolean isCadastroImobiliario() {
        return tipoCadastroTributario != null && TipoCadastroTributario.IMOBILIARIO.equals(tipoCadastroTributario);
    }

    public boolean isCadastroEconomico() {
        return tipoCadastroTributario != null && TipoCadastroTributario.ECONOMICO.equals(tipoCadastroTributario);
    }

    public boolean isCadastroRural() {
        return tipoCadastroTributario != null && TipoCadastroTributario.RURAL.equals(tipoCadastroTributario);
    }

    public boolean isPessoaFisica() {
        return tipoPessoa != null && TipoPessoa.FISICA.equals(tipoPessoa);
    }

    public boolean isPessoaJuridica() {
        return tipoPessoa != null && TipoPessoa.JURIDICA.equals(tipoPessoa);
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
    }
}
