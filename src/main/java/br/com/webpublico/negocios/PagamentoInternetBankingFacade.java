package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.entidades.PagamentoInternetBanking;
import br.com.webpublico.tributario.consultadebitos.dtos.ItemPagamentoDTO;
import br.com.webpublico.tributario.consultadebitos.dtos.PagamentoDTO;

import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Stateless
public class PagamentoInternetBankingFacade extends AbstractFacade<PagamentoInternetBanking> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private LoteBaixaFacade loteBaixaFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;

    public PagamentoInternetBankingFacade() {
        super(PagamentoInternetBanking.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void registrarPagamento(PagamentoDTO pagamentoDTO) throws Exception {
        for (ItemPagamentoDTO itemPagamentoDTO : pagamentoDTO.getItens()) {
            DAM dam = loteBaixaFacade.recuperaDamPorCodigoBarrasSemDigitoVerificador(itemPagamentoDTO.getLinhaDigitavel());
            if (dam == null) {
                throw new ExcecaoNegocioGenerica("DAM não encontrado com o código de barras " + itemPagamentoDTO.getLinhaDigitavel());
            }
            PagamentoInternetBanking pagamentoInternetBanking = new PagamentoInternetBanking();
            pagamentoInternetBanking.setDam(dam);
            pagamentoInternetBanking.setDataPagamento(pagamentoDTO.getDataPagamento());
            pagamentoInternetBanking.setDataCredito(pagamentoDTO.getDataCredito());
            pagamentoInternetBanking.setCodigoBanco(pagamentoDTO.getCodigoBanco());
            pagamentoInternetBanking.setCodigoAgencia(pagamentoDTO.getCodigoAgencia());
            pagamentoInternetBanking.setCodigoConta(pagamentoDTO.getCodigoConta());
            pagamentoInternetBanking.setAutenticacao(itemPagamentoDTO.getAutenticacao());
            pagamentoInternetBanking.setValorPago(itemPagamentoDTO.getValorPago());
            em.persist(pagamentoInternetBanking);
            consultaDebitoFacade.baixarParcelasDoDam(dam);
        }
    }

    public PagamentoInternetBanking buscarPagamentoPorDam(DAM dam) {
        List resultList = em.createNativeQuery("select * " +
                "  from pagamentointernetbanking pib " +
                " where pib.dam_id = :idDam " +
                " order by pib.id desc " +
                " fetch first 1 row only ", PagamentoInternetBanking.class)
            .setParameter("idDam", dam.getId())
            .getResultList();
        return !resultList.isEmpty() ? (PagamentoInternetBanking) resultList.get(0) : null;
    }
}
