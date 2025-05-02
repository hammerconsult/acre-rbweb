/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Divida;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ParamParcelamento;
import br.com.webpublico.enums.SituacaoDebito;
import br.com.webpublico.enums.TipoCadastroTributario;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.tributario.consultadebitos.enums.TipoCadastroTributarioDTO;
import br.com.webpublico.tributario.consultadebitos.enums.TipoDeDebitoDTO;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class ParametroParcelamentoFacade extends AbstractFacade<ParamParcelamento> {

    private final BigDecimal CEM = new BigDecimal("100");
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    @EJB
    private DividaFacade dividaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private TributoFacade tributoFacade;

    public ParametroParcelamentoFacade() {
        super(ParamParcelamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public AtoLegalFacade getAtoLegalFacade() {
        return atoLegalFacade;
    }

    public DividaFacade getDividaFacade() {
        return dividaFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public boolean isCodigoEmUso(ParamParcelamento selecionado) {
        if (selecionado == null || selecionado.getCodigo() == null) {
            return false;
        }
        String hql = "from ParamParcelamento pp where pp.codigo = :codigo";
        if (selecionado.getId() != null) {
            hql += " and pp != :paramParcelamento";
        }
        Query q = em.createQuery(hql);
        q.setParameter("codigo", selecionado.getCodigo());
        if (selecionado.getId() != null) {
            q.setParameter("paramParcelamento", selecionado);
        }
        return !q.getResultList().isEmpty();
    }

    public boolean isDescricaoEmUso(ParamParcelamento selecionado) {
        if (selecionado == null || selecionado.getDescricao() == null || selecionado.getDescricao().trim().length() <= 0) {
            return false;
        }
        String hql = "from ParamParcelamento pp where lower(pp.descricao) = :descricao";
        if (selecionado.getId() != null) {
            hql += " and pp != :paramParcelamento";
        }
        Query q = em.createQuery(hql);
        q.setParameter("descricao", selecionado.getDescricao().toLowerCase().trim());
        if (selecionado.getId() != null) {
            q.setParameter("paramParcelamento", selecionado);
        }
        return !q.getResultList().isEmpty();
    }


    @Override
    public ParamParcelamento recuperar(Object id) {
        ParamParcelamento p = em.find(ParamParcelamento.class, id);
        p.getDividas().size();
        p.getFaixas().size();
        p.getTributos().size();
        return p;
    }

    public boolean percentualValido(BigDecimal valor) {

        return valor != null ? ((valor.compareTo(BigDecimal.ZERO) >= 0) && (valor.compareTo(CEM) <= 0)) : false;
    }

    public List<ParamParcelamento> listaVigentesPorTipoCadastro(TipoCadastroTributario tipoCadastro) {
        String hql = " from ParamParcelamento param where param.tipoCadastroTributario = :tipo and param.vigenciaInicio" +
            " <= current_date  and coalesce(param.vigenciaFim, current_date ) >= current_date ";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", tipoCadastro);
        return q.getResultList();

    }

    public List<Long> buscarIdsDividasParcelamento(Divida divida) {
        String sql = "select distinct coalesce(param.dividaparcelamento_id,0) from paramparcelamento param "
            + " inner join paramparcelamentodivida paramdiv on paramdiv.paramparcelamento_id = param.id "
            + " where paramdiv.divida_id in (:idDivida, :idAtiva)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idDivida", divida.getId());
        q.setParameter("idAtiva", divida.getDivida().getId());
        return q.getResultList();
    }

    public List<Divida> buscarDividasParcelamento(Divida divida) {
        String sql = "select distinct div.* from paramparcelamento param "
            + " inner join paramparcelamentodivida paramdiv on paramdiv.paramparcelamento_id = param.id "
            + " inner join divida div on div.id = param.dividaparcelamento_id "
            + " where paramdiv.divida_id in (:idsDividas)";
        Query q = em.createNativeQuery(sql, Divida.class);
        List<Long> idsDividas = Lists.newArrayList(divida.getId());
        if (divida.getDivida() != null) {
            idsDividas.add(divida.getDivida().getId());
        }
        q.setParameter("idsDividas", idsDividas);
        return q.getResultList();
    }


    public List<ParamParcelamento> buscarParametrosVigentes(Exercicio exercicio) {
        String sql = "select pr.* from PARAMPARCELAMENTO pr " +
            "where trunc(:fimVigencia) BETWEEN pr.vigenciainicio and coalesce(pr.vigenciafim, trunc(:fimVigencia))";
        Query q = em.createNativeQuery(sql, ParamParcelamento.class);
        q.setParameter("fimVigencia", DataUtil.getDia(31, 12, exercicio.getAno() - 1));
        List<ParamParcelamento> parametros = q.getResultList();
        for (ParamParcelamento parametro : parametros) {
            Hibernate.initialize(parametro.getDividas());
            Hibernate.initialize(parametro.getFaixas());
            Hibernate.initialize(parametro.getTributos());
        }
        return parametros;
    }

    public boolean hasParametroNoExercicio(Exercicio exercicio) {
        String sql = "select pr.id from PARAMPARCELAMENTO pr " +
            " where to_date('01/01/" + exercicio.getAno() + "','dd/MM/yyyy') >= pr.vigenciainicio " +
            "   and to_date('31/12/" + exercicio.getAno() + "','dd/MM/yyyy') <= coalesce(pr.vigenciafim, to_date('31/12/" + exercicio.getAno() + "','dd/MM/yyyy'))";
        Query q = em.createNativeQuery(sql);
        return !q.getResultList().isEmpty();
    }

    public List<ParamParcelamento> buscarParametroPorCodigoOrDescricao(String parte) {
        return em.createNativeQuery(" select * from paramparcelamento pp " +
                " where to_char(codigo) like :parte or trim(lower(pp.descricao)) like :parte " +
                " order by pp.id desc ", ParamParcelamento.class)
            .setParameter("parte", "%" + parte.trim().toLowerCase() + "%")
            .getResultList();
    }

    public List<ParamParcelamento> buscarParametrosPorParcelas(List<ResultadoParcela> parcelas) {
        List<TipoCadastroTributarioDTO> tiposCadastro = parcelas.stream()
            .map(ResultadoParcela::getTipoCadastroTributarioEnumValue)
            .distinct()
            .collect(Collectors.toList());
        if (tiposCadastro.size() > 1) {
            return Lists.newArrayList();
        }
        List<TipoDeDebitoDTO> tiposDeDebito = parcelas.stream()
            .map(ResultadoParcela::getEnumTipoDeDebito)
            .map((tipoDeDebitoDTO -> tipoDeDebitoDTO.equals(TipoDeDebitoDTO.DAP) ? TipoDeDebitoDTO.DA : tipoDeDebitoDTO))
            .map((tipoDeDebitoDTO -> tipoDeDebitoDTO.equals(TipoDeDebitoDTO.AJZP) ? TipoDeDebitoDTO.AJZ : tipoDeDebitoDTO))
            .distinct()
            .collect(Collectors.toList());
        if (tiposDeDebito.size() > 1) {
            return Lists.newArrayList();
        }
        SituacaoDebito situacaoDebito = SituacaoDebito.findByTipoDeDebito(tiposDeDebito.get(0));
        if (situacaoDebito == null) {
            return Lists.newArrayList();
        }
        List<Integer> exercicios = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelas) {
            exercicios.add(parcela.getExercicio());
        }
        List<Long> idsDividas = Lists.newArrayList();
        for (ResultadoParcela parcela : parcelas) {
            idsDividas.add(parcela.getIdDivida());
        }
        String sql = " select * from paramparcelamento p " +
            " where current_date between p.vigenciainicio and p.vigenciafim " +
            "      and p.tipocadastrotributario = '" + tiposCadastro.get(0).name() + "' " +
            "      and p.situacaodebito = '" + situacaoDebito.name() + "' ";
        for (Integer exercicio : exercicios) {
            sql += " and exists (select 1 " +
                "                        from paramparcelamentodivida pd " +
                "                       inner join exercicio ei on ei.id = pd.exercicioinicial_id " +
                "                       inner join exercicio ef on ef.id = pd.exerciciofinal_id " +
                "                     where pd.paramparcelamento_id = p.id " +
                "                       and " + exercicio + " between ei.ano and ef.ano) ";
        }
        for (Long idDivida : idsDividas) {
            sql += " and exists (select 1 " +
                "                        from paramparcelamentodivida pd " +
                "                     where pd.paramparcelamento_id = p.id " +
                "                       and pd.divida_id = " + idDivida + ") ";
        }
        sql += " order by p.id ";
        return em.createNativeQuery(sql, ParamParcelamento.class).getResultList();

    }
}
