package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.ResumoItemIRPParticipanteVO;
import br.com.webpublico.enums.SituacaoIRP;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by Desenvolvimento on 12/01/2016.
 */

@Stateless
public class IntencaoRegistroPrecoFacade extends AbstractFacade<IntencaoRegistroPreco> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParticipanteIRPFacade participanteIRPFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private FormularioCotacaoFacade formularioCotacaoFacade;

    public IntencaoRegistroPrecoFacade() {
        super(IntencaoRegistroPreco.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public IntencaoRegistroPreco recuperar(Object id) {
        IntencaoRegistroPreco entity = super.recuperar(id);
        Hibernate.initialize(entity.getParticipantes());
        Hibernate.initialize(entity.getLotes());
        Hibernate.initialize(entity.getUnidades());
        for (LoteIntencaoRegistroPreco lote : entity.getLotes()) {
            Hibernate.initialize(lote.getItens());
        }
        for (ParticipanteIRP participante : entity.getParticipantes()) {
            participanteIRPFacade.atribuirUnidadeVigenteParticipante(participante);
            Hibernate.initialize(participante.getItens());
        }
        atribuirUnidadeVigenteIRP(entity);
        return entity;
    }

    public IntencaoRegistroPreco recuperarComDependenciasItens(Object id) {
        IntencaoRegistroPreco entity = super.recuperar(id);
        Hibernate.initialize(entity.getLotes());
        for (LoteIntencaoRegistroPreco lote : entity.getLotes()) {
            Hibernate.initialize(lote.getItens());
        }
        return entity;
    }

    private void atribuirUnidadeVigenteIRP(IntencaoRegistroPreco entity) {
        HierarquiaOrganizacional hoGerenciadora = buscarUnidadeGerenciadoraVigente(entity);
        if (hoGerenciadora != null) {
            entity.setUnidadeGerenciadora(hoGerenciadora);
        }
    }

    public IntencaoRegistroPreco salvarIrp(IntencaoRegistroPreco entity) {
        inseirUnidadeIRP(entity);
        inserirParticipanteIrpGerenciador(entity);
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(IntencaoRegistroPreco.class, "numero"));
        }
        entity = em.merge(entity);
        gerarNotitificacao(entity);
        return entity;
    }

    public void inserirParticipanteIrpGerenciador(IntencaoRegistroPreco entity) {
        ParticipanteIRP partIrp = entity.getParticipanteGerenciador();
        if (partIrp == null) {
            partIrp = new ParticipanteIRP();
            partIrp.setIntencaoRegistroDePreco(entity);
            partIrp.setNumero(singletonGeradorCodigo.getProximoCodigo(ParticipanteIRP.class, "numero"));
            partIrp.setGerenciador(true);
            partIrp.setDataInteresse(entity.getDataLancamento());
            partIrp.setHierarquiaOrganizacional(entity.getUnidadeGerenciadora());
            partIrp.setInteresse(entity.getDescricao());
            partIrp.setSituacao(SituacaoIRP.APROVADA);
            partIrp.setParticipante(entity.getResponsavel());
            participanteIRPFacade.inserirUnidadeParticipante(partIrp);
        }

        partIrp.setItens(Lists.newArrayList());
        for (LoteIntencaoRegistroPreco lote : entity.getLotes()) {
            for (ItemIntencaoRegistroPreco itemIrp : lote.getItens()) {
                ItemParticipanteIRP itemPart = new ItemParticipanteIRP();
                itemPart.setParticipanteIRP(partIrp);
                itemPart.setItemIntencaoRegistroPreco(itemIrp);

                BigDecimal quantidadeOrValorParticipanteNaoGerenciador = participanteIRPFacade.getQuantidadeOrValorParticipanteNaoGerenciador(itemIrp);

                BigDecimal valorAtualIrp = itemIrp.getValorColuna();
                if (quantidadeOrValorParticipanteNaoGerenciador.compareTo(BigDecimal.ZERO) > 0) {
                    valorAtualIrp = valorAtualIrp.subtract(quantidadeOrValorParticipanteNaoGerenciador);
                }
                if (itemIrp.isTipoControlePorValor()) {
                    itemPart.setQuantidade(BigDecimal.ZERO);
                    itemPart.setValor(valorAtualIrp);
                } else {
                    itemPart.setQuantidade(valorAtualIrp);
                }
                Util.adicionarObjetoEmLista(partIrp.getItens(), itemPart);
            }
        }
        Util.adicionarObjetoEmLista(entity.getParticipantes(), partIrp);
    }

    private void inseirUnidadeIRP(IntencaoRegistroPreco entity) {
        entity.setUnidades(Lists.<UnidadeIRP>newArrayList());
        UnidadeIRP unidadeIRP = new UnidadeIRP();
        unidadeIRP.setIntencaoRegistroPreco(entity);
        unidadeIRP.setInicioVigencia(new Date());
        unidadeIRP.setUnidadeGerenciadora(entity.getUnidadeGerenciadora().getSubordinada());
        Util.adicionarObjetoEmLista(entity.getUnidades(), unidadeIRP);
    }

    private void gerarNotitificacao(IntencaoRegistroPreco entity) {
        String link = "/intencao-de-registro-de-preco/ver/" + entity.getId() + "/";
        Notificacao notificacao = new Notificacao();
        notificacao.setCriadoEm(new Date());
        notificacao.setUsuarioSistema(entity.getResponsavel());
        notificacao.setTitulo("Intenção de Registro de Preço");
        notificacao.setDescricao(entity.getNotificacao());
        notificacao.setLink(link);
        notificacao.setGravidade(Notificacao.Gravidade.ATENCAO);
        notificacao.setTipoNotificacao(TipoNotificacao.INTENCAO_REGISTRO_DE_PRECO);
        NotificacaoService.getService().notificar(notificacao);
    }

    public HierarquiaOrganizacional buscarUnidadeGerenciadoraVigente(IntencaoRegistroPreco entity) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ho.*  ")
            .append(" from intencaoregistropreco irp  ")
            .append("   inner join unidadeirp uirp on uirp.intencaoregistropreco_id = irp.id  ")
            .append("   inner join hierarquiaorganizacional ho on ho.subordinada_id = uirp.unidadeGerenciadora_id  ")
            .append(" where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("   and ho.tipohierarquiaorganizacional = :tipoHierarquia ")
            .append("   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uirp.inicioVigencia) and coalesce(trunc(uirp.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append("   and irp.id = :idIntencao ");
        Query q = em.createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("idIntencao", entity.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        try {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Nenhum unidade vigente encontrada para a intenção de registro de preço: " + entity + ".");
        } catch (NonUniqueResultException ne) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de uma unidade vigente para a intenção: " + entity + ".");
        }
    }

    public List<IntencaoRegistroPreco> buscarIRPVigentePorSituacao(Date dataOperacao, String filtro, SituacaoIRP situacao) {
        StringBuilder sql = new StringBuilder();
        sql.append("select irp.* from intencaoregistropreco irp  ")
            .append("   where irp.situacaoirp = :situacaoirp ")
            .append("   and (lower(irp.descricao) like :filtro or irp.numero like :filtro) ")
            .append("   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(irp.inicioVigencia) and coalesce(trunc(irp.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))");
        Query q = em.createNativeQuery(sql.toString(), IntencaoRegistroPreco.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setParameter("situacaoirp", situacao.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<IntencaoRegistroPreco> buscarIRPPorSituacao(String filtro, SituacaoIRP situacao) {
        String sql = " select irp.* from intencaoregistropreco irp  " +
            "          where irp.situacaoirp = :situacaoirp " +
            "           and  not exists (select 1 from formulariocotacao fc where fc.intencaoregistropreco_id = irp.id)" +
            "           and (lower(irp.descricao) like :filtro or irp.numero like :filtro) ";
        Query q = em.createNativeQuery(sql, IntencaoRegistroPreco.class);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        q.setParameter("situacaoirp", situacao.name());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<IntencaoRegistroPreco> buscarIRP(String filtro) {
        String sql = " select irp.* from intencaoregistropreco irp  " +
            "           where (lower(irp.descricao) like :filtro or irp.numero like :filtro) " +
            "          order by irp.id desc ";
        Query q = em.createNativeQuery(sql, IntencaoRegistroPreco.class);
        q.setParameter("filtro", "%" + filtro.toLowerCase() + "%");
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<UnidadeOrganizacional> buscarUnidadesParticipantesIRP(IntencaoRegistroPreco irp) {
        String sql = " select unid.* from unidadeparticipanteirp unidpart" +
            "           inner join unidadeorganizacional unid on unid.id = unidpart.unidadeparticipante_id " +
            "           inner join participanteirp part on part.id = unidpart.participanteirp_id " +
            "          where part.intencaoregistrodepreco_id = :idIrp ";
        Query q = em.createNativeQuery(sql, UnidadeOrganizacional.class);
        q.setParameter("idIrp", irp.getId());
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public List<ResumoItemIRPParticipanteVO> montarResumoItemPorParticipantes(IntencaoRegistroPreco irp) {
        List<ResumoItemIRPParticipanteVO> itens = Lists.newArrayList();
        for (LoteIntencaoRegistroPreco lote : irp.getLotes()) {
            for (ItemIntencaoRegistroPreco itemIrp : lote.getItens()) {
                ResumoItemIRPParticipanteVO itemVo = new ResumoItemIRPParticipanteVO();
                itemVo.setIrp(itemIrp);

                List<ItemParticipanteIRP> itensParticipantes = participanteIRPFacade.buscarItensParticipantesPorItemIrp(itemIrp);
                for (ItemParticipanteIRP itemPart : itensParticipantes) {
                    itemPart.getParticipanteIRP().setHierarquiaOrganizacional(participanteIRPFacade.buscarUnidadeParticipanteVigente(itemPart.getParticipanteIRP()));
                }
                itemVo.setItensParticipantes(itensParticipantes);
                itens.add(itemVo);
            }
        }
        return itens;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public FormularioCotacaoFacade getFormularioCotacaoFacade() {
        return formularioCotacaoFacade;
    }


}
