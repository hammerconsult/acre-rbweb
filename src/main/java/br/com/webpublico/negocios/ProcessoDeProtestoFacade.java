package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ObjetoCampoValor;
import br.com.webpublico.enums.SituacaoCertidaoDA;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.exception.AtributosNulosException;
import br.com.webpublico.exception.UFMException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.eventos.NovoParcelamentoEvento;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.*;
import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ProcessoDeProtestoFacade extends AbstractFacade<ProcessoDeProtesto> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private ArquivoFacade arquivoFacade;
    @EJB
    private CadastroImobiliarioFacade cadastroImobiliarioFacade;
    @EJB
    private CadastroRuralFacade cadastroRuralFacade;
    @EJB
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    @EJB
    private LoteFacade loteFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private CadastroFacade cadastroFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private TipoDoctoOficialFacade tipoDoctoOficialFacade;
    @EJB
    private RemessaProtestoFacade remessaFacade;
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;

    public ProcessoDeProtestoFacade() {
        super(ProcessoDeProtesto.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ProcessoDeProtesto recuperar(Object id) {
        ProcessoDeProtesto p = super.recuperar(id);
        Hibernate.initialize(p.getArquivos());
        Hibernate.initialize(p.getItens());
        Hibernate.initialize(p.getCdas());
        return p;
    }

    public RemessaProtestoFacade getRemessaFacade() {
        return remessaFacade;
    }

    public List<AtoLegal> completarAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrando(parte.trim(), "nome");
    }

    public String getMimeType(InputStream is) {
        return arquivoFacade.getMimeType(is);
    }

    public Arquivo novoArquivoMemoria(Arquivo arquivo, InputStream is) throws Exception {
        return arquivoFacade.novoArquivoMemoria(arquivo, is);
    }

    public CadastroImobiliario recuperarCadastroImobiliario(Long id) {
        return cadastroImobiliarioFacade.recuperar(id);
    }

    public CadastroRural recuperarCadastroRural(Long id) {
        return cadastroRuralFacade.recuperar(id);
    }

    public CadastroEconomico recuperarCadastroEconomico(Long id) {
        return cadastroEconomicoFacade.recuperar(id);
    }

    public SituacaoCadastroEconomico recuperarUltimaSituacaoDoCadastroEconomico(CadastroEconomico cadastroEconomico) {
        return cadastroEconomicoFacade.recuperarUltimaSituacaoDoCadastroEconomico(cadastroEconomico);
    }

    public Testada recuperarTestadaPrincipal(Lote lote) {
        return loteFacade.recuperarTestadaPrincipal(lote);
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public void setDividaFacade(DividaFacade dividaFacade) {
        this.dividaFacade = dividaFacade;
    }

    public List<Divida> listaFiltrandoDividas(String s) {
        return dividaFacade.listaFiltrandoDividas(s, "descricao");
    }

    public void adicionarItem(ResultadoParcela resultadoParcelas, ProcessoDeProtesto processoDeProtesto, List<ItemProcessoDeProtesto> itens, List<ResultadoParcela> resultadoConsulta) {
        ValidacaoException ve = new ValidacaoException();
        if (permitirAdicionarItem(itens, resultadoParcelas)) {
            ItemProcessoDeProtesto itemProcessoDeProtesto = new ItemProcessoDeProtesto();
            itemProcessoDeProtesto.setProcessoDeProtesto(processoDeProtesto);
            itemProcessoDeProtesto.setParcela(recuperarParcelaValorDivida(resultadoParcelas.getIdParcela()));
            itemProcessoDeProtesto.setReferencia(resultadoParcelas.getReferencia());
            itemProcessoDeProtesto.setResultadoParcela(resultadoParcelas);
            itens.add(itemProcessoDeProtesto);
            resultadoConsulta.remove(resultadoParcelas);
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A parcela selecionada: " + resultadoParcelas.getReferencia() + " - " + resultadoParcelas.getDivida() + " - " + resultadoParcelas.getParcela() + " já foi adicionada ao processo!");
            ve.lancarException();
        }
    }

    public void adicionarCda(CertidaoDividaAtiva cda,
                             ProcessoDeProtesto processoDeProtesto,
                             List<CdaProcessoDeProtesto> cdasProcesso,
                             List<ItemProcessoDeProtesto> itens, List<CertidaoDividaAtiva> cdas) {
        ValidacaoException ve = new ValidacaoException();
        if (permitirAdicionarItem(cdasProcesso, cda)) {
            CdaProcessoDeProtesto cdaProcessoDeProtesto = new CdaProcessoDeProtesto();
            cdaProcessoDeProtesto.setProcessoDeProtesto(processoDeProtesto);
            cdaProcessoDeProtesto.setCda(cda);
            if (adicionarParcelasDaCda(cda, processoDeProtesto, itens)) {
                cdasProcesso.add(cdaProcessoDeProtesto);
                cdas.remove(cda);
            } else {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A CDA selecionada: " + cda.getNumeroCdaComExercicio() + " não pode ser adicionada ao processo!");
            }
        } else {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A CDA selecionada: " + cda.getNumeroCdaComExercicio() + " já foi adicionada ao processo!");
        }
        ve.lancarException();
    }

    private boolean adicionarParcelasDaCda(CertidaoDividaAtiva cda, ProcessoDeProtesto processoDeProtesto, List<ItemProcessoDeProtesto> itens) {
        boolean podeAdicionar = false;
        List<ResultadoParcela> parcelas = certidaoDividaAtivaFacade.recuperaParcelasDaCertidaoDaView(cda);
        for (ResultadoParcela parcela : parcelas) {
            if (!contemParcelaAdicionada(parcela, itens)) {
                if (parcela.isEmAberto() && !parcela.getDebitoProtestado()) {
                    ItemProcessoDeProtesto itemProcessoDeProtesto = new ItemProcessoDeProtesto();
                    itemProcessoDeProtesto.setProcessoDeProtesto(processoDeProtesto);
                    itemProcessoDeProtesto.setParcela(recuperarParcelaValorDivida(parcela.getIdParcela()));
                    itemProcessoDeProtesto.setReferencia(parcela.getReferencia());
                    itemProcessoDeProtesto.setResultadoParcela(parcela);
                    itemProcessoDeProtesto.setCda(cda);
                    itens.add(itemProcessoDeProtesto);
                    podeAdicionar = true;
                }
            }
        }
        return podeAdicionar;
    }

    private boolean contemParcelaAdicionada(ResultadoParcela parcela, List<ItemProcessoDeProtesto> itens) {
        boolean contem = false;
        for (ItemProcessoDeProtesto item : itens) {
            if (item.getParcela().getId().equals(parcela.getIdParcela())) {
                contem = true;
                break;
            }
        }
        return contem;
    }

    public void removerParcelasDaCda(CertidaoDividaAtiva cda, ProcessoDeProtesto processoDeProtesto, List<ItemProcessoDeProtesto> itens) {
        List<ResultadoParcela> parcelas = certidaoDividaAtivaFacade.recuperaParcelasDaCertidaoDaView(cda);
        List<ItemProcessoDeProtesto> remover = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelas) {
            for (ItemProcessoDeProtesto item : itens) {
                if (item.getParcela().getId().equals(parcela.getIdParcela())) {
                    remover.add(item);
                }
            }
        }
        for (ItemProcessoDeProtesto itemProcessoDeProtesto : remover) {
            itens.remove(itemProcessoDeProtesto);
        }
    }

    public void deletarItens(List<ItemProcessoDeProtesto> itens) {
        for (ItemProcessoDeProtesto item : itens) {
            if (item.getId() != null) {
                em.remove(em.find(ItemProcessoDeProtesto.class, item.getId()));
            }
        }
    }

    public void salvarItens(List<ItemProcessoDeProtesto> itens) {
        for (ItemProcessoDeProtesto item : itens) {
            em.merge(item);
        }
    }


    private boolean permitirAdicionarItem(List<ItemProcessoDeProtesto> itens, ResultadoParcela resultadoParcelas) {
        boolean valida = true;
        for (ItemProcessoDeProtesto item : itens) {
            if (item.getParcela().getId().equals(resultadoParcelas.getIdParcela())) {
                valida = false;
                break;
            }
        }
        return valida;
    }

    private boolean permitirAdicionarItem(List<CdaProcessoDeProtesto> itens, CertidaoDividaAtiva cda) {
        boolean valida = true;
        for (CdaProcessoDeProtesto item : itens) {
            if (item.getCda().getId().equals(cda.getId())) {
                valida = false;
                break;
            }
        }
        return valida;
    }

    public List<BigDecimal> buscarIdsCadastrosAtivosDaPessoa(Pessoa pessoa, Boolean consultarQuadroSocietarioCmc) {
        return cadastroFacade.buscarIdsCadastrosAtivosDaPessoa(pessoa, consultarQuadroSocietarioCmc);
    }

    public int contarItens(ProcessoDeProtesto entity, List<ItemProcessoDeProtesto> itensNotIn) {
        if (entity.getId() == null) {
            return 0;
        }
        Query q = getQuery("select count(i.id) ", entity, itensNotIn);
        return ((Number) q.getSingleResult()).intValue();
    }

    private Query getQuery(String select, ProcessoDeProtesto entity, List<ItemProcessoDeProtesto> itensNotIn) {
        List<Long> ids = Lists.newArrayList();
        for (ItemProcessoDeProtesto itemProcessoDeProtesto : itensNotIn) {
            ids.add(itemProcessoDeProtesto.getId());
        }
        String sql = select + " from ItemProcessoDeProtesto i where i.processoDeProtesto = :processoDeProtesto ";
        if (!itensNotIn.isEmpty()) {
            sql += " and i.id not in (:itens)";
        }
        Query q = em.createQuery(sql);
        q.setParameter("processoDeProtesto", entity);
        if (!itensNotIn.isEmpty()) {
            q.setParameter("itens", ids);
        }
        return q;
    }

    public List<ItemProcessoDeProtesto> buscarItemProcesso(int primeiroRegistro, int qtdeRegistro,
                                                           ProcessoDeProtesto entity, List<ItemProcessoDeProtesto> itensNotIn) {
        if (entity.getId() == null) {
            return Lists.newArrayList();
        }
        Query q = getQuery("select i ", entity, itensNotIn);
        q.setFirstResult(primeiroRegistro);
        q.setMaxResults(qtdeRegistro);
        List<ItemProcessoDeProtesto> list = q.getResultList();
        for (ItemProcessoDeProtesto itemProcessoDeProtesto : list) {
            ConsultaParcela consulta = new ConsultaParcela();
            List<ResultadoParcela> resultados = consulta
                .addParameter(ConsultaParcela.Campo.PARCELA_ID, ConsultaParcela.Operador.IGUAL, itemProcessoDeProtesto.getParcela().getId())
                .executaConsulta()
                .getResultados();
            itemProcessoDeProtesto.setResultadoParcela(resultados.get(0));
        }
        return list;
    }

    public boolean hasProcessoDeProtestoAtivoParaParcela(Long id) {
        String sql = "select pp.* " +
            "from processodeprotesto pp " +
            "         inner join itemprocessodeprotesto ipp " +
            "         on pp.ID = ipp.processodeprotesto_id " +
            "where ipp.parcela_id = :idParcela" +
            "  and pp.situacao in (:situacoes)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcela", id);
        q.setParameter("situacoes", Lists.newArrayList(SituacaoProcessoDebito.EM_ABERTO.name(), SituacaoProcessoDebito.FINALIZADO.name()));
        return !q.getResultList().isEmpty();
    }

    public UsuarioSistema getUsuarioCorrente() {
        return sistemaFacade.getUsuarioCorrente();
    }

    public Date getDataOperacao() {
        return sistemaFacade.getDataOperacao();
    }

    public Exercicio getExercicioCorrente() {
        return sistemaFacade.getExercicioCorrente();
    }

    public Long recuperarProximoCodigoPorExercicio(Exercicio exercicio) {
        String sql = " select max(coalesce(obj.codigo,0)) from ProcessoDeProtesto obj "
            + " where obj.exercicio = :exercicio";
        Query query = em.createQuery(sql);
        query.setParameter("exercicio", exercicio);
        query.setMaxResults(1);
        try {
            Long resultado = (Long) query.getSingleResult();
            if (resultado == null) {
                resultado = 0l;
            }
            return resultado + 1;
        } catch (Exception e) {
            return 1l;
        }
    }

    private ParcelaValorDivida recuperarParcelaValorDivida(Long id) {
        Query q = em.createQuery(" select pvd from ParcelaValorDivida pvd where id = :id");
        q.setParameter("id", id);
        return (ParcelaValorDivida) q.getResultList().get(0);
    }

    public ProcessoDeProtesto buscarProcessoAtivoDaParcela(Long idParcela) {
        String sql = "select pp.* " +
            " from processodeprotesto pp " +
            "         inner join itemprocessodeprotesto item " +
            "         on pp.ID = item.processodeprotesto_id " +
            " where item.parcela_id = :idParcela " +
            "  and pp.situacao = :situacao ";
        Query q = em.createNativeQuery(sql, ProcessoDeProtesto.class);
        q.setParameter("idParcela", idParcela);
        q.setParameter("situacao", SituacaoProcessoDebito.FINALIZADO.name());
        List<ProcessoDeProtesto> resultado = q.getResultList();
        if (resultado != null && !resultado.isEmpty()) {
            return resultado.get(0);
        }
        return null;
    }

    public void gerarDocumento(ProcessoDeProtesto processo) throws UFMException, AtributosNulosException {
        TipoDoctoOficial tipo = tipoDoctoOficialFacade.buscarTipoDoctoProcessoProtesto(processo);
        processo.setDocumentoOficial(documentoOficialFacade.gerarDocumentoProcessoProtesto(processo, tipo));
        salvar(processo);
    }

    public ProcessoDeProtesto processarProtesto(ProcessoDeProtesto processoDeProtesto) {
        processoDeProtesto.setSituacao(SituacaoProcessoDebito.FINALIZADO);
        return salvarRetornando(processoDeProtesto);
    }

    public ProcessoDeProtesto cancelarProtesto(ProcessoDeProtesto processoDeProtesto, String motivoCancelamento) {
        processoDeProtesto.setSituacao(SituacaoProcessoDebito.CANCELADO);
        processoDeProtesto.setMotivoCancelamento(motivoCancelamento);
        removerProtestoDasParcelas(processoDeProtesto);
        return salvarRetornando(processoDeProtesto);
    }

    @Asynchronous
    public void onNovoParcelamento(@Observes NovoParcelamentoEvento novoParcelamentoEvento) {
        List<ProcessoDeProtesto> processosDeProtesto = buscarProcessosDeProtestoPorParcelamento(novoParcelamentoEvento.getProcessoParcelamento());
        for (ProcessoDeProtesto processoDeProtesto : processosDeProtesto) {
            cancelarProtesto(processoDeProtesto, "Cancelamento automático pela criação do parcelamento: " +
                novoParcelamentoEvento.getProcessoParcelamento().toString());
        }
    }

    private List<ProcessoDeProtesto> buscarProcessosDeProtestoPorParcelamento(ProcessoParcelamento processoParcelamento) {
        return em.createNativeQuery(" select pp.* " +
                "    from processodeprotesto pp " +
                "where exists (select 1  " +
                "                from itemprocessodeprotesto ipt " +
                "               inner join itemprocessoparcelamento ipp on ipp.parcelavalordivida_id = ipt.parcela_id  " +
                "              where ipt.processodeprotesto_id = pp.id " +
                "                and ipp.processoparcelamento_id = :parcelamentoId) ", ProcessoDeProtesto.class)
            .setParameter("parcelamentoId", processoParcelamento.getId())
            .getResultList();
    }

    public void removerProtestoDasParcelas(ProcessoDeProtesto processoDeProtesto) {
        for (ItemProcessoDeProtesto item : processoDeProtesto.getItens()) {
            ParcelaValorDivida pvd = em.find(ParcelaValorDivida.class, item.getParcela().getId());
            pvd.setDebitoProtestado(Boolean.FALSE);
            pvd.setDataProtesto(null);
            em.merge(pvd);
        }
    }

    public String buscarNumeroCDA(Long idParcela) {
        CertidaoDividaAtiva cda = remessaFacade.buscarCDAParcela(idParcela);
        return cda != null ? cda.getNumeroCdaComExercicio() : "";
    }

    public List<ObjetoCampoValor> atribuirResultadoDetalhamento(ResultadoParcela resultadoParcela) {
        return remessaFacade.getConsultaDebitoFacade().carregarListaObjetoCampoValorParcelaSelecionada(resultadoParcela);
    }

    public Long buscarIdParcelaOriginalDaParcelaDoCancelamento(Long idParcela) {
        return remessaFacade.buscarIdParcelaOriginalDaParcelaDoCancelamento(idParcela);
    }

    public List<CertidaoDividaAtiva> buscarCertidoesDividaAtivaDaParcela(Long idParcela) {
        return remessaFacade.buscarCertidoesDividaAtivaDaParcela(idParcela);
    }

    public List<CertidaoDividaAtiva> buscarCdasPorFiltros(Cadastro cadastro, Pessoa pessoa, Exercicio exercicioInicial, Exercicio exercicioFinal, String numeroCdaInicial, String numeroCdaFinal) {
        String sql = "select cda.* from CertidaoDividaAtiva cda " +
            " inner join exercicio ex on ex.id = cda.exercicio_id " +
            " where cda.situacaoCertidaoDA = :situacaoCda " +
            (cadastro != null ? " and cda.cadastro_id = :idCadastro" : pessoa != null ? "and cda.pessoa_id = :idPessoa" : "");
        if (exercicioInicial != null) {
            sql += " and ex.ano >= :anoInicial ";
        }
        if (exercicioFinal != null) {
            sql += " and ex.ano <= :anoFinal ";
        }
        if (numeroCdaInicial != null && !numeroCdaInicial.trim().isEmpty()) {
            sql += " and cda.numero >= :numeroInicial ";
        }
        if (numeroCdaFinal != null && !numeroCdaFinal.trim().isEmpty()) {
            sql += " and cda.numero <= :numeroFinal ";
        }
        sql += " order by ex.ano desc, cda.numero";
        logger.error("SQL: " + sql);
        Query q = em.createNativeQuery(sql, CertidaoDividaAtiva.class);
        q.setParameter("situacaoCda", SituacaoCertidaoDA.ABERTA.name());
        if (cadastro != null) {
            q.setParameter("idCadastro", cadastro.getId());
        } else if (pessoa != null) {
            q.setParameter("idPessoa", pessoa.getId());
        }
        if (exercicioInicial != null) {
            q.setParameter("anoInicial", exercicioInicial.getAno());
        }
        if (exercicioFinal != null) {
            q.setParameter("anoFinal", exercicioFinal.getAno());
        }
        if (numeroCdaInicial != null && !numeroCdaInicial.trim().isEmpty()) {
            q.setParameter("numeroInicial", numeroCdaInicial);
        }
        if (numeroCdaFinal != null && !numeroCdaFinal.trim().isEmpty()) {
            q.setParameter("numeroFinal", numeroCdaFinal);
        }
        return q.getResultList();
    }

    public List<ResultadoParcela> buscarParcelasDaCda(CertidaoDividaAtiva cda) {
        return certidaoDividaAtivaFacade.recuperaParcelasDaCertidaoDaView(cda);
    }
}
