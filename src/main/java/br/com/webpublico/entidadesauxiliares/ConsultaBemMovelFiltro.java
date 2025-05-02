package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConsultaBemMovelFiltro implements Serializable {

    private static String MSG_VALIDACAO_DATA_INICIAL = "A data inicial deve ser informada.";
    private static String MSG_VALIDACAO_DATA_FINAL = "A data final deve ser informada.";
    private static String MSG_VALIDACAO_PERIODO = "A data final deve ser posterior ou igual a data inicial.";

    private String registroPatrimonial;
    private String registroAnterior;
    private Date dataAquisicaoInicial;
    private Date dataAquisicaoFinal;
    private String descricaoBem;
    private String marca;
    private String modelo;
    private HierarquiaOrganizacional hierarquiaAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrcamentaria;
    private GrupoBem grupoPatrimonial;
    private GrupoObjetoCompra grupoObjetoCompra;
    private Pessoa fornecedor;
    private TipoAquisicaoBem tipoAquisicaoBem;
    private EstadoConservacaoBem estadoConservacaoBem;
    private SituacaoConservacaoBem situacaoConservacaoBem;
    private TipoGrupo tipoGrupo;
    private String numeroNotaFiscal;
    private Date dataNotaFiscalInicial;
    private Date dataNotaFiscalFinal;
    private String numeroEmpenho;
    private Date dataEmpenhoInicial;
    private Date dataEmpenhoFinal;
    private BigDecimal valorOriginal;
    private BigDecimal valorDepreciacao;
    private BigDecimal valorAmortizacao;
    private BigDecimal valorExaustao;
    private BigDecimal valorAjuste;
    private TipoEventoBem tipoEventoBem;
    private UsuarioSistema usuarioSistema;
    private TipoOrdem tipoOrdem;
    private CampoOrdenacao[] camposOrdenacao;

    public ConsultaBemMovelFiltro() {
        limparFiltros();
    }

    public String getRegistroPatrimonial() {
        return registroPatrimonial;
    }

    public void setRegistroPatrimonial(String registroPatrimonial) {
        this.registroPatrimonial = registroPatrimonial;
    }

    public String getRegistroAnterior() {
        return registroAnterior;
    }

    public void setRegistroAnterior(String registroAnterior) {
        this.registroAnterior = registroAnterior;
    }

    public Date getDataAquisicaoInicial() {
        return dataAquisicaoInicial;
    }

    public void setDataAquisicaoInicial(Date dataAquisicaoInicial) {
        this.dataAquisicaoInicial = dataAquisicaoInicial;
    }

    public Date getDataAquisicaoFinal() {
        return dataAquisicaoFinal;
    }

    public void setDataAquisicaoFinal(Date dataAquisicaoFinal) {
        this.dataAquisicaoFinal = dataAquisicaoFinal;
    }

    public String getDescricaoBem() {
        return descricaoBem;
    }

    public void setDescricaoBem(String descricaoBem) {
        this.descricaoBem = descricaoBem;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public GrupoBem getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(GrupoBem grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public GrupoObjetoCompra getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(GrupoObjetoCompra grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
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

    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public Date getDataNotaFiscalInicial() {
        return dataNotaFiscalInicial;
    }

    public void setDataNotaFiscalInicial(Date dataNotaFiscalInicial) {
        this.dataNotaFiscalInicial = dataNotaFiscalInicial;
    }

    public Date getDataNotaFiscalFinal() {
        return dataNotaFiscalFinal;
    }

    public void setDataNotaFiscalFinal(Date dataNotaFiscalFinal) {
        this.dataNotaFiscalFinal = dataNotaFiscalFinal;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public Date getDataEmpenhoInicial() {
        return dataEmpenhoInicial;
    }

    public void setDataEmpenhoInicial(Date dataEmpenhoInicial) {
        this.dataEmpenhoInicial = dataEmpenhoInicial;
    }

    public Date getDataEmpenhoFinal() {
        return dataEmpenhoFinal;
    }

    public void setDataEmpenhoFinal(Date dataEmpenhoFinal) {
        this.dataEmpenhoFinal = dataEmpenhoFinal;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public BigDecimal getValorOriginal() {
        return valorOriginal;
    }

    public void setValorOriginal(BigDecimal valorOriginal) {
        this.valorOriginal = valorOriginal;
    }

    public BigDecimal getValorDepreciacao() {
        return valorDepreciacao;
    }

    public void setValorDepreciacao(BigDecimal valorDepreciacao) {
        this.valorDepreciacao = valorDepreciacao;
    }

    public BigDecimal getValorAmortizacao() {
        return valorAmortizacao;
    }

    public void setValorAmortizacao(BigDecimal valorAmortizacao) {
        this.valorAmortizacao = valorAmortizacao;
    }

    public BigDecimal getValorExaustao() {
        return valorExaustao;
    }

    public void setValorExaustao(BigDecimal valorExaustao) {
        this.valorExaustao = valorExaustao;
    }

    public BigDecimal getValorAjuste() {
        return valorAjuste;
    }

    public void setValorAjuste(BigDecimal valorAjuste) {
        this.valorAjuste = valorAjuste;
    }

    public TipoEventoBem getTipoEventoBem() {
        return tipoEventoBem;
    }

    public void setTipoEventoBem(TipoEventoBem tipoEventoBem) {
        this.tipoEventoBem = tipoEventoBem;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public TipoOrdem getTipoOrdem() {
        return tipoOrdem;
    }

    public void setTipoOrdem(TipoOrdem tipoOrdem) {
        this.tipoOrdem = tipoOrdem;
    }

    public CampoOrdenacao[] getCamposOrdenacao() {
        return camposOrdenacao;
    }

    public void setCamposOrdenacao(CampoOrdenacao[] camposOrdenacao) {
        this.camposOrdenacao = camposOrdenacao;
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();
        validarFiltrosData(ve);
        validarFiltrosPeriodo(ve);
        ve.lancarException();
    }

    private void validarFiltrosPeriodo(ValidacaoException ve) {
        if (verificarDataFinalAnteriorAInicial(dataAquisicaoInicial, dataAquisicaoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_PERIODO);
        }
        if (verificarDataFinalAnteriorAInicial(dataNotaFiscalInicial, dataNotaFiscalFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_PERIODO);
        }
        if (verificarDataFinalAnteriorAInicial(dataEmpenhoInicial, dataEmpenhoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_PERIODO);
        }
    }

    private void validarFiltrosData(ValidacaoException ve) {
        if (verificarSeDataInicialDoPeriodoFoiInformada(dataAquisicaoInicial, dataAquisicaoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_INICIAL);
        }
        if (verificarSeDataFinalDoPeriodoFoiInformada(dataAquisicaoInicial, dataAquisicaoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_FINAL);
        }

        if (verificarSeDataInicialDoPeriodoFoiInformada(dataNotaFiscalInicial, dataNotaFiscalFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_INICIAL);
        }
        if (verificarSeDataFinalDoPeriodoFoiInformada(dataNotaFiscalInicial, dataNotaFiscalFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_FINAL);
        }

        if (verificarSeDataInicialDoPeriodoFoiInformada(dataEmpenhoInicial, dataEmpenhoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_INICIAL);
        }
        if (verificarSeDataFinalDoPeriodoFoiInformada(dataEmpenhoInicial, dataEmpenhoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_FINAL);
        }
        ve.lancarException();
    }

    private boolean verificarValorMaiorQueZero(BigDecimal valor) {
        return valor.compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean verificarDataFinalAnteriorAInicial(Date dataInicial, Date dataFinal) {
        return dataInicial != null && dataFinal != null && dataFinal.before(dataInicial);
    }

    private boolean verificarSeDataInicialDoPeriodoFoiInformada(Date dataInicial, Date dataFinal) {
        return dataFinal != null && dataInicial == null;
    }

    private boolean verificarSeDataFinalDoPeriodoFoiInformada(Date dataInicial, Date dataFinal) {
        return dataInicial != null && dataFinal == null;
    }

    public void limparFiltros() {
        camposOrdenacao = new CampoOrdenacao[0];
        tipoOrdem = TipoOrdem.DESC;
        registroPatrimonial = null;
        registroAnterior = null;
        dataAquisicaoInicial = null;
        dataAquisicaoFinal = null;
        descricaoBem = null;
        marca = null;
        modelo = null;
        hierarquiaAdministrativa = null;
        hierarquiaOrcamentaria = null;
        grupoObjetoCompra = null;
        grupoPatrimonial = null;
        tipoAquisicaoBem = null;
        estadoConservacaoBem = null;
        situacaoConservacaoBem = null;
        tipoGrupo = null;
        numeroNotaFiscal = null;
        dataNotaFiscalInicial = null;
        dataNotaFiscalFinal = null;
        numeroEmpenho = null;
        dataEmpenhoInicial = null;
        dataEmpenhoFinal = null;
        fornecedor = null;
        tipoEventoBem = null;
        valorOriginal = null;
        valorDepreciacao = null;
        valorAmortizacao = null;
        valorExaustao = null;
        valorAjuste = null;
    }

    public String montarOrdenacao() {
        StringBuilder ordenacao = new StringBuilder(" order by ");
        if (camposOrdenacao.length == 0) {
            return "order by obj.id " + tipoOrdem.name();
        } else {
            for (CampoOrdenacao campo : camposOrdenacao) {
                ordenacao.append(campo.ordenacao).append(" ").append(tipoOrdem.name()).append(", ");
            }
        }
        return ordenacao.substring(0, ordenacao.length() - 2);
    }

    public String montarCondicao() {
        StringBuilder condicao = new StringBuilder();
        if (!Strings.isNullOrEmpty(registroPatrimonial)) {
            condicao.append(" and obj.identificacao = '").append(registroPatrimonial.trim()).append("' ");
        }
        if (!Strings.isNullOrEmpty(registroAnterior)) {
            condicao.append(" and obj.registroAnterior = '").append(registroAnterior.trim()).append("' ");
        }
        if (dataAquisicaoInicial != null && dataAquisicaoFinal != null) {
            condicao.append(" and obj.dataAquisicao between to_date('").append(DataUtil.getDataFormatada(dataAquisicaoInicial)).append("', 'dd/MM/yyyy') and to_date('").append(DataUtil.getDataFormatada(dataAquisicaoFinal)).append("', 'dd/MM/yyyy') ");
        }
        if (!Strings.isNullOrEmpty(descricaoBem)) {
            condicao.append(" and upper(obj.descricao) like ").append("'%").append(descricaoBem.toUpperCase().trim()).append("%' ");
        }
        if (!Strings.isNullOrEmpty(marca)) {
            condicao.append(" and upper(obj.marca) like ").append("'%").append(marca.toUpperCase().trim()).append("%' ");
        }
        if (!Strings.isNullOrEmpty(modelo)) {
            condicao.append(" and upper(obj.modelo) like ").append("'%").append(modelo.toUpperCase().trim()).append("%' ");
        }
        if (grupoPatrimonial != null) {
            condicao.append(" and gb.id = ").append(grupoPatrimonial.getId());
        }
        if (grupoObjetoCompra != null) {
            condicao.append(" and grupoobj.id = ").append(grupoObjetoCompra.getId());
        }
        if (hierarquiaAdministrativa != null) {
            condicao.append(" and hoadm.codigo like '").append(hierarquiaAdministrativa.getCodigoSemZerosFinais()).append("%' ");
        }
        if (hierarquiaOrcamentaria != null) {
            condicao.append(" and hoorc.codigo = '").append(hierarquiaOrcamentaria.getCodigo()).append("' ");
        }
        if (tipoAquisicaoBem != null) {
            condicao.append(" and est.tipoAquisicaoBem = '").append(tipoAquisicaoBem.name()).append("' ");
        }
        if (estadoConservacaoBem != null) {
            condicao.append(" and est.estadoBem = '").append(estadoConservacaoBem.name()).append("' ");
        }
        if (situacaoConservacaoBem != null) {
            condicao.append(" and est.situacaoConservacaoBem = '").append(situacaoConservacaoBem.name()).append("' ");
        }
        if (tipoGrupo != null) {
            condicao.append(" and est.tipoGrupo = '").append(tipoGrupo.name()).append("' ");
        }
        if (!Strings.isNullOrEmpty(numeroNotaFiscal)) {
            condicao.append(" and nota.numeroNotaFiscal = '").append(numeroNotaFiscal.trim()).append("' ");
        }
        if (dataNotaFiscalInicial != null && dataNotaFiscalFinal != null) {
            condicao.append(" and nota.dataNotaFiscal between to_date('").append(DataUtil.getDataFormatada(dataNotaFiscalInicial)).append("', 'dd/MM/yyyy') and to_date('").append(DataUtil.getDataFormatada(dataNotaFiscalFinal)).append("', 'dd/MM/yyyy') ");
        }
        if (!Strings.isNullOrEmpty(numeroEmpenho)) {
            condicao.append(" and notaEmp.numeroEmpenho = '").append(numeroEmpenho.trim()).append("' ");
        }
        if (dataEmpenhoInicial != null && dataEmpenhoFinal != null) {
            condicao.append(" and trunc(notaEmp.dataEmpenho) between to_date('").append(DataUtil.getDataFormatada(dataEmpenhoInicial)).append("', 'dd/MM/yyyy') and to_date('").append(DataUtil.getDataFormatada(dataEmpenhoFinal)).append("', 'dd/MM/yyyy') ");;
        }
        if (fornecedor != null) {
            condicao.append(" and obj.fornecedor_id = ").append(fornecedor.getId());
        }
        if (valorOriginal != null && verificarValorMaiorQueZero(valorOriginal)) {
            condicao.append(" and est.valorOriginal = ").append(valorOriginal);
        }
        if (valorDepreciacao != null && verificarValorMaiorQueZero(valorDepreciacao)) {
            condicao.append(" and est.valorAcumuladoDaDepreciacao = ").append(valorDepreciacao);
        }
        if (valorAmortizacao != null && verificarValorMaiorQueZero(valorAmortizacao)) {
            condicao.append(" and est.valorAcumuladoDaAmortizacao = ").append(valorAmortizacao);
        }
        if (valorExaustao != null && verificarValorMaiorQueZero(valorExaustao)) {
            condicao.append(" and est.valorAcumuladoDaExaustao = ").append(valorExaustao);
        }
        if (valorAjuste != null && verificarValorMaiorQueZero(valorAjuste)) {
            condicao.append(" and est.valorAcumuladoDeAjuste = ").append(valorAjuste);
        }
        if (tipoEventoBem != null && getTipoEventoIngresso() != null) {
            condicao.append(" and exists( select 1 from EventoBem ev1 where ev1.tipoEventoBem = '").append(getTipoEventoIngresso().name()).append("' and ev1.bem_id = obj.id) ");
        }
        return condicao.toString();
    }

    private TipoEventoBem getTipoEventoIngresso() {
        switch (tipoEventoBem) {
            case INCORPORACAOBEM:
                return TipoEventoBem.INCORPORACAOBEM;
            case ITEMAQUISICAO:
                return TipoEventoBem.ITEMAQUISICAO;
            case EFETIVACAOLEVANTAMENTOBEM:
                return TipoEventoBem.EFETIVACAOLEVANTAMENTOBEM;
            default:
                return null;
        }
    }

    public enum TipoOrdem {
        ASC("Crescente"),
        DESC("Decrescente");

        private String descricao;

        TipoOrdem(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return getDescricao();
        }
    }

    public enum CampoOrdenacao {

        REGISTRO_PATRIMONIAL("Registro Patrimonial", "to_number(regexp_replace(obj.identificacao, '[^0-9]'))"),
        REGISTRO_ANTERIOR("Registro Anterior", "obj.registroAnterior"),
        DATA_AQUISICAO("Data de Aquisição", "obj.dataAquisicao"),
        DESCRICAO("Descrição", "obj.descricao"),
        MARCA("Marca", "obj.marca"),
        MODELO("Modelo", "obj.modelo"),
        GRUPO_PATRIMONIAL("Grupo Patrimonial", "gb.id"),
        GRUPO_OBJETO_COMPRA("Grupo Objeto de Compra", "grupoobj.id"),
        CODIGO_UNIDADE_ADMINISTRATIVA("Código Unidade Administrativa", "hoadm.codigo"),
        CODIGO_UNIDADE_ORCAMENTARIA("Código Unidade Orçamentária", "hoorc.codigo"),
        TIPO_AQUISICAO("Tipo de Aquisição", "est.tipoAquisicaoBem"),
        ESTADO_BEM("Estado de Conservação", "est.estadoBem"),
        TIPO_GRUPO("Tipo de Grupo", "est.tipoGrupo"),
        NUMERO_NOTA_FISCAL("Número da Nota Fiscal", "nota.numeroNotaFiscal"),
        DATA_NOTA_FISCAL("Data da Nota Fiscal", "nota.dataNotaFiscal"),
        NUMERO_EMPENHO("Número do Empenho", "notaEmp.numeroEmpenho"),
        DATA_EMPENHO("Data do Empenho", "notaEmp.dataEmpenho"),
        FORNECEDOR("Fornecedor", "obj.fornecedor_id"),
        VALOR_ORIGINAL("Valor Original", "est.valorOriginal"),
        VALOR_DEPRECIACAO("Valor Depreciação", "est.valorAcumuladoDaDepreciacao"),
        VALOR_AMORTIZACAO("Valor Amortizacão", "est.valorAcumuladoDaAmortizacao"),
        VALOR_AJUSTE("Valor Ajuste Perdas", "est.valorAcumuladoDeAjuste");
        private String descricao;
        private String ordenacao;

        CampoOrdenacao(String descricao, String ordenacao) {
            this.descricao = descricao;
            this.ordenacao = ordenacao;
        }

        public String getDescricao() {
            return descricao;
        }

        public String getOrdenacao() {
            return ordenacao;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }

    public static List<VOBem> preencherListaVOBensApartirArrayObject(List<Object[]> objetosDaConsulta) {
        List<VOBem> retornaBens = new ArrayList<>();
        for (Object[] object : objetosDaConsulta) {
            VOBem bem = new VOBem();
            bem.setId(object[0] != null ? ((BigDecimal) object[0]).longValue() : null);
            bem.setRegistroPatimonial(object[1] != null ? ((String) object[1]) : "");
            bem.setRegistroAnterior(object[2] != null ? (String) object[2] : "");
            bem.setDataAquisicao(object[3] != null ? (Date) object[3] : null);
            bem.setDescricao(object[4] != null ? (String) object[4] : "");
            bem.setMarca(object[5] != null ? (String) object[5] : "");
            bem.setModelo(object[6] != null ? (String) object[6] : "");
            bem.setGrupoPatrimonial(object[7] != null ? (String) object[7] : "");
            bem.setGrupoObjetoCompra(object[8] != null ? (String) object[8] : "");
            bem.setUnidadeAdministrativa((String) object[9]);
            bem.setUnidadeOrcamentaria((String) object[10]);
            bem.setTipoAquisicao(object[11] != null ? TipoAquisicaoBem.valueOf((String) object[11]).getDescricao() : "");
            bem.setEstadoConservacao(object[12] != null ? EstadoConservacaoBem.valueOf((String) object[12]).getDescricao() : "");
            bem.setSituacaoConservacao(object[13] != null ? SituacaoConservacaoBem.valueOf((String) object[13]).getDescricao() : "");
            bem.setTipoGrupo(object[14] != null ? TipoGrupo.valueOf((String) object[14]).getDescricao() : "");
            bem.setValorOriginal(object[15] != null ? (BigDecimal) object[15] : BigDecimal.ZERO);
            bem.setValorDepreciacao(object[16] != null ? (BigDecimal) object[16] : BigDecimal.ZERO);
            bem.setValorAmortizacao(object[17] != null ? (BigDecimal) object[17] : BigDecimal.ZERO);
            bem.setValorAjuste(object[18] != null ? (BigDecimal) object[18] : BigDecimal.ZERO);
            bem.setValorLiquido(object[19] != null ? (BigDecimal) object[19] : BigDecimal.ZERO);
            retornaBens.add(bem);
        }
        return retornaBens;
    }

    public boolean hasFiltrosNotaOrEmpenhoUtilizados() {
        return getDataNotaFiscalInicial() != null
            || !Strings.isNullOrEmpty(getNumeroNotaFiscal())
            || getDataEmpenhoInicial() != null
            || !Strings.isNullOrEmpty(getNumeroEmpenho());
    }
}
