package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentariaDetalhamento;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentariaDocumentoComprobatorio;
import br.com.webpublico.enums.*;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
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

/**
 * Created by mga on 26/07/2017.
 */
@Stateless
public class ObrigacaoAPagarEstornoFacade extends SuperFacadeContabil<ObrigacaoAPagarEstorno> {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoObrigacaoAPagarFacade configuracaoObrigacaoAPagarFacade;
    @EJB
    private ObrigacaoAPagarFacade obrigacaoAPagarFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private EmpenhoFacade empenhoFacade;
    @EJB
    private DoctoFiscalLiquidacaoFacade doctoFiscalLiquidacaoFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;

    public ObrigacaoAPagarEstornoFacade() {
        super(ObrigacaoAPagarEstorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ObrigacaoAPagarEstorno recuperar(Object id) {
        ObrigacaoAPagarEstorno entity = em.find(ObrigacaoAPagarEstorno.class, id);
        entity.getDesdobramentos().size();
        entity.getDocumentosFiscais().size();
        return entity;
    }

    @Override
    public void salvar(ObrigacaoAPagarEstorno entity) {
        entity.realizarValidacoes();
        entity.gerarHistoricos();
        super.salvar(entity);
    }

    public ObrigacaoAPagarEstorno salvarEstorno(ObrigacaoAPagarEstorno entity) {
        try {
            if (getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroObrigacaoAPagar(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataEstorno()));
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                movimentarObrigacaoPagar(entity);
                movimentarSaldoEmpenho(entity);
                contabilizar(entity);
            }
            return entity;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void movimentarObrigacaoPagar(ObrigacaoAPagarEstorno entity) {
        ObrigacaoAPagar obrigacaoAPagar = obrigacaoAPagarFacade.recuperar(entity.getObrigacaoAPagar().getId());

        obrigacaoAPagar.setSaldo(obrigacaoAPagar.getSaldo().subtract(entity.getValor()));
        if (entity.getObrigacaoAPagar().isObrigacaoPagarAntesEmpenho()) {
            obrigacaoAPagar.setSaldoEmpenho(obrigacaoAPagar.getSaldoEmpenho().subtract(entity.getValor()));

        }
        movimentarSaldoDesdobramento(entity, obrigacaoAPagar);
        movimentarSaldoDocumentoFiscal(entity, obrigacaoAPagar);
        em.merge(obrigacaoAPagar);
    }

    private void movimentarSaldoDocumentoFiscal(ObrigacaoAPagarEstorno entity, ObrigacaoAPagar obrigacaoAPagar) {
        for (ObrigacaoPagarEstornoDoctoFiscal doctoEstorno : entity.getDocumentosFiscais()) {
            for (ObrigacaoAPagarDoctoFiscal doctoObrigacao : obrigacaoAPagar.getDocumentosFiscais()) {
                if (doctoEstorno.getDocumentoFiscal().equals(doctoObrigacao.getDocumentoFiscal())) {
                    doctoObrigacao.setSaldo(doctoObrigacao.getSaldo().subtract(doctoEstorno.getValor()));
                }
            }
        }
    }

    private void movimentarSaldoDesdobramento(ObrigacaoAPagarEstorno entity, ObrigacaoAPagar obrigacaoAPagar) {
        for (DesdobramentoObrigacaoAPagarEstorno desdEstorno : entity.getDesdobramentos()) {
            for (DesdobramentoObrigacaoPagar desdObrigacao : obrigacaoAPagar.getDesdobramentos()) {
                if (desdEstorno.getConta().equals(desdObrigacao.getConta())) {
                    desdObrigacao.setSaldo(desdObrigacao.getSaldo().subtract(desdEstorno.getValor()));
                }
            }
        }
    }

    private void movimentarSaldoEmpenho(ObrigacaoAPagarEstorno entity) {
        if (entity.getObrigacaoAPagar() != null && entity.getObrigacaoAPagar().isObrigacaoPagarDepoisEmpenho()) {
            Empenho empenho = empenhoFacade.recuperarSimples(entity.getObrigacaoAPagar().getEmpenho().getId());
            empenho.setSaldoObrigacaoPagarDepoisEmp(empenho.getSaldoObrigacaoPagarDepoisEmp().subtract(entity.getValor()));
            em.merge(empenho);
        }
    }

    private void contabilizar(ObrigacaoAPagarEstorno entity) {
        for (DesdobramentoObrigacaoAPagarEstorno desdobramento : entity.getDesdobramentos()) {
            contabilizarDesdobramento(desdobramento);
        }
    }

    private void contabilizarDesdobramento(DesdobramentoObrigacaoAPagarEstorno desdobramento) {
        if (!Hibernate.isInitialized(desdobramento.getObrigacaoAPagarEstorno().getDesdobramentos())) {
            desdobramento.setObrigacaoAPagarEstorno(recuperar(desdobramento.getObrigacaoAPagarEstorno().getId()));
        }
        EventoContabil eventoContabil = buscarEventoContabil(desdobramento);
        Preconditions.checkNotNull(eventoContabil, "Evento contábil não encontrado.");
        desdobramento.setEventoContabil(eventoContabil);
        desdobramento.getObrigacaoAPagarEstorno().gerarHistoricos();
        ParametroEvento parametroEvento = criarParametroEvento(desdobramento);
        contabilizacaoFacade.contabilizar(parametroEvento);
    }

    public ParametroEvento criarParametroEvento(DesdobramentoObrigacaoAPagarEstorno desdobramento) {
        ParametroEvento parametroEvento = getParametroEvento(desdobramento);
        ItemParametroEvento item = getItemParametroEvento(desdobramento.getValor(), parametroEvento);
        List<ObjetoParametro> listaObj = getObjetoParametros(desdobramento, item);
        item.setObjetoParametros(listaObj);
        parametroEvento.getItensParametrosEvento().add(item);
        return parametroEvento;
    }

    private List<ObjetoParametro> getObjetoParametros(DesdobramentoObrigacaoAPagarEstorno desdobramento, ItemParametroEvento item) {
        Preconditions.checkNotNull(desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getClasseCredor(), "A classe credor não foi preenchida.");
        Preconditions.checkNotNull(desdobramento.getConta(), "A conta de despesa não foi preenchida.");
        Preconditions.checkNotNull(desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos(), "A fonte de recurso não foi preenchida.");
        Preconditions.checkNotNull(desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa(), "A provisão ppa despesa não foi preenchida.");

        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(desdobramento.getObrigacaoAPagarEstorno(), item));
        objetos.add(new ObjetoParametro(desdobramento.getConta(), item));
        objetos.add(new ObjetoParametro(desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getDespesaORC().getProvisaoPPADespesa(), item));
        objetos.add(new ObjetoParametro(getFonteRecurso(desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar()), item));
        objetos.add(new ObjetoParametro(desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getClasseCredor(), item));
        objetos.add(new ObjetoParametro(desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getPessoa(), item));
        return objetos;
    }

    private ItemParametroEvento getItemParametroEvento(BigDecimal valor, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(valor);
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private ParametroEvento getParametroEvento(DesdobramentoObrigacaoAPagarEstorno desdobramento) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(desdobramento.getObrigacaoAPagarEstorno().getHistoricoRazao());
        parametroEvento.setDataEvento(desdobramento.getObrigacaoAPagarEstorno().getDataEstorno());
        parametroEvento.setUnidadeOrganizacional(desdobramento.getObrigacaoAPagarEstorno().getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(desdobramento.getEventoContabil());
        parametroEvento.setExercicio(desdobramento.getConta().getExercicio());
        parametroEvento.setClasseOrigem(DesdobramentoObrigacaoAPagarEstorno.class.getSimpleName());
        parametroEvento.setIdOrigem(desdobramento.getId() == null ? null : desdobramento.getId().toString());
        return parametroEvento;
    }

    private FonteDeRecursos getFonteRecurso(ObrigacaoAPagar entity) {
        Preconditions.checkNotNull(entity.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos(), "A destinação de recurso não foi preenchida.");
        Preconditions.checkNotNull(((ContaDeDestinacao) entity.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos(), "A fonte de recurso não foi preenchida.");
        return ((ContaDeDestinacao) entity.getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursos()).getFonteDeRecursos();
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        DesdobramentoObrigacaoAPagarEstorno desdobramento = (DesdobramentoObrigacaoAPagarEstorno) entidadeContabil;
        contabilizarDesdobramento(desdobramento);
    }

    public EventoContabil buscarEventoContabil(DesdobramentoObrigacaoAPagarEstorno desdobramento) {
        EventoContabil eventoContabil = null;
        ConfigObrigacaoAPagar configuracao = configuracaoObrigacaoAPagarFacade.buscarCDEObrigacaoPagar(
            desdobramento.getConta(),
            TipoLancamento.ESTORNO,
            desdobramento.getObrigacaoAPagarEstorno().getDataEstorno(),
            desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getTipoContaDespesa(),
            desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getSubTipoDespesa(),
            desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getTipoReconhecimento(),
            desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getEmpenho() != null ? desdobramento.getObrigacaoAPagarEstorno().getObrigacaoAPagar().getEmpenho().getCategoriaOrcamentaria() : CategoriaOrcamentaria.NORMAL);
        if (configuracao != null) {
            eventoContabil = configuracao.getEventoContabil();
        }
        return eventoContabil;
    }

    public ObrigacaoAPagarFacade getObrigacaoAPagarFacade() {
        return obrigacaoAPagarFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public DoctoFiscalLiquidacaoFacade getDoctoFiscalLiquidacaoFacade() {
        return doctoFiscalLiquidacaoFacade;
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(DesdobramentoObrigacaoAPagarEstorno.class, filtros);
        consulta.incluirJoinsComplementar(" inner join obrigacaoAPagarEstorno est on obj.obrigacaoAPagarEstorno_id = est.id ");
        consulta.incluirJoinsComplementar(" inner join obrigacaoAPagar obrig on est.obrigacaoAPagar_id = obrig.id ");
        consulta.incluirJoinsOrcamentoDespesa(" obrig.fontedespesaorc_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(est.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(est.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "est.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "est.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "obrig.pessoa_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "obrig.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "est.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    public List<NotaExecucaoOrcamentaria> buscarNotasDeEstornosDeObrigacoesAPagar(String condicao) {
        String sql = " select " +
            "    nota.numero||'/'||exerc.ano as numero, " +
            "    emp.numero || '/'||exerc.ano as numeroempenho, " +
            "    nota.dataestorno as data_nota, " +
            "    coalesce(nota.historiconota, 'Sem histórico') as historico_nota, " +
            "    emp.tipoempenho as tipo, " +
            "    trim(vworg.codigo) as cd_orgao, " +
            "    trim(vworg.descricao) as desc_orgao, " +
            "    trim(vw.codigo) as cd_unidade, " +
            "    trim(vw.descricao) as desc_unidade, " +
            "    substr(despesa.codigo, 8, 25) as cd_prog_trabalho, " +
            "    ct_desp.codigo as elemento, " +
            "    ct_desp.descricao as especificao_despesa, " +
            "    coalesce(pf.nome, pj.razaosocial)as nome_pessoa, " +
            "    formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "    fonte_r.codigo || ' - ' || fonte_r.descricao as descricaoDestinacao, " +
            "    nota.valor as valor, " +
            "    frt_extenso_monetario(nota.valor)||'  ***********************' as valor_extenso, " +
            "    coalesce(endereco.logradouro, 'Cidade não cadastrada para essa pessoa') as logradouro, " +
            "    coalesce(endereco.localidade, 'Cidade não cadastrada para essa pessoa') as localidade, " +
            "    a.descricao as desc_acao, " +
            "    lic.numerolicitacao || ' / ' || to_char(lic.emitidaem,'dd/MM/YYYY') as modalidade, " +
            "    emp.modalidadelicitacao as modalidadelicitacao, " +
            "    classe.codigo || ' - ' || classe.descricao as desc_classepessoa, " +
            "    coalesce(op.valor ,0) as valor_liquidado, " +
            "    coalesce(op.saldo, 0) as saldo_estornar, " +
            "    coalesce(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "    coalesce(endereco.uf,'sem UF ') as uf, " +
            "    coalesce(substr(endereco.cep,1,5)||'-'||substr(endereco.cep,6,3),'sem CEP') as cep, " +
            "    vw.subordinada_id as idUnidadeOrcamentaria, " +
            "    nota.id as id " +
            " from obrigacaoapagarestorno nota " +
            "    inner join obrigacaoapagar op on nota.obrigacaoapagar_id = op.id " +
            "    inner join fontedespesaorc fonte on op.fontedespesaorc_id = fonte.id " +
            "    inner join despesaorc despesa on fonte.despesaorc_id = despesa.id " +
            "    inner join provisaoppadespesa provisao on despesa.provisaoppadespesa_id= provisao.id " +
            "    inner join provisaoppafonte ppf on ppf.id = fonte.provisaoppafonte_id " +
            "    inner join subacaoppa sub on sub.id = provisao.subacaoppa_id " +
            "    inner join acaoppa a on a.id = sub.acaoppa_id " +
            "    inner join tipoacaoppa tpa on tpa.id = a.tipoacaoppa_id " +
            "    inner join programappa p on p.id = a.programa_id " +
            "    inner join funcao f on f.id = a.funcao_id " +
            "    inner join subfuncao sf on sf.id = a.subfuncao_id " +
            "    inner join conta ct_desp on provisao.contadedespesa_id = ct_desp.id " +
            "    inner join exercicio exerc on op.exercicio_id = exerc.id " +
            "    inner join vwhierarquiaorcamentaria vw on op.unidadeorganizacional_id  = vw.subordinada_id " +
            "    inner join vwhierarquiaorcamentaria vworg on vw.superior_id  = vworg.subordinada_id " +
            "    left join pessoa pes on op.pessoa_id = pes.id " +
            "    left join pessoafisica pf on pes.id = pf.id " +
            "    left join pessoajuridica pj on pes.id = pj.id " +
            "    left join enderecocorreio endereco on pes.enderecoprincipal_id = endereco.id " +
            "    left join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id " +
            "    left join fontederecursos fonte_r on fonte_r.id = cd.fontederecursos_id and fonte_r.exercicio_id = exerc.id " +
            "    left join empenho emp on op.empenho_id = emp.id " +
            "    left join contrato contrato on emp.contrato_id = contrato.id " +
            "    left join conlicitacao conlic on contrato.id = conlic.CONTRATO_ID " +
            "    left join licitacao lic on conlic.licitacao_id = lic.id " +
            "    left join classecredor classe on emp.classecredor_id = classe.id " +
            " where trunc(nota.dataestorno) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(nota.dataestorno)) " +
            "   and trunc(nota.dataestorno) between vworg.iniciovigencia and coalesce(vworg.fimvigencia, trunc(nota.dataestorno)) " +
            condicao;
        Query q = em.createNativeQuery(sql);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
                nota.setNomeDaNota("NOTA DE ESTORNO DE OBRIGAÇÃO A PAGAR");
                nota.setNumeroDocumento((String) obj[0]);
                nota.setNumeroEmpenho((String) obj[1]);
                nota.setData(DataUtil.getDataFormatada((Date) obj[2]));
                nota.setHistorico((String) obj[3]);
                nota.setTipoEmpenho(obj[4] != null ? TipoEmpenho.valueOf((String) obj[4]).getDescricao() : "");
                nota.setCodigoOrgao((String) obj[5]);
                nota.setDescricaoOrgao((String) obj[6]);
                nota.setCodigoUnidade((String) obj[7]);
                nota.setDescricaoUnidade((String) obj[8]);
                nota.setCodigoProgramaTrabalho((String) obj[9]);
                nota.setNaturezaDespesa((String) obj[10]);
                nota.setEspecificacaoDespesa((String) obj[11]);
                nota.setNomePessoa((String) obj[12]);
                nota.setCpfCnpj((String) obj[13]);
                nota.setDescricaoDestinacao((String) obj[14]);
                nota.setValor((BigDecimal) obj[15]);
                nota.setValorPorExtenso((String) obj[16]);
                nota.setLogradouro((String) obj[17]);
                nota.setLocalidade((String) obj[18]);
                nota.setCidade((String) obj[18]);
                nota.setDescricaoProjetoAtividade((String) obj[19]);
                nota.setModalidadeLicitacao(obj[21] != null ? ModalidadeLicitacaoEmpenho.valueOf((String) obj[21]).getDescricao() + " " + (String) obj[20] : "");
                nota.setClassePessoa((String) obj[22]);
                nota.setValorLiquidado((BigDecimal) obj[23]);
                nota.setSaldoAtual((BigDecimal) obj[24]);
                nota.setBairro((String) obj[25]);
                nota.setUf((String) obj[26]);
                nota.setCep((String) obj[27]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[28]);
                nota.setId(((BigDecimal) obj[29]).longValue());
                nota.setDocumentosComprobatorios(buscarDocumentos(nota.getId()));
                nota.setDetalhamentos(buscarDetalhamentos(nota.getId()));
                retorno.add(nota);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaDocumentoComprobatorio> buscarDocumentos(Long idObrigacaoAPagarEstorno) {
        String sql = " select " +
            "     coalesce(doc.numero, '') as numero, " +
            "     coalesce(doc.serie, '') as serie, " +
            "     doc.datadocto as data, " +
            "     coalesce(tipo.descricao, '') as tipo, " +
            "     coalesce(uf.nome, '') as uf, " +
            "     coalesce(obr.valor, 0) as valor " +
            " from obrigacaopagestdoctofiscal obr " +
            "    inner join doctofiscalliquidacao doc on obr.documentofiscal_id = doc.id " +
            "    left join tipodocumentofiscal tipo on doc.tipodocumentofiscal_id = tipo.id " +
            "    left join uf uf on doc.uf_id = uf.id " +
            " where obr.obrigacaoapagarestorno_id = :idObrigacaoAPagarEstorno ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObrigacaoAPagarEstorno", idObrigacaoAPagarEstorno);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaDocumentoComprobatorio> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaDocumentoComprobatorio dc = new NotaExecucaoOrcamentariaDocumentoComprobatorio();
                dc.setNumero((String) obj[0]);
                dc.setSerie((String) obj[1]);
                dc.setData((Date) obj[2]);
                dc.setTipo((String) obj[3]);
                dc.setUf((String) obj[4]);
                dc.setValor((BigDecimal) obj[5]);
                retorno.add(dc);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaDetalhamento> buscarDetalhamentos(Long idObrigacaoAPagarEstorno) {
        String sql = " select " +
            "      c.codigo as conta, " +
            "      c.descricao, " +
            "      eve.codigo as evento, " +
            "      desd.valor " +
            " from desdobramentoobrigacaoest desd " +
            "    inner join conta c on desd.conta_id = c.id " +
            "    left join eventocontabil eve on desd.eventocontabil_id = eve.id " +
            " where desd.obrigacaoapagarestorno_id = :idObrigacaoAPagarEstorno ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObrigacaoAPagarEstorno", idObrigacaoAPagarEstorno);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaDetalhamento> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaDetalhamento d = new NotaExecucaoOrcamentariaDetalhamento();
                d.setConta((String) obj[0]);
                d.setDescricao((String) obj[1]);
                d.setEvento((String) obj[2]);
                d.setValor((BigDecimal) obj[3]);
                retorno.add(d);
            }
        }
        return retorno;
    }
}
