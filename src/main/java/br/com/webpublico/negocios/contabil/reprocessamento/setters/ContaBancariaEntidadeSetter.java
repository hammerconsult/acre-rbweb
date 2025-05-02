package br.com.webpublico.negocios.contabil.reprocessamento.setters;

import br.com.webpublico.entidades.ContaBancariaEntidade;
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
public class ContaBancariaEntidadeSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_AUD = "insert into contabancariaentidade_aud (digitoverificador, numeroconta, situacao, agencia_id, entidade_id, " +
        "                                   codigodoconvenio, tipocontabancaria, observacao, nomeconta, " +
        "                                   dataabertura, dataencerramento, modalidadeconta, contaprincipalfp, " +
        "                                   parametrotransmissao, contabancaria, carteiracedente, numerocarteiracobranca, " +
        "                                   variacaocarteiracobranca, densidade, versaolayoutlotecreditosalario, " +
        "                                   indicativoformapagamentoserv, camaracompensacao, tipodecompromisso, " +
        "                                   codigodocompromisso, rev, revtype, id)  " +
        "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private final ContaBancariaEntidade contaBancariaEntidade;
    private final Long id;
    private Long idRev;
    private Integer typeRev;

    public ContaBancariaEntidadeSetter(ContaBancariaEntidade contaBancariaEntidade, Long id) {
        this.contaBancariaEntidade = contaBancariaEntidade;
        this.id = id;
    }

    public ContaBancariaEntidadeSetter(ContaBancariaEntidade contaBancariaEntidade, Long id, Long idRev, Integer typeRev) {
        this.contaBancariaEntidade = contaBancariaEntidade;
        this.id = id;
        this.idRev = idRev;
        this.typeRev = typeRev;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        ps.setString(1, contaBancariaEntidade.getDigitoVerificador());
        ps.setString(2, contaBancariaEntidade.getNumeroConta());
        ps.setString(3, contaBancariaEntidade.getSituacao().name());
        ps.setLong(4, contaBancariaEntidade.getAgencia().getId());
        if(Util.isNotNull(contaBancariaEntidade.getEntidade())){
            ps.setLong(5, contaBancariaEntidade.getEntidade().getId());
        }else {
            ps.setNull(5, Types.LONGNVARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getCodigoDoConvenio())){
            ps.setString(6,contaBancariaEntidade.getCodigoDoConvenio());
        }else{
            ps.setNull(6, Types.VARCHAR);
        }
        ps.setString(7, contaBancariaEntidade.getTipoContaBancaria().name());
        if(Util.isNotNull( contaBancariaEntidade.getObservacao())){
            ps.setString(8, contaBancariaEntidade.getObservacao());
        }else{
            ps.setNull(8, Types.VARCHAR);
        }
        ps.setString(9, contaBancariaEntidade.getNomeConta());
        if(Util.isNotNull(contaBancariaEntidade.getDataAbertura())){
            ps.setDate(10, new Date(contaBancariaEntidade.getDataAbertura().getTime()));
        }else{
            ps.setNull(10, Types.DATE);
        }
        if(Util.isNotNull(contaBancariaEntidade.getDataEncerramento())){
            ps.setDate(11, new Date(contaBancariaEntidade.getDataEncerramento().getTime()));
        }else{
            ps.setNull(11, Types.DATE);
        }
        ps.setString(12, contaBancariaEntidade.getModalidadeConta().name());
        ps.setBoolean(13, contaBancariaEntidade.getContaPrincipalFP());
        if(Util.isNotNull(contaBancariaEntidade.getParametroTransmissao())){
            ps.setString(14, contaBancariaEntidade.getParametroTransmissao());
        }else{
            ps.setNull(14, Types.VARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getContaBancaria())){
            ps.setString(15, contaBancariaEntidade.getContaBancaria());
        }else{
            ps.setNull(15, Types.VARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getCarteiraCedente())){
            ps.setString(16, contaBancariaEntidade.getCarteiraCedente());
        }else{
            ps.setNull(16, Types.VARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getNumeroCarteiraCobranca())){
            ps.setString(17, contaBancariaEntidade.getNumeroCarteiraCobranca());
        }else{
            ps.setNull(17, Types.VARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getVariacaoCarteiraCobranca())){
            ps.setString(18, contaBancariaEntidade.getVariacaoCarteiraCobranca());
        }else{
            ps.setNull(18, Types.VARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getDensidade())){
            ps.setString(19, contaBancariaEntidade.getDensidade());
        }else{
            ps.setNull(19, Types.VARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getVersaoLayoutLoteCreditoSalario())){
            ps.setString(20, contaBancariaEntidade.getVersaoLayoutLoteCreditoSalario());
        }else{
            ps.setNull(20, Types.VARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getIndicativoFormaPagamentoServ())){
            ps.setString(21, contaBancariaEntidade.getIndicativoFormaPagamentoServ());
        }else{
            ps.setNull(21, Types.VARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getCamaraCompensacao())){
            ps.setString(22, contaBancariaEntidade.getCamaraCompensacao());
        }else{
            ps.setNull(22, Types.VARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getTipoDeCompromisso())){
            ps.setString(23, contaBancariaEntidade.getTipoDeCompromisso());
        }else{
            ps.setNull(23, Types.VARCHAR);
        }
        if(Util.isNotNull(contaBancariaEntidade.getCodigoDoCompromisso())){
            ps.setString(24, contaBancariaEntidade.getCodigoDoCompromisso());
        }else{
            ps.setNull(24, Types.VARCHAR);
        }

        if(Util.isNotNull(idRev)){
            ps.setLong(25, idRev);
            ps.setInt(26, typeRev);
            ps.setLong(27, id);
        }else {
            ps.setLong(25, id);
        }
    }

    @Override
    public int getBatchSize() {
        return 1;
    }
}
