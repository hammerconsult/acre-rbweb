/*
 * Codigo gerado automaticamente em Sun Nov 27 17:13:06 BRST 2011
 * Gerador de Facace
 */
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
import java.util.Date;
import java.util.List;

@Stateless
public class VeiculoFacade extends AbstractFacade<Veiculo> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private LancamentoTaxaVeiculoFacade lancamentoTaxaVeiculoFacade;
    @EJB
    private BaixaObjetoFrotaFacade baixaObjetoFrotaFacade;
    @EJB
    private ParametroFrotasFacade parametroFrotasFacade;
    @EJB
    private TaxaVeiculoFacade taxaVeiculoFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private ProgramaRevisaoVeiculoFacade programaRevisaoVeiculoFacade;

    public VeiculoFacade() {
        super(Veiculo.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Veiculo recuperar(Object id) {
        Veiculo veiculo = super.recuperar(id);
        veiculo.getProgramaRevisao().size();
        veiculo.getKmPercorridos().size();
        if (veiculo.getContrato() != null && veiculo.getContrato().getId() != null) {
            veiculo.setContrato(contratoFacade.recuperar(veiculo.getContrato().getId()));
        }
        if (veiculo.getDetentorArquivoComposicao() != null && veiculo.getDetentorArquivoComposicao().getArquivosComposicao() != null) {
            veiculo.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        veiculo.setBaixaObjetoFrota(baixaObjetoFrotaFacade.recuperarBaixaDoObjetoFrota(veiculo));
        veiculo.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
            TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
            veiculo.getUnidadeOrganizacional(),
            sistemaFacade.getDataOperacao()));

        if (veiculo.getUnidadeOrganizacionalResp() != null) {
            veiculo.setHierarquiaOrganizacionalResponsavel(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                veiculo.getUnidadeOrganizacionalResp(),
                sistemaFacade.getDataOperacao()));
        }
        veiculo.getUnidades().size();
        for (UnidadeObjetoFrota unidadeObjetoFrota : veiculo.getUnidades()) {
            unidadeObjetoFrota.setHierarquiaOrganizacional(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                unidadeObjetoFrota.getUnidadeOrganizacional(),
                sistemaFacade.getDataOperacao()));
        }
        return veiculo;
    }


    public List<Veiculo> buscarVeiculosPorPlacaOrDescricaoOrIdentificacaoOrNumeroProcessoContrato(String parte) {
        HierarquiaOrganizacional hierarquiaOrganizacionalCorrente =
            hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(
                TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente(),
                sistemaFacade.getDataOperacao());

        if (hierarquiaOrganizacionalCorrente != null) {
            String sql = " select obj.*, v.* " +
                "    from veiculo v " +
                "   inner join objetofrota obj on v.id = obj.id " +
                "   inner join unidadeobjetofrota ubf on ubf.objetofrota_id = obj.id " +
                "   inner join vwhierarquiaadministrativa vw on vw.subordinada_id = ubf.unidadeorganizacional_id " +
                "   left join bem b on obj.bem_id = b.id " +
                "   left join contrato c on obj.contrato_id = c.id " +
                " where not exists (select 1 from baixaObjetoFrota baixa where baixa.objetofrota_id = v.id) " +
                "   and obj.dataaquisicao <= current_date " +
                "   and (c.id is null or (c.terminovigencia >= current_date) ) " +
                "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(vw.inicioVigencia) and coalesce(trunc(vw.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
                "   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ubf.inicioVigencia) and coalesce(trunc(ubf.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))" +
                "   and (lower(trim(replace(v.placa, '-',''))) like :filtro " +
                "        or lower(b.descricao) like :filtro " +
                "        or lower(b.identificacao) like :filtro " +
                "        or lower(obj.descricao) like :filtro " +
                "        or lower(obj.identificacao) like :filtro " +
                "        or to_char(c.numeroProcesso) like :filtro)";
                //"   and substr(vw.codigo, 1, 5) = :secretaria ";
            Query q = em.createNativeQuery(sql, Veiculo.class);
            q.setParameter("filtro", "%" + parte.toLowerCase().trim() + "%");
            //q.setParameter("secretaria", hierarquiaOrganizacionalCorrente.getCodigo().substring(0, 5));
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            return q.getResultList();
        } else {
            throw new ExcecaoNegocioGenerica("Hierarquia da unidade: " + sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente() + " não encontrada.");
        }
    }

    public List<Veiculo> buscarVeiculosVigentes() {
        String sql = " select obj.*, v.* " +
            "    from veiculo v " +
            "   inner join objetofrota obj on v.id = obj.id " +
            "   left join bem b on obj.bem_id = b.id " +
            "   left join contrato c on obj.contrato_id = c.id " +
            " where not exists (select 1 from baixaObjetoFrota baixa where baixa.objetofrota_id = v.id) " +
            "   and obj.dataaquisicao <= current_date " +
            "   and (c.id is null or (c.terminovigencia >= current_date) ) ";
        Query q = em.createNativeQuery(sql, Veiculo.class);
        return q.getResultList();
    }

    public boolean identificacaoRegistrada(Veiculo veiculo) {
        if (veiculo.getUnidadeOrganizacional() == null) {
            return false;
        }
        String hql = " select veic from Veiculo veic " +
            "           inner join veic.unidades unid " +
            "          where unid.unidadeOrganizacional = :unidade " +
            "           and veic.identificacao = :identificacao ";
        if (veiculo.getId() != null) {
            hql += " and veic.id <> :id ";
        }
        Query q = em.createQuery(hql);
        q.setParameter("unidade", veiculo.getUnidadeOrganizacional());
        q.setParameter("identificacao", veiculo.getIdentificacao());
        if (veiculo.getId() != null) {
            q.setParameter("id", veiculo.getId());
        }
        return q.getResultList() != null && q.getResultList().size() > 0;
    }

    public List<Veiculo> buscarVeiculosComRevisoesAVencer() {
        List<Veiculo> toReturn = Lists.newArrayList();
        ParametroFrotas parametroFrotas = parametroFrotasFacade.buscarParametroFrotas();
        if (parametroFrotas != null) {
            String sql = "select obj.*, veic.* " +
                "    from veiculo veic " +
                "   inner join objetofrota obj on veic.id = obj.id " +
                "   left join kmpercorridoveiculo kmPerc on kmPerc.id = veic.kmPercorrido_id " +
                "   left join bem b on obj.bem_id = b.id " +
                "   left join contrato c on obj.contrato_id = c.id " +
                " where not exists (select 1 from baixaObjetoFrota baixa where baixa.objetofrota_id = veic.id) " +
                "   and trunc(obj.dataaquisicao) <= to_date(:dataOperacao, 'dd/MM/yyyy') " +
                "   and (c.id is null or (trunc(c.terminovigencia) >= to_date(:dataOperacao, 'dd/MM/yyyy')) ) " +
                "   and exists(select 1 " +
                "                 from programarevisaoveiculo revisao                 " +
                "                 where revisao.veiculo_id = veic.id " +
                "                 and revisao.revisaorealizada = 0 " +
                "                 and ((revisao.prazo * 30 >= (to_date(:dataOperacao, 'dd/MM/yyyy') - trunc(obj.dataaquisicao)) and (revisao.prazo * 30 - (to_date(:dataOperacao, 'dd/MM/yyyy') - trunc(obj.dataaquisicao)) <= :dias_a_vencer))  " +
                "                  or (((veic.kmaquisicao + revisao.km) >= kmPerc.kmatual) and ( (veic.kmaquisicao + revisao.km - kmPerc.kmatual) <= :kms_a_vencer)) )) ";
            Query q = em.createNativeQuery(sql, Veiculo.class);
            q.setParameter("dias_a_vencer", parametroFrotas.getDiasDaRevisaoAVencer());
            q.setParameter("kms_a_vencer", parametroFrotas.getQuilometrosDaRevisaoAVencer());
            q.setParameter("dataOperacao", DataUtil.getDataFormatada(new Date()));
            return q.getResultList();
        }
        return toReturn;
    }

    @Override
    public void salvarNovo(Veiculo entity) {
        entity = em.merge(entity);
        criarLancamentoKmPercorrido(entity, entity.getKmAtual());
    }

    public void criarLancamentoKmPercorrido(Veiculo veiculo, BigDecimal kmAtual) {
        em.persist(new KmPercorridoVeiculo(veiculo, kmAtual));
    }

    private void criarNotificacaoVeiculoComRevisaoVencendo(Veiculo veiculo) {
        String mensagem = "O veículo " + veiculo + " possui revisão(ões) próxima(s) do vencimento.";
        String link = "/frota/veiculo/editar/" + veiculo.getId();
        NotificacaoService.getService().notificar(
            TipoNotificacao.AVISO_VEICULOS_COM_REVISAO_VENCENDO.getDescricao(),
            mensagem,
            link,
            Notificacao.Gravidade.ATENCAO,
            TipoNotificacao.AVISO_VEICULOS_COM_REVISAO_VENCENDO);
    }

    public void lancarNotificacoesDeVeiculosComRevisaoAVencer() {
        List<Veiculo> veiculosANotificar = buscarVeiculosComRevisoesAVencer();
        NotificacaoService.getService().marcarComoLida(TipoNotificacao.AVISO_VEICULOS_COM_REVISAO_VENCENDO);
        for (Veiculo veiculo : veiculosANotificar) {
            criarNotificacaoVeiculoComRevisaoVencendo(veiculo);
        }
    }

    public HierarquiaOrganizacional buscarUnidadeVigenteVeiculo(Veiculo veiculo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ho.*  ")
            .append(" from veiculo v  ")
            .append("   inner join objetofrota obj on v.id = obj.id  ")
            .append("   inner join unidadeobjetofrota ubf on ubf.objetofrota_id = obj.id  ")
            .append("   inner join hierarquiaorganizacional ho on ho.subordinada_id = ubf.unidadeorganizacional_id  ")
            .append(" where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("   and ho.tipohierarquiaorganizacional = :tipoHierarquia ")
            .append("   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ubf.inicioVigencia) and coalesce(trunc(ubf.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append("   and v.id = :idVeiculo ");
        Query q = em.createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("idVeiculo", veiculo.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        try {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NoResultException e) {
            throw new ExcecaoNegocioGenerica("Nenhum unidade vigente encontrato para o veículo: " + veiculo + ".");
        } catch (NonUniqueResultException ne) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de uma  unidade vigente para o veículo: " + veiculo + ".");
        }
    }


    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public ParametroFrotasFacade getParametroFrotasFacade() {
        return parametroFrotasFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public LancamentoTaxaVeiculoFacade getLancamentoTaxaVeiculoFacade() {
        return lancamentoTaxaVeiculoFacade;
    }

    public TaxaVeiculoFacade getTaxaVeiculoFacade() {
        return taxaVeiculoFacade;
    }

    public ProgramaRevisaoVeiculoFacade getProgramaRevisaoVeiculoFacade() {
        return programaRevisaoVeiculoFacade;
    }
}
