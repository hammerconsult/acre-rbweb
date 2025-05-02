package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssociacaoDocumentoLiquidacaoEntradaPorCompra;
import br.com.webpublico.entidadesauxiliares.DocumentoFiscalIntegracaoGrupoItem;
import br.com.webpublico.entidadesauxiliares.VOLiquidacaoDocumentoFiscal;
import br.com.webpublico.enums.SituacaoEntradaMaterial;
import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class AssociacaoDocumentoLiquidacaoEntradaCompraFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DoctoFiscalLiquidacaoFacade doctoFiscalLiquidacaoFacade;
    @EJB
    private LocalEstoqueFacade localEstoqueFacade;

    public AssociacaoDocumentoLiquidacaoEntradaPorCompra salvarAssociacao(AssociacaoDocumentoLiquidacaoEntradaPorCompra selecionado) {
        if (selecionado.isAssociarDocumento()) {
            DoctoFiscalEntradaCompra doctoEntrada = selecionado.getDocumentoFiscalEntrada();
            DoctoFiscalLiquidacao documentoLiquidacao = selecionado.getDocumentosFiscaisLiquidacao().get(0).getLiquidacaoDoctoFiscal().getDoctoFiscalLiquidacao();
            DoctoFiscalLiquidacao documentoEntrada = doctoEntrada.getDoctoFiscalLiquidacao();

            distribuirValorLiquidadoItenDoctoItemEntrada(documentoEntrada, selecionado.getValorTotalLiquidado());
            atribuirDocumentoLiquidacaoNaEntradaPorCompra(selecionado.getValorTotalLiquidado(), doctoEntrada, documentoLiquidacao);
            adicionarItensDocumentoNoDoctoFiscalLiquidacao(documentoEntrada, documentoLiquidacao);
            deletarDocumentoEntrada(documentoEntrada);
            atribuirSituacaoLiquidadaEntradaPorCompra(doctoEntrada);
        } else if (selecionado.isDesbloquearGrupo()) {
            selecionado.getItensDocumentoEntrada().forEach(item -> em.merge(item));
            atribuirDocumentoLiquidacaoNaEntradaPorCompra(selecionado.getValorTotalLiquidadoDocumentoFiscalLiquidacao(), selecionado.getDocumentoFiscalEntrada(), selecionado.getDocumentoFiscalEntrada().getDoctoFiscalLiquidacao());
        } else if (selecionado.isGerarNumeroSituacao()) {
            gerarNumeroSituacaoEntradaCompra(selecionado, selecionado.getEntradaCompraMaterial());
            em.merge(selecionado.getEntradaCompraMaterial());
        }
        return selecionado;
    }

    private void gerarNumeroSituacaoEntradaCompra(AssociacaoDocumentoLiquidacaoEntradaPorCompra selecionado, EntradaCompraMaterial entrada) {
        boolean isTodosDoctosLiquidados = selecionado.getDocumentosFiscaisEntrada().stream().allMatch(doc -> doc.getSituacao().isLiquidado());
        if (isTodosDoctosLiquidados) {
            entrada.setSituacao(SituacaoEntradaMaterial.ATESTO_DEFINITIVO_LIQUIDADO);
            if (entrada.getNumero() == null) {
                entradaMaterialFacade.gerarNumeroEntrada(entrada);
            }
            if (entrada.getDataConclusao() == null) {
                Date ultimaDataLiquidacaoDocto = entradaMaterialFacade.buscarUltimaDataLiquidacaoDocto(entrada);
                entrada.setDataConclusao(ultimaDataLiquidacaoDocto != null ? ultimaDataLiquidacaoDocto : new Date());
            }
        }
    }

    private void atribuirSituacaoLiquidadaEntradaPorCompra(DoctoFiscalEntradaCompra doctoEntrada) {
        EntradaCompraMaterial entrada = (EntradaCompraMaterial) entradaMaterialFacade.recuperarEntradaComDependenciasDocumentos(doctoEntrada.getEntradaCompraMaterial().getId());
        BigDecimal valorTotalLiquidadoDocumentoLiquidacao = doctoFiscalLiquidacaoFacade.getValorLiquidadoEntradaPorCompra(entrada);

        BigDecimal valorTotalDocumento = entrada.getValorTotalDocumento();
        if (valorTotalDocumento.compareTo(valorTotalLiquidadoDocumentoLiquidacao) == 0) {
            entrada.setSituacao(SituacaoEntradaMaterial.ATESTO_DEFINITIVO_LIQUIDADO);
            em.merge(entrada);
        }

    }

    public void distribuirValorLiquidadoItenDoctoItemEntrada(DoctoFiscalLiquidacao doctoFiscal, BigDecimal valorDocumento) {
        List<ItemDoctoItemEntrada> itens = entradaMaterialFacade.buscarItemDoctoItemEntrada(doctoFiscal, null);
        for (ItemDoctoItemEntrada item : itens) {
            item.setValorLiquidado(BigDecimal.ZERO);
            atribuirValorProporcionalItemDoctoItemEntrada(valorDocumento, item);
            em.merge(item);
        }
    }

    public void distribuirValorLiquidadoItenDoctoItemEntrada(AssociacaoDocumentoLiquidacaoEntradaPorCompra selecionado) {
        for (ItemDoctoItemEntrada item : selecionado.getItensDocumentoEntrada()) {
            item.setValorLiquidado(BigDecimal.ZERO);
            atribuirValorProporcionalItemDoctoItemEntrada(selecionado.getValorTotalLiquidadoDocumentoFiscalLiquidacao(), item);
        }
    }

    public void atribuirValorProporcionalItemDoctoItemEntrada(BigDecimal valorLiquidado, ItemDoctoItemEntrada item) {
            BigDecimal valorTotalDocumento = item.getDoctoFiscalEntradaCompra().getDoctoFiscalLiquidacao().getValor();
            BigDecimal valorProporcionalAoItem = DocumentoFiscalIntegracaoGrupoItem.getValorProporcionalAoItem(valorTotalDocumento, valorLiquidado, item.getValorTotal());
            item.setValorLiquidado(item.getValorLiquidado() != null
                ? item.getValorLiquidado().add(valorProporcionalAoItem)
                : valorProporcionalAoItem);

            boolean isLiquidado = item.getValorLiquidado().compareTo(item.getItemEntradaMaterial().getValorTotal()) == 0;
            item.setSituacao(isLiquidado ? SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO : SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO_PARCIALMENTE);
    }

    private void adicionarItensDocumentoNoDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoEntrada, DoctoFiscalLiquidacao doctoLiquidacao) {
        List<ItemDoctoFiscalLiquidacao> itensDocumento = doctoFiscalLiquidacaoFacade.buscarItensDocumento(doctoEntrada);
        if (!Util.isListNullOrEmpty(itensDocumento)) {
            for (ItemDoctoFiscalLiquidacao itemDoctoEntrada : itensDocumento) {
                String sql = " update itemdoctofiscalliquidacao set doctofiscalliquidacao_id = :idDoctoLiquidacao where id = :idItem ";
                Query q = em.createNativeQuery(sql);
                q.setParameter("idItem", itemDoctoEntrada.getId());
                q.setParameter("idDoctoLiquidacao", doctoLiquidacao.getId());
                q.executeUpdate();
            }
        }
    }

    private void deletarDocumentoEntrada(DoctoFiscalLiquidacao doctoLiquidacao) {
        String sql = " delete from doctofiscalliquidacao where id = :idDocumento";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumento", doctoLiquidacao.getId());
        q.executeUpdate();
    }

    public void atribuirDocumentoLiquidacaoNaEntradaPorCompra(BigDecimal valorTotalLiquidado, DoctoFiscalEntradaCompra doctoEnt, DoctoFiscalLiquidacao documentoLiquidacao) {
        boolean isLiquidadoTotal = valorTotalLiquidado.compareTo(doctoEnt.getDoctoFiscalLiquidacao().getValor()) == 0;
        SituacaoDocumentoFiscalEntradaMaterial situacao = isLiquidadoTotal
            ? SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO
            : SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO_PARCIALMENTE;

        String sql = " update doctofiscalentradacompra set doctofiscalliquidacao_id = :idDoctoLiquidacao, situacao = :situacao where id = :idDocto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDoctoLiquidacao", documentoLiquidacao.getId());
        q.setParameter("situacao", situacao.name());
        q.setParameter("idDocto", doctoEnt.getId());
        q.executeUpdate();
    }

    public List<VOLiquidacaoDocumentoFiscal> buscarDocumentoFiscalNaoIntegradoComLiquidacao(String condicao) {
        String sql = "" +
            "  select ldf.* from doctofiscalliquidacao docto " +
            "  inner join liquidacaodoctofiscal ldf on ldf.doctofiscalliquidacao_id = docto.id " +
            "  inner join liquidacao liq on liq.id = ldf.liquidacao_id " +
            "  inner join empenho emp on emp.id = liq.empenho_id  " + condicao;
        Query q = em.createNativeQuery(sql, LiquidacaoDoctoFiscal.class);
        List<VOLiquidacaoDocumentoFiscal> toReturn = Lists.newArrayList();
        List<LiquidacaoDoctoFiscal> documentos = q.getResultList();
        for (LiquidacaoDoctoFiscal docLiquidacao : documentos) {
            VOLiquidacaoDocumentoFiscal documento = new VOLiquidacaoDocumentoFiscal();
            documento.setLiquidacaoDoctoFiscal(docLiquidacao);
            documento.setSelecionado(false);
            toReturn.add(documento);
        }
        return toReturn;
    }

    public LiquidacaoFacade getLiquidacaoFacade() {
        return liquidacaoFacade;
    }

    public EntradaMaterialFacade getEntradaMaterialFacade() {
        return entradaMaterialFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public DoctoFiscalLiquidacaoFacade getDoctoFiscalLiquidacaoFacade() {
        return doctoFiscalLiquidacaoFacade;
    }

    public LocalEstoqueFacade getLocalEstoqueFacade() {
        return localEstoqueFacade;
    }
}
