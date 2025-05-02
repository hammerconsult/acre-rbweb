package br.com.webpublico.negocios.administrativo.dao;


import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Repository
public class JdbcIntegracaoDocumentoFiscalLiquidacao extends JdbcDaoSupport implements Serializable {

    public static final String INSERT_REVISAO_AUDITORIA = " insert into revisaoauditoria (ID, DATAHORA, IP, USUARIO) VALUES (?, ?, ?, ?) ";

    public static final String UPDATE_ITEMDOCTOITEMENTRADA = " update itemdoctoitementrada set valorliquidado = ?, situacao = ? where id = ? ";
    public static final String INSERT_ITEMDOCTOITEMENTRADA_AUD = " insert into itemdoctoitementrada_aud (ID, REV, REVTYPE, DOCTOFISCALENTRADACOMPRA_ID, ITEMENTRADAMATERIAL_ID, VALORLIQUIDADO, SITUACAO) VALUES (?, ?, ?, ?, ?, ?, ?) ";

    public static final String UPDATE_DOCTOFISCALENTRADACOMPRA = " update doctofiscalentradacompra set situacao = ? where id = ? ";
    public static final String INSERT_DOCTOFISCALENTRADACOMPRA_AUD = " insert into doctofiscalentradacompra_aud (ID, ENTRADACOMPRAMATERIAL_ID, DOCTOFISCALLIQUIDACAO_ID, REV, REVTYPE, SITUACAO) VALUES (?,?,?,?,?,?) ";

    public static final String UPDATE_BEM_DATAAQUISICAO = " update bem set dataaquisicao = ? where id = ? ";
    public static final String INSERT_BEM_AUD = " insert into bem_aud (ID, REV, REVTYPE, DESCRICAO, IDENTIFICACAO, DATAAQUISICAO, MARCA, MODELO, SEGURADORA_ID, " +
        "                     GARANTIA_ID, OBJETOCOMPRA_ID, REGISTROANTERIOR, OBSERVACAO, DETENTORORIGEMRECURSO_ID, " +
        "                     FORNECEDOR_ID, CONDICAODEOCUPACAO_ID, BCI, NUMEROREGISTRO) " +
        " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

    public static final String UPDATE_BEM_DETENTORORIGEMRECURSO = " update bem set detentororigemrecurso_id = ? where id = ? ";

    public static final String UPDATE_ESTADOBEM = " update estadobem set valorliquidado = ? where id = ? ";
    public static final String INSERT_ESTADOBEM_AUD = " insert into estadobem_aud (ID, REV, REVTYPE, VALORORIGINAL, IDENTIFICACAO, GRUPOBEM_ID, DETENTORAADMINISTRATIVA_ID, " +
        "                           ESTADOBEM, DETENTORAORCAMENTARIA_ID, TIPOGRUPO, SITUACAOCONSERVACAOBEM, " +
        "                           VALORACUMULADODEAJUSTE, VALORACUMULADODAAMORTIZACAO, VALORACUMULADODADEPRECIACAO, " +
        "                           VALORACUMULADODAEXAUSTAO, GRUPOOBJETOCOMPRA_ID, TIPOAQUISICAOBEM, LOCALIZACAO, " +
        "                           VALORLIQUIDADO) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

    public static final String UPDATE_EVENTOBEM = " update eventobem set situacaoeventobem = ? where id = ? ";
    public static final String INSERT_EVENTOBEM_AUD = " insert into eventobem_aud (ID, REV, REVTYPE, ESTADOINICIAL_ID, ESTADORESULTANTE_ID, BEM_ID, DATAOPERACAO, " +
        "                           DATALANCAMENTO, SITUACAOEVENTOBEM, TIPOEVENTOBEM, VALORDOLANCAMENTO, TIPOOPERACAO, " +
        "                           DETENTORARQUIVOCOMPOSICAO_ID) values (?,?,?,?,?,?,?,?,?,?,?,?,?) ";

    public static final String INSERT_BEMNOTAFISCAL = " insert into bemnotafiscal (ID, BEM_ID, NUMERONOTAFISCAL, DATANOTAFISCAL, DOCTOFISCALLIQUIDACAO_ID) values (?, ?, ?, ?, ?) ";
    public static final String INSERT_BEMNOTAFISCAL_AUD = " insert into bemnotafiscal_aud (ID, BEM_ID, NUMERONOTAFISCAL, DATANOTAFISCAL, DOCTOFISCALLIQUIDACAO_ID, REV, REVTYPE) VALUES (?,?,?,?,?,?,?) ";

