/*
 * Codigo gerado automaticamente em Sat Nov 26 15:13:39 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotas;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.enums.administrativo.frotas.SituacaoSolicitacaoObjetoFrota;
import br.com.webpublico.negocios.administrativo.frotas.ParametroFrotasFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Stateless
public class ReservaObjetoFrotaFacade extends AbstractFacade<ReservaObjetoFrota> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroFrotasFacade parametroFrotasFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private DiarioTrafegoFacade diarioTrafegoFacade;

    public ReservaObjetoFrotaFacade() {
        super(ReservaObjetoFrota.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public boolean hasReservaParaObjetoFrota(ReservaObjetoFrota reservaObjetoFrota) {
        String sql = "select reserva.* " +
            "   from reservaobjetofrota reserva " +
            "  inner join solicitacaoobjetofrota solicitacao on reserva.solicitacaoobjetofrota_id = solicitacao.id " +
            " where reserva.objetofrota_id = :objeto_id " +
            "  and solicitacao.situacao = :aprovado " +
            "  and ((to_date(to_char(solicitacao.dataretirada, 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi') <= to_date(to_char(:data_retirada, 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi') " +
            "        and to_date(to_char(coalesce(solicitacao.datadevolucao, current_timestamp), 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi') >= to_date(to_char(:data_retirada, 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi')) or " +
            "       (to_date(to_char(solicitacao.dataretirada, 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi') <= to_date(to_char(:data_devolucao, 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi') " +
            "        and to_date(to_char(coalesce(solicitacao.datadevolucao, current_timestamp), 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi') >= to_date(to_char(:data_devolucao, 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi')) or " +
            "       (to_date(to_char(solicitacao.dataretirada, 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi') > to_date(to_char(:data_retirada, 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi') " +
            "        and to_date(to_char(coalesce(solicitacao.datadevolucao, current_timestamp), 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi') < to_date(to_char(:data_devolucao, 'dd/MM/yyyy HH24:mi'), 'dd/MM/yyyy HH24:mi'))) ";
        Query q = em.createNativeQuery(sql, ReservaObjetoFrota.class);
        q.setParameter("objeto_id", reservaObjetoFrota.getObjetoFrota().getId());
        q.setParameter("aprovado", SituacaoSolicitacaoObjetoFrota.APROVADO.name());
        q.setParameter("data_retirada", reservaObjetoFrota.getSolicitacaoObjetoFrota().getDataRetirada());
        if (reservaObjetoFrota.getSolicitacaoObjetoFrota().getDataDevolucao() != null) {
            q.setParameter("data_devolucao", reservaObjetoFrota.getSolicitacaoObjetoFrota().getDataDevolucao());
        } else {
            q.setParameter("data_devolucao", reservaObjetoFrota.getRealizadaEm());
        }
        return !q.getResultList().isEmpty();
    }

    public ReservaObjetoFrota salvarReservaAprovada(ReservaObjetoFrota reservaObjetoFrota, ItemDiarioTrafego itemDiarioTrafego) {
        itemDiarioTrafego.getReservaObjetoFrota().setSolicitacaoObjetoFrota(em.merge(reservaObjetoFrota.getSolicitacaoObjetoFrota()));
        itemDiarioTrafego.setDiarioTrafego(em.merge(itemDiarioTrafego.getDiarioTrafego()));
        itemDiarioTrafego.setReservaObjetoFrota(em.merge(itemDiarioTrafego.getReservaObjetoFrota()));
        em.merge(itemDiarioTrafego);
        criarNotificacaoAvaliacaoSolicitacaoReserva(itemDiarioTrafego.getReservaObjetoFrota());
        return itemDiarioTrafego.getReservaObjetoFrota();
    }

    public ReservaObjetoFrota salvarReservaRejeitada(ReservaObjetoFrota entity) {
        entity.setSolicitacaoObjetoFrota(em.merge(entity.getSolicitacaoObjetoFrota()));
        entity = em.merge(entity);
        criarNotificacaoAvaliacaoSolicitacaoReserva(entity);
        return entity;
    }

    @Override
    public void remover(ReservaObjetoFrota entity) {
        entity.getSolicitacaoObjetoFrota().setMotivo("");
        entity.getSolicitacaoObjetoFrota().setSituacao(SituacaoSolicitacaoObjetoFrota.AGUARDANDO_RESERVA);
        em.merge(entity.getSolicitacaoObjetoFrota());
        ItemDiarioTrafego item = recuperarItemDiarioTrafego(entity.getId());
        DiarioTrafego diarioTrafego = (DiarioTrafego) diarioTrafegoFacade.recuperar(DiarioTrafego.class, item.getDiarioTrafego().getId());
        removerItemDiarioTrafero(item);
        if (diarioTrafego.getItensDiarioTrafego().size() == 1) {
            removerDiarioTrafero(diarioTrafego);
        }
        super.remover(entity);
    }


    public void removerDiarioTrafero(DiarioTrafego entity) {
        Query q = em.createNativeQuery(" delete diariotrafego dt where dt.id = :id");
        q.setParameter("id", entity.getId());
        q.executeUpdate();
    }

    private void removerItemDiarioTrafero(ItemDiarioTrafego entity) {
        Query q = em.createNativeQuery(" delete itemdiariotrafego i where i.id = :id");
        q.setParameter("id", entity.getId());
        q.executeUpdate();
    }

    private ItemDiarioTrafego recuperarItemDiarioTrafego(Long id) {
        List<ItemDiarioTrafego> retorno = Lists.newArrayList();
        String sql = "select * from itemdiariotrafego i  " +
            "    inner join diariotrafego dt  on dt.id = i.diariotrafego_id  " +
            "where i.reservaobjetofrota_id = :idReserva";
        Query q = em.createNativeQuery(sql, ItemDiarioTrafego.class);
        q.setParameter("idReserva", id);
        retorno.addAll(q.getResultList());
        return retorno.get(0);
    }

    private void criarNotificacaoAvaliacaoSolicitacaoReserva(ReservaObjetoFrota entity) {
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao("Existe uma nova avaliação de sua solicitação de reserva: Solicitante: " + entity.getSolicitacaoObjetoFrota().getPessoaFisica().getNome() + ".");
        notificacao.setGravidade(Notificacao.Gravidade.INFORMACAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_NOVA_AVALIACAO_SOLICITACAO_VEICULO_EQUIPAMENTO.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_NOVA_AVALIACAO_SOLICITACAO_VEICULO_EQUIPAMENTO);
        notificacao.setUsuarioSistema(entity.getSolicitacaoObjetoFrota().getUsuarioCadastro());
        notificacao.setUnidadeOrganizacional(entity.getSolicitacaoObjetoFrota().getUnidadeSolicitante());
        notificacao.setLink("/frota/solicitacao/ver/" + entity.getSolicitacaoObjetoFrota().getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    private void criarNotificacaoReservaObjetoFrotaComReservaVencendoParaUnidade(ReservaObjetoFrota entity) {
        String mensagem = "Existe uma reserva com data de retirada próxima de ocorrer: reserva " + entity;
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(mensagem);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_RESERVA_VEICULO_EQUIPAMENTO_PROXIMA_PARA_GESTOR_FROTAS.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_RESERVA_VEICULO_EQUIPAMENTO_PROXIMA_PARA_GESTOR_FROTAS);
        notificacao.setUnidadeOrganizacional(entity.getSolicitacaoObjetoFrota().getUnidadeOrganizacional());
        notificacao.setLink("/frota/reserva/ver/" + entity.getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    private void criarNotificacaoSolicitacaoObjetoFrotaProximaAOcorrerParaUsuario(ReservaObjetoFrota entity) {
        String mensagem = "Existe uma solicitação de reserva aprovada com data de retirada próxima a ocorrer " + entity.getSolicitacaoObjetoFrota();
        Notificacao notificacao = new Notificacao();
        notificacao.setDescricao(mensagem);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTitulo(TipoNotificacao.AVISO_RESERVA_VEICULO_EQUIPAMENTO_PROXIMA_PARA_SOLICITANTE.getDescricao());
        notificacao.setTipoNotificacao(TipoNotificacao.AVISO_RESERVA_VEICULO_EQUIPAMENTO_PROXIMA_PARA_SOLICITANTE);
        notificacao.setUsuarioSistema(entity.getSolicitacaoObjetoFrota().getUsuarioCadastro());
        notificacao.setUnidadeOrganizacional(entity.getSolicitacaoObjetoFrota().getUnidadeSolicitante());
        notificacao.setLink("/frota/solicitacao/ver/" + entity.getSolicitacaoObjetoFrota().getId() + "/");
        NotificacaoService.getService().notificar(notificacao);
    }

    public void lancarNotificacoesDeReservasFuturas() {
        List<ReservaObjetoFrota> reservasANotificar = buscarReservaObjetoFrotaComRevisoesAVencer();
        NotificacaoService.getService().marcarComoLida(TipoNotificacao.AVISO_RESERVA_VEICULO_EQUIPAMENTO_PROXIMA_PARA_GESTOR_FROTAS);
        NotificacaoService.getService().marcarComoLida(TipoNotificacao.AVISO_RESERVA_VEICULO_EQUIPAMENTO_PROXIMA_PARA_SOLICITANTE);
        for (ReservaObjetoFrota reservaObjetoFrota : reservasANotificar) {
            criarNotificacaoReservaObjetoFrotaComReservaVencendoParaUnidade(reservaObjetoFrota);
            criarNotificacaoSolicitacaoObjetoFrotaProximaAOcorrerParaUsuario(reservaObjetoFrota);
        }
    }

    private List<ReservaObjetoFrota> buscarReservaObjetoFrotaComRevisoesAVencer() {
        List<ReservaObjetoFrota> retorno = Lists.newArrayList();
        ParametroFrotas parametroFrotas = parametroFrotasFacade.buscarParametroFrotas();
        if (parametroFrotas != null) {
            String sql = " select re.* from reservaobjetofrota re " +
                " inner join solicitacaoobjetofrota sol on re.solicitacaoobjetofrota_id = sol.id " +
                " where (trunc(sol.dataretirada) - trunc(to_date(:dataOperacao, 'dd/mm/yyyy'))) <= :diasARetirar " +
                " and (trunc(sol.dataretirada) - trunc(to_date(:dataOperacao, 'dd/mm/yyyy'))) >= 0 " +
                " and sol.situacao = :situacao ";
            Query q = em.createNativeQuery(sql, ReservaObjetoFrota.class);
            q.setParameter("diasARetirar", parametroFrotas.getDiasRetiradaVeiculoEquipamento());
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(new Date()));
            q.setParameter("situacao", SituacaoSolicitacaoObjetoFrota.APROVADO.name());
            retorno.addAll(q.getResultList());
        }
        return retorno;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public List<ReservaObjetoFrota> buscarReservaPorUnidadeAdministrativa(String parte, UnidadeOrganizacional unidadeAdm) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select r.* from reservaobjetofrota r ")
            .append("   inner join solicitacaoobjetofrota sol on sol.id = r.solicitacaoobjetofrota_id ")
            .append("   inner join pessoafisica pf on pf.id = sol.pessoafisica_id ")
            .append(" where not exists (select 1 from cancelamentoobjetofrota cancelamento where cancelamento.reservaobjetofrota_id = r.id) ")
            .append("   and (r.id like :filtro or lower(pf.nome) like :filtro or pf.cpf like :filtro ) ")
            .append("   and sol.situacao = :situacaoAprovada ")
            .append("   and sol.unidadeorganizacional_id = :idUnidade ");
        Query q = em.createNativeQuery(sql.toString(), ReservaObjetoFrota.class);
        q.setParameter("idUnidade", unidadeAdm.getId());
        q.setParameter("situacaoAprovada", SituacaoSolicitacaoObjetoFrota.APROVADO.name());
        q.setParameter("filtro", "%" + parte.trim() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public DiarioTrafego buscarDiarioTrafego(Long id, Integer mes, Integer ano) {
        String sql = "select dt.* from diariotrafego dt " +
            "    inner join veiculo v on dt.veiculo_id = v.id " +
            "where v.id = :id and dt.ano = :ano and dt.mes = :mes";
        Query q = em.createNativeQuery(sql, DiarioTrafego.class);
        q.setParameter("mes", mes);
        q.setParameter("ano", ano);
        q.setParameter("id", id);
        List<DiarioTrafego> diarioTrafego = q.getResultList();
        if (diarioTrafego.isEmpty()) {
            return new DiarioTrafego();
        }
        return diarioTrafego.get(0);
    }

    public void salvarSolicitacao(SolicitacaoObjetoFrota solicitacaoObjetoFrota) {
        em.merge(solicitacaoObjetoFrota);
    }

    public String montaItem(ItemDiarioTrafego itemDiarioTrafego) {
        if (Util.isNotNull(itemDiarioTrafego)) {
            return "data de saida: " + (Util.isNull(itemDiarioTrafego.getDataLancamento()) ? "não informado" : DataUtil.getDataFormatada(itemDiarioTrafego.getDataLancamento())) +
                ", horaSaida: " + (Util.isNull(itemDiarioTrafego.getHoraSaida()) ? "não informado" : itemDiarioTrafego.getHoraSaida().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().toLocalTime()) +
                ", dataChegada: " + (Util.isNull(itemDiarioTrafego.getDataChegada()) ? "não informado" : DataUtil.getDataFormatada(itemDiarioTrafego.getDataChegada())) +
                ", horaChegada: " + (Util.isNull(itemDiarioTrafego.getHoraChegada()) ? "não informado" : itemDiarioTrafego.getHoraChegada().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime().toLocalTime());
        }
        return "";
    }

    public boolean validaSolicitacao(ReservaObjetoFrota selecionado) {

        if (Util.isNull(selecionado.getRealizadaEm())) {
            FacesUtil.addOperacaoNaoPermitida("O campo Reservado em deve ser informado!");
        }
        if (Util.isNull(selecionado.getSolicitacaoObjetoFrota().getPessoaFisica())) {
            FacesUtil.addOperacaoNaoPermitida("O campo Solicitante deve ser informado!");
        }
        if (Util.isNull(selecionado.getSolicitacaoObjetoFrota().getDataRetirada())) {
            FacesUtil.addOperacaoNaoPermitida("O campo data da retirada deve ser informado!");
        }
        if (Util.isNull(selecionado.getSolicitacaoObjetoFrota().getDataDevolucao())) {
            FacesUtil.addOperacaoNaoPermitida("O campo data da Devolução deve ser informado!");
        }
        return Util.isNotNull(selecionado.getRealizadaEm())
            && Util.isNotNull(selecionado.getSolicitacaoObjetoFrota().getPessoaFisica())
            && Util.isNotNull(selecionado.getSolicitacaoObjetoFrota().getDataDevolucao())
            && Util.isNotNull(selecionado.getSolicitacaoObjetoFrota().getDataRetirada());

    }

    public ItemDiarioTrafego inicializarItem(ReservaObjetoFrota selecionado) {
        ItemDiarioTrafego itemDiarioTrafego = new ItemDiarioTrafego();
        itemDiarioTrafego.setResponsavel(pessoaFacade.recuperar(selecionado.getSolicitacaoObjetoFrota().getPessoaFisica().getId()));
        itemDiarioTrafego.setDataLancamento(selecionado.getSolicitacaoObjetoFrota().getDataRetirada());
        itemDiarioTrafego.setDataChegada(selecionado.getSolicitacaoObjetoFrota().getDataDevolucao());
        itemDiarioTrafego.setReservaObjetoFrota(selecionado);
        itemDiarioTrafego.setHouveRetorno(true);
        return itemDiarioTrafego;
    }
}
