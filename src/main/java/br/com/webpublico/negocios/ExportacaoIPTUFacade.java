package br.com.webpublico.negocios;

import br.com.webpublico.controle.ExportacaoIPTUControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ExportacaoIPTU;
import br.com.webpublico.entidadesauxiliares.VOCadastroImobiliario;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.tributario.TipoEnderecoExportacaoIPTU;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcCalculoIptuDAO;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.web.context.ContextLoader;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ExportacaoIPTUFacade extends AbstractFacade<ProcessoExportacaoIPTU> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private String formatoData = "yyyyMMdd";
    private final int QUANTIDADE_CADASTROS = 10000;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private MoedaFacade moedaFacade;

    @Resource
    private SessionContext context;
    @Resource
    private UserTransaction userTransaction;
    @EJB
    private SetorFacade setorFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private QuadraFacade quadraFacade;
    @EJB
    private BairroFacade bairroFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DAMFacade damFacade;

    @EJB
    private ArquivoFacade arquivoFacade;
    private JdbcCalculoIptuDAO calculoDAO;

    public ExportacaoIPTUFacade() {
        super(ProcessoExportacaoIPTU.class);
        calculoDAO = (JdbcCalculoIptuDAO) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcCalculoIptuDAO");
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private ParametroExportacaoIPTU buscaParametroExportacaoIPTU() {
        String sql = "select * from ParametroExportacaoIPTU where tipoParametro = :tipo";
        Query q = em.createNativeQuery(sql, ParametroExportacaoIPTU.class);
        q.setParameter("tipo", ParametroExportacaoIPTU.TipoParametroExportacaoIPTU.IPTU.name());
        List<ParametroExportacaoIPTU> retorno = q.getResultList();
        if (!retorno.isEmpty()) {
            return retorno.get(0);
        }
        return null;
    }

    private List<Tributo> tributosUsadoNoIPTUDoExercicio(Exercicio exercicio) {
        StringBuilder sql = new StringBuilder("select distinct trib.* from memoriacalculoiptu mem ")
            .append("inner join eventocalculo even on even.id = mem.evento_id ")
            .append("inner join tributo trib on trib.id = even.tributo_id ")
            .append("inner join calculoIPTu calc on calc.id = mem.CALCULOIPTU_ID ")
            .append("inner join processoCalculo pro on pro.id = calc.processocalculoiptu_id ")
            .append("where pro.exercicio_id = :exercicio ")
            .append("and even.tributo_id is not null and rownum <= 9 ")
            .append("order by trib.codigo");
        Query q = em.createNativeQuery(sql.toString(), Tributo.class);
        q.setParameter("exercicio", exercicio.getId());
        return q.getResultList();
    }

    private List<CalculoExportacaoIPTU> listaCalculosIptu(ExportacaoIPTU filtros) {
        StringBuilder sql = new StringBuilder("select distinct calc.id, calc.dataCalculo, calc.valorReal, ")
            .append("ci.id as idCadastro, (select memoria.valor from memoriacalculoiptu memoria ")
            .append("inner join eventocalculo ev on ev.id = memoria.evento_id and ev.identificacao = 'valorVenalImovel' ")
            .append("where memoria.calculoiptu_id = calc.id) as valorvenal, ")
            .append("(SELECT sum(MEMOAUX.VALOR) ")
            .append("  FROM MEMORIACALCULOIPTU MEMOAUX ")
            .append("  INNER JOIN EVENTOCALCULO EVENTOAUX ")
            .append("  ON EVENTOAUX.ID = MEMOAUX.EVENTO_ID ")
            .append("  WHERE EVENTOAUX.IDENTIFICACAO = 'iptu' ")
            .append("  AND MEMOAUX.CALCULOIPTU_ID = CARNEIPTU.calculo_id ")
            .append("  and (memoaux.construcao_id = carneiptu.construcao_id or memoaux.construcao_id is null)) as iptu, ")
            .append(" CASE ")
            .append("    WHEN (SELECT sum(MEMOAUX.VALOR) ")
            .append("      FROM MEMORIACALCULOIPTU MEMOAUX ")
            .append("      INNER JOIN EVENTOCALCULO EVENTOAUX ")
            .append("      ON EVENTOAUX.ID = MEMOAUX.EVENTO_ID ")
            .append("      WHERE EVENTOAUX.IDENTIFICACAO = 'iluminacaoPublica' ")
            .append("      AND MEMOAUX.CALCULOIPTU_ID    = CARNEIPTU.calculo_id ")
            .append("      and (memoaux.construcao_id = carneiptu.construcao_id or memoaux.construcao_id is null)) <> 0 ")
            .append("    THEN ")
            .append("      (SELECT sum(MEMOAUX.VALOR) ")
            .append("      FROM MEMORIACALCULOIPTU MEMOAUX ")
            .append("      INNER JOIN EVENTOCALCULO EVENTOAUX ")
            .append("      ON EVENTOAUX.ID = MEMOAUX.EVENTO_ID ")
            .append("      WHERE EVENTOAUX.IDENTIFICACAO = 'iluminacaoPublica' ")
            .append("      AND MEMOAUX.CALCULOIPTU_ID    = CARNEIPTU.calculo_id ")
            .append("      and (memoaux.construcao_id = carneiptu.construcao_id or memoaux.construcao_id is null) ")
            .append("      ) ELSE ")
            .append("      (SELECT sum(MEMOAUX.VALOR) ")
            .append("      FROM MEMORIACALCULOIPTU MEMOAUX ")
            .append("      INNER JOIN EVENTOCALCULO EVENTOAUX ")
            .append("      ON EVENTOAUX.ID = MEMOAUX.EVENTO_ID ")
            .append("      WHERE EVENTOAUX.IDENTIFICACAO = 'txLixo' ")
            .append("      AND MEMOAUX.CALCULOIPTU_ID    = CARNEIPTU.calculo_id ")
            .append("      and (memoaux.construcao_id = carneiptu.construcao_id or memoaux.construcao_id is null) ")
            .append("      ) END AS TSU, ")
            .append("  CARNEIPTU.fracaoIdeal,")
            .append("  CARNEIPTU.fatorCorrecao, ")
            .append("  CARNEIPTU.vlrVenalTerreno, ")
            .append("  CARNEIPTU.areaConstruida, ")
            .append("  CARNEIPTU.vlrM2Construido, ")
            .append("  CARNEIPTU.vlrVenalEdificacao, ")
            .append("  CASE WHEN (select count(id) from construcao where imovel_id = ci.id and coalesce(cancelada, 0) = 0) > 0 ")
            .append("       THEN 'PREDIAL' ELSE 'TERRITORIAL' END AS tipoIptu, ")

            .append("  (select vp.VALOR ")
            .append(" from construcao_valoratributo civa ")
            .append(" inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append(" inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append(" inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append(" inner join construcao on construcao.id = civa.CONSTRUCAO_ID ")
            .append(" where construcao.id = CARNEIPTU.construcao_id ")
            .append(" and atributo.IDENTIFICACAO = 'utilizacao_imovel' and rownum =1) as utilizacao_imovel, ")

            .append("  (select vp.VALOR ")
            .append(" from lote_valoratributo civa ")
            .append(" inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append(" inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append(" inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append(" inner join lote on lote.id = civa.LOTE_ID ")
            .append(" inner join cadastroimobiliario cad on cad.LOTE_ID = lote.id ")
            .append(" where cad.id = ci.id ")
            .append(" and atributo.IDENTIFICACAO = 'topografia' and rownum =1) as topografia, ")

            .append(" (select vp.VALOR ")
            .append("  from lote_valoratributo civa ")
            .append("  inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append("  inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append("  inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append("  inner join lote on lote.id = civa.LOTE_ID ")
            .append("  inner join cadastroimobiliario cad on cad.LOTE_ID = lote.id ")
            .append("  where cad.id = ci.id ")
            .append("  and atributo.IDENTIFICACAO = 'situacao' and rownum =1) as situacao, ")

            .append("  (select replace(vp.VALOR, 'Padrão ', '') ")
            .append(" from construcao_valoratributo civa ")
            .append(" inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append(" inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append(" inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append(" inner join construcao on construcao.id = civa.CONSTRUCAO_ID ")
            .append(" where construcao.id = CARNEIPTU.construcao_id ")
            .append(" and atributo.IDENTIFICACAO = 'qualidade_construcao' and rownum =1) as qualidade_construcao, ")

            .append("  (select vp.VALOR from lote_valoratributo civa ")
            .append(" inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append(" inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append(" inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append(" inner join lote on lote.id = civa.lote_id ")
            .append(" inner join cadastroimobiliario cad on cad.lote_id = lote.id ")
            .append(" where cad.id = ci.id ")
            .append(" and atributo.IDENTIFICACAO = 'patrimonio' and rownum =1) as patrimonio,")

            .append(" (select case when su.identificacao = 'coleta_lixo_alternado' then 'ALTERNADO' ")
            .append("   ELSE case when su.identificacao = 'coleta_lixo_diario' then 'DIÁRIO' ELSE '' END END as lixo ")
            .append("  from servicourbano su ")
            .append("  inner join faceservico fs on fs.servicourbano_id = su.id ")
            .append("  inner join face face on face.id = fs.face_id ")
            .append("  inner join testada testada on testada.face_id = face.id ")
            .append("  inner join lote lote on lote.id = testada.lote_id ")
            .append("  inner join cadastroimobiliario on cadastroimobiliario.lote_id = lote.id ")
            .append("  where cadastroimobiliario.id = ci.id ")
            .append("  and su.identificacao like '%lixo%' and rownum = 1) as coleta_lixo, ")

            .append("  CARNEIPTU.vlrM2Terreno, ")
            .append("  CARNEIPTU.fatorPeso, ")
            .append("  CARNEIPTU.areaExcedente, ")
            .append("  CARNEIPTU.vlrVenalExcedente, ")
            .append("  CARNEIPTU.aliquota, ")

            .append("  (select vp.VALOR ")
            .append(" from construcao_valoratributo civa ")
            .append(" inner join valoratributo va on va.ID = civa.ATRIBUTOS_ID ")
            .append(" inner join valorpossivel vp on vp.id = va.VALORDISCRETO_ID ")
            .append(" inner join atributo on atributo.id = civa.ATRIBUTOS_KEY ")
            .append(" inner join construcao on construcao.id = civa.CONSTRUCAO_ID ")
            .append(" where construcao.id = CARNEIPTU.construcao_id ")
            .append(" and atributo.IDENTIFICACAO = 'tipo_imovel' and rownum =1) as tipo_imovel, ")

            .append("  coalesce(pf.nome, pj.razaosocial) as nomePessoa, ")
            .append("  ci.inscricaocadastral, ")
            .append("  case when pf.id is null then '2' else '1' end as TipoPessoa, ")
            .append("  coalesce(pf.cpf, pj.cnpj) as cpfCnpj, ");

        selecionarEnderecoQueVaiNaExportacao(sql, TipoEnderecoExportacaoIPTU.ENDERECO_IMOVEL, false);
        selecionarEnderecoQueVaiNaExportacao(sql, filtros.getTipoEndereco(), true);

        sql.append("  coalesce(pfComp.nome, pjComp.razaosocial) as nomeCompromissario, ")
            .append("  coalesce(pfComp.cpf, pjComp.cnpj) as cpfCnpjCompromissario, ")
            .append("  case when pfComp.id is null then '2' else '1' end as TipoPessoaCompromissario ")

            .append(" from calculoIPTU iptu ")
            .append("inner join calculo calc on calc.id = iptu.id ")
            .append("inner join cadastroimobiliario ci on ci.id = calc.cadastro_id ")
            .append("inner join processocalculo pro on pro.id = calc.processocalculo_id ")
            .append("inner join CARNEIPTU on CARNEIPTU.calculo_id = calc.id and (CARNEIPTU.construcao_id = (select max(construcao_id) from CARNEIPTU where CARNEIPTU.calculo_id = calc.id) OR CARNEIPTU.construcao_id IS NULL) ")
            .append("inner join vwconsultadedebitossemvalores vw on vw.calculo_id = calc.id ")
            .append("inner join propriedade on propriedade.imovel_id = ci.id ")
            .append("inner join pessoa pessoa on pessoa.id = propriedade.pessoa_id ")
            .append("left join pessoafisica pf ON pf.id = pessoa.id ")
            .append("left join pessoajuridica pj ON pj.id = pessoa.id ")
            .append("left join compromissario compromissario on compromissario.cadastroimobiliario_id = ci.id ")
            .append("left join pessoa pessoacomp on pessoacomp.id = compromissario.compromissario_id ")
            .append("left join pessoafisica pfComp ON pfComp.id = pessoacomp.id ")
            .append("left join pessoajuridica pjComp ON pjComp.id = pessoacomp.id ")
            .append("where pro.exercicio_id = :exercicio ")
            .append("  and propriedade.id = (select prop.id " +
                "                        from propriedade prop " +
                "                                 left join pessoafisica pfprop on prop.pessoa_id = pfprop.id " +
                "                                 left join pessoajuridica pjprop on prop.pessoa_id = pjprop.id " +
                "                        where prop.imovel_id = ci.id " +
                "                          and (prop.finalvigencia is null or trunc(prop.finalvigencia) > trunc(sysdate)) " +
                "                        order by prop.proporcao desc, coalesce(pfprop.nome, pjprop.razaosocial) " +
                "                            fetch first 1 row only) ")
            .append("  and (compromissario.id = (select max(id) from compromissario where cadastroimobiliario_id = ci.id and fimvigencia is null) or compromissario.id is null) ")
            .append("  and (not exists (select parcela.id from ParcelaValorDivida parcela ")
            .append("    inner join SituacaoParcelaValorDivida situacao on situacao.id = parcela.situacaoAtual_id ")
            .append("    where parcela.valorDivida_id = vw.valorDivida_id and situacao.situacaoParcela = 'PAGO'))")
            .append("  and (ci.inscricaocadastral >= :inscricaoInicial and ci.inscricaocadastral <= :inscricaoFinal) ")
            .append("  and vw.situacaoparcela = :situacaoParcela ")
            .append("  and (calc.valorReal between :valorInicial and :valorFinal) ");
        if (!filtros.getGeraParcelasJaImpressas()) {
            sql.append("  and vw.quantidadeimpressoesdam = :quatidadeZero ");
        }
        if (ExportacaoIPTU.TipoImovelExportacao.PREDIAL.equals(filtros.getTipoImovel())) {
            sql.append("  and (select count(id) from construcao where imovel_id = ci.id and coalesce(cancelada, 0) = 0) > 0 ");
        } else if (ExportacaoIPTU.TipoImovelExportacao.TERRITORIAL.equals(filtros.getTipoImovel())) {
            sql.append("  and (select count(id) from construcao where imovel_id = ci.id and coalesce(cancelada, 0) = 0) <= 0 ");
        }
        if (!filtros.getCadastrosIgnorados().isEmpty()) {
            montarIdsIgnorados(filtros, sql);
        }
        if (filtros.getQuantidadeParcelasInicial() > 0) {
            sql.append(" and (select count(1) from parcelavalordivida pvd" +
                " inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
                " where pvd.valordivida_id = vw.valorDivida_id" +
                "   and coalesce(op.promocional, 0) = 0) >= :quantidadeParcelasInicial ");
        }
        if (filtros.getQuantidadeParcelasFinal() > 0) {
            sql.append(" and (select count(1) from parcelavalordivida pvd" +
                " inner join opcaopagamento op on op.id = pvd.opcaopagamento_id " +
                " where pvd.valordivida_id = vw.valorDivida_id" +
                "   and coalesce(op.promocional, 0) = 0) <= :quantidadeParcelasFinal ");
        }
        Query q = em.createNativeQuery(sql.toString());
        if (!filtros.getGeraParcelasJaImpressas()) {
            q.setParameter("quatidadeZero", 0);
        }
        q.setParameter("exercicio", filtros.getExercicio().getId());
        q.setParameter("inscricaoInicial", filtros.getInscricaoInicial());
        q.setParameter("inscricaoFinal", filtros.getInscricaoFinal());
        q.setParameter("situacaoParcela", SituacaoParcela.EM_ABERTO.name());
        q.setParameter("valorInicial", filtros.getValorInicial());
        q.setParameter("valorFinal", filtros.getValorFinal());
        if (filtros.getQuantidadeParcelasInicial() > 0) {
            q.setParameter("quantidadeParcelasInicial", filtros.getQuantidadeParcelasInicial());
        }
        if (filtros.getQuantidadeParcelasFinal() > 0) {
            q.setParameter("quantidadeParcelasFinal", filtros.getQuantidadeParcelasFinal());
        }
        List<Object[]> lista = q.getResultList();
        List<CalculoExportacaoIPTU> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            CalculoExportacaoIPTU calculo = new CalculoExportacaoIPTU();
            calculo.setId(((BigDecimal) obj[0]).longValue());
            calculo.setDataCalculo((Date) obj[1]);
            calculo.setValorReal(((BigDecimal) obj[2]));
            calculo.setIdCadastro(((BigDecimal) obj[3]).longValue());
            calculo.setValorVenal(((BigDecimal) obj[4]));
            calculo.setValorIptu(((BigDecimal) obj[5]));
            calculo.setValorTsu(((BigDecimal) obj[6]));
            calculo.setFracaoIdeal(((BigDecimal) obj[7]));
            calculo.setFatorCorrecao(((BigDecimal) obj[8]));
            calculo.setVlrVenalTerreno(((BigDecimal) obj[9]));
            calculo.setAreaConstruida(((BigDecimal) obj[10]));
            calculo.setVlrM2Construido(((BigDecimal) obj[11]));
            calculo.setVlrVenalEdificacao(((BigDecimal) obj[12]));
            calculo.setTipoIptu(((String) obj[13]));
            calculo.setUtilizacaoImovel(((String) obj[14]));
            calculo.setTopografia(((String) obj[15]));
            calculo.setSituacao(((String) obj[16]));
            calculo.setQualidadeConstrucao(((String) obj[17]));
            calculo.setPatrimonio(((String) obj[18]));
            calculo.setColetaDeLixo(((String) obj[19]));
            calculo.setVlrM2Terreno(((BigDecimal) obj[20]));
            calculo.setFatorPeso(((BigDecimal) obj[21]));
            calculo.setAreaExcedente(((BigDecimal) obj[22]));
            calculo.setVlrVenalExcedente(((BigDecimal) obj[23]));
            calculo.setAliquota(((BigDecimal) obj[24]));

            calculo.setTipoImovel(((String) obj[25]));
            calculo.setNomePessoa((String) obj[26]);
            calculo.setInscricaoCadastral((String) obj[27]);
            calculo.setTipoPessoa(((Character) obj[28]).toString());
            calculo.setCpfCnpj((String) obj[29]);


            calculo.setLogradouroImovel((String) obj[30]);
            calculo.setCepImovel((String) obj[31]);
            calculo.setBairroImovel((String) obj[32]);
            calculo.setComplementoImovel((String) obj[33]);
            calculo.setMunicipioImovel((String) obj[34]);
            calculo.setUfImovel((String) obj[35]);


            calculo.setLogradouroEntrega((String) obj[36]);
            calculo.setCepEntrega((String) obj[37]);
            calculo.setBairroEntrega((String) obj[38]);
            calculo.setComplementoEntrega((String) obj[39]);
            calculo.setMunicipioEntrega((String) obj[40]);
            calculo.setUfEntrega((String) obj[41]);

            calculo.setCompromissario(obj[42] != null ? (String) obj[42] : "");
            calculo.setCpfCnpjCompromissario(obj[43] != null ? (String) obj[43] : "");
            calculo.setTipoPessoaCompromissario(obj[44] != null ? ((Character) obj[44]).toString() : "");

            retorno.add(calculo);
        }
        return retorno;
    }

    private void selecionarEnderecoQueVaiNaExportacao(StringBuilder sql, TipoEnderecoExportacaoIPTU tipoEndereco, boolean enderecoEntrega) {
        StringBuilder logradouroImovel = new StringBuilder(" (select logradouro || ', ' || numero from vwenderecobci where cadastroimobiliario_id = ci.id and rownum = 1) ");
        StringBuilder cepImovel = new StringBuilder(" (select cep from vwenderecobci where cadastroimobiliario_id = ci.id and rownum = 1) ");
        StringBuilder bairroImovel = new StringBuilder(" (select bairro from vwenderecobci where cadastroimobiliario_id = ci.id and rownum = 1) ");
        StringBuilder complementoImovel = new StringBuilder(" ci.complementoEndereco ");
        String municipio = "'RIO BRANCO'";
        String uf = "'AC'";

        if (tipoEndereco == null || tipoEndereco.equals(TipoEnderecoExportacaoIPTU.ENDERECO_IMOVEL)) {
            sql.append(logradouroImovel).append(" as logradouro").append(enderecoEntrega ? "_entrega" : "_imovel").append(", ");
            sql.append(cepImovel).append(" as cep").append(enderecoEntrega ? "_entrega" : "_imovel").append(", ");
            sql.append(bairroImovel).append(" as bairro").append(enderecoEntrega ? "_entrega" : "_imovel").append(", ");
            sql.append(complementoImovel).append(" as complemento").append(enderecoEntrega ? "_entrega" : "_imovel").append(", ");
            sql.append(municipio).append(" as municipio").append(enderecoEntrega ? "_entrega" : "_imovel").append(", ");
            sql.append(uf).append(" as uf").append(enderecoEntrega ? "_entrega" : "_imovel").append(", ");
        } else {
            sql.append(subSelectBuscarEnderecoPessoaParaExportarArquivoIptu("ec.logradouro || ', ' || ec.numero", "logradouro_entrega", tipoEndereco, logradouroImovel.toString()));
            sql.append(subSelectBuscarEnderecoPessoaParaExportarArquivoIptu("ec.cep", "cep_entrega", tipoEndereco, cepImovel.toString()));
            sql.append(subSelectBuscarEnderecoPessoaParaExportarArquivoIptu("ec.bairro", "bairro_entrega", tipoEndereco, bairroImovel.toString()));
            sql.append(subSelectBuscarEnderecoPessoaParaExportarArquivoIptu("ec.complemento", "complemento_entrega", tipoEndereco, complementoImovel.toString()));
            sql.append(subSelectBuscarEnderecoPessoaParaExportarArquivoIptu("ec.localidade", "localidade_entrega", tipoEndereco, municipio));
            sql.append(subSelectBuscarEnderecoPessoaParaExportarArquivoIptu("ec.uf", "uf_entrega", tipoEndereco, uf));
        }
    }

    private String subSelectBuscarEnderecoPessoaParaExportarArquivoIptu(String field, String aliasField, TipoEnderecoExportacaoIPTU tipoEndereco, String enderecoPadrao) {
        String retorno = " case when (select ec.id " +
            "                          from ENDERECOCORREIO ec " +
            "                                   inner join PESSOA_ENDERECOCORREIO pec on ec.ID = pec.ENDERECOSCORREIO_ID " +
            "                                   inner join propriedade prop on prop.PESSOA_ID = pec.PESSOA_ID " +
            "                                   left join pessoafisica pf on prop.PESSOA_ID = pf.ID " +
            "                                   left join PESSOAJURIDICA pj on prop.PESSOA_ID = pj.ID " +
            "                          where (prop.FINALVIGENCIA is null or prop.FINALVIGENCIA > sysdate) " +
            "                            and prop.IMOVEL_ID = ci.ID ";
        if (tipoEndereco.equals(TipoEnderecoExportacaoIPTU.DOMICILIO_FISCAL)) {
            retorno += " and ec.TIPOENDERECO = '" + tipoEndereco.name() + "' ";
        } else if (tipoEndereco.equals(TipoEnderecoExportacaoIPTU.PRINCIPAL)) {
            retorno += " and coalesce(ec.PRINCIPAL, 0) = 1";
        }
        retorno += " order by coalesce(pf.NOME, pj.RAZAOSOCIAL), prop.PROPORCAO desc FETCH FIRST 1 ROWS ONLY) is not null " +
            " THEN (select " + field +
            "   from ENDERECOCORREIO ec " +
            "   inner join PESSOA_ENDERECOCORREIO pec on ec.ID = pec.ENDERECOSCORREIO_ID " +
            "   inner join propriedade prop on prop.PESSOA_ID = pec.PESSOA_ID " +
            "   left join pessoafisica pf on prop.PESSOA_ID = pf.ID " +
            "   left join PESSOAJURIDICA pj on prop.PESSOA_ID = pj.ID " +
            "       where (prop.FINALVIGENCIA is null or prop.FINALVIGENCIA > sysdate) " +
            "       and prop.IMOVEL_ID = ci.ID ";
        if (tipoEndereco.equals(TipoEnderecoExportacaoIPTU.DOMICILIO_FISCAL)) {
            retorno += " and ec.TIPOENDERECO = '" + tipoEndereco.name() + "' ";
        } else if (tipoEndereco.equals(TipoEnderecoExportacaoIPTU.PRINCIPAL)) {
            retorno += " and coalesce(ec.PRINCIPAL, 0) = 1";
        }
        retorno += " order by coalesce(pf.NOME, pj.RAZAOSOCIAL), " +
            " prop.PROPORCAO desc FETCH FIRST 1 ROWS ONLY) " +
            " else " + enderecoPadrao + " end as " + aliasField + ", ";
        return retorno;
    }

    private void montarIdsIgnorados(ExportacaoIPTU filtros, StringBuilder sql) {
        String clausula = " and not exists(select cad.id from cadastroimobiliario cad where (";
        for (List<Long> ids : Lists.partition(getIdsCadastros(filtros.getCadastrosIgnorados()), 1000)) {
            sql.append(clausula);
            sql.append("ci.id").append(" in (").append(Joiner.on(",").join(ids)).append(")");
            clausula = " or ";
        }
        sql.append("))");
    }

    public List<Long> getIdsCadastros(List<VOCadastroImobiliario> cadastros) {
        List<Long> ids = Lists.newArrayList();
        for (VOCadastroImobiliario cadastro : cadastros) {
            ids.add(cadastro.getId());
        }
        return ids;
    }

    private boolean possuiDebitosIptuAntesAnteriores(Long idCadastro, Exercicio exercicio, Divida dividaIptu, Divida dividaDAIptu) {
        List<Long> dividas = Lists.newArrayList();
        dividas.add(dividaIptu.getId());
        dividas.add(dividaDAIptu.getId());

        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, idCadastro);
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, dividas);
        consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.MENOR, exercicio.getAno());
        consulta.executaConsulta();

        return !consulta.getResultados().isEmpty();
    }

    private List<ResultadoParcela> buscaParcelaDoIptu(Exercicio exercicio, Long idCadastro, Divida dividaIptu) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, idCadastro);
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
        consulta.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IGUAL, dividaIptu.getId());
        consulta.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO, ConsultaParcela.Operador.IGUAL, exercicio.getAno());
        consulta.executaConsulta();
        return consulta.getResultados();
    }

    public String valorDecimal(BigDecimal valor, int tamanho, int casaDecimais) {
        if (valor == null) {
            valor = BigDecimal.ZERO;
        }

        String formato = "###,###,##0." + zeroEsqueda("0", casaDecimais);

        String strValor = new DecimalFormat(formato).format(valor);
        strValor = strValor.replace(".", "").replace(",", "");
        return zeroEsqueda(strValor, tamanho);
    }

    private String zeroEsqueda(String texto, int tamanho) {
        if (texto != null) {
            return StringUtil.cortaOuCompletaEsquerda(texto, tamanho, "0");
        }
        return StringUtil.cortaOuCompletaEsquerda("0", tamanho, "0");
    }

    private String caracter(String texto, int tamanho) {
        return StringUtil.cortaOuCompletaDireita(texto, tamanho, " ");
    }

    private String data(Date data) {
        if (data != null) {
            return new SimpleDateFormat(formatoData).format(data);
        }
        return "00000000";
    }


    @Override
    public ProcessoExportacaoIPTU recuperar(Object id) {
        try {
            begin();
            ProcessoExportacaoIPTU recuperar = super.recuperar(id);
            Hibernate.initialize(recuperar.getDetentorArquivoComposicao().getArquivosComposicao());
            commit();
            return recuperar;
        } catch (Exception ex) {
            logger.error("Erro na geração do arquivo de iptu. {}", ex.getMessage());
            logger.debug("Detalhes do Erro", ex);
            return null;
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<ProcessoExportacaoIPTU> gerarArquivo(ExportacaoIPTU filtros, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        try {
            String nomeArquivo = filtros.getInscricaoInicial() + "-" + filtros.getInscricaoFinal() + "-" + filtros.getExercicio().getAno();

            List<StringBuilder> arquivos = conteudoArquivo(filtros, assistente);
            if (arquivos.isEmpty()) {
                return new AsyncResult<>(null);
            }
            DetentorArquivoComposicao detentorArquivoComposicao = new DetentorArquivoComposicao();
            ProcessoExportacaoIPTU processoExportacaoIPTU = new ProcessoExportacaoIPTU();
            processoExportacaoIPTU.setDetentorArquivoComposicao(detentorArquivoComposicao);
            processoExportacaoIPTU.setUsuario(assistente.getUsuarioSistema());
            processoExportacaoIPTU.setQuantidadeLinhas(assistente.getTotal());
            processoExportacaoIPTU.setNovoSequencial(filtros.getNovoSequencial());
            int contador = 0;
            for (StringBuilder conteudoArquivo : arquivos) {
                contador++;
                if (conteudoArquivo != null && !Strings.isNullOrEmpty(conteudoArquivo.toString())) {
                    File file = new File(nomeArquivo + ".txt");
                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        assistente.setDescricaoProcesso("Finalizando o arquivo de exportação de IPTU");
                        String mime = "text/plain";
                        fos.write(conteudoArquivo.toString().getBytes("ISO-8859-1"));
                        InputStream stream = new FileInputStream(file);
                        Arquivo arquivo = arquivoFacade.novoArquivoMemoria(new Arquivo(), stream);
                        arquivo.setNome(nomeArquivo + (arquivos.size() > 1 ? "_parte_" + contador : "") + ".txt");
                        arquivo.setMimeType(mime);
                        ArquivoComposicao arquivoComposicao = new ArquivoComposicao();
                        arquivoComposicao.setArquivo(arquivo);
                        arquivoComposicao.setDetentorArquivoComposicao(detentorArquivoComposicao);
                        arquivoComposicao.setDataUpload(new Date());
                        detentorArquivoComposicao.getArquivosComposicao().add(arquivoComposicao);
                        assistente.setDescricaoProcesso("Arquivo de exportação de IPTU finailzado");
                    }
                }
            }
            begin();
            processoExportacaoIPTU = em.merge(processoExportacaoIPTU);
            assistente.setProcessoExportacaoIPTU(processoExportacaoIPTU);
            commit();
            return new AsyncResult<>(processoExportacaoIPTU);
        } catch (Exception ex) {
            logger.error("Erro na geração do arquivo de iptu. {}", ex.getMessage());
            logger.debug("Detalhes do Erro", ex);
        }
        return new AsyncResult<>(null);
    }

    public List<StringBuilder> conteudoArquivo(ExportacaoIPTU filtros, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        List<CalculoExportacaoIPTU> todosCalculos = listaCalculosIptu(filtros);
        if (todosCalculos.isEmpty()) return Lists.newArrayList();

        assistente.setTributosUsadoNoIPTUDoExercicio(Lists.newArrayList());
        assistente.setNumeroDaLinha(0);

        List<List<CalculoExportacaoIPTU>> partition = Lists.partition(todosCalculos, QUANTIDADE_CADASTROS);
        List<StringBuilder> conteudos = Lists.newArrayList();
        assistente.setTotal(todosCalculos.size());

        Long sequencial = filtros.getNovoSequencial() ? 0L : buscarSequencial();

        for (List<CalculoExportacaoIPTU> calculoExportacaoIPTUS : partition) {

            StringBuilder linha = new StringBuilder();
            ParametroExportacaoIPTU param = buscaParametroExportacaoIPTU();
            if (param != null) {
                if (param.getFormatoData() != null) {
                    formatoData = param.getFormatoData().getFormato();
                }

                ConfiguracaoTributario config = configuracaoTributarioFacade.retornaUltimo();
                List<OpcaoPagamentoDivida> opcoes = dividaFacade.getOpcaoPagamentoFacade().retornaOpcoesDaDividaNoExercicio(config.getDividaIPTU(), filtros.getExercicio());
                int qtdeOpcoes = 0;
                OpcaoPagamento opcaoParcelas = null;
                for (OpcaoPagamentoDivida opcaoPagamento : opcoes) {
                    qtdeOpcoes = qtdeOpcoes + opcaoPagamento.getOpcaoPagamento().getNumeroParcelas();
                    if (!opcaoPagamento.getOpcaoPagamento().getPromocional()) {
                        opcaoParcelas = opcaoPagamento.getOpcaoPagamento();
                    }
                }

                linha.append(segmentoA(param, assistente)).append("\n");
                linha.append(segmentoB(param, assistente)).append("\n");
                linha.append(segmentoC(param, assistente)).append("\n");
                linha.append(segmentoD(param, assistente)).append("\n");
                linha.append(segmentoE(param, filtros.getExercicio(), qtdeOpcoes, opcaoParcelas, assistente)).append("\n");
                linha.append(segmentoF(param, assistente)).append("\n");
                linha.append(segmentoG(param, assistente)).append("\n");

                linha.append(segmentoJ(param, filtros.getExercicio(), assistente)).append("\n");
                linha.append(segmentoK(param, filtros.getExercicio(), assistente)).append("\n");


                assistente.setNumeroDaLinhaPorInscricao(0);
                assistente.setDescricaoProcesso("Gerando o arquivo de exportação de IPTU");
                for (CalculoExportacaoIPTU calculo : calculoExportacaoIPTUS) {
                    try {
                        sequencial++;
                        assistente.setNumeroDaLinhaPorInscricao(assistente.getNumeroDaLinhaPorInscricao() + 1);

                        List<ResultadoParcela> parcelas = buscaParcelaDoIptu(filtros.getExercicio(), calculo.getIdCadastro(), config.getDividaIPTU());

                        linha.append(segmentoR(param, calculo, assistente)).append("\n");
                        linha.append(segmentoS(param, calculo, qtdeOpcoes, opcaoParcelas, assistente, sequencial)).append("\n");
                        linha.append(segmentoT(param, calculo, assistente)).append("\n");
                        linha.append(segmentoU(param, calculo, assistente)).append("\n");
                        linha.append(segmentoV(param, calculo, assistente)).append("\n");
                        linha.append(segmentoW(param, calculo, assistente)).append("\n");
                        linha.append(segmentoX(param, calculo.getIdCadastro(), filtros.getExercicio(), config.getDividaIPTU(), config.getDividaIPTU().getDivida(), parcelas.size() - 1, assistente)).append("\n");
                        linha.append(segmentoY(param, parcelas, assistente.getUsuarioSistema(), assistente)).append("\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        assistente.conta();
                    }
                }
            }
            conteudos.add(linha);
        }

        return conteudos;
    }

    private String segmentoA(ParametroExportacaoIPTU param, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segA = new StringBuilder("A");

        segA.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segA.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segA.append(caracter(param.getNomeDoArquivo(), 6));
        segA.append(caracter(param.getNumeroVersaoLayout(), 7));
        segA.append(caracter("", 8));
        segA.append(zeroEsqueda("0", 6));
        segA.append(zeroEsqueda(param.getIdentificacaoProduto(), 1));
        segA.append(data(new Date()));
        segA.append("1");
        segA.append("1"); // 1 = Teste 2 = Produção
        segA.append("S");
        segA.append("04034583000122"); // CNPJ do Convenente
        segA.append(caracter("", 9));
        segA.append(caracter("", 161));
        segA.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segA.toString();
    }

    private String segmentoB(ParametroExportacaoIPTU param, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segB = new StringBuilder("B");

        segB.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segB.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segB.append("04034583000122"); // CNPJ do Convenente
        segB.append(caracter("PREFEITURA MUNICIPAL DE RIO BRANCO", 50));
        segB.append(caracter("RUA: RUI BARBOSA, 285", 35));
        segB.append(caracter("69900120", 8));
        segB.append(caracter("RIO BRANCO", 30));
        segB.append(caracter("CENTRO", 30));
        segB.append(caracter("AC", 2));
        segB.append(caracter("", 54));
        segB.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segB.toString();
    }

    private String segmentoC(ParametroExportacaoIPTU param, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segC = new StringBuilder("C");

        segC.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segC.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segC.append(zeroEsqueda(param.getSegmentoFebrabanCovenente(), 1));
        segC.append(zeroEsqueda("00003646", 8));
        segC.append(zeroEsqueda("0", 2));
        segC.append(caracter("", 3));
        segC.append(zeroEsqueda(param.getMoedaConvenio().getValor(), 3));
        segC.append(param.getCasasDecimaisMoeda());
        segC.append(param.getReceberAposVencimento() ? "S" : "N");
        segC.append(param.getMoedaConvenio().getValor());
        segC.append(caracter("", 203));
        segC.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segC.toString();
    }

    private String segmentoD(ParametroExportacaoIPTU param, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segD = new StringBuilder("D");

        segD.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segD.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segD.append(caracter("CONTRIBUINTE", 25));
        segD.append(caracter("INSCRICAO", 10));
        segD.append(caracter("Inscrição Cadastral", 50));
        segD.append(caracter("A", 1));
        segD.append(zeroEsqueda("15", 2));
        segD.append(caracter("", 25));
        segD.append(caracter("", 25));
        segD.append(caracter("", 85));
        segD.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segD.toString();
    }

    private String segmentoE(ParametroExportacaoIPTU param, Exercicio exercicio, int qtdeOpcoes, OpcaoPagamento opcaoParcelas, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segE = new StringBuilder("E");

        segE.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segE.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segE.append(caracter("IMPOSTO PREDIAL TERRITORIAL URBANO", 40));
        segE.append(caracter("IPTU", 12));
        segE.append(caracter("", 40));
        segE.append(caracter("SECRETARIA MUNICIPAL DE FINANÇAS", 40));
        segE.append(caracter("EXERCÍCIO", 10));
        segE.append(caracter(exercicio.getAno() + "", 10));
        segE.append(zeroEsqueda(qtdeOpcoes + "", 2));
        segE.append(zeroEsqueda(opcaoParcelas != null ? opcaoParcelas.getNumeroParcelas() + "" : "0", 2));
        segE.append(valorDecimal(opcaoParcelas != null ? opcaoParcelas.getValorMinimoParcela() : BigDecimal.ZERO, 5, 2));
        segE.append(valorDecimal(BigDecimal.ZERO, 4, 2));
        segE.append(zeroEsqueda("0", 2));
        segE.append(zeroEsqueda("0", 2));
        segE.append(caracter("COMPOSIÇÃO DO IPTU", 25));
        segE.append(caracter("", 1));
        segE.append(caracter("C", 1));
        segE.append(caracter("", 10));
        segE.append(caracter("N", 1));
        segE.append(caracter("", 4));
        segE.append(caracter("S", 1));
        segE.append(caracter("S", 1));

        segE.append(caracter("", 10));
        segE.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segE.toString();
    }

    private String segmentoF(ParametroExportacaoIPTU param, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segF = new StringBuilder("F");

        segF.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segF.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segF.append(caracter("IMOVEL", 15));
        segF.append(caracter("INSCRIÇÃO", 10));
        segF.append(caracter("INSCRIÇÃO DO IMOVEL NO CADASTRO", 40));
        segF.append(caracter("A", 1));
        segF.append(zeroEsqueda("15", 2));
        segF.append(caracter("9.999.9999.9999.999", 25));
        segF.append(caracter("CARACTERISTICAS DO IMOVEL", 25));

        segF.append(caracter("", 105));
        segF.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segF.toString();
    }

    private String segmentoG(ParametroExportacaoIPTU param, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segG = new StringBuilder("G");

        segG.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segG.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segG.append("1");
        segG.append(caracter(param.getMsgSegG01Notificacao() + " " + param.getMsgSegG02Notificacao() + param.getMsgSegG03Notificacao() + param.getMsgSegG04Notificacao(), 220));

        segG.append(caracter("", 2));
        segG.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segG.toString();
    }

    private String segmentoJ(ParametroExportacaoIPTU param, Exercicio exercicio, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segJ = new StringBuilder("J");

        segJ.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segJ.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segJ.append(caracter("", 6));
        segJ.append(caracter("", 4));

        if (assistente.getUfmDoAno() == null) {
            assistente.setUfmDoAno(moedaFacade.recuperaValorUFMPorExercicio(exercicio.getAno()));
        }
        segJ.append(valorDecimal(assistente.getUfmDoAno(), 6, 2));

        segJ.append(caracter("", 207));
        segJ.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segJ.toString();
    }

    private String segmentoK(ParametroExportacaoIPTU param, Exercicio exercicio, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segK = new StringBuilder("");

        if (assistente.getTributosUsadoNoIPTUDoExercicio() == null || assistente.getTributosUsadoNoIPTUDoExercicio().isEmpty()) {
            assistente.setTributosUsadoNoIPTUDoExercicio(tributosUsadoNoIPTUDoExercicio(exercicio));
        }
        int count = 0;
        for (Tributo tributo : assistente.getTributosUsadoNoIPTUDoExercicio()) {
            if (!"".equals(segK.toString().trim())) {
                segK.append("\n");
            }
            count++;
            segK.append("K");
            segK.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
            segK.append(zeroEsqueda(param.getNumeroConvenio(), 6));
            segK.append(zeroEsqueda(count + "", 1));
            segK.append(caracter(tributo.getCodigo() + "", 6));
            segK.append(caracter(tributo.getDescricao(), 25));
            segK.append(valorDecimal(BigDecimal.ZERO, 6, 2));

            segK.append(caracter("", 185));
            segK.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));
        }
        return segK.toString();
    }

    private String segmentoS(ParametroExportacaoIPTU param, CalculoExportacaoIPTU calculo, int qtdeOpcoes, OpcaoPagamento opcaoParcelas, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente, Long sequencial) {
        StringBuilder segS = new StringBuilder("S");

        segS.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segS.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segS.append(zeroEsqueda(assistente.contaNumeroDaLinhaPorInscricao(), 6));
        segS.append(data(calculo.getDataCalculo()));
        segS.append(zeroEsqueda("0", 8));
        segS.append(zeroEsqueda(qtdeOpcoes + "", 2));
        segS.append(zeroEsqueda(opcaoParcelas != null ? opcaoParcelas.getNumeroParcelas() + "" : "0", 2));
        segS.append(valorDecimal(calculo.getValorReal(), 11, 2));
        segS.append("C");

        segS.append(caracter("", 4));
        segS.append(zeroEsqueda(sequencial.toString(), 6));
        segS.append(caracter(quebrarInscricaoParaCDE(calculo.getInscricaoCadastral()), 12));

        segS.append(caracter("", 22));
        segS.append(caracter("", 1));
        segS.append(caracter("", 14));
        segS.append(caracter("", 40));
        segS.append(caracter("", 40));
        segS.append(caracter("", 46));
        segS.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segS.toString();
    }

    private String segmentoT(ParametroExportacaoIPTU param, CalculoExportacaoIPTU calculo, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segT = new StringBuilder("T");

        segT.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segT.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segT.append(zeroEsqueda(assistente.contaNumeroDaLinhaPorInscricao(), 6));
        segT.append(caracter(calculo.getNomePessoa(), 40));
        segT.append(caracter(calculo.getInscricaoCadastral(), 25));
        segT.append(calculo.getTipoPessoa());
        segT.append(caracter(calculo.getCpfCnpj().replace(".", "").replace("-", "").replace("/", "").replace(",", ""), 14));

        segT.append(caracter(calculo.getLogradouroEntrega(), 45));
        segT.append(caracter(calculo.getCepEntrega(), 8));
        segT.append(caracter(calculo.getMunicipioEntrega(), 10));
        segT.append(caracter(calculo.getBairroEntrega(), 30));
        segT.append(caracter(calculo.getUfEntrega(), 2));
        segT.append(caracter(calculo.getComplementoEntrega(), 40));


        segT.append(caracter("", 2));
        segT.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segT.toString();
    }

    private String segmentoR(ParametroExportacaoIPTU param, CalculoExportacaoIPTU calculo, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segR = new StringBuilder("R");
        segR.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segR.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segR.append(zeroEsqueda(assistente.contaNumeroDaLinhaPorInscricao(), 6));

        segR.append(caracter(calculo.getLogradouroImovel(), 45));
        segR.append(caracter(calculo.getCepImovel(), 8));
        segR.append(caracter(calculo.getMunicipioImovel(), 10));
        segR.append(caracter(calculo.getBairroImovel(), 30));
        segR.append(caracter(calculo.getUfImovel(), 2));
        segR.append(caracter(calculo.getComplementoImovel(), 40));

        segR.append(caracter("", 80));
        segR.append(caracter("", 2));
        segR.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segR.toString();
    }

    private String segmentoV(ParametroExportacaoIPTU param, CalculoExportacaoIPTU calculo, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segV = new StringBuilder("V");

        segV.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segV.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segV.append(zeroEsqueda(assistente.contaNumeroDaLinhaPorInscricao(), 6));
        segV.append(caracter(calculo.getCompromissario(), 40));
        segV.append(calculo.getTipoPessoaCompromissario());
        segV.append(caracter(calculo.getCpfCnpjCompromissario().replace(".", "").replace("-", "").replace("/", "").replace(",", ""), 14));
        segV.append(caracter("", 162));
        segV.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segV.toString();
    }

    private String segmentoU(ParametroExportacaoIPTU param, CalculoExportacaoIPTU calculo, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segU = new StringBuilder("U");

        segU.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segU.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segU.append(zeroEsqueda(assistente.contaNumeroDaLinhaPorInscricao(), 6));

        segU.append(valorDecimal(calculo.getValorIptu(), 11, 2));
        segU.append(zeroEsqueda("0", 5));
        segU.append(valorDecimal(calculo.getValorTsu(), 11, 2));
        segU.append(zeroEsqueda("0", 5));

        segU.append(valorDecimal(BigDecimal.ZERO, 11, 2));
        segU.append(zeroEsqueda("0", 5));
        segU.append(valorDecimal(BigDecimal.ZERO, 11, 2));
        segU.append(zeroEsqueda("0", 5));
        segU.append(valorDecimal(BigDecimal.ZERO, 11, 2));
        segU.append(zeroEsqueda("0", 5));
        segU.append(valorDecimal(BigDecimal.ZERO, 11, 2));
        segU.append(zeroEsqueda("0", 5));
        segU.append(valorDecimal(BigDecimal.ZERO, 11, 2));
        segU.append(zeroEsqueda("0", 5));
        segU.append(valorDecimal(BigDecimal.ZERO, 11, 2));
        segU.append(zeroEsqueda("0", 5));
        segU.append(valorDecimal(BigDecimal.ZERO, 11, 2));
        segU.append(zeroEsqueda("0", 5));

        segU.append(caracter("", 73));
        segU.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segU.toString();
    }

    private String segmentoW(ParametroExportacaoIPTU param, CalculoExportacaoIPTU calculo, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segW = new StringBuilder("W");

        segW.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segW.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segW.append(zeroEsqueda(assistente.contaNumeroDaLinhaPorInscricao(), 6));

        segW.append(valorDecimal(calculo.getFracaoIdeal(), 8, 2));
        segW.append(valorDecimal(calculo.getFatorCorrecao(), 6, 3));
        segW.append(valorDecimal(calculo.getAreaConstruida(), 8, 2));
        segW.append(valorDecimal(calculo.getVlrVenalTerreno(), 11, 2));
        segW.append(valorDecimal(calculo.getValorReal(), 11, 2));
        segW.append(valorDecimal(calculo.getVlrM2Construido(), 6, 3));
        segW.append(valorDecimal(calculo.getVlrVenalEdificacao().add(calculo.getVlrVenalTerreno().add(calculo.getVlrVenalExcedente())), 11, 2));
        segW.append(caracter(calculo.getSituacao(), 15));
        segW.append(caracter(calculo.getUtilizacaoImovel(), 12));
        segW.append(caracter(calculo.getTopografia(), 10));
        segW.append(caracter(calculo.getQualidadeConstrucao(), 10));
        segW.append(caracter(calculo.getPatrimonio(), 10));
        segW.append(caracter(calculo.getColetaDeLixo(), 10));
        segW.append(caracter(calculo.getTipoIptu(), 10));
        segW.append(valorDecimal(calculo.getVlrM2Terreno(), 6, 3));
        segW.append(valorDecimal(calculo.getFatorPeso(), 6, 3));
        segW.append(valorDecimal(calculo.getVlrVenalEdificacao(), 11, 2));
        segW.append(valorDecimal(calculo.getAreaExcedente(), 11, 2));
        segW.append(valorDecimal(calculo.getVlrM2Terreno(), 6, 3));
        segW.append(valorDecimal(calculo.getFatorCorrecao(), 6, 3));
        segW.append(valorDecimal(calculo.getVlrVenalExcedente(), 11, 2));
        segW.append(valorDecimal(calculo.getAliquota(), 11, 2));
        segW.append(valorDecimal(calculo.getBaseCalculo(), 11, 2));

        segW.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));

        return segW.toString();
    }

    private String segmentoX(ParametroExportacaoIPTU param, Long idCadastro, Exercicio exercicio, Divida dividaIptu, Divida dividaDAIptu, int qtdeParcelas, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segX = new StringBuilder("X");

        segX.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
        segX.append(zeroEsqueda(param.getNumeroConvenio(), 6));
        segX.append(zeroEsqueda(assistente.contaNumeroDaLinhaPorInscricao(), 6));

        if (possuiDebitosIptuAntesAnteriores(idCadastro, exercicio, dividaIptu, dividaDAIptu)) {
            segX.append(caracter(param.getMsgSegX01Devedores(), 60));
            segX.append(caracter(param.getMsgSegX02Devedores(), 60));
        } else {
            segX.append(caracter(param.getMsgSegX03NaoDevedores(), 60));
            segX.append(caracter(param.getMsgSegX04NaoDevedores(), 60));
        }
        segX.append(caracter(param.getMsgSegX05Parcelamento().replace("$PARCELAS", qtdeParcelas + ""), 60));

        segX.append(caracter("", 37));
        segX.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));
        return segX.toString();
    }

    private String segmentoY(ParametroExportacaoIPTU param, List<ResultadoParcela> parcelas, UsuarioSistema usuario, ExportacaoIPTUControlador.AssistenteBarraProgressoExportacaoIPTU assistente) {
        StringBuilder segY = new StringBuilder("");

        for (ResultadoParcela parcela : parcelas) {
            DAM dam = recuperaDAMPeloIdParcelaExportacaoIPTU(parcela.getIdParcela());
            if (dam == null) {
                try {
                    damFacade.cancelarDamsDaParcela(parcela.getIdParcela(), usuario, DAM.Tipo.UNICO);
                    dam = gerarDamImportacaoIPTU(parcela, usuario);
                } catch (Exception e) {
                    dam = null;
                    e.printStackTrace();
                }
            }

            if (!segY.toString().trim().isEmpty()) {
                segY.append("\n");
            }
            segY.append("Y");
            segY.append(zeroEsqueda(param.getIdentificacaoRegistroSegmento(), 4));
            segY.append(zeroEsqueda(param.getNumeroConvenio(), 6));
            segY.append(zeroEsqueda(assistente.contaNumeroDaLinhaPorInscricao(), 6));

            if (parcela.getCotaUnica()) {
                segY.append("C");
                segY.append("00");
            } else {
                segY.append("P");
                segY.append(zeroEsqueda(parcela.getParcela().substring(0, parcela.getParcela().indexOf("/")), 2));
            }
            segY.append(data(parcela.getVencimento()));
            segY.append(valorDecimal(parcela.getValorTotal(), 11, 2));

            if (dam != null) {
                // inicio codigo de barras
                segY.append(zeroEsqueda(dam.getCodigoBarras().length() == 55 ? dam.getCodigoBarras().substring(0, 11) : "", 11));
                segY.append(zeroEsqueda(dam.getCodigoBarras().length() == 55 ? dam.getCodigoBarras().substring(12, 13) : "", 1));
                segY.append(zeroEsqueda(dam.getCodigoBarras().length() == 55 ? dam.getCodigoBarras().substring(14, 25) : "", 11));
                segY.append(zeroEsqueda(dam.getCodigoBarras().length() == 55 ? dam.getCodigoBarras().substring(26, 27) : "", 1));
                segY.append(zeroEsqueda(dam.getCodigoBarras().length() == 55 ? dam.getCodigoBarras().substring(28, 39) : "", 11));
                segY.append(zeroEsqueda(dam.getCodigoBarras().length() == 55 ? dam.getCodigoBarras().substring(40, 41) : "", 1));
                segY.append(zeroEsqueda(dam.getCodigoBarras().length() == 55 ? dam.getCodigoBarras().substring(42, 53) : "", 11));
                segY.append(zeroEsqueda(dam.getCodigoBarras().length() == 55 ? dam.getCodigoBarras().substring(54, 55) : "", 1));
                // fim codigo de barras
            } else {
                segY.append(caracter("DAM NÃO ENCONTRADO", 48));
            }

            segY.append(valorDecimal(parcela.getValorImposto(), 15, 2));
            segY.append(valorDecimal(parcela.getValorTaxa(), 15, 2));
            segY.append(valorDecimal(parcela.getValorDesconto(), 15, 2));
            if (dam != null) {
                segY.append(zeroEsqueda(dam.getNumero() + "", 10));
                segY.append(zeroEsqueda(dam.getExercicio().getAno() + "", 4));
            } else {
                segY.append(zeroEsqueda("0", 6));
                segY.append(zeroEsqueda("0", 4));
            }
            segY.append(caracter("", 88));
            segY.append(zeroEsqueda(assistente.contaNumeroDaLinha(), 6));
        }


        return segY.toString();
    }

    private String quebrarInscricaoParaCDE(String inscricao) {
        String setor = inscricao.substring(1, 4);
        String quadra = inscricao.substring(4, 8);
        String lote = inscricao.substring(8, 12);
        String unidade = inscricao.substring(12, 15);
        return setor.charAt(2) + quadra + lote + unidade;
    }

    private Long buscarSequencial() {
        String sql1 = "select proc.* " +
            " from processoexportacaoiptu proc " +
            " where proc.novosequencial = 1 order by proc.datahora desc";
        Query q = em.createNativeQuery(sql1, ProcessoExportacaoIPTU.class);
        q.setMaxResults(1);
        ProcessoExportacaoIPTU ultimoProcessoComNovoSequencial = null;
        List<ProcessoExportacaoIPTU> result = q.getResultList();
        if (!result.isEmpty()) ultimoProcessoComNovoSequencial = result.get(0);
        if (ultimoProcessoComNovoSequencial == null) return 0L;

        String sql2 = "select sum(coalesce(proc.quantidadelinhas, 0)) " +
            " from processoexportacaoiptu proc " +
            " where proc.datahora > :dataHoraNovoSequencial order by proc.datahora";
        Query query = em.createNativeQuery(sql2);
        query.setParameter("dataHoraNovoSequencial", ultimoProcessoComNovoSequencial.getDataHora());
        List<BigDecimal> results = query.getResultList();
        if (results.isEmpty() || results.get(0) == null) {
            return ultimoProcessoComNovoSequencial.getQuantidadeLinhas().longValue();
        }
        return results.get(0).longValue() + ultimoProcessoComNovoSequencial.getQuantidadeLinhas();
    }

    public DAM recuperaDAMPeloIdParcelaExportacaoIPTU(Long idParcela) {
        String sql = "select dam.* from DAM dam " +
            " inner join ItemDAM item on item.dam_id = dam.id " +
            " where item.parcela_id = :idParcela " +
            " and dam.vencimento >= :dataAtual " +
            " and dam.situacao = :situacao " +
            " and dam.tipo = :tipo ";

        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("situacao", DAM.Situacao.ABERTO.name());
        q.setParameter("tipo", DAM.Tipo.UNICO.name());
        q.setParameter("dataAtual", new Date());
        if (!q.getResultList().isEmpty()) {
            return (DAM) q.getResultList().get(0);
        }
        return null;
    }

    public class CalculoExportacaoIPTU {
        private Long id;
        private Date dataCalculo;
        private BigDecimal valorReal;
        private Long idCadastro;
        private BigDecimal valorVenal;
        private BigDecimal valorIptu;
        private BigDecimal valorTsu;
        private BigDecimal fracaoIdeal;
        private BigDecimal fatorCorrecao;
        private BigDecimal vlrVenalTerreno;
        private BigDecimal areaConstruida;
        private BigDecimal vlrM2Construido;
        private BigDecimal vlrVenalEdificacao;
        private String utilizacaoImovel;
        private String topografia;
        private String situacao;
        private String tipoIptu;
        private String qualidadeConstrucao;
        private String patrimonio;
        private String coletaDeLixo;
        private BigDecimal vlrM2Terreno;
        private BigDecimal fatorPeso;
        private BigDecimal areaExcedente;
        private BigDecimal vlrVenalExcedente;
        private BigDecimal aliquota;
        private String tipoImovel;
        private String nomePessoa;
        private String inscricaoCadastral;
        private String tipoPessoa;
        private String cpfCnpj;

        private String logradouroImovel;
        private String cepImovel;
        private String bairroImovel;
        private String complementoImovel;
        private String municipioImovel;
        private String ufImovel;

        private String logradouroEntrega;
        private String cepEntrega;
        private String bairroEntrega;
        private String complementoEntrega;
        private String municipioEntrega;
        private String ufEntrega;

        private String compromissario;
        private String cpfCnpjCompromissario;
        private String tipoPessoaCompromissario;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Date getDataCalculo() {
            return dataCalculo;
        }

        public void setDataCalculo(Date dataCalculo) {
            this.dataCalculo = dataCalculo;
        }

        public BigDecimal getValorReal() {
            return valorReal != null ? valorReal : BigDecimal.ZERO;
        }

        public void setValorReal(BigDecimal valorReal) {
            this.valorReal = valorReal;
        }

        public Long getIdCadastro() {
            return idCadastro;
        }

        public void setIdCadastro(Long idCadastro) {
            this.idCadastro = idCadastro;
        }

        public BigDecimal getValorVenal() {
            return valorVenal != null ? valorVenal : BigDecimal.ZERO;
        }

        public void setValorVenal(BigDecimal valorVenal) {
            this.valorVenal = valorVenal;
        }

        public BigDecimal getValorIptu() {
            return valorIptu != null ? valorIptu : BigDecimal.ZERO;
        }

        public void setValorIptu(BigDecimal valorIptu) {
            this.valorIptu = valorIptu;
        }

        public BigDecimal getValorTsu() {
            return valorTsu != null ? valorTsu : BigDecimal.ZERO;
        }

        public void setValorTsu(BigDecimal valorTsu) {
            this.valorTsu = valorTsu;
        }

        public BigDecimal getFracaoIdeal() {
            return fracaoIdeal != null ? fracaoIdeal : BigDecimal.ZERO;
        }

        public void setFracaoIdeal(BigDecimal fracaoIdeal) {
            this.fracaoIdeal = fracaoIdeal;
        }

        public BigDecimal getFatorCorrecao() {
            return fatorCorrecao != null ? fatorCorrecao : BigDecimal.ZERO;
        }

        public void setFatorCorrecao(BigDecimal fatorCorrecao) {
            this.fatorCorrecao = fatorCorrecao;
        }

        public BigDecimal getVlrVenalTerreno() {
            return vlrVenalTerreno != null ? vlrVenalTerreno : BigDecimal.ZERO;
        }

        public void setVlrVenalTerreno(BigDecimal vlrVenalTerreno) {
            this.vlrVenalTerreno = vlrVenalTerreno;
        }

        public BigDecimal getAreaConstruida() {
            return areaConstruida != null ? areaConstruida : BigDecimal.ZERO;
        }

        public void setAreaConstruida(BigDecimal areaConstruida) {
            this.areaConstruida = areaConstruida;
        }

        public BigDecimal getVlrM2Construido() {
            return vlrM2Construido != null ? vlrM2Construido : BigDecimal.ZERO;
        }

        public void setVlrM2Construido(BigDecimal vlrM2Construido) {
            this.vlrM2Construido = vlrM2Construido;
        }

        public BigDecimal getVlrVenalEdificacao() {
            return vlrVenalEdificacao != null ? vlrVenalEdificacao : BigDecimal.ZERO;
        }

        public void setVlrVenalEdificacao(BigDecimal vlrVenalEdificacao) {
            this.vlrVenalEdificacao = vlrVenalEdificacao;
        }

        public String getTipoIptu() {
            return tipoIptu != null ? tipoIptu : "";
        }

        public void setTipoIptu(String tipoIptu) {
            this.tipoIptu = tipoIptu;
        }

        public String getUtilizacaoImovel() {
            return utilizacaoImovel != null ? utilizacaoImovel : "";
        }

        public void setUtilizacaoImovel(String utilizacaoImovel) {
            this.utilizacaoImovel = utilizacaoImovel;
        }

        public String getTopografia() {
            return topografia != null ? topografia : "";
        }

        public void setTopografia(String topografia) {
            this.topografia = topografia;
        }

        public String getSituacao() {
            return situacao != null ? situacao : "";
        }

        public void setSituacao(String situacao) {
            this.situacao = situacao;
        }

        public String getQualidadeConstrucao() {
            return qualidadeConstrucao != null ? qualidadeConstrucao : "";
        }

        public void setQualidadeConstrucao(String qualidadeConstrucao) {
            this.qualidadeConstrucao = qualidadeConstrucao;
        }

        public String getPatrimonio() {
            return patrimonio != null ? patrimonio : "";
        }

        public void setPatrimonio(String patrimonio) {
            this.patrimonio = patrimonio;
        }

        public String getColetaDeLixo() {
            return coletaDeLixo != null ? coletaDeLixo : "";
        }

        public void setColetaDeLixo(String coletaDeLixo) {
            this.coletaDeLixo = coletaDeLixo;
        }

        public BigDecimal getVlrM2Terreno() {
            return vlrM2Terreno != null ? vlrM2Terreno : BigDecimal.ZERO;
        }

        public void setVlrM2Terreno(BigDecimal vlrM2Terreno) {
            this.vlrM2Terreno = vlrM2Terreno;
        }

        public BigDecimal getFatorPeso() {
            return fatorPeso != null ? fatorPeso : BigDecimal.ZERO;
        }

        public void setFatorPeso(BigDecimal fatorPeso) {
            this.fatorPeso = fatorPeso;
        }

        public BigDecimal getAreaExcedente() {
            return areaExcedente != null ? areaExcedente : BigDecimal.ZERO;
        }

        public void setAreaExcedente(BigDecimal areaExcedente) {
            this.areaExcedente = areaExcedente;
        }

        public BigDecimal getVlrVenalExcedente() {
            return vlrVenalExcedente != null ? vlrVenalExcedente : BigDecimal.ZERO;
        }

        public void setVlrVenalExcedente(BigDecimal vlrVenalExcedente) {
            this.vlrVenalExcedente = vlrVenalExcedente;
        }

        public BigDecimal getAliquota() {
            return aliquota != null ? aliquota : BigDecimal.ZERO;
        }

        public void setAliquota(BigDecimal aliquota) {
            this.aliquota = aliquota;
        }

        public BigDecimal getBaseCalculo() {
            return (getVlrVenalEdificacao().add(getVlrVenalExcedente()).add(getVlrVenalTerreno())).subtract(((getVlrVenalEdificacao().add(getVlrVenalExcedente()).add(getVlrVenalTerreno())).multiply(BigDecimal.valueOf(0.2))));
        }

        public String getNomePessoa() {
            return nomePessoa != null ? nomePessoa : "";
        }

        public void setNomePessoa(String nomePessoa) {
            this.nomePessoa = nomePessoa;
        }

        public String getInscricaoCadastral() {
            return inscricaoCadastral != null ? inscricaoCadastral : "";
        }

        public void setInscricaoCadastral(String inscricaoCadastral) {
            this.inscricaoCadastral = inscricaoCadastral;
        }

        public String getTipoPessoa() {
            return tipoPessoa;
        }

        public void setTipoPessoa(String tipoPessoa) {
            this.tipoPessoa = tipoPessoa;
        }

        public String getCpfCnpj() {
            return cpfCnpj != null ? cpfCnpj : "";
        }

        public void setCpfCnpj(String cpfCnpj) {
            this.cpfCnpj = cpfCnpj;
        }

        public String getLogradouroImovel() {
            if (logradouroImovel == null) logradouroImovel = "";
            return logradouroImovel;
        }

        public void setLogradouroImovel(String logradouroImovel) {
            this.logradouroImovel = logradouroImovel;
        }

        public String getCepImovel() {
            if (cepImovel == null) cepImovel = "";
            return cepImovel;
        }

        public void setCepImovel(String cepImovel) {
            this.cepImovel = cepImovel;
        }

        public String getBairroImovel() {
            if (bairroImovel == null) bairroImovel = "";
            return bairroImovel;
        }

        public void setBairroImovel(String bairroImovel) {
            this.bairroImovel = bairroImovel;
        }

        public String getComplementoImovel() {
            if (complementoImovel == null) complementoImovel = "";
            return complementoImovel;
        }

        public void setComplementoImovel(String complementoImovel) {
            this.complementoImovel = complementoImovel;
        }

        public String getMunicipioImovel() {
            if (municipioImovel == null) municipioImovel = "";
            return municipioImovel;
        }

        public void setMunicipioImovel(String municipioImovel) {
            this.municipioImovel = municipioImovel;
        }

        public String getUfImovel() {
            if (ufImovel == null) ufImovel = "";
            return ufImovel;
        }

        public void setUfImovel(String ufImovel) {
            this.ufImovel = ufImovel;
        }

        public String getLogradouroEntrega() {
            if (logradouroEntrega == null) logradouroEntrega = "";
            return logradouroEntrega;
        }

        public void setLogradouroEntrega(String logradouroEntrega) {
            this.logradouroEntrega = logradouroEntrega;
        }

        public String getCepEntrega() {
            if (cepEntrega == null) cepEntrega = "";
            return cepEntrega;
        }

        public void setCepEntrega(String cepEntrega) {
            this.cepEntrega = cepEntrega;
        }

        public String getBairroEntrega() {
            if (bairroEntrega == null) bairroEntrega = "";
            return bairroEntrega;
        }

        public void setBairroEntrega(String bairroEntrega) {
            this.bairroEntrega = bairroEntrega;
        }

        public String getComplementoEntrega() {
            if (complementoEntrega == null) complementoEntrega = "";
            return complementoEntrega;
        }

        public void setComplementoEntrega(String complementoEntrega) {
            this.complementoEntrega = complementoEntrega;
        }

        public String getMunicipioEntrega() {
            if (municipioEntrega == null) municipioEntrega = "";
            return municipioEntrega;
        }

        public void setMunicipioEntrega(String municipioEntrega) {
            this.municipioEntrega = municipioEntrega;
        }

        public String getUfEntrega() {
            if (ufEntrega == null) ufEntrega = "";
            return ufEntrega;
        }

        public void setUfEntrega(String ufEntrega) {
            this.ufEntrega = ufEntrega;
        }

        public String getTipoImovel() {
            return tipoImovel != null ? tipoImovel : "";
        }

        public void setTipoImovel(String tipoImovel) {
            this.tipoImovel = tipoImovel;
        }

        public String getCompromissario() {
            return compromissario != null ? compromissario.trim() : "";
        }

        public void setCompromissario(String compromissario) {
            this.compromissario = compromissario;
        }

        public String getCpfCnpjCompromissario() {
            return cpfCnpjCompromissario != null ? cpfCnpjCompromissario : "";
        }

        public void setCpfCnpjCompromissario(String cpfCnpjCompromissario) {
            this.cpfCnpjCompromissario = cpfCnpjCompromissario;
        }

        public String getTipoPessoaCompromissario() {
            return tipoPessoaCompromissario;
        }

        public void setTipoPessoaCompromissario(String tipoPessoaCompromissario) {
            this.tipoPessoaCompromissario = tipoPessoaCompromissario;
        }
    }

    public DAM gerarDamImportacaoIPTU(ResultadoParcela resultadoParcela, UsuarioSistema usuario) throws SystemException, NotSupportedException, HeuristicRollbackException, HeuristicMixedException, RollbackException {
        return damFacade.gerarDAMUnicoViaApi(usuario, resultadoParcela);
    }

    private void begin() throws NotSupportedException, SystemException {
        userTransaction = context.getUserTransaction();
        userTransaction.begin();
    }

    private void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SystemException {
        userTransaction.commit();
    }

    public List<VOCadastroImobiliario> buscarCadastrosImobiliariosIgnorados(ExportacaoIPTU selecionado) {

        String sql = " select ci.id," +
            "                 ci.inscricaocadastral, " +
            "                 setor.codigo || ' - ' || setor.nome," +
            "                 lote.codigolote," +
            "                 quadra.descricao, " +
            "                 bairro.codigo || ' - ' || bairro.descricao," +
            "                 logradouro.codigo || ' - ' || logradouro.nome, " +
            "                 ci.numero as numero " +
            "     from cadastroimobiliario ci " +
            "       inner join lote lote on lote.id = ci.lote_id" +
            "       inner join quadra quadra on lote.quadra_id = quadra.id" +
            "       inner join setor setor on lote.setor_id = setor.id" +
            "       inner join testada testadas on lote.id = testadas.lote_id" +
            "       inner join face face on testadas.face_id = face.id" +
            "        left join logradourobairro lograbairro on face.logradourobairro_id = lograbairro.id" +
            "        left join logradouro logradouro on lograbairro.logradouro_id = logradouro.id" +
            "        left join bairro bairro on lograbairro.bairro_id = bairro.id ";
        sql = montarCondicao(selecionado, sql);
        Query q = em.createNativeQuery(sql);
        if (!Strings.isNullOrEmpty(selecionado.getInscricaoInicial())) {
            q.setParameter("inscricaoInicial", selecionado.getInscricaoInicial());
        }
        if (!Strings.isNullOrEmpty(selecionado.getInscricaoFinal())) {
            q.setParameter("inscricaoFinal", selecionado.getInscricaoFinal());
        }
        if (!selecionado.getSetores().isEmpty()) {
            q.setParameter("idsSetores", getIdsSetores(selecionado.getSetores()));
        }
        if (!selecionado.getLotes().isEmpty()) {
            q.setParameter("codigosLotes", getCodigosLotes(selecionado.getLotes()));
        }
        if (!selecionado.getQuadras().isEmpty()) {
            q.setParameter("codigosQuadras", getCodigosQuadras(selecionado.getQuadras()));
        }
        if (!selecionado.getLogradouros().isEmpty()) {
            q.setParameter("idsLogradouros", getIdsLogradouros(selecionado.getLogradouros()));
        }
        if (!selecionado.getBairros().isEmpty()) {
            q.setParameter("idsBairros", getIdsBairros(selecionado.getBairros()));
        }
        if (selecionado.getCadastrosCepInvalido()) {
            q.setParameter("listaNegraCep", Lists.newArrayList("00000000"));
        }
        List resultList = q.getResultList();
        List<VOCadastroImobiliario> retorno = Lists.newArrayList();

        if (!resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                VOCadastroImobiliario cadastro = new VOCadastroImobiliario();
                cadastro.setId(((BigDecimal) obj[0]).longValue());
                cadastro.setInscricaoCadastral((String) obj[1]);
                cadastro.setSetor((String) obj[2]);
                cadastro.setLote((String) obj[3]);
                cadastro.setQuadra((String) obj[4]);
                cadastro.setBairro((String) obj[5]);
                cadastro.setLogradouro((String) obj[6]);
                cadastro.setNumero((String) obj[7]);
                retorno.add(cadastro);
            }
        }
        return retorno;
    }

    private String montarCondicao(ExportacaoIPTU selecionado, String sql) {
        String juncao = " where ";
        if (!Strings.isNullOrEmpty(selecionado.getInscricaoInicial())) {
            sql += juncao + " ci.inscricaocadastral >= :inscricaoInicial ";
            juncao = "and";
        }
        if (!Strings.isNullOrEmpty(selecionado.getInscricaoFinal())) {
            sql += juncao + " ci.inscricaocadastral <= :inscricaoFinal ";
            juncao = "and";
        }
        if (!selecionado.getSetores().isEmpty()) {
            sql += juncao + " setor.id in (:idsSetores) ";
            juncao = "and";
        }
        if (!selecionado.getLotes().isEmpty()) {
            sql += juncao + " lote.codigoLote in (:codigosLotes) ";
            juncao = "and";
        }
        if (!selecionado.getQuadras().isEmpty()) {
            sql += juncao + " quadra.codigo in (:codigosQuadras) ";
            juncao = "and";
        }
        if (!selecionado.getLogradouros().isEmpty()) {
            sql += juncao + " logradouro.id in (:idsLogradouros) ";
            juncao = "and";
        }

        if (!selecionado.getBairros().isEmpty()) {
            sql += juncao + " bairro.id in :idsBairros ";
            juncao = " and ";
        }
        if (selecionado.getCadastrosNumeroInvalido() || selecionado.getCadastrosCepInvalido()) sql += juncao + " ( ";

        if (selecionado.getCadastrosNumeroInvalido()) {
            if (TipoEnderecoExportacaoIPTU.ENDERECO_IMOVEL.equals(selecionado.getTipoEndereco())) {
                sql += " (ci.numero is null"
                    + " or lower(ci.numero) like '%s/n%' "
                    + " or lower(ci.numero) like '%s\\n%' "
                    + " or regexp_like(ci.numero, '^[0]+$')) ";

            } else {
                sql += " (exists (select e.id from EnderecoCorreio e" +
                    " inner join PESSOA_ENDERECOCORREIO pe on pe.enderecoscorreio_id = e.id " +
                    " inner join propriedade prop on prop.pessoa_id = pe.pessoa_id" +
                    "    where prop.imovel_id = ci.id"
                    + " and " + (TipoEnderecoExportacaoIPTU.DOMICILIO_FISCAL.equals(selecionado.getTipoEndereco()) ?
                    " e.tipoEndereco = 'DOMICILIO_FISCAL' " : " coalesce(e.principal, 0) = 1 ") + " and "
                    + "(lower(e.numero) like '%s/n%' "
                    + " or lower(e.numero) like '%s\\n%' "
                    + " or e.numero is null "
                    + " or regexp_like(e.numero, '^[0]+$'))) or not exists(select pe.ENDERECOSCORREIO_ID " +
                    "         from EnderecoCorreio e " +
                    "                  inner join PESSOA_ENDERECOCORREIO pe on pe.enderecoscorreio_id = e.id " +
                    "                  inner join propriedade prop on prop.pessoa_id = pe.pessoa_id " +
                    "         where prop.imovel_id = ci.id and (prop.FINALVIGENCIA is null or trunc(prop.FINALVIGENCIA) > trunc(sysdate)) "
                    + (!TipoEnderecoExportacaoIPTU.DOMICILIO_FISCAL.equals(selecionado.getTipoEndereco()) ? "and e.tipoendereco = '" + TipoEnderecoExportacaoIPTU.DOMICILIO_FISCAL.name() + "' " : " and coalesce(e.principal, 0) = 1 ")
                    + "))  ";
            }
        }
        juncao = " or ";
        if (selecionado.getCadastrosCepInvalido()) {
            if (TipoEnderecoExportacaoIPTU.ENDERECO_IMOVEL.equals(selecionado.getTipoEndereco())) {
                sql += (selecionado.getCadastrosNumeroInvalido() ? juncao : "") + " (lograbairro.CEP is null " +
                    " or lower(lograbairro.CEP) like '%s/n%' " +
                    " or lower(lograbairro.CEP) like '%s\\n%' " +
                    " or trim(lograbairro.CEP) in (:listaNegraCep) " +
                    " or length(trim(lograbairro.CEP)) != 8 " +
                    " or length(trim(translate(lograbairro.CEP, '0123456789',''))) is not null) ";
            } else {
                sql += (selecionado.getCadastrosNumeroInvalido() ? juncao : "") + " (" +
                    " exists ( select e.id from EnderecoCorreio e" +
                    " inner join PESSOA_ENDERECOCORREIO pe on pe.enderecoscorreio_id = e.id " +
                    " inner join propriedade prop on prop.pessoa_id = pe.pessoa_id" +
                    "    where prop.imovel_id = ci.id" +
                    " and " + (TipoEnderecoExportacaoIPTU.DOMICILIO_FISCAL.equals(selecionado.getTipoEndereco()) ?
                    " e.tipoEndereco = '" + TipoEnderecoExportacaoIPTU.DOMICILIO_FISCAL.name() + "' " : " coalesce(e.principal, 0) = 1 ") +
                    " and ( e.CEP is null " +
                    " or lower(e.CEP) like '%s/n%' " +
                    " or lower(e.CEP) like '%s\\n%' " +
                    " or trim(e.CEP) in (:listaNegraCep) " +
                    " or length(trim(e.CEP)) != 8 " +
                    " or length(trim(translate(e.CEP, '0123456789',''))) is not null)) or not exists(select pe.ENDERECOSCORREIO_ID from EnderecoCorreio ec " +
                    "    inner join PESSOA_ENDERECOCORREIO pe on pe.enderecoscorreio_id = ec.id " +
                    "    inner join propriedade prop on prop.pessoa_id = pe.pessoa_id " +
                    "    where prop.imovel_id = ci.id and (prop.FINALVIGENCIA is null or trunc(prop.FINALVIGENCIA) > trunc(sysdate)) "
                    + (TipoEnderecoExportacaoIPTU.DOMICILIO_FISCAL.equals(selecionado.getTipoEndereco()) ? " and ec.tipoendereco = '" + TipoEnderecoExportacaoIPTU.DOMICILIO_FISCAL.name() + "' " : " and coalesce(ec.principal, 0) = 1 ")
                    + ")) ";
            }
        }
        if (selecionado.getCadastrosNumeroInvalido() || selecionado.getCadastrosCepInvalido()) sql += " ) ";
        sql += " and coalesce(ci.ativo, 1) = 1  order by ci.inscricaocadastral";
        return sql;
    }

    public List<Long> getIdsBairros(List<Bairro> bairros) {
        List<Long> ids = Lists.newArrayList();
        for (Bairro bairro : bairros) {
            ids.add(bairro.getId());
        }
        return ids;
    }

    public List<Long> getIdsLogradouros(List<Logradouro> logradouros) {
        List<Long> ids = Lists.newArrayList();
        for (Logradouro logra : logradouros) {
            ids.add(logra.getId());
        }
        return ids;
    }

    public List<Long> getIdsSetores(List<Setor> setores) {
        List<Long> ids = Lists.newArrayList();
        for (Setor setor : setores) {
            ids.add(setor.getId());
        }
        return ids;
    }

    public List<String> getCodigosLotes(List<Lote> lotes) {
        List<String> codigos = Lists.newArrayList();
        for (Lote lote : lotes) {
            codigos.add(lote.getCodigoLote());
        }
        return codigos;
    }

    public List<String> getCodigosQuadras(List<Quadra> quadras) {
        List<String> codigosLotes = Lists.newArrayList();
        for (Quadra quadra : quadras) {
            codigosLotes.add(quadra.getCodigo());
        }
        return codigosLotes;
    }

    public SetorFacade getSetorFacade() {
        return setorFacade;
    }

    public LoteFacade getLoteFacade() {
        return loteFacade;
    }

    public QuadraFacade getQuadraFacade() {
        return quadraFacade;
    }

    public BairroFacade getBairroFacade() {
        return bairroFacade;
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
