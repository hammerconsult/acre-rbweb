/*
 * Codigo gerado automaticamente em Thu Dec 01 17:13:45 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.LicitacaoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.administrativo.LicitacaoVO;
import br.com.webpublico.entidadesauxiliares.administrativo.ProgressaoLanceCertame;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.ITipoDocumentoAnexo;
import br.com.webpublico.negocios.administrativo.RepresentanteLicitacaoFacade;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.singletons.SingletonGeradorCodigoAdministrativo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.model.SelectItem;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Stateless
public class LicitacaoFacade extends AbstractFacade<Licitacao> {

    private static final int DOIS = 2;
    private static final int TRES = 3;
    @EJB
    private ReservaFonteDespesaOrcFacade reservaFonteDespesaOrcFacade;
    @EJB
    private ProcessoDeCompraFacade processoDeCompraFacade;
    @EJB
    private ComissaoFacade comissaoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private VeiculoDePublicacaoFacade veiculoDePublicacaoFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ModeloDoctoFacade modeloDoctoFacade;
    @EJB
    private VersaoDoctoLicitacaoFacade versaoDoctoLicitacaoFacade;
    @EJB
    private DoctoHabilitacaoFacade doctoHabilitacaoFacade;
    @EJB
    private LicitacaoDoctoFornecedorFacade licitacaoDoctoFornecedorFacade;
    @EJB
    private PregaoFacade pregaoFacade;
    @EJB
    private ConfiguracaoAdministrativaFacade configuracaoAdministrativaFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private SolicitacaoEmpenhoFacade solicitacaoEmpenhoFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private DotacaoSolicitacaoMaterialFacade dotacaoSolicitacaoMaterialFacade;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private DoctoLicitacaoFacade doctoLicitacaoFacade;
    @EJB
    private ParecerLicitacaoFacade parecerLicitacaoFacade;
    @EJB
    private ItemPregaoFacade itemPregaoFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private FeriadoFacade feriadoFacade;
    @EJB
    private DocumentosNecessariosAnexoFacade documentosNecessariosAnexoFacade;
    @EJB
    private DocumentoLicitacaoFacade documentoLicitacaoFacade;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private ObjetoCompraFacade objetoCompraFacade;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private String parte;
    @EJB
    private CertameFacade certameFacade;
    @EJB
    private MapaComparativoTecnicaPrecoFacade mapaComparativoTecnicaPrecoFacade;
    @EJB
    private SingletonGeradorCodigoAdministrativo singletonGeradorCodigoAdministrativo;
    @EJB
    private RepresentanteLicitacaoFacade representanteLicitacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private StatusFornecedorLicitacaoFacade statusFornecedorLicitacaoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private StatusLicitacaoFacade statusLicitacaoFacade;
    @EJB
    private ConfiguracaoLicitacaoFacade configuracaoLicitacaoFacade;

    public LicitacaoFacade() {
        super(Licitacao.class);
    }

    public CertameFacade getCertameFacade() {
        return certameFacade;
    }

    public MapaComparativoTecnicaPrecoFacade getMapaComparativoTecnicaPrecoFacade() {
        return mapaComparativoTecnicaPrecoFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<Licitacao> buscarLicitacaoPorTipoAvaliacaoAndNumeroOrDescricaoOrExercico(String parte, TipoAvaliacaoLicitacao tipoAvaliacaoLicitacao) {
        String hql = " select l from Licitacao l "
            + " inner join l.listaDeStatusLicitacao situacoes "
            + " where situacoes.id = (select max(s.id) from StatusLicitacao s where s.licitacao = l) "
            + "  and (to_char(situacoes.tipoSituacaoLicitacao) <> :cancelada "
            + adicionarCondicoesStatusLicitacaoHQL("l", "situacoes") + " ) "
            + "  and l.tipoAvaliacao = :avaliacao "
            + "  and ( lower(l.resumoDoObjeto) like :param " +
            "   or l.numeroLicitacao like :param "
            + " or  lower(l.processoDeCompra.descricao) like :param  "
            + " or to_char(l.exercicio.ano) like :param) "
            + " order by  l.numeroLicitacao desc ";
        Query q = em.createQuery(hql);
        q.setParameter("avaliacao", tipoAvaliacaoLicitacao);
        q.setParameter("param", "%" + parte.toLowerCase() + "%");
        q.setParameter("cancelada", TipoSituacaoLicitacao.CANCELADA.name());

        return q.getResultList();
    }

    public List<Licitacao> buscarLicitacaoParaCertameFiltrandoNumeroOrDescricaoOrExercico(String parte) {
        String hql = " select l from Licitacao l "
            + " inner join l.listaDeStatusLicitacao situacoes "
            + " where situacoes.id = (select max(s.id) from StatusLicitacao s where s.licitacao = l) "
            + "  and to_char(situacoes.tipoSituacaoLicitacao) <> :situacao "
            + adicionarCondicoesStatusLicitacaoHQL("l", "situacoes")
            + "  and l.tipoAvaliacao <> :mod1 "
            + "  and l.modalidadeLicitacao <> :mod2 "
            + "  and ( lower(l.resumoDoObjeto) like :param "
            + "        or l.numeroLicitacao like :param "
            + "        or to_char(l.exercicio.ano) like :param) "
            + "  order by  l.numeroLicitacao desc ";
        Query q = em.createQuery(hql);
        q.setParameter("mod1", TipoAvaliacaoLicitacao.TECNICA_PRECO);
        q.setParameter("mod2", ModalidadeLicitacao.PREGAO);
        q.setParameter("param", "%" + parte.toLowerCase() + "%");
        q.setParameter("situacao", TipoSituacaoLicitacao.CANCELADA.name());

        return q.getResultList();
    }

    public List<Licitacao> buscarLicitacoesPorModalidadeAndNumeroOrDescricaoOrExercico(String parte, ModalidadeLicitacao modalidadeLicitacao) {
        String hql = " select l from Licitacao l "
            + " where l.modalidadeLicitacao = :modalidade "
            + " and ( lower(l.processoDeCompra.descricao) like :param "
            + " or l.numeroLicitacao like :param "
            + " or l.exercicio.ano like :param) "
            + " order by  l.numeroLicitacao desc ";
        Query q = em.createQuery(hql);
        q.setParameter("modalidade", modalidadeLicitacao);
        q.setParameter("param", "%" + parte.toLowerCase() + "%");

        return q.getResultList();
    }

    public List<Licitacao> buscarLicitacaoPorModalidade(String parte, ModalidadeLicitacao... modalidades) {
        String sql = "" +
            " select distinct * from ( " +
            " select lic.* from licitacao lic " +
            "  inner join statuslicitacao st on st.licitacao_id = lic.id " +
            "  inner join exercicio e on e.id = lic.exercicio_id " +
            "  inner join processodecompra processo on lic.processodecompra_id = processo.id " +
            " where lic.modalidadelicitacao in (:modalidades) " +
            "   and (lower(lic.resumodoobjeto) like :filter " +
            "    or lower(processo.numero) like :filter " +
            "    or to_char(lic.numerolicitacao) like :filter " +
            "    or to_char(lic.numerolicitacao) || '/' || to_char(e.ano) like :filter " +
            "    or replace(to_char(lic.numerolicitacao) || '/' || to_char(e.ano), '/', '') like :filter) " +
            " order by e.ano desc, lic.numerolicitacao desc " +
            " )";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("filter", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("modalidades", Util.getListOfEnumName(modalidades));

        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }


    public List<Licitacao> buscarLicitacaoHomologadaPorModalidade(String parte, ModalidadeLicitacao... modalidades) {
        String sql = "  " +
            " select lic.* from licitacao lic " +
            "  inner join statuslicitacao st on st.licitacao_id = lic.id " +
            "  inner join exercicio e on e.id = lic.exercicio_id " +
            "  inner join processodecompra processo on lic.processodecompra_id = processo.id " +
            " where st.datastatus = (select max(status.datastatus) from statuslicitacao status where status.licitacao_id = lic.id) " +
            "   and st.tiposituacaolicitacao = :statusHomologada " +
            "   and lic.modalidadelicitacao in (:modalidades) " +
            "   and (lower(lic.resumodoobjeto) like :filter " +
            "    or lower(processo.numero) like :filter " +
            "    or to_char(lic.numerolicitacao) like :filter " +
            "    or to_char(lic.numerolicitacao) || '/' || to_char(e.ano) like :filter " +
            "    or replace(to_char(lic.numerolicitacao) || '/' || to_char(e.ano), '/', '') like :filter) " +
            " order by e.ano desc, lic.numerolicitacao desc ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("filter", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("statusHomologada", TipoSituacaoLicitacao.HOMOLOGADA.name());
        q.setParameter("modalidades", Util.getListOfEnumName(modalidades));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }


    public String complementaWhereConsultaLicitacao(List<String> palavras) {
        String retorno = "";

        for (String s : palavras) {
            String param = ":parte" + palavras.indexOf(s);
            try {
                Integer i = Integer.parseInt(s);
                if (s.length() == 4) {
                    retorno += " to_char(l.exercicio.ano) = " + param + " and";
                } else {
                    retorno += " to_char(l.numeroLicitacao) = " + param + " and";
                }
            } catch (NumberFormatException nfe) {
                retorno += " to_char(lower(l.modalidadeLicitacao)) like " + param + " and";
            }
        }

        retorno = retorno.length() > 0 ? retorno.substring(0, retorno.length() - 3) : retorno;

        return retorno;
    }

    public List<Licitacao> buscarLicitacoesComItensHomologadosDaUnidadeLogada(String parte) {
        String sql;
        sql = "select distinct lic.* " +
            "from ( select distinct sm.id as idSolicitacaoMaterial " +
            "       from (    select uo.id as idUnidadeUsuario, uo.descricao as descricaoUO " +
            "                   from UsuarioSistema us " +
            "             inner join UsuarioUnidadeOrganizacio uuo on uuo.usuarioSistema_ID = us.id " +
            "             inner join UnidadeOrganizacional uo on uo.id = uuo.unidadeOrganizacional_ID " +
            "                  where us.login = :login and uuo.gestorlicitacao = 1) " +
            "       inner join SolicitacaoMaterial sm on sm.unidadeOrganizacional_ID = idUnidadeUsuario " +
            "       where sm.unidadeOrganizacional_ID = :id_unidade " +
            "       union " +
            "       select distinct sm.id as idSolicitacaoMaterial " +
            "       from (    select uo.id as idUnidadeUsuario, uo.descricao as descricaoUO " +
            "                   from UsuarioSistema us " +
            "             inner join UsuarioUnidadeOrganizacio uuo on uuo.usuarioSistema_ID = us.id " +
            "             inner join UnidadeOrganizacional uo on uo.id = uuo.unidadeOrganizacional_ID " +
            "                  where us.login = :login and uuo.gestorlicitacao = 1) " +
            "       inner join hierarquiaorganizacional ho on ho.subordinada_id = idUnidadeUsuario " +
            "                                             and :data_operacao between ho.iniciovigencia and coalesce(ho.fimvigencia, :data_operacao) " +
            "                                             and ho.tipohierarquiaorganizacional = :tipo_hierarquia_adm " +
            "       inner join hierarquiaorgresp resp on resp.hierarquiaorgadm_id = ho.id " +
            "                                        and :data_operacao between resp.datainicio and coalesce(resp.datafim, :data_operacao) " +
            "       inner join hierarquiaorganizacional hoo on hoo.id = resp.hierarquiaorgorc_id " +
            "       inner join UGestoraUOrganizacional ugesuo on ugesuo.unidadeOrganizacional_ID = hoo.subordinada_id " +
            "       inner join UnidadeGestora uniges on uniges.id = ugesuo.unidadeGestora_ID " +
            "       inner join exercicio e on e.id = uniges.exercicio_id and e.ano = extract(year from :data_operacao)" +
            "       inner join UnidadeSolicitacaoMat unisolmat on unisolmat.unidadeGestora_ID = uniges.id " +
            "       inner join SolicitacaoMaterial sm on sm.id = unisolmat.solicitacaoMaterial_ID " +
            "       where ho.subordinada_id = :id_unidade  " +
            "       union " +
            "       select distinct sm.id as idSolicitacaoMaterial " +
            "               from (    select uo.id as idUnidadeUsuario, uo.descricao as descricaoUO " +
            "                   from UsuarioSistema us " +
            "             inner join UsuarioUnidadeOrganizacio uuo on uuo.usuarioSistema_ID = us.id " +
            "             inner join UnidadeOrganizacional uo on uo.id = uuo.unidadeOrganizacional_ID " +
            "                  where us.login = :login and uuo.gestorlicitacao = 1) " +
            "       inner join solicitacaomaterialext sme on sme.unidadeorganizacional_id = idUnidadeUsuario " +
            "       inner join adesao a on a.solicitacaomaterialexterno_id = sme.id and a.adesaoaceita = 1 " +
            "       inner join ataregistropreco arp on arp.id = a.ataderegistrodepreco_id " +
            "       inner join licitacao l on l.id = arp.licitacao_id " +
            "       inner join processodecompra pc on pc.id = l.processodecompra_id " +
            "       inner join SolicitacaoMaterial sm on sm.id = pc.solicitacaomaterial_id " +
            "       where sme.unidadeorganizacional_id = :id_unidade " +
            ") idSolicitacaoMaterial " +
            "inner join LoteSolicitacaoMaterial lotesm on lotesm.solicitacaoMaterial_ID = idSolicitacaoMaterial " +
            "inner join ItemSolicitacao items on items.loteSolicitacaoMaterial_ID = lotesm.id " +
            "inner join ItemProcessoDeCompra itempdc on itempdc.itemSolicitacaoMaterial_ID = items.id " +
            "inner join LoteProcessoDeCompra lotepdc on lotepdc.id = itempdc.loteProcessoDeCompra_ID " +
            "inner join ProcessoDeCompra pdc on pdc.id = lotepdc.processoDeCompra_ID " +
            "inner join Licitacao lic on lic.processoDeCompra_ID = pdc.id " +
            "inner join StatusLicitacao status on status.id = (select max(id) from StatusLicitacao where licitacao_ID = lic.id) " +
            "where itempdc.situacaoItemProcessoDeCompra = :situacao_item_homologada " +
            "  and (lic.numerolicitacao like :parte or lower(lic.resumodoobjeto) like :parte or lower(lic.modalidadelicitacao) like :parte) " +
            "order by lic.numeroLicitacao desc ";

        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("login", sistemaFacade.getUsuarioCorrente().getLogin());
        q.setParameter("id_unidade", sistemaFacade.getUnidadeOrganizacionalAdministrativaCorrente().getId());
        q.setParameter("tipo_hierarquia_adm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("data_operacao", sistemaFacade.getDataOperacao());
        q.setParameter("situacao_item_homologada", SituacaoItemProcessoDeCompra.HOMOLOGADO.name());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<Licitacao> buscarLicitacoesComItensHomologadosPorUnidadesUsuario(String parte) {
        StringBuilder sql = new StringBuilder();
        sql.append("select distinct lic.*  ")
            .append("from ( select distinct sm.id as idSolicitacaoMaterial  ")
            .append("       from (select uo.id as idUnidadeUsuario, uo.descricao as descricaoUO  ")
            .append("             from UsuarioSistema us  ")
            .append("             inner join UsuarioUnidadeOrganizacio uuo on uuo.usuarioSistema_ID = us.id  ")
            .append("             inner join UnidadeOrganizacional uo on uo.id = uuo.unidadeOrganizacional_ID  ")
            .append("             where us.login = :login ")
            .append("             and uuo.gestorlicitacao = 1)  ")
            .append("       inner join SolicitacaoMaterial sm on sm.unidadeOrganizacional_ID = idUnidadeUsuario  ")
            .append("       union  ")
            .append("       select distinct sm.id as idSolicitacaoMaterial  ")
            .append("       from (select uo.id as idUnidadeUsuario, uo.descricao as descricaoUO  ")
            .append("             from UsuarioSistema us  ")
            .append("             inner join UsuarioUnidadeOrganizacio uuo on uuo.usuarioSistema_ID = us.id  ")
            .append("             inner join UnidadeOrganizacional uo on uo.id = uuo.unidadeOrganizacional_ID  ")
            .append("             where us.login = :login ")
            .append("             and uuo.gestorlicitacao = 1) ")
            .append("       inner join hierarquiaorganizacional ho on ho.subordinada_id = idUnidadeUsuario  ")
            .append("         and :data_operacao between ho.iniciovigencia and coalesce(ho.fimvigencia, :data_operacao)  ")
            .append("         and ho.tipohierarquiaorganizacional = :tipo_hierarquia_adm  ")
            .append("       inner join hierarquiaorgresp resp on resp.hierarquiaorgadm_id = ho.id  ")
            .append("         and :data_operacao between resp.datainicio and coalesce(resp.datafim, :data_operacao)  ")
            .append("       inner join hierarquiaorganizacional hoo on hoo.id = resp.hierarquiaorgorc_id  ")
            .append("       inner join UGestoraUOrganizacional ugesuo on ugesuo.unidadeOrganizacional_ID = hoo.subordinada_id  ")
            .append("       inner join UnidadeGestora uniges on uniges.id = ugesuo.unidadeGestora_ID  ")
            .append("       inner join exercicio e on e.id = uniges.exercicio_id and e.ano = extract(year from :data_operacao) ")
            .append("       inner join UnidadeSolicitacaoMat unisolmat on unisolmat.unidadeGestora_ID = uniges.id  ")
            .append("       inner join SolicitacaoMaterial sm on sm.id = unisolmat.solicitacaoMaterial_ID  ")
            .append("       union  ")
            .append("       select distinct sm.id as idSolicitacaoMaterial  ")
            .append("               from (select uo.id as idUnidadeUsuario, uo.descricao as descricaoUO  ")
            .append("                     from UsuarioSistema us  ")
            .append("                     inner join UsuarioUnidadeOrganizacio uuo on uuo.usuarioSistema_ID = us.id  ")
            .append("                     inner join UnidadeOrganizacional uo on uo.id = uuo.unidadeOrganizacional_ID  ")
            .append("                     where us.login = :login ")
            .append("                     and uuo.gestorlicitacao = 1) ")
            .append("       inner join solicitacaomaterialext sme on sme.unidadeorganizacional_id = idUnidadeUsuario  ")
            .append("       inner join adesao a on a.solicitacaomaterialexterno_id = sme.id and a.adesaoaceita = 1  ")
            .append("       inner join ataregistropreco arp on arp.id = a.ataderegistrodepreco_id  ")
            .append("       inner join licitacao l on l.id = arp.licitacao_id  ")
            .append("       inner join processodecompra pc on pc.id = l.processodecompra_id  ")
            .append("       inner join SolicitacaoMaterial sm on sm.id = pc.solicitacaomaterial_id  ")
            .append(" ) idSolicitacaoMaterial  ")
            .append(" inner join LoteSolicitacaoMaterial lotesm on lotesm.solicitacaoMaterial_ID = idSolicitacaoMaterial  ")
            .append(" inner join ItemSolicitacao items on items.loteSolicitacaoMaterial_ID = lotesm.id  ")
            .append(" inner join ItemProcessoDeCompra itempdc on itempdc.itemSolicitacaoMaterial_ID = items.id  ")
            .append(" inner join LoteProcessoDeCompra lotepdc on lotepdc.id = itempdc.loteProcessoDeCompra_ID  ")
            .append(" inner join ProcessoDeCompra pdc on pdc.id = lotepdc.processoDeCompra_ID  ")
            .append(" inner join Licitacao lic on lic.processoDeCompra_ID = pdc.id  ")
            .append(" inner join StatusLicitacao status on status.id = (select max(id) from StatusLicitacao where licitacao_ID = lic.id)  ")
            .append(" where itempdc.situacaoItemProcessoDeCompra = :situacao_item_homologada  ")
            .append("   and (lic.numerolicitacao like :parte or lower(lic.resumodoobjeto) like :parte or lower(lic.modalidadelicitacao) like :parte)  ")
            .append(" order by lic.numeroLicitacao desc ");
        Query q = em.createNativeQuery(sql.toString(), Licitacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("login", sistemaFacade.getUsuarioCorrente().getLogin());
        q.setParameter("tipo_hierarquia_adm", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        q.setParameter("data_operacao", sistemaFacade.getDataOperacao());
        q.setParameter("situacao_item_homologada", SituacaoItemProcessoDeCompra.HOMOLOGADO.name());
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<Licitacao> buscarLicitacoesPorModalidadeAndNumeroOrDescricaoOrExercico(String parte) {
        String hql = "select l from " + Licitacao.class.getSimpleName() + " as l"
            + " inner join l.listaDeStatusLicitacao situacoes "
            + "   inner join l.exercicio e "
            + "   inner join l.exercicioModalidade exMod "
            + " where situacoes.id = (select max(s.id) from StatusLicitacao s where s.licitacao = l) "
            + "  and (to_char(situacoes.tipoSituacaoLicitacao) <> :cancelada "
            + adicionarCondicoesStatusLicitacaoHQL("l", "situacoes") + ") " +
            "  and (lower(l.resumoDoObjeto) like :parte "
            + "    or to_char(l.numeroLicitacao) like :parteNumero "
            + "    or to_char(l.numeroLicitacao) || '/' || to_char(exMod.ano) like :parte "
            + "    or replace(to_char(l.numeroLicitacao) || '/' || to_char(e.ano), '/', '') like :parte "
            + "    or to_char(l.numero) like :parteNumero "
            + "    or to_char(l.numero) || '/' || to_char(exMod.ano) like :parte "
            + "    or replace(to_char(l.numero) || '/' || to_char(exMod.ano), '/', '') like :parte )"
            + " order by l.numeroLicitacao desc ";
        Query query = em.createQuery(hql);
        query.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        query.setParameter("parteNumero", parte.trim() + "%");
        query.setParameter("cancelada", TipoSituacaoLicitacao.CANCELADA.name());
        query.setMaxResults(50);
        try {
            List<Licitacao> retorno = query.getResultList();
            if (!retorno.isEmpty()) {
                for (Licitacao lic : retorno) {
                    lic.getListaDeStatusLicitacao().size();
                }
            }
            return retorno;
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public List<ItemPropostaFornecedor> getItensVencidosPeloFornecedorPorStatus(Licitacao licitacao, SituacaoItemProcessoDeCompra situacaoIpc,
                                                                                Pessoa p, List<TipoClassificacaoFornecedor> status) {
        return getItensVencidosPeloFornecedorPorStatus(licitacao, situacaoIpc, p, status, null, null);
    }

    public List<ItemPropostaFornecedor> getItensVencidosPeloFornecedorPorStatus(Licitacao licitacao, SituacaoItemProcessoDeCompra situacaoIpc,
                                                                                Pessoa p, List<TipoClassificacaoFornecedor> status, UnidadeOrganizacional uo, Date dataReferencia) {
        if (licitacao == null) {
            return null;
        }
        if (licitacao.isPregao() || licitacao.isRDCPregao()) {
            return getItensVencidosPeloFornecedorPorStatusNoPregao(licitacao, p, status, Lists.newArrayList(situacaoIpc), uo, dataReferencia);
        }
        if (licitacao.isCredenciamento()) {
            return getItensVencidosPeloFornecedorPorStatusNoCredenciamento(licitacao, p, status);
        }
        List<ItemPropostaFornecedor> retorno;
        retorno = getItensVencidosPeloFornecedorPorStatusNoCertame(licitacao, p, status, situacaoIpc, uo, dataReferencia);
        if (retorno != null && !retorno.isEmpty()) {
            return retorno;
        }
        retorno = getItensVencidosPeloFornecedorPorStatusNoMapaComparativoTecnicaPreco(licitacao, p, status, situacaoIpc, uo, dataReferencia);
        return retorno;
    }

    public List<ItemPropostaFornecedor> getItensVencidosPeloFornecedorPorStatusNoCredenciamento(Licitacao licitacao, Pessoa fornecedor, List<TipoClassificacaoFornecedor> status) {
        String sql = " select item.* from licitacao lic " +
            "           inner join licitacaofornecedor lf on lf.licitacao_id = lic.id " +
            "           inner join propostafornecedor pf on pf.licitacaofornecedor_id = lf.id " +
            "           inner join lotepropfornec lote on lote.propostafornecedor_id = pf.id " +
            "           inner join itempropfornec item on item.lotepropostafornecedor_id = lote.id " +
            "         where lic.modalidadelicitacao = :credenciado " +
            "           and lf.empresa_id = :idFornecedor " +
            "           and lic.id = :idLicitacao " +
            "           and lf.tipoClassificacaoFornecedor in (:status) ";
        Query q = em.createNativeQuery(sql, ItemPropostaFornecedor.class);
        q.setParameter("idFornecedor", fornecedor.getId());
        q.setParameter("idLicitacao", licitacao.getId());
        q.setParameter("credenciado", ModalidadeLicitacao.CREDENCIAMENTO.name());
        q.setParameter("status", Util.getListOfEnumName(status));
        return q.getResultList();
    }

    public List<ItemPropostaFornecedor> getItensVencidosPeloFornecedorPorStatusNoPregao(Licitacao l, Pessoa p, List<TipoClassificacaoFornecedor> status, List<SituacaoItemProcessoDeCompra> situacoesIpc) {
        return getItensVencidosPeloFornecedorPorStatusNoPregao(l, p, status, situacoesIpc, null, null);
    }

    public List<ItemPropostaFornecedor> getItensVencidosPeloFornecedorPorStatusNoPregao(Licitacao l, Pessoa p, List<TipoClassificacaoFornecedor> status, List<SituacaoItemProcessoDeCompra> situacoesIpc, UnidadeOrganizacional uo, Date dataReferencia) {
        String hql = "";

        if (TipoApuracaoLicitacao.ITEM.equals(l.getTipoApuracao())) {
            hql = " select ipf from ItemPregao ip" +
                "  inner join ip.pregao p" +
                "  inner join p.licitacao lic" +
                "  inner join ip.itemPregaoItemProcesso ipip" +
                "  inner join ipip.itemProcessoDeCompra ipc" +
                "  inner join ipc.itensPropostaFornecedor ipf" +
                "  inner join ipf.propostaFornecedor pf" +
                "  inner join ip.itemPregaoLanceVencedor iplv" +
                "  inner join iplv.lancePregao lpv" +
                (uo != null ? " inner join lic.unidades licUnidades " : "") +
                "       where lic = :licitacao " +
                "         and lpv.propostaFornecedor = pf " +
                "         and iplv.status = :statusVencedorPregao" +
                "         and pf.fornecedor = :contratado " +
                "         and ipc.situacaoItemProcessoDeCompra in :situacaoIpc " +
                (uo != null ?
                    " and to_date(:dataReferencia, 'dd/MM/yyyy') between licUnidades.inicioVigencia and coalesce(licUnidades.fimVigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))" +
                        " and licUnidades.unidadeAdministrativa = :uo " : "");
            hql += status.equals(TipoClassificacaoFornecedor.getTodosTipos()) ? " and (ip.statusFornecedorVencedor in :statusFornecedor or ip.statusFornecedorVencedor is null)" : " and ip.statusFornecedorVencedor in :statusFornecedor";
        } else {
            hql = " select ipf from ItemPregao ip" +
                "  inner join ip.pregao p" +
                "  inner join p.licitacao lic" +
                "  inner join ip.itemPregaoLoteProcesso iplp" +
                "  inner join iplp.loteProcessoDeCompra lpc" +
                "  inner join lpc.itensProcessoDeCompra ipc" +
                "  inner join ipc.itensPropostaFornecedor ipf" +
                "  inner join ipf.propostaFornecedor pf" +
                "  inner join ip.itemPregaoLanceVencedor iplv" +
                "  inner join iplv.lancePregao lpv" +
                (uo != null ? " inner join lic.unidades licUnidades " : "") +
                "       where lic = :licitacao" +
                "         and lpv.propostaFornecedor = pf" +
                "         and iplv.status = :statusVencedorPregao" +
                "         and pf.fornecedor = :contratado" +
                "         and ipc.situacaoItemProcessoDeCompra in :situacaoIpc " +
                (uo != null ?
                    " and to_date(:dataReferencia, 'dd/MM/yyyy') between licUnidades.inicioVigencia and coalesce(licUnidades.fimVigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))" +
                        " and licUnidades.unidadeAdministrativa = :uo " : "");
            hql += status.equals(TipoClassificacaoFornecedor.getTodosTipos()) ? " and (ip.statusFornecedorVencedor in :statusFornecedor or ip.statusFornecedorVencedor is null)" : " and ip.statusFornecedorVencedor in :statusFornecedor";
        }
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", l);
        q.setParameter("contratado", p);
        q.setParameter("statusFornecedor", status);
        q.setParameter("situacaoIpc", situacoesIpc);
        q.setParameter("statusVencedorPregao", StatusLancePregao.VENCEDOR);
        if (uo != null) {
            q.setParameter("uo", uo);
            q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        }
        return q.getResultList();
    }

    public List<ItemPropostaFornecedor> getItensVencidosPeloFornecedorPorStatusNoCertame(Licitacao l, Pessoa p, List<TipoClassificacaoFornecedor> status, SituacaoItemProcessoDeCompra situacaoItemProcessoCompra) {
        return getItensVencidosPeloFornecedorPorStatusNoCertame(l, p, status, situacaoItemProcessoCompra, null, null);
    }

    public List<ItemPropostaFornecedor> getItensVencidosPeloFornecedorPorStatusNoCertame(Licitacao l, Pessoa p, List<TipoClassificacaoFornecedor> status, SituacaoItemProcessoDeCompra situacaoItemProcessoCompra, UnidadeOrganizacional uo, Date dataReferencia) {
        String hql = "";
        if (l.getTipoApuracao().equals(TipoApuracaoLicitacao.ITEM)) {
            hql = "select distinct itemprop"
                + " from ItemCertame item"
                + " inner join item.certame certame"
                + " inner join certame.licitacao lic"
                + " inner join lic.fornecedores lf"
                + "  left join item.itemCertameItemProcesso itemce"
                + "  left join itemce.itemProcessoDeCompra ipc"
                + "  left join itemce.itemPropostaVencedor itemprop"
                + " inner join itemprop.propostaFornecedor prop"
                + (uo != null ? " inner join lic.unidades licUnidades " : "")
                + "      where item.situacaoItemCertame = :statusVencedor"
                + "        and lf.tipoClassificacaoFornecedor in :status"
                + "        and ipc.situacaoItemProcessoDeCompra = :situacaoItemProcessoCompra"
                + "        and prop.fornecedor = :contratado"
                + "        and lic = :licitacao" +
                (uo != null ?
                    " and to_date(:dataReferencia, 'dd/MM/yyyy') between licUnidades.inicioVigencia and coalesce(licUnidades.fimVigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))" +
                        " and licUnidades.unidadeAdministrativa = :uo " : "");
        } else {
            hql = "select distinct itemlote from ItemCertame item, ItemPropostaFornecedor itemlote"
                + " inner join item.certame certame"
                + " inner join certame.licitacao lic"
                + " inner join lic.fornecedores lf"
                + " inner join itemlote.propostaFornecedor prop"
                + " left join item.itemCertameLoteProcesso lotece"
                + " left join lotece.loteProcessoDeCompra lpc "
                + " left join lpc.itensProcessoDeCompra ipc "
                + " left join lotece.lotePropFornecedorVencedor loteprop"
                + (uo != null ? " inner join lic.unidades licUnidades " : "")
                + "     where item.situacaoItemCertame = :statusVencedor"
                + "       and itemlote.lotePropostaFornecedor = loteprop"
                + "        and lf.tipoClassificacaoFornecedor in :status"
                + "        and ipc.situacaoItemProcessoDeCompra = :situacaoItemProcessoCompra"
                + "        and prop.fornecedor = :contratado"
                + "        and lic = :licitacao" +
                (uo != null ?
                    " and to_date(:dataReferencia, 'dd/MM/yyyy') between licUnidades.inicioVigencia and coalesce(licUnidades.fimVigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))" +
                        " and licUnidades.unidadeAdministrativa = :uo " : "");
        }
        Query q = em.createQuery(hql);
        q.setParameter("contratado", p);
        q.setParameter("licitacao", l);
        q.setParameter("status", status);
        q.setParameter("statusVencedor", SituacaoItemCertame.VENCEDOR);
        q.setParameter("situacaoItemProcessoCompra", situacaoItemProcessoCompra);
        if (uo != null) {
            q.setParameter("uo", uo);
            q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        }
        return q.getResultList();
    }

    public List<ItemPropostaFornecedor> getItensVencidosPeloFornecedorPorStatusNoMapaComparativoTecnicaPreco(Licitacao l, Pessoa p, List<TipoClassificacaoFornecedor> status, SituacaoItemProcessoDeCompra situacaoItemProcessoCompra) {
        return getItensVencidosPeloFornecedorPorStatusNoMapaComparativoTecnicaPreco(l, p, status, situacaoItemProcessoCompra, null, null);
    }

    public List<ItemPropostaFornecedor> getItensVencidosPeloFornecedorPorStatusNoMapaComparativoTecnicaPreco(Licitacao l, Pessoa p, List<TipoClassificacaoFornecedor> status, SituacaoItemProcessoDeCompra situacaoItemProcessoCompra, UnidadeOrganizacional uo, Date dataReferencia) {
        String hql = "";
        if (l.getTipoApuracao().equals(TipoApuracaoLicitacao.ITEM)) {
            hql = "select distinct itemprop"
                + " from ItemMapaComparativoTecnicaPreco item"
                + " inner join item.mapaComTecnicaPreco certame"
                + " inner join certame.licitacao lic"
                + " inner join lic.fornecedores lf"
                + "  left join item.itemMapaComparativoTecnicaPrecoItemProcesso itemce"
                + "  left join itemce.itemProcessoDeCompra ipc "
                + "  left join itemce.itemPropostaVencedor itemprop"
                + " inner join itemprop.propostaFornecedor prop"
                + (uo != null ? " inner join lic.unidades licUnidades " : "")
                + "      where item.situacaoItem = :statusVencedor"
                + "        and lf.tipoClassificacaoFornecedor in :status"
                + "        and ipc.situacaoItemProcessoDeCompra = :situacaoItemProcessoCompra"
                + "        and prop.fornecedor = :contratado"
                + "        and lic = :licitacao" +
                (uo != null ?
                    " and to_date(:dataReferencia, 'dd/MM/yyyy') between licUnidades.inicioVigencia and coalesce(licUnidades.fimVigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))" +
                        " and licUnidades.unidadeAdministrativa = :uo " : "");
        } else {
            hql = "select distinct itemlote from ItemMapaComparativoTecnicaPreco item, ItemPropostaFornecedor itemlote"
                + " inner join item.mapaComTecnicaPreco certame"
                + " inner join certame.licitacao lic"
                + " inner join lic.fornecedores lf"
                + " inner join itemlote.propostaFornecedor prop"
                + " left join item.itemMapaComparativoTecnicaPrecoLoteProcesso lotece"
                + " left join lotece.lotePropostaVencedor loteprop"
                + " left join lotece.loteProcessoDeCompra tpc "
                + " left join tpc.itensProcessoDeCompra ipc "
                + (uo != null ? " inner join lic.unidades licUnidades " : "")
                + "     where item.situacaoItem = :statusVencedor"
                + "       and itemlote.lotePropostaFornecedor = loteprop"
                + "        and lf.tipoClassificacaoFornecedor in :status"
                + "        and ipc.situacaoItemProcessoDeCompra = :situacaoItemProcessoCompra"
                + "        and prop.fornecedor = :contratado"
                + "        and lic = :licitacao" +
                (uo != null ?
                    " and to_date(:dataReferencia, 'dd/MM/yyyy') between licUnidades.inicioVigencia and coalesce(licUnidades.fimVigencia, to_date(:dataReferencia, 'dd/MM/yyyy'))" +
                        " and licUnidades.unidadeAdministrativa = :uo " : "");
        }
        Query q = em.createQuery(hql);
        q.setParameter("contratado", p);
        q.setParameter("licitacao", l);
        q.setParameter("status", status);
        q.setParameter("statusVencedor", SituacaoItemCertame.VENCEDOR);
        q.setParameter("situacaoItemProcessoCompra", situacaoItemProcessoCompra);
        if (uo != null) {
            q.setParameter("uo", uo);
            q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        }
        return q.getResultList();
    }

    public Boolean isItemVencidoPorFornecedorStatusPregao(Licitacao licitacao,
                                                          Pessoa p, List<TipoClassificacaoFornecedor> status,
                                                          SituacaoItemProcessoDeCompra situacaoItemProcessoDeCompra,
                                                          ItemProcessoDeCompra itemProcessoDeCompra) {
        String hql = "";
        if (licitacao.isTipoApuracaoPrecoItem()) {
            hql = " select ipf from ItemPregao ip" +
                "  inner join ip.pregao p" +
                "  inner join p.licitacao lic" +
                "  inner join ip.itemPregaoItemProcesso ipip" +
                "  inner join ipip.itemProcessoDeCompra ipc" +
                "  inner join ipc.itensPropostaFornecedor ipf" +
                "  inner join ipf.propostaFornecedor pf" +
                "  inner join ip.itemPregaoLanceVencedor iplv" +
                "  inner join iplv.lancePregao lpv" +
                "       where lic = :licitacao" +
                "         and lpv.propostaFornecedor = pf" +
                "         and lpv.statusLancePregao = :statusVencedor" +
                "         and ipc.situacaoItemProcessoDeCompra = :situacaoItemProcessoCompra" +
                "         and pf.fornecedor = :contratado " +
                "         and ipc = :itemProcessoCompra ";
            hql += status.equals(TipoClassificacaoFornecedor.getTodosTipos())
                ? " and (ip.statusFornecedorVencedor in :status or ip.statusFornecedorVencedor is null)"
                : " and ip.statusFornecedorVencedor in :status";
        } else {
            hql = " select ipf from ItemPregao ip" +
                "  inner join ip.pregao p" +
                "  inner join p.licitacao lic" +
                "  inner join ip.itemPregaoLoteProcesso iplp" +
                "  inner join iplp.loteProcessoDeCompra lpc" +
                "  inner join lpc.itensProcessoDeCompra ipc" +
                "  inner join ipc.itensPropostaFornecedor ipf" +
                "  inner join ipf.propostaFornecedor pf" +
                "  inner join ip.itemPregaoLanceVencedor iplv" +
                "  inner join iplv.lancePregao lpv" +
                "       where lic = :licitacao" +
                "         and lpv.propostaFornecedor = pf" +
                "         and lpv.statusLancePregao = :statusVencedor" +
                "         and ipc.situacaoItemProcessoDeCompra = :situacaoItemProcessoCompra" +
                "         and pf.fornecedor = :contratado " +
                "         and ipc = :itemProcessoCompra ";
            hql += status.equals(TipoClassificacaoFornecedor.getTodosTipos())
                ? " and (ip.statusFornecedorVencedor in :status or ip.statusFornecedorVencedor is null)"
                : " and ip.statusFornecedorVencedor in :status";
        }
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        q.setParameter("contratado", p);
        q.setParameter("status", status);
        q.setParameter("statusVencedor", StatusLancePregao.VENCEDOR);
        q.setParameter("situacaoItemProcessoCompra", situacaoItemProcessoDeCompra);
        q.setParameter("itemProcessoCompra", itemProcessoDeCompra);
        return !q.getResultList().isEmpty();
    }

    private String setNaturezasDoProcedimentoParaHql(String hql, TipoNaturezaDoProcedimentoLicitacao... tipos) {
        Integer i = 0;
        hql += " and (";
        for (TipoNaturezaDoProcedimentoLicitacao tpNat : tipos) {
            hql += "l.naturezaDoProcedimento = :tpNat" + i + " or ";
            i++;
        }
        hql = hql.substring(0, hql.length() - 3);
        hql += ")";
        return hql;
    }

    private Query setParametrosParaQuery(TipoNaturezaDoProcedimentoLicitacao[] tipos, Query q) {
        Integer i = 0;
        for (TipoNaturezaDoProcedimentoLicitacao tpNat : tipos) {
            q.setParameter("tpNat" + i, tpNat);
            i++;
        }
        return q;
    }

    public List<Licitacao> autoCompletarLicitacoesHomologadasPelaNaturezaProcedimento(String parte, TipoNaturezaDoProcedimentoLicitacao... tipos) {
        String[] p = parte.split(" ");
        List<String> palavras = Arrays.asList(p);

        String hql = "select new Licitacao(l.id, l.numero, l.processoDeCompra, l.exercicio, l.contratos.size, s) from Licitacao l"
            + " inner join l.processoDeCompra as p"
            + " inner join l.listaDeStatusLicitacao s"
            + " where maxelement(l.listaDeStatusLicitacao) = s";
        hql = setNaturezasDoProcedimentoParaHql(hql, tipos);
        hql += " and ";
        hql += complementaWhereConsultaLicitacao(palavras);

        Query q = em.createQuery(hql);
        q.setMaxResults(10);
        q = setParametrosParaQuery(tipos, q);
        for (String s : palavras) {
            q.setParameter("parte" + palavras.indexOf(s), "%" + s.toLowerCase() + "%");
        }

        try {
            return q.getResultList();
        } catch (RuntimeException re) {
            return new ArrayList<>();
        }
    }

    public List<Licitacao> buscarLicitacoes(String parte) {
        String hql = "select l from Licitacao l"
            + "  inner join l.listaDeStatusLicitacao situacoes "
            + " where (lower(l.resumoDoObjeto) like :parte "
            + "   or to_char(l.numeroLicitacao) like :parte "
            + "   or to_char(l.exercicio.ano) like :parte)"
            + adicionarCondicoesStatusLicitacaoHQL("l", "situacoes")
            + " order by  l.numeroLicitacao desc";


        Query q = em.createQuery(hql);
        q.setMaxResults(20);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return new ArrayList<>();
        }
    }

    public List<Licitacao> buscarLicitacaoHomologadaPregaoRealizado(String parte) {
        String sql = "  " +
            " select lic.* from licitacao lic " +
            "  inner join statuslicitacao st on st.licitacao_id = lic.id " +
            "  inner join exercicio e on e.id = lic.exercicio_id " +
            "  inner join pregao p on p.licitacao_id = lic.id " +
            "  inner join processodecompra processo on lic.processodecompra_id = processo.id " +
            " where st.datastatus = (select max(status.datastatus) from statuslicitacao status where status.licitacao_id = lic.id) " +
            "   and st.tiposituacaolicitacao = :statusHomologada " +
            "   and (lower(lic.resumodoobjeto) like :filter " +
            "    or lower(processo.numero) like :filter " +
            "    or to_char(lic.numerolicitacao) like :filter " +
            "    or to_char(lic.numerolicitacao) || '/' || to_char(e.ano) like :filter " +
            "    or replace(to_char(lic.numerolicitacao) || '/' || to_char(e.ano), '/', '') like :filter) " +
            " order by e.ano desc, lic.numerolicitacao desc ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("filter", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("statusHomologada", TipoSituacaoLicitacao.HOMOLOGADA.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        List resultList = q.getResultList();
        return resultList.isEmpty() ? new ArrayList<Licitacao>() : resultList;
    }

    public List<Licitacao> buscarLicitacaoPorNumeroModalidadeOrNumeroLicitacao(String filter) {

        String sql = "  " +
            " select  " +
            "  lic.*  " +
            " from licitacao lic  " +
            "  inner join exercicio e on e.id = lic.exercicio_id  " +
            "  inner join processodecompra processo on lic.processodecompra_id = processo.id  " +
            "    where (lower(lic.resumodoobjeto) like :filter " +
            "            or lower(processo.numero) like :filter " +
            "            or to_char(lic.numerolicitacao) like :filter " +
            "            or to_char(lic.numerolicitacao) || '/' || to_char(e.ano) like :filter " +
            "            or replace(to_char(lic.numerolicitacao) || '/' || to_char(e.ano), '/', '') like :filter " +
            "            ) " +
            "order by e.ano desc, lic.numerolicitacao desc ";

        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("filter", "%" + filter.trim().toLowerCase() + "%");
        q.setMaxResults(50);
        return q.getResultList().isEmpty() ? new ArrayList<Licitacao>() : q.getResultList();
    }

    public List<Licitacao> listarLicitacaoComDataAbertura(String parte) {
        String hql = "select l from Licitacao l"
            + " where (lower(l.processoDeCompra.descricao) like :parte "
            + "    or l.numero like :parte "
            + "    or l.exercicio.ano like :parte)"
            + "    and l.abertaEm is not null ";


        Query q = em.createQuery(hql);
        q.setMaxResults(20);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        try {
            return q.getResultList();
        } catch (RuntimeException re) {
            return new ArrayList<>();
        }
    }

    public List<StatusLicitacao> recuperarListaDeStatus(Licitacao licitacao) {
        String hql = "  from StatusLicitacao sl "
            + "    where sl.licitacao = :param "
            + " order by sl.numero";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);

        return q.getResultList();
    }

    public List<ParecerLicitacao> recuperarListaDeParecer(Licitacao licitacao) {
        String hql = "from ParecerLicitacao pl where pl.licitacao = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);

        return q.getResultList();
    }

    public List<RecursoLicitacao> recuperarListaDeRecursos(Licitacao licitacao) {
        String hql = "from RecursoLicitacao rl where rl.licitacao = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);

        return q.getResultList();
    }

    public List<PublicacaoLicitacao> recuperarListaDePublicacoes(Licitacao licitacao) {
        String hql = "from PublicacaoLicitacao pl where pl.licitacao = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);

        return q.getResultList();
    }

    public void salvar(Licitacao licitacao, List<ParecerLicitacao> listaDeParecerLicitacaoRemover, List<RecursoLicitacao> listaDeRecursoLicitacaoRemover, List<PublicacaoLicitacao> listaDePublicacaoLicitacaoRemover) {
        if (licitacao.getId() != null) {
            licitacao = em.merge(licitacao);
        }

        em.persist(licitacao);

        if (!listaDeParecerLicitacaoRemover.isEmpty()) {
            for (ParecerLicitacao parecerLicitacao : listaDeParecerLicitacaoRemover) {
                em.remove(em.merge(parecerLicitacao));
            }
        }

        if (!listaDeRecursoLicitacaoRemover.isEmpty()) {
            for (RecursoLicitacao recursoLicitacao : listaDeRecursoLicitacaoRemover) {
                em.remove(em.merge(recursoLicitacao));
            }
        }

        if (!listaDePublicacaoLicitacaoRemover.isEmpty()) {
            for (PublicacaoLicitacao publicacaoLicitacao : listaDePublicacaoLicitacaoRemover) {
                em.remove(em.merge(publicacaoLicitacao));
            }
        }
    }

    public void salvar(Licitacao licitacao, List<RecursoLicitacao> listaDeRecursoLicitacaoRemover, List<PublicacaoLicitacao> listaDePublicacaoLicitacaoRemover) {
        if (licitacao.getId() != null) {
            licitacao = em.merge(licitacao);
        }

        em.persist(licitacao);

        if (!listaDeRecursoLicitacaoRemover.isEmpty()) {
            for (RecursoLicitacao recursoLicitacao : listaDeRecursoLicitacaoRemover) {
                em.remove(em.merge(recursoLicitacao));
            }
        }

        if (!listaDePublicacaoLicitacaoRemover.isEmpty()) {
            for (PublicacaoLicitacao publicacaoLicitacao : listaDePublicacaoLicitacaoRemover) {
                em.remove(em.merge(publicacaoLicitacao));
            }
        }
    }

    public boolean validaExclusao(Licitacao licitacao) {
        String hql = "from PropostaFornecedor pf "
            + "  where pf.licitacao = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);

        if (q.getResultList().isEmpty()) {
            return true;
        }

        return false;
    }

    public Pregao licitacaoPossuiVinculoComPregao(Licitacao licitacao) {
        String hql = " from Pregao p"
            + "   where p.licitacao = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);

        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            Pregao pregao = (Pregao) q.getSingleResult();

            pregao.getListaDeItemPregao().size();

            return pregao;
        }
    }

    public Certame licitacaoPossuiVinculoComMapaComparativo(Licitacao licitacao) {
        String hql = " from Certame c"
            + "   where c.licitacao = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);

        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            Certame certame = (Certame) q.getSingleResult();

            certame.getListaItemCertame().size();

            return certame;
        }
    }

    public List<DoctoLicitacao> recuperaListaDeDocumentos(Licitacao licitacao) {
        String hql = "from DoctoLicitacao docto where docto.licitacao = :licitacao";
        Query query = em.createQuery(hql);
        query.setParameter("licitacao", licitacao);
        try {
            List<DoctoLicitacao> doctos = (List<DoctoLicitacao>) query.getResultList();
            for (DoctoLicitacao docto : doctos) {
                docto.getListaDeVersoes().size();
            }
            return doctos;
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    private void validarDocumentoObrigatorioAdicionado(ValidacaoException ve, Licitacao licitacao) {
        if (licitacao.getDocumentosProcesso() == null || licitacao.getDocumentosProcesso().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar pelo menos um documento obrigatório.");
        }
    }


    private void validarDataDeAberturaAposAdicionarParecerDoTipoJuridicoEdital(ValidacaoException ve, Licitacao licitacao) {
        if (parecerLicitacaoFacade.recuperarParecerDoTipoJuridicoEdital(licitacao) && licitacao.getAbertaEm() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esta licitação possui parecer do tipo jurídico edital. Portanto, o campo data de abertura na aba publicação é obrigatório.");
        }
    }

    public void validaPublicacaoAdicionadaAposAdicionarParecerDoTipoJuridicoEdital(ValidacaoException ve, Licitacao licitacao) {
        if (parecerLicitacaoFacade.recuperarParecerDoTipoJuridicoEdital(licitacao) && (licitacao.getPublicacoes() == null || licitacao.getPublicacoes().isEmpty())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Esta licitação possui um parecer do tipo jurídico edital. Portanto é obrigatório adicionar pelo menos uma publicação.");
        }
    }

    public void validarNumeroDaModalidadeAposAdicionarParecerDoTipoJuridicoEdital(ValidacaoException ve, Licitacao licitacao) {
        if (parecerLicitacaoFacade.recuperarParecerDoTipoJuridicoEdital(licitacao) && licitacao.getNumero() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo número da modalidade é obrigatório para licitação com parecer do tipo jurídico edital.");
        }
    }

    private void validarMembrosSelecionados(ValidacaoException ve, Licitacao licitacao) {
        int quantidadeMembros = 0;
        if (licitacao.getListaDeLicitacaoMembroComissao() != null) {
            quantidadeMembros = licitacao.getListaDeLicitacaoMembroComissao().size();
        }
        if (licitacao.isPregao() && licitacao.getPregoeiroResponsavel() != null && licitacao.isPregoeiroMembroComissao()) {
            quantidadeMembros -= 1;
        }
        if (quantidadeMembros < TRES && !licitacao.isPregao()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar pelo menos três membros da comissão.");
        }
        if (quantidadeMembros < DOIS && licitacao.isPregao()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É obrigatório adicionar pelo menos dois membros da comissão com exceção do pregoeiro. ");
        }
    }

    public void validarDataDeValidadeDaComissao(ValidacaoException ve, Licitacao licitacao) {
        if (licitacao.getComissao().getFinalVigencia() != null
            && licitacao.getComissao().getFinalVigencia().before(sistemaFacade.getDataOperacao())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A comissão selecionada está vencida. Data final de vigência da comissão: "
                + DataUtil.getDataFormatada(licitacao.getComissao().getFinalVigencia()));
        }
    }

    public void carregarListas(Licitacao licitacao) {
        licitacao.setDocumentosProcesso(recuperaDoctosHabLicitacao(licitacao));
        licitacao.setListaDeDocumentos(recuperaListaDeDocumentos(licitacao));
        licitacao.setFornecedores(recuperarListaDeLicitacaoFornecedor(licitacao));
        licitacao.setListaDeLicitacaoMembroComissao(recuperarListaDeLicitacaoMembroComissao(licitacao));
        licitacao.setPareceres(recuperarListaDeParecer(licitacao));
        licitacao.setPublicacoes(recuperarListaDePublicacoes(licitacao));
        licitacao.setListaDeRecursoLicitacao(recuperarListaDeRecursos(licitacao));
        licitacao.setListaDeStatusLicitacao(recuperarListaDeStatus(licitacao));
        licitacao.setComissao(recuperarComissao(licitacao));
    }

    private void validarStatusLicitacaoEAlteracoes(ValidacaoException ve, Licitacao licitacao) {
        if (isLicitacaoComStatusQueNaoPermiteAlteracao(licitacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A licitação está " + licitacao.getStatusAtual().getTipoSituacaoLicitacao().getDescricao() + " e não pode ser alterada.");
        }
    }

    private boolean isLicitacaoComStatusQueNaoPermiteAlteracao(Licitacao licitacao) {
        StatusLicitacao status = licitacao.getStatusAtualLicitacao();
        if ( licitacao.isStatusLicitacaoAdjucada(status)
            || licitacao.isStatusLicitacaoRevogada(status)
            || licitacao.isStatusLicitacaoDeserta(status)
            || licitacao.isStatusLicitacaoCancelada(status)
            || licitacao.isStatusLicitacaoFracassada(status)
            || licitacao.isStatusLicitacaoAnulada(status)
            || licitacao.isStatusLicitacaoEmRecurso(status)) {
            return true;
        }
        return false;
    }

    private void validaDataDeAberturaAnteriorDataEmissao(ValidacaoException ve, Licitacao licitacao) {
        if (licitacao.getAbertaEm() != null) {
            if (licitacao.getAbertaEm().before(licitacao.getEmitidaEm())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de abertura da licitação deve ser igual ou posterior a data de emissão do edital.");
            }
            if (licitacao.getEncerradaEm().before(licitacao.getAbertaEm())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data e hora do encerramento da licitação deve ser igual ou posterior a data e hora da abertura.");
            }
        }
    }

    public void validarDataDeAberturaReferenteAoPrazoPermitido(ValidacaoException ve, Licitacao licitacao) {
        Date prazoMinimoAbertura = licitacao.getPrazoMinimoParaAberturaDaLicitacao(licitacao, feriadoFacade);
        if (licitacao.getAbertaEm() != null && prazoMinimoAbertura != null) {
            if (licitacao.getAbertaEm().before(prazoMinimoAbertura)) {
                licitacao.setAbertaEm(prazoMinimoAbertura);
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de abertura da licitação deve ser igual ou posterior a data " +
                    DataUtil.getDataFormatada(prazoMinimoAbertura));
            }
        }
    }

    public void validarRegrasNegocioLicitacao(Licitacao licitacao) {
        ValidacaoException ve = new ValidacaoException();

        Util.validarCampos(licitacao);

        getDocumentosNecessariosAnexoFacade().validarSeTodosOsTipoDeDocumentoForamAnexados
            (TipoFuncionalidadeParaAnexo.LICITACAO, licitacao.getDetentorDocumentoLicitacao() != null ?
                licitacao.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList() : Lists.<ITipoDocumentoAnexo>newArrayList());
        validarDocumentoObrigatorioAdicionado(ve, licitacao);
        validarMembrosSelecionados(ve, licitacao);
        validarDataDeValidadeDaComissao(ve, licitacao);
        validarStatusLicitacaoEAlteracoes(ve, licitacao);
        validarNumeroDaModalidadeAposAdicionarParecerDoTipoJuridicoEdital(ve, licitacao);
        validaPublicacaoAdicionadaAposAdicionarParecerDoTipoJuridicoEdital(ve, licitacao);
        validarDataDeAberturaAposAdicionarParecerDoTipoJuridicoEdital(ve, licitacao);
        validaDataDeAberturaAnteriorDataEmissao(ve, licitacao);
        ve.lancarException();
    }

    public Licitacao salvarLicitacao(Licitacao licitacao) {
        validarRegrasNegocioLicitacao(licitacao);
        gerarNumeroLicitacao(licitacao);
        validarNumeroLicitacao(licitacao);
        salvarRepresentanteLicitacao(licitacao);
        criarUnidadeLicitacao(licitacao);
        licitacao = em.merge(licitacao);
        salvarDoctosLicitacao(licitacao);
        salvarPortal(licitacao);
        return em.merge(licitacao);
    }

    private void criarUnidadeLicitacao(Licitacao licitacao) {
        licitacao.setUnidades(Lists.newArrayList());
        UnidadeLicitacao unidadeLicitacao = new UnidadeLicitacao();
        unidadeLicitacao.setLicitacao(licitacao);
        unidadeLicitacao.setInicioVigencia(licitacao.getEmitidaEm());
        unidadeLicitacao.setUnidadeAdministrativa(licitacao.getProcessoDeCompra().getUnidadeOrganizacional());
        licitacao.setUnidades(Lists.<UnidadeLicitacao>newArrayList(unidadeLicitacao));
    }

    private void salvarRepresentanteLicitacao(Licitacao licitacao) {
        for (LicitacaoFornecedor licitacaoFornecedor : licitacao.getFornecedores()) {
            if (licitacaoFornecedor.getEmpresa() instanceof PessoaJuridica) {
                if (licitacaoFornecedor.getRepresentante().getId() != null) {
                    getRepresentanteLicitacaoFacade().salvar(licitacaoFornecedor.getRepresentante());
                } else {
                    getRepresentanteLicitacaoFacade().salvarNovo(licitacaoFornecedor.getRepresentante());
                }
            } else {
                licitacaoFornecedor.setRepresentante(null);
            }
        }
    }

    public Licitacao salvarRetornando(Licitacao licitacao) {
        return em.merge(licitacao);
    }

    private void validarNumeroLicitacao(Licitacao licitacao) {
        if (hasLicitacaoComNumeroAndExercicio(licitacao)) {
            ValidacaoException ve = new ValidacaoException();
            ve.adicionarMensagemDeOperacaoNaoPermitida("O número " + licitacao.getNumeroLicitacao() + " para o exercício de " +
                licitacao.getExercicio().getAno() + " já encontra-se registrado.");
            ve.lancarException();
        }
    }

    private boolean hasLicitacaoComNumeroAndExercicio(Licitacao licitacao) {
        String sql = " select l.id " +
            "   from Licitacao l " +
            " where l.exercicio_id = :exercicio_id " +
            "   and l.numerolicitacao = :numero " +
            "   and l.modalidadelicitacao in (:modalidade)";
        if (licitacao.getId() != null) {
            sql += " and l.id <> :id ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("exercicio_id", licitacao.getExercicio().getId());
        q.setParameter("numero", licitacao.getNumeroLicitacao());
        q.setParameter("modalidade", Util.getListOfEnumName(ModalidadeLicitacao.getModalidadesLicitacao()));
        if (licitacao.getId() != null) {
            q.setParameter("id", licitacao.getId());
        }
        return !q.getResultList().isEmpty();
    }

    private void gerarNumeroLicitacao(Licitacao licitacao) {
        if (licitacao.getId() == null && (licitacao.getNumeroLicitacao() == null || licitacao.getNumeroLicitacao() <= 0)) {
            licitacao.setNumeroLicitacao(singletonGeradorCodigoAdministrativo.getProximoCodigoPorFuncionalidade(
                null, licitacao.getExercicio().getId(),
                FuncionalidadeAdministrativo.LICITACAO));
        }
    }

    private void salvarDoctosLicitacao(Licitacao licitacao) {
        List<DoctoLicitacao> docs = new ArrayList<>();

        for (DoctoLicitacao doc : licitacao.getListaDeDocumentos()) {
            doc.setLicitacao(licitacao);
            docs.add(em.merge(doc));
        }

        licitacao.getListaDeDocumentos().clear();
        licitacao.getListaDeDocumentos().addAll(docs);

        for (DoctoLicitacao docto : licitacao.getListaDeDocumentos()) {
            for (VersaoDoctoLicitacao versao : docto.getListaDeVersoes()) {
                versao.setDoctoLicitacao(docto);
                em.persist(versao);
            }
        }
    }

    public void salvarPortal(Licitacao licitacao) {
        if (licitacao.getAbertaEm() != null) {
            portalTransparenciaNovoFacade.salvarPortal(new LicitacaoPortal(licitacao));
        }
    }

    public List<DoctoHabLicitacao> recuperaDoctosHabLicitacao(Licitacao licitacao) {
        String hql = "select doc from DoctoHabLicitacao doc"
            + " inner join doc.licitacao lic"
            + " where lic= :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        try {
            return (List<DoctoHabLicitacao>) q.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public List<Licitacao> buscarLicitacaoPorTipoApuracaoAndTipoSolicitacao(TipoApuracaoLicitacao tipoApuracaoLicitacao, String parte) {
        String sql = "select lic.* from Licitacao lic " +
            "           inner join processodecompra pro on pro.id = lic.processodecompra_id " +
            "           inner join statuslicitacao status on status.licitacao_id = lic.id " +
            "           inner join exercicio ex on ex.id = lic.exercicio_id "
            + "       where lic.tipoApuracao = :tipoApuracaoLicitacao "
            + "        and lic.modalidadeLicitacao in (:modalidade)"
            + "        and lic.naturezadoprocedimento in (:natureza)"
            + "        and (lower(pro.descricao) like :parte "
            + "         or lic.numeroLicitacao like :parte "
            + "         or to_char(lic.numerolicitacao) || '/' || to_char(ex.ano) like :parte "
            + "         or ex.ano like :parte) "
            + "        and not exists (select p.* from Pregao p where p.licitacao_id = lic.id) "
            + "         and status.id = (select max(s.id) from StatusLicitacao s where s.licitacao_id = status.licitacao_id)"
            + "         and status.tipoSituacaoLicitacao not in (" + StatusLicitacao.concatenarListaStatus() + ") "
            + "       order by ex.ano desc, lic.numeroLicitacao desc ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");
        q.setParameter("tipoApuracaoLicitacao", tipoApuracaoLicitacao.name());
        q.setParameter("modalidade", Lists.newArrayList(new String[]{ModalidadeLicitacao.PREGAO.name(), ModalidadeLicitacao.RDC.name()}));
        q.setParameter("natureza", TipoNaturezaDoProcedimentoLicitacao.getTiposNaturezaAsString(TipoNaturezaDoProcedimentoLicitacao.getNaturezaRDCAndPregao()));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public Licitacao recuperarLicitacaoDoProcessoCompra(ProcessoDeCompra processoDeCompra) {
        String sql = " " +
            " select " +
            "   lic.* " +
            " from licitacao lic " +
            " where lic.processodecompra_id = :param";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("param", processoDeCompra.getId());
        q.setMaxResults(1);
        try {
            return (Licitacao) q.getSingleResult();
        } catch (NoResultException ex) {
            return null;
        }
    }

    public List<LicitacaoMembroComissao> recuperarListaDeLicitacaoMembroComissao(Licitacao licitacao) {
        String hql = "from LicitacaoMembroComissao lmc where lmc.licitacao = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);

        return q.getResultList();
    }

    public List<LicitacaoFornecedor> recuperarListaDeLicitacaoFornecedor(Licitacao licitacao) {
        String hql = "from LicitacaoFornecedor lf where lf.licitacao = :param";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);

        List<LicitacaoFornecedor> retorno = q.getResultList();
        retorno.size();
        if (!retorno.isEmpty()) {
            for (LicitacaoFornecedor lf : retorno) {
                lf.getDocumentosFornecedor().size();
            }
        }

        return retorno;
    }

    public List<ItemPropostaFornecedor> recuperaItensProposta(Licitacao licitacao) {
        String hql = "select item from ItemPropostaFornecedor item"
            + " inner join item.propostaFornecedor proposta"
            + " inner join proposta.licitacao lic"
            + " where lic= :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        try {
            return (List<ItemPropostaFornecedor>) q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<ItemPropostaFornecedor> buscarItensPropostaFornecedor(Licitacao licitacao, Pessoa fornecedor) {
        String hql = "select item from ItemPropostaFornecedor item"
            + " inner join item.propostaFornecedor proposta"
            + " inner join proposta.licitacao lic"
            + " where lic= :licitacao"
            + "   and proposta.fornecedor = :fornecedor  ";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        q.setParameter("fornecedor", fornecedor);
        try {
            return (List<ItemPropostaFornecedor>) q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<LotePropostaFornecedor> buscarLotesPropostaFornecedor(Licitacao licitacao, Pessoa fornecedor) {
        String hql = "select lote from LotePropostaFornecedor lote"
            + " inner join lote.propostaFornecedor proposta"
            + " inner join proposta.licitacao lic"
            + " where lic= :licitacao"
            + "   and proposta.fornecedor = :fornecedor  ";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        q.setParameter("fornecedor", fornecedor);
        try {
            return (List<LotePropostaFornecedor>) q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<ItemProcessoDeCompra> recuperaItensProcessoDeCompra(Licitacao licitacao) {
        String hql = "select item from Licitacao lic"
            + " inner join lic.processoDeCompra processo"
            + " inner join processo.lotesProcessoDeCompra lote"
            + " inner join lote.itensProcessoDeCompra item"
            + " where lic = :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        try {
            return (List<ItemProcessoDeCompra>) q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<LotePropostaFornecedor> recuperaLotesDaProposta(Licitacao licitacao) {
        String hql = "select lote from LotePropostaFornecedor lote"
            + " inner join lote.propostaFornecedor proposta"
            + " inner join proposta.licitacao lic"
            + " where lic= :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        try {
            return (List<LotePropostaFornecedor>) q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public LotePropostaFornecedor recuperarLotePropostaFornecedor(Long id) {
        LotePropostaFornecedor lotePropostaFornecedor = em.find(LotePropostaFornecedor.class, id);
        lotePropostaFornecedor.getItens().size();
        return lotePropostaFornecedor;
    }


    public void validaExclusaoLicitacaoHomologada(Licitacao licitacao) {
        String hql = "select status from StatusLicitacao status "
            + " where status.licitacao = :licitacao "
            + " order by numero desc";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        q.setMaxResults(1);
        StatusLicitacao status = (StatusLicitacao) q.getSingleResult();
        if (TipoSituacaoLicitacao.HOMOLOGADA.equals(status.getTipoSituacaoLicitacao())) {
            throw new ValidacaoException("A licitação " + licitacao + " - '" + licitacao.getResumoDoObjeto() + "' não pode ser alterada, pois está em status de HOMOLOGADA!");
        }

    }

    public List<LoteProcessoDeCompra> recuperaLotesProcessoDeCompra(Licitacao licitacao) {
        String hql = "select lote from Licitacao lic"
            + " inner join lic.processoDeCompra processo"
            + " inner join processo.lotesProcessoDeCompra lote"
            + " where lic= :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        try {
            return (List<LoteProcessoDeCompra>) q.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public boolean validaSeLicitacaoNaoPossuiOutroCertame(Licitacao licitacao) {
        String hql = "select certame from Certame certame"
            + " where licitacao = :licitacao";
        Query consulta = em.createQuery(hql);
        consulta.setParameter("licitacao", licitacao);


        try {
            if (consulta.getSingleResult().equals(licitacao)) {
                return false;
            }
        } catch (Exception e) {
            return true;
        }
        return false;
    }

    //VERIFICAR COM ARTHUR COMO CONSIDERAR UMA LICITAÇÃO ABERTA E INCLUIR NA QUERY
    public List<Licitacao> recuperarLicitacoesQuePossuamOMembroDeComissao(MembroComissao membro) {
        String sql = "     SELECT l.*"
            + "          FROM licitmembcom lmc"
            + "    INNER JOIN licitacao l ON l.id = lmc.licitacao_id"
            + "    INNER JOIN membrocomissao mc ON mc.id = lmc.membrocomissao_id"
            + "    INNER JOIN pessoafisica pf ON pf.id = mc.pessoafisica_id"
            + "         WHERE mc.id = :param";

        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("param", membro.getId());
        List licitacoes = q.getResultList();

        if (licitacoes.isEmpty()) {
            return null;
        } else {
            return licitacoes;
        }
    }

    public boolean removerMembroComissaoDaLicitacao(MembroComissao membro, Licitacao licitacao) {
        licitacao.setListaDeLicitacaoMembroComissao(recuperarListaDeLicitacaoMembroComissao(licitacao));
        LicitacaoMembroComissao remover = null;

        if (validaExclusaoDeMembroComissaoDaLicitacao(membro, licitacao)) {
            for (LicitacaoMembroComissao lmc : licitacao.getListaDeLicitacaoMembroComissao()) {
                if (lmc.getMembroComissao().equals(membro)) {
                    remover = lmc;
                    break;
                }
            }
        }

        if (remover != null) {
            licitacao.getListaDeLicitacaoMembroComissao().remove(remover);
            salvar(licitacao);
            return true;
        } else {
            return false;
        }
    }

    private boolean validaExclusaoDeMembroComissaoDaLicitacao(MembroComissao membro, Licitacao licitacao) {
        if (membro == null) {
            return false;
        }

        if (licitacao == null) {
            return false;
        }

        if (licitacao.getListaDeLicitacaoMembroComissao() == null) {
            return false;
        }

        if (licitacao.getListaDeLicitacaoMembroComissao().isEmpty()) {
            return false;
        }

        return true;
    }

    public Comissao recuperarComissao(Licitacao licitacao) {
        String hql = "select l.comissao from Licitacao l"
            + " where l = :licitacao";

        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        if (q.getResultList().isEmpty()) {
            return null;
        } else {
            return (Comissao) q.getSingleResult();
        }
    }

    public ComissaoFacade getComissaoFacade() {
        return comissaoFacade;
    }

    public ConfiguracaoAdministrativaFacade getConfiguracaoAdministrativaFacade() {
        return configuracaoAdministrativaFacade;
    }

    public DoctoHabilitacaoFacade getDoctoHabilitacaoFacade() {
        return doctoHabilitacaoFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public ModeloDoctoFacade getModeloDoctoFacade() {
        return modeloDoctoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public PregaoFacade getPregaoFacade() {
        return pregaoFacade;
    }

    public ProcessoDeCompraFacade getProcessoDeCompraFacade() {
        return processoDeCompraFacade;
    }

    public RequisicaoDeCompraFacade getRequisicaoDeCompraFacade() {
        return requisicaoDeCompraFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public VeiculoDePublicacaoFacade getVeiculoDePublicacaoFacade() {
        return veiculoDePublicacaoFacade;
    }

    public VersaoDoctoLicitacaoFacade getVersaoDoctoLicitacaoFacade() {
        return versaoDoctoLicitacaoFacade;
    }

    public SolicitacaoEmpenhoFacade getSolicitacaoEmpenhoFacade() {
        return solicitacaoEmpenhoFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public DotacaoSolicitacaoMaterialFacade getDotacaoSolicitacaoMaterialFacade() {
        return dotacaoSolicitacaoMaterialFacade;
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public ParecerLicitacaoFacade getParecerLicitacaoFacade() {
        return parecerLicitacaoFacade;
    }

    public ItemPregaoFacade getItemPregaoFacade() {
        return itemPregaoFacade;
    }

    public DocumentoOficialFacade getDocumentoOficialFacade() {
        return documentoOficialFacade;
    }

    public FeriadoFacade getFeriadoFacade() {
        return feriadoFacade;
    }

    public RepresentanteLicitacaoFacade getRepresentanteLicitacaoFacade() {
        return representanteLicitacaoFacade;
    }

    public Licitacao recuperarLote(Object id) {
        Licitacao lic = super.recuperar(id);
        if (lic.getProcessoDeCompra() != null) {
            lic.getProcessoDeCompra().getLotesProcessoDeCompra().size();
//            for (LoteProcessoDeCompra loteProcessoDeCompra : lic.getProcessoDeCompra().getLotesProcessoDeCompra()) {
//                loteProcessoDeCompra.getItensProcessoDeCompra().size();
//            }
        }
        return lic;
    }

    @Override
    public Licitacao recuperar(Object id) {
        Licitacao lic = super.recuperar(id);
        if (lic != null) {
            inicializarLicitacao(lic);
            findUnidadesLicitacao(lic);
            return lic;
        }
        return null;
    }

    public void inicializarLicitacao(Licitacao licitacao) {
        licitacao.getDocumentosProcesso().size();
        licitacao.getListaDeDoctoHabilitacao().size();
        licitacao.getListaDeDocumentos().size();
        for (DoctoLicitacao doctoLicitacao : licitacao.getListaDeDocumentos()) {
            doctoLicitacao.getListaDeVersoes().size();
        }

        if (licitacao.getDetentorDocumentoLicitacao() != null) {
            licitacao.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList().size();
        }

        licitacao.getFornecedores().size();
        for (LicitacaoFornecedor lf : licitacao.getFornecedores()) {
            lf.getDocumentosFornecedor().size();
        }

        licitacao.getListaDeLicitacaoMembroComissao().size();
        licitacao.getPareceres().size();
        licitacao.getPublicacoes().size();
        licitacao.getListaDeRecursoLicitacao().size();
        licitacao.getListaDeStatusLicitacao().size();

        if (licitacao.getProcessoDeCompra() != null) {
            licitacao.getProcessoDeCompra().getLotesProcessoDeCompra().size();
            for (LoteProcessoDeCompra loteProcessoDeCompra : licitacao.getProcessoDeCompra().getLotesProcessoDeCompra()) {
                loteProcessoDeCompra.getItensProcessoDeCompra().size();
            }
        }
    }

    public Licitacao recuperarParaPortal(Object id) {
        Licitacao lic = super.recuperar(id);
        inicializarLicitacao(lic);
        return lic;
    }

    public Licitacao recuperarCredenciamento(Object id) {
        Licitacao licitacao = super.recuperar(id);
        Hibernate.initialize(licitacao.getDocumentosProcesso());
        Hibernate.initialize(licitacao.getListaDeDoctoHabilitacao());
        Hibernate.initialize(licitacao.getListaDeDocumentos());
        for (DoctoLicitacao doctoLicitacao : licitacao.getListaDeDocumentos()) {
            Hibernate.initialize(doctoLicitacao.getListaDeVersoes());
        }
        if (licitacao.getDetentorDocumentoLicitacao() != null) {
            Hibernate.initialize(licitacao.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList());
        }
        Hibernate.initialize(licitacao.getFornecedores());
        for (LicitacaoFornecedor lf : licitacao.getFornecedores()) {
            Hibernate.initialize(lf.getDocumentosFornecedor());
            Hibernate.initialize(lf.getPropostaFornecedor());

            if (lf.getPropostaFornecedor() != null) {
                Hibernate.initialize(lf.getPropostaFornecedor().getLotes());
                for (LotePropostaFornecedor lote : lf.getPropostaFornecedor().getLotes()) {
                    Hibernate.initialize(lote.getItens());
                }
            }
        }
        Hibernate.initialize(licitacao.getPareceres());
        Hibernate.initialize(licitacao.getPublicacoes());
        Hibernate.initialize(licitacao.getListaDeStatusLicitacao());
        return licitacao;
    }

    public Licitacao recuperarSomenteLicitacao(Object id) {
        return super.recuperar(id);
    }

    public Licitacao recuperarSomenteStatus(Object id) {
        Licitacao lic = super.recuperar(id);
        lic.getListaDeStatusLicitacao().size();
        return lic;
    }

    public Licitacao recuperarSomenteUnidades(Object id) {
        Licitacao lic = super.recuperar(id);
        findUnidadesLicitacao(lic);
        return lic;
    }

    private void findUnidadesLicitacao(Licitacao lic) {
        Hibernate.initialize(lic.getUnidades());
        for (UnidadeLicitacao unidade : lic.getUnidades()) {
            unidade.setHierarquiaAdministrativa(hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), unidade.getUnidadeAdministrativa(), sistemaFacade.getDataOperacao()));
        }
        lic.setUnidadeAdministrativa(recuperarUnidadeVigenteLicitacao(lic));
    }

    public Licitacao recuperarSomenteArquivos(Long id) {
        Licitacao lic = super.recuperar(id);
        if (lic.getDetentorDocumentoLicitacao() != null) {
            lic.getDetentorDocumentoLicitacao().getDocumentoLicitacaoList().size();
        }
        lic.getListaDeDocumentos().size();
        return lic;
    }

    public HierarquiaOrganizacional recuperarUnidadeVigenteLicitacao(Licitacao entity) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ho.*  ")
            .append(" from licitacao lic  ")
            .append("   inner join unidadelicitacao ul on ul.licitacao_id = lic.id  ")
            .append("   inner join hierarquiaorganizacional ho on ho.subordinada_id = ul.unidadeadministrativa_id  ")
            .append(" where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ho.inicioVigencia) and coalesce(trunc(ho.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) ")
            .append("   and ho.tipohierarquiaorganizacional = :tipoHierarquia ")
            .append("   and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(ul.inicioVigencia) and coalesce(trunc(ul.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))")
            .append("   and lic.id = :idLicitacao ");
        Query q = em.createNativeQuery(sql.toString(), HierarquiaOrganizacional.class);
        q.setParameter("idLicitacao", entity.getId());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("tipoHierarquia", TipoHierarquiaOrganizacional.ADMINISTRATIVA.name());
        try {
            return (HierarquiaOrganizacional) q.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } catch (NonUniqueResultException ne) {
            throw new ExcecaoNegocioGenerica("Foi encontrado mais de uma unidade vigente para a licitação: " + entity + ".");
        }
    }

    public List<Licitacao> buscarLicitacoesPendentesDeAdjudicacaoHomologacao(String filtro, TipoSituacaoFornecedorLicitacao tipoSituacao) {
        String sql = "" +
            " select distinct licitacao.* from ( " +
            "   select lic.* from licitacao lic " +
            "       inner join pregao pr on pr.licitacao_id = lic.id " +
            "       inner join itempregao ip on ip.pregao_id = pr.id " +
            "       inner join itempregaolancevencedor iplv on iplv.id = ip.itempregaolancevencedor_id " +
            "       inner join lancepregao lp on lp.id = iplv.lancepregao_id " +
            "   where ip.tipostatusitempregao = :tipoStatusItemPregao " +
            " union all " +
            "   select lic.* from licitacao lic " +
            "       inner join certame cert on cert.licitacao_id = lic.id " +
            "       inner join itemcertame itc on itc.certame_id = cert.id " +
            "       left join itemcertameitempro itcitp on itcitp.itemcertame_id = itc.id " +
            "       left join itempropfornec itempf on itempf.id = itcitp.itempropostavencedor_id " +
            "       left join itemcertamelotepro itcltp on itcltp.itemcertame_id = itc.id " +
            "       left join lotepropfornec lotepf on lotepf.id = itcltp.lotepropfornecedorvencedor_id " +
            "    where itc.situacaoitemcertame = :situacaoItemCertame " +
            " union all " +
            "   select lic.* from licitacao lic " +
            "       inner join mapacomtecprec mapac on mapac.licitacao_id = lic.id " +
            "       inner join itemmapacomtecpre itemmc on itemmc.mapacomtecnicapreco_id = mapac.id " +
            "       left join itemmacotecprecitemproc itemmcitempro on itemmcitempro.itemmapacomtecnicapreco_id = itemmc.id " +
            "       left join itempropfornec itempf on itempf.id = itemmcitempro.itempropostavencedor_id " +
            "       left join itemmacotecprecloteproc itemmclotepro on itemmclotepro.itemmapacomtecnicapreco_id = itemmc.id " +
            "       left join lotepropfornec lotepf on lotepf.id = itemmclotepro.lotepropostavencedor_id " +
            "    where itemmc.situacaoitem = :situacaoItemCertame " +
            "   ) licitacao " +
            "         inner join licitacaofornecedor lf on lf.licitacao_id = licitacao.id " +
            "         inner join processodecompra pdc on pdc.id = licitacao.processodecompra_id " +
            "         inner join loteprocessodecompra lpdc on pdc.id = lpdc.processodecompra_id " +
            "         inner join itemprocessodecompra ipdc on lpdc.id = ipdc.loteprocessodecompra_id " +
            "         inner join statuslicitacao status on status.licitacao_id = licitacao.id " +
            "         inner join exercicio ex on ex.id = licitacao.exercicio_id " +
            "         inner join pessoa p on lf.empresa_id = p.id " +
            "         left join pessoafisica pf on p.id = pf.id " +
            "         left join pessoajuridica pj on p.id = pj.id " +
            "         left join exercicio exmodalidade on exmodalidade.id = licitacao.exerciciomodalidade_id " +
            " where lf.tipoclassificacaofornecedor = :tipoClassificacaoFornecedor " +
            "  and ipdc.situacaoitemprocessodecompra = :situacaoItemPdc " +
            "  and status.tiposituacaolicitacao = :andamento " +
            "  and status.datastatus = (select max(st.datastatus) from statuslicitacao st where st.licitacao_id = licitacao.id) " +
            adicionarCondicoesStatusLicitacaoSQL("licitacao") +
            "  and (lower(licitacao.resumodoobjeto) like :filtro " +
            "    or to_char(licitacao.numerolicitacao) || '/' || to_char(ex.ano) like :filtro " +
            "    or to_char(licitacao.numero) || '/' || to_char(exmodalidade.ano) like :filtro " +
            "    or to_char(licitacao.numerolicitacao) like :numero " +
            "    or to_char(ex.ano) like :filtro " +
            "    or lower(pf.nome) like :filtro " +
            "    or lower(pj.razaosocial) like :filtro " +
            "    or replace(replace(pf.cpf, '.', ''), '-', '') like :filtro " +
            "    or replace(replace(replace(pj.cnpj, '.', ''), '-', ''), '/', '') like :filtro) " +
            " order by licitacao.exercicio_id desc, licitacao.numerolicitacao desc ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("tipoStatusItemPregao", TipoStatusItemPregao.FINALIZADO.name());
        q.setParameter("situacaoItemCertame", SituacaoItemCertame.VENCEDOR.name());
        q.setParameter("tipoClassificacaoFornecedor", TipoClassificacaoFornecedor.HABILITADO.name());
        q.setParameter("situacaoItemPdc", TipoSituacaoFornecedorLicitacao.ADJUDICADA.equals(tipoSituacao) ? SituacaoItemProcessoDeCompra.AGUARDANDO.name() : SituacaoItemProcessoDeCompra.ADJUDICADO.name());
        q.setParameter("andamento", TipoSituacaoLicitacao.ANDAMENTO.name());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        q.setParameter("numero", filtro.trim().toLowerCase() + "%");
        List resultList = q.getResultList();
        if (resultList == null || resultList.isEmpty()) {
            return new ArrayList<>();
        }
        return resultList;
    }

    public List<LicitacaoFornecedor> getFornecedoresVencedoresDoPregao(Licitacao licitacao) {
        String query = "select distinct lf.* " +
            "         from licitacao lic " +
            "   inner join pregao pr on pr.licitacao_id = lic.id " +
            "   inner join itempregao ip on ip.pregao_id = pr.id " +
            "   INNER JOIN ITEMPREGAOLANCEVENCEDOR iplv ON iplv.ID = ip.ITEMPREGAOLANCEVENCEDOR_ID " +
            "   inner join lancepregao lp on lp.id = iplv.LANCEPREGAO_ID " +
            "   inner join propostafornecedor propforn on propforn.id = lp.propostafornecedor_id " +
            "   inner join licitacaofornecedor lf on lf.empresa_id = propforn.fornecedor_id and lf.licitacao_id = lic.id " +
            "        where ip.tipostatusitempregao = :tipoStatusItemPregao" +
            "          and lic.id = :licitacaoId ";

        Query q = em.createNativeQuery(query, LicitacaoFornecedor.class);
        q.setParameter("tipoStatusItemPregao", TipoStatusItemPregao.FINALIZADO.name());
        q.setParameter("licitacaoId", licitacao.getId());

        List<LicitacaoFornecedor> retorno = q.getResultList();
        if (retorno == null || retorno.isEmpty()) {
            return new ArrayList<>();
        }

        for (LicitacaoFornecedor lf : retorno) {
            lf.getDocumentosFornecedor().size();
        }

        return retorno;
    }

    public List<LicitacaoFornecedor> getFornecedoresVencedoresHabilitados(Licitacao licitacao) {
        String query = "SELECT DISTINCT lf.* "
            + "     FROM  "
            + "    (SELECT DISTINCT lic.*, lp.propostafornecedor_id AS propostavencedor_id "
            + "     FROM licitacao lic "
            + "       INNER JOIN pregao pr ON pr.licitacao_id = lic.ID "
            + "       INNER JOIN itempregao ip ON ip.pregao_id = pr.ID "
            + "       INNER JOIN ITEMPREGAOLANCEVENCEDOR iplv ON iplv.ID = ip.ITEMPREGAOLANCEVENCEDOR_ID "
            + "       INNER JOIN lancepregao lp ON lp.ID = iplv.LANCEPREGAO_ID "
            + "     WHERE ip.tipostatusitempregao = :tipoStatusItemPregao "
            + "    UNION ALL "
            + "    SELECT DISTINCT lic.*, CASE WHEN itempf.ID IS NOT NULL THEN itempf.propostafornecedor_id ELSE lotepf.propostafornecedor_id END AS propostavencedor_id "
            + "    FROM licitacao lic "
            + "      INNER JOIN certame cert ON cert.licitacao_id = lic.ID "
            + "      INNER JOIN itemcertame itc ON itc.certame_id = cert.ID "
            + "       LEFT JOIN itemcertameitempro itcitp ON itcitp.itemcertame_id = itc.ID "
            + "       LEFT JOIN itempropfornec itempf on itempf.id = itcitp.itempropostavencedor_id "
            + "       LEFT JOIN itemcertamelotepro itcltp ON itcltp.itemcertame_id = itc.ID "
            + "       LEFT JOIN lotepropfornec lotepf on lotepf.id = itcltp.lotepropfornecedorvencedor_id "
            + "    WHERE itc.situacaoitemcertame = :situacaoItemCertame "
            + "    "
            + "    UNION ALL    "
            + "    SELECT DISTINCT lic.*, CASE WHEN itempf.id IS NOT NULL THEN itempf.propostafornecedor_id ELSE lotepf.propostafornecedor_id END AS propostavencedor_id "
            + "    FROM licitacao lic "
            + "      INNER JOIN mapacomtecprec mapac ON mapac.licitacao_id = lic.ID "
            + "      INNER JOIN itemmapacomtecpre itemmc ON itemmc.mapacomtecnicapreco_id = mapac.ID "
            + "       LEFT JOIN itemmacotecprecitemproc itemmcitempro ON itemmcitempro.itemmapacomtecnicapreco_id = itemmc.ID "
            + "       LEFT JOIN itempropfornec itempf on itempf.id = itemmcitempro.itempropostavencedor_id "
            + "       LEFT JOIN itemmacotecprecloteproc itemmclotepro ON itemmclotepro.itemmapacomtecnicapreco_id = itemmc.ID "
            + "       LEFT JOIN lotepropfornec lotepf on lotepf.id = itemmclotepro.lotepropostavencedor_id "
            + "    WHERE itemmc.situacaoitem = :situacaoItemCertame "
            + "  ) licitacao  "
            + "  INNER JOIN propostafornecedor propforn ON propforn.ID = licitacao.propostavencedor_id "
            + "  INNER JOIN licitacaofornecedor lf ON lf.empresa_id = propforn.fornecedor_id AND lf.licitacao_id = licitacao.ID "
            + "WHERE lf.tipoclassificacaofornecedor = :tipoClassificacaoFornecedor "
            + "AND licitacao.id = :licitacaoId ";
        Query q = em.createNativeQuery(query, LicitacaoFornecedor.class);
        q.setParameter("tipoClassificacaoFornecedor", TipoClassificacaoFornecedor.HABILITADO.name());
        q.setParameter("situacaoItemCertame", SituacaoItemCertame.VENCEDOR.name());
        q.setParameter("tipoStatusItemPregao", TipoStatusItemPregao.FINALIZADO.name());
        q.setParameter("licitacaoId", licitacao.getId());
        List<LicitacaoFornecedor> retorno = q.getResultList();
        if (!retorno.isEmpty()) {
            for (LicitacaoFornecedor licitacaoFornecedor : retorno) {
                Hibernate.initialize(licitacaoFornecedor.getDocumentosFornecedor());
                Hibernate.initialize(licitacaoFornecedor.getListaDeStatus());
            }
            return retorno;
        }
        return Lists.newArrayList();
    }

    public List<LicitacaoFornecedor> buscarFornecedoresVencedoresComItensHomologados(Licitacao licitacao) {
        String query = "SELECT DISTINCT lf.* " +
            "     FROM  " +
            "    (SELECT DISTINCT lic.*, lp.propostafornecedor_id AS propostavencedor_id " +
            "     FROM licitacao lic " +
            "       INNER JOIN pregao pr ON pr.licitacao_id = lic.ID " +
            "       INNER JOIN itempregao ip ON ip.pregao_id = pr.ID " +
            "       INNER JOIN ITEMPREGAOLANCEVENCEDOR iplv ON iplv.ID = ip.ITEMPREGAOLANCEVENCEDOR_ID " +
            "       INNER JOIN lancepregao lp ON lp.ID = iplv.LANCEPREGAO_ID " +
            "     WHERE ip.tipostatusitempregao = :tipoStatusItemPregao " +
            "    UNION ALL " +
            "    SELECT DISTINCT lic.*, CASE WHEN itempf.ID IS NOT NULL THEN itempf.propostafornecedor_id ELSE lotepf.propostafornecedor_id END AS propostavencedor_id " +
            "    FROM licitacao lic " +
            "      INNER JOIN certame cert ON cert.licitacao_id = lic.ID " +
            "      INNER JOIN itemcertame itc ON itc.certame_id = cert.ID " +
            "       LEFT JOIN itemcertameitempro itcitp ON itcitp.itemcertame_id = itc.ID " +
            "       LEFT JOIN itempropfornec itempf on itempf.id = itcitp.itempropostavencedor_id " +
            "       LEFT JOIN itemcertamelotepro itcltp ON itcltp.itemcertame_id = itc.ID " +
            "       LEFT JOIN lotepropfornec lotepf on lotepf.id = itcltp.lotepropfornecedorvencedor_id " +
            "    WHERE itc.situacaoitemcertame = :situacaoItemCertame " +
            "    UNION ALL    " +
            "    SELECT DISTINCT lic.*, CASE WHEN itempf.id IS NOT NULL THEN itempf.propostafornecedor_id ELSE lotepf.propostafornecedor_id END AS propostavencedor_id " +
            "    FROM licitacao lic " +
            "      INNER JOIN mapacomtecprec mapac ON mapac.licitacao_id = lic.ID " +
            "      INNER JOIN itemmapacomtecpre itemmc ON itemmc.mapacomtecnicapreco_id = mapac.ID " +
            "       LEFT JOIN itemmacotecprecitemproc itemmcitempro ON itemmcitempro.itemmapacomtecnicapreco_id = itemmc.ID " +
            "       LEFT JOIN itempropfornec itempf on itempf.id = itemmcitempro.itempropostavencedor_id " +
            "       LEFT JOIN itemmacotecprecloteproc itemmclotepro ON itemmclotepro.itemmapacomtecnicapreco_id = itemmc.ID " +
            "       LEFT JOIN lotepropfornec lotepf on lotepf.id = itemmclotepro.lotepropostavencedor_id " +
            "    WHERE itemmc.situacaoitem = :situacaoItemCertame " +
            "  ) licitacao  " +
            "  INNER JOIN propostafornecedor propforn ON propforn.ID = licitacao.propostavencedor_id " +
            "  INNER JOIN licitacaofornecedor lf ON lf.empresa_id = propforn.fornecedor_id AND lf.licitacao_id = licitacao.ID " +
            "WHERE lf.tipoclassificacaofornecedor = :tipoClassificacaoFornecedor" +
            "  AND licitacao.id = :licitacaoId" +
            "  and exists (select 1 " +
            "                   from itemprocessodecompra ipc " +
            "                  inner join loteprocessodecompra lpc on lpc.id = ipc.loteprocessodecompra_id " +
            "                  inner join processodecompra pc on pc.id = lpc.processodecompra_id " +
            "                  inner join licitacao l on l.processodecompra_id = pc.id " +
            "                  inner join licitacaofornecedor s_lf on s_lf.licitacao_id = l.id " +
            "                  inner join statusfornecedorlicitacao slf on slf.licitacaofornecedor_id = s_lf.id " +
            "                where l.id = licitacao.id " +
            "                  and s_lf.id = lf.id " +
            "                  and ipc.situacaoitemprocessodecompra =:situacao_item_homologado) ";
        Query q = em.createNativeQuery(query, LicitacaoFornecedor.class);
        q.setParameter("tipoClassificacaoFornecedor", TipoClassificacaoFornecedor.HABILITADO.name());
        q.setParameter("situacaoItemCertame", SituacaoItemCertame.VENCEDOR.name());
        q.setParameter("tipoStatusItemPregao", TipoStatusItemPregao.FINALIZADO.name());
        q.setParameter("licitacaoId", licitacao.getId());
        q.setParameter("situacao_item_homologado", SituacaoItemProcessoDeCompra.HOMOLOGADO.name());
        List retorno = q.getResultList();
        if (retorno == null || retorno.isEmpty()) {
            return new ArrayList<>();
        }

        return retorno;
    }

    public StatusLicitacao buscarNovoStatusDaLicitacao(Licitacao licitacao) {
        String sql = "WITH ITEM (LICITACAO_ID, MODALIDADE, ITEM_ID, LICITACAOFORNECEDOR_ID) AS ( " +
            "    SELECT LIC.ID   AS LICITACAO_ID,  " +
            "           'PREGAO' AS MODALIDADE, " +
            "           IP.ID    AS ITEM_ID, " +
            "           LF.ID    AS LICITACAOFORNECEDOR_ID " +
            "      FROM ITEMPREGAO IP " +
            "INNER JOIN PREGAO PG                   ON PG.ID = IP.PREGAO_ID " +
            "INNER JOIN LICITACAO LIC               ON LIC.ID = PG.LICITACAO_ID " +
            "LEFT JOIN ITEMPREGAOLANCEVENCEDOR iplv ON iplv.ID = IP.ITEMPREGAOLANCEVENCEDOR_ID " +
            " LEFT JOIN LANCEPREGAO LP              ON iplv.LANCEPREGAO_ID = LP.ID " +
            " LEFT JOIN PROPOSTAFORNECEDOR PROPFORN ON PROPFORN.ID = LP.PROPOSTAFORNECEDOR_ID " +
            " LEFT JOIN LICITACAOFORNECEDOR LF      ON LF.LICITACAO_ID = PROPFORN.LICITACAO_ID AND LF.EMPRESA_ID = PROPFORN.FORNECEDOR_ID " +
            "     UNION " +
            "    SELECT LIC.ID    AS LICITACAO_ID, " +
            "           'CERTAME' AS MODALIDADE, " +
            "           IC.ID     AS ITEM_ID, " +
            "           LF.ID     AS LICITACAOFORNECEDOR_ID " +
            "      FROM ITEMCERTAME IC " +
            "INNER JOIN CERTAME CERT                ON CERT.ID = IC.CERTAME_ID " +
            "INNER JOIN LICITACAO LIC               ON LIC.ID = CERT.LICITACAO_ID " +
            "INNER JOIN ITEMCERTAMEITEMPRO ICIP     ON ICIP.ITEMCERTAME_ID = IC.ID " +
            "INNER JOIN ITEMPROPFORNEC IPF          ON IPF.ID = ICIP.ITEMPROPOSTAVENCEDOR_ID " +
            "INNER JOIN PROPOSTAFORNECEDOR PROPFORN ON PROPFORN.ID = IPF.PROPOSTAFORNECEDOR_ID " +
            "INNER JOIN LICITACAOFORNECEDOR LF      ON LF.LICITACAO_ID = PROPFORN.LICITACAO_ID AND LF.EMPRESA_ID = PROPFORN.FORNECEDOR_ID) " +
            "SELECT COUNT(1) " +
            "  FROM ITEM " +
            " WHERE LICITACAOFORNECEDOR_ID IS NULL " +
            "   AND LICITACAO_ID = :licitacao_id";

        Query q = em.createNativeQuery(sql);
        q.setParameter("licitacao_id", licitacao.getId());

        BigDecimal valor = (BigDecimal) q.getSingleResult();

        if (valor.intValue() > 0) {
            return null;
        }

        List<LicitacaoFornecedor> vencedores = getFornecedoresVencedoresHabilitados(licitacao);
        licitacao.setListaDeStatusLicitacao(recuperarListaDeStatus(licitacao));

        StatusLicitacao novoStatusLicitacao = null;

        if (todosAdjudicadosENaoHomologados(vencedores)) {
            novoStatusLicitacao = getNovoStatusLicitacao(TipoSituacaoLicitacao.ADJUDICADA, licitacao);
            novoStatusLicitacao.setMotivoStatusLicitacao("Status criado automaticamente pelo sistema para adjudicação da licitação.");

        }

        if (todosHomologados(vencedores)) {
            novoStatusLicitacao = getNovoStatusLicitacao(TipoSituacaoLicitacao.HOMOLOGADA, licitacao);
            novoStatusLicitacao.setMotivoStatusLicitacao("Status criado automaticamente pelo sistema para homologação da licitação.");
        }


        return novoStatusLicitacao;
    }

    private boolean todosHomologados(List<LicitacaoFornecedor> vencedores) {
        for (LicitacaoFornecedor lf : vencedores) {
            if (!lf.foiHomologado()) {
                return false;
            }
        }

        return true;
    }

    private boolean todosAdjudicadosENaoHomologados(List<LicitacaoFornecedor> vencedores) {
        for (LicitacaoFornecedor lf : vencedores) {
            if (!lf.foiAdjudicado() || lf.foiHomologado()) {
                return false;
            }
        }

        return true;
    }

    private StatusLicitacao getNovoStatusLicitacao(TipoSituacaoLicitacao tipo, Licitacao licitacao) {
        StatusLicitacao sl = new StatusLicitacao();
        sl.setNumero(licitacao.getListaDeStatusLicitacao().size() + 1l);
        sl.setDataStatus(new Date());
        sl.setTipoSituacaoLicitacao(tipo);
        sl.setLicitacao(licitacao);
        sl.setResponsavel(sistemaFacade.getUsuarioCorrente());
        return sl;
    }

    public DoctoLicitacaoFacade getDoctoLicitacaoFacade() {
        return doctoLicitacaoFacade;
    }


    public void salvarPessoaJuridica(PessoaJuridica pj) {
        pj = em.merge(pj);
    }

    public void salvarPessoaFisica(PessoaFisica pf) {
        pf = em.merge(pf);
    }

    public PublicacaoLicitacao recuperarUltimaPublicacaoDaLicitacao(Licitacao licitacao) {
        licitacao = recuperar(licitacao.getId());
        String hql = "select pl from PublicacaoLicitacao pl where pl.licitacao = :param order by pl.dataPublicacao desc ";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);


        if (q.getResultList() != null) {
            if (!licitacao.getPublicacoes().isEmpty()) {
                return (PublicacaoLicitacao) q.getResultList().get(0);
            }
        }
        return new PublicacaoLicitacao();
    }

    public List<PublicacaoLicitacao> recuperarLocalPublicacoesDoAvisoDaLicitacao(Licitacao licitacao) {
        String sql = "SELECT "
            + "  PL.* "
            + "FROM PUBLICACAOLICITACAO PL "
            + "WHERE PL.LICITACAO_ID = :idLicitacao";

        Query q = em.createNativeQuery(sql, PublicacaoLicitacao.class);
        q.setParameter("idLicitacao", licitacao.getId());

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<LicitacaoFornecedor> recuperarEmpresasPorLicitacaoETipoClassificacao(Licitacao licitacao, TipoClassificacaoFornecedor classificacaoFornecedor) {
        String hql = "from LicitacaoFornecedor lf where lf.licitacao = :licitacao and lf.tipoClassificacaoFornecedor = :classificacao";

        Query q = em.createQuery(hql);
        q.setParameter("licitacao", licitacao);
        q.setParameter("classificacao", classificacaoFornecedor);

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<LicitacaoFornecedor> recuperarListaDeLicitacaoFornecedorComRepresentante(Licitacao licitacao) {
        String hql = "from LicitacaoFornecedor lf where lf.licitacao = :param and lf.representante is not null";

        Query q = em.createQuery(hql);
        q.setParameter("param", licitacao);

        List<LicitacaoFornecedor> retorno = q.getResultList();
        retorno.size();
        if (!retorno.isEmpty()) {
            for (LicitacaoFornecedor lf : retorno) {
                lf.getDocumentosFornecedor().size();
            }
        }

        return retorno;
    }

    public List<Licitacao> buscarLicitacaoPorAnoOrNumeroOrDescricaoDoPCSemAtaDeRegPrecoAndUsuarioGestorLicitacao(String parte, UsuarioSistema usuarioSistema) {
        String sql = " " +
            " select l.* from licitacao l  " +
            "   inner join exercicio e on e.id = l.exercicio_id  " +
            "   inner join processodecompra pc on pc.id = l.processodecompra_id  " +
            "   inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id  " +
            "   inner join cotacao cot on cot.id = sol.cotacao_id  " +
            "   inner join formulariocotacao forn on forn.id = cot.formulariocotacao_id  " +
            "  where ( lower(l.resumodoobjeto) like :parte " +
            "       or lower(pc.descricao) like :parte " +
            "       or l.numerolicitacao like :parte " +
            "       or to_char(e.ano) like :parte " +
            "       or to_char(l.numerolicitacao) || '/' || to_char(e.ano) like :parte) " +
            "  and l.modalidadelicitacao in (:modalidadesLicitacao)   " +
            "  and case when forn.intencaoRegistroPreco_id is not null then  " +
            "          (select distinct 1  " +
            "          from usuariounidadeorganizacio u_un  " +
            "          where u_un.usuariosistema_id = :id_usuario  " +
            "          and u_un.unidadeorganizacional_id in (select unidPart.unidadeparticipante_id from participanteirp part  " +
            "                                                inner join unidadeparticipanteirp unidPart on unidPart.participanteirp_id = part.id  " +
            "                                                where to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(unidPart.inicioVigencia) and coalesce(trunc(unidPart.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))  " +
            "                                                and part.INTENCAOREGISTRODEPRECO_ID = forn.INTENCAOREGISTROPRECO_ID)  " +
            "                 and u_un.gestorlicitacao = :gestor_licitacao)  " +
            "          else  " +
            "              (select distinct 1  " +
            "               from usuariounidadeorganizacio u_un  " +
            "               where u_un.usuariosistema_id = :id_usuario  " +
            "               and u_un.unidadeorganizacional_id = pc.unidadeorganizacional_id  " +
            "               and u_un.gestorlicitacao = :gestor_licitacao) end = 1 " +
            adicionarCondicoesStatusLicitacaoSQL("l") +
            " order by e.ano desc, l.numerolicitacao desc ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("id_usuario", usuarioSistema.getId());
        q.setParameter("gestor_licitacao", Boolean.TRUE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("modalidadesLicitacao", Util.getListOfEnumName(ModalidadeLicitacao.getModalidadesLicitacao()));
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        List resultList = q.getResultList();
        if (resultList != null && !resultList.isEmpty()) {
            return resultList;
        }
        return new ArrayList<>();
    }

    public Licitacao recuperarLicitacaoPorProcessoCompra(ProcessoDeCompra processo) {
        String sql = "select lic.* from licitacao lic " +
            " where lic.processodecompra_id = :idProcesso ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setMaxResults(1);
        q.setParameter("idProcesso", processo.getId());
        try {
            return (Licitacao) q.getSingleResult();
        } catch (NoResultException npe) {
            return null;
        }
    }

    public Pregao licitacaDePregao(Licitacao licitacao) {
        String hql = "from Pregao p where p.licitacao.id = :li";
        Query q = em.createQuery(hql);
        q.setParameter("li", licitacao.getId());
        List<Pregao> list = q.getResultList();
        if (list.isEmpty()) {
            return null;
        }
        list.get(0).getListaDeIntencaoDeRecursos().size();
        return (Pregao) list.get(0);
    }

    public SolicitacaoMaterial recuperarSolicitacaoProcessoDeCompra(ProcessoDeCompra compra) {
        String hql = "select distinct solicitacao from ProcessoDeCompra pr " +
            "    join pr.lotesProcessoDeCompra lotes " +
            "    join lotes.itensProcessoDeCompra itns " +
            "    join itns.itemSolicitacaoMaterial item " +
            "    join item.loteSolicitacaoMaterial.solicitacaoMaterial solicitacao " +
            "  where pr.id = :compra  ";
        Query q = em.createQuery(hql);
        q.setParameter("compra", compra.getId());
        q.setMaxResults(1);
        try {
            return (SolicitacaoMaterial) q.getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<SelectItem> getTipoEmpresa() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(TipoEmpresa.MICRO));
        retorno.add(new SelectItem(TipoEmpresa.PEQUENA));
        retorno.add(new SelectItem(TipoEmpresa.NORMAL));
        return retorno;
    }

    public Boolean isLicitacaoComItensDefinidoPorTipoObjetoCompra(Licitacao licitacao, List<TipoObjetoCompra> tiposObjetoCompra) {
        String sql = " select " +
            "           distinct lic.* " +
            "          from licitacao lic " +
            "            inner join processodecompra pc on pc.id = lic.processodecompra_id " +
            "            inner join solicitacaomaterial sol on sol.id = pc.solicitacaomaterial_id " +
            "            inner join cotacao cotacao on cotacao.id = sol.cotacao_id " +
            "            inner join lotecotacao loteCot on lotecot.cotacao_id = cotacao.id " +
            "            inner join itemcotacao item on item.lotecotacao_id = lotecot.id " +
            "            inner join objetocompra oc on oc.id = item.objetocompra_id " +
            "          where oc.tipoobjetocompra in (:tiposObjetoCompra) " +
            "            and lic.id = :idLicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLicitacao", licitacao.getId());
        q.setParameter("tiposObjetoCompra", TipoObjetoCompra.recuperarListaName(tiposObjetoCompra));
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public Boolean isLicitacaoComItensDeMateriais(Licitacao lic) {
        String hql = "select l.id from Licitacao l " +
            " inner join l.processoDeCompra p" +
            " inner join p.lotesProcessoDeCompra lpc" +
            " inner join lpc.itensProcessoDeCompra ipc" +
            " inner join ipc.itemSolicitacaoMaterial itSol" +
            " where l = :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", lic);
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public Boolean isLicitacaoComItensDeServicos(Licitacao lic) {
        String hql = "select l.id from Licitacao l " +
            " inner join l.processoDeCompra p" +
            " inner join p.lotesProcessoDeCompra lpc" +
            " inner join lpc.itensProcessoDeCompra ipc" +
            " inner join ipc.itemSolicitacaoMaterial itSol" +
            " inner join itSol.itemSolicitacaoServico iss" +
            " where l = :licitacao";
        Query q = em.createQuery(hql);
        q.setParameter("licitacao", lic);
        q.setMaxResults(1);
        try {
            return q.getSingleResult() != null;
        } catch (NoResultException nre) {
            return false;
        }
    }

    public boolean licitacaoTemPropostaFornecedor(Licitacao licitacao) {
        String sql = "    SELECT * "
            + "      FROM LICITACAO LIC ";

        if (licitacao.isTecnicaEPreco()) {
            sql += "  INNER JOIN PROPOSTATECNICA PT ON PT.LICITACAO_ID = LIC.ID  ";
        } else {
            sql += "  INNER JOIN PROPOSTAFORNECEDOR PF ON PF.LICITACAO_ID = LIC.ID  ";
        }

        sql += "           WHERE LIC.ID = :param ";

        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("param", licitacao.getId());
        q.setMaxResults(1);

        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

    public boolean fornecedorDaLicitacaoTemProposta(Licitacao licitacao, Pessoa fornecedor) {
        String sql = "     SELECT * "
            + "           FROM LICITACAO LIC ";

        if (licitacao.isTecnicaEPreco()) {
            sql += "   INNER JOIN PROPOSTATECNICA PT ON PT.LICITACAO_ID = LIC.ID  ";
            sql += "   INNER JOIN PESSOA P ON P.ID = PT.FORNECEDOR_ID ";
        } else {
            sql += "   INNER JOIN PROPOSTAFORNECEDOR PRO ON PRO.LICITACAO_ID = LIC.ID  ";
            sql += "   INNER JOIN PESSOA P ON P.ID = PRO.FORNECEDOR_ID ";
        }
        sql += "            WHERE LIC.ID = :lic ";
        sql += "              AND P.ID = :forn ";

        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("lic", licitacao.getId());
        q.setParameter("forn", fornecedor.getId());
        q.setMaxResults(1);

        return q.getResultList() != null && !q.getResultList().isEmpty();
    }

    public DocumentosNecessariosAnexoFacade getDocumentosNecessariosAnexoFacade() {
        return documentosNecessariosAnexoFacade;
    }

    public DocumentoLicitacaoFacade getDocumentoLicitacaoFacade() {
        return documentoLicitacaoFacade;
    }

    public List<ItemProcessoDeCompra> ordenaLotesEItensDoProcessoEPreencheLista(ProcessoDeCompra pdc) {
        List<ItemProcessoDeCompra> itens = new ArrayList<>();

        Collections.sort(pdc.getLotesProcessoDeCompra(), new Comparator<LoteProcessoDeCompra>() {
            @Override
            public int compare(LoteProcessoDeCompra o1, LoteProcessoDeCompra o2) {
                return o1.getNumero().compareTo(o2.getNumero());
            }
        });

        for (LoteProcessoDeCompra lpdc : pdc.getLotesProcessoDeCompra()) {
            Collections.sort(lpdc.getItensProcessoDeCompra(), new Comparator<ItemProcessoDeCompra>() {
                @Override
                public int compare(ItemProcessoDeCompra o1, ItemProcessoDeCompra o2) {
                    return o1.getNumero().compareTo(o2.getNumero());
                }
            });
            itens.addAll(lpdc.getItensProcessoDeCompra());
        }
        return itens;
    }

    public ItemSolicitacao mergerItemSolicitacao(ItemSolicitacao item) {
        return em.merge(item);
    }

    public ObjetoCompraFacade getObjetoCompraFacade() {
        return objetoCompraFacade;
    }

    public List<ItemProcessoDeCompra> recuperarItensDoProcessoDeCompraPorLicitacaoAndSituacaoDoItem(Licitacao licitacao, SituacaoItemProcessoDeCompra situacaoItemProcessoDeCompra) {
        String sql = "SELECT ITEM.* " +
            "FROM LICITACAO LIC " +
            "INNER JOIN PROCESSODECOMPRA PROCESSO ON LIC.PROCESSODECOMPRA_ID = PROCESSO.ID " +
            "INNER JOIN LOTEPROCESSODECOMPRA LOTE ON LOTE.PROCESSODECOMPRA_ID = PROCESSO.ID " +
            "INNER JOIN ITEMPROCESSODECOMPRA ITEM ON ITEM.LOTEPROCESSODECOMPRA_ID       = LOTE.ID " +
            "WHERE LIC.ID                          = :licitacao " +
            "AND ITEM.SITUACAOITEMPROCESSODECOMPRA = :situacaoItemProcessoDeCompra ";
        Query q = em.createNativeQuery(sql, ItemProcessoDeCompra.class);
        q.setParameter("licitacao", licitacao.getId());
        q.setParameter("situacaoItemProcessoDeCompra", situacaoItemProcessoDeCompra.name());
        return q.getResultList();
    }

    public void verificarStatusLicitacao(Licitacao licitacaoSelecionada) {
        StatusLicitacaoException exception = new StatusLicitacaoException();
        StatusLicitacao ultimoStatusLicitacao = statusLicitacaoFacade.recuperarUltimoStatusLicitacao(licitacaoSelecionada);
        if (ultimoStatusLicitacao != null) {
            if (TipoSituacaoLicitacao.ANULADA.equals(ultimoStatusLicitacao.getTipoSituacaoLicitacao())) {
                exception.adicionarMensagemDeOperacaoNaoPermitida("A licitação: " + licitacaoSelecionada.toString() + " está anulada!");
            } else if (TipoSituacaoLicitacao.DESERTA.equals(ultimoStatusLicitacao.getTipoSituacaoLicitacao())) {
                exception.adicionarMensagemDeOperacaoNaoPermitida("A licitação: " + licitacaoSelecionada.toString() + " está desertada!");
            } else if (TipoSituacaoLicitacao.REVOGADA.equals(ultimoStatusLicitacao.getTipoSituacaoLicitacao())) {
                exception.adicionarMensagemDeOperacaoNaoPermitida("A licitação: " + licitacaoSelecionada.toString() + " está revogada!");
            } else if (TipoSituacaoLicitacao.FRACASSADA.equals(ultimoStatusLicitacao.getTipoSituacaoLicitacao())) {
                exception.adicionarMensagemDeOperacaoNaoPermitida("A licitação: " + licitacaoSelecionada.toString() + " está fracassada!");
            }
        }
        if (exception.temMensagens()) {
            throw exception;
        }
    }

    public String adicionarCondicoesStatusLicitacaoSQL(String aliasLicitacao) {
        return "and  NOT EXISTS ( select status.* " +
            "                   from statuslicitacao status " +
            "                   where status.id =( select max(s.id) " +
            "                                      from statuslicitacao s " +
            "                                      where s.licitacao_id = " + aliasLicitacao + ".id ) " +
            "                  AND status.tipoSituacaoLicitacao in (" + StatusLicitacao.concatenarListaStatus() + ") " +
            " ) ";
    }

    public String adicionarCondicoesStatusLicitacaoHQL(String aliasLicitacao, String aliasStatus) {
        return "and  " + aliasStatus + ".id = ( select max(s.id) from StatusLicitacao s" +
            "                               where s.licitacao.id = " + aliasLicitacao + ".id )" +
            "   and " + aliasStatus + ".tipoSituacaoLicitacao not in (" + StatusLicitacao.concatenarListaStatus() + ") ";
    }

    public boolean isLicitacaoIRP(Licitacao lic) {
        String sql = " " +
            " SELECT lic.* FROM LICITACAO lic " +
            "  INNER JOIN PROCESSODECOMPRA pc ON lic.PROCESSODECOMPRA_ID = pc.ID " +
            "  INNER JOIN SOLICITACAOMATERIAL sol ON sol.ID = pc.SOLICITACAOMATERIAL_ID " +
            "  INNER JOIN COTACAO cot ON sol.COTACAO_ID = cot.ID " +
            "  INNER JOIN FORMULARIOCOTACAO fc ON fc.id = cot.FORMULARIOCOTACAO_ID " +
            "  INNER JOIN INTENCAOREGISTROPRECO IRP ON IRP.ID = FC.INTENCAOREGISTROPRECO_ID " +
            " WHERE lic.id = :idLicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLicitacao", lic.getId());
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

    public boolean isLicitacaoPossuiCertame(Licitacao lic) {

        if (lic.getId() == null)
            return false;

        String sql = " SELECT lic.* " +
            " FROM LICITACAO lic " +
            "  INNER JOIN CERTAME cer ON lic.ID = cer.LICITACAO_ID " +
            " WHERE lic.id = :licitacaoId ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("licitacaoId", lic.getId());
        q.setMaxResults(1);
        return !q.getResultList().isEmpty();
    }

    public List<Licitacao> buscarLicitacoesNumeroOrDescricaoOrExercicoParaArquivoProposta(String parte) {
        String sql =
            " SELECT L.* FROM LICITACAO L                                " +
                " INNER JOIN STATUSLICITACAO STATUS ON L.ID = STATUS.LICITACAO_ID " +
                " INNER JOIN EXERCICIO EX ON EX.ID = L.EXERCICIO_ID               " +
                " WHERE STATUS.ID = (SELECT MAX(S.ID)                             " +
                "                     FROM STATUSLICITACAO S                      " +
                "                    WHERE S.LICITACAO_ID = L.ID )                " +
                " AND STATUS.TIPOSITUACAOLICITACAO IN (:andamento)                " +
                " and l.abertaem is not null                " +
                " AND EXISTS (SELECT 1                                            " +
                "              FROM LICITACAOFORNECEDOR LF                        " +
                "             WHERE LF.LICITACAO_ID = L.ID )                      " +
                " AND (lower(translate(l.RESUMODOOBJETO,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')) like translate(:FILTRO,'âàãáÁÂÀÃéêÉÊíÍóôõÓÔÕüúÜÚÇç','aaaaaaaaeeeeiioooooouuuucc')" +
                "      OR TO_CHAR(L.NUMEROLICITACAO)  LIKE :FILTRO                " +
                "      OR TO_CHAR(EX.ANO) LIKE :FILTRO     " +
                "      or to_char(l.numerolicitacao) || '/' || to_char(ex.ano) like :FILTRO) " +
                " order by ex.ano desc, l.numerolicitacao desc ";
        Query query = em.createNativeQuery(sql, Licitacao.class);
        query.setParameter("FILTRO", "%" + parte.toLowerCase().trim() + "%");
        query.setParameter("andamento", TipoSituacaoLicitacao.ANDAMENTO.name());
        query.setMaxResults(50);
        return query.getResultList();
    }

    public List<Licitacao> buscarLicitacoesPortalTransparencia(Exercicio exercicio) {
        String sql = "select l.* from Licitacao l" +
            " inner join PROCESSODECOMPRA pc on l.PROCESSODECOMPRA_ID = pc.id" +
            " inner join exercicio ex on pc.exercicio_id = ex.id"
            + " where ex.id = :exercicio";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("exercicio", exercicio.getId());
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public List<Pessoa> recuperarVencedoresDaLicitacao(Licitacao lic) {
        List<Pessoa> pessoas = new ArrayList<>();
        if ((!lic.isPregao()) && TipoNaturezaDoProcedimentoLicitacao.getNaturezaTipoRDCCertame().contains(lic.getNaturezaDoProcedimento())) {
            pessoas = recuperarVencedoresDaLicitacaoApartirDoCertame(lic);
        }
        if (lic.isPregao() || lic.isRDC()) {
            pessoas = recuperarVencedoresDaLicitacaoApartirDoPregao(lic);
        }
        return pessoas;
    }

    public List<Pessoa> recuperarVencedoresDaLicitacaoApartirDoPregao(Licitacao licitacao) {
        String hqlPregao = "";
        if (licitacao.getTipoApuracao().equals(TipoApuracaoLicitacao.ITEM)) {
            hqlPregao = "select fornec"
                + " from ItemPregao item, ItemPropostaFornecedor itemprop"
                + " inner join item.itemPregaoItemProcesso itpre"
                + "  inner join item.itemPregaoLanceVencedor iplv"
                + "  inner join iplv.lancePregao lance"
                + " inner join lance.propostaFornecedor prop"
                + " inner join prop.fornecedor fornec"
                + " inner join itpre.itemProcessoDeCompra itemprocesso"
                + " inner join item.pregao pregao"
                + " inner join pregao.licitacao lic"
                + "      where lic = :licitacao"
                + "        and itemprop.propostaFornecedor = prop"
                + "        and itemprop.itemProcessoDeCompra = itemprocesso";
        } else {
            hqlPregao = "  select fornec"
                + " from ItemPregao item, LotePropostaFornecedor loteprop, ItemPropostaFornecedor itemprop"
                + " inner join item.itemPregaoLoteProcesso lotepre"
                + " inner join lotepre.loteProcessoDeCompra lote"
                + "  inner join item.itemPregaoLanceVencedor iplv"
                + "  inner join iplv.lancePregao lance"
                + " inner join lance.propostaFornecedor prop"
                + " inner join prop.fornecedor fornec"
                + " inner join item.pregao pregao"
                + " inner join pregao.licitacao lic"
                + " where lic = :licitacao"
                + "   and loteprop.loteProcessoDeCompra = lote"
                + "   and loteprop.propostaFornecedor = prop"
                + "   and itemprop.lotePropostaFornecedor = loteprop";
        }

        Query consultaDoPregao = em.createQuery(hqlPregao);
        consultaDoPregao.setParameter("licitacao", licitacao);
        List<Pessoa> pessoas = consultaDoPregao.getResultList();
        pessoas = new ArrayList(new HashSet(pessoas));
        return pessoas;
    }

    public List<Pessoa> recuperarVencedoresDaLicitacaoApartirDoCertame(Licitacao licitacao) {
        String hqlCertame = "";
        if (licitacao.getTipoApuracao().equals(TipoApuracaoLicitacao.ITEM)) {
            hqlCertame = "select fornec"
                + " from ItemCertame item"
                + " inner join item.certame certame"
                + " inner join certame.licitacao lic"
                + "  left join item.itemCertameItemProcesso itemce"
                + "  left join itemce.itemPropostaVencedor itemprop"
                + "  left join itemprop.propostaFornecedor prop"
                + "  left join prop.fornecedor fornec"
                + "      where item.situacaoItemCertame = 'VENCEDOR'"
                + "        and lic = :licitacao";
        } else {
            hqlCertame = "select fornec from ItemCertame item, ItemPropostaFornecedor itemlote"
                + " inner join item.certame certame"
                + " inner join certame.licitacao lic"
                + " left join item.itemCertameLoteProcesso lotece"
                + " left join lotece.lotePropFornecedorVencedor loteprop"
                + " left join itemlote.propostaFornecedor prop"
                + " left join prop.fornecedor fornec"
                + "     where item.situacaoItemCertame = 'VENCEDOR'"
                + "       and itemlote.lotePropostaFornecedor = loteprop"
                + "       and lic = :licitacao ";
        }
        Query consultaDoCertame = em.createQuery(hqlCertame);
        consultaDoCertame.setParameter("licitacao", licitacao);
        List<Pessoa> pessoas = consultaDoCertame.getResultList();
        pessoas = new ArrayList(new HashSet(pessoas));
        return pessoas;
    }


    public List<LicitacaoFornecedor> buscarFornecedoresLicitacao(Licitacao licitacao) {
        String query = "select lf.* from licitacao lic " +
            "           inner join licitacaofornecedor lf on lf.licitacao_id = lic.id " +
            "           where lic.id = :idLicitacao";
        Query q = em.createNativeQuery(query, LicitacaoFornecedor.class);
        q.setParameter("idLicitacao", licitacao.getId());
        List<LicitacaoFornecedor> list = q.getResultList();
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        return list;
    }

    public List<ProgressaoLanceCertame> buscarLancesCertame(Licitacao licitacao, boolean porItem) {
        String sql;
        if (porItem) {
            if (licitacao.getModalidadeLicitacao().isPregao()) {
                sql = "select rod.numero as rodada, " +
                    "       lote.numero as lote, " +
                    "       ob.codigo || ' - ' || ob.descricao as Item, " +
                    "       itemfor.marca as marca, " +
                    "       p.id as participante, " +
                    "       rep.nome as responsavel, " +
                    "       itemfor.preco as valorPrevisto, " +
                    "       case when rod.numero = 1 then itemfor.preco else " +
                    "       (select l.valor from lancepregao l " +
                    "       where l.rodadapregao_id = (select max(rMax.id) from rodadapregao rMax " +
                    "       where rMax.itempregao_id = item.id " +
                    "       and l.propostafornecedor_id = lanc.propostafornecedor_id " +
                    "       and rmax.numero < ((select max(rMax.numero) from rodadapregao rMax " +
                    "       where rMax.id = lanc.rodadapregao_id))) " +
                    "       and rownum = 1) end as lanceAnterior, " +
                    "       lanc.valor as lanceAtual, " +
                    "       lanc.statuslancepregao as situacao " +
                    "  from pregao " +
                    " inner join itempregao item on item.pregao_id = pregao.id " +
                    " inner join rodadapregao rod on rod.itempregao_id = item.id " +
                    " inner join lancepregao lanc on lanc.rodadapregao_id = rod.id " +
                    " inner join itpreitpro it on it.itempregao_id = item.id " +
                    " inner join ItemProcessoDeCompra ipc on ipc.id = it.itemprocessodecompra_id " +
                    " inner join loteprocessodecompra lote on ipc.loteprocessodecompra_id = lote.id " +
                    " inner join itemsolicitacao itemSol on itemSol.id = ipc.itemsolicitacaomaterial_id " +
                    " inner join objetocompra ob on ob.id = itemSol.objetocompra_id " +
                    " inner join PropostaFornecedor pro on pro.id = lanc.propostafornecedor_id " +
                    " inner join pessoa p on p.id = pro.fornecedor_id " +
                    "  left join pessoafisica pfForn on pfForn.id = p.id " +
                    "  left join pessoajuridica pjForn on pjForn.id = p.id " +
                    "  left join RepresentanteLicitacao rep on rep.id = pro.representante_id " +
                    " inner join ITEMPROPFORNEC itemfor on itemfor.propostafornecedor_id = pro.id " +
                    " where pregao.licitacao_id = :licitacao " +
                    " order by rod.numero";
            } else {
                sql = "select '1' as rodada, " +
                    "       lote.numero as lote, " +
                    "       ipc.numero || ' - ' || ob.descricao as Item, " +
                    "       itemfor.marca as marca, " +
                    "       p.id as participante, " +
                    "       rep.nome as responsavel, " +
                    "       itemsol.preco as valorPrevisto, " +
                    "       0 as lanceAnterior, " +
                    "       itemfor.preco as lanceAtual, " +
                    "       item.situacaoitemcertame as situacao " +
                    "  from certame ct " +
                    " inner join itemcertame item on item.certame_id = ct.id " +
                    " inner join ITEMCERTAMEITEMPRO itemPro on itempro.itemcertame_id = item.id " +
                    " inner join ItemProcessoDeCompra ipc on ipc.id = itempro.itemprocessodecompra_id " +
                    " inner join loteprocessodecompra lote on ipc.loteprocessodecompra_id = lote.id " +
                    " inner join itemsolicitacao itemSol on itemSol.id = ipc.itemsolicitacaomaterial_id " +
                    " inner join objetocompra ob on ob.id = itemSol.objetocompra_id " +
                    " inner join ITEMPROPFORNEC itemfor on itemfor.id = itempro.itempropostavencedor_id " +
                    " inner join PropostaFornecedor pro on pro.id = itemfor.propostafornecedor_id " +
                    " inner join pessoa p on p.id = pro.fornecedor_id " +
                    "  left join pessoafisica pfForn on pfForn.id = p.id " +
                    "  left join pessoajuridica pjForn on pjForn.id = p.id " +
                    "  left join RepresentanteLicitacao rep on rep.id = pro.representante_id " +
                    " where ct.licitacao_id = :licitacao ";
            }
        } else {
            if (licitacao.getModalidadeLicitacao().isPregao()) {
                sql = "select lote.numero                             as numeroLote, " +
                    "       lote.descricao                            as descricaoLote, " +
                    "       p.id as participante, " +
                    "       lanc.statuslancepregao                    as situacao, " +
                    "       lanc.valor " +
                    " from pregao " +
                    "         inner join itempregao item on item.pregao_id = pregao.id " +
                    "         inner join rodadapregao rod on rod.itempregao_id = item.id " +
                    "         inner join lancepregao lanc on lanc.rodadapregao_id = rod.id " +
                    "         inner join itprelotpro it on it.itempregao_id = item.id " +
                    "         inner join loteprocessodecompra lote on lote.id = it.loteprocessodecompra_id " +
                    "         inner join PropostaFornecedor pro on pro.id = lanc.propostafornecedor_id " +
                    "         inner join pessoa p on p.id = pro.fornecedor_id " +
                    "         left join pessoafisica pfForn on pfForn.id = p.id " +
                    "         left join pessoajuridica pjForn on pjForn.id = p.id " +
                    " where pregao.licitacao_id = :licitacao " +
                    " order by rod.numero ";
            } else {
                sql = "select " +
                    "    lote.numero as numeroLote, " +
                    "    lote.descricao as descricaoLote, " +
                    "    p.id as participante, " +
                    "    item.situacaoitemcertame as situacao, " +
                    "    lote.valor " +
                    " from certame ct " +
                    "         inner join itemcertame item on item.certame_id = ct.id " +
                    "         inner join ITEMCERTAMELOTEPRO itemlote on itemlote.itemcertame_id = item.id " +
                    "         inner join loteprocessodecompra lote on itemlote.loteprocessodecompra_id = lote.id " +
                    "         inner join LOTEPROPFORNEC loteFor on loteFor.id= itemlote.lotepropfornecedorvencedor_id " +
                    "         inner join PropostaFornecedor pro on pro.id = loteFor.propostafornecedor_id " +
                    "         inner join pessoa p on p.id = pro.fornecedor_id " +
                    "         left join pessoafisica pfForn on pfForn.id = p.id " +
                    "         left join pessoajuridica pjForn on pjForn.id = p.id " +
                    " where ct.licitacao_id = :licitacao";
            }
        }

        Query q = em.createNativeQuery(sql);
        q.setParameter("licitacao", licitacao.getId());
        List<Object[]> consulta = q.getResultList();
        if (!consulta.isEmpty()) {
            List<ProgressaoLanceCertame> certames = Lists.newArrayList();
            for (Object[] objeto : consulta) {
                if (porItem) {
                    ProgressaoLanceCertame item = new ProgressaoLanceCertame();
                    item.setRodada(objeto[0].toString());
                    item.setNumeroLote(objeto[1].toString());
                    item.setItem(objeto[2].toString());
                    item.setMarca(objeto[3] != null ? objeto[3].toString() : null);
                    item.setParticipante(pessoaFacade.recuperar(((BigDecimal) objeto[4]).longValue()));
                    item.setResponsavel(objeto[5] != null ? objeto[5].toString() : "");
                    item.setValorPrevisto((BigDecimal) objeto[6]);
                    item.setLanceAnterior((BigDecimal) objeto[7]);
                    item.setLanceAtual((BigDecimal) objeto[8]);
                    if (licitacao.getModalidadeLicitacao().isPregao()) {
                        item.setSituacao(StatusLancePregao.valueOf(objeto[9].toString()).getDescricao());
                    } else {
                        item.setSituacao(SituacaoItemCertame.valueOf(objeto[9].toString()).getDescricao());
                    }
                    certames.add(item);
                } else {
                    ProgressaoLanceCertame item = new ProgressaoLanceCertame();
                    item.setNumeroLote(objeto[0].toString());
                    item.setDescricao(objeto[1].toString());
                    item.setParticipante(pessoaFacade.recuperar(((BigDecimal) objeto[2]).longValue()));
                    if (licitacao.getModalidadeLicitacao().isPregao()) {
                        item.setSituacao(StatusLancePregao.valueOf(objeto[3].toString()).getDescricao());
                    } else {
                        item.setSituacao(SituacaoItemCertame.valueOf(objeto[3].toString()).getDescricao());
                    }
                    item.setValor((BigDecimal) objeto[4]);
                    certames.add(item);
                }
            }
            return certames;
        }
        return null;
    }

    public List<Licitacao> buscarLicitacoesUsuarioGestorUnidade(String parte) {
        String sql = " " +
            "  select distinct lic.* from licitacao lic " +
            "         inner join exercicio ex on ex.id = lic.exercicio_id " +
            "         inner join processodecompra pdc on pdc.id = lic.processodecompra_id " +
            "         inner join loteprocessodecompra lpdc on pdc.id = lpdc.processodecompra_id " +
            "         inner join itemprocessodecompra ipdc on lpdc.id = ipdc.loteprocessodecompra_id " +
            "         inner join solicitacaomaterial sol on sol.id = pdc.solicitacaomaterial_id " +
            "         inner join cotacao cot on cot.id = sol.cotacao_id " +
            "         inner join formulariocotacao forn on forn.id = cot.formulariocotacao_id " +
            "         inner join usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = pdc.unidadeorganizacional_id " +
            "         inner join statuslicitacao status on status.id = (select max(id) from statuslicitacao sl where licitacao_id = lic.id) " +
            "  where (lic.numerolicitacao like :parte or lower(lic.resumodoobjeto) like :parte or lower(lic.modalidadelicitacao) like :parte or lic.NUMEROLICITACAO || '/' || ex.ANO like :parte) " +
            "   and ipdc.situacaoitemprocessodecompra = :item_homologado " +
            "   and status.tiposituacaolicitacao = :status_homologada " +
            "   and lic.modalidadelicitacao in (:modalidadesLicitacao) " +
            "   and uu.usuariosistema_id = :idUsuario " +
            "   and uu.gestorlicitacao = :gestor_licitacao " +
            "   and lic.id not in (select licirp.id  " +
            "                      from licitacao licirp  " +
            "                              inner join processodecompra pdc on pdc.id = licirp.processodecompra_id  " +
            "                              inner join solicitacaomaterial sol on sol.id = pdc.solicitacaomaterial_id  " +
            "                              inner join cotacao cot on cot.id = sol.cotacao_id  " +
            "                              inner join formulariocotacao forn on forn.id = cot.formulariocotacao_id  " +
            "                              inner join intencaoregistropreco irp on irp.id = forn.intencaoregistropreco_id)  " +
            "  order by lic.exercicio_id desc, lic.numerolicitacao desc ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("gestor_licitacao", Boolean.TRUE);
        q.setParameter("modalidadesLicitacao", Util.getListOfEnumName(ModalidadeLicitacao.getModalidadesLicitacao()));
        q.setParameter("item_homologado", SituacaoItemProcessoDeCompra.HOMOLOGADO.name());
        q.setParameter("status_homologada", TipoSituacaoLicitacao.HOMOLOGADA.name());
        return q.getResultList();
    }

    public List<Licitacao> buscarLicitacoesIrpUsuarioGestorUnidade(String parte) {
        String sql = " select distinct lic.* " +
            " from Licitacao lic " +
            "  inner join EXERCICIO ex on ex.ID = lic.EXERCICIO_ID " +
            "  inner join ProcessoDeCompra pdc on pdc.ID = lic.PROCESSODECOMPRA_ID " +
            "  inner join LoteProcessoDeCompra lpdc on pdc.ID = lpdc.PROCESSODECOMPRA_ID " +
            "  inner join ItemProcessoDeCompra ipdc on lpdc.ID = ipdc.LOTEPROCESSODECOMPRA_ID " +
            "  inner join solicitacaomaterial sol on sol.id = pdc.solicitacaomaterial_id  " +
            "  inner join cotacao cot on cot.id = sol.cotacao_id  " +
            "  inner join formulariocotacao forn on forn.id = cot.formulariocotacao_id  " +
            "  inner join intencaoregistropreco irp on irp.id = forn.intencaoregistropreco_id " +
            "  inner join participanteirp part on irp.id = part.intencaoregistrodepreco_id " +
            "  inner join unidadeparticipanteirp unidPart on unidPart.participanteirp_id = part.id  " +
            "     and to_date(:dataOperacao, 'dd/MM/yyyy') between trunc(unidPart.inicioVigencia) and coalesce(trunc(unidPart.fimVigencia), to_date(:dataOperacao, 'dd/MM/yyyy'))  " +
            "  inner join usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = unidPart.unidadeparticipante_id " +
            "  inner join StatusLicitacao status on status.id = (select max(id) from StatusLicitacao sl where licitacao_ID = lic.id) " +
            " where ipdc.situacaoItemProcessoDeCompra = :item_homologado " +
            "  and (lic.numerolicitacao like :parte or lower(lic.resumodoobjeto) like :parte or lower(lic.modalidadelicitacao) like :parte or lic.NUMEROLICITACAO || '/' || ex.ANO like :parte) " +
            "  and status.TIPOSITUACAOLICITACAO = :status_homologada " +
            "  and uu.gestorlicitacao = :gestor_licitacao " +
            "  and uu.usuariosistema_id = :idUsuario " +
            " order by lic.numeroLicitacao desc ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("gestor_licitacao", Boolean.TRUE);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("item_homologado", SituacaoItemProcessoDeCompra.HOMOLOGADO.name());
        q.setParameter("status_homologada", TipoSituacaoLicitacao.HOMOLOGADA.name());
        return q.getResultList();
    }

    public Long recuperarIdRevisaoAuditoriaLicitacao(Long idLicitacao) {
        String sql = " select rev.id from licitacao_aud aud " +
            " inner join revisaoauditoria rev on rev.id = aud.rev " +
            " where rev.id = (select rev from licitacao_aud laud where id = :idLicitacao and laud.revtype = 0) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLicitacao", idLicitacao);
        try {
            return ((BigDecimal) q.getSingleResult()).longValue();
        } catch (Exception ex) {
            return null;
        }
    }

    public List<LicitacaoVO> buscarLicitacoesVO(String condicao, TipoSituacaoLicitacao tipoSituacaoLicitacao) {
        String sql = " select distinct " +
            "  obj.id as idLicitacao, " +
            "  obj.numero as numeroModalidade, " +
            "  obj.numeroLicitacao as numeroProcessoLicitatorio, " +
            "  'Nº ' || pc.numero || '/' || exPc.ano || ' - ' || " +
            "  case pc.tipoProcessoDeCompra when :licitacao then 'Licitação' when :dispensaInexginilidade then 'Dispensa Licitação/Inexigibilidade' else '' end " +
            "  || ' - ' || " +
            "  case when length(pc.descricao) > 70 then substr(pc.descricao, 0, 70) || '...' else pc.descricao end as processoDeCompra, " +
            "  case when com.titulo is null then to_char(com.codigo) else com.titulo end as comissao," +
            "  vwadm.codigo || ' - ' || vwadm.descricao as unidade " +
            " from licitacao obj " +
            "  inner join unidadelicitacao ul on ul.licitacao_id = obj.id " +
            "  inner join VWHIERARQUIAADMINISTRATIVA vwadm on ul.unidadeadministrativa_id = vwadm.SUBORDINADA_ID " +
            "  inner join exercicio ex on obj.exercicio_id = ex.id " +
            "  inner join exercicio exModalidade on obj.exercicioModalidade_id = exModalidade.id " +
            "  INNER JOIN usuariounidadeorganizacio uu on uu.unidadeorganizacional_id = ul.unidadeadministrativa_id " +
            "  inner join ProcessoDeCompra pc on obj.PROCESSODECOMPRA_ID = pc.id " +
            "  inner join usuariosistema usu on usu.id = pc.USUARIOSISTEMA_ID " +
            "  left join pessoafisica pfUsu on usu.pessoafisica_id = pfUsu.id " +
            "  inner join solicitacaoMaterial solMat on pc.solicitacaoMaterial_id = solMat.id " +
            "  inner join exercicio exPc on pc.exercicio_id = exPc.ID " +
            "  inner join comissao com on obj.COMISSAO_ID = com.ID " +
            "  inner join StatusLicitacao status on status.LICITACAO_ID = obj.ID " +
            " where to_date(:dataOperacao, 'dd/MM/yyyy') between vwadm.INICIOVIGENCIA and coalesce(vwadm.FIMVIGENCIA, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "  AND status.DATASTATUS = (SELECT MAX(st.DATASTATUS) FROM statuslicitacao st WHERE st.licitacao_id = obj.ID ) " +
            "  AND status.TIPOSITUACAOLICITACAO = :tipoSituacao " +
            "  AND uu.gestorlicitacao = 1 " +
            "  AND uu.usuariosistema_id = :idUsuario " +
            condicao +
            " order by obj.id desc ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("idUsuario", sistemaFacade.getUsuarioCorrente().getId());
        q.setParameter("tipoSituacao", tipoSituacaoLicitacao.name());
        q.setParameter("licitacao", TipoProcessoDeCompra.LICITACAO.name());
        q.setParameter("dispensaInexginilidade", TipoProcessoDeCompra.DISPENSA_LICITACAO_INEXIGIBILIDADE.name());
        List<Object[]> resultado = q.getResultList();
        List<LicitacaoVO> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                LicitacaoVO licVo = new LicitacaoVO();
                licVo.setId(((BigDecimal) obj[0]).longValue());
                licVo.setNumero(obj[1] != null ? ((BigDecimal) obj[1]).intValue() : null);
                licVo.setNumeroLicitacao(obj[2] != null ? ((BigDecimal) obj[2]).intValue() : null);
                licVo.setProcessoDeCompra((String) obj[3]);
                licVo.setComissao((String) obj[4]);
                licVo.setUnidadeAdministrativa((String) obj[5]);
                retorno.add(licVo);
            }
        }
        return retorno;
    }

    public List<Licitacao> buscarLicitacaoPorTipoApuracao(String parte, TipoApuracaoLicitacao tipoApuracao) {
        String sql = "  " +
                " select distinct lic.* from licitacao lic " +
                "  inner join statuslicitacao st on st.licitacao_id = lic.id " +
                "  inner join exercicio e on e.id = lic.exercicio_id " +
                "  inner join processodecompra processo on lic.processodecompra_id = processo.id " +
                " where st.datastatus = (select max(status.datastatus) from statuslicitacao status where status.licitacao_id = lic.id) " +
                "  and st.tipoSituacaoLicitacao not in (" + StatusLicitacao.concatenarListaStatus() + ") " +
                "  and lic.tipoapuracao = :tipoApuracao " +
                "   and (lower(lic.resumodoobjeto) like :filter " +
                "    or lower(processo.numero) like :filter " +
                "    or to_char(lic.numerolicitacao) like :filter " +
                "    or to_char(lic.numerolicitacao) || '/' || to_char(e.ano) like :filter " +
                "    or replace(to_char(lic.numerolicitacao) || '/' || to_char(e.ano), '/', '') like :filter) " +
                " order by lic.numerolicitacao desc ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("filter", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoApuracao", tipoApuracao.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();

    }


    public List<Licitacao> buscarLicitacoesParaAlteracaoFonrecedor(String parte) {
        String sql = "  " +
            " select lic.* from licitacao lic " +
            "  inner join statuslicitacao st on st.licitacao_id = lic.id " +
            "  inner join exercicio e on e.id = lic.exercicio_id " +
            "  inner join processodecompra processo on lic.processodecompra_id = processo.id " +
            " where st.datastatus = (select max(status.datastatus) from statuslicitacao status where status.licitacao_id = lic.id) " +
            "  and st.tipoSituacaoLicitacao not in (" + StatusLicitacao.concatenarListaStatus() + ") " +
            "   and (lower(lic.resumodoobjeto) like :filter " +
            "    or lower(processo.numero) like :filter " +
            "    or to_char(lic.numerolicitacao) like :filter " +
            "    or to_char(lic.numerolicitacao) || '/' || to_char(e.ano) like :filter " +
            "    or replace(to_char(lic.numerolicitacao) || '/' || to_char(e.ano), '/', '') like :filter) " +
            " order by e.ano desc, lic.numerolicitacao desc ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("filter", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }


    public List<LoteProcessoDeCompra> buscarLotesExclusivoMicroPequenaEmpresa(Long idProcessoCompra) {
        String sql = " select distinct LOTEPC.* " +
            "    from LOTEPROCESSODECOMPRA lotepc " +
            "    inner join processodecompra pc on lotepc.PROCESSODECOMPRA_ID = pc.ID " +
            "    inner join SOLICITACAOMATERIAL sol on pc.SOLICITACAOMATERIAL_ID = sol.ID " +
            "    inner join cotacao c on sol.COTACAO_ID = c.ID " +
            "    inner join lotecotacao on c.ID = LOTECOTACAO.COTACAO_ID " +
            " where LOTECOTACAO.TIPOBENEFICIO = :tipoBeneficio and pc.id = :idProcessoCompra " ;

        Query q = em.createNativeQuery(sql, LoteProcessoDeCompra.class);
        q.setParameter("tipoBeneficio", TipoBeneficio.PARTICIPACAO_EXCLUSIVA.name());
        q.setParameter("idProcessoCompra", idProcessoCompra);
        try {
            return q.getResultList();
        } catch (NoResultException ex) {
            return Lists.newArrayList();
        }
    }

    public Licitacao salvarCredenciamento(Licitacao entity) {
        criarUnidadeLicitacao(entity);
        return em.merge(entity);
    }

    public Licitacao concluirCredenciamento(Licitacao entity) {
        if (entity.getNumeroLicitacao() == null) {
            entity.setNumeroLicitacao(singletonGeradorCodigoAdministrativo.gerarNumeroCredenciamento(entity.getExercicio(), entity.getProcessoDeCompra().getUnidadeOrganizacional(), sistemaFacade.getDataOperacao()));
        }
        homologarItemProcessoCompra(entity);
        StatusLicitacao status = statusLicitacaoFacade.getNovoStatus(TipoSituacaoLicitacao.HOMOLOGADA, entity, "Status criado ao concluir o credenciamento");
        entity.getListaDeStatusLicitacao().add(status);
        return em.merge(entity);
    }

    private void homologarItemProcessoCompra(Licitacao entity) {
        for (LicitacaoFornecedor forn : entity.getFornecedores()) {
            for (LotePropostaFornecedor lote : forn.getPropostaFornecedor().getLotes()) {
                for (ItemPropostaFornecedor item : lote.getItens()) {
                    ItemProcessoDeCompra itemProcesso = item.getItemProcessoDeCompra();
                    itemProcesso.setSituacaoItemProcessoDeCompra(SituacaoItemProcessoDeCompra.HOMOLOGADO);
                    em.merge(itemProcesso);
                }
            }
        }
    }

    public StatusLicitacaoFacade getStatusLicitacaoFacade() {
        return statusLicitacaoFacade;
    }

    public ConfiguracaoLicitacaoFacade getConfiguracaoLicitacaoFacade() {
        return configuracaoLicitacaoFacade;
    }

    public List<Licitacao> buscarLicitacoesPorLeiEData(Date dataInicial, Date dataFinal, LeiLicitacao leiLicitacao) {
        String sql = " select l.* " +
            " from licitacao l" +
            "          inner join processodecompra pc on l.processodecompra_id = pc.id " +
            "          inner join solicitacaomaterial sm on pc.solicitacaomaterial_id = sm.id " +
            "          inner join amparolegal al on sm.amparolegal_id = al.id " +
            "          inner join usuariounidadeorganizacio uuo on uuo.UNIDADEORGANIZACIONAL_ID = pc.UNIDADEORGANIZACIONAL_ID " +
            "          inner join vwhierarquiaadministrativa vw on vw.SUBORDINADA_ID = uuo.UNIDADEORGANIZACIONAL_ID " +
            " where al.leilicitacao = :leiLicitacao " +
            "   and l.emitidaem between :dataInicial and :dataFinal " +
            "   and to_date(:data, 'dd/mm/yyyy') between vw.inicioVigencia and coalesce(vw.fimvigencia, to_date(:data, 'dd/mm/yyyy')) " +
            "   and uuo.usuariosistema_id = :usuarioId ";
        Query q = em.createNativeQuery(sql, Licitacao.class);
        q.setParameter("leiLicitacao", leiLicitacao.name());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("data", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        q.setParameter("usuarioId", sistemaFacade.getUsuarioCorrente().getId());

        List<Licitacao> licitacoes = q.getResultList();
        for (Licitacao licitacao : licitacoes) {
            Hibernate.initialize(licitacao.getListaDeStatusLicitacao());
            Hibernate.initialize(licitacao.getProcessoDeCompra().getLotesProcessoDeCompra());
            for (LoteProcessoDeCompra loteProcessoDeCompra : licitacao.getProcessoDeCompra().getLotesProcessoDeCompra()) {
                Hibernate.initialize(loteProcessoDeCompra.getItensProcessoDeCompra());
            }
        }

        return licitacoes;
    }

    public List<MembroComissao> buscarMembrosComissaoSelecionados(Licitacao licitacao) {
        String sql = " select mc.* " +
            "      from licitacao lic " +
            " inner join licitmembcom lmc on lmc.licitacao_id = lic.id " +
            " inner join membrocomissao mc on mc.id = lmc.membrocomissao_id " +
            " inner join pessoafisica pf on pf.id = mc.pessoafisica_id " +
            "     where lic.id = :idLicitacao " +
            " order by pf.nome ";
        Query q = em.createNativeQuery(sql, MembroComissao.class);
        q.setParameter("idLicitacao", licitacao.getId());
        try {
            return q.getResultList();
        } catch (NoResultException ex) {
            return Lists.newArrayList();
        }
    }

    public List<Object[]> buscarExtratoLicitacaoParaPortal(Licitacao licitacao) {
        if (licitacao == null || licitacao.getId() == null) return null;
        String sql = " select funcional, " +
            "       fonte, " +
            "       conta, " +
            "       datareg, " +
            "       numero, " +
            "       operacao, " +
            "       coalesce(sum(valor), 0) as valor " +
            "from (select distinct desp.codigo                              as funcional, " +
            "                      fonte.codigo || ' - ' || fonte.descricao as fonte, " +
            "                      c.codigo || ' - ' || c.descricao         as conta, " +
            "                      trunc(emp.dataempenho)                   as datareg, " +
            "                      emp.numero                               as numero, " +
            "                      'Nota de Empenho'                        as operacao, " +
            "                      emp.valor                                as valor, " +
            "                      1                                        as ordem " +
            "      from empenho emp " +
            "               inner join despesaorc desp on emp.despesaorc_id = desp.id " +
            "               inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id " +
            "               inner join conta c on prov.contadedespesa_id = c.id " +
            "               inner join fontedespesaorc fontedesp on emp.fontedespesaorc_id = fontedesp.id " +
            "               inner join provisaoppafonte provfonte on fontedesp.provisaoppafonte_id = provfonte.id " +
            "               inner join contadedestinacao cd on provfonte.destinacaoderecursos_id = cd.id " +
            "               inner join conta ccd on cd.id = ccd.id " +
            "               inner join fontederecursos fonte on cd.fontederecursos_id = fonte.id " +
            "               inner join conlicitacao cl on cl.contrato_id = emp.contrato_id " +
            "      where emp.categoriaorcamentaria = :normal " +
            "        and cl.licitacao_id = :idLicitacao " +
            "      union all " +
            "      select distinct desp.codigo                              as funcional, " +
            "                      fonte.codigo || ' - ' || fonte.descricao as fonte, " +
            "                      c.codigo || ' - ' || c.descricao         as conta, " +
            "                      trunc(emp.dataempenho)                   as datareg, " +
            "                      emp.numero                               as numero, " +
            "                      'Restos a Pagar Processados'             as operacao, " +
            "                      emp.valor                                as valor, " +
            "                      2                                        as ordem " +
            "      from empenho emp " +
            "               inner join empenho empOriginal on emp.empenho_id = empOriginal.id " +
            "               inner join despesaorc desp on emp.despesaorc_id = desp.id " +
            "               inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id " +
            "               inner join conta c on prov.contadedespesa_id = c.id " +
            "               inner join fontedespesaorc fontedesp on emp.fontedespesaorc_id = fontedesp.id " +
            "               inner join provisaoppafonte provfonte on fontedesp.provisaoppafonte_id = provfonte.id " +
            "               inner join contadedestinacao cd on provfonte.destinacaoderecursos_id = cd.id " +
            "               inner join conta ccd on cd.id = ccd.id " +
            "               inner join fontederecursos fonte on cd.fontederecursos_id = fonte.id " +
            "               inner join conlicitacao cl on cl.contrato_id = emp.contrato_id " +
            "      WHERE emp.categoriaorcamentaria = :resto " +
            "        and emp.tiporestosprocessados = :processados " +
            "        and cl.licitacao_id = :idLicitacao " +
            "      union all " +
            "      select distinct desp.codigo                                as funcional, " +
            "                      fonte.codigo || ' - ' || fonte.descricao   as fonte, " +
            "                      c.codigo || ' - ' || c.descricao           as conta, " +
            "                      trunc(emp.dataempenho)                     as datareg, " +
            "                      emp.numero                                 as numero, " +
            "                      'Restos a Pagar Não-Processados Inscritos' as operacao, " +
            "                      emp.valor                                  as valor, " +
            "                      3                                          as ordem " +
            "      from empenho emp " +
            "               inner join empenho empOriginal on emp.empenho_id = empOriginal.id " +
            "               inner join despesaorc desp on emp.despesaorc_id = desp.id " +
            "               inner join provisaoppadespesa prov on desp.provisaoppadespesa_id = prov.id " +
            "               inner join conta c on prov.contadedespesa_id = c.id " +
            "               inner join fontedespesaorc fontedesp on emp.fontedespesaorc_id = fontedesp.id " +
            "               inner join provisaoppafonte provfonte on fontedesp.provisaoppafonte_id = provfonte.id " +
            "               inner join contadedestinacao cd on provfonte.destinacaoderecursos_id = cd.id " +
            "               inner join conta ccd on cd.id = ccd.id " +
            "               inner join fontederecursos fonte on cd.fontederecursos_id = fonte.id " +
            "               inner join conlicitacao cl on cl.contrato_id = emp.contrato_id " +
            "      WHERE emp.categoriaorcamentaria = :resto " +
            "        and emp.tiporestosprocessados = :naoProcessados " +
            "        and cl.licitacao_id = :idLicitacao) reg " +
            "group by datareg, numero, operacao, funcional, fonte, conta, ordem " +
            "order by ordem, numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idLicitacao", licitacao.getId());
        q.setParameter("normal", CategoriaOrcamentaria.NORMAL.name());
        q.setParameter("resto", CategoriaOrcamentaria.RESTO.name());
        q.setParameter("processados", TipoRestosProcessado.PROCESSADOS.name());
        q.setParameter("naoProcessados", TipoRestosProcessado.NAO_PROCESSADOS.name());
        return q.getResultList();
    }
}
