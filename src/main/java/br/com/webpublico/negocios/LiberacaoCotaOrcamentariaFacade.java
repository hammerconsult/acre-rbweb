package br.com.webpublico.negocios;

import br.com.webpublico.entidades.CotaOrcamentaria;
import br.com.webpublico.entidades.LiberacaoCotaOrcamentaria;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 09/07/15
 * Time: 10:45
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class LiberacaoCotaOrcamentariaFacade extends AbstractFacade<LiberacaoCotaOrcamentaria> {

    @EJB
    private GrupoOrcamentarioFacade grupoOrcamentarioFacade;
    @EJB
    private CotaOrcamentariaFacade cotaOrcamentariaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private ReprocessamentoCotaOrcamentariaFacade reprocessamentoCotaOrcamentariaFacade;


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public LiberacaoCotaOrcamentariaFacade() {
        super(LiberacaoCotaOrcamentaria.class);
    }


    public void salvarNovo(LiberacaoCotaOrcamentaria entity, List<CotaOrcamentaria> cotaOrcamentarias) {
        try {
            Integer mes = (Integer) DataUtil.getMes(entity.getDataLiberacao());
            BigDecimal valor = entity.getValor();
            movimentarCotaOrcamentariaPorMes(entity, cotaOrcamentarias, mes, valor);
            em.persist(entity);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void recacularPercentualCotaOrcamentaria(CotaOrcamentaria cotaOrcamentaria) {
        BigDecimal saldoGrupoOrc = BigDecimal.ZERO;
        saldoGrupoOrc = fonteDespesaORCFacade.recuperarSaldoAtualPorGrupoOrc(cotaOrcamentaria.getGrupoCotaORC().getGrupoOrcamentario());
        BigDecimal prog = cotaOrcamentaria.getValorProgramado();
        BigDecimal multiply = prog.multiply(new BigDecimal(100));
        cotaOrcamentaria.setPercentual(multiply.divide(saldoGrupoOrc, 2, RoundingMode.HALF_UP));
    }

    private void movimentarCotaOrcamentariaPorMes(LiberacaoCotaOrcamentaria entity, List<CotaOrcamentaria> cotaOrcamentarias, Integer mes, BigDecimal valor) {
        for (CotaOrcamentaria cotaOrcamentaria : cotaOrcamentarias) {

            if (cotaOrcamentaria.getIndice().equals(mes)) {
                if (entity.getOperacao().equals(OperacaoFormula.ADICAO)) {
                    cotaOrcamentaria.setValorProgramado(cotaOrcamentaria.getValorProgramado().add(valor));
                    recacularPercentualCotaOrcamentaria(cotaOrcamentaria);
                } else {
                    cotaOrcamentaria.setValorProgramado(cotaOrcamentaria.getValorProgramado().subtract(valor));
                    recacularPercentualCotaOrcamentaria(cotaOrcamentaria);
                }

                cotaOrcamentariaFacade.salvar(cotaOrcamentaria);
                salvarNotificaoLiberacaoCotaORC(entity, cotaOrcamentaria);
            }
        }
    }

    public void salvarNotificaoLiberacaoCotaORC(LiberacaoCotaOrcamentaria entity, CotaOrcamentaria cotaOrcamentaria) {
        if (entity.getUnidadeOrganizacional() != null) {
            Notificacao notificacao = new Notificacao();
            notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
            notificacao.setTipoNotificacao(TipoNotificacao.COTA_ORCAMENTARIA);
            notificacao.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            if (entity.getOperacao().equals(OperacaoFormula.ADICAO)) {
                notificacao.setDescricao("Cota Orçamentária Liberada no valor de " + Util.formataValor(entity.getValor()) + " para a unidade " + entity.getUnidadeOrganizacional() + " na data " + DataUtil.getDataFormatada(entity.getDataLiberacao()));
                notificacao.setTitulo("Cota Liberada");
            } else {
                notificacao.setDescricao("Cota Orçamentária Removida no valor de " + Util.formataValor(entity.getValor()) + " para a unidade " + entity.getUnidadeOrganizacional() + " na data " + DataUtil.getDataFormatada(entity.getDataLiberacao()));
                notificacao.setTitulo("Cota Removida");
            }
            notificacao.setLink("/cota-orcamentaria/ver/" + cotaOrcamentaria.getGrupoCotaORC().getId() + "/");
            NotificacaoService.getService().notificar(notificacao);
        }
    }

    public GrupoOrcamentarioFacade getGrupoOrcamentarioFacade() {
        return grupoOrcamentarioFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public CotaOrcamentariaFacade getCotaOrcamentariaFacade() {
        return cotaOrcamentariaFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public ReprocessamentoCotaOrcamentariaFacade getReprocessamentoCotaOrcamentariaFacade() {
        return reprocessamentoCotaOrcamentariaFacade;
    }

    public void setReprocessamentoCotaOrcamentariaFacade(ReprocessamentoCotaOrcamentariaFacade reprocessamentoCotaOrcamentariaFacade) {
        this.reprocessamentoCotaOrcamentariaFacade = reprocessamentoCotaOrcamentariaFacade;
    }
}