    public static final String INSERT_BEMNOTAFISCALEMPENHO = " insert into bemnotafiscalempenho (ID, BEMNOTAFISCAL_ID, NUMEROEMPENHO, DATAEMPENHO, EMPENHO_ID) values (?, ?, ?, ?, ?) ";
    public static final String INSERT_BEMNOTAFISCALEMPENHO_AUD = " insert into bemnotafiscalempenho_aud (ID, BEMNOTAFISCAL_ID, NUMEROEMPENHO, DATAEMPENHO, EMPENHO_ID, REV, REVTYPE) values (?,?,?,?,?,?,?) ";

    public static final String INSERT_DETENTORORIGEMRECURSO = " insert into detentororigemrecurso (ID) values (?) ";
    public static final String INSERT_DETENTORORIGEMRECURSO_AUD = " insert into detentororigemrecurso_aud (ID, REV, REVTYPE) VALUES (?,?,?) ";

    public static final String INSERT_BEMORIGEMRECURSO = " insert into origemrecursobem (ID, DETENTORORIGEMRECURSO_ID, DESCRICAO, FONTEDESPESA, DESPESAORCAMENTARIA) values (?, ?, ?, ?, ?) ";
    public static final String INSERT_BEMORIGEMRECURSO_AUD = " insert into origemrecursobem_aud (ID, DETENTORORIGEMRECURSO_ID, DESCRICAO, FONTEDESPESA, DESPESAORCAMENTARIA, REV, REVTYPE) values (?,?,?,?,?,?,?) ";

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcIntegracaoDocumentoFiscalLiquidacao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public void updateItemDoctoItemEntrada(BigDecimal valorLiquidado, SituacaoDocumentoFiscalEntradaMaterial situacao, ItemDoctoItemEntrada obj) {
        getJdbcTemplate().update(UPDATE_ITEMDOCTOITEMENTRADA, valorLiquidado, situacao.name(), obj.getId());
        getJdbcTemplate().update(INSERT_ITEMDOCTOITEMENTRADA_AUD, getAtributosItemDoctoItemEntradaAud(valorLiquidado, situacao, obj));
    }

    public void updateDoctoFiscalEntradaCompra(SituacaoDocumentoFiscalEntradaMaterial situacao, DoctoFiscalEntradaCompra obj) {
        getJdbcTemplate().update(UPDATE_DOCTOFISCALENTRADACOMPRA, situacao.name(), obj.getId());
        getJdbcTemplate().update(INSERT_DOCTOFISCALENTRADACOMPRA_AUD, getAtributosDoctoFiscalEntradaAud(situacao, obj));
    }

    public void updateDataAquisicaoBem(Date dataAquisicao, Bem obj) {
        getJdbcTemplate().update(UPDATE_BEM_DATAAQUISICAO, dataAquisicao, obj.getId());

        Long idDetentor = obj.getDetentorOrigemRecurso() != null ? obj.getDetentorOrigemRecurso().getId() : null;
        getJdbcTemplate().update(INSERT_BEM_AUD, getAtributosBemAud(dataAquisicao, idDetentor, obj));
    }

    public void updateValorLiquidadoEstadoBem(BigDecimal valorLiquidado, EstadoBem obj) {
        getJdbcTemplate().update(UPDATE_ESTADOBEM, valorLiquidado, obj.getId());
        getJdbcTemplate().update(INSERT_ESTADOBEM_AUD, getAtributosEstadoBemAud(valorLiquidado, obj));
    }

    public void updateEventoBem(SituacaoEventoBem situacao, EventoBem obj) {
        getJdbcTemplate().update(UPDATE_EVENTOBEM, situacao.name(), obj.getId());
        getJdbcTemplate().update(INSERT_EVENTOBEM_AUD, getAtributosEventoBemAud(situacao, obj));
    }

    public void insertBemNotaFiscal(Bem bem, DoctoFiscalLiquidacao docNota, Liquidacao liquidacao) {
        Long idBemNotaFiscal = geradorDeIds.getProximoId();

        getJdbcTemplate().update(INSERT_BEMNOTAFISCAL, getAtributosBemNotaFiscal(idBemNotaFiscal, bem, docNota, false));
        getJdbcTemplate().update(INSERT_BEMNOTAFISCAL_AUD, getAtributosBemNotaFiscal(idBemNotaFiscal, bem, docNota, true));

        getJdbcTemplate().update(INSERT_BEMNOTAFISCALEMPENHO, getAtributosBemNotaFiscalEmpenho(idBemNotaFiscal, liquidacao, false));
        getJdbcTemplate().update(INSERT_BEMNOTAFISCALEMPENHO_AUD, getAtributosBemNotaFiscalEmpenho(idBemNotaFiscal, liquidacao, true));
    }

