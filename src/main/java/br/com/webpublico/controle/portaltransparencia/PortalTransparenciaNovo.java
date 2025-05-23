package br.com.webpublico.controle.portaltransparencia;

import br.com.webpublico.controle.portaltransparencia.entidades.*;
import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoObjetoPortalTransparencia;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

/**
 * Created by romanini on 25/08/15.
 */
public class PortalTransparenciaNovo {


    private String url;
    private Date dataOperacao;
    private Exercicio exercicio = null;
    private Double quantoFalta;
    private Integer total;
    private Long decorrido;
    private Long tempo;
    private Integer processados;
    private Boolean calculando;
    private AssistenteBarraProgresso assistente;

    private List<HierarquiaOrganizacional> unidades;


    private Boolean enviarUnidades;
    private Boolean enviarPlanejamentoDespesa;
    private Boolean enviarPlanejamentoReceita;
    private Boolean enviarDiaria;
    private Boolean enviarExecucaoOrcamentaria;
    private Boolean enviarRestoAPagar;
    private Boolean enviarAtoLegal;
    private Boolean enviarReceitaRealizada;
    private Boolean enviarAlteracaoOrcamentaria;
    private Boolean enviarDadosDaPrefeitura;
    private Boolean enviarServidores;
    private Boolean enviarLicitacao;
    private Boolean enviarContrato;
    private Boolean enviarContratoLicitacao;
    private Boolean enviarLink;
    private Boolean enviarGlossario;
    private Boolean enviarAnexos;
    private Boolean enviarConvenios;
    private Boolean enviarFinanceiro;
    private Boolean enviarDadosRh;
    private Boolean enviarObra;
    private Boolean enviarCalamidadePublica;
    private Boolean enviarBem;
    private Boolean enviarDicionarioDeDados;
    private Boolean enviarLOA;
    private Boolean enviarLDO;
    private Boolean enviarPPA;
    private Boolean enviarItensLotesMapaComparativo;
    private Boolean enviarInstitucional;
    public static String WS_EMENDA_PARLAMENTAR = "/ws/api/emenda-parlamentar";

    //DADOS DA PREFEITURA
    private List<PrefeituraPortal> prefeituras;
    private PrefeituraPortal prefeitura;
    private HierarquiaOrganizacional unidade;
    private ModuloPrefeituraPortal modulo;

    //LINK
    private List<LinkPortal> links;
    private LinkPortal linkPortal;

    //Importacao de AtoLegal
    private List<AtoLegal> atos;
    private AtoLegal atoLegal;

    //Glossário
    private List<GlossarioPortal> glossarios;
    private GlossarioPortal glossarioPortal;

    //Envio Manual
    private String hql;
    private TipoObjetoPortalTransparencia tipo;
    private List<EntidadePortalTransparencia> objetos;

