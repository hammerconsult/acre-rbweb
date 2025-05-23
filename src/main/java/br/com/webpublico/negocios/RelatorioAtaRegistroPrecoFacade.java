package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.TipoMascaraUnidadeMedidaDTO;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Stateless
public class RelatorioAtaRegistroPrecoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AdesaoFacade adesaoFacade;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ItemContratoFacade itemContratoFacade;
    @EJB
    private ItemPropostaFornecedorFacade itemPropostaFornecedorFacade;

    public List<AdesaoAtaRegistroPreco> buscarDadosAdesaoAtaRegistroPreco(Adesao adesao) {
        String sql = "" +
            "select  " +
            "    numero_solicitacao,  " +
            "    exercicio_solicitacao, " +
            "    data_solicitacao, " +
            "    unidade_solicitante,  " +
            "    numero_ata, " +
            "    numero_licitacao, " +
            "    numero_modalidade, " +
            "    modalidade, " +
            "    resumo_objeto, " +
            "    objeto_compra,   " +
            "    descricao_objeto_compra,   " +
            "    coalesce(quantidade_aprovada,0) as quantidade_aprovada,  " +
            "    coalesce(valor_unitario,0) as valor_unitario,   " +
            "    coalesce(valor_total,0) as valor_total,   " +
            "    coalesce(quantidade_contratada,0) as quantidade_contratada,  " +
            "    coalesce(quantidade_aprovada, 0) -  coalesce(quantidade_contratada,0) as saldo_contratar, " +
            "    numero_item," +
            "    id_unidade_medida " +
            "   from (  " +
            "      select  " +
            "      sol.numero as numero_solicitacao,  " +
            "      ex.ano as exercicio_solicitacao, " +
            "      sol.datasolicitacao as data_solicitacao, " +
            "      vw.codigo || ' - ' || vw.descricao as unidade_solicitante,  " +
            "      ata.numero as numero_ata, " +
            "      lic.numerolicitacao as numero_licitacao, " +
            "      lic.numero as numero_modalidade, " +
            "      lic.modalidadelicitacao as modalidade, " +
            "      substr(lic.resumodoobjeto,0,100) as resumo_objeto, " +
            "      oc.descricao as objeto_compra,   " +
            "      to_char(coalesce(item.descricaocomplementar ,ise.descricaocomplementar)) as descricao_objeto_compra,   " +
            "      coalesce(ise.quantidade,0) as quantidade_aprovada,  " +
            "      coalesce(ise.valorunitario,0) as valor_unitario,   " +
            "      coalesce(ise.valortotal,0) as valor_total,   " +
            "      coalesce((select sum(ic.quantidadetotalcontrato) from itemcontrato ic  " +
            "      inner join itemcontratoadesaoataint icf on icf.itemcontrato_id = ic.id  " +
            "      where icf.itemsolicitacaoexterno_id = ise.id),0) as quantidade_contratada,  " +
            "      ipc.numero as numero_item," +
            "      um.id as id_unidade_medida " +
            "     from ataregistropreco ata    " +
            "      inner join licitacao lic on lic.id = ata.licitacao_id   " +
            "      inner join solicitacaomaterialext sol on sol.ataregistropreco_id = ata.id   " +
            "      inner join exercicio ex on ex.id = sol.exercicio_id " +
            "      inner join adesao ad on ad.solicitacaomaterialexterno_id = sol.id " +
            "      inner join vwhierarquiaadministrativa vw on vw.subordinada_id = sol.unidadeorganizacional_id  " +
            "      inner join itemsolicitacaoexterno ise on ise.solicitacaomaterialexterno_id = sol.id   " +
            "      left join objetocompra oc on oc.id = ise.objetocompra_id   " +
            "      left join itemprocessodecompra ipc on ipc.id = ise.itemprocessocompra_id  " +
            "      left join itemsolicitacao item on item.id = ipc.itemsolicitacaomaterial_id   " +
            "      left join unidademedida um on um.id = item.unidademedida_id  " +
            "      where ad.id = :idAdesao " +
            "      and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vw.iniciovigencia) and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   ) dados  " +
            "   order by numero_ata, numero_item ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAdesao", adesao.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List<AdesaoAtaRegistroPreco> toReturn = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                AdesaoAtaRegistroPreco item = new AdesaoAtaRegistroPreco();
                item.setNumeroSolicitacao(((BigDecimal) obj[0]).longValue());
                item.setExercicioSolicitacao(((BigDecimal) obj[1]).intValue());
                item.setDataSolicitacao((Timestamp) obj[2]);
                item.setUnidadeSolicitante((String) obj[3]);
                item.setNumeroAta(((BigDecimal) obj[4]).longValue());

                int numeroLicitacao = ((BigDecimal) obj[5]).intValue();
                int numeroModalidade = ((BigDecimal) obj[6]).intValue();
                String modalidade = ModalidadeLicitacao.valueOf((String) obj[7]).getDescricao();
                String resumoObjeto = (String) obj[8];
                item.setLicitacao("Nº " + numeroLicitacao + " - Modalidade " + modalidade + " nº " + numeroModalidade + " - " + resumoObjeto);

                item.setAtaRegistroPreco("Nº " + item.getNumeroAta() + " nº da modalidade " + numeroModalidade + " - " + resumoObjeto);

                item.setObjetoCompra((String) obj[9]);
                item.setDescricaoComplementar((String) obj[10]);
                item.setQuantidade((BigDecimal) obj[11]);
                item.setValorUnitario((BigDecimal) obj[12]);
                item.setValorTotal((BigDecimal) obj[13]);
                item.setQuantidadeContratada((BigDecimal) obj[14]);
                item.setQuantidadeDisponivel((BigDecimal) obj[15]);
                item.setUnidadeMedida(obj[16] != null ? em.find(UnidadeMedida.class, ((BigDecimal) obj[16]).longValue()) : null);
                item.setMascaraQuantidade(item.getUnidadeMedida() != null ? item.getUnidadeMedida().getMascaraQuantidade().getMascara() : TipoMascaraUnidadeMedidaDTO.DUAS_CASAS_DECIMAL.getMascara());
                item.setMascaraValorUnitario(item.getUnidadeMedida() != null ? item.getUnidadeMedida().getMascaraValorUnitario().getMascara() : TipoMascaraUnidadeMedidaDTO.DUAS_CASAS_DECIMAL.getMascara());
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public List<AtaRegistroPrecoContrato> buscarDadosAdesaoContato(Adesao adesao) {
        String sql = " " +
            "select  " +
            "  c.numerocontrato || ' - ' || c.numerotermo ||'/'|| ex.ano " +
            "  || ' - ' || vw.codigo || ' - ' || vw.descricao " +
            "  || ' - ' || formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) || ' - ' || coalesce(pf.nome, pj.razaosocial) as contrato," +
            "  c.id as idcontrato, " +
            "  c.valortotal as valor_total " +
            "from contrato c " +
            "  inner join pessoa p on p.id = c.contratado_id " +
            "  left join pessoafisica pf on pf.id = p.id " +
            "  left join pessoajuridica pj on pj.id = p.id " +
            "  inner join exercicio ex on ex.id = c.exerciciocontrato_id " +
            "  inner join conlicitacao cl on cl.contrato_id = c.id " +
            "  inner join unidadecontrato uc on uc.contrato_id = c.id " +
            "  inner join vwhierarquiaadministrativa vw on vw.subordinada_id = uc.unidadeadministrativa_id " +
            "where cl.solicitacaomaterialexterno_id = :idSme " +
            "and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vw.iniciovigencia) and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "order by c.numerocontrato ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSme", adesao.getSolicitacaoMaterialExterno().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List<AtaRegistroPrecoContrato> toReturn = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                AtaRegistroPrecoContrato item = new AtaRegistroPrecoContrato();
                item.setContrato((String) obj[0]);
                item.setIdContrato(((BigDecimal) obj[1]).longValue());
                BigDecimal valorTotal = (BigDecimal) obj[2];
                item.setContrato(item.getContrato() + ", " + Util.formataValor(valorTotal));
                item.setItensContratoQuantidade(buscarDadosItensContrato(item.getIdContrato()));
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public String retornarMascaraQuantidade(ItemPropostaFornecedor itemProposta) {
        try {
            return itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida().getMascaraQuantidade().getMascara();
        } catch (Exception ex) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL.name();
        }
    }

    public String retornarMascaraValorUnitario(ItemPropostaFornecedor itemProposta) {
        try {
            return itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida().getMascaraValorUnitario().getMascara();
        } catch (Exception ex) {
            return TipoMascaraUnidadeMedida.DUAS_CASAS_DECIMAL.name();
        }
    }

    public RelatorioAtaRegistroPreco buscarDadosAtaRegistroPreco(AtaRegistroPreco ataRegistroPreco) {
        Licitacao licitacao = ataRegistroPreco.getLicitacao();
        RelatorioAtaRegistroPreco relAta = new RelatorioAtaRegistroPreco();
        relAta.setNumeroAta(ataRegistroPreco.getNumero());
        relAta.setDataInicio(ataRegistroPreco.getDataInicio());
        relAta.setDataVencimento(ataRegistroPreco.getDataVencimento());
        relAta.setLicitacao(licitacao.toString());
        relAta.setNumeroProcesso(licitacao.getNumero().toString());
        relAta.setModalidade(licitacao.getModalidadeLicitacao().getDescricao());
        relAta.setSecretaria(licitacao.getUnidadeAdministrativa().toString());
        relAta.setObjeto(licitacao.getResumoDoObjeto());

        List<Pessoa> fornecedores = licitacaoFacade.recuperarVencedoresDaLicitacao(licitacao);
        for (Pessoa fornecedor : fornecedores) {
            AtaRegistroPrecoFornecedor relFornecedor = new AtaRegistroPrecoFornecedor();
            relFornecedor.setFornecedor(fornecedor.toString());

            List<ItemPropostaFornecedor> itensVencidos = licitacaoFacade.getItensVencidosPeloFornecedorPorStatus(licitacao, SituacaoItemProcessoDeCompra.HOMOLOGADO,
                fornecedor, TipoClassificacaoFornecedor.getHabilitados(), ataRegistroPreco.getUnidadeOrganizacional(), sistemaFacade.getDataOperacao());

            for (ItemPropostaFornecedor itemProposta : itensVencidos) {
                AtaRegistroPrecoItens relItem = new AtaRegistroPrecoItens();
                relItem.setFornecedor(relFornecedor.getFornecedor());
                relItem.setObjetoCompra(itemProposta.getObjetoCompra().toString());
                relItem.setDescricaoComplementar(itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricaoComplementar());
                relItem.setMascaraQuantidade(retornarMascaraQuantidade(itemProposta));
                relItem.setMascaraValorUnitario(retornarMascaraValorUnitario(itemProposta));
                relItem.setUnidadeMedida(itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida());

                if (itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getItemCotacao().isTipoControlePorQuantidade()) {
                    relItem.setQuantidade(itemProposta.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getQuantidade());
                    relItem.setValorUnitario(itemPropostaFornecedorFacade.getValorUnitarioVencedorDoItemPropostaFornecedor(itemProposta));
                    relItem.setQuantidadeContratada(itemContratoFacade.recuperarQuantidadeOrValorUtilizadoEmOutrosContratosLicitacao(itemProposta));
                    relItem.setQuantidadeDisponivel(relItem.getQuantidade().subtract(relItem.getQuantidadeContratada()));
                    relItem.setValorTotal(relItem.getQuantidade().multiply(relItem.getValorUnitario()));
                    relFornecedor.getItensPorQuantidade().add(relItem);
                } else {
                    relItem.setValorTotal(itemPropostaFornecedorFacade.getValorUnitarioVencedorDoItemPropostaFornecedor(itemProposta));
                    relItem.setValorContratado(itemContratoFacade.recuperarQuantidadeOrValorUtilizadoEmOutrosContratosLicitacao(itemProposta));
                    relItem.setValorDisponivel(relItem.getValorTotal().subtract(relItem.getValorContratado()));
                    relFornecedor.getItensPorValor().add(relItem);
                }
            }
            relAta.getFornecedores().add(relFornecedor);
        }
        relAta.setContratos(buscarDadosAtaContrato(ataRegistroPreco, ataRegistroPreco.getUnidadeOrganizacional()));
        return relAta;
    }

    public List<AtaRegistroPrecoContrato> buscarDadosAtaContrato(AtaRegistroPreco ataRegistroPreco, UnidadeOrganizacional unidadeAta) {
        String sql = " " +
            " select  " +
            "  c.numerocontrato || ' - ' || c.numerotermo ||'/'|| ex.ano " +
            "  || ' - ' || vw.codigo || ' - ' || vw.descricao " +
            "  || ' - ' || formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) || ' - ' || coalesce(pf.nome, pj.razaosocial) as contrato," +
            "  c.id as idcontrato, " +
            "  c.valortotal as valor_total " +
            " from contrato c " +
            "  inner join pessoa p on p.id = c.contratado_id " +
            "  left join pessoafisica pf on pf.id = p.id " +
            "  left join pessoajuridica pj on pj.id = p.id " +
            "  inner join exercicio ex on ex.id = c.exerciciocontrato_id " +
            "  left join conlicitacao conlic on conlic.contrato_id = c.id " +
            "  left join licitacao lic on lic.id = conlic.licitacao_id " +
            "  inner join unidadelicitacao licuni on lic.id = licuni.licitacao_id " +
            "  inner join ataregistropreco ata on ata.licitacao_id = lic.id " +
            "  inner join unidadecontrato uc on uc.contrato_id = c.id " +
            "  inner join vwhierarquiaadministrativa vw on vw.subordinada_id = uc.unidadeadministrativa_id " +
            " where ata.id = :idAta " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(licuni.iniciovigencia) and coalesce(licuni.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   and licuni.unidadeadministrativa_id = :idUnidadeAta " +
            "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vw.iniciovigencia) and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " order by c.numerocontrato ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAta", ataRegistroPreco.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idUnidadeAta", unidadeAta.getId());
        List<AtaRegistroPrecoContrato> toReturn = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                AtaRegistroPrecoContrato item = new AtaRegistroPrecoContrato();
                item.setContrato((String) obj[0]);
                item.setIdContrato(((BigDecimal) obj[1]).longValue());
                BigDecimal valorTotal = (BigDecimal) obj[2];
                item.setContrato(item.getContrato() + ", " + Util.formataValor(valorTotal));
                List<AtaRegistroPrecoItensContrato> itensContrato = buscarDadosItensContrato(item.getIdContrato());
                for (AtaRegistroPrecoItensContrato arpItemContrato : itensContrato) {
                    if (TipoControleLicitacao.QUANTIDADE.equals(arpItemContrato.getTipoControle())) {
                        item.getItensContratoQuantidade().add(arpItemContrato);
                        continue;
                    }
                    item.getItensContratoValor().add(arpItemContrato);
                }
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public List<AtaRegistroPrecoItensContrato> buscarDadosItensContrato(Long idContrato) {
        String sql = " select distinct " +
            "  c.numerocontrato || ' - ' || c.numerotermo ||'/'|| ex.ano" +
            "  || ' - ' || vw.codigo || ' - ' || vw.descricao" +
            "  || ' - ' || formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) || ' - ' || coalesce(pf.nome, pj.razaosocial) as contrato," +
            "  c.id as id_contrato," +
            "  ipc.numero as numero_item," +
            "  ob.descricao as descricao_item," +
            "  to_char(isol.descricaocomplementar) as descricao_objeto_compra,   " +
            "  ic.quantidadetotalcontrato," +
            "  ic.valorunitario," +
            "  ic.valortotal," +
            "  c.valortotal as valor_total_contrato," +
            "  um.id as id_unidade_medida, " +
            "  ic.tipocontrole as tipocontrole " +
            " from contrato c" +
            "         left join pessoa p on p.id = c.contratado_id " +
            "         left join pessoafisica pf on pf.id = p.id " +
            "         left join pessoajuridica pj on pj.id = p.id " +
            "         left join exercicio ex on ex.id = c.exerciciocontrato_id " +
            "         left join itemcontrato ic on ic.contrato_id = c.id " +
            "         left join itemcontratoadesaoataint item on item.itemcontrato_id = ic.id " +
            "         left join itemcontratoitemirp itemirp on ic.id = itemirp.itemcontrato_id " +
            "         left join itemcontratoitempropfornec iciof on ic.id = iciof.itemcontrato_id " +
            "         left join itempropfornec ipf on ipf.id = coalesce(item.itempropostafornecedor_id, itemirp.itempropostafornecedor_id, iciof.itempropostafornecedor_id)" +
            "         left join itemprocessodecompra ipc on ipc.id = ipf.itemprocessodecompra_id " +
            "         left join itemsolicitacao isol on isol.id = ipc.itemsolicitacaomaterial_id " +
            "         left join unidademedida um on um.id = isol.unidademedida_id " +
            "         left join objetocompra ob on ob.id = isol.objetocompra_id " +
            "         left join unidadecontrato uc on uc.contrato_id = c.id " +
            "         left join vwhierarquiaadministrativa vw on vw.subordinada_id = uc.unidadeadministrativa_id " +
            " where c.id  = :idContrato " +
            " and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vw.iniciovigencia) and coalesce(vw.fimvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            " and (ic.quantidadetotalcontrato > 0 or ic.valortotal > 0) " +
            " and c.tipoaquisicao in (:tiposAquisicao) " +
            " order by ipc.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", idContrato);
        q.setParameter("tiposAquisicao", Util.getListOfEnumName(TipoAquisicaoContrato.getTiposLicitacao()));
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        List<AtaRegistroPrecoItensContrato> toReturn = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                AtaRegistroPrecoItensContrato item = new AtaRegistroPrecoItensContrato();
                item.setContrato((String) obj[0]);
                item.setIdContrato(((BigDecimal) obj[1]).longValue());
                item.setNumero(obj[2] != null ? ((BigDecimal) obj[2]).longValue() : null);
                item.setObjetoCompra(obj[3] != null ? (String) obj[3] : null);
                item.setDescricaoComplementar(obj[4] != null ? (String) obj[4] : null);
                item.setQuantidade((BigDecimal) obj[5]);
                item.setValorUnitario((BigDecimal) obj[6]);
                item.setValorTotal((BigDecimal) obj[7]);
                BigDecimal valorTotal = (BigDecimal) obj[8];
                item.setTipoControle(obj[10] != null ? TipoControleLicitacao.valueOf((String) obj[10]) : null);
                item.setContrato(item.getContrato() + ", " + Util.formataValor(valorTotal));
                item.setUnidadeMedida(obj[2] != null ? em.find(UnidadeMedida.class, ((BigDecimal) obj[2]).longValue()) : null);
                item.setMascaraQuantidade(item.getUnidadeMedida() != null ? item.getUnidadeMedida().getMascaraQuantidade().getMascara() : TipoMascaraUnidadeMedidaDTO.DUAS_CASAS_DECIMAL.getMascara());
                item.setMascaraValorUnitario(item.getUnidadeMedida() != null ? item.getUnidadeMedida().getMascaraValorUnitario().getMascara() : TipoMascaraUnidadeMedidaDTO.DUAS_CASAS_DECIMAL.getMascara());
                toReturn.add(item);
            }
        }
        return toReturn;
    }

    public AtaRegistroPrecoFacade getAtaRegistroPrecoFacade() {
        return ataRegistroPrecoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public AdesaoFacade getAdesaoFacade() {
        return adesaoFacade;
    }

    public LicitacaoFacade getLicitacaoFacade() {
        return licitacaoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
