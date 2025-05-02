package br.com.webpublico.negocios.rh.saudeservidor;

import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.esocial.ItemConfiguracaoEmpregadorESocial;
import br.com.webpublico.entidades.rh.saudeservidor.CAT;
import br.com.webpublico.esocial.comunicacao.enums.TipoArquivoESocial;
import br.com.webpublico.esocial.enums.SituacaoESocial;
import br.com.webpublico.esocial.service.ESocialUtilService;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.PrestadorServicosFacade;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * @Author peixe on 13/09/2016  18:11.
 */
@Stateless
public class CATFacade extends AbstractFacade<CAT> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    private ESocialUtilService eSocialUtilService;

    @EJB
    private PrestadorServicosFacade prestadorServicosFacade;

    @PostConstruct
    public void init() {
        eSocialUtilService = (ESocialUtilService) Util.getSpringBeanPeloNome("eSocialUtilService");
    }

    public CATFacade() {
        super(CAT.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void enviarEventoS2210(ConfiguracaoEmpregadorESocial configuracao, CAT cat, ContratoFP contrato) throws ValidacaoException {
        eSocialUtilService.getS2210Service().enviarS2210(configuracao, cat, contrato);
    }


    public List<CAT> buscarCatPorPessoaCPF(String parte, Boolean somenteNaoEnviados, ConfiguracaoEmpregadorESocial empregadorESocial) {
        String sql = "select distinct cat.* from VINCULOFP vinculo " +
            " inner join contratofp contrato on vinculo.ID = contrato.ID " +
            " inner join matriculafp mat on vinculo.MATRICULAFP_ID = mat.ID " +
            " inner join pessoafisica pf on mat.PESSOA_ID = pf.ID " +
            " inner join cat on pf.ID = CAT.COLABORADOR_ID " +
            " where vinculo.UNIDADEORGANIZACIONAL_ID in (:unidades) " +
            " and cat.ocorridoEm between vinculo.INICIOVIGENCIA and coalesce (vinculo.FINALVIGENCIA, sysdate)" ;
        if (!Strings.isNullOrEmpty(parte)) {
            sql += " and ((lower(pf.nome) like :parte) " +
                " or (replace(replace(pf.cpf,'.',''),'-','') like :filtroCpf))";
        }
        if (somenteNaoEnviados) {
            sql += " and not exists(select *" +
                "                 from REGISTROESOCIAL registro" +
                "                 where registro.IDESOCIAL like to_char('%' || contrato.id|| '%')" +
                "                   and registro.TIPOARQUIVOESOCIAL = :tipoArquivoESocial " +
                "                   and registro.SITUACAO in (:situacoes) " +
                "    ) ";
        }
        Query q = em.createNativeQuery(sql, CAT.class);
        q.setParameter("unidades", montarIdOrgaoEmpregador(empregadorESocial));
        if (!Strings.isNullOrEmpty(parte)) {
            q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
            q.setParameter("filtroCpf", "%" + parte.toLowerCase().trim().replace(".", "").replace("-", "") + "%");
        }
        if (somenteNaoEnviados) {
            q.setParameter("situacoes", com.google.common.collect.Lists.newArrayList(SituacaoESocial.PROCESSADO_COM_SUCESSO.name(), SituacaoESocial.PROCESSADO_COM_ADVERTENCIA.name()));
            q.setParameter("tipoArquivoESocial", TipoArquivoESocial.S2210.name());
        }
        return q.getResultList();
    }

    private List<Long> montarIdOrgaoEmpregador(ConfiguracaoEmpregadorESocial empregador) {
        List<Long> ids = com.google.common.collect.Lists.newArrayList();
        for (ItemConfiguracaoEmpregadorESocial item : empregador.getItemConfiguracaoEmpregadorESocial()) {
            ids.add(item.getUnidadeOrganizacional().getId());
        }
        return ids;
    }
}
