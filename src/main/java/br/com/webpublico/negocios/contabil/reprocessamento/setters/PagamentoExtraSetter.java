package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.PagamentoExtra;
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
public class PagamentoExtraSetter implements BatchPreparedStatementSetter {

    public static final String SQL_UPDATE = "update pagamentoextra set status = ?, numeropagamento = ?, numerore = ? where id = ?";

    public static final String SQL_UPDATE_STATUS = "update pagamentoextra set status = ? where id = ?";
    public static final String SQL_UPDATE_INDEFERIR = "update pagamentoextra set status = ?, dataconciliacao = ? where id = ?";

    public static final String SQL_INSERT_AUD = "insert into pagamentoextra_aud (datapagto, complementohistorico, fornecedor_id, numero, contaextraorcamentaria_id,\n" +
        "                            fontederecursos_id, previstopara, usuariosistema_id, valor, saldo, unidadeorganizacional_id,\n" +
        "                            subconta_id, status, classecredor_id, eventocontabil_id, exercicio_id,\n" +
        "                            unidadeorganizacionaladm_id, tipooperacaopagto, numeropagamento, tipodocumento,\n" +
        "                            dataconciliacao, finalidadepagamento_id, contacorrentebancaria_id, historicorazao,\n" +
        "                            historiconota, numerore, uuid, identificador_id, versao, identificadordeposito,\n" +
        "                            contadedestinacao_id, idobn600, idobn350, rev, revtype, id)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_INSERT = "insert into pagamentoextra (datapagto, complementohistorico, fornecedor_id, numero, contaextraorcamentaria_id,\n" +
        "                            fontederecursos_id, previstopara, usuariosistema_id, valor, saldo, unidadeorganizacional_id,\n" +
        "                            subconta_id, status, classecredor_id, eventocontabil_id, exercicio_id,\n" +
        "                            unidadeorganizacionaladm_id, tipooperacaopagto, numeropagamento, tipodocumento,\n" +
        "                            dataconciliacao, finalidadepagamento_id, contacorrentebancaria_id, historicorazao,\n" +
        "                            historiconota, numerore, uuid, identificador_id, versao, identificadordeposito,\n" +
        "                            contadedestinacao_id, idobn600, idobn350, id)\n" +
        "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    public static final String SQL_UPDATE_ALL = "update pagamentoextra\n" +
        "set datapagto                   = ?,\n" +
        "    complementohistorico        = ?,\n" +
        "    fornecedor_id               = ?,\n" +
        "    numero                      = ?,\n" +
        "    contaextraorcamentaria_id   = ?,\n" +
        "    fontederecursos_id          = ?,\n" +
        "    previstopara                = ?,\n" +
        "    usuariosistema_id           = ?,\n" +
        "    valor                       = ?,\n" +
        "    saldo                       = ?,\n" +
        "    unidadeorganizacional_id    = ?,\n" +
        "    subconta_id                 = ?,\n" +
        "    status                      = ?,\n" +
        "    classecredor_id             = ?,\n" +
        "    eventocontabil_id           = ?,\n" +
        "    exercicio_id                = ?,\n" +
        "    unidadeorganizacionaladm_id = ?,\n" +
        "    tipooperacaopagto           = ?,\n" +
        "    numeropagamento             = ?,\n" +
        "    tipodocumento               = ?,\n" +
        "    dataconciliacao             = ?,\n" +
        "    finalidadepagamento_id      = ?,\n" +
        "    contacorrentebancaria_id    = ?,\n" +
        "    historicorazao              = ?,\n" +
        "    historiconota               = ?,\n" +
        "    numerore                    = ?,\n" +
        "    uuid                        = ?,\n" +
        "    identificador_id            = ?,\n" +
        "    versao                      = ?,\n" +
        "    identificadordeposito       = ?,\n" +
        "    contadedestinacao_id        = ?,\n" +
        "    idobn600                    = ?,\n" +
        "    idobn350                    = ?\n" +
        "where id = ? ";
    private final PagamentoExtra pagamentoExtra;
    private final Long id;
    private Long idRev;
    private Integer typeRev;

    public PagamentoExtraSetter(PagamentoExtra pagamentoExtra, Long id) {
        this.pagamentoExtra = pagamentoExtra;
        this.id = id;
    }

    public PagamentoExtraSetter(PagamentoExtra pagamentoExtra, Long id, Long idRev, Integer typeRev) {
        this.pagamentoExtra = pagamentoExtra;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {

        if (Util.isNotNull(pagamentoExtra.getDataPagto())) {
            ps.setDate(1, new Date(pagamentoExtra.getDataPagto().getTime()));
        } else {
            ps.setNull(1, Types.DATE);
        }
        ps.setString(2, pagamentoExtra.getComplementoHistorico());
        ps.setLong(3, pagamentoExtra.getFornecedor().getId());
        ps.setString(4, pagamentoExtra.getNumero());
        ps.setLong(5, pagamentoExtra.getContaExtraorcamentaria().getId());
        ps.setLong(6, pagamentoExtra.getFonteDeRecursos().getId());
        ps.setDate(7, new Date(pagamentoExtra.getPrevistoPara().getTime()));
        if (Util.isNotNull(pagamentoExtra.getUsuarioSistema())) {
            ps.setLong(8, pagamentoExtra.getUsuarioSistema().getId());
        } else {
            ps.setNull(8, Types.LONGNVARCHAR);
        }
        ps.setBigDecimal(9, pagamentoExtra.getValor());
        ps.setBigDecimal(10, pagamentoExtra.getSaldo());
        ps.setLong(11, pagamentoExtra.getUnidadeOrganizacional().getId());
        ps.setLong(12, pagamentoExtra.getSubConta().getId());
        ps.setString(13, pagamentoExtra.getStatus().name());
        ps.setLong(14, pagamentoExtra.getClasseCredor().getId());
        if (Util.isNotNull(pagamentoExtra.getEventoContabil())) {
            ps.setLong(15, pagamentoExtra.getEventoContabil().getId());
        } else {
            ps.setNull(15, Types.LONGNVARCHAR);
        }
        ps.setLong(16, pagamentoExtra.getExercicio().getId());
        if (Util.isNotNull(pagamentoExtra.getUnidadeOrganizacionalAdm())) {
            ps.setLong(17, pagamentoExtra.getUnidadeOrganizacionalAdm().getId());
        } else {
            ps.setNull(17, Types.LONGNVARCHAR);
        }
        ps.setString(18, pagamentoExtra.getTipoOperacaoPagto().name());
        if (Util.isNotNull(pagamentoExtra.getNumeroPagamento())) {
            ps.setString(19, pagamentoExtra.getNumeroPagamento());
        } else {
            ps.setNull(19, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamentoExtra.getTipoDocumento())) {
            ps.setString(20, pagamentoExtra.getTipoDocumento().name());
        } else {
            ps.setNull(20, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamentoExtra.getDataConciliacao())) {
            ps.setDate(21, new Date(pagamentoExtra.getDataConciliacao().getTime()));
        } else {
            ps.setNull(21, Types.DATE);
        }
        if (Util.isNotNull(pagamentoExtra.getFinalidadePagamento())) {
            ps.setLong(22, pagamentoExtra.getFinalidadePagamento().getId());
        } else {
            ps.setNull(22, Types.LONGNVARCHAR);
        }

        if (Util.isNotNull(pagamentoExtra.getContaCorrenteBancaria())) {
            ps.setLong(23, pagamentoExtra.getContaCorrenteBancaria().getId());
        } else {
            ps.setNull(23, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(pagamentoExtra.getHistoricoRazao())) {
            ps.setString(24, pagamentoExtra.getHistoricoRazao());
        } else {
            ps.setNull(24, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamentoExtra.getHistoricoNota())) {
            ps.setString(25, pagamentoExtra.getHistoricoNota());
        } else {
            ps.setNull(25, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamentoExtra.getNumeroRE())) {
            ps.setString(26, pagamentoExtra.getNumeroRE());
        } else {
            ps.setNull(26, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamentoExtra.getUuid())) {
            ps.setString(27, pagamentoExtra.getUuid());
        } else {
            ps.setNull(27, Types.VARCHAR);
        }
        if (Util.isNotNull(pagamentoExtra.getIdentificador())) {
            ps.setLong(28, pagamentoExtra.getIdentificador().getId());
        } else {
            ps.setNull(28, Types.LONGNVARCHAR);
        }
        ps.setLong(29, pagamentoExtra.getVersao());
        if (Util.isNotNull(pagamentoExtra.getIdentificadorDeposito())) {
            ps.setString(30, pagamentoExtra.getIdentificadorDeposito());
        } else {
            ps.setNull(30, Types.VARCHAR);
        }
        ps.setLong(31, pagamentoExtra.getContaDeDestinacao().getId());
        if (Util.isNotNull(pagamentoExtra.getIdObn600())) {
            ps.setLong(32, pagamentoExtra.getIdObn600());
        } else {
            ps.setNull(32, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(pagamentoExtra.getIdObn350())) {
            ps.setLong(33, pagamentoExtra.getIdObn350());
        } else {
            ps.setNull(33, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(idRev)) {
            ps.setLong(34, idRev);
            ps.setInt(35, typeRev);
            ps.setLong(36, id);
        } else {
            ps.setLong(34, id);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
