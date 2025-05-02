package br.com.webpublico.negocios.tributario.dao;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.tributario.auxiliares.AtributoGenerico;
import br.com.webpublico.negocios.tributario.rowmapper.*;
import br.com.webpublico.negocios.tributario.setter.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Repository
public class JdbcCalculoIptuDAO extends JdbcDaoSupport implements Serializable {

    @Autowired
    private SingletonGeradorId geradorDeIds;

    @Autowired
    public JdbcCalculoIptuDAO(DataSource dataSource) {
        setDataSource(dataSource);
    }

    public CadastroImobiliario cadastroPorId(Long id) {
        String sql = "SELECT BCI.*, PROPRIEDADE.PESSOA_ID AS PESSOA_ID FROM CADASTROIMOBILIARIO BCI " +
            "  LEFT JOIN PROPRIEDADE ON PROPRIEDADE.IMOVEL_ID = BCI.ID " +
            "  WHERE PROPRIEDADE.ID = (SELECT MAX(PROP.ID) FROM PROPRIEDADE PROP WHERE PROP.IMOVEL_ID = BCI.ID and PROP.FINALVIGENCIA IS NULL) " +
            "  AND BCI.ID = ?";
        CadastroImobiliario cadastro = (CadastroImobiliario) getJdbcTemplate().queryForObject(
            sql, new Object[]{id}, new CadastroImobiliarioRowMapper());
        return cadastro;
    }

    public List<CadastroImobiliario> cadastroPorInicioFim(String inicio, String fim) {
        String sql = "SELECT BCI.*, PROPRIEDADE.PESSOA_ID AS PESSOA_ID FROM CADASTROIMOBILIARIO BCI " +
            "  LEFT JOIN PROPRIEDADE ON PROPRIEDADE.IMOVEL_ID = BCI.ID " +
            "  WHERE PROPRIEDADE.ID = (SELECT MAX(PROP.ID) FROM PROPRIEDADE PROP WHERE PROP.IMOVEL_ID = BCI.ID and PROP.FINALVIGENCIA IS NULL) " +
            "  AND BCI.INSCRICAOCADASTRAL >= ? AND BCI.INSCRICAOCADASTRAL <= ?" +
            "  AND coalesce(BCI.ATIVO,0)  = 1 ";
        List<CadastroImobiliario> cadastros = getJdbcTemplate().query(
            sql, new Object[]{inicio, fim}, new CadastroImobiliarioRowMapper());
        return cadastros;
    }

    public List<CadastroImobiliario> buscarCadastrosPorIncricaoSetorQuadraLote(String inscricaoInicial, String inscricaoFinal,
                                                                               Setor setorInicial, Setor setorFinal,
                                                                               Quadra quadraInicial, Quadra quadraFinal,
                                                                               Lote loteInicial, Lote loteFinal) {
        String complemento = "";
        if (inscricaoInicial != null && !inscricaoInicial.isEmpty()) {
            complemento += " and bci.inscricaocadastral >= '" + inscricaoInicial.trim() + "' ";
        }
        if (inscricaoFinal != null && !inscricaoFinal.isEmpty()) {
            complemento += " and bci.inscricaocadastral <= '" + inscricaoFinal.trim() + "' ";
        }
        if (setorInicial != null) {
            complemento += " and s.codigo >= '" + setorInicial.getCodigo() + "' ";
        }
        if (setorFinal != null) {
            complemento += " and s.codigo <= '" + setorFinal.getCodigo() + "' ";
        }
        if (quadraInicial != null) {
            complemento += " and q.codigo >= '" + quadraInicial.getCodigo() + "' ";
        }
        if (quadraFinal != null) {
            complemento += " and q.codigo <= '" + quadraFinal.getCodigo() + "' ";
        }
        if (loteInicial != null) {
            complemento += " and l.codigolote >= '" + loteInicial.getCodigoLote() + "' ";
        }
        if (loteFinal != null) {
            complemento += " and l.codigolote <= '" + loteFinal.getCodigoLote() + "' ";
        }
        String sql = " select bci.*, p.pessoa_id " +
            " from cadastroimobiliario bci " +
            "         inner join lote l on bci.lote_id = l.id " +
            "         inner join setor s on l.setor_id = s.id " +
            "         inner join quadra q on l.quadra_id = q.id " +
            "         left join propriedade p on p.imovel_id = bci.id " +
            " where p.id = (select max(prop.id) from propriedade prop where prop.imovel_id = bci.id and prop.finalvigencia is null) " +
            complemento;
        return getJdbcTemplate().query(sql, new CadastroImobiliarioRowMapper());
    }