    public void insertBemOrigemRecurso(Conta conta, Bem bem, Liquidacao liquidacao) {
        Long idDetentor = geradorDeIds.getProximoId();

        getJdbcTemplate().update(INSERT_DETENTORORIGEMRECURSO, idDetentor);
        getJdbcTemplate().update(INSERT_DETENTORORIGEMRECURSO_AUD, idDetentor, gerarRevisaoAuditoria(), 1);

        getJdbcTemplate().update(INSERT_BEMORIGEMRECURSO, getAtributosBemOrigemRecurso(idDetentor, conta, liquidacao, false));
        getJdbcTemplate().update(INSERT_BEMORIGEMRECURSO_AUD, getAtributosBemOrigemRecurso(idDetentor, conta, liquidacao, true));

        getJdbcTemplate().update(UPDATE_BEM_DETENTORORIGEMRECURSO, idDetentor, bem.getId());
        getJdbcTemplate().update(INSERT_BEM_AUD, getAtributosBemAud(bem.getDataAquisicao(), idDetentor, bem));
    }

    private Object[] getAtributosBemNotaFiscal(Long idBemNotaFiscal, Bem bem, DoctoFiscalLiquidacao docNota, Boolean isAud) {
        int x = isAud ? 7 : 5;
        Object[] objetos = new Object[x];
        objetos[0] = isAud ? geradorDeIds.getProximoId() : idBemNotaFiscal;
        objetos[1] = bem.getId();
        objetos[2] = docNota.getNumero();
        objetos[3] = docNota.getDataDocto();
        objetos[4] = docNota.getId();
        if (isAud) {
            objetos[5] = gerarRevisaoAuditoria();
            objetos[6] = 1;
        }
        return objetos;
    }

    private Object[] getAtributosBemNotaFiscalEmpenho(Long idBemNotaFiscal, Liquidacao liquidacao, Boolean isAud) {
        int x = isAud ? 7 : 5;
        Object[] objetos = new Object[x];
        objetos[0] = geradorDeIds.getProximoId();
        objetos[1] = idBemNotaFiscal;
        objetos[2] = liquidacao.getEmpenho().getNumero();
        objetos[3] = liquidacao.getEmpenho().getDataEmpenho();
        objetos[4] = liquidacao.getEmpenho().getId();
        if (isAud) {
            objetos[5] = gerarRevisaoAuditoria();
            objetos[6] = 1;
        }
        return objetos;
    }

    private Object[] getAtributosBemOrigemRecurso(Long idDententor, Conta conta, Liquidacao liquidacao, Boolean isAud) {
        int x = isAud ? 7 : 5;
        Object[] objetos = new Object[x];
        objetos[0] = geradorDeIds.getProximoId();
        objetos[1] = idDententor;
        objetos[2] = "Liquidação " + liquidacao.getCategoriaOrcamentaria().getDescricao();
        objetos[3] = liquidacao.getEmpenho().getFonteDespesaORC().getDescricaoFonteDeRecurso();
        objetos[4] = conta != null ? conta.toString() : "";
        if (isAud) {
            objetos[5] = gerarRevisaoAuditoria();
            objetos[6] = 1;
        }
        return objetos;
    }

    private Object[] getAtributosItemDoctoItemEntradaAud(BigDecimal valor, SituacaoDocumentoFiscalEntradaMaterial situacao, ItemDoctoItemEntrada item) {
        Object[] objetos = new Object[7];
        objetos[0] = geradorDeIds.getProximoId();
        objetos[1] = gerarRevisaoAuditoria();
        objetos[2] = 1;
        objetos[3] = item.getItemEntradaMaterial().getId();
        objetos[5] = valor;
        objetos[6] = situacao.name();
        return objetos;
    }

    private Object[] getAtributosDoctoFiscalEntradaAud(SituacaoDocumentoFiscalEntradaMaterial situacao, DoctoFiscalEntradaCompra obj) {
        Object[] objetos = new Object[6];
        objetos[0] = geradorDeIds.getProximoId();
        objetos[1] = obj.getEntradaCompraMaterial().getId();
        objetos[2] = obj.getDoctoFiscalLiquidacao().getId();
        objetos[3] = gerarRevisaoAuditoria();
        objetos[4] = 1;
        objetos[5] = situacao.name();
        return objetos;
    }

