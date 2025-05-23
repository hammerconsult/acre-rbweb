/*
 * Codigo gerado automaticamente em Wed Jan 04 09:24:44 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
public class PagamentoExtraEstornoFacade extends SuperFacadeContabil<PagamentoExtraEstorno> {

    @EJB
    private PagamentoExtraFacade pagamentoExtraFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private SaldoExtraorcamentarioFacade saldoExtraorcamentarioFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ConfigDespesaExtraFacade configDespesaExtraFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public PagamentoExtraEstornoFacade() {
        super(PagamentoExtraEstorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public PagamentoExtraEstorno recuperar(Object id) {
        Query q = em.createQuery("from PagamentoExtraEstorno where id = :id");
        q.setParameter("id", id);
        PagamentoExtraEstorno p = (PagamentoExtraEstorno) q.getSingleResult();
        if (p != null) {
            p.getPagamentoEstornoRecExtras().size();
        }
        return p;
    }

    public void salvarNovo(PagamentoExtraEstorno entity, List<ReceitaExtra> listaRecExtraSelecionadas) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEstorno());
                validarMovimento(entity, listaRecExtraSelecionadas);
                singletonConcorrenciaContabil.bloquear(entity.getPagamentoExtra());

                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroPagamentoExtra(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataEstorno()));
                    criarAssociacaoPagamentoReceitaExtraEstorno(listaRecExtraSelecionadas, entity);
                    entity = recuperarObjetosParaHistorico(entity);
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity = recuperarObjetosParaHistorico(entity);
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                movimentarReceitaExtra(listaRecExtraSelecionadas);
                movimentarPagamentoExtra(entity);

                gerarSaldoFinanceiro(entity);
                gerarSaldoExtraOrcamentario(entity);
                contabilizarDespesaExtraEstorno(entity);
                singletonConcorrenciaContabil.desbloquear(entity.getPagamentoExtra());
            }
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (Exception e) {
            singletonConcorrenciaContabil.desbloquear(entity.getPagamentoExtra());
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void validarMovimento(PagamentoExtraEstorno entity, List<ReceitaExtra> listaRecExtraSelecionadas) {
        ValidacaoException ve = new ValidacaoException();
        if (!singletonConcorrenciaContabil.isDisponivel(entity.getPagamentoExtra())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Despesa Extra: " + entity.getPagamentoExtra() + " está sendo utilizada por outro usuário. Caso o problema persistir, selecione novamente a despesa extra.");
        }
        if (listaRecExtraSelecionadas.isEmpty() && entity.getId() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Para estornar esta despesa é necessário selecionar uma ou mais receita(s) extraorçamentária(s).");
        }
        if (getSaldoReceitaSelecionadas(listaRecExtraSelecionadas).compareTo(entity.getValor()) < 0
            && entity.getId() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O valor a ser estornado de <b> " + Util.formataValor(entity.getValor()) + "</b> é maior que o saldo de <b>" + Util.formataValor(getSaldoReceitaSelecionadas(listaRecExtraSelecionadas)) + "</b> disponível para esta despesa extraorçamentária.");
        }
        if (DataUtil.dataSemHorario(entity.getDataEstorno()).compareTo(DataUtil.dataSemHorario(entity.getPagamentoExtra().getDataPagto())) < 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" A data do Estorno da Despesa Extra deve ser maior ou igual a data da Despesa Extra Selecionada selecionada. Data da Despesa Extra: <b>" + DataUtil.getDataFormatada(entity.getPagamentoExtra().getDataPagto()) + "</b>.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private BigDecimal getSaldoReceitaSelecionadas(List<ReceitaExtra> listaReceitaExtraSelecionadas) {
        BigDecimal saldo = BigDecimal.ZERO;
        for (ReceitaExtra receita : listaReceitaExtraSelecionadas) {
            if (SituacaoReceitaExtra.EFETUADO.equals(receita.getSituacaoReceitaExtra())) {
                saldo = saldo.add(receita.getValor());
            }
        }
        return saldo;
    }

    private void gerarSaldoFinanceiro(PagamentoExtraEstorno entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacional(),
            entity.getPagamentoExtra().getSubConta(),
            entity.getPagamentoExtra().getContaDeDestinacao(),
            entity.getValor(),
            TipoOperacao.CREDITO,
            entity.getDataEstorno(),
            entity.getEventoContabil(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.ESTORNO_DA_DESPESA_EXTRA,
            entity.getUuid(),
            true);
    }

    @Override
    public void salvar(PagamentoExtraEstorno entity) {
        entity = recuperarObjetosParaHistorico(entity);
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public void movimentarReceitaExtra(List<ReceitaExtra> listaRecExtraSelecionadas) {
        for (ReceitaExtra re : listaRecExtraSelecionadas) {
            estornarReceitaExtra(re);
            receitaExtraFacade.salvar(re);
        }
    }

    public void estornarReceitaExtra(ReceitaExtra re) {
        BigDecimal saldoAtualizado = re.getValor().subtract(re.getValorEstornado());
        re.setSaldo(re.getSaldo().add(saldoAtualizado));
        re.setSituacaoReceitaExtra(SituacaoReceitaExtra.ABERTO);
    }

    public void criarAssociacaoPagamentoReceitaExtraEstorno(List<ReceitaExtra> listaRecExtraSelecionadas, PagamentoExtraEstorno entity) {
        entity.setPagamentoExtra(pagamentoExtraFacade.recuperar(entity.getPagamentoExtra().getId()));
        for (ReceitaExtra re : listaRecExtraSelecionadas) {
            PagamentoEstornoRecExtra pere = new PagamentoEstornoRecExtra();
            pere.setPagamentoExtraEstorno(entity);
            pere.setReceitaExtra(re);
            entity.getPagamentoEstornoRecExtras().add(pere);
        }
    }

    public void movimentarPagamentoExtra(PagamentoExtraEstorno entity) {
        PagamentoExtra pag = em.find(PagamentoExtra.class, entity.getPagamentoExtra().getId());
        pag.getPagamentoReceitaExtras().size();

        pag.setSaldo(pag.getSaldo().subtract(entity.getValor()));
        if (pag.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            if (pag.getDataConciliacao() == null) {
                pag.setDataConciliacao(sistemaFacade.getDataOperacao());
            }

            entity.setDataConciliacao(sistemaFacade.getDataOperacao());
            pag.setStatus(StatusPagamento.ESTORNADO);
        }
        entity.setUnidadeOrganizacional(pag.getUnidadeOrganizacional());
        entity.setUnidadeOrganizacionalAdm(pag.getUnidadeOrganizacionalAdm());

        for (PagamentoEstornoRecExtra pagamentoEstornoRecExtra : entity.getPagamentoEstornoRecExtras()) {
            for (PagamentoReceitaExtra pagamentoReceitaExtra : entity.getPagamentoExtra().getPagamentoReceitaExtras()) {
                if (pagamentoReceitaExtra.getReceitaExtra().equals(pagamentoEstornoRecExtra.getReceitaExtra())) {
                    pagamentoReceitaExtra.setPagamentoEstornoRecExtra(pagamentoEstornoRecExtra);
                }
            }
        }
        em.merge(pag);
    }

    public void salvarEstornoIntegracaoBancaria(PagamentoExtraEstorno entity) {
        entity.setValor(entity.getPagamentoExtra().getValor());
        setaEvento(entity);
        gerarSaldoExtraOrcamentario(entity);
        List<ReceitaExtra> listaPorPagamentoExtra = receitaExtraFacade.listaPorPagamentoExtra(entity.getPagamentoExtra());
        for (ReceitaExtra re : listaPorPagamentoExtra) {
            PagamentoEstornoRecExtra pere = new PagamentoEstornoRecExtra();
            pere.setPagamentoExtraEstorno(entity);
            pere.setReceitaExtra(re);
            entity.getPagamentoEstornoRecExtras().add(pere);
            re.setSituacaoReceitaExtra(SituacaoReceitaExtra.CANCELADO);
            receitaExtraFacade.salvar(re);
        }
        salvarNovo(entity);
    }

    public void setaEvento(PagamentoExtraEstorno entity) {
        Preconditions.checkNotNull(entity, "Estorno do Pagamento Extra não foi instanciado corretamente. Não foi possivel localizar o evento!");
        Preconditions.checkNotNull(entity.getPagamentoExtra(), "O pagamento extra esta vazio e não foi possivel encontrar o evento para o estorno");
        ContaExtraorcamentaria ce = (ContaExtraorcamentaria) entity.getPagamentoExtra().getContaExtraorcamentaria();
        Preconditions.checkNotNull(ce, "Erro ao buscar o evento, não foi possovel localizar a conta extra orçamentaria do evento");
        Preconditions.checkNotNull(ce.getTipoContaExtraorcamentaria(), "Erro ao buscar o evento, não foi possovel localizada o tipo da conta extra para a conta extra " + ce.getCodigo() + " - " + ce.getDescricao());
        ConfigDespesaExtra recuperaEvento = pagamentoExtraFacade.getConfigDespesaExtraFacade().recuperaEvento(TipoLancamento.ESTORNO, ce.getTipoContaExtraorcamentaria(), entity.getDataEstorno());
        Preconditions.checkNotNull(ce, "Não foi encontrado evento do tipo de lançamento Estorno, conta extra orçamentaria" + ce.getCodigo() + " tipo " + ce.getTipoContaExtraorcamentaria().getDescricao() + " na data " + DataUtil.getDataFormatada(entity.getDataEstorno()));
        entity.setEventoContabil(recuperaEvento.getEventoContabil());
    }

    public void gerarSaldoExtraOrcamentario(PagamentoExtraEstorno entity) throws ExcecaoNegocioGenerica {
        saldoExtraorcamentarioFacade.gerarSaldoExtraorcamentario(entity.getDataEstorno(), TipoOperacao.CREDITO, entity.getValor(), entity.getPagamentoExtra().getContaExtraorcamentaria(), entity.getPagamentoExtra().getContaDeDestinacao(), entity.getPagamentoExtra().getUnidadeOrganizacional());
    }

    private PagamentoExtraEstorno recuperarObjetosParaHistorico(PagamentoExtraEstorno entity) {
        if (!Hibernate.isInitialized(entity.getPagamentoEstornoRecExtras())) {
            entity = recuperar(entity.getId());
        }
        for (PagamentoEstornoRecExtra pagamentoReceitaExtra : entity.getPagamentoEstornoRecExtras()) {
            if (!Hibernate.isInitialized(pagamentoReceitaExtra.getPagamentoExtraEstorno().getPagamentoExtra().getPagamentoReceitaExtras())) {
                pagamentoReceitaExtra.getPagamentoExtraEstorno().setPagamentoExtra(pagamentoExtraFacade.recuperar(pagamentoReceitaExtra.getPagamentoExtraEstorno().getPagamentoExtra().getId()));
            }
            if (pagamentoReceitaExtra.getReceitaExtra().getRetencaoPgto() != null) {
                if (!Hibernate.isInitialized(pagamentoReceitaExtra.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getDesdobramentos())) {
                    pagamentoReceitaExtra.getReceitaExtra().getRetencaoPgto().getPagamento().setLiquidacao(liquidacaoFacade.recuperar(pagamentoReceitaExtra.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getId()));
                }
            }
        }
        return entity;
    }

    public void contabilizarDespesaExtraEstorno(PagamentoExtraEstorno entity) throws ExcecaoNegocioGenerica {
        entity = recuperarObjetosParaHistorico(entity);
        ConfigDespesaExtra ce = configDespesaExtraFacade.recuperaEvento(TipoLancamento.ESTORNO, ((ContaExtraorcamentaria) entity.getPagamentoExtra().getContaExtraorcamentaria()).getTipoContaExtraorcamentaria(), entity.getDataEstorno());
        if (ce != null) {
            if (ce.getEventoContabil() != null) {
                entity.setEventoContabil(ce.getEventoContabil());
            }
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Estorno de Despesa Extra.");
        }


        if (entity.getEventoContabil() != null) {

            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataEstorno());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabil());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), PagamentoExtraEstorno.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getPagamentoExtra().getContaExtraorcamentaria().getId().toString(), ContaExtraorcamentaria.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getPagamentoExtra().getSubConta().getId().toString(), SubConta.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getPagamentoExtra().getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    public ReceitaExtraFacade getReceitaExtraFacade() {
        return receitaExtraFacade;
    }

    public HistoricoContabilFacade getHistoricoContabilFacade() {
        return historicoContabilFacade;
    }

    public PagamentoExtraFacade getPagamentoExtraFacade() {
        return pagamentoExtraFacade;
    }

    public SaldoExtraorcamentarioFacade getSaldoExtraorcamentarioFacade() {
        return saldoExtraorcamentarioFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarDespesaExtraEstorno((PagamentoExtraEstorno) entidadeContabil);
    }

    public List<PagamentoExtraEstorno> recuperarEstornosPagamentoExtra(PagamentoExtra pag) {
        String sql = " select est.* from pagamentoextra pe " +
            " inner join pagamentoextraestorno est on est.pagamentoextra_id = pe.id " +
            " where pe.id = :param";
        Query q = em.createNativeQuery(sql, PagamentoExtraEstorno.class);
        q.setParameter("param", pag.getId());
        return q.getResultList();
    }

    public void estornarConciliacao(PagamentoExtraEstorno pagamentoExtraEstorno) {
        try {
            pagamentoExtraEstorno.setDataConciliacao(null);
            getEntityManager().merge(pagamentoExtraEstorno);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(PagamentoExtraEstorno.class, filtros);
        consulta.incluirJoinsComplementar(" inner join pagamentoExtra pag on obj.pagamentoExtra_id = pag.id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "pag.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "pag.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "pag.subConta_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "pag.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_EXTRA, "pag.contaExtraorcamentaria_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    public NotaExecucaoOrcamentaria buscarNotaPagamentoExtraEstorno(PagamentoExtraEstorno pagamentoExtraEstorno) {
        String sql = " SELECT nota.numero||'/'||exerc.ano as numero, " +
            "       nota.dataestorno as data_nota, " +
            "       coalesce(nota.historiconota, 'Histórico não cadastrado') as historico_nota, " +
            "       TRIM(vworg.CODIGO) AS CD_ORGAO, " +
            "       TRIM(vworg.DESCRICAO) AS DESC_ORAGAO, " +
            "       TRIM(vw.CODIGO) AS CD_UNIDADE, " +
            "       TRIM(vw.DESCRICAO) AS DESC_UNIDADE, " +
            "       coalesce(pf.nome, pj.razaosocial) as nome_pessoa, " +
            "       formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "       fonte.codigo || ' - ' || fonte.descricao as desc_fonte, " +
            "       NOTA.valor as valor, " +
            "       c.codigo || ' - ' || c.descricao as contaextra, " +
            "       frt_extenso_monetario(NOTA.valor)||'  ***********************' as valor_extenso, " +
            "       classe.codigo || ' - ' ||classe.descricao as desc_classepessoa, " +
            "       coalesce(pag.SALDO, 0) as saldo_anterior, " +
            "       nota.valor - coalesce(pag.SALDO, 0) as saldo_atual, " +
            "       COALESCE(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "       COALESCE(ENDERECO.uf,'sem UF ') AS UF, " +
            "       COALESCE(SUBSTR(ENDERECO.CEP,1,5)||'-'||SUBSTR(ENDERECO.CEP,6,3),'sem CEP') AS CEP, " +
            "       COALESCE(ENDERECO.LOGRADOURO,'Pessoa não possui Logradouro') AS LOGRADOURO, " +
            "       coalesce(endereco.localidade,'Pessoa não possui Localidade') as localidade, " +
            "       coalesce(ag.nomeagencia,'Pessoa não possui agência cadastrada') as nomeagencia, " +
            "       coalesce(banco.descricao,'Pessoa não possui banco cadastrado') as descricao, " +
            "       banco.numerobanco as numerobanco, " +
            "       ag.numeroagencia as numeroagencia, " +
            "       AG.DIGITOVERIFICADOR as digitoverificador, " +
            "       cc.numeroconta as numerocontacorrente, " +
            "       cc.digitoverificador as digitoverificadorcontacorrente, " +
            "       cbe.TIPOCONTABANCARIA as tipocontabancaria, " +
            "       cbe.SITUACAO as situacaocontabancaria, " +
            "       coalesce(banco.numerobanco||'/'||ag.numeroagencia || '-' || AG.DIGITOVERIFICADOR ||'/'||cc.numeroconta||'-'||cc.digitoverificador||cbe.TIPOCONTABANCARIA||cbe.SITUACAO,'Pessoa sem Dados Bancarios') as banco_gencia_conta, " +
            "       nota.unidadeorganizacional_id as idUnidade " +
            " FROM pagamentoextraestorno NOTA " +
            "   inner join pagamentoextra pag on nota.pagamentoextra_id = pag.id " +
            "   inner join fontederecursos fonte on pag.fontederecursos_id = fonte.id " +
            "   inner join conta c on contaextraorcamentaria_id = c.id " +
            "   INNER JOIN PESSOA Pes ON pag.fornecedor_id = Pes.id " +
            "   left join pessoafisica pf on pes.id = pf.id " +
            "   LEFT JOIN PESSOAJURIDICA PJ ON PES.ID = PJ.ID " +
            "   LEFT JOIN ENDERECOCORREIO ENDERECO ON PES.ENDERECOPRINCIPAL_ID = ENDERECO.ID " +
            "   left join ContaCorrenteBancaria cc on pag.CONTACORRENTEBANCARIA_ID = cc.id " +
            "   left join agencia ag on cc.agencia_id =ag.id " +
            "   left join banco banco on ag.banco_id=banco.id " +
            "   inner join PAGAMENTOEXTRA pg on pg.id = nota.PAGAMENTOEXTRA_ID " +
            "   inner join subconta sc on pg.SUBCONTA_ID = sc.id " +
            "   inner join contabancariaentidade cbe on sc.CONTABANCARIAENTIDADE_ID = cbe.id " +
            "   inner join vwhierarquiaorcamentaria vw on nota.unidadeorganizacional_id  = vw.subordinada_id " +
            "   inner join vwhierarquiaorcamentaria vworg on vw.superior_id  = vworg.subordinada_id " +
            "   inner join exercicio exerc on pag.exercicio_id = exerc.id " +
            "   left join classecredor classe on pag.classecredor_id = classe.id " +
            " where nota.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, nota.dataestorno) " +
            "   AND nota.dataestorno BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, NOTA.dataestorno) " +
            "   and nota.id = :idPagamentoExtraEstorno ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamentoExtraEstorno", pagamentoExtraEstorno.getId());
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
                nota.setId(pagamentoExtraEstorno.getId());
                nota.setNumeroDocumento((String) obj[0]);
                nota.setData(DataUtil.getDataFormatada((Date) obj[1]));
                nota.setHistorico((String) obj[2] + " Usuário Emissor: " + sistemaFacade.getUsuarioCorrente().getNome());
                nota.setCodigoOrgao((String) obj[3]);
                nota.setDescricaoOrgao((String) obj[4]);
                nota.setCodigoUnidade((String) obj[5]);
                nota.setDescricaoUnidade((String) obj[6]);
                nota.setNomePessoa((String) obj[7]);
                nota.setCpfCnpj((String) obj[8]);
                nota.setDescricaoDestinacao((String) obj[9]);
                nota.setValor((BigDecimal) obj[10]);
                nota.setContaExtraorcamentaria((String) obj[11]);
                nota.setValorPorExtenso((String) obj[12]);
                nota.setClassePessoa((String) obj[13]);
                nota.setSaldoAnterior((BigDecimal) obj[14]);
                nota.setSaldoAtual((BigDecimal) obj[15]);
                nota.setBairro((String) obj[16]);
                nota.setUf((String) obj[17]);
                nota.setCep((String) obj[18]);
                nota.setLogradouro((String) obj[19]);
                nota.setLocalidade((String) obj[20]);
                nota.setDescricaoAgencia((String) obj[21]);
                nota.setDescricaoBanco((String) obj[22]);
                nota.setNumeroBanco((String) obj[23]);
                nota.setNumeroAgencia((String) obj[24]);
                nota.setDigitoVerificador((String) obj[25]);
                nota.setNumeroContaCorrente((String) obj[26]);
                nota.setDigitoVerificadorContaCorrente((String) obj[27]);
                nota.setTipoContaBancaria((String) obj[28]);
                nota.setSituacaoContaBancaria((String) obj[29]);
                nota.setContaFinanceira((String) obj[30]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[31]);
                nota.setBancoAgenciaConta(nota.getNumeroAgencia() != null ?
                    nota.getNumeroBanco() + "/" + nota.getNumeroAgencia() +
                        (nota.getDigitoVerificador() != null ? " - " + nota.getDigitoVerificador() : "") + "/" +
                        nota.getNumeroContaCorrente() + " - " + nota.getDigitoVerificadorContaCorrente() + " - " +
                        TipoContaBancaria.valueOf(nota.getTipoContaBancaria()).getDescricao() + " - " +
                        SituacaoConta.valueOf(nota.getSituacaoContaBancaria()).getDescricao()
                    : "Pessoa sem Dados Bancários");
                retorno.add(nota);
            }
        }
        return retorno.get(0);
    }
}
