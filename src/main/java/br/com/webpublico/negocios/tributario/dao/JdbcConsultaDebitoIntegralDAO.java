package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ResultadoValorDivida;
import br.com.webpublico.negocios.tributario.rowmapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.List;

@Repository("consultaDebitoIntegralDAO")
public class JdbcConsultaDebitoIntegralDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    public JdbcConsultaDebitoIntegralDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public ResultadoValorDivida recuperarResultadoValorDivida(Long idParcelaValorDivida) {
        String sql = " SELECT VD.ID ID_VALORDIVIDA, "
                + "        D.ID ID_DIVIDA, "
                + "        D.DESCRICAO DESCRICAO_DIVIDA,"
                + "        EX.ANO ANO_EXERCICIO,"
                + "        CLC.SUBDIVIDA SD,"
                + "        CASE "
                + "           WHEN CI.ID IS NOT NULL THEN 'CADASTRO IMOBILIÁRIO' "
                + "           WHEN CE.ID IS NOT NULL THEN 'CADASTRO ECONÔMICO' "
                + "           WHEN CR.ID IS NOT NULL THEN 'CADASTRO RURAL' "
                + "           ELSE 'CONTRIBUINTE GERAL' "
                + "        END TIPOCADASTRO,"
                + "        COALESCE(CI.INSCRICAOCADASTRAL,COALESCE(CE.INSCRICAOCADASTRAL, COALESCE(CR.NUMEROINCRA, COALESCE(PF.CPF, PJ.CNPJ)))) INSCRICAO, "
                + "        VD.VALOR, "
                + "        VD.EMISSAO EMISSAO,   "
                + "        CLC.ID CALCULO_ID,  "
                + "        CLC.TIPOCALCULO "
                + "    FROM VALORDIVIDA VD "
                + "   INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.VALORDIVIDA_ID = VD.ID"
                + "   INNER JOIN DIVIDA D ON VD.DIVIDA_ID = D.ID "
                + "   INNER JOIN EXERCICIO EX ON EX.ID = VD.EXERCICIO_ID"
                + "   INNER JOIN CALCULO CLC ON CLC.ID = VD.CALCULO_ID "
                + "   INNER JOIN CALCULOPESSOA CLC_P ON CLC_P.ID = (SELECT MAX(ID) FROM CALCULOPESSOA S_CLC_P WHERE S_CLC_P.CALCULO_ID = CLC.ID) "
                + "   LEFT JOIN PESSOAFISICA PF ON PF.ID = CLC_P.PESSOA_ID "
                + "   LEFT JOIN PESSOAJURIDICA PJ ON PJ.ID = CLC_P.PESSOA_ID "
                + "   LEFT JOIN CADASTROIMOBILIARIO CI ON CLC.CADASTRO_ID = CI.ID "
                + "   LEFT JOIN CADASTROECONOMICO CE ON CLC.CADASTRO_ID = CE.ID "
                + "   LEFT JOIN CADASTRORURAL CR ON CLC.CADASTRO_ID = CR.ID "
                + " WHERE PVD.ID =  " + idParcelaValorDivida;
        List<ResultadoValorDivida> lista = getJdbcTemplate().query(sql, new Object[]{}, new ValorDividaRowMapper());
        return lista.get(0);
    }

    public List<Pessoa> listarPessoasDoCadastroDoValorDivida(Long idParcelaValorDivida) {
        String sql = " SELECT P.ID, "
                + "        PF.NOME, "
                + "        PF.CPF, "
                + "        PJ.RAZAOSOCIAL, "
                + "        PJ.NOMEFANTASIA, "
                + "        PJ.CNPJ, "
                + "        CASE WHEN PF.ID IS NOT NULL THEN 'F' ELSE 'J' END TIPOPESSOA "
                + "    FROM ("
                + " SELECT PVD.ID PARCELAVALORDIVIDA_ID, VD.ID VALORDIVIDA_ID, CE.ID CADASTRO_ID, CE.PESSOA_ID "
                + "    FROM CADASTROECONOMICO CE"
                + "   INNER JOIN CALCULO C ON C.CADASTRO_ID = CE.ID "
                + "   INNER JOIN VALORDIVIDA VD ON VD.CALCULO_ID = C.ID "
                + "   INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.VALORDIVIDA_ID = VD.ID"
                + " UNION "
                + " SELECT PVD.ID PARCELAVALORDIVIDA_ID, VD.ID VALORDIVIDA_ID, CR.ID CADASTRO_ID, CR_PROP.PESSOA_ID "
                + "    FROM CADASTRORURAL CR "
                + "   INNER JOIN PROPRIEDADE CR_PROP ON CR_PROP.IMOVEL_ID = CR.ID "
                + "   INNER JOIN CALCULO C ON C.CADASTRO_ID = CR.ID "
                + "   INNER JOIN VALORDIVIDA VD ON VD.CALCULO_ID = C.ID "
                + "   INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.VALORDIVIDA_ID = VD.ID"
                + " WHERE CR_PROP.FINALVIGENCIA IS NULL OR trunc(CR_PROP.FINALVIGENCIA) <= trunc(SYSDATE) "
                + " UNION "
                + " SELECT PVD.ID PARCELAVALORDIVIDA_ID, VD.ID VALORDIVIDA_ID, CI.ID CADASTRO_ID, CI_PROP.PESSOA_ID "
                + "    FROM CADASTROIMOBILIARIO CI "
                + "   INNER JOIN PROPRIEDADE CI_PROP ON CI_PROP.IMOVEL_ID = CI.ID "
                + "   INNER JOIN CALCULO C ON C.CADASTRO_ID = CI.ID "
                + "   INNER JOIN VALORDIVIDA VD ON VD.CALCULO_ID = C.ID "
                + "   INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.VALORDIVIDA_ID = VD.ID"
                + " WHERE CI_PROP.FINALVIGENCIA IS NULL OR trunc(CI_PROP.FINALVIGENCIA) <= trunc(SYSDATE)) CADASTROPESSOA "
                + "  INNER JOIN PESSOA P ON CADASTROPESSOA.PESSOA_ID = P.ID "
                + "  LEFT JOIN PESSOAFISICA PF ON PF.ID = P.ID "
                + "  LEFT JOIN PESSOAJURIDICA PJ ON  PJ.ID = P.ID "
                + " WHERE CADASTROPESSOA.PARCELAVALORDIVIDA_ID = ? ";
        List<Pessoa> lista = getJdbcTemplate().query(sql, new Object[]{idParcelaValorDivida}, new PessoaRowMapper());
        return lista;
    }

    public CalculoIPTU buscarCalculoIPTU(Long idParcelaValorDivida) {
        String sql = " SELECT CALC.ID, CALC.CADASTRO_ID, CALC.CONSISTENTE, CALC.DATACALCULO, CALC.ISENTO, "
                + "       CALC.SIMULACAO, CALC.TIPOCALCULO, CALC.VALOREFETIVO, CALC.VALORREAL, CALC.SUBDIVIDA, "
            + "       CIPTU.CADASTROIMOBILIARIO_ID, CIPTU.CONSTRUCAO_ID, CIPTU.PROCESSOCALCULOIPTU_ID, "
            + "       CIPTU.ISENCAOCADASTROIMOBILIARIO_ID  "
                + "   FROM CALCULOIPTU CIPTU "
                + "  INNER JOIN CALCULO CALC ON CALC.ID = CIPTU.ID "
                + "  INNER JOIN VALORDIVIDA VD ON CIPTU.ID = VD.CALCULO_ID"
                + "  INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.VALORDIVIDA_ID = VD.ID"
                + " WHERE PVD.ID = ? ";
        List<CalculoIPTU> lista = getJdbcTemplate().query(sql, new Object[]{idParcelaValorDivida}, new CalculoIPTURowMapper());
        if (lista != null && lista.size() > 0) {
            return lista.get(0);
        }
        return null;
    }

    public List<Pessoa> listarPessoasDoCalculoDoValorDivida(Long idParcelaValorDivida) {
        String sql = " SELECT P.ID, "
                + "        PF.NOME, "
                + "        PF.CPF, "
                + "        PJ.RAZAOSOCIAL, "
                + "        PJ.NOMEFANTASIA, "
                + "        PJ.CNPJ, "
                + "        CASE WHEN PF.ID IS NOT NULL THEN 'F' ELSE 'J' END TIPOPESSOA "
                + "    FROM CALCULOPESSOA CP "
                + "   INNER JOIN VALORDIVIDA VD ON CP.CALCULO_ID = VD.CALCULO_ID "
                + "   INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.VALORDIVIDA_ID = VD.ID "
                + "   INNER JOIN PESSOA P ON CP.PESSOA_ID = P.ID "
                + "   LEFT JOIN PESSOAFISICA PF ON PF.ID = P.ID "
                + "   LEFT JOIN PESSOAJURIDICA PJ ON  PJ.ID = P.ID "
                + " WHERE PVD.ID = ? ";
        List<Pessoa> lista = getJdbcTemplate().query(sql, new Object[]{idParcelaValorDivida}, new PessoaRowMapper());
        return lista;
    }

    public List<ParcelaValorDivida> listaParcelaValorDivida(Long idParcelaValorDivida) {
        String sql = "SELECT PVD.ID,"
                + "       PVD.SEQUENCIAPARCELA,"
                + "       PVD.VENCIMENTO,"
                + "       PVD.VALOR,"
                + "       OPC.DESCRICAO AS OPCAOPAGAMENTO_DESCRICAO,"
                + "       PVD.DIVIDAATIVA,"
                + "       PVD.DIVIDAATIVAAJUIZADA,"
                + "       OPC.ID AS OPCAOPAGAMENTO_ID"
                + " FROM PARCELAVALORDIVIDA PVD"
                + " INNER JOIN OPCAOPAGAMENTO OPC ON PVD.OPCAOPAGAMENTO_ID = OPC.ID"
                + " WHERE PVD.ID = ? "
                + " ORDER BY OPC.DESCRICAO, CAST(regexp_replace(PVD.SEQUENCIAPARCELA,'[^0-9]') AS NUMERIC(19)) ";

        List<ParcelaValorDivida> lista = getJdbcTemplate().query(
                sql, new Object[]{idParcelaValorDivida}, new ParcelaValorDividaRowMapper());
        return lista;
    }

    public List<SituacaoParcelaValorDivida> listaSituacaoParcela(Long idParcelaValorDivida) {
        String sql = "SELECT"
                + "  SPC.ID,"
                + "  SPC.DATALANCAMENTO,"
                + "  SPC.SALDO,"
                + "  SPC.SITUACAOPARCELA"
                + " FROM SITUACAOPARCELAVALORDIVIDA SPC"
                + " WHERE SPC.PARCELA_ID = ?"
                + " ORDER BY SPC.DATALANCAMENTO";
        List<SituacaoParcelaValorDivida> lista = getJdbcTemplate().query(
                sql, new Object[]{idParcelaValorDivida}, new SituacaoParcelaValorDividaRowMapper());
        return lista;
    }

    public List<ItemParcelaValorDivida> listaItemParcelaValorDividas(Long idParcelaValorDivida) {
        String sql = "SELECT"
                + "   ITEM.ID,"
                + "   TB.DESCRICAO AS TRIBUTO_DESCRICAO,"
                + "   ITEM.VALOR,"
                + "   TB.ID AS TRIBUTO_ID, "
                + "   TB.TIPOTRIBUTO"
                + " FROM ITEMPARCELAVALORDIVIDA ITEM"
                + " INNER JOIN ITEMVALORDIVIDA ITEM_VD ON ITEM_VD.ID = ITEM.ITEMVALORDIVIDA_ID"
                + " INNER JOIN TRIBUTO TB ON TB.ID = ITEM_VD.TRIBUTO_ID"
                + " WHERE ITEM.PARCELAVALORDIVIDA_ID = ?";

        List<ItemParcelaValorDivida> lista = getJdbcTemplate().query(
                sql, new Object[]{idParcelaValorDivida}, new ItemParcelaValorDividaRoWMapper(false));
        return lista;
    }

    public List<DescontoItemParcela> listaDescontosParcelaVD(Long idParcelaValorDivida) {
        String sql = " SELECT D_PC.ID, "
                + "      D_PC.DESCONTO, "
                + "      D_PC.INICIO, "
                + "      D_PC.FIM, "
                + "      D_PC.MOTIVO, "
                + "      D_PC.ORIGEM, "
                + "      D_PC.TIPO "
                + "  FROM DESCONTOITEMPARCELA D_PC "
                + " INNER JOIN ITEMPARCELAVALORDIVIDA I_PC ON D_PC.ITEMPARCELAVALORDIVIDA_ID = I_PC.ID "
                + " WHERE I_PC.PARCELAVALORDIVIDA_ID = ? ";

        List<DescontoItemParcela> lista = getJdbcTemplate().query(
                sql, new Object[]{idParcelaValorDivida}, new DescontoItemParcelaRowMapper());
        return lista;
    }

    public List<ItemValorDivida> listaItemValorDivida(Long idParcelaValorDivida) {
        String sql = " SELECT IVD.ID, "
                + "       IVD.VALOR, "
                + "       IVD.ISENTO, "
                + "       TRIB.ID TRIBUTO_ID, "
                + "       TRIB.DESCRICAO TRIBUTO_DESCRICAO "
                + "   FROM ITEMVALORDIVIDA IVD "
                + "  INNER JOIN TRIBUTO TRIB ON IVD.TRIBUTO_ID = TRIB.ID "
                + "  INNER JOIN PARCELAVALORDIVIDA PVD ON PVD.VALORDIVIDA_ID = IVD.VALORDIVIDA_ID "
                + " WHERE PVD.ID = ? ";
        List<ItemValorDivida> lista = getJdbcTemplate().query(
                sql, new Object[]{idParcelaValorDivida}, new ItemValorDividaRowMapper());
        return lista;
    }

    public void atualizaDams(Integer ano){

    }

    public List<DAM> listaDams(Integer ano) {
        String sql = " SELECT DAM.ID, "
                + "       DAM.NUMERODAM, "
                + "       DAM.CODIGOBARRAS, "
                + "       DAM.EMISSAO, "
                + "       DAM.VENCIMENTO, "
                + "       DAM.SITUACAO, "
                + "       DAM.TIPO, "
                + "       DAM.VALORORIGINAL, "
                + "       DAM.DESCONTO, "
                + "       DAM.MULTA, "
                + "       DAM.JUROS, "
                + "       DAM.CORRECAOMONETARIA "
                + "   FROM DAM DAM WHERE DAM.NUMERODAM IN ('3737918/2011', " +
                "'482560/2012', " +
                "'505587/2012', " +
                "'505587/2012'," +
                "'500437/2012'," +
                "'484628/2012'," +
                "'506290/2012'," +
                "'3737900/2011'," +
                "'3737934/2011'," +
                "'503738/2012'," +
                "'3737977/2011'," +
                "'3737675/2011'," +
                "'208850/2012'," +
                "'4661850/2011'" +
                ")";
        List<DAM> lista = getJdbcTemplate().query(
                sql,  new DAMRowMapper());
        return lista;
    }

    public List<DAM> listaDamsValorDivida(Long idParcelaValorDivida) {
        String sql = " SELECT DAM.ID, "
                + "       DAM.NUMERODAM, "
                + "       DAM.CODIGOBARRAS, "
                + "       DAM.EMISSAO, "
                + "       DAM.VENCIMENTO, "
                + "       DAM.SITUACAO, "
                + "       DAM.TIPO, "
                + "       DAM.VALORORIGINAL, "
                + "       DAM.DESCONTO, "
                + "       DAM.MULTA, "
                + "       DAM.JUROS, "
                + "       DAM.CORRECAOMONETARIA "
                + "  FROM DAM DAM "
                + " WHERE EXISTS(SELECT ITEM.ID FROM ITEMDAM ITEM "
                + "               WHERE ITEM.DAM_ID = DAM.ID AND ITEM.PARCELA_ID = ?)";
        List<DAM> lista = getJdbcTemplate().query(
                sql, new Object[]{idParcelaValorDivida}, new DAMRowMapper());
        return lista;
    }

    public List<ItemDAM> listaItensDAM(Long idDAM) {
        String sql = " SELECT ITD.ID, "
                + "        ITD.PARCELA_ID, "
                + "        PVD.SEQUENCIAPARCELA PARCELA_SEQUENCIA, "
                + "        PVD.VENCIMENTO PARCELA_VENCIMENTO, "
                + "        PVD.VALOR PARCELA_VALOR, "
                + "        ITD.VALORORIGINALDEVIDO, "
                + "        ITD.DESCONTO, "
                + "        ITD.MULTA, "
                + "        ITD.JUROS, "
                + "        ITD.CORRECAOMONETARIA "
                + "    FROM ITEMDAM ITD "
                + "   INNER JOIN PARCELAVALORDIVIDA PVD ON ITD.PARCELA_ID = PVD.ID "
                + " WHERE ITD.DAM_ID = ? ";
        List<ItemDAM> lista = getJdbcTemplate().query(
                sql, new Object[]{idDAM}, new ItemDAMRowMapper());
        return lista;
    }

    public void recuperarMemoriaCalculoIPTU(CalculoIPTU calculoIPTU) {
        String sql = " SELECT MEM.*, EVT.DESCRICAO EVT_DESCRICAO "
                + "   FROM MEMORIACALCULOIPTU MEM "
                + "  INNER JOIN EVENTOCALCULO EVT ON MEM.EVENTO_ID = EVT.ID "
                + " WHERE MEM.CALCULOIPTU_ID = ? ";
        List<MemoriaCaluloIPTU> lista = getJdbcTemplate().query(
                sql, new Object[]{calculoIPTU.getId()}, new MemoriaCalculoIPTURowMapper());
        for (MemoriaCaluloIPTU memoria : lista) {
            memoria.setCalculoIPTU(calculoIPTU);
        }
        calculoIPTU.setMemorias(lista);
    }

    public void recuperarProcessoCalculoIPTU(CalculoIPTU calculoIPTU) {
        String sql = " SELECT PC_IPTU.ID, PC_IPTU.CADASTROINICAL, PC_IPTU.CADASTROFINAL, "
                + "   PC_IPTU.CONFIGURACAOEVENTOIPTU_ID, PC_IPTU.DESCONTOVALOVENALCONSTRUCAO, "
                + "   PC_IPTU.DESCONTOVALOVENALTERRENO, PC_IPTU.LOG, PC_IPTU.REFERENCIAIMPOSTO, "
                + "   PC_IPTU.TIPOIMOVEL, PC_IPTU.UTILIZACAOIMOVEL, PC_IPTU.VALORMINIMOIMPOSTOPREDIAL, "
                + "   PC_IPTU.VALORMINIMOIMPOSTOTERRITORIAL, PC.DESCRICAO, CONFIG_IPTU.CODIGO CFG_CODIGO, "
                + "   CONFIG_IPTU.DESCRICAO CFG_DESCRICAO "
                + "   FROM PROCESSOCALCULOIPTU PC_IPTU "
                + "  INNER JOIN PROCESSOCALCULO PC ON PC_IPTU.ID = PC.ID "
                + "  INNER JOIN CONFIGURACAOEVENTOIPTU CONFIG_IPTU ON CONFIG_IPTU.ID = PC_IPTU.CONFIGURACAOEVENTOIPTU_ID "
                + "  INNER JOIN CALCULOIPTU CIPTU ON CIPTU.PROCESSOCALCULOIPTU_ID = PC_IPTU.ID "
                + " WHERE CIPTU.ID = ? ";
        List<ProcessoCalculoIPTU> lista = getJdbcTemplate().query(
                sql, new Object[]{calculoIPTU.getId()}, new ProcessoCalculoIPTURowMapper());
        ProcessoCalculoIPTU processoCalculoIPTU = lista.get(0);
        calculoIPTU.setProcessoCalculoIPTU(processoCalculoIPTU);
    }

    public String pessoasDoCalculoDoValorDivida(Long id) {
        String resultado = "";
        List<Pessoa> pessoas = listarPessoasDoCalculoDoValorDivida(id);
        for (Pessoa p : pessoas) {
            resultado += p.getNome() + " - " + (p.getCpf_Cnpj() == null ? "" : p.getCpf_Cnpj()) + "<br/>";
        }
        return resultado;
    }

}
