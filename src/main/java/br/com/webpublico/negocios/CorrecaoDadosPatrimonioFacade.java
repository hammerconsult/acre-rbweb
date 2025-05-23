package br.com.webpublico.negocios;

import br.com.webpublico.entidades.DoctoFiscalEntradaCompra;
import br.com.webpublico.entidades.DetentorDocto;
import br.com.webpublico.entidades.EfetivacaoMaterial;
import br.com.webpublico.entidades.ItemDoctoItemEntrada;
import br.com.webpublico.entidades.Material;
import br.com.webpublico.entidadesauxiliares.CorrecaoDadosAdministrativoVO;
import br.com.webpublico.entidadesauxiliares.ItemDoctoItemEntradaVO;
import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import br.com.webpublico.singletons.SingletonConcorrenciaMaterial;
import br.com.webpublico.singletons.SingletonContrato;
import br.com.webpublico.util.AssistenteBarraProgresso;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class CorrecaoDadosPatrimonioFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LoteEfetivacaoTransferenciaBemFacade loteEfetivacaoTransferenciaBemFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;
    @EJB
    private SingletonContrato singletonContrato;
    @EJB
    private SingletonConcorrenciaMaterial singletonConcorrenciaMaterial;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AssociacaoDocumentoLiquidacaoEntradaCompraFacade associacaoDocumentoLiquidacaoEntradaCompraFacade;
    @EJB
    private MaterialFacade materialFacade;

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<CorrecaoDadosAdministrativoVO> buscarDocumentosFiscaisAndItens(CorrecaoDadosAdministrativoVO selecionado) {
        AssistenteBarraProgresso assistente = selecionado.getAssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Atribuindo valor liquidado aos itens documento fiscal entrada...");

        String sql = " select item.id from ItemDoctoItemEntrada item " +
            "          where item.situacao = :situacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("situacao", SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO_PARCIALMENTE.name());
        List<BigDecimal> resultado = q.getResultList();

        assistente.setTotal(resultado.size());
        for (BigDecimal id : resultado) {
            ItemDoctoItemEntrada item = em.find(ItemDoctoItemEntrada.class, ((BigDecimal) id).longValue());
            item.setValorLiquidado(BigDecimal.ZERO);
            ItemDoctoItemEntradaVO itemVO = new ItemDoctoItemEntradaVO();
            itemVO.setItemDoctoItemEntrada(item);
            itemVO.setValorLiquidadoDocumento(associacaoDocumentoLiquidacaoEntradaCompraFacade.getDoctoFiscalLiquidacaoFacade().buscarValorLiquidado(item.getDoctoFiscalEntradaCompra().getDoctoFiscalLiquidacao()));
            associacaoDocumentoLiquidacaoEntradaCompraFacade.atribuirValorProporcionalItemDoctoItemEntrada(itemVO.getValorLiquidadoDocumento(), item);

            DoctoFiscalEntradaCompra doctoEnt = associacaoDocumentoLiquidacaoEntradaCompraFacade.getEntradaMaterialFacade().recuperarDoctoFiscalEntradaCompra(item.getDoctoFiscalEntradaCompra().getDoctoFiscalLiquidacao());
            if (doctoEnt != null) {
                associacaoDocumentoLiquidacaoEntradaCompraFacade.atribuirDocumentoLiquidacaoNaEntradaPorCompra(itemVO.getValorLiquidadoDocumento(), doctoEnt, doctoEnt.getDoctoFiscalLiquidacao());
            }
            selecionado.getItensDocumentoEntrada().add(itemVO);
            assistente.conta();
        }
        return new AsyncResult<CorrecaoDadosAdministrativoVO>(selecionado);
    }
    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<CorrecaoDadosAdministrativoVO> inserirEfetivacaoMaterial(CorrecaoDadosAdministrativoVO selecionado) {
        AssistenteBarraProgresso assistente = selecionado.getAssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Inserir Efetivação de Material...");

        List<Material> materiais = materialFacade.buscarTodosMateriais();
        assistente.setTotal(materiais.size());
        for (Material mat : materiais) {
            EfetivacaoMaterial efetivacaoMatNova;

            EfetivacaoMaterial efetivacaoMaterialRecuperada = materialFacade.recuperarEfetivacao(mat);
            if (efetivacaoMaterialRecuperada != null) {
                efetivacaoMatNova = new EfetivacaoMaterial();
                efetivacaoMatNova.setMaterial(mat);
                efetivacaoMatNova.setDataRegistro(efetivacaoMaterialRecuperada.getDataRegistro());
                efetivacaoMatNova.setUsuarioSistema(efetivacaoMaterialRecuperada.getUsuarioSistema());
                efetivacaoMatNova.setSituacao(mat.getStatusMaterial());
                efetivacaoMatNova.setObservacao(efetivacaoMaterialRecuperada.getObservacao());
            } else {
                efetivacaoMatNova = novaEfetivacaoMaterial(mat, selecionado);
            }
            em.merge(efetivacaoMatNova);
            assistente.conta();
        }
        return new AsyncResult<CorrecaoDadosAdministrativoVO>(selecionado);
    }

    public EfetivacaoMaterial novaEfetivacaoMaterial(Material material, CorrecaoDadosAdministrativoVO selecionado) {
        EfetivacaoMaterial efetivacaoMaterial = new EfetivacaoMaterial();
        efetivacaoMaterial.setMaterial(material);
        efetivacaoMaterial.setDataRegistro(new Date());
        efetivacaoMaterial.setUsuarioSistema(selecionado.getUsuarioSistema());
        efetivacaoMaterial.setUnidadeAdministrativa(selecionado.getUnidadeOrganizacional());
        efetivacaoMaterial.setSituacao(material.getStatusMaterial());
        return efetivacaoMaterial;
    }

    public LoteEfetivacaoTransferenciaBemFacade getLoteEfetivacaoTransferenciaBemFacade() {
        return loteEfetivacaoTransferenciaBemFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SingletonGeradorCodigoPatrimonio getSingletonGeradorCodigoPatrimonio() {
        return singletonGeradorCodigoPatrimonio;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }


    public SingletonContrato getSingletonContrato() {
        return singletonContrato;
    }

    public SingletonConcorrenciaMaterial getSingletonConcorrenciaMaterial() {
        return singletonConcorrenciaMaterial;
    }
}
