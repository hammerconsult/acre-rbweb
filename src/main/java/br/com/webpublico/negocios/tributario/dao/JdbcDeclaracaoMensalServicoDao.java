package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.negocios.tributario.setter.DeclaracaoMensalServicoSetter;
import br.com.webpublico.negocios.tributario.setter.DeclaracaoMensalServicoUpdateSetter;
import br.com.webpublico.negocios.tributario.setter.NotaDeclaradaSetter;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import br.com.webpublico.nfse.domain.DeclaracaoMensalServico;
import br.com.webpublico.nfse.domain.NotaDeclarada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

@Repository(value = "declaracaoMensalServicoDao")
public class JdbcDeclaracaoMensalServicoDao extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcDeclaracaoMensalServicoDao(DataSource dataSource) {
        setDataSource(dataSource);
    }


    public DeclaracaoMensalServico inserir(DeclaracaoMensalServico declaracao) {
        String sql = "INSERT INTO DECLARACAOMENSALSERVICO " +
            "(ID, CODIGO, TIPO, PRESTADOR_ID, PROCESSOCALCULO_ID, MES, EXERCICIO_ID, SITUACAO, QTDNOTAS, TOTALSERVICOS, TOTALISS, " +
            "ABERTURA, ENCERRAMENTO, TIPOMOVIMENTO, REFERENCIA, USUARIODECLARACAO, LANCADOPOR) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

        getJdbcTemplate().batchUpdate(sql, new DeclaracaoMensalServicoSetter(declaracao, geradorDeIds));
        salvar(declaracao.getNotas());
        return declaracao;
    }

    public DeclaracaoMensalServico update(DeclaracaoMensalServico declaracao) {
        String sql = " UPDATE DECLARACAOMENSALSERVICO " +
            " SET CODIGO = ?, TIPO = ?, PRESTADOR_ID = ?, PROCESSOCALCULO_ID = ?, MES = ?, EXERCICIO_ID = ?, SITUACAO = ?, QTDNOTAS = ?, " +
            " TOTALSERVICOS = ?, TOTALISS = ?, ABERTURA = ?, ENCERRAMENTO = ?, TIPOMOVIMENTO = ?, REFERENCIA = ? " +
            " WHERE ID = ? ";
        getJdbcTemplate().batchUpdate(sql, new DeclaracaoMensalServicoUpdateSetter(declaracao));
        return declaracao;
    }

    public void salvar(List<NotaDeclarada> notas) {
        String sql = "INSERT INTO NOTADECLARADA " +
            "(ID, DECLARACAOPRESTACAOSERVICO_ID, DECLARACAOMENSALSERVICO_ID) " +
            "VALUES (?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new NotaDeclaradaSetter(notas, geradorDeIds));


    }


}
