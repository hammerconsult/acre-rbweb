package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.TransferenciaContaFinanceira;
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
public class TransferenciaFinanceiraSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE_STATUS = "update transferenciacontafinanc set statuspagamento = ? where id = ?";
    public static final String SQL_UPDATE_INDEFERIR = "update transferenciacontafinanc set statuspagamento = ?, dataconciliacao = ?, recebida =? where id = ?";

    public static final String SQL_INSERT_REV = "insert into transferenciacontafinanc_aud (id, unidadeorganizacional_id, numero, datatransferencia, resultanteindependente,\n" +
        "                                      tipotransferenciafinanceira, subcontadeposito_id, subcontaretirada_id,\n" +
        "                                      fontederecursosdeposito_id, fontederecursosretirada_id, historico, valor, saldo,\n" +
        "                                      exercicio_id, eventocontabil_id, eventocontabilretirada_id,\n" +
        "                                      unidadeorganizacionaladm_id, statuspagamento, dataconciliacao, tipodocumento,\n" +
        "                                      unidadeorgconcedida_id, tipooperacaopagto, finalidadepagamento_id, historicorazao,\n" +
        "                                      historiconota, recebida, uuid, identificador_id, contadedestinacaodeposito_id,\n" +
        "                                      contadedestinacaoretirada_id, rev, revtype)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final TransferenciaContaFinanceira transferenciaContaFinanceira;
    private final Long id;
    private Long idRev;
    private Integer typeRev;

    public TransferenciaFinanceiraSetter(TransferenciaContaFinanceira transferenciaContaFinanceira, Long id) {
        this.transferenciaContaFinanceira = transferenciaContaFinanceira;
        this.id = id;
    }

    public TransferenciaFinanceiraSetter(TransferenciaContaFinanceira transferenciaContaFinanceira, Long id, Long idRev, Integer typeRev) {
        this.transferenciaContaFinanceira = transferenciaContaFinanceira;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, id);
        ps.setLong(2, transferenciaContaFinanceira.getUnidadeOrganizacional().getId());
        ps.setString(3, transferenciaContaFinanceira.getNumero());
        ps.setDate(4, new Date(transferenciaContaFinanceira.getDataTransferencia().getTime()));
        ps.setString(5, transferenciaContaFinanceira.getResultanteIndependente().name());
        if(Util.isNotNull(transferenciaContaFinanceira.getTipoTransferenciaFinanceira())) {
            ps.setString(6, transferenciaContaFinanceira.getTipoTransferenciaFinanceira().name());
        }else{
            ps.setNull(6, Types.VARCHAR);
        }
        ps.setLong(7, transferenciaContaFinanceira.getSubContaDeposito().getId());
        ps.setLong(8, transferenciaContaFinanceira.getSubContaRetirada().getId());
        ps.setLong(9, transferenciaContaFinanceira.getFonteDeRecursosDeposito().getId());
        ps.setLong(10, transferenciaContaFinanceira.getFonteDeRecursosRetirada().getId());
        if(Util.isNotNull(transferenciaContaFinanceira.getHistorico())) {
            ps.setString(11, transferenciaContaFinanceira.getHistorico());
        }else{
            ps.setNull(11, Types.VARCHAR);
        }
        ps.setBigDecimal(12, transferenciaContaFinanceira.getValor());
        ps.setBigDecimal(13, transferenciaContaFinanceira.getSaldo());
        ps.setLong(14, transferenciaContaFinanceira.getExercicio().getId());
        if (Util.isNotNull(transferenciaContaFinanceira.getEventoContabil())) {
            ps.setLong(15, transferenciaContaFinanceira.getEventoContabil().getId());
        } else {
            ps.setNull(15, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(transferenciaContaFinanceira.getEventoContabilRetirada())) {
            ps.setLong(16, transferenciaContaFinanceira.getEventoContabilRetirada().getId());
        } else {
            ps.setNull(16, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(transferenciaContaFinanceira.getUnidadeOrganizacionalAdm())) {
            ps.setLong(17, transferenciaContaFinanceira.getUnidadeOrganizacionalAdm().getId());
        } else {
            ps.setNull(17, Types.LONGNVARCHAR);
        }
        ps.setString(18, transferenciaContaFinanceira.getStatusPagamento().name());
        if(Util.isNotNull(transferenciaContaFinanceira.getDataConciliacao())) {
            ps.setDate(19, new Date(transferenciaContaFinanceira.getDataConciliacao().getTime()));
        }else {
            ps.setNull(19, Types.DATE);
        }
        if (Util.isNotNull(transferenciaContaFinanceira.getTipoDocumento())) {
            ps.setString(20, transferenciaContaFinanceira.getTipoDocumento().name());
        } else {
            ps.setNull(20, Types.VARCHAR);
        }
        ps.setLong(21, transferenciaContaFinanceira.getUnidadeOrgConcedida().getId());
        if (Util.isNotNull(transferenciaContaFinanceira.getTipoOperacaoPagto())) {
            ps.setString(22, transferenciaContaFinanceira.getTipoOperacaoPagto().name());
        } else {
            ps.setNull(22, Types.VARCHAR);
        }
        if (Util.isNotNull(transferenciaContaFinanceira.getFinalidadePagamento())) {
            ps.setLong(23, transferenciaContaFinanceira.getFinalidadePagamento().getId());
        } else {
            ps.setNull(23, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(transferenciaContaFinanceira.getHistoricoRazao())) {
            ps.setString(24, transferenciaContaFinanceira.getHistoricoRazao());
        } else {
            ps.setNull(24, Types.VARCHAR);
        }
        if (Util.isNotNull(transferenciaContaFinanceira.getHistoricoNota())) {
            ps.setString(25, transferenciaContaFinanceira.getHistoricoNota());
        } else {
            ps.setNull(25, Types.VARCHAR);
        }
        if(Util.isNotNull(transferenciaContaFinanceira.getRecebida())) {
            ps.setDate(26, new Date(transferenciaContaFinanceira.getRecebida().getTime()));
        }else{
            ps.setNull(26, Types.DATE);
        }
        if (Util.isNotNull(transferenciaContaFinanceira.getUuid())) {
            ps.setString(27, transferenciaContaFinanceira.getUuid());
        } else {
            ps.setNull(27, Types.VARCHAR);
        }
        if (Util.isNotNull(transferenciaContaFinanceira.getIdentificador())) {
            ps.setLong(28, transferenciaContaFinanceira.getIdentificador().getId());
        } else {
            ps.setNull(28, Types.LONGNVARCHAR);
        }
        ps.setLong(29, transferenciaContaFinanceira.getContaDeDestinacaoDeposito().getId());
        ps.setLong(30, transferenciaContaFinanceira.getContaDeDestinacaoRetirada().getId());
        if (Util.isNotNull(idRev)) {
            ps.setLong(31, idRev);
            ps.setLong(32, typeRev);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
