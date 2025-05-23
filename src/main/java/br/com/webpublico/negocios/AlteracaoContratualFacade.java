package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.DotacaoAlteracaoContratualVO;
import br.com.webpublico.entidadesauxiliares.DotacaoProcessoCompraVO;
import br.com.webpublico.entidadesauxiliares.FiltroDotacaoProcessoCompraVO;
import br.com.webpublico.entidadesauxiliares.VOExecucaoReservadoPorLicitacao;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class AlteracaoContratualFacade extends AbstractFacade<AlteracaoContratual> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private SaldoItemContratoFacade saldoItemContratoFacade;
    @EJB
    private ReprocessamentoSaldoContratoFacade reprocessamentoSaldoContratoFacade;
    @EJB
    private AjusteContratoFacade ajusteContratoFacade;
    @EJB
    private SolicitacaoMaterialExternoFacade solicitacaoMaterialExternoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;
    @EJB
    private DotacaoProcessoCompraFacade dotacaoProcessoCompraFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private AtaRegistroPrecoFacade ataRegistroPrecoFacade;
    @EJB
    private SaldoProcessoLicitatorioFacade saldoProcessoLicitatorioFacade;

    public AlteracaoContratualFacade() {
        super(AlteracaoContratual.class);
    }

    @Override
    public AlteracaoContratual recuperar(Object id) {
        AlteracaoContratual entity = em.find(AlteracaoContratual.class, id);
        Hibernate.initialize(entity.getMovimentos());
        Hibernate.initialize(entity.getPublicacoes());
        for (MovimentoAlteracaoContratual movimento : entity.getMovimentos()) {
            Hibernate.initialize(movimento.getItens());
        }
        if (entity.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(entity.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        return entity;
    }

    public AlteracaoContratual recuperarComDependenciasMovimentosAndItens(Object id) {
        AlteracaoContratual entity = em.find(AlteracaoContratual.class, id);
        Hibernate.initialize(entity.getMovimentos());
        for (MovimentoAlteracaoContratual movimento : entity.getMovimentos()) {
            Hibernate.initialize(movimento.getItens());
        }
        return entity;
    }

    public AlteracaoContratual recuperarSemDependencias(Object id) {
        return super.recuperar(id);
    }

    public AlteracaoContratual salvarAlteracao(AlteracaoContratual entity) {
        if (Strings.isNullOrEmpty(entity.getNumero())) {
            entity.setNumero(Util.zerosAEsquerda(getProximoNumero(entity.getIdProcesso(), "numero", entity.getTipoAlteracaoContratual()).toString(), 4));
        }
        if (Strings.isNullOrEmpty(entity.getNumeroTermo())) {
            entity.setNumeroTermo(Util.zerosAEsquerda(getProximoNumero(entity.getIdProcesso(), "numeroTermo", entity.getTipoAlteracaoContratual()).toString(), 4));
        }
        return em.merge(entity);
    }

    public void criarMovimentoItemContrato(List<MovimentoAlteracaoContratual> movimentos, Date dataOperacao) {
        Collections.sort(movimentos);
        movimentos.forEach(mov -> {
            mov.getItens().stream()
                .filter(MovimentoAlteracaoContratualItem::hasValorTotalMaiorQueZero)
                .forEach(movItem -> {
                    if (mov.getValorVariacaoContrato()) {
                        saldoItemContratoFacade.gerarSaldoAlteracaoContratual(movItem, SubTipoSaldoItemContrato.VARIACAO, dataOperacao);
                    }
                    saldoItemContratoFacade.gerarSaldoAlteracaoContratual(movItem, SubTipoSaldoItemContrato.EXECUCAO, dataOperacao);
                });
        });
    }

    private void movimentarOperacaoTransferencia(AlteracaoContratual entity, Contrato contrato) {
        entity.getMovimentos().forEach(mov -> {
            if (mov.isTransferenciaFornecedor()) {
                contratoFacade.transferirFornecedorContrato(contrato, mov.getFornecedor(), mov.getResponsavelFornecedor());
            }
            if (mov.isTransferenciaUnidade()) {
                contratoFacade.transferirUnidadeContrato(contrato, mov.getUnidadeOrganizacional(), entity.getDataAprovacao());
            }
        });
        transferirDotacao(entity, contrato);
    }

    private void transferirDotacao(AlteracaoContratual entity, Contrato contrato) {
        if (entity.isTransferenciaDotacao()) {
            TipoReservaSolicitacaoCompra tipoReserva = entity.getTipoAlteracaoContratual().isAditivo() ? TipoReservaSolicitacaoCompra.ADITIVO : TipoReservaSolicitacaoCompra.APOSTILAMENTO;

            MovimentoAlteracaoContratual movSupressao = entity.getMovimentos().stream().filter(mov -> mov.getFinalidade().isSupressao()).findFirst().get();

            DotacaoSolicitacaoMaterialItemFonte dotSolMatFonte = dotacaoSolicitacaoMaterialFacade.recuperarDotacaoSolicitacaoMaterialFontePorFonteDespesaOrc(contrato.getObjetoAdequado().getSolicitacaoMaterial(), movSupressao.getFonteDespesaORC());

            DotacaoSolicitacaoMaterialItemFonte novaDotacaoEstorno = new DotacaoSolicitacaoMaterialItemFonte();
            novaDotacaoEstorno.setDotacaoSolMatItem(dotSolMatFonte.getDotacaoSolMatItem());
            novaDotacaoEstorno.setFonteDespesaORC(movSupressao.getFonteDespesaORC());
            novaDotacaoEstorno.setValor(movSupressao.getValor());
            novaDotacaoEstorno.setTipoOperacao(TipoOperacaoORC.ESTORNO);
            novaDotacaoEstorno.setTipoReserva(tipoReserva);
            novaDotacaoEstorno.setIdOrigem(movSupressao.getId());
            novaDotacaoEstorno.setDataLancamento(DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao()));
            novaDotacaoEstorno.setExercicio(sistemaFacade.getExercicioCorrente());
            novaDotacaoEstorno = em.merge(novaDotacaoEstorno);

            if (novaDotacaoEstorno.getFonteDespesaORC().getDespesaORC().getExercicio().equals(sistemaFacade.getExercicioCorrente())) {
                gerarSaldoFonteReservadoPorLicitacao(novaDotacaoEstorno.getFonteDespesaORC(), TipoOperacaoORC.ESTORNO, movSupressao, novaDotacaoEstorno.getDataLancamento());
            }

            MovimentoAlteracaoContratual movAcrescimo = entity.getMovimentos().stream().filter(mov -> mov.getFinalidade().isAcrescimo()).findFirst().get();
            DotacaoSolicitacaoMaterialItemFonte novaDotacaoNormal = new DotacaoSolicitacaoMaterialItemFonte();
            novaDotacaoNormal.setDotacaoSolMatItem(dotSolMatFonte.getDotacaoSolMatItem());
            novaDotacaoNormal.setFonteDespesaORC(movAcrescimo.getFonteDespesaORC());
            novaDotacaoNormal.setValor(movAcrescimo.getValor());
            novaDotacaoNormal.setTipoOperacao(TipoOperacaoORC.NORMAL);
            novaDotacaoNormal.setTipoReserva(tipoReserva);
            novaDotacaoNormal.setIdOrigem(movAcrescimo.getId());
            novaDotacaoNormal.setDataLancamento(DataUtil.getDataComHoraAtual(sistemaFacade.getDataOperacao()));
            novaDotacaoNormal.setExercicio(sistemaFacade.getExercicioCorrente());
            novaDotacaoNormal = em.merge(novaDotacaoNormal);

            if (novaDotacaoNormal.getFonteDespesaORC().getDespesaORC().getExercicio().equals(sistemaFacade.getExercicioCorrente())) {
                gerarSaldoFonteReservadoPorLicitacao(novaDotacaoNormal.getFonteDespesaORC(), TipoOperacaoORC.NORMAL, movAcrescimo, novaDotacaoNormal.getDataLancamento());
            }
        }
    }

    public void movimentarOperacaoValorAndPrazoContrato(Contrato contrato, List<MovimentoAlteracaoContratual> movimentos) {
        for (MovimentoAlteracaoContratual movimento : movimentos) {
            if (movimento.isOperacoesPrazo()) {
                contrato.setTerminoVigenciaAtual(movimento.getTerminoVigencia() != null ? movimento.getTerminoVigencia() : contrato.getTerminoVigenciaAtual());
                contrato.setTerminoExecucaoAtual(movimento.getTerminoExecucao() != null ? movimento.getTerminoExecucao() : contrato.getTerminoExecucaoAtual());
            }
            if (movimento.isOperacoesValor()) {
                if (movimento.getValorVariacaoContrato()) {
                    BigDecimal valorAtual = contrato.getValorAtualContrato();
                    valorAtual = movimento.getFinalidade().isAcrescimo() ? valorAtual.add(movimento.getValor()) : valorAtual.subtract(movimento.getValor());
                    contrato.setValorAtualContrato(valorAtual);

                    BigDecimal percentual = contrato.getVariacaoAtualContrato();
                    BigDecimal percentualMovimento = movimento.getPercentual() != null ? movimento.getPercentual() : BigDecimal.ZERO;
                    percentual = movimento.getFinalidade().isAcrescimo() ? percentual.add(percentualMovimento) : percentual.subtract(percentualMovimento);

                    contrato.setVariacaoAtualContrato(percentual);
                }
                BigDecimal saldoAtual = contrato.getSaldoAtualContrato();
                saldoAtual = movimento.getFinalidade().isAcrescimo() ? saldoAtual.add(movimento.getValor()) : saldoAtual.subtract(movimento.getValor());
                contrato.setSaldoAtualContrato(saldoAtual);
            }
        }
    }

    public void movimentarOperacaoPrazoAta(AtaRegistroPreco ata, List<MovimentoAlteracaoContratual> movimentos) {
        for (MovimentoAlteracaoContratual movimento : movimentos) {
            if (movimento.isOperacoesPrazo()) {
                ata.setDataVencimentoAtual(movimento.getTerminoVigencia() != null ? movimento.getTerminoVigencia() : ata.getDataVencimento());
            }
        }
    }

    public AlteracaoContratual aprovar(AlteracaoContratual entity) {
        entity.setSituacao(SituacaoContrato.APROVADO);
        entity = em.merge(entity);

        if (entity.getTipoCadastro().isContrato()) {
            Contrato contrato = entity.getContrato();
            criarMovimentoItemContrato(entity.getMovimentos(), entity.getDataLancamento());
            movimentarOperacaoValorAndPrazoContrato(contrato, entity.getMovimentos());
            movimentarOperacaoTransferencia(entity, contrato);
            estonarSaldoReservadoPorLicitacaoMovimentoValorVariacaoSupressao(entity);
            em.merge(contrato);
        }
        if (entity.getTipoCadastro().isAta()) {
            AtaRegistroPreco ata = entity.getAtaRegistroPreco();
            movimentarOperacaoPrazoAta(ata, entity.getMovimentos());
            em.merge(ata);
        }
        return entity;
    }

    private void gerarSaldoFonteReservadoPorLicitacao(FonteDespesaORC fonteDespesaORC, TipoOperacaoORC tipoOperacao,
                                                      MovimentoAlteracaoContratual movAlt, Date dataMovimento) {
        AlteracaoContratual alteracaoCont = movAlt.getAlteracaoContratual();
        SaldoFonteDespesaORCVO saldoEstorno = new SaldoFonteDespesaORCVO(
            fonteDespesaORC,
            OperacaoORC.RESERVADO_POR_LICITACAO,
            tipoOperacao,
            movAlt.getValor(),
            dataMovimento,
            fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
            movAlt.getId().toString(),
            movAlt.getClass().getSimpleName(),
            alteracaoCont.getNumeroAnoTermo(),
            getHistoricoSaldoFonteDespesaOrc(fonteDespesaORC, alteracaoCont.getContrato()));
        saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(saldoEstorno);
    }

    public String getHistoricoSaldoFonteDespesaOrc(FonteDespesaORC fonteDespesaORC, Contrato contrato) {
        return "Contrato: " + contrato.getNumeroContratoAno() + " | " + contrato.getNumeroAnoTermo() + " | " + DataUtil.getDataFormatada(contrato.getDataLancamento()) + " | " +
            " Unidade Administrativa: " + contrato.getUnidadeAdministrativa() + " | " +
            " Elemento de Despesa: " + fonteDespesaORC.getDespesaORC() + "/" + fonteDespesaORC.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getDescricao() + " | " +
            " Fonte de Recurso: " + fonteDespesaORC.getDescricaoFonteDeRecurso().trim() + ",";
    }

    public AlteracaoContratual deferir(AlteracaoContratual alteracaoContratual) {
        alteracaoContratual.setSituacao(SituacaoContrato.DEFERIDO);
        return em.merge(alteracaoContratual);
    }

    public List<AlteracaoContratual> buscarAlteracaoContratualContratoPorSituacoes(String parte, TipoAlteracaoContratual tipoAlteracao, SituacaoContrato... situacoes) {
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
            "           or lower(" + Util.getTranslate("pf.nome") + ")  like " + Util.getTranslate(":parte") +
            "           or lower(" + Util.getTranslate("pj.razaoSocial") + ") like " + Util.getTranslate(":parte") +
            "           or pf.cpf like :parte " +
            "           or replace(replace(pf.cpf,'.',''),'-','') like :parte " +
            "           or pj.cnpj like :parte " +
            "           or replace(replace(replace(pj.cnpj,'.',''),'-',''), '/', '') like :parte " +
            "           or to_char(c.numerotermo) || '/' || to_char(exCont.ano) like :parte " +
            "           or to_char(c.numerocontrato) || '/' || to_char(extract (year from c.datalancamento)) like :parte " +
            "          )" +
            "     and ad.situacao in (:situacoes) " +
            "     and ad.tipoalteracaocontratual = :tipoAlteracao " +
            "   order by ad.numerotermo desc ";
        Query q = em.createNativeQuery(sql, AlteracaoContratual.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoAlteracao", tipoAlteracao.name());
        q.setParameter("situacoes", SituacaoContrato.getSituacoesAsString(Arrays.asList(situacoes)));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<AlteracaoContratual> buscarPorSituacaoAndTermoDiferenteCadastral(String parte, TipoAlteracaoContratual tipoAlteracao, SituacaoContrato... situacoes) {
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
            "     and ad.situacao in (:situacoes) " +
            "     and ad.tipotermo <> :tipoCadastral  " +
            "     and ad.tipoalteracaocontratual = :tipoAlteracao " +
            "   order by ad.numerotermo desc ";
        Query q = em.createNativeQuery(sql, AlteracaoContratual.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoAlteracao", tipoAlteracao.name());
        q.setParameter("tipoCadastral", TipoTermoAlteracaoContratual.ALTERACAO_CADASTRAL.name());
        q.setParameter("situacoes", SituacaoContrato.getSituacoesAsString(Arrays.asList(situacoes)));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }

        return Lists.newArrayList();
    }

    public List<AlteracaoContratual> buscarAlteracoesContratuaisPorContrato(Contrato contrato) {
        String sql = " select ac.* from alteracaocontratual ac " +
            "           inner join alteracaocontratualcont acc on acc.alteracaocontratual_id = ac.id " +
            "           where acc.contrato_id = :idContrato ";
        Query q = em.createNativeQuery(sql, AlteracaoContratual.class);
        q.setParameter("idContrato", contrato.getId());
        return q.getResultList();
    }

    public List<AlteracaoContratual> buscarAlteracoesContratuaisPorAta(AtaRegistroPreco ataRegistroPreco) {
        String sql = " select ac.* from alteracaocontratual ac " +
            "           inner join alteracaocontratualata aca on aca.alteracaocontratual_id = ac.id " +
            "           where aca.ataregistropreco_id = :idAta ";
        Query q = em.createNativeQuery(sql, AlteracaoContratual.class);
        q.setParameter("idAta", ataRegistroPreco.getId());
        return q.getResultList();
    }

    public List<MovimentoAlteracaoContratual> buscarMovimentosAndItens(Long idAlteracaoCont) {
        String sql = " select mov.* from movimentoalteracaocont mov " +
            "           where mov.alteracaocontratual_id = :idAlteracaoCont ";
        Query q = em.createNativeQuery(sql, MovimentoAlteracaoContratual.class);
        q.setParameter("idAlteracaoCont", idAlteracaoCont);
        List<MovimentoAlteracaoContratual> movimentos = q.getResultList();
        movimentos.forEach(mov -> Hibernate.initialize(mov.getItens()));
        return movimentos;
    }

    public List<MovimentoAlteracaoContratualItem> buscarItensMovimento(Long idMovimento) {
        String sql = " select item.* from movimentoalteracaocontitem item " +
            "           where item.movimentoalteracaocont_id = :idMovimento ";
        Query q = em.createNativeQuery(sql, MovimentoAlteracaoContratualItem.class);
        q.setParameter("idMovimento", idMovimento);
        return q.getResultList();
    }

    public List<AlteracaoContratual> buscarAlteracoesContratuaisAndDependecias(Long idProcesso, TipoAlteracaoContratual tipoAlteracaoContratual) {
        String sql = " select ad.* from alteracaocontratual ad " +
            "           left join alteracaocontratualcont acc on acc.alteracaocontratual_id = ad.id " +
            "           left join alteracaocontratualata aca on aca.alteracaocontratual_id = ad.id " +
            "          where coalesce(acc.contrato_id, aca.ataregistropreco_id) = :idContrato" +
            "           and ad.tipoalteracaocontratual = :tipoAteracao ";
        Query q = em.createNativeQuery(sql, AlteracaoContratual.class);
        q.setParameter("idContrato", idProcesso);
        q.setParameter("tipoAteracao", tipoAlteracaoContratual.name());
        try {
            List<AlteracaoContratual> list = Lists.newArrayList();
            if (!q.getResultList().isEmpty()) {
                list = q.getResultList();
                for (AlteracaoContratual entity : list) {
                    Hibernate.initialize(entity.getMovimentos());
                    for (MovimentoAlteracaoContratual mov : entity.getMovimentos()) {
                        Hibernate.initialize(mov.getItens());
                    }
                }
            }
            return list;
        } catch (NoResultException ex) {
            return Lists.newArrayList();
        }
    }

    public String buscarDescricaoAlteracaoContratual(Long idAlteracao) {
        String sql = " " +
            "  select ad.numero || ' ' || ad.numerotermo || '/' || ex.ano " +
            "   from alteracaocontratual ad " +
            "        inner join exercicio ex on ex.id = ad.exercicio_id " +
            "  where ad.id = :idAlteracaoCont ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAlteracaoCont", idAlteracao);
        return (String) q.getSingleResult();
    }

    public Boolean isMovimentoAjusteValor(ItemContrato itemContrato, Long idAlteracao) {
        String sql = " select mov.* from movimentoalteracaocont mov " +
            "           inner join alteracaocontratual ac on ac.id = mov.alteracaocontratual_id " +
            "           inner join movimentoalteracaocontitem movitem on mov.id = movitem.movimentoalteracaocont_id   " +
            "           inner join itemcontrato ic on ic.id = movitem.itemcontrato_id " +
            "           where ac.id = :idAlteracao " +
            "           and ic.id = :idItemContrato " +
            "           and ac.tipotermo in (:tipoTermoValor, :tipoTermoPrazoValor) " +
            "           and mov.operacao in (:opReajuste, :opReequilibrio)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemContrato", itemContrato.getId());
        q.setParameter("idAlteracao", idAlteracao);
        q.setParameter("tipoTermoValor", TipoTermoAlteracaoContratual.VALOR.name());
        q.setParameter("tipoTermoPrazoValor", TipoTermoAlteracaoContratual.PRAZO_VALOR.name());
        q.setParameter("opReajuste", OperacaoMovimentoAlteracaoContratual.REAJUSTE_PRE_EXECUCAO.name());
        q.setParameter("opReequilibrio", OperacaoMovimentoAlteracaoContratual.REEQUILIBRIO_PRE_EXECUCAO.name());
        return !q.getResultList().isEmpty();
    }

    public Boolean isMovimentoApostilamentoReajusteValorPreExecucao(ItemContrato itemContrato, Long idAlteracao) {
        String sql = " select mov.* from movimentoalteracaocont mov " +
            "           inner join alteracaocontratual ac on ac.id = mov.alteracaocontratual_id " +
            "           inner join movimentoalteracaocontitem movitem on mov.id = movitem.movimentoalteracaocont_id   " +
            "           inner join itemcontrato ic on ic.id = movitem.itemcontrato_id " +
            "           where ac.id = :idAlteracao " +
            "           and ic.id = :idItemContrato " +
            "           and ac.tipoalteracaocontratual = :tipoAlteracao " +
            "           and ac.tipotermo = :tipoTermo " +
            "           and mov.operacao = :operacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemContrato", itemContrato.getId());
        q.setParameter("idAlteracao", idAlteracao);
        q.setParameter("tipoAlteracao", TipoAlteracaoContratual.APOSTILAMENTO.name());
        q.setParameter("tipoTermo", TipoTermoAlteracaoContratual.VALOR.name());
        q.setParameter("operacao", OperacaoMovimentoAlteracaoContratual.REAJUSTE_PRE_EXECUCAO.name());
        return !q.getResultList().isEmpty();
    }

    public Boolean isMovimentoOperacaoDe(AlteracaoContratual alteracaoCont, List<OperacaoMovimentoAlteracaoContratual> operacoes) {
        String sql = " select mov.* from movimentoalteracaocont mov " +
            "           where mov.alteracaocontratual_id = :idAlteracao " +
            "           and mov.operacao in (:operacoes) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAlteracao", alteracaoCont.getId());
        q.setParameter("operacoes", Util.getListOfEnumName(operacoes));
        return !q.getResultList().isEmpty();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalPorUnidade(UnidadeOrganizacional unidadeOrganizacional, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalPorUnidade(
            sistemaFacade.getDataOperacao(),
            unidadeOrganizacional,
            tipoHierarquiaOrganizacional);
    }

    private void estonarSaldoReservadoPorLicitacaoMovimentoValorVariacaoSupressao(AlteracaoContratual entity) {
        for (MovimentoAlteracaoContratual movimento : entity.getMovimentos()) {
            if (movimento.getGerarReserva()
                && movimento.getValorVariacaoContrato()
                && movimento.getFinalidade().isSupressao()
                && validarValorColunaReservadoPorLicitacao(movimento)) {

                SaldoFonteDespesaORCVO saldoVo = new SaldoFonteDespesaORCVO(
                    movimento.getFonteDespesaORC(),
                    OperacaoORC.RESERVADO_POR_LICITACAO,
                    TipoOperacaoORC.ESTORNO,
                    movimento.getValor(),
                    sistemaFacade.getDataOperacao(),
                    movimento.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                    movimento.getId().toString(),
                    movimento.getClass().getSimpleName(),
                    entity.getNumeroAnoTermo(),
                    getHistoricoSaldoFonteDespesaOrc(movimento.getFonteDespesaORC(), movimento.getAlteracaoContratual().getContrato()));
                saldoFonteDespesaORCFacade.gerarSaldoOrcamentarioSemRealizarValidacao(saldoVo);
            }
        }
    }

    private boolean validarValorColunaReservadoPorLicitacao(MovimentoAlteracaoContratual movimento) {
        return saldoFonteDespesaORCFacade.validarValorColunaIndividual(
            movimento.getValor(),
            movimento.getFonteDespesaORC(),
            sistemaFacade.getDataOperacao(),
            movimento.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
            OperacaoORC.RESERVADO_POR_LICITACAO);
    }

    public List<VOExecucaoReservadoPorLicitacao> buscarValoresExecutadoReservaoPorLicitacao(MovimentoAlteracaoContratual movimento) {
        String sql = " select  " +
            "  sol.fontedespesaorc_id, " +
            "  coalesce(sum(sol.valor), 0) as solicitacao_empenho,  " +
            "  coalesce(sum(emp.valor), 0) as empenho " +
            " from solicitacaoempenho sol " +
            "  inner join execucaocontratoempenho exemp on exemp.solicitacaoempenho_id = sol.id " +
            "  inner join execucaocontrato ex on ex.id = exemp.execucaocontrato_id " +
            "  left join empenho emp on emp.id = exemp.empenho_id " +
            " where extract(year from sol.datasolicitacao) = :exercicio " +
            "  and ex.contrato_id = :idContrato " +
            "  and sol.fontedespesaorc_id = :idFonte " +
            "group by sol.fontedespesaorc_id ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", movimento.getAlteracaoContratual().getContrato().getId());
        q.setParameter("idFonte", movimento.getFonteDespesaORC().getId());
        q.setParameter("exercicio", movimento.getAlteracaoContratual().getExercicio().getAno());
        List<VOExecucaoReservadoPorLicitacao> toReturn = Lists.newArrayList();
        List resultList = q.getResultList();

        SaldoFonteDespesaORC saldoFonteDespesaORC = saldoFonteDespesaORCFacade.recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(
            movimento.getFonteDespesaORC(),
            movimento.getAlteracaoContratual().getDataLancamento(),
            movimento.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());
        if (!resultList.isEmpty()) {
            for (Object[] obj : (List<Object[]>) resultList) {
                VOExecucaoReservadoPorLicitacao vo = new VOExecucaoReservadoPorLicitacao();
                vo.setFonteDespesaORC(em.find(FonteDespesaORC.class, ((BigDecimal) obj[0]).longValue()));
                vo.setSolicitacoEmpenho((BigDecimal) obj[1]);
                vo.setEmpenho((BigDecimal) obj[2]);
                vo.setValorMovimento(movimento.getValor());
                vo.setReservadoPorLicitacao(saldoFonteDespesaORC.getReservadoPorLicitacao());
                vo.setValorFinal(vo.getReservadoPorLicitacao().subtract(vo.getValorMovimento()));
                vo.setSaldoFonteDespesa(saldoFonteDespesaORC.getSaldoAtual());
                vo.setSaldoAtual(saldoFonteDespesaORC.getSaldoAtual().add(vo.getValorMovimento()));
                toReturn.add(vo);
            }
        }
        return toReturn;
    }

    public Long getProximoNumero(Long idMovimento, String campoNumero, TipoAlteracaoContratual tipoAlteracao) {
        String sql = " select coalesce(numero, 0) +1 from " +
            "         (select max(cast( ad." + campoNumero + " as number)) as numero " +
            "         from alteracaocontratual ad " +
            "        left join alteracaocontratualcont acc on acc.alteracaocontratual_id = ad.id " +
            "        left join alteracaocontratualata aca on aca.alteracaocontratual_id = ad.id " +
            "         where coalesce(acc.contrato_id, aca.ataregistropreco_id) = :idContrato" +
            "         and ad.tipoalteracaocontratual = :tipoAlteracao) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", idMovimento);
        q.setParameter("tipoAlteracao", tipoAlteracao.name());
        return ((BigDecimal) q.getSingleResult()).longValue();
    }

    public Long recuperarIdRevisaoAuditoria(Long idContrato) {
        String sql = " select rev.id from alteracaocontratual_aud aud " +
            " inner join revisaoauditoria rev on rev.id = aud.rev " +
            " where rev.id = (select rev from alteracaocontratual_aud caud where id = :idAlteracao and caud.revtype = 0) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idAlteracao", idContrato);
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public void setContratoFacade(ContratoFacade contratoFacade) {
        this.contratoFacade = contratoFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public ReprocessamentoSaldoContratoFacade getReprocessamentoSaldoContratoFacade() {
        return reprocessamentoSaldoContratoFacade;
    }

    public AjusteContratoFacade getAjusteContratoFacade() {
        return ajusteContratoFacade;
    }

    public SolicitacaoMaterialExternoFacade getSolicitacaoMaterialExternoFacade() {
        return solicitacaoMaterialExternoFacade;
    }

    public void validarDataLancamento(AlteracaoContratual entity, Contrato contrato) {
        ValidacaoException ve = new ValidacaoException();
        if (entity.getDataLancamento() != null && contrato.getDataAssinatura() != null) {
            if (entity.getDataLancamento().before(DataUtil.dataSemHorario(contrato.getDataAssinatura()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O " + entity.getTipoAlteracaoContratual().getDescricao()
                    + " deve ser lançado posterior a " + DataUtil.getDataFormatada(contrato.getDataAssinatura()) + " referente a assinatura do contrato.");
            }
        }
        ve.lancarException();
    }

    public void validarDataAprovacao(AlteracaoContratual selecionado) {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getDataAprovacao() != null && selecionado.getTipoCadastro().isContrato()) {
            if (DataUtil.dataSemHorario(selecionado.getDataAprovacao()).before(DataUtil.dataSemHorario(selecionado.getContrato().getInicioVigencia()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data de aprovação deve ser superior ou igual a da de inicío de vigência do contrato.");
            }
            ve.lancarException();
            if (selecionado.getDataDeferimento() != null) {
                if (selecionado.getDataDeferimento() != null && DataUtil.dataSemHorario(selecionado.getDataAprovacao()).after(DataUtil.dataSemHorario(selecionado.getDataDeferimento()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data de aprovação deve ser inferior ou igual a data de deferimento.");
                }
            }
        }
        ve.lancarException();
    }

    public void validarDataAssinatura(AlteracaoContratual entity) {
        ValidacaoException ve = new ValidacaoException();
        if (entity.getDataAssinatura() != null) {
            if (DataUtil.isSabadoDomingo(entity.getDataAssinatura())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de assinatura do " + entity.getDescricaoTipoTermo() + " não pode ser realizada em um Sábado ou Domingo. Por favor, mude-a para um dia de semana.");
            }
            ve.lancarException();
            if (entity.getDataAprovacao() != null && entity.getDataDeferimento() != null) {
                if (entity.getDataAssinatura().before(DataUtil.dataSemHorario(entity.getDataAprovacao()))
                    || entity.getDataAssinatura().after(DataUtil.dataSemHorario(entity.getDataDeferimento()))) {
                    ve.adicionarMensagemDeOperacaoNaoPermitida("A data de assinatura deve estar entre a aprovação e o deferimento do " + entity.getDescricaoTipoTermo() + ".");
                }
            }
        }
        ve.lancarException();
    }

    public List<DotacaoAlteracaoContratualVO> criarTrasnferenciaDotacaoAlteracaoContratualVO(Contrato contrato, Set<TipoObjetoCompra> tiposObjetoCompra) {
        List<DotacaoAlteracaoContratualVO> toReturn = Lists.newArrayList();
        for (TipoObjetoCompra tipo : tiposObjetoCompra) {
            FiltroDotacaoProcessoCompraVO filtro = new FiltroDotacaoProcessoCompraVO();
            filtro.setSolicitacaoMaterial(contrato.getObjetoAdequado().getSolicitacaoMaterial());
            filtro.setTipoObjetoCompra(tipo);
            filtro.setExercicio(sistemaFacade.getExercicioCorrente());

            List<DotacaoProcessoCompraVO> reservas = dotacaoProcessoCompraFacade.buscarValoresReservaDotacaoProcessoCompra(filtro);
            for (DotacaoProcessoCompraVO res : reservas) {
                DotacaoAlteracaoContratualVO novaTransfDot = new DotacaoAlteracaoContratualVO();
                novaTransfDot.setFonteDespesaORC(res.getFonteDespesaORC());
                novaTransfDot.setTipoObjetoCompra(tipo);
                novaTransfDot.setValorReservado(res.getValorReservado());
                novaTransfDot.setValorEstornadoReservado(res.getValorEstornoReservado());
                novaTransfDot.setValorExecutado(res.getValorExecutadoLiquido());
                toReturn.add(novaTransfDot);
            }
        }
        return toReturn;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public ProcessoDeCompraFacade getProcessoDeCompraFacade() {
        return processoDeCompraFacade;
    }

    public SaldoItemContratoFacade getSaldoItemContratoFacade() {
        return saldoItemContratoFacade;
    }

    public AtaRegistroPrecoFacade getAtaRegistroPrecoFacade() {
        return ataRegistroPrecoFacade;
    }

    public SaldoProcessoLicitatorioFacade getSaldoProcessoLicitatorioFacade() {
        return saldoProcessoLicitatorioFacade;
    }
}
