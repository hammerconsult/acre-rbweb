/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloTipoDoctoOficial;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.enums.TipoDocumentoOficialPortal;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author leonardo
 */
@Stateless
public class TipoDoctoOficialFacade extends AbstractFacade<TipoDoctoOficial> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TextoFixoFacade textoFixoFacade;
    @EJB
    private GrupoDoctoOficialFacade grupoDoctoOficialFacade;
    @EJB
    private TributoFacade tributoFacade;
    @EJB
    private FinalidadeFacade finalidadeFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public TipoDoctoOficialFacade() {
        super(TipoDoctoOficial.class);
    }

    public GrupoDoctoOficialFacade getGrupoDoctoOficialFacade() {
        return grupoDoctoOficialFacade;
    }

    public TributoFacade getTributoFacade() {
        return tributoFacade;
    }

    public FinalidadeFacade getFinalidadeFacade() {
        return finalidadeFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    @Override
    protected EntityManager getEntityManager() {
        return this.em;
    }

    @Override
    public TipoDoctoOficial recuperar(Object id) {
        TipoDoctoOficial tipoDoctoOficial = em.find(TipoDoctoOficial.class, id);
        tipoDoctoOficial.getListaAtributosDoctoOficial().size();
        tipoDoctoOficial.getTipoDoctoFinalidades().size();
        tipoDoctoOficial.getListaUsuarioTipoDocto().size();
        tipoDoctoOficial.getListaModeloDoctoOficial().size();
        tipoDoctoOficial.getAssinaturas().size();
        return tipoDoctoOficial;
    }

    public Long ultimoCodigoMaisUm() {
        Query q = em.createNativeQuery("SELECT coalesce(max(codigo), 0) + 1 AS codigo FROM TipoDoctoOficial tp");
        BigDecimal resultado = (BigDecimal) q.getSingleResult();
        return resultado.longValue();
    }

    public Long ultimoCodigo() {
        String sql = "SELECT max(codigo) FROM TipoDoctoOficial";
        Query q = em.createNativeQuery(sql);
        String resultado = (String) q.getResultList().get(0);
        return resultado != null ? new BigDecimal(resultado).longValue() : 1;
    }

    public boolean existeCodigo(Long codigo) {
        String sql = "SELECT * FROM TipoDoctoOficial WHERE codigo = :codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", codigo);
        return !q.getResultList().isEmpty();
    }

    public boolean existeCodigoTipoDoctoOficial(TipoDoctoOficial tipo) {
        String sql = "SELECT * FROM TipoDoctoOficial WHERE codigo = :codigo AND id != :id";
        Query q = em.createNativeQuery(sql);
        q.setParameter("codigo", tipo.getCodigo());
        q.setParameter("id", tipo.getId());
        return !q.getResultList().isEmpty();
    }

    public List<Finalidade> listaFinalidadePeloTipoDocto(TipoDoctoOficial tipo, String parte) {

        String hql = "select fin.finalidade from TipoDoctoOficial tipoDoc left join tipoDoc.tipoDoctoFinalidades fin where tipoDoc = :tipo and lower(fin.finalidade.descricao) like :parte";
        Query q = em.createQuery(hql);
        q.setParameter("tipo", tipo);
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");

        return q.getResultList();
    }

    public List<AtributoDoctoOficial> listaAtributosPorTipoDocto() {
        String sql = "SELECT att.* FROM TipoDoctoOficial tipoDoc JOIN AtributoDoctoOficial att ON tipodoc.id = att.tipodoctooficial_id ";
        Query q = em.createNativeQuery(sql, AtributoDoctoOficial.class);
        // q.setParameter("tipo", tipoDoctoOficial);
        return q.getResultList();
    }

    public List<AtributoDoctoOficial> recuperaAtributoPorTipoDocto(TipoDoctoOficial tipoDoctoOficial) {
        String sql = "SELECT ado.* FROM atributodoctooficial ado WHERE ado.tipodoctooficial_id = :tipo";
        Query q = em.createNativeQuery(sql, AtributoDoctoOficial.class);
        q.setParameter("tipo", tipoDoctoOficial.getId());
        return q.getResultList();
    }

    public ModeloDoctoOficial recuperaModeloVigente(TipoDoctoOficial tipoDoctoOficial) {
        String hql = "from ModeloDoctoOficial where tipoDoctoOficial = :tipo and vigenciaFinal is null";
        Query q = em.createQuery(hql);
        q.setMaxResults(1);
        q.setParameter("tipo", tipoDoctoOficial);
        if (q.getResultList().size() > 0) {
            return (ModeloDoctoOficial) q.getSingleResult();
        } else {
            return null;
        }
    }

    public List<TipoDoctoOficial> tipoDoctoPorUsuario(String parte, UsuarioSistema usuarioSistema) {
        String sql = "SELECT td.* FROM tipodoctooficial td "
            + "JOIN usuariotipodocto utd ON utd.tipodoctooficial_id = td.id "
            + "AND utd.usuariosistema_id = :usuario "
            + "AND lower(td.descricao) LIKE :parte "
            + "WHERE td.moduloTipoDoctoOficial = 'SOLICITACAO'";
        Query q = em.createNativeQuery(sql, TipoDoctoOficial.class);
        q.setParameter("usuario", usuarioSistema.getId());
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");

        return q.getResultList();
    }

    public List<TipoDoctoOficial> tipoDoctoPorUsuario(UsuarioSistema usuarioSistema) {
        String sql = "SELECT td.* FROM tipodoctooficial td "
            + "JOIN usuariotipodocto utd ON utd.tipodoctooficial_id = td.id "
            + "AND utd.usuariosistema_id = :usuario "
            + "WHERE td.moduloTipoDoctoOficial = 'SOLICITACAO' order by td.descricao ";
        Query q = em.createNativeQuery(sql, TipoDoctoOficial.class);
        q.setParameter("usuario", usuarioSistema.getId());
        return q.getResultList();
    }

    public List<TipoDoctoOficial> tipoDoctoPorModulo(String parte, ModuloTipoDoctoOficial modulo) {
        String sql = "SELECT td.* FROM tipodoctooficial td "
            + "WHERE lower(td.descricao) LIKE :parte "
            + "AND td.moduloTipoDoctoOficial = :modulo";
        Query q = em.createNativeQuery(sql, TipoDoctoOficial.class);
        q.setParameter("modulo", modulo.name());
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");

        return q.getResultList();
    }

    public TipoDoctoOficial buscarTipoDoctoPorModulo(ModuloTipoDoctoOficial modulo) {
        String sql = "SELECT td.* FROM tipodoctooficial td "
            + "WHERE  td.moduloTipoDoctoOficial = :modulo";
        Query q = em.createNativeQuery(sql, TipoDoctoOficial.class);
        q.setParameter("modulo", modulo.name());
        if (!q.getResultList().isEmpty()) {
            return (TipoDoctoOficial) q.getResultList().get(0);
        }
        return null;
    }

    public List<TipoDoctoOficial> tipoDoctoPorModuloTipoCadastro(String parte, ModuloTipoDoctoOficial modulo, TipoCadastroDoctoOficial... tipoCadastro) {
        String sql = "SELECT td.* FROM tipodoctooficial td "
            + " WHERE lower(td.descricao) LIKE :parte "
            + " AND td.moduloTipoDoctoOficial = :modulo";
        if (tipoCadastro != null && tipoCadastro.length > 0) {
            sql += " AND (";
            for (int i = 0; i < tipoCadastro.length; i++) {
                if (i > 0) sql += " OR ";
                sql += "td.tipoCadastroDoctoOficial = :" + tipoCadastro[i].name();
            }
            sql += ") ";
        }
        Query q = em.createNativeQuery(sql, TipoDoctoOficial.class);
        q.setParameter("modulo", modulo.name());
        if (tipoCadastro != null && tipoCadastro.length > 0) {
            for (TipoCadastroDoctoOficial tipoCadastroDoctoOficial : tipoCadastro) {
                q.setParameter(tipoCadastroDoctoOficial.name(), tipoCadastroDoctoOficial.name());
            }
        }
        q.setParameter("parte", "%" + parte.toLowerCase() + "%");

        return q.getResultList();
    }

    public TipoDoctoOficial buscarTipoDoctoProcessoProtesto(ProcessoDeProtesto processo) {
        String sql = "SELECT td.* FROM tipodoctooficial td "
            + " WHERE td.moduloTipoDoctoOficial = :modulo"
            + " AND td.tipoCadastroDoctoOficial = :tipoCadastro";

        TipoCadastroDoctoOficial tipoCadastro = TipoCadastroDoctoOficial.NENHUM;
        if (processo.getCadastroProtesto() != null) {
            switch (processo.getTipoCadastroTributario()) {
                case IMOBILIARIO:
                    tipoCadastro = TipoCadastroDoctoOficial.CADASTROIMOBILIARIO;
                    break;
                case ECONOMICO:
                    tipoCadastro = TipoCadastroDoctoOficial.CADASTROECONOMICO;
                    break;
                case RURAL:
                    tipoCadastro = TipoCadastroDoctoOficial.CADASTRORURAL;
                    break;
            }
        } else if (processo.getPessoaProtesto() != null && processo.getPessoaProtesto() instanceof PessoaFisica) {
            tipoCadastro = TipoCadastroDoctoOficial.PESSOAFISICA;
        } else if (processo.getPessoaProtesto() != null && processo.getPessoaProtesto() instanceof PessoaJuridica) {
            tipoCadastro = TipoCadastroDoctoOficial.PESSOAJURIDICA;
        }

        Query q = em.createNativeQuery(sql, TipoDoctoOficial.class);
        q.setParameter("modulo", ModuloTipoDoctoOficial.PROCESSO_PROTESTO.name());
        q.setParameter("tipoCadastro", tipoCadastro.name());

        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }
        return (TipoDoctoOficial) resultList.get(0);
    }

    public boolean existeAtributoNaSolicitacao(AtributoDoctoOficial atributo) {
        String hql = "from ValorAtributoSolicitacao v where v.atributoDoctoOficial = :atributo";
        Query q = em.createQuery(hql);
        q.setParameter("atributo", atributo);
        return !q.getResultList().isEmpty();
    }

    public TextoFixoFacade getTextoFixoFacade() {
        return textoFixoFacade;
    }

    public List<TipoDoctoOficial> recuperaTiposDoctoOficialPorModulo(ModuloTipoDoctoOficial mod, String parte) {
        Query q = em.createQuery("from TipoDoctoOficial tipo"
            + " where upper(tipo.descricao) like :filtro"
            + " and  tipo.moduloTipoDoctoOficial = :mod");
        q.setParameter("mod", mod);
        q.setParameter("filtro", "%" + parte.toUpperCase() + "%");
        return q.getResultList();
    }

    public List<TipoDoctoOficial> buscarTiposDoctoOficialPorModulos(String parte, List<ModuloTipoDoctoOficial> modulos) {
        Query q = em.createQuery("from TipoDoctoOficial tipo"
            + " where upper(tipo.descricao) like :filtro"
            + " and  tipo.moduloTipoDoctoOficial in :modulos");
        q.setParameter("modulos", modulos);
        q.setParameter("filtro", "%" + parte.toUpperCase() + "%");
        return q.getResultList();
    }

    public TipoDoctoOficial buscarTipoDoctoOficial(TipoDocumentoOficialPortal tipoDocumentoOficialPortal, TipoCadastroDoctoOficial tipoCadastroDoctoOficial) {
        String sql = " select tipo.* from tipodoctooficial tipo " +
            " where tipo.tipovalidacaodoctooficial = :tipoValidacao " +
            " and tipo.tipocadastrodoctooficial = :tipoCadastro ";

        if (tipoDocumentoOficialPortal.isFiltrarModulo()) {
            sql += " and tipo.modulotipodoctooficial = :solicitacao ";
        }

        Query q = em.createNativeQuery(sql, TipoDoctoOficial.class);
        q.setParameter("tipoValidacao", tipoDocumentoOficialPortal.getTipoValidacaoDoctoOficial().name());
        q.setParameter("tipoCadastro", tipoCadastroDoctoOficial.name());

        if (tipoDocumentoOficialPortal.isFiltrarModulo()) {
            q.setParameter("solicitacao", ModuloTipoDoctoOficial.SOLICITACAO.name());
        }

        List<TipoDoctoOficial> tipos = q.getResultList();
        return (tipos != null && !tipos.isEmpty()) ? tipos.get(0) : null;
    }


}
