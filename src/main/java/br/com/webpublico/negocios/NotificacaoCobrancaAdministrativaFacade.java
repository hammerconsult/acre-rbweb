package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteNotificacaoCobranca;
import br.com.webpublico.entidadesauxiliares.IdCalculoIdCadastroView;
import br.com.webpublico.enums.AgrupadorCobrancaAdm;
import br.com.webpublico.enums.TipoAcaoCobranca;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.enums.TipoSituacaoProgramacaoCobranca;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Wanderley
 * Date: 29/11/13
 * Time: 09:37
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class NotificacaoCobrancaAdministrativaFacade extends AbstractFacade<NotificacaoCobrancaAdmin> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public NotificacaoCobrancaAdministrativaFacade() {
        super(NotificacaoCobrancaAdmin.class);
    }

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private CalculoFacade calculoFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    @EJB
    private DAMFacade damFacade;
    @EJB
    private ProgramacaoCobrancaFacade programacaoCobrancaFacade;
    @EJB
    private ParametroCobrancaAdministrativaFacade parametroCobrancaAdministrativaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemNotificacao salvarObjeto(ItemNotificacao obj) {
        return em.merge(obj);
    }

    @Override
    public NotificacaoCobrancaAdmin recuperar(Object id) {
        return inicializar(super.recuperar(id));
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteNotificacaoCobranca> salvarNotificacaoAdm(NotificacaoCobrancaAdmin notificacaoAdmin,
                                                                      AssistenteBarraProgresso assistente,
                                                                      AssistenteNotificacaoCobranca assistenteNotificacao) throws Exception {

        assistente.setDescricaoProcesso("Salvando Aviso/Notificação de Cobrança Administrativa...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(notificacaoAdmin.getItemNotificacaoLista().size());

        processarNotificacao(notificacaoAdmin, assistente, assistenteNotificacao);

        List<ItemNotificacao> itens = Lists.newArrayList(notificacaoAdmin.getItemNotificacaoLista());
        notificacaoAdmin.getItemNotificacaoLista().clear();

        notificacaoAdmin = inserirDataVencimento(notificacaoAdmin);

        notificacaoAdmin = em.merge(notificacaoAdmin);

        assistente.setDescricaoProcesso("Salvando Itens do Aviso/Notificação de Cobrança Administrativa...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(itens.size());

        for (ItemNotificacao itemNotificacao : itens) {
            itemNotificacao.setNotificacaoADM(notificacaoAdmin);
            salvarObjeto(itemNotificacao);
            assistente.conta();
        }

        atribuirSituacaoProgramacao(notificacaoAdmin, assistente);
        gerarAceiteParaTodosOsItens(assistente, notificacaoAdmin);

        assistenteNotificacao.setSelecionado(notificacaoAdmin);
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("");

        return new AsyncResult<>(assistenteNotificacao);
    }

    private NotificacaoCobrancaAdmin inserirDataVencimento(NotificacaoCobrancaAdmin notificacaoAdmin){
        ParametrosCobrancaAdministrativa parametrosCobrancaAdministrativa = parametroCobrancaAdministrativaFacade.parametrosCobrancaAdministrativaPorExercicio(notificacaoAdmin.
            getProgramacaoCobranca().getExercicio());
        Date data = SistemaFacade.getDataCorrente();
        Calendar dataOperacao = Calendar.getInstance();
        dataOperacao.setTime(data);
        dataOperacao.set(Calendar.DAY_OF_MONTH,dataOperacao.get(Calendar.DAY_OF_MONTH)+ parametrosCobrancaAdministrativa.getDiasAposNotificacao().intValue());
        notificacaoAdmin.setVencimentoNotificacao(dataOperacao.getTime());
        return notificacaoAdmin;
    }

    private void atribuirSituacaoProgramacao(NotificacaoCobrancaAdmin notificacaoAdmin, AssistenteBarraProgresso assistente) {

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Gerando nova situação para programação de cobrança...");
        assistente.setTotal(notificacaoAdmin.getProgramacaoCobranca().getListaSituacaoProgramacaoCobrancas().size());

        ProgramacaoCobranca programacaoCobranca = getProgramacaoCobrancaFacade().recuperarSimples(notificacaoAdmin.getProgramacaoCobranca().getId());
        SituacaoProgramacaoCobranca situacao = getProgramacaoCobrancaFacade().ultimaSituacaoProgramacaoCobranca(programacaoCobranca);

        if (TipoSituacaoProgramacaoCobranca.PROGRAMADO.equals(situacao.getSituacaoProgramacaoCobranca())) {
            programacaoCobranca.getListaSituacaoProgramacaoCobrancas().add(new SituacaoProgramacaoCobranca(programacaoCobranca,
                TipoSituacaoProgramacaoCobranca.EXECUTANDO, assistente.getDataAtual()));
            assistente.conta();
            programacaoCobrancaFacade.salvarProgramacao(programacaoCobranca);
        }
    }


    @Override
    public Object recuperar(Class entidade, Object id) {
        return inicializar((NotificacaoCobrancaAdmin) super.recuperar(entidade, id));
    }

    public NotificacaoCobrancaAdmin recuperarSimples(NotificacaoCobrancaAdmin obj) {
        NotificacaoCobrancaAdmin p = em.find(NotificacaoCobrancaAdmin.class, obj);
        p.getItemNotificacaoLista().size();
        return p;
    }

    private NotificacaoCobrancaAdmin inicializar(NotificacaoCobrancaAdmin obj) {
        if (obj.getId() != null) {
            obj.getItemNotificacaoLista().size();
            for (ItemNotificacao item : obj.getItemNotificacaoLista()) {
                item.getItemProgramacaoCobrancaLista().size();
            }
            Hibernate.initialize(obj.getProgramacaoCobranca());
            Hibernate.initialize(obj.getProgramacaoCobranca().getListaSituacaoProgramacaoCobrancas());
        }
        return obj;
    }

    @Override
    public void remover(NotificacaoCobrancaAdmin entity) {
        for (ItemNotificacao itemNotificacao : entity.getItemNotificacaoLista()) {
            for (ItemProgramacaoCobranca itemProgramacaoCobranca : itemNotificacao.getItemProgramacaoCobrancaLista()) {
                itemProgramacaoCobranca.setItemNotificacao(null);
                em.merge(itemNotificacao);
            }
        }
        entity.setItemNotificacaoLista(null);
        super.remover(entity);
    }

    public NotificacaoCobrancaAdmin recarregarNotificacao(Object id) {
        NotificacaoCobrancaAdmin notificacao = em.find(NotificacaoCobrancaAdmin.class, id);
        return inicializar(notificacao);
    }

    public Boolean jaPossuiNotificacao(ProgramacaoCobranca programacao) {
        Query retorno = em.createQuery("select nt from NotificacaoCobrancaAdmin " +
            "nt where nt.programacaoCobranca = :programacao").setParameter("programacao", programacao);
        return retorno.getResultList().size() > 0;
    }

    public List<ItemNotificacao> recuperaItemNotificacaoSemAceite(NotificacaoCobrancaAdmin notificacao) {
        String sql = " select * from itemnotificacao " +
            " where notificacaoadm_id = :notificacao";
        Query q = em.createNativeQuery(sql, ItemNotificacao.class);
        q.setParameter("notificacao", notificacao.getId());
        return q.getResultList();
    }

    public List<ItemNotificacao> recuperaItemNotificacaoComAceite(NotificacaoCobrancaAdmin notificacao) {
        Query retorno = em.createQuery("select item " +
            "                             from ItemNotificacao item " +
            "                       where item.notificacaoADM = :notificacao and aceite is not null ");
        retorno.setParameter("notificacao", notificacao);
        return retorno.getResultList();
    }

    public List<ItemProgramacaoCobranca> buscarItemProgramacaoDoItemNotificacao(ItemNotificacao item) {
        String sql = " select item.* " +
            " from itemprogramacaocobranca item" +
            " where item.itemnotificacao_id = :itemNotificacao ";
        Query q = em.createNativeQuery(sql, ItemProgramacaoCobranca.class);
        q.setParameter("itemNotificacao", item.getId());

        return q.getResultList();
    }

    public List<ItemNotificacao> buscarItemNotificacaoDaNotificacao(NotificacaoCobrancaAdmin notificacao) {
        return em.createQuery("select it " +
            "from NotificacaoCobrancaAdmin notif " +
            "join notif.itemNotificacaoLista it " +
            "where notif.id = :id_notificacao").setParameter("id_notificacao", notificacao.getId()).getResultList();
    }

    public List<NotificacaoCobrancaAdmin> listaNotificacaoParaAceite(String filtro) {
        String sql = " select notif.* " +
            "       from notificacaocobrancaadmin notif " +
            " inner join programacaocobranca prog on notif.programacaocobranca_id = prog.id " +
            " inner join situacao_prog_cobranca sit on sit.id = (select max(id) " +
            "                                                      from situacao_prog_cobranca s_sit " +
            "                                                     where s_sit.programacaocobranca_id = prog.id) " +
            "      where (prog.numero like :filtro " +
            "         or prog.descricao like :filtro) " +
            "        and sit.situacaoprogramacaocobranca in(" + "'" + TipoSituacaoProgramacaoCobranca.PROGRAMADO.name() + "', '" + TipoSituacaoProgramacaoCobranca.EXECUTANDO.name() + "' " + ") ";

        Query q = em.createNativeQuery(sql, NotificacaoCobrancaAdmin.class);
        q.setParameter("filtro", "%" + filtro.trim() + "%");
        return q.getResultList();
    }

    public NotificacaoCobrancaAdmin salvarMerge(NotificacaoCobrancaAdmin obj) {
        return inicializar(em.merge(obj));
    }

    public ItemNotificacao salvarItemNotificacao(ItemNotificacao itemNotificacao) {
        return em.merge(itemNotificacao);
    }

    public List<DAM> recuperaListaDAM(ItemNotificacao item) {
        String sql = "select d.*  " +
            "      from dam d  " +
            "      inner join documentonotificacaodam dnd on dnd.dam_id = d.id  " +
            "      Inner Join Documentonotificacao Dn On Dn.Id = Dnd.Documentonotificacao_Id  " +
            "      Inner Join Itemnotificacao Itn On Itn.Id = Dn.Itemnotificacao_Id  " +
            "      Inner Join Itemdam Itemdam On Itemdam.Dam_Id = D.Id  " +
            "      Inner Join Parcelavalordivida Pvd On Pvd.Id = Itemdam.Parcela_Id  " +
            "      Inner Join Situacaoparcelavalordivida Spvd On Spvd.Id = pvd.situacaoAtual_id " +
            "      where Spvd.Situacaoparcela = 'EM_ABERTO' and itn.id = :item";
        Query q = em.createNativeQuery(sql, DAM.class);
        q.setParameter("item", item.getId());
        List<DAM> lista = q.getResultList();
        if (lista != null && !lista.isEmpty()) {
            for (DAM d : lista) {
                d.getItens().size();
                for (ItemDAM itemDAM : d.getItens()) {
                    itemDAM.getParcela().getValorDivida().getCalculo().getId();
                }
            }
            return lista;
        }
        return null;
    }

    public List<ParcelaValorDivida> recuperaListaParcelas(List<Long> idParcelas) {
        String sql = "select pvd.*   " +
            "      From  Parcelavalordivida pvd  " +
            "      Inner Join Situacaoparcelavalordivida Spvd On Spvd.Id = pvd.SITUACAOATUAL_ID  " +
            "      where Spvd.Situacaoparcela = 'EM_ABERTO' and pvd.id in :parcelas ";
        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("parcelas", idParcelas);
        return q.getResultList();
    }

    public IdCalculoIdCadastroView buscarIdCalculoIdCadastroPorIdValorDivida(Long idValorDivida) {
        String sql = "select c.cadastro_id, c.id from calculo c " +
            " inner join valordivida vd on vd.calculo_id = c.id " +
            " where vd.id = :idValorDivida";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idValorDivida", idValorDivida);
        List<Object[]> lista = q.getResultList();
        IdCalculoIdCadastroView retorno = null;
        if (!lista.isEmpty()) {
            Object[] obj = lista.get(0);
            retorno = new IdCalculoIdCadastroView();
            retorno.setIdCadastro(obj[0] != null ? ((BigDecimal) obj[0]).longValue() : null);
            retorno.setIdCalculo(obj[1] != null ? ((BigDecimal) obj[1]).longValue() : null);
        }
        return retorno;
    }

    public void buscarAvisosAndNotificacoesAdministrativasComPrazoVencido(Date data) {
        String sql = " select nca.id from notificacaocobrancaadmin nca " +
            " where trunc(nca.vencimentodam) < to_date(:dataLogada, 'dd/MM/yyyy') ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("dataLogada", DataUtil.getDataFormatada(data));

        List<BigDecimal> ids = q.getResultList();
        if (!ids.isEmpty()) {
            for (BigDecimal id : ids) {
                gerarNotificacaoDeVenciamentoAvisoNotificao(id.longValue());
            }
        }
    }

    private void gerarNotificacaoDeVenciamentoAvisoNotificao(Long idCobranca) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Existem notificações/avisos vencidas.");
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_NOTIFICAO_COBRANCA_ADMINISTRATIVA_VENCIDA.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_NOTIFICAO_COBRANCA_ADMINISTRATIVA_VENCIDA);
        notificacao.setLink("/baixa-notificacao-administrativa/novo/?notificacaoCobrancaAdmin=" + idCobranca + "&amp;");
        NotificacaoService.getService().notificar(notificacao);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public Future<AssistenteNotificacaoCobranca> agruparDebitos(NotificacaoCobrancaAdmin selecionado,
                                                                ProgramacaoCobranca programacao, AssistenteBarraProgresso assistente) {

        AssistenteNotificacaoCobranca assistenteCobranca = new AssistenteNotificacaoCobranca(selecionado);

        selecionado.setProgramacaoCobranca(programacao);

        List<ResultadoParcela> resultadoParcelas = pesquisarParcelasItemProgramacao(selecionado, assistente);
        assistenteCobranca.setResultadosParcela(resultadoParcelas);

        if (AgrupadorCobrancaAdm.CADASTRO.equals(selecionado.getAgrupado())) {
            assistenteCobranca.setSelecionado(agruparCadastro(selecionado, assistente));
        }
        if (AgrupadorCobrancaAdm.CONTRIBUINTE.equals(selecionado.getAgrupado())) {
            assistenteCobranca.setSelecionado(agruparContribuinte(selecionado, assistente));
        }
        return new AsyncResult<>(assistenteCobranca);
    }

    private List<ResultadoParcela> pesquisarParcelasItemProgramacao(NotificacaoCobrancaAdmin selecionado, AssistenteBarraProgresso assistente) {

        assistente.setDescricaoProcesso("Pesquisando Débitos ...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(selecionado.getProgramacaoCobranca().getListaItemProgramacaoCobrancas().size());

        List<ResultadoParcela> resultadoConsulta = Lists.newArrayList();
        List<Long> ids = Lists.newArrayList();
        if (selecionado.getProgramacaoCobranca().getListaItemProgramacaoCobrancas().size() > 1000) {
            for (ItemProgramacaoCobranca item : selecionado.getProgramacaoCobranca().getListaItemProgramacaoCobrancas()) {
                ids.add(item.getParcelaValorDivida().getId());
                assistente.conta();
                if (ids.size() == 1000) {
                    novaConsultaParcela(ids, resultadoConsulta);
                    ids.clear();
                }
            }
            if (!ids.isEmpty()) {
                novaConsultaParcela(ids, resultadoConsulta);
            }
        } else if (selecionado.getProgramacaoCobranca().getListaItemProgramacaoCobrancas().size() > 0) {
            novaConsultaParcela(getIdsParcelaNaProgramacao(selecionado), resultadoConsulta);
        }

        return resultadoConsulta;
    }

    private void novaConsultaParcela(List<Long> idsParcela, List<ResultadoParcela> resultadoConsulta) {
        ConsultaParcela consulta = new ConsultaParcela();
        consulta.addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IN, idsParcela);
        consulta.executaConsulta();
        resultadoConsulta.addAll(consulta.getResultados());
    }

    private List<Long> getIdsParcelaNaProgramacao(NotificacaoCobrancaAdmin selecionado) {
        List<Long> idsParcela = Lists.newArrayList();
        for (ItemProgramacaoCobranca item : selecionado.getProgramacaoCobranca().getListaItemProgramacaoCobrancas()) {
            idsParcela.add(item.getParcelaValorDivida().getId());
        }
        return idsParcela;
    }

    private NotificacaoCobrancaAdmin agruparContribuinte(NotificacaoCobrancaAdmin selecionado, AssistenteBarraProgresso assistente) {

        HashMap<Pessoa, List<ItemProgramacaoCobranca>> contribuintesAgrupados = new HashMap<>();

        assistente.setDescricaoProcesso("Agrupando Débitos por Contribuinte...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(selecionado.getProgramacaoCobranca().getListaItemProgramacaoCobrancas().size());

        for (ItemProgramacaoCobranca item : selecionado.getProgramacaoCobranca().getListaItemProgramacaoCobrancas()) {
            Calculo calculo = calculoFacade.retornaCalculoDoValorDivida(item.getParcelaValorDivida().getValorDivida());
            if (calculo == null) {
                continue;
            }
            Pessoa pessoa = !calculo.getPessoas().isEmpty() ? calculo.getPessoas().get(0).getPessoa() : new PessoaFisica();
            if (contribuintesAgrupados.get(pessoa) == null) {
                contribuintesAgrupados.put(pessoa, new ArrayList<ItemProgramacaoCobranca>());
            }
            contribuintesAgrupados.get(pessoa).add(item);
            assistente.conta();
        }

        assistente.setDescricaoProcesso("Populando itens da Notificação de Cobrança...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(contribuintesAgrupados.keySet().size());

        String replace = selecionado.getProgramacaoCobranca().getNumero().replace("/", "");
        BigDecimal codigoitem = new BigDecimal(replace);
        codigoitem = codigoitem.multiply(new BigDecimal(10000));
        selecionado.setItemNotificacaoLista(new ArrayList<ItemNotificacao>());
        for (Pessoa pessoa : contribuintesAgrupados.keySet()) {
            ItemNotificacao itemNotificacao = new ItemNotificacao();
            itemNotificacao.setContribuinte(pessoa);
            itemNotificacao.setNumero(codigoitem);
            itemNotificacao.setNotificacaoADM(selecionado);
            codigoitem = codigoitem.add(BigDecimal.ONE);
            for (ItemProgramacaoCobranca item : contribuintesAgrupados.get(pessoa)) {
                item.setItemNotificacao(itemNotificacao);
                itemNotificacao.getItemProgramacaoCobrancaLista().add(item);
            }
            selecionado.getItemNotificacaoLista().add(itemNotificacao);
            assistente.conta();
        }
        return selecionado;
    }

    private NotificacaoCobrancaAdmin agruparCadastro(NotificacaoCobrancaAdmin selecionado, AssistenteBarraProgresso assistente) {

        assistente.setDescricaoProcesso("Agrupando Débitos por Cadastro...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(selecionado.getProgramacaoCobranca().getListaItemProgramacaoCobrancas().size());

        HashMap<Long, List<ItemProgramacaoCobranca>> cadastrosAgrupados = new HashMap<>();

        for (ItemProgramacaoCobranca item : selecionado.getProgramacaoCobranca().getListaItemProgramacaoCobrancas()) {
            IdCalculoIdCadastroView calculo = buscarIdCalculoIdCadastroPorIdValorDivida(item.getParcelaValorDivida().getValorDivida().getId());
            if (cadastrosAgrupados.get(calculo.getIdCadastro()) == null) {
                cadastrosAgrupados.put(calculo.getIdCadastro(), new ArrayList<ItemProgramacaoCobranca>());
            }
            cadastrosAgrupados.get(calculo.getIdCadastro()).add(item);
            assistente.conta();
        }

        assistente.setDescricaoProcesso("Populando itens da Notificação de Cobrança...");
        assistente.zerarContadoresProcesso();
        assistente.setTotal(cadastrosAgrupados.keySet().size());

        String replace = selecionado.getProgramacaoCobranca().getNumero().replace("/", "");
        BigDecimal codigoitem = new BigDecimal(replace);
        codigoitem = codigoitem.multiply(new BigDecimal(10000));
        selecionado.setItemNotificacaoLista(new ArrayList<ItemNotificacao>());
        for (Long idCadastro : cadastrosAgrupados.keySet()) {
            ItemNotificacao itemNotificacao = new ItemNotificacao();
            itemNotificacao.setCadastro(cadastroFacade.recuperar(idCadastro));
            itemNotificacao.setNumero(codigoitem);
            itemNotificacao.setNotificacaoADM(selecionado);
            codigoitem = codigoitem.add(BigDecimal.ONE);
            for (ItemProgramacaoCobranca item : cadastrosAgrupados.get(idCadastro)) {
                item.setItemNotificacao(itemNotificacao);
                itemNotificacao.getItemProgramacaoCobrancaLista().add(item);
            }
            selecionado.getItemNotificacaoLista().add(itemNotificacao);
            assistente.conta();
        }

        return selecionado;
    }

    private void processarNotificacao(NotificacaoCobrancaAdmin selecionado, AssistenteBarraProgresso assistente, AssistenteNotificacaoCobranca assistenteCobrancao) throws Exception {

        if (selecionado.getEmitirGuia()) {

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Gerando DAM para Itens...");
            assistente.setTotal(selecionado.getItemNotificacaoLista().size());

            for (ItemNotificacao item : selecionado.getItemNotificacaoLista()) {
                gerarDAM(item, assistente, selecionado);
                gerarNotificacao(assistenteCobrancao, item, selecionado.getTipoAcaoCobranca());
                assistente.conta();
            }
        } else if (selecionado.getDamAgrupado()) {

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Emitindo DAM Agrupado para os Itens...");
            assistente.setTotal(selecionado.getItemNotificacaoLista().size());

            for (ItemNotificacao item : selecionado.getItemNotificacaoLista()) {
                gerarDAMAgrupado(item, assistente, selecionado);
                gerarNotificacao(assistenteCobrancao, item, selecionado.getTipoAcaoCobranca());
                assistente.conta();
            }

        } else {
            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Emitindo Notificação de Cobrança para os Itens...");
            assistente.setTotal(selecionado.getItemNotificacaoLista().size());

            for (ItemNotificacao item : selecionado.getItemNotificacaoLista()) {
                gerarNotificacao(assistenteCobrancao, item, selecionado.getTipoAcaoCobranca());
                assistente.conta();
            }
        }
    }

    private void gerarDAM(ItemNotificacao item, AssistenteBarraProgresso assistente, NotificacaoCobrancaAdmin selecionado) throws Exception {
        if (item != null) {

            List<DAM> dams = damFacade.geraDAMs(item.parcelas(), assistente.getExercicio(), assistente.getUsuarioSistema());
            selecionado.setVencimentoDam(dams.get(0).getVencimento());

            for (DAM dam : dams) {
                DocumentoNotificacaoDAM documentoNotificacaoDAM = new DocumentoNotificacaoDAM();
                documentoNotificacaoDAM.setDocumentoNotificacao(item.getDocumentoNotificacao());
                documentoNotificacaoDAM.setDam(dam);
                documentoNotificacaoDAM.getDocumentoNotificacao().setItemNotificacao(item);
                item.getDocumentoNotificacao().getListaDocumentoNotificacaoDAM().add(documentoNotificacaoDAM);
            }
        }
    }

    private void gerarDAMAgrupado(ItemNotificacao item, AssistenteBarraProgresso assistente, NotificacaoCobrancaAdmin selecionado) {
        if (item != null) {

            DAM dam = damFacade.geraDAM(item.parcelas(), selecionado.getVencimentoDam(), assistente.getExercicio(), assistente.getUsuarioSistema());
            DocumentoNotificacaoDAM documentoNotificacaoDAM = new DocumentoNotificacaoDAM();
            documentoNotificacaoDAM.setDam(dam);
            documentoNotificacaoDAM.setDocumentoNotificacao(item.getDocumentoNotificacao());
            item.getDocumentoNotificacao().getListaDocumentoNotificacaoDAM().add(0, documentoNotificacaoDAM);
        }
    }

    public void gerarNotificacao(AssistenteNotificacaoCobranca assistente, ItemNotificacao item, TipoAcaoCobranca tipoAcaoCobranca) throws Exception {
        if (item != null) {
            programacaoCobrancaFacade.geraDocumento(assistente, item, tipoAcaoCobranca);
        }
    }

    private void gerarAceiteParaTodosOsItens(AssistenteBarraProgresso assistente, NotificacaoCobrancaAdmin selecionado) {

        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Aceite para todos os Itens...");
        assistente.setTotal(selecionado.getItemNotificacaoLista().size());

        for (ItemNotificacao item : selecionado.getItemNotificacaoLista()) {

            if (item.getAceite() == null) {
                Aceite aceite = new Aceite(assistente.getUsuarioSistema(), assistente.getDataAtual());
                item.setAceite(aceite);
                assistente.conta();
            }
        }
    }

    public DAMFacade getDamFacade() {
        return damFacade;
    }

    public ProgramacaoCobrancaFacade getProgramacaoCobrancaFacade() {
        return programacaoCobrancaFacade;
    }

    public ParametroCobrancaAdministrativaFacade getParametroCobrancaAdministrativaFacade() {
        return parametroCobrancaAdministrativaFacade;
    }
}
