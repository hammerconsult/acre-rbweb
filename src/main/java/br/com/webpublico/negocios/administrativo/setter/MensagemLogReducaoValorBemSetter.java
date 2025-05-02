package br.com.webpublico.negocios.administrativo.setter;

import br.com.webpublico.entidades.MsgLogReducaoOuEstorno;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 27/10/14
 * Time: 14:13
 * To change this template use File | Settings | File Templates.
 */
public class MensagemLogReducaoValorBemSetter implements BatchPreparedStatementSetter {

    public static final String SQL_INSERT_MENSAGEM = "INSERT INTO MSGLOGREDUCAOOUESTORNO (ID, LOGREDUCAOOUESTORNO_ID, VALOR, BEM_ID, MENSAGEM) VALUES (?, ?, ?, ?, ?)";
    private final SingletonGeradorId geradorDeIds;
    private List<MsgLogReducaoOuEstorno> mensagens;

    public MensagemLogReducaoValorBemSetter(List<MsgLogReducaoOuEstorno> mensagens, SingletonGeradorId geradorDeIds) {
        this.mensagens = mensagens;
        this.geradorDeIds = geradorDeIds;
    }

    @Override
    public void setValues(PreparedStatement ps, int i) throws SQLException {
        MsgLogReducaoOuEstorno msg = mensagens.get(i);

        ps.setLong(1, geradorDeIds.getProximoId());
        ps.setLong(2, msg.getLogReducaoOuEstorno().getId());
        ps.setBigDecimal(3, msg.getValor());
        ps.setLong(4, msg.getBem().getId());
        ps.setString(5, msg.getMensagem());
    }

    @Override
    public int getBatchSize() {
        return mensagens.size();
    }
}