    //WEB SERVICE
    public static String WS_PREFEITURA = "/ws/api/prefeitura";
    public static String WS_PREFEITURA_UNIDADE = "/ws/api/prefeitura-unidade";
    public static String WS_PREFEITURA_MODULO = "/ws/api/prefeitura-modulo";
    public static String WS_UNIDADE = "/ws/api/unidade";
    public static String WS_EXERCICIO = "/ws/api/exercicio";
    public static String WS_FUNCAO = "/ws/api/funcao";
    public static String WS_SUBFUNCAO = "/ws/api/sub-funcao";
    public static String WS_FONTE_DE_RECURSO = "/ws/api/fonte-de-recurso";
    public static String WS_PPA = "/ws/api/ppa";
    public static String WS_LDO = "/ws/api/ldo";
    public static String WS_META_FISCAL = "/ws/api/meta-fiscal";
    public static String WS_LOA = "/ws/api/loa";
    public static String WS_PROGRAMA = "/ws/api/programa";
    public static String WS_ACAO = "/ws/api/acao";
    public static String WS_SUBACAO = "/ws/api/sub-acao";
    public static String WS_ELEMENTO_DESPESA = "/ws/api/elemento-despesa";
    public static String WS_PREVISAO_DESPESA = "/ws/api/previsao-despesa";
    public static String WS_PREVISAO_DESPESA_FONTE = "/ws/api/previsao-despesa-fonte";
    public static String WS_PESSOA = "/ws/api/pessoa";
    public static String WS_SERVIDOR = "/ws/api/servidor";
    public static String WS_SERVIDOR_RECURSO_FP = "/ws/api/recurso-fp";
    public static String WS_SERVIDOR_CARGO = "/ws/api/cargo";
    public static String WS_SERVIDOR_FUNCAO_GRATIFICADA = "/ws/api/funcao-gratificada";
    public static String WS_SERVIDOR_CATEGORIA_FUNCAO_GRATIFICADA = "/ws/api/categoria-funcao-gratificada";
    public static String WS_SERVIDOR_LOTACAO = "/ws/api/lotacao";
    public static String WS_SERVIDOR_MODALIDADE = "/ws/api/modalidade";
    public static String WS_SERVIDOR_CEDENCIA = "/ws/api/cedencia";
    public static String WS_SERVIDOR_REMOVER = "/ws/api/servidor-deletar";
    public static String WS_EMPENHO = "/ws/api/empenho";
    public static String WS_EMPENHO_ESTORNO = "/ws/api/empenho-estorno";
    public static String WS_LIQUIDACAO = "/ws/api/liquidacao";
    public static String WS_LIQUIDACAO_DOCTO_FISCAL = "/ws/api/liquidacao-documento-fiscal";
    public static String WS_LIQUIDACAO_ESTORNO = "/ws/api/liquidacao-estorno";
    public static String WS_PAGAMENTO = "/ws/api/pagamento";
    public static String WS_PAGAMENTO_ESTORNO = "/ws/api/pagamento-estorno";
    public static String WS_CONTA_RECEITA = "/ws/api/conta-receita";
    public static String WS_RECEITA_LOA = "/ws/api/receita-loa";
    public static String WS_RECEITA_LOA_FONTE = "/ws/api/receita-loa-fonte";
    public static String WS_ATO_LEGAL = "/ws/api/ato-legal";
    public static String WS_ARQUIVO = "/ws/api/arquivo";
    public static String WS_LANCAMENTO_RECEITA = "/ws/api/lancamento-receita";
    public static String WS_LANCAMENTO_RECEITA_ESTORNO = "/ws/api/lancamento-receita-estorno";
    public static String WS_LANCAMENTO_RECEITA_FONTE = "/ws/api/lancamento-receita-fonte";
    public static String WS_LANCAMENTO_RECEITA_FONTE_ESTORNO = "/ws/api/lancamento-receita-fonte-estorno";
    public static String WS_ALTERACAO_ORC = "/ws/api/alteracao-orcamentaria";
    public static String WS_ANULACAO_ORC = "/ws/api/anulacao-orcamentaria";
    public static String WS_SUPLEMENTACAO_ORC = "/ws/api/suplementacao-orcamentaria";
    public static String WS_ALTERACAO_ORC_ESTORNO = "/ws/api/alteracao-orcamentaria-estorno";
    public static String WS_ANULACAO_ORC_ESTORNO = "/ws/api/anulacao-orcamentaria-estorno";
    public static String WS_SUPLEMENTACAO_ORC_ESTORNO = "/ws/api/suplementacao-orcamentaria-estorno";
    public static String WS_LICITACAO = "/ws/api/licitacao";
    public static String WS_LICITACAO_ARQUIVO = "/ws/api/licitacao-arquivo";
    public static String WS_LICITACAO_NATUREZA_OBJETO = "/ws/api/licitacao-natureza-objeto";
    public static String WS_LICITACAO_FONTE_DE_RECURSO = "/ws/api/licitacao-fonte-de-recurso";
    public static String WS_LICITACAO_PUBLICACAO = "/ws/api/licitacao-publicacao";
    public static String WS_LICITACAO_STATUS = "/ws/api/licitacao-status";
    public static String WS_LICITACAO_FORNECEDOR = "/ws/api/licitacao-fornecedor";
    public static String WS_LICITACAO_VENCEDOR = "/ws/api/vencedor-licitacao";
    public static String WS_LICITACAO_EXTRATO = "/ws/api/extrato-licitacao";
    public static String WS_LICITACAO_PROCESSO_DE_COMPRA = "/ws/api/licitacao-processo-de-compra";
    public static String WS_PROCESSO_DE_COMPRA_SOLICITACAO_DE_COMPRA = "/ws/api/solicitacao-de-compra";
    public static String WS_SOLICITACAO_DE_COMPRA_AVALIACAO_SOLICITACAO = "/ws/api/avaliacao-solicitacao-compra";
    public static String WS_SOLICITACAO_DE_COMPRA_DOTACAO = "/ws/api/dotacao-solicitacao-de-compra";
    public static String WS_LICITACAO_DOCTO_FORNECEDOR = "/ws/api/licitacao-docto-fornecedor";
    public static String WS_DOCTO_HABILITACAO = "/ws/api/docto-habilitacao";
    public static String WS_LICITACAO_PROGRAMA_TRABALHO = "/ws/api/licitacao-programa-trabalho";
    public static String WS_LICITACAO_MAPA_COMPARATIVO_ITEM = "/ws/api/licitacao-mapa-comparativo-item";
    public static String WS_LICITACAO_MAPA_COMPARATIVO_LOTE = "/ws/api/licitacao-mapa-comparativo-lote";
    public static String WS_LICITACAO_COTACAO = "/ws/api/licitacao-cotacao";
    public static String WS_LICITACAO_COTACAO_FORNECEDOR = "/ws/api/licitacao-cotacao-fornecedor";
    public static String WS_LICITACAO_COTACAO_LOTE = "/ws/api/licitacao-cotacao-lote";
    public static String WS_LICITACAO_COTACAO_ITEM = "/ws/api/licitacao-cotacao-item";
    public static String WS_FORMULARIO_COTACAO = "/ws/api/formulario-cotacao";
    public static String WS_IRP = "/ws/api/irp";
    public static String WS_LICITACAO_PROPOSTA_FORNECEDOR = "/ws/api/licitacao-proposta-fornecedor";
    public static String WS_LICITACAO_PROPOSTA_LOTE = "/ws/api/licitacao-proposta-lote";
    public static String WS_LICITACAO_PROPOSTA_ITEM = "/ws/api/licitacao-proposta-item";
    public static String WS_CONTRATO = "/ws/api/contrato";
    public static String WS_CONTRATO_REMOVER = "/ws/api/contrato-deletar";
    public static String WS_CONTRATO_ITEM = "/ws/api/contrato-item";
    public static String WS_CONTRATO_ARQUIVO = "/ws/api/contrato-arquivo";
    public static String WS_CONTRATO_ORDEM_SERVICO = "/ws/api/contrato-ordem-servico";
    public static String WS_CONTRATO_GESTOR = "/ws/api/contrato-gestor";
    public static String WS_CONTRATO_ALTERACAO_CONTRATUAL = "/ws/api/contrato-alteracao-contratual";
    public static String WS_OBRA = "/ws/api/obra";
    public static String WS_OBRA_ITEM = "/ws/api/obra-item";
    public static String WS_LINK = "/ws/api/link";
    public static String WS_GLOSSARIO = "/ws/api/glossario";
    public static String WS_DIARIA = "/ws/api/diaria";
    public static String WS_DIARIA_ARQUIVO = "/ws/api/diaria-arquivo";
    public static String WS_ANEXO = "/ws/api/anexo-contabilidade";
    public static String WS_CONVENIO_DESPESA = "/ws/api/convenio-despesa";
    public static String WS_CONVENIO_DESPESA_SITUACAO = "/ws/api/convenio-despesa-situacao";
    public static String WS_CONVENIO_DESPESA_RECURSO = "/ws/api/convenio-despesa-recurso";
    public static String WS_CONVENIO_DESPESA_ADITIVO = "/ws/api/convenio-despesa-aditivo";
    public static String WS_CONVENIO_RECEITA = "/ws/api/convenio-receita";
    public static String WS_CONVENIO_RECEITA_SITUACAO = "/ws/api/convenio-receita-situacao";
    public static String WS_CONVENIO_RECEITA_RECURSO = "/ws/api/convenio-receita-recurso";
    public static String WS_CALAMIDADE_PUBLICA = "/ws/api/calamidade-publica";
    public static String WS_CALAMIDADE_PUBLICA_ATO_LEGAL = "/ws/api/calamidade-publica-ato-legal";
    public static String WS_CALAMIDADE_PUBLICA_CONTRATO = "/ws/api/calamidade-publica-contrato";
    public static String WS_CALAMIDADE_PUBLICA_RECURSO = "/ws/api/calamidade-publica-recurso";
    public static String WS_CALAMIDADE_PUBLICA_BEM_SERVICO = "/ws/api/calamidade-publica-bem-servico";
    public static String WS_CALAMIDADE_PUBLICA_BEM_DOADO = "/ws/api/calamidade-publica-bem-doado";
    public static String WS_CALAMIDADE_PUBLICA_BEM_DOADO_ARQUIVO = "/ws/api/calamidade-publica-bem-doado-arquivo";
    public static String WS_DICIONARIO_DADOS = "/ws/api/dicionario-de-dados";
    public static String WS_DICIONARIO_DADOS_COLUNA = "/ws/api/dicionario-de-dados-coluna";
    public static String WS_LIBERACAO_FINANCEIRA = "/ws/api/liberacao-financeira";
    public static String WS_LIBERACAO_FINANCEIRA_ESTORNO = "/ws/api/liberacao-financeira-estorno";
    public static String WS_BEM = "/ws/api/bem";
    public static String WS_RESPONSAVEL_LOTACAO = "/ws/api/lotacao-responsavel";
    public static String WS_AGENDA_RESPONSAVEL_LOTACAO = "/ws/api/agenda-lotacao-responsavel";
    public static String WS_ANEXO_RESPONSAVEL_LOTACAO = "/ws/api/anexo-lotacao-responsavel";
    public static String WS_ANEXO_LOTACAO = "/ws/api/lotacao-arquivo";
    public static String URL_PORTAL_HOMOLOGACAO = "http://172.16.0.76:8080";
    private Boolean enviarEmendaParlamentar;

