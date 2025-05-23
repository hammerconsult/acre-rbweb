package br.com.webpublico.negocios;

import br.com.webpublico.controle.CertidaoDividaAtivaControlador;
import br.com.webpublico.controle.ConsultaCertidaoDividaAtivaControlador;
import br.com.webpublico.controle.ConsultaCertidaoDividaAtivaNaoComunicadasControlador;
import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.VOItemCertidaoDividaAtiva;
import br.com.webpublico.entidadesauxiliares.VOProcessoParcelamento;
import br.com.webpublico.entidadesauxiliares.ValoresAtualizadosCDA;
import br.com.webpublico.entidadesauxiliares.VoExportacaoCertidaoDividaAtiva;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.negocios.tributario.consultaparcela.CalculadorAcrescimos;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.UltimoLinhaDaPaginaDoLivroDividaAtiva;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.procuradoria.IntegraSoftplanFacade;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Stateless
public class CertidaoDividaAtivaFacade extends AbstractFacade<CertidaoDividaAtiva> {

    private static final Logger logger = LoggerFactory.getLogger(CertidaoDividaAtivaFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private InscricaoDividaAtivaFacade inscricaoDividaAtivaFacade;
    @EJB
    private ParametrosDividaAtivaFacade parametrosDividaAtivaFacade;
    @EJB
    private PeticaoFacade peticaoFacade;
    @EJB
    private ProcessoParcelamentoFacade processoParcelamentoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private PagamentoJudicialFacade pagamentoJudicialFacade;
    @EJB
    private CancelamentoParcelamentoFacade cancelamentoParcelamentoFacade;
    @EJB
    private IntegraSoftplanFacade integraSoftplanFacade;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlanFacade;
    @EJB
    private BloqueioJudicialFacade bloqueioJudicialFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public CertidaoDividaAtivaFacade() {
        super(CertidaoDividaAtiva.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PeticaoFacade getPeticaoFacade() {
        return peticaoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public CadastroImobiliarioFacade getCadastroImobiliarioFacade() {
        return cadastroImobiliarioFacade;
    }

    public CadastroEconomicoFacade getCadastroEconomicoFacade() {
        return cadastroEconomicoFacade;
    }

    public CadastroRuralFacade getCadastroRuralFacade() {
        return cadastroRuralFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public InscricaoDividaAtivaFacade getInscricaoDividaAtivaFacade() {
        return inscricaoDividaAtivaFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public PagamentoJudicialFacade getPagamentoJudicialFacade() {
        return pagamentoJudicialFacade;
    }

    public List<Pessoa> completaPessoa(String parte) {
        return pessoaFacade.listaTodasPessoas(parte.trim());
    }

    public List<CadastroImobiliario> completaCadastroImobiliario(String parte) {
        return cadastroImobiliarioFacade.listaFiltrando(parte.trim(), "inscricaoCadastral");
    }

    public List<CadastroEconomico> completaCadastroEconomico(String parte) {
        return cadastroEconomicoFacade.buscarCadastrosPorInscricaoOrCpfCnpjOrNome(parte.trim());
    }

    public List<CadastroRural> completaCadastroRural(String parte) {
        return cadastroRuralFacade.listaFiltrandoPorCodigo(parte.trim());
    }

    public List<PessoaFisica> completaPessoaFisica(String parte) {
        return this.pessoaFisicaFacade.listaFiltrandoPorTipoPerfil(parte.trim(), PerfilEnum.PERFIL_TRIBUTARIO);
    }

    public ProcessoParcelamentoFacade getProcessoParcelamentoFacade() {
        return processoParcelamentoFacade;
    }

    public List<TipoDoctoOficial> completaTipoDoctoOficial(String parte, TipoCadastroDoctoOficial tipoCadastroDoctoOficial) {
        Query consulta = em.createQuery(" select tipo from TipoDoctoOficial tipo where tipo.moduloTipoDoctoOficial = 'CERTIDADIVIDAATIVA'"
            + " and (lower(tipo.descricao) like :parte or to_char(tipo.codigo) like :parte)"
            + " and tipo.tipoCadastroDoctoOficial = :tipo");
        consulta.setParameter("parte", "%" + parte.toLowerCase() + "%");
        consulta.setParameter("tipo", tipoCadastroDoctoOficial);
        try {
            return (List<TipoDoctoOficial>) consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    @TransactionTimeout(value = 1, unit = TimeUnit.HOURS)
    public CertidaoDividaAtivaControlador.AssistenteCDA recuperarItensInscricaoDividaAtiva(CertidaoDividaAtivaControlador controlador) {
        controlador.getAssistente().setTotal(1);
        String sql = "select item.id as idItem, " +
            "case when pf.id is not null then 'F' else 'J' end as tipoPessoa, " +
            "pessoa.id as idPessoa, " +
            "case " +
            "   when ce.id is not null then 'ECONOMICO'" +
            "   when ci.id is not null then 'IMOBILIARIO' " +
            "   when cr.id is not null then 'RURAL' " +
            "   else 'PESSOA' " +
            "end as tipoCadastro, " +
            "cadastro.id as idCadastro, " +
            "inscricao.id as idProcesso, " +
            "exercicio.id as idExercicio," +
            "coalesce(pf.cpf, pj.cnpj) as cpf_cnpj, " +
            "coalesce(ce.inscricaocadastral, ci.inscricaocadastral, to_char(cr.codigo)) as cadastro, " +
            "exercicio.ano as anoExercicio, " +
            "item.divida_id as idDivida " +
            "from iteminscricaodividaativa item " +
            "inner join calculo on calculo.id = item.id " +
            "inner join valordivida vd on vd.calculo_id = calculo.id " +
            "inner join inscricaodividaativa inscricao on inscricao.id = item.inscricaodividaativa_id " +
            "inner join exercicio on exercicio.id = vd.exercicio_id " +
            "inner join divida on divida.id = item.divida_id " +
            "left join cadastro on cadastro.id = calculo.cadastro_id " +
            "left join cadastroeconomico ce on ce.id = cadastro.id  " +
            "left join cadastroimobiliario ci on ci.id = cadastro.id  " +
            "left join cadastrorural cr on cr.id = cadastro.id  " +
            "left join propriedaderural propRural on propRural.imovel_id = cadastro.id " +
            "left join propriedade propImo on propImo.imovel_id = cadastro.id " +
            "left join pessoa on pessoa.id = coalesce(propImo.pessoa_id, propRural.pessoa_id, ce.pessoa_id, item.pessoa_id) " +
            "left join pessoafisica pf on pf.id = pessoa.id " +
            "left join pessoajuridica pj on pj.id = pessoa.id " +
            "where item.situacao = :situacaoItem " +
            "and inscricao.situacaoinscricaodividaativa = :situacaoInscricaoDividaAtiva " +
            "and not exists (select 1 from itemcertidaodividaativa icda " +
            " inner join certidaodividaativa cda on cda.id = icda.certidao_id and cda.situacaoCertidaoDA = :situacaoCDA " +
            " where icda.iteminscricaodividaativa_id = item.id) " +
            "and (propRural.id is null or propRural.id = (select max(id) from propriedaderural where imovel_id = ci.id)) " +
            "and (propImo.id is null or propImo.id = (select prop.id " +
            "                                           from propriedade prop " +
            "                                                    left join pessoafisica pf on pf.id = prop.PESSOA_ID " +
            "                                                    left join pessoajuridica pj on pj.id = prop.PESSOA_ID " +
            "                                           where (coalesce(trunc(prop.FINALVIGENCIA), trunc(sysdate)) >= trunc(sysdate)) " +
            "                                             and imovel_id = ci.id " +
            "                                           order by coalesce(pf.NOME, pj.RAZAOSOCIAL) fetch first 1 rows only)) ";

        if (controlador.getExercicioInicial() != null && controlador.getExercicioInicial().trim().length() > 0) {
            sql += " and exercicio.ano >=  :exercicioInicial";
        }
        if (controlador.getExercicioFinal() != null && controlador.getExercicioFinal().trim().length() > 0) {
            sql += " and exercicio.ano <= :exercicioFinal";
        }
        if (controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA) && controlador.getPessoa() != null) {
            sql += " and pessoa.id = :idPessoa";
        }
        if (!controlador.getDividasSelecionadas().isEmpty()) {
            sql += " and divida.id in (:idsDivida) ";
        }
        if (controlador.getTipoCadastroTributario() != null) {
            sql += " and inscricao.tipoCadastroTributario = :tipoCadastro";
            if (!controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)
                && controlador.getCadastroInicial() != null && controlador.getCadastroInicial().trim().length() > 0) {

                if (controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.IMOBILIARIO)) {
                    sql += " and ci.inscricaoCadastral >=  :cadastroInicial";
                }
                if (controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.ECONOMICO)) {
                    sql += " and ce.inscricaoCadastral >=  :cadastroInicial";
                }
                if (controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.RURAL)) {
                    sql += " and to_char(cr.codigo) >=  :cadastroInicial";
                }
            }
            if (!controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA) && controlador.getCadastroFinal() != null && controlador.getCadastroFinal().trim().length() > 0) {
                if (controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.IMOBILIARIO)) {
                    sql += " and ci.inscricaoCadastral <=  :cadastroFinal";
                }
                if (controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.ECONOMICO)) {
                    sql += " and ce.inscricaoCadastral <= :cadastroFinal";
                }
                if (controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.RURAL)) {
                    sql += " and to_char(cr.codigo) <=  :cadastroFinal";
                }
            }
        }
        Query consulta = em.createNativeQuery(sql);

        consulta.setParameter("situacaoItem", ItemInscricaoDividaAtiva.Situacao.ATIVO.name());
        consulta.setParameter("situacaoCDA", SituacaoCertidaoDA.ABERTA.name());
        consulta.setParameter("situacaoInscricaoDividaAtiva", SituacaoInscricaoDividaAtiva.FINALIZADO.name());
        if (controlador.getExercicioInicial() != null && controlador.getExercicioInicial().trim().length() > 0) {
            consulta.setParameter("exercicioInicial", controlador.getExercicioInicial());
        }
        if (controlador.getExercicioFinal() != null && controlador.getExercicioFinal().trim().length() > 0) {
            consulta.setParameter("exercicioFinal", controlador.getExercicioFinal());
        }

