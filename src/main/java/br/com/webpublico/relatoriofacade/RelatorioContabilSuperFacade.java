package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.ApresentacaoRelatorio;
import br.com.webpublico.negocios.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Edi on 01/06/2015.
 */
@Stateless
public class RelatorioContabilSuperFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private DividaPublicaFacade dividaPublicaFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private FuncaoFacade funcaoFacade;
    @EJB
    private SubFuncaoFacade subFuncaoFacade;
    @EJB
    private ProgramaPPAFacade programaPPAFacade;
    @EJB
    private TipoAcaoPPAFacade tipoAcaoPPAFacade;
    @EJB
    private ProjetoAtividadeFacade projetoAtividadeFacade;
    @EJB
    private SubProjetoAtividadeFacade subProjetoAtividadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;

    public EntityManager getEm() {
        return em;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public LoteBaixaFacade getLoteBaixaFacade() {
        return loteBaixaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public DividaPublicaFacade getDividaPublicaFacade() {
        return dividaPublicaFacade;
    }

    public EventoContabilFacade getEventoContabilFacade() {
        return eventoContabilFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public FuncaoFacade getFuncaoFacade() {
        return funcaoFacade;
    }

    public SubFuncaoFacade getSubFuncaoFacade() {
        return subFuncaoFacade;
    }

    public ProgramaPPAFacade getProgramaPPAFacade() {
        return programaPPAFacade;
    }

    public TipoAcaoPPAFacade getTipoAcaoPPAFacade() {
        return tipoAcaoPPAFacade;
    }

    public ProjetoAtividadeFacade getProjetoAtividadeFacade() {
        return projetoAtividadeFacade;
    }

    public SubProjetoAtividadeFacade getSubProjetoAtividadeFacade() {
        return subProjetoAtividadeFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ReceitaExtraFacade getReceitaExtraFacade() {
        return receitaExtraFacade;
    }

    @Deprecated
    // Utilizar UtilRelatorioContabil.adicionarParametros(parametros, q);
    public Query adicionaParametros(List<ParametrosRelatorios> parametros, Query q) {
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
            if (parametrosRelatorios.getCampo2() != null) {
                q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
            }
        }
        return q;
    }

    @Deprecated
    // Utilizar UtilRelatorioContabil.adicionarParametrosRetirandoLocais(parametros, q, ...);
    public Query adicionaParametrosMenosLocal(List<ParametrosRelatorios> parametros, Query q, Integer local) {
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() != local) {
                q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
                if (parametrosRelatorios.getCampo2() != null) {
                    q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
                }
            }
        }
        return q;
    }

    @Deprecated
    // Utilizar UtilRelatorioContabil.adicionarParametrosRetirandoLocais(parametros, q, ...);
    public Query adicionarParametrosRetirandoLocais(List<ParametrosRelatorios> parametros, Query q, Integer... locais) {
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (!hasLocal(parametrosRelatorios, locais)) {
                q.setParameter(parametrosRelatorios.getCampo1SemDoisPontos(), parametrosRelatorios.getValor1());
                if (parametrosRelatorios.getCampo2() != null) {
                    q.setParameter(parametrosRelatorios.getCampo2SemDoisPontos(), parametrosRelatorios.getValor2());
                }
            }
        }
        return q;
    }

    private boolean hasLocal(ParametrosRelatorios parametrosRelatorios, Integer[] locais) {
        for (Integer local : locais) {
            if (local.intValue() == parametrosRelatorios.getLocal())
                return true;
        }
        return false;

    }

    @Deprecated
    // Utilizar UtilRelatorioContabil.concatenarParametros(parametros, 1, " and ");
    public String concatenaParametros(List<ParametrosRelatorios> parametros, Integer local, String clausula) {
        StringBuilder sql = new StringBuilder();
        for (ParametrosRelatorios parametrosRelatorios : parametros) {
            if (parametrosRelatorios.getLocal() == local) {
                sql.append(clausula + parametrosRelatorios.getNomeAtributo() + " " + parametrosRelatorios.getOperacao().getDescricao() + " " + parametrosRelatorios.getCampo1());
                sql.append(parametrosRelatorios.getCampo2() != null ? " and " + parametrosRelatorios.getCampo2() : " ");
            }
        }
        return sql.toString();
    }

    @Deprecated
    public String buscarJoinUnidadeGestora(String apresentacao, Boolean pesquisouUg) {
        return pesquisouUg || "UNIDADE_GESTORA".equals(apresentacao) ?
            " inner join UGESTORAUORGANIZACIONAL UgUo on vw.subordinada_id = UgUo.unidadeorganizacional_id " +
                " inner join unidadegestora ug on UgUo.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "";
    }

    public StringBuilder buscarJoinUnidadeGestora(ApresentacaoRelatorio apresentacao, Boolean pesquisouUg) {
        StringBuilder sql = new StringBuilder();
        sql.append(pesquisouUg || apresentacao.isApresentacaoUnidadeGestora() ? " inner join UGESTORAUORGANIZACIONAL UgUnidade on vw.subordinada_id = ugunidade.unidadeorganizacional_id  inner join unidadegestora ug on ugunidade.unidadegestora_id = ug.id and ug.exercicio_id = :exercicio " : "");
        return sql;
    }

    public String buscarJoinVwUnidade(String campoJoin) {
        return " INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON " + campoJoin + " = VW.SUBORDINADA_ID ";
    }

    public String buscarJoinVwOrgao() {
        return " INNER JOIN VWHIERARQUIAORCAMENTARIA VWORG ON VW.SUPERIOR_ID = VWORG.SUBORDINADA_ID ";
    }

    public String getAndVigenciaVwUnidade(String campoData) {
        return " and " + campoData + " between VW.iniciovigencia and coalesce(VW.fimvigencia, " + campoData + ") ";
    }

    public String getAndVigenciaVwOrgao(String campoData) {
        return " and " + campoData + " between vworg.iniciovigencia and coalesce(vworg.fimvigencia, " + campoData + ") ";
    }

    public StringBuilder buscarJoinsFuncionalProgramatica(String aliasProvisaoPPADespesa) {
        StringBuilder sql = new StringBuilder();
        return sql.append(" inner join subacaoppa sub on sub.id = ").append(aliasProvisaoPPADespesa).append(".subacaoppa_id")
            .append(" inner join acaoppa a on a.id = sub.acaoppa_id ")
            .append(" inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id ")
            .append(" inner join programappa prog on prog.id = a.programa_id ")
            .append(" inner join funcao f on f.id = a.funcao_id ")
            .append(" inner join subfuncao sf on sf.id = a.subfuncao_id ");
    }
}
