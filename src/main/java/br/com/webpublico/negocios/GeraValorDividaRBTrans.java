package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Calendar;
import java.util.List;

@Stateless
public class GeraValorDividaRBTrans extends ValorDividaFacade {

    @EJB
    private ParametrosTransitoRBTransFacade parametrosTransitoRBTransFacade;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;

    public GeraValorDividaRBTrans() {
        super(false);
    }

    @Override
    protected void lancaItem(ValorDivida valorDivida) {
        for (ItemCalculoRBTrans item : ((CalculoRBTrans) valorDivida.getCalculo()).getItensCalculo()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(valorDivida.getValor());
            itemValorDivida.setValorDivida(valorDivida);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @Override
    protected void defineVencimentosParcelas(Calculo calculo, OpcaoPagamento op, int i, ParcelaValorDivida parcelaValorDivida, ValorDivida valorDivida, Calendar vencimento, Integer numeroParcelas) {
        CalculoRBTrans calculoRBTrans = (CalculoRBTrans) calculo;
        if (calculoRBTrans.getVencimento() != null) {
            parcelaValorDivida.setVencimento(calculoRBTrans.getVencimento());
        } else {
            parcelaValorDivida.setVencimento(permissaoTransporteFacade.recuperarUltimoDiaUtilDoMesCorrente());
        }

    }

    @Override
    public void salvaValorDivida(ValorDivida valordivida) throws Exception {
        valordivida = em.merge(valordivida);
        em.flush();
//        ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
//        ServiceIntegracaoTributarioContabil service = (ServiceIntegracaoTributarioContabil) ap.getBean("serviceIntegracaoTributarioContabil");
//        service.criarItemIntegracaoValorDivida(valordivida, TipoIntegracao.INSCRICAO);
    }

    public void geraDam(CalculoRBTrans calculo) {
        List<ValorDivida> valoresDivida = em.createQuery("from ValorDivida  vd where vd.calculo = :calculo")
            .setParameter("calculo", calculo)
            .getResultList();
        for (ValorDivida valorDivida : valoresDivida) {
            try {
                damFacade.geraDAM(valorDivida);
            } catch (Exception e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }
}
