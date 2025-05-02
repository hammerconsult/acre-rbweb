/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.util.FacesUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * @author fabio
 */
@Stateless
public class CalculoDoctoOficialFacade {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private GeraValorDividaDocumentoOficial geraDebito;

    protected EntityManager getEntityManager() {
        return em;
    }

    public void salvar(CalculoDoctoOficial calculo, TipoCadastroDoctoOficial tipoCadastroDoctoOficial) {
        try {
            ProcessoCalculoDoctoOfc processoCalculoDoctoOfc = new ProcessoCalculoDoctoOfc();
            processoCalculoDoctoOfc.setExercicio(calculo.getExercicio());
            processoCalculoDoctoOfc.setDescricao(calculo.getSolicitacaoDoctoOficial().getTipoDoctoOficial().getDescricao());
            processoCalculoDoctoOfc.setDataLancamento(calculo.getDataCalculo());
            if (TipoCadastroDoctoOficial.CADASTROECONOMICO.equals(tipoCadastroDoctoOficial)) {
                processoCalculoDoctoOfc.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaDoctoOfcMobiliario());
            } else if (TipoCadastroDoctoOficial.CADASTROIMOBILIARIO.equals(tipoCadastroDoctoOficial)) {
                processoCalculoDoctoOfc.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaDoctoOfcImobiliario());
            } else if (TipoCadastroDoctoOficial.CADASTRORURAL.equals(tipoCadastroDoctoOficial)) {
                processoCalculoDoctoOfc.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaDoctoOfcRural());
            } else {
                processoCalculoDoctoOfc.setDivida(configuracaoTributarioFacade.retornaUltimo().getDividaDoctoOficial());
            }
            processoCalculoDoctoOfc.getCalculosDoctoOficiais().add(calculo);
            calculo.setProcessoCalculoDoctoOfc(processoCalculoDoctoOfc);


            em.persist(processoCalculoDoctoOfc);
            em.persist(calculo);

            geraDebito.geraDebito(calculo.getProcessoCalculoDoctoOfc());

        } catch (Exception e) {
            FacesUtil.addError("Ocorreu um erro", e.getMessage());
        }
    }

    public ValorDivida retornaValorDividaDoCalculo(Calculo calculo) {
        Query q = em.createQuery("from ValorDivida vd where vd.calculo = :calculo");
        q.setParameter("calculo", calculo);
        List<ValorDivida> resultado = q.getResultList();
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            ValorDivida valorDivida = (ValorDivida) resultado.get(0);
            valorDivida.getParcelaValorDividas().size();
            return valorDivida;
        }
    }

    public CalculoDoctoOficial recuperaCalculo(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        String hql = "from CalculoDoctoOficial where solicitacaoDoctoOficial = :solicitacao";
        Query q = em.createQuery(hql);
        q.setParameter("solicitacao", solicitacaoDoctoOficial);
        List<CalculoDoctoOficial> resultado = q.getResultList();
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return resultado.get(0);
        }
    }

    public boolean parcelaPaga(SolicitacaoDoctoOficial solicitacao) {
        CalculoDoctoOficial calculoDoctoOficial = recuperaCalculo(solicitacao);
        if (calculoDoctoOficial == null) {
            return false;
        }
        List<ResultadoParcela> resultadoConsulta = getResultadoParcelas(calculoDoctoOficial, SituacaoParcela.PAGO);
        return !resultadoConsulta.isEmpty();
    }

    public boolean parcelaEmAberto(SolicitacaoDoctoOficial solicitacao) {
        CalculoDoctoOficial calculoDoctoOficial = recuperaCalculo(solicitacao);
        if (calculoDoctoOficial == null) {
            return false;
        }
        List<ResultadoParcela> resultadoConsulta = getResultadoParcelas(calculoDoctoOficial, SituacaoParcela.EM_ABERTO);
        return !resultadoConsulta.isEmpty();
    }


    public boolean parcelaCancelada(SolicitacaoDoctoOficial solicitacao) {
        CalculoDoctoOficial calculoDoctoOficial = recuperaCalculo(solicitacao);
        if (calculoDoctoOficial == null) {
            return false;
        }
        List<ResultadoParcela> resultadoConsulta = getResultadoParcelas(calculoDoctoOficial, SituacaoParcela.CANCELAMENTO);
        return !resultadoConsulta.isEmpty();
    }

    private List<ResultadoParcela> getResultadoParcelas(CalculoDoctoOficial calculoDoctoOficial, SituacaoParcela situacaoParcela) {
        List<ResultadoParcela> resultadoConsulta = new ArrayList<>();
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_SITUACAO, ConsultaParcela.Operador.IGUAL, situacaoParcela);
        consulta.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, calculoDoctoOficial.getId());
        consulta.executaConsulta();
        resultadoConsulta.addAll(consulta.getResultados());
        return resultadoConsulta;
    }
}
