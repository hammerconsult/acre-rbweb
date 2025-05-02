package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DesbloqueioBemAquisicaoDoctoAquisicao;
import br.com.webpublico.entidadesauxiliares.DesbloqueioBemAquisicaoDoctoLiquidacaoMov;
import br.com.webpublico.entidadesauxiliares.DesbloqueioBemAquisicaoLiquidado;
import br.com.webpublico.entidadesauxiliares.ItemDoctoFiscalAquisicao;
import br.com.webpublico.enums.CategoriaOrcamentaria;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoLancamento;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Stateless
public class DesbloqueioBemAquisicaoLiquidadadoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private EfetivacaoAquisicaoFacade efetivacaoAquisicaoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private BemFacade bemFacade;

    public void salvarDesbloqueio(DesbloqueioBemAquisicaoLiquidado selecionado) {
        selecionado.getDocumentosAquisicao().stream().flatMap(doc -> doc.getItensDocumento().stream()).forEach(item -> {
            bemFacade.atualizarSituacaoEventoBem(item.getIdItemAquisicao(), SituacaoEventoBem.FINALIZADO);

            em.merge(item.getEstadoBem());

            item.getEstadosBem().forEach(est -> {
                est.setValorLiquidado(item.getEstadoBem().getValorLiquidado());
                em.merge(est);
            });
            em.merge(item.getBem());
        });
    }

    public List<DesbloqueioBemAquisicaoDoctoAquisicao> buscarDocumentos(Aquisicao aquisicao) {
        List<DoctoFiscalSolicitacaoAquisicao> documentosAquisicao = efetivacaoAquisicaoFacade.buscarDocumentoFiscalAquisicao(aquisicao);

        List<DesbloqueioBemAquisicaoDoctoAquisicao> documentos = Lists.newArrayList();
        for (DoctoFiscalSolicitacaoAquisicao doc : documentosAquisicao) {
            DesbloqueioBemAquisicaoDoctoAquisicao novoDoc = new DesbloqueioBemAquisicaoDoctoAquisicao();
            novoDoc.setDocumentoAquisicao(doc.getDocumentoFiscal());
            novoDoc.setItensDocumento(buscarItensDocumento(novoDoc.getDocumentoAquisicao(), aquisicao));

            novoDoc.setMovimentosDoctoLiquidacao(buscarMovimentoDoctoFiscal(doc.getDocumentoFiscal()));
            documentos.add(novoDoc);
        }
        return documentos;
    }

    public void gerarOrigemRecursoAndNotasEmpenho(DesbloqueioBemAquisicaoDoctoAquisicao docto) {
        Desdobramento desdobramento = liquidacaoFacade.buscarDesdobramentoPorDocumentoFiscal(docto.getDocumentoAquisicao());

        for (ItemDoctoFiscalAquisicao item : docto.getItensDocumento()) {
            if (desdobramento != null) {
                DetentorOrigemRecurso detentor;
                if (item.getBem().getDetentorOrigemRecurso() != null) {
                    item.getBem().getDetentorOrigemRecurso().setListaDeOriemRecursoBem(Lists.newArrayList());
                    detentor = item.getBem().getDetentorOrigemRecurso();
                } else {
                    detentor = new DetentorOrigemRecurso();
                }

                OrigemRecursoBem origem = new OrigemRecursoBem();
                origem.setDespesaOrcamentaria(desdobramento.getConta().toString());
                origem.setDescricao("Liquidação " + desdobramento.getLiquidacao().getCategoriaOrcamentaria().getDescricao());
                origem.setFonteDespesa(desdobramento.getLiquidacao().getEmpenho().getFonteDespesaORC().getDescricaoFonteDeRecurso());
                origem.setDetentorOrigemRecurso(detentor);
                Util.adicionarObjetoEmLista(detentor.getListaDeOriemRecursoBem(), origem);
                item.getBem().setDetentorOrigemRecurso(detentor);
            }
        }
    }

    public void preencherNotas(DesbloqueioBemAquisicaoDoctoAquisicao docto) {
        DoctoFiscalLiquidacao docNota = docto.getDocumentoAquisicao();
        Empenho empenho = buscarEmpenhoPorDocumentoFiscal(docto.getDocumentoAquisicao());


        for (ItemDoctoFiscalAquisicao item : docto.getItensDocumento()) {
            item.getBem().setNotasFiscais(Lists.newArrayList());
            BemNotaFiscal nota = new BemNotaFiscal();
            nota.setNumeroNotaFiscal(docNota.getNumero());
            nota.setDataNotaFiscal(docNota.getDataDocto());
            nota.setDoctoFiscalLiquidacao(docNota);
            nota.setBem(item.getBem());

            if (empenho != null) {
                BemNotaFiscalEmpenho notaEmpenho = new BemNotaFiscalEmpenho();
                notaEmpenho.setBemNotaFiscal(nota);
                notaEmpenho.setEmpenho(empenho);
                notaEmpenho.setNumeroEmpenho(empenho.getNumero());
                notaEmpenho.setDataEmpenho(empenho.getDataEmpenho());
                Util.adicionarObjetoEmLista(nota.getEmpenhos(), notaEmpenho);
            }
            Util.adicionarObjetoEmLista(item.getBem().getNotasFiscais(), nota);
        }
    }

    public List<ItemDoctoFiscalAquisicao> buscarItensDocumento(DoctoFiscalLiquidacao documento, Aquisicao aquisicao) {
        String sql = " " +
            " select item.id as id_item," +
            "        bem.id as id_bem,   " +
            "        est.id as id_estadoBem,     " +
            "        ev.situacaoeventobem " +
            "   from itemaquisicao item " +
            "        inner join eventobem ev on ev.id = item.id " +
            "        inner join bem on bem.id = ev.bem_id" +
            "        inner join estadobem est on est.id = ev.estadoresultante_id " +
            "        inner join grupobem gb on gb.id = est.grupobem_id " +
            "        inner join itemsolicitacaoaquisicao itemsol on itemsol.id = item.itemsolicitacaoaquisicao_id " +
            "        inner join itemdoctoitemaquisicao idia on idia.id = itemsol.itemdoctoitemaquisicao_id " +
            "        inner join itemdoctofiscalliquidacao itemdoc on itemdoc.id = idia.itemdoctofiscalliquidacao_id " +
            "   where item.aquisicao_id = :idAquisicao " +
            "       and itemdoc.doctofiscalliquidacao_id = :idDocumento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAquisicao", aquisicao.getId());
        q.setParameter("idDocumento", documento.getId());
        List<ItemDoctoFiscalAquisicao> itens = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            ItemDoctoFiscalAquisicao item = new ItemDoctoFiscalAquisicao();
            item.setIdItemAquisicao(((BigDecimal) obj[0]).longValue());
            Bem bem = bemFacade.recuperarBemComDependenciasNotasFicaisAndOrigemRecurso(((BigDecimal) obj[1]).longValue());
            Hibernate.initialize(bem.getMovimentosBloqueioBem());
            item.setBem(bem);
            item.setEstadoBem(bemFacade.recuperarEstadoBemPorId(((BigDecimal) obj[2]).longValue()));
            item.setSituacaoEventoBem(SituacaoEventoBem.valueOf((String) obj[3]));
            item.setMovimentoBloqueioBem(configMovimentacaoBemFacade.buscarUltimoMovimentoBloqueio(item.getBem().getId()));
            itens.add(item);
        }
        return itens;
    }

    public List<DesbloqueioBemAquisicaoDoctoLiquidacaoMov> buscarMovimentoDoctoFiscal(DoctoFiscalLiquidacao documento) {
        String sql = "" +
            " select id_mov," +
            "        numero_mov," +
            "        data_mov," +
            "        tipo_lancamento, " +
            "        categoria," +
            "        valor_liquidado " +
            " from ( " +
            "  select liq.id                           as id_mov, " +
            "         liq.numero                       as numero_mov, " +
            "         cast(liq.dataliquidacao as date) as data_mov, " +
            "         'NORMAL'                         as tipo_lancamento, " +
            "         liq.categoriaorcamentaria        as categoria, " +
            "         coalesce(ldf.valorliquidado, 0)  as valor_liquidado " +
            "   from liquidacaodoctofiscal ldf " +
            "         inner join liquidacao liq on liq.id = ldf.liquidacao_id " +
            "   where ldf.doctofiscalliquidacao_id = :idDocumento " +
            "  union all " +
            "  select le.id                        as id_mov, " +
            "         le.numero                    as numero_mov, " +
            "         cast(le.dataestorno as date) as data_mov, " +
            "         'ESTORNO'                    as tipo_lancamento, " +
            "         le.categoriaorcamentaria     as categoria, " +
            "         coalesce(ldf.valor, 0)       as valor_liquidado " +
            "   from liquidacaoestdoctofiscal ldf " +
            "         inner join liquidacaoestorno le on le.id = ldf.liquidacaoestorno_id " +
            "   where ldf.documentofiscal_id = :idDocumento " +
            " ) order by id_mov ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumento", documento.getId());
        List<DesbloqueioBemAquisicaoDoctoLiquidacaoMov> itens = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            DesbloqueioBemAquisicaoDoctoLiquidacaoMov item = new DesbloqueioBemAquisicaoDoctoLiquidacaoMov();
            item.setIdMov(((BigDecimal) obj[0]).longValue());
            item.setNumero((String) obj[1]);
            item.setData((Date) obj[2]);
            item.setTipoLancamento(TipoLancamento.valueOf((String) obj[3]));
            item.setCategoriaOrcamentaria(CategoriaOrcamentaria.valueOf((String) obj[4]));
            item.setValor((BigDecimal) obj[5]);
            item.setValorEstorno(getTotalEstornoLiquidacao(item.getIdMov()));
            item.setValorPago(getTotalPago(item.getIdMov()));
            itens.add(item);
        }
        return itens;
    }

    public BigDecimal getTotalEstornoLiquidacao(Long idMovimento) {
        String sql = "select sum(est.valor) " +
            " from liquidacao liq " +
            "    inner join liquidacaoestorno est on liq.id = est.liquidacao_id " +
            " where liq.id = :idMovimento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMovimento", idMovimento);
        if (q.getSingleResult() != null) {
            return (BigDecimal) q.getSingleResult();
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getTotalPago(Long idMovimento) {
        String sql = "select sum(p.valor) " +
            " from liquidacao liq " +
            "   inner join pagamento p on liq.id = p.liquidacao_id " +
            " where liq.id = :idMovimento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMovimento", idMovimento);
        if (q.getSingleResult() != null) {
            return (BigDecimal) q.getSingleResult();
        }
        return BigDecimal.ZERO;
    }

    public Empenho buscarEmpenhoPorDocumentoFiscal(DoctoFiscalLiquidacao docto) {
        String sql = "" +
            "  select emp.* from empenho emp " +
            "   inner join liquidacao liq on liq.empenho_id = emp.id " +
            "   inner join liquidacaodoctofiscal ldf on ldf.liquidacao_id = liq.id " +
            "  where ldf.doctofiscalliquidacao_id = :idDocto  " +
            "  order by liq.id desc ";
        Query q = em.createNativeQuery(sql, Empenho.class);
        q.setParameter("idDocto", docto.getId());
        q.setMaxResults(1);
        try {
            return (Empenho) q.getSingleResult();
        } catch (NoResultException r) {
            return null;
        }
    }

    public EfetivacaoAquisicaoFacade getEfetivacaoAquisicaoFacade() {
        return efetivacaoAquisicaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public LiquidacaoFacade getLiquidacaoFacade() {
        return liquidacaoFacade;
    }
}
