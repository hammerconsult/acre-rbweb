package br.com.webpublico.negocios.administrativo.licitacao;

import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.Licitacao;
import br.com.webpublico.entidades.SaldoFonteDespesaORC;
import br.com.webpublico.entidades.administrativo.licitacao.LiberacaoReservaLicitacao;
import br.com.webpublico.entidades.administrativo.licitacao.LiberacaoReservaLicitacaoItem;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SaldoFonteDespesaORCFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wellington on 24/10/17.
 */
@Stateless
public class LiberacaoReservaLicitacaoFacade extends AbstractFacade<LiberacaoReservaLicitacao> {

    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LiberacaoReservaLicitacaoFacade() {
        super(LiberacaoReservaLicitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Licitacao> buscarLicitacoesParaLiberacao(String parte) {
        String sql = "select distinct l.* from ( " +
            "        select l.id id_licitacao, " +
            "               ipc.id id_processo_compra, " +
            "               ipc.quantidade quantidade_licitada, " +
            "               (select sum(ic.quantidadetotalcontrato) " +
            "                   from itemcontrato ic " +
"                                   left join itemcontratoitempropfornec icpf on icpf.itemcontrato_id = ic.id " +
            "                       left join itemcontratoitemirp iirp on iirp.itemcontrato_id = ic.id " +
            "                       left join itemcontratoadesaoataint iata on iata.itemcontrato_id = ic.id " +
            "                       left join itempropfornec ipf on coalesce(icpf.itempropostafornecedor_id, iirp.itempropostafornecedor_id, iata.itempropostafornecedor_id) = ipf.id "+
            "                where ipf.itemprocessodecompra_id = ipc.id) quantidade_contratada, " +
            "               (select sum(dsmid.valor) " +
            "                   from dotsolmat dsm " +
            "                  inner join solicitacaomaterial sm on sm.id = dsm.solicitacaomaterial_id " +
            "                  inner join processodecompra s_pc on s_pc.solicitacaomaterial_id = sm.id " +
            "                  inner join dotacaosolmatitem dsmi on dsmi.dotacaosolicitacaomaterial_id = dsm.id " +
            "                  inner join dotacaosolmatitemfonte dsmid on dsmid.dotacaosolmatitem_id = dsmi.id " +
            "                where s_pc.id = pc.id) valor_reservado, " +
            "                (select sum(ectf.valor) " +
            "                   from contrato c " +
            "                   left join conlicitacao conlic on conlic.contrato_id = c.id " +
            "                   left join licitacao lic on lic.id = conlic.licitacao_id " +
            "                  inner join execucaocontrato ec on ec.contrato_id = c.id " +
            "                  inner join execucaocontratotipo ect on ect.execucaocontrato_id = ec.id " +
            "                  inner join execucaocontratotipofonte ectf on ectf.execucaocontratotipo_id = ect.id " +
            "                where lic.id = l.id) valor_executado " +
            "           from licitacao l " +
            "          inner join statuslicitacao sl on sl.datastatus = (select max(s_sl.datastatus) from statuslicitacao s_sl where s_sl.licitacao_id = l.id) " +
            "          inner join processodecompra pc on pc.id = l.processodecompra_id " +
            "          inner join loteprocessodecompra lpc on lpc.processodecompra_id = pc.id " +
            "          inner join itemprocessodecompra ipc on ipc.loteprocessodecompra_id = lpc.id " +
            "        where sl.tiposituacaolicitacao = 'HOMOLOGADA') dados " +
            "  inner join licitacao l on l.id = dados.id_licitacao " +
            "  inner join processodecompra pc on pc.id = l.processodecompra_id " +
            "where dados.quantidade_licitada = dados.quantidade_contratada " +
            "  and dados.valor_reservado > 0 and dados.valor_reservado > dados.valor_executado " +
            "  and not exists (select 1 from liberacaoreservalicitacao lrd where lrd.licitacao_id = l.id) " +
            "  and pc.unidadeorganizacional_id = :id_unidade " +
            "  and (l.numerolicitacao like :parte or lower(l.resumodoobjeto) like :parte) ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("id_unidade", sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente().getId());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public void popularFontesDespesaOrcLiberacaoSaldo(LiberacaoReservaLicitacao liberacaoReservaLicitacao) {
        String sql = "select dados.fontedespesaorc_id, sum(valor_reservado) valor_reservado, sum(valor_executado) valor_executado from (\n" +
            "    select l.id as licitacao_id, dsmid.fontedespesaorc_id, sum(dsmid.valor) valor_reservado, 0 valor_executado\n" +
            "       from dotsolmat dsm\n" +
            "      inner join solicitacaomaterial sm on sm.id = dsm.solicitacaomaterial_id\n" +
            "      inner join dotacaosolmatitem dsmi on dsmi.dotacaosolicitacaomaterial_id = dsm.id\n" +
            "      inner join dotacaosolmatitemfonte dsmid on dsmid.dotacaosolmatitem_id = dsmi.id\n" +
            "      inner join processodecompra pc on pc.solicitacaomaterial_id = sm.id\n" +
            "      inner join licitacao l on l.processodecompra_id = pc.id\n" +
            "    group by l.id, dsmid.fontedespesaorc_id\n" +
            "    union all\n" +
            "    select lic.id, ectf.fontedespesaorc_id, 0 valor_reservado, sum(ectf.valor) valor_executado\n" +
            "       from contrato c\n" +
            "       left join conlicitacao conlic on conlic.contrato_id = c.id " +
            "       left join licitacao lic on lic.id = conlic.licitacao_id " +
            "      inner join execucaocontrato ec on ec.contrato_id = c.id\n" +
            "      inner join execucaocontratotipo ect on ect.execucaocontrato_id = ec.id\n" +
            "      inner join execucaocontratotipofonte ectf on ectf.execucaocontratotipo_id = ect.id\n" +
            "    group by lic.id, ectf.fontedespesaorc_id) dados\n" +
            "where dados.licitacao_id = :id_licitacao " +
            "group by dados.fontedespesaorc_id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("id_licitacao", liberacaoReservaLicitacao.getLicitacao().getId());
        if (!q.getResultList().isEmpty()) {
            liberacaoReservaLicitacao.setItens(new ArrayList());
            for (Object[] obj : (List<Object[]>) q.getResultList()) {
                LiberacaoReservaLicitacaoItem item = new LiberacaoReservaLicitacaoItem();
                item.setLiberacaoReservaLicitacao(liberacaoReservaLicitacao);
                item.setFonteDespesaORC(em.find(FonteDespesaORC.class, ((BigDecimal) obj[0]).longValue()));
                item.setValorReservado((BigDecimal) obj[1]);
                item.setValorExecutado((BigDecimal) obj[2]);
                liberacaoReservaLicitacao.getItens().add(item);
            }
        }
    }

    @Override
    public LiberacaoReservaLicitacao recuperar(Object id) {
        LiberacaoReservaLicitacao liberacaoReservaLicitacao = super.recuperar(id);
        liberacaoReservaLicitacao.getItens().size();
        return liberacaoReservaLicitacao;
    }

    @Override
    public void salvarNovo(LiberacaoReservaLicitacao liberacaoReservaLicitacao) {
        super.salvarNovo(liberacaoReservaLicitacao);
        liberarReservasRestantes(liberacaoReservaLicitacao);
    }

    private void liberarReservasRestantes(LiberacaoReservaLicitacao liberacaoReservaLicitacao) {
        for (LiberacaoReservaLicitacaoItem item : liberacaoReservaLicitacao.getItens()) {
            SaldoFonteDespesaORC saldoFonteDespesaORC =
                saldoFonteDespesaORCFacade.recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(item.getFonteDespesaORC(),
                    liberacaoReservaLicitacao.getLiberadoEm(), item.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional());
            if (saldoFonteDespesaORC.getReservadoPorLicitacao().compareTo(item.getValorLiberar()) >= 0) {
                SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                    item.getFonteDespesaORC(),
                    OperacaoORC.RESERVADO_POR_LICITACAO,
                    TipoOperacaoORC.ESTORNO,
                    item.getValorLiberar(),
                    liberacaoReservaLicitacao.getLiberadoEm(),
                    item.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                    item.getId().toString(),
                    item.getClass().getSimpleName(),
                    liberacaoReservaLicitacao.getLicitacao().getNumeroAnoLicitacao(),
                    getHistoricoLiberacao(liberacaoReservaLicitacao, item));
                saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
            }
        }
    }

    private String getHistoricoLiberacao(LiberacaoReservaLicitacao liberacaoReservaLicitacao, LiberacaoReservaLicitacaoItem item) {
        return " Liberação de Reserva da Licitação na data: " + DataUtil.getDataFormatada(liberacaoReservaLicitacao.getLiberadoEm()) + " e responsável: " + liberacaoReservaLicitacao.getResponsavel().getNome() + " | " +
            " Número/Ano da Licitação: " + liberacaoReservaLicitacao.getLicitacao().getNumeroAnoLicitacao() + " | " +
            " Número do Processo de Compra: " + liberacaoReservaLicitacao.getLicitacao().getProcessoDeCompra().getNumeroAndExercicio() + " | " +
            " Unidade Administrativa: " + hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(sistemaFacade.getDataOperacao(), liberacaoReservaLicitacao.getLicitacao().getUnidades().get(0).getUnidadeAdministrativa(), TipoHierarquiaOrganizacional.ADMINISTRATIVA) + " | " +
            " Elemento de Despesa: " + item.getFonteDespesaORC().getDespesaORC() + "/" + item.getFonteDespesaORC().getProvisaoPPAFonte().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " | " +
            " Fonte de Recurso: " + item.getFonteDespesaORC().getDescricaoFonteDeRecurso().trim();
    }
}