    private Object[] getAtributosBemAud(Date dataAquisicao, Long idDetentor, Bem bem) {
        Object[] objetos = new Object[18];
        objetos[0] = geradorDeIds.getProximoId();
        objetos[1] = gerarRevisaoAuditoria();
        ;
        objetos[2] = 1;
        objetos[3] = bem.getDescricao();
        objetos[4] = bem.getIdentificacao();
        objetos[5] = dataAquisicao;
        objetos[6] = bem.getMarca();
        objetos[7] = bem.getModelo();
        objetos[8] = bem.getSeguradora() != null ? bem.getSeguradora().getId() : null;
        objetos[9] = bem.getGarantia() != null ? bem.getGarantia().getId() : null;
        objetos[10] = bem.getObjetoCompra().getId();
        objetos[11] = bem.getRegistroAnterior();
        objetos[12] = bem.getObservacao();
        objetos[13] = idDetentor;
        objetos[14] = bem.getFornecedor() != null ? bem.getFornecedor().getId() : null;
        objetos[15] = bem.getCondicaoDeOcupacao() != null ? bem.getCondicaoDeOcupacao().getId() : null;
        objetos[16] = bem.getBci();
        objetos[17] = bem.getNumeroRegistro();
        return objetos;
    }

    private Object[] getAtributosEstadoBemAud(BigDecimal valorLiquidado, EstadoBem obj) {
        Object[] objetos = new Object[19];
        objetos[0] = geradorDeIds.getProximoId();
        objetos[1] = gerarRevisaoAuditoria();
        objetos[2] = 1;
        objetos[3] = obj.getValorOriginal();
        objetos[4] = obj.getIdentificacao();
        objetos[5] = obj.getGrupoBem() != null ? obj.getGrupoBem().getId() : null;
        objetos[6] = obj.getDetentoraAdministrativa() != null ? obj.getDetentoraAdministrativa().getId() : null;
        objetos[7] = obj.getEstadoBem().name();
        objetos[8] = obj.getDetentoraOrcamentaria() != null ? obj.getDetentoraOrcamentaria().getId() : null;
        objetos[9] = obj.getTipoGrupo().name();
        objetos[10] = obj.getSituacaoConservacaoBem().name();
        objetos[11] = obj.getValorAcumuladoDeAjuste();
        objetos[12] = obj.getValorAcumuladoDaAmortizacao();
        objetos[13] = obj.getValorAcumuladoDaDepreciacao();
        objetos[14] = obj.getValorAcumuladoDaExaustao();
        objetos[15] = obj.getGrupoObjetoCompra() != null ? obj.getGrupoObjetoCompra().getId() : null;
        objetos[16] = obj.getTipoAquisicaoBem().name();
        objetos[17] = obj.getLocalizacao();
        objetos[18] = valorLiquidado;
        return objetos;
    }

    private Object[] getAtributosEventoBemAud(SituacaoEventoBem situacao, EventoBem obj) {
        Object[] objetos = new Object[13];
        objetos[0] = geradorDeIds.getProximoId();
        objetos[1] = gerarRevisaoAuditoria();
        objetos[2] = 1;
        objetos[3] = obj.getEstadoInicial().getId();
        objetos[4] = obj.getEstadoResultante().getId();
        objetos[5] = obj.getBem().getId();
        objetos[6] = obj.getDataOperacao();
        objetos[7] = obj.getDataLancamento();
        objetos[8] = situacao.name();
        objetos[9] = obj.getTipoEventoBem().name();
        objetos[10] = obj.getValorDoLancamento();
        objetos[11] = obj.getTipoOperacao().name();
        objetos[12] = obj.getDetentorArquivoComposicao() != null ? obj.getDetentorArquivoComposicao().getId() : null;
        return objetos;
    }

    private Long gerarRevisaoAuditoria() {
        Long idRevisao = geradorDeIds.getProximoId();
        getJdbcTemplate().update(INSERT_REVISAO_AUDITORIA, getAtributosRevisaoAuditoria(idRevisao));
        return idRevisao;
    }

    private Object[] getAtributosRevisaoAuditoria(Long idRevisao) {
        Object[] objetos = new Object[4];
        objetos[0] = idRevisao;
        objetos[1] = LocalDateTime.now().toDate();
        objetos[2] = SistemaFacade.obtemIp();
        objetos[3] = SistemaFacade.obtemLogin();
        return objetos;
    }
}
