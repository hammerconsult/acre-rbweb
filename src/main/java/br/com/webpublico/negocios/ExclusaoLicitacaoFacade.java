package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.AtaRegistroPrecoPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.LicitacaoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.RelacionamentoLicitacao;
import br.com.webpublico.enums.SituacaoItemProcessoDeCompra;
import br.com.webpublico.enums.TipoApuracaoLicitacao;
import br.com.webpublico.enums.TipoSolicitacaoRegistroPreco;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ExclusaoLicitacaoFacade extends AbstractFacade<ExclusaoLicitacao> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ExclusaoLicitacaoFacade() {
        super(ExclusaoLicitacao.class);
    }

    @Override
    public ExclusaoLicitacao salvarRetornando(ExclusaoLicitacao entity) {
        updateItemProcessoCompra(entity);
        executarScript(entity);
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ExclusaoLicitacao.class, "numero"));
        }
        excluirLicitacao(entity);
        return em.merge(entity);
    }

    private void updateItemProcessoCompra(ExclusaoLicitacao entity) {
        List<ItemProcessoDeCompra> itens = processoDeCompraFacade.buscarItensProcessoCompra(entity.getLicitacao().getProcessoDeCompra());
        for (ItemProcessoDeCompra item : itens) {
            if (!SituacaoItemProcessoDeCompra.AGUARDANDO.equals(item.getSituacaoItemProcessoDeCompra())) {
                item.setSituacaoItemProcessoDeCompra(SituacaoItemProcessoDeCompra.AGUARDANDO);
                em.merge(item);
            }
        }
    }

    public void excluirLicitacao(ExclusaoLicitacao entity) {
        Licitacao licitacao = em.find(Licitacao.class, entity.getLicitacao().getId());
        em.remove(licitacao);
    }

    private void executarScript(ExclusaoLicitacao entity) {
        for (RelacionamentoLicitacao rel : entity.getRelacionamentos()) {
            if (rel.isPortal()) {
                LicitacaoPortal licitacaoPortal = em.find(LicitacaoPortal.class, rel.getId());
                em.remove(licitacaoPortal);
            }
            if (rel.isPropostaFornecedor()) {
                PropostaFornecedor propostaFornecedor = em.find(PropostaFornecedor.class, rel.getId());
                em.remove(propostaFornecedor);
            }
            if (rel.isPropostaTecnicaFornecedor()) {
                PropostaTecnica propostaTecnica = em.find(PropostaTecnica.class, rel.getId());
                em.remove(propostaTecnica);
            }
            if (rel.isPregaoPorItem() || rel.isPregaoPorLote()) {
                Pregao pregao = em.find(Pregao.class, rel.getId());
                em.remove(pregao);
            }
            if (rel.isAtaRegistroPrecoPortal()) {
                AtaRegistroPrecoPortal ataRegistroPrecoPortal = em.find(AtaRegistroPrecoPortal.class, rel.getId());
                em.remove(ataRegistroPrecoPortal);
            }
            if (rel.isSolicitacaoAtaRegistroPreco()) {
                SolicitacaoMaterialExterno solicitacaoMaterialExterno = em.find(SolicitacaoMaterialExterno.class, rel.getId());
                em.remove(solicitacaoMaterialExterno);
            }
            if (rel.isAtaRegistroPreco()) {
                AtaRegistroPreco ataRegistroPreco = em.find(AtaRegistroPreco.class, rel.getId());
                em.remove(ataRegistroPreco);
            }
            if (rel.isMapaComparativo()) {
                Certame certame = em.find(Certame.class, rel.getId());
                em.remove(certame);
            }
            if (rel.isMapaComparativoTecnicaPreco()) {
                MapaComparativoTecnicaPreco mapaComparativoTecnicaPreco = em.find(MapaComparativoTecnicaPreco.class, rel.getId());
                em.remove(mapaComparativoTecnicaPreco);
            }
        }
    }

    public List<RelacionamentoLicitacao> buscarRelacionamentosLicitacao(Licitacao licitacao) {
        String sql = " select " +
            "       id, " +
            "       tipo_movimento, " +
            "       data_movimento, " +
            "       movimento, " +
            "       cor " +
            "   from (" +
            "   select lp.id as id, " +
            "      'PORTAL' as tipo_movimento, " +
            "       lic.emitidaEm as data_movimento, " +
            "       'Id da entidade portal ' || lp.id as movimento, " +
            "       'green' as cor " +
            "   from LICITACAOPORTAL lp " +
            "   inner join licitacao lic on lic.id = lp.licitacao_id " +
            "   where lic.id = :idLicitacao " +
            " union all " +
            " select " +
            "  propF.id as id, " +
            "  'PROPOSTA_FORNECEDOR' as tipo_movimento, " +
            "  propF.DATAPROPOSTA as data_movimento, " +
            "  coalesce(pf.CPF, pj.CNPJ) || ' - ' || coalesce(pf.NOME, pj.RAZAOSOCIAL) as movimento, " +
            "  'green' as cor " +
            " from PropostaFornecedor propF " +
            "  inner join pessoa p on p.ID = propF.FORNECEDOR_ID " +
            "  left join PESSOAFISICA pf on p.ID = pf.id " +
            "  left join PESSOAJURIDICA pj on p.ID = pj.id " +
            " where propF.LICITACAO_ID = :idLicitacao " +
            " union all " +
            " select " +
            "  propF.id as id, " +
            "  'PROPOSTA_TECNICA_FORNECEDOR' as tipo_movimento, " +
            "  propF.DATA as data_movimento, " +
            "  coalesce(pf.CPF, pj.CNPJ) || ' - ' || coalesce(pf.NOME, pj.RAZAOSOCIAL) as movimento, " +
            "  'green' as cor " +
            " from PropostaTecnica propF " +
            "  inner join pessoa p on p.ID = propF.FORNECEDOR_ID " +
            "  left join PESSOAFISICA pf on p.ID = pf.id " +
            "  left join PESSOAJURIDICA pj on p.ID = pj.id " +
            " where propF.LICITACAO_ID = :idLicitacao " +
            " union all " +
            " select " +
            "  p.id as id, " +
            "  'PREGAO_ITEM' as tipo_movimento, " +
            "  p.REALIZADOEM as data_movimento, " +
            "  'Realizado em : ' || to_char(p.REALIZADOEM, 'dd/MM/yyyy') || ' - ' || lic.NUMEROLICITACAO || '/' || exLic.ANO as movimento, " +
            "  'green' as cor " +
            " from PREGAO p " +
            "  inner join LICITACAO lic on lic.ID = p.LICITACAO_ID " +
            "  inner join EXERCICIO exLic on exLic.id = lic.EXERCICIO_ID " +
            " where lic.ID = :idLicitacao and lic.TIPOAPURACAO = :item " +
            " union all " +
            " select " +
            "  p.id as id, " +
            "  'PREGAO_LOTE' as tipo_movimento, " +
            "  p.REALIZADOEM as data_movimento, " +
            "  'Realizado em : ' || to_char(p.REALIZADOEM, 'dd/MM/yyyy') || ' - ' || lic.NUMEROLICITACAO || '/' || exLic.ANO as movimento, " +
            "  'green' as cor " +
            " from PREGAO p " +
            "  inner join LICITACAO lic on lic.ID = p.LICITACAO_ID " +
            "  inner join EXERCICIO exLic on exLic.id = lic.EXERCICIO_ID " +
            " where lic.ID = :idLicitacao and lic.TIPOAPURACAO = :lote " +
            " union all " +
            " select " +
            "  arpPortal.id as id, " +
            "  'ATA_REGISTRO_PRECO_PORTAL' as tipo_movimento, " +
            "  arp.DATAINICIO as data_movimento, " +
            "  'Id da ata registro de preço da entidade portal ' || arpPortal.id as movimento, " +
            "  'green' as cor " +
            " from AtaRegistroPrecoPortal arpPortal " +
            "   inner join AtaRegistroPreco arp on arp.id = arpPortal.ATAREGISTROPRECO_ID " +
            " where arp.LICITACAO_ID = :idLicitacao " +
            " union all " +
            " select sol.id                                   as id, " +
            "       'SOLICITACAO_ATA_REGISTRO_PRECO_INTERNA' as tipo_movimento, " +
            "       sol.DATASOLICITACAO                      as data_movimento, " +
            "       'Nº Solicitação: ' || sol.NUMERO         as movimento, " +
            "       'green'                                  as cor " +
            " from SOLICITACAOMATERIALEXT sol " +
            "         inner join ataregistropreco ata on ata.id = sol.ATAREGISTROPRECO_ID " +
            " where sol.TIPOSOLICITACAOREGISTROPRECO = :tipoInterna " +
            "  and ata.LICITACAO_ID = :idLicitacao " +
            " union all " +
            " select sol.id                                   as id, " +
            "       'SOLICITACAO_ATA_REGISTRO_PRECO_EXTERNA' as tipo_movimento, " +
            "       sol.DATASOLICITACAO                      as data_movimento, " +
            "       'Nº Solicitação: ' || sol.NUMERO         as movimento, " +
            "       'green'                                  as cor " +
            " from SOLICITACAOMATERIALEXT sol " +
            "         inner join ataregistropreco ata on ata.id = sol.ATAREGISTROPRECO_ID " +
            " where sol.TIPOSOLICITACAOREGISTROPRECO = :tipoExterna " +
            "  and ata.LICITACAO_ID  = :idLicitacao" +
            " union all " +
            " select " +
            "   arp.id as id, " +
            "   'ATA_REGISTRO_PRECO' as tipo_movimento, " +
            "   arp.DATAINICIO as data_movimento, " +
            "   'Nº Ata: ' || arp.NUMERO as movimento, " +
            "   'green' as cor " +
            "  from AtaRegistroPreco arp " +
            " where arp.LICITACAO_ID = :idLicitacao " +
            " union all " +
            " select cer.id as id, " +
            "   'MAPA_COMPARATIVO' as tipo_movimento, " +
            "   cer.DATAREALIZADO as data_movimento, " +
            "   'Data: ' || TO_CHAR(cer.DATAREALIZADO, 'dd/MM/yyyy') || ' Licitação: ' || lic.NUMEROLICITACAO || '/' || exLic.ANO as movimento, " +
            "   'green' as cor " +
            " from CERTAME cer " +
            "  inner join licitacao lic on lic.id = cer.licitacao_id " +
            "  inner join EXERCICIO exLic on exLic.id = lic.EXERCICIO_ID " +
            " where lic.id = :idLicitacao " +
            " union all " +
            " select " +
            "   mctp.id as id, " +
            "   'MAPA_COMPARATIVO_TECNICA_PRECO' as tipo_movimento, " +
            "   mctp.DATA as data_movimento, " +
            "   'Data: ' || TO_CHAR(mctp.DATA, 'dd/MM/yyyy') || ' Licitação: ' || lic.NUMEROLICITACAO || '/' || exLic.ANO as movimento, " +
            "   'green' as cor " +
            "  from MAPACOMTECPREC mctp " +
            "  inner join licitacao lic on lic.id = mctp.licitacao_id " +
            "  inner join EXERCICIO exLic on exLic.id = lic.EXERCICIO_ID " +
            " where lic.id = :idLicitacao " +
            " union all " +
            " select " +
            "  c.id as id, " +
            "  'CONTRATO' as tipo_movimento, " +
            "  c.DATALANCAMENTO as data_movimento, " +
            "  c.NUMEROCONTRATO || '/' || coalesce(exC.ANO, extract (year from c.DATALANCAMENTO)) || ' - ' || coalesce(pf.NOME, pj.RAZAOSOCIAL) as movimento, " +
            "  'red' as cor " +
            " from contrato c " +
            " inner join conlicitacao conlic on conlic.contrato_id = c.id " +
            " inner join licitacao lic on lic.id = conlic.licitacao_id " +
            "  left join EXERCICIO exC on exC.id = c.EXERCICIOCONTRATO_ID " +
            "  inner join PESSOA p on p.id = c.CONTRATADO_ID " +
            "  left join PESSOAFISICA pf on p.id = pf.id " +
            "  left join PESSOAJURIDICA pj on p.id = pj.id " +
            " where lic.id = :idLicitacao " +
            " ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLicitacao", licitacao.getId());
        q.setParameter("item", TipoApuracaoLicitacao.ITEM.name());
        q.setParameter("lote", TipoApuracaoLicitacao.LOTE.name());
        q.setParameter("tipoInterna", TipoSolicitacaoRegistroPreco.INTERNA.name());
        q.setParameter("tipoExterna", TipoSolicitacaoRegistroPreco.EXTERNA.name());
        List<RelacionamentoLicitacao> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            RelacionamentoLicitacao rel = new RelacionamentoLicitacao();
            rel.setId(((BigDecimal) obj[0]).longValue());
            rel.setTipo(RelacionamentoLicitacao.TipoRelacionamento.valueOf((String) obj[1]));
            rel.setData((Date) obj[2]);
            rel.setMovimento((String) obj[3]);
            rel.setCor((String) obj[4]);
            toReturn.add(rel);
        }
        return toReturn;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
