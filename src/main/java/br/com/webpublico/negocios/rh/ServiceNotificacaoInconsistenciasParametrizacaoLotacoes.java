package br.com.webpublico.negocios.rh;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.webreportdto.dto.comum.TipoHierarquiaOrganizacionalDTO;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Service
public class ServiceNotificacaoInconsistenciasParametrizacaoLotacoes {

    @PersistenceContext
    protected transient EntityManager em;

    public void recuperarInconsistencias() {
        if (buscarRecursoFPVigenteEmMaisDeUmaHierarquiaVigente() || buscarRecursoFPVigenteNaoAssociadoHierarquiaVigente() ||
            buscarRecursoFPNaoVigenteAssociadoVinculoVigente() || buscarHierarquiasVigentesEmMaisDeUmaEntidadeDPC() ||
            buscarHierarquiasVigentesNãoAssociadaAUmaEntidadeDPC() || buscarHierarquiasNaoVigentesAssociadaVinculoVigente()) {
            criarNotificacao("/relatorio-inconsistencias-parametrizacao-lotacoes/", "Encontrada(s) inconsistência(s) na parametrização de lotações.");
        }
    }

    private void criarNotificacao(String url, String descricao) {
        List<Notificacao> notificacoesSalvas = NotificacaoService.getService().buscarNotificacoesPorLinkAndTipoNaoVisualizado(url, TipoNotificacao.INCONSISTENCIA_PARAMETRIZACAO_LOTACOES);
        if (notificacoesSalvas.isEmpty()) {
            NotificacaoService.getService().notificar(TipoNotificacao.INCONSISTENCIA_PARAMETRIZACAO_LOTACOES.getDescricao(),
                descricao, url, Notificacao.Gravidade.ATENCAO,
                TipoNotificacao.INCONSISTENCIA_PARAMETRIZACAO_LOTACOES);
        }
    }

