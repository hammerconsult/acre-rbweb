package br.com.webpublico.controle;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.EntidadeMetaData;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mga on 16/01/2018.
 */
public abstract class AbstractPesquisaMateriais extends ComponentePesquisaGenerico {

    protected List<BigDecimal> idsUnidades = Lists.newArrayList();
    protected String condicaoUnidade = "";
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SaidaMaterialFacade saidaMaterialFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;
    @EJB
    private RequisicaoMaterialFacade requisicaoMaterialFacade;
    @EJB
    private AprovacaoMaterialFacade aprovacaoMaterialFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private LevantamentoEstoqueFacade levantamentoEstoqueFacade;
    @EJB
    private EfetivacaoLevantamentoEstoqueFacade efetivacaoLevantamentoEstoqueFacade;


    public abstract TipoMovimento getTipoMovimento();

    public abstract String getNivelHierarquia();

    protected List<BigDecimal> buscarHerarquiaOrganizacionalPaiEFilhoOndeUsuarioEhGestorMaterial() {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPaiEFilhoOndeUsuarioEhGestorMaterial(
            "",
            getNivelHierarquia(),
            getSistemaFacade().getUsuarioCorrente(),
            getSistemaFacade().getDataOperacao());

    }

    public String montarCondicaoUnidade(String alias) {
        String retorno = "";
        List<List<BigDecimal>> idsUnidades = Lists.partition(buscarHerarquiaOrganizacionalPaiEFilhoOndeUsuarioEhGestorMaterial(), 1000);
        for (List<BigDecimal> idHierarquia : idsUnidades) {
            if (!retorno.isEmpty()) {
                retorno += " or ";
            }
            retorno += buscarIdsUnidadeOndeUsuarioEGestorMaterial(alias, idHierarquia);
        }
        if (!retorno.isEmpty()) {
            return "(" + retorno + ")";
        }
        return retorno;
    }

    public String montarCondicaoUnidade(String alias, List<BigDecimal> idsUnidades) {
        String retorno = "";
        for (List<BigDecimal> idHierarquia : Lists.partition(idsUnidades, 1000)) {
            if (!retorno.isEmpty()) {
                retorno += " or ";
            }
            retorno += buscarIdsUnidadeOndeUsuarioEGestorMaterial(alias, idHierarquia);
        }
        if (!retorno.isEmpty()) {
            return "(" + retorno + ")";
        }
        return retorno;
    }

    private String buscarIdsUnidadeOndeUsuarioEGestorMaterial(String alias, List<BigDecimal> unidadesAdministrativas) {
        String toReturn = "";
        for (BigDecimal obj : unidadesAdministrativas) {
            toReturn += obj.longValue() + ",";
        }
        toReturn = !toReturn.isEmpty() ? toReturn.substring(0, toReturn.length() - 1) : "0";
        return alias + " in (" + toReturn + ") ";
    }

    protected void validarUsuarioGestorMateriais() {
        ValidacaoException ve = new ValidacaoException();
        if (!isGestorMateriais()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O usuário: " + getSistemaControlador().getUsuarioCorrente().getPessoaFisica() + " não possui permissão como gestor de materiais.");
        }
        ve.lancarException();
    }

    private boolean isGestorMateriais() {
        return getSistemaControlador().unidadesOrganizacionaisGestorMateriais() != null && !getSistemaControlador().unidadesOrganizacionaisGestorMateriais().isEmpty();
    }

    @Override
    public Integer getTotalDeRegistrosExistentes() {
        totalDeRegistrosExistentes = 0;
        if (isGestorMateriais()) {
            String hql = getHqlParaTotalDeRegistros();
            atribuirHqlConsultaRelatorioTodosRegistros();
            if (getTipoMovimento() != null) {
                switch (getTipoMovimento()) {
                    case SAIDA:
                        totalDeRegistrosExistentes = saidaMaterialFacade.count(hql).intValue();
                        break;
                    case ENTRADA:
                        totalDeRegistrosExistentes = entradaMaterialFacade.count(hql).intValue();
                        break;
                    case LOCAL_ESTOQUE:
                        totalDeRegistrosExistentes = localEstoqueFacade.count(hql).intValue();
                        break;
                    case REQUISICAO_MATERIAL:
                        totalDeRegistrosExistentes = requisicaoMaterialFacade.count(hql).intValue();
                        break;
                    case LEVANTAMENTO_ESTOQUE:
                        totalDeRegistrosExistentes = levantamentoEstoqueFacade.count(hql).intValue();
                        break;
                    case EFETIVACAO_LEVANTAMENTO_ESTOQUE:
                        totalDeRegistrosExistentes = efetivacaoLevantamentoEstoqueFacade.count(hql).intValue();
                        break;
                }
            }
        }
        return totalDeRegistrosExistentes;
    }

    public void processarRetorno(Object[] retorno) {
        try {
            lista = (ArrayList) retorno[0];
            totalDeRegistrosExistentes = ((Long) retorno[1]).intValue();
            if (!lista.isEmpty()) {
                metadata = new EntidadeMetaData(lista.get(0).getClass());
            }
        } catch (Exception ex) {
            logger.debug(ex.getMessage());
        }
    }

    public enum TipoMovimento {
        SAIDA,
        ENTRADA,
        LOCAL_ESTOQUE,
        REQUISICAO_MATERIAL,
        APROVACAO_MATERIAL,
        LEVANTAMENTO_ESTOQUE,
        EFETIVACAO_LEVANTAMENTO_ESTOQUE;
    }

    public AprovacaoMaterialFacade getAprovacaoMaterialFacade() {
        return aprovacaoMaterialFacade;
    }

    public SaidaMaterialFacade getSaidaMaterialFacade() {
        return saidaMaterialFacade;
    }

    public EntradaMaterialFacade getEntradaMaterialFacade() {
        return entradaMaterialFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }

    public RequisicaoMaterialFacade getRequisicaoMaterialFacade() {
        return requisicaoMaterialFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public List<BigDecimal> getIdsUnidades() {
        return idsUnidades;
    }

    public void setIdsUnidades(List<BigDecimal> idsUnidades) {
        this.idsUnidades = idsUnidades;
    }

    public String getCondicaoUnidade() {
        return condicaoUnidade;
    }

    public void setCondicaoUnidade(String condicaoUnidade) {
        this.condicaoUnidade = condicaoUnidade;
    }

    public LevantamentoEstoqueFacade getLevantamentoEstoqueFacade() {
        return levantamentoEstoqueFacade;
    }

    public EfetivacaoLevantamentoEstoqueFacade getEfetivacaoLevantamentoEstoqueFacade() {
        return efetivacaoLevantamentoEstoqueFacade;
    }
}
