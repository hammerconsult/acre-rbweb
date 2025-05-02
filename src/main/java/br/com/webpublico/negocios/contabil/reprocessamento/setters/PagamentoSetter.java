package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.Pagamento;
import br.com.webpublico.util.Util;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/10/14
 * Time: 10:14
 * To change this template use File | Settings | File Templates.
 */
public class PagamentoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE = "update pagamento set status = ?, numdocumento = ?, numerore = ? where id = ?";
    public static final String SQL_UPDATE_STATUS = "update pagamento set status = ? where id = ?";

    public static final String SQL_INSERT_AUD = "insert into pagamento_aud (datapagamento, dataregistro, valor, saldo, previstopara, usuariosistema_id,\n" +
        "                           historicocontabil_id, liquidacao_id, numeropagamento, numeroprevisao, pago,\n" +
        "                           movimentodespesaorc_id, status, subconta_id, categoriaorcamentaria, pagamento_id,\n" +
        "                           exercicio_id,\n" +
        "                           numeropagamentooriginal, exerciciooriginal_id, complementohistorico, tipodocpagto,\n" +
        "                           numdocumento,\n" +
        "                           valorfinal, unidadeorganizacionaladm_id, unidadeorganizacional_id, eventocontabil_id,\n" +
        "                           historiconota, historicorazao, tipooperacaopagto, contapgto_id, dataconciliacao,\n" +
        "                           finalidadepagamento_id, saldofinal, numerore, uuid, identificador_id, versao, idobn600,\n" +
        "                           idobn350, rev, revtype, id)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,\n" +
        "        ?, ?, ?, ?, ?)";
    public static final String SQL_UPDATE_ALL = "update pagamento\n" +
        "set datapagamento          = ?,\n" +
        "    dataregistro           = ?,\n" +
        "    valor                  = ?,\n" +
        "    saldo                  = ?,\n" +
        "    previstopara           = ?,\n" +
        "    usuariosistema_id      = ?,\n" +
        "    historicocontabil_id   = ?,\n" +
        "    liquidacao_id          = ?,\n" +
        "    numeropagamento        = ?,\n" +
        "    numeroprevisao         = ?,\n" +
        "    pago                   = ?,\n" +
        "    movimentodespesaorc_id = ?,\n" +
        "    status                 = ?,\n" +
        "    subconta_id            = ?,\n" +
        "    categoriaorcamentaria  = ?,\n" +
        "    pagamento_id           = ?,\n" +
        "    exercicio_id = ?,\n" +
        "    numeropagamentooriginal = ?,\n" +
        "    exerciciooriginal_id = ?,\n" +
        "    complementohistorico = ?,\n" +
        "    tipodocpagto = ?,\n" +
        "    numdocumento = ?,\n" +
        "    valorfinal = ?,\n" +
        "    unidadeorganizacionaladm_id = ?,\n" +
        "    unidadeorganizacional_id = ?,\n" +
        "    eventocontabil_id = ?,\n" +
        "    historiconota = ?,\n" +
        "    historicorazao = ?,\n" +
        "    tipooperacaopagto = ?,\n" +
        "    contapgto_id = ?,\n" +
        "    dataconciliacao = ?,\n" +
        "    finalidadepagamento_id = ?,\n" +
        "    saldofinal = ?,\n" +
        "    numerore = ?,\n" +
        "    uuid = ?,\n" +
        "    identificador_id = ?,\n" +
        "    versao = ?,\n" +
        "    idobn600 = ?,\n" +
        "    idobn350 = ?\n" +
        "where id = ?";

    public static final String SQL_INSERT = "insert into pagamento (datapagamento, dataregistro, valor, saldo, previstopara, usuariosistema_id,\n" +
        "                       historicocontabil_id, liquidacao_id, numeropagamento, numeroprevisao, pago,\n" +
        "                       movimentodespesaorc_id, status, subconta_id, categoriaorcamentaria, pagamento_id, exercicio_id,\n" +
        "                       numeropagamentooriginal, exerciciooriginal_id, complementohistorico, tipodocpagto, numdocumento,\n" +
        "                       valorfinal, unidadeorganizacionaladm_id, unidadeorganizacional_id, eventocontabil_id,\n" +
        "                       historiconota, historicorazao, tipooperacaopagto, contapgto_id, dataconciliacao,\n" +
        "                       finalidadepagamento_id, saldofinal, numerore, uuid, identificador_id, versao, idobn600, idobn350, id)\n" +
        "values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private final Pagamento pagamento;
    private final Long id;
    private Long idRev;

    private Integer typeRev;

    public PagamentoSetter(Pagamento pagamento, Long id) {
        this.pagamento = pagamento;
        this.id = id;
    }

    public PagamentoSetter(Pagamento pagamento, Long id, Long idRev, Integer typeRev) {
        this.pagamento = pagamento;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }


    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        if(Util.isNotNull(pagamento.getDataPagamento())) {
            ps.setDate(1, new Date(pagamento.getDataPagamento().getTime()));
        }else {
            ps.setNull(1, Types.DATE);
        }
        if (Util.isNotNull(pagamento.getDataRegistro())) {
            ps.setDate(2, new Date(pagamento.getDataRegistro().getTime()));
        } else {
            ps.setNull(2, Types.DATE);
        }
        ps.setBigDecimal(3, pagamento.getValor());
        ps.setBigDecimal(4, pagamento.getSaldo());
        ps.setDate(5, new Date(pagamento.getPrevistoPara().getTime()));
        if (Util.isNotNull(pagamento.getUsuarioSistema())) {
            ps.setLong(6, pagamento.getUsuarioSistema().getId());
        } else {
            ps.setNull(6, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(pagamento.getHistoricoContabil())) {
            ps.setLong(7, pagamento.getHistoricoContabil().getId());
        } else {
            ps.setNull(7, Types.LONGNVARCHAR);
        }
        ps.setLong(8, pagamento.getLiquidacao().getId());
        if(Util.isNotNull(pagamento.getNumeroPagamento())) {
            ps.setString(9, pagamento.getNumeroPagamento());
        }else{
            ps.setNull(9, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamento.getNumeroPrevisao())) {
            ps.setString(10, pagamento.getNumeroPrevisao());
        } else {
            ps.setNull(10, Types.VARCHAR);
        }
        ps.setBoolean(11, pagamento.getPago());
        if (Util.isNotNull(pagamento.getMovimentoDespesaORC())) {
            ps.setLong(12, pagamento.getMovimentoDespesaORC().getId());
        } else {
            ps.setNull(12, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(pagamento.getStatus())) {
            ps.setString(13, pagamento.getStatus().name());
        }else{
            ps.setNull(13, Types.VARCHAR);
        }
        ps.setLong(14, pagamento.getSubConta().getId());
        if(Util.isNotNull(pagamento.getCategoriaOrcamentaria())) {
            ps.setString(15, pagamento.getCategoriaOrcamentaria().name());
        }else{
            ps.setNull(15, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamento.getPagamento())) {
            ps.setLong(16, pagamento.getPagamento().getId());
        } else {
            ps.setNull(16, Types.LONGNVARCHAR);
        }
        ps.setLong(17, pagamento.getExercicio().getId());
        if (Util.isNotNull(pagamento.getNumeroPagamentoOriginal())) {
            ps.setString(18, pagamento.getNumeroPagamentoOriginal());
        } else {
            ps.setNull(18, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamento.getExercicioOriginal())) {
            ps.setLong(19, pagamento.getExercicioOriginal().getId());
        } else {
            ps.setNull(19, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(pagamento.getComplementoHistorico())) {
            ps.setString(20, pagamento.getComplementoHistorico());
        } else {
            ps.setNull(20, Types.VARCHAR);
        }
        if(Util.isNotNull(pagamento.getTipoDocPagto())) {
            ps.setString(21, pagamento.getTipoDocPagto().name());
        }else{
            ps.setNull(21, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamento.getNumDocumento())) {
            ps.setString(22, pagamento.getNumDocumento());
        } else {
            ps.setNull(22, Types.VARCHAR);
        }
        if(Util.isNotNull(pagamento.getValorFinal())) {
            ps.setBigDecimal(23, pagamento.getValorFinal());
        }else{
            ps.setNull(23, Types.DECIMAL);
        }
        if(Util.isNotNull(pagamento.getUnidadeOrganizacionalAdm())) {
            ps.setLong(24, pagamento.getUnidadeOrganizacionalAdm().getId());
        }else {
            ps.setNull(24, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(pagamento.getUnidadeOrganizacional())) {
            ps.setLong(25, pagamento.getUnidadeOrganizacional().getId());
        }else {
            ps.setNull(25, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(pagamento.getEventoContabil())) {
            ps.setLong(26, pagamento.getEventoContabil().getId());
        } else {
            ps.setNull(26, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(pagamento.getHistoricoNota())) {
            ps.setString(27, pagamento.getHistoricoNota());
        }else{
            ps.setNull(27, Types.VARCHAR);
        }
        if(Util.isNotNull(pagamento.getHistoricoRazao())) {
            ps.setString(28, pagamento.getHistoricoRazao());
        }else {
            ps.setNull(28, Types.VARCHAR);
        }
        if(Util.isNotNull(pagamento.getTipoOperacaoPagto())) {
            ps.setString(29, pagamento.getTipoOperacaoPagto().name());
        }else{
            ps.setNull(29, Types.VARCHAR);
        }
        if(Util.isNotNull(pagamento.getContaPgto())) {
            ps.setLong(30, pagamento.getContaPgto().getId());
        }else{
            ps.setNull(30, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(pagamento.getDataConciliacao())) {
            ps.setDate(31, new Date(pagamento.getDataConciliacao().getTime()));
        } else {
            ps.setNull(31, Types.DATE);
        }
        if(Util.isNotNull(pagamento.getFinalidadePagamento())) {
            ps.setLong(32, pagamento.getFinalidadePagamento().getId());
        }else{
            ps.setNull(32, Types.LONGNVARCHAR);
        }
        ps.setBigDecimal(33, pagamento.getSaldoFinal());
        if (Util.isNotNull(pagamento.getNumeroRE())) {
            ps.setString(34, pagamento.getNumeroRE());
        } else {
            ps.setNull(34, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamento.getUuid())) {
            ps.setString(35, pagamento.getUuid());
        } else {
            ps.setNull(35, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamento.getIdentificador())) {
            ps.setLong(36, pagamento.getIdentificador().getId());
        } else {
            ps.setNull(36, Types.LONGNVARCHAR);
        }
        ps.setLong(37, pagamento.getVersao());
        if (Util.isNotNull(pagamento.getIdObn600())) {
            ps.setLong(38, pagamento.getIdObn600());
        } else {
            ps.setNull(38, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(pagamento.getIdObn350())) {
            ps.setLong(39, pagamento.getIdObn350());
        } else {
            ps.setNull(39, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(idRev)) {
            ps.setLong(40, idRev);
            ps.setInt(41, typeRev);
            ps.setLong(42, id);
        }else{
            ps.setLong(40, id);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
