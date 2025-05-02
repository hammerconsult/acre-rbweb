package br.com.webpublico.singletons;

import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

import javax.ejb.*;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;


@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 5000)
public class SingletonContrato implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    private Set<UnidadeOrganizacional> unidades;

    @Lock(LockType.READ)
    public String getProximoNumeroContrato(Exercicio exercicio) {
        try {
            Preconditions.checkNotNull(exercicio, "Exercício do contrato está nulo.");
            String sql = "  " +
                " select " +
                "   max(cast(obj.numeroContrato as number)) as numero " +
                " from Contrato obj " +
                " where obj.exercicioContrato_id = :ex " +
                "   having max(cast(obj.numeroContrato as number)) is not null ";
            Query q = em.createNativeQuery(sql);
            q.setParameter("ex", exercicio.getId());
            Number numero = ((Number) q.getSingleResult()).longValue() + 1;
            return numero.toString();
        } catch (NoResultException e) {
            return "1";
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do contrato para o exercício " + exercicio.getAno() + ".");
        }
    }

    @Lock(LockType.READ)
    public String getProximoNumeroTermoAte2020(Contrato contrato) {
        try {
            Object numeroTermo = recuperarUltimoNumeroTermo(contrato);
            if (numeroTermo == null) {
                return "1";
            }
            return String.valueOf(Integer.parseInt(numeroTermo.toString()) + 1);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(" Erro ao gerar o número do termo do contrato para a unidade " + contrato.getUnidadeAdministrativa() + ". Detalhes: " + e.getMessage());
        }
    }

    @Lock(LockType.WRITE)
    public String getProximoNumeroTermo(Contrato contrato) {
        try {
            Object numeroTermo = recuperarUltimoNumeroTermo(contrato);
            if (numeroTermo == null) {
                return gerarPrimeiroNumeroTermo(contrato);
            }
            return acrescentarProximoNumeroTermo(numeroTermo.toString());
        } catch (NonUniqueResultException e) {
            throw new ExcecaoNegocioGenerica("Não foi possível gerar o número do contrato para o exercício " + contrato.getExercicioContrato().getAno() + ", órgão " + contrato.getUnidadeAdministrativa() + ". Entre em contato com o suporte!");
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Não foi possível gerar o número do contrato para o exercício " + contrato.getExercicioContrato().getAno() + ", unidade " + contrato.getUnidadeAdministrativa() + "  " + e.getMessage() + ". Entre em contato com o suporte!");
        }
    }

    public String gerarPrimeiroNumeroTermo(Contrato contrato) {
        String numero = contrato.getUnidadeAdministrativa().getCodigo().substring(0, 5) + "0001";
        return numero.replace(".", "");
    }

    public String acrescentarProximoNumeroTermo(Object singleResult) {
        int ultimoNumero = Integer.parseInt(singleResult.toString());
        String numero = (ultimoNumero + 1) + "";
        return numero.length() == 8 ? numero : "0" + numero;
    }

    public Object recuperarUltimoNumeroTermo(Contrato contrato) {
        Preconditions.checkNotNull(contrato.getExercicioContrato(), "Exercício está nulo.");
        Preconditions.checkNotNull(contrato.getUnidadeAdministrativa(), "Unidade Administrativa está nula.");
        String sql = " select max(cast(obj.numerotermo as number)) as numero " +
            "          from contrato obj " +
            "           inner join exercicio ex on ex.id = obj.exerciciocontrato_id " +
            "           inner join unidadecontrato uc on uc.contrato_id = obj.id " +
            "           inner join hierarquiaorganizacional ho on ho.subordinada_id = uc.unidadeadministrativa_id " +
            "          where ex.id = :idExercicio " +
            "           and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            "           and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.iniciovigencia) and coalesce(trunc(ho.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))   " +
            "           and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(uc.iniciovigencia) and coalesce(trunc(uc.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))   " +
            "           and substr(ho.codigo,0,6) = :codigoHo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idExercicio", contrato.getExercicioContrato().getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("codigoHo", contrato.getUnidadeAdministrativa().getCodigo().substring(0, 6));
        return q.getSingleResult();
    }

    @Lock(LockType.WRITE)
    public void bloquearUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoDaUnidade(unidadeOrganizacional, sistemaFacade.getDataOperacao());
        if (unidades == null) {
            reiniciarUnidades();
        }
        unidades.add(orgao.getSubordinada());
    }

    @Lock(LockType.WRITE)
    public void desbloquearUnidade(UnidadeOrganizacional unidadeOrganizacional) {
        HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoDaUnidade(unidadeOrganizacional, sistemaFacade.getDataOperacao());
        unidades.remove(orgao.getSubordinada());
    }

    @Lock(LockType.WRITE)
    public boolean isBloqueado(UnidadeOrganizacional unidadeOrganizacional) {
        HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoDaUnidade(unidadeOrganizacional, sistemaFacade.getDataOperacao());
        return unidades != null && unidades.contains(orgao.getSubordinada());
    }

    public void reiniciarUnidades() {
        unidades = Sets.newHashSet();
    }

    public Set<UnidadeOrganizacional> getUnidades() {
        return unidades;
    }

    public void setUnidades(Set<UnidadeOrganizacional> unidades) {
        this.unidades = unidades;
    }
}