    public PortalTransparenciaNovo(Date dataOperacao, Exercicio exercicio) {
        this.url = URL_PORTAL_HOMOLOGACAO;
        this.dataOperacao = dataOperacao;
        this.exercicio = exercicio;
        unidades = Lists.newArrayList();

        decorrido = 0l;
        tempo = 0l;
        processados = 0;
        total = 0;
        calculando = false;

        enviarUnidades = Boolean.TRUE;
        enviarPlanejamentoDespesa = Boolean.TRUE;
        enviarPlanejamentoReceita = Boolean.TRUE;
        enviarExecucaoOrcamentaria = Boolean.TRUE;
        enviarRestoAPagar = Boolean.TRUE;
        enviarAtoLegal = Boolean.TRUE;
        enviarReceitaRealizada = Boolean.TRUE;
        enviarAlteracaoOrcamentaria = Boolean.TRUE;
        enviarDadosDaPrefeitura = Boolean.TRUE;
        enviarServidores = Boolean.TRUE;
        enviarLicitacao = Boolean.TRUE;
        enviarContrato = Boolean.TRUE;
        enviarContratoLicitacao = Boolean.FALSE;
        enviarLink = Boolean.TRUE;
        enviarGlossario = Boolean.TRUE;
        enviarDiaria = Boolean.TRUE;
        enviarAnexos = Boolean.TRUE;
        enviarConvenios = Boolean.TRUE;
        enviarFinanceiro = Boolean.TRUE;
        enviarDadosRh = Boolean.TRUE;
        enviarObra = Boolean.TRUE;
        enviarCalamidadePublica = Boolean.TRUE;
        enviarBem = Boolean.TRUE;
        enviarDicionarioDeDados = Boolean.TRUE;
        enviarLOA = Boolean.TRUE;
        enviarLDO = Boolean.TRUE;
        enviarPPA = Boolean.TRUE;
        enviarInstitucional = Boolean.TRUE;
        enviarItensLotesMapaComparativo = Boolean.FALSE;
        enviarEmendaParlamentar = Boolean.TRUE;
        atos = Lists.newArrayList();
        atoLegal = new AtoLegal();
        assistente = new AssistenteBarraProgresso();
    }