    public Boolean buscarRecursoFPVigenteEmMaisDeUmaHierarquiaVigente() {
        String sql = " select rec.CODIGO         codigoRecursoFP, " +
            "       rec.DESCRICAO      descricaoRecursoFP, " +
            "       rec.INICIOVIGENCIA inicioVigenciaRecursoFP, " +
            "       rec.FINALVIGENCIA  finalVigenciaRecursoFP, " +
            "       ho.CODIGO          codigoOrgao, " +
            "       ho.DESCRICAO       descricaoOrgao, " +
            "       ho.INICIOVIGENCIA  inicioVigenciaOrgao, " +
            "       ho.FIMVIGENCIA     finalVigenciaOrgao, " +
            "       grup.NOME          descricaoGrupoRecurso " +
            " from RECURSOFP rec " +
            "         inner join AGRUPAMENTORECURSOFP agrup on rec.ID = agrup.RECURSOFP_ID " +
            "         inner join GRUPORECURSOFP grup on agrup.GRUPORECURSOFP_ID = grup.ID " +
            "         inner join VWHIERARQUIAADMINISTRATIVA ho on grup.HIERARQUIAORGANIZACIONAL_ID = ho.ID " +
            " where rec.id in (select recurso.ID " +
            "                 from recursofp recurso " +
            "                          inner join AGRUPAMENTORECURSOFP agrupamento on recurso.ID = agrupamento.RECURSOFP_ID " +
            "                          inner join GRUPORECURSOFP grupo on agrupamento.GRUPORECURSOFP_ID = grupo.ID " +
            "                          inner join VWHIERARQUIAADMINISTRATIVA vw on grupo.HIERARQUIAORGANIZACIONAL_ID = vw.ID " +
            "                 where  :datareferencia between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA,  :datareferencia) " +
            "                   and  :datareferencia between recurso.INICIOVIGENCIA and coalesce(recurso.FINALVIGENCIA,  :datareferencia) " +
            "                 group by recurso.ID " +
            "                 having count(recurso.ID) > 1) " +
            "  and  :datareferencia between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA,  :datareferencia) " +
            "  and  :datareferencia between rec.INICIOVIGENCIA and coalesce(rec.FINALVIGENCIA,  :datareferencia) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("datareferencia", SistemaFacade.getDataCorrente());

        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return true;
        }
        return false;
    }


    public Boolean buscarRecursoFPVigenteNaoAssociadoHierarquiaVigente() {
        String sql = " select rec.CODIGO         codigoRecursoFP,  " +
            "       rec.DESCRICAO      descricaoRecursoFP,  " +
            "       rec.INICIOVIGENCIA inicioVigenciaRecursoFP,  " +
            "       rec.FINALVIGENCIA  finalVigenciaRecursoFP  " +
            " from RECURSOFP rec  " +
            " where rec.id not in (select agrup.RECURSOFP_ID  " +
            "                     from AGRUPAMENTORECURSOFP agrup  " +
            "                              inner join GRUPORECURSOFP grup on agrup.GRUPORECURSOFP_ID = grup.ID  " +
            "                              inner join VWHIERARQUIAADMINISTRATIVA ho on grup.HIERARQUIAORGANIZACIONAL_ID = ho.ID  " +
            "                     where  :datareferencia between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA,  :datareferencia))  " +
            "  and  :datareferencia between rec.INICIOVIGENCIA and coalesce(rec.FINALVIGENCIA,  :datareferencia)";
        Query q = em.createNativeQuery(sql);
        q.setParameter("datareferencia", SistemaFacade.getDataCorrente());

        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return true;
        }
        return false;
    }

    public Boolean buscarRecursoFPNaoVigenteAssociadoVinculoVigente() {
        String sql = " select rec.CODIGO         codigoRecursoFP, " +
            "       rec.DESCRICAO      descricaoRecursoFP, " +
            "       rec.INICIOVIGENCIA inicioVigenciaRecursoFP, " +
            "       rec.FINALVIGENCIA  finalVigenciaRecursoFP, " +
            "       mat.MATRICULA      Matricula, " +
            "       v.NUMERO           Contrato, " +
            "       pf.NOME            Vinculo, " +
            "       v.INICIOVIGENCIA inicioVigenciaVinculo, " +
            "       v.FINALVIGENCIA finalVigenciaVinculo " +
            " from VINCULOFP v " +
            "         inner join matriculafp mat on v.MATRICULAFP_ID = mat.ID " +
            "         inner join pessoafisica pf on mat.PESSOA_ID = pf.ID " +
            "         inner join RECURSODOVINCULOFP recVinc on v.ID = recVinc.VINCULOFP_ID " +
            "         inner join RECURSOFP rec on recVinc.RECURSOFP_ID = rec.ID " +
            " where   :datareferencia between v.INICIOVIGENCIA and coalesce(v.FINALVIGENCIA,   :datareferencia) " +
            "  and   :datareferencia between recVinc.INICIOVIGENCIA and coalesce(recVinc.FINALVIGENCIA,   :datareferencia) " +
            "  and   :datareferencia not between rec.INICIOVIGENCIA and coalesce(rec.FINALVIGENCIA,   :datareferencia) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("datareferencia", SistemaFacade.getDataCorrente());

        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return true;
        }
        return false;
    }

    public Boolean buscarHierarquiasVigentesEmMaisDeUmaEntidadeDPC() {
        String sql = " select distinct ho.CODIGO                                                codigoOrgao,  " +
            "                ho.DESCRICAO                                             descricaoOrgao,  " +
            "                ho.INICIOVIGENCIA                                        inicioVigenciaOrgao,  " +
            "                ho.FIMVIGENCIA                                           finalVigenciaOrgao,  " +
            "                dpc.CODIGO || ' - ' || dpc.TIPODECLARACAOPRESTACAOCONTAS EntidadePrestacaoContas,  " +
            "                entDPC.INICIOVIGENCIA                                    inicioVigenciaEntidadePrestacaoContas,  " +
            "                entDPC.FINALVIGENCIA                                     finalVigenciaEntidadePrestacaoContas,  " +
            "                e.NOME                                                   descricaoEntidade,  " +
            "                entDPC.id                                                idEntidadeDPC " +
            "from (select vw.id hierarquia, entDP.id entidade  " +
            "      from ITEMENTIDADEUNIDADEORG itemUnidade  " +
            "               inner join VWHIERARQUIAADMINISTRATIVA vw on vw.id = itemUnidade.HIERARQUIAORGANIZACIONAL_ID  " +
            "               inner join ItemEntidadeDPContas itemEntidade on itemUnidade.ITEMENTIDADEDPCONTAS_ID = itemEntidade.ID  " +
            "               inner join EntidadeDPContas entDP on itemEntidade.ENTIDADEDPCONTAS_ID = entDP.ID  " +
            "      where   :datareferencia between entDP.INICIOVIGENCIA and coalesce(entDP.FINALVIGENCIA,   :datareferencia)  " +
            "        and   :datareferencia between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA,   :datareferencia)  " +
            "      group by vw.id, entDP.id  " +
            "      having count(vw.id) > 1) dados  " +
            "         inner join EntidadeDPContas entDPC on entDPC.id = dados.entidade  " +
            "         inner join DECLARACAOPRESTACAOCONTAS dpc on entDPC.DECLARACAOPRESTACAOCONTAS_ID = dpc.ID  " +
            "         inner join VWHIERARQUIAADMINISTRATIVA ho on ho.id = dados.hierarquia  " +
            "         inner join ITEMENTIDADEUNIDADEORG itemUn on itemUn.HIERARQUIAORGANIZACIONAL_ID = ho.id  " +
            "         inner join ItemEntidadeDPContas itemEntidade on itemUn.ITEMENTIDADEDPCONTAS_ID = itemEntidade.ID  " +
            "         inner join entidade e on itemEntidade.ENTIDADE_ID = e.ID  " +
            "order by ho.CODIGO, EntidadePrestacaoContas, entDPC.id, e.NOME ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("datareferencia", SistemaFacade.getDataCorrente());

        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return true;
        }
        return false;
    }


    public Boolean buscarHierarquiasVigentesNãoAssociadaAUmaEntidadeDPC() {
        String sql = " select distinct  ho.CODIGO         codigoOrgao, " +
            "                 ho.DESCRICAO      descricaoOrgao, " +
            "                 ho.INICIOVIGENCIA inicioVigenciaOrgao, " +
            "                 ho.FIMVIGENCIA    finalVigenciaOrgao " +
            " from HIERARQUIAORGANIZACIONAL ho " +
            " where ho.TIPOHIERARQUIAORGANIZACIONAL =  :tipoHierarquia " +
            "  and  :datareferencia between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA,  :datareferencia) " +
            "  and ho.INDICEDONO = 2 " +
            "  and ho.id not in (select hierarquia.id " +
            "                    from ITEMENTIDADEUNIDADEORG itemUnidade " +
            "                             inner join ItemEntidadeDPContas itemEntidade " +
            "                                        on itemUnidade.ITEMENTIDADEDPCONTAS_ID = itemEntidade.ID " +
            "                             inner join EntidadeDPContas entDP on itemEntidade.ENTIDADEDPCONTAS_ID = entDP.ID " +
            "                             inner join HIERARQUIAORGANIZACIONAL hierarquia " +
            "                                        on hierarquia.id = itemUnidade.HIERARQUIAORGANIZACIONAL_ID " +
            "                    where  :datareferencia between entDP.INICIOVIGENCIA and coalesce(entDP.FINALVIGENCIA,  :datareferencia) " +
            "                      and  :datareferencia between hierarquia.INICIOVIGENCIA and coalesce(hierarquia.FIMVIGENCIA,  :datareferencia) " +
            "                      and hierarquia.TIPOHIERARQUIAORGANIZACIONAL =  :tipoHierarquia " +
            "                      and hierarquia.INDICEDONO = 2) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("datareferencia", SistemaFacade.getDataCorrente());
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacionalDTO.ADMINISTRATIVA.name());

        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return true;
        }
        return false;
    }

    public Boolean buscarHierarquiasNaoVigentesAssociadaVinculoVigente() {
        String sql = " select vw.CODIGO         codigoOrgao, " +
            "       vw.DESCRICAO      descricaoOrgao, " +
            "       vw.INICIOVIGENCIA inicioVigenciaOrgao, " +
            "       vw.FIMVIGENCIA    finalVigenciaOrgao, " +
            "       Matricula, " +
            "       Contrato, " +
            "       Vinculo, " +
            "       inicioVigenciaVinculo, " +
            "       finalVigenciaVinculo, " +
            "       idUnidade " +
            " from (select distinct v.id                         idVinculo, " +
            "                      mat.MATRICULA                Matricula, " +
            "                      v.NUMERO                     Contrato, " +
            "                      pf.NOME                      Vinculo, " +
            "                      v.INICIOVIGENCIA             inicioVigenciaVinculo, " +
            "                      v.FINALVIGENCIA              finalVigenciaVinculo, " +
            "                      lot.UNIDADEORGANIZACIONAL_ID idUnidade " +
            "      from VINCULOFP v " +
            "               inner join lotacaofuncional lot on v.ID = lot.VINCULOFP_ID " +
            "               inner join matriculafp mat on v.MATRICULAFP_ID = mat.ID " +
            "               inner join pessoafisica pf on mat.PESSOA_ID = pf.ID " +
            "      where  :datareferencia between v.INICIOVIGENCIA and coalesce(v.FINALVIGENCIA,  :datareferencia) " +
            "        and  :datareferencia between lot.INICIOVIGENCIA and coalesce(lot.FINALVIGENCIA,  :datareferencia) " +
            "        and not exists(select 1 " +
            "                       from VWHIERARQUIAADMINISTRATIVA ho " +
            "                       where  :datareferencia between ho.INICIOVIGENCIA and coalesce(ho.FIMVIGENCIA,  :datareferencia) " +
            "                         and ho.SUBORDINADA_ID = lot.UNIDADEORGANIZACIONAL_ID)) dados " +
            "         inner join VWHIERARQUIAADMINISTRATIVA vw on vw.SUBORDINADA_ID = dados.idUnidade and vw.id = (select max(vw.ID) " +
            "                                                                                                      from VWHIERARQUIAADMINISTRATIVA vw " +
            "                                                                                                      where vw.SUBORDINADA_ID = dados.idUnidade) " +
            " order by vw.CODIGO, dados.Vinculo, dados.Matricula, dados.Contrato";
        Query q = em.createNativeQuery(sql);
        q.setParameter("datareferencia", SistemaFacade.getDataCorrente());

        List<Object[]> resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return true;
        }
        return false;
    }
}
