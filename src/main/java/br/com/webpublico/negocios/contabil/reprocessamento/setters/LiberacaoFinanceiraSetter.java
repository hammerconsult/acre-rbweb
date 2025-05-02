package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.LiberacaoCotaFinanceira;
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
public class LiberacaoFinanceiraSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE_STATUS = "update liberacaocotafinanceira set statuspagamento = ? where id = ?";
    public static final String SQL_UPDATE_INDEFERIR = "update liberacaocotafinanceira set statuspagamento = ?, dataconciliacao = ?, dataliberacao =? where id = ?";

    public static final String SQL_INSERT_AUD = "insert into liberacaocotafinanceira_aud(id, numero, dataliberacao, valor, saldo, observacoes, tipoliberacaofinanceira,\n" +
        "                                    unidadeorganizacional_id, usuariosistema_id, solicitacaocotafinanceira_id,\n" +
        "                                    subconta_id, fontederecursos_id, exercicio_id, unidadeorganizacionaladm_id,\n" +
        "                                    dataconciliacao, statuspagamento, migracaochave, eventocontabildeposito_id,\n" +
        "                                    eventocontabilretirada_id, tipodocumento, tipooperacaopagto, finalidadepagamento_id,\n" +
        "                                    historicorazao, historiconota, recebida, uuid, identificador_id, versao,\n" +
        "                                    contadedestinacao_id, rev, revtype)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final LiberacaoCotaFinanceira liberacaoCotaFinanceira;
    private final Long id;
    private Long idRev;
    private Integer typeRev;

    public LiberacaoFinanceiraSetter(LiberacaoCotaFinanceira liberacaoCotaFinanceira, Long id) {
        this.liberacaoCotaFinanceira = liberacaoCotaFinanceira;
        this.id = id;
    }

    public LiberacaoFinanceiraSetter(LiberacaoCotaFinanceira liberacaoCotaFinanceira, Long id, Long idRev, Integer typeRev) {
        this.liberacaoCotaFinanceira = liberacaoCotaFinanceira;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, id);
        if(Util.isNotNull(liberacaoCotaFinanceira.getNumero())) {
            ps.setString(2, liberacaoCotaFinanceira.getNumero());
        }else {
            ps.setNull(2, Types.VARCHAR);
        }
        ps.setDate(3, new Date(liberacaoCotaFinanceira.getDataLiberacao().getTime()));
        ps.setBigDecimal(4, liberacaoCotaFinanceira.getValor());
        ps.setBigDecimal(5, liberacaoCotaFinanceira.getSaldo());
        ps.setString(6, liberacaoCotaFinanceira.getObservacoes());
        ps.setString(7, liberacaoCotaFinanceira.getTipoLiberacaoFinanceira().name());
        if(Util.isNotNull(liberacaoCotaFinanceira.getUnidadeOrganizacional())) {
            ps.setLong(8, liberacaoCotaFinanceira.getUnidadeOrganizacional().getId());
        }else{
            ps.setNull(8, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(liberacaoCotaFinanceira.getUsuarioSistema())) {
            ps.setLong(9, liberacaoCotaFinanceira.getUsuarioSistema().getId());
        } else {
            ps.setNull(9, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getSolicitacaoCotaFinanceira())) {
            ps.setLong(10, liberacaoCotaFinanceira.getSolicitacaoCotaFinanceira().getId());
        }else{
            ps.setNull(10, Types.LONGNVARCHAR);
        }
        ps.setLong(11, liberacaoCotaFinanceira.getSubConta().getId());
        if(Util.isNotNull(liberacaoCotaFinanceira.getFonteDeRecursos())) {
            ps.setLong(12, liberacaoCotaFinanceira.getFonteDeRecursos().getId());
        }else{
            ps.setNull(12, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getExercicio())) {
            ps.setLong(13, liberacaoCotaFinanceira.getExercicio().getId());
        }else{
            ps.setNull(13, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getUnidadeOrganizacionalAdm())) {
            ps.setLong(14, liberacaoCotaFinanceira.getUnidadeOrganizacionalAdm().getId());
        }else{
            ps.setNull(14, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getDataConciliacao())) {
            ps.setDate(15, new Date(liberacaoCotaFinanceira.getDataConciliacao().getTime()));
        }else {
            ps.setNull(15, Types.DATE);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getStatusPagamento())) {
            ps.setString(16, liberacaoCotaFinanceira.getStatusPagamento().name());
        }else{
            ps.setNull(16, Types.VARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getMigracaoChave())) {
            ps.setString(17, liberacaoCotaFinanceira.getMigracaoChave());
        }else {
            ps.setNull(17, Types.VARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getEventoContabilDeposito())) {
            ps.setLong(18, liberacaoCotaFinanceira.getEventoContabilDeposito().getId());
        }else{
            ps.setNull(18, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getEventoContabilRetirada())) {
            ps.setLong(19, liberacaoCotaFinanceira.getEventoContabilRetirada().getId());
        }else{
            ps.setNull(19, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getTipoDocumento())) {
            ps.setString(20, liberacaoCotaFinanceira.getTipoDocumento().name());
        }else {
            ps.setNull(20, Types.VARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getTipoOperacaoPagto())) {
            ps.setString(21, liberacaoCotaFinanceira.getTipoOperacaoPagto().name());
        }else {
            ps.setNull(21, Types.VARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getFinalidadePagamento())) {
            ps.setLong(22, liberacaoCotaFinanceira.getFinalidadePagamento().getId());
        }else{
            ps.setNull(22, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getHistoricoRazao())) {
            ps.setString(23, liberacaoCotaFinanceira.getHistoricoRazao());
        }else {
            ps.setNull(23, Types.VARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getHistoricoNota())) {
            ps.setString(24, liberacaoCotaFinanceira.getHistoricoNota());
        }else{
            ps.setNull(24, Types.VARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getRecebida())) {
            ps.setDate(25, new Date(liberacaoCotaFinanceira.getRecebida().getTime()));
        }else{
            ps.setNull(25, Types.DATE);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getUuid())) {
            ps.setString(26, liberacaoCotaFinanceira.getUuid());
        }else{
            ps.setNull(26, Types.VARCHAR);
        }
        if(Util.isNotNull(liberacaoCotaFinanceira.getIdentificador())) {
            ps.setLong(27, liberacaoCotaFinanceira.getIdentificador().getId());
        }else{
            ps.setNull(27, Types.LONGNVARCHAR);
        }
        ps.setLong(28, liberacaoCotaFinanceira.getVersao());
        ps.setLong(29, liberacaoCotaFinanceira.getContaDeDestinacaoRetirada().getId());
        if (Util.isNotNull(idRev)) {
            ps.setLong(30, idRev);
            ps.setLong(31, typeRev);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