    public void inicializarLink() {
        links = Lists.newArrayList();
        linkPortal = new LinkPortal();
    }

    public void inicializarGlossario() {
        glossarioPortal = new GlossarioPortal();
        glossarios = Lists.newArrayList();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }


    public Double getQuantoFalta() {
        return quantoFalta;
    }

    public void setQuantoFalta(Double quantoFalta) {
        this.quantoFalta = quantoFalta;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Long getDecorrido() {
        return decorrido;
    }

    public void setDecorrido(Long decorrido) {
        this.decorrido = decorrido;
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public Integer getProcessados() {
        return processados;
    }

    public void setProcessados(Integer processados) {
        this.processados = processados;
    }

    public Boolean isCalculando() {
        return calculando;
    }

    public void setCalculando(Boolean calculando) {
        this.calculando = calculando;
    }

    public List<HierarquiaOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(List<HierarquiaOrganizacional> unidades) {
        this.unidades = unidades;
    }

    public static String getWS_UNIDADE() {
        return WS_UNIDADE;
    }

    public static void setWS_UNIDADE(String WS_UNIDADE) {
        PortalTransparenciaNovo.WS_UNIDADE = WS_UNIDADE;
    }

    public static String getWS_EXERCICIO() {
        return WS_EXERCICIO;
    }

    public static void setWS_EXERCICIO(String WS_EXERCICIO) {
        PortalTransparenciaNovo.WS_EXERCICIO = WS_EXERCICIO;
    }

    public static String getWS_FUNCAO() {
        return WS_FUNCAO;
    }

    public static void setWS_FUNCAO(String WS_FUNCAO) {
        PortalTransparenciaNovo.WS_FUNCAO = WS_FUNCAO;
    }

    public static String getWS_FONTE_DE_RECURSO() {
        return WS_FONTE_DE_RECURSO;
    }

    public static void setWS_FONTE_DE_RECURSO(String WS_FONTE_DE_RECURSO) {
        PortalTransparenciaNovo.WS_FONTE_DE_RECURSO = WS_FONTE_DE_RECURSO;
    }

    public static String getWS_PROGRAMA() {
        return WS_PROGRAMA;
    }

    public static void setWS_PROGRAMA(String WS_PROGRAMA) {
        PortalTransparenciaNovo.WS_PROGRAMA = WS_PROGRAMA;
    }

    public static String getWS_ACAO() {
        return WS_ACAO;
    }

    public static void setWS_ACAO(String WS_ACAO) {
        PortalTransparenciaNovo.WS_ACAO = WS_ACAO;
    }

    public static String getWS_ELEMENTO_DESPESA() {
        return WS_ELEMENTO_DESPESA;
    }

    public static void setWS_ELEMENTO_DESPESA(String WS_ELEMENTO_DESPESA) {
        PortalTransparenciaNovo.WS_ELEMENTO_DESPESA = WS_ELEMENTO_DESPESA;
    }

    public static String getWS_PREVISAO_DESPESA() {
        return WS_PREVISAO_DESPESA;
    }

    public static void setWS_PREVISAO_DESPESA(String WS_PREVISAO_DESPESA) {
        PortalTransparenciaNovo.WS_PREVISAO_DESPESA = WS_PREVISAO_DESPESA;
    }

    public static String getWS_PREVISAO_DESPESA_FONTE() {
        return WS_PREVISAO_DESPESA_FONTE;
    }

    public static void setWS_PREVISAO_DESPESA_FONTE(String WS_PREVISAO_DESPESA_FONTE) {
        PortalTransparenciaNovo.WS_PREVISAO_DESPESA_FONTE = WS_PREVISAO_DESPESA_FONTE;
    }

    public Boolean getEnviarUnidades() {
        return enviarUnidades;
    }

    public void setEnviarUnidades(Boolean enviarUnidades) {
        this.enviarUnidades = enviarUnidades;
    }

    public Boolean getEnviarPlanejamentoDespesa() {
        return enviarPlanejamentoDespesa;
    }

    public void setEnviarPlanejamentoDespesa(Boolean enviarPlanejamentoDespesa) {
        this.enviarPlanejamentoDespesa = enviarPlanejamentoDespesa;
    }

    public Boolean getEnviarPlanejamentoReceita() {
        return enviarPlanejamentoReceita;
    }

    public void setEnviarPlanejamentoReceita(Boolean enviarPlanejamentoReceita) {
        this.enviarPlanejamentoReceita = enviarPlanejamentoReceita;
    }

    public Boolean getEnviarExecucaoOrcamentaria() {
        return enviarExecucaoOrcamentaria;
    }

    public void setEnviarExecucaoOrcamentaria(Boolean enviarExecucaoOrcamentaria) {
        this.enviarExecucaoOrcamentaria = enviarExecucaoOrcamentaria;
    }

    public Boolean getEnviarAtoLegal() {
        return enviarAtoLegal;
    }

    public void setEnviarAtoLegal(Boolean enviarAtoLegal) {
        this.enviarAtoLegal = enviarAtoLegal;
    }

    public Boolean getEnviarReceitaRealizada() {
        return enviarReceitaRealizada;
    }

    public void setEnviarReceitaRealizada(Boolean enviarReceitaRealizada) {
        this.enviarReceitaRealizada = enviarReceitaRealizada;
    }

    public Boolean getEnviarAlteracaoOrcamentaria() {
        return enviarAlteracaoOrcamentaria;
    }

    public void setEnviarAlteracaoOrcamentaria(Boolean enviarAlteracaoOrcamentaria) {
        this.enviarAlteracaoOrcamentaria = enviarAlteracaoOrcamentaria;
    }


    public Boolean getEnviarDadosDaPrefeitura() {
        return enviarDadosDaPrefeitura;
    }

    public void setEnviarDadosDaPrefeitura(Boolean enviarDadosDaPrefeitura) {
        this.enviarDadosDaPrefeitura = enviarDadosDaPrefeitura;
    }

    public Boolean getEnviarServidores() {
        return enviarServidores;
    }

    public void setEnviarServidores(Boolean enviarServidores) {
        this.enviarServidores = enviarServidores;
    }


    public Boolean getEnviarLicitacao() {
        return enviarLicitacao;
    }

    public void setEnviarLicitacao(Boolean enviarLicitacao) {
        this.enviarLicitacao = enviarLicitacao;
    }

    public List<LinkPortal> getLinks() {
        return links;
    }

    public void setLinks(List<LinkPortal> links) {
        this.links = links;
    }

    public Boolean getEnviarLink() {
        return enviarLink;
    }

    public void setEnviarLink(Boolean enviarLink) {
        this.enviarLink = enviarLink;
    }

    public Boolean getEnviarDiaria() {
        return enviarDiaria;
    }

    public void setEnviarDiaria(Boolean enviarDiaria) {
        this.enviarDiaria = enviarDiaria;
    }

    public Boolean getEnviarAnexos() {
        return enviarAnexos;
    }

    public void setEnviarAnexos(Boolean enviarAnexos) {
        this.enviarAnexos = enviarAnexos;
    }

    public Boolean getEnviarConvenios() {
        return enviarConvenios;
    }

    public void setEnviarConvenios(Boolean enviarConvenios) {
        this.enviarConvenios = enviarConvenios;
    }

    public Boolean getEnviarObra() {
        return enviarObra;
    }

    public void setEnviarObra(Boolean enviarObra) {
        this.enviarObra = enviarObra;
    }

    public Boolean getEnviarCalamidadePublica() {
        return enviarCalamidadePublica;
    }

    public void setEnviarCalamidadePublica(Boolean enviarCalamidadePublica) {
        this.enviarCalamidadePublica = enviarCalamidadePublica;
    }

    public Boolean getEnviarRestoAPagar() {
        return enviarRestoAPagar;
    }

    public void setEnviarRestoAPagar(Boolean enviarRestoAPagar) {
        this.enviarRestoAPagar = enviarRestoAPagar;
    }

    public Boolean getEnviarFinanceiro() {
        return enviarFinanceiro;
    }

    public void setEnviarFinanceiro(Boolean enviarFinanceiro) {
        this.enviarFinanceiro = enviarFinanceiro;
    }

    public Boolean getEnviarDadosRh() {
        return enviarDadosRh;
    }

    public void setEnviarDadosRh(Boolean enviarDadosRh) {
        this.enviarDadosRh = enviarDadosRh;
    }

    public LinkPortal getLinkPortal() {
        return linkPortal;
    }

    public void setLinkPortal(LinkPortal linkPortal) {
        this.linkPortal = linkPortal;
    }

    public List<AtoLegal> getAtos() {
        return atos;
    }

    public void setAtos(List<AtoLegal> atos) {
        this.atos = atos;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public List<GlossarioPortal> getGlossarios() {
        return glossarios;
    }

    public void setGlossarios(List<GlossarioPortal> glossarios) {
        this.glossarios = glossarios;
    }

    public GlossarioPortal getGlossarioPortal() {
        return glossarioPortal;
    }

    public void setGlossarioPortal(GlossarioPortal glossarioPortal) {
        this.glossarioPortal = glossarioPortal;
    }

    public Boolean getEnviarGlossario() {
        return enviarGlossario;
    }

    public void setEnviarGlossario(Boolean enviarGlossario) {
        this.enviarGlossario = enviarGlossario;
    }

    public List<PrefeituraPortal> getPrefeituras() {
        return prefeituras;
    }

    public void setPrefeituras(List<PrefeituraPortal> prefeituras) {
        this.prefeituras = prefeituras;
    }

    public PrefeituraPortal getPrefeitura() {
        return prefeitura;
    }

    public void setPrefeitura(PrefeituraPortal prefeitura) {
        this.prefeitura = prefeitura;
    }

    public HierarquiaOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(HierarquiaOrganizacional unidade) {
        this.unidade = unidade;
    }

    public ModuloPrefeituraPortal getModulo() {
        return modulo;
    }

    public void setModulo(ModuloPrefeituraPortal modulo) {
        this.modulo = modulo;
    }

    public Boolean getEnviarContrato() {
        return enviarContrato;
    }

    public void setEnviarContrato(Boolean enviarContrato) {
        this.enviarContrato = enviarContrato;
    }

    public Boolean getEnviarBem() {
        return enviarBem;
    }

    public void setEnviarBem(Boolean enviarBem) {
        this.enviarBem = enviarBem;
    }

    public Boolean getEnviarDicionarioDeDados() {
        return enviarDicionarioDeDados;
    }

    public void setEnviarDicionarioDeDados(Boolean enviarDicionarioDeDados) {
        this.enviarDicionarioDeDados = enviarDicionarioDeDados;
    }

    public Boolean getEnviarContratoLicitacao() {
        return enviarContratoLicitacao;
    }

    public void setEnviarContratoLicitacao(Boolean enviarContratoLicitacao) {
        this.enviarContratoLicitacao = enviarContratoLicitacao;
    }

    public AssistenteBarraProgresso getAssistente() {
        return assistente;
    }

    public void setAssistente(AssistenteBarraProgresso assistente) {
        this.assistente = assistente;
    }

    public Boolean getEnviarLOA() {
        return enviarLOA;
    }

    public void setEnviarLOA(Boolean enviarLOA) {
        this.enviarLOA = enviarLOA;
    }

    public Boolean getEnviarLDO() {
        return enviarLDO;
    }

    public void setEnviarLDO(Boolean enviarLDO) {
        this.enviarLDO = enviarLDO;
    }

    public Boolean getEnviarPPA() {
        return enviarPPA;
    }

    public void setEnviarPPA(Boolean enviarPPA) {
        this.enviarPPA = enviarPPA;
    }

    public Boolean getEnviarItensLotesMapaComparativo() {
        return enviarItensLotesMapaComparativo;
    }

    public void setEnviarItensLotesMapaComparativo(Boolean enviarItensLotesMapaComparativo) {
        this.enviarItensLotesMapaComparativo = enviarItensLotesMapaComparativo;
    }

    public Boolean getEnviarEmendaParlamentar() {
        return enviarEmendaParlamentar;
    }

    public void setEnviarEmendaParlamentar(Boolean enviarEmendaParlamentar) {
        this.enviarEmendaParlamentar = enviarEmendaParlamentar;
    }

    public Boolean getEnviarInstitucional() {
        return enviarInstitucional;
    }

    public void setEnviarInstitucional(Boolean enviarInstitucional) {
        this.enviarInstitucional = enviarInstitucional;
    }

    public Date getDataInicialFiltro() {
        return getAssistente().getDataAtual();
    }

    public Date getDataFinalFiltro() {
        Date dataInicial = getDataInicialFiltro();
        Date dataFim = getDataOperacao();
        if (dataInicial == null) {
            dataFim = DataUtil.criarDataComMesEAno(1, DataUtil.getAno(getDataOperacao())).toDate();
        }
        return dataFim;
    }


    public String getHql() {
        return hql;
    }

    public void setHql(String hql) {
        this.hql = hql;
    }

    public TipoObjetoPortalTransparencia getTipo() {
        return tipo;
    }

    public void setTipo(TipoObjetoPortalTransparencia tipo) {
        this.tipo = tipo;
    }

    public List<EntidadePortalTransparencia> getObjetos() {
        return objetos;
    }

    public void setObjetos(List<EntidadePortalTransparencia> objetos) {
        this.objetos = objetos;
    }

    public void marcarDesmarcarTodos(boolean marcarDesmarcar) {
        enviarUnidades = marcarDesmarcar;
        enviarPlanejamentoDespesa = marcarDesmarcar;
        enviarPlanejamentoReceita = marcarDesmarcar;
        enviarDiaria = marcarDesmarcar;
        enviarExecucaoOrcamentaria = marcarDesmarcar;
        enviarRestoAPagar = marcarDesmarcar;
        enviarAtoLegal = marcarDesmarcar;
        enviarReceitaRealizada = marcarDesmarcar;
        enviarAlteracaoOrcamentaria = marcarDesmarcar;
        enviarDadosDaPrefeitura = marcarDesmarcar;
        enviarServidores = marcarDesmarcar;
        enviarLicitacao = marcarDesmarcar;
        enviarContrato = marcarDesmarcar;
        enviarContratoLicitacao = marcarDesmarcar;
        enviarLink = marcarDesmarcar;
        enviarGlossario = marcarDesmarcar;
        enviarAnexos = marcarDesmarcar;
        enviarConvenios = marcarDesmarcar;
        enviarFinanceiro = marcarDesmarcar;
        enviarDadosRh = marcarDesmarcar;
        enviarObra = marcarDesmarcar;
        enviarCalamidadePublica = marcarDesmarcar;
        enviarBem = marcarDesmarcar;
        enviarDicionarioDeDados = marcarDesmarcar;
        enviarLOA = marcarDesmarcar;
        enviarLDO = marcarDesmarcar;
        enviarPPA = marcarDesmarcar;
        enviarItensLotesMapaComparativo = marcarDesmarcar;
        enviarInstitucional = marcarDesmarcar;
        enviarEmendaParlamentar = marcarDesmarcar;
    }
}
