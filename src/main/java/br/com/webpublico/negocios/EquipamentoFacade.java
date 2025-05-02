package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.frotas.ParametroFrotas;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.administrativo.frotas.ParametroFrotasFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 08/09/14
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class EquipamentoFacade extends AbstractFacade<Equipamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private BaixaObjetoFrotaFacade baixaObjetoFrotaFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private ParametroFrotasFacade parametroFrotasFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private ProgramaRevisaoEquipamentoFacade programaRevisaoEquipamentoFacade;

    public EquipamentoFacade() {
        super(Equipamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Equipamento> buscarEquipamentosPorAnoOrDescricaoOrIdentificacaoOrNumeroProcessoContratoPorUnidadeQuePessoaTemPermissao(String parte, UsuarioSistema usuario) {
        List<HierarquiaOrganizacional> unidadesAdministrativasDoUsuario = hierarquiaOrganizacionalFacade.buscarHierarquiaOrganizacionalAdministrativaDoUsuario("",null,sistemaFacade.getDataOperacao(),usuario,null, null);
        List<String>codigoUnidadeAdministrativaDoUsuario = new ArrayList<>();

        for(HierarquiaOrganizacional unidadeAdministrativasDoUsuario : unidadesAdministrativasDoUsuario ){
            codigoUnidadeAdministrativaDoUsuario.add(unidadeAdministrativasDoUsuario.getCodigo().substring(0,5));
        }

        if (unidadesAdministrativasDoUsuario != null) {
            String sql = " select obj.*, e.* " +
                "    from equipamento e " +
                "   inner join objetofrota obj on e.id = obj.id " +
                "   inner join unidadeobjetofrota ubf on ubf.objetofrota_id = obj.id " +
                "   inner join vwhierarquiaadministrativa vw on vw.subordinada_id = ubf.unidadeorganizacional_id " +
                "   left join bem b on obj.bem_id = b.id " +
                "   left join contrato c on obj.contrato_id = c.id " +
                " where not exists (select 1 from baixaObjetoFrota baixa where baixa.objetofrota_id = e.id) " +
                "   and obj.dataaquisicao <= current_date" +
                "   and (c.id is null or (c.terminoVigencia >= current_date) ) " +
                "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vw.inicioVigencia) and coalesce(trunc(vw.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ubf.inicioVigencia) and coalesce(trunc(ubf.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "   and (to_char(e.ano) like :filtro " +
                "        or lower(b.descricao) like :filtro " +
                "        or lower(b.identificacao) like :filtro " +
                "        or lower(obj.descricao) like :filtro " +
                "        or lower(obj.identificacao) like :filtro " +
                "        or to_char(c.numeroProcesso) like :filtro) " +
                "   and substr(vw.codigo, 1, 5) in :codigoUnidadeAdministrativaDoUsuario ";
            Query q = em.createNativeQuery(sql, Equipamento.class);
            q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
            q.setParameter("codigoUnidadeAdministrativaDoUsuario", codigoUnidadeAdministrativaDoUsuario.stream().distinct().collect(Collectors.toList()));
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));

            return q.getResultList();
        } else {
            throw new ExcecaoNegocioGenerica("Hierarquia da unidade " + sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente() + " não encontrada.");
        }
    }


    public List<Equipamento> buscarEquipamentosPorAnoOrDescricaoOrIdentificacaoOrNumeroProcessoContrato(String parte) {
        HierarquiaOrganizacional hierarquiaOrganizacionalCorrente =
            hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(),
                sistemaFacade.getDataOperacao());

        if (hierarquiaOrganizacionalCorrente != null) {
            String sql = " select obj.*, e.* " +
                "    from equipamento e " +
                "   inner join objetofrota obj on e.id = obj.id " +
                "   inner join unidadeobjetofrota ubf on ubf.objetofrota_id = obj.id " +
                "   inner join vwhierarquiaadministrativa vw on vw.subordinada_id = ubf.unidadeorganizacional_id " +
                "   left join bem b on obj.bem_id = b.id " +
                "   left join contrato c on obj.contrato_id = c.id " +
                " where not exists (select 1 from baixaObjetoFrota baixa where baixa.objetofrota_id = e.id) " +
                "   and obj.dataaquisicao <= current_date" +
                "   and (c.id is null or (c.terminoVigencia >= current_date) ) " +
                "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vw.inicioVigencia) and coalesce(trunc(vw.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ubf.inicioVigencia) and coalesce(trunc(ubf.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "   and (to_char(e.ano) like :filtro " +
                "        or lower(b.descricao) like :filtro " +
                "        or lower(b.identificacao) like :filtro " +
                "        or lower(obj.descricao) like :filtro " +
                "        or lower(obj.identificacao) like :filtro " +
                "        or to_char(c.numeroProcesso) like :filtro) " +
                "   and substr(vw.codigo, 1, 5) = :secretaria ";
            Query q = em.createNativeQuery(sql, Equipamento.class);
            q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
            q.setParameter("secretaria", hierarquiaOrganizacionalCorrente.getCodigo().substring(0, 5));
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            return q.getResultList();
        } else {
            throw new ExcecaoNegocioGenerica("Hierarquia da unidade " + sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente() + " não encontrada.");
        }
    }

    public List<Equipamento> buscarEquipamentosVigentes() {
        String sql = " select obj.*, e.* " +
            "    from equipamento e " +
            "   inner join objetofrota obj on e.id = obj.id " +
            "   left join bem b on obj.bem_id = b.id " +
            "   left join contrato c on obj.contrato_id = c.id " +
            " where not exists (select 1 from baixaObjetoFrota baixa where baixa.objetofrota_id = e.id) " +
            "   and obj.dataaquisicao <= current_date " +
            "   and (c.id is null or (c.terminoVigencia >= current_date) ) ";
        Query q = em.createNativeQuery(sql, Equipamento.class);
        return q.getResultList();
    }

    public boolean identificacaoRegistrada(Equipamento equipamento) {
        if (equipamento.getUnidadeOrganizacional() == null) {
            return false;
        }
        String hql = " select equip from Equipamento equip " +
            "           inner join equip.unidades unid " +
            "          where unid.unidadeOrganizacional = :unidade " +
            "          and equip.identificacao = :identificacao ";
        if (equipamento.getId() != null) {
            hql += " and equip.id <> :id ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("unidade", equipamento.getUnidadeOrganizacional());
        q.setParameter("identificacao", equipamento.getIdentificacao());
        if (equipamento.getId() != null) {
            q.setParameter("id", equipamento.getId());
        }
        return q.getResultList() != null && q.getResultList().size() > 0;
    }

    public Equipamento recuperarEquipamentoAndRevisoes(Object id) {
        Equipamento equipamento = super.recuperar(id);
        equipamento.getRevisoesEquipamento().size();
        equipamento.getItemHoraPercorrida().size();
        return equipamento;
    }


    public void criarLancamentoHoraPercorrido(Equipamento equipamento, BigDecimal horaAtual) {
        em.persist(new HoraPercorridaEquipamento(equipamento, horaAtual));
    }

    @Override
    public void salvarNovo(Equipamento entity) {
        entity = em.merge(entity);
        criarLancamentoHoraPercorrido(entity, entity.getHorasUso());
    }

    @Override
    public Equipamento recuperar(Object id) {
        Equipamento equipamento = super.recuperar(id);
        if (equipamento.getContrato() != null && equipamento.getContrato().getId() != null) {
            equipamento.setContrato(contratoFacade.recuperar(equipamento.getContrato().getId()));
        }
        if (equipamento.getDetentorArquivoComposicao() != null) {
            equipamento.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        equipamento.getRevisoesEquipamento().size();
        equipamento.getItemHoraPercorrida().size();
        equipamento.setBaixaObjetoFrota(baixaObjetoFrotaFacade.recuperarBaixaDoObjetoFrota(equipamento));
        equipamento.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            equipamento.getUnidadeOrganizacional(),
            sistemaFacade.getDataOperacao()));

        if (equipamento.getUnidadeOrganizacionalResp() != null) {
            equipamento.setHierarquiaOrganizacionalResponsavel(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                equipamento.getUnidadeOrganizacionalResp(),
                sistemaFacade.getDataOperacao()));
        }
        equipamento.getUnidades().size();
        for (UnidadeObjetoFrota unidadeObjetoFrota : equipamento.getUnidades()) {
            unidadeObjetoFrota.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                unidadeObjetoFrota.getUnidadeOrganizacional(),
                sistemaFacade.getDataOperacao()));
        }
        return equipamento;
    }

    public List<Equipamento> buscarEquipamentosComRevisoesAVencer() {
        List<Equipamento> toReturn = Lists.newArrayList();
        ParametroFrotas parametroFrotas = parametroFrotasFacade.buscarParametroFrotas();
        if (parametroFrotas != null) {
            String sql = "" +
                "select  " +
                "  obj.*, " +
                "  equip.* " +
                "from equipamento equip  " +
                "  inner join objetofrota obj on equip.id = obj.id " +
                "  inner join horapercorridaequipamento horaPerc on horaperc.id = equip.horapercorrida_id " +
                "  left join bem b on obj.bem_id = b.id " +
                "  left join contrato c on obj.contrato_id = c.id " +
                "where not exists (select 1 from baixaobjetofrota baixa where baixa.objetofrota_id = equip.id) " +
                "  and obj.dataaquisicao <= sysdate " +
                "  and (c.id is null or (c.terminoVigencia >= sysdate)) " +
                "  and exists " +
                "  (select 1 " +
                "  from programarevisaoequipamento revisao " +
                "  where revisao.equipamento_id = equip.id " +
                "  and revisao.revisaorealizada = 0 " +
                "  and ( ((revisao.prazoemmeses * 30 >= (sysdate - obj.dataaquisicao)) " +
                "  and (revisao.prazoemmeses * 30 - (sysdate - obj.dataaquisicao) <= :dias_a_vencer)) " +
                "      or (((equip.horasusoaquisicao + revisao.prazoemsegundos) >= horaperc.horaatual) " +
                "  and ((equip.horasusoaquisicao + revisao.prazoemsegundos - horaperc.horaatual) <= :horas_a_vencer))) " +
                "  ) ";
            Query q = em.createNativeQuery(sql, Equipamento.class);
            q.setParameter("dias_a_vencer", parametroFrotas.getDiasDaRevisaoAVencer());
            q.setParameter("horas_a_vencer", parametroFrotas.getSegundosRevisaoAVencer());
            return q.getResultList();
        }
        return toReturn;
    }

    private void criarNotificacaoEquipamentoComRevisaoVencendo(Equipamento equipamento) {
        String mensagem = "O equipamento " + equipamento + " possui revisão(ões) próxima(s) do vencimento.";
        String link = "/frota/equipamento/editar/" + equipamento.getId();
        NotificacaoService.getService().notificar(TipoNotificacao.AVISO_EQUIPAMENTOS_COM_REVISAO_VENCENDO.getDescricao(),
            mensagem, link, Notificacao.Gravidade.ATENCAO, TipoNotificacao.AVISO_EQUIPAMENTOS_COM_REVISAO_VENCENDO);
    }

    public void lancarNotificacoesDeEquipamentosComRevisaoAVencer() {
        List<Equipamento> equipamentosANotificar = buscarEquipamentosComRevisoesAVencer();
        NotificacaoService.getService().marcarComoLida(TipoNotificacao.AVISO_EQUIPAMENTOS_COM_REVISAO_VENCENDO);
        for (Equipamento equipamento : equipamentosANotificar) {
            criarNotificacaoEquipamentoComRevisaoVencendo(equipamento);
        }
    }

    public HierarquiaOrganizacional buscarUnidadeVigenteEquipamento(Equipamento equipamento) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ho.*  ")
            .append(" from equipamento eq ")
            .append("   inner join objetofrota obj on eq.id = obj.id  ")
            .append("   inner join unidadeobjetofrota ubf on ubf.objetofrota_id = obj.id  ")
            .append("   inner join hierarquiaorganizacional ho on ho.subordinada_id = ubf.unidadeorganizacional_id  ")
            .append(" where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("   and ho.tipohierarquiaorganizacional = :tipoHierarquia ")
            .append("   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ubf.inicioVigencia) and coalesce(trunc(ubf.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append("   and eq.id = :idEquipamento ");
        Query q = em.createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("idEquipamento", equipamento.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        try {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Nenhum unidade vigente encontrato para o equipamento: " + equipamento + ".");
        } catch (NonUniqueResultException ne) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de uma  unidade vigente para o equipamento: " + equipamento + ".");
        }
    }

    public ParametroFrotasFacade getParametroFrotasFacade() {
        return parametroFrotasFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ProgramaRevisaoEquipamentoFacade getProgramaRevisaoEquipamentoFacade() {
        return programaRevisaoEquipamentoFacade;
    }
}
