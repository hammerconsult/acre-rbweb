package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.TransferenciaMesmaUnidade;
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
public class TransferenciaMesmaUnidadeSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE_STATUS = "update transferenciamesmaunidade set statuspagamento = ? where id = ?";
    public static final String SQL_UPDATE_INDEFERIR = "update transferenciamesmaunidade set statuspagamento = ?, dataconciliacao = ?, recebida =? where id = ?";

    public static final String SQL_INSERT_AUD = "insert into transferenciamesmaunidade_aud (id, unidadeorganizacional_id, numero, datatransferencia, tipotransferencia,\n" +
        "                                       subcontadeposito_id, subcontaretirada_id, fontederecursosdeposito_id,\n" +
        "                                       fontederecursosretirada_id, historico, valor, saldo, eventocontabil_id,\n" +
        "                                       resultanteindependente, eventocontabilretirada_id, unidadeorganizacionaladm_id,\n" +
        "                                       statuspagamento, dataconciliacao, exercicio_id, tipooperacaopagto,\n" +
        "                                       finalidadepagamento_id, tipodocpagto, historicorazao, historiconota, recebida,\n" +
        "                                       uuid, identificador_id, contadedestinacaodeposito_id,\n" +
        "                                       contadedestinacaoretirada_id, rev, revtype)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final TransferenciaMesmaUnidade transferenciaMesmaUnidade;
    private final Long id;

    private Long idRev;
    private Integer typeRev;

    public TransferenciaMesmaUnidadeSetter(TransferenciaMesmaUnidade transferenciaMesmaUnidade, Long id) {
        this.transferenciaMesmaUnidade = transferenciaMesmaUnidade;
        this.id = id;
    }

    public TransferenciaMesmaUnidadeSetter(TransferenciaMesmaUnidade transferenciaMesmaUnidade, Long id, Long idRev, Integer typeRev) {
        this.transferenciaMesmaUnidade = transferenciaMesmaUnidade;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setLong(1, id);
        ps.setLong(2, transferenciaMesmaUnidade.getUnidadeOrganizacional().getId());
        if (Util.isNotNull(transferenciaMesmaUnidade.getNumero())) {
            ps.setString(3, transferenciaMesmaUnidade.getNumero());
        } else {
            ps.setNull(3, Types.VARCHAR);
        }
        if (Util.isNotNull(transferenciaMesmaUnidade.getDataTransferencia())) {
            ps.setDate(4, new Date(transferenciaMesmaUnidade.getDataTransferencia().getTime()));
        } else {
            ps.setNull(4, Types.DATE);
        }
        if(Util.isNotNull(transferenciaMesmaUnidade.getTipoTransferencia())) {
            ps.setString(5, transferenciaMesmaUnidade.getTipoTransferencia().name());
        }else{
            ps.setNull(5, Types.VARCHAR);
        }
        ps.setLong(6, transferenciaMesmaUnidade.getSubContaDeposito().getId());
        ps.setLong(7, transferenciaMesmaUnidade.getSubContaRetirada().getId());
        ps.setLong(8, transferenciaMesmaUnidade.getFonteDeRecursosDeposito().getId());
        ps.setLong(9, transferenciaMesmaUnidade.getFonteDeRecursosRetirada().getId());
        if(Util.isNotNull(transferenciaMesmaUnidade.getHistorico())) {
            ps.setString(10, transferenciaMesmaUnidade.getHistorico());
        }else{
            ps.setNull(10, Types.VARCHAR);
        }
        ps.setBigDecimal(11, transferenciaMesmaUnidade.getValor());
        ps.setBigDecimal(12, transferenciaMesmaUnidade.getSaldo());
        if (Util.isNotNull(transferenciaMesmaUnidade.getEventoContabil())) {
            ps.setLong(13, transferenciaMesmaUnidade.getEventoContabil().getId());
        } else {
            ps.setNull(13, Types.LONGNVARCHAR);
        }
        ps.setString(14, transferenciaMesmaUnidade.getResultanteIndependente().name());
        if (Util.isNotNull(transferenciaMesmaUnidade.getEventoContabilRetirada())) {
            ps.setLong(15, transferenciaMesmaUnidade.getEventoContabilRetirada().getId());
        } else {
            ps.setNull(15, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(transferenciaMesmaUnidade.getUnidadeOrganizacionalAdm())) {
            ps.setLong(16, transferenciaMesmaUnidade.getUnidadeOrganizacionalAdm().getId());
        } else {
            ps.setNull(16, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(transferenciaMesmaUnidade.getStatusPagamento())) {
            ps.setString(17, transferenciaMesmaUnidade.getStatusPagamento().name());
        } else {
            ps.setNull(17, Types.VARCHAR);
        }
        if (Util.isNotNull(transferenciaMesmaUnidade.getDataConciliacao())) {
            ps.setDate(18, new Date(transferenciaMesmaUnidade.getDataConciliacao().getTime()));
        } else {
            ps.setNull(18, Types.DATE);
        }
        ps.setLong(19, transferenciaMesmaUnidade.getExercicio().getId());
        if (Util.isNotNull(transferenciaMesmaUnidade.getTipoOperacaoPagto())) {
            ps.setString(20, transferenciaMesmaUnidade.getTipoOperacaoPagto().name());
        } else {
            ps.setNull(20, Types.VARCHAR);
        }
        if (Util.isNotNull(transferenciaMesmaUnidade.getFinalidadePagamento())) {
            ps.setLong(21, transferenciaMesmaUnidade.getFinalidadePagamento().getId());
        } else {
            ps.setNull(21, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(transferenciaMesmaUnidade.getTipoDocPagto())) {
            ps.setString(22, transferenciaMesmaUnidade.getTipoDocPagto().name());
        }else{
            ps.setNull(22, Types.VARCHAR);
        }
        if(Util.isNotNull(transferenciaMesmaUnidade.getHistoricoRazao())) {
            ps.setString(23, transferenciaMesmaUnidade.getHistoricoRazao());
        }else {
            ps.setNull(23, Types.VARCHAR);
        }
        if(Util.isNotNull(transferenciaMesmaUnidade.getHistoricoNota())) {
            ps.setString(24, transferenciaMesmaUnidade.getHistoricoNota());
        }else{
            ps.setNull(24, Types.VARCHAR);
        }
        if (Util.isNotNull(transferenciaMesmaUnidade.getRecebida())) {
            ps.setDate(25, new Date(transferenciaMesmaUnidade.getRecebida().getTime()));
        } else {
            ps.setNull(25, Types.DATE);
        }
        ps.setString(26, transferenciaMesmaUnidade.getUuid());
        if (Util.isNotNull(transferenciaMesmaUnidade.getIdentificador())) {
            ps.setLong(27, transferenciaMesmaUnidade.getIdentificador().getId());
        } else {
            ps.setNull(27, Types.LONGNVARCHAR);
        }
        ps.setLong(28, transferenciaMesmaUnidade.getContaDeDestinacaoDeposito().getId());
        ps.setLong(29, transferenciaMesmaUnidade.getContaDeDestinacaoRetirada().getId());
        if (Util.isNotNull(idRev)) {
            ps.setLong(30, idRev);
            ps.setInt(31, typeRev);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
