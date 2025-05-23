package br.com.webpublico.negocios;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteImpressaoMalaDiretaGeral;
import br.com.webpublico.entidadesauxiliares.ContribuinteTributario;
import br.com.webpublico.entidadesauxiliares.FiltroMalaDiretaGeral;
import br.com.webpublico.entidadesauxiliares.ImpressaoMalaDiretaGeral;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDamDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcMalaDiretaGeral;
import br.com.webpublico.negocios.tributario.services.ServiceDAM;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by William on 07/06/2016.
 */
@Stateless
public class MalaDiretaGeralFacade extends AbstractFacade<MalaDiretaGeral> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    private JdbcMalaDiretaGeral daoMalaDiretaGeral;
    private JdbcDamDAO daoDAM;
    private ServiceDAM serviceDAM;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private LogradouroFacade logradouroFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private PixFacade pixFacade;
    @EJB
    private DAMFacade damFacade;


    public MalaDiretaGeralFacade() {
        super(MalaDiretaGeral.class);
    }

    public LogradouroFacade getLogradouroFacade() {
        return logradouroFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    private List<Long> getIdsDaLista(List lista) {
        List<Long> ids = Lists.newArrayList();
        for (Object obj : lista) {
            ids.add((Long) Persistencia.getId(obj));
        }
        return ids;
    }

    private List<String> montarListBairros(List<Bairro> bairros) {
        List<String> descricoes = Lists.newArrayList();
        for (Bairro bairro : bairros) {
            if (bairro != null && bairro.getDescricao() != null) {
                descricoes.add(bairro.getDescricao().trim().toLowerCase());
            }
        }
        return descricoes;
    }

    private List<String> montarListLogradouros(List<Logradouro> logradouros) {
        List<String> descricoes = Lists.newArrayList();
        for (Logradouro logradouro : logradouros) {
            if (logradouro != null && logradouro.getNome() != null) {
                descricoes.add(logradouro.getNome().trim().toLowerCase());
            }
        }
        return descricoes;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<ImpressaoMalaDiretaGeral> buscarIdsDeBcesParaMalaDireta(FiltroMalaDiretaGeral filtroMalaDiretaGeral) {
        Map<String, Object> parameters = Maps.newHashMap();

        String sql = "select distinct cadastro.id, " +
            " cadastro.inscricaocadastral, " +
            " COALESCE(pf.nome, pj.razaosocial), " +
            " COALESCE(pf.cpf, pj.cnpj)," +
            " pessoa.id as pessoaId" +
            " from cadastroeconomico cadastro" +
            " inner join EnquadramentoFiscal enq on enq.cadastroeconomico_id = cadastro.id " +
            " inner join pessoa on pessoa.id = cadastro.pessoa_id " +
            "  left join pessoafisica pf on pf.id = pessoa.id " +
            "  left join pessoajuridica pj on pj.id = pessoa.id " +
            " and sysdate between coalesce(enq.inicioVigencia, sysdate) and coalesce(enq.fimVigencia, sysdate) ";

        String where = "where cadastro.inscricaocadastral between :inicial and :final ";


        parameters.put("inicial", filtroMalaDiretaGeral.getFiltroEconomico().getInscricaoIncial());
        parameters.put("final", filtroMalaDiretaGeral.getFiltroEconomico().getInscricaoFinal());

        if (!filtroMalaDiretaGeral.getFiltroEconomico().getCnaes().isEmpty()) {
            where += " and  exists (select * from economicocnae eco where eco.cadastroeconomico_id = cadastro.id and eco.cnae_id in (:idsCnaes)) ";
            parameters.put("idsCnaes", getIdsDaLista(filtroMalaDiretaGeral.getFiltroEconomico().getCnaes()));
        }

        if (!filtroMalaDiretaGeral.getFiltroEconomico().getClassificacoes().isEmpty()) {
            where += " and cadastro.classificacaoAtividade in :classificacoes";
            List<String> classificacoes = Lists.newArrayList();
            for (ClassificacaoAtividade classificacao : filtroMalaDiretaGeral.getFiltroEconomico().getClassificacoes()) {
                classificacoes.add(classificacao.name());
            }
            parameters.put("classificacoes", classificacoes);
        }

        if (!filtroMalaDiretaGeral.getFiltroEconomico().getSituacoes().isEmpty()) {
            sql += " inner join CE_SITUACAOCADASTRAL ce_sit on ce_sit.cadastroeconomico_id = cadastro.id " +
                " inner join situacaocadastroeconomico situacao on situacao.id = ce_sit.situacaocadastroeconomico_id ";
            where += "      and situacao.dataRegistro = (SELECT max(sit.dataRegistro) FROM situacaocadastroeconomico sit " +
                " INNER JOIN ce_situacaocadastral ceSit on ceSit.situacaocadastroeconomico_id = sit.id " +
                " WHERE ceSit.CADASTROECONOMICO_ID = cadastro.id)";

            where += " and situacao.situacaoCadastral  in :situacoes ";
            List<String> situacoes = Lists.newArrayList();
            for (SituacaoCadastralCadastroEconomico situacao : filtroMalaDiretaGeral.getFiltroEconomico().getSituacoes()) {
                situacoes.add(situacao.name());
            }
            parameters.put("situacoes", situacoes);
        }

        if (!filtroMalaDiretaGeral.getFiltroEconomico().getTiposAutonomos().isEmpty()) {
            where += " and  cadastro.tipoautonomo_id in (:tiposAutonomo) ";
            parameters.put("tiposAutonomo", getIdsDaLista(filtroMalaDiretaGeral.getFiltroEconomico().getTiposAutonomos()));
        }

        if (!filtroMalaDiretaGeral.getFiltroEconomico().getNaturezasJuridicas().isEmpty()) {
            where += " and  cadastro.naturezajuridica_id in (:naturezas) ";
            parameters.put("naturezas", getIdsDaLista(filtroMalaDiretaGeral.getFiltroEconomico().getNaturezasJuridicas()));
        }

        if (!filtroMalaDiretaGeral.getFiltroEconomico().getSocios().isEmpty()) {
            sql += " left join sociedadecadastroeconomico sociedade on sociedade.cadastroeconomico_id = cadastro.id " +
                " and sysdate between coalesce(sociedade.datainicio, sysdate) and coalesce(sociedade.datafim, sysdate) ";

            where += " and  sociedade.socio_id in (:socios) ";
            parameters.put("socios", getIdsDaLista(filtroMalaDiretaGeral.getFiltroEconomico().getSocios()));
        }

        if (!filtroMalaDiretaGeral.getFiltroEconomico().getPessoas().isEmpty()) {
            where += " and  cadastro.pessoa_id in (:pessoas) ";
            parameters.put("pessoas", getIdsDaLista(filtroMalaDiretaGeral.getFiltroEconomico().getPessoas()));
        }

        if (!filtroMalaDiretaGeral.getFiltroEconomico().getTiposIssqn().isEmpty()) {
            where += " and enq.tipoIssqn in :tiposIss";
            List<String> tiposIss = Lists.newArrayList();
            for (TipoIssqn tipo : filtroMalaDiretaGeral.getFiltroEconomico().getTiposIssqn()) {
                tiposIss.add(tipo.name());
            }
            parameters.put("tiposIss", tiposIss);
        }

        if (!filtroMalaDiretaGeral.getFiltroEconomico().getPortes().isEmpty()) {
            where += " and enq.porte in :portes";
            List<String> portes = Lists.newArrayList();
            for (TipoPorte tipo : filtroMalaDiretaGeral.getFiltroEconomico().getPortes()) {
                portes.add(tipo.name());
            }
            parameters.put("portes", portes);
        }

        if (!filtroMalaDiretaGeral.getFiltroEconomico().getBairros().isEmpty()
            || !filtroMalaDiretaGeral.getFiltroEconomico().getLogradouros().isEmpty()) {
            sql += " left join vwenderecopessoa vwendereco on vwendereco.pessoa_id = cadastro.pessoa_id ";

            List<String> bairros = montarListBairros(filtroMalaDiretaGeral.getFiltroEconomico().getBairros());
            if (!bairros.isEmpty()) {
                where += " and lower(trim(vwendereco.bairro)) in (:bairros) ";
                parameters.put("bairros", bairros);
            }
            List<String> logradouros = montarListLogradouros(filtroMalaDiretaGeral.getFiltroEconomico().getLogradouros());
            if (!logradouros.isEmpty()) {
                where += " and lower(trim(vwendereco.logradouros)) in (:logradouros) ";
                parameters.put("logradouros", logradouros);
            }
        }


        where = montarFiltroDividas(filtroMalaDiretaGeral, parameters, where);

        Query q = em.createNativeQuery(sql + where);
        for (String parametro : parameters.keySet()) {
            q.setParameter(parametro, parameters.get(parametro));
        }

        List<ImpressaoMalaDiretaGeral> toReturn = montarImpressaoCadastro(q);
        return toReturn;
    }

    private List<ImpressaoMalaDiretaGeral> montarImpressaoCadastro(Query q) {
        List<ImpressaoMalaDiretaGeral> toReturn = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        for (Object[] obj : resultList) {
            Long idCadastro = ((BigDecimal) obj[0]).longValue();
            if (!hasCadastroAdicionado(toReturn, idCadastro)) {
                ImpressaoMalaDiretaGeral impressao = new ImpressaoMalaDiretaGeral();
                impressao.setId(idCadastro);
                impressao.setNumeroCadastro((String) obj[1]);
                impressao.setNomeContribuinte((String) obj[2]);
                impressao.setCpfContribuinte((String) obj[3]);
                impressao.setIdPessoa(((BigDecimal) obj[4]).longValue());
                toReturn.add(impressao);
            }
        }
        return toReturn;
    }

    private boolean hasCadastroAdicionado(List<ImpressaoMalaDiretaGeral> lista, Long idCadastro) {
        for (ImpressaoMalaDiretaGeral impressaoMalaDiretaGeral : lista) {
            if (impressaoMalaDiretaGeral.getId().equals(idCadastro)) {
                return true;
            }
        }
        return false;
    }


    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<ImpressaoMalaDiretaGeral> buscarIdsDePessoasParaMalaDireta(FiltroMalaDiretaGeral filtroMalaDiretaGeral) {

        if (filtroMalaDiretaGeral.getMalaDiretaGeral().getTipoMalaDireta().isCobranca()) {
            Map<String, Object> parameters = Maps.newHashMap();

            String sql = "  select pessoa.id from Pessoa where exists (select pvd.id from PARCELAVALORDIVIDA pvd " +
                " inner join SITUACAOPARCELAVALORDIVIDA spvd on spvd.id = pvd.SITUACAOATUAL_ID " +
                " inner join VALORDIVIDA vd on vd.id = pvd.VALORDIVIDA_ID " +
                " inner join Calculo cal on cal.id = vd.CALCULO_ID " +
                " inner join CalculoPessoa calPessoa on calPessoa.calculo_id = cal.id" +
                " inner join exercicio ex on ex.id = vd.exercicio_id " +
                " where spvd.SITUACAOPARCELA = :situacao " +
                " and calPessoa.pessoa_id = pessoa.id " +
                " and vd.DIVIDA_ID in :idsDivida ";

            if (filtroMalaDiretaGeral.getVencimentoInicial() != null) {
                sql += " and pvd.vencimento >= :vencimentoInicial ";
                parameters.put("vencimentoInicial", filtroMalaDiretaGeral.getVencimentoInicial());
            }

            if (filtroMalaDiretaGeral.getVencimentoFinal() != null) {
                sql += " and pvd.vencimento <= :vencimentoFinal ";
                parameters.put("vencimentoFinal", filtroMalaDiretaGeral.getVencimentoFinal());
            }

            sql += " and ex.ano between :exercicioInicial and  :exercicioFinal) and pessoa.id in :idsPessoa ";
            parameters.put("situacao", SituacaoParcela.EM_ABERTO.name());
            parameters.put("exercicioInicial", filtroMalaDiretaGeral.getExercicioInicial().getAno());
            parameters.put("exercicioFinal", filtroMalaDiretaGeral.getExercicioFinal().getAno());
            parameters.put("idsDivida", getIdsDaLista(filtroMalaDiretaGeral.getDividas()));
            parameters.put("idsPessoa", getIdsDaLista(filtroMalaDiretaGeral.getFiltroPessoa().getPessoas()));
            Query q = em.createNativeQuery(sql);
            for (String parametro : parameters.keySet()) {
                q.setParameter(parametro, parameters.get(parametro));
            }
            List<ImpressaoMalaDiretaGeral> toReturn = Lists.newArrayList();
            for (Object id : q.getResultList()) {
                toReturn.add(adicionarPessoaNaLista(filtroMalaDiretaGeral, ((BigDecimal) id).longValue()));
            }
            return toReturn;
        } else {
            List<ImpressaoMalaDiretaGeral> toReturn = Lists.newArrayList();
            for (Pessoa pessoa : filtroMalaDiretaGeral.getFiltroPessoa().getPessoas()) {
                toReturn.add(criarImpressaoPessoa(pessoa));
            }
            return toReturn;

        }
    }

    private ImpressaoMalaDiretaGeral adicionarPessoaNaLista(FiltroMalaDiretaGeral filtroMalaDiretaGeral, Long id) {
        for (Pessoa pessoa : filtroMalaDiretaGeral.getFiltroPessoa().getPessoas()) {
            if (pessoa.getId().equals(id)) {
                return criarImpressaoPessoa(pessoa);
            }
        }
        return null;
    }

    private ImpressaoMalaDiretaGeral criarImpressaoPessoa(Pessoa pessoa) {
        ImpressaoMalaDiretaGeral impressao = new ImpressaoMalaDiretaGeral();
        impressao.setId(pessoa.getId());
        impressao.setNumeroCadastro(pessoa.getId().toString());
        impressao.setNomeContribuinte(pessoa.getNome());
        impressao.setCpfContribuinte(pessoa.getCpf_Cnpj());
        impressao.setId(pessoa.getId());
        return impressao;
    }

    private String montarFiltroDividas(FiltroMalaDiretaGeral filtroMalaDiretaGeral, Map<String, Object> parameters, String where) {
        if (filtroMalaDiretaGeral.getMalaDiretaGeral().getTipoMalaDireta().isCobranca()) {
            where += "  and exists (select pvd.id from PARCELAVALORDIVIDA pvd " +
                " inner join SITUACAOPARCELAVALORDIVIDA spvd on spvd.id = pvd.SITUACAOATUAL_ID " +
                " inner join VALORDIVIDA vd on vd.id = pvd.VALORDIVIDA_ID " +
                " inner join Calculo cal on cal.id = vd.CALCULO_ID " +
                " inner join exercicio ex on ex.id = vd.exercicio_id " +
                " where spvd.SITUACAOPARCELA = :situacao " +
                " and cal.CADASTRO_ID = cadastro.id " +
                " and vd.DIVIDA_ID in :idsDivida ";

            if (filtroMalaDiretaGeral.getVencimentoInicial() != null) {
                where += " and pvd.vencimento >= :vencimentoInicial ";
                parameters.put("vencimentoInicial", filtroMalaDiretaGeral.getVencimentoInicial());
            }

            if (filtroMalaDiretaGeral.getVencimentoFinal() != null) {
                where += " and pvd.vencimento <= :vencimentoFinal ";
                parameters.put("vencimentoFinal", filtroMalaDiretaGeral.getVencimentoFinal());
            }

            where += " and ex.ano between :exercicioInicial and  :exercicioFinal ) ";
            parameters.put("situacao", SituacaoParcela.EM_ABERTO.name());
            parameters.put("exercicioInicial", filtroMalaDiretaGeral.getExercicioInicial().getAno());
            parameters.put("exercicioFinal", filtroMalaDiretaGeral.getExercicioFinal().getAno());


            List<Long> idsDividas = Lists.newArrayList();
            for (Divida divida : filtroMalaDiretaGeral.getDividas()) {
                idsDividas.add(divida.getId());
            }
            parameters.put("idsDivida", idsDividas);
        }
        return where;
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public List<ImpressaoMalaDiretaGeral> buscarIdsDeBcisParaMalaDireta(FiltroMalaDiretaGeral filtroMalaDiretaGeral) {
        Map<String, Object> parameters = Maps.newHashMap();
        String sql = "select distinct cadastro.id, " +
            " cadastro.inscricaocadastral, " +
            " COALESCE(pf.nome, pj.razaosocial), " +
            " COALESCE(pf.cpf, pj.cnpj)," +
            " pessoa.id as pessoaId " +
            "  from cadastroimobiliario cadastro " +
            "  inner join propriedade pp on pp.imovel_id = cadastro.id " +
            "  inner join pessoa on pessoa.id = pp.pessoa_id " +
            "  left join pessoafisica pf on pf.id = pessoa.id" +
            "  left join pessoajuridica pj on pj.id = pessoa.id" +
            "  left join vwenderecobci vwend on vwend.cadastroimobiliario_id = cadastro.id ";

        String where = "where sysdate between pp.iniciovigencia and coalesce(pp.finalvigencia, sysdate) " +
            "  and cadastro.inscricaocadastral between :inicial and :final ";

        parameters.put("inicial", filtroMalaDiretaGeral.getFiltroImovel().getInscricaoIncial());
        parameters.put("final", filtroMalaDiretaGeral.getFiltroImovel().getInscricaoFinal());

        if (!filtroMalaDiretaGeral.getFiltroImovel().getProprietarios().isEmpty()) {
            where += " and  pp.pessoa_id in :idsProprietario ";
            List<Long> ids = Lists.newArrayList();
            for (Pessoa pessoa : filtroMalaDiretaGeral.getFiltroImovel().getProprietarios()) {
                ids.add(pessoa.getId());
            }
            parameters.put("idsProprietario", ids);
        }

        if (!filtroMalaDiretaGeral.getFiltroImovel().getCompromissarios().isEmpty()) {
            sql += " left join compromissario comp on comp.cadastroimobiliario_id = cadastro.id ";
            where += " and  comp.compromissario_id in :idsCompromissario ";
            List<Long> ids = Lists.newArrayList();
            for (Pessoa pessoa : filtroMalaDiretaGeral.getFiltroImovel().getCompromissarios()) {
                ids.add(pessoa.getId());
            }
            parameters.put("idsCompromissario", ids);
        }

        if (!Strings.isNullOrEmpty(filtroMalaDiretaGeral.getFiltroImovel().getSetorInicial())) {
            where += " and  cast(vwend.setor as int) >= :setorInicial ";
            parameters.put("setorInicial", filtroMalaDiretaGeral.getFiltroImovel().getSetorInicial());
        }
        if (!Strings.isNullOrEmpty(filtroMalaDiretaGeral.getFiltroImovel().getSetorFinal())) {
            where += " and  cast(vwend.setor as int) <= :setorFinal ";
            parameters.put("setorFinal", filtroMalaDiretaGeral.getFiltroImovel().getSetorFinal());
        }
        if (!Strings.isNullOrEmpty(filtroMalaDiretaGeral.getFiltroImovel().getQuadraInicial())) {
            where += " and  vwend.quadra >= :quadraInicial";
            parameters.put("quadraInicial", filtroMalaDiretaGeral.getFiltroImovel().getQuadraInicial());
        }
        if (!Strings.isNullOrEmpty(filtroMalaDiretaGeral.getFiltroImovel().getQuadraFinal())) {
            where += " and  vwend.quadra <= :quadraFinal ";
            parameters.put("quadraFinal", filtroMalaDiretaGeral.getFiltroImovel().getQuadraFinal());
        }
        if (!Strings.isNullOrEmpty(filtroMalaDiretaGeral.getFiltroImovel().getLoteInicial())) {
            where += " and  vwend.lote >= :loteInicial ";
            parameters.put("loteInicial", filtroMalaDiretaGeral.getFiltroImovel().getLoteInicial());
        }
        if (!Strings.isNullOrEmpty(filtroMalaDiretaGeral.getFiltroImovel().getLoteFinal())) {
            where += " and  vwend.lote <= :loteFinal ";
            parameters.put("loteFinal", filtroMalaDiretaGeral.getFiltroImovel().getLoteFinal());
        }
        if (!filtroMalaDiretaGeral.getFiltroImovel().getLogradouros().isEmpty()) {
            where += " and  vwend.ID_LOGRADOURO in :idsLogradouro ";
            List<Long> ids = Lists.newArrayList();
            for (Logradouro logradouro : filtroMalaDiretaGeral.getFiltroImovel().getLogradouros()) {
                ids.add(logradouro.getId());
            }
            parameters.put("idsLogradouro", ids);
        }
        if (!filtroMalaDiretaGeral.getFiltroImovel().getBairros().isEmpty()) {
            where += " and  vwend.ID_BAIRRO in :idsBairro ";
            List<Long> ids = Lists.newArrayList();
            for (Bairro bairro : filtroMalaDiretaGeral.getFiltroImovel().getBairros()) {
                ids.add(bairro.getId());
            }
            parameters.put("idsBairro", ids);
        }
        if (TipoImovel.PREDIAL.equals(filtroMalaDiretaGeral.getFiltroImovel().getTipoImovel())) {
            where += " and exists (select ct.id from construcao ct " +
                " where ct.imovel_id = cadastro.id and coalesce(ct.cancelada,0) = 0) ";
        }
        if (TipoImovel.TERRITORIAL.equals(filtroMalaDiretaGeral.getFiltroImovel().getTipoImovel())) {
            where += " and not exists (select ct.id from construcao ct " +
                " where ct.imovel_id = cadastro.id  and coalesce(ct.cancelada,0) = 0) ";
        }
        if (!filtroMalaDiretaGeral.getFiltroImovel().getAtivo()) {
            where += " and COALESCE(cadastro.ativo,1) = 0 ";
        } else {
            where += " and COALESCE(cadastro.ativo,1) = 1 ";
        }

        where = montarFiltroDividas(filtroMalaDiretaGeral, parameters, where);
        Query q = em.createNativeQuery(sql + where);
        for (String parametro : parameters.keySet()) {
            q.setParameter(parametro, parameters.get(parametro));
        }
        List<ImpressaoMalaDiretaGeral> toReturn = montarImpressaoCadastro(q);
        return toReturn;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<ImpressaoMalaDiretaGeral> listaImpressaoMalaDireta(List<Long> listaIdsDosItens, MalaDiretaGeral malaDiretaGeral) {
        Template templateCorpo = createTemplate(malaDiretaGeral.getTexto());
        Template templateCabecalho = createTemplate(malaDiretaGeral.getCabecalho());
        String sql = "SELECT itemmala.id, " +
            "  '' AS texto, " +
            "  COALESCE(pf.nome,pj.razaosocial) AS contribuinte, " +
            "  COALESCE(pf.cpf, pj.cnpj) AS cpfcnpj, " +
            "  COALESCE(ci.inscricaocadastral, ce.inscricaocadastral) AS cadastro, " +
            "  dam.vencimento, " +
            "  dam.numerodam, " +
            "  dam.VALORORIGINAL, " +
            "  dam.JUROS, " +
            "  dam.MULTA, " +
            "  dam.CORRECAOMONETARIA, " +
            "  dam.HONORARIOS, " +
            "  dam.CODIGOBARRAS codigobarrasdigitos, " +
            "  (SUBSTR(dam.codigoBarras, 0, 11) " +
            "  || SUBSTR(dam.codigoBarras, 15, 11) " +
            "  || SUBSTR(dam.codigoBarras, 29, 11) " +
            "  || SUBSTR(dam.codigoBarras, 43, 11)) AS codigobarras, " +
            "  (SELECT regexp_replace(LISTAGG(divida.descricao, ',') WITHIN GROUP ( " +
            "    ORDER BY divida.descricao), '([^,]+)(,\\1)*(,|$)', '\\1\\3') " +
            "     FROM itemdam " +
            "    INNER JOIN parcelavalordivida pvd ON pvd.id =itemdam.parcela_id " +
            "    INNER JOIN valordivida vd ON vd.id = pvd.valordivida_id " +
            "    INNER JOIN divida ON divida.id = vd.divida_id " +
            "    WHERE itemdam.dam_id = dam.id) AS dividas, " +
            "  COALESCE(endbci.LOGRADOURO,endbce.LOGRADOURO)   AS logradouro_cadastro, " +
            "  COALESCE(endbci.bairro,endbce.bairro)           AS bairro_cadastro, " +
            "  COALESCE(endbci.numero,endbce.numero)           AS numero_endereco_cadastro, " +
            "  COALESCE(endbci.complemento,endbce.complemento) AS complemento_endereco_cadastro, " +
            "  COALESCE(endbci.cep,endbce.cep)                 AS cep_cadastro, " +
            "  endbci.lote, " +
            "  endbci.quadra, " +
            "  endbci.setor, " +
            "  endpessoa.LOGRADOURO   AS logradouro_pessoa, " +
            "  endpessoa.bairro       AS bairro_pessoa, " +
            "  endpessoa.numero       AS numero_endereco_pessoa, " +
            "  endpessoa.complemento  AS complemento_endereco_pessoa, " +
            "  endpessoa.cep          AS cep_pessoa, " +
            "  endpessoa.localidade   AS localidade_pessoa, " +
            "  endpessoa.uf           AS uf_pessoa, " +
            "  endpessoa.TIPOENDERECO AS tipo_endereco_pessoa, " +
            "  pessoa.email AS email_pessoa, " +
            "  sitce.situacaocadastral, " +
            "  ce.abertura, " +
            "  COALESCE(pfce.nome, pjce.razaosocial), " +
            "  COALESCE(pfce.nome, pj.nomefantasia)," +
            "  dam.qrcodepix, dam.id as idDam, coalesce(dam.desconto) as descontoDam " +
            "FROM ItemMalaDiretaGeral itemMala " +
            "INNER JOIN MalaDiretaGeral mala ON mala.id = itemMala.malaDiretaGeral_id " +
            "LEFT JOIN ParametroMalaDireta parametro ON parametro.id = mala.parametroMalaDireta_id " +
            "LEFT JOIN Cadastro ON cadastro.id = itemMala.cadastro_id " +
            "LEFT JOIN CadastroImobiliario ci ON ci.id = cadastro.id " +
            "LEFT JOIN VWENDERECOBCI endbci ON endbci.cadastroImobiliario_id = ci.id " +
            "LEFT JOIN CadastroEconomico ce ON ce.id = cadastro.id " +
            "LEFT JOIN CE_SituacaoCadastral cesit ON cesit.cadastroEconomico_id = ce.id " +
            "LEFT JOIN SituacaoCadastroEconomico sitCe ON sitce.id = cesit.situacaoCadastroEconomico_id " +
            "LEFT JOIN PessoaFisica pfCe ON pfce.id = ce.pessoa_id " +
            "LEFT JOIN PessoaJuridica pjCe ON pjce.id = ce.pessoa_id " +
            "LEFT JOIN VWENDERECOBCE endbce ON endbce.CADASTROECONOMICO_ID = ce.id " +
            "LEFT JOIN Pessoa ON pessoa.id = itemMala.pessoa_id " +
            "LEFT JOIN VWENDERECOPESSOA endpessoa ON endpessoa.PESSOA_ID = PESSOA.id " +
            "LEFT JOIN PessoaFisica pf ON pf.id = pessoa.id " +
            "LEFT JOIN PessoaJuridica pj ON pj.id = pessoa.id " +
            "LEFT JOIN Dam ON dam.id = itemmala.dam_id " +
            "WHERE ((sitce.dataregistro = (SELECT MAX(sitce2.dataregistro) FROM SituacaoCadastroEconomico sitce2 " +
            "      INNER JOIN CE_SituacaoCadastral cesit2 ON cesit2.situacaocadastroeconomico_id = sitce2.id " +
            "      WHERE cesit2.cadastroeconomico_id = ce.id) AND ce.id IS NOT NULL) OR ce.id IS NULL)";

        if (listaIdsDosItens != null && !listaIdsDosItens.isEmpty()) {
            sql += " AND itemmala.id IN :listaIds ";
        } else {
            sql += " AND mala.id = :idMala ";
        }

        Query q = em.createNativeQuery(sql);
        if (listaIdsDosItens != null && !listaIdsDosItens.isEmpty()) {
            q.setParameter("listaIds", listaIdsDosItens);
        } else {
            q.setParameter("idMala", malaDiretaGeral.getId());
        }
        List<Object[]> lista = q.getResultList();
        List<ImpressaoMalaDiretaGeral> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            ImpressaoMalaDiretaGeral impressao = new ImpressaoMalaDiretaGeral();
            impressao.setId(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
            impressao.setTexto(obj[1] != null ? ((String) obj[1]) : "");
            impressao.setNomeContribuinte(obj[2] != null ? ((String) obj[2]) : "");
            impressao.setCpfContribuinte(obj[3] != null ? ((String) obj[3]) : "");
            impressao.setNumeroCadastro(obj[4] != null ? ((String) obj[4]) : "");
            impressao.setVencimentoDam(obj[5] != null ? ((Date) obj[5]) : null);
            impressao.setNumeroDam(obj[6] != null ? ((String) obj[6]) : null);
            impressao.setValorOriginalDam(obj[7] != null ? ((BigDecimal) obj[7]) : BigDecimal.ZERO);
            impressao.setJutosDam(obj[8] != null ? ((BigDecimal) obj[8]) : BigDecimal.ZERO);
            impressao.setMultaDam(obj[9] != null ? ((BigDecimal) obj[9]) : BigDecimal.ZERO);
            impressao.setCorrecaoDam(obj[10] != null ? ((BigDecimal) obj[10]) : BigDecimal.ZERO);
            impressao.setHonorariosDam(obj[11] != null ? ((BigDecimal) obj[11]) : BigDecimal.ZERO);
            impressao.setCodigoBarrasDigitosDam(obj[12] != null ? ((String) obj[12]) : "");
            impressao.setCodigoBarrasDam(obj[13] != null ? ((String) obj[13]) : "");
            impressao.setDividasDam(obj[14] != null ? ((String) obj[14]) : "");
            impressao.setLogradouroCadastro(obj[15] != null ? ((String) obj[15]) : "");
            impressao.setBairroCadastro(obj[16] != null ? ((String) obj[16]) : "");
            impressao.setNumeroEnderecoCadastro(obj[17] != null ? ((String) obj[17]) : "");
            impressao.setComplementoEnderecoCadastro(obj[18] != null ? ((String) obj[18]) : "");
            impressao.setCepCadastro(obj[19] != null ? ((String) obj[19]) : "");
            impressao.setLoteCadastroImobiliario(obj[20] != null ? ((String) obj[20]) : "");
            impressao.setQuadraCadastroImobiliario(obj[21] != null ? ((String) obj[21]) : "");
            impressao.setSetorCadastroImobiliario(obj[22] != null ? ((String) obj[22]) : "");
            impressao.setLogradouroContribuinte(obj[23] != null ? ((String) obj[23]) : "");
            impressao.setBairroContribuinte(obj[24] != null ? ((String) obj[24]) : "");
            impressao.setNumeroEnderecoContribuinte(obj[25] != null ? ((String) obj[25]) : "");
            impressao.setComplementoEnderecoCadastro(obj[26] != null ? ((String) obj[26]) : "");
            impressao.setCepContribuinte(obj[27] != null ? ((String) obj[27]) : "");
            impressao.setCidadeContribuinte(obj[28] != null ? ((String) obj[28]) : "");
            impressao.setUfContribuinte(obj[29] != null ? ((String) obj[29]) : "");
            impressao.setTipoEnderecoContribuinte(obj[30] != null ? ((String) obj[30]) : "");
            impressao.setEmailContribuinte(obj[31] != null ? ((String) obj[31]) : "");
            impressao.setSituacaoCadastralCadastroEconomico(obj[32] != null ? ((String) obj[32]) : "");
            impressao.setDataAberturaCadastroEconomico(obj[33] != null ? ((Date) obj[33]) : null);
            impressao.setRazaoSocialCadastroEconomico(obj[34] != null ? ((String) obj[34]) : "");
            impressao.setNomeFantasiaCadastroEconomico(obj[35] != null ? ((String) obj[35]) : "");
            impressao.setQrCodePix(obj[36] != null ? ((String) obj[36]) : null);
            impressao.setIdDam(obj[37] != null ? (((BigDecimal) obj[37]).longValue()) : null);
            impressao.setDescontoDam(obj[38] != null ? (((BigDecimal) obj[38])) : BigDecimal.ZERO);

            impressao.setCabecalho(completarConteudo(trocarTags(templateCabecalho, impressao)));
            impressao.setTexto(completarConteudo(trocarTags(templateCorpo, impressao)));

            retorno.add(impressao);
        }
        return retorno;
    }

    public String completarConteudo(String corpo) {
        String conteudo = " <html>"
            + " <head>"
            + "     <title>WebPúblico</title>"
            + " </head>"
            + " <body>";

        conteudo += FacesUtil.alteraURLImagens(corpo);
        conteudo += " </body></html>";
        return conteudo;
    }

    public List<Long> buscarIdsDosDamDaMalaDireta(MalaDiretaGeral malaDiretaGeral) {
        String sql = "SELECT dam_id FROM ItemMalaDiretaGeral " +
            " WHERE malaDiretaGeral_id = :idMalaDireta";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMalaDireta", malaDiretaGeral.getId());
        return q.getResultList();
    }

    public InputStream getImagemInputStream(String caminhoImagem) {
        try {
            String pathImagem = caminhoImagem + "MalaDireta_Geral.png";
            File imagem = new File(pathImagem);
            FileInputStream fileInputStream = new FileInputStream(imagem);
            return fileInputStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteImpressaoMalaDiretaGeral> imprimirDamsMalaDireta(AssistenteImpressaoMalaDiretaGeral assistente, List<ImpressaoMalaDiretaGeral> lista, int numFuture, UsuarioSistema usuarioSistema, String caminhoReport, String caminhoImagem, Long idMalaDireta, String pastaMalaDireta, Exercicio exercicio) throws JRException, IOException {
        String arquivoJasper = "DAM_MALA_DIRETA_GERAL";
        AbstractReport report = AbstractReport.getAbstractReport();

        List<List<ImpressaoMalaDiretaGeral>> particoes = Lists.partition(lista, 500);
        int qtdePartes = 0;
        for (List<ImpressaoMalaDiretaGeral> parte : particoes) {
            List<DAM> dans = buscarDansDaMalaDireta(parte);
            pixFacade.gerarQrCodePIX(dans);

            qtdePartes++;
            String nomeDoArquivo = pastaMalaDireta + arquivoJasper +
                "_" + idMalaDireta + "_" +
                StringUtil.preencheString(numFuture + "", 2, '0') +
                StringUtil.preencheString(qtdePartes + "", 5, '0') + ".pdf";

            InputStream imagem = getImagemInputStream(caminhoImagem);
            try {
                HashMap parameters = new HashMap<>();
                parameters.put("USUARIO", usuarioSistema.getLogin());
                parameters.put("BRASAO", caminhoImagem);
                parameters.put("IMAGEM_FUNDO", imagem);
                parameters.put("HOMOLOGACAO", getServiceDAM().isAmbienteHomologacao());
                parameters.put("MSG_PIX", "Pagamento Via QrCode PIX");
                JasperPrint print = report.gerarBytesDeRelatorioComDadosEmCollectionView(caminhoReport,
                    arquivoJasper + ".jasper", parameters, new JRBeanCollectionDataSource(parte));
                OutputStream output = new FileOutputStream(new File(nomeDoArquivo));
                JasperExportManager.exportReportToPdfStream(print, output);
                output.close();
            } catch (Exception e) {
                logger.error("Erro ao gerar o arquivo " + nomeDoArquivo + ": {}", e);
            }
            assistente.contar(parte.size());
            imagem.close();
        }
        return new AsyncResult<>(new AssistenteImpressaoMalaDiretaGeral());
    }

    public List<DAM> buscarDansDaMalaDireta(List<ImpressaoMalaDiretaGeral> impressoes) {
        List<DAM> dans = Lists.newArrayList();
        for (ImpressaoMalaDiretaGeral impressao : impressoes) {
            DAM dam = new DAM();
            dam.setId(impressao.getIdDam());
            dam.setVencimento(impressao.getVencimentoDam());
            dam.setCodigoBarras(impressao.getCodigoBarrasDigitosDam());
            dam.setValorOriginal(impressao.getValorOriginalDam());
            dam.setHonorarios(impressao.getHonorariosDam());
            dam.setJuros(impressao.getJutosDam());
            dam.setMulta(impressao.getMultaDam());
            dam.setCorrecaoMonetaria(impressao.getCorrecaoDam());
            dam.setDesconto(impressao.getDescontoDam());

            dans.add(dam);
        }
        return dans;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public JdbcMalaDiretaGeral getDaoMalaDiretaGeral() {
        if (daoMalaDiretaGeral == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            daoMalaDiretaGeral = (JdbcMalaDiretaGeral) ap.getBean("malaDiretaGeralDAO");
        }
        return daoMalaDiretaGeral;
    }

    public JdbcDamDAO getDaoDAM() {
        if (daoDAM == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            daoDAM = (JdbcDamDAO) ap.getBean("damDAO");
        }
        return daoDAM;
    }

    public ServiceDAM getServiceDAM() {
        if(serviceDAM == null) {
            serviceDAM = (ServiceDAM) Util.getSpringBeanPeloNome("serviceDAM");
        }
        return serviceDAM;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    public Future<Map<ContribuinteTributario, List<ResultadoParcela>>> buscarDebitosDaMalaDiretaDe(
        FiltroMalaDiretaGeral filtroMalaDiretaGeral,
        List<ImpressaoMalaDiretaGeral> idsCadastro) {
        Map<ContribuinteTributario, List<ResultadoParcela>> toReturn = Maps.newHashMap();

        List<Long> dividas = Lists.newArrayList();
        for (Divida divida : filtroMalaDiretaGeral.getDividas()) {
            dividas.add(divida.getId());
        }

        for (ImpressaoMalaDiretaGeral idCadastro : idsCadastro) {
            ConsultaParcela consultaParcela = new ConsultaParcela();
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO,
                ConsultaParcela.Operador.MAIOR_IGUAL, filtroMalaDiretaGeral.getExercicioInicial().getAno());
            consultaParcela.addParameter(ConsultaParcela.Campo.EXERCICIO_ANO,
                ConsultaParcela.Operador.MENOR_IGUAL, filtroMalaDiretaGeral.getExercicioFinal().getAno());
            if (filtroMalaDiretaGeral.getTipoCadastroTributario().isPessoa()) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PESSOA_ID, ConsultaParcela.Operador.IGUAL, idCadastro.getId());
            } else {
                consultaParcela.addParameter(ConsultaParcela.Campo.CADASTRO_ID, ConsultaParcela.Operador.IGUAL, idCadastro.getId());
            }
            consultaParcela.addParameter(ConsultaParcela.Campo.DIVIDA_ID, ConsultaParcela.Operador.IN, dividas);
            consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, SituacaoParcela.EM_ABERTO);
            if (filtroMalaDiretaGeral.getVencimentoInicial() != null) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO,
                    ConsultaParcela.Operador.MAIOR_IGUAL, filtroMalaDiretaGeral.getVencimentoInicial());
            }
            if (filtroMalaDiretaGeral.getVencimentoFinal() != null) {
                consultaParcela.addParameter(ConsultaParcela.Campo.PARCELA_VENCIMENTO,
                    ConsultaParcela.Operador.MENOR_IGUAL, filtroMalaDiretaGeral.getVencimentoFinal());
            }
            consultaParcela.addParameter(ConsultaParcela.Campo.PROMOCIAL, ConsultaParcela.Operador.DIFERENTE, 1);
            List<ResultadoParcela> parcelas = consultaParcela.executaConsulta().getResultados();
            toReturn.put(new ContribuinteTributario(idCadastro.getId(), idCadastro.getIdPessoa(), filtroMalaDiretaGeral.getTipoCadastroTributario()), parcelas);
        }
        return new AsyncResult<>(toReturn);
    }


    private List<ParcelaMalaDiretaGeral> criarParcelasMalaDiretaIPTU(ItemMalaDiretaGeral itemMalaDiretaGeral, List<ResultadoParcela> debitos) {
        List<ParcelaMalaDiretaGeral> toReturn = Lists.newArrayList();
        for (ResultadoParcela resultadoParcela : debitos) {
            ParcelaMalaDiretaGeral parcela = new ParcelaMalaDiretaGeral();
            parcela.setItemMalaDiretaGeral(itemMalaDiretaGeral);
            parcela.setParcela(em.find(ParcelaValorDivida.class, resultadoParcela.getIdParcela()));
            toReturn.add(parcela);
        }

        return toReturn;
    }

    public BigDecimal getValorTotal(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorTotal());
        }

        return toReturn;
    }

    public BigDecimal getValorOriginal(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorOriginal());
        }

        return toReturn;
    }

    public BigDecimal getValorJuros(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorJuros());
        }

        return toReturn;
    }

    public BigDecimal getValorMulta(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorMulta());
        }

        return toReturn;
    }

    public BigDecimal getValorCorrecao(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorCorrecao());
        }

        return toReturn;
    }

    public BigDecimal getValorDesconto(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorDesconto());
        }

        return toReturn;
    }

    public BigDecimal getValorHonorarios(List<ResultadoParcela> parcelas) {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelas) {
            toReturn = toReturn.add(parcela.getValorHonorarios());
        }

        return toReturn;
    }

    public DAM criarDamCadastroMalaDireta(List<ResultadoParcela> parcelas, Exercicio exercicio, ConfiguracaoDAM configuracaoDAM, Date vencimento, UsuarioSistema usuario, DAM.Tipo tipoDam, boolean gerarHistorico) {
        return DAM.Tipo.UNICO.equals(tipoDam) ? damFacade.gerarDAMUnicoViaApi(usuario, parcelas.get(0)) :
                damFacade.gerarDAMCompostoViaApi(usuario, parcelas, vencimento);
    }

    private List<TributoParcela> buscarItensParcela(Long idParcela) {

        String sql = "select tr.id, item.valor " +
            " from ItemParcelaValorDivida item " +
            " inner join itemvalordivida ivd on ivd.id = item.itemvalordivida_id " +
            " inner join tributo tr on tr.id = ivd.tributo_id " +
            " where item.parcelaValorDivida_id = :idParcela";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", idParcela);
        List<Object[]> result = q.getResultList();
        List<TributoParcela> toReturn = Lists.newArrayList();
        for (Object[] obj : result) {
            toReturn.add(new TributoParcela((BigDecimal) obj[0], (BigDecimal) obj[1]));
        }
        return toReturn;
    }

    public ItemMalaDiretaGeral gerarItemMalaDireta(MalaDiretaGeral malaDireta,
                                                   ContribuinteTributario contribuinteTributario,
                                                   List<ResultadoParcela> debitosDoCadastro, Date vencimento, UsuarioSistema usuario) {

        ItemMalaDiretaGeral item = new ItemMalaDiretaGeral();
        item.setMalaDiretaGeral(malaDireta);
        List<ParcelaMalaDiretaGeral> parcelas = criarParcelasMalaDiretaIPTU(item, debitosDoCadastro);
        item.setParcelas(parcelas);
        item.setContribuinteTributario(contribuinteTributario);
        item.setTexto(malaDireta.getTexto());
        if (malaDireta.getTipoMalaDireta().isCobranca()) {
            DAM dam = criarDamCadastroMalaDireta(debitosDoCadastro,
                malaDireta.getExercicio(),
                malaDireta.getConfiguracaoDAM(),
                vencimento, usuario, DAM.Tipo.COMPOSTO, false);
            item.setDam(dam);
        }
        getDaoMalaDiretaGeral().persisteCadastroMalaDiretaIptu(item);
        return item;
    }

    public String trocarTags(Template template, ImpressaoMalaDiretaGeral impressao) {
        VelocityContext context = new VelocityContext();

        addTag(context, TagMalaDireta.CONTRIBUINTE_NOME.name(), impressao.getNomeContribuinte());
        addTag(context, TagMalaDireta.CONTRIBUINTE_CPF_CNPJ.name(), impressao.getCpfContribuinte());
        addTag(context, TagMalaDireta.CONTRIBUINTE_LOGRADOURO.name(), impressao.getLogradouroContribuinte());
        addTag(context, TagMalaDireta.CONTRIBUINTE_BAIRRO.name(), impressao.getBairroContribuinte());
        addTag(context, TagMalaDireta.CONTRIBUINTE_NUMERO_ENDERECO.name(), impressao.getNumeroEnderecoContribuinte());
        addTag(context, TagMalaDireta.CONTRIBUINTE_COMPLEMENTO_ENDERECO.name(), "");
        addTag(context, TagMalaDireta.CONTRIBUINTE_CEP.name(), impressao.getCepContribuinte());
        addTag(context, TagMalaDireta.CONTRIBUINTE_CIDADE.name(), impressao.getCidadeContribuinte());
        addTag(context, TagMalaDireta.CONTRIBUINTE_UF.name(), impressao.getUfContribuinte());
        addTag(context, TagMalaDireta.CONTRIBUINTE_TIPO_ENDERECO.name(), impressao.getTipoEnderecoContribuinte());
        addTag(context, TagMalaDireta.CADASTRO_NUMERO.name(), impressao.getNumeroCadastro());
        addTag(context, TagMalaDireta.CADASTRO_LOGRADOURO.name(), impressao.getLogradouroCadastro());
        addTag(context, TagMalaDireta.CADASTRO_BAIRRO.name(), impressao.getBairroCadastro());
        addTag(context, TagMalaDireta.CADASTRO_NUMERO_ENDERECO.name(), impressao.getNumeroEnderecoCadastro());
        addTag(context, TagMalaDireta.CADASTRO_COMPLEMENTO_ENDERECO.name(), impressao.getComplementoEnderecoCadastro());
        addTag(context, TagMalaDireta.CADASTRO_CEP.name(), impressao.getCepCadastro());
        addTag(context, TagMalaDireta.CADASTRO_IMOBILIARIO_LOTE.name(), impressao.getLoteCadastroImobiliario());
        addTag(context, TagMalaDireta.CADASTRO_IMOBILIARIO_SETOR.name(), impressao.getSetorCadastroImobiliario());
        addTag(context, TagMalaDireta.CADASTRO_IMOBILIARIO_QUADRA.name(), impressao.getQuadraCadastroImobiliario());
        addTag(context, TagMalaDireta.CADASTRO_ECONOMICO_SITUACAO_CADASTRAL.name(), impressao.getSituacaoCadastralCadastroEconomico());
        addTag(context, TagMalaDireta.CADASTRO_ECONOMICO_DATA_ABERTURA.name(), DataUtil.getDataFormatada(impressao.getDataAberturaCadastroEconomico()));
        addTag(context, TagMalaDireta.CADASTRO_ECONOMICO_RAZAO_SOCIAL.name(), impressao.getRazaoSocialCadastroEconomico());
        addTag(context, TagMalaDireta.CADASTRO_ECONOMICO_NOME_FANTASIA.name(), impressao.getNomeFantasiaCadastroEconomico());
        addTag(context, TagMalaDireta.DAM_VENCIMENTO.name(), DataUtil.getDataFormatada(impressao.getVencimentoDam()));
        addTag(context, TagMalaDireta.DAM_NUMERO.name(), impressao.getNumeroDam());
        addTag(context, TagMalaDireta.DAM_VALOR_ORIGINAL.name(), Util.formataValor(impressao.getValorOriginalDam()));
        addTag(context, TagMalaDireta.DAM_JUROS.name(), Util.formataValor(impressao.getJutosDam()));
        addTag(context, TagMalaDireta.DAM_MULTA.name(), Util.formataValor(impressao.getMultaDam()));
        addTag(context, TagMalaDireta.DAM_CORRECAO_MONETARIA.name(), Util.formataValor(impressao.getCorrecaoDam()));
        addTag(context, TagMalaDireta.DAM_HONORARIOS.name(), Util.formataValor(impressao.getHonorariosDam()));
        addTag(context, TagMalaDireta.DAM_CODIGO_BARRAS_DIGITOS.name(), impressao.getCodigoBarrasDigitosDam());
        addTag(context, TagMalaDireta.DAM_CODIGO_BARRAS.name(), impressao.getCodigoBarrasDam());
        addTag(context, TagMalaDireta.DAM_DIVIDAS.name(), impressao.getDividasDam());

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private void addTag(VelocityContext contexto, String chave, Object valor) {
        if (valor != null) {
            contexto.put(chave, valor);
        } else {
            contexto.put(chave, "");
        }
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 10)
    public Future<List<ItemMalaDiretaGeral>> criarItemMalaDireta(
        MalaDiretaGeral malaDiretaGeral,
        Map<ContribuinteTributario, List<ResultadoParcela>> mapaParcelasPorCadastro,
        AssistenteBarraProgresso assistenteBarraProgresso, UsuarioSistema usuario) {

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH));
        Date vencimento = DataUtil.ultimoDiaUtil(c, feriadoFacade).getTime();

        List<ItemMalaDiretaGeral> toReturn = Lists.newArrayList();
        for (ContribuinteTributario contribuinteTributario : mapaParcelasPorCadastro.keySet()) {
            List<ResultadoParcela> debitos = mapaParcelasPorCadastro.get(contribuinteTributario);
            ItemMalaDiretaGeral item = gerarItemMalaDireta(malaDiretaGeral, contribuinteTributario, debitos, vencimento, usuario);
            toReturn.add(item);
            assistenteBarraProgresso.conta();
        }

        return new AsyncResult<>(toReturn);
    }

    public Template createTemplate(String texto) {
        try {
            Template template = new Template();
            RuntimeServices runtimeServices = RuntimeSingleton.getRuntimeServices();
            StringReader reader = new StringReader(texto);
            SimpleNode node = runtimeServices.parse(reader, "Template name");
            template.setRuntimeServices(runtimeServices);
            template.setData(node);
            template.initDocument();
            return template;
        } catch (Exception e) {
            logger.error("Não gerou o template: {}", e);
            return null;
        }
    }

    public MalaDiretaGeral salvarRetornando(MalaDiretaGeral malaDiretaGeral) {
        return em.merge(malaDiretaGeral);
    }

    public Integer buscarQuantidadeDeCadastrosNaMalaDireta(MalaDiretaGeral malaDiretaGeral) {
        String sql = " SELECT count(1) FROM ItemMalaDiretaGeral item " +
            " WHERE item.malaDiretaGeral_id = :id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", malaDiretaGeral.getId());
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getSingleResult()).intValue();
        }
        return 0;
    }

    public int buscarQuantidadeCadastrosDaMalaDireta(MalaDiretaGeral malaDireta, FiltroMalaDiretaGeral filtro) {
        Map<String, Object> parameters = Maps.newHashMap();
        String sql = " SELECT count(1) FROM itemmaladiretageral cmi " +
            "   LEFT JOIN cadastroimobiliario ci ON ci.id = cmi.cadastro_id " +
            "   LEFT JOIN cadastroeconomico ce ON ce.id = cmi.cadastro_id " +

            "   LEFT JOIN pessoa ON pessoa.id = cmi.pessoa_id " +
            " WHERE cmi.maladiretageral_id = :maladireta_id ";
        parameters.put("maladireta_id", malaDireta.getId());
        if (!Strings.isNullOrEmpty(filtro.getFiltroImovel().getInscricaoIncial())) {
            String cadstro = malaDireta.getTipoCadastro().isEconomico() ? "ce" : "ci";
            sql += " and " + cadstro + ".inscricaocadastral >= :inscricao_inicial ";
            parameters.put("inscricao_inicial", filtro.getFiltroImovel().getInscricaoIncial());
        }
        if (!Strings.isNullOrEmpty(filtro.getFiltroImovel().getInscricaoFinal())) {
            String cadstro = malaDireta.getTipoCadastro().isEconomico() ? "ce" : "ci";
            sql += " and " + cadstro + ".inscricaocadastral <= :inscricao_final ";
            parameters.put("inscricao_final", filtro.getFiltroImovel().getInscricaoFinal());
        }
        Query q = em.createNativeQuery(sql);
        for (String param : parameters.keySet()) {
            q.setParameter(param, parameters.get(param));
        }
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getSingleResult()).intValue();
        }
        return 0;
    }


    public List<ItemMalaDiretaGeral> buscarCadastrosDaMalaDireta(MalaDiretaGeral malaDireta,
                                                                 FiltroMalaDiretaGeral filtro,
                                                                 int indexInicial, int quantidade) {
        Map<String, Object> parameters = Maps.newHashMap();

        String sql = " select cmi.* FROM itemmaladiretageral cmi " +
            "   LEFT JOIN cadastroimobiliario ci ON ci.id = cmi.cadastro_id " +
            "   LEFT JOIN cadastroeconomico ce ON ce.id = cmi.cadastro_id " +

            "   LEFT JOIN pessoa ON pessoa.id = cmi.pessoa_id " +
            " WHERE cmi.maladiretageral_id = :maladireta_id ";
        parameters.put("maladireta_id", malaDireta.getId());
        if (!Strings.isNullOrEmpty(filtro.getFiltroImovel().getInscricaoIncial())) {
            String cadstro = malaDireta.getTipoCadastro().isEconomico() ? "ce" : "ci";
            sql += " and " + cadstro + ".inscricaocadastral >= :inscricao_inicial ";
            parameters.put("inscricao_inicial", filtro.getFiltroImovel().getInscricaoIncial());
        }
        if (!Strings.isNullOrEmpty(filtro.getFiltroImovel().getInscricaoFinal())) {
            String cadstro = malaDireta.getTipoCadastro().isEconomico() ? "ce" : "ci";
            sql += " and " + cadstro + ".inscricaocadastral <= :inscricao_final ";
            parameters.put("inscricao_final", filtro.getFiltroImovel().getInscricaoFinal());
        }
        Query q = em.createNativeQuery(sql, ItemMalaDiretaGeral.class);
        for (String param : parameters.keySet()) {
            q.setParameter(param, parameters.get(param));
        }
        q.setFirstResult(indexInicial);
        q.setMaxResults(quantidade);
        return q.getResultList();
    }

    public ItemMalaDiretaGeral recuperarItemMalaDireta(Long id) {
        return em.find(ItemMalaDiretaGeral.class, id);
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }


    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteImpressaoMalaDiretaGeral> envarEmail(List<ImpressaoMalaDiretaGeral> lista,
                                                                 UsuarioSistema usuarioSistema,
                                                                 String caminhoReport,
                                                                 String caminhoImagem,
                                                                 String assunto) {
        String arquivoJasper = "DAM_MALA_DIRETA_GERAL";
        AbstractReport report = AbstractReport.getAbstractReport();
        List<DAM> dans = buscarDansDaMalaDireta(lista);
        pixFacade.gerarQrCodePIX(dans);

        for (ImpressaoMalaDiretaGeral impressao : lista) {
            try {
                InputStream imagem = getImagemInputStream(caminhoImagem);

                HashMap parameters = new HashMap<>();
                parameters.put("USUARIO", usuarioSistema.getLogin());
                parameters.put("BRASAO", caminhoImagem);
                parameters.put("IMAGEM_FUNDO", imagem);
                parameters.put("HOMOLOGACAO", getServiceDAM().isAmbienteHomologacao());
                parameters.put("MSG_PIX", "Pagamento Via QrCode PIX");
                JasperPrint print = report.gerarBytesDeRelatorioComDadosEmCollectionView(caminhoReport,
                    arquivoJasper + ".jasper", parameters, new JRBeanCollectionDataSource(Lists.newArrayList(impressao)));
                ByteArrayOutputStream out = report.exportarJasperParaPDF(print);
                EmailService.getInstance().enviarEmail(impressao.getEmailContribuinte(),
                    assunto, impressao.getTexto(), out);
                out.close();
                imagem.close();
            } catch (Exception e) {
                logger.error("Erro ao gerar o arquivo: {}", e);
            }
        }
        return new AsyncResult<>(new AssistenteImpressaoMalaDiretaGeral());
    }

    public void lancarNotificacoes() {
        Query q = em.createQuery("select mala from MalaDiretaGeral mala where mala.vencimento < current_date");
        for (MalaDiretaGeral mala : (List<MalaDiretaGeral>) q.getResultList()) {
            String mensagem = "Mala direta vencida!";
            String link = "/mala-direta/ver/" + mala.getId();
            NotificacaoService.getService().notificar(TipoNotificacao.MALA_DIRETA_GERAL.getDescricao(),
                mensagem, link, Notificacao.Gravidade.ATENCAO, TipoNotificacao.MALA_DIRETA_GERAL);
        }
    }

    public class TributoParcela {
        BigDecimal idTributo;
        BigDecimal valor;

        public TributoParcela(BigDecimal idTributo, BigDecimal valor) {
            this.idTributo = idTributo;
            this.valor = valor;

        }
    }

}
