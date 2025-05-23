/*
 * Codigo gerado automaticamente em Mon Dec 26 14:45:38 BRST 2011
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
public class ReceitaExtraEstornoFacade extends SuperFacadeContabil<ReceitaExtraEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoExtraorcamentarioFacade saldoExtraorcamentarioFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigReceitaExtraFacade configReceitaExtraFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;

    public ReceitaExtraEstornoFacade() {
        super(ReceitaExtraEstorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SaldoExtraorcamentarioFacade getSaldoExtraorcamentarioFacade() {
        return saldoExtraorcamentarioFacade;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    public void movimentarReceitaExtra(ReceitaExtraEstorno ree) {
        ReceitaExtra re = receitaExtraFacade.recuperar(ree.getReceitaExtra().getId());
        estornarReceitaExtra(ree, re);
        em.merge(re);
    }

    public void estornarReceitaExtra(ReceitaExtraEstorno ree, ReceitaExtra re) {
        re.setSaldo(re.getSaldo().subtract(ree.getValor()));
        re.setValorEstornado(re.getValorEstornado().add(ree.getValor()));
        if (re.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
            re.setSituacaoReceitaExtra(SituacaoReceitaExtra.ESTORNADA);
            if (re.getDataConciliacao() == null) {
                re.setDataConciliacao(sistemaFacade.getDataOperacao());
            }
            ree.setDataConciliacao(sistemaFacade.getDataOperacao());
        }
    }

    public ReceitaExtraEstorno salvarNovoEstorno(ReceitaExtraEstorno entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                validarConcorrencia(entity);
                singletonConcorrenciaContabil.bloquear(entity.getReceitaExtra());
                entity.setUnidadeOrganizacionalAdm(entity.getReceitaExtra().getUnidadeOrganizacionalAdm());
                receitaExtraFacade.getHierarquiaOrganizacionalFacade().validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEstorno());
                movimentarReceitaExtra(entity);
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroReceitaExtra(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataEstorno()));
                    recuperarDesdobramentoParaHistorico(entity);
                    em.persist(entity);
                } else {
                    recuperarDesdobramentoParaHistorico(entity);
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                gerarSaldoExtraorcamentario(entity);
                gerarSaldoFinanceiro(entity);

                contabilizarReceitaExtraEstorno(entity);
                singletonConcorrenciaContabil.desbloquear(entity.getReceitaExtra());
            }
            return entity;
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (Exception e) {
            singletonConcorrenciaContabil.desbloquear(entity.getReceitaExtra());
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void validarConcorrencia(ReceitaExtraEstorno entity) {
        ValidacaoException ve = new ValidacaoException();
        if (!singletonConcorrenciaContabil.isDisponivel(entity.getReceitaExtra())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Receita Extra " + entity.getReceitaExtra() + " está sendo utilizada por outro usuário. Caso o problema persistir, selecione novamente a receita.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void gerarSaldoFinanceiro(ReceitaExtraEstorno entity) {
        if (entity.getReceitaExtra().getRetencaoPgto() == null) {
            saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacional(),
                entity.getReceitaExtra().getSubConta(),
                entity.getReceitaExtra().getContaDeDestinacao(),
                entity.getValor(),
                TipoOperacao.DEBITO,
                entity.getDataEstorno(),
                entity.getEventoContabil(),
                entity.getComplementoHistorico(),
                MovimentacaoFinanceira.ESTORNO_DE_RECEITA_EXTRA,
                entity.getUuid(),
                true);
        }
    }

    private void gerarSaldoExtraorcamentario(ReceitaExtraEstorno entity) {
        saldoExtraorcamentarioFacade.gerarSaldoExtraorcamentario(entity.getDataEstorno(),
            TipoOperacao.DEBITO,
            entity.getValor(),
            entity.getReceitaExtra().getContaExtraorcamentaria(),
            entity.getReceitaExtra().getContaDeDestinacao(),
            entity.getReceitaExtra().getUnidadeOrganizacional());
    }

    @Override
    public void salvar(ReceitaExtraEstorno entity) {
        recuperarDesdobramentoParaHistorico(entity);
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public ReceitaExtraEstorno salvarRetornando(ReceitaExtraEstorno entity) {
        try {
            recuperarDesdobramentoParaHistorico(entity);
            entity.gerarHistoricos();
            return em.merge(entity);
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void contabilizarReceitaExtraEstorno(ReceitaExtraEstorno entity) throws ExcecaoNegocioGenerica {
        recuperarDesdobramentoParaHistorico(entity);
        ConfigReceitaExtra configReceitaExtra = configReceitaExtraFacade.recuperaEventoPorContaReceita(TipoLancamento.ESTORNO, entity.getReceitaExtra());
        if (configReceitaExtra != null && configReceitaExtra.getEventoContabil() != null) {
            entity.setEventoContabil(configReceitaExtra.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil de Receita Extra para a operação selecionada.");
        }
        if (entity.getEventoContabil() != null) {

            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataEstorno());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setEventoContabil(entity.getEventoContabil());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);
            item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getReceitaExtra().getPessoa(), entity.getReceitaExtra().getClasseCredor(), entity.getDataEstorno()));

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), ReceitaExtraEstorno.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getReceitaExtra().getContaExtraorcamentaria().getId().toString(), ContaExtraorcamentaria.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getReceitaExtra().getSubConta().getId().toString(), SubConta.class.getSimpleName(), item));
            if (entity.getReceitaExtra().getRetencaoPgto() != null) {
                objetos.add(new ObjetoParametro(entity.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
                objetos.add(new ObjetoParametro(entity.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getId().toString(), ProvisaoPPADespesa.class.getSimpleName(), item));
                objetos.add(new ObjetoParametro(entity.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao().getFonteDeRecursos().getId().toString(), FonteDeRecursos.class.getSimpleName(), item));
                if (entity.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getDividaPublica() != null) {
                    objetos.add(new ObjetoParametro(entity.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getDividaPublica().getCategoriaDividaPublica().getId().toString(), CategoriaDividaPublica.class.getSimpleName(), item));
                    objetos.add(new ObjetoParametro(entity.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getEmpenho().getDividaPublica().getId().toString(), DividaPublica.class.getSimpleName(), item));
                }
            } else {
                objetos.add(new ObjetoParametro(entity.getReceitaExtra().getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
            }
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil de Estorno de Receita Extra.");
        }
    }

    private void recuperarDesdobramentoParaHistorico(ReceitaExtraEstorno entity) {
        if (entity.getReceitaExtra() != null) {
            if (entity.getReceitaExtra().getRetencaoPgto() != null) {
                if (!Hibernate.isInitialized(entity.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getDesdobramentos())) {
                    entity.getReceitaExtra().getRetencaoPgto().getPagamento().setLiquidacao(liquidacaoFacade.recuperar(entity.getReceitaExtra().getRetencaoPgto().getPagamento().getLiquidacao().getId()));
                }
            }
        }
    }

    public ConfigReceitaExtraFacade getConfigReceitaExtraFacade() {
        return configReceitaExtraFacade;
    }

    public SingletonGeradorCodigoContabil getSingletonGeradorCodigoContabil() {
        return singletonGeradorCodigoContabil;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public ReceitaExtraFacade getReceitaExtraFacade() {
        return receitaExtraFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarReceitaExtraEstorno((ReceitaExtraEstorno) entidadeContabil);
    }

    public void estornarConciliacao(ReceitaExtraEstorno receitaExtraEstorno) {
        try {
            receitaExtraEstorno.setDataConciliacao(null);
            getEntityManager().merge(receitaExtraEstorno);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a baixa da despesa extra. Consulte o suporte!");
        }
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(ReceitaExtraEstorno.class, filtros);
        consulta.incluirJoinsComplementar(" inner join receitaExtra rec on obj.receitaExtra_id = rec.id ");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "rec.pessoa_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "rec.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "rec.subConta_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "rec.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_EXTRA, "rec.contaExtraorcamentaria_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicorazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    public NotaExecucaoOrcamentaria buscarNotaReceitaExtraEstorno(ReceitaExtraEstorno receitaExtraEstorno) {
        String sql = " SELECT " +
            "     nota.numero||'/'||exerc.ano as numero, " +
            "     nota.dataestorno as data_nota, " +
            "     coalesce(nota.HISTORICONOTA, 'Histórico não cadastrado') as historico_nota, " +
            "     TRIM(vworg.CODIGO) AS CD_ORGAO, " +
            "     TRIM(vworg.DESCRICAO) AS DESC_ORAGAO, " +
            "     TRIM(vw.CODIGO) AS CD_UNIDADE, " +
            "     TRIM(vw.DESCRICAO) AS DESC_UNIDADE, " +
            "     coalesce(pf.nome, pj.razaosocial) as nome_pessoa, " +
            "     formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "     fonte.codigo || ' - ' || fonte.descricao as desc_fonte, " +
            "     NOTA.valor as valor, " +
            "     c.codigo || ' - ' || c.descricao as contaextra, " +
            "     frt_extenso_monetario(NOTA.valor)||'  ***********************' as valor_extenso, " +
            "     classe.codigo || ' - ' ||classe.descricao as desc_classepessoa, " +
            "     coalesce(rec.SALDO, 0) as saldo_anterior, " +
            "     coalesce(rec.SALDO, 0) - nota.valor as saldo_atual, " +
            "     COALESCE(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "     COALESCE(ENDERECO.uf,'sem UF ') AS UF, " +
            "     COALESCE(SUBSTR(ENDERECO.CEP,1,5)||'-'||SUBSTR(ENDERECO.CEP,6,3),'sem CEP') AS CEP, " +
            "     COALESCE(ENDERECO.LOGRADOURO,'Pessoa não possui Logradouro') AS LOGRADOURO, " +
            "     coalesce(endereco.localidade,'Pessoa não possui Localidade') as localidade, " +
            "     coalesce(ag.nomeagencia,'Pessoa não possui agência cadastrada') as nomeagencia, " +
            "     coalesce(banco.descricao,'Pessoa não possui banco cadastrado') as descricao, " +
            "     coalesce(replace(banco.numerobanco||' / '||ag.numeroagencia||' / '||cc.numeroconta||'-'||cc.digitoverificador,' //- '),'Pessoa sem Dados Bancarios') as banco_gencia_conta, " +
            "     banco.numerobanco as banco, " +
            "     nota.unidadeorganizacional_id as idUnidade " +
            "    FROM receitaextraestorno NOTA " +
            "      inner join receitaextra rec on nota.receitaextra_id = rec.id " +
            "      inner join fontederecursos fonte on rec.fontederecursos_id = fonte.id " +
            "      inner join conta c on contaextraorcamentaria_id = c.id " +
            "      INNER JOIN PESSOA Pes ON rec.pessoa_id = Pes.id " +
            "      left join pessoafisica pf on pes.id = pf.id " +
            "      LEFT JOIN PESSOAJURIDICA PJ ON PES.ID = PJ.ID " +
            "      LEFT JOIN ENDERECOCORREIO ENDERECO ON PES.ENDERECOPRINCIPAL_ID = ENDERECO.ID " +
            "      left join ContaCorrenteBancPessoa ccP on pes.id = ccp.pessoa_id and ccp.principal = 1 " +
            "      left join ContaCorrenteBancaria cc on ccp.ContaCorrenteBancaria_id = cc.id " +
            "      left join agencia ag on cc.agencia_id =ag.id " +
            "      left join banco banco on ag.banco_id=banco.id " +
            "      inner join vwhierarquiaorcamentaria vw on nota.unidadeorganizacional_id  = vw.subordinada_id " +
            "      inner join vwhierarquiaorcamentaria vworg on vw.superior_id  = vworg.subordinada_id " +
            "      inner join exercicio exerc on rec.exercicio_id = exerc.id " +
            "      left join classecredor classe on rec.classecredor_id = classe.id " +
            "    where nota.dataestorno between vw.iniciovigencia and coalesce(vw.fimvigencia, nota.dataestorno) " +
            "      AND nota.dataestorno BETWEEN VWORG.INICIOVIGENCIA AND COALESCE(VWORG.FIMVIGENCIA, NOTA.dataestorno) " +
            "      and nota.id = :idReceitaExtraEstorno ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idReceitaExtraEstorno", receitaExtraEstorno.getId());
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
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
                nota.setBancoAgenciaConta((String) obj[23]);
                nota.setNumeroBanco((String) obj[24]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[25]);
                retorno.add(nota);
            }
        }
        return retorno.get(0);
    }
}
