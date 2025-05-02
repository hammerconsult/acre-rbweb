package br.com.webpublico.negocios;

import br.com.webpublico.entidades.ConciliacaoBancaria;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Buzatto
 * Date: 24/09/13
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class ConciliacaoBancariaFacade extends AbstractFacade<ConciliacaoBancaria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private ConfigReceitaRealizadaFacade receitaRealizadaFacade;
    @EJB
    private RelatorioPesquisaGenericoFacade relatorioPesquisaGenericoFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private PagamentoEstornoFacade pagamentoEstornoFacade;
    @EJB
    private PagamentoExtraFacade pagamentoExtraFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private LiberacaoCotaFinanceiraFacade liberacaoCotaFinanceiraFacade;
    @EJB
    private TransferenciaContaFinanceiraFacade transferenciaContaFinanceiraFacade;
    @EJB
    private TransferenciaMesmaUnidadeFacade transferenciaMesmaUnidadeFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;

    public ConciliacaoBancariaFacade() {
        super(ConciliacaoBancaria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ConfigReceitaRealizadaFacade getReceitaRealizadaFacade() {
        return receitaRealizadaFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public RelatorioPesquisaGenericoFacade getRelatorioPesquisaGenericoFacade() {
        return relatorioPesquisaGenericoFacade;
    }

    public void salvarObjeto(ConciliacaoBancaria selecionado, List listaObjetos) {
        String campo = selecionado.getCampoDataConcilicao();
        for (Object obj : listaObjetos) {
            for (Field field : Persistencia.getAtributos(obj.getClass())) {
                field.setAccessible(true);
                if (field.getName().equals(campo)) {
                    try {
                        if (selecionado.getTipoLancamento().equals(TipoLancamento.NORMAL)) {
                            field.set(obj, selecionado.getDataConciliacao());
                        } else {
                            field.set(obj, null);
                        }
                    } catch (Exception e) {
                    }
                }
            }
            em.merge(obj);
        }
    }

    public void validarObjetos(ConciliacaoBancaria selecionado, List objetos) {
        ValidacaoException ve = new ValidacaoException();
        percorrerCamposEValidar(selecionado, objetos, ve, (selecionado.getTipoLancamento().isNormal() ? selecionado.getCampoDataMovimento() : selecionado.getCampoDataConcilicao()), selecionado.getCampoUnidadeMovimento());
        ve.lancarException();
    }

    private void percorrerCamposEValidar(ConciliacaoBancaria selecionado, List objetos, ValidacaoException ve, String campoData, String campoUnidade) {
        for (Object obj : objetos) {
            Date dataMovimento = null;
            UnidadeOrganizacional unidadeOrganizacional = null;
            for (Field field : Persistencia.getAtributos(obj.getClass())) {
                field.setAccessible(true);
                try {
                    if (field.getName().equals(campoData)) {
                        dataMovimento = (Date) field.get(obj);
                    } else if (field.getName().equals(campoUnidade)) {
                        unidadeOrganizacional = (UnidadeOrganizacional) field.get(obj);
                    }
                } catch (Exception e) {
                }
            }
            if (selecionado.getTipoLancamento().isNormal()) {
                validarBaixar(ve, selecionado.getDataConciliacao(), dataMovimento, unidadeOrganizacional);
            } else {
                validarEstorno(ve, dataMovimento, selecionado, unidadeOrganizacional);
            }
        }
    }

    private void validarBaixar(ValidacaoException ve, Date dataConciliacao, Date dataMovimento, UnidadeOrganizacional unidadeOrganizacional) {
        if (dataConciliacao != null && Util.getDataHoraMinutoSegundoZerado(dataConciliacao).before(Util.getDataHoraMinutoSegundoZerado(dataMovimento))) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A Data de Conciliação deve ser maior ou igual a data do movimento (" + DataUtil.getDataFormatada(dataMovimento) + ").");
        }
        validarFase(ve, dataConciliacao, unidadeOrganizacional);
    }


    private void validarFase(ValidacaoException ve, Date dataConciliacao, UnidadeOrganizacional unidadeOrganizacional) {
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(DataUtil.getAno(dataConciliacao));
        if (faseFacade.temBloqueioFaseParaRecurso("/financeiro/conciliacao/novaconciliacaobancaria/lista.xhtml", dataConciliacao, unidadeOrganizacional, exercicio)) {
            HierarquiaOrganizacional hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataConciliacao, unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
            ve.adicionarMensagemDeOperacaoNaoRealizada("A data " + DataUtil.getDataFormatada(dataConciliacao) + " está fora do período fase para a unidade " + hierarquiaOrganizacional.toString() + "!");
        }
    }

    private void validarEstorno(ValidacaoException ve, Date dataMovimento, ConciliacaoBancaria selecionado, UnidadeOrganizacional unidadeOrganizacional) {
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(DataUtil.getAno(dataMovimento));
        if (selecionado.getTipoLancamento().isEstorno() &&
            faseFacade.temBloqueioFaseParaRecurso("/financeiro/conciliacao/novaconciliacaobancaria/lista.xhtml", dataMovimento, unidadeOrganizacional, exercicio)) {
            HierarquiaOrganizacional hierarquiaOrganizacional = hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataMovimento, unidadeOrganizacional, TipoHierarquiaOrganizacional.ORCAMENTARIA);
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data " + DataUtil.getDataFormatada(dataMovimento) + " está fora do período fase para a unidade " + hierarquiaOrganizacional.toString() + "!");
        }
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
    }

    public PagamentoEstornoFacade getPagamentoEstornoFacade() {
        return pagamentoEstornoFacade;
    }

    public PagamentoExtraFacade getPagamentoExtraFacade() {
        return pagamentoExtraFacade;
    }

    public ReceitaExtraFacade getReceitaExtraFacade() {
        return receitaExtraFacade;
    }

    public LancamentoReceitaOrcFacade getLancamentoReceitaOrcFacade() {
        return lancamentoReceitaOrcFacade;
    }

    public LiberacaoCotaFinanceiraFacade getLiberacaoCotaFinanceiraFacade() {
        return liberacaoCotaFinanceiraFacade;
    }

    public TransferenciaContaFinanceiraFacade getTransferenciaContaFinanceiraFacade() {
        return transferenciaContaFinanceiraFacade;
    }

    public TransferenciaMesmaUnidadeFacade getTransferenciaMesmaUnidadeFacade() {
        return transferenciaMesmaUnidadeFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }
}