        if (!controlador.getDividasSelecionadas().isEmpty()) {
            consulta.setParameter("idsDivida", controlador.recuperarIDsDividas());
        }
        if (controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA) && controlador.getPessoa() != null) {
            consulta.setParameter("idPessoa", controlador.getPessoa().getId());
        }
        if (controlador.getTipoCadastroTributario() != null) {
            consulta.setParameter("tipoCadastro", controlador.getTipoCadastroTributario().name());
            if (!controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)
                && controlador.getCadastroInicial() != null
                && controlador.getCadastroInicial().trim().length() > 0) {
                consulta.setParameter("cadastroInicial", controlador.getCadastroInicial().trim());
            }
            if (!controlador.getTipoCadastroTributario().equals(TipoCadastroTributario.PESSOA)
                && controlador.getCadastroFinal() != null && controlador.getCadastroFinal().trim().length() > 0) {
                consulta.setParameter("cadastroFinal", controlador.getCadastroFinal().trim());
            }

        }

        controlador.getAssistente().removerProcessoDoAcompanhamento();
        List<Object[]> lista = consulta.getResultList();
        List<ItemInscricaoDividaAtiva> retorno = Lists.newArrayList();
        for (Object[] obj : lista) {
            try {
                ItemInscricaoDividaAtiva item = new ItemInscricaoDividaAtiva();
                item.setId(((BigDecimal) obj[0]).longValue());

                if (obj[1] != null && obj[1].toString().equals("F")) {
                    PessoaFisica p = new PessoaFisica();
                    p.setId(((BigDecimal) obj[2]).longValue());
                    p.setCpf(obj[7] != null ? obj[7].toString() : "");
                    item.setPessoa(p);

                } else if (obj[1] != null && obj[1].toString().equals("J")) {
                    PessoaJuridica p = new PessoaJuridica();
                    p.setId(((BigDecimal) obj[2]).longValue());
                    p.setCnpj(obj[7] != null ? obj[7].toString() : "");
                    item.setPessoa(p);
                }
                Cadastro cadastro = null;
                TipoCadastroTributario tipoCadastro = TipoCadastroTributario.valueOf(obj[3].toString());
                switch (tipoCadastro) {
                    case IMOBILIARIO:
                        cadastro = new CadastroImobiliario();
                        ((CadastroImobiliario) cadastro).setInscricaoCadastral(obj[8] != null ? obj[8].toString() : "");
                        break;
                    case ECONOMICO:
                        cadastro = new CadastroEconomico();
                        ((CadastroEconomico) cadastro).setInscricaoCadastral(obj[8] != null ? obj[8].toString() : "");
                        break;
                    case RURAL:
                        cadastro = new CadastroRural();
                        ((CadastroRural) cadastro).setCodigo(obj[8] != null ? Long.valueOf(obj[8].toString()) : null);
                        break;
                }

                if (cadastro != null) {
                    cadastro.setId(((BigDecimal) obj[4]).longValue());
                    item.setCadastro(cadastro);
                }

                InscricaoDividaAtiva processo = new InscricaoDividaAtiva();
                processo.setId(((BigDecimal) obj[5]).longValue());

                Exercicio exercicio = new Exercicio();
                exercicio.setId(((BigDecimal) obj[6]).longValue());
                exercicio.setAno(Integer.valueOf(obj[9].toString()));
                processo.setExercicio(exercicio);

                item.setDivida(em.find(Divida.class, ((BigDecimal) obj[10]).longValue()));

                item.setProcessoCalculo(processo);
                item.setInscricaoDividaAtiva(processo);
                retorno.add(item);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        controlador.setItensInscricaoDividaAtivas(retorno);
        controlador.getAssistente().conta();
        return controlador.getAssistenteCDA();
    }

    public UltimoLinhaDaPaginaDoLivroDividaAtiva recuperaLinhaSequenciaNumeroDoLivro(ParcelaValorDivida parcela) {
        Query consulta = em.createNativeQuery("select linha.numerodalinha,linha.sequencia,linha.pagina,livro.numero"
            + " from iteminscricaodividaativa item"
            + " inner join inscricaoDividaParcela insParc on insParc.itemInscricaoDividaAtiva_id = item.id "
            + " inner join linhadolivrodividaativa linha on item.id = linha.iteminscricaodividaativa_id"
            + " inner join itemlivrodividaativa itemlivro on linha.itemlivrodividaativa_id = itemlivro.id"
            + " inner join livrodividaativa livro on itemlivro.livrodividaativa_id = livro.id"
            + " inner join exercicio ex on livro.exercicio_id = ex.id"
            + " where insParc.parcelaValorDivida_id = :parcela");
        consulta.setParameter("parcela", parcela.getId());
        try {
            List<Object[]> listaReturn = consulta.getResultList();
            for (Object[] value : listaReturn) {
                return new UltimoLinhaDaPaginaDoLivroDividaAtiva(((BigDecimal) value[0]).longValue(), ((BigDecimal) value[1]).longValue(), ((BigDecimal) value[2]).longValue(), ((BigDecimal) value[3]).longValue());
            }
        } catch (Exception ex) {
            return new UltimoLinhaDaPaginaDoLivroDividaAtiva(0l, 0l, 0l, 0l);
        }
        return null;
    }

    public UltimoLinhaDaPaginaDoLivroDividaAtiva buscarLinhaSequenciaNumeroDoLivroDoItemInscricao(ItemInscricaoDividaAtiva item) {
        String sql = "select linha.numeroDaLinha, " +
            "                linha.sequencia, " +
            "                linha.pagina, " +
            "                livro.numero " +
            " from LinhaDoLivroDividaAtiva linha " +
            " inner join ItemLivroDividaAtiva item on item.ID = linha.itemLivroDividaAtiva_id " +
            " inner join LivroDividaAtiva livro on livro.id = item.livroDividaAtiva_id " +
            " where linha.itemInscricaoDividaAtiva_id = :idItem";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItem", item.getId());
        q.setMaxResults(1);
        List<Object[]> lista = q.getResultList();
        if (!lista.isEmpty()) {
            Object[] obj = lista.get(0);
            return new UltimoLinhaDaPaginaDoLivroDividaAtiva(
                ((BigDecimal) obj[0]).longValue(),
                ((BigDecimal) obj[1]).longValue(),
                ((BigDecimal) obj[2]).longValue(),
                ((BigDecimal) obj[3]).longValue());
        }
        return new UltimoLinhaDaPaginaDoLivroDividaAtiva(0l, 0l, 0l);
    }

    public String recuperaStringDoLivroDaParcelaValorDivida(ParcelaValorDivida parcela) {
        Query consulta = em.createNativeQuery("select livro.numero || ' - '|| ex.ano || ' ('|| livro.tipocadastrotributario||')' "
            + " from iteminscricaodividaativa item"
            + " inner join inscricaoDividaParcela insParc on insParc.itemInscricaoDividaAtiva_id = item.id "
            + " inner join linhadolivrodividaativa linha on item.id = linha.iteminscricaodividaativa_id"
            + " inner join itemlivrodividaativa itemlivro on linha.itemlivrodividaativa_id = itemlivro.id"
            + " inner join livrodividaativa livro on itemlivro.livrodividaativa_id = livro.id"
            + " inner join exercicio ex on livro.exercicio_id = ex.id"
            + " where insParc.parcelaValorDivida_id = :parcela");
        consulta.setParameter("parcela", parcela.getId());
        List listaReturn = consulta.getResultList();
        if (listaReturn.isEmpty()) {
            return "";
        }
        return (String) listaReturn.get(0);
    }

    public String recuperaStringDoLivroDaParcelaValorDivida(Long idParcela) {
        Query consulta = em.createNativeQuery("select livro.numero || ' - '|| ex.ano || ' ('|| livro.tipocadastrotributario||')' "
            + " from iteminscricaodividaativa item"
            + " inner join inscricaoDividaParcela insParc on insParc.itemInscricaoDividaAtiva_id = item.id "
            + " inner join linhadolivrodividaativa linha on item.id = linha.iteminscricaodividaativa_id"
            + " inner join itemlivrodividaativa itemlivro on linha.itemlivrodividaativa_id = itemlivro.id"
            + " inner join livrodividaativa livro on itemlivro.livrodividaativa_id = livro.id"
            + " inner join exercicio ex on livro.exercicio_id = ex.id"
            + " where insParc.parcelaValorDivida_id = :parcela");
        consulta.setParameter("parcela", idParcela);
        List listaReturn = consulta.getResultList();
        if (listaReturn.isEmpty()) {
            return "";
        }
        return (String) listaReturn.get(0);
    }

    public InscricaoDividaAtiva recuperaInscricaoParcela(ParcelaValorDivida parcela) {
        List<InscricaoDividaAtiva> listReturn = em.createQuery("select ins from InscricaoDividaAtiva ins " +
            " join ins.itensInscricaoDividaAtivas itens " +
            " join itens.itensInscricaoDividaParcelas itensParc" +
            " where itensParc.parcelaValorDivida = :parcela").setParameter("parcela", parcela).getResultList();
        if (!listReturn.isEmpty()) {
            return listReturn.get(0);
        }
        return null;
    }

    public InscricaoDividaAtiva recuperaInscricaoDividaAtiva(Long idParcela) {
        String sql = " select insc.*, pc.* " +
            "         from inscricaodividaativa insc " +
            "        inner join processocalculo pc on insc.id = pc.id " +
            "        inner join iteminscricaodividaativa item on item.inscricaodividaativa_id = insc.id " +
            "        inner join valordivida vd on vd.calculo_id = item.id " +
            "        inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            "     where pvd.id = :idParcela ";
        Query q = em.createNativeQuery(sql, InscricaoDividaAtiva.class);
        q.setParameter("idParcela", idParcela);
        return q.getResultList().size() > 0 ? (InscricaoDividaAtiva) q.getResultList().get(0) : null;
    }

    public CertidaoDividaAtiva salvaCertidao(CertidaoDividaAtiva certidaoDividaAtiva) {
        return em.merge(certidaoDividaAtiva);
    }

    public CertidaoDividaAtivaControlador.AssistenteCDA gerarCDA(AssistenteBarraProgresso assistente,
                                                                 CertidaoDividaAtivaControlador.AssistenteCDA assistenteCDA) {
        assistente.setTotal(assistenteCDA.getItens().size());
        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
        JdbcDividaAtivaDAO dao = (JdbcDividaAtivaDAO) ap.getBean("dividaAtivaDAO");
        assistente.removerProcessoDoAcompanhamento();
        List<CertidaoDividaAtiva> certidoes = Lists.newArrayList();
        for (CertidaoDividaAtivaControlador.AssistenteAgrupadorCDA agrupador : assistenteCDA.getItens().keySet()) {
            try {
                CertidaoDividaAtiva certidao = obterNovaCertidaoInicializada(agrupador, assistenteCDA.getItens().get(agrupador), assistenteCDA.getExercicio());
                certidoes.add(certidao);
                assistenteCDA.conta();
                if (certidoes.size() >= 50) {
                    dao.persisteCertidao(certidoes);
                    certidoes.clear();
                }
            } catch (Exception e) {
//                e.printStackTrace();
                assistenteCDA.contaNaoGerados();
            }
            assistente.conta();
        }
        if (!certidoes.isEmpty()) {
            dao.persisteCertidao(certidoes);
        }
        assistenteCDA.setSituacao(CertidaoDividaAtivaControlador.Situacao.GERADO);
        return assistenteCDA;
    }

    public TipoDoctoOficial recuperaTipoDocumentoPorCertidaoDividaAtiva(CertidaoDividaAtiva certidao) {
        ParametrosDividaAtiva parametros = parametrosDividaAtivaFacade.parametrosDividaAtivaPorExercicio(certidao.getExercicio());
        if (parametros != null) {
            if (certidao.getCadastro() != null) {
                if (certidao.getCadastro() instanceof CadastroImobiliario) {
                    return parametros.getTipoDoctoCertidaoImob();
                } else if (certidao.getCadastro() instanceof CadastroEconomico) {
                    return parametros.getTipoDoctoCertidaoMob();
                } else if (certidao.getCadastro() instanceof CadastroRural) {
                    return parametros.getTipoDoctoCertidaoRural();
                }
            } else {
                if (certidao.getPessoa() instanceof PessoaFisica) {
                    return parametros.getTipoDoctoCertidaoContribPF();
                } else {
                    return parametros.getTipoDoctoCertidaoContribPJ();
                }
            }
        }
        return null;
    }

    public CertidaoDividaAtiva geraDocumentoSemImprimir(CertidaoDividaAtiva certidao, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        TipoDoctoOficial tipo = recuperaTipoDocumentoPorCertidaoDividaAtiva(certidao);
        if (tipo != null) {
            DocumentoOficial docto = documentoOficialFacade.geraDocumentoDividaAtivaSemImprimir(certidao, certidao.getDocumentoOficial(), certidao.getCadastro(), certidao.getPessoa(), tipo, sistemaControlador);
            certidao.setDocumentoOficial(docto);
            return em.merge(certidao);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado o parâmetro do tipo de documento para a CDA solicitada.");
        }
    }

    public CertidaoDividaAtiva geraDocumento(CertidaoDividaAtiva certidao, SistemaControlador sistemaControlador) throws UFMException, AtributosNulosException {
        return geraDocumento(certidao, sistemaControlador, true);
    }

    public CertidaoDividaAtiva geraDocumento(CertidaoDividaAtiva certidao, SistemaControlador sistemaControlador, boolean novo) throws UFMException, AtributosNulosException {
        TipoDoctoOficial tipo = recuperaTipoDocumentoPorCertidaoDividaAtiva(certidao);
        DocumentoOficial docto;
        if (tipo != null) {
            if (!novo) {
                docto = documentoOficialFacade.geraDocumentoDividaAtiva(certidao, certidao.getDocumentoOficial(), certidao.getCadastro(), certidao.getPessoa(), tipo, sistemaControlador);
            } else {
                docto = documentoOficialFacade.geraDocumentoDividaAtiva(certidao, null, certidao.getCadastro(), certidao.getPessoa(), tipo, sistemaControlador);
            }
            certidao.setDocumentoOficial(docto);
            return em.merge(certidao);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado o parâmetro do tipo de documento para a CDA solicitada.");
        }
    }

    public CertidaoDividaAtiva gerarDocumento(CertidaoDividaAtiva certidao, UsuarioSistema usuario, Exercicio exercicio, String ip) throws AtributosNulosException, UFMException {
        TipoDoctoOficial tipo = recuperaTipoDocumentoPorCertidaoDividaAtiva(certidao);
        if (tipo != null) {
            DocumentoOficial documentoOficial = documentoOficialFacade.gerarNovoDocumento(certidao, tipo, certidao.getCadastro(), certidao.getPessoa(), usuario, exercicio, ip);
            documentoOficialFacade.alteraSituacaoDocumentoOficial(documentoOficial, (certidao.getSituacaoCertidaoDA().equals(SituacaoCertidaoDA.CANCELADA) ? DocumentoOficial.SituacaoDocumentoOficial.CANCELADO : DocumentoOficial.SituacaoDocumentoOficial.ATIVO));
            certidao.setDocumentoOficial(documentoOficial);
            return em.merge(certidao);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado o parâmetro do tipo de documento para a CDA solicitada.");
        }
    }

    public CertidaoDividaAtiva recuperaCertidao(ItemInscricaoDividaAtiva item) {
        Query consulta = em.createQuery("select cert from CertidaoDividaAtiva cert join cert.itensCertidaoDividaAtiva item " +
            " where cert.situacaoCertidaoDA <> :situacao and item.itemInscricaoDividaAtiva = :item ");
        consulta.setParameter("item", item);
        consulta.setParameter("situacao", SituacaoCertidaoDA.CANCELADA);
        List<CertidaoDividaAtiva> listaCert = consulta.getResultList();
        if (!listaCert.isEmpty()) {
            return listaCert.get(0);
        }
        return null;
    }

    public Long recuperaProximoNumeroCDA() {
        return singletonGeradorCodigo.getProximoCodigo(CertidaoDividaAtiva.class, "numero");
    }

    public List<CertidaoDividaAtiva> listaFiltrandoCertidaoDividaAtiva(String parte) {
        Query consulta = em.createQuery("from CertidaoDividaAtiva cert "
            + "where to_char(cert.numero) like :parte");
        consulta.setParameter("parte", "%" + parte + "%");
        return consulta.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<CertidaoDividaAtiva> recuperaCDAValidasSoftPlan(int incio, List<Long> dividas) {
        logger.debug("Recuperando 500 CDAS ...");
        String sql = "SELECT distinct cda.* FROM certidaodividaativa cda " +
            "INNER JOIN pessoa ON pessoa.id = cda.pessoa_id " +
            "LEFT JOIN pessoafisica pf ON pf.id = pessoa.id " +
            "LEFT JOIN pessoajuridica pj ON pj.id = pessoa.id " +
            "INNER JOIN enderecocorreio ec ON ec.id = pessoa.enderecoprincipal_id " +
            "INNER JOIN itemcertidaodividaativa icda on icda.certidao_id = cda.id " +
            "inner join iteminscricaodividaativa ida on ida.id = icda.iteminscricaodividaativa_id " +
            "inner join Divida divida on divida.id = ida.divida_id " +
            "inner join valordivida vd on vd.calculo_id = ida.id " +
            "INNER JOIN exercicio ex ON ex.id = vd.exercicio_id " +
            "inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            "inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoAtual_id " +
            "WHERE divida.id in (:dividas) " +
            "and not exists (select 1 from ComunicacaoSoftPlan where cda_id = cda.id) ";
        Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class);
        q.setMaxResults(500);
        q.setFirstResult(incio);
        q.setParameter("dividas", dividas);
        return q.getResultList();
    }


    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<CertidaoDividaAtiva> buscarCDABySQL(String sql, int inicio, int max) {
        logger.debug("Vai recuperar partindo de " + inicio);
        sql = "SELECT  cda.* FROM certidaodividaativa cda " + sql;
        Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class);
        q.setFirstResult(inicio);
        q.setMaxResults(max);
        List resultList = Lists.newArrayList(Sets.newHashSet(q.getResultList()));
        logger.debug("recuperou " + resultList.size());
        return resultList;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public Integer contarCDABySQL(String sql) {
        sql = "SELECT count(distinct cda.id) FROM certidaodividaativa cda " + sql;
        Query q = em.createNativeQuery(sql);
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getResultList().get(0)).intValue();
        }
        return 0;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<CertidaoDividaAtiva> buscarCDAsParaArquivoSoftPlan() {
        logger.debug("Recuperando TODAS CDAS ...");

        List<CertidaoDividaAtiva> result = Lists.newArrayList();
        int first = 0;
        int max = 20000;

        String sql = "select distinct cda.id, cda.numero, excda.ano\n" +
            "FROM certidaodividaativa cda \n" +
            "INNER JOIN itemcertidaodividaativa icda ON icda.certidao_id = cda.id \n" +
            "INNER JOIN exercicio excda ON excda.id = cda.exercicio_id\n" +
            "INNER JOIN iteminscricaodividaativa ida ON ida.id = icda.iteminscricaodividaativa_id \n" +
            "INNER JOIN Divida divida ON divida.id = ida.divida_id \n" +
            "INNER JOIN valordivida vd ON vd.calculo_id = ida.id \n" +
            "INNER JOIN exercicio ex ON ex.id = vd.exercicio_id \n" +
            "INNER JOIN parcelavalordivida pvd ON pvd.valordivida_id = vd.id \n" +
            "INNER JOIN situacaoparcelavalordivida spvd ON spvd.id = pvd.situacaoatual_id\n" +
            "WHERE EXISTS \n" +
            "  (SELECT 1 \n" +
            "  FROM ComunicacaoSoftPlan \n" +
            "  WHERE cda_id = cda.id \n" +
            "  AND to_number(codigoResposta) = 0) \n" +
            "  and cda.situacaocertidaoda = :situacaoCda\n" +
            "  and ex.ano >= :ano\n" +
            "order by excda.ano, cda.numero";

        List<Object[]> listaArrayObjetos = criaExecutaQueryArquvoSofPlan(first, max, sql);

        while (listaArrayObjetos != null && !listaArrayObjetos.isEmpty()) {
            for (Object[] o : listaArrayObjetos) {
                CertidaoDividaAtiva cda = new CertidaoDividaAtiva();
                cda.setId((BigDecimal) o[0]);
                cda.setNumero((BigDecimal) o[1]);
                cda.setAno((BigDecimal) o[2]);
                result.add(cda);
            }
            first = first + max;
            listaArrayObjetos = criaExecutaQueryArquvoSofPlan(first, max, sql);
        }
        return result;
    }

    private List<Object[]> criaExecutaQueryArquvoSofPlan(int first, int max, String sql) {
        logger.debug("Buscando registros inciado com " + first);
        List<Object[]> listaArrayObjetos;
        Query q = em.createNativeQuery(sql);
        q.setMaxResults(max);
        q.setFirstResult(first);
        q.setParameter("ano", (Calendar.getInstance().get(Calendar.YEAR) - 5));
        q.setParameter("situacaoCda", SituacaoCertidaoDA.ABERTA.name());
        listaArrayObjetos = q.getResultList();
        Util.imprimeSQL(sql, q);
        return listaArrayObjetos;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<ProcessoParcelamento> recuperaParcelamentosValidosSoftPlan(int incio, List<Long> dividas) {
        String sql = "SELECT DISTINCT " +
            "calculo.id," +
            "calculo.dataCalculo," +
            "calculo.simulacao," +
            "calculo.valorReal," +
            "calculo.valorEfetivo," +
            "calculo.isento," +
            "calculo.cadastro_id," +
            "calculo.subDivida," +
            "calculo.tipoCalculo," +
            "calculo.consistente," +
            "calculo.processoCalculo_id," +
            "calculo.referencia," +
            "calculo.observacao," +
            "pp.situacaoParcelamento," +
            "pp.estornoParcelamento_id," +
            "pp.valorDivida_id," +
            "pp.paramParcelamento_id," +
            "pp.pessoa_id," +
            "pp.numeroParcelas," +
            "pp.valorParcela," +
            "pp.dataProcessoParcelamento," +
            "pp.numero," +
            "pp.exercicio_id," +
            "pp.vencimentoPrimeiraParcela," +
            "pp.valorTotalMulta," +
            "pp.valorTotalJuros," +
            "pp.valorTotalCorrecao," +
            "pp.valorEntrada," +
            "pp.valorPrimeiraParcela," +
            "pp.cancelamentoParcelamento_id," +
            "pp.percentualEntrada," +
            "pp.fiador_id," +
            "pp.numeroreParcelamento," +
            "pp.valorTotalHonorarios," +
            "pp.termo_id," +
            "pp.valorTotalImposto," +
            "pp.valorTotalTaxa," +
            "pp.valorTotalHonorariosAtualizado," +
            "pp.valorUltimaParcela," +
            "pp.faixaDesconto_id," +
            "pp.procurador_id" +
            "FROM CertidaoDividaAtiva cda " +
            "INNER JOIN ItemCertidaoDividaAtiva icda ON icda.certidao_id = cda.id " +
            "INNER JOIN ItemInscricaoDividaAtiva ida ON ida.id = icda.itemInscricaoDividaAtiva_id " +
            "INNER JOIN Divida divida ON divida.id = ida.divida_id " +
            "INNER JOIN ValorDivida vd ON vd.calculo_id = ida.id " +
            "INNER JOIN Exercicio ex ON ex.id = vd.exercicio_id " +
            "INNER JOIN ParcelaValorDivida pvd ON pvd.valorDivida_id = vd.id " +
            "INNER JOIN SituacaoParcelaValorDivida spvd ON spvd.id = pvd.situacaoAtual_id " +
            "INNER JOIN ItemProcessoParcelamento ipp on ipp.parcelaValorDivida_id = pvd.id " +
            "INNER JOIN ProcessoParcelamento pp on pp.id = ipp.processoParcelamento_id " +
            "INNER JOIN Calculo on calculo.id = pp.id " +
            "WHERE EXISTS (SELECT 1 FROM ComunicacaoSoftPlan " +
            "               WHERE cda_id = cda.id " +
            "                 AND to_number(codigoResposta) = 0) " +
            "  AND cda.situacaoCertidaoDA = 'QUITADA' " +
            "  AND ex.ano >= (extract(YEAR FROM SysDate) -5) " +
            //  "AND spvd.situacaoparcela IN ('EM_ABERTO', 'PARCELADO') " +
            "  AND divida.id IN (:dividas) " +
            " order by pp.dataProcessoParcelamento";
        Query q = em.createNativeQuery(sql, ProcessoParcelamento.class);
        q.setMaxResults(500);
        q.setFirstResult(incio);
        return q.getResultList();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<ProcessoParcelamento> recuperaParcelamentosEstornadosSoftPlan(int incio, List<Long> dividas) {
        String sql = "SELECT " +
            "distinct  " +
            "calculo.ID                   ," +
            "calculo.DATACALCULO          ," +
            "calculo.SIMULACAO            ," +
            "calculo.VALORREAL            ," +
            "calculo.VALOREFETIVO         ," +
            "calculo.ISENTO               ," +
            "calculo.CADASTRO_ID          ," +
            "calculo.SUBDIVIDA            ," +
            "calculo.TIPOCALCULO          ," +
            "calculo.CONSISTENTE          ," +
            "calculo.PROCESSOCALCULO_ID   ," +
            "calculo.REFERENCIA           ," +
            "calculo.OBSERVACAO           ," +
            "pp.SITUACAOPARCELAMENTO              ," +
            "pp.ESTORNOPARCELAMENTO_ID            ," +
            "pp.VALORDIVIDA_ID                    ," +
            "pp.PARAMPARCELAMENTO_ID              ," +
            "pp.PESSOA_ID                         ," +
            "pp.NUMEROPARCELAS                    ," +
            "pp.VALORPARCELA                      ," +
            "pp.DATAPROCESSOPARCELAMENTO          ," +
            "pp.NUMERO                            ," +
            "pp.EXERCICIO_ID                      ," +
            "pp.VENCIMENTOPRIMEIRAPARCELA         ," +
            "pp.VALORTOTALMULTA                   ," +
            "pp.VALORTOTALJUROS                   ," +
            "pp.VALORTOTALCORRECAO                ," +
            "pp.VALORENTRADA                      ," +
            "pp.VALORPRIMEIRAPARCELA              ," +
            "pp.CANCELAMENTOPARCELAMENTO_ID       ," +
            "pp.PERCENTUALENTRADA                 ," +
            "pp.FIADOR_ID                         ," +
            "pp.NUMEROREPARCELAMENTO              ," +
            "pp.VALORTOTALHONORARIOS              ," +
            "pp.TERMO_ID                          ," +
            "pp.VALORTOTALIMPOSTO                 ," +
            "pp.VALORTOTALTAXA                    ," +
            "pp.VALORTOTALHONORARIOSATUALIZADO    ," +
            "pp.VALORULTIMAPARCELA                ," +
            "pp.FAIXADESCONTO_ID                  ," +
            "pp.PROCURADOR_ID                     ," +
            "pp.MIGRADO                           " +

            "FROM certidaodividaativa cda " +
            "INNER JOIN itemcertidaodividaativa icda ON icda.certidao_id = cda.id " +
            "INNER JOIN iteminscricaodividaativa ida ON ida.id = icda.iteminscricaodividaativa_id " +
            "INNER JOIN Divida divida ON divida.id = ida.divida_id " +
            "INNER JOIN valordivida vd ON vd.calculo_id = ida.id " +
            "INNER JOIN exercicio ex ON ex.id = vd.exercicio_id " +
            "INNER JOIN parcelavalordivida pvd ON pvd.valordivida_id = vd.id " +
            "INNER JOIN situacaoparcelavalordivida spvd ON spvd.id = pvd.situacaoAtual_id " +
            "inner join itemprocessoparcelamento ipp on ipp.parcelavalordivida_id = pvd.id " +
            "inner join processoparcelamento pp on pp.id = ipp.processoparcelamento_id " +
            "inner join calculo on calculo.id = pp.id " +
            "WHERE EXISTS " +
            "  (SELECT 1 " +
            "  FROM ComunicacaoSoftPlan " +
            "  WHERE cda_id                  = cda.id " +
            "  AND to_number(codigoResposta) = 0 " +
            "  ) " +
            "AND cda.situacaocertidaoda = :situacaoCDA " +
            "AND ex.ano                >= (extract(YEAR FROM sysdate) -5) " +
            "AND spvd.situacaoparcela                                IN (:situacoesParcela) " +
            "AND divida.id                                           IN (:dividas) "
            + "AND pp.situacaoParcelamento in (:situacoesParcelamento) ";

        Query q = em.createNativeQuery(sql, ProcessoParcelamento.class);
        q.setMaxResults(500);
        q.setFirstResult(incio);
        q.setParameter("dividas", dividas);
        q.setParameter("situacaoCDA", SituacaoCertidaoDA.ABERTA.name());
        q.setParameter("situacoesParcela", Lists.newArrayList(SituacaoParcela.EM_ABERTO.name(), SituacaoParcela.PARCELADO.name()));
        q.setParameter("situacoesParcelamento", Lists.newArrayList(SituacaoParcelamento.CANCELADO.name(), SituacaoParcelamento.ESTORNADO.name()));
        return q.getResultList();
    }

    public List<CertidaoDividaAtiva> recuperaCDA(CertidaoDividaAtiva.FiltrosPesquisaCertidaoDividaAtiva filtros,
                                                 int inicio, int max, boolean apenasValidas, boolean apenasNaoEnviadas) {
        return buscarCDAs(filtros.getTipoCadastroTributario(), filtros.getCadastroInicial(), filtros.getCadastroFinal(),
            filtros.getExercicioInicial(), filtros.getExercicioFinal(), filtros.getNumeroCdaInicial(), filtros.getNumeroCdaFinal(),
            filtros.getDivida(), filtros.getPessoa(), Lists.newArrayList(filtros.getSituacaoCertidao()), inicio, max, apenasValidas, apenasNaoEnviadas, null);
    }

    public List<CertidaoDividaAtiva> buscarCDAs(TipoCadastroTributario tipoCadastroTributario, String cadastroInicial, String cadastroFinal,
                                                Exercicio exercicioInicial, Exercicio exercicioFinal, Long numeroCdaInicial, Long numeroCdaFinal,
                                                Divida divida, Pessoa pessoa, List<SituacaoCertidaoDA> situacaoCertidao,
                                                int inicio, int max, boolean apenasValidas, boolean apenasNaoEnviadas,
                                                SituacaoJudicial situacaoJudicial) {
        StringBuilder sql = new StringBuilder("select distinct certidao.* ")
            .append(" from CertidaoDividaAtiva certidao ")
            .append(" inner join Exercicio exercicio on exercicio.id = certidao.exercicio_id ")
            .append(" inner join Cadastro cadastro on cadastro.id = certidao.cadastro_id ");

        if (apenasNaoEnviadas) {
            sql.append(" where certidao.id not in (select cda_id from comunicacaosoftplan where codigoresposta = '00') ");
        } else {
            sql.append(" where 1=1 ");
        }
        if (situacaoCertidao != null && !situacaoCertidao.isEmpty()) {
            sql.append(" and (certidao.situacaoCertidaoDA is null or certidao.situacaoCertidaoDA in (:situacaoCertidao)) ");
        }
        if (pessoa != null) {
            sql.append(" and certidao.pessoa_id = :pessoa");
        }
        if (exercicioInicial != null) {
            sql.append(" and exercicio.ano >= :exercicioInicial ");
        }
        if (exercicioFinal != null) {
            sql.append(" and exercicio.ano <= :exercicioFinal ");
        }
        if (numeroCdaInicial != null) {
            sql.append(" and certidao.numero >= :numeroCdaInicial ");
        }
        if (numeroCdaFinal != null) {
            sql.append(" and certidao.numero <= :numeroCdaFinal ");
        }
        if (divida != null) {
            sql.append(" and certidao.id in (select cda.id ")
                .append("                   from ItemCertidaoDividaAtiva icda ")
                .append("             inner join CertidaoDividaAtiva cda on cda.id = icda.certidao_id ")
                .append("             inner join ItemInscricaoDividaAtiva iteminscricao on iteminscricao.id = icda.itemInscricaoDividaAtiva_id ")
                .append("             inner join Divida divida on divida.id = iteminscricao.divida_id ")
                .append("                  where divida.id = :divida)");
        }
        if (tipoCadastroTributario != null && !tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
            if (cadastroInicial != null && cadastroInicial.trim().length() > 0) {
                sql.append(" and cadastro.id in (");
                if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                    sql.append(" select cadI.id from CadastroImobiliario cadI where cadI.inscricaoCadastral >= :cadastroInicial ");
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                    sql.append(" select cadE.id from CadastroEconomico cadE where cadE.inscricaoCadastral >= :cadastroInicial ");
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                    sql.append(" select cadR.id from CadastroRural cadR where to_char(cadR.codigo) >= :cadastroInicial ");
                }
                sql.append(" )");
            }
            if (cadastroFinal != null && cadastroFinal.trim().length() > 0) {
                sql.append(" and cadastro.id in (");
                if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                    sql.append(" select cadI.id from CadastroImobiliario cadI where cadI.inscricaoCadastral <= :cadastroFinal ");
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                    sql.append(" select cadE.id from CadastroEconomico cadE where cadE.inscricaoCadastral <= :cadastroFinal ");
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                    sql.append(" select cadR.id from CadastroRural cadR where to_char(cadR.codigo) <= :cadastroFinal ");
                }
                sql.append(" )");
            }
        }
        if (situacaoJudicial != null) {
            sql.append(" and certidao.situacaojudicial = :situacaoJudicial ");
        }
        if (apenasValidas) {
            sql.append(" and certidao.id in (");
            sql.append(" select cda.id from certidaodividaativa cda");
            sql.append(" inner join pessoa on pessoa.id = cda.pessoa_id");
            sql.append(" left join pessoafisica pf on pf.id = pessoa.id");
            sql.append(" left join pessoajuridica pj on pj.id = pessoa.id");
            sql.append(" inner join enderecocorreio ec on ec.id = pessoa.enderecoprincipal_id");
            sql.append(" inner join telefone tel on tel.id = pessoa.telefoneprincipal_id");
            sql.append(" where valida_cpf_cnpj(coalesce(pf.cpf, pj.cnpj)) = 'S'");
            sql.append(" and trim(ec.logradouro) is not null");
            sql.append(" and trim(ec.bairro) is not null");
            sql.append(" and trim(ec.localidade) is not null");
            sql.append(" and trim(ec.numero) is not null");
            sql.append(" and trim(ec.uf) is not null");
            sql.append(" and trim(ec.cep) is not null");
            sql.append(" and ec.tipoEndereco in ('COMERCIAL','RESIDENCIAL')");
            sql.append(" and trim(tel.telefone) is not null");
            sql.append(" and tel.tipoFone in ('COMERCIAL','RESIDENCIAL')");
            sql.append(" and (exists (select 1 from rg inner join documentopessoal dp on dp.id = rg.id");
            sql.append(" where dp.pessoafisica_id = pf.id");
            sql.append(" and trim(rg.numero) is not null and rg.orgaoemissao is not null) or pj.inscricaoestadual is not null)");
            sql.append(" )");
        }

        sql.append(" order by certidao.numero");
        Query consulta = em.createNativeQuery(sql.toString(), CertidaoDividaAtiva.class);

        if (situacaoCertidao != null && !situacaoCertidao.isEmpty()) {
            List<String> situacoes = Lists.newArrayList();
            for (SituacaoCertidaoDA situacaoCertidaoDA : situacaoCertidao) {
                situacoes.add(situacaoCertidaoDA.name());
            }
            consulta.setParameter("situacaoCertidao", situacoes);
        }
        if (pessoa != null) {
            consulta.setParameter("pessoa", pessoa.getId());
        }
        if (exercicioInicial != null) {
            consulta.setParameter("exercicioInicial", exercicioInicial.getAno());
        }
        if (exercicioFinal != null) {
            consulta.setParameter("exercicioFinal", exercicioFinal.getAno());
        }
        if (numeroCdaInicial != null) {
            consulta.setParameter("numeroCdaInicial", numeroCdaInicial);
        }
        if (numeroCdaFinal != null) {
            consulta.setParameter("numeroCdaFinal", numeroCdaFinal);
        }
        if (divida != null) {
            consulta.setParameter("divida", divida.getId());
        }
        if (tipoCadastroTributario != null && !tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {

            if (cadastroInicial != null && cadastroInicial.trim().length() > 0) {
                consulta.setParameter("cadastroInicial", cadastroInicial);
            }
            if (cadastroFinal != null && cadastroFinal.trim().length() > 0) {
                consulta.setParameter("cadastroFinal", cadastroFinal);
            }
        }
        if (situacaoJudicial != null) {
            consulta.setParameter("situacaoJudicial", situacaoJudicial.name());
        }
        consulta.setMaxResults(max);
        consulta.setFirstResult(inicio);
        try {
            List<CertidaoDividaAtiva> lista = (List<CertidaoDividaAtiva>) consulta.getResultList();
            if (lista != null && !lista.isEmpty()) {
                for (CertidaoDividaAtiva certidao : lista) {
                    certidao.getItensCertidaoDividaAtiva().size();
                    List<ItemCertidaoDividaAtiva> item = certidao.getItensCertidaoDividaAtiva();
                    for (ItemCertidaoDividaAtiva aux : item) {
                        aux.getItemInscricaoDividaAtiva().getItensInscricaoDividaParcelas().size();
                    }
                }
            }
            return lista;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }


    public List<CertidaoDividaAtiva> completaCertidaoDividaAtiva(String parte) {
        String hql = "select certidao from CertidaoDividaAtiva certidao where to_char(certidao.numero) like :parte";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte + "%");
        return q.getResultList();
    }

    public List<CertidaoDividaAtiva> consultaCDA(TipoCadastroTributario tipoCadastroTributario, String cadastroInicial, String cadastroFinal,
                                                 Long numeroCertidaoInicial, Long numeroCertidaoFinal, Date dataInscricaoInicial, Date dataInscricaoFinal,
                                                 BigDecimal valorMinimo, BigDecimal valorMaximo, Pessoa pessoa, List<Divida> dividas, Exercicio exercicioInicial, Exercicio exercicioFinal) throws UFMException {

        String hql = "select distinct cda from CertidaoDividaAtiva cda "
            + " join cda.itensCertidaoDividaAtiva itemcda join itemcda.itemInscricaoDividaAtiva item"
            + " left join item.pessoas pessoa"
            + " join item.itensInscricaoDividaParcelas itemParcela"
            + " join item.inscricaoDividaAtiva inscricao "
            + " join itemParcela.parcelaValorDivida parcela"
            + " join parcela.valorDivida valorDivida"
            + " join parcela.situacoes situacao"
            + " left join valorDivida.divida dividaDestino"
            + " where 1=1 "
            + " and situacao.id = (select max(s.id) from SituacaoParcelaValorDivida s where s.parcela = parcela) ";


        if (numeroCertidaoInicial != null) {
            hql += " and cda.numero >= :numeroCertidaoInicial ";
        }
        if (numeroCertidaoFinal != null) {
            hql += " and cda.numero <= :numeroCertidaoFinal ";
        }
        if (exercicioInicial != null) {
            hql += " and cda.exercicio >= :exercicioInicial ";
        }
        if (exercicioFinal != null) {
            hql += " and cda.exercicio <= :exercicioFinal ";
        }

        if (pessoa != null) {
            hql += " and pessoa.pessoa = :pessoa ";
        }

        if (dataInscricaoInicial != null) {
            hql += " and inscricao.dataInscricao >= :dataInscricaoInicial ";
        }
        if (dataInscricaoFinal != null) {
            hql += " and inscricao.dataInscricao <= :dataInscricaoFinal ";
        }

        if (tipoCadastroTributario != null && !tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
            hql += " and inscricao.tipoCadastroTributario = :tipoCadastro";

            if (cadastroInicial != null && cadastroInicial.trim().length() > 0) {
                hql += " and item.cadastro in (";
                if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                    hql += " select cadI from CadastroImobiliario cadI where cadI.inscricaoCadastral >= :cadastroInicial ";
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                    hql += " select cadE from CadastroEconomico cadE where cadE.inscricaoCadastral >= :cadastroInicial ";
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                    hql += " select cadR from CadastroRural cadR where to_char(cadR.codigo) >= :cadastroInicial ";
                }
                hql += " )";
            }
            if (cadastroFinal != null && cadastroFinal.trim().length() > 0) {
                hql += " and item.cadastro in (";
                if (tipoCadastroTributario.equals(TipoCadastroTributario.IMOBILIARIO)) {
                    hql += " select cadI from CadastroImobiliario cadI where cadI.inscricaoCadastral <= :cadastroFinal ";
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.ECONOMICO)) {
                    hql += " select cadE from CadastroEconomico cadE where cadE.inscricaoCadastral <= :cadastroFinal ";
                }
                if (tipoCadastroTributario.equals(TipoCadastroTributario.RURAL)) {
                    hql += " select cadR from CadastroRural cadR where to_char(cadR.codigo) <= :cadastroFinal ";
                }
                hql += " )";
            }
        }
        if (dividas != null && dividas.size() > 0) {
            hql += " and dividaDestino in (select dividaOrigem from Divida dividaOrigem join dividaOrigem.divida divida where dividaOrigem in (:dividas)) ";
        }

        if (valorMinimo != null) {
            hql += " and (select sum(s.saldo) from SituacaoParcelaValorDivida s where s.parcela = parcela) >= :valorMinimo ";
        }
        if (valorMaximo != null) {
            hql += " and (select sum(s.saldo) from SituacaoParcelaValorDivida s where s.parcela = parcela) <= :valorMaximo ";
        }

        Query q = em.createQuery(hql);
        if (dividas != null && dividas.size() > 0) {
            q.setParameter("dividas", dividas);
        }
        if (valorMinimo != null) {
            q.setParameter("valorMinimo", valorMinimo);
        }
        if (valorMaximo != null) {
            q.setParameter("valorMaximo", valorMaximo);
        }

        if (pessoa != null) {
            q.setParameter("pessoa", pessoa);
        }
        if (numeroCertidaoInicial != null) {
            q.setParameter("numeroCertidaoInicial", numeroCertidaoInicial);
        }
        if (numeroCertidaoFinal != null) {
            q.setParameter("numeroCertidaoFinal", numeroCertidaoFinal);
        }
        if (exercicioInicial != null) {
            q.setParameter("exercicioInicial", exercicioInicial);
        }
        if (exercicioFinal != null) {
            q.setParameter("exercicioFinal", exercicioFinal);
        }
        if (dataInscricaoInicial != null) {
            q.setParameter("dataInscricaoInicial", dataInscricaoInicial);
        }
        if (dataInscricaoFinal != null) {
            q.setParameter("dataInscricaoFinal", dataInscricaoFinal);
        }
        if (tipoCadastroTributario != null && !tipoCadastroTributario.equals(TipoCadastroTributario.PESSOA)) {
            q.setParameter("tipoCadastro", tipoCadastroTributario);

            if (cadastroInicial != null && cadastroInicial.trim().length() > 0) {
                q.setParameter("cadastroInicial", cadastroInicial);
            }
            if (cadastroFinal != null && cadastroFinal.trim().length() > 0) {
                q.setParameter("cadastroFinal", cadastroFinal);
            }
        }
        //new Util().imprimeSQL(hql, q);
        return (List<CertidaoDividaAtiva>) q.getResultList();
    }

    public String recuperaDividaOrigemPorCertidaoDividaAtiva(CertidaoDividaAtiva certidao) {
        String sql = "select distinct dividaOrigem.descricao "
            + " from CertidaoDividaAtiva cda "
            + " join itemInscricaoDividaAtiva item on cda.iteminscricaodividaativa_id = item.id "
            + " join inscricaodividaparcela itemParcela on item.id = itemparcela.iteminscricaodividaativa_id "
            + " join parcelaValorDivida parcela on itemparcela.parcelavalordivida_id = parcela.id "
            + " join valorDivida valorDivida on valorDivida.id = parcela.valordivida_id "
            + " join divida dividaDestino on valorDivida.divida_id = dividaDestino.id "
            + " join divida dividaOrigem on dividaorigem.divida_id = dividadestino.id "
            + " where cda.id = :certidao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("certidao", certidao.getId());
        if (q.getResultList().isEmpty()) {
            return "-";
        } else {
            return q.getSingleResult().toString();
        }
    }

    public List<ParcelaValorDivida> recuperaParcelasValorDividaRelatorioRolCertidaoDividaAtiva(String condicao) {
        String sql = "select pvd.*"
            + " from linhadolivrodividaativa linha"
            + " inner join iteminscricaodividaativa item on linha.iteminscricaodividaativa_id = item.id"
            + " inner join certidaodividaativa cert on item.id = cert.iteminscricaodividaativa_id"
            + " inner join inscricaodividaativa inscricao on item.inscricaodividaativa_id = inscricao.id"
            + " inner join itemlivrodividaativa itemLivro on linha.itemlivrodividaativa_id = itemLivro.id"
            + " inner join livrodividaativa livro on itemlivro.livrodividaativa_id = livro.id"
            + " inner join exercicio ex on livro.exercicio_id = ex.id"
            + " inner join divida div on item.divida_id = div.id"
            + " inner join inscricaodividaparcela idp on idp.iteminscricaodividaativa_id = item.id"
            + " inner join parcelavalordivida pvd on idp.parcelavalordivida_id = pvd.id"
            + " inner join valordivida vd on pvd.valorDivida_id = vd.id"
            + " inner join calculo calculo on vd.calculo_id = calculo.id"
            + " inner join cadastro cad on calculo.cadastro_id = cad.id"
            + " left join cadastroimobiliario bci on cad.id = bci.id"
            + " left join cadastroeconomico cmc on cad.id = cmc.id"
            + " left join cadastrorural bcr on cad.id = bcr.id"
            + " inner join calculopessoa calcp on calculo.id = calcp.calculo_id"
            + " inner join pessoa p on calcp.pessoa_id = p.id"
            + " left join pessoafisica pf on p.id = pf.id"
            + " left join pessoajuridica pj on p.id = pj.id"
            + " inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id "
            + " left join temp_acrescimos temp on temp.parcela_id = pvd.id " + condicao;
        Query consulta = em.createNativeQuery(sql, ParcelaValorDivida.class);
        List<ParcelaValorDivida> listaDeParcelas = consulta.getResultList();
        for (ParcelaValorDivida parcela : listaDeParcelas) {
            parcela = CalculadorAcrescimos.calculaAcrescimo(parcela, new Date(), consultaDebitoFacade.getValorAtualizadoParcela(parcela), dividaFacade.configuracaoVigente(parcela.getValorDivida().getDivida()));
        }
        return listaDeParcelas;
    }

    public List<ParcelaValorDivida> recuperaParcelasDaCertidao(CertidaoDividaAtiva certidao) {
        String hql = "select i.parcelaValorDivida from InscricaoDividaParcela i where i.itemInscricaoDividaAtiva " +
            "in (select item.itemInscricaoDividaAtiva from ItemCertidaoDividaAtiva item where item.certidao = :certidao)";
        return em.createQuery(hql).setParameter("certidao", certidao).getResultList();
    }

    public List<ResultadoParcela> recuperaParcelasDaCertidaoDaView(CertidaoDividaAtiva cda) {
        List<ResultadoParcela> parcelas = Lists.newArrayList();
        List<VOItemCertidaoDividaAtiva> itens = buscarVOItensCertidao(cda.getId());
        for (VOItemCertidaoDividaAtiva item : itens) {
            if (ItemInscricaoDividaAtiva.Situacao.ATIVO.equals(item.getSituacaoItemInscricao())) {
                parcelas.addAll(buscarParcelasDoCalculo(item.getIdItemInscricao()));
            }
        }
        Set<ResultadoParcela> parcelasSemRepeticao = new HashSet<>(parcelas);
        return Lists.newArrayList(parcelasSemRepeticao);
    }

    public ValoresAtualizadosCDA calcularValoresAtualizadosDaCDA(CertidaoDividaAtiva cda) {
        ValoresAtualizadosCDA valoresAtualizadosCDA = new ValoresAtualizadosCDA();
        integraSoftplanFacade.calcularValoresDaCDA(valoresAtualizadosCDA, cda);
        return valoresAtualizadosCDA;
    }

    public ValoresAtualizadosCDA valorAtualizadoDaCertidao(CertidaoDividaAtiva cda) {
        return calcularValoresAtualizadosDaCDA(cda);
    }

    public BigDecimal valorDaCertidao(CertidaoDividaAtiva certidao) {
        return valorAtualizadoDaCertidao(certidao).getValorTotal();
    }

    public List<VOItemCertidaoDividaAtiva> buscarVOItensCertidao(Long idCda) {
        String sql = "select icda.certidao_id, icda.id, icda.iteminscricaodividaativa_id, iida.situacao " +
            "from ItemCertidaoDividaAtiva icda " +
            "inner join iteminscricaodividaativa iida on iida.id = icda.iteminscricaodividaativa_id " +
            "where icda.certidao_id = :idCda";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCda", idCda);
        List<Object[]> lista = q.getResultList();
        List<VOItemCertidaoDividaAtiva> itens = Lists.newArrayList();
        for (Object[] obj : lista) {
            VOItemCertidaoDividaAtiva item = new VOItemCertidaoDividaAtiva();
            item.setIdCertidao(((BigDecimal) obj[0]).longValue());
            item.setIdItemCertidao(((BigDecimal) obj[1]).longValue());
            item.setIdItemInscricao(((BigDecimal) obj[2]).longValue());
            item.setSituacaoItemInscricao(ItemInscricaoDividaAtiva.Situacao.valueOf((String) obj[3]));
            itens.add(item);
        }
        return itens;
    }


    @Override
    public CertidaoDividaAtiva recuperar(Object id) {
        CertidaoDividaAtiva certidao = em.find(CertidaoDividaAtiva.class, id);
        certidao.getItensCertidaoDividaAtiva().size();
        certidao.getOcorrencias().size();
        certidao.getComunicacoes().size();
        Collections.sort(certidao.getComunicacoes());
        certidao.getCertidoesLegadas().size();
        if (certidao.getPessoa() != null) {
            certidao.getPessoa().getRg_InscricaoEstadual();
            certidao.getPessoa().getEnderecos().size();
            certidao.getPessoa().getTelefones().size();
            for (DadosRetificacaoPessoaCda dados : certidao.getPessoa().getDadosRetificacao()) {
                dados.getTelefones().size();
            }
        }
        return certidao;
    }

    public String retornaInscricaoDoCadastro(Cadastro cadastro) {
        if (cadastro instanceof CadastroImobiliario) {
            return ((CadastroImobiliario) cadastro).getInscricaoCadastral();
        } else if (cadastro instanceof CadastroEconomico) {
            return ((CadastroEconomico) cadastro).getInscricaoCadastral();
        } else if (cadastro instanceof CadastroRural) {
            return ((CadastroRural) cadastro).getNumeroIncra();
        }
        return "   -   ";
    }

    public boolean naoTemCertidao(ItemInscricaoDividaAtiva item) {
        return recuperaCertidao(item) == null;
    }

    public CertidaoDividaAtiva obterNovaCertidaoInicializada(CertidaoDividaAtivaControlador.AssistenteAgrupadorCDA agrupadorCDA,
                                                             List<ItemInscricaoDividaAtiva> itens, Exercicio exercicio) {
        if (validaCpfCnpj(agrupadorCDA.pessoa.getCpf_Cnpj())) {
            CertidaoDividaAtiva certidaoDividaAtiva = new CertidaoDividaAtiva();
            certidaoDividaAtiva.setExercicio(exercicio);
            certidaoDividaAtiva.setNumero(recuperaProximoNumeroCDA());
            certidaoDividaAtiva.setDataCertidao(new Date());
            certidaoDividaAtiva.setFuncionarioEmissao(null);
            certidaoDividaAtiva.setFuncionarioAssinante(null);
            certidaoDividaAtiva.setCadastro(agrupadorCDA.cadastro);
            certidaoDividaAtiva.setPessoa(agrupadorCDA.pessoa);
            certidaoDividaAtiva.setIntegrada(Boolean.FALSE);
            certidaoDividaAtiva.setSituacaoCertidaoDA(SituacaoCertidaoDA.ABERTA);
            certidaoDividaAtiva.setSituacaoJudicial(SituacaoJudicial.NAO_AJUIZADA);
            for (ItemInscricaoDividaAtiva item : itens) {
                ItemCertidaoDividaAtiva itemCertidao = new ItemCertidaoDividaAtiva();
                itemCertidao.setCertidao(certidaoDividaAtiva);
                itemCertidao.setItemInscricaoDividaAtiva(item);
                certidaoDividaAtiva.getItensCertidaoDividaAtiva().add(itemCertidao);
            }
//            geraDadosRetificacao(certidaoDividaAtiva);
            return certidaoDividaAtiva;
        } else {
            throw new ExcecaoNegocioGenerica("Pessoa Inválida");
        }
    }

    public void geraDadosRetificacao(Pessoa pessoa) {
        try {
            pessoa = DadosRetificacaoPessoaCda.criarRetificacao(pessoa);
        } catch (Exception e) {
            e.printStackTrace();
        }
        em.merge(pessoa);
        em.flush();
    }

    boolean validaCpfCnpj(String cpfCnpj) {
        if (cpfCnpj == null || cpfCnpj.trim().isEmpty()) {
            return false;
        }
        return Util.valida_CpfCnpj(cpfCnpj);
    }

    private List<Long> buscarParcelasOriginais(List<Long> idsParcelas) {
        String sql = "select distinct pvdoriginal.id as pvdOriginal " +
            "from parcelavalordivida pvd  " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "inner join processoparcelamento parcelamento on parcelamento.id = vd.CALCULO_ID " +
            "inner join ITEMPROCESSOPARCELAMENTO itemparcelamento on itemparcelamento.PROCESSOPARCELAMENTO_ID = parcelamento.id " +
            "inner join parcelavalordivida pvdoriginal on pvdoriginal.id = itemparcelamento.PARCELAVALORDIVIDA_ID " +
            "inner join valordivida vdoriginal on vdoriginal.id = pvdoriginal.valordivida_id " +
            "where pvd.id in (:parcelas)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("parcelas", idsParcelas);
        List<BigDecimal> originais = q.getResultList();
        List<Long> retorno = Lists.newArrayList();
        if (!originais.isEmpty()) {
            for (BigDecimal original : originais) {
                retorno.add(original.longValue());
            }
            idsParcelas = buscarParcelasOriginais(retorno);
        }
        return idsParcelas;
    }

    public List<CertidaoDividaAtiva> certidoesDoParcelamento(ProcessoParcelamento parcelamento, boolean somenteEnviadas) {
        return certidoesDoParcelamento(parcelamento.getId(), somenteEnviadas);
    }

    public List<CertidaoDividaAtiva> certidoesDoParcelamento(Long idProcessoParcelamento, boolean somenteEnviadas) {
        List<Long> idsParcelas = processoParcelamentoFacade.buscarIdsParcelasOriginaisParcelamento(idProcessoParcelamento);
        List<Long> originais = buscarParcelasOriginais(idsParcelas);
        if (!originais.isEmpty()) {
            String sql = "select cda.* from certidaodividaativa cda  " +
                "inner join itemcertidaodividaativa itemcda on itemcda.certidao_id = cda.id  " +
                "inner join iteminscricaodividaativa itemda on itemda.id = itemcda.iteminscricaodividaativa_id  " +
                "inner join valordivida vd on vd.calculo_id = itemda.id  " +
                "inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
                " where pvd.id in (:parcelas) ";
            if (somenteEnviadas) {
                sql += "  AND exists (select * from ComunicacaoSoftPlan where cda_id = cda.id and codigoresposta in ('00','02'))  ";
            }
            Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class);
            q.setParameter("parcelas", originais);
            return q.getResultList();
        }
        return null;
    }

    public List<ItemCertidaoDividaAtiva> buscarItensCertidaoDoParcelamento(ProcessoParcelamento parcelamento) {
        List<Long> idsParcelas = processoParcelamentoFacade.buscarIdsParcelasOriginaisParcelamento(parcelamento.getId());
        idsParcelas = buscarParcelasOriginais(idsParcelas);
        if (!idsParcelas.isEmpty()) {
            List<Long> idsParcelasParametro = Lists.newArrayList();
            for (Long idParcela : idsParcelas) {
                Calculo.TipoCalculo tipoCalculo = consultaDebitoFacade.buscarTipoCalculoDaParcela(idParcela);
                if (Calculo.TipoCalculo.CANCELAMENTO_PARCELAMENTO.equals(tipoCalculo)) {
                    Long idParcelaOriginalDaParcelaDoCancelamento = cancelamentoParcelamentoFacade.buscarIdParcelaOriginalDaParcelaDoCancelamento(idParcela);
                    if (idParcelaOriginalDaParcelaDoCancelamento != null) {
                        idsParcelasParametro.add(idParcelaOriginalDaParcelaDoCancelamento);
                        continue;
                    }
                }
                idsParcelasParametro.add(idParcela);
            }
            if (!idsParcelasParametro.isEmpty()) {
                String sql = "select itemcda.* from itemcertidaodividaativa itemcda " +
                    "inner join iteminscricaodividaativa itemda on itemda.id = itemcda.iteminscricaodividaativa_id  " +
                    "inner join valordivida vd on vd.calculo_id = itemda.id  " +
                    "inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
                    " where pvd.id in (:parcelas) ";
                Query q = em.createNativeQuery(sql, ItemCertidaoDividaAtiva.class);
                logger.debug("IDS Parcelas [{}]", idsParcelasParametro);
                q.setParameter("parcelas", idsParcelasParametro);
                return q.getResultList();
            }
        }
        return null;
    }

    public List<CertidaoDividaAtiva> buscarCertidoesComDebitosDiferentesAberto(ProcessoJudicial processoJudicial) {
        String sql = "select cda.* " +
            "   from certidaodividaativa cda " +
            "  inner join processojudicialcda processo_cda on processo_cda.certidaodividaativa_id = cda.id " +
            "  inner join processojudicial processo on processo.id = processo_cda.processojudicial_id " +
            " where replace(replace(replace(processo.numeroprocessoforum,'.',''),'-',''),'/','') like :numeroProcessoForum " +
            "   and cda.situacaocertidaoda <> :situacaoJuncao " +
            "   and exists (select 1 " +
            "                 from parcelavalordivida pvd " +
            "                inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "                inner join valordivida vd on vd.id = pvd.valordivida_id" +
            "                inner join iteminscricaodividaativa iida on iida.id = vd.calculo_id " +
            "                inner join itemcertidaodividaativa icda on icda.iteminscricaodividaativa_id = iida.id" +
            "               where icda.certidao_id = cda.id" +
            "                 and spvd.situacaoparcela <> :emAberto) ";
        Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class);
        q.setParameter("numeroProcessoForum", "%" + StringUtil.removeZerosEsquerda(
            StringUtil.retornaApenasNumeros(processoJudicial.getNumeroProcessoForum().trim())) + "%");
        q.setParameter("situacaoJuncao", SituacaoCertidaoDA.JUNCAO_CDALEGADA.name());
        q.setParameter("emAberto", SituacaoParcela.EM_ABERTO.name());
        return q.getResultList();
    }

    public List<CertidaoDividaAtiva> certidoesDoProcessoJudicialParcelamento(ProcessoJudicial processoJudicial) {
        String sql = "select cda.* from certidaodividaativa cda " +
            "inner join processojudicialcda processo_cda on processo_cda.certidaodividaativa_id = cda.id " +
            "where processo_cda.processojudicial_id = :processo" +
            "  and cda.situacaoCertidaoDA <> :situacaoJuncao";
        Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class);
        q.setParameter("processo", processoJudicial.getId());
        q.setParameter("situacaoJuncao", SituacaoCertidaoDA.JUNCAO_CDALEGADA.name());
        return q.getResultList();
    }

    public List<CertidaoDividaAtiva> recuperaTodasCertidoesIntegradas() {
        Query q = em.createQuery("select cda from CertidaoDividaAtiva  cda where cda.retornoComunicacao = :sucesso");
        q.setParameter("sucesso", CertidaoDividaAtiva.RetornoComunicacao.SUCESSO);
        return q.getResultList();
    }

    public List<CertidaoDividaAtiva> listaCertidaoDividaAtivaPorPessoa(Pessoa pessoa) {
        String sql = "select cda.* from CertidaoDividaAtiva cda where cda.pessoa_id = :idPessoa " +
            " and exists (select soft.id from ComunicacaoSoftPlan soft where soft.cda_id = cda.id)";
        Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class).setParameter("idPessoa", pessoa);
        return q.getResultList();
    }

    public LivroDividaAtiva recuperarLivroDivida(InscricaoDividaAtiva inscricaoDividaAtiva) {
        if (inscricaoDividaAtiva != null) {
            String sql = " select livro.* " +
                "         from livrodividaativa livro " +
                "        inner join itemlivrodividaativa itemlivro on itemlivro.livrodividaativa_id = livro.id " +
                "      where itemlivro.inscricaodividaativa_id = :id_inscricaodividaativa ";
            Query q = em.createNativeQuery(sql, LivroDividaAtiva.class);
            q.setParameter("id_inscricaodividaativa", inscricaoDividaAtiva.getId());
            return !q.getResultList().isEmpty() ? (LivroDividaAtiva) q.getResultList().get(0) : null;
        }
        return null;
    }

    public LinhaDoLivroDividaAtiva recuperarLinhaDoLivroDividaAtiva(Long idItemInscricao) {
        String sql = " select linhalivro.* " +
            "         from LinhaDoLivroDividaAtiva linhalivro " +
            "        inner join iteminscricaodividaativa iteminsc on linhalivro.iteminscricaodividaativa_id = iteminsc.id " +
            "      where iteminsc.id = :idItemInscricao ";
        Query q = em.createNativeQuery(sql, LinhaDoLivroDividaAtiva.class);
        q.setParameter("idItemInscricao", idItemInscricao);
        List resultList = q.getResultList();
        return resultList.size() > 0 ? (LinhaDoLivroDividaAtiva) resultList.get(0) : null;
    }

    public String numeroProcessoAjuizamentoDaCda(CertidaoDividaAtiva cda) {
        String sql = "select processo.numeroprocessoforum from processojudicialcda processoCda " +
            "inner join processojudicial processo on processo.id = processocda.processojudicial_id " +
            "where processoCda.certidaodividaativa_id = :idCda " +
            "and processo.situacao = :situacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idCda", cda.getId());
        q.setParameter("situacao", ProcessoJudicial.Situacao.ATIVO.name());
        List<String> lista = q.getResultList();
        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public ProcessoJudicial processoAjuizamentoDaCda(CertidaoDividaAtiva cda) {
        String sql = "select processo.* from processojudicialcda processoCda " +
            "inner join processojudicial processo on processo.id = processocda.processojudicial_id " +
            "where processoCda.certidaodividaativa_id = :idCda" +
            "  and processo.situacao = :situacao ";
        Query q = em.createNativeQuery(sql, ProcessoJudicial.class);
        q.setParameter("idCda", cda.getId());
        q.setParameter("situacao", ProcessoJudicial.Situacao.ATIVO.name());
        List<ProcessoJudicial> lista = q.getResultList();
        if (lista.isEmpty()) {
            return null;
        }
        return lista.get(0);
    }

    public List<ConsultaCertidaoDividaAtivaControlador.ItensCertidaoDaConsulta> itensCertidaoDaConsulta(CertidaoDividaAtiva certidao) {
        String sql = "select * from ( " +
            "select distinct " +
            "   ins.numero as inscricao,  " +
            "   ex.ano as ano, " +
            "   itemins.situacao as situacao, " +
            "   ins.tipoCadastroTributario as tipocadastrotributario,  " +
            "   divida.descricao as divida, " +
            "   itemins.id as idItemInscricao, " +
            "   cal.cadastro_id as cadastro_id, " +
            "   coalesce(max(linha.numerodalinha),0) as linha,  " +
            "   coalesce(max(livro.numero),0) as livro,   " +
            "   coalesce(max(linha.pagina),0) as pagina, " +
            "   coalesce(max(exLivro.ano),0) as ano_livro  " +
            " from itemcertidaodividaativa item  " +
            " inner join iteminscricaodividaativa itemins on itemins.id = item.iteminscricaodividaativa_id  " +
            " inner join Calculo cal on cal.id = itemins.id " +
            " inner join certidaodividaativa cda on cda.id = item.certidao_id  " +
            " inner join inscricaodividaativa ins on ins.id = itemins.inscricaodividaativa_id  " +
            " inner join exercicio ex on ex.id = ins.exercicio_id  " +
            " inner join divida on divida.id = itemins.divida_id  " +
            " left join linhadolivrodividaativa linha on linha.iteminscricaodividaativa_id = itemins.id  " +
            " left join itemlivrodividaativa itemlivro on itemlivro.id = linha.itemlivrodividaativa_id  " +
            " left join livrodividaativa livro on livro.id = itemlivro.livrodividaativa_id  " +
            " left join exercicio exLivro on exLivro.id = livro.exercicio_id  " +
            " where item.certidao_id = :idCda and (cal.cadastro_id = cda.cadastro_id or itemins.pessoa_id = cda.pessoa_id) " +
            " group by  " +
            " ins.numero,  " +
            "   ex.ano, itemins.situacao, ins.tipoCadastroTributario,  " +
            "   divida.descricao, itemins.id, cal.cadastro_id " +
            "  )  " +
            " order by ano, livro, pagina, linha";

        List<Object[]> lista = em.createNativeQuery(sql).setParameter("idCda", certidao.getId()).getResultList();
        List<ConsultaCertidaoDividaAtivaControlador.ItensCertidaoDaConsulta> retorno = Lists.newArrayList();

        for (Object[] obj : lista) {
            ConsultaCertidaoDividaAtivaControlador.ItensCertidaoDaConsulta item = new ConsultaCertidaoDividaAtivaControlador.ItensCertidaoDaConsulta();

            item.setNumero(((BigDecimal) obj[0]).longValue());
            item.setAno(((BigDecimal) obj[1]).intValue());
            item.setSituacaoItemInscricao(ItemInscricaoDividaAtiva.Situacao.valueOf(((String) obj[2])));
            item.setTipoCadastroTributario(TipoCadastroTributario.valueOf(((String) obj[3])));
            item.setDescricaoDivida(((String) obj[4]));
            item.setIdItemInscricaoDividaAtiva(((BigDecimal) obj[5]).longValue());
            item.setIdCadastro(obj[6] != null ? ((BigDecimal) obj[6]).longValue() : null);


            item.setLinhaLivro(((BigDecimal) obj[7]).longValue());
            item.setNumeroLivro(((BigDecimal) obj[8]).longValue());
            item.setPaginaLivro(((BigDecimal) obj[9]).longValue());
            item.setAnoLivro(((BigDecimal) obj[10]).longValue());
            retorno.add(item);
        }
        return retorno;
    }

    public List<InscricaoDividaParcela> carregaInscricoesDividaParcela(Long idItemInscricaoDividaAtiva) {
        String hql = "select ins from InscricaoDividaParcela ins where ins.itemInscricaoDividaAtiva.id = :idItemInscricao";
        return em.createQuery(hql).setParameter("idItemInscricao", idItemInscricaoDividaAtiva).getResultList();
    }

    public List<CertidaoDividaAtivaLegada> listaCertidoesLegadas(CertidaoDividaAtiva certidaoDividaAtiva) {
        String hql = "select legada from CertidaoDividaAtivaLegada legada where legada.certidaoDividaAtiva = :certidaoDividaAtiva";
        return em.createQuery(hql).setParameter("certidaoDividaAtiva", certidaoDividaAtiva).getResultList();
    }

    public void pagaCertidoesDoPacelamento(ProcessoParcelamento parcelamento) {
        List<CertidaoDividaAtiva> certidoes = certidoesDoParcelamento(parcelamento, false);

        for (CertidaoDividaAtiva cda : certidoes) {
            List<ProcessoParcelamento> parcelamentosDaCDA = processoParcelamentoFacade.recuperaParcelamentoDaCda(cda);
            boolean hasProcessoParcelamentoAtivo = false;
            boolean hasDebitoEmAberto = false;

            for (ProcessoParcelamento pp : parcelamentosDaCDA) {
                if (!SituacaoParcelamento.PAGO.equals(pp.getSituacaoParcelamento())
                    && !SituacaoParcelamento.CANCELADO.equals(pp.getSituacaoParcelamento())) {
                    hasProcessoParcelamentoAtivo = true;
                    break;
                }
            }

            for (ItemCertidaoDividaAtiva i : cda.getItensCertidaoDividaAtiva()) {
                List<ResultadoParcela> resultadoParcelas = buscarParcelasDoCalculoOriginal(i.getItemInscricaoDividaAtiva().getId());
                for (ResultadoParcela r : resultadoParcelas) {
                    if (SituacaoParcela.EM_ABERTO.name().equals(r.getSituacao())) {
                        hasDebitoEmAberto = true;
                        break;
                    }
                }
            }

            if (!hasProcessoParcelamentoAtivo && !hasDebitoEmAberto) {
                cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.QUITADA);
                em.merge(cda);
            }
        }
    }

    public List<CertidaoDividaAtiva> recuperarCDAQueNaoEstaoEMProcessoJudicial(String parte) {
        String[] split = parte.trim().split("/");
        String numero = "";
        String exercicio = "";
        if(split.length > 0) {
            numero = split[0];
            if(split.length > 1) exercicio = split[1];
        }
        String sql = "select cda.* from certidaodividaativa cda " +
            " inner join exercicio excda on excda.id = cda.exercicio_id " +
            " where not exists(select processocda.certidaodividaativa_id " +
            " from ProcessoJudicialCDA processocda " +
            " inner join ProcessoJudicial processo on processo.id = processocda.processoJudicial_id " +
            " where processocda.certidaodividaativa_id = cda.id" +
            " and processo.situacao = :situacao) ";
        if (!numero.trim().isEmpty()) sql += " and to_char(cda.numero) like :numero";
        if (!exercicio.trim().isEmpty()) sql += " and to_char(excda.ano) like :exercicio";
        Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class);
        q.setParameter("situacao", ProcessoJudicial.Situacao.ATIVO.name());
        if (!numero.trim().isEmpty()) q.setParameter("numero", "%" + numero.trim() + "%");
        if (!exercicio.trim().isEmpty()) q.setParameter("exercicio", "%" + exercicio.trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public String envioAProcuradoria(CertidaoDividaAtiva cda) {
        String sql = "select * from ComunicacaoSoftPlan where cda_id = :cda_id";
        Query q = em.createNativeQuery(sql, ComunicacaoSoftPlan.class);
        q.setParameter("cda_id", cda.getId());
        List<ComunicacaoSoftPlan> retorno = q.getResultList();
        if (retorno.isEmpty()) {
            return CertidaoDividaAtiva.RetornoComunicacao.NAO_ENVIADO.getDescricao();
        }
        for (ComunicacaoSoftPlan comunicacaoSoftPlan : retorno) {
            if ("00".equals(comunicacaoSoftPlan.getCodigoResposta())) {
                return CertidaoDividaAtiva.RetornoComunicacao.SUCESSO.getDescricao();
            }
        }
        return CertidaoDividaAtiva.RetornoComunicacao.FALHA.getDescricao();
    }

    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public File geraLinhasCDA(AssistenteBarraProgresso assistente, List<CertidaoDividaAtiva> cdas) {
        logger.debug("Vai gerar linhas para " + cdas.size() + " CDAs");
        File linhasCDA = null;
        try {
            linhasCDA = File.createTempFile("linhasCDA", ".tmp");
            try (BufferedOutputStream outputStreamWriter = new BufferedOutputStream(new FileOutputStream(linhasCDA), 16384)) {
                int i = 0;
                int total = cdas.size();
                List<CertidaoDividaAtiva> cdasComErro = Lists.newArrayList();
                for (CertidaoDividaAtiva cda : cdas) {
                    try {
                        StringBuilder linha = geraLinhaCDA(cda, cdas.indexOf(cda));
                        if (linha != null) {
                            outputStreamWriter.write(linha.append(System.getProperty("line.separator")).toString().getBytes());
                            i++;
                            if (i > 0 && i % 100 == 0) {
                                logger.debug("Foram calculadas " + i + " certidões de " + total);
                            }
                        } else {
                            cdasComErro.add(cda);
                        }
                    } catch (Exception e) {
                        logger.warn("Não foi possível gerar a linha para a CDA " + cda.getNumero());
                    }
                }
                for (CertidaoDividaAtiva cdaErrada : cdasComErro) {
                    cdas.remove(cdaErrada);
                }
                logger.debug("Gerou linhas para " + cdas.size() + " CDAs");
            }
        } catch (IOException e) {
            logger.error("Erroa ao gerar linhas da CDA", e);
        }
        assistente.contar(cdas.size());
        return linhasCDA;
    }

    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<Long> recuperarIdsParcelasPorCertidaoDividaAtiva(CertidaoDividaAtiva cda) {
        String sql = "SELECT distinct id " +
            "  FROM " +
            "    ( WITH cteparcelamento(originada, original, calculo) AS " +
            "    (SELECT COALESCE(pvd.id,pvdoriginal.id) AS originada, " +
            "      pvdoriginal.id                        AS original, " +
            "      vdoriginal.calculo_id                 AS calculo " +
            "    FROM parcelavalordivida pvdoriginal " +
            "    INNER JOIN valordivida vdoriginal " +
            "    ON vdoriginal.id = pvdoriginal.valordivida_id " +
            "    LEFT JOIN INSCRICAODIVIDAPARCELA idp " +
            "    ON idp.parcelaValorDivida_id = pvdoriginal.id " +
            "    LEFT JOIN ITEMINSCRICAODIVIDAATIVA ida " +
            "    ON ida.id = idp.itemInscricaoDividaAtiva_id " +
            "    LEFT JOIN valordivida vdoriginado " +
            "    ON vdoriginado.calculo_id = ida.id " +
            "    LEFT JOIN parcelavalordivida pvd " +
            "    ON pvd.valordivida_id       = vdoriginado.id " +
            "    inner join itemcertidaodividaativa icda on icda.ITEMINSCRICAODIVIDAATIVA_ID = ida.id " +
            "    WHERE icda.CERTIDAO_ID = :cda " +
            "    UNION ALL " +
            "    SELECT originada.id AS originada, " +
            "      pvd.id            AS original, " +
            "      pp.id             AS calculo " +
            "    FROM parcelavalordivida pvd " +
            "    INNER JOIN cteparcelamento cte " +
            "    ON cte.originada = pvd.id " +
            "    INNER JOIN itemprocessoparcelamento ipp " +
            "    ON ipp.PARCELAVALORDIVIDA_ID = pvd.id " +
            "    INNER JOIN processoparcelamento pp " +
            "    ON pp.id = ipp.processoparcelamento_id " +
            "    INNER JOIN valordivida vd " +
            "    ON vd.calculo_id = pp.id " +
            "    INNER JOIN parcelavalordivida originada " +
            "    ON originada.valordivida_id = vd.id " +
            "    " +
            "    ) " +
            "  SELECT pvd.id " +
            "  FROM parcelavalordivida pvd " +
            "  INNER JOIN CTEPARCELAMENTO cte " +
            "  ON cte.originada = pvd.id " +
            "   inner join situacaoparcelavalordivida spvd on spvd.id = pvd.situacaoatual_id " +
            "  ORDER BY pvd.id DESC " +
            "    )";
        Query q = em.createNativeQuery(sql);
        q.setParameter("cda", cda.getId());
        List<Long> idsParcela = Lists.newArrayList();
        for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
            idsParcela.add(id.longValue());
        }
        return idsParcela;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    private StringBuilder geraLinhaCDA(CertidaoDividaAtiva cda, int index) {
        StringBuilder linhaCDA = new StringBuilder("");
        linhaCDA.append("1");//fixo de acordo com o layout
        linhaCDA.append(ComunicaSofPlanFacade.formatterDataSemBarra.format(LocalDate.now().toDate()));
        linhaCDA.append(StringUtil.cortarOuCompletarEsquerda(cda.getNumero() + "" + cda.getAno(), 18, "0"));

        ValoresAtualizadosCDA valoresAtualizadosCDA = calcularValoresAtualizadosDaCDA(cda);

        linhaCDA.append(StringUtil.cortarOuCompletarEsquerda(valoresAtualizadosCDA.getValorPrincipal().toString().replace(".", ""), 18, "0"));
        linhaCDA.append(StringUtil.cortarOuCompletarEsquerda(valoresAtualizadosCDA.getValorMulta().toString().replace(".", ""), 18, "0"));
        linhaCDA.append(StringUtil.cortarOuCompletarEsquerda(valoresAtualizadosCDA.getValorJuros().toString().replace(".", ""), 18, "0"));
        linhaCDA.append(StringUtil.cortarOuCompletarEsquerda(valoresAtualizadosCDA.getValorCorrecao().toString().replace(".", ""), 18, "0"));
        linhaCDA.append(StringUtil.cortarOuCompletarEsquerda(valoresAtualizadosCDA.getValorTotal().toString().replace(".", ""), 18, "0"));
        linhaCDA.append(StringUtil.cortarOuCompletarEsquerda(String.valueOf(index + 1), 7, "0"));
        return linhaCDA;
    }

    public CertidaoDividaAtiva atualizarValorCertidao(CertidaoDividaAtiva certidao) {
        ValoresAtualizadosCDA valoresAtualizadosCDA = new ValoresAtualizadosCDA();
        integraSoftplanFacade.calcularValoresDaCDA(valoresAtualizadosCDA, certidao);
        certidao.setValorOriginal(valoresAtualizadosCDA.getValorTotal());
        certidao.setValorJuros(valoresAtualizadosCDA.getValorJuros());
        certidao.setValorMulta(valoresAtualizadosCDA.getValorMulta());
        return certidao;
    }

    public List<Pessoa> recuperaPessoasComRetificacaoAindaNaoEnvidas() {
        String sql = "select dados.pessoa from DadosRetificacaoPessoaCda dados where dados.enviado <> :enviado";
        Query q = em.createQuery(sql);
        q.setParameter("enviado", Boolean.TRUE);
        return q.getResultList();
    }

    public List<CertidaoDividaAtiva> buscarCertidoesDividaAtivaDaParcela(Long idParcela) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select cda.* from CertidaoDividaAtiva cda ");
        sb.append(" inner join ItemCertidaoDividaAtiva icda on icda.certidao_id = cda.id ");
        sb.append(" inner join ValorDivida vd on vd.calculo_id = icda.itemInscricaoDividaAtiva_id ");
        sb.append(" inner join ParcelaValorDivida pvd on pvd.valorDivida_id = vd.id ");
        sb.append(" where pvd.id = :idParcela ");
        sb.append(" and cda.situacaoCertidaoDA in (:situacoes)");

        Query q = em.createNativeQuery(sb.toString(), CertidaoDividaAtiva.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoCertidaoDA.ABERTA.name(), SituacaoCertidaoDA.QUITADA.name(),
            SituacaoCertidaoDA.PETICIONADA.name(), SituacaoCertidaoDA.COMPENSADA.name()));

        List<CertidaoDividaAtiva> toReturn = q.getResultList();
        if (!toReturn.isEmpty()) {
            return toReturn;
        }
        return Lists.newArrayList();
    }

    public void salvarSqlCargaSoftPlan(String sqlCargaSofplanCDA, String sqlCargaSofplanParcelamento) {
        ParametrosDividaAtiva parametrosDividaAtiva = parametrosDividaAtivaFacade.parametrosDividaAtivaExercicioCorrente();
        parametrosDividaAtiva.setSqlCargaCDA(sqlCargaSofplanCDA);
        parametrosDividaAtiva.setSqlCargaParcelamento(sqlCargaSofplanParcelamento);
        em.merge(parametrosDividaAtiva);
    }

    public String recuperarSqlCargaSoftPlanCDA() {
        ParametrosDividaAtiva parametrosDividaAtiva = parametrosDividaAtivaFacade.parametrosDividaAtivaExercicioCorrente();
        return parametrosDividaAtiva.getSqlCargaCDA();
    }

    public String recuperarSqlCargaSoftPlanParcelamento() {
        ParametrosDividaAtiva parametrosDividaAtiva = parametrosDividaAtivaFacade.parametrosDividaAtivaExercicioCorrente();
        return parametrosDividaAtiva.getSqlCargaParcelamento();
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<ProcessoParcelamento> buscarParcelamentoBySQL(String sql, int inicio, int max) {
        logger.debug("Vai recuperar partindo de " + inicio);
        String sqlParcelamento = "select pp.id " +
            " FROM processoparcelamento pp      " +
            " inner join calculo c on c.id = pp.id " + sql;
        Query q = em.createNativeQuery(sqlParcelamento);
        q.setFirstResult(inicio);
        q.setMaxResults(max);
        List resultList = q.getResultList();
        logger.debug("recuperou " + resultList.size());
        List<ProcessoParcelamento> parcelamentos = Lists.newArrayList();
        for (Number id : (List<Number>) resultList) {
            parcelamentos.add(em.find(ProcessoParcelamento.class, id.longValue()));
        }
        return parcelamentos;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public Integer contarParcelamentoBySQL(String sql) {
        sql = "SELECT count(pp.id) FROM ProcessoParcelamento pp " + sql;
        Query q = em.createNativeQuery(sql);
        if (!q.getResultList().isEmpty()) {
            return ((BigDecimal) q.getResultList().get(0)).intValue();
        }
        return 0;
    }

    public List<CertidaoDividaAtiva> buscarCertidoesAbertasPorCadastro(Cadastro cadastro) {
        String hql = "select cda from CertidaoDividaAtiva cda " +
            " where cda.cadastro = :cadastro" +
            "  and cda.situacaoCertidaoDA = :situacaoCda";
        Query q = em.createQuery(hql);
        q.setParameter("cadastro", cadastro);
        q.setParameter("situacaoCda", SituacaoCertidaoDA.ABERTA);
        return q.getResultList();
    }

    public List<ComunicacaoSoftPlan> recuperarComunicacoesCDA(CertidaoDividaAtiva certidaoDividaAtiva) {
        String hql = "select con from ComunicacaoSoftPlan con" +
            " where con.cda = :cda order by con.dataComunicacao desc, con.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("cda", certidaoDividaAtiva);
        return q.getResultList();

    }

    public List<ComunicacaoSoftPlan> recuperarComunicacoesParcelamento(ProcessoParcelamento parcelamento) {
        String hql = "select con from ComunicacaoSoftPlan con" +
            " where con.processoParcelamento = :parcelamento order by con.dataComunicacao desc, con.id desc";
        Query q = em.createQuery(hql);
        q.setParameter("parcelamento", parcelamento);
        return q.getResultList();
    }

    public boolean exiteCDAComProblemaDeComunicacao() {
        String sql = "select cda.id from CertidaoDividaAtiva cda " +
            " where exists (select 1 from ComunicacaoSoftPlan " +
            "                where cda_id = cda.id " +
            "                  and to_number(codigoResposta) <> 0) " +
            " and not exists (select 1 from ComunicacaoSoftPlan " +
            "                  where cda_id = cda.id " +
            "                    and to_number(codigoResposta) = 0) " +
            " and cda.situacaoCertidaoDA <> :situacaoCDA";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacaoCDA", SituacaoCertidaoDA.JUNCAO_CDALEGADA.name());
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

    public List<ResultadoParcela> buscarParcelasDoCalculoOriginal(Long idCalculo) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, idCalculo);
        consultaParcela.executaConsulta();

        return consultaParcela.getResultados();
    }

    public List<ResultadoParcela> buscarParcelasDoCalculo(Long idCalculo) {
        return buscarParcelasDoCalculo(idCalculo, true);
    }

    public List<ResultadoParcela> buscarParcelasDoCalculo(Long idCalculo, boolean verificarBloqueioJudicial) {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, idCalculo);
        consultaParcela.executaConsulta();
        Set<ResultadoParcela> parcelas = Sets.newHashSet();
        parcelas.addAll(consultaParcela.getResultados());
        for (ResultadoParcela resultadoParcela : consultaParcela.getResultados()) {
            if (SituacaoParcela.PAGO_PARCELAMENTO.equals(resultadoParcela.getSituacaoEnumValue())) {
                Long idParcelaCancelamento = cancelamentoParcelamentoFacade.buscarIdParcelaDoCancelamentoDaParcelaOriginal(resultadoParcela.getIdParcela());
                if (idParcelaCancelamento != null) {
                    ConsultaParcela consultaParcelaCancelamento = new ConsultaParcela();
                    consultaParcelaCancelamento.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, idParcelaCancelamento);
                    consultaParcelaCancelamento.executaConsulta();
                    parcelas.addAll(consultaParcelaCancelamento.getResultados());

                    for (ResultadoParcela parcela : consultaParcelaCancelamento.getResultados()) {
                        if (parcela.isParcelado()) {
                            List<ProcessoParcelamento> parcelamentos = processoParcelamentoFacade.buscarParcelamentosDaParcela(parcela.getIdParcela());
                            for (ProcessoParcelamento parcelamento : parcelamentos) {
                                parcelas.addAll(buscarParcelasDoCalculo(parcelamento.getId(), verificarBloqueioJudicial));
                            }
                        } else if (parcela.getTipoCalculoEnumValue().isCancelamentoParcelamento()) {
                            parcelas.addAll(buscarParcelasDoCalculo(parcela.getIdCalculo(), verificarBloqueioJudicial));
                        }
                    }
                }
            } else if (SituacaoParcela.PARCELADO.equals(resultadoParcela.getSituacaoEnumValue()) || SituacaoParcela.ESTORNADO.equals(resultadoParcela.getSituacaoEnumValue())) {
                List<ProcessoParcelamento> parcelamentos = processoParcelamentoFacade.buscarParcelamentosDaParcela(resultadoParcela.getIdParcela());
                for (ProcessoParcelamento parcelamento : parcelamentos) {
                    parcelas.addAll(buscarParcelasDoCalculo(parcelamento.getId(), verificarBloqueioJudicial));
                }
            } else if (verificarBloqueioJudicial && resultadoParcela.isParcelaBloqueioJudicial()) {
                Long idCalculoBloqueio = bloqueioJudicialFacade.buscarIdCalculoParcelaResidual(resultadoParcela.getIdParcela());
                if (idCalculoBloqueio != null) {
                    parcelas.addAll(buscarParcelasDoCalculo(idCalculoBloqueio));
                }
            }
        }

        return Lists.newArrayList(parcelas);
    }

    public ItemCertidaoDividaAtiva recuperaItemCertidaoDividaAtiva(Long idItem) {
        String sql = "select ic.* " +
            "   from ITEMCERTIDAODIVIDAATIVA ic " +
            "  where ic.ITEMINSCRICAODIVIDAATIVA_ID = :idItem ";
        Query q = em.createNativeQuery(sql, ItemCertidaoDividaAtiva.class);
        q.setParameter("idItem", idItem);
        return q.getResultList().size() > 0 ? (ItemCertidaoDividaAtiva) q.getResultList().get(0) : null;
    }

    public CertidaoDividaAtiva salvarRetornando(CertidaoDividaAtiva certidaoDividaAtiva) {
        return em.merge(certidaoDividaAtiva);
    }

    public void adicionarNovaCDANoProcessoJudicial(CertidaoDividaAtiva certidaoDividaAtiva, CertidaoDividaAtiva cdaDesvinculada) {
        ProcessoJudicial processoJudicial = processoAjuizamentoDaCda(certidaoDividaAtiva);
        if (processoJudicial != null) {
            ProcessoJudicialCDA processoJudicialCDA = new ProcessoJudicialCDA();
            processoJudicialCDA.setCertidaoDividaAtiva(cdaDesvinculada);
            processoJudicialCDA.setProcessoJudicial(processoJudicial);
            em.merge(processoJudicialCDA);
        }
    }

    public int contarCertidoesNaoComunicadasPorServiceId(ComunicaSofPlanFacade.ServiceId serviceId) {
        try {
            String sql = "SELECT count(cda.id) " + getFromNaoInteradas();
            Query q = em.createNativeQuery(sql);
            q.setParameter("serviceId", serviceId.id);
            return ((Number) q.getResultList().get(0)).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    private String getFromNaoInteradas() {
        return " FROM CERTIDAODIVIDAATIVA cda " +
            " INNER JOIN cadastro " +
            " ON cadastro.id =cda.cadastro_id " +
            " INNER JOIN pessoa " +
            " ON pessoa.id = cda.pessoa_id " +
            " inner join exercicio ex on ex.id = cda.exercicio_id " +
            " left join cadastroimobiliario ci on ci.id = cadastro.id " +
            " left join cadastroeconomico ce on ce.id = cadastro.id " +
            " left join cadastrorural cr on cr.id = cadastro.id " +
            "  " +
            " left join pessoafisica pf on pf.id = pessoa.id " +
            " left join pessoajuridica pj on pj.id = pessoa.id " +
            "  " +
            " WHERE EXISTS " +
            "   (SELECT 1 " +
            "   FROM ComunicacaoSoftPlan " +
            "   WHERE cda_id                   = cda.id " +
            "   AND SERVICEID                  = :serviceId " +
            "   AND to_number(codigoResposta) <> 0 " +
            "   ) " +
            " AND NOT EXISTS " +
            "   (SELECT 1 " +
            "   FROM ComunicacaoSoftPlan " +
            "   WHERE cda_id                  = cda.id " +
            "   AND SERVICEID                 = :serviceId " +
            "   AND to_number(codigoResposta) = 0 " +
            "   ) " +
            " And cda.SITUACAOCERTIDAODA <> 'JUNCAO_CDALEGADA' " +
            " order by ex.ano asc, cda.numero asc";
    }

    public List<ConsultaCertidaoDividaAtivaNaoComunicadasControlador.CdaDTO> buscarCertidoesNaoComunicadasPorServiceId(int first, int pageSize, ComunicaSofPlanFacade.ServiceId serviceId) {
        try {
            String sql = "SELECT cda.id, cda.numero ||'/'|| ex.ano as cda, " +
                " cda.DATACERTIDAO as geracao, " +
                " cda.SITUACAOCERTIDAODA as situacao, " +
                " coalesce(pf.nome, pj.razaosocial) as nome, " +
                " coalesce(pf.cpf, pj.cnpj) as cpfcnpj, " +
                " coalesce(ci.inscricaocadastral, ce.inscricaocadastral, to_char(cr.codigo)) as cadastro " +
                getFromNaoInteradas();

            Query q = em.createNativeQuery(sql);
            q.setParameter("serviceId", serviceId.id);
            q.setMaxResults(pageSize);
            q.setFirstResult(first);
            List<Object[]> resultado = q.getResultList();
            List<ConsultaCertidaoDividaAtivaNaoComunicadasControlador.CdaDTO> dtos = Lists.newArrayList();
            for (Object[] obj : resultado) {
                ConsultaCertidaoDividaAtivaNaoComunicadasControlador.CdaDTO dto = new ConsultaCertidaoDividaAtivaNaoComunicadasControlador.CdaDTO();
                dto.setId(((Number) obj[0]).longValue());
                dto.setNumero((String) obj[1]);
                dto.setDataGeracao((Date) obj[2]);
                dto.setSituacao(SituacaoCertidaoDA.valueOf((String) obj[3]).getDescricao());
                dto.setNomePessoa((String) obj[4]);
                dto.setCpfPessoa((String) obj[5]);
                dto.setInscricaoCadastral((String) obj[5]);
                dtos.add(dto);
            }
            return dtos;
        } catch (Exception e) {
            e.printStackTrace();
            return Lists.newArrayList();
        }
    }


    public List<ResultadoParcela> separarParcelasDoUltimoParcelamentoAtivo(ValoresAtualizadosCDA valoresAtualizadosCDA, List<ResultadoParcela> parcelasDaCda) {
        List<Long> idsParcelamento = Lists.newArrayList();
        for (ResultadoParcela resultadoParcela : parcelasDaCda) {
            if (Calculo.TipoCalculo.PARCELAMENTO.equals(resultadoParcela.getTipoCalculoEnumValue())) {
                if (!idsParcelamento.contains(resultadoParcela.getIdCalculo())) {
                    idsParcelamento.add(resultadoParcela.getIdCalculo());
                }
            }
        }
        Collections.sort(idsParcelamento);
        Long idDoParcelamentoAtivo = null;
        for (Long idParcelamento : idsParcelamento) {
            if (idDoParcelamentoAtivo == null) {
                VOProcessoParcelamento parcelamento = processoParcelamentoFacade.recuperarIdNumeroSituacao(idParcelamento);
                if (parcelamento.getSituacao().equals(SituacaoParcelamento.FINALIZADO)
                    || parcelamento.getSituacao().equals(SituacaoParcelamento.PAGO)) {
                    if (valoresAtualizadosCDA != null) {
                        String detalhe = "Último Parcelamento: " + parcelamento.getNumero() + "/" + parcelamento.getAno();
                        if (!valoresAtualizadosCDA.getDetalhamento().contains(detalhe)) {
                            valoresAtualizadosCDA.getDetalhamento().add(detalhe);
                        }
                    }
                    idDoParcelamentoAtivo = idParcelamento;
                    break;
                }
            }
        }
        List<ResultadoParcela> parcelasDoParcelamentoAtivo = Lists.newArrayList();
        for (ResultadoParcela resultadoParcela : parcelasDaCda) {
            if (resultadoParcela.getIdCalculo().equals(idDoParcelamentoAtivo)) {
                parcelasDoParcelamentoAtivo.add(resultadoParcela);
            }
        }
        return parcelasDoParcelamentoAtivo;
    }

    public ResultadoParcela separarValorOriginalDaCda(List<ResultadoParcela> parcelas) {
        for (ResultadoParcela parcela : parcelas) {
            if ((parcela.getTipoCalculoEnumValue().isInscricaoDividaAtiva()
                || parcela.getTipoCalculoEnumValue().isCancelamentoParcelamento())
                && !parcela.isCancelado()
                && !SituacaoParcela.PAGO_PARCELAMENTO.equals(parcela.getSituacaoEnumValue())) {
                return parcela;
            }
        }
        return null;
    }

    public BigDecimal separarValorTotalDoUltimoParcelamento(List<ResultadoParcela> parcelasDoUltimoParcelamentoAtivo) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        for (ResultadoParcela parcela : parcelasDoUltimoParcelamentoAtivo) {
            if (parcela.isEmAberto() || parcela.getSituacaoEnumValue().isAguardandoPagamentoBloqueioJudicial()) {
                valorTotal = valorTotal.add(parcela.getValorTotalSemHonorarios());
            }
        }
        return valorTotal;
    }

    public BigDecimal buscarValorOriginalDoUltimoParcelamento(List<ResultadoParcela> parcelasDoUltimoParcelamentoAtivo) {
        BigDecimal valorTotal = BigDecimal.ZERO;
        if (!parcelasDoUltimoParcelamentoAtivo.isEmpty()) {
            Long idParcelamento = parcelasDoUltimoParcelamentoAtivo.get(0).getIdCalculo();
            String sql = "select coalesce(sum(coalesce(ipp.imposto,0) + " +
                "                             coalesce(ipp.taxa,0) + " +
                "                             coalesce(ipp.juros,0) + " +
                "                             coalesce(ipp.multa,0) + " +
                "                             coalesce(ipp.correcao,0)),0) as valor " +
                " from ItemProcessoParcelamento ipp " +
                " where ipp.processoParcelamento_id = :idParcelamento";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idParcelamento", idParcelamento);
            valorTotal = (BigDecimal) q.getSingleResult();
        }
        return valorTotal;
    }

    public void alterarSituacaoDaCdaPorTipoProcessoDebito(ProcessoDebito processo, Calculo calculo) {
        String sql = "select cda.* from CertidaoDividaAtiva cda " +
            "inner join ItemCertidaoDividaAtiva icda on icda.CERTIDAO_ID = cda.id " +
            "inner join ITEMINSCRICAODIVIDAATIVA iida on iida.id = icda.ITEMINSCRICAODIVIDAATIVA_ID " +
            "where iida.id = :idCalculo";
        Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class);
        q.setParameter("idCalculo", calculo.getId());
        q.setMaxResults(1);
        List<CertidaoDividaAtiva> cdas = q.getResultList();
        if (!cdas.isEmpty()) {
            boolean cdaFoiAlterada = false;
            CertidaoDividaAtiva cda = cdas.get(0);
            switch (processo.getTipo()) {
                case BAIXA:
                    if (SituacaoProcessoDebito.FINALIZADO.equals(processo.getSituacao())) {
                        cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.QUITADA);
                    } else {
                        cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.ABERTA);
                    }
                    cdaFoiAlterada = true;
                    break;
                case CANCELAMENTO:
                    if (SituacaoProcessoDebito.FINALIZADO.equals(processo.getSituacao())) {
                        cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.CANCELADA);
                    } else {
                        cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.ABERTA);
                    }
                    cdaFoiAlterada = true;
                    break;
                case REATIVACAO:
                    if (SituacaoProcessoDebito.FINALIZADO.equals(processo.getSituacao())) {
                        cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.ABERTA);
                    } else {
                        cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.CANCELADA);
                    }
                    cdaFoiAlterada = true;
                    break;
                case COMPENSACAO:
                    if (SituacaoProcessoDebito.FINALIZADO.equals(processo.getSituacao())) {
                        cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.COMPENSADA);
                    } else {
                        cda.setSituacaoCertidaoDA(SituacaoCertidaoDA.ABERTA);
                    }
                    cdaFoiAlterada = true;
                    break;
            }
            if (cdaFoiAlterada) {
                cda = em.merge(cda);
                comunicaSofPlan(cda);
            }
        }
    }

    private void comunicaSofPlan(CertidaoDividaAtiva cda) {
        try {
            comunicaSofPlanFacade.enviarCDA(Lists.newArrayList(cda));
        } catch (Exception ex) {
            logger.error("Erro ao enviar a CDA. {}", ex.getMessage());
            logger.debug("Detalhes do erro ao enviar a CDA.", ex);
        }
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<VoExportacaoCertidaoDividaAtiva> buscarDadosExportacaoCertidaoDividaAtiva(AssistenteBarraProgresso assistenteBarraProgresso) {
        List<VoExportacaoCertidaoDividaAtiva> dados = Lists.newArrayList();
        assistenteBarraProgresso.setDescricaoProcesso("Consultando Certidões...");
        String sql = " select " +
            "       cda.id, " +
            "       e.ano as exercicio, " +
            "       cda.numero, " +
            "       coalesce(pf.cpf, pj.cnpj) as cpf_cnpj, " +
            "       coalesce(pf.nome, pj.razaosocial) as nome_razaosocial, " +
            "       coalesce(ce.inscricaocadastral, ci.inscricaocadastral, to_char(cr.codigo)) as inscricaocadastral, " +
            "       cda.situacaocertidaoda as situacao, " +
            "       (select count(distinct pp.id) " +
            "           from itemcertidaodividaativa icda " +
            "          inner join iteminscricaodividaativa iida on iida.id = icda.iteminscricaodividaativa_id " +
            "          inner join valordivida vd on vd.calculo_id = iida.id " +
            "          inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            "          inner join itemprocessoparcelamento ipp on ipp.parcelavalordivida_id = pvd.id " +
            "          inner join processoparcelamento pp on pp.id = ipp.processoparcelamento_id " +
            "        where icda.certidao_id = cda.id" +
            "          and pp.situacaoparcelamento in (:situacao_finalizado, :situacao_pago) ) as quantidade_parcelamentos " +
            "   from certidaodividaativa cda " +
            "  inner join exercicio e on e.id = cda.exercicio_id " +
            "  left join pessoafisica pf on pf.id = cda.pessoa_id " +
            "  left join pessoajuridica pj on pj.id = cda.pessoa_id " +
            "  left join cadastroimobiliario ci on ci.id = cda.cadastro_id " +
            "  left join cadastroeconomico ce on ce.id = cda.cadastro_id " +
            "  left join cadastrorural cr on cr.id = cda.cadastro_id " +
            "where exists (select 1 " +
            "                 from itemcertidaodividaativa icda " +
            "                inner join iteminscricaodividaativa iida on iida.id = icda.iteminscricaodividaativa_id " +
            "                inner join valordivida vd on vd.calculo_id = iida.id " +
            "                inner join parcelavalordivida pvd on pvd.valordivida_id = vd.id " +
            "                inner join itemprocessoparcelamento ipp on ipp.parcelavalordivida_id = pvd.id " +
            "                inner join processoparcelamento pp on pp.id = ipp.processoparcelamento_id " +
            "              where icda.certidao_id = cda.id and pp.situacaoparcelamento = :situacao_finalizado) " +
            "  and cda.situacaocertidaoda = :situacao_certidao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao_certidao", SituacaoCertidaoDA.QUITADA.name());
        q.setParameter("situacao_finalizado", SituacaoParcelamento.FINALIZADO.name());
        q.setParameter("situacao_pago", SituacaoParcelamento.PAGO.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            assistenteBarraProgresso.setDescricaoProcesso("Atualizando valores da certidões...");
            assistenteBarraProgresso.setTotal(resultList.size());
            assistenteBarraProgresso.removerProcessoDoAcompanhamento();
            for (Object[] obj : (List<Object[]>) resultList) {
                VoExportacaoCertidaoDividaAtiva vo = VoExportacaoCertidaoDividaAtiva.fromObject(obj);
                CertidaoDividaAtiva certidaoDividaAtiva = atualizarValorCertidao(recuperar(vo.getId()));
                vo.setValorAtualizado(certidaoDividaAtiva.getValorOriginal()
                    .add(certidaoDividaAtiva.getValorJuros())
                    .add(certidaoDividaAtiva.getValorMulta()));
                dados.add(vo);
                assistenteBarraProgresso.conta();
            }
        }
        return dados;
    }

    public ProcessoDeProtesto buscarProcessoAtivoDaParcela(Long idParcela) {
        return consultaDebitoFacade.buscarProcessoAtivoDaParcela(idParcela);
    }

    public InscricaoDividaAtiva recuperarInscricaoDividaAtiva(Long idItemInscricao) {
        String sql = " select pc.*, ida.* from InscricaoDividaAtiva ida " +
            " inner join ProcessoCalculo pc on pc.id = ida.id " +
            " inner join ItemInscricaoDividaAtiva iteminsc on iteminsc.inscricaoDividaAtiva_id = ida.id " +
            " where iteminsc.id = :idItemInscricao ";
        Query q = em.createNativeQuery(sql, InscricaoDividaAtiva.class);
        q.setParameter("idItemInscricao", idItemInscricao);
        List resultList = q.getResultList();
        return resultList.size() > 0 ? (InscricaoDividaAtiva) resultList.get(0) : null;
    }
}
