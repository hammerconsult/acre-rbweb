package br.com.webpublico.relatoriofacade;

import br.com.webpublico.entidadesauxiliares.ConciliacaoBancariaConstante;
import br.com.webpublico.entidadesauxiliares.ConciliacaoBancariaItem;
import br.com.webpublico.entidadesauxiliares.ConciliacaoBancariaMovimentos;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.negocios.ContaBancariaEntidadeFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by mateus on 04/08/17.
 */
@Stateless
public abstract class AbstractRelatorioConciliacaoBancariaFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;

    public abstract ConciliacaoBancariaItem gerarConsultaSaldo(Boolean pesquisouUg, List<ParametrosRelatorios> parametros, String dataSaldoAnterior, HashMap parametrosItens, ConciliacaoBancariaItem item);

    public abstract List<ConciliacaoBancariaConstante> gerarConsultaSubReportSaldoConstante(Boolean pesquisouUg, List<ParametrosRelatorios> parametros, String mesReferencia);

    public abstract List<ConciliacaoBancariaItem> buscarRelatorio(List<ParametrosRelatorios> parametros, Boolean pesquisouUg, String dataSaldoAnterior, Boolean movimentosDiferenteZero);

    public abstract List<ConciliacaoBancariaMovimentos> gerarConsultaMovimentos(Boolean pesquisouUg, List<ParametrosRelatorios> parametros, HashMap parametrosItens, Boolean mostrarMovimentoIgualZero);

    public Boolean hasValorDiferenteDeZero(List<ConciliacaoBancariaMovimentos> itens) {
        for (ConciliacaoBancariaMovimentos item : itens) {
            if ((item.getCredito().compareTo(BigDecimal.ZERO) != 0) ||
                (item.getDebito().compareTo(BigDecimal.ZERO) != 0)) {
                return true;
            }
        }
        return false;
    }

    public EntityManager getEm() {
        return em;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }
}
