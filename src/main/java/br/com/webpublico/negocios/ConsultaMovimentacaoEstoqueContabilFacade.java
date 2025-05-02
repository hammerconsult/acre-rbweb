package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.MovimentacaoGrupoMaterial;
import br.com.webpublico.entidadesauxiliares.FiltroConsultaMovimentacaoEstoqueContabil;
import br.com.webpublico.entidadesauxiliares.MovimentoEstoqueContabil;
import br.com.webpublico.entidadesauxiliares.VOLiquidacaoDocumentoFiscal;
import br.com.webpublico.enums.*;
import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Stateless
public class ConsultaMovimentacaoEstoqueContabilFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private EstoqueFacade estoqueFacade;
    @EJB
    private MovimentoEstoqueFacade movimentoEstoqueFacade;
    @EJB
    private SaldoGrupoMaterialFacade saldoGrupoMaterialFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private EntradaMaterialFacade entradaMaterialFacade;

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public List<MovimentacaoGrupoMaterial> consultar(FiltroConsultaMovimentacaoEstoqueContabil filtro) {
        String sql = "select distinct id_grupo, grupo " +
            " from (select grupo.id as id_grupo, " +
            "       grupo.codigo || ' - ' || grupo.descricao as grupo " +
            "         from grupomaterial grupo " +
            "         inner join movimentogrupomaterial mgm on grupo.id = mgm.grupomaterial_id " +
            "         where mgm.unidadeorganizacional_id = :idUnidade ";
        sql += filtro.getGrupoMaterial() != null ? " and grupo.id = :idGrupo " : " ";
        sql += ") order by grupo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        if (filtro.getGrupoMaterial() != null) {
            q.setParameter("idGrupo", filtro.getGrupoMaterial().getId());
        }
        List<MovimentacaoGrupoMaterial> movimentos = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                MovimentacaoGrupoMaterial mov = new MovimentacaoGrupoMaterial();
                GrupoMaterial grupoMaterial = em.find(GrupoMaterial.class, ((BigDecimal) obj[0]).longValue());
                filtro.setGrupoMaterial(grupoMaterial);
                mov.setGrupoMaterial(grupoMaterial);
                mov.setEntradasEstoque(buscarEntradasEstoque(filtro));
                mov.setSaidasEstoque(buscarSaidasEstoque(filtro));

                Date dataSaldoAnterior = DataUtil.removerDias(filtro.getDataInicial(), 1);
                mov.setSaldoEstoqueAnterior(estoqueFacade.buscarUltimoSaldoEstoquePorData(filtro.getGrupoMaterial(), filtro.getHierarquiaOrcamentaria().getSubordinada(), TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO, dataSaldoAnterior));
                mov.setSaldoEstoqueAtual(estoqueFacade.buscarUltimoSaldoEstoquePorData(filtro.getGrupoMaterial(), filtro.getHierarquiaOrcamentaria().getSubordinada(), TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO, filtro.getDataFinal()).setScale(2, RoundingMode.HALF_EVEN));
                mov.setMovimentosEstoque(buscarMovimentosEstoque(filtro));

                mov.setMovimentosGrupoMaterialContabil(buscarMovimentosGrupoContabil(filtro, null));
                BigDecimal saldoAtualGrupoContabil = buscarSaldoGrupoContabil(filtro, filtro.getDataFinal());
                mov.setSaldoAtualGrupoContabil(saldoAtualGrupoContabil);
                mov.setSaldoAnteriorGrupoContabil(buscarSaldoGrupoContabil(filtro, dataSaldoAnterior));

                mov.getEntradasContabil().addAll(buscarEntradasContabil(filtro));
                mov.getEntradasContabil().addAll(buscarLiquidacoes(filtro));
                mov.getEntradasContabil().addAll(buscarEstornosLiquidacoes(filtro));

                mov.getMovimentosEntradaContabil().addAll(buscarMovimentosGrupoContabil(filtro, TipoOperacaoBensEstoque.getOperacoesCreditoIntegracaoMateriais()));
                mov.getMovimentosEntradaContabil().addAll(buscarMovimentosGrupoContabil(filtro, Lists.<TipoOperacaoBensEstoque>newArrayList(TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE)));

                mov.setSaidasContabil(buscarSaidasContabil(filtro));
                mov.setMovimentosSaidaContabil(buscarMovimentosGrupoContabil(filtro, TipoOperacaoBensEstoque.getOperacoesDebitoIntegracaoMateriais()));
                movimentos.add(mov);
            }
        }
        return movimentos;
    }

    private BigDecimal buscarSaldoGrupoContabil(FiltroConsultaMovimentacaoEstoqueContabil filtro, Date dataReferencia) {
        return saldoGrupoMaterialFacade.buscarUltimoSaldo(
            filtro.getGrupoMaterial(),
            filtro.getHierarquiaOrcamentaria().getSubordinada(),
            TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO,
            NaturezaTipoGrupoMaterial.ORIGINAL,
            dataReferencia);
    }

    public SaldoGrupoMaterial buscarUltimoSaldoGrupoMaterial(FiltroConsultaMovimentacaoEstoqueContabil filtro) {
        MovimentoGrupoMaterial movGrupo = new MovimentoGrupoMaterial();
        movGrupo.setGrupoMaterial(filtro.getGrupoMaterial());
        movGrupo.setUnidadeOrganizacional(filtro.getHierarquiaOrcamentaria().getSubordinada());
        movGrupo.setTipoEstoque(TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO);
        movGrupo.setNaturezaTipoGrupoMaterial(NaturezaTipoGrupoMaterial.ORIGINAL);
        movGrupo.setDataMovimento(filtro.getDataFinal());
        return saldoGrupoMaterialFacade.buscarUltimoSaldoPorData(movGrupo);
    }

    public List<MovimentoEstoqueContabil> buscarEntradasEstoque(FiltroConsultaMovimentacaoEstoqueContabil filtro) {
        String sql = "" +
            " select id                 as id, " +
            "        numero             as numero, " +
            "        data               as data, " +
            "        numero_entrada     as numero_entrada, " +
            "        data_entrada       as data_entrada, " +
            "        tipo               as tipo, " +
            "        sum(total)         as total " +
            " from (select ent.id                               as id, " +
            "              ent.numero                           as numero, " +
            "              ent.dataconclusao                    as data, " +
            "              null                                 as numero_entrada, " +
            "              null                                 as data_entrada, " +
            "              'ENTRADA_COMPRA'                     as tipo, " +
            "              trunc(item.valortotal,2) as total " +
            "        from entradamaterial ent " +
            "                  inner join entradacompramaterial ecm on ecm.id = ent.id " +
            "                  inner join itementradamaterial item on item.entradamaterial_id = ent.id " +
            "                  inner join movimentoestoque mov on mov.id = item.movimentoestoque_id " +
            "                  inner join material mat on mat.id = mov.material_id " +
            "                  inner join localestoqueorcamentario leo on leo.id = mov.localestoqueorcamentario_id " +
            "          where trunc(ent.dataconclusao) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and leo.unidadeorcamentaria_id = :idUnidade " +
            "        and mat.grupo_id = :idGrupo " +
            "     union all " +
            "       select efet.id                       as id, " +
            "              lev.codigo                           as numero, " +
            "              lev.datalevantamento                 as data, " +
            "              ent.numero                           as numero_entrada, " +
            "              ent.dataentrada                      as data_entrada, " +
            "              'ENTRADA_LEVANTAMENTO'               as tipo, " +
            "              trunc(item.valortotal, 2) as total " +
            "  from efetivacaolevantamentoesto efet " +
            "         inner join entradamaterial ent on ent.id = efet.id " +
            "         inner join itemefetivlevantestoque itemefet on itemefet.efetivacao_id = efet.id " +
            "         inner join levantamentoestoque lev on lev.id = itemefet.levantamentoestoque_id " +
            "         inner join itemlevantamentoestoque item on item.levantamentoestoque_id = lev.id " +
            "         inner join material mat on mat.id = item.material_id " +
            "          where trunc(ent.dataentrada) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and lev.unidadeorcamentaria_id = :idUnidade " +
            "        and mat.grupo_id = :idGrupo " +
            "     union all " +
            "      select ent.id                               as id, " +
            "             ent.numero                           as numero, " +
            "             ent.dataentrada                      as data, " +
            "             null                                 as numero_entrada, " +
            "             null                                 as data_entrada, " +
            "             'ENTRADA_INCORPORACAO'               as tipo, " +
            "             trunc(item.valortotal, 2) as total " +
            "        from entradamaterial ent " +
            "             inner join entradaincorporacao ei on ei.id = ent.id " +
            "             inner join itementradamaterial item on item.entradamaterial_id = ent.id " +
            "             inner join movimentoestoque mov on mov.id = item.movimentoestoque_id " +
            "             inner join material mat on mat.id = mov.material_id " +
            "             inner join localestoqueorcamentario leo on leo.id = mov.localestoqueorcamentario_id " +
            "          where trunc(ent.dataentrada) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "        and leo.unidadeorcamentaria_id = :idUnidade " +
            "        and mat.grupo_id = :idGrupo " +
            "     ) " +
            " group by id, numero, data, numero_entrada, data_entrada, tipo " +
            " order by data, numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        q.setParameter("idGrupo", filtro.getGrupoMaterial().getId());
        List<MovimentoEstoqueContabil> movimentos = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                MovimentoEstoqueContabil mov = new MovimentoEstoqueContabil();
                mov.setId(((BigDecimal) obj[0]).longValue());
                mov.setNumero(((BigDecimal) obj[1]).longValue());
                mov.setData((Date) obj[2]);
                mov.setNumeroEntradaLevantamento(obj[3] != null ? ((BigDecimal) obj[3]).longValue() : null);
                mov.setDataEntradaLevantamento(obj[4] != null ? (Date) obj[4] : null);
                mov.setTipo(MovimentoEstoqueContabil.TipoMovimentoMaterial.valueOf((String) obj[5]));
                mov.setValor((BigDecimal) obj[6]);
                if (filtro.getNotaFiscal()) {
                    buscarDocumentosFiscais(mov);
                }
                movimentos.add(mov);
            }
        }
        return movimentos;
    }

    private void buscarDocumentosFiscais(MovimentoEstoqueContabil mov) {
        if (mov.isEntradaPorCompra()) {
            EntradaCompraMaterial entradaCompraMaterial = (EntradaCompraMaterial) entradaMaterialFacade.recuperarEntradaComDependenciasDocumentos(mov.getId());
            for (DoctoFiscalEntradaCompra doctoEnt : entradaCompraMaterial.getDocumentosFiscais()) {
                String mensagem = "";
                DoctoFiscalLiquidacao doctoEntrada = doctoEnt.getDoctoFiscalLiquidacao();
                if (SituacaoDocumentoFiscalEntradaMaterial.LIQUIDADO.equals(doctoEnt.getSituacao())) {
                    LiquidacaoDoctoFiscal liquidacaoDoctoFiscal = liquidacaoFacade.buscarLiquidacaoDoctoFiscalPorDocumentoFiscal(doctoEntrada);
                    mov.setDocumentoFiscalIntegrado(true);
                    VOLiquidacaoDocumentoFiscal doc = novoVoDocumentoFiscal(doctoEntrada, liquidacaoDoctoFiscal, mensagem);
                    mov.getDocumentosFiscais().add(doc);
                } else {
                    List<LiquidacaoDoctoFiscal> documentos = liquidacaoFacade.buscarDocumentoFiscalNaoIntegradoComLiquidacao(doctoEntrada);
                    if (documentos.isEmpty()) {
                        mensagem = "Documento fiscal da liquidação não encontrado para o documento fiscal da entrada por compra " + doctoEntrada + ".";
                        VOLiquidacaoDocumentoFiscal doc = novoVoDocumentoFiscal(doctoEntrada, null, mensagem);
                        mov.getDocumentosFiscais().add(doc);
                    } else {
                        for (LiquidacaoDoctoFiscal doctoLiquidacao : documentos) {
                            VOLiquidacaoDocumentoFiscal doc = novoVoDocumentoFiscal(doctoEntrada, doctoLiquidacao, mensagem);
                            mov.getDocumentosFiscais().add(doc);
                        }
                    }
                }
            }
        }
    }

    private VOLiquidacaoDocumentoFiscal novoVoDocumentoFiscal(DoctoFiscalLiquidacao doctoEntrada, LiquidacaoDoctoFiscal liquidacaoDoctoFiscal, String mensagem) {
        VOLiquidacaoDocumentoFiscal doc = new VOLiquidacaoDocumentoFiscal();
        doc.setDoctoFiscalLiquidacao(doctoEntrada);
        doc.setLiquidacaoDoctoFiscal(liquidacaoDoctoFiscal);
        doc.setMensagem(mensagem);
        return doc;
    }

    public List<MovimentoEstoqueContabil> buscarSaidasEstoque(FiltroConsultaMovimentacaoEstoqueContabil filtro) {
        String sql = "" +
            " select id        as id, " +
            "       data       as data, " +
            "       numero     as numero, " +
            "       tipo       as tipo, " +
            "       sum(total) as total " +
            " from ( " +
            "         select saida.id                                    as id, " +
            "                saida.dataconclusao                         as data, " +
            "                saida.numero                                as numero, " +
            "                'SAIDA_CONSUMO'                             as tipo, " +
            "                trunc(item.valortotal, 2) as total " +
            "         from saidamaterial saida " +
            "                  inner join saidarequisicaomaterial srm on srm.id = saida.id " +
            "                  inner join requisicaomaterial req on req.id = srm.requisicaomaterial_id " +
            "                  inner join itemsaidamaterial item on item.saidamaterial_id = srm.id " +
            "                  inner join movimentoestoque mov on mov.id = item.movimentoestoque_id " +
            "                  inner join material mat on mat.id = mov.material_id " +
            "                  inner join localestoqueorcamentario leo on leo.id = mov.localestoqueorcamentario_id " +
            "         where req.tiporequisicao = :tipoReqConsumo " +
            "          and trunc(saida.dataconclusao) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "         and leo.unidadeorcamentaria_id = :idUnidade " +
            "         and mat.grupo_id = :idGrupo " +
            "         union all " +
            "         select saida.id                                    as id, " +
            "                saida.dataconclusao                         as data, " +
            "                saida.numero                                as numero, " +
            "                'SAIDA_DESINCORPORACAO'                     as tipo, " +
            "                trunc(item.valortotal, 2) as total " +
            "         from saidamaterial saida " +
            "                  inner join saidamatdesincorporacao srd on srd.id = saida.id " +
            "                  inner join itemsaidamaterial item on item.saidamaterial_id = srd.id " +
            "                  inner join movimentoestoque mov on mov.id = item.movimentoestoque_id " +
            "                  inner join material mat on mat.id = mov.material_id " +
            "                  inner join localestoqueorcamentario leo on leo.id = mov.localestoqueorcamentario_id " +
            "          where trunc(saida.dataconclusao) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "         and leo.unidadeorcamentaria_id = :idUnidade " +
            "         and mat.grupo_id = :idGrupo " +
            "         union all " +
            "         select saida.id                                    as id, " +
            "                saida.datasaida                             as data, " +
            "                saida.numero                                as numero, " +
            "                'SAIDA_TRANSFERENCIA'                       as tipo, " +
            "                trunc(item.valortotal,2) as total " +
            "         from saidamaterial saida " +
            "                  inner join saidarequisicaomaterial srm on srm.id = saida.id " +
            "                  inner join requisicaomaterial req on req.id = srm.requisicaomaterial_id " +
            "                  inner join itemsaidamaterial item on item.saidamaterial_id = srm.id " +
            "                  inner join movimentoestoque mov on mov.id = item.movimentoestoque_id " +
            "                  inner join material mat on mat.id = mov.material_id " +
            "                  inner join localestoqueorcamentario leo on leo.id = mov.localestoqueorcamentario_id " +
            "         where req.tiporequisicao = :tipoReqTransferencia " +
            "          and trunc(saida.datasaida) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "         and leo.unidadeorcamentaria_id = :idUnidade " +
            "         and mat.grupo_id = :idGrupo " +
            "     ) " +
            " group by id, data, numero, tipo " +
            " order by data, numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        q.setParameter("idGrupo", filtro.getGrupoMaterial().getId());
        q.setParameter("tipoReqConsumo", TipoRequisicaoMaterial.CONSUMO.name());
        q.setParameter("tipoReqTransferencia", TipoRequisicaoMaterial.TRANSFERENCIA.name());
        List<MovimentoEstoqueContabil> movimentos = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                MovimentoEstoqueContabil mov = new MovimentoEstoqueContabil();
                mov.setId(((BigDecimal) obj[0]).longValue());
                mov.setData((Date) obj[1]);
                mov.setNumero(obj[2] != null ? ((BigDecimal) obj[2]).longValue() : null);
                mov.setTipo(MovimentoEstoqueContabil.TipoMovimentoMaterial.valueOf((String) obj[3]));
                mov.setValor(((BigDecimal) obj[4]).setScale(2, RoundingMode.HALF_EVEN));
                movimentos.add(mov);
            }
        }
        return movimentos;
    }

    public List<MovimentoEstoqueContabil> buscarMovimentosEstoque(FiltroConsultaMovimentacaoEstoqueContabil filtro) {
        String sql = " " +
            " select " +
            "  mov.datamovimento       as data, " +
            "  mov.tipooperacao        as tipo_operacao, " +
            "  mov.descricaodaoperacao as movimento, " +
            "  case mov.tipooperacao when 'CREDITO' then sum(trunc(mov.financeiro, 2)) else 0 end  as credito," +
            "  case mov.tipooperacao when 'DEBITO' then sum(trunc(mov.financeiro, 2))  else 0 end  as debito " +
            " from movimentoestoque mov " +
            "   inner join estoque est on est.id = mov.estoque_id " +
            "   inner join localestoqueorcamentario leo on leo.id = mov.localestoqueorcamentario_id " +
            "   inner join material mat on mat.id = mov.material_id " +
            " where mat.grupo_id = :grupoMaterial " +
            "   and leo.unidadeorcamentaria_id = :unidade  " +
            "   and est.tipoestoque = :tipoEstoque " +
            "     and trunc(mov.datamovimento) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "   group by mov.datamovimento, mov.descricaodaoperacao, mov.tipooperacao " +
            " order by mov.datamovimento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("grupoMaterial", filtro.getGrupoMaterial().getId());
        q.setParameter("unidade", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        q.setParameter("tipoEstoque", TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));

        List<MovimentoEstoqueContabil> movimentos = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                MovimentoEstoqueContabil mov = new MovimentoEstoqueContabil();
                mov.setData((Date) obj[0]);
                mov.setDescricao((String) obj[2]);
                mov.setCredito((BigDecimal) obj[3]);
                mov.setDebito((BigDecimal) obj[4]);
                movimentos.add(mov);
            }
        }
        return movimentos;
    }

    public List<MovimentoEstoqueContabil> buscarMovimentosGrupoContabil(FiltroConsultaMovimentacaoEstoqueContabil filtro, List<TipoOperacaoBensEstoque> operacoes) {
        String sql = " " +
            " select " +
            "    mov.datamovimento, " +
            "    mov.naturezatipogrupomaterial, " +
            "    mov.operacao," +
            "    mov.tipolancamento," +
            "    case when mov.tipolancamento = :tipoLancamento then sum(mov.credito) else sum(mov.credito) *-1 end as credito," +
            "    case when mov.tipolancamento = :tipoLancamento then sum(mov.debito) else sum(mov.debito) *-1 end as debito " +
            " from movimentogrupomaterial mov " +
            " where trunc(mov.datamovimento) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "   and mov.grupomaterial_id = :grupoMaterial " +
            "   and mov.unidadeorganizacional_id = :unidade " +
            "   and mov.tipoestoque = :tipoEstoque " +
            "   and mov.naturezatipogrupomaterial = :natureza ";
        sql += operacoes != null ? " and mov.operacao in (:operacao) " : "";
        sql += " group by mov.datamovimento, mov.naturezatipogrupomaterial, mov.operacao, mov.tipolancamento " +
            " order by mov.datamovimento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("grupoMaterial", filtro.getGrupoMaterial().getId());
        q.setParameter("unidade", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        q.setParameter("tipoEstoque", TipoEstoque.MATERIAL_CONSUMO_PRINCIPAL_ALMOXARIFADO.name());
        q.setParameter("tipoLancamento", TipoLancamento.NORMAL.name());
        q.setParameter("natureza", NaturezaTipoGrupoMaterial.ORIGINAL.name());
        q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        if (operacoes != null) {
            q.setParameter("operacao", TipoOperacaoBensEstoque.getOperacoesAsString(operacoes));
        }

        List<MovimentoEstoqueContabil> movimentos = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                MovimentoEstoqueContabil mov = new MovimentoEstoqueContabil();
                mov.setData((Date) obj[0]);
                NaturezaTipoGrupoMaterial natureza = NaturezaTipoGrupoMaterial.valueOf((String) obj[1]);
                TipoOperacaoBensEstoque operacao = TipoOperacaoBensEstoque.valueOf((String) obj[2]);
                mov.setDescricao(natureza + " / " + operacao);
                mov.setTipoLancamento(TipoLancamento.valueOf((String) obj[3]));
                mov.setCredito((BigDecimal) obj[4]);
                mov.setDebito((BigDecimal) obj[5]);
                movimentos.add(mov);
            }
        }
        return movimentos;
    }

    public List<MovimentoEstoqueContabil> buscarLiquidacoes(FiltroConsultaMovimentacaoEstoqueContabil filtro) {
        String sql = " " +
            " select liq.id, " +
            "        liq.dataliquidacao, " +
            "        liq.numero, " +
            "        case when liq.categoriaorcamentaria = :categoriaNormal then 'LIQUIDACAO' else 'LIQUIDACAO_RESTO' end as tipo, " +
            "        liq.categoriaorcamentaria as categoria, " +
            "        desd.valor " +
            " from liquidacao liq " +
            "   inner join desdobramento desd on desd.liquidacao_id = liq.id " +
            "   inner join conta cliq on cliq.id = desd.conta_id " +
            "   inner join configgrupomaterial cgm on cgm.contadespesa_id = cliq.id " +
            "   inner join grupomaterial grupo on grupo.id = cgm.grupomaterial_id " +
            " where liq.unidadeorganizacional_id = :idUnidade " +
            "   and grupo.id = :idGrupo " +
            "   and trunc(liq.dataliquidacao) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "   and trunc(liq.dataliquidacao) between trunc(cgm.iniciovigencia) and coalesce(trunc(cgm.fimvigencia), liq.dataliquidacao) " +
            " order by liq.dataliquidacao, liq.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        q.setParameter("idGrupo", filtro.getGrupoMaterial().getId());
        q.setParameter("categoriaNormal", CategoriaOrcamentaria.NORMAL.name());
        List<MovimentoEstoqueContabil> movimentos = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                MovimentoEstoqueContabil mov = new MovimentoEstoqueContabil();
                mov.setId(((BigDecimal) obj[0]).longValue());
                mov.setData((Date) obj[1]);
                mov.setNumero(Long.valueOf((String) obj[2]));
                mov.setTipo(MovimentoEstoqueContabil.TipoMovimentoMaterial.valueOf((String) obj[3]));
                mov.setRestoPagar(CategoriaOrcamentaria.valueOf((String) obj[4]).isResto());
                mov.setValor((BigDecimal) obj[5]);
                mov.setDescricao(TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE.getDescricao());
                mov.setTipoLancamento(TipoLancamento.NORMAL);
                movimentos.add(mov);
            }
        }
        return movimentos;
    }

    public List<MovimentoEstoqueContabil> buscarEstornosLiquidacoes(FiltroConsultaMovimentacaoEstoqueContabil filtro) {
        String sql = " " +
            " select est.id, " +
            "        est.dataestorno, " +
            "        est.numero, " +
            "        case when liq.categoriaorcamentaria = :categoriaNormal then 'LIQUIDACAO' else 'LIQUIDACAO_RESTO' end as tipo, " +
            "        liq.categoriaorcamentaria as categoria, " +
            "        desd.valor " +
            " from liquidacaoestorno est " +
            "   inner join liquidacao liq on liq.id = est.liquidacao_id    " +
            "   inner join desdobramentoliqestorno desd on desd.liquidacaoestorno_id = est.id " +
            "   inner join conta cliq on cliq.id = desd.conta_id " +
            "   inner join configgrupomaterial cgm on cgm.contadespesa_id = cliq.id " +
            "   inner join grupomaterial grupo on grupo.id = cgm.grupomaterial_id " +
            " where liq.unidadeorganizacional_id = :idUnidade " +
            "   and grupo.id = :idGrupo " +
            "   and trunc(est.dataestorno) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "   and trunc(est.dataestorno) between trunc(cgm.iniciovigencia) and coalesce(trunc(cgm.fimvigencia), est.dataestorno) " +
            " order by est.dataestorno, liq.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        q.setParameter("idGrupo", filtro.getGrupoMaterial().getId());
        q.setParameter("categoriaNormal", CategoriaOrcamentaria.NORMAL.name());
        List<MovimentoEstoqueContabil> movimentos = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                MovimentoEstoqueContabil mov = new MovimentoEstoqueContabil();
                mov.setId(((BigDecimal) obj[0]).longValue());
                mov.setData((Date) obj[1]);
                mov.setNumero(Long.valueOf((String) obj[2]));
                mov.setTipo(MovimentoEstoqueContabil.TipoMovimentoMaterial.valueOf((String) obj[3]));
                mov.setRestoPagar(CategoriaOrcamentaria.valueOf((String) obj[4]).isResto());
                mov.setValor((BigDecimal) obj[5]);
                mov.setDescricao(TipoOperacaoBensEstoque.AQUISICAO_BENS_ESTOQUE.getDescricao());
                mov.setTipoLancamento(TipoLancamento.ESTORNO);
                movimentos.add(mov);
            }
        }
        return movimentos;
    }

    public List<MovimentoEstoqueContabil> buscarSaidasContabil(FiltroConsultaMovimentacaoEstoqueContabil filtro) {
        String sql = " " +
            " select * from ( " +
            "         select bens.id                   as id, " +
            "                bens.databem              as data, " +
            "                bens.numero               as numero, " +
            "                bens.operacoesbensestoque as operacao, " +
            "                bens.tipolancamento       as tipo_lancamento, " +
            "                'BENS_ESTOQUE'            as tipo, " +
            "                case when bens.tipolancamento = :tipoLancamento then bens.valor else bens.valor * -1 end as total " +
            "         from bensestoque bens " +
            "         where trunc(bens.databem) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "           and bens.unidadeorganizacional_id = :idUnidade " +
            "           and bens.grupomaterial_id = :idGrupo " +
            "           and bens.operacoesbensestoque in (:operacaoBens) " +
            "         union all " +
            "         select transf.id                as id, " +
            "                transf.datatransferencia as data, " +
            "                transf.numero            as numero, " +
            "                transf.operacaoorigem    as operacao, " +
            "                transf.tipolancamento    as tipo_lancamento, " +
            "                'TRANSF_BENS_ESTOQUE'    as tipo, " +
            "                case when transf.tipolancamento = :tipoLancamento then transf.valor else transf.valor * -1 end as total " +
            "         from transfbensestoque transf " +
            "         where trunc(transf.datatransferencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "           and transf.unidadeorgorigem_id = :idUnidade " +
            "           and transf.grupomaterial_id = :idGrupo " +
            "           and transf.operacaoorigem in (:operacaoTransf) " +
            "     ) " +
            "order by data, to_number(numero) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        q.setParameter("idGrupo", filtro.getGrupoMaterial().getId());
        q.setParameter("tipoLancamento", TipoLancamento.NORMAL.name());
        q.setParameter("operacaoBens", TipoOperacaoBensEstoque.getOperacoesAsString(TipoOperacaoBensEstoque.getOperacoesDebitoIntegracaoMateriais()));
        q.setParameter("operacaoTransf", TipoOperacaoBensEstoque.getOperacoesAsString(Lists.<TipoOperacaoBensEstoque>newArrayList(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_CONCEDIDA)));
        List<MovimentoEstoqueContabil> movimentos = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                MovimentoEstoqueContabil mov = new MovimentoEstoqueContabil();
                mov.setId(((BigDecimal) obj[0]).longValue());
                mov.setData((Date) obj[1]);
                mov.setNumero(Long.valueOf((String) obj[2]));
                mov.setDescricao(TipoOperacaoBensEstoque.valueOf((String) obj[3]).getDescricao());
                mov.setTipoLancamento(TipoLancamento.valueOf((String) obj[4]));
                mov.setTipo(MovimentoEstoqueContabil.TipoMovimentoMaterial.valueOf((String) obj[5]));
                mov.setValor((BigDecimal) obj[6]);
                movimentos.add(mov);
            }
        }
        return movimentos;
    }

    public List<MovimentoEstoqueContabil> buscarEntradasContabil(FiltroConsultaMovimentacaoEstoqueContabil filtro) {
        String sql = " " +
            " select * from ( " +
            "         select bens.id                   as id, " +
            "                bens.databem              as data, " +
            "                bens.numero               as numero, " +
            "                bens.operacoesbensestoque as operacao, " +
            "                'BENS_ESTOQUE'            as tipo, " +
            "                bens.tipolancamento       as tipo_lancamento, " +
            "                case when bens.tipolancamento = :tipoLancamento then bens.valor else bens.valor * -1 end as total " +
            "         from bensestoque bens " +
            "         where trunc(bens.databem) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "           and bens.unidadeorganizacional_id = :idUnidade " +
            "           and bens.grupomaterial_id = :idGrupo " +
            "           and bens.operacoesbensestoque in (:operacaoBens) " +
            "         union all " +
            "         select transf.id                as id, " +
            "                transf.datatransferencia as data, " +
            "                transf.numero            as numero, " +
            "                transf.operacaodestino   as operacao, " +
            "                'TRANSF_BENS_ESTOQUE'    as tipo, " +
            "                transf.tipolancamento    as tipo_lancamento, " +
            "                case when transf.tipolancamento = :tipoLancamento then transf.valor else transf.valor * -1 end as total " +
            "         from transfbensestoque transf " +
            "         where trunc(transf.datatransferencia) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') " +
            "           and transf.unidadeorgdestino_id = :idUnidade " +
            "           and transf.grupomaterialdestino_id = :idGrupo " +
            "           and transf.operacaodestino in (:operacaoTransf) " +
            "     ) " +
            "order by data, to_number(numero) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", filtro.getHierarquiaOrcamentaria().getSubordinada().getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        q.setParameter("idGrupo", filtro.getGrupoMaterial().getId());
        q.setParameter("operacaoBens", TipoOperacaoBensEstoque.getOperacoesAsString(TipoOperacaoBensEstoque.getOperacoesCreditoIntegracaoMateriais()));
        q.setParameter("operacaoTransf", TipoOperacaoBensEstoque.getOperacoesAsString(Lists.<TipoOperacaoBensEstoque>newArrayList(TipoOperacaoBensEstoque.TRANSFERENCIA_BENS_ESTOQUE_RECEBIDA)));
        q.setParameter("tipoLancamento", TipoLancamento.NORMAL.name());
        List<MovimentoEstoqueContabil> movimentos = Lists.newArrayList();
        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            for (Object[] obj : resultList) {
                MovimentoEstoqueContabil mov = new MovimentoEstoqueContabil();
                mov.setId(((BigDecimal) obj[0]).longValue());
                mov.setData((Date) obj[1]);
                mov.setNumero(Long.valueOf((String) obj[2]));
                mov.setDescricao(TipoOperacaoBensEstoque.valueOf((String) obj[3]).getDescricao());
                mov.setTipo(MovimentoEstoqueContabil.TipoMovimentoMaterial.valueOf((String) obj[4]));
                mov.setTipoLancamento(TipoLancamento.valueOf((String) obj[5]));
                mov.setValor((BigDecimal) obj[6]);
                movimentos.add(mov);
            }
        }
        return movimentos;
    }

    public SaldoGrupoMaterialFacade getSaldoGrupoMaterialFacade() {
        return saldoGrupoMaterialFacade;
    }

    public void setSaldoGrupoMaterialFacade(SaldoGrupoMaterialFacade saldoGrupoMaterialFacade) {
        this.saldoGrupoMaterialFacade = saldoGrupoMaterialFacade;
    }
}
