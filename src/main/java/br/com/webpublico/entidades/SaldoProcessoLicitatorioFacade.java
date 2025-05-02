package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.FiltroSaldoProcessoLicitatorioVO;
import br.com.webpublico.entidadesauxiliares.SaldoProcessoLicitatorioItemVO;
import br.com.webpublico.entidadesauxiliares.SaldoProcessoLicitatorioOrigemVO;
import br.com.webpublico.entidadesauxiliares.SaldoProcessoLicitatorioVO;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.OrigemSaldoExecucaoProcesso;
import br.com.webpublico.enums.TipoNaturezaDoProcedimentoLicitacao;
import br.com.webpublico.negocios.AlteracaoContratualFacade;
import br.com.webpublico.negocios.ParticipanteIRPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Stateless
public class SaldoProcessoLicitatorioFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ParticipanteIRPFacade participanteIRPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;

    public SaldoProcessoLicitatorioVO getSaldoProcesso(FiltroSaldoProcessoLicitatorioVO filtro) {
        SaldoProcessoLicitatorioVO saldoVO = new SaldoProcessoLicitatorioVO(filtro.getTipoProcesso());
        List<SaldoProcessoLicitatorioOrigemVO> saldosAgrupadosPorOrigem = buscarSaldosAgrupadosPorOrigem(filtro);
        Collections.sort(saldosAgrupadosPorOrigem);
        saldoVO.setSaldosAgrupadosPorOrigem(saldosAgrupadosPorOrigem);
        return saldoVO;
    }

    public List<SaldoProcessoLicitatorioOrigemVO> buscarSaldosAgrupadosPorOrigem(FiltroSaldoProcessoLicitatorioVO filtro) {
        String sql = "" +
            "   select " +
            "   vw.idprocesso      as id_processo, " +
            "   vw.idfornecedor    as id_fornecedor, " +
            "   vw.origemsaldo     as origem, " +
            "   vw.naturezasaldo   as natureza " +
            " from vwsaldoprocessosemcontrato vw ";
        sql += filtro.getIdAta() != null ? " where (vw.idProcesso = :idProcesso or vw.idAta = :idAta) " : "   where vw.idProcesso = :idProcesso ";
        sql += filtro.getFornecedor() != null ? " and vw.idFornecedor = :idFornecedor " : " ";
        sql += filtro.getUnidadeOrganizacional() != null ? " and vw.idUnidade = :idUnidade " : " ";
        sql += " group by vw.idprocesso, vw.idfornecedor, vw.origemsaldo, vw.naturezasaldo  ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", filtro.getIdProcesso());
        if (filtro.getIdAta() != null) {
            q.setParameter("idAta", filtro.getIdAta());
        }
        if (filtro.getFornecedor() != null) {
            q.setParameter("idFornecedor", filtro.getFornecedor().getId());
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("idUnidade", filtro.getUnidadeOrganizacional().getId());
        }
        List<Object[]> list = q.getResultList();
        List<SaldoProcessoLicitatorioOrigemVO> itens = Lists.newArrayList();
        for (Object[] obj : list) {
            SaldoProcessoLicitatorioOrigemVO saldoVO = new SaldoProcessoLicitatorioOrigemVO();
            saldoVO.setIdOrigem(((BigDecimal) obj[0]).longValue());
            saldoVO.setIdFornecedor(((BigDecimal) obj[1]).longValue());
            saldoVO.setOrigem(OrigemSaldoExecucaoProcesso.valueOf((String) obj[2]));
            saldoVO.setNatureza(SaldoProcessoLicitatorioOrigemVO.NaturezaSaldo.valueOf((String) obj[3]));
            saldoVO.setProcessoIrp(filtro.getProcessoIrp());
            if (filtro.getRecuperarOrigem()) {
                saldoVO.setDescricaoMovimento(buscarDescricaoOrigemSaldo(saldoVO.getOrigem(), saldoVO.getIdOrigem(), filtro.getIdAta()));
            }
            saldoVO.setItens(buscarItensSaldo(filtro, saldoVO));
            itens.add(saldoVO);
        }
        return itens;
    }

    public List<SaldoProcessoLicitatorioItemVO> buscarItensSaldo(FiltroSaldoProcessoLicitatorioVO filtro, SaldoProcessoLicitatorioOrigemVO saldoVO) {
        String sql = "" +
            "   select " +
            "      idItemProcesso             as id_item_processo," +
            "      idProcesso                 as id_processo," +
            "      idFornecedor               as id_fornecedor," +
            "      origemSaldo                as origem_saldo," +
            "      naturezaSaldo              as natureza_saldo," +
            "      quantidadeProcesso         as qtde_processo, " +
            "      quantidadeAcrescimo        as qtde_acrescimo, " +
            "      quantidadeContratada       as qtde_contrato, " +
            "      quantidadeExecutada        as qtde_executada, " +
            "      quantidadeEstornada        as qtde_estornada, " +
            "      valorUnitarioHomolgado     as vl_unit, " +
            "      valorTotalHomolgado        as vl_total_homologado," +
            "      valorAcrescimo             as vl_total_acrescimo," +
            "      valorContratado            as vl_contrato, " +
            "      valorExecutado             as vl_executado, " +
            "      valorEstornado             as vl_estornado " +
            "   from vwsaldoprocessosemcontrato " +
            "   where idProcesso = :idProcesso " ;
        sql += saldoVO.getIdFornecedor() != null ? " and idFornecedor = :idFornecedor " : " ";
        sql += filtro.getUnidadeOrganizacional() != null ? " and idUnidade = :idUnidade " : " ";
        sql+="  order by numeroLote, numeroItem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idProcesso", saldoVO.getIdOrigem());
        if (saldoVO.getIdFornecedor() != null) {
            q.setParameter("idFornecedor", saldoVO.getIdFornecedor());
        }
        if (filtro.getUnidadeOrganizacional() != null) {
            q.setParameter("idUnidade", filtro.getUnidadeOrganizacional().getId());
        }
        List<Object[]> list = q.getResultList();

        List<SaldoProcessoLicitatorioItemVO> itens = Lists.newArrayList();
        for (Object[] obj : list) {
            SaldoProcessoLicitatorioItemVO itemVO = new SaldoProcessoLicitatorioItemVO();
            ItemProcessoDeCompra itemProcessoCompra = em.find(ItemProcessoDeCompra.class, ((BigDecimal) obj[0]).longValue());
            itemVO.setItemProcessoCompra(itemProcessoCompra);
            itemVO.setIdProcesso(((BigDecimal) obj[1]).longValue());
            itemVO.setIdFornecedor(((BigDecimal) obj[2]).longValue());
            itemVO.setOrigemSaldo(OrigemSaldoExecucaoProcesso.valueOf((String) obj[3]));
            itemVO.setNaturezaSaldo(SaldoProcessoLicitatorioOrigemVO.NaturezaSaldo.valueOf((String) obj[4]));
            itemVO.setQuantidadeProcesso((BigDecimal) obj[5]);
            itemVO.setQuantidadeAcrescimo((BigDecimal) obj[6]);
            itemVO.setQuantidadeContratada((BigDecimal) obj[7]);
            itemVO.setQuantidadeExecutada((BigDecimal) obj[8]);
            itemVO.setQuantidadeEstornada((BigDecimal) obj[9]);
            itemVO.setValorUnitarioHomologado((BigDecimal) obj[10]);
            itemVO.setValorlHomologado((BigDecimal) obj[11]);
            itemVO.setValorAcrescimo((BigDecimal) obj[12]);
            itemVO.setValorContratado((BigDecimal) obj[13]);
            itemVO.setValorExecutado((BigDecimal) obj[14]);
            itemVO.setValorEstornado((BigDecimal) obj[15]);

            if (saldoVO.getOrigem().isAta()) {
                itemVO.setQuantidadeAta(itemVO.getQuantidadeProcesso());
                atribuirQuantidadeAndValorAta(filtro, itemVO);
            }
            itens.add(itemVO);
        }
        return itens;
    }

    public String buscarDescricaoOrigemSaldo(OrigemSaldoExecucaoProcesso origem, Long idOrigem, Long idAta) {
        String descricao = "";
        if (origem.isAta()) {
            String sql = " " +
                " select lic.numerolicitacao        as n_lic, " +
                "       lic.modalidadelicitacao    as modalidade, " +
                "       lic.naturezadoprocedimento as natureza, " +
                "       ex.ano                     as ano_lic, " +
                "       ata.numero                 as n_ata " +
                " from licitacao lic " +
                "         inner join ataregistropreco ata on ata.licitacao_id = lic.id " +
                "         inner join exercicio ex on ex.id = lic.exercicio_id " +
                " where lic.id = :idOrigem ";
            if (idAta != null) {
                sql += " and ata.id = :idAta ";
            }
            Query q = em.createNativeQuery(sql);
            q.setParameter("idOrigem", idOrigem);
            if (idAta != null) {
                q.setParameter("idAta", idAta);
            }

            if (!Util.isListNullOrEmpty(q.getResultList())) {
                Object[] obj = (Object[]) q.getSingleResult();
                String numeroLic = ((BigDecimal) obj[0]).toString();
                String modalidade = ModalidadeLicitacao.valueOf((String) obj[1]).getDescricao();
                String natureza = TipoNaturezaDoProcedimentoLicitacao.valueOf((String) obj[2]).getDescricao();
                String ano = ((BigDecimal) obj[3]).toString();
                String numeroAta = ((BigDecimal) obj[4]).toString();
                descricao = "Ata nº " + numeroAta + " - " + modalidade + " - " + natureza + " - " + numeroLic + "/" + ano;
            }
        } else if (origem.isDispensa()) {
            String sql = " " +
                " select 'Nº' || disp.numerodadispensa || '/' || ex.ano as descricao " +
                " from dispensadelicitacao disp " +
                "         inner join exercicio ex on ex.id = disp.exerciciodadispensa_id " +
                " where disp.id = :idOrigem ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idOrigem", idOrigem);
            descricao = (String) q.getSingleResult();
        } else {
            String sql = " " +
                "  select 'Nº ' || ad.numero || ' - Termo ' || ad.numerotermo || '/' || ex.ano " +
                "   from alteracaocontratual ad " +
                "        inner join exercicio ex on ex.id = ad.exercicio_id " +
                "  where ad.id = :idOrigem ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("idOrigem", idOrigem);
            descricao = (String) q.getSingleResult();
        }
        return descricao;
    }

    private void atribuirQuantidadeAndValorAta(FiltroSaldoProcessoLicitatorioVO filtro, SaldoProcessoLicitatorioItemVO itemVO) {
        if (filtro.getProcessoIrp()) {
            ItemParticipanteIRP itemPartIRP = participanteIRPFacade.buscarItemPorUnidade(
                filtro.getIdProcesso(),
                filtro.getUnidadeOrganizacional(),
                itemVO.getItemProcessoCompra().getObjetoCompra(),
                itemVO.getItemProcessoCompra().getNumero(),
                itemVO.getItemProcessoCompra().getNumeroLote());

            if (itemPartIRP != null) {
                itemVO.setQuantidadeAta(itemPartIRP.getQuantidade());

                if (itemVO.getItemProcessoCompra().getItemSolicitacaoMaterial().getItemCotacao().isTipoControlePorValor()
                    && filtro.getTipoAvaliacaoLicitacao().isMaiorDesconto()) {
                    itemVO.setValorUnitarioHomologado(itemPartIRP.getValor());
                }
            }
        }
        itemVO.setValorAta(itemVO.calcularValorTotalAta());
    }

    public BigDecimal getValorUnitarioHomologado(Long idItemProcesso) {
        String sql = "" +
            "   select coalesce(valorUnititarioHomologado, 0) from vwsaldoprocessosemcontrato " +
            "   where idItemProcesso = :idItemProcesso ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemProcesso", idItemProcesso);
        return (BigDecimal) q.getSingleResult();
    }
}
