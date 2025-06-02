/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.PrestadorServicos;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.esocial.service.S2300Service;
import br.com.webpublico.esocial.service.S2306Service;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author peixe
 */
@Stateless
public class PrestadorServicosFacade extends AbstractFacade<PrestadorServicos> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private S2300Service s2300Service;
    private S2306Service s2306Service;

    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PrestadorServicosFacade() {
        super(PrestadorServicos.class);
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public S2300Service getS2300Service() {
        if (s2300Service == null) {
            s2300Service = Util.recuperarSpringBean(S2300Service.class);
        }
        return s2300Service;
    }

    public S2306Service getS2306Service() {
        if (s2306Service == null) {
            s2306Service = Util.recuperarSpringBean(S2306Service.class);
        }
        return s2306Service;
    }

    public List<PrestadorServicos> listaPrestadoresPorUsuarioFiltrando(String filtro, UsuarioSistema usuarioSistema, Date dataOperacao) {
        Long id;
        try {
            id = Long.parseLong(filtro);
        } catch (Exception e) {
            id = 0l;
        }
        String sql = "select prestador.*  " +
            " from prestadorservicos prestador  " +
            "         inner join pessoa on prestador.prestador_id = pessoa.id  " +
            "         inner join unidadeorganizacional lotacao on prestador.lotacao_id = lotacao.id  " +
            " where lotacao.id in (select distinct ho.subordinada_id  " +
            "                     from unidadeorganizacional uo  " +
            "                              inner join hierarquiaorganizacional ho  " +
            "                                         on uo.id = ho.subordinada_id and  " +
            "                                            ho.tipohierarquiaorganizacional = :tipoHierarquia  " +
            "                                             and :dataOperacao between ho.iniciovigencia and coalesce(ho.fimvigencia, :dataOperacao)  " +
            "                     start with ho.id in (select distinct hierarquia.id  " +
            "                                          from hierarquiaorganizacional hierarquia  " +
            "                                                   inner join usuariounidadeorganizacio uu  " +
            "                                                              on uu.unidadeorganizacional_id = hierarquia.subordinada_id  " +
            "                                                   inner join usuariosistema u on u.id = uu.usuariosistema_id  " +
            "                                          where u.id = :idUsuario  " +
            "                                            and hierarquia.tipohierarquiaorganizacional =  :tipoHierarquia  " +
            "                                            and :dataOperacao between trunc(hierarquia.iniciovigencia) and coalesce(trunc(hierarquia.fimvigencia), :dataOperacao))  " +
            "                     connect by prior ho.subordinada_id = ho.superior_id)  " +
            "  and (pessoa.id in (select pf.id  " +
            "                     from pessoafisica pf  " +
            "                     where pf.id = :filtroId  " +
            "                        or lower(pf.nome) like :filtro  " +
            "                        or lower(pf.cpf) like :filtro)  " +
            "    or pessoa.id in (select pj.id  " +
            "                     from pessoajuridica pj  " +
            "                     where pj.id = :filtroId  " +
            "                        or lower(pj.razaosocial) like :filtro  " +
            "                        or lower(pj.cnpj) like :filtro  " +
            "                        or lower(pj.nomefantasia) like :filtro))";
        Query q = em.createNativeQuery(sql, PrestadorServicos.class);
        q.setParameter("filtroId", id);
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("idUsuario", usuarioSistema.getId());
        q.setParameter("dataOperacao", dataOperacao);
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setMaxResults(10);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return Lists.newArrayList();
    }

    public Long retornaUltimoCodigo() {
        Long l;
        String hql = " select max(obj.codigo) from PrestadorServicos obj ";
        Query query = getEntityManager().createQuery(hql);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            l = (Long) query.getSingleResult();
            if (l == null) {
                l = 0l;
            }
            l += 1l;
        } else {
            return 1l;
        }
        return l;
    }

    public List<PrestadorServicos> buscarPrestadorServicoPorEmpregador(ConfiguracaoEmpregadorESocial empregadorESocial, Boolean somenteNaoEnviados, PrestadorServicos prestador, Date inicio, Date fim) {
        String sql = " select prestador.* from PrestadorServicos prestador " +
            " where prestador.lotacao_id in (:unidades)";
        if (prestador != null) {
            sql += " and prestador.id = :idPrestador ";
        }
        if (inicio != null) {
            sql += " and prestador.INICIOVIGENCIACONTRATO >= :inicioContrato ";
        }
        if (fim != null) {
            sql += " and prestador.FINALVIGENCIACONTRATO <= :fimContrato ";
        }
        if (somenteNaoEnviados) {
            sql += " and not exists (" +
                "    select * from REGISTROESOCIAL registro" +
                "    where registro.IDESOCIAL = prestador.ID and" +
                "          registro.TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
                " and registro.SITUACAO in :situacoes " +
                " and registro.EMPREGADOR_ID = :config)  ";
        }
        Query q = em.createNativeQuery(sql, PrestadorServicos.class);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        if (prestador != null) {
            q.setParameter("idPrestador", prestador.getId());
        }
        if (inicio != null) {
            q.setParameter("inicioContrato", inicio);
        }
        if (fim != null) {
            q.setParameter("fimContrato", fim);
        }
        if (somenteNaoEnviados) {
            q.setParameter("situacoes", Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", TipoArquivoESocial.S2300.name());
            q.setParameter("config", empregadorESocial.getId());
        }
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return resultList;
        }
        return null;
    }

    private List<Long> montarIdOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<Long> ids = Lists.newArrayList();
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            ids.add(item.getUnidadeOrganizacional().getId());
        }
        return ids;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void enviarS2300ESocial(ConfiguracaoEmpregadorESocial empregador, PrestadorServicos prestador) throws ValidacaoException {
        prestador.setPrestador(pessoaFisicaFacade.recuperarComDocumentos(prestador.getPrestador().getId()));
        getS2300Service().enviarS2300(empregador, prestador);
    }

    public void enviarS2306ESocial(ConfiguracaoEmpregadorESocial empregador, PrestadorServicos prestador) throws ValidacaoException {
        prestador.setPrestador(pessoaFisicaFacade.recuperarComDocumentos(prestador.getPrestador().getId()));
        getS2306Service().enviarS2306(empregador, prestador);
    }

    public List<PrestadorServicos> buscaPrestadorServicosNomeCPF(String parte) {
        String sql = "select p.* from PrestadorServicos p " +
            " inner join pessoafisica pf on p.prestador_id = pf.id" +
            " where lower(pf.nome) like :filtro " +
            " or lower(pf.cpf) like :filtro " +
            " or (replace(replace(pf.cpf,'.',''),'-','') like :filtro) ";
        Query q = em.createNativeQuery(sql, PrestadorServicos.class);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setMaxResults(10);
        List toReturn = q.getResultList();
        if (!toReturn.isEmpty()) {
            return toReturn;
        }
        return null;
    }

    public PrestadorServicos buscarPrestadorServicosPorIdPessoa(Long id) {
        String sql = "select p.* from PrestadorServicos p " +
            " inner join pessoafisica pf on p.prestador_id = pf.id" +
            " where pf.id = :idPessoa ";
        Query q = em.createNativeQuery(sql, PrestadorServicos.class);
        q.setParameter("idPessoa", id);
        List toReturn = q.getResultList();
        if (!toReturn.isEmpty()) {
            return (PrestadorServicos) toReturn.get(0);
        }
        return null;
    }

    public List<PrestadorServicos> buscaPrestadorServicosNomeCPFEmpregador(String parte, ConfiguracaoEmpregadorESocial empregadorESocial) {
        String sql = "select p.* from PrestadorServicos p " +
            " inner join pessoafisica pf on p.prestador_id = pf.id" +
            " where p.lotacao_id in (:unidades)" +
            " and (lower(pf.nome) like :filtro " +
            " or lower(pf.cpf) like :filtro " +
            " or (replace(replace(pf.cpf,'.',''),'-','') like :filtro)) ";
        Query q = em.createNativeQuery(sql, PrestadorServicos.class);
        q.setParameter("filtro", "%" + parte.toLowerCase() + "%");
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        q.setMaxResults(50);
        List toReturn = q.getResultList();
        if (!toReturn.isEmpty()) {
            return toReturn;
        }
        return Lists.newArrayList();
    }

    public boolean existePrestadorServicoVigenteNaData(Date data, HierarquiaOrganizacional hierarquiaOrganizacional) {
        String sql = " select ps.id from prestadorservicos ps " +
            " inner join hierarquiaorganizacional ho on ho.subordinada_id = ps.lotacao_id " +
            " where to_date(:data, 'dd/mm/yyyy') between ho.iniciovigencia and coalesce(ho.fimvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            " and to_date(:data, 'dd/mm/yyyy') between ps.iniciovigenciacontrato and coalesce(ps.finalvigenciacontrato, to_date(:data, 'dd/mm/yyyy')) " +
            " and to_date(:data, 'dd/mm/yyyy') between ps.iniciolotacao and coalesce(ps.finallotacao, to_date(:data, 'dd/mm/yyyy')) " +
            " and ho.tipohierarquiaorganizacional = :tipoHierarquia " +
            " and ho.id = :hierarquiaId ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("hierarquiaId", hierarquiaOrganizacional.getId());
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setMaxResults(1);
        List resultado = q.getResultList();
        if (resultado != null) {
            return !resultado.isEmpty();
        }
        return false;
    }

    public boolean prestadorServicoJaCadastrado(PrestadorServicos ps, HierarquiaOrganizacional lotacao) {
        String sql = " select distinct ps.id from prestadorservicos ps " +
            "   left join pessoafisica pf on ps.prestador_id = pf.id " +
            "   left join pessoajuridica pj on ps.prestador_id = pj.id " +
            "   inner join categoriatrabalhador ct on ct.id = ps.categoriatrabalhador_id " +
            "   inner join vwhierarquiaadministrativa vw on ps.lotacao_id = vw.subordinada_id " +
            "   where current_date between ps.iniciovigenciacontrato and coalesce(ps.finalvigenciacontrato, current_date) " +
            "   and FORMATACPFCNPJ(pf.CPF) = :cpf " +
            "and ct.codigo = :codigoCategoriaTrab " +
            "and vw.codigo =:codigoOrgao "+
            "and coalesce(ps.numerocontrato, '0') = coalesce(:numerocontrato, '0')";

        Query q = em.createNativeQuery(sql);
        q.setParameter("cpf", ps.getPrestador().getCpf_Cnpj());
        q.setParameter("numerocontrato", ps.getNumeroContrato());
        q.setParameter("codigoCategoriaTrab", ps.getCategoriaTrabalhador().getCodigo());
        q.setParameter("codigoOrgao", lotacao.getCodigo());
        q.setMaxResults(1);
        List resultado = q.getResultList();
        if (resultado != null) {
            return !resultado.isEmpty();
        }
        return false;
    }

}
