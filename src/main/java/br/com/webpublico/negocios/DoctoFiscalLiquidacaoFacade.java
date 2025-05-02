package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.OrigemDoctoFiscalLiquidacaoVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.rh.esocial.ConfiguracaoEmpregadorESocialFacade;
import br.com.webpublico.reinf.eventos.EventoReinfDTO;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 26/05/14
 * Time: 17:18
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class DoctoFiscalLiquidacaoFacade extends AbstractFacade<DoctoFiscalLiquidacao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoDocumentoFiscalFacade tipoDocumentoFiscalFacade;
    @EJB
    private UFFacade ufFacade;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ConfiguracaoEmpregadorESocialFacade configuracaoEmpregadorESocialFacade;

    public DoctoFiscalLiquidacaoFacade() {
        super(DoctoFiscalLiquidacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void salvar(LiquidacaoDoctoFiscal liquidacaoDoctoFiscal, boolean isContaDespesaEventoReinf, ConfiguracaoContabil configuracaoContabil) {
        validarReinf(liquidacaoDoctoFiscal, isContaDespesaEventoReinf, configuracaoContabil);
        try {
            em.merge(liquidacaoDoctoFiscal);
        } catch (Exception ex) {
            logger.error("Problema ao alterar", ex);
        }
    }

    private void validarReinf(LiquidacaoDoctoFiscal liquidacaoDoctoFiscal, boolean isContaDespesaEventoReinf, ConfiguracaoContabil configuracaoContabil) {
        if (isContaDespesaEventoReinf) {
            ValidacaoException ve = new ValidacaoException();
            BigDecimal porcentagem = configuracaoContabil.getPorcentagemMinimaCalculoBase();
            BigDecimal valorMinimoDocumentoFiscal = liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getValor().multiply(porcentagem).divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

            if (liquidacaoDoctoFiscal.getRetencaoPrevidenciaria()) {
                if (liquidacaoDoctoFiscal.getContaExtra() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Conta Extraorçamentária INSS padrão na configuração da REINF.");
                }
                if (liquidacaoDoctoFiscal.getPorcentagemRetencaoMaxima() == null || liquidacaoDoctoFiscal.getPorcentagemRetencaoMaxima().compareTo(BigDecimal.ZERO) == 0) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo % INSS Retido deve ser informado e maior que 0.");
                }
                ve.lancarException();
                BigDecimal porcentagemRetencaoMaximaContaExtraInss = getRetencaoMaximaContaExtra(liquidacaoDoctoFiscal.getContaExtra());
                if (Util.isNotNull(porcentagemRetencaoMaximaContaExtraInss) && porcentagemRetencaoMaximaContaExtraInss.compareTo(BigDecimal.ZERO) != 0) {
                    if (liquidacaoDoctoFiscal.getPorcentagemRetencaoMaxima().compareTo(porcentagemRetencaoMaximaContaExtraInss) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A porcentagem máxima de retenção permitida para Conta Extraorçamentária " + liquidacaoDoctoFiscal.getContaExtra().toString() + " é de " + porcentagemRetencaoMaximaContaExtraInss + "%.");
                    }
                }

                if (liquidacaoDoctoFiscal.getValorBaseCalculo().compareTo(liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getValor()) > 0 || liquidacaoDoctoFiscal.getValorBaseCalculo().compareTo(valorMinimoDocumentoFiscal) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor base de cálculo <b>" + Util.formataValor(liquidacaoDoctoFiscal.getValorBaseCalculo()) +
                        "</b> deve ser menor ou igual o valor do documento <b>" + Util.formataValor(liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getValor()) +
                        "</b> e maior que " + porcentagem + "% do valor do documento <b>" + Util.formataValor(valorMinimoDocumentoFiscal) + "</b>.");
                }

                if (liquidacaoDoctoFiscal.getTipoServicoReinf() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Tipo de Serviço Reinf deve ser informado.");
                }
                if (configuracaoContabil.getPessoaInssPadraoDocLiq() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Configuração REINF para Pessoa Padrão INSS.");
                }
                if (configuracaoContabil.getClasseCredInssPadraoDocLiq() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Configuração REINF para Classe Padrão INSS.");
                }
            }

            if (!liquidacaoDoctoFiscal.getOptanteSimplesNacional()) {
                if (liquidacaoDoctoFiscal.getContaExtraIrrf() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Conta Extraorçamentária IRRF padrão na configuração da REINF.");
                }
                if (liquidacaoDoctoFiscal.getPorcentagemRetencaoMaximaIrrf() == null || liquidacaoDoctoFiscal.getPorcentagemRetencaoMaximaIrrf().compareTo(BigDecimal.ZERO) == 0) {
                    ve.adicionarMensagemDeCampoObrigatorio("O campo % IR Retido deve ser informado e maior que 0.");
                }
                ve.lancarException();
                BigDecimal porcentagemRetencaoMaximaContaExtraIrrf = getRetencaoMaximaContaExtra(liquidacaoDoctoFiscal.getContaExtraIrrf());
                if (Util.isNotNull(porcentagemRetencaoMaximaContaExtraIrrf) && porcentagemRetencaoMaximaContaExtraIrrf.compareTo(BigDecimal.ZERO) != 0) {
                    if (liquidacaoDoctoFiscal.getPorcentagemRetencaoMaximaIrrf().compareTo(porcentagemRetencaoMaximaContaExtraIrrf) > 0) {
                        ve.adicionarMensagemDeOperacaoNaoPermitida("A porcentagem máxima de retenção permitida para Conta Extraorçamentária " + liquidacaoDoctoFiscal.getContaExtraIrrf().toString() + " é de " + porcentagemRetencaoMaximaContaExtraIrrf + "%.");
                    }
                }

                if (liquidacaoDoctoFiscal.getValorBaseCalculoIrrf().compareTo(liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getValor()) > 0 || liquidacaoDoctoFiscal.getValorBaseCalculoIrrf().compareTo(valorMinimoDocumentoFiscal) < 0) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O Valor base de cálculo <b>" + Util.formataValor(liquidacaoDoctoFiscal.getValorBaseCalculoIrrf()) +
                        "</b> deve ser menor ou igual o valor do documento <b>" + Util.formataValor(liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getValor()) +
                        "</b> e maior que " + porcentagem + "% do valor do documento <b>" + Util.formataValor(valorMinimoDocumentoFiscal) + "</b>.");
                }

                if (liquidacaoDoctoFiscal.getNaturezaRendimentoReinf() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Natureza de Rendimentos Reinf deve ser informado.");
                }
                if (configuracaoContabil.getPessoaIrrfPadraoDocLiq() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Configuração REINF para Pessoa Padrão IRRF.");
                }
                if (configuracaoContabil.getClasseCredIrrfPadraoDocLiq() == null) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi encontrado Configuração REINF para Classe Padrão IRRF.");
                }
            }

            UnidadeOrganizacional uo = sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente();
            Date dataDocto = liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getDataDocto();
            TipoDocumentoFiscal tipoDocumentoFiscal = liquidacaoDoctoFiscal.getDoctoFiscalLiquidacao().getTipoDocumentoFiscal();
            if (tipoDocumentoFiscal.getValidarCopetencia() && faseFacade.temBloqueioFaseParaRecurso("/financeiro/orcamentario/liquidacao/edita.xhtml", dataDocto, uo, exercicioFacade.getExercicioPorAno(DataUtil.getAno(dataDocto)))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data do documento comprobatório <b>" + DataUtil.getDataFormatada(dataDocto) + "</b> está fora do período fase para a unidade <b>" +
                    hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(dataDocto, uo, TipoHierarquiaOrganizacional.ORCAMENTARIA).toString() + "</b>");
            }
            ve.lancarException();
        }
    }

    public List<DoctoFiscalLiquidacao> buscarDocumentoFiscalPorGrupoMaterial(String parte, Empenho empenho) {
        String sql = " select distinct documento.* from entradacompramaterial entrada " +
            "           inner join entradamaterial ent on ent.id = entrada.id " +
            "           inner join requisicaodecompra compra on compra.id = entrada.requisicaodecompra_id " +
            "           inner join itementradamaterial item on item.entradamaterial_id = entrada.id " +
            "           inner join itemdoctoitementrada associacao on associacao.itementradamaterial_id = item.id " +
            "           inner join doctofiscalentradacompra itemdoc on itemdoc.id = associacao.doctofiscalentradacompra_id " +
            "           inner join doctofiscalliquidacao documento on documento.id = itemdoc.doctofiscalliquidacao_id " +
            "           inner join tipodocumentofiscal tipo on tipo.id = documento.tipodocumentofiscal_id " +
            "           left join requisicaocompraexecucao rce on rce.requisicaocompra_id = compra.id " +
            "           left join execucaocontrato ex on ex.id = rce.execucaocontrato_id " +
            "           left join execucaocontratoempenho exemp on exemp.execucaocontrato_id = ex.id " +
            "           left join execucaoprocesso exProc on exProc.id = rce.execucaoprocesso_id " +
            "           left join execucaoprocessoempenho exEmpProc on exProc.id = exEmpProc.execucaoprocesso_id " +
            "           left join reconhecimentodivida rd on rd.id = compra.reconhecimentodivida_id " +
            "           left join solicitacaoempenho sol on sol.reconhecimentodivida_id = rd.id " +
            "           inner join empenho emp on emp.id = coalesce(exemp.empenho_id, exEmpProc.empenho_id, sol.empenho_id)" +
            "          where emp.id = :idEmpenho " +
            "           and (tipo.codigo like :parte or lower(tipo.descricao) like :parte or documento.numero like :parte )" +
            "           and ent.situacao = :situacaoEntrada ";
        Query q = em.createNativeQuery(sql, DoctoFiscalLiquidacao.class);
        q.setParameter("idEmpenho", empenho.getId());
        q.setParameter("situacaoEntrada", SituacaoEntradaMaterial.ATESTO_PROVISORIO_AGUARDANDO_LIQUIDACAO.name());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        }
        return (List<DoctoFiscalLiquidacao>) resultList;
    }

    public List<DoctoFiscalLiquidacao> buscarDocumentoFiscalPorGrupoBem(String parte, Empenho empenho) {
        String sql = " " +
            "   select distinct dfl.* from aquisicao aq " +
            "     inner join solicitacaoaquisicao solAq on solAq.id = aq.solicitacaoaquisicao_id " +
            "     inner join itemaquisicao iaq on iaq.aquisicao_id = aq.id " +
            "     inner join itemsolicitacaoaquisicao isaq on iaq.itemsolicitacaoaquisicao_id = isaq.id " +
            "     inner join itemdoctoitemaquisicao idfaq ON idfaq.id = isaq.itemdoctoitemaquisicao_id " +
            "     inner join itemdoctofiscalliquidacao idfl ON idfl.id = idfaq.itemdoctofiscalliquidacao_id " +
            "     inner join doctofiscalliquidacao dfl ON dfl.id = idfl.doctofiscalliquidacao_id " +
            "     inner join tipodocumentofiscal tdf on dfl.tipodocumentofiscal_id = tdf.id " +
            "     inner join requisicaodecompra req on req.id = solAq.requisicaodecompra_id " +
            "     left join requisicaocompraexecucao reqExec on reqExec.requisicaocompra_id = req.id " +
            "     left join execucaocontrato ex on ex.id = reqExec.execucaocontrato_id " +
            "     left join execucaocontratoempenho execemp on ex.id = execemp.execucaocontrato_id " +
            "     left join execucaoprocesso exProc on exProc.id = reqExec.execucaoprocesso_id " +
            "     left join execucaoprocessoempenho exEmpProc on exProc.id = exEmpProc.execucaoprocesso_id " +
            "     left join reconhecimentodivida rd on rd.id = req.reconhecimentodivida_id " +
            "     left join solicitacaoempenho sol on sol.reconhecimentodivida_id = rd.id " +
            "     inner join empenho emp on emp.id = coalesce(execemp.empenho_id, exEmpProc.empenho_id, sol.empenho_id)" +
            "     left join liquidacao liq on liq.empenho_id = emp.id " +
            "   where dfl.total > (COALESCE((select SUM(lqd.valorliquidado) " +
            "                      from liquidacaodoctofiscal lqd " +
            "                       where Lqd.Doctofiscalliquidacao_Id = Dfl.Id " +
            "                       and lqd.liquidacao_id = liq.id) - (select coalesce(SUM(est.valor), 0) " +
            "                                                          from liquidacaoestorno est " +
            "                                                          where est.liquidacao_id = liq.id), 0)) " +
            "   and emp.id = :idEmpenho " +
            "   and (tdf.codigo like :parte or lower(tdf.descricao) like :parte or dfl.numero like :parte) " +
            "   and aq.situacao = :situacaoAquisicao ";
        Query q = em.createNativeQuery(sql, DoctoFiscalLiquidacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("idEmpenho", empenho.getId());
        q.setParameter("situacaoAquisicao", SituacaoAquisicao.FINALIZADO.name());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return (List<DoctoFiscalLiquidacao>) q.getResultList();
        }
    }


    public List<DoctoFiscalLiquidacao> buscarDocumentoFiscalPorObrigacoesPagar(List<Long> idsObrigacao, String parte) {
        String sql = "" +
            "  select DISTINCT documento.* " +
            "   from doctofiscalliquidacao documento " +
            "    inner join tipodocumentofiscal tipo on tipo.id = documento.tipodocumentofiscal_id " +
            "    inner join obrigacaoapagardoctofiscal opDocto on opdocto.documentofiscal_id = documento.id " +
            "    where opdocto.obrigacaoapagar_id in (:idsObrigacao) " +
            "    and (tipo.codigo like :parte " +
            "     or lower(tipo.descricao) like :parte " +
            "     or documento.numero like :parte ) ";
        Query q = em.createNativeQuery(sql, DoctoFiscalLiquidacao.class);
        q.setParameter("idsObrigacao", idsObrigacao);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }


    public List<DoctoFiscalLiquidacao> buscarDocumentoFiscalPorObrigacaoPagar(ObrigacaoAPagar obrigacaoAPagar, String parte) {
        String sql = "" +
            "  select DISTINCT documento.* " +
            "   from doctofiscalliquidacao documento " +
            "    inner join tipodocumentofiscal tipo on tipo.id = documento.tipodocumentofiscal_id " +
            "    inner join obrigacaoapagardoctofiscal opDocto on opdocto.documentofiscal_id = documento.id " +
            "    where opdocto.obrigacaoapagar_id = :idsObrigacao " +
            "    and (tipo.codigo like :parte " +
            "     or lower(tipo.descricao) like :parte " +
            "     or documento.numero like :parte ) ";
        Query q = em.createNativeQuery(sql, DoctoFiscalLiquidacao.class);
        q.setParameter("idsObrigacao", obrigacaoAPagar.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }


    public List<DoctoFiscalLiquidacao> buscarDocumentoFiscalPorLiquidacao(Liquidacao liquidacao, String parte) {
        String sql = "" +
            "  select DISTINCT documento.* " +
            "   from doctofiscalliquidacao documento " +
            "    inner join tipodocumentofiscal tipo on tipo.id = documento.tipodocumentofiscal_id " +
            "    inner join liquidacaodoctofiscal liqDocto on liqDocto.doctoFiscalLiquidacao_id = documento.id " +
            "    where liqDocto.liquidacao_id = :idLiquidacao " +
            "    and (tipo.codigo like :parte " +
            "     or lower(tipo.descricao) like :parte " +
            "     or documento.numero like :parte ) ";
        Query q = em.createNativeQuery(sql, DoctoFiscalLiquidacao.class);
        q.setParameter("idLiquidacao", liquidacao.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(50);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<DoctoFiscalLiquidacao> buscarDoctoFiscalLiquidacao(EntradaCompraMaterial entradaCompra) {
        String sql = " select docto.* from doctofiscalliquidacao docto " +
            "           inner join doctofiscalentradacompra dfe on dfe.doctofiscalliquidacao_id = docto.id" +
            "           where dfe.entradacompramaterial_id =:idEntrada ";
        Query q = em.createNativeQuery(sql, DoctoFiscalLiquidacao.class);
        q.setParameter("idEntrada", entradaCompra.getId());
        return q.getResultList();
    }

    public BigDecimal buscarValorLiquidado(DoctoFiscalLiquidacao documentoFiscal) {
        String sql = "" +
            "  select  coalesce(lqd.valor,0) - coalesce(estorno.valor, 0) as saldo " +
            "         from ( " +
            "              select docto.id as idDocumento, coalesce(sum(ldf.valorliquidado),0) as valor  " +
            "              from liquidacao liq  " +
            "              inner join liquidacaodoctofiscal ldf on ldf.liquidacao_id = liq.id  " +
            "              inner join doctofiscalliquidacao docto on docto.id = ldf.doctofiscalliquidacao_id " +
            "              where docto.id = :idDocumentoFiscal " +
            "              group by docto.id " +
            "              ) lqd " +
            "              left join ( " +
            "              select docto.id as idDocumento, coalesce(sum(ldf.valor),0) as valor  " +
            "              from liquidacao liq  " +
            "              inner join liquidacaoestorno liqest on liqest.liquidacao_id = liq.id  " +
            "              inner join liquidacaoestdoctofiscal ldf on ldf.liquidacaoestorno_id = liqest.id " +
            "              inner join doctofiscalliquidacao docto on docto.id = ldf.documentofiscal_id " +
            "              where docto.id = :idDocumentoFiscal" +
            "              group by docto.id " +
            "              ) estorno on estorno.idDocumento = lqd.idDocumento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocumentoFiscal", documentoFiscal.getId());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public BigDecimal getValorLiquidadoEntradaPorCompra(EntradaMaterial entradaMaterial) {
        String sql = "" +
            "  select  sum(coalesce(lqd.valor,0) - coalesce(estorno.valor, 0)) as valor_liquidado " +
            "         from ( " +
            "              select docto.id as idDocumento, coalesce(sum(ldf.valorliquidado),0) as valor  " +
            "              from liquidacao liq  " +
            "              inner join liquidacaodoctofiscal ldf on ldf.liquidacao_id = liq.id  " +
            "              inner join doctofiscalliquidacao docto on docto.id = ldf.doctofiscalliquidacao_id " +
            "              inner join doctofiscalentradacompra docEnt on docEnt.doctofiscalliquidacao_id = docto.id " +
            "              where docEnt.entradaCompraMaterial_id = :idEntrada " +
            "              group by docto.id " +
            "              ) lqd " +
            "              left join ( " +
            "              select docto.id as idDocumento, coalesce(sum(ldf.valor),0) as valor  " +
            "              from liquidacao liq  " +
            "              inner join liquidacaoestorno liqest on liqest.liquidacao_id = liq.id  " +
            "              inner join liquidacaoestdoctofiscal ldf on ldf.liquidacaoestorno_id = liqest.id " +
            "              inner join doctofiscalliquidacao docto on docto.id = ldf.documentofiscal_id " +
            "              inner join doctofiscalentradacompra docEnt on docEnt.doctofiscalliquidacao_id = docto.id " +
            "              where docEnt.entradaCompraMaterial_id = :idEntrada " +
            "              group by docto.id " +
            "              ) estorno on estorno.idDocumento = lqd.idDocumento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEntrada", entradaMaterial.getId());
        q.setMaxResults(1);
        if (q.getResultList().isEmpty()) {
            return BigDecimal.ZERO;
        }
        return (BigDecimal) q.getSingleResult();
    }

    public List<ItemDoctoFiscalLiquidacao> buscarItensDocumento(DoctoFiscalLiquidacao documento) {
        String sql = " select item.* from itemdoctofiscalliquidacao item where item.doctofiscalliquidacao_id = :idDocumento ";
        Query q = em.createNativeQuery(sql, ItemDoctoFiscalLiquidacao.class);
        q.setParameter("idDocumento", documento.getId());
        return q.getResultList();
    }

    public LiquidacaoDoctoFiscal recuperarLiquidacaoDoctoFiscalPorDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        if (doctoFiscalLiquidacao == null || doctoFiscalLiquidacao.getId() == null) return null;
        String sql = " select * from liquidacaodoctofiscal where doctofiscalliquidacao_id = :idDoc ";
        Query q = em.createNativeQuery(sql, LiquidacaoDoctoFiscal.class);
        q.setParameter("idDoc", doctoFiscalLiquidacao.getId());
        List<LiquidacaoDoctoFiscal> resultado = q.getResultList();
        return !resultado.isEmpty() ? resultado.get(0) : null;
    }

    public boolean isDocumentoFiscalLiquidacaoReinf(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        if (doctoFiscalLiquidacao == null || doctoFiscalLiquidacao.getId() == null) return false;
        String sql = " select doc.id " +
            "from doctofiscalliquidacao doc " +
            "         inner join liquidacaodoctofiscal doctoliq on doctoliq.doctofiscalliquidacao_id = doc.id " +
            "         inner join liquidacao liq on liq.id = doctoliq.liquidacao_id " +
            "         inner join empenho emp on emp.id = liq.empenho_id " +
            "where emp.tipocontadespesa in (select cctipoconta.tipocontadespesa from configconttipocontadesp cctipoconta) " +
            "  and doc.id = :idDoc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDoc", doctoFiscalLiquidacao.getId());
        List<BigDecimal> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }

    public boolean hasPagamentoNaoEstornadoParaDoctoFiscalLiquidacao(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        if (doctoFiscalLiquidacao == null || doctoFiscalLiquidacao.getId() == null) return false;
        String sql = " select pag.id " +
            "from pagamento pag " +
            "         inner join liquidacaodoctofiscal doctoliq on doctoliq.liquidacao_id = pag.liquidacao_id " +
            "         left join pagamentoestorno estpag on estpag.pagamento_id = pag.id " +
            "where doctoliq.doctofiscalliquidacao_id = :idDoc " +
            "  and estpag.id is null ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDoc", doctoFiscalLiquidacao.getId());
        List<BigDecimal> resultado = q.getResultList();
        return resultado != null && !resultado.isEmpty();
    }

    public OrigemDoctoFiscalLiquidacaoVO buscarOrigemDoctoFiscalLiquidacaoVO(DoctoFiscalLiquidacao doctoFiscalLiquidacao) {
        if (doctoFiscalLiquidacao == null || doctoFiscalLiquidacao.getId() == null) return null;
        String sql = " select distinct coalesce(em.id, aq.id, liqest.id, liq.id) as idOrigem, " +
            "                case " +
            "                    when em.id is not null then 'EntradaMaterial' " +
            "                    when aq.id is not null then 'Aquisicao' " +
            "                    when liqest.id is not null then 'LiquidacaoEstorno' " +
            "                    else 'Liquidacao' end as origem, " +
            "                case " +
            "                    when em.id is not null then em.situacao " +
            "                    when aq.id is not null then aq.situacao " +
            "                    else 'CONCLUIDA' " +
            "                    end as situacao, " +
            "                coalesce(solaq.motivo, em.historico, liq.complemento) as descricao, " +
            "                coalesce(liqest.categoriaorcamentaria, liq.categoriaorcamentaria) as categoriaorcamentaria " +
            " from doctofiscalliquidacao obj " +
            "         left join liquidacaodoctofiscal doctoliq on doctoliq.doctofiscalliquidacao_id = obj.id " +
            "         left join liquidacao liq on liq.id = doctoliq.liquidacao_id " +
            "         left join doctofiscalentradacompra dfec on dfec.doctofiscalliquidacao_id = obj.id " +
            "         left join entradacompramaterial ecm on ecm.id = dfec.entradacompramaterial_id " +
            "         left join entradamaterial em on em.id = ecm.id " +
            "         left join doctofiscalsolicaquisicao docsolaq on docsolaq.documentofiscal_id = obj.id " +
            "         left join solicitacaoaquisicao solaq on solaq.id = docsolaq.solicitacaoaquisicao_id " +
            "         left join aquisicao aq on aq.solicitacaoaquisicao_id = solaq.id " +
            "         left join liquidacaoestdoctofiscal estdoc on estdoc.documentofiscal_id = obj.id " +
            "         left join liquidacaoestorno liqest on liqest.id = estdoc.liquidacaoestorno_id " +
            " where obj.id = :idDoc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDoc", doctoFiscalLiquidacao.getId());
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            Object[] obj = resultado.get(0);
            if (obj[0] != null) {
                OrigemDoctoFiscalLiquidacaoVO retorno = new OrigemDoctoFiscalLiquidacaoVO();
                retorno.setIdOrigem(((BigDecimal) obj[0]).longValue());
                retorno.setOrigem((String) obj[1]);
                retorno.setSituacao(obj[2] != null ? SituacaoEntradaMaterialOuAquisicao.valueOf((String) obj[2]) : null);
                retorno.setDescricao((String) obj[3]);
                retorno.setCategoriaOrcamentaria(obj[4] != null ? CategoriaOrcamentaria.valueOf((String) obj[4]) : null);
                return retorno;
            }
        }
        return null;
    }

    public EventoReinfDTO buscarEventoAtualReinfDoDoctoFiscalPorTipo(DoctoFiscalLiquidacao doctoFiscalLiquidacao, TipoArquivoReinf tipo) {
        if (doctoFiscalLiquidacao == null || doctoFiscalLiquidacao.getId() == null) return null;
        String sql = " select distinct pjempregadoresocial.cnpj, " +
            (TipoArquivoReinf.R4020.equals(tipo)
                ? " extract(month from pag.datapagamento) as mes, extract(year from pag.datapagamento) as ano, "
                : " extract(month from dfl.datadocto) as mes, extract(year from dfl.datadocto) as ano, ") +
            " coalesce(pf.cpf, pj.cnpj) as cnpjfornecedor " +
            " from registroeventoretencaoreinf registro " +
            "         inner join configempregadoresocial cee on cee.id = registro.empregador_id " +
            "         inner join entidade ent on ent.id = cee.entidade_id " +
            "         inner join pessoajuridica pjempregadoresocial on pjempregadoresocial.id = ent.pessoajuridica_id " +
            "         inner join notareinf nr on nr.registro_id = registro.id " +
            "         inner join liquidacaodoctofiscal ldf on ldf.id = nr.nota_id " +
            "         inner join doctofiscalliquidacao dfl on dfl.id = ldf.doctofiscalliquidacao_id " +
            "         inner join liquidacao liq on liq.id = ldf.liquidacao_id " +
            "         inner join empenho emp on emp.id = liq.empenho_id " +
            "         left join pessoafisica pf on pf.id = emp.fornecedor_id " +
            "         left join pessoajuridica pj on pj.id = emp.fornecedor_id " +
            "         inner join pagamento pag on pag.liquidacao_id = liq.id " +
            " where ldf.doctofiscalliquidacao_id = :idDocto ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDocto", doctoFiscalLiquidacao.getId());
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            Object[] obj = resultado.get(0);
            return configuracaoEmpregadorESocialFacade.getEventoPorEmpregadorTipoArquivoMesAnoAndCNPJFornecedor(
                (String) obj[0],
                tipo,
                Mes.getMesToInt(((BigDecimal) obj[1]).intValue()),
                ((BigDecimal) obj[2]).intValue(),
                (String) obj[3]
            );
        }
        return null;
    }

    public BigDecimal getRetencaoMaximaContaExtra(Conta conta) {
        return configuracaoContabilFacade.buscarRetencaoMaximaReinfPorContaExtraETipoArquivo(conta, null);
    }

    public ContaExtraorcamentaria buscarContaPadraoInssExercicioCorrente() {
        return configuracaoContabilFacade.buscarContaPadraoInssPorExercicio(sistemaFacade.getExercicioCorrente());
    }

    public ContaExtraorcamentaria buscarContaPadraoIrrfExercicioCorrente() {
        return configuracaoContabilFacade.buscarContaPadraoIrrfPorExercicio(sistemaFacade.getExercicioCorrente());
    }

    public TipoDocumentoFiscalFacade getTipoDocumentoFiscalFacade() {
        return tipoDocumentoFiscalFacade;
    }

    public UFFacade getUfFacade() {
        return ufFacade;
    }

    public ConfiguracaoContabilFacade getConfiguracaoContabilFacade() {
        return configuracaoContabilFacade;
    }
}
