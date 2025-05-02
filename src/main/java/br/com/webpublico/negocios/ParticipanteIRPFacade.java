package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoIRP;
import br.com.webpublico.enums.TipoControleLicitacao;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ParticipanteIRPFacade extends AbstractFacade<ParticipanteIRP> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private IntencaoRegistroPrecoFacade intencaoRegistroPrecoFacade;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private GrupoBemFacade grupoBemFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;


    public ParticipanteIRPFacade() {
        super(ParticipanteIRP.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public ParticipanteIRP recuperar(Object id) {
        ParticipanteIRP entity = super.recuperar(id);
        Hibernate.initialize(entity.getUnidades());
        Hibernate.initialize(entity.getItens());
        atribuirUnidadeVigenteParticipante(entity);
        return entity;
    }

    public ParticipanteIRP recuperarItens(Object id) {
        ParticipanteIRP entity = super.recuperar(id);
        atribuirUnidadeVigenteParticipante(entity);
        Hibernate.initialize(entity.getItens());
        for (ItemParticipanteIRP item : entity.getItens()) {
            ItemIntencaoRegistroPreco itemIRP = item.getItemIntencaoRegistroPreco();
            itemIRP.getObjetoCompra().setGrupoContaDespesa(objetoCompraFacade.criarGrupoContaDespesa(itemIRP.getObjetoCompra().getTipoObjetoCompra(), itemIRP.getObjetoCompra().getGrupoObjetoCompra()));
        }
        return entity;
    }

    public void atribuirUnidadeVigenteParticipante(ParticipanteIRP entity) {
        HierarquiaOrganizacional hierarquia = buscarUnidadeParticipanteVigente(entity);
        if (hierarquia != null) {
            entity.setHierarquiaOrganizacional(hierarquia);
        }
    }

    public ParticipanteIRP salvarParticipante(ParticipanteIRP entity) {
        inserirUnidadeParticipante(entity);
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ParticipanteIRP.class, "numero"));
        }
        entity = em.merge(entity);
        return entity;
    }

    public void inserirUnidadeParticipante(ParticipanteIRP entity) {
        if (entity.getUnidades() == null || entity.getUnidades().isEmpty()) {
            UnidadeParticipanteIRP unidadeParticipante = new UnidadeParticipanteIRP();
            unidadeParticipante.setParticipanteIRP(entity);
            unidadeParticipante.setInicioVigencia(new Date());
            unidadeParticipante.setUnidadeParticipante(entity.getHierarquiaOrganizacional().getSubordinada());
            Util.adicionarObjetoEmLista(entity.getUnidades(), unidadeParticipante);
        } else {
            UnidadeParticipanteIRP unidadeParticipanteIRP = entity.getUnidades().get(entity.getUnidades().size() - 1);
            unidadeParticipanteIRP.setUnidadeParticipante(entity.getHierarquiaOrganizacional().getSubordinada());
            Util.adicionarObjetoEmLista(entity.getUnidades(), unidadeParticipanteIRP);
        }
    }

    public boolean isMesmoItemObjetoCompra(String especificacao1, ObjetoCompra objetoCompra1,
                                           String especificacao2, ObjetoCompra objetoCompra2) {
        if (!Strings.isNullOrEmpty(especificacao1) && !Strings.isNullOrEmpty(especificacao2)) {
            return objetoCompra1.equals(objetoCompra2) && especificacao1.trim().equals(especificacao2.trim());
        }
        return objetoCompra1.equals(objetoCompra2);
    }

    public List<ParticipanteIRP> buscarParticipantesIRPAprovado(String parte, Licitacao licitacao) {
        String sql = " select part.* from participanteirp part " +
            "           inner join intencaoregistropreco irp on irp.id = part.intencaoregistrodepreco_id " +
            "           inner join formulariocotacao fc on fc.intencaoregistropreco_id = irp.id " +
            "           inner join cotacao cot on cot.formulariocotacao_id = fc.id " +
            "           inner join solicitacaomaterial sol on sol.cotacao_id = cot.id " +
            "           inner join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
            "           inner join licitacao lic on lic.processodecompra_id = pc.id " +
            "           inner join unidadeparticipanteirp uirp on uirp.participanteIRP_id = part.id " +
            "           inner join hierarquiaorganizacional ho on ho.subordinada_id = uirp.UNIDADEPARTICIPANTE_ID " +
            "          where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            "            and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            "            and (upper(translate(ho.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:str,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')  " +
            "                or ho.codigo like :str or replace(ho.codigo, '.', '') like :str ) " +
            "            and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uirp.inicioVigencia) and coalesce(trunc(uirp.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "            and lic.id = :idLicitacao " +
            "            and part.situacao = :situacao ";
        Query q = em.createNativeQuery(sql, ParticipanteIRP.class);
        q.setParameter("idLicitacao", licitacao.getId());
        q.setParameter("str", "%" + parte.toUpperCase().trim() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("situacao", SituacaoIRP.APROVADA.name());
        List<ParticipanteIRP> list = q.getResultList();
        for (ParticipanteIRP part : list) {
            part.setHierarquiaOrganizacional(buscarUnidadeParticipanteVigente(part));
        }
        return list;
    }

    public List<ParticipanteIRP> buscarParticipantesIRPAprovado(String parte, IntencaoRegistroPreco irp) {
        String sql = " select part.* from participanteirp part " +
            "           inner join intencaoregistropreco irp on irp.id = part.intencaoregistrodepreco_id " +
            "           inner join unidadeparticipanteirp uirp on uirp.participanteIRP_id = part.id " +
            "           inner join hierarquiaorganizacional ho on ho.subordinada_id = uirp.UNIDADEPARTICIPANTE_ID " +
            "          where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
            "            and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            "            and (upper(translate(ho.descricao,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:str,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')  " +
            "                or ho.codigo like :str or replace(ho.codigo, '.', '') like :str ) " +
            "            and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uirp.inicioVigencia) and coalesce(trunc(uirp.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "            and irp.id = :idIrp " +
            "            and part.situacao = :situacao ";
        Query q = em.createNativeQuery(sql, ParticipanteIRP.class);
        q.setParameter("idIrp", irp.getId());
        q.setParameter("str", "%" + parte.toUpperCase().trim() + "%");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("situacao", SituacaoIRP.APROVADA.name());
        List<ParticipanteIRP> list = q.getResultList();
        for (ParticipanteIRP part : list) {
            part.setHierarquiaOrganizacional(buscarUnidadeParticipanteVigente(part));
        }
        return list;
    }

    public HierarquiaOrganizacional buscarUnidadeParticipanteVigente(ParticipanteIRP entity) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ho.*  ")
            .append(" from participanteirp irp  ")
            .append("   inner join unidadeparticipanteirp uirp on uirp.participanteIRP_id = irp.id  ")
            .append("   inner join hierarquiaorganizacional ho on ho.subordinada_id = uirp.unidadeParticipante_id  ")
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
            throw new ExcecaoNegocioGenerica("Nenhuma unidade vigente encontrada para o participante IRP: " + entity + ".");
        } catch (NonUniqueResultException ne) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de uma unidade vigente para a intenção participante: " + entity + ".");
        }
    }

    public List<ItemParticipanteIRP> buscarItensUnidadeParticipante(ParticipanteIRP participanteIRP) {
        String sql = "  select item.* from itemparticipanteirp item " +
            "               inner join participanteIRP part on item.participanteIRP_id = part.id " +
            "               and part.id = :idParticipante " +
            "               and part.situacao = :situacaoItem ";
        Query q = em.createNativeQuery(sql, ItemParticipanteIRP.class);
        q.setParameter("idParticipante", participanteIRP.getId());
        q.setParameter("situacaoItem", SituacaoIRP.APROVADA.name());
        List list = q.getResultList();
        if (!Util.isListNullOrEmpty(list)) {
            return list;
        }
        return Lists.newArrayList();
    }

    public List<ItemParticipanteIRP> buscarItensParticipantesPorItemIrp(ItemIntencaoRegistroPreco itemIrp) {
        String sql = "  select item.* from itemparticipanteirp item " +
            "           inner join participanteIRP part on item.participanteIRP_id = part.id " +
            "           where item.itemintencaoregistropreco_id = :idItemIrp " +
            "           and part.situacao = :situacaoItem " +
            "           order by part.gerenciador desc, part.numero ";
        Query q = em.createNativeQuery(sql, ItemParticipanteIRP.class);
        q.setParameter("idItemIrp", itemIrp.getId());
        q.setParameter("situacaoItem", SituacaoIRP.APROVADA.name());
        return q.getResultList();
    }

    public ItemParticipanteIRP buscarItemPorUnidade(Long idLicitacao, UnidadeOrganizacional unidadeOrganizacional, ObjetoCompra objetoCompra,
                                                    Integer numeroItem, Integer numeroLote) {
        String sql = "  select item.* from itemparticipanteirp item " +
            "               inner join participanteIRP part on item.participanteIRP_id = part.id " +
            "               inner join itemintencaoregistropreco itemIrp on itemIrp.id = item.itemintencaoregistropreco_id " +
            "               inner join loteintencaoregistropreco loteIrp on loteIrp.id = itemIrp.loteintencaoregistropreco_id " +
            "               inner join intencaoregistropreco irp on irp.id = loteIrp.intencaoregistropreco_id " +
            "               inner join formulariocotacao fc on fc.intencaoregistropreco_id = irp.id " +
            "               inner join cotacao cot on cot.formulariocotacao_id = fc.id " +
            "               inner join solicitacaomaterial sol on sol.cotacao_id = cot.id" +
            "               inner join processodecompra pc on pc.solicitacaomaterial_id = sol.id " +
            "               inner join licitacao lic on lic.processodecompra_id = pc.id " +
            "               inner join unidadeparticipanteirp unidpart on unidpart.participanteirp_id = part.id " +
            "               inner join hierarquiaorganizacional ho on ho.subordinada_id = unidpart.unidadeparticipante_id " +
            "           where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "               and ho.tipohierarquiaorganizacional = :tipoHierarquia  " +
            "               and ho.subordinada_id = :idUnidade" +
            "               and itemIrp.objetocompra_id = :idObjetoCompra" +
            "               and part.situacao = :situacaoItem " +
            "               and lic.id = :idLicitacao " +
            "               and itemIrp.numero = :numeroItem " +
            "               and loteirp.numero = :numeroLote ";
        Query q = em.createNativeQuery(sql, ItemParticipanteIRP.class);
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("idObjetoCompra", objetoCompra.getId());
        q.setParameter("idLicitacao", idLicitacao);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("situacaoItem", SituacaoIRP.APROVADA.name());
        q.setParameter("numeroItem", numeroItem);
        q.setParameter("numeroLote", numeroLote);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return (ItemParticipanteIRP) q.getSingleResult();
        }
        return null;
    }

    public BigDecimal getQuantidadeOrValorParticipanteNaoGerenciador(ItemIntencaoRegistroPreco itemIrp) {
        if (itemIrp.getId() == null) {
            return BigDecimal.ZERO;
        }
        String sql;
        sql = "  select coalesce(case when itemirp.tipocontrole = :controleQuantidade " +
            "                  then sum(itempart.quantidade) " +
            "                  else sum(itempart.valor) end, 0) " +
            "  from itemparticipanteirp itempart " +
            "   inner join itemintencaoregistropreco itemirp on itemirp.id = itempart.itemintencaoregistropreco_id " +
            "   inner join participanteirp part on part.id = itempart.participanteirp_id " +
            " where itemirp.id = :idItemIrp " +
            " and part.gerenciador <> :gerenciador " +
            " group by itemirp.tipocontrole ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idItemIrp", itemIrp.getId());
        q.setParameter("gerenciador", Boolean.TRUE);
        q.setParameter("controleQuantidade", TipoControleLicitacao.QUANTIDADE.name());
        Util.imprimeSQL(sql, q);
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public IntencaoRegistroPrecoFacade getIntencaoRegistroPrecoFacade() {
        return intencaoRegistroPrecoFacade;
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public GrupoBemFacade getGrupoBemFacade() {
        return grupoBemFacade;
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }
}
