/*
 * Codigo gerado automaticamente em Fri Aug 05 11:11:20 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.SiprevControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.rh.ArquivoMargemVinculo;
import br.com.webpublico.entidades.rh.administracaodepagamento.FilaProcessamentoFolha;
import br.com.webpublico.entidades.rh.administracaodepagamento.LancamentoTercoFeriasAut;
import br.com.webpublico.entidades.rh.concursos.ClassificacaoInscricao;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.VinculoFPEventoEsocial;
import br.com.webpublico.entidades.rh.parametro.cargocomissaofuncaogratificada.ParametroControleCargoFG;
import br.com.webpublico.entidadesauxiliares.ItemVaga;
import br.com.webpublico.entidadesauxiliares.rh.esocial.VWContratoFP;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.rh.esocial.TipoOperacaoESocial;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.dto.BuscarEventoDTO;
import br.com.webpublico.esocial.dto.EventoESocialDTO;
import br.com.webpublico.esocial.service.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.FolhaCalculavel;
import br.com.webpublico.negocios.rh.administracaodepagamento.FilaProcessamentoFolhaFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.LancamentoTercoFeriasAutFacade;
import br.com.webpublico.negocios.rh.concursos.ClassificacaoConcursoFacade;
import br.com.webpublico.negocios.rh.configuracao.ConfiguracaoRHFacade;
import br.com.webpublico.negocios.rh.esocial.CategoriaTrabalhadorFacade;
import br.com.webpublico.negocios.rh.parametro.cargocomissaofuncaogratificada.ParametroControleCargoFGFacade;
import br.com.webpublico.pessoa.dto.ContratoFPDTO;
import br.com.webpublico.util.*;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.joda.time.DateTime;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
public class ContratoFPFacade extends AbstractFacade<ContratoFP> {

    private static final double DIAS_ANOS = 365.25;
    public static final int PREFEITO_SECRETARIO = 5;
    @EJB
    private HorarioContratoFPFacade horarioContratoFPFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacadeOLD hierarquiaOrganizacionalFacadeOLD;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private TipoProvimentoFacade tipoProvimentoFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private PastaGavetaFacade pastaGavetaFacade;
    @EJB
    private PrevidenciaVinculoFPFacade previdenciaVinculoFPFacade;
    @EJB
    private OpcaoValeTransporteFPFacade opcaoValeTransporteFPFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private SindicatoVinculoFPFacade sindicatoVinculoFPFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private SituacaoContratoFPFacade situacaoContratoFPFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private LancamentoFPFacade lancamentoFPFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private ClassificacaoConcursoFacade classificacaoConcursoFacade;
    @EJB
    private ConfiguracaoRHFacade configuracaoRHFacade;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    @EJB
    private CategoriaTrabalhadorFacade categoriaTrabalhadorFacade;
    @EJB
    private ParametroControleCargoFGFacade parametroControleCargoFGFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private LancamentoTercoFeriasAutFacade lancamentoTercoFeriasAutFacade;
    @EJB
    private FilaProcessamentoFolhaFacade filaProcessamentoFolhaFacade;
    private ESocialService eSocialService;

    private S2190Service s2190Service;
    private S2200Service s2200Service;
    private S2205Service s2205Service;
    private S2206Service s2206Service;
    private S1200Service s1200Service;
    private EmpregadorService empregadorService;

    public ContratoFPFacade() {
        super(ContratoFP.class);
        eSocialService = (ESocialService) Util.getSpringBeanPeloNome("eSocialService");
        s2190Service = (S2190Service) Util.getSpringBeanPeloNome("s2190Service");
        s2200Service = (S2200Service) Util.getSpringBeanPeloNome("s2200Service");
        s2205Service = (S2205Service) Util.getSpringBeanPeloNome("s2205Service");
        s2206Service = (S2206Service) Util.getSpringBeanPeloNome("s2206Service");
        s1200Service = (S1200Service) Util.getSpringBeanPeloNome("s1200Service");
        empregadorService = (EmpregadorService) Util.getSpringBeanPeloNome("empregadorService");
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EventoFPFacade getEventoFPFacade() {
        return eventoFPFacade;
    }

    public CategoriaTrabalhadorFacade getCategoriaTrabalhadorFacade() {
        return categoriaTrabalhadorFacade;
    }

    public ContratoFP salvaRetornando(ContratoFP contratoFP) {
        return em.merge(contratoFP);
    }

    public VinculoFP recuperarContratoMatricula(String matricula, String numero) {
        Query createQuery = em.createQuery("select contrato from VinculoFP contrato inner join contrato.matriculaFP matricula"
            + " where matricula.matricula = :matricula and contrato.numero = :numero");
        createQuery.setParameter("numero", numero);
        createQuery.setParameter("matricula", matricula);
        createQuery.setMaxResults(1);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        }
        return (VinculoFP) createQuery.getResultList().get(0);
    }

    /**
     * consulta nativa
     *
     * @param matricula
     * @param numero
     * @return um vinculofp
     */
    public VinculoFP recuperarContratoMatriculaNative(String matricula, String numero) {
        StringBuilder sb = new StringBuilder();
        sb.append(" select v.id From VINCULOFP v ");
        sb.append(" inner join MATRICULAFP m on v.MATRICULAFP_ID = m.ID ");
        sb.append(" where m.MATRICULA = :matricula ");
        sb.append(" and v.NUMERO = :numero and rownum <=1 ");
        Query createQuery = em.createNativeQuery(sb.toString());
        createQuery.setParameter("numero", numero);
        createQuery.setParameter("matricula", matricula);
        BigDecimal ids = new BigDecimal(BigInteger.ZERO);
        if (createQuery.getResultList().isEmpty()) {
            return null;
        } else {
            ids = (BigDecimal) createQuery.getResultList().get(0);
        }
        VinculoFP v = (VinculoFP) em.createQuery("from VinculoFP v where v.id = :id").setParameter("id", ids.longValue()).getSingleResult();

        return v;
    }

    public String retornaCodigo(MatriculaFP matricula) {
        String num;
        String sql = " SELECT max(cast(COALESCE(b.numero,'0') AS INTEGER)) "
            + " FROM VinculoFP b WHERE b.matriculaFP_id = :matricula ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("matricula", matricula.getId());
        q.setMaxResults(1);

        if (!q.getResultList().isEmpty()) {
            Long l = 1l;
            if (q.getSingleResult() != null) {
                l += Long.parseLong(q.getSingleResult().toString());
            }

            return String.valueOf(l);
        }

        return "1";
    }

    public ContratoFP recuperarSomenteContrato(Object id) {
        ContratoFP con = em.find(ContratoFP.class, id);
        return con;
    }

    public ContratoFP recuperarContratoComSituacaoFuncional(Object id) {
        ContratoFP contratoFP = em.find(ContratoFP.class, id);
        Hibernate.initialize(contratoFP.getSituacaoFuncionals());
        Hibernate.initialize(contratoFP.getSituacaoFuncionalsBkp());
        return contratoFP;
    }

    public ContratoFP recuperarContratoComLotacaoFuncional(Object id) {
        ContratoFP contratoFP = em.find(ContratoFP.class, id);
        Hibernate.initialize(contratoFP.getLotacaoFuncionals());
        return contratoFP;
    }

    @Override
    public Object recuperar(Class entidade, Object id) {
        return recuperar(id);
    }

    @Override
    public ContratoFP recuperar(Object id) {
        ContratoFP con = em.find(ContratoFP.class, id);
        con.getLotacaoFuncionals().size();
        con.getPrevidenciaVinculoFPs().size();
        for (PrevidenciaVinculoFP previdenciaVinculoFP : con.getPrevidenciaVinculoFPs()) {
            if (previdenciaVinculoFP.getPrevidenciaArquivos() != null) {
                Hibernate.initialize(previdenciaVinculoFP.getPrevidenciaArquivos());
            }
        }
        con.getOpcaoValeTransporteFPs().size();
        con.getPeriodosAquisitivosFLs().size();
        con.getSindicatosVinculosFPs().size();
        con.getSituacaoFuncionals().size();
        con.getAssociacaosVinculosFPs().size();
        con.getContratoVinculoDeContratos().size();
        con.getLotacaoFolhaExercicios().size();
        con.getOpcaoSalarialVinculoFP().size();
        con.getRecursosDoVinculoFP().size();
        con.getExoneracoesRescisao().size();
        con.getLotacaoOutrosVinculos().size();
        con.getFichasFinanceiraFP().size();
        con.getCargos().size();
        con.getPastasContratosFP().size();
        con.getEnquadramentos().size();
        con.getModalidadeContratoFPDatas().size();
        return con;
    }

    public ContratoFP recuperarContratoComHierarquia(Object id) {
        ContratoFP contrato = em.find(ContratoFP.class, id);
        if (contrato.getUnidadeOrganizacional() != null) {
            contrato.getUnidadeOrganizacional().getHierarquiasOrganizacionais().size();
        }
        return contrato;
    }


    public ContratoFP recuperarSomentePeriodosAquisitivos(Object id) {
        ContratoFP con = em.find(ContratoFP.class, id);
        con.getPeriodosAquisitivosFLs().size();
        return con;
    }

    /**
     * @param s          "String passada pelo autocomplete"
     * @param "atributos da classe nao está sendo utilizado" Método
     *                   Criado utilizando realizando a mesma função que o método listaFiltrando.
     * @return retorna uma lista de bancos, filtrados pela descrição e pelo
     * numero do Banco.
     * @author Peixe
     */
    public List<ContratoFP> recuperaContrato(String s) {
        String hql = "select obj from ContratoFP obj "
            + " inner join obj.matriculaFP matricula"
            + " where (lower(obj.matriculaFP.pessoa.nome) like :filtro OR lower(obj.matriculaFP.pessoa.cpf) like :filtro OR matricula.matricula like :filtro) "
            + " and :dataVigencia >= obj.inicioVigencia and "
            + " :dataVigencia <= coalesce(obj.finalVigencia,:dataVigencia)";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratosComCargoVincABasePAFerias(String s, Date dataVerificacao, HierarquiaOrganizacional hierarquiaOrganizacional) {
        List<ContratoFP> contratosFPs = new ArrayList<>();

        String sql = "select distinct contrato.id from contratofp contrato " +
            "         inner join vinculofp v on v.id = contrato.id " +
            "         inner join matriculafp matricula on v.matriculafp_id = matricula.id  " +
            "         inner join pessoafisica pf on matricula.pessoa_id = pf.id " +
            "         inner join lotacaofuncional lotacao on lotacao.vinculofp_id = contrato.id  " +
            "         inner join unidadeorganizacional un on un.id = lotacao.unidadeorganizacional_id  " +
            "         inner join vwhierarquiaadministrativa ho on ho.subordinada_id = un.id " +
            " where :dataVigencia between v.iniciovigencia and coalesce(v.finalvigencia, :dataVigencia) " +
            "  and exists(select 1 " +
            "             from cargo c " +
            "                      inner join basecargo b on b.cargo_id = c.id " +
            "                      inner join baseperiodoaquisitivo bpa on bpa.id = b.baseperiodoaquisitivo_id " +
            "             where c.id = contrato.CARGO_ID " +
            "               and bpa.TIPOPERIODOAQUISITIVO = 'FERIAS')" +
            " and (lower(pf.nome) like :filtro OR lower(pf.cpf) like :filtro OR matricula.matricula like :filtro) ";
        if (hierarquiaOrganizacional != null) {
            sql += " and ho.codigo like :unidade";
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        if(dataVerificacao != null){
            q.setParameter("dataVigencia", dataVerificacao);
        } else {
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        }
        if (hierarquiaOrganizacional != null) {
            q.setParameter("unidade", hierarquiaOrganizacional.getCodigoSemZerosFinais() + "%");
        }
        q.setMaxResults(10);

        for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
            contratosFPs.add(em.find(ContratoFP.class, id.longValue()));
        }
        return contratosFPs;
    }

    public List<ContratoFP> recuperaContratoComEnquadramento(String s) {
        String hql = "select obj from ContratoFP obj, EnquadramentoFuncional ef "
            + " inner join obj.matriculaFP matricula "
            + " where (lower(obj.matriculaFP.pessoa.nome) like :filtro OR lower(obj.matriculaFP.pessoa.cpf) like :filtro OR matricula.matricula like :filtro) "
            + " and :dataVigencia >= obj.inicioVigencia  and ef.contratoServidor = obj and "
            + " :dataVigencia <= coalesce(obj.finalVigencia,:dataVigencia)"
            + " and :dataVigencia >= ef.inicioVigencia and "
            + " :dataVigencia <= coalesce(ef.finalVigencia,:dataVigencia)";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratoEstatutarioComEnquadramento(String s) {
        String hql = "select obj from ContratoFP obj, EnquadramentoFuncional ef "
            + " inner join obj.matriculaFP matricula "
            + " where (lower(obj.matriculaFP.pessoa.nome) like :filtro OR lower(obj.matriculaFP.pessoa.cpf) like :filtro OR matricula.matricula like :filtro) "
            + " and :dataVigencia >= obj.inicioVigencia  and ef.contratoServidor = obj and "
            + " :dataVigencia <= coalesce(obj.finalVigencia,:dataVigencia)"
            + " and :dataVigencia >= ef.inicioVigencia and "
            + " :dataVigencia <= coalesce(ef.finalVigencia,:dataVigencia) "
            + " and obj.tipoRegime.codigo = :tipoRegime  ";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoRegime", new Long(2));
        return q.getResultList();
    }

    public List<ContratoFP> buscarContratoPorTipoRegime(String s, Long... tipoRegime) {
        String hql = "select new ContratoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome) from ContratoFP obj where obj.tipoRegime.codigo in (:tiposRegime) "
            + " and (lower(obj.matriculaFP.pessoa.nome) like :filtro " +
            "   or lower(obj.matriculaFP.matricula) like :filtro  "
            + " or lower(obj.matriculaFP.pessoa.cpf) like :filtro) "
            + " and :dataVigencia >= obj.inicioVigencia and "
            + " :dataVigencia <= coalesce(obj.finalVigencia,:dataVigencia)";
        Query q = em.createQuery(hql);
        q.setMaxResults(25);

        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setParameter("tiposRegime", Arrays.asList(tipoRegime));
        return q.getResultList();
    }

    public boolean verificaContratoEnquadramentoFuncional(ContratoFP contrato) {
        String hql = "from EnquadramentoFuncional e where e.contratoServidor = :contrato";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", contrato);
        List<EnquadramentoFuncional> lista = q.getResultList();
        for (EnquadramentoFuncional e : lista) {
            if (e.getFinalVigencia() == null) {
                if (TipoPCS.QUADRO_EFETIVO.equals(e.getCategoriaPCS().getPlanoCargosSalarios().getTipoPCS()) || TipoPCS.QUADRO_TEMPORARIO.equals(e.getCategoriaPCS().getPlanoCargosSalarios().getTipoPCS())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean contratoPossuiEnquadramento(ContratoFP contrato) {
        String hql = "from EnquadramentoFuncional e where e.contratoServidor = :contrato";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", contrato);
        q.setMaxResults(1);

        try {
            return q.getSingleResult() != null;
        } catch (NoResultException e) {
            return false;
        }
    }

    public List<ContratoFP> recuperaContratosPorMatriculaFP(MatriculaFP matriculaFP) {
        Query q = em.createQuery(" from ContratoFP cfp "
            + " where cfp.matriculaFP = :matricula "
            + " and :dataVigencia >= cfp.inicioVigencia "
            + " and :dataVigencia <= coalesce(cfp.finalVigencia,:dataVigencia) and cfp.class = ContratoFP "
            + " order by cfp.inicioVigencia ");

        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setParameter("matricula", matriculaFP);
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ContratoFP> recuperaOutrosContratosMesmaMatriculaFP(ContratoFP contratoFP) {
        if (contratoFP == null) {
            return Lists.newArrayList();
        }
        String sql = " select c.id, m.matricula||'/'||v.numero||' - '||pf.nome  " +
            " from contratofp c  " +
            " inner join vinculofp v on v.id = c.id " +
            " inner join matriculafp m on m.id = v.matriculafp_id " +
            " inner join pessoafisica pf on pf.id = m.pessoa_id " +
            " where c.id <> :contratoFPId " +
            " AND m.id = :matriculaFPId " +
            " and :dataVigencia between v.iniciovigencia and coalesce(v.finalvigencia, :dataVigencia) " +
            " ORDER BY v.iniciovigencia ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("contratoFPId", contratoFP.getId());
        q.setParameter("matriculaFPId", contratoFP.getMatriculaFP().getId());

        List<Object[]> retorno = q.getResultList();
        List<ContratoFP> resultado = Lists.newArrayList();
        if (retorno != null) {
            for (Object[] o : retorno) {
                ContratoFP contrato = new ContratoFP(o[0] != null ? ((BigDecimal) o[0]).longValue() : null, (String) o[1]);
                resultado.add(contrato);
            }
        }
        return resultado;
    }

    public List<ContratoFP> recuperaTodosContratosPorMatriculaFP(MatriculaFP matriculaFP) {
        Query q = em.createQuery(" from ContratoFP cfp "
            + " where cfp.matriculaFP = :matricula ");

        q.setParameter("matricula", matriculaFP);

        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ContratoFP> recuperarTodosContratosEncerradosPorMatriculaFP(MatriculaFP matriculaFP) {
        Query q = em.createQuery(" from ContratoFP cfp "
            + " where cfp.matriculaFP = :matricula and cfp.finalVigencia is not null and cfp.finalVigencia < :dataOperacao");
        q.setParameter("matricula", matriculaFP);
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ContratoFP> recuperaContratosPorUnidadeMatriculada(UnidadeOrganizacional unidade, Date data) {
        Query q = em.createQuery("from ContratoFP cfp where cfp.matriculaFP.unidadeMatriculado = :uo and :dataVigencia >= cfp.inicioVigencia and "
            + " :dataVigencia <= coalesce(cfp.finalVigencia,:dataVigencia)");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(data));
        q.setParameter("uo", unidade);
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public List<ContratoFP> recuperaContratosPorUnidadeMatriculadaRecursiva(HierarquiaOrganizacional unidade) {
        List<ContratoFP> contratosFPs = new ArrayList<>();
        Query q = em.createQuery("from ContratoFP v where v.unidadeOrganizacional = :unidade and :dataHoje >= v.inicioVigencia and "
            + " :dataHoje <= coalesce(v.finalVigencia,:dataHoje)");
        q.setParameter("unidade", unidade.getSubordinada());
        q.setParameter("dataHoje", Util.getDataHoraMinutoSegundoZerado(new Date()));
        for (Object o : q.getResultList()) {
            contratosFPs.add((ContratoFP) o);
        }
        for (HierarquiaOrganizacional u : hierarquiaOrganizacionalFacadeOLD.filtrandoHierarquiaHorganizacionalEntidadeTipo(" ".trim(), unidade.getTipoHierarquiaOrganizacional().toString(), unidade.getExercicio())) {
            q = em.createQuery("from ContratoFP v where v.unidadeOrganizacional = :unidade and :dataHoje >= v.inicioVigencia and "
                + " :dataHoje <= coalesce(v.finalVigencia,:dataHoje)");
            q.setParameter("unidade", u.getSubordinada());
            q.setParameter("dataHoje", Util.getDataHoraMinutoSegundoZerado(new Date()));
            for (ContratoFP obj : (List<ContratoFP>) q.getResultList()) {
                if (!contratosFPs.contains(obj)) {
                    contratosFPs.add(obj);
                }
            }
        }
        return contratosFPs;
    }

    public List<ContratoFP> recuperaContratosPorUnidadeOrganizacionalPossuindoEntidadeComPessoaJuridica() {
        Query q = em.createQuery("select cfp from ContratoFP cfp "
            + " inner join cfp.unidadeOrganizacional uo "
            + " inner join uo.entidade ent "
            + " inner join ent.pessoaJuridica pessoa "
            + " where cfp.unidadeOrganizacional.id = uo.id "
            + " and uo.entidade.id = ent.id "
            + " and ent.pessoaJuridica.id = pessoa.id ");
        try {
            return q.getResultList();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean verificaSeExite(String numero, MatriculaFP matricula) {
        Query q = em.createQuery("from ContratoFP b where b.numero=:numero and b.matriculaFP = :matricula");
        q.setParameter("numero", numero);
        q.setParameter("matricula", matricula);
        return q.getResultList().isEmpty();
    }

    /**
     * Método utilizando para editar, casa o codigo ja esteja em utilização não
     * salva.
     *
     * @param
     * @return
     */
    public boolean verificaCodigoEditar(ContratoFP contrato) {
        Query q = em.createQuery("from ContratoFP e where (e.numero = :numero and e = :contrato)");
        q.setParameter("contrato", contrato);
        q.setParameter("numero", contrato.getNumero());
        return !q.getResultList().isEmpty();
    }

    public EntityManager getEm() {
        return em;
    }

    public void setEm(EntityManager em) {
        this.em = em;
    }

    /**
     * recupear contratos por numero de matricula e numero do contrato Obs:
     * recupera somente contrato vigente
     *
     * @param s
     * @return Lista de Contratos.
     */
    public List<ContratoFP> buscaContratoVigenteFiltrandoAtributosMatriculaFP(String s) {
        String hql = "select new ContratoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome||' ('||obj.matriculaFP.pessoa.nomeTratamento||')') " +
            "       from ContratoFP obj, VwHierarquiaAdministrativa vw " +
            " inner join obj.lotacaoFuncionals lotacao " +
            " inner join lotacao.unidadeOrganizacional un " +
            "      where un.id = vw.subordinadaId " +
            "        and :dataVigencia between obj.inicioVigencia and coalesce(obj.finalVigencia,:dataVigencia) " +
            "        and to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy') between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia,to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy')) " +
            "        and to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy') between to_date(vw.inicioVigencia,'dd/mm/yyyy') and coalesce(to_date(vw.fimVigencia,'dd/mm/yyyy'),to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy')) " +
            "        and substring(vw.codigo,1,5) in " +
            "            (select substring(orgao.codigo,1,5) from UsuarioSistema usu, HierarquiaOrganizacional orgao " +
            "            inner join usu.usuarioUnidadeOrganizacional uno " +
            "            where orgao.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' and orgao.indiceDoNo = 2 and uno.unidadeOrganizacional.id = orgao.subordinada.id and usu.id =  " + sistemaFacade.getUsuarioCorrente().getId() +
            "            and to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy') between to_date(orgao.inicioVigencia,'dd/mm/yyyy') and coalesce(to_date(orgao.fimVigencia,'dd/mm/yyyy'),to_date('" + Util.dateToString(sistemaFacade.getDataOperacao()) + "','dd/mm/yyyy'))) " +
            "        and ((lower(obj.matriculaFP.pessoa.nome) like :filtro) " +
            "         or (lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro) " +
            "         or (lower(obj.numero) like :filtro) " +
            "         or (lower(obj.matriculaFP.matricula) like :filtro)) ";

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<ContratoFP> buscarContratosFiltrandoAtributosMatriculaFP(String s) {
        String hql = "select new ContratoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome) from ContratoFP obj where ";
        hql += " ((lower(obj.matriculaFP.pessoa.nome) like :filtro) "
            + " OR (lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro) "
            + " OR (lower(obj.numero) like :filtro) "
            + " OR (lower(obj.matriculaFP.matricula) like :filtro)) ";

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        return q.getResultList();
    }

    /**
     * recupear contratos por numero de matricula e numero do contrato Obs:
     * recupera somente contrato vigente
     *
     * @param s
     * @return Lista de Contratos por tipo de previdencia 3 e 4.
     */
    public List<ContratoFP> buscaContratosMatriculaFPTipoPrevidencia(String s) {
        UsuarioSistema us = usuarioSistemaFacade.recuperarAutorizacaoUsuarioRH(sistemaFacade.getUsuarioCorrente().getId());
        String hql = " select obj from ContratoFP obj "
            + " inner join obj.previdenciaVinculoFPs previdencia "
            + " where (previdencia.tipoPrevidenciaFP.codigo = 3 or previdencia.tipoPrevidenciaFP.codigo = 4) "
            + "   and (lower(obj.matriculaFP.pessoa.nome) like :filtro "
            + "        OR lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro "
            + "        OR lower(obj.numero) like :filtro "
            + "        OR lower(obj.matriculaFP.matricula) like :filtro) ";
        if (!us.hasPermissaoLancamentoAverbacaoContratosEncerrados()) {
            hql += " and :dataVigencia >= obj.inicioVigencia and :dataVigencia <= coalesce(obj.finalVigencia,:dataVigencia) "
                + " and :dataVigencia >= previdencia.inicioVigencia and :dataVigencia <= coalesce(previdencia.finalVigencia,:dataVigencia) ";
        }
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        if (!us.hasPermissaoLancamentoAverbacaoContratosEncerrados()) {
            q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        }
        return q.getResultList();
    }

    @Deprecated
    public List<ContratoFP> buscaContratosMatriculaFPTipoPrevidenciaParaAposen(String s) {
        Set<ContratoFP> contratoFPs = new LinkedHashSet<>();
        String hql = " select obj from ContratoFP obj "
            + " inner join obj.previdenciaVinculoFPs previdencia inner join obj.situacaoFuncionals sit "
            + " where ((lower(obj.matriculaFP.pessoa.nome) like :filtro) "
            + " OR (lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro) "
            + " OR (lower(obj.numero) like :filtro) "
            + " OR (lower(obj.matriculaFP.matricula) like :filtro))  " +
            "   and :dataVigencia between sit.inicioVigencia and coalesce(sit.finalVigencia, :dataVigencia) and" +
            "  sit.situacaoFuncional.codigo in (6) and obj not in (select ap.contratoFP from Aposentadoria ap)" +
            " and previdencia.tipoPrevidenciaFP.codigo in (3,4)";
//
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        //workaround TODO problema da ultima prevividencia vigente(duplica o resultado da consulta).
        List<ContratoFP> retorno = q.getResultList();
        contratoFPs.addAll(retorno);
        retorno = new LinkedList<>();
        retorno.addAll(contratoFPs);
        return retorno;
    }

    public List<ContratoFP> recuperaContratoMatricula(String s) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select new ContratoFP(contrato.id, matricula.matricula||'/'||contrato.numero||' - '||pf.nome||' ('||pf.nomeTratamento||')'||' - '||formatacpfcnpj(pf.cpf)) ");
        hql.append(" from ContratoFP contrato ");
        hql.append(" inner join contrato.matriculaFP matricula ");
        hql.append(" inner join matricula.pessoa pf WHERE");
        hql.append(" (lower(pf.nome) like :filtro) or");
        hql.append(" (lower(pf.cpf) like :filtro) or");
        hql.append(" (lower(matricula.matricula) like :filtro) ");
        hql.append(" order by to_number(matricula.matricula) asc, to_number(contrato.numero) asc ");

        Query q = em.createQuery(hql.toString());
        q.setMaxResults(10);

        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        return q.getResultList();
    }

    public List<ContratoFP> buscarContratosFiltrandoMatriculaOrNomeOrCPFPorTipoPrevidencia(String s, Long tipoPrevidencia) {
        String hql = " select new ContratoFP(contrato.id, matricula.matricula||'/'||contrato.numero||' - '||pf.nome||' ('||pf.nomeTratamento||')'||' - '||formatacpfcnpj(pf.cpf)) " +
            " from ContratoFP contrato " +
            " inner join contrato.matriculaFP matricula " +
            " inner join matricula.pessoa pf " +
            " inner join contrato.previdenciaVinculoFPs previdencia" +
            " WHERE ((lower(pf.nome) like :filtro) or" +
            "           (lower(pf.cpf) like :filtro) or" +
            "           (lower(matricula.matricula) like :filtro)) " +
            " and :dataVigencia between previdencia.inicioVigencia and coalesce(previdencia.finalVigencia, :dataVigencia) " +
            " and :dataVigencia between contrato.inicioVigencia and coalesce(contrato.finalVigencia, :dataVigencia) " +
            " and previdencia.tipoPrevidenciaFP.codigo = :tipoPrevidencia " +
            " order by to_number(matricula.matricula) asc, to_number(contrato.numero) asc ";

        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", UtilRH.getDataOperacao());
        q.setParameter("tipoPrevidencia", tipoPrevidencia);
        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratoMatriculaApo(String s) {
        Set<ContratoFP> contratoFPs = new LinkedHashSet<>();


        StringBuilder hql = new StringBuilder();
        hql.append(" select new ContratoFP(contrato.id, matricula.matricula||'/'||contrato.numero||' - '||pf.nome||' ('||pf.nomeTratamento||')'||' - '||formatacpfcnpj(pf.cpf)) ");
        hql.append(" from ContratoFP contrato ");
        hql.append(" inner join contrato.matriculaFP matricula ");
        hql.append(" inner join matricula.pessoa pf ");
        hql.append(" inner join contrato.previdenciaVinculoFPs prev ");
        hql.append(" inner join contrato.modalidadeContratoFP modalidade ");
        hql.append(" WHERE ((lower(pf.nome) like :filtro) or");
        hql.append(" (lower(pf.cpf) like :filtro) or");
        hql.append(" (lower(matricula.matricula) like :filtro)) ");
        hql.append(" and (prev.tipoPrevidenciaFP.codigo in (3,4)) ");
        hql.append(" and prev.id = (select max(previdencia.id) from PrevidenciaVinculoFP previdencia where previdencia.contratoFP = contrato)");
        hql.append(" and  contrato not in (select ap.contratoFP from Aposentadoria ap ");
        hql.append(" where :dataAtual between ap.inicioVigencia and coalesce(ap.finalVigencia, :dataAtual) )");
        hql.append(" and modalidade.codigo = 1 ");
        hql.append(" order by to_number(matricula.matricula) asc ");

        Query q = em.createQuery(hql.toString());
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataAtual", new Date());
        List<ContratoFP> retorno = q.getResultList();
        contratoFPs.addAll(retorno);
        retorno = new LinkedList<>();
        retorno.addAll(contratoFPs);
        return retorno;
    }

    public List<ContratoFP> recuperaContratoMatriculaSql(String s) {
        String orderBy = "";
        try {
            int i = Integer.parseInt(s);
            orderBy = " order by to_number(matricula.matricula) asc";
        } catch (Exception e) {
            orderBy = " order by pf.nome asc";
        }

        StringBuilder hql = new StringBuilder();
        hql.append(" select new ContratoFP(contrato.id, matricula.matricula||'/'||contrato.numero||' - '||pf.nome||' ('||pf.nomeTratamento||')'||' - '||formatacpfcnpj(pf.cpf)) ");
        hql.append(" from ContratoFP contrato ");
        hql.append(" inner join contrato.matriculaFP matricula ");
        hql.append(" inner join matricula.pessoa pf WHERE");
        hql.append(" ((lower(pf.nome) like :filtro) or");
        hql.append(" (lower(pf.cpf) like :filtro) or");
        hql.append(" (lower(matricula.matricula) like :filtro)) ");
        hql.append(" and :dataVigencia >= contrato.inicioVigencia ");
        hql.append(" and :dataVigencia <= coalesce(contrato.finalVigencia,:dataVigencia)");
        hql.append(orderBy);

        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setMaxResults(10);

        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratoComEnquadramentoTipoPCS(String parte, TipoPCS tipoPCS) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select distinct new ContratoFP(contrato.id, matricula.matricula||'/'||contrato.numero||' - '||pf.nome) ");
        hql.append(" from EnquadramentoFuncional enquadramento ");
        hql.append(" join enquadramento.categoriaPCS.planoCargosSalarios pcs ");
        hql.append(" join enquadramento.contratoServidor contrato ");
        hql.append(" join contrato.matriculaFP matricula ");
        hql.append(" join matricula.pessoa pf WHERE");
        hql.append(" (lower(pf.nome) like :filtro) or");
        hql.append(" (lower(pf.cpf) like :filtro) or");
        hql.append(" (lower(matricula.matricula) like :filtro) ");
        hql.append(" and :dataVigencia >= contrato.inicioVigencia ");
        hql.append(" and :dataVigencia <= coalesce(contrato.finalVigencia,:dataVigencia)");
        hql.append(" and pcs.tipoPCS = :tipo");
        hql.append(" and enquadramento.finalVigencia = null");


        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setParameter("tipo", tipoPCS);
        q.setMaxResults(9);

        List<ContratoFP> contratos = new ArrayList<>();
        contratos = q.getResultList();

        return contratos;
    }

    public List<ContratoFP> recuperaContratoFuncaoGratificada(String s) {
        Query q = em.createQuery(" select contrato from FuncaoGratificada funcao "
            + " inner join funcao.contratoFP contrato "
            + " where :dataVigencia >= funcao.inicioVigencia "
            + " and :dataVigencia <= coalesce(funcao.finalVigencia,:dataVigencia) and ((lower(contrato.matriculaFP.pessoa.nome) like :filtro) "
            + " OR (lower(contrato.matriculaFP.pessoa.cpf) like :filtro) OR (lower(contrato.matriculaFP.matricula) like :filtro)) "
            + " and :dataVigencia >= contrato.inicioVigencia "
            + " and :dataVigencia <= coalesce(contrato.finalVigencia,:dataVigencia)");
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        return q.getResultList();

    }

    public List<ContratoFP> recuperaContratoVigenteMatricula(String s) {
        String hql = "from ContratoFP obj where ";
        hql += "((lower(obj.matriculaFP.pessoa.nome) like :filtro) OR (lower(obj.matriculaFP.pessoa.cpf) like :filtro) OR (lower(obj.matriculaFP.matricula) like :filtro)) "
            + " and :dataVigencia >= obj.inicioVigencia "
            + " and :dataVigencia <= coalesce(obj.finalVigencia,:dataVigencia)";

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratoVigentePorModalidades(String s, List<Long> codigosModalidades) {
        String hql = "select distinct obj from ContratoFP obj "
            + " join obj.matriculaFP matricula "
            + " join matricula.pessoa pes "
            + " left join pes.documentosPessoais docs "
            + " where ((lower(pes.nome) like :filtro) OR (lower(pes.cpf) like :filtro) OR (lower(matricula.matricula) like :filtro) or (docs.numero like :filtro)) "
            + " and :dataVigencia between obj.inicioVigencia and coalesce(obj.finalVigencia, :dataVigencia) "
            + " and obj.modalidadeContratoFP.codigo in (:codigos) ";

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("codigos", codigosModalidades);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<VinculoFP> recuperaContratoVigenteSemCedenciaOuAfastamento(String s, Integer mes, Integer ano) {
        String hql = "select new VinculoFP(v.id,  v.matriculaFP.matricula||'/'|| v.numero||' - '|| v.matriculaFP.pessoa.nome|| ' '||(select ' - APOSENTADO' from Aposentadoria a where a.id =  v.id)||' '||(select ' - PENSIONISTA' from Pensionista a where a.id =  v.id)) from VinculoFP v where " +
            " ((lower(v.matriculaFP.pessoa.nome) like :filtro) " +
            " OR (lower(v.matriculaFP.pessoa.nomeAbreviado) like :filtro) " +
            " OR (lower(v.matriculaFP.pessoa.cpf) like :filtro) " +
            " OR (lower(v.matriculaFP.matricula) like :filtro)) " +
            " and to_date(to_char(:dataVigencia,'mm/yyyy'),'mm/yyyy') between " +
            " to_date(to_char(v.inicioVigencia,'mm/yyyy'),'mm/yyyy') " +
            " and to_date(to_char(coalesce(v.finalVigencia,:dataVigencia),'mm/yyyy'),'mm/yyyy') " +
             " and not exists ( "+
             "    select cedencia.contratoFP.id "+
            "    from CedenciaContratoFP cedencia " +
            "    where cedencia.contratoFP.id = v.id " +
            "      and cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia " +
            "      and ((:dataInicial >= cedencia.dataCessao  " +
            "           and not exists (  " +
            "                select 1 " +
            "                from RetornoCedenciaContratoFP r " +
            "                where r.cedenciaContratoFP.id = cedencia.id) " +
            "                or exists (  " +
            " select 1 from Afastamento a  " +
            "  join a.tipoAfastamento t " +
            " where a.contratoFP.id = v.id  " +
            "  and t.naoCalcularFichaSemRetorno = true " +
            "  and (a.retornoInformado = false or a.retornoInformado is null)  " +
            "  and (  " +
            "    (a.inicio <= :dataInicial and (a.termino is null or a.termino >= :dataFinal))  " +
            "    or (  " +
            "      a.inicio = (  " +
            "        select min(a2.inicio) from Afastamento a2  " +
            "          join a2.tipoAfastamento t2 " +
            "        where a2.contratoFP.id = v.id  " +
            "          and t2.naoCalcularFichaSemRetorno = true  " +
            "          and (a2.retornoInformado = false or a2.retornoInformado is null)  " +
            "            and a2.termino <= :dataInicial  " +
            "        )  " +
            "      ) or ( a.inicio <= :dataInicial and (TRUNC(a.termino) + 1) >= cedencia.dataCessao and :dataFinal <= (select max(r.dataRetorno) " +
            " from RetornoCedenciaContratoFP r where r.cedenciaContratoFP.id = cedencia.id)) " +
            " or (:dataInicial >= cedencia.dataCessao " +
            "          and a.termino >= ( " +
            "              select max(r.dataRetorno) " +
            "              from RetornoCedenciaContratoFP r " +
            "              where r.cedenciaContratoFP.id = cedencia.id " +
            "          ) and a.termino >= :dataFinal and a.termino < cedencia.dataCessao " +
            "        ) " +
            "        or (a.inicio < cedencia.dataCessao and (a.inicio <= :dataInicial and a.termino >= :dataFinal))" +
            "         or (a.termino >= ( " +
            "              select max(r.dataRetorno) " +
            "              from RetornoCedenciaContratoFP r " +
            "              where r.cedenciaContratoFP.id = cedencia.id  " +
            "                ) and a.termino >= :dataFinal and a.inicio > cedencia.dataCessao)" +
            ") " +
            "   ))  or (:dataInicial >= cedencia.dataCessao and :dataFinal <= (  " +
            "                select Max(r.dataRetorno) " +
            "                from RetornoCedenciaContratoFP r " +
            "                where r.cedenciaContratoFP.id = cedencia.id " +
            "            )) " +
            "      )) "+
            " order by cast(v.matriculaFP.matricula as integer) ";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(Util.criaDataPrimeiroDiaMesJoda(mes, ano).toDate()));
        q.setParameter("dataInicial", Util.getDataHoraMinutoSegundoZerado(Util.criaDataPrimeiroDiaMesJoda(mes, ano).toDate()));
        q.setParameter("dataFinal", Util.getDataHoraMinutoSegundoZerado(DataUtil.criarDataUltimoDiaMes(mes, ano).toDate()));
        q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<VinculoFP> buscarContratosExoneradosSemFolhaEfetivadaFiltrando(String s, FolhaCalculavel folha) {
        String hql = "select v from VinculoFP v where v.id in (select obj.id from ExoneracaoRescisao recisao "
            + " inner join recisao.vinculoFP obj where "
            + " ((lower(obj.matriculaFP.pessoa.nome) like :filtro) "
            + " OR (lower(obj.matriculaFP.pessoa.cpf) like :filtro) "
            + " OR (lower(obj.matriculaFP.matricula) like :filtro)) "
            + " and (obj not in(select vin from FichaFinanceiraFP ficha "
            + " inner join ficha.folhaDePagamento folha "
            + " inner join ficha.vinculoFP vin "
            + " where folha.efetivadaEm != null and folha.tipoFolhaDePagamento =:tipoFolha) "
            + "    or (:tipoRescisao = 'RESCISAO' and to_date(to_char(:dataFolha, 'mm/yyyy'), 'mm/yyyy') = to_date(to_char(recisao.dataRescisao, 'mm/yyyy'), 'mm/yyyy'))"
            + "    or (:tipoRescisao = 'RESCISAO_COMPLEMENTAR' and to_date(to_char(:dataFolha, 'mm/yyyy'), 'mm/yyyy') >= to_date(to_char(recisao.dataRescisao, 'mm/yyyy'), 'mm/yyyy'))))"
            + " order by cast(v.matriculaFP.matricula as integer) ";

        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("tipoFolha", TipoFolhaDePagamento.RESCISAO);
        q.setParameter("tipoRescisao", folha.getTipoFolhaDePagamento().name());
        q.setParameter("dataFolha", DataUtil.criarDataPrimeiroDiaMes(folha.getMes().getNumeroMes(), folha.getAno()));
        return q.getResultList();
    }

    public boolean hasFolhaCalculada(ContratoFP contrato, Date data) {
        return !podeExcluir(contrato, data);
    }

    public boolean podeExcluir(ContratoFP contrato, Date data) {
        if (contrato == null || contrato.getId() == null) {
            return true;
        } else {
            Query query = em.createQuery("from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha "
                + " where ficha.vinculoFP = :vinculo and "
                + " folha.ano >= :ano  and folha.mes >= :mes");
            query.setParameter("ano", DataUtil.getAno(data));
            query.setParameter("mes", Mes.getMesToInt(DataUtil.getMes(data)));
            query.setParameter("vinculo", contrato);
            return query.getResultList().isEmpty();
        }
    }

    public boolean temFichaNaCompetencia(VinculoFP contrato, Date data) {
        if (contrato == null || contrato.getId() == null) {
            return true;
        } else {
            Query query = em.createQuery("from FolhaDePagamento folha inner join folha.fichaFinanceiraFPs ficha "
                + " where ficha.vinculoFP = :vinculo and "
                + " folha.ano = :ano  and folha.mes = :mes");
            query.setParameter("ano", DataUtil.getAno(data));
            query.setParameter("mes", Mes.getMesToInt(DataUtil.getMes(data)));
            query.setParameter("vinculo", contrato);
            return query.getResultList().isEmpty();
        }
    }

    @Override
    public List<ContratoFP> listaFiltrando(String s, String... atributos) {
        String hql = "from SituacaoFuncional obj where ";
        for (String atributo : atributos) {
            hql += "lower(obj." + atributo + ") like :filtro OR ";
        }
        hql = hql.substring(0, hql.length() - 3);
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<VinculoFP> listaContratosExportacao(Integer mes, Integer ano, HierarquiaOrganizacional ho, List<ArquivoMargemVinculo> arquivoMargemVinculos) {
        String s = "select distinct vinculo from FichaFinanceiraFP ficha inner join ficha.vinculoFP vinculo "
            + " inner join ficha.folhaDePagamento folha inner join ficha.itemFichaFinanceiraFP itens "
            + " "
            + " where  "
            + " folha.ano = :ano and folha.mes = :mes ";
        if (arquivoMargemVinculos != null && !arquivoMargemVinculos.isEmpty()) {
            s += " and vinculo in :vinculos";
        }
        Query q = em.createQuery(s);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("ano", ano);
        if (arquivoMargemVinculos != null && !arquivoMargemVinculos.isEmpty()) {
            q.setParameter("vinculos", getVinculosArquivoMargem(arquivoMargemVinculos));
        }
        return q.getResultList();
    }

    private List<VinculoFP> getVinculosArquivoMargem(List<ArquivoMargemVinculo> arquivoMargemVinculos) {
        List<VinculoFP> vinculos = Lists.newArrayList();
        for (ArquivoMargemVinculo arquivoMargemVinculo : arquivoMargemVinculos) {
            vinculos.add(arquivoMargemVinculo.getVinculoFP());
        }
        return vinculos;
    }

    private DateTime getDataCorreta(Integer mes, Integer ano) {
        DateTime dt = new DateTime();
        dt = dt.withYear(ano);
        dt = dt.withMonthOfYear(mes);
        dt = dt.withDayOfMonth(15);
        dt = dt.withMinuteOfHour(0);
        dt = dt.withHourOfDay(0);
        dt = dt.withSecondOfMinute(0);
        dt = dt.withMillisOfSecond(0);
        return dt;
    }

    public List<ItemVaga> recuperaVagasOcupadasPorHierarquia(Cargo cargo) {
        Query q = em.createQuery(" select unidade,count(lotacao.id) from ContratoFP  contrato inner join contrato.lotacaoFuncionals lotacao "
            + " inner join contrato.cargo cargo"
            + " inner join lotacao.unidadeOrganizacional unidade "
            + " where :dataVigencia >= contrato.inicioVigencia and "
            + " :dataVigencia <= coalesce(contrato.finalVigencia,:dataVigencia) "
            + " and :dataVigencia >= lotacao.inicioVigencia and "
            + " :dataVigencia <= coalesce(lotacao.finalVigencia,:dataVigencia) "
            + " and cargo = :parametroCargo ");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("parametroCargo", cargo);
        return null;
    }

    public List<ContratoFP> recuperaContratoAposentadoFiltrandoAtributosPessoaFisica(String s) {
        StringBuilder queryString = new StringBuilder();
        List<ContratoFP> contratos = new ArrayList<>();

        queryString.append("select contrato from Aposentadoria aposentadoria");
        queryString.append(" inner join aposentadoria.contratoFP contrato");
        queryString.append(" where (lower(contrato.matriculaFP.pessoa.nome) like :filtro) or (lower(contrato.numero) like :filtro)");
        Query q = em.createQuery(queryString.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        contratos.addAll(q.getResultList());
        return contratos;
    }

    public void fecharVigenciaContratoFP(PessoaFisica pf, Date finalVigencia) {
        StringBuilder queryString = new StringBuilder();
        queryString.append(" from ContratoFP contrato");
        queryString.append(" where contrato.matriculaFP.pessoa = :pessoa");
        queryString.append(" and :data >= contrato.inicioVigencia");
        queryString.append(" and :data <= coalesce(contrato.finalVigencia, :data)");

        Query q = em.createQuery(queryString.toString());
        q.setParameter("pessoa", pf);
        q.setParameter("data", finalVigencia);

        for (ContratoFP contrato : (List<ContratoFP>) q.getResultList()) {
            contrato.setFinalVigencia(finalVigencia);
            em.merge(contrato);
        }
    }

    public List<ContratoFP> recuperaContratoMatriculaPorLocalDeTrabalho(String s, HierarquiaOrganizacional ho) {
        String hql = " select distinct contrato from LotacaoFuncional lotacao, ContratoFP contrato"
            + " inner join lotacao.vinculoFP vinculo ";

        if (ho != null) {
            hql += " where lotacao.unidadeOrganizacional.id in (" + ho.getSubordinada().getId();

            for (HierarquiaOrganizacional u : hierarquiaOrganizacionalFacadeOLD.filtrandoHierarquiaHorganizacionalTipo(ho)) {
                if (u.getSubordinada() != null) {
                    if (!u.getSubordinada().equals(ho.getSuperior())) {
                        hql += "," + u.getSubordinada().getId();
                    }
                }
            }

            hql += ") ";
        } else {
            hql += " where 1 = 1 ";
        }

        hql += " and contrato = vinculo and :dataVigencia >= lotacao.inicioVigencia "
            + " and :dataVigencia <= coalesce(lotacao.finalVigencia,:dataVigencia)"
            + " and ((lower(contrato.matriculaFP.pessoa.nome) like :filtro) OR (lower(contrato.matriculaFP.pessoa.cpf) like :filtro) OR (lower(contrato.matriculaFP.matricula) like :filtro))";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));

        return q.getResultList();
    }

    public boolean isContratoPrevidenciaRPPS(ContratoFP contratoFP) {
        Query q = em.createQuery(" from PrevidenciaVinculoFP previdencia"
            + " where (previdencia.tipoPrevidenciaFP.codigo = 3 or previdencia.tipoPrevidenciaFP.codigo = 4) "
            + " and :dataVigencia >= previdencia.inicioVigencia and "
            + " :dataVigencia <= coalesce(previdencia.finalVigencia,:dataVigencia) "
            + " and previdencia.contratoFP = :parametroContrato ");

        q.setParameter("parametroContrato", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        return !q.getResultList().isEmpty();
    }

    public List<ContratoFP> recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursiva(String s, HierarquiaOrganizacional ho, Date data) {
        List<ContratoFP> contratosFPs = new ArrayList<>();

        String sql = "select distinct contrato.id from LOTACAOFUNCIONAL lotacao "
            + " inner join CONTRATOFP contrato on contrato.ID = lotacao.VINCULOFP_ID "
            + " inner join VINCULOFP vinculo on vinculo.ID = contrato.ID "
            + " inner join UNIDADEORGANIZACIONAL un on un.id = lotacao.unidadeorganizacional_id "
            + " inner join HierarquiaOrganizacional h on h.subordinada_id = un.id "
            + " inner join VWHIERARQUIAADMINISTRATIVA ho on ho.id = h.id "
            + " inner join MATRICULAFP matricula on matricula.id = vinculo.matriculafp_id "
            + " inner join PESSOAFISICA pessoa on matricula.pessoa_id = pessoa.id "
            + " where "
            + " :vigencia >= vinculo.inicioVigencia and :vigencia <= coalesce(vinculo.finalVigencia,:vigencia) "
            + " and :vigencia >= lotacao.inicioVigencia and :vigencia <= coalesce(lotacao.finalVigencia,:vigencia) "
            + " and :vigencia >= ho.inicioVigencia and :vigencia <= coalesce(ho.fimVigencia,:vigencia) "
            + " and ho.codigo like :unidade  "
            + " and ((lower(pessoa.nome) like :filtro) OR "
            + " (lower(pessoa.cpf) like :filtro) OR "
            + " (lower(matricula.matricula) like :filtro)) "
            + " and rownum <= 10 ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("vigencia", Util.getDataHoraMinutoSegundoZerado(data), TemporalType.DATE);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");

        for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
            contratosFPs.add(em.find(ContratoFP.class, id.longValue()));
        }
        return contratosFPs;
    }

    public List<ContratoFP> recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursiva(HierarquiaOrganizacional ho) {

        List<ContratoFP> contratosFPs = new ArrayList<>();
        Query q = em.createQuery(" select distinct contrato from ContratoFP contrato inner join contrato.lotacaoFuncionals lotacao "
            + " where :dataVigencia >= lotacao.inicioVigencia "
            + " and :dataVigencia <= coalesce(lotacao.finalVigencia,:dataVigencia)"
            + " and lotacao.unidadeOrganizacional in :unidade "
            + " order by contrato.matriculaFP");

        List<HierarquiaOrganizacional> listaHierarquia = new ArrayList<>();
        List<UnidadeOrganizacional> listaUnidade = new ArrayList<>();

        listaHierarquia.add(ho);

        if (ho.getSubordinada() != null) {
            for (HierarquiaOrganizacional item : (List<HierarquiaOrganizacional>) hierarquiaOrganizacionalFacadeOLD.recuperaHierarquiasFilhas(listaHierarquia, ho)) {
                listaUnidade.add(item.getSubordinada());
            }
        } else {
            listaUnidade.add(ho.getSubordinada());
        }

        q.setParameter("unidade", listaUnidade);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));

        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursivaPelaView(HierarquiaOrganizacional ho) {

        List<ContratoFP> contratosFPs = new ArrayList<>();
        String s = new String();
        s = "SELECT contrato.id FROM LOTACAOFUNCIONAL lotacao INNER JOIN CONTRATOFP contrato ON contrato.ID = lotacao.VINCULOFP_ID "
            + "INNER JOIN VINCULOFP vinculo ON vinculo.ID = contrato.ID "
            + "INNER JOIN UNIDADEORGANIZACIONAL un ON un.id = lotacao.unidadeorganizacional_id "
            + "INNER JOIN VWHIERARQUIAADMINISTRATIVA ho ON ho.subordinada_id = un.id "
            + "INNER JOIN MATRICULAFP matricula ON matricula.id = vinculo.matriculafp_id "
            + " "
            + "WHERE "
            + "    :vigencia >= vinculo.inicioVigencia AND :vigencia <= coalesce(vinculo.finalVigencia,:vigencia) "
            + "AND :vigencia >= lotacao.inicioVigencia AND :vigencia <= coalesce(lotacao.finalVigencia,:vigencia) "
            + "AND :vigencia >= ho.inicioVigencia AND :vigencia <= coalesce(ho.fimVigencia,:vigencia) "
            + "AND ho.codigo LIKE :unidade  "
            + " ORDER BY matricula.matricula";

        Query q = em.createNativeQuery(s);
        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("vigencia", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
            contratosFPs.add(em.find(ContratoFP.class, id.longValue()));
        }


        return contratosFPs;
    }

    public List<VinculoFP> listarVinculosPorHierarquiaSemCedenciaEstagioOuAfastamento(List<HierarquiaOrganizacional> hos, Mes mes, Integer ano, Boolean isExonerados) {

        String ss = "select distinct v from VinculoFP v, HierarquiaOrganizacional ho inner join v.lotacaoFuncionals lotacao where " +
            " ho.subordinada.id = lotacao.unidadeOrganizacional.id  " +
            " and :vigencia between v.inicioVigencia and coalesce(v.finalVigencia,:vigencia) " +
            " and :vigencia >= lotacao.inicioVigencia and :vigencia <= coalesce(lotacao.finalVigencia,:vigencia) " +
            " and :vigencia >= ho.inicioVigencia and :vigencia <= coalesce(ho.fimVigencia,:vigencia) and ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' ";
        if (isExonerados) {
            ss += " and v.id in(select ex.vinculoFP.id from ExoneracaoRescisao ex ) ";
        }
        ss += gerarSqlHierarquiaOrganizacional(hos);
        ss +=  " and not exists ( " +
            "    select 1 " +
            "    from CedenciaContratoFP cedencia " +
            "    where cedencia.contratoFP.id = v.id " +
            "      and cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia " +
            "      and ((:dataInicial >= cedencia.dataCessao  " +
            "           and not exists (  " +
            "                select 1 " +
            "                from RetornoCedenciaContratoFP r " +
            "                where r.cedenciaContratoFP.id = cedencia.id) " +
            "                or exists (  " +
            " select 1 from Afastamento a  " +
            "  join a.tipoAfastamento t " +
            " where a.contratoFP.id = v.id  " +
            "  and t.naoCalcularFichaSemRetorno = true  " +
            "  and (a.retornoInformado = false or a.retornoInformado is null)  " +
            "  and (  " +
            "    (a.inicio <= :dataInicial and (a.termino is null or a.termino >= :dataFinal))  " +
            "    or (  " +
            "      a.inicio = (  " +
            "        select min(a2.inicio) from Afastamento a2  " +
            "          join a2.tipoAfastamento t2 " +
            "        where a2.contratoFP.id = v.id  " +
            "          and t2.naoCalcularFichaSemRetorno = true  " +
            "          and (a2.retornoInformado = false or a2.retornoInformado is null)  " +
            "            and a2.termino <= :dataInicial  " +
            "        )  " +
            "      ) or ( a.inicio <= :dataInicial and (a.termino + 1 ) >= cedencia.dataCessao and :dataFinal <= (select Max(r.dataRetorno) " +
            " from RetornoCedenciaContratoFP r where r.cedenciaContratoFP.id = cedencia.id)) " +
            " or (:dataInicial >= cedencia.dataCessao " +
            "          and a.termino >= ( " +
            "              select max(r.dataRetorno) " +
            "              from RetornoCedenciaContratoFP r " +
            "              where r.cedenciaContratoFP.id = cedencia.id " +
            "          ) and a.termino >= :dataFinal and a.termino < cedencia.dataCessao " +
            "        ) " +
            "        or (a.inicio < cedencia.dataCessao and (a.inicio <= :dataInicial and a.termino >= :dataFinal))" +
            "         or (a.termino >= ( " +
            "              select max(r.dataRetorno) " +
            "              from RetornoCedenciaContratoFP r " +
            "              where r.cedenciaContratoFP.id = cedencia.id  " +
            "                ) and a.termino >= :dataFinal and a.inicio > cedencia.dataCessao)" +
            "      ) " +
            "   ))  or (:dataInicial >= cedencia.dataCessao and :dataFinal <= (  " +
            "                select Max(r.dataRetorno) " +
            "                from RetornoCedenciaContratoFP r " +
            "                where r.cedenciaContratoFP.id = cedencia.id " +
            "            )) " +
            "      )) "+
            "    and not exists (select 1 from Estagiario e where e.id = v.id) order by v.inicioVigencia";

        Query q = em.createQuery(ss);
        q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);
        q.setParameter("vigencia", Util.getDataHoraMinutoSegundoZerado(Util.criaDataPrimeiroDiaMesJoda(mes.getNumeroMes(), ano).toDate()));
        q.setParameter("dataInicial", Util.getDataHoraMinutoSegundoZerado(Util.criaDataPrimeiroDiaMesJoda(mes.getNumeroMes(), ano).toDate()));
        q.setParameter("dataFinal", Util.getDataHoraMinutoSegundoZerado(DataUtil.criarDataUltimoDiaMes(mes.getNumeroMes(), ano).toDate()));

        return q.getResultList();
    }

    private String gerarSqlHierarquiaOrganizacional(List<HierarquiaOrganizacional> hos) {
        if (hos.isEmpty()) {
            return "";
        }
        List<String> codigosSemZerosFinal = Lists.newArrayList();
        for (HierarquiaOrganizacional ho : hos) {
            codigosSemZerosFinal.add(ho.getCodigoSemZerosFinais());
        }

        List<String> codigosFiltrados = new ArrayList<>(codigosSemZerosFinal);
        List<String> codigosRemovidos = new ArrayList<>();
        codigosSemZerosFinal.forEach((codigo) -> {
            List<String> removidos = codigosFiltrados.stream()
                .filter(cod -> cod.startsWith(codigo)).collect(Collectors.toList());
            codigosFiltrados.removeAll(removidos);
            if (!codigosRemovidos.contains(codigo)) {
                codigosFiltrados.add(codigo);
            }
            codigosRemovidos.addAll(removidos);
        });

        List<String> listaSqlComLike = Lists.newArrayList();
        List<String> codigosParaSqlComIn = Lists.newArrayList();
        int tamanhoMaximo = hos.get(0).getCodigo().length();
        codigosFiltrados.forEach((codigo) -> {
            if (codigo.length() == tamanhoMaximo) {
                codigosParaSqlComIn.add("'" + codigo + "'");
            } else {
                listaSqlComLike.add("ho.codigo like '" + codigo + "%'");
            }
        });
        String sqlComLike = listaSqlComLike.isEmpty() ? "" : "and (" + StringUtils.join(listaSqlComLike, " or ") + ") ";
        String sqlComIn = codigosParaSqlComIn.isEmpty() ? "" : " and ho.codigo in (" + StringUtils.join(codigosParaSqlComIn, ", ") + ") ";
        return sqlComLike + sqlComIn;
    }

    public List<ContratoFP> recuperaContratoAtivoTipoRegime(String parte, Long codigoTipoRegime) {
        String hql = " from ContratoFP contrato "
            + " inner join contrato.tipoRegime tipoRegime "
            + " where (lower(contrato.matriculaFP.pessoa.nome) like :filtro "
            + " OR lower(contrato.matriculaFP.pessoa.cpf) like :filtro)"
            + " and :dataVigencia >= contrato.inicioVigencia"
            + " and :dataVigencia <= coalesce(contrato.finalVigencia, :dataVigencia)"
            + " and tipoRegime.codigo = :regime";
        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(new Date()));
        q.setParameter("regime", codigoTipoRegime);

        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratosComRegistroDeObito(String s) {
        StringBuilder queryString = new StringBuilder();
        queryString.append(" select contrato from ContratoFP contrato, RegistroDeObito obito inner join contrato.previdenciaVinculoFPs prev");
        queryString.append(" inner join contrato.matriculaFP.pessoa pessoa");
        queryString.append(" where prev.tipoPrevidenciaFP.codigo in (3, 4) ");
        queryString.append("   and obito.pessoaFisica = pessoa");
        queryString.append("   and ((lower(contrato.matriculaFP.pessoa.nome) like :filtro) or (lower(contrato.numero) like :filtro))");
        Query query = em.createQuery(queryString.toString());
        query.setParameter("filtro", "%" + s.toLowerCase() + "%");

        return query.getResultList();
    }

    public List<ContratoFP> buscarContratosComRegistroDeObitoNaoInstituidor(String s) {
        StringBuilder queryString = new StringBuilder();
        queryString.append(" select new ContratoFP(contrato.id, contrato.matriculaFP.matricula||'/'||contrato.numero||' - '||pessoa.nome) from ContratoFP contrato, RegistroDeObito obito inner join contrato.previdenciaVinculoFPs prev");
        queryString.append(" inner join contrato.matriculaFP.pessoa pessoa");
        queryString.append(" where prev.tipoPrevidenciaFP.codigo in (3, 4) ");
        queryString.append(" and contrato not in (select pensao.contratoFP from Pensao pensao)");
        queryString.append("   and obito.pessoaFisica = pessoa");
        queryString.append("   and (prev.inicioVigencia = (select max(previ.inicioVigencia) from PrevidenciaVinculoFP previ where previ.contratoFP = contrato)    ");
        queryString.append("   or :data between prev.inicioVigencia and coalesce(prev.finalVigencia,:data)  ) ");
        queryString.append("   and ((lower(contrato.matriculaFP.pessoa.nome) like :filtro) ");
        queryString.append("   or (lower(contrato.numero) like :filtro) ");
        queryString.append("   or (lower(contrato.matriculaFP.matricula) like :filtro))");
        Query query = em.createQuery(queryString.toString());
        query.setParameter("filtro", "%" + s.toLowerCase() + "%");
        query.setParameter("data", sistemaFacade.getDataOperacao());

        return query.getResultList();
    }

    public Boolean isContratoComRegistroDeObito(Long idContrato) {
        StringBuilder queryString = new StringBuilder();
        queryString.append(" select contrato from ContratoFP contrato, RegistroDeObito obito inner join contrato.previdenciaVinculoFPs prev");
        queryString.append(" inner join contrato.matriculaFP.pessoa pessoa");
        queryString.append(" where prev.tipoPrevidenciaFP.codigo in (3, 4) ");
        queryString.append("   and obito.pessoaFisica = pessoa");
        queryString.append("   and contrato.id= :id ");
        Query query = em.createQuery(queryString.toString());
        query.setParameter("id", idContrato);
        return query.getResultList().isEmpty();
    }

    public Boolean isContratoInstituidor(Long idContrato) {
        StringBuilder queryString = new StringBuilder();
        queryString.append("  select pensao.contratoFP from Pensao pensao ");
        queryString.append("   where pensao.contratoFP.id = :id ");
        Query query = em.createQuery(queryString.toString());
        query.setParameter("id", idContrato);
        return !query.getResultList().isEmpty();
    }


    public List<ContratoFP> recuperaContratoMatriculaPorLocalDeTrabalhoRecursiva(String s, HierarquiaOrganizacional ho) {
        List<ContratoFP> contratosFPs = new ArrayList<>();
        Query q = em.createQuery(" select distinct contrato from LotacaoFuncional lotacao, ContratoFP contrato "
            + " inner join lotacao.vinculoFP vinculo "
            + " where vinculo = contrato and ((lower(contrato.matriculaFP.pessoa.nome) like :filtro) OR "
            + " (lower(contrato.matriculaFP.pessoa.cpf) like :filtro) OR "
            + " (lower(contrato.matriculaFP.matricula) like :filtro)) "
            + " and lotacao.unidadeOrganizacional in :unidade ");

        List<HierarquiaOrganizacional> listaHierarquia = new ArrayList<>();
        List<UnidadeOrganizacional> listaUnidade = new ArrayList<>();

        listaHierarquia.add(ho);

        if (ho.getSubordinada() != null) {
            for (HierarquiaOrganizacional item : (List<HierarquiaOrganizacional>) hierarquiaOrganizacionalFacadeOLD.recuperaHierarquiasFilhas(listaHierarquia, ho)) {
                listaUnidade.add(item.getSubordinada());
            }
        } else {
            listaUnidade.add(ho.getSubordinada());
        }

        q.setParameter("unidade", listaUnidade);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        return q.getResultList();
    }

    private ContratoFP tratarClassificacaoInscricao(ContratoFP contratoFP) {
        ClassificacaoInscricao ci = contratoFP.getClassificacaoInscricao();
        if (ci == null) {
            return contratoFP;
        }

        ci = classificacaoConcursoFacade.buscarClassificacaoInscricao(ci.getId());
        ci.setContratoFP(contratoFP);
        contratoFP.setClassificacaoInscricao(ci);

        return contratoFP;
    }

    public String recuperarCodigoHierarquiaVinculoFP(VinculoFP vinculo) {
        String sql = " select distinct substr(vw.CODIGO, 0, 6) from vinculofp vinculo " +
            " inner join VWHIERARQUIAADMINISTRATIVA vw on vinculo.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
            " where vinculo.UNIDADEORGANIZACIONAL_ID = :idUnidade and :dataOperacao between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, :dataOperacao)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", vinculo.getUnidadeOrganizacional().getId());
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        return (String) q.getResultList().get(0);
    }

    public void verificarParametrosControleCargoComissaoAndValores(ContratoFP contratoFP) {
        ValidacaoException ve = new ValidacaoException();
        String codigoHO = recuperarCodigoHierarquiaVinculoFP(contratoFP);
        ParametroControleCargoFG parametro = parametroControleCargoFGFacade.recuperarPorEntidadeAndVigencia(codigoHO, sistemaFacade.getDataOperacao());
        if (parametro != null) {
            if (parametro.getQuantidadeCargoComissao() != null) {
                Integer quantidadeAtualServidores = parametroControleCargoFGFacade.recuperarQuantidadeServidoresVigentesCargoComissaoPorEntidade(codigoHO, sistemaFacade.getDataOperacao());
                if (parametro.getQuantidadeCargoComissao() <= quantidadeAtualServidores) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade Máxima de Cargo em Comissão para a Entidade <b>" + contratoFP.getUnidadeOrganizacional().getEntidade() + "</b> " +
                        "é de <b>" + parametro.getQuantidadeCargoComissao() + "</b> servidores,  no momento possuindo <b>" + quantidadeAtualServidores + "</b> servidores.");
                }
            }

        }
        ve.lancarException();
    }

    public void verificarParametrosControleCargoFuncaoGratificadaAndValores(ContratoFP contratoFP) {
        ValidacaoException ve = new ValidacaoException();
        String codigoHO = recuperarCodigoHierarquiaVinculoFP(contratoFP);
        ParametroControleCargoFG parametro = parametroControleCargoFGFacade.recuperarPorEntidadeAndVigencia(codigoHO, sistemaFacade.getDataOperacao());
        if (parametro != null) {
            if (ModalidadeContratoFP.FUNCAO_COORDENACAO.equals(contratoFP.getModalidadeContratoFP().getCodigo()) && parametro.getQuantidadeFgCoordenacao() != null) {
                Integer quantidadeAtualServidores = parametroControleCargoFGFacade.recuperarQuantidadeServidoresVigentesFuncaoGratificadaPorEntidade(codigoHO, sistemaFacade.getDataOperacao(), true);
                if (parametro.getQuantidadeFgCoordenacao() <= quantidadeAtualServidores) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade Máxima de servidores para a Entidade <b>" + contratoFP.getUnidadeOrganizacional().getEntidade() + "</b> " +
                        "é de <b>" + parametro.getQuantidadeFgCoordenacao() + "</b> servidores,  no momento possuindo <b>" + quantidadeAtualServidores + "</b> servidores.");
                }
            } else if (parametro.getQuantidadeFuncaoGratificada() != null) {
                Integer quantidadeAtualServidores = parametroControleCargoFGFacade.recuperarQuantidadeServidoresVigentesFuncaoGratificadaPorEntidade(codigoHO, sistemaFacade.getDataOperacao(), false);
                if (parametro.getQuantidadeFuncaoGratificada() <= quantidadeAtualServidores) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A quantidade Máxima de servidores para a Entidade <b>" + contratoFP.getUnidadeOrganizacional().getEntidade() + "</b> " +
                        "é de <b>" + parametro.getQuantidadeFuncaoGratificada() + "</b> servidores,  no momento possuindo <b>" + quantidadeAtualServidores + "</b> servidores.");
                }
            }
        }
        ve.lancarException();
    }

    public void salvarNovoContratoFP(ContratoFP contratoFP) {
        contratoFP.setDataRegistro(UtilRH.getDataOperacao());
        contratoFP = tratarClassificacaoInscricao(contratoFP);

        em.persist(contratoFP);
        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setVinculoFP(contratoFP);
        if (contratoFP.getModalidadeContratoFP().getCodigo() == ModalidadeContratoFP.CONTRATO_TEMPORARIO) {
            provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.NOMEACAO_CONTRATO_TEMPORARIO));
        } else {
            provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.NOMEACAO));
        }

        provimentoFP.setDataProvimento(contratoFP.getInicioVigencia());
        provimentoFP.setAtoLegal(contratoFP.getAtoLegal());
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(contratoFP));
        em.persist(provimentoFP);

        provimentoFP = (ProvimentoFP) provimentoFPFacade.recuperar(ProvimentoFP.class, provimentoFP.getId());
        contratoFP.setProvimentoFP(provimentoFP);
        em.merge(contratoFP);
        em.flush();
        criarRegistroESocialNovoContrato(contratoFP);
    }

    public String gerarLinkQRCode(ContratoFP contratoFP) {
        return String.format("%s%s", gerarLinkQRCode(), contratoFP.getId());
    }

    public String gerarLinkQRCode() {
        ConfiguracaoTributario configuracaoTributario = configuracaoTributarioFacade.retornaUltimo();
        return String.format("%s%s", configuracaoTributario.getUrlPortalContribuinte(),
            "dados-servidor/");
    }

    private void criarRegistroESocialNovoContrato(ContratoFP contrato) {
        ConfiguracaoEmpregadorESocial empregador = buscarEmpregadorPorVinculoFP(contrato);
    }

    private void criarRegistroESocialAlteracaoContrato(ContratoFP contrato) {
        ConfiguracaoEmpregadorESocial empregador = buscarEmpregadorPorVinculoFP(contrato);
    }

    public void salvarContratoFPEnquadramento(ContratoFP contratoFP) {
        em.merge(contratoFP);
        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setVinculoFP(contratoFP);
        provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.ENQUADRAMENTO));
        provimentoFP.setDataProvimento(contratoFP.getInicioVigencia());
        provimentoFP.setAtoLegal(contratoFP.getAtoLegal());
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(contratoFP));
        em.persist(provimentoFP);
    }

    // Esse método deve ser igual ao método de mesmo nome do UsuarioSistemaService
    public List<ContratoFP> listaContratosVigentesPorPessoaFisica(PessoaFisica pessoaFisica) {
        Query q = em.createQuery(" select contrato from ContratoFP contrato"
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " where pf = :pessoaFisica "
            + " and :data >= contrato.inicioVigencia "
            + " and :data <= coalesce(contrato.finalVigencia, :data)");
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setParameter("pessoaFisica", pessoaFisica);
        return q.getResultList();
    }

    public List<ContratoFPDTO> buscarContratosVigentesPorPessoaFisica(Long idPessoaFisica) {
        String sql = " select matricula.MATRICULA, vinculo.numero, " +
            "                 vwadm.codigo || ' - '|| vwadm.descricao as localDeTrabalho, " +
            "                 vworgao.codigo || ' - ' || vworgao.descricao as orga " +
            "            from ContratoFP contrato " +
            "           INNER JOIN VINCULOFP vinculo ON vinculo.ID = contrato.ID  " +
            "           INNER JOIN MATRICULAFP matricula ON matricula.id = vinculo.matriculafp_id  " +
            "           inner join PESSOAFISICA pessoa on matricula.pessoa_id = pessoa.id " +
            "           inner join lotacaofuncional lot on lot.VINCULOFP_ID = contrato.id " +
            "           inner join vwhierarquiaadministrativa vwadm on lot.UNIDADEORGANIZACIONAL_ID = vwadm.SUBORDINADA_ID " +
            "           inner join vwhierarquiaadministrativa vwOrgao on vinculo.UNIDADEORGANIZACIONAL_ID = vwOrgao.SUBORDINADA_ID " +
            "           where  to_date(:data, 'dd/mm/yyyy') between vinculo.inicioVigencia and coalesce(vinculo.finalVigencia, to_date(:data, 'dd/mm/yyyy')) " +
            "             and to_date(:data, 'dd/mm/yyyy') between vwadm.inicioVigencia and coalesce(vwadm.fimVigencia, to_date(:data, 'dd/mm/yyyy')) " +
            "             and to_date(:data, 'dd/mm/yyyy') between vworgao.inicioVigencia and coalesce(vworgao.fimVigencia, to_date(:data, 'dd/mm/yyyy')) " +
            "             and to_date(:data, 'dd/mm/yyyy') between lot.inicioVigencia and coalesce(lot.finalVigencia, to_date(:data, 'dd/mm/yyyy')) " +
            "             and pessoa.id = :pessoaFisica " +
            "           order by matricula.matricula, vinculo.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("data", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("pessoaFisica", idPessoaFisica);
        List<ContratoFPDTO> retorno = Lists.newArrayList();
        List<Object[]> resultado = q.getResultList();
        for (Object[] obj : resultado) {
            ContratoFPDTO contratoFPDTO = new ContratoFPDTO();
            contratoFPDTO.setMatricula((String) obj[0]);
            contratoFPDTO.setContrato((String) obj[1]);
            contratoFPDTO.setLocalDeTrabalho((String) obj[2]);
            contratoFPDTO.setOrgao((String) obj[3]);
            retorno.add(contratoFPDTO);
        }
        return retorno;
    }

    public List<PessoaFisica> listaPessoasComContratosVigentes(String parte) {
        String hql = " select new PessoaFisica(pessoa.id, pessoa.nome, pessoa.cpf) from ContratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pessoa "
            + " where :data >= contrato.inicioVigencia "
            + " and :data <= coalesce(contrato.finalVigencia, :data) "
            + " and ((lower(pessoa.nome) like :parte) or (pessoa.cpf like :parte)) "
            + " order by pessoa.nome";
        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setParameter("parte", "%" + parte.toLowerCase().trim().replace(".", "").replace("-", "") + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<PessoaFisica> listaPessoasComContratos(String parte) {
        String hql = " select distinct new PessoaFisica(pessoa.id, pessoa.nome) from ContratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pessoa "
            + " where ((lower(pessoa.nome) like :parte) or (pessoa.cpf like :parte) or (matricula.matricula like :parte)) ";
        Query q = em.createQuery(hql);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(20);
        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratoMatriculaPorLocalDeTrabalhoRecursiva(HierarquiaOrganizacional ho) {
        List<ContratoFP> contratosFPs = new ArrayList<>();
        Query q = em.createQuery(" select distinct contrato from LotacaoFuncional lotacao, ContratoFP contrato"
            + " inner join lotacao.vinculoFP vinculo "
            + " where vinculo = contrato and lotacao.unidadeOrganizacional in :unidade "
            + " order by contrato.matriculaFP");

        List<HierarquiaOrganizacional> listaHierarquia = new ArrayList<>();
        List<UnidadeOrganizacional> listaUnidade = new ArrayList<>();

        listaHierarquia.add(ho);

        if (ho.getSubordinada() != null) {
            for (HierarquiaOrganizacional item : (List<HierarquiaOrganizacional>) hierarquiaOrganizacionalFacadeOLD.recuperaHierarquiasFilhas(listaHierarquia, ho)) {
                listaUnidade.add(item.getSubordinada());
            }
        } else {
            listaUnidade.add(ho.getSubordinada());
        }

        q.setParameter("unidade", listaUnidade);
        return q.getResultList();
    }


    public List<ContratoFP> listaFiltrandoX(String s, int inicio, int max) {
        String hql = " select obj from ContratoFP obj "
            + " inner join obj.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " where (lower(pf.nome) like :filtro) "
            + " or (lower(pf.cpf) like :filtro) "
            + " or (lower(matricula.matricula) like :filtro) "
            + " or (lower(obj.numero) like :filtro)";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");

        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();

//        StringBuilder hql = new StringBuilder();
//        hql.append(" select ");
//        hql.append(" contrato.id  ");
//        hql.append(" from CONTRATOFP contrato inner join VINCULOFP vinculo on vinculo.ID = contrato.ID inner join");
//        hql.append(" MATRICULAFP matricula on matricula.ID = vinculo.MATRICULAFP_ID");
//        hql.append(" inner join PESSOAFISICA pf on pf.ID = matricula.PESSOA_ID WHERE");
//        hql.append(" (lower(pf.NOME) like :filtro) or");
//        hql.append(" (lower(pf.cpf) like :filtro) or");
//        hql.append(" (lower(matricula.MATRICULA) like :filtro) ");
//        hql.append(" and rownum <= :maximo ");
//        Query q = em.createNativeQuery(hql.toString());
//        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
//        q.setParameter("maximo", max);
//        q.setMaxResults(max + 1);
//        q.setFirstResult(inicio);
//        List<BigDecimal> ids = q.getResultList();
//        List<ContratoFP> contratos = new ArrayList<>();
//        for (BigDecimal id : ids) {
//            contratos.add(em.find(ContratoFP.class, id.longValue()));
//        }
//        return contratos;

    }

    public List<VinculoFP> buscaContratoFiltrandoAtributosMatriculaFP(String s) {
        String hql = " select new ContratoFP(obj.id, matricula.matricula||'/'||obj.numero||' - '||pessoa.nome) "
            + " from ContratoFP obj "
            + " inner join obj.matriculaFP matricula "
            + " inner join matricula.pessoa pessoa "
            + " left join matricula.unidadeMatriculado unidade "
            + " where ((lower(pessoa.nome) like :filtro) "
            + " OR (lower(unidade.descricao) like :filtro) "
            + " OR (lower(obj.numero) like :filtro) "
            + " OR (lower(matricula.matricula) like :filtro)) order by to_number(matricula.matricula)";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<VinculoFP> buscaContratoFiltrandoAtributosVinculoMatriculaFP(String s) {
        String id = StringUtil.retornaApenasNumeros(s);
        StringBuilder hql = new StringBuilder();
        hql.append("select new VinculoFP(obj.id, matricula.matricula||'/'||obj.numero||' - '||pessoa.nome|| ' (' || pessoa.nomeTratamento || ') ' || ' '||(select ' - APOSENTADO' from Aposentadoria a where a.id = obj.id)||' '||(select ' - PENSIONISTA' from Pensionista a where a.id = obj.id)) ");
        hql.append(" from VinculoFP obj ");
        hql.append(" inner join obj.matriculaFP matricula ");
        hql.append(" inner join matricula.pessoa pessoa ");
        hql.append(" left join matricula.unidadeMatriculado unidade ");
        hql.append(" where ((lower(pessoa.nome) like :filtro) ");
        hql.append(" OR (lower(unidade.descricao) like :filtro) ");
        hql.append(" OR (lower(obj.numero) like :filtro) ");
        hql.append(" OR (lower(obj.id) like :filtro) "
            + " OR (lower(matricula.matricula) like :filtro) ");
        hql.append(" OR (replace(replace(pessoa.cpf,'.',''),'-','') = :cpf) ");
        if (!Strings.isNullOrEmpty(id)) {
            hql.append(" OR (obj.id = :id)) ");
        } else {
            hql.append(")");
        }
        hql.append(" order by to_number(matricula.matricula), to_number(obj.numero)");
        Query q = em.createQuery(hql.toString());
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("cpf", StringUtil.retornaApenasNumeros(s));
        if (!Strings.isNullOrEmpty(id)) {
            q.setParameter("id", Long.parseLong(id));
        }
        return q.getResultList();

    }

    public List<VinculoFP> buscarDetalhesFuncionariosPorFiltro(String s) {
        String sql = "select result.id, result.text " +
            "from (select ap.id as id, mat.matricula || '/' || vinculo.numero || ' - ' || pf.nome || (replace(replace(pf.cpf,'.',''),'-','') ) ||' - APOSENTADO' as text " +
            "      from Aposentadoria ap " +
            "               inner join VinculoFP vinculo on ap.id = vinculo.id " +
            "               inner join MatriculaFP mat on vinculo.MATRICULAFP_ID = mat.id " +
            "               inner join PessoaFisica pf on mat.pessoa_id = pf.id " +
            " " +
            "union all " +
            "select pen.id as id, mat.matricula || '/' || vinculo.numero || ' - ' || pf.nome || (replace(replace(pf.cpf,'.',''),'-','') ) || ' - PENSIONISTA' as text " +
            "      from Pensionista pen " +
            "               inner join VinculoFP vinculo on pen.id = vinculo.id " +
            "               inner join MatriculaFP mat on vinculo.MATRICULAFP_ID = mat.id " +
            "               inner join PessoaFisica pf on mat.pessoa_id = pf.id " +
            " " +
            "union all " +
            "select bene.id as id, mat.matricula || '/' || vinculo.numero || ' - ' || pf.nome || (replace(replace(pf.cpf,'.',''),'-','') ) || ' - BENEFICIARIO' as text " +
            "      from Beneficiario bene " +
            "               inner join VinculoFP vinculo on bene.id = vinculo.id " +
            "               inner join MatriculaFP mat on vinculo.MATRICULAFP_ID = mat.id " +
            "               inner join PessoaFisica pf on mat.pessoa_id = pf.id " +
            "      ) result " +
            "where result.text like :filtro or result.text like :cpf ";

        Query q = em.createNativeQuery(sql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toUpperCase().trim() + "%");
        q.setParameter("cpf", StringUtil.retornaApenasNumeros(s));

        List resultList = q.getResultList();
        Util.imprimeSQL(sql, q);
        List<VinculoFP> retorno = Lists.newArrayList();
        for (Object o : resultList) {
            Object[] obj = (Object[]) o;
            VinculoFP vinculoFP = new VinculoFP(((Number) obj[0]).longValue(), (String) obj[1]);
            retorno.add(vinculoFP);
        }

        return retorno;
    }

    public List<VinculoFP> buscaContratoFiltrandoAtributosVinculoMatriculaFPPorEmpregador(String s, ConfiguracaoEmpregadorESocial empregador, Date competencia) {
        String id = StringUtil.retornaApenasNumeros(s);
        String hql = "select new VinculoFP(obj.id, matricula.matricula||'/'||obj.numero||' - '||pessoa.nome|| ' (' || pessoa.nomeTratamento || ') ' || ' '||(select ' - APOSENTADO' from Aposentadoria a where a.id = obj.id)||' '||(select ' - PENSIONISTA' from Pensionista a where a.id = obj.id)) " +
            " from VinculoFP obj , VwHierarquiaAdministrativa vw " +
            " inner join obj.lotacaoFuncionals lotacao " +
            " inner join lotacao.unidadeOrganizacional un " +
            " inner join obj.matriculaFP matricula " +
            " inner join matricula.pessoa pessoa " +
            " left join matricula.unidadeMatriculado unidade " +
            " where un.id = vw.subordinadaId " +
            " and substring(vw.codigo,1,5) in (select substring(hierarquia.codigo,1,5) from ItemConfiguracaoEmpregadorESocial item " +
            "             inner join item.configEmpregadorEsocial conf " +
            "             inner join item.hierarquiaOrganizacional hierarquia " +
            "             where conf.id = :empregador)" +
            " and :competencia between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia,:competencia) " +
            " and :dataAtual between vw.inicioVigencia and coalesce(vw.fimVigencia,:dataAtual) " +
            " and ((lower(pessoa.nome) like :filtro) " +
            " OR (lower(unidade.descricao) like :filtro) " +
            " OR (lower(obj.numero) like :filtro) " +
            " OR (lower(matricula.matricula) like :filtro) " +
            " OR (replace(replace(pessoa.cpf,'.',''),'-','') = :cpf) ";
        if (!Strings.isNullOrEmpty(id)) {
            hql += " OR (obj.id = :id)) ";
        } else {
            hql += ")";
        }
        hql += " order by to_number(matricula.matricula), to_number(obj.numero)";
        Query q = em.createQuery(hql.toString());
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("cpf", StringUtil.retornaApenasNumeros(s));
        q.setParameter("empregador", empregador.getId());
        q.setParameter("competencia", competencia);
        q.setParameter("dataAtual", sistemaFacade.getDataOperacao());
        if (!Strings.isNullOrEmpty(id)) {
            q.setParameter("id", Long.parseLong(id));
        }
        List toReturn = q.getResultList();
        if (!toReturn.isEmpty()) {
            return toReturn;
        }
        return Lists.newArrayList();

    }

    public List<ContratoFP> buscaContratoFPFiltrandoAtributosVinculoMatriculaFP(String s) {
        String hql = " select new ContratoFP(obj.id, matricula.matricula||'/'||obj.numero||' - '||pessoa.nome|| ' '||(select ' - APOSENTADO' from Aposentadoria a where a.id = obj.id)||' '||(select ' - PENSIONISTA' from Pensionista a where a.id = obj.id)) "
            + " from VinculoFP obj "
            + " inner join obj.matriculaFP matricula "
            + " inner join matricula.pessoa pessoa "
            + " left join matricula.unidadeMatriculado unidade "
            + " where ((lower(pessoa.nome) like :filtro) "
            + " OR (lower(unidade.descricao) like :filtro) "
            + " OR (lower(obj.numero) like :filtro) "
            + " OR (lower(matricula.matricula) like :filtro)) order by to_number(matricula.matricula), to_number(obj.numero)";
        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        return q.getResultList();

    }

    public List<VinculoFP> buscaContratoFiltrandoAtributosVinculoMatriculaFPHO(String s, HierarquiaOrganizacional hierarquia, Date data) {
        String hql = " select new VinculoFP(obj.id, matricula.matricula||'/'||obj.numero||' - '||pessoa.nome|| ' '||"
            + " (select ' - APOSENTADO' from Aposentadoria a where a.id = obj.id)||' '||(select ' - PENSIONISTA' from Pensionista a where a.id = obj.id)) "
            + " from VinculoFP obj,  VwHierarquiaAdministrativa view "
            + " inner join obj.matriculaFP matricula "
            + " inner join matricula.pessoa pessoa "
            + " inner join  obj.unidadeOrganizacional uni "
            + " where obj.unidadeOrganizacional.id = view.subordinadaId "
            + " and :data between obj.inicioVigencia and coalesce(obj.finalVigencia, :data) "
            + " and :data between view.inicioVigencia and coalesce(view.fimVigencia, :data) "
            + " and view.codigo like :codigo "
            + " and ((lower(pessoa.nome) like :filtro) "
            + " OR (lower(uni.descricao) like :filtro) "
            + " OR (lower(obj.numero) like :filtro) "
            + " OR (lower(matricula.matricula) like :filtro))"
            + " order by pessoa.nome ";

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("codigo", hierarquia.getCodigoSemZerosFinais() + "%");
        q.setParameter("data", UtilRH.getDataOperacao());
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public List<VinculoFP> buscaContratoFiltrandoAtributosVinculoMatriculaFPVigente(String s) {
        String hql = " select new VinculoFP(obj.id, matricula.matricula||'/'||obj.numero||' - '||pessoa.nome|| ' '||(select ' - APOSENTADO' from Aposentadoria a where a.id = obj.id)||' '||(select ' - PENSIONISTA' from Pensionista a where a.id = obj.id)) "
            + " from VinculoFP obj "
            + " inner join obj.matriculaFP matricula "
            + " inner join matricula.pessoa pessoa "
            + " left join matricula.unidadeMatriculado unidade "
            + " where (:data between obj.inicioVigencia and coalesce(obj.finalVigencia,:data)) AND " +
            "  ((lower(pessoa.nome) like :filtro) "
            + " OR (lower(unidade.descricao) like :filtro) "
            + " OR (lower(obj.numero) like :filtro) "
            + " OR (lower(matricula.matricula) like :filtro)) ";
        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("data", UtilRH.getDataOperacao());
        return q.getResultList();
    }


    public List<VinculoFP> buscaContratoFiltrandoAtributosVinculoMatriculaFPVigenteNaoCedidoSemOnus(String s, Date dataInicial, Date dataFinal) {
        String hql = " select new VinculoFP(obj.id, matricula.matricula||'/'||obj.numero||' - '||pessoa.nome|| ' '||(select ' - APOSENTADO' from Aposentadoria a where a.id = obj.id)||' '||(select ' - PENSIONISTA' from Pensionista a where a.id = obj.id)) "
            + " from VinculoFP obj "
            + " inner join obj.matriculaFP matricula "
            + " inner join matricula.pessoa pessoa "
            + " left join matricula.unidadeMatriculado unidade "
            + " where :dataAtual between obj.inicioVigencia and coalesce(obj.finalVigencia,:dataAtual) AND "
            + "  ((lower(pessoa.nome) like :filtro) "
            + " OR (lower(unidade.descricao) like :filtro) "
            + " OR (lower(obj.numero) like :filtro) "
            + " OR (lower(matricula.matricula) like :filtro))"
            + " and obj.id not in (select contratoFP.id "
            + "                   from CedenciaContratoFP cedencia "
            + "                            inner join cedencia.contratoFP contratoFP "
            + "                   where cedencia.tipoContratoCedenciaFP = :tipoCedencia "
            + "                     and :dataInicial between cedencia.dataCessao and coalesce(cedencia.dataRetorno, :dataInicial) "
            + "                     and :dataFinal between cedencia.dataCessao and coalesce(cedencia.dataRetorno, :dataFinal)) "
            + " and obj.id not in (select contratoFP.id "
            + "                from RetornoCedenciaContratoFP retorno "
            + "                         inner join retorno.cedenciaContratoFP cedencia "
            + "                         inner join cedencia.contratoFP contratoFP "
            + "                where cedencia.tipoContratoCedenciaFP = :tipoCedencia "
            + "                  and retorno.oficioRetorno = 0 "
            + "                  and :dataInicial between cedencia.dataCessao and coalesce(retorno.dataRetorno, :dataInicial)" +
            "                    and :dataFinal between cedencia.dataCessao and coalesce(retorno.dataRetorno, :dataFinal))";
        Query q = em.createQuery(hql);
        q.setMaxResults(50);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("dataAtual", UtilRH.getDataOperacao());
        q.setParameter("dataInicial", dataInicial);
        q.setParameter("dataFinal", dataFinal);
        q.setParameter("tipoCedencia", TipoCedenciaContratoFP.SEM_ONUS);
        return q.getResultList();
    }

    public boolean isVigente(VinculoFP vinculo) {
        return !em.createQuery("from VinculoFP v where v = :vinculo and  :data >= v.inicioVigencia and :data <= coalesce(v.finalVigencia, :data)").setParameter("data", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao())).setParameter("vinculo", vinculo).getResultList().isEmpty();
    }

    @Override
    public void salvar(ContratoFP entity) {
        processarHistoricoContratoFPAndAssociacoes(entity);
        entity = tratarClassificacaoInscricao(entity);
        em.merge(entity);
        em.flush();
        criarRegistroESocialAlteracaoContrato(entity);
    }

    private void processarHistoricoContratoFPAndAssociacoes(ContratoFP entity) {
        processarHistoricoContratoFP(entity);
        processarHistoricoSituacaoContratoFP(entity);
        processarHistoricoPrevidenciaVinculoFP(entity);
        processarHistoricoOpcaoValeTransporteFP(entity);
        processarHistoricoRecursoDoVinculoFP(entity);
        processarHistoricoLotacaoFuncionalAndHorarioContratoFP(entity);
        processarHistoricoSindicatoVinculoFP(entity);
        processarHistoricoLancamentoFP(entity);
        processarHistoricoEnquadramentoFuncional(entity);
    }

    public void processarHistoricoContratoFP(ContratoFP contrato) {
        ContratoFP contratoFPAtualmentePersistido = getContratoFPAtualmentePersistido(contrato);
        contrato.criarOrAtualizarAndAssociarHistorico(contratoFPAtualmentePersistido);
    }

    private ContratoFP getContratoFPAtualmentePersistido(ContratoFP entity) {
        return em.find(ContratoFP.class, entity.getId());
    }

    private void processarHistoricoLancamentoFP(ContratoFP contrato) {
        DateTime dt = new DateTime(contrato.getFinalVigencia());
        for (LancamentoFP lancamentoFP : lancamentoFPFacade.listaLancamentos(contrato, dt.getYear(), dt.getMonthOfYear())) {
            LancamentoFP lancamentoFPComHistorico = lancamentoFPFacade.getLancamentoFPComHistorico(lancamentoFP);
            em.merge(lancamentoFPComHistorico);
        }
    }

    private void processarHistoricoEnquadramentoFuncional(ContratoFP contrato) {
        EnquadramentoFuncional enquadramentoFuncionalVigente = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigente(contrato);
        if (enquadramentoFuncionalVigente != null) {
            EnquadramentoFuncional enquadramentoFuncionalComHistorico = enquadramentoFuncionalFacade.getEnquadramentoFuncionalComHistorico(enquadramentoFuncionalVigente);
            Util.adicionarObjetoEmLista(contrato.getEnquadramentos(), enquadramentoFuncionalComHistorico);
        }
    }

    private void processarHistoricoSindicatoVinculoFP(ContratoFP contrato) {
        SindicatoVinculoFP sindicatoVinculoFPVigente = contrato.getSindicatoVinculoFPVigente();
        if (sindicatoVinculoFPVigente != null) {
            SindicatoVinculoFP sindicatoVinculoFPComHistorico = sindicatoVinculoFPFacade.getSindicatoVinculoFPComHistorico(sindicatoVinculoFPVigente);
            Util.adicionarObjetoEmLista(contrato.getSindicatosVinculosFPs(), sindicatoVinculoFPComHistorico);
        }
    }

    private void processarHistoricoLotacaoFuncionalAndHorarioContratoFP(ContratoFP contrato) {
        LotacaoFuncional lotacaoFuncionalVigente = contrato.getLotacaoFuncionalVigente();
        if (lotacaoFuncionalVigente != null) {
            LotacaoFuncional lotacaoFuncionalComHistorico = lotacaoFuncionalFacade.getLotacaoFuncionalComHistorico(lotacaoFuncionalVigente);
            if (lotacaoFuncionalComHistorico.getHorarioContratoFP() != null && lotacaoFuncionalComHistorico.getHorarioContratoFP().getId() != null) {
                lotacaoFuncionalComHistorico.setHorarioContratoFP(horarioContratoFPFacade.getHorarioContratoFPComHistorico(lotacaoFuncionalComHistorico.getHorarioContratoFP()));
            }
            Util.adicionarObjetoEmLista(contrato.getLotacaoFuncionals(), lotacaoFuncionalComHistorico);
        }
    }

    private void processarHistoricoRecursoDoVinculoFP(ContratoFP contrato) {
        RecursoDoVinculoFP recursoDoVinculoFPVigente = contrato.getRecursoDoVinculoFPVigente();
        if (recursoDoVinculoFPVigente != null) {
            RecursoDoVinculoFP recursoDoVinculoFPComHistorico = recursoDoVinculoFPFacade.getRecursoDoVinculoFPComHistorico(recursoDoVinculoFPVigente);
            Util.adicionarObjetoEmLista(contrato.getRecursosDoVinculoFP(), recursoDoVinculoFPComHistorico);
        }
    }

    private void processarHistoricoOpcaoValeTransporteFP(ContratoFP contrato) {
        OpcaoValeTransporteFP opcaoValeTransporteFPVigente = contrato.getOpcaoValeTransporteFPVigente();
        if (opcaoValeTransporteFPVigente != null) {
            OpcaoValeTransporteFP opcaoValeTransporteFPComHistorico = opcaoValeTransporteFPFacade.getOpcaoValeTransporteFPComHistorico(opcaoValeTransporteFPVigente);
            Util.adicionarObjetoEmLista(contrato.getOpcaoValeTransporteFPs(), opcaoValeTransporteFPComHistorico);
        }
    }

    private void processarHistoricoPrevidenciaVinculoFP(ContratoFP contrato) {
        PrevidenciaVinculoFP previdenciaVinculoFPVigente = contrato.getPrevidenciaVinculoFPVigente();
        if (previdenciaVinculoFPVigente != null) {
            PrevidenciaVinculoFP previdenciaVinculoFPComHistorico = previdenciaVinculoFPFacade.getPrevidenciaVinculoFPComHistorico(previdenciaVinculoFPVigente);
            Util.adicionarObjetoEmLista(contrato.getPrevidenciaVinculoFPs(), previdenciaVinculoFPComHistorico);
        }
    }

    private void processarHistoricoSituacaoContratoFP(ContratoFP contrato) {
        SituacaoContratoFP situacaoContratoFPVigente = contrato.getSituacaoContratoFPVigente();
        if (situacaoContratoFPVigente != null) {
            SituacaoContratoFP situacaoContratoFPComHistorico = situacaoContratoFPFacade.getSituacaoContratoFPComHistorico(situacaoContratoFPVigente);
            Util.adicionarObjetoEmLista(contrato.getSituacaoFuncionals(), situacaoContratoFPComHistorico);
        }
    }

    public void processarHistoricoAndEncerrarVigenciasPorContratoFP(ContratoFP contratoFP, Date finalVigencia) {
        contratoFP = recuperar(contratoFP.getId());
        processarHistoricoContratoFPAndAssociacoes(contratoFP);

        encerrarVigenciaContratoFP(contratoFP, finalVigencia);
        encerrarVigenciaSituacaoContratoFP(contratoFP);
        encerrarVigenciaPrevidenciaVinculoFP(contratoFP);
        encerrarVigenciaOpcaoValeTransporteFP(contratoFP);
        encerrarVigenciaRecursoDoVinculoFP(contratoFP);
        encerrarVigenciaLotacaoFuncionalAndHorarioContratoFP(contratoFP);
        encerrarVigenciaLancamentoFP(contratoFP);
        encerrarVigenciaSindicatoVinculoFP(contratoFP);
        encerrarVigenciaEnquadramentoFuncional(contratoFP);
    }

    public void encerrarVigenciaEnquadramentoFuncional(ContratoFP contratoFP) {
        EnquadramentoFuncional enquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigente(contratoFP);
        if (enquadramentoFuncional != null) {
            enquadramentoFuncional.setFinalVigencia(contratoFP.getFinalVigencia());
            enquadramentoFuncional.setDataCadastroFinalVigencia(contratoFP.getFinalVigencia());
            em.merge(enquadramentoFuncional);
        }
    }

    public void encerrarVigenciaSindicatoVinculoFP(ContratoFP contratoFP) {
        SindicatoVinculoFP sindicatoVinculoFP = sindicatoVinculoFPFacade.recuperaSindicatoVinculoFPVigente(contratoFP, new Date());
        if (sindicatoVinculoFP != null) {
            sindicatoVinculoFP.setFinalVigencia(contratoFP.getFinalVigencia());
            em.merge(sindicatoVinculoFP);
        }
    }

    public void encerrarVigenciaLancamentoFP(ContratoFP contratoFP) {
        DateTime dt = new DateTime(contratoFP.getFinalVigencia());
        for (LancamentoFP lancamentoFP : lancamentoFPFacade.listaLancamentos(contratoFP, dt.getYear(), dt.getMonthOfYear())) {
            lancamentoFP.setMesAnoFinal(contratoFP.getFinalVigencia());
            em.merge(lancamentoFP);
        }
    }

    public void encerrarVigenciaLotacaoFuncionalAndHorarioContratoFP(ContratoFP contratoFP) {
        for (LotacaoFuncional lotacaoFuncional : lotacaoFuncionalFacade.recuperaLotacaosFuncionalVigentePorContratoFP(contratoFP)) {
            lotacaoFuncional.setFinalVigencia(contratoFP.getFinalVigencia());
            em.merge(lotacaoFuncional);
            HorarioContratoFP horarioContratoFP = lotacaoFuncional.getHorarioContratoFP();
            horarioContratoFP.setFinalVigencia(contratoFP.getFinalVigencia());
            em.merge(horarioContratoFP);
        }
    }

    public void encerrarVigenciaRecursoDoVinculoFP(ContratoFP contratoFP) {
        RecursoDoVinculoFP recursoDoVinculoFP = recursoDoVinculoFPFacade.recuperarRecursosDoVinculoByVinculoUltimoVigente(contratoFP);
        if (recursoDoVinculoFP != null) {
            recursoDoVinculoFP.setFinalVigencia(contratoFP.getFinalVigencia());
            em.merge(recursoDoVinculoFP);
        }
    }

    public void encerrarVigenciaOpcaoValeTransporteFP(ContratoFP contratoFP) {
        OpcaoValeTransporteFP opcaoValeTransporteFP = opcaoValeTransporteFPFacade.recuperaOpcaoValeTransporteFPVigente(contratoFP, new Date());
        if (opcaoValeTransporteFP != null) {
            opcaoValeTransporteFP.setFinalVigencia(contratoFP.getFinalVigencia());
            em.merge(opcaoValeTransporteFP);
        }
    }

    public void encerrarVigenciaPrevidenciaVinculoFP(ContratoFP contratoFP) {
        PrevidenciaVinculoFP previdenciaVinculoFPVigente = previdenciaVinculoFPFacade.buscarPrevidenciaVinculoFPVigentePorContratoFP(contratoFP);
        if (previdenciaVinculoFPVigente != null) {
            previdenciaVinculoFPVigente.setFinalVigencia(contratoFP.getFinalVigencia());
            em.merge(previdenciaVinculoFPVigente);
        }
    }

    public void encerrarVigenciaSituacaoContratoFP(ContratoFP contratoFP) {
        SituacaoContratoFP situacaoContratoFPRecuperada = situacaoContratoFPFacade.recuperaSituacaoContratoFPVigente(contratoFP);
        if (situacaoContratoFPRecuperada != null) {
            situacaoContratoFPRecuperada.setFinalVigencia(contratoFP.getFinalVigencia());
            em.merge(situacaoContratoFPRecuperada);
        }
    }

    public void encerrarVigenciaContratoFP(ContratoFP contratoFP, Date finalVigencia) {
        contratoFP.setFinalVigencia(finalVigencia);
        contratoFP = em.merge(contratoFP);
    }

    public void voltarVigenciasContratoFPAndAssociacoes(ContratoFP contratoFP, ProvimentoReversao provimentoReversao) {
        contratoFP = recuperar(contratoFP.getId());
        voltarFinalVigenciaContratoFP(contratoFP, provimentoReversao);
        voltarFinalVigenciaPrevidenciaVinculoFP(contratoFP);
        voltarFinalVigenciaOpcaoValeTransporteFP(contratoFP);
        voltarFinalVigenciaLotacaoFuncionalAndHorarioContratoFP(contratoFP);
        voltarFinalVigenciaSindicatoVinculoFP(contratoFP);
        voltarFinalVigenciaSituacaoContratoFP(contratoFP, SituacaoFuncional.EXONERADO_RESCISO);
        voltarFinalVigenciaRecursoDoVinculoFP(contratoFP);
        voltarFinalVigenciaEnquadramentoFuncional(contratoFP);
        voltarFinalVigenciaLancamentoFP(contratoFP);
    }

    public void voltarFinalVigenciaLancamentoFP(ContratoFP contratoFP) {
        DateTime dt = new DateTime(contratoFP.getFinalVigencia());
        for (LancamentoFP lancamentoFP : lancamentoFPFacade.listaLancamentos(contratoFP, dt.getYear(), dt.getMonthOfYear())) {
            lancamentoFP.voltarHistorico();
            em.merge(lancamentoFP);
        }
    }

    public void voltarFinalVigenciaEnquadramentoFuncional(ContratoFP contratoFP) {
        EnquadramentoFuncional enquadramentoFuncionalVigente = contratoFP.getEnquadramentoFuncionalVigente();
        if (enquadramentoFuncionalVigente != null) {
            enquadramentoFuncionalVigente.voltarHistorico();
            em.merge(enquadramentoFuncionalVigente);
        }
    }

    public void voltarFinalVigenciaRecursoDoVinculoFP(ContratoFP contratoFP) {
        RecursoDoVinculoFP recursoDoVinculoFPVigente = contratoFP.getRecursoDoVinculoFPVigente();
        if (recursoDoVinculoFPVigente != null) {
            recursoDoVinculoFPVigente.voltarHistorico();
            em.merge(recursoDoVinculoFPVigente);
        }
    }

    public void voltarFinalVigenciaSituacaoContratoFP(ContratoFP contratoFP, Long codigoSituacao) {
        excluirSituacaoContratoFPCriadaAoSalvarExoneracao(contratoFP, codigoSituacao);
        SituacaoContratoFP situacaoContratoFPVigente = contratoFP.getSituacaoContratoFPVigente();
        if (situacaoContratoFPVigente != null) {
            situacaoContratoFPVigente.voltarHistorico();
            em.merge(situacaoContratoFPVigente);
        }
    }

    private void excluirSituacaoContratoFPCriadaAoSalvarExoneracao(ContratoFP contratoFP, Long codigoSituacao) {
        SituacaoContratoFP situacaoContratoFPExoneracaoRescisao = situacaoContratoFPFacade.buscarSituacaoContratoFPPorContratoFPAndCodigoSituacaoFuncional(contratoFP, codigoSituacao);
        if (situacaoContratoFPExoneracaoRescisao != null) {
            contratoFP.getSituacaoFuncionals().remove(situacaoContratoFPExoneracaoRescisao);
            contratoFP = em.merge(contratoFP);
        }
    }

    public void voltarFinalVigenciaSindicatoVinculoFP(ContratoFP contratoFP) {
        SindicatoVinculoFP sindicatoVinculoFPVigente = contratoFP.getSindicatoVinculoFPVigente();
        if (sindicatoVinculoFPVigente != null) {
            sindicatoVinculoFPVigente.voltarHistorico();
            em.merge(sindicatoVinculoFPVigente);
        }
    }

    public void voltarFinalVigenciaLotacaoFuncionalAndHorarioContratoFP(ContratoFP contratoFP) {
        LotacaoFuncional lotacaoFuncionalVigente = contratoFP.getLotacaoFuncionalVigente();
        if (lotacaoFuncionalVigente != null) {
            lotacaoFuncionalVigente.voltarHistorico();
            em.merge(lotacaoFuncionalVigente);
        }
    }

    public void voltarFinalVigenciaOpcaoValeTransporteFP(ContratoFP contratoFP) {
        OpcaoValeTransporteFP opcaoValeTransporteFPVigente = contratoFP.getOpcaoValeTransporteFPVigente();
        if (opcaoValeTransporteFPVigente != null) {
            opcaoValeTransporteFPVigente.voltarHistorico();
            em.merge(opcaoValeTransporteFPVigente);
        }
    }

    public void voltarFinalVigenciaPrevidenciaVinculoFP(ContratoFP contratoFP) {
        PrevidenciaVinculoFP previdenciaVinculoFPVigente = contratoFP.getPrevidenciaVinculoFPVigente();
        if (previdenciaVinculoFPVigente != null) {
            previdenciaVinculoFPVigente.voltarHistorico();
            em.merge(previdenciaVinculoFPVigente);
        }
    }

    private void voltarFinalVigenciaContratoFP(ContratoFP contratoFP, ProvimentoReversao provimentoReversao) {
        contratoFP.voltarHistorico();
        if (provimentoReversao != null) {
            contratoFP.setFinalVigencia(provimentoReversao.getFinalVigencia());
        }
        contratoFP = em.merge(contratoFP);
    }

    private void reabrirVigenciaContratoFP(ContratoFP contratoFP) {
        VinculoFPHist historico = contratoFP.getVinculoFPHist();
        contratoFP.setFinalVigencia(historico.getFinalVigencia());

    }

    public List<ContratoFP> recuperaServidoresVigentesNaCompetenciaMatriculadosNaEntidade(HierarquiaOrganizacional ho, Integer mes, Integer ano) {
        Calendar primeiroDiaCompetencia = Calendar.getInstance();
        primeiroDiaCompetencia.set(Calendar.DAY_OF_MONTH, 1);
        primeiroDiaCompetencia.set(Calendar.MONTH, mes - 1);
        primeiroDiaCompetencia.set(Calendar.YEAR, ano);
        primeiroDiaCompetencia.setTime(Util.getDataHoraMinutoSegundoZerado(primeiroDiaCompetencia.getTime()));

        Query q = em.createQuery(" select distinct contrato from ContratoFP contrato, VwHierarquiaAdministrativa view "
            + " inner join contrato.lotacaoFuncionals lot"
            + " inner join contrato.previdenciaVinculoFPs previdencia "
            + " inner join previdencia.tipoPrevidenciaFP tipoPrevidencia "
            + " where lot.unidadeOrganizacional.id = view.subordinadaId "
            + "   and :data between lot.inicioVigencia and coalesce(lot.finalVigencia, :data) "
            + "   and :data between contrato.inicioVigencia and coalesce(contrato.finalVigencia, :data) "
            + "   and :data between previdencia.inicioVigencia and coalesce(previdencia.finalVigencia, :data) "
            + "   and tipoPrevidencia.codigo = :codigo "
            + "   and view.codigo like :codigoHo "
            + "     order by contrato.dataAdmissao ");
        q.setParameter("codigoHo", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("data", primeiroDiaCompetencia.getTime());
        q.setParameter("codigo", TipoPrevidenciaFP.PREVIDENCIA_GERAL);

        return q.getResultList();
    }


    public void salvarPrevidenciaArquivo(ContratoFP cfp) {
        for (PrevidenciaVinculoFP previdenciaVinculoFP : cfp.getPrevidenciaVinculoFPs()) {
            for (PrevidenciaArquivo obj : previdenciaVinculoFP.getPrevidenciaArquivos()) {
                if (obj.getArquivo().getId() == null && !obj.getExcluido()) {
                    salvarArquivo((UploadedFile) obj.getFile(), obj.getArquivo());
                }
            }
        }
    }

    public void salvarArquivo(UploadedFile uploadedFile, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = uploadedFile;
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType());
            arq.setTamanho(arquivoRecebido.getSize());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public List<ContratoFP> contratosTipoPrevidenciaRPPS(ContratoFP contrato) {

        Query q = em.createQuery(" select distinct contrato from ContratoFP contrato"
            + " inner join fetch contrato.previdenciaVinculoFPs previdencia "
            + " inner join previdencia.tipoPrevidenciaFP tipoPrevidencia "
            + " where contrato = :contrato "
            + " and tipoPrevidencia.codigo = 3 ");
        q.setParameter("contrato", contrato);
        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratosFpExoneradosEAdmitidosPorDataUnidadeOrganizacionalEPrevidenciaINSS(Mes mes, Integer ano, HierarquiaOrganizacional ho) {
        Date dataInicial = DataUtil.montaData(01, mes.getNumeroMes() - 1, ano).getTime();
        Date dataFinal = DataUtil.montaData(DataUtil.ultimoDiaDoMes(mes.getNumeroMes()), mes.getNumeroMes() - 1, ano).getTime();

        String hql = "  select distinct contrato "
            + "       from ExoneracaoRescisao er, ContratoFP con, FichaFinanceiraFP ff"
            + "      right join er.vinculoFP contrato "
            + " inner join con.recursosDoVinculoFP rv"
            + " inner join rv.recursoFP rfp"
            + " inner join ff.folhaDePagamento fp"
            + "      where con.id = contrato.id"
            + " and (:inicio between rv.inicioVigencia and coalesce(rv.finalVigencia, coalesce(rv.finalVigencia, :fim))"
            + "  or :fim between rv.inicioVigencia and coalesce(rv.finalVigencia, coalesce(rv.finalVigencia, :fim))"
            + "  or (rv.inicioVigencia between :inicio and :fim and rv.finalVigencia between :inicio and :fim))"
            + "        and ((extract(month from er.dataRescisao) = :mes "
            + "             and extract(year from er.dataRescisao) = :ano) "
            + "         or (extract(month from con.dataAdmissao) = :mes "
            + "             and extract(year from con.dataAdmissao) = :ano)) "
            + "        and ff.vinculoFP = con"
            + "        and rfp.codigoOrgao = :unidade";

        Query q = em.createQuery(hql);
        q.setParameter("mes", mes.getNumeroMes());
        q.setParameter("ano", ano);
        q.setParameter("inicio", dataInicial, TemporalType.DATE);
        q.setParameter("fim", dataFinal, TemporalType.DATE);
        q.setParameter("unidade", Integer.parseInt(ho.getCodigoDo2NivelDeHierarquia()));
        return q.getResultList();
    }

    public BigDecimal recuperaContratosVigentesPorData(HierarquiaOrganizacional ho, Date data) {
        String sql = "SELECT count(distinct con.id) FROM contratofp con " +
            " INNER JOIN vinculofp vinc ON con.ID = vinc.ID " +
            " INNER JOIN situacaoContratoFP situacaoContratoFP ON situacaoContratoFP.contratofp_id = vinc.ID " +
            " INNER JOIN situacaofuncional situacao on situacaoContratoFP.situacaofuncional_id = situacao.id " +
            " INNER JOIN LotacaoFuncional lot ON lot.vinculofp_id = vinc.ID " +
            " INNER JOIN VwHierarquiaAdministrativa ho ON lot.unidadeorganizacional_id = ho.subordinada_id " +
            " INNER JOIN previdenciavinculofp prevvinc ON prevvinc.contratofp_id = vinc.ID " +
            " INNER JOIN tipoprevidenciafp tipoprev ON tipoprev.ID = prevvinc.tipoPrevidenciaFP_id " +
            " INNER JOIN fichafinanceirafp ficha ON ficha.vinculofp_id = vinc.ID " +
            " INNER JOIN folhadepagamento folha ON ficha.folhadepagamento_id = folha.ID " +
            " WHERE ho.codigo LIKE :ho " +
            " AND folha.mes = :mes " +
            " AND folha.ano = :ano " +
            " AND tipoprev.codigo = 1 " +
            " AND situacao.codigo NOT IN (6, 10, 11) " +
            " AND (to_date(:dataReferencia, 'dd/MM/yyyy') BETWEEN vinc.inicioVigencia AND  coalesce(vinc.finalVigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))) " +
            " AND (to_date(:dataReferencia, 'dd/MM/yyyy') BETWEEN lot.inicioVigencia AND COALESCE(lot.finalVigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))) " +
            " AND (to_date(:dataReferencia, 'dd/MM/yyyy') BETWEEN ho.inicioVigencia AND coalesce(ho.fimVigencia,to_date(:dataReferencia, 'dd/MM/yyyy')))";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(data));
        q.setParameter("ho", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("mes", DataUtil.getMes(data) - 1);
        q.setParameter("ano", DataUtil.getAno(data));
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public List<ContratoFP> recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursivaPelaView(HierarquiaOrganizacional ho, Date data) {
        List<ContratoFP> contratosFPs = new ArrayList<>();
        String s = new String();
        s = "SELECT contrato.id FROM LOTACAOFUNCIONAL lotacao INNER JOIN CONTRATOFP contrato ON contrato.ID = lotacao.VINCULOFP_ID "
            + "INNER JOIN VINCULOFP vinculo ON vinculo.ID = contrato.ID "
            + "INNER JOIN UNIDADEORGANIZACIONAL un ON un.id = lotacao.unidadeorganizacional_id "
            + "INNER JOIN VWHIERARQUIAADMINISTRATIVA ho ON ho.subordinada_id = un.id "
            + "INNER JOIN MATRICULAFP matricula ON matricula.id = vinculo.matriculafp_id "
            + "WHERE "
            + "    :vigencia >= vinculo.inicioVigencia AND :vigencia <= coalesce(vinculo.finalVigencia,:vigencia) "
            + "AND :vigencia >= lotacao.inicioVigencia AND :vigencia <= coalesce(lotacao.finalVigencia,:vigencia) "
            + "AND :vigencia >= ho.inicioVigencia AND :vigencia <= coalesce(ho.fimVigencia,:vigencia) "
            + "AND ho.codigo LIKE :unidade  "
            + " ORDER BY matricula.matricula";

        Query q = em.createNativeQuery(s);
        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("vigencia", Util.getDataHoraMinutoSegundoZerado(data));
        for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
            contratosFPs.add(em.find(ContratoFP.class, id.longValue()));
        }
        return contratosFPs;
    }

    public List<ContratoFP> recuperaContratosVigentesPelaHierarquiaDataETemFichaFinanceira(HierarquiaOrganizacional ho, Calendar calendar) {
        List<ContratoFP> contratosFPs = new ArrayList<>();
        String s = "SELECT distinct contrato.id FROM LOTACAOFUNCIONAL lotacao " +
            " INNER JOIN CONTRATOFP contrato ON contrato.ID = lotacao.VINCULOFP_ID " +
            " INNER JOIN VINCULOFP vinculo ON vinculo.ID = contrato.ID " +
            " INNER JOIN UNIDADEORGANIZACIONAL un ON un.id = lotacao.unidadeorganizacional_id " +
            " INNER JOIN VWHIERARQUIAADMINISTRATIVA ho ON ho.subordinada_id = un.id " +
            " INNER JOIN MATRICULAFP matricula ON matricula.id = vinculo.matriculafp_id " +
            " inner join fichafinanceirafp ficha on ficha.vinculofp_id = vinculo.id " +
            " inner join folhadepagamento folha on folha.id = ficha.folhadepagamento_id " +
            " WHERE to_date(:data) >= vinculo.inicioVigencia AND to_date(:data) <= coalesce(vinculo.finalVigencia,to_date(:data)) " +
            " AND to_date(:data) >= lotacao.inicioVigencia AND to_date(:data) <= coalesce(lotacao.finalVigencia,to_date(:data)) " +
            " AND to_date(:data) >= ho.inicioVigencia AND to_date(:data) <= coalesce(ho.fimVigencia,to_date(:data)) " +
            " AND ho.codigo LIKE :unidade " +
            " and folha.mes = :mes and folha.ano = :ano and folha.tipofolhadepagamento = 'NORMAL' ";

        Query q = em.createNativeQuery(s);

        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("data", calendar.getTime(), TemporalType.DATE);
        q.setParameter("mes", calendar.get(Calendar.MONTH));
        q.setParameter("ano", calendar.get(Calendar.YEAR));

        for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
            contratosFPs.add(em.find(ContratoFP.class, id.longValue()));
        }
        return contratosFPs;
    }

    public List<ContratoFP> recuperaContratosVigentesComFaltasInjustificadas(String parte) {
        String hql = "select distinct c from Faltas f inner join f.contratoFP c where :data between c.inicioVigencia and coalesce(c.finalVigencia,:data)" +
            " and f.tipoFalta = :tipo and " +
            " ((lower(c.matriculaFP.pessoa.nome) like :filtro) "
            + " OR (replace(replace(replace(c.matriculaFP.pessoa.cpf,'.',''),'-',''),'/','') like :filtro) "
            + " OR (lower(c.numero) like :filtro) "
            + " OR (lower(c.matriculaFP.matricula) like :filtro)) ";
        Query q = em.createQuery(hql);

        q.setParameter("data", UtilRH.getDataOperacao(), TemporalType.DATE);
        q.setParameter("tipo", TipoFalta.FALTA_INJUSTIFICADA);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");

        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratoVigenteMatriculaPorLocalDeTrabalhoRecursivaParaCreditoDeSalario(String s, HierarquiaOrganizacional ho, Date data, FolhaDePagamento folhaPagamento) {
        List<ContratoFP> contratosFPs = new ArrayList<>();

        String sql = "select distinct contrato.id from LOTACAOFUNCIONAL lotacao "
            + " inner join CONTRATOFP contrato on contrato.ID = lotacao.VINCULOFP_ID "
            + " inner join VINCULOFP vinculo on vinculo.ID = contrato.ID "
            + " inner join UNIDADEORGANIZACIONAL un on un.id = lotacao.unidadeorganizacional_id "
            + " inner join HierarquiaOrganizacional h on h.subordinada_id = un.id "
            + " inner join VWHIERARQUIAADMINISTRATIVA ho on ho.id = h.id "
            + " inner join MATRICULAFP matricula on matricula.id = vinculo.matriculafp_id "
            + " inner join PESSOAFISICA pessoa on matricula.pessoa_id = pessoa.id "
            + " inner join FICHAFINANCEIRAFP ficha on ficha.vinculoFP_id = vinculo.ID "
            + " where "
            + " :vigencia >= vinculo.inicioVigencia and :vigencia <= coalesce(vinculo.finalVigencia,:vigencia) "
            + " and ficha.creditoSalarioPago = 0 "
            + " and ficha.folhaDePagamento_id = :folha "
            + " and :vigencia >= lotacao.inicioVigencia and :vigencia <= coalesce(lotacao.finalVigencia,:vigencia) "
            + " and :vigencia >= ho.inicioVigencia and :vigencia <= coalesce(ho.fimVigencia,:vigencia) "
            + " and ho.codigo like :unidade  "
            + " and ((lower(pessoa.nome) like :filtro) OR "
            + " (lower(pessoa.cpf) like :filtro) OR "
            + " (lower(matricula.matricula) like :filtro)) "
            + " and rownum <= 10 ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("vigencia", Util.getDataHoraMinutoSegundoZerado(data), TemporalType.DATE);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("folha", folhaPagamento.getId());

        for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
            contratosFPs.add(em.find(ContratoFP.class, id.longValue()));
        }
        return contratosFPs;
    }

    private void montarS3000(ContratoFP contrato) {
        ConfiguracaoEmpregadorESocial config = buscarEmpregadorPorVinculoFP(contrato);
        if (config != null) {
            List<EventoESocialDTO> itensS2190 = Lists.newArrayList();
            List<EventoESocialDTO> itensS2200 = Lists.newArrayList();
            List<EventoESocialDTO> itensS2205 = Lists.newArrayList();
            List<EventoESocialDTO> itensS2206 = Lists.newArrayList();

            List<EventoESocialDTO> eventoESocial = empregadorService.getEventosPorEmpregadorAndIdEsocial(config.getEntidade(), contrato.getId().toString());
            for (EventoESocialDTO evento : eventoESocial) {
                if (TipoArquivoESocial.S2190.equals(evento.getTipoArquivo())) {
                    itensS2190.add(evento);
                }
                if (TipoArquivoESocial.S2200.equals(evento.getTipoArquivo())) {
                    itensS2200.add(evento);
                }
                if (TipoArquivoESocial.S2205.equals(evento.getTipoArquivo())) {
                    itensS2205.add(evento);
                }
                if (TipoArquivoESocial.S2206.equals(evento.getTipoArquivo())) {
                    itensS2206.add(evento);
                }
            }
        }
    }

    @Override
    public void remover(ContratoFP entity) {
        if (contratoPossuiEnquadramento(entity)) {
            throw new ExcecaoNegocioGenerica("O contrato " + entity + " possui enquadramento funcional e não poderá ser excluído.");
        }

        entity.setProvimentoFP(null);
        entity = em.merge(entity);
        List<ProvimentoFP> provimentos = provimentoFPFacade.recuperaProvimentosPorVinculoFP(entity);
        for (ProvimentoFP prov : provimentos) {
            provimentoFPFacade.remover(prov);
        }

        pastaGavetaFacade.removerPastaGavetaContratoFP(entity);
        List<LancamentoTercoFeriasAut> lancamentosTerco = lancamentoTercoFeriasAutFacade.recuperaLancamentosTercoFeriasAutPorContrato(entity);
        for (LancamentoTercoFeriasAut terco : lancamentosTerco) {
            if (fichaFinanceiraFPFacade.recuperaFichaFinanceiraFPPorVinculoFPMesAno(entity, terco.getAno(), terco.getMes()) == null) {
                lancamentoTercoFeriasAutFacade.remover(terco);
            }
        }

        List<FilaProcessamentoFolha> filas = filaProcessamentoFolhaFacade.buscarFilasProcessamentosFolhaPorVinculoFP(entity);
        for (FilaProcessamentoFolha fila : filas) {
            filaProcessamentoFolhaFacade.remover(fila);
        }

        super.remover(entity);
    }

    public List<ContratoFP> recuperarContratosVigentesComFichaFinanceira(Integer ano, Integer mes, Date data) {
        Query q = em.createQuery("select contrato from FichaFinanceiraFP ff, ContratoFP contrato join ff.vinculoFP vinculo where vinculo = contrato " +
            "                       and ff.folhaDePagamento.ano = :ano and ff.folhaDePagamento.mes = :mes" +
            "                       and :data between vinculo.inicioVigencia and coalesce(vinculo.finalVigencia, :data) order by cast(contrato.matriculaFP.matricula as integer)");
        q.setParameter("ano", ano);
        q.setParameter("mes", Mes.getMesToInt(mes));
        q.setParameter("data", data, TemporalType.DATE);
        return q.getResultList();
    }

    public SituacaoContratoFP colocarContratoFPEmSituacaoFuncional(ContratoFP c, Date dataEntradaSituacaoFuncional, Long codigoSituacaoFuncional) {
        SituacaoFuncional sf = situacaoFuncionalFacade.recuperaCodigo(codigoSituacaoFuncional);
        if (sf == null) {
            throw new ExcecaoNegocioGenerica("Código da situação funcional para disponibilidade não está cadastrado. Por favor, cadastre uma situação funcional com o código 12 que será utilizada para os servidores que entrarão em Disponibilidade.");
        }

        SituacaoContratoFP scNova = new SituacaoContratoFP();
        SituacaoContratoFP scAntiga = situacaoContratoFPFacade.recuperarSituacaoFuncionalVigenteEm(c, dataEntradaSituacaoFuncional);
        if (scAntiga != null) {
            Date dataFinalScAntiga = DataUtil.getDataDiaAnterior(dataEntradaSituacaoFuncional);
            scAntiga.setFinalVigencia(dataFinalScAntiga);
            em.merge(scAntiga);
        }

        scNova.setContratoFP(c);
        scNova.setInicioVigencia(dataEntradaSituacaoFuncional);
        scNova.setSituacaoFuncional(sf);
        scNova = em.merge(scNova);

//        c.setSituacaoFuncionals(Util.adicionarObjetoEmLista(c.getSituacaoFuncionals(), scNova));
//        em.merge(c);
        return scNova;
    }

    public ProvimentoFP adicionarProvimentoNoContrato(ContratoFP c, Date dataProvimento, AtoLegal atoLegal, Integer cod) {
        TipoProvimento tp = tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(cod);
        if (tp == null) {
            throw new ExcecaoNegocioGenerica("Tipo de provimento para disponibilidade não está cadastrado. Por favor, cadastre um tipo de provimento com o código 71 que será utilizado para os servidores que entrarão em Disponibilidade.");
        }

        ProvimentoFP p = new ProvimentoFP();
        p.setVinculoFP(c);
        p.setDataProvimento(dataProvimento);
        p.setTipoProvimento(tp);
        p.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(c));
        p.setAtoLegal(atoLegal);
        p = em.merge(p);

        c.setProvimentoFP(p);
        em.merge(c);
        return p;
    }

//    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
//    public List<ContratoFP> buscarServidoresAtivosArquivoAtuarial(Long codigoTipoRegime, Date dataReferencia) {
//
//        String sql = "select distinct contrato from ContratoFP contrato " +
//                "        inner join VinculoFP vinc on contrato.id = vinc.id " +
//                "        inner join situacaocontratofp sitcon on sitcon.contratofp_id = contrato.id " +
//                "        inner join situacaofuncional sitfun on sitcon.situacaofuncional_id = sitfun.id " +
//                "        inner join tiporegime regime on contrato.tiporegime_id = regime.id " +
//                "        left join (select af.* from afastamento af " +
//                "                   inner join tipoAfastamento tipoAF on tipoAF.id = af.tipoAfastamento_ID " +
//                "                        where tipoaf.processanormal = :processaNormal) af ON af.contratofp_id = contrato.id " +
//                "        where :dataReferencia between vinc.inicioVigencia and coalesce(vinc.finalVigencia, :dataReferencia) " +
//                "        and regime.codigo = :regime " +
//                "        and (sitfun.codigo = :situacaoFuncionalAtivoFolha or sitfun.codigo = :situacaoFuncionalEmFerias) " +
//                "        and :dataReferencia between sitcon.inicioVigencia and coalesce(sitcon.finalVigencia, :dataReferencia)";
//        Query q = em.createNativeQuery(sql);
//
//        q.setParameter("dataReferencia", dataReferencia);
//        q.setParameter("regime", codigoTipoRegime);
//
//        q.setParameter("processaNormal", true);
//        q.setParameter("situacaoFuncionalAtivoFolha", SituacaoFuncional.ATIVO_PARA_FOLHA);
//        q.setParameter("situacaoFuncionalEmFerias", SituacaoFuncional.EM_FERIAS);
//
//        return q.getResultList();
//    }

    public List<Long> buscarServidoresAtivosArquivoAtuarial(Date dataReferencia) {
        return buscarServidoresAtivosArquivoAtuarial(dataReferencia, null);
    }

    public List<Long> buscarServidoresAtivosArquivoAtuarial(Date dataReferencia, Exercicio exercicioAdmissao) {
        String sql = "select distinct v.id " +
            " from  VinculoFP v " +
            " inner join Contratofp contrato on contrato.id = v.id " +
            " inner join FichaFinanceiraFP ffp on ffp.vinculofp_id = v.id " +
            " inner join FolhaDePagamento fp on fp.id = ffp.folhadepagamento_id " +
            " inner join ItemFichaFinanceiraFP item on item.fichafinanceirafp_id = ffp.id " +
            " inner join EventoFP evento on evento.id = item.eventofp_id " +
            " inner join previdenciavinculofp previdencia on previdencia.contratofp_id = v.id " +
            " inner join tipoprevidenciafp tiporegime on previdencia.tipoprevidenciafp_id = tiporegime.id" +
            "      where fp.mes = :mes            " +
            "        and fp.ano = :ano            " +
            "        and evento.codigo = :codigo " +
            "        and tiporegime.codigo = :tipoRegime ";

        if (exercicioAdmissao != null && exercicioAdmissao.getAno() != null) {
            sql += " and extract(year from c.dataAdmissao) = :anoAdmissao";
        }
        EventoFP rpps = configuracaoRHFacade.recuperarConfiguracaoRHVigente(dataReferencia).getConfiguracaoFP().getRpps();
        if (rpps == null) {
            throw new ValidacaoException("A Configuração da verba de RPPS não foi encontrada no sistema. Realize a configuração para prosseguir com a geração do arquivo. Verifique em Configurações Gerais RH.");
        }

        Query q = em.createNativeQuery(sql);
        DateTime date = new DateTime(dataReferencia);
        q.setParameter("codigo", rpps.getCodigo());
        q.setParameter("mes", DataUtil.getMesIniciandoEmZero(dataReferencia));

        q.setParameter("ano", date.getYear());
        q.setParameter("tipoRegime", TipoPrevidenciaFP.RPPS);

        if (exercicioAdmissao != null && exercicioAdmissao.getAno() != null) {
            q.setParameter("anoAdmissao", exercicioAdmissao.getAno());
        }

        Set<Long> ids = new HashSet<>();
        for (Object o : q.getResultList()) {
            ids.add(((Number) o).longValue());
        }
        return new ArrayList<>(ids);
    }

    public List<Long> buscarServidoresComRegistroObito(Date dataReferencia, Exercicio exercicioAdmissao) {
        String sql = "  select distinct vinculo.id from vinculofp vinculo " +
            " inner join contratofp contrato on vinculo.id = contrato.id" +
            " inner join matriculafp mat on vinculo.MATRICULAFP_ID = mat.id " +
            " inner join pessoafisica pf on mat.PESSOA_ID =pf.id " +
            " inner join REGISTRODEOBITO obito on obito.PESSOAFISICA_ID = pf.id " +
            " inner join previdenciavinculofp previdencia on previdencia.contratofp_id = vinculo.id " +
            " inner join tipoprevidenciafp tiporegime on previdencia.tipoprevidenciafp_id = tiporegime.id " +
            " where extract(year from obito.DATAFALECIMENTO) = :ano " +
            " and extract (month from obito.DATAFALECIMENTO) = :mes " +
            " and tiporegime.codigo = :tipoRegime ";
        if (exercicioAdmissao != null && exercicioAdmissao.getAno() != null) {
            sql += " and extract(year from vinculo.inicioVigencia) = :admissao";
        }
        Query q = em.createNativeQuery(sql);
        DateTime date = new DateTime(dataReferencia);
        q.setParameter("mes", date.getMonthOfYear());
        q.setParameter("ano", date.getYear());
        q.setParameter("tipoRegime", TipoPrevidenciaFP.RPPS);
        if (exercicioAdmissao != null && exercicioAdmissao.getAno() != null) {
            q.setParameter("admissao", exercicioAdmissao.getAno());
        }
        List<Long> ids = Lists.newArrayList();
        for (Object o : q.getResultList()) {
            ids.add(((BigDecimal) o).longValue());
        }
        return ids;
    }

    public List<Long> buscarServidoresExonerados(Date dataReferencia, Exercicio exercicioAdmissao) {
        String sql = "  select distinct vinculo.id from vinculofp vinculo " +
            " inner join contratofp contrato on vinculo.id = contrato.id " +
            " inner join matriculafp mat on vinculo.MATRICULAFP_ID = mat.id " +
            " inner join pessoafisica pf on mat.PESSOA_ID =pf.id " +
            " inner join EXONERACAORESCISAO exonerado on exonerado.VINCULOFP_ID = vinculo.id " +
            " inner join previdenciavinculofp previdencia on previdencia.contratofp_id = vinculo.id " +
            " inner join tipoprevidenciafp tiporegime on previdencia.tipoprevidenciafp_id = tiporegime.id " +
            " where extract(year from exonerado.DATARESCISAO) = :ano " +
            " and extract (month from exonerado.DATARESCISAO) = :mes " +
            " and tiporegime.codigo = :tipoRegime ";

        if (exercicioAdmissao != null && exercicioAdmissao.getAno() != null) {
            sql += " and extract(year from vinculo.inicioVigencia) = :anoAdmissao";
        }
        Query q = em.createNativeQuery(sql);
        DateTime date = new DateTime(dataReferencia);
        q.setParameter("mes", date.getMonthOfYear());
        q.setParameter("ano", date.getYear());
        q.setParameter("tipoRegime", TipoPrevidenciaFP.RPPS);
        if (exercicioAdmissao != null && exercicioAdmissao.getAno() != null) {
            q.setParameter("anoAdmissao", exercicioAdmissao.getAno());
        }
        List<Long> ids = Lists.newArrayList();
        for (Object o : q.getResultList()) {
            ids.add(((BigDecimal) o).longValue());
        }
        return ids;
    }

    public List<Cargo> recuperarCargosServidoresAtivosBBAtuarial(Date dataReferencia) {
        String hql = "  select c.cargo from VinculoFP v, ContratoFP c"
            + " inner join v.fichasFinanceiraFP ffp "
            + " inner join ffp.folhaDePagamento fp  "
            + "      where fp.mes = :mes            "
            + "        and fp.ano = :ano            "
            + "        and c.id = v.id              "
            + "        and c.tipoRegime.codigo = :codigoTipoRegime order by to_number(c.cargo.codigoDoCargo)";

        Query q = em.createQuery(hql);
        q.setParameter("mes", Mes.getMesToInt(DataUtil.getMes(dataReferencia)));
        q.setParameter("ano", DataUtil.getAno(dataReferencia));
        q.setParameter("codigoTipoRegime", TipoRegime.ESTATUTARIO);

        List<Cargo> cargos = new ArrayList(new HashSet(q.getResultList()));
        return cargos;
    }


    public List<NivelEscolaridade> recuperarEscolaridadeServidoresAtivosBBAtuarial(Date dataReferencia) {
        String hql = "  select c.matriculaFP.pessoa.nivelEscolaridade from VinculoFP v, ContratoFP c"
            + " inner join v.fichasFinanceiraFP ffp "
            + " inner join ffp.folhaDePagamento fp  "
            + "      where fp.mes = :mes            "
            + "        and fp.ano = :ano            "
            + "        and c.id = v.id              "
            + "        and c.tipoRegime.codigo = :codigoTipoRegime order by c.matriculaFP.pessoa.nivelEscolaridade.ordem";

        Query q = em.createQuery(hql);
        q.setParameter("mes", Mes.getMesToInt(DataUtil.getMes(dataReferencia)));
        q.setParameter("ano", DataUtil.getAno(dataReferencia));
        q.setParameter("codigoTipoRegime", TipoRegime.ESTATUTARIO);

        List<NivelEscolaridade> ne = new ArrayList(new HashSet(q.getResultList()));
        return ne;
    }

    public List<ContratoFP> recuperarFiltrandoContratosVigentesEm(String s, Date dataOperacao) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select contrato ");
        hql.append(" from ContratoFP contrato ");
        hql.append(" inner join contrato.matriculaFP matricula ");
        hql.append(" inner join matricula.pessoa pf WHERE");
        hql.append(" ((lower(pf.nome) like :filtro) or");
        hql.append(" (lower(pf.cpf) like :filtro) or");
        hql.append(" (lower(matricula.matricula) like :filtro)) ");
        hql.append(" and :dataVigencia >= contrato.inicioVigencia ");
        hql.append(" and :dataVigencia <= coalesce(contrato.finalVigencia,:dataVigencia) order by to_number(matricula.matricula), pf.nome");

        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataOperacao));
        q.setMaxResults(50);

        return q.getResultList();
    }

    public List<ContratoFP> buscarContratos(String s) {
        StringBuilder hql = new StringBuilder();
        hql.append(" select contrato ");
        hql.append(" from ContratoFP contrato ");
        hql.append(" inner join contrato.matriculaFP matricula ");
        hql.append(" inner join matricula.pessoa pf WHERE");
        hql.append(" ((lower(pf.nome) like :filtro) or");
        hql.append(" (lower(pf.cpf) like :filtro) or");
        hql.append(" (lower(matricula.matricula) like :filtro)) ");
        hql.append(" order by to_number(matricula.matricula), pf.nome");

        Query q = em.createQuery(hql.toString());
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList();
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public List<ContratoFP> recuperaContratosVigentesPelaHierarquiaDataETemFichaFinanceiraRAIS(HierarquiaOrganizacional ho, Integer ano) {

        Date dataFinal = DataUtil.montaData(31, 11, ano).getTime();

        String hql = " select distinct contrato " +
            " from ContratoFP contrato, FichaFinanceiraFP ficha, HierarquiaOrganizacional ho " +
            " inner join ficha.folhaDePagamento folha " +
            " inner join contrato.lotacaoFuncionals lotacao " +
            " where lotacao.unidadeOrganizacional = ho.subordinada" +
            " and contrato.id = ficha.vinculoFP.id" +
            " and contrato.modalidadeContratoFP.codigo not in ('6', '7', '8')" +
            " and contrato.vinculoEmpregaticio.codigo <> 0 " +
            " and (folha.ano = :ano or (folha.mes = 11 and folha.ano = :anoAnterior))" +
            " and folha.tipoFolhaDePagamento in ('NORMAL', 'SALARIO_13', 'RESCISAO', 'ADIANTAMENTO_13_SALARIO', 'RESCISAO_COMPLEMENTAR')" +
            " and ho.tipoHierarquiaOrganizacional = 'ADMINISTRATIVA' " +
            " and ho.codigo like :unidade " +
            " and (extract(year from ho.inicioVigencia) <= :ano and extract(year from coalesce(ho.fimVigencia, :dataFinal)) >= :ano)" +
            " and (extract(year from contrato.inicioVigencia) <= :ano and extract(year from coalesce(contrato.finalVigencia, :dataFinal)) >= :ano)" +
            " and (extract(year from lotacao.inicioVigencia) <= :ano and extract(year from coalesce(lotacao.finalVigencia, :dataFinal)) >= :ano)" +
            " and (lotacao.inicioVigencia between :hoInicioVigencia and coalesce(:hoFinalVigencia, :dataFinal)" +
            "     or lotacao.finalVigencia between :hoInicioVigencia and coalesce(:hoFinalVigencia, :dataFinal)) ";

        Query q = em.createQuery(hql);

        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
        q.setParameter("dataFinal", dataFinal);
        q.setParameter("hoInicioVigencia", ho.getInicioVigencia());
        q.setParameter("hoFinalVigencia", ho.getFimVigencia());
        q.setParameter("ano", DataUtil.getAno(dataFinal));
        q.setParameter("anoAnterior", (DataUtil.getAno(dataFinal) - 1));

        return q.getResultList();
    }

    public List<PessoaFisica> listaPessoasComContratosVigentesAno(String parte, Date dataReferencia) {
        String hql = " select new PessoaFisica(pessoa.id, pessoa.nome, pessoa.cpf) from ContratoFP contrato "
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pessoa "
            + " where extract(year from :dataReferencia) between extract (year from contrato.inicioVigencia) "
            + " and extract(year from coalesce(contrato.finalVigencia, :dataReferencia)) "
            + " and ((lower(pessoa.nome) like :parte) or (pessoa.cpf like :parte)) "
            + " order by pessoa.nome";

        Query q = em.createQuery(hql);
        q.setParameter("dataReferencia", dataReferencia);
        q.setParameter("parte", "%" + parte.toLowerCase().trim().replace(".", "").replace("-", "") + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<ContratoFP> recuperaContratosVigentesPelaHierarquiaMesAno(HierarquiaOrganizacional ho, Mes mes, Integer ano) {

        String hql = " select distinct contrato " +
            " from FichaFinanceiraFP ficha, VwHierarquiaAdministrativa ho " +
            " inner join ficha.vinculoFP contrato " +
            " inner join ficha.folhaDePagamento folha " +
            " inner join contrato.lotacaoFuncionals lotacao " +
            " where lotacao.unidadeOrganizacional.id = ho.subordinadaId " +
            " and folha.mes = :mes and folha.ano = :ano and folha.tipoFolhaDePagamento in ('NORMAL')" +
            " and ho.codigo like :unidade " +
            " and to_date(to_char(:dataFinal,'yyyy'),'yyyy') between " +
            "                                     to_date(to_char(contrato.inicioVigencia,'yyyy'),'yyyy') " +
            "                                     and to_date(to_char(coalesce(contrato.finalVigencia,:dataFinal),'yyyy'),'yyyy') " +
            " and to_date(to_char(:dataFinal,'yyyy'),'yyyy') between " +
            "                                     to_date(to_char(ho.inicioVigencia,'yyyy'),'yyyy') " +
            "                                     and to_date(to_char(coalesce(ho.fimVigencia,:dataFinal),'yyyy'),'yyyy') " +
            " and to_date(to_char(:dataFinal,'yyyy'),'yyyy') between " +
            "                                     to_date(to_char(lotacao.inicioVigencia,'yyyy'),'yyyy') " +
            "                                     and to_date(to_char(coalesce(lotacao.finalVigencia,:dataFinal),'yyyy'),'yyyy') ";

        Query q = em.createQuery(hql);

        q.setParameter("unidade", ho.getCodigoSemZerosFinais() + "%");
//        q.setParameter("dataFinal", dataFinal.getTime(), TemporalType.DATE);
//        q.setParameter("ano", dataFinal.get(Calendar.YEAR));

        return q.getResultList();
    }

    public List<ContratoFP> recuperarFGComContratoFPVigente(int ano, int mes) {
        String hql = " select contrato from FuncaoGratificada funcao " +
            " inner join funcao.contratoFP contrato " +
            " where (extract(year from coalesce(funcao.finalVigencia, :dataAtual)) >= :ano and " +
            " extract(month from coalesce(funcao.finalVigencia, :dataAtual)) >= :mes and " +
            " extract(YEAR FROM COALESCE(funcao.inicioVigencia, :dataAtual)) <= :ano AND " +
            " extract(MONTH FROM COALESCE(funcao.inicioVigencia, :dataAtual)) <= :mes) and " +
            " (extract (year from contrato.inicioVigencia) <= :ano and " +
            " extract(month from coalesce(contrato.finalVigencia, :dataAtual)) >= :mes and" +
            " extract (year from coalesce(contrato.finalVigencia, :dataAtual)) >= :ano and " +
            " extract(month from coalesce(contrato.inicioVigencia, :dataAtual)) <= :mes)";
        Query q = em.createQuery(hql);
        q.setParameter("dataAtual", new Date());
        q.setParameter("ano", ano);
        q.setParameter("mes", mes);
        return q.getResultList();
    }


    public SituacaoContratoFP recuperaSituacaoVigenteContratoFP(ContratoFP contratoFP, Date dataOperacao) {
        Query q = em.createQuery(" select situacaoContratoFP from SituacaoContratoFP situacaoContratoFP "
            + " where situacaoContratoFP.contratoFP = :contratoFP "
            + " and :dataVigencia >= situacaoContratoFP.inicioVigencia and "
            + " :dataVigencia <= coalesce(situacaoContratoFP.finalVigencia,:dataVigencia) ");
        q.setParameter("contratoFP", contratoFP);
        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataOperacao));
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (SituacaoContratoFP) q.getSingleResult();
        }
    }

//    public List<SituacaoContratoFP> recuperaSituacaoContratoFP(ContratoFP contratoFP, Date dataOperacao) {
//        Query q = em.createQuery(" select situacaoContratoFP from SituacaoContratoFP situacaoContratoFP "
//                + " where situacaoContratoFP.contratoFP = :contratoFP "
//                + " and :dataVigencia >= situacaoContratoFP.inicioVigencia and "
//                + " :dataVigencia <= coalesce(situacaoContratoFP.finalVigencia,:dataVigencia) ");
//        q.setParameter("contratoFP", contratoFP);
//        q.setParameter("dataVigencia", Util.getDataHoraMinutoSegundoZerado(dataOperacao));
//        if (q.getResultList().isEmpty()) {
//            return null;
//        } else {
//            return q.getResultList();
//        }
//    }

    public String recuperarDescricaoCarreiraPensionista(VinculoFP vinculoFP) {
        String sql = "  SELECT PLANO.DESCRICAO" +
            "        FROM VINCULOFP V" +
            "        inner join contratofp instituidor on instituidor.id= v.id" +
            "        inner join pensao p on p.contratofp_id = v.id" +
            "        inner join enquadramentofuncional enq on enq.contratoservidor_id = instituidor.id" +
            "        inner join categoriapcs cat on cat.id = enq.categoriapcs_id inner join progressaopcs prog on prog.id = enq.progressaopcs_id" +
            "        INNER JOIN PLANOCARGOSSALARIOS PLANO ON PLANO.ID = CAT.PLANOCARGOSSALARIOS_ID" +
            "        where instituidor.id = :pensionista and enq.iniciovigencia = (select max(en.iniciovigencia) from enquadramentofuncional en where en.contratoservidor_id = v.id)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("pensionista", vinculoFP.getId());
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

    public String recuperarDescricaoCarreiraContratoFP(ContratoFP contratoFP) {
        String sql = "   SELECT plano.DESCRICAO FROM  ENQUADRAMENTOFUNCIONAL ENQ " +
            "    inner join contratofp on enq.contratoservidor_id = contratofp.id " +
            "    inner join categoriapcs cat on cat.id = enq.categoriapcs_id inner join progressaopcs prog on prog.id = enq.progressaopcs_id " +
            "    INNER JOIN PLANOCARGOSSALARIOS PLANO ON PLANO.ID= CAT.PLANOCARGOSSALARIOS_ID " +
            "    WHERE ENQ.INICIOVIGENCIA = (SELECT MAX(EN.INICIOVIGENCIA) FROM ENQUADRAMENTOFUNCIONAL EN WHERE EN.CONTRATOSERVIDOR_ID = CONTRATOFP.ID) " +
            "    and contratofp.id = :contratoFP";
        Query q = em.createNativeQuery(sql);
        q.setParameter("contratoFP", contratoFP.getId());
        if (!q.getResultList().isEmpty()) {
            return (String) q.getResultList().get(0);
        }
        return "";
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<SiprevControlador.ContratoFPeItemFichaFinanceira> recuperarContratoFPComFichaFinanceira(int mes, int ano, List<String> codigoEvento) {
        String sql = " SELECT vinculo, item FROM ItemFichaFinanceiraFP item" +
            " JOIN item.fichaFinanceiraFP ficha " +
            " join ficha.vinculoFP vinculo " +
            " JOIN item.eventoFP evento" +
            " WHERE evento.codigo in :codigo and item.ano = :ano and item.mes = :mes and vinculo.class = ContratoFP";
        Query q = em.createQuery(sql);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("codigo", codigoEvento);
        return listaContratoFPItemFichaFinanceira(q.getResultList());
    }

    public List<SiprevControlador.ContratoFPeItemFichaFinanceira> listaContratoFPItemFichaFinanceira(List<Object[]> resultado) {
        List<SiprevControlador.ContratoFPeItemFichaFinanceira> retorno = new ArrayList<>();
        for (Object[] obj : resultado) {
            SiprevControlador.ContratoFPeItemFichaFinanceira contrato = new SiprevControlador.ContratoFPeItemFichaFinanceira();
            contrato.setContratoFP((ContratoFP) obj[0]);
            contrato.setItemFichaFinanceiraFP((ItemFichaFinanceiraFP) obj[1]);
            retorno.add(contrato);
        }
        return retorno;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<ContratoFP> recuperarContratoHistoricoFuncionalRPPs(List<Long> situacaoFuncional, Date dataOperacao) {
        String hql = "select distinct c from ContratoFP c, PrevidenciaVinculoFP previdencia" +
            " inner join c.situacaoFuncionals situacaoFuncional " +
            " inner join situacaoFuncional.situacaoFuncional situacao " +
            " where situacao.codigo in :codigo " +
            " and previdencia.contratoFP = c " +
            " and :dataOperacao BETWEEN previdencia.inicioVigencia AND COALESCE(previdencia.finalVigencia, :dataFinalVigencia) " +
            " and previdencia.tipoPrevidenciaFP.codigo = :codigoPrevidencia" +
            " AND :dataOperacao BETWEEN situacaoFuncional.inicioVigencia AND COALESCE(situacaoFuncional.finalVigencia, :dataFinalVigencia)";
        Query q = em.createQuery(hql);
        q.setParameter("codigo", situacaoFuncional);
        q.setParameter("dataOperacao", DataUtil.dataSemHorario(dataOperacao));
        q.setParameter("dataFinalVigencia", DataUtil.ultimoDiaMes(dataOperacao));
        q.setParameter("codigoPrevidencia", 3l);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<SituacaoContratoFP> recuperarSituacaoFuncionalDoContratoFP(ContratoFP contrato, Date dataOperacao) {
        String hql = "select situacao from SituacaoContratoFP situacao" +
            " where situacao.contratoFP = :contrato " +
            " and TO_DATE(TO_CHAR(:dataOperacao,'mm/yyyy'),'mm/yyyy') BETWEEN " +
            " TO_DATE(TO_CHAR(situacao.inicioVigencia,'mm/yyyy'),'mm/yyyy')  " +
            " and to_date(to_char(coalesce(situacao.finalVigencia, current_date),'mm/yyyy'),'mm/yyyy')";
        Query q = em.createQuery(hql);
        q.setParameter("contrato", contrato);
        q.setParameter("dataOperacao", DataUtil.dataSemHorario(dataOperacao));
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<String> listaMatriculasDoContratoVigentesPorPessoaFisicaNaData(PessoaFisica pessoaFisica, Date dataReferencia) {
        Query q = em.createQuery(" select matricula.matricula from ContratoFP contrato"
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " where pf = :pessoaFisica "
            + " and :data >= contrato.inicioVigencia "
            + " and :data <= coalesce(contrato.finalVigencia, :data)");
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(dataReferencia));
        q.setParameter("pessoaFisica", pessoaFisica);
        return q.getResultList();
    }

    public List<ContratoFP> listaContratosPermitidosParaUsuarioLogado(String s) {
        if (sistemaFacade.getUsuarioCorrente().getAcessoTodosVinculosRH()) {
            return listaContratosVigentesEm(s, sistemaFacade.getDataOperacao());
        } else {
            HierarquiaOrganizacional ho = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(), TipoHierarquiaOrganizacional.ADMINISTRATIVA);

            if (ho.getNivelNaEntidade() == 2) {
                return listaContratosDoOrgaoLogadoNaDataOperacao(s, ho);
            } else {
                return listaContratosComLotacaoFuncionalNaUnidade(s, ho.getSubordinada());
            }
        }
    }

    private List<ContratoFP> listaContratosVigentesEm(String s, Date dataOperacao) {
        String hql = "select new ContratoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome) from ContratoFP obj" +
            " where :dataOperacao between obj.inicioVigencia and coalesce(obj.finalVigencia,:dataOperacao) " +
            "   and ((lower(obj.matriculaFP.pessoa.nome) like :filtro) " +
            "    or (lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro) " +
            "    or (lower(obj.numero) like :filtro) " +
            "    or (lower(obj.matriculaFP.matricula) like :filtro)) " +
            "    order by to_number(obj.matriculaFP.matricula) asc, to_number(obj.numero) asc, obj.matriculaFP.pessoa.nome asc";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataOperacao", dataOperacao);
        return q.getResultList();
    }


    private List<ContratoFP> listaContratosDoOrgaoLogadoNaDataOperacao(String s, HierarquiaOrganizacional ho) {
        if (ho.getNivelNaEntidade() != 2) {
            return new ArrayList<>();
        }

        String hql = "select new ContratoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome) from ContratoFP obj" +
            " inner join obj.lotacaoFuncionals lotacao " +
            " inner join lotacao.unidadeOrganizacional uo" +
            " inner join uo.hierarquiasOrganizacionais ho" +
            "      where substr(ho.codigo,1,6) = :codigoOrgao" +
            "        and :dataOperacao between ho.inicioVigencia and coalesce(ho.fimVigencia,:dataOperacao)" +
            "        and :dataOperacao between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia,:dataOperacao)" +
            "        and :dataOperacao between obj.inicioVigencia and coalesce(obj.finalVigencia, :dataOperacao)" +
            "        and ho.tipoHierarquiaOrganizacional = :tipoHierarquia" +
            "        and ((lower(obj.matriculaFP.pessoa.nome) like :filtro) " +
            "         or (lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro) " +
            "         or (lower(obj.numero) like :filtro) " +
            "         or (lower(obj.matriculaFP.matricula) like :filtro)) " +
            "        order by to_number(obj.matriculaFP.matricula) asc, to_number(obj.numero) asc, obj.matriculaFP.pessoa.nome asc";

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("codigoOrgao", ho.getCodigo().substring(0, 6));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA);
        return q.getResultList();
    }


    private List<ContratoFP> listaContratosComLotacaoFuncionalNaUnidade(String s, UnidadeOrganizacional uo) {
        String orderBy = "";

        String hql = " select new ContratoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome) from ContratoFP obj" +
            " inner join obj.lotacaoFuncionals lotacao" +
            "      where lotacao.unidadeOrganizacional = :unidade" +
            "        and :dataOperacao between lotacao.inicioVigencia and coalesce(lotacao.finalVigencia,:dataOperacao) " +
            "        and ((lower(obj.matriculaFP.pessoa.nome) like :filtro) " +
            "         or (lower(obj.matriculaFP.unidadeMatriculado.descricao) like :filtro) " +
            "         or (lower(obj.numero) like :filtro) " +
            "         or (lower(obj.matriculaFP.matricula) like :filtro)) " +
            "         order by to_number(obj.matriculaFP.matricula) asc, to_number(obj.numero) asc, obj.matriculaFP.pessoa.nome asc";
        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        q.setParameter("unidade", uo);
        return q.getResultList();
    }

    public ContratoFP recuperarParaReintegracao(Long id) {
        ContratoFP c = em.find(ContratoFP.class, id);
        c.getEnquadramentos().size();
        c.getPrevidenciaVinculoFPs().size();
        c.getOpcaoValeTransporteFPs().size();
        c.getHorarioContratoFPs().size();
        c.getSindicatosVinculosFPs().size();
        c.getAssociacaosVinculosFPs().size();
        c.getSituacaoFuncionals().size();
        c.getLotacaoFuncionals().size();
        return c;
    }

    public ContratoFP recuperarParaReversao(Long id) {
        ContratoFP c = em.find(ContratoFP.class, id);
        c.getEnquadramentos().size();
        c.getModalidadeContratoFPDatas().size();
        c.getPrevidenciaVinculoFPs().size();
        c.getOpcaoValeTransporteFPs().size();
        c.getLotacaoFuncionals().size();
        c.getSindicatosVinculosFPs().size();
        c.getAssociacaosVinculosFPs().size();
        c.getContratoVinculoDeContratos().size();
        c.getRecursosDoVinculoFP().size();
        c.getCargos().size();
   //     c.getEventosEsocial().size();
        return c;
    }

    public ContratoFP recuperarPrevidencia(Long id) {
        ContratoFP c = em.find(ContratoFP.class, id);
        Hibernate.initialize(c.getPrevidenciaVinculoFPs());
        return c;
    }

    public ContratoFP recuperarParaTelaAlteracaoDeCargo(Long id) {
        ContratoFP c = em.find(ContratoFP.class, id);
        c.getCargos().size();
        c.getLotacaoFuncionals().size();
        c.getEnquadramentos().size();
        c.getRecursosDoVinculoFP().size();
        return c;
    }
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public ContratoFP recuperarParaArquivoAtuarial(Long id) {
        ContratoFP c = em.find(ContratoFP.class, id);
        Hibernate.initialize(c.getPrevidenciaVinculoFPs());
        Hibernate.initialize(c.getLotacaoFuncionals());
        Hibernate.initialize(c.getCargos());
        Hibernate.initialize(c.getMatriculaFP().getPessoa().getItemTempoContratoFPPessoa());
        return c;
    }


    public ContratoFP recuperarPessoaParaArquivoAtuarial(Long id) {
        ContratoFP c = em.find(ContratoFP.class, id);
        Hibernate.initialize(c.getMatriculaFP().getPessoa());
        return c;
    }

    public ContratoFP recuperarParaTelaEnquadramentoFuncional(Long id) {
        ContratoFP c = em.find(ContratoFP.class, id);
        c.getEnquadramentos().size();
        return c;
    }

    public Integer buscarQuantidadeDeServidoresNomeados(Date inicio) {
        Integer total = 0;
        String hql = "select count(contrato) from ContratoFP contrato where contrato.dataRegistro = :data ";
        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(inicio));
        if (q.getResultList().isEmpty()) {
            return total;
        }
        Long bg = (Long) q.getSingleResult();
        total = bg.intValue();
        return total;
    }

    public List<ContratoFP> buscarContratoFPVigente() {
        StringBuilder hql = new StringBuilder();
        hql.append(" select obj from ContratoFP obj");
        hql.append(" where obj.inicioVigencia <= :data");
        hql.append("   and coalesce(obj.finalVigencia, :data) >= :data");

        Query q = em.createQuery(hql.toString());
        q.setParameter("data", UtilRH.getDataOperacao());
        return q.getResultList();
    }

    public BigDecimal buscarCargaHorariaPorContrato(Long id) {
        String sql = " select cargo.cargaHoraria from ContratoFP c inner join Cargo cargo on cargo.id = c.cargo_id where c.id = :id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id", id);
        if (!q.getResultList().isEmpty()) {
            return (BigDecimal) q.getResultList().get(0);
        }
        return BigDecimal.ZERO;
    }

    public void salvarContratosEmLote(List<ContratoFP> contratoFPS) {
        for (ContratoFP contratoFP : contratoFPS) {
            salvarMatricula(contratoFP);
            em.persist(contratoFP);
            ProvimentoFP provimentoFP = criarProvimentoNomeacao(contratoFP);
            contratoFP.setProvimentoFP(provimentoFP);
            em.merge(contratoFP);
        }
    }

    private ProvimentoFP criarProvimentoNomeacao(ContratoFP contratoFP) {
        ProvimentoFP provimentoFP = new ProvimentoFP();
        provimentoFP.setVinculoFP(contratoFP);
        if (contratoFP.getModalidadeContratoFP().getCodigo() == ModalidadeContratoFP.CONTRATO_TEMPORARIO) {
            provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.NOMEACAO_CONTRATO_TEMPORARIO));
        } else {
            provimentoFP.setTipoProvimento(tipoProvimentoFacade.recuperaTipoProvimentoPorCodigo(TipoProvimento.NOMEACAO));
        }

        provimentoFP.setDataProvimento(contratoFP.getInicioVigencia());
        provimentoFP.setAtoLegal(contratoFP.getAtoLegal());
        provimentoFP.setSequencia(provimentoFPFacade.geraSequenciaPorVinculoFP(contratoFP));
        em.persist(provimentoFP);
        provimentoFP = (ProvimentoFP) provimentoFPFacade.recuperar(ProvimentoFP.class, provimentoFP.getId());
        return provimentoFP;
    }

    private void salvarMatricula(ContratoFP contratoFP) {
        if (contratoFP.getMatriculaFP().getId() == null) {
            matriculaFPFacade.salvarNovo(contratoFP.getMatriculaFP());
        }
    }

    public List<ContratoFP> buscarContratoPorTipoPeriodoAquisitivo(String filtro, TipoPeriodoAquisitivo tipoPeriodo) {
        String hql = "select distinct new ContratoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome ||' ('||obj.matriculaFP.pessoa.nomeTratamento||')') from ContratoFP obj " +
            " inner join obj.periodosAquisitivosFLs pa" +
            " inner join pa.baseCargo.basePeriodoAquisitivo bpa" +
            " where bpa.tipoPeriodoAquisitivo = :tipoPeriodo "
            + " and (lower(obj.matriculaFP.pessoa.nome) like :filtro " +
            "   or lower(obj.matriculaFP.matricula) like :filtro  "
            + " or lower(obj.matriculaFP.pessoa.cpf) like :filtro) "
            + " and TO_DATE(:data, 'dd/MM/yyyy') >= obj.inicioVigencia and "
            + " TO_DATE(:data, 'dd/MM/yyyy') <= coalesce(obj.finalVigencia,TO_DATE(:data, 'dd/MM/yyyy'))";
        Query q = em.createQuery(hql, ContratoFP.class);
        q.setParameter("data", DataUtil.getDataFormatada(UtilRH.getDataOperacao()));
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("tipoPeriodo", tipoPeriodo);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public ObjetoData getDataFromOracle(Integer totalDias) {
        Query q = em.createNativeQuery("select " +
            "           trunc((:dias/:parametro)) as anos, " +
            "           trunc((:dias/:parametro - trunc((:dias/:parametro))) * 12) as meses, " +
            "           round((((:dias/:parametro - trunc((:dias/:parametro))) * 12) - trunc((:dias/:parametro - trunc((:dias/:parametro))) * 12)) * 30) as dias " +
            " from dual");
        q.setParameter("dias", totalDias);
        q.setParameter("parametro", DIAS_ANOS);
        List resultList = q.getResultList();

        try {
            if (resultList != null && !resultList.isEmpty()) {
                return criarObjeto(resultList);
            }
        } catch (Exception e) {
            throw e;
        }
        return new ObjetoData();

    }


    public ObjetoData criarObjeto(List resultList) {
        ObjetoData objetoData = new ObjetoData();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            objetoData.setAnos(((BigDecimal) objeto[0]).intValue());
            objetoData.setMeses(((BigDecimal) objeto[1]).intValue());
            objetoData.setDias(((BigDecimal) objeto[2]).intValue());
        }
        return objetoData;
    }


    public List<ContratoFP> buscarContratoFPPorCapacitacao(String parte, Capacitacao capacitacao) {
        String hql = " select distinct new ContratoFP(obj.id, obj.matriculaFP.matricula||'/'||obj.numero||' - '||obj.matriculaFP.pessoa.nome) "
            + " from ContratoFP obj, InscricaoCapacitacao inscricao "
            + " inner join obj.matriculaFP matricula "
            + " inner join inscricao.capacitacao capacitacao "
            + " where (lower(obj.matriculaFP.pessoa.nome) "
            + " like :filtro OR lower(obj.matriculaFP.pessoa.cpf) "
            + " like :filtro OR matricula.matricula like :filtro) "
            + " and inscricao.matriculaFP.id = matricula.id "
            + " and :data >= obj.inicioVigencia and :data <= coalesce(obj.finalVigencia, :data) ";
        if (capacitacao != null) {
            hql += (" and capacitacao.id = :capacitacao");
        }
        Query q = em.createQuery(hql);
        q.setParameter("data", Util.getDataHoraMinutoSegundoZerado(sistemaFacade.getDataOperacao()));
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        if (capacitacao != null) {
            q.setParameter("capacitacao", capacitacao.getId());
        }
        return q.getResultList();
    }
    public BigDecimal buscarTempoServicoEmDias(ContratoFP contrato) {
        String sql = " select (coalesce(sum(tempoBruto), 0) - (coalesce(sum(injustificada), 0) + coalesce(sum(penalidades), 0))) as tempoServico from ( " +
            "  SELECT " +
            "    ( select sum(TOTALFALTASEFETIVAS) " +
            "      from VWFALTA " +
            "       where TIPOFALTA = :FALTA_INJUSTIFICADA " +
            "       and CONTRATOFP_ID = :CONTRATO_ID ) AS injustificada, " +
            "    (SELECT trunc(coalesce(SUM((pfp.finalvigencia - pfp.iniciovigencia) + 1), 0 )) AS dias " +
            "     FROM " +
            "       penalidadefp pfp " +
            "     WHERE " +
            "       pfp.contratofp_id = :CONTRATO_ID)                              AS penalidades, " +
            "    (SELECT trunc(sum((coalesce(vin.FINALVIGENCIA, sysdate) - vin.INICIOVIGENCIA) + 1)) " +
            "     FROM VINCULOFP vin " +
            "     where vin.ID = :CONTRATO_ID)                                           AS tempoBruto " +
            "  FROM " +
            "    contratofp contrato " +
            "    INNER JOIN vinculofp vinculo ON vinculo.id = contrato.id " +
            "    INNER JOIN matriculafp matricula ON vinculo.matriculafp_id = matricula.id " +
            "  where vinculo.id = :CONTRATO_ID " +
            ") diasTempoServico ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("CONTRATO_ID", contrato.getId());
        q.setParameter("FALTA_INJUSTIFICADA", TipoFalta.FALTA_INJUSTIFICADA.name());
        q.setMaxResults(1);

        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<ContratoFP> buscarContratoFpPorEmpregador(ConfiguracaoEmpregadorESocial empregadorESocial, Date dataOperacao) {
        List<ContratoFP> contratosFPs = Lists.newArrayList();
        String sql = " select contrato.id from vinculofp vinculo " +
            " inner join contratofp contrato on vinculo.id = contrato.id " +
            " where :dataAtual between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA, :dataAtual) " +
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataAtual", dataOperacao);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        if (!q.getResultList().isEmpty()) {
            for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
                contratosFPs.add(em.find(ContratoFP.class, id.longValue()));
            }
            return contratosFPs;
        }
        return null;
    }

    public ConfiguracaoEmpregadorESocial buscarEmpregadorPorVinculoFP(VinculoFP vinculoFP) {
        String sql = "  select empregador.* from vinculofp vinculo " +
            " inner join UNIDADEORGANIZACIONAL unidade on vinculo.UNIDADEORGANIZACIONAL_ID = unidade.id" +
            " inner join VWHIERARQUIAADMINISTRATIVA ho on ho.SUBORDINADA_ID = unidade.id " +
            " inner join ITEMEMPREGADORESOCIAL item on item.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID " +
            " inner join CONFIGEMPREGADORESOCIAL empregador on item.CONFIGEMPREGADORESOCIAL_ID = empregador.id" +
            " where vinculo.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID" +
            " and vinculo.id = :idContrato" +
            " and :dataOperacao between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA, :dataOperacao) ";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        q.setParameter("idContrato", vinculoFP.getId());
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        if (!q.getResultList().isEmpty()) {
            return (ConfiguracaoEmpregadorESocial) q.getResultList().get(0);
        }
        return null;
    }

    public ConfiguracaoEmpregadorESocial buscarEmpregadorPorPrestadorServicos(PrestadorServicos prestadorServicos) {
        String sql = "  select empregador.* from PRESTADORSERVICOS prestador " +
            " inner join UNIDADEORGANIZACIONAL unidade on prestador.LOTACAO_ID = unidade.id" +
            " inner join VWHIERARQUIAADMINISTRATIVA ho on ho.SUBORDINADA_ID = unidade.id " +
            " inner join ITEMEMPREGADORESOCIAL item on item.UNIDADEORGANIZACIONAL_ID = ho.SUBORDINADA_ID " +
            " inner join CONFIGEMPREGADORESOCIAL empregador on item.CONFIGEMPREGADORESOCIAL_ID = empregador.id" +
            " where prestador.LOTACAO_ID = ho.SUBORDINADA_ID" +
            " and prestador.id = :idPrestador " +
            " and :dataOperacao between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA, :dataOperacao) ";
        Query q = em.createNativeQuery(sql, ConfiguracaoEmpregadorESocial.class);
        q.setParameter("idPrestador", prestadorServicos.getId());
        q.setParameter("dataOperacao", sistemaFacade.getDataOperacao());
        if (!q.getResultList().isEmpty()) {
            return (ConfiguracaoEmpregadorESocial) q.getResultList().get(0);
        }
        return null;
    }

    public List<Long> montarIdOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<Long> ids = Lists.newArrayList();
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            ids.add(item.getUnidadeOrganizacional().getId());
        }
        return ids;
    }

    public void enviarS2190ESocial(ConfiguracaoEmpregadorESocial empregador, VWContratoFP vwContratoFP) throws ValidacaoException {
        ContratoFP contratoFP = recuperarSomenteContrato(vwContratoFP.getId());
        PrestadorServicos prestadorServicos = prestadorServicosFacade
            .buscarPrestadorServicosPorIdPessoa(
                Optional.of(contratoFP)
                    .map(ContratoFP::getMatriculaFP)
                    .map(MatriculaFP::getPessoa)
                    .map(Pessoa::getId)
                    .orElse(null)
            );
        s2190Service.enviarS2190(empregador, contratoFP, prestadorServicos);
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 3)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void enviarS2200ESocial(ConfiguracaoEmpregadorESocial empregador, VWContratoFP contratoFPDto, Date dataOperacao, AssistenteBarraProgresso assistenteBarraProgresso) throws ValidacaoException {
        TipoOperacaoESocial tipoOperacaoESocial = contratoFPDto.getTipoOperacaoESocial();
        ContratoFP contratoFP = recuperarSomenteContrato(contratoFPDto.getId());
        contratoFP = recuperarPrevidencia(contratoFP.getId());
        contratoFP.setTipoOperacaoESocial(tipoOperacaoESocial);
        s2200Service.enviarS2200(empregador, contratoFP, dataOperacao, assistenteBarraProgresso);
    }

    public void enviarS2205ESocial(ConfiguracaoEmpregadorESocial empregador, ContratoFP contratoFP) throws ValidacaoException {
        s2205Service.enviarS2205(empregador, contratoFP);
    }

    public void enviarS2206ESocial(ConfiguracaoEmpregadorESocial empregador, ContratoFP contratoFP, Date dataOperacao, Date dataAlteracaoContrato) throws ValidacaoException {
        contratoFP = recuperarPrevidencia(contratoFP.getId());
        s2206Service.enviarS2206(empregador, contratoFP, dataOperacao, dataAlteracaoContrato);
    }

    public List<VinculoFP> buscarVinculosPorItemDPContasSemCedenciaOuAfastamento(ItemEntidadeDPContas itemEntidadeDPContas, Boolean isRescisao, Mes mes, Integer ano) {
        List<VinculoFP> vunculoFP = Lists.newArrayList();
        String sql = " select contrato.id from vinculofp vinculo " +
            " inner join contratofp contrato on vinculo.id = contrato.id " +
            " where  :dataAtual between vinculo.INICIOVIGENCIA and coalesce(vinculo.FINALVIGENCIA,  :dataAtual) " +
            "   and not exists ( " +
            "    select 1 " +
            "    from CedenciaContratoFP cedencia " +
            "    where cedencia.contratofp_id = contrato.id " +
            "      and cedencia.tipoContratoCedenciaFP = :parametroTipoCedencia " +
            "      and ((:dataInicial >= cedencia.dataCessao  " +
            "           and not exists (  " +
            "                select 1 " +
            "                from RetornoCedenciaContratoFP r " +
            "                where r.cedenciacontratofp_id = cedencia.id) " +
            "                or exists (  " +
            " select 1 from Afastamento a  " +
            "  join tipoafastamento t on t.id = a.tipoafastamento_id " +
            " where a.contratofp_id = vinculo.id " +
            "  and t.naoCalcularFichaSemRetorno = 1 " +
            "  and (a.retornoInformado = 0 or a.retornoInformado is null) " +
            "  and (  " +
            "    (a.inicio <= :dataInicial and (a.termino is null or a.termino >= :dataFinal)) " +
            "    or (  " +
            "      a.inicio = (  " +
            "        select min(a2.inicio) from Afastamento a2  " +
            "          join tipoafastamento t2 on t2.id = a2.tipoafastamento_id " +
            "        where a2.CONTRATOFP_ID = vinculo.id " +
            "          and t2.naoCalcularFichaSemRetorno = 1 " +
            "          and (a2.retornoInformado = 0 or a2.retornoInformado is null) " +
            "            and a2.termino <= :dataInicial  " +
            "        )  " +
            "      ) or ( a.inicio <= :dataInicial and (a.termino + 1 ) >= cedencia.dataCessao and :dataFinal <= (select Max(r.dataRetorno) " +
            " from RetornoCedenciaContratoFP r where r.cedenciacontratofp_id = cedencia.id )) " +
            " or (:dataInicial >= cedencia.dataCessao " +
            "          and a.termino >= ( " +
            "              select max(r.dataRetorno) " +
            "              from RetornoCedenciaContratoFP r " +
            "              where r.cedenciacontratofp_id = cedencia.ID " +
            "          ) and a.termino >= :dataFinal and a.termino < cedencia.dataCessao ) " +
            "        or (a.inicio < cedencia.dataCessao and  (a.inicio <= :dataInicial and a.termino >= :dataFinal)) " +
            "         or (a.termino >= ( " +
            "              select max(r.dataRetorno) " +
            "              from RetornoCedenciaContratoFP r " +
            "              where r.cedenciacontratofp_id = cedencia.ID " +
            "                ) and a.termino >= :dataFinal and a.inicio > cedencia.dataCessao)" +
            "      ) " +
            "   ))  or (:dataInicial >= cedencia.dataCessao and :dataFinal <= ( " +
            "                select Max(r.dataRetorno) " +
            "                from RetornoCedenciaContratoFP r " +
            "                where r.cedenciacontratofp_id = cedencia.ID " +
            "            )) " +
            "      ))"+
            " and vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades)";
        if (isRescisao) {
            sql += "and vinculo.id in(select ex.vinculoFP_id from ExoneracaoRescisao ex ) ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataAtual", sistemaFacade.getDataOperacao());
        q.setParameter("unidades", montarIdOrgaoItemEntidadeDPContas(itemEntidadeDPContas));
        q.setParameter("parametroTipoCedencia", TipoCedenciaContratoFP.SEM_ONUS.name());
        q.setParameter("dataInicial", Util.getDataHoraMinutoSegundoZerado(Util.criaDataPrimeiroDiaMesJoda(mes.getNumeroMes(), ano).toDate()));
        q.setParameter("dataFinal", Util.getDataHoraMinutoSegundoZerado(DataUtil.criarDataUltimoDiaMes(mes.getNumeroMes(), ano).toDate()));
        if (!q.getResultList().isEmpty()) {
            for (BigDecimal id : (List<BigDecimal>) q.getResultList()) {
                vunculoFP.add(em.find(VinculoFP.class, id.longValue()));
            }
            return vunculoFP;
        }
        return null;
    }

    private List<Long> montarIdOrgaoItemEntidadeDPContas(ItemEntidadeDPContas itemEntidadeDPContas) {
        itemEntidadeDPContas = em.find(ItemEntidadeDPContas.class, itemEntidadeDPContas.getId());
        itemEntidadeDPContas.getItemEntidadeUnidadeOrganizacional().size();
        List<Long> ids = Lists.newArrayList();
        for (ItemEntidadeDPContasUnidadeOrganizacional item : itemEntidadeDPContas.getItemEntidadeUnidadeOrganizacional()) {
            ids.add(item.getHierarquiaOrganizacional().getSubordinada().getId());
        }
        return ids;
    }

    public List<EventoESocialDTO> getEventoPorIdentificador(Long id, String cpf, List<Long> fichasId, List<Long> exoneracoesId, List<Long> afastamentosId, List<Long> cedenciasId) {
        try {
            BuscarEventoDTO dto = new BuscarEventoDTO();
            dto.setIdEsocial(id.toString());
            dto.setCpf(cpf);
            dto.setFichasId(fichasId);
            dto.setExoneracoesId(exoneracoesId);
            dto.setAfastamentosId(afastamentosId);
            dto.setCedenciasId(cedenciasId);
            return eSocialService.getEventosPorIdEsocialOrCPF(dto);
        } catch (Exception e) {
            logger.debug("Não foi possível buscar o histórico do esocial para o contrato");
            return null;
        }
    }

    public boolean isContratoFpPorModalidade(Long idPessoa, Long codigoModalidade) {
        String sql = " select vin.* " +
            "         from vinculofp vin " +
            "            inner join contratofp contrato on vin.id = contrato.id " +
            "            inner join matriculafp mat on vin.matriculafp_id = mat.id " +
            "            inner join pessoafisica pf on mat.pessoa_id = pf.id " +
            "            inner join modalidadecontratofp modal on modal.id = contrato.modalidadecontratofp_id" +
            "         where pf.id = :idPessoa " +
            "         and modal.codigo = :codigoModalidade ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigoModalidade", codigoModalidade);
        q.setParameter("idPessoa", idPessoa);
        return (!q.getResultList().isEmpty());
    }

    public String buscarIdVinculoFPPorPessoaFisica(PessoaFisica pessoa, Date dataReferencia) {
        String hql = "  select v.id from VinculoFP v, ContratoFP c"
            + " inner join v.fichasFinanceiraFP ffp "
            + " inner join ffp.folhaDePagamento fp  "
            + " inner join v.matriculaFP m          "
            + " inner join m.pessoa pf              "
            + " inner join ffp.itemFichaFinanceiraFP item "
            + "      where fp.mes = :mes            "
            + "        and fp.ano = :ano            "
            + "        and c.id = v.id              "
            + "        and pf.id = :pessoa          ";
        Query q = em.createQuery(hql);
        DateTime date = new DateTime(dataReferencia);
        q.setParameter("pessoa", pessoa.getId());
        q.setParameter("mes", Mes.getMesToInt(date.getMonthOfYear()));
        q.setParameter("ano", date.getYear());
        try {
            return q.getResultList().get(0).toString();
        } catch (Exception e) {
            return null;
        }
    }

    public ContratoFP buscarContratoPorMatriculaAndContrato(String matricula, String numero) {
        String hql = (" select contrato from ContratoFP contrato " +
            " inner join contrato.matriculaFP matricula"
            + " where matricula.matricula = :matricula and contrato.numero = :numero");
        Query q = em.createQuery(hql);
        q.setParameter("numero", numero);
        q.setParameter("matricula", matricula);
        List<ContratoFP> contratos = q.getResultList();
        if (!contratos.isEmpty()) {
            return contratos.get(0);
        }
        return null;
    }

    public Date dataIncicioPrimeiroContratoPorMatriculaAndContrato(String matricula, String numero) {
        String hql = (" select contrato.inicioVigencia from ContratoFP contrato " +
            " inner join contrato.matriculaFP matricula"
            + " where matricula.matricula = :matricula and contrato.numero = :numero");
        Query q = em.createQuery(hql);
        q.setParameter("numero", numero);
        q.setParameter("matricula", matricula);
        List<Date> contratos = q.getResultList();
        if (!contratos.isEmpty()) {
            return contratos.get(0);
        }
        return null;
    }

    public List<ContratoFP> recuperarContratoDiferentePrefeitoAndSecretario(String s) {
        String hql = "select distinct obj from ContratoFP obj "
            + " join obj.matriculaFP matricula "
            + " join matricula.pessoa pes "
            + " where ((lower(pes.nome) like :filtro) OR (lower(pes.cpf) like :filtro) OR (lower(matricula.matricula) like :filtro) ) "
            + " and obj.modalidadeContratoFP.codigoVinculoSicap <> :codigo ";

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q.setParameter("filtro", "%" + s.toLowerCase() + "%");
        q.setParameter("codigo", PREFEITO_SECRETARIO);

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<VinculoFPEventoEsocial> buscarVinculosFPEventoEsocialPorVinculo(VinculoFP vinculoFP) {
        String sql = " select vinc.* from VinculoFPEventoEsocial vinc " +
            " where vinc.VINCULOFP_ID = :vinculo ";
        Query q = em.createNativeQuery(sql, VinculoFPEventoEsocial.class);
        q.setParameter("vinculo", vinculoFP.getId());
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<VinculoFP> buscarContratoPorNomeOrUnidadeOrNumeroOrMatriculaFP(String parte) {
        String sql = " SELECT v.id, M.MATRICULA " +
            " ||'/' " +
            " ||V.NUMERO " +
            " ||' - ' " +
            " ||PF.NOME " +
            " ||' ' " +
            " || " +
            " CASE WHEN V.ID = A.ID THEN ' - APOSENTADO' ELSE '' END " +
            " ||' ' " +
            " || " +
            " CASE WHEN V.ID = P.ID THEN ' - PENSIONISTA' ELSE '' END " +
            " FROM VINCULOFP V " +
            " INNER JOIN MATRICULAFP M " +
            " ON M.id = V.MATRICULAFP_ID " +
            " INNER JOIN PESSOAFISICA PF " +
            " ON PF.id = M.PESSOA_ID " +
            " INNER JOIN UNIDADEORGANIZACIONAL UO " +
            " ON UO.ID = M.UNIDADEMATRICULADO_ID " +
            " LEFT JOIN APOSENTADORIA A ON A.ID = V.ID " +
            " LEFT JOIN PENSIONISTA P ON P.ID = V.ID " +
            "WHERE ((lower(pf.nome) like :filtro) " +
            "or (lower(UO.DESCRICAO) like :filtro) " +
            "or (lower(V.NUMERO) LIKE :filtro) " +
            "or (lower(m.matricula) like :filtro)) " +
            "order by TO_NUMBER(M.MATRICULA)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        List resultList = q.getResultList();
        List<VinculoFP> retorno = Lists.newArrayList();
        for (Object o : resultList) {
            Object[] objeto = (Object[]) o;
            Number idVinculo = (Number) objeto[0];
            String descricaoVinculo = (String) objeto[1];
            VinculoFP vinculoFP = new VinculoFP(idVinculo.longValue(), descricaoVinculo);
            retorno.add(vinculoFP);
        }
        return retorno;
    }

    public ContratoFP buscarContratoFPPorPessoa(Long idPessoa, Date dataReferencia) {
        String sql = "select obj from ContratoFP obj " +
            "        inner join obj.matriculaFP mat " +
            "where mat.pessoa.id = :idPessoa and :dataReferencia between obj.inicioVigencia and coalesce(obj.finalVigencia, :dataReferencia)";
        Query q = em.createQuery(sql);
        q.setParameter("idPessoa", idPessoa);
        q.setParameter("dataReferencia", dataReferencia);
        List result = q.getResultList();
        if (result != null && !result.isEmpty()) {
            return (ContratoFP) result.get(0);
        }
        return null;
    }
}
