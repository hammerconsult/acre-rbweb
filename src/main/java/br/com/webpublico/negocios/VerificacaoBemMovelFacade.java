package br.com.webpublico.negocios;

import br.com.webpublico.entidades.VerificacaoBemMovel;
import br.com.webpublico.entidadesauxiliares.VOVerificacaoBemMovel;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SituacaoVerificacaoBemMovel;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Stateless
public class VerificacaoBemMovelFacade extends AbstractFacade<VerificacaoBemMovel> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;

    public VerificacaoBemMovelFacade() {
        super(VerificacaoBemMovel.class);
    }

    public VerificacaoBemMovel salvarRetornando(VerificacaoBemMovel verificacaoBemMovel) {
        return em.merge(verificacaoBemMovel);
    }

    public VerificacaoBemMovel concluirVerificacao(VerificacaoBemMovel verificacaoBemMovel) {
        verificacaoBemMovel.setDataConclusao(new Date());
        verificacaoBemMovel.setSituacao(SituacaoVerificacaoBemMovel.FINALIZADA);
        if (verificacaoBemMovel.getCodigo() == null) {
            verificacaoBemMovel.setCodigo(singletonGeradorCodigo.getProximoCodigo(VerificacaoBemMovel.class, "codigo"));
        }
        return em.merge(verificacaoBemMovel);
    }

    @Override
    public VerificacaoBemMovel recuperar(Object id) {
        VerificacaoBemMovel verificacaoBemMovel = em.find(VerificacaoBemMovel.class, id);
        verificacaoBemMovel.getBensVerificacao().size();
        if (verificacaoBemMovel.getDetentorArquivoComposicao() != null) {
            verificacaoBemMovel.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        verificacaoBemMovel.setHierarquiaOrganizacional(getHierarquiaOrganizacionalFacade()
            .getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(),
                verificacaoBemMovel.getUnidadeOrganizacional(),
                verificacaoBemMovel.getInicioVerificacao()));
        return verificacaoBemMovel;
    }

    public List<VOVerificacaoBemMovel> buscarDadosBensNaoVerificados(VerificacaoBemMovel verificacaoBemMovel) {

        String sql = "select distinct " +
            "                null as dataConclusao, " +
            "                bem.identificacao as registroBem, " +
            "                bem.registroanterior as registroAnteriorBem, " +
            "                bem.descricao as descricaoBem, " +
            "                grupo.codigo as grupoBem, " +
            "                estado.localizacao as localizacao, " +
            "                vwadm.codigo || ' - ' || vwadm.descricao as administrativa, " +
            "                vworc.codigo || ' - ' || vworc.descricao as orcamentaria, " +
            "                bem.marca as marca, " +
            "                bem.modelo as modelo, " +
            "                estado.estadobem as estadoConservacao, " +
            "                estado.tipoaquisicaobem as tipoAquisicao, " +
            "                bem.dataaquisicao as dataAquisicao, " +
            "                estado.valororiginal as valorOriginal " +
            " from bem " +
            "         inner join eventobem ev on bem.id = ev.bem_id " +
            "         inner join estadobem estado on ev.estadoresultante_id = estado.id " +
            "         inner join grupobem grupo on estado.grupobem_id = grupo.id " +
            "         inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = estado.detentoraorcamentaria_id " +
            "         inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = estado.detentoraadministrativa_id " +
            "            where to_date(:dataReferencia, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(trunc(vwadm.fimvigencia), to_date(:dataReferencia, 'dd/MM/yyyy')) " +
            "         and to_date(:dataReferencia, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(trunc(vworc.fimvigencia), to_date(:dataReferencia, 'dd/MM/yyyy')) " +
            "         and vwadm.codigo like :codigoAdm " +
            "         and estado.estadobem <> :estadoBaixado " +
            "         and ev.situacaoeventobem <> :situacaoBloqueado " +
            "         and ev.dataoperacao = (select max(ultimoEvento.dataoperacao) from eventobem ultimoEvento where ultimoEvento.bem_id = bem.id) " +
            "         and not exists (select 1 from verificacaobemmovel v " +
            "                         inner join itemverificacaobemmovel item on v.id = item.verificacaobemmovel_id " +
            "                          where item.bem_id = bem.id" +
            "                          and v.id = :IdVerificacao) " +
            "    order by administrativa, orcamentaria ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("IdVerificacao", verificacaoBemMovel.getId());
        q.setParameter("codigoAdm", verificacaoBemMovel.getHierarquiaOrganizacional().getCodigoSemZerosFinais() + "%");
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(verificacaoBemMovel.getDataConclusao()));
        q.setParameter("estadoBaixado", EstadoConservacaoBem.BAIXADO.name());
        q.setParameter("situacaoBloqueado", SituacaoEventoBem.BLOQUEADO.name());
        return preencher(q);
    }

    public List<VOVerificacaoBemMovel> buscarDadosBensVerificados(VerificacaoBemMovel verificacaoBemMovel) {
        String sql = "select distinct " +
            "                verifica.dataconclusao as dataconclusao, " +
            "                bem.identificacao as registrobem, " +
            "                bem.registroanterior as registroanteriorbem, " +
            "                bem.descricao as descricaobem, " +
            "                grupo.codigo as grupobem, " +
            "                estado.localizacao as localizacao, " +
            "                vwadm.codigo || ' - ' || vwadm.descricao as administrativa, " +
            "                vworc.codigo || ' - ' || vworc.descricao as orcamentaria, " +
            "                bem.marca as marca, " +
            "                bem.modelo as modelo, " +
            "                estado.estadobem as estadoconservacao, " +
            "                estado.tipoaquisicaobem as tipoaquisicao, " +
            "                bem.dataaquisicao as dataaquisicao, " +
            "                estado.valororiginal as valororiginal " +
            " " +
            "from verificacaobemmovel verifica " +
            "         inner join itemverificacaobemmovel item on verifica.id = item.verificacaobemmovel_id " +
            "         inner join bem bem on item.bem_id = bem.id " +
            "         inner join eventobem ev on bem.id = ev.bem_id " +
            "         inner join estadobem estado on ev.estadoresultante_id = estado.id " +
            "         inner join grupobem grupo on estado.grupobem_id = grupo.id " +
            "         inner join vwhierarquiaorcamentaria vworc on vworc.subordinada_id = estado.detentoraorcamentaria_id " +
            "         inner join vwhierarquiaadministrativa vwadm on vwadm.subordinada_id = estado.detentoraadministrativa_id " +
            "         where to_date(:dataReferencia, 'dd/MM/yyyy') between trunc(vwadm.iniciovigencia) and coalesce(trunc(vwadm.fimvigencia), to_date(:dataReferencia, 'dd/MM/yyyy')) " +
            "         and to_date(:dataReferencia, 'dd/MM/yyyy') between trunc(vworc.iniciovigencia) and coalesce(trunc(vworc.fimvigencia), to_date(:dataReferencia, 'dd/MM/yyyy')) " +
            "    and verifica.id = :IdVerificacao " +
            "    and ev.dataoperacao = (select max(ultimoEvento.dataoperacao) from eventobem ultimoEvento where ultimoEvento.bem_id = bem.id) " +
            "     order by administrativa, orcamentaria ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("IdVerificacao", verificacaoBemMovel.getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(verificacaoBemMovel.getDataConclusao()));
        return preencher(q);
    }

    private List<VOVerificacaoBemMovel> preencher(Query q) {
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            List<VOVerificacaoBemMovel> toRetun = Lists.newArrayList();
            VOVerificacaoBemMovel relatorio;
            for (Object[] object : (List<Object[]>) q.getResultList()) {
                relatorio = new VOVerificacaoBemMovel(object);
                toRetun.add(relatorio);
            }
            return toRetun;
        }
    }

    public Boolean verificarSeExisteVerificacaoBemParaMesmoMes(VerificacaoBemMovel verificacaoBemMovel) {
        String sql = " select v.* from VERIFICACAOBEMMOVEL v " +
            "where v.UNIDADEORGANIZACIONAL_ID = :idUnidade " +
            "  and extract(month from v.INICIOVERIFICACAO) = :mes " +
            "  and extract(year from v.INICIOVERIFICACAO) = :ano" +
            "  and v.SITUACAO = :situacao ";
        if (verificacaoBemMovel.getId() != null) {
            sql += " and v.id <> :idVerificacao ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("idUnidade", verificacaoBemMovel.getUnidadeOrganizacional().getId());
        q.setParameter("situacao", SituacaoVerificacaoBemMovel.EM_ELABORACAO.name());
        q.setParameter("mes", DataUtil.getMes(verificacaoBemMovel.getInicioVerificacao()));
        q.setParameter("ano", DataUtil.getAno(verificacaoBemMovel.getInicioVerificacao()));
        if (verificacaoBemMovel.getId() != null) {
            q.setParameter("idVerificacao", verificacaoBemMovel.getId());
        }
        return !q.getResultList().isEmpty();
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

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

}

