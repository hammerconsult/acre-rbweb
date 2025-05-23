/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.GrupoContaDespesa;
import br.com.webpublico.entidadesauxiliares.ValidacaoObjetoCompraEspecificacao;
import br.com.webpublico.enums.SituacaoObjetoCompra;
import br.com.webpublico.enums.TipoObjetoCompra;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author Romanini
 */
@Stateless
public class ObjetoCompraFacade extends AbstractFacade<ObjetoCompra> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoObjetoCompraFacade grupoObjetoCompraFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private GrupoObjetoCompraGrupoBemFacade associacaoGrupoBemFacade;
    @EJB
    private AssociacaoGrupoObjetoCompraGrupoMaterialFacade associacaoGrupoMaterialFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConfigGrupoMaterialFacade configGrupoMaterialFacade;
    @EJB
    private ConfigDespesaBensFacade configGrupoBemFacade;
    @EJB
    private DerivacaoObjetoCompraFacade derivacaoObjetoCompraFacade;

    public ObjetoCompraFacade() {
        super(ObjetoCompra.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public GrupoObjetoCompraFacade getGrupoObjetoCompraFacade() {
        return grupoObjetoCompraFacade;
    }

    public long gerarCodigoNovo() {
        String hql = "from ObjetoCompra a where a.codigo = (select max(b.codigo) from ObjetoCompra b)";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        if (!q.getResultList().isEmpty()) {
            return ((ObjetoCompra) q.getSingleResult()).getCodigo() + 1;
        } else {
            return 1;
        }
    }

    public Boolean validaCodigoRepetido(ObjetoCompra objetoCompra) {
        String sql = "select o.* from ObjetoCompra o where o.codigo = :codigo";
        if (objetoCompra.getId() != null) {
            sql += " and o.id <> :id";
        }
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("codigo", objetoCompra.getCodigo());
        if (objetoCompra.getId() != null) {
            q.setParameter("id", objetoCompra.getId());
        }
        if (q.getResultList().isEmpty()) {
            return true;
        }
        return false;
    }

    public List<ObjetoCompra> completaObjetoCompra(String parte) {
        String sql = " select *"
            + "      from objetocompra"
            + "     where (lower(descricao) like :parte"
            + "        or to_char(codigo) like :parte)" +
            "          and situacaoObjetoCompra = '" + SituacaoObjetoCompra.DEFERIDO + "'" +
            "          and ativo = 1";
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<ObjetoCompra> buscarObjetoCompraPorCodigoOrDescricaoAndListTipoObjetoCompra(String codigoOrDescricao, List<TipoObjetoCompra> tiposObjetoCompra) {
        List<ObjetoCompra> toReturn = Lists.newArrayList();
        for (TipoObjetoCompra tipoObjetoCompra : tiposObjetoCompra) {
            toReturn.addAll(buscarObjetoCompraPorCodigoOrDescricaoAndTipoObjetoCompra(codigoOrDescricao, tipoObjetoCompra));
        }
        return toReturn;
    }

    public List<ObjetoCompra> buscarObjetoCompraPorDescricaoOrcodigoAndListTipoObjetoCompra(String codigoOrDescricao, List<TipoObjetoCompra> tiposObjetoCompra) {
        List<ObjetoCompra> toReturn = Lists.newArrayList();
        for (TipoObjetoCompra tipoObjetoCompra : tiposObjetoCompra) {
            toReturn.addAll(buscarObjetoCompraPorCodigoOrDescricaoAndTipoObjetoCompra(codigoOrDescricao, tipoObjetoCompra));
        }
        return toReturn;
    }


    public List<ObjetoCompra> buscarObjetoCompraPorCodigoOrDescricaoAndTipoObjetoCompra(String codigoOrDescricao, TipoObjetoCompra tipoObjetoCompra) {
        if (tipoObjetoCompra == null) {
            return Lists.newArrayList();
        }
        String sql = " select *"
            + "      from objetocompra"
            + "     where (lower(descricao) like :codigoOrDescricao"
            + "        or to_char(codigo) like :codigoOrDescricao)" +
            "         and tipoobjetocompra = :tipoObjetoCompra " +
            "         and situacaoObjetoCompra = '" + SituacaoObjetoCompra.DEFERIDO + "'" +
            "         and ativo = 1";

        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("codigoOrDescricao", "%" + codigoOrDescricao.trim().toLowerCase() + "%");
        q.setParameter("tipoObjetoCompra", tipoObjetoCompra.name());
        q.setMaxResults(50);
        return q.getResultList();
    }

    public List<ObjetoCompra> buscarObjetoCompraNaoReferenciaPorTipoObjetoCompra(String parte, TipoObjetoCompra tipoObjetoCompra) {
        if (tipoObjetoCompra == null) {
            return Lists.newArrayList();
        }
        String sql = " select * from objetocompra " +
            "          where (lower(" + Util.getTranslate("descricao") + ") like " + Util.getTranslate(":parte") +
            "            or to_char(codigo) like :parte) " +
            "           and tipoobjetocompra = :tipoObjetoCompra " +
            "           and situacaoObjetoCompra = :deferido " +
            "           and ativo = :ativo " +
            "           and referencial = :referencial ";
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoObjetoCompra", tipoObjetoCompra.name());
        q.setParameter("ativo", Boolean.TRUE);
        q.setParameter("referencial", Boolean.FALSE);
        q.setParameter("deferido", SituacaoObjetoCompra.DEFERIDO.name());
        try {
            return q.getResultList();
        } catch (NoResultException nre) {
            return Lists.newArrayList();
        }
    }

    public List<ObjetoCompra> completaObjetoCompraTipoConsumo(String parte) {
        String sql = " select *"
            + "      from objetocompra"
            + "     where (lower(descricao) like :parte"
            + "        or to_char(codigo) like :parte)" +
            "         and tipoobjetocompra = :tipo_consumo" +
            "         and situacaoObjetoCompra = '" + SituacaoObjetoCompra.DEFERIDO + "'" +
            "         and ativo = 1";
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipo_consumo", TipoObjetoCompra.CONSUMO.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<ObjetoCompra> buscarObjetoCompraPorSituacao(String parte, SituacaoObjetoCompra situacao) {
        String sql = " select *"
            + "      from objetocompra"
            + "     where (lower(descricao) like :parte"
            + "        or to_char(codigo) like :parte)" +
            "         and situacaoObjetoCompra = :situacao ";
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("situacao", situacao.name());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<ObjetoCompra> buscarPorGrupoObjetoCompra(String parte, GrupoObjetoCompra grupo) {
        String sql = " select * from objetocompra" +
            "          where grupoobjetocompra_id = :idGoc "
            + "        and (lower(descricao) like :parte or to_char(codigo) like :parte)" +
            "          and situacaoObjetoCompra = :deferido " +
            "          order by codigo ";
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("deferido", SituacaoObjetoCompra.DEFERIDO.name());
        q.setParameter("idGoc", grupo.getId());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public ObjetoCompra recuperarPorCodigoOuDescricao(String codigoDescricao) {
        String sql = "select * " +
            "from objetocompra " +
            "where to_char(codigo) = :codigo_descricao " +
            "or lower(descricao) = :codigo_descricao ";

        Query q = em.createNativeQuery(sql, ObjetoCompra.class).setParameter("codigo_descricao", codigoDescricao.toLowerCase().trim());
        List resultList = q.getResultList();

        if (resultList.isEmpty()) {
            return null;
        }

        return (ObjetoCompra) resultList.get(0);
    }

    @Override
    public ObjetoCompra recuperar(Object id) {
        ObjetoCompra obj = super.recuperar(id);
        Hibernate.initialize(obj.getEspecificacaoes());
        return obj;
    }

    public Boolean utilizadoEmProcessoLicitatorio(ObjetoCompra objetoCompra) {
        String sql = "" +
            "   select distinct oc.* from objetocompra oc " +
            "       inner join itemintencaoregistropreco item on item.objetocompra_id = oc.id " +
            "   where item.objetocompra_id = :idObjetoCompra " +
            "   union all " +
            "   select distinct oc.* from objetocompra oc " +
            "       inner join itemloteformulariocotacao item on item.objetocompra_id = oc.id " +
            "   where item.objetocompra_id = :idObjetoCompra " +
            "   union all " +
            "   select distinct oc.* from objetocompra oc " +
            "       inner join itemcotacao item on item.objetocompra_id = oc.id " +
            "   where item.objetocompra_id = :idObjetoCompra " +
            "   union all " +
            "   select distinct oc.* from objetocompra oc " +
            "      inner join itemrequisicaodecompra item on item.objetocompra_id = oc.id " +
            "   where item.objetocompra_id = :idObjetoCompra";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idObjetoCompra", objetoCompra.getId());
        return !q.getResultList().isEmpty();
    }

    public ObjetoDeCompraEspecificacao salvarEspecificacao(ObjetoDeCompraEspecificacao especificacao) {
        return em.merge(especificacao);
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    @Override
    public ObjetoCompra salvarRetornando(ObjetoCompra entity) {
        if (entity.getCodigo() == null) {
            entity.setCodigo(getSingletonGeradorCodigo().getProximoCodigo(ObjetoCompra.class, "codigo"));
        }
        return super.salvarRetornando(entity);
    }

    public List<ObjetoCompra> buscarObjetoCompraPorGrupoOrSituacao(GrupoObjetoCompra grupo, SituacaoObjetoCompra situacao, ObjetoCompra objetoCompra) {
        String sql = " select obj.* from objetocompra obj " +
            "            where obj.situacaoObjetoCompra <> :situacaoDeferido ";
        if (grupo != null) {
            sql += " and obj.grupoObjetoCompra_id = :idGrupo ";
        }
        if (objetoCompra != null) {
            sql += " and obj.id = :idObjetoCompra ";
        }
        if (situacao != null) {
            sql += " and obj.situacaoObjetoCompra = :situacao ";
        }
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("situacaoDeferido", SituacaoObjetoCompra.DEFERIDO.name());
        if (grupo != null) {
            q.setParameter("idGrupo", grupo.getId());
        }
        if (objetoCompra != null) {
            q.setParameter("idObjetoCompra", objetoCompra.getId());
        }
        if (situacao != null) {
            q.setParameter("situacao", situacao.name());
        }
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<ObjetoCompra> buscarObjetoCompraPorDescricaoAndSituacao(String descricao, SituacaoObjetoCompra situacao) {
        String sql = " select obj.* from objetocompra obj " +
            " where obj.situacaoObjetoCompra = :situacao " +
            "   and soundex(upper(obj.descricao)) = soundex(:descricao) ";
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("situacao", situacao.name());
        q.setParameter("descricao", descricao.trim().replace(" ", "|").toUpperCase());
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }

    public List<ObjetoCompra> buscarObjetoCompraConsumoPorGrupoMaterial(String parte, Long idGrupo, Date dataOperacao) {
        String sql = " select obc.*  from objetocompra obc " +
                "           inner join grupoobjetocompra grupo on grupo.id = obc.grupoobjetocompra_id " +
                "           inner join associacaogruobjcomgrumat associacao on grupo.id = associacao.grupoobjetocompra_id " +
                "      where to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(associacao.iniciovigencia) and coalesce(trunc(associacao.finalvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
                "       and (lower(" + Util.getTranslate("obc.descricao") + ") like " + Util.getTranslate(":parte") +
                "            or to_char(obc.codigo) like :parte) " +
                "       and obc.tipoobjetocompra = :tipoObjetoCompra " +
                "       and associacao.grupomaterial_id = :idGrupo " +
                "       and obc.situacaoObjetoCompra = :situacao " +
                "       and obc.ativo = :ativo " +
                "       and obc.referencial = :referencial";
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoObjetoCompra", TipoObjetoCompra.CONSUMO.name());
        q.setParameter("situacao", SituacaoObjetoCompra.DEFERIDO.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("idGrupo", idGrupo);
        q.setParameter("ativo", Boolean.TRUE);
        q.setParameter("referencial", Boolean.FALSE);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<ObjetoCompra> buscarObjetoCompraPermanentePorGrupoPatrimonial(String parte, Long idGrupo, Date dataOperacao) {
        String sql = " " +
            " select obc.*  from objetocompra obc " +
            "   inner join grupoobjetocompra grupo on grupo.id = obc.grupoobjetocompra_id " +
            "   inner join grupoobjcompragrupobem associacao on grupo.id = associacao.grupoobjetocompra_id " +
            " where to_date(:dataOperacao, 'dd/MM/yyyy') BETWEEN trunc(associacao.iniciovigencia) and coalesce(trunc(associacao.fimvigencia), to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "   and (lower(" + Util.getTranslate("obc.descricao") + ") like " + Util.getTranslate(":parte") +
            "        or to_char(obc.codigo) like :parte) " +
            "   and obc.tipoobjetocompra = :tipoObjetoCompra " +
            "   and associacao.grupobem_id = :idGrupo " +
            "   and obc.situacaoObjetoCompra = :situacao " +
            "   and obc.ativo = :ativo " +
            "   and obc.referencial = :referencial";
        Query q = em.createNativeQuery(sql, ObjetoCompra.class);
        q.setParameter("parte", "%" + parte.trim().toLowerCase() + "%");
        q.setParameter("tipoObjetoCompra", TipoObjetoCompra.PERMANENTE_MOVEL.name());
        q.setParameter("situacao", SituacaoObjetoCompra.DEFERIDO.name());
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("idGrupo", idGrupo);
        q.setParameter("ativo", Boolean.TRUE);
        q.setParameter("referencial", Boolean.FALSE);
        q.setMaxResults(MAX_RESULTADOS_NA_CONSULTA);
        return q.getResultList();
    }

    public List<ObjetoDeCompraEspecificacao> buscarEspecificacoes(ObjetoCompra objetoCompra) {
        String sql = " " +
            " select esp.* from objetocompraespecificacao esp " +
            "   where esp.objetocompra_id = :idObjetoCompra " +
            "   and esp.ativo = :ativo ";
        Query q = em.createNativeQuery(sql, ObjetoDeCompraEspecificacao.class);
        q.setParameter("idObjetoCompra", objetoCompra.getId());
        q.setParameter("ativo", Boolean.TRUE);
        if (q.getResultList() != null && !q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return Lists.newArrayList();
    }

    public void adicionarNovaEspecificacao(ObjetoCompra objetoCompra, String especificacao, List<ObjetoDeCompraEspecificacao> especificacoes) {
        boolean deveAdicionarNovo = true;
        objetoCompra = recuperar(objetoCompra.getId());
        for (ObjetoDeCompraEspecificacao objetoEspecificacao : objetoCompra.getEspecificacaoesAtivas()) {
            if (objetoEspecificacao.getTexto().trim().equals(especificacao.trim())) {
                deveAdicionarNovo = false;
            }
        }
        for (ObjetoDeCompraEspecificacao objetoEspecificacao : especificacoes) {
            if (objetoEspecificacao.getTexto().trim().equals(especificacao.trim())) {
                deveAdicionarNovo = false;
            }
        }
        if (deveAdicionarNovo) {
            ObjetoDeCompraEspecificacao objetoEspecificacao = new ObjetoDeCompraEspecificacao();
            objetoEspecificacao.setObjetoCompra(objetoCompra);
            objetoEspecificacao.setTexto(especificacao);
            objetoEspecificacao.setAtivo(true);
            especificacoes.add(objetoEspecificacao);
        }
    }

    public void salvarEspecificacoes(List<ObjetoDeCompraEspecificacao> especificacoes) {
        if (!especificacoes.isEmpty()) {
            for (ObjetoDeCompraEspecificacao especificacao : especificacoes) {
                salvarEspecificacao(especificacao);
            }
        }
    }

    public GrupoContaDespesa criarGrupoContaDespesa(TipoObjetoCompra tipoObjetoCompra, GrupoObjetoCompra grupoObjetoCompra) {
        List<Conta> contasDespConfig = Lists.newArrayList();
        GrupoContaDespesa grupoContaDespesa = new GrupoContaDespesa(tipoObjetoCompra);
        grupoContaDespesa.setGrupoObjetoCompra(grupoObjetoCompra);
        switch (tipoObjetoCompra) {
            case CONSUMO:
                AssociacaoGrupoObjetoCompraGrupoMaterial associacaoGrupoMat = associacaoGrupoMaterialFacade.recuperarAssociacaoVigentePorGrupoObjetoCompra(grupoObjetoCompra, sistemaFacade.getDataOperacao());
                if (associacaoGrupoMat == null) {
                    grupoContaDespesa.getMensagensValidacao().add("Nenhuma associação de grupo material encontrada para grupo de objeto de compra: " + grupoObjetoCompra.getCodigo());
                    return grupoContaDespesa;
                }
                GrupoMaterial grupoMaterial = associacaoGrupoMat.getGrupoMaterial();
                grupoContaDespesa.setCodigoGrupo(grupoMaterial.getCodigo());
                grupoContaDespesa.setDescricaoGrupo(grupoMaterial.getDescricao());
                grupoContaDespesa.setGrupo(grupoMaterial.toString());
                grupoContaDespesa.setIdGrupo(grupoMaterial.getId());

                contasDespConfig = configGrupoMaterialFacade.buscarContasDespesaPorGrupoMaterial(grupoMaterial, sistemaFacade.getDataOperacao(), null);
                if (Util.isListNullOrEmpty(contasDespConfig)) {
                    grupoContaDespesa.getMensagensValidacao().add("Nenhuma conta de despesa configurada para o grupo: " + grupoContaDespesa.getGrupo() + ".");
                    return grupoContaDespesa;
                }
                grupoContaDespesa.getContasDespesa().addAll(contasDespConfig);
                return grupoContaDespesa;
            case PERMANENTE_MOVEL:
                GrupoObjetoCompraGrupoBem associacaoGrupoBem = associacaoGrupoBemFacade.recuperarAssociacaoDoGrupoObjetoCompra(grupoObjetoCompra, sistemaFacade.getDataOperacao());
                if (associacaoGrupoBem == null) {
                    grupoContaDespesa.getMensagensValidacao().add("Nenhuma associação de grupo patrimonial encontrada para grupo de objeto de compra: " + grupoObjetoCompra.getCodigo());
                    return grupoContaDespesa;
                }
                GrupoBem grupoBem = associacaoGrupoBem.getGrupoBem();
                grupoContaDespesa.setCodigoGrupo(grupoBem.getCodigo());
                grupoContaDespesa.setDescricaoGrupo(grupoBem.getDescricao());
                grupoContaDespesa.setGrupo(grupoBem.toString());
                grupoContaDespesa.setIdGrupo(grupoBem.getId());

                contasDespConfig = configGrupoBemFacade.buscarContasPorGrupoBem(grupoBem, sistemaFacade.getDataOperacao(), null);
                if (Util.isListNullOrEmpty(contasDespConfig)) {
                    grupoContaDespesa.getMensagensValidacao().add("Nenhuma conta de despesa configurada para o grupo: " + grupoContaDespesa.getGrupo() + ".");
                    return grupoContaDespesa;
                }
                grupoContaDespesa.getContasDespesa().addAll(contasDespConfig);
                return grupoContaDespesa;
            default:
                grupoContaDespesa.getMensagensValidacao().add("O grupo objeto de compra não possui configuração de grupo e conta.");
                return grupoContaDespesa;
        }
    }

    public void validarObjetoCompraAndEspecificacaoIguais(ValidacaoObjetoCompraEspecificacao validacao) {
        ValidacaoException ve = new ValidacaoException();
        String complementoMsg = validacao.getNumeroLote() != null ?
            " Lote " + validacao.getNumeroLote() + " e Item " + validacao.getNumeroItem() + "."
            : " Item " + validacao.getNumeroItem() + ".";

        if (validacao.getObjetoCompraSelecionado().equals(validacao.getObjetoCompraEmLista())) {
            if (Strings.isNullOrEmpty(validacao.getEspeficicacaoEmLista())
                && Strings.isNullOrEmpty(validacao.getEspeficicacaoSelecionada())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Quando o mesmo objeto de compra é adicionado mais de uma vez, o campo Especificação deve ser informado.");
                ve.adicionarMensagemDeOperacaoNaoPermitida("", "Objeto de compra já adicionado no " + complementoMsg);
            }
            if (!Strings.isNullOrEmpty(validacao.getEspeficicacaoEmLista())
                && !Strings.isNullOrEmpty(validacao.getEspeficicacaoSelecionada())
                && validacao.getEspeficicacaoSelecionada().trim().equalsIgnoreCase(validacao.getEspeficicacaoEmLista().trim())
                && Objects.equals(validacao.getTipoBeneficioSelecionado(), validacao.getTipoBeneficioEmLista())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe um objeto de compra com a mesma especificação e tipo de benefício adicionado no " + complementoMsg);
            }
        }
        ve.lancarException();
    }

    public DerivacaoObjetoCompraFacade getDerivacaoObjetoCompraFacade() {
        return derivacaoObjetoCompraFacade;
    }
}
