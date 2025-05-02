package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.GuiaPagamento;
import br.com.webpublico.entidades.PagamentoEstorno;
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
public class PagamentoEstornoSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_AUD = "insert into pagamentoestorno_aud (dataestorno, valor, numero, pagamento_id, movimentodespesaorc_id,\n" +
        "                              categoriaorcamentaria, historicocontabil_id, complementohistorico,\n" +
        "                              unidadeorganizacionaladm_id, unidadeorganizacional_id, dataconciliacao, historicorazao,\n" +
        "                              historiconota, eventocontabil_id, usuariosistema_id, valorfinal, exercicio_id, uuid,\n" +
        "                              identificador_id, rev, revtype, id) \n" +
        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static final String SQL_UPDATE_ALL = "update pagamentoestorno\n" +
        "set dataestorno = ?,\n" +
        "    valor = ?,\n" +
        "    numero = ?,\n" +
        "    pagamento_id = ?,\n" +
        "    movimentodespesaorc_id = ?,\n" +
        "    categoriaorcamentaria = ?,\n" +
        "    historicocontabil_id = ?,\n" +
        "    complementohistorico = ?,\n" +
        "    unidadeorganizacionaladm_id = ?,\n" +
        "    unidadeorganizacional_id = ?,\n" +
        "    dataconciliacao = ?,\n" +
        "    historicorazao = ?,\n" +
        "    historiconota = ?,\n" +
        "    eventocontabil_id = ?,\n" +
        "    usuariosistema_id = ?,\n" +
        "    valorfinal = ?,\n" +
        "    exercicio_id = ?,\n" +
        "    uuid = ?,\n" +
        "    identificador_id = ?\n" +
        "where id = ?";

    public static final String SQL_INSERT = "insert into pagamentoestorno (dataestorno, valor, numero, pagamento_id, movimentodespesaorc_id,\n" +
        "                              categoriaorcamentaria, historicocontabil_id, complementohistorico,\n" +
        "                              unidadeorganizacionaladm_id, unidadeorganizacional_id, dataconciliacao, historicorazao,\n" +
        "                              historiconota, eventocontabil_id, usuariosistema_id, valorfinal, exercicio_id, uuid,\n" +
        "                              identificador_id, id) \n" +
        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final PagamentoEstorno pagamentoEstorno;
    private final Long id;
    private Long idRev;

    private Integer typeRev;

    public PagamentoEstornoSetter(PagamentoEstorno pagamentoEstorno, Long id) {
        this.pagamentoEstorno = pagamentoEstorno;
        this.id = id;
    }

    public PagamentoEstornoSetter(PagamentoEstorno pagamentoEstorno, Long id, Long idRev, Integer typeRev) {
        this.pagamentoEstorno = pagamentoEstorno;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }


    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setDate(1, new Date(pagamentoEstorno.getDataEstorno().getTime()));
        ps.setBigDecimal(2, pagamentoEstorno.getValor());
        if(Util.isNotNull(pagamentoEstorno.getNumero())){
            ps.setString(3, pagamentoEstorno.getNumero());
        }else {
            ps.setNull(3, Types.VARCHAR);
        }
        ps.setLong(4, pagamentoEstorno.getPagamento().getId());
        if(Util.isNotNull(pagamentoEstorno.getMovimentoDespesaORC())){
            ps.setLong(5, pagamentoEstorno.getMovimentoDespesaORC().getId());
        }else {
            ps.setNull(5, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(pagamentoEstorno.getCategoriaOrcamentaria())){
            ps.setString(6, pagamentoEstorno.getCategoriaOrcamentaria().name());
        }else {
            ps.setNull(6, Types.VARCHAR);
        }
        if(Util.isNotNull(pagamentoEstorno.getHistoricoContabil())){
            ps.setLong(7, pagamentoEstorno.getHistoricoContabil().getId());
        }else {
            ps.setNull(7, Types.LONGNVARCHAR);
        }
        ps.setString(8, pagamentoEstorno.getComplementoHistorico());
        if(Util.isNotNull(pagamentoEstorno.getUnidadeOrganizacionalAdm())){
            ps.setLong(9, pagamentoEstorno.getUnidadeOrganizacionalAdm().getId());
        }else {
            ps.setNull(9, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(pagamentoEstorno.getUnidadeOrganizacional())){
            ps.setLong(10, pagamentoEstorno.getUnidadeOrganizacional().getId());
        }else {
            ps.setNull(10, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(pagamentoEstorno.getDataConciliacao())){
            ps.setDate(11, new Date(pagamentoEstorno.getDataConciliacao().getTime()));
        }else {
            ps.setNull(11, Types.DATE);
        }
        if(Util.isNotNull(pagamentoEstorno.getHistoricoRazao())){
            ps.setString(12, pagamentoEstorno.getHistoricoRazao());
        }else {
            ps.setNull(12, Types.VARCHAR);
        }
        if(Util.isNotNull(pagamentoEstorno.getHistoricoNota())){
            ps.setString(13, pagamentoEstorno.getHistoricoNota());
        }else {
            ps.setNull(13, Types.VARCHAR);
        }
        ps.setLong(14, pagamentoEstorno.getEventoContabil().getId());
        if(Util.isNotNull(pagamentoEstorno.getUsuarioSistema())){
            ps.setLong(15, pagamentoEstorno.getUsuarioSistema().getId());
        }else {
            ps.setNull(15, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(pagamentoEstorno.getValorFinal())){
            ps.setBigDecimal(16, pagamentoEstorno.getValorFinal());
        }else {
            ps.setNull(16, Types.DECIMAL);
        }
        if(Util.isNotNull(pagamentoEstorno.getExercicio())){
            ps.setLong(17, pagamentoEstorno.getExercicio().getId());
        }else {
            ps.setNull(17, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(pagamentoEstorno.getUuid())){
            ps.setString(18, pagamentoEstorno.getUuid());
        }else {
            ps.setNull(18, Types.VARCHAR);
        }
        if(Util.isNotNull(pagamentoEstorno.getIdentificador())){
            ps.setLong(19, pagamentoEstorno.getId());
        }else {
            ps.setNull(19, Types.LONGNVARCHAR);
        }
        if (Util.isNotNull(idRev)) {
            ps.setLong(20, idRev);
            ps.setInt(21, typeRev);
            ps.setLong(22, id);
        }else {
            ps.setLong(20, id);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
