/*
 * Codigo gerado automaticamente em Tue Feb 22 10:54:59 BRT 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.AtoLegalPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.OrgaoAtoLegalVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.AssistenteBarraProgressoImportarAto;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.ExcelUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.ws.model.WSArquivo;
import br.com.webpublico.ws.model.WSAtoLegal;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Future;

@Stateless
public class AtoLegalFacade extends AbstractFacade<AtoLegal> {

    private static final Logger logger = LoggerFactory.getLogger(AtoLegalFacade.class);

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private UnidadeExternaFacade unidadeExternaFacade;
    @EJB
    private EsferaGovernoFacade esferaGovernoFacade;
    @EJB
    private AtoDeComissaoFacade atoDeComissaoFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private ComissaoFacade comissaoFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;

    public AtoLegalFacade() {
        super(AtoLegal.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AtoLegal recuperar(Object id) {
        AtoLegal atoLegal = super.recuperar(id);
        if (atoLegal.temAtoDeComissao()) {
            atoDeComissaoFacade.recuperar(atoLegal.getAtoDeComissao().getId());
        }
        atoLegal.getRepublicacoes().size();
        return atoLegal;
    }

    @Override
    public void preSave(AtoLegal entidade) {
        ValidacaoException ve = new ValidacaoException();
        if (hasAtoComNumeroTipoExercicioEUnidadeJaCadastrado(entidade)) {
            String unidade = null;
            if (!entidade.getTipoAtoLegal().isLeisOuDecreto()) {
                if (entidade.getUnidadeExterna() != null) {
                    unidade = "<b>Unidade Externa (" + entidade.getUnidadeExterna().toString() + ")</b>, ";
                }
                if (entidade.getUnidadeOrganizacional() != null) {
                    unidade = "<b>Unidade Organizacional (" + hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), entidade.getUnidadeOrganizacional(), TipoHierarquiaOrganizacional.ADMINISTRATIVA).toString() + ")</b>, ";
                }
            }
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um ato legal com o " +
                "<b>Número (" + entidade.getNumero() + ")</b>, " +
                (unidade != null ? unidade : "") +
                "<b>Tipo (" + entidade.getTipoAtoLegal().getDescricao() + ")</b> e " +
                "<b>Exercício (" + entidade.getExercicio().getAno() + ")</b> cadastrado.");
        }
        ve.lancarException();
    }

    public void salvarArquivo(FileUploadEvent fileUploadEvent, Arquivo arq) {
        try {
            UploadedFile arquivoRecebido = fileUploadEvent.getFile();
            arq.setNome(arquivoRecebido.getFileName());
            arq.setMimeType(arquivoRecebido.getContentType()); //No prime 2 não funciona
            arq.setTamanho(arquivoRecebido.getSize());
            arquivoFacade.novoArquivo(arq, arquivoRecebido.getInputstream());
            arquivoRecebido.getInputstream().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public boolean verificaSeExisteArquivo(Arquivo arq) {
        Arquivo ar = null;
        if (arq == null) {
            return false;
        }
        try {
            ar = arquivoFacade.recupera(arq.getId());
        } catch (Exception e) {
            return false;
        }
        if (arq.equals(ar)) {
            return true;
        }
        return false;
    }

    public List<AtoLegal> listaFiltrandoParteEPropositoAtoLegal(String parte, PropositoAtoLegal propositoAtoLegal, String... atributos) {
        String sql = " SELECT ato.* FROM AtoLegal ato WHERE ato.propositoAtoLegal = :parametro AND rownum < 11 AND (";
        for (String atributo : atributos) {
            sql += "lower(ato." + atributo + " ) like :parte OR ";
        }
        sql = sql.substring(0, sql.length() - 3) + ") ";

        Query q = em.createNativeQuery(sql, AtoLegal.class);
        q.setParameter("parametro", propositoAtoLegal.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public List<AtoLegal> listaFiltrando(String parte) {
        String sql = " SELECT ato.id, ato.nome, ato.numero FROM AtoLegal ato " +
            " WHERE (lower(trim(ato.nome)) like :parte or lower(trim(ato.numero)) like :parte) and rownum <= 10";
        Query q = em.createNativeQuery(sql);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        List<AtoLegal> retorno = Lists.newArrayList();
        for (Object[] o : (List<Object[]>) q.getResultList()) {
            retorno.add(new AtoLegal(((Number) o[0]).longValue(), (String) o[1], (String) o[2]));
        }
        return retorno;
    }

    public AtoLegalORC recuperaAtolegaOrc(AtoLegal ato) {
        String hql = "from AtoLegalORC a  where a.atoLegal=:param";
//        logger.debug(hql.replace(":param", ato.getId() + ""));
        if (ato.getId() != null) {
            Query q = getEntityManager().createQuery(hql);
            q.setParameter("param", ato);
            if (!q.getResultList().isEmpty()) {
                return (AtoLegalORC) q.getSingleResult();
            }
            return new AtoLegalORC();
        } else {
            return new AtoLegalORC();
        }

    }

    public AtoLegal findAtoByAnoAndTipoAndNumero(String numero, Integer ano, TipoAtoLegal tipo) {
        Query q = em.createQuery("select ato from AtoLegal ato where ato.tipoAtoLegal = :tipo " +
            " and ato.numero = :numero and ato.exercicio.ano = :ano ");
        q.setParameter("ano", ano);
        q.setParameter("tipo", tipo);
        q.setParameter("numero", numero);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (AtoLegal) q.getResultList().get(0);


    }

    public AtoLegal findAtoByAnoAndTipoAndNumeroAndUnidadeOrganizacional(String numero, Integer ano, TipoAtoLegal tipo, UnidadeOrganizacional un) {
        Query q = em.createQuery("select ato from AtoLegal ato where ato.tipoAtoLegal = :tipo " +
            " and ato.numero = :numero and ato.exercicio.ano = :ano and ato.unidadeOrganizacional = :un");
        q.setParameter("ano", ano);
        q.setParameter("tipo", tipo);
        q.setParameter("numero", numero);
        q.setParameter("un", un);
        if (q.getResultList().isEmpty()) {
            return null;
        }
        return (AtoLegal) q.getResultList().get(0);


    }

    /*
     * Lista Todos os AtoLegalOrc
     *
     */

    public List<AtoLegalORC> listaAtolegaOrc() {
        String hql = "from AtoLegalORC a order by a.id ";
        return getEntityManager().createQuery(hql).getResultList();
    }

    /*
     * LIsta todos os atos legais que ainda não tenha
     */
    public List<AtoLegal> listaFiltrandoAtoLegalTipoDecreto(String parte, Exercicio ex) {
//        String sql = "SELECT a.* FROM AtoLegal a "
//                + "WHERE a.tipoAtoLegal=:tipoAto "
//                + "AND a.propositoAtoLegal=:proposta "
//                + "AND ((lower(a.nome) LIKE :param) OR ((a.numero) LIKE :param)) "
//                + "AND a.exercicio_id = :ex "
//                + "AND a.id NOT IN "
//                + "(SELECT ao.atolegal_id FROM alteracaoorc ao)";
        String sql = "select aa.* from AtoLegal aa "
            + " where aa.id not in (SELECT a.id FROM AlteracaoORC ao "
            + "                     inner join AtoLegal a on  a.id = ao.atolegal_id) "
            + " AND aa.exercicio_id = :ex "
            + " and aa.tipoAtoLegal = :tipoAto "
            + " AND aa.propositoAtoLegal = :proposta "
            + " AND ((lower(aa.nome) LIKE :param)OR ((aa.numero) LIKE :param)) ";

        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
        q.setParameter("param", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipoAto", TipoAtoLegal.DECRETO.toString());
        q.setParameter("proposta", PropositoAtoLegal.ALTERACAO_ORCAMENTARIA.toString());
        q.setParameter("ex", ex.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<AtoLegal> buscarAtoLegalTipoDecretoResolucaoPorExercicio(String parte, Exercicio ex, EsferaDoPoder esferaDoPoder, Boolean verificarEstorno) {
        String complemento = "";
        if (verificarEstorno) {
            complemento += "     where aa.id not in (SELECT a.id FROM AlteracaoORC ao "
                + "                         inner join AtoLegal a on  a.id = ao.atolegal_id"
                + "                         where ao.status <> '" + StatusAlteracaoORC.ESTORNADA.name() + "'"
                + "                        ) and ";
        } else {
            complemento = " where  ";
        }
        String sql = " select distinct aa.* from AtoLegal aa "
            + "      left join alteracaoorc alt on alt.atolegal_id = aa.id"
            + complemento
            + "     aa.exercicio_id = :ex ";
        if (esferaDoPoder.isExecutivo()) {
            sql += "     and aa.tipoAtoLegal in (:tipoDecreto, :tipoMedidaProvisoria)  ";
        } else if (esferaDoPoder.isLegislativo()) {
            sql += "     and aa.tipoAtoLegal = :tipoAto ";
        }
        sql += "     and aa.propositoAtoLegal = :proposta "
            + "     and ((lower(aa.nome) LIKE :param)OR ((aa.numero) LIKE :param)) ";
        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
        q.setParameter("param", "%" + parte.toLowerCase() + "%");
        if (esferaDoPoder.isExecutivo()) {
            q.setParameter("tipoDecreto", TipoAtoLegal.DECRETO.name());
            q.setParameter("tipoMedidaProvisoria", TipoAtoLegal.MEDIDA_PROVISORIA.name());
        }
        if (esferaDoPoder.isLegislativo()) {
            q.setParameter("tipoAto", TipoAtoLegal.RESOLUCAO.name());
        }
        q.setParameter("proposta", PropositoAtoLegal.ALTERACAO_ORCAMENTARIA.name());
        q.setParameter("ex", ex.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }


    public List<AtoLegal> buscarAtosLegaisPorExercicio(String parte, Exercicio exercicio, List<TipoAtoLegal> tiposAtos) {
        String sql = " select distinct aa.* from AtoLegal aa "
            + "      left join alteracaoorc alt on alt.atolegal_id = aa.id"
            + "     where aa.id not in (SELECT a.id FROM AlteracaoORC ao "
            + "                         inner join AtoLegal a on  a.id = ao.atolegal_id"
            + "                         where ao.status <> '" + StatusAlteracaoORC.ESTORNADA.name() + "'"
            + "                        ) "
            + "     and aa.exercicio_id = :ex "
            + "     and aa.tipoAtoLegal in (" + montarClausulaIn(tiposAtos) + ") "
            + "     and aa.propositoAtoLegal = :proposta "
            + "     and ((lower(aa.nome) LIKE :param) OR ((aa.numero) LIKE :param)) ";
        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
        q.setParameter("param", "%" + parte.toLowerCase() + "%");
        q.setParameter("proposta", PropositoAtoLegal.ALTERACAO_ORCAMENTARIA.name());
        q.setParameter("ex", exercicio.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    private String montarClausulaIn(List<TipoAtoLegal> tiposAtos) {
        StringBuilder clausulaIn = new StringBuilder();
        String juncao = "";
        for (TipoAtoLegal tipoAto : tiposAtos) {
            clausulaIn.append(juncao).append("'").append(tipoAto.name()).append("'");
            juncao = ", ";
        }
        return clausulaIn.toString();
    }

    public List<AtoLegal> listaFiltrandoAtoLegalPropositoAtoLegal(String parte) {
        String hql = "from AtoLegal a where a.propositoAtoLegal=:prop and upper(a.nome) like :param ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("param", "%" + parte.toUpperCase() + "%");
        q.setParameter("prop", PropositoAtoLegal.ALTERACAO_ORCAMENTARIA);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<AtoLegal> listaFiltrandoAtoLegalPorPropositoAtoLegal(String parte, PropositoAtoLegal pro) {
        String hql = "from AtoLegal a " +
            "      where a.propositoAtoLegal = :prop " +
            "      and (upper(a.nome) like :param or a.numero like :param ) order by dataPublicacao desc";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("param", "%" + parte.toUpperCase() + "%");
        q.setParameter("prop", pro);
        q.setMaxResults(10);

        return q.getResultList();
    }

    public List<AtoLegal> buscarAtosLegaisPorPropositoAtoLegal(String parte, PropositoAtoLegal pro) {
        String hql = "select a.* from AtoLegal a " +
            "      where a.propositoAtoLegal = :prop " +
            "      and (upper(a.nome) like :param or a.numero like :param ) ";
        Query q = getEntityManager().createNativeQuery(hql, AtoLegal.class);
        q.setParameter("param", "%" + parte.toUpperCase() + "%");
        q.setParameter("prop", pro.name());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<AtoLegal> listaFiltrandoAtoLegalPorPropositoAtoLegalSQL(String parte, PropositoAtoLegal pro) {
        String hql = "SELECT ato.* FROM atolegal ato WHERE ato.propositoatolegal = :prop AND lower(ato.nome) LIKE :param AND rownum <= 11";
        Query q = getEntityManager().createNativeQuery(hql, AtoLegal.class);
        q.setParameter("param", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("prop", pro.name());
        return q.getResultList();
    }

    public List<AtoLegal> listaFiltrandoAtoLegalPropositoAtoLegal() {
        String hql = "from AtoLegal a where a.propositoAtoLegal=:prop";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("prop", PropositoAtoLegal.ALTERACAO_ORCAMENTARIA);
        return q.getResultList();
    }

    public List<AtoLegal> listaFiltrandoAtoLegalAtoLegalDecreto() {
        String hql = "from AtoLegal a where ((a.propositoAtoLegal=:prop) or(a.propositoAtoLegal=:propOrc))  and ((a.tipoAtoLegal=:tipoAto) or (a.tipoAtoLegal=:tipoAto2)) ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("prop", PropositoAtoLegal.ALTERACAO_ORCAMENTARIA);
        q.setParameter("propOrc", PropositoAtoLegal.ORCAMENTO);
        q.setParameter("tipoAto2", TipoAtoLegal.PROJETO_DE_LEI);
        q.setParameter("tipoAto", TipoAtoLegal.LEI_ORDINARIA);
        return q.getResultList();
    }

    @Override
    public List<AtoLegal> lista() {
        String sql = "SELECT * FROM ATOLEGAL ORDER BY id DESC";
        Query q = em.createNativeQuery(sql, AtoLegal.class);
        return q.getResultList();
    }

    /*
     * Lista os Atos legais que contenham decretos
     */
    public List<AtoLegal> listaAtoLegaComDecretos(String parte) {
        String hql = "select pai from AtoLegal pai, AtoLegal filho where upper(pai.nome)=:param "
            + " and pai= filho.superior";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("param", "%" + parte.toUpperCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    /*
     * Lista os Atos legais que que são filhos
     */
    public List<AtoLegal> listaAtoLegaFilhos(AtoLegal ato) {
        String hql = "select filho from AtoLegal pai, AtoLegal filho where pai=:param and pai= filho.superior ";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("param", ato);
        return q.getResultList();
    }

    @Override
    public void remover(AtoLegal entity) {
        AtoLegalORC orc = recuperaAtolegaOrc(entity);
        try {
            getEntityManager().remove(orc);
        } catch (Exception e) {
            e.getMessage();
        }
        super.remover(entity);
    }

    public AtoDeComissao recuperaAtoDeComissao(AtoLegal ato) {
        String hql = "from " + AtoDeComissao.class.getSimpleName() + " a  where a.atoLegal=:param";
        if (ato.getId() != null) {
            Query q = getEntityManager().createQuery(hql);
            q.setParameter("param", ato);
            q.setMaxResults(1);

            try {
                return (AtoDeComissao) q.getSingleResult();
            } catch (NoResultException ex) {
                return new AtoDeComissao();
            }
        } else {
            return new AtoDeComissao();
        }
    }

    public boolean existeNumeroDoAtoLegalRepetido(AtoLegal atoLegal) {
        String sql = "select a.* from atolegal a  " +
            " where a.numero = :numero " +
            " and a.tipoAtoLegal = :tipo " +
            " and a.propositoAtoLegal = :proposito " +
            " and a.exercicio_id = :ex";
        if (atoLegal.getUnidadeOrganizacional() != null) {
            sql += " and a.unidadeOrganizacional_id = :unid ";
        }
        if (atoLegal.getUnidadeExterna() != null) {
            sql += " and a.unidadeExterna_id = :unidExterna ";
        }
        if (atoLegal.getEsferaGoverno() != null) {
            sql += " and a.esferaGoverno_id = :esferaGoverno ";
        }

        if (atoLegal.getId() != null) {
            sql += " and a.id <> :id";
        }
        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);

        if (atoLegal.getId() != null) {
            q.setParameter("id", atoLegal.getId());
        }

        q.setParameter("numero", atoLegal.getNumero());
        q.setParameter("proposito", atoLegal.getPropositoAtoLegal().name());
        q.setParameter("tipo", atoLegal.getTipoAtoLegal().name());
        q.setParameter("ex", atoLegal.getExercicio().getId());

        if (atoLegal.getUnidadeOrganizacional() != null) {
            q.setParameter("unid", atoLegal.getUnidadeOrganizacional().getId());
        }
        if (atoLegal.getUnidadeExterna() != null) {
            q.setParameter("unidExterna", atoLegal.getUnidadeExterna().getId());
        }
        if (atoLegal.getEsferaGoverno() != null) {
            q.setParameter("esferaGoverno", atoLegal.getEsferaGoverno().getId());
        }
        q.setMaxResults(1);
        try {
            Object singleResult = q.getSingleResult();
            return singleResult == null ? false : true;
        } catch (NoResultException ex) {
            return false;
        }
    }

    public List<AtoLegal> listaFiltrandoAtoDePessoal(String parte) {
        String hql = " from AtoLegal al where (al.propositoAtoLegal = :prop or al.propositoAtoLegal = :prop2) " +
            " and (lower(al.nome) like :filtro"
            + "        or lower(al.numero) like :filtro"
            + "        or lower(al.tipoAtoLegal) like :filtro" +
            "          or to_char(al.dataPublicacao, 'DD/MM/YYYY') like :filtro" +
            "          or to_char(al.dataEnvio, 'dd/mm/yyyy') like :filtro" +
            "          or to_char(al.dataEmissao, 'dd/mm/yyyy') like :filtro " +
            "          or lower(al.descricaoConformeDo) like :filtro) ";
        Query q = getEntityManager().createQuery(hql);
        q.setMaxResults(15);
        q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("prop", PropositoAtoLegal.ATO_DE_PESSOAL);
        q.setParameter("prop2", PropositoAtoLegal.BENEFICIO_PREVIDENCIARIO);
        return q.getResultList();
    }

    /**
     * Busca nativa para ato legal
     *
     * @param s
     * @return
     */
    public List<AtoLegal> listaFiltrandoAtoLegal(String s) {

        String hql = " select al from AtoLegal al"
            + "     where (al.propositoAtoLegal = :tipo or al.propositoAtoLegal = :tipo1)"
            + "       and (lower(al.nome) like :filtro"
            + "        or lower(al.numero) like :filtro"
            + "        or lower(al.tipoAtoLegal) like :filtro" +
            "          or to_char(al.dataPublicacao, 'DD/MM/YYYY') like :filtro" +
            "          or to_char(al.dataEnvio, 'dd/mm/yyyy') like :filtro" +
            "          or to_char(al.dataEmissao, 'dd/mm/yyyy') like :filtro " +
            "          or lower(al.descricaoConformeDo) like :filtro )";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("tipo", PropositoAtoLegal.ATO_DE_PESSOAL);
        q.setParameter("tipo1", PropositoAtoLegal.BENEFICIO_PREVIDENCIARIO);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<AtoLegal> buscarAtosLegais(String s) {
        String hql = " select al from AtoLegal al" +
            "     where (al.propositoAtoLegal = :atoDePessoal or al.propositoAtoLegal = :beneficio)" +
            "       and (lower(al.nome) like :filtro" +
            "        or lower(al.numero) like :filtro" +
            "        or lower(al.tipoAtoLegal) like :filtro" +
            "          or to_char(al.dataPublicacao, 'DD/MM/YYYY') like :filtro" +
            "          or to_char(al.dataEnvio, 'dd/mm/yyyy') like :filtro" +
            "          or to_char(al.dataEmissao, 'dd/mm/yyyy') like :filtro " +
            "          or lower(al.descricaoConformeDo) like :filtro )" +
            " order by al.numero asc ";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setParameter("atoDePessoal", PropositoAtoLegal.ATO_DE_PESSOAL);
        q.setParameter("beneficio", PropositoAtoLegal.BENEFICIO_PREVIDENCIARIO);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<AtoLegal> buscarAtoLegalPorNomeNumero(String s) {

        String hql = " select al from AtoLegal al"
            + "     where (lower(al.nome) like :filtro"
            + "        or lower(al.numero) like :filtro"
            + "        or lower(al.tipoAtoLegal) like :filtro" +
            "          or to_char(al.dataPublicacao, 'DD/MM/YYYY') like :filtro" +
            "          or to_char(al.dataEnvio, 'dd/mm/yyyy') like :filtro" +
            "          or to_char(al.dataEmissao, 'dd/mm/yyyy') like :filtro " +
            "          or lower(al.descricaoConformeDo) like :filtro )";

        Query q = em.createQuery(hql);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<AtoLegal> listaPorPropositoAtoLegal(PropositoAtoLegal propositoAtoLegal, String parte) {
        Query q = getEntityManager().createQuery("from AtoLegal a where a.propositoAtoLegal = :proposito or a.nome = :parte");
        q.setParameter("proposito", propositoAtoLegal);
        q.setParameter("parte", parte);
        q.setMaxResults(1000);
        return q.getResultList();
    }

    public List<AtoLegal> listaOficiosBCENaoUsados(CadastroEconomico bce, String parte) {
        String hql = "from AtoLegal a where a.propositoAtoLegal = :proposito and not exists(select c from CadastroEconomico c where c.oficio = a";
        if (bce != null && bce.getId() != null) {
            hql += " and c != :bce";
        }
        hql += ")";
        hql += " and a.numero like :parte and a.nome like :parte";
        Query q = getEntityManager().createQuery(hql);
        q.setParameter("proposito", PropositoAtoLegal.OFICIO_BCE);
        q.setParameter("parte", "%" + parte.trim() + "%");
        if (bce != null && bce.getId() != null) {
            q.setParameter("bce", bce);
        }
        return q.getResultList();
    }

    public List<AtoLegal> listaPorNomeIsNotNull(String parte) {
        String sql = "SELECT * FROM atolegal WHERE nome IS NOT null AND nome LIKE :nome AND propositoatolegal = 'TRIBUTARIO'";
        Query q = em.createNativeQuery(sql, AtoLegal.class);
        q.setParameter("nome", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<AtoLegal> listaFiltrandoX(String s, int inicio, int max) {
        String hql = " select ato from AtoLegal ato "
            + " inner join ato.esferaGoverno esfera "
            + " where ((lower(ato.numero) like :filtro)"
            + " or (lower(ato.nome) like :filtro) "
            + " or (ato.propositoAtoLegal in (:listaPropositoAtoLegal)) "
            + " or (ato.tipoAtoLegal in (:listaTipoAtoLegal)) "
            + " or (lower(esfera.nome) like :filtro)) ";
        Query q = getEntityManager().createQuery(hql);
        s = s.toLowerCase().trim();

        List<PropositoAtoLegal> listaPropositoAtoLegal = new ArrayList<PropositoAtoLegal>();

        for (PropositoAtoLegal object : PropositoAtoLegal.values()) {
            if (object.getDescricao().toLowerCase().contains(s)) {
                listaPropositoAtoLegal.add(object);
            }
        }

        List<TipoAtoLegal> listaTipoAtoLegal = new ArrayList<TipoAtoLegal>();
        for (TipoAtoLegal object : TipoAtoLegal.values()) {
            if (object.getDescricao().toLowerCase().contains(s)) {
                listaTipoAtoLegal.add(object);
            }
        }

        if (listaPropositoAtoLegal.isEmpty()) {
            listaPropositoAtoLegal = null;
        }
        if (listaTipoAtoLegal.isEmpty()) {
            listaTipoAtoLegal = null;
        }

        q.setParameter("listaPropositoAtoLegal", listaPropositoAtoLegal);
        q.setParameter("listaTipoAtoLegal", listaTipoAtoLegal);
        q.setParameter("filtro", "%" + s + "%");
        q.setMaxResults(max + 1);
        q.setFirstResult(inicio);
        return q.getResultList();
    }

    public List<AtoLegal> listaFiltrandoNomeNumero(String s) {
        Query q = em.createQuery(" from AtoLegal where lower(numero) like :filtro or lower(nome) like :filtro ");
        q.setMaxResults(9);
        q.setParameter("filtro", "%" + s.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        return q.getResultList();
    }

    /*
     *
     * filtra ato legal por exercici e proposito
     *
     */
    public List<AtoLegal> buscarAtosLegaisPorExercicio(String parte, Exercicio exerc) {
        String sql = getSelectAtoLegal() +
            "ORDER BY ATO.NUMERO ";
        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
        parte = parte.toLowerCase().trim();
        parte = "%" + parte.toLowerCase().trim() + "%";
        q.setParameter("exerc", exerc.getId());
        q.setParameter("parte", parte);
        q.setMaxResults(10);
        return q.getResultList();
    }

    private String getSelectAtoLegal() {
        return " SELECT * FROM atolegal ato " +
            " WHERE (lower(ato.nome) LIKE :parte or lower(ato.numero) like :parte)" +
            " AND ato.exercicio_id = :exerc ";
    }

    public List<AtoLegal> buscarAtosLegaisPorExercicioAndNumeroOrNome(String parte, Exercicio exerc) {
        String sql = getSelectAtoLegal() +
            " ORDER BY ato.dataPublicacao desc, ATO.NUMERO asc ";
        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
        parte = parte.toLowerCase().trim();
        parte = "%" + parte.toLowerCase().trim() + "%";
        q.setParameter("exerc", exerc.getId());
        q.setParameter("parte", parte);
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<AtoLegal> buscarTodosAtosLegaisPorExercicio(String parte, Exercicio exerc) {
        try {
            String sql = " SELECT * FROM atolegal ato " +
                " WHERE (lower(ato.nome) LIKE :parte or lower(ato.numero) like :parte)"
                + " AND ato.exercicio_id = :exerc ORDER BY ATO.NUMERO ";
            Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
            parte = parte.toLowerCase().trim();
            parte = "%" + parte.toLowerCase().trim() + "%";
            q.setParameter("exerc", exerc.getId());
            q.setParameter("parte", parte);
            return q.getResultList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public List<AtoLegal> listTodosAtoExerc(Exercicio exerc) {
        String sql = " SELECT * FROM atolegal ato " +
            " WHERE ato.exercicio_id =:exerc " +
            " ORDER BY to_number(ATO.NUMERO) ";
        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
        q.setParameter("exerc", exerc.getId());
        return q.getResultList();
    }

    /*
     *
     * filtra ato legal por exercicio
     *
     */
    public List<AtoLegal> listAtoPorPropositoAtoLegal(String parte, PropositoAtoLegal propisito, Exercicio exerc) {
        String sql = " SELECT * FROM atolegal ato WHERE ato.nome LIKE :parte"
            + " AND ato.propositoatolegal=:proposito"
            + " AND ato.exercicio_id =:exerc AND rownum < 11";
        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
        parte = "%" + parte.toLowerCase().trim() + "%";

        q.setParameter("proposito", propisito.name());
        q.setParameter("exerc", exerc.getId());
        q.setParameter("parte", parte);
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<AtoLegal> listAtoPorPropositoAtoLegal(String parte, PropositoAtoLegal propisito) {
        String sql = " SELECT * FROM atolegal ato WHERE ato.nome LIKE :parte"
            + " AND ato.propositoatolegal = :proposito ";
        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
        parte = "%" + parte.toLowerCase().trim() + "%";

        q.setParameter("proposito", propisito.name());
        q.setParameter("parte", parte);
        q.setMaxResults(10);
        return q.getResultList();
    }


    public List<AtoLegal> listaFiltrandoAtoLegalPorTipoExercicio(String parte, Exercicio ex, TipoAtoLegal tipoAtoLegal) {
        String sql = "select aa.* from AtoLegal aa "
            + " where aa.exercicio_id = :ex "
            + " and aa.tipoAtoLegal = :tipoAto "
            + " AND ((lower(aa.nome) LIKE :param)OR ((aa.numero) LIKE :param)) ";

        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
        q.setParameter("param", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipoAto", tipoAtoLegal.toString());
        q.setParameter("ex", ex.getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ArquivoFacade getArquivoFacade() {
        return arquivoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public UnidadeExternaFacade getUnidadeExternaFacade() {
        return unidadeExternaFacade;
    }

    public EsferaGovernoFacade getEsferaGovernoFacade() {
        return esferaGovernoFacade;
    }

    public AtoDeComissaoFacade getAtoDeComissaoFacade() {
        return atoDeComissaoFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public ComissaoFacade getComissaoFacade() {
        return comissaoFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public WSAtoLegal transformarAtoLegalEmWSAtoLegal(AtoLegal atoLegal) {
        WSAtoLegal wsAtoLegal = new WSAtoLegal();
        wsAtoLegal.setId(atoLegal.getId());
        wsAtoLegal.setNumero(atoLegal.getNumero());
        wsAtoLegal.setNome(atoLegal.getNome());
        wsAtoLegal.setDataEmissao(atoLegal.getDataEmissao());
        wsAtoLegal.setDataPromulgacao(atoLegal.getDataPromulgacao());
        wsAtoLegal.setDataEnvio(atoLegal.getDataEnvio());
        wsAtoLegal.setDataPublicacao(atoLegal.getDataPublicacao());
        wsAtoLegal.setDataValidade(atoLegal.getDataValidade());
        wsAtoLegal.setDescricaoConformeDo(atoLegal.getDescricaoConformeDo());
        if (atoLegal.getEsferaGoverno() != null) {
            wsAtoLegal.setEsferaDoGoverno(atoLegal.getEsferaGoverno().getNome());
        }
        if (atoLegal.getExercicio() != null) {
            wsAtoLegal.setExercicio(atoLegal.getExercicio().getAno());
        }
        wsAtoLegal.setFundamentacaoLegal(atoLegal.getFundamentacaoLegal());
        wsAtoLegal.setNumeroPublicacao(atoLegal.getNumeroPublicacao());
        wsAtoLegal.setPropositoAtoLegal(atoLegal.getPropositoAtoLegal().getDescricao());
        wsAtoLegal.setTipoAtoLegal(atoLegal.getTipoAtoLegal().getDescricao());
        if (atoLegal.getVeiculoDePublicacao() != null) {
            wsAtoLegal.setVeiculoDePublicacao(atoLegal.getVeiculoDePublicacao().getNome());
        }
        return wsAtoLegal;
    }

    public List<WSAtoLegal> consultarAtosLegais(WSAtoLegal filtros, int inicio, int quantidade) {
        List<WSAtoLegal> toReturn = Lists.newArrayList();
        StringBuilder sql = new StringBuilder();
        sql.append(" select * from atolegal ato ");
        String juncao = " where ";
        Map<String, Object> parameters = Maps.newHashMap();
        if (filtros.getNumero() != null && !filtros.getNumero().isEmpty()) {
            sql.append(juncao).append(" ato.numero = :numero ");
            juncao = " and ";
            parameters.put("numero", filtros.getNumero());
        }
        if (filtros.getNome() != null && !filtros.getNome().isEmpty()) {
            sql.append(juncao).append(" ato.nome like CONCAT(:nome, '%')");
            juncao = " and ";
            parameters.put("nome", filtros.getNome());
        }
        if (filtros.getDataPublicacao() != null) {
            sql.append(juncao).append(" ato.dataPublicacao = :dataPublicacao ");
            juncao = " and ";
            parameters.put("dataPublicacao", filtros.getDataPublicacao());
        }
        if (filtros.getDataPromulgacao() != null) {
            sql.append(juncao).append(" ato.dataPromulgacao = :dataPromulgacao ");
            juncao = " and ";
            parameters.put("dataPromulgacao", filtros.getDataPromulgacao());
        }
        sql.append(" order by id desc ");
        Query q = em.createNativeQuery(sql.toString(), AtoLegal.class);
        for (String key : parameters.keySet()) {
            q.setParameter(key, parameters.get(key));
        }
        q.setFirstResult(inicio);
        q.setMaxResults(quantidade);
        if (!q.getResultList().isEmpty()) {
            for (AtoLegal atoLegal : ((List<AtoLegal>) q.getResultList())) {
                toReturn.add(transformarAtoLegalEmWSAtoLegal(atoLegal));
            }
        }
        return toReturn;
    }

    public WSArquivo recuperaArquivo(Long idAtoLegal) {
        WSArquivo toReturn = null;
        AtoLegal atoLegal = recuperar(idAtoLegal);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            arquivoFacade.recupera(atoLegal.getArquivo().getId(), byteArrayOutputStream);
            toReturn = new WSArquivo();
            toReturn.setNome(atoLegal.getArquivo().getNome());
            toReturn.setMimeType(atoLegal.getArquivo().getMimeType());
            toReturn.setDados(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
        }
        return toReturn;
    }

    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public List<AtoLegal> buscarTodosParaPortalTransparencia(Exercicio exerc) {
        List<String> tipos = Lists.newArrayList();
        tipos.add(TipoAtoLegal.OFICIO.name());
        tipos.add(TipoAtoLegal.MEMORANDO.name());
        tipos.add(TipoAtoLegal.PROCESSO.name());
        tipos.add(TipoAtoLegal.ATESTADO.name());
        tipos.add(TipoAtoLegal.CIRCULAR.name());
        tipos.add(TipoAtoLegal.DESPACHO.name());

        String sql = " SELECT * FROM atolegal ato " +
            " WHERE ato.exercicio_id =:exerc " +
            " and ato.tipoAtoLegal not in :tipos " +
            " ORDER BY to_number(ATO.NUMERO) ";
        Query q = getEntityManager().createNativeQuery(sql, AtoLegal.class);
        q.setParameter("exerc", exerc.getId());
        q.setParameter("tipos", tipos);
        return q.getResultList();
    }

    @Override
    public void salvar(AtoLegal entity) {
        super.salvar(entity);
        salvarPortal(entity);
    }

    @Override
    public void salvarNovo(AtoLegal entity) {
        super.salvarNovo(entity);
        salvarPortal(entity);
    }

    private void salvarPortal(AtoLegal entity) {
        portalTransparenciaNovoFacade.salvarPortal(new AtoLegalPortal(entity));
    }

    public AtoLegalORC buscarAtolegaOrcPorAtoLegal(AtoLegal ato) {
        String sql = " select a.* from AtoLegalORC a  " +
            "           where a.atoLegal_id = :param ";
        Query q = em.createNativeQuery(sql, AtoLegalORC.class);
        q.setParameter("param", ato.getId());

        if (!q.getResultList().isEmpty()) {
            return (AtoLegalORC) q.getResultList().get(0);
        }
        return null;
    }

    private boolean hasAtoComNumeroTipoExercicioEUnidadeJaCadastrado(AtoLegal selecionado) {
        String sql = " select ato.* from atolegal ato " +
            " where trim(ato.numero) = :numeroAto " +
            "   and ato.tipoAtoLegal = :tipo " +
            "   and ato.exercicio_id = :idExercicio " +
            (selecionado.getId() != null ? " and ato.id <> :idAto " : "");
        if (!selecionado.getTipoAtoLegal().isLeisOuDecreto()) {
            sql += (selecionado.getUnidadeExterna() != null ? " and ato.unidadeexterna_id = :idUnidadeExterna " : "");
            sql += (selecionado.getUnidadeOrganizacional() != null ? " and ato.unidadeorganizacional_id = :idUnidade " : "");
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("numeroAto", selecionado.getNumero().trim());
        q.setParameter("tipo", selecionado.getTipoAtoLegal().name());
        q.setParameter("idExercicio", selecionado.getExercicio().getId());
        if (!selecionado.getTipoAtoLegal().isLeisOuDecreto()) {
            if (selecionado.getUnidadeExterna() != null) {
                q.setParameter("idUnidadeExterna", selecionado.getUnidadeExterna().getId());
            }
            if (selecionado.getUnidadeOrganizacional() != null) {
                q.setParameter("idUnidade", selecionado.getUnidadeOrganizacional().getId());
            }
        }
        if (selecionado.getId() != null) {
            q.setParameter("idAto", selecionado.getId());
        }
        List<Object[]> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }

    public void validarAtoLegal(AtoLegal selecionado, AssistenteBarraProgressoImportarAto assistente, boolean validarDataPublicacaoComDataOperacao, boolean validarArquivo) {
        HierarquiaOrganizacional hierarquiaOrganizacional = getHierarquiaOrganizacionalFacade().getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), selecionado.getUnidadeOrganizacional(), selecionado.getDataPublicacao()
        );
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        validarDataPublicacao(selecionado, ve, validarDataPublicacaoComDataOperacao);
        validarDemaisCamposObrigatorios(selecionado, ve, validarArquivo);
        validarValoresDasOperacoesDeCreditoAndSuplementacao(selecionado, assistente, ve);
        validarMesmoAtoLegal(selecionado, hierarquiaOrganizacional, ve);
        validarPeriodoDatas(selecionado, ve);
        validarRegrasPropositoComissao(selecionado, ve);
        validarSituacao(selecionado, ve);
        ve.lancarException();
    }

    public void validarDemaisCamposObrigatorios(AtoLegal selecionado, ValidacaoException ve, boolean validarArquivo) {
        if (!selecionado.isTipoAtoLegalOficio()) {
            if (selecionado.getVeiculoDePublicacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Veículo de Publicação deve ser informado!");
            }
        }
        if (selecionado.isTipoAtoLegalProjetoDeLei()) {
            if (selecionado.getDataEnvio() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Envio deve ser informado!");
            }
        }
        if (renderizaPainelUnidades(selecionado) || isTipoAtoLegalLegislativoOrControleExterno(selecionado)) {
            if (selecionado.isTipoAtoLegalUnidadeInterna()) {
                if (selecionado.getUnidadeOrganizacional() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Organizacional deve ser informada!");
                }
            }
            if (selecionado.isTipoAtoLegalUnidadeExterna()) {
                if (selecionado.getUnidadeExterna() == null) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo Unidade Externa deve ser informado!");
                }
            }

        } else {
            if (selecionado.getDataPromulgacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Sanção deve ser informada!");
            }
            if (selecionado.getDataPublicacao() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Publicação deve ser informada!");
            }
        }
        if (validarArquivo && selecionado.getArquivo() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Anexar Arquivo deve ser informado.");
        }
    }

    public void validaAtoLegalORC(AtoLegal selecionado, AssistenteBarraProgressoImportarAto assistente) {
        boolean isAlteracaoOrcamento = PropositoAtoLegal.ALTERACAO_ORCAMENTARIA.equals(selecionado.getPropositoAtoLegal());
        boolean isOrcamento = PropositoAtoLegal.ORCAMENTO.equals(selecionado.getPropositoAtoLegal());
        boolean isLei = TipoAtoLegal.LEI_ORDINARIA.equals(selecionado.getTipoAtoLegal());
        boolean isProjeto = TipoAtoLegal.EMENDA.equals(selecionado.getTipoAtoLegal());
        boolean isProjetoLei = TipoAtoLegal.PROJETO_DE_LEI.equals(selecionado.getTipoAtoLegal());
        boolean isDecreto = TipoAtoLegal.DECRETO.equals(selecionado.getTipoAtoLegal());
        boolean isMedidaProvisao = TipoAtoLegal.MEDIDA_PROVISORIA.equals(selecionado.getTipoAtoLegal());
        boolean isResolucao = TipoAtoLegal.RESOLUCAO.equals(selecionado.getTipoAtoLegal());
        if ((isAlteracaoOrcamento || isOrcamento) && (isLei || isDecreto || isMedidaProvisao || isProjeto || isProjetoLei || isResolucao)) {
            assistente.getAtoLegalORC().setAtoLegal(selecionado);
            selecionado.setAtoLegalORC(assistente.getAtoLegalORC());
        } else {
            assistente.getAtoLegalORC().setAtoLegal(null);
            selecionado.setAtoLegalORC(null);
        }
    }

    private void validarMesmoAtoLegal(AtoLegal selecionado, HierarquiaOrganizacional hierarquiaOrganizacional, ValidacaoException ve) {
        if (existeNumeroDoAtoLegalRepetido(selecionado)) {
            validarMesmoAtoLegalParaCombinacoesDiferente(selecionado, hierarquiaOrganizacional, ve);
        }
    }

    private void validarMesmoAtoLegalParaCombinacoesDiferente(AtoLegal selecionado, HierarquiaOrganizacional hierarquiaOrganizacional, ValidacaoException ve) {
        if (renderizaPainelUnidades(selecionado)) {
            if (hierarquiaOrganizacional != null && selecionado.isTipoAtoLegalUnidadeInterna()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existem um Ato Legal cadastrado com o número <b>" + selecionado.getNumero() + "</b>" +
                    " do Tipo <b>" + selecionado.getTipoAtoLegal().getDescricao() + "</b>" +
                    " e Propósito <b>" + selecionado.getPropositoAtoLegal().getDescricao() + "</b>" +
                    " e Unidade Organizacional <b>" + (hierarquiaOrganizacional != null ? hierarquiaOrganizacional : selecionado.getUnidadeOrganizacional()) + "</b>" +
                    " para o exercício de <b>" + selecionado.getExercicio().getAno() + "</b>.");
            } else if (selecionado.getUnidadeExterna() != null && selecionado.isTipoAtoLegalUnidadeExterna()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existem um Ato Legal cadastrado com o número <b>" + selecionado.getNumero() + "</b>" +
                    " do Tipo <b>" + selecionado.getTipoAtoLegal().getDescricao() + "</b>" +
                    " e Propósito <b>" + selecionado.getPropositoAtoLegal().getDescricao() + "</b>" +
                    " e Unidade Externa <b>" + selecionado.getUnidadeExterna() + "</b>" +
                    " para o exercício de <b>" + selecionado.getExercicio().getAno() + "</b>.");
            }
        } else {
            if (!PropositoAtoLegal.ALTERACAO_ORCAMENTARIA.equals(selecionado.getPropositoAtoLegal())
                && !TipoAtoLegal.DECRETO.equals(selecionado.getTipoAtoLegal())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existem um Ato Legal cadastrado com o número <b>" + selecionado.getNumero() + "</b>" +
                    " do Tipo <b>" + selecionado.getTipoAtoLegal().getDescricao() + "</b>" +
                    " para o exercício de <b>" + selecionado.getExercicio().getAno() + "</b>.");
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existem um Ato Legal cadastrado com o número <b>" + selecionado.getNumero() + "</b>" +
                    " do Tipo <b>" + selecionado.getTipoAtoLegal().getDescricao() + "</b>" +
                    " e esfera <b>" + selecionado.getEsferaGoverno().getNome() + "</b>" +
                    " para o exercício de <b>" + selecionado.getExercicio().getAno() + "</b>.");
            }
        }
    }

    private void validarDataPublicacao(AtoLegal selecionado, ValidacaoException ve, boolean validarDataPublicacaoComDataOperacao) {
        if (validarDataPublicacaoComDataOperacao && sistemaFacade.getDataOperacao().before(selecionado.getDataPublicacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Publicação não pode ser superior a data atual!");
        }
    }

    private void validarValoresDasOperacoesDeCreditoAndSuplementacao(AtoLegal selecionado, AssistenteBarraProgressoImportarAto assistente, ValidacaoException ve) {
        if (assistente != null && selecionado.isPropositoAtoLegalAlteracaoOrcamentaria()) {
            assistente.calcularLancamentos();
            if (!assistente.getValorOperacoesCredito().isValidaTotais() && assistente.getAtoLegalORC().getSuperAvit().compareTo(BigDecimal.ZERO) == 0) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Corrigir os valores das Operações de Crédito e Suplementações para continuar.");
            }
        }
    }

    private void validarRegrasPropositoComissao(AtoLegal selecionado, ValidacaoException ve) {
        if (selecionado.isPropositoAtoLegalComissao()) {
            if (!temComissaoAdicionada(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Adicione ao menos uma comissão!");
            }
            if (temComissaoSemMembro(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Toda comissão deve ter ao menos um membro adicionado!");
            }
        }
    }

    private boolean temComissaoAdicionada(AtoLegal selecionado) {
        try {
            return selecionado.getAtoDeComissao().temComissaoAdicionada();
        } catch (NullPointerException npe) {
            logger.error("Erro ao temComissaoAdicionada: ", npe);
            return false;
        }
    }

    private boolean temComissaoSemMembro(AtoLegal selecionado) {
        return selecionado.getAtoDeComissao().temComissaoSemMembro();
    }

    public boolean renderizaPainelUnidades(AtoLegal selecionado) {
        return PropositoAtoLegal.ATO_DE_CARGO.equals(selecionado.getPropositoAtoLegal()) ||
            PropositoAtoLegal.ATO_DE_PESSOAL.equals(selecionado.getPropositoAtoLegal()) ||
            PropositoAtoLegal.BENEFICIO_PREVIDENCIARIO.equals(selecionado.getPropositoAtoLegal()) ||
            PropositoAtoLegal.CONCESSAO_BENEFICIO.equals(selecionado.getPropositoAtoLegal()) ||
            PropositoAtoLegal.COMISSAO.equals(selecionado.getPropositoAtoLegal()) ||
            PropositoAtoLegal.OUTROS.equals(selecionado.getPropositoAtoLegal());
    }

    private void validarPeriodoDatas(AtoLegal selecionado, ValidacaoException ve) {
        boolean logico = !selecionado.isTipoAtoLegalEmenda()
            || !selecionado.isTipoAtoLegalProjetoDeLei() || !selecionado.isTipoAtoLegalDecreto();
        if (!selecionado.isPropositoAtoLegalAlteracaoOrcamentaria()) {
            if (logico) {
                if (selecionado.getDataPromulgacao() != null && selecionado.getDataPublicacao() != null) {
                    if (selecionado.getDataPromulgacao().after(selecionado.getDataPublicacao())) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Publicação deve ser posterior que a Data de Sanção!");
                    }
                }
            }
        }
    }

    private void validarSituacao(AtoLegal selecionado, ValidacaoException ve) {
        if (isValidarSituacao(selecionado)) {
            if (selecionado.getSituacaoAtoLegal() == null) {
                ve.adicionarMensagemDeCampoObrigatorio("O campo Situação deve ser informado!");
            }
        }
    }

    public boolean isValidarSituacao(AtoLegal selecionado) {
        return PropositoAtoLegal.ATO_DE_CARGO.equals(selecionado.getPropositoAtoLegal()) || PropositoAtoLegal.ATO_DE_PESSOAL.equals(selecionado.getPropositoAtoLegal());
    }

    public boolean isTipoAtoLegalLegislativoOrControleExterno(AtoLegal selecionado) {
        return TipoAtoLegal.CONTROLE_EXTERNO.equals(selecionado.getTipoAtoLegal()) ||
            TipoAtoLegal.PORTARIA.equals(selecionado.getTipoAtoLegal()) ||
            TipoAtoLegal.LEGISLATIVO.equals(selecionado.getTipoAtoLegal());
    }

    public Exercicio recuperarExercicio(String ano) {
        if (ano != null && !ano.isEmpty()) {
            return exercicioFacade.getExercicioPorAno(Integer.parseInt(ano));
        }
        return null;
    }

    public EsferaGoverno recuperarEsferaGoverno(String nome) {
        return esferaGovernoFacade.findEsferaGovernoByNome(nome);
    }

    public VeiculoDePublicacao recuperarVeiculoDePublicacao(String nomeVeiculo) {
        List<VeiculoDePublicacao> veiculos = veiculoDePublicacaoFacade.buscarVeiculosPorNome(nomeVeiculo);
        return (veiculos != null && !veiculos.isEmpty()) ? veiculos.get(0) : null;
    }

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Future<AssistenteBarraProgressoImportarAto> importarAtos(AssistenteBarraProgressoImportarAto assistente) throws IOException {
        assistente.setDescricaoProcesso("Recuperando os Atos Legais do arquivo. Por favor aguarde!");
        recuperarAtosDoArquivoEValidar(assistente);
        return new AsyncResult(assistente);
    }

    public void recuperarAtosDoArquivoEValidar(AssistenteBarraProgressoImportarAto assistente) throws IOException {
        List<AtoLegal> atosRecuperados = montarAtosLegais(assistente.getEvento(), assistente);
        assistente.setDescricaoProcesso("Atos recuperados, validando os campos.");
        assistente.setTotal(atosRecuperados.size());
        for (AtoLegal atoRecuperado : atosRecuperados) {
            try {
                validarAtoLegal(atoRecuperado, assistente, false, false);
                validaAtoLegalORC(atoRecuperado, assistente);
                assistente.getAtosNaoCadastrados().add(atoRecuperado);
            } catch (ValidacaoException ve) {
                String menssagens = "<br/>Erro: ";
                for (FacesMessage message : ve.getMensagens()) {
                    menssagens += message.getDetail() + "<br/>";
                }
                menssagens += "<br/>";
                assistente.getErrosAoImportar().append(atoRecuperado.toString()).append(menssagens);
            } catch (Exception ex) {
                logger.error("Erro ao importar arquivo de atos legais.", ex);
                assistente.getErrosAoImportar().append(atoRecuperado.toString()).append("<br/>Erro: ").append(ex.getMessage()).append("<br/><br/>");
            }
            assistente.conta();
        }
    }

    private List<AtoLegal> montarAtosLegais(FileUploadEvent evento, AssistenteBarraProgressoImportarAto assistente) throws IOException {
        List<AtoLegal> atosRecuperados = Lists.newArrayList();
        UploadedFile file = evento.getFile();
        InputStreamReader in = new InputStreamReader(file.getInputstream(), StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(in);
        String linha;
        while ((linha = bufferedReader.readLine()) != null) {
            if (linha.trim().startsWith("1") || linha.trim().startsWith("2")) {
                String[] partes = ExcelUtil.splitCSV(linha);
                try {
                    atosRecuperados.add(montarNovoAtoLegal(partes, assistente));
                } catch (Exception ex) {
                    logger.error("Erro ao importar arquivo de atos legais.", ex);
                    assistente.getErrosAoImportar().append(partes[5]).append("<br/>Erro: ").append(ex.getMessage()).append("<br/>").append("<br/>");
                }
            }
        }
        return atosRecuperados;
    }

    private AtoLegal montarNovoAtoLegal(String[] partes, AssistenteBarraProgressoImportarAto assistente) {
        Exercicio ex = recuperarExercicio(partes[0]);
        Integer numeroPublicacao = isStringNullOrEmpty(partes[1]) ? null : Integer.valueOf(partes[1]);
        String numero = isStringNullOrEmpty(partes[2]) ? null : partes[2];
        Date dataPromulgacao = recuperarDataDoArquivo(partes[3], ex);
        Date dataPublicacao = recuperarDataDoArquivo(partes[4], ex);
        String nome = isStringNullOrEmpty(partes[5]) ? null : partes[5].replace("\u0002", "");
        UnidadeOrganizacional uo = getOrgaoPorTipo(partes[6], assistente);
        EsferaGoverno eg = recuperarEsferaGoverno(partes[7]);
        PropositoAtoLegal propositoAto = isStringNullOrEmpty(partes[8]) ? null : PropositoAtoLegal.valueOf(partes[8]);
        TipoAtoLegal tipoAto = isStringNullOrEmpty(partes[9]) ? null : TipoAtoLegal.getTipoAtoLegalPorDescricao(partes[9]);
        VeiculoDePublicacao vdp = recuperarVeiculoDePublicacao(partes[10]);
        return new AtoLegal(ex, numeroPublicacao, numero, dataPromulgacao, dataPublicacao, nome, uo, eg, propositoAto, tipoAto, vdp);
    }

    private Date recuperarDataDoArquivo(String parte, Exercicio exercicio) {
        if (exercicio != null && !isStringNullOrEmpty(parte)) {
            if (parte.trim().length() == 8) {
                parte = parte.substring(0, 6);
                return DataUtil.getDateParse(parte + exercicio.getAno());
            }
            return DataUtil.getDateParse(parte);
        }
        return null;
    }

    private boolean isStringNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    private UnidadeOrganizacional getOrgaoPorTipo(String tipo, AssistenteBarraProgressoImportarAto assistente) {
        if (assistente.getOrgaosPorTipo() == null || assistente.getOrgaosPorTipo().isEmpty()) return null;
        Optional<OrgaoAtoLegalVO> retorno = assistente.getOrgaosPorTipo().stream().filter(orgaoAtoLegalVO -> orgaoAtoLegalVO.getTipo().name().equals(tipo)).findFirst();
        return retorno.map(orgaoAtoLegalVO -> orgaoAtoLegalVO.getHierarquiaOrganizacional().getSubordinada()).orElse(null);
    }
}
