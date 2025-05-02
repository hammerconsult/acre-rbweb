package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.CorrecaoDadosAdministrativoVO;
import br.com.webpublico.entidadesauxiliares.ItemDoctoItemEntradaVO;
import br.com.webpublico.enums.OperacaoMovimentoAlteracaoContratual;
import br.com.webpublico.enums.SituacaoContrato;
import br.com.webpublico.enums.TipoTermoAlteracaoContratual;
import br.com.webpublico.enums.administrativo.SituacaoDocumentoFiscalEntradaMaterial;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import br.com.webpublico.singletons.SingletonConcorrenciaMaterial;
import br.com.webpublico.singletons.SingletonContrato;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class CorrecaoDadosAdministrativoFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LoteEfetivacaoTransferenciaBemFacade loteEfetivacaoTransferenciaBemFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;
    @EJB
    private SingletonConcorrenciaMaterial singletonConcorrenciaMaterial;
    @EJB
    private SingletonContrato singletonContrato;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AssociacaoDocumentoLiquidacaoEntradaCompraFacade associacaoDocumentoLiquidacaoEntradaCompraFacade;
    @EJB
    private MaterialFacade materialFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;
    @EJB
    private ReprocessamentoSaldoContratoFacade reprocessamentoSaldoContratoFacade;
    @EJB
    private ContratoFacade contratoFacade;

    @Asynchronous
    @TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 5)
    public Future<CorrecaoDadosAdministrativoVO> reprocessarContrato(CorrecaoDadosAdministrativoVO selecionado) {

        AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
        selecionado.setAssistenteBarraProgresso(assistente);
        assistente.setDescricaoProcesso("Buscando Contratos...");

        List<Contrato> contratos = buscarTodosContratos();

        assistente.setDescricaoProcesso("Reprocessando Contratos...  " + contratos.size() + " de " + countContratos());
        assistente.setTotal(contratos.size());

        reprocessamentoSaldoContratoFacade.gerarSaldoAndMovimentoItemContrato(assistente, selecionado.getDataOperacao(), contratos);
        assistente.setDescricaoProcesso("Finalizando Processo...");
        assistente.zerarContadoresProcesso();
        return new AsyncResult<>(selecionado);
    }

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

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<CorrecaoDadosAdministrativoVO> reprocessarAlteracaoContratual(CorrecaoDadosAdministrativoVO selecionado) {
        AssistenteBarraProgresso assistente = selecionado.getAssistenteBarraProgresso();
        assistente.setDescricaoProcesso("Buscando movimentos para reprocessar...");

        List<AlteracaoContratual> alteracoes = buscarAlteracoesNaoReprocessadas("");
        selecionado.setAlteracoesContratuais(alteracoes);
        assistente.setTotal(selecionado.getAlteracoesContratuais().size());

        assistente.setDescricaoProcesso("Reprocessando movimentos alteração contratual...");
        for (AlteracaoContratual alt : selecionado.getAlteracoesContratuais()) {
            AlteracaoContratual altCont = alteracaoContratualFacade.recuperarComDependenciasMovimentosAndItens(alt.getId());
            unificarMovimentosAlteracaoContratual(altCont);
            assistente.conta();
        }
        return new AsyncResult<CorrecaoDadosAdministrativoVO>(selecionado);
    }

    public List<Contrato> buscarTodosContratos() {
        String sql = " select c.* from contrato c " +
            "           where c.reprocessado = :reprocessado " +
            "           and c.situacaocontrato in (:aprovado, :deferido)";
        Query q = em.createNativeQuery(sql, Contrato.class);
        q.setParameter("reprocessado", Boolean.FALSE);
        q.setParameter("aprovado", SituacaoContrato.APROVADO.name());
        q.setParameter("deferido", SituacaoContrato.DEFERIDO.name());
        q.setMaxResults(200);
        return q.getResultList();
    }

    public Long countContratos() {
        String sql = " select count(c.id) from contrato c " +
            "           where c.reprocessado = :reprocessado " +
            "           and c.situacaocontrato in (:aprovado, :deferido) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("reprocessado", Boolean.FALSE);
        q.setParameter("aprovado", SituacaoContrato.APROVADO.name());
        q.setParameter("deferido", SituacaoContrato.DEFERIDO.name());
        return ((BigDecimal) q.getSingleResult()).longValue();
    }


    public List<AlteracaoContratual> buscarAlteracoesNaoReprocessadas(String parte) {
        String sql = "" +
            "   select ad.* from alteracaocontratual ad " +
            "     inner join exercicio e on ad.exercicio_id = e.id " +
            "     inner join alteracaocontratualcont acc on acc.alteracaocontratual_id = ad.id   " +
            "     inner join contrato c on c.id = acc.contrato_id " +
            "     inner join exercicio exCont on c.exerciciocontrato_id = exCont.id " +
            "     inner join pessoa p on c.contratado_id = p.id " +
            "     left join pessoafisica pf on p.id = pf.id " +
            "     left join pessoajuridica pj on p.id = pj.id " +
            "   where (ad.numerotermo like :parte " +
            "           or ad.numero like :parte " +
            "           or lower(" + Util.getTranslate("ad.justificativa") + ") like " + Util.getTranslate(":parte") +
            "           or lower(" + Util.getTranslate("c.objeto") + ") like " + Util.getTranslate(":parte") +
            "           or e.ano like :parte " +
            "           or to_char(ad.numerotermo) || '/' || to_char(e.ano) like :parte " +
            "           or to_char(ad.numero) || '/' || to_char(extract(year from ad.datalancamento)) like :parte " +
            "           or lower(" + Util.getTranslate("pf.nome") + ") like " + Util.getTranslate(":parte") +
            "           or lower(" + Util.getTranslate("pj.razaoSocial") + ") like " + Util.getTranslate(":parte") +
            "           or pf.cpf like :parte " +
            "           or replace(replace(pf.cpf,'.',''),'-','') like :parte " +
            "           or pj.cnpj like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') like :parte " +
            "           or to_char(c.numerotermo) || '/' || to_char(exCont.ano) like :parte " +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :parte " +
            "          )" +
            "     and ad.situacao <> :em_elaboracao " +
            "     and ad.tipotermo in (:valor, :prazoValor, :prazo) " +
            "   order by ad.numerotermo desc ";
        Query q = em.createNativeQuery(sql, AlteracaoContratual.class);
        q.setParameter("em_elaboracao", SituacaoContrato.EM_ELABORACAO.name());
        q.setParameter("valor", TipoTermoAlteracaoContratual.VALOR.name());
        q.setParameter("prazo", TipoTermoAlteracaoContratual.PRAZO.name());
        q.setParameter("prazoValor", TipoTermoAlteracaoContratual.PRAZO_VALOR.name());
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public void unificarMovimentosAlteracaoContratual(AlteracaoContratual alt) {
        Map<OperacaoMovimentoAlteracaoContratual, List<MovimentoAlteracaoContratual>> mapValor = Maps.newHashMap();
        List<MovimentoAlteracaoContratual> movimentosPrazo = Lists.newArrayList();

        for (MovimentoAlteracaoContratual movAlt : alt.getMovimentos()) {
            OperacaoMovimentoAlteracaoContratual operacao = movAlt.getFinalidade().isAcrescimo() ? OperacaoMovimentoAlteracaoContratual.DILACAO_PRAZO : OperacaoMovimentoAlteracaoContratual.REDUCAO_PRAZO;
            if (movAlt.getInicioVigencia() != null) {
                movAlt.setOperacao(operacao);
                movimentosPrazo.add(movAlt);
            }
            if (movAlt.getInicioExecucao() != null) {
                movAlt.setOperacao(operacao);
                movimentosPrazo.add(movAlt);
            }
            if (movAlt.isOperacoesValor()) {
                if (!mapValor.containsKey(movAlt.getOperacao())) {
                    mapValor.put(movAlt.getOperacao(), Lists.newArrayList(movAlt));
                } else {
                    List<MovimentoAlteracaoContratual> movimentosMap = mapValor.get(movAlt.getOperacao());
                    movimentosMap.add(movAlt);
                    mapValor.put(movAlt.getOperacao(), movimentosMap);
                }
            }
        }
        if (movimentosPrazo.size() > 1) {
            MovimentoAlteracaoContratual primeiroMovPrazo = movimentosPrazo.get(0);
            MovimentoAlteracaoContratual ultimoMovPrazo = movimentosPrazo.get(movimentosPrazo.size() - 1);

            if (primeiroMovPrazo.getInicioVigencia() == null) {
                primeiroMovPrazo.setInicioVigencia(ultimoMovPrazo.getInicioVigencia());
            }
            if (primeiroMovPrazo.getTerminoVigencia() == null) {
                primeiroMovPrazo.setTerminoVigencia(ultimoMovPrazo.getTerminoVigencia());
            }

            if (primeiroMovPrazo.getInicioExecucao() == null) {
                primeiroMovPrazo.setInicioExecucao(ultimoMovPrazo.getInicioExecucao());
            }
            if (primeiroMovPrazo.getTerminoExecucao() == null) {
                primeiroMovPrazo.setTerminoExecucao(ultimoMovPrazo.getTerminoExecucao());
            }
            alt.getMovimentos().remove(ultimoMovPrazo);
        }

        for (Map.Entry<OperacaoMovimentoAlteracaoContratual, List<MovimentoAlteracaoContratual>> entry : mapValor.entrySet()) {
            if (entry.getValue().size() > 1) {
                MovimentoAlteracaoContratual primeiroMovValor = entry.getValue().get(0);
                MovimentoAlteracaoContratual ultimoMovValor = entry.getValue().get(entry.getValue().size() - 1);

                if (!primeiroMovValor.getValorVariacaoContrato()) {
                    alt.getMovimentos().remove(primeiroMovValor);
                }
                if (!ultimoMovValor.getValorVariacaoContrato()) {
                    alt.getMovimentos().remove(ultimoMovValor);
                }
            }
        }
        em.merge(alt);
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

    public AlteracaoContratualFacade getAlteracaoContratualFacade() {
        return alteracaoContratualFacade;
    }

    public SingletonConcorrenciaMaterial getSingletonConcorrenciaMaterial() {
        return singletonConcorrenciaMaterial;
    }
}