    public IsencaoCadastroImobiliario isencaoPorCadastroExercicio(Long idCadastro, Exercicio exercicio) {
        try {
            String sql = "SELECT isencao.* FROM IsencaoCadastroImobiliario isencao " +
                "inner join ProcessoIsencaoIptu proce on proce.ID = isencao.processoIsencaoIptu_id " +
                " where trunc(sysdate) BETWEEN trunc(isencao.inicioVigencia) and trunc(coalesce(isencao.finalVigencia, sysdate)) " +
                " and proce.situacao = 'DEFERIDO' " +
                " and isencao.situacao = 'ATIVO' " +
                " and isencao.cadastroimobiliario_id = ? " +
                " and proce.exercicioReferencia_id = ? " +
                " order by isencao.id desc " +
                " fetch first 1 row only ";

            return (IsencaoCadastroImobiliario) getJdbcTemplate().queryForObject(
                sql, new Object[]{idCadastro, exercicio.getId()}, new IsencaoRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Construcao> construcaoPorIdCadastro(Long id) {
        String sql = "SELECT coalesce((select sum(c.areaconstruida) from construcao c " +
            " where c.imovel_id = bci.id and coalesce(c.cancelada,0) != 1),0)" +
            "  as areatotal " +
            "  , construcao.* " +
            " FROM CONSTRUCAO " +
            " inner join cadastroimobiliario bci on bci.id = construcao.imovel_id " +
            " where construcao.englobado_id is null " +
            " and construcao.IMOVEL_ID = ?" +
            " and coalesce(construcao.cancelada,0) != 1 order by construcao.codigo";
        List<Construcao> construcoes = getJdbcTemplate().query(sql, new Object[]{id},
            new ConstrucaoRowMapper());
        return construcoes;
    }

    public Lote lotePorId(Long id) {
        try {
            String sql = "select lote.*, quadra.codigo as quadra, setor.codigo as setor, " +
                " zonaFiscal.id as idZonaFiscal, zonaFiscal.INDICE, zonaFiscal.VALORUNITARIOTERRENO " +
                " from lote " +
                "inner join quadra on quadra.id = lote.quadra_id " +
                "inner join setor on setor.id = lote.setor_id " +
                "left join zonaFiscal on zonaFiscal.id = lote.zonaFiscal_id " +
                "where lote.id = ?";
            Lote lote = (Lote) getJdbcTemplate().queryForObject(
                sql, new Object[]{id}, new LoteRowMapper());
            return lote;
        } catch (Exception e) {
//            e.printStackTrace();
            throw new ExcecaoNegocioGenerica("Sem Lote");
        }
    }

    public EventoCalculo eventoPorIdentificacao(String id) {
        try {
            String sql = "SELECT E.*, DESCONTO.IDENTIFICACAO AS DESCONTO FROM EVENTOCALCULO E " +
                "  LEFT JOIN EVENTOCALCULO DESCONTO ON DESCONTO.ID = E.DESCONTOSOBRE_ID " +
                " WHERE E.IDENTIFICACAO = ? AND trunc(e.iniciovigencia) <= trunc(sysdate) " +
                " and (e.finalvigencia is null or trunc(e.finalvigencia) >= trunc(sysdate)) ";
            EventoCalculo evento = (EventoCalculo) getJdbcTemplate().queryForObject(
                sql, new Object[]{id}, new EventoRowMapper());
            return evento;
        } catch (Exception e) {
//            //System.out.println("Não achou " + id);
            return null;
        }
    }

    public List<EventoCalculo> eventosVigentes() {
        String sql = "SELECT E.*, DESCONTO.IDENTIFICACAO AS DESCONTO FROM EVENTOCALCULO E " +
            "  LEFT JOIN EVENTOCALCULO DESCONTO ON DESCONTO.ID = E.DESCONTOSOBRE_ID " +
            " WHERE trunc(e.iniciovigencia) <= trunc(sysdate) and (e.finalvigencia is null or trunc(e.finalvigencia) >= trunc(sysdate)) ";
        return (List<EventoCalculo>) getJdbcTemplate().query(
            sql, new EventoRowMapper());
    }

    public List<AtributoGenerico> atributosConstrucao(Long id) {
        String sql = getQueryAtributos("construcao_valoratributo", "construcao_id");
        List<AtributoGenerico> atrs = getJdbcTemplate().query(sql, new Object[]{id},
            new AtributoGenericoRowMapper());
        return atrs;
    }

    public List<AtributoGenerico> atributosImovel(Long id) {
        String sql = getQueryAtributos("CI_ValorAtributos", "cadastroimobiliario_id");
        List<AtributoGenerico> atrs = getJdbcTemplate().query(sql, new Object[]{id},
            new AtributoGenericoRowMapper());
        return atrs;
    }

    public List<AtributoGenerico> atributosLote(Long id) {
        String sql = getQueryAtributos("LOTE_VALORATRIBUTO", "lote_id");
        List<AtributoGenerico> atrs = getJdbcTemplate().query(sql, new Object[]{id},
            new AtributoGenericoRowMapper());
        return atrs;
    }

    public Double areaConstruidaLote(Integer lote, Integer quadra, Integer setor) {
        String sql = "select coalesce(sum(construcao.areaconstruida),0) from lote " +
            "inner join quadra on quadra.id = lote.quadra_id " +
            "inner join setor on setor.id = lote.setor_id " +
            "inner join cadastroimobiliario bci on bci.lote_id = lote.id " +
            "inner join construcao on construcao.imovel_id = bci.id " +
            "where to_number(setor.codigo) =? and to_number(quadra.codigo) =? " +
            "  and to_number(lote.codigolote) = ? " +
            "  and coalesce(construcao.cancelada,0) <> 1 " +
            "  and coalesce(bci.ativo,0) = 1 ";
        try {
            return getJdbcTemplate().queryForObject(sql, Double.class, setor, quadra, lote);
        } catch (EmptyResultDataAccessException e) {
            return 0.0;
        }
    }

    public Double ufmVigente(Integer ano) {
        String sql = "select m.valor from Moeda m " +
            " inner join indiceeconomico i on i.id = m.indiceEconomico_id " +
            " where i.descricao = 'UFM' " +
            " and m.ano = ? " +
            " and m.mes = ? " +
            " and rownum = 1";
        try {
            return getJdbcTemplate().queryForObject(sql, Double.class, ano, 1);
        } catch (EmptyResultDataAccessException e) {
            return 0.0;
        }
    }

    public Double valorFaceDoLote(Lote lote, Exercicio ex) {
        StringBuilder sql = new StringBuilder("");
        sql.append(" select fv.valor from face ");
        sql.append(" inner join testada on testada.face_id = face.id and testada.principal = 1 ");
        sql.append(" inner join lote on lote.id = testada.lote_id ");
        sql.append(" inner join facevalor fv on fv.face_id = face.id ");
        sql.append(" inner join quadra quadra_lote on quadra_lote.id = lote.quadra_id ");
        sql.append(" inner join quadra quadra_face on quadra_face.id = fv.quadra_id ");
        sql.append(" inner join exercicio ex on ex.id = fv.exercicio_id");
        sql.append(" where lote.id = ? and ex.ano = ? and quadra_face.codigo = quadra_lote.codigo and rownum = 1");
        try {
            return getJdbcTemplate().queryForObject(sql.toString(), Double.class, lote.getId(), ex.getAno());
        } catch (EmptyResultDataAccessException e) {
            return 0.0;
        }
    }

    public boolean faceContemServico(Lote lote, ServicoUrbano servico) {
        String sql = "select count(fs.id) "
            + " from faceservico fs " +
            "inner join face on face.id = fs.face_id " +
            "inner join testada on testada.face_id = face.id " +
            "inner join lote on lote.id = testada.lote_id " +
            "where testada.principal = 1 " +
            "and lote.id = ? " +
            "and fs.servicourbano_id = ?";
        Integer integer = getJdbcTemplate().queryForObject(sql, Integer.class, lote.getId(), servico.getId());
        return integer > 0;
    }

    public List<ServicoUrbano> todosServicos() {
        String sql = "select * from servicourbano";
        return getJdbcTemplate().query(sql, new ServicoRowMapper());
    }

    public List<Pontuacao> todasPontuacoes(Integer ano) {
        String sql = "select * from pontuacao " +
            "inner join exercicio on pontuacao.exercicio_id = exercicio.id " +
            "where exercicio.ano = ?";
        List<Pontuacao> pontuacoes = getJdbcTemplate().query(sql, new Object[]{ano},
            new PontuacaoRowMapper());
        return pontuacoes;
    }

    public Double pontos(Pontuacao pontuacao, Construcao construcao) {
        String sql = "select item.pontos from itempontuacao item " +
            " where item.pontuacao_id = ? " +
            "   and ((select va.valordiscreto_id from construcao c " +
            "                                    inner join construcao_valoratributo cva on cva.construcao_id = c.id " +
            "                                    inner join valoratributo va on va.id = cva.atributos_id " +
            "                                    where c.id = ? " +
            "                                    and cva.atributos_key in (select pa.atributos_id from pontuacao_atributo pa " +
            "                                                               where pa.pontuacao_id = item.pontuacao_id)) in " +
            "   (select ipv.valores_id from ipont_vpos ipv where ipv.itempontuacao_id = item.id))";
        try {
            return getJdbcTemplate().queryForObject(sql, Double.class, pontuacao.getId(), construcao.getId());
        } catch (EmptyResultDataAccessException e) {
            return 0.0;
        }
    }

    public Long quantidadeCalculosEfetivados(Long idCadastro, Integer ano) {
        String sql = "select count(1) from calculoiptu iptu " +
            "inner join cadastroimobiliario ci on ci.id = iptu.cadastroimobiliario_id " +
            "inner join valordivida vd on vd.calculo_id = iptu.id " +
            "inner join exercicio ex on ex.id = vd.exercicio_id " +
            "where ci.id = ? and ex.ano = ?";
        try {
            return getJdbcTemplate().queryForObject(sql, Long.class, idCadastro, ano);
        } catch (EmptyResultDataAccessException e) {
            return 0L;
        }
    }

    public Double pontos(Pontuacao pontuacao, Lote lote) {
        String sql = "select item.pontos from itempontuacao item " +
            " where item.pontuacao_id = ? " +
            "   and ((select va.valordiscreto_id from lote l " +
            "                                    inner join lote_valoratributo lva on lva.lote_id = l.id " +
            "                                    inner join valoratributo va on va.id = lva.atributos_id " +
            "                                    where l.id = ? " +
            "                                    and lva.atributos_key in (select pa.atributos_id from pontuacao_atributo pa " +
            "                                                               where pa.pontuacao_id = item.pontuacao_id)) in " +
            "   (select ipv.valores_id from ipont_vpos ipv where ipv.itempontuacao_id = item.id))";
        try {
            return getJdbcTemplate().queryForObject(sql, Double.class, pontuacao.getId(), lote.getId());
        } catch (EmptyResultDataAccessException e) {
            return 0.0;
        }
    }

    public Double pontos(Pontuacao pontuacao, CadastroImobiliario cadastro) {
        String sql = " select item.pontos from itempontuacao item " +
            " where item.pontuacao_id = ? " +
            "   and (select va.valordiscreto_id from cadastroimobiliario c " +
            "                                   inner join ci_valoratributos cva on cva.cadastroimobiliario_id = c.id " +
            "                                   inner join valoratributo va on va.id = cva.atributos_id " +
            "                                   where c.id = ? " +
            "                                   and cva.atributos_key in (select pa.atributos_id from pontuacao_atributo pa " +
            "                                                              where pa.pontuacao_id = item.pontuacao_id)) in " +
            "   (select ipv.valores_id from ipont_vpos ipv where ipv.itempontuacao_id = item.id) ";
        try {
            return getJdbcTemplate().queryForObject(sql, Double.class, pontuacao.getId(), cadastro.getId());
        } catch (EmptyResultDataAccessException e) {
            return 0.0;
        }
    }

    public String getQueryAtributos(String from, String where) {
        return new StringBuilder("select atributo.identificacao, vt.valordata, vt.valordecimal, vt.valorinteiro, " +
            " vt.valorstring, vp.codigo valorpossivel, vp.fator, vp.id, coalesce(atributo.ativo, 1) as ativo ")
            .append(" from ").append(from).append(" atr ")
            .append(" inner join atributo on atributo.id = atr.atributos_key ")
            .append(" inner join valoratributo vt on vt.id = atr.atributos_id ")
            .append(" left join valorpossivel vp on vp.id = vt.valordiscreto_id ")
            .append("WHERE atr.").append(where).append(" = ?").toString();
    }

    private List<BigDecimal> buscarProprietariosAtivos(CadastroImobiliario cadastroImobiliario) {
        String sql = "select prop.pessoa_id from Propriedade prop " +
            "where trunc(current_date) BETWEEN trunc(prop.INICIOVIGENCIA) and trunc(coalesce(prop.FINALVIGENCIA, current_date)) " +
            " and prop.imovel_id = ?";
        try {
            return getJdbcTemplate().queryForList(sql, BigDecimal.class, cadastroImobiliario.getId());
        } catch (EmptyResultDataAccessException e) {
            return Lists.newArrayList();
        }
    }

    public void persisteIsencaoIPTU(List<IsencaoCadastroImobiliario> isencoes) {
        String sql = "INSERT INTO ISENCAOCADASTROIMOBILIARIO " +
            "(ID, PROCESSOISENCAOIPTU_ID, INICIOVIGENCIA,  " +
            "FINALVIGENCIA, LANCAIMPOSTO, LANCATAXA, SEQUENCIA,  " +
            "TIPOLANCAMENTOISENCAO, CADASTROIMOBILIARIO_ID, SITUACAO, PERCENTUAL) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new IsencaoCalculoIPTUSetter(isencoes, geradorDeIds));
    }

    public void encerraVigenciaIsencaoIPTU(Long id, Date dataEncerramento) {
        String sql = "UPDATE ISENCAOCADASTROIMOBILIARIO SET FINALVIGENCIA = ? " +
            " WHERE ID = ? ";
        getJdbcTemplate().update(sql, dataEncerramento, id);
    }

    public void alterarSituacaoProcessoIsencao(Long idProcesso, ProcessoIsencaoIPTU.Situacao situacao) {
        String sql = "UPDATE ProcessoIsencaoIPTU SET SITUACAO = ? WHERE ID = ? ";
        getJdbcTemplate().update(sql, situacao.name(), idProcesso);
    }

    public void alterarSituacaoIsencao(Long idProcesso, IsencaoCadastroImobiliario.Situacao situacao) {
        String sql = "UPDATE IsencaoCadastroImobiliario SET SITUACAO = ? WHERE processoIsencaoIPTU_ID = ? AND SITUACAO <> ? ";
        getJdbcTemplate().update(sql, situacao.name(), idProcesso, IsencaoCadastroImobiliario.Situacao.CANCELADO.name());
    }

    public void encerrarVigenciaAndMudaSituacaoParaCanceladoIsencaoIPTU(Long id, Date dataEncerramento) {
        String sql = "UPDATE ISENCAOCADASTROIMOBILIARIO SET " +
            " FINALVIGENCIA = ?, SITUACAO = 'CANCELADO' " +
            " WHERE ID = ? ";
        getJdbcTemplate().update(sql, dataEncerramento, id);
    }

    public void persisteEventosDoBci(List<CadastroImobiliario> cadastros) {
        for (CadastroImobiliario cadastro : cadastros) {
            String delete = "DELETE FROM EVENTOCALCULOBCI WHERE CADASTROIMOBILIARIO_ID = ?";
            getJdbcTemplate().update(delete, cadastro.getId());
            String sql = "INSERT INTO EVENTOCALCULOBCI " +
                "(ID, CADASTROIMOBILIARIO_ID, EVENTOCALCULO_ID, VALOR) " +
                "VALUES (?,?,?,?)";
            getJdbcTemplate().batchUpdate(sql, new EventoCalculoBCISetter(cadastro.getEventosCalculoBCI(), geradorDeIds));
            for (Construcao construcao : cadastro.getConstrucoesAtivas()) {
                delete = "DELETE FROM EVENTOCALCULOCONSTRUCAO WHERE CONSTRUCAO_ID = ?";
                getJdbcTemplate().update(delete, construcao.getId());
            }
            sql = "INSERT INTO EVENTOCALCULOCONSTRUCAO " +
                "(ID, CONSTRUCAO_ID, EVENTOCALCULO_ID, VALOR) " +
                "VALUES (?,?,?,?)";
            getJdbcTemplate().batchUpdate(sql, new EventoCalculoConstrucaoSetter(cadastro.getEventosCalculoConstrucoes(), geradorDeIds));

        }
    }

    public void persisteTudo(List<CalculoIPTU> calculos) {
        try {
            persisteCalculos(calculos);
            for (CalculoIPTU calculo : calculos) {
                persisteItensDeCalculo(calculo);
                persistePessoasDoCalculo(calculo);
                persisteOcorrenciasDoCalculo(calculo);
                persisteMemoriasDoCalculo(calculo);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void persisteMemoriasDoCalculo(CalculoIPTU calculo) {
        String sql = "INSERT INTO MEMORIACALCULOIPTU " +
            "(ID, CALCULOIPTU_ID, EVENTO_ID, VALOR, CONSTRUCAO_ID) " +
            "VALUES (?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new MemoriaCalculoSetter(calculo.getMemorias(), geradorDeIds));
    }

    private void persisteOcorrenciasDoCalculo(CalculoIPTU calculo) {
        String sql = "INSERT INTO OCORRENCIA " +
            "(ID, CONTEUDO, DATAREGISTRO, NIVELOCORRENCIA, TIPOOCORRENCIA, DETALHESTECNICOS) " +
            "VALUES (?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new OcorrenciaSetter(calculo.getOcorrenciaCalculoIPTUs(), geradorDeIds));

        sql = "INSERT INTO OCORRENCIACALCULOIPTU " +
            "(ID, OCORRENCIA_ID, CADASTROIMOBILIARIO_ID, CONSTRUCAO_ID, CALCULOIPTU_ID) " +
            "VALUES (?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new OcorrenciaIPTUSetter(calculo.getOcorrenciaCalculoIPTUs(), geradorDeIds));
    }

    private void persistePessoasDoCalculo(CalculoIPTU calculo) {
        List<BigDecimal> idsPropriedades = buscarProprietariosAtivos(calculo.getCadastroImobiliario());
        for (BigDecimal id : idsPropriedades) {
            String sql = "INSERT INTO CALCULOPESSOA " +
                "(ID, CALCULO_ID, PESSOA_ID) " +
                "VALUES (?,?,?)";
            getJdbcTemplate().update(sql, new Object[]{geradorDeIds.getProximoId(),
                calculo.getId(),
                id.longValue()
            });
        }
    }

    private void persisteItensDeCalculo(CalculoIPTU calculo) {
        String sql = "INSERT INTO ITEMCALCULOIPTU " +
            "(ID, CALCULOIPTU_ID, EVENTO_ID, DATAREGISTRO, ISENTO, VALORREAL, VALOREFETIVO, CONSTRUCAO_ID, IMUNE) " +
            "VALUES (?,?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new ItemCalculoIPTUSetter(calculo.getItensCalculo(), geradorDeIds));
    }

    private void persisteCalculos(List<CalculoIPTU> calculos) {
        String sql = "INSERT INTO CALCULO " +
            "(ID, DATACALCULO, SIMULACAO, VALORREAL, VALOREFETIVO, ISENTO, CADASTRO_ID, SUBDIVIDA, TIPOCALCULO, CONSISTENTE, PROCESSOCALCULO_ID, REFERENCIA, ISENTAACRESCIMOS) " +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new CalculoSetter(calculos, geradorDeIds));

        sql = "INSERT INTO CALCULOIPTU " +
            "(ID, CONSTRUCAO_ID, PROCESSOCALCULOIPTU_ID, CADASTROIMOBILIARIO_ID, ISENCAOCADASTROIMOBILIARIO_ID) " +
            "VALUES (?,?,?,?, ?)";
        getJdbcTemplate().batchUpdate(sql, new CalculoIPTUSetter(calculos));


        sql = "INSERT INTO CARNEIPTU " +
            "(ID, CALCULO_ID, AREACONSTRUIDA, FATORCORRECAO, AREAEXCEDENTE, FATORPESO, " +
            " FRACAOIDEAL, UFMRB, VLRM2CONSTRUIDO, VLRM2EXCEDENTE, VLRM2TERRENO, VLRVENALEDIFICACAO, " +
            " VLRVENALEXCEDENTE, VLRVENALTERRENO, ALIQUOTA, CONSTRUCAO_ID)" +
            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        for (CalculoIPTU calculo : calculos) {
            getJdbcTemplate().batchUpdate(sql, new CarneIPTUSetter(calculo.getCarnes(), geradorDeIds));
        }
    }

    public ProcessoCalculoIPTU gerarProcessoCalculo(ProcessoCalculoIPTU processo) {
        String sql = "INSERT INTO PROCESSOCALCULO " +
            "(ID, EXERCICIO_ID, DIVIDA_ID, DATALANCAMENTO, DESCRICAO, USUARIOSISTEMA_ID, NUMEROPROTOCOLO, ANOPROTOCOLO) " +
            "VALUES (?,?,?,?,?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new ProcessoCalculoSetter(processo, geradorDeIds));

        sql = "INSERT INTO PROCESSOCALCULOIPTU " +
            "(ID, CONFIGURACAOEVENTOIPTU_ID, CADASTROINICAL, CADASTROFINAL) " +
            "VALUES (?,?,?,?)";
        getJdbcTemplate().batchUpdate(sql, new ProcessoCalculoIPTUSetter(processo));
        return processo;

    }
}
