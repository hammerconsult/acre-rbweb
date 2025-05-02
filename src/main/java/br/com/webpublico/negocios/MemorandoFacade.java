package br.com.webpublico.negocios;

import br.com.webpublico.controle.MemorandoControlador;
import br.com.webpublico.entidades.Memorando;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.nfse.domain.dtos.MemorandoDTO;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Criado por Mateus
 * Data: 15/03/2017.
 */
@Stateless
public class MemorandoFacade extends AbstractFacade<Memorando> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private PessoaFacade pessoaFacade;

    public MemorandoFacade() {
        super(Memorando.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public Memorando recuperar(Object id) {
        Memorando memorando = super.recuperar(id);
        if (memorando.getDetentorArquivoComposicao() != null) {
            memorando.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return memorando;
    }

    public Integer buscarQuantidadeMemorandosPorUsuarioNaoLidos(UsuarioSistema usuarioSistema) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(mem.id) from Memorando mem ")
            .append(" inner join usuarioSistema usu on mem.usuarioOrigem_id = usu.id ")
            .append(" inner join pessoafisica pf on usu.pessoafisica_id = pf.id ")
            .append(" where coalesce(mem.removido,0) = :removido ")
            .append("   and mem.usuariodestino_id = :usuarioSistema ")
            .append("   and mem.visualizado = :visualizado ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("removido", Boolean.FALSE);
        q.setParameter("usuarioSistema", usuarioSistema.getId());
        q.setParameter("visualizado", Boolean.FALSE);
        return ((BigDecimal) q.getResultList().get(0)).intValue();
    }

    public Integer buscarQuantidadeMemorandosPorUsuario(UsuarioSistema usuarioSistema, String filtro, MemorandoControlador.TipoMemorando tipo) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select count(mem.id) from Memorando mem ");
        if (MemorandoControlador.TipoMemorando.ENVIADO.equals(tipo)) {
            sql.append("inner join usuariosistema usu on  mem.usuarioorigem_id = usu.id ").
                append("inner join usuariosistema usuFiltro on  mem.usuariodestino_id = usuFiltro.id ");
        } else {
            sql.append("inner join usuariosistema usu on  mem.usuariodestino_id = usu.id ")
                .append("inner join usuariosistema usuFiltro on  mem.usuarioorigem_id = usuFiltro.id ");
        }
        sql.append(" inner join pessoafisica pf on usuFiltro.pessoafisica_id = pf.id ")
            .append(" where usu.id = :usuarioSistema ");
        if (MemorandoControlador.TipoMemorando.RECEBIDO.equals(tipo)) {
            sql.append(" and coalesce(mem.removido,0) = :removido ");
        }
        sql.append(" and (lower(mem.titulo) like :filtro or lower(pf.nome) like :filtro) ");

        Query q = em.createNativeQuery(sql.toString());
        if (MemorandoControlador.TipoMemorando.RECEBIDO.equals(tipo)) {
            q.setParameter("removido", Boolean.FALSE);
        }
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase().trim() + "%");
        q.setParameter("usuarioSistema", usuarioSistema.getId());
        return ((BigDecimal) q.getResultList().get(0)).intValue();
    }

    public List<Memorando> buscarMemorandosPorUsuarioAndTituloOrUsuarioOrigem(UsuarioSistema usuarioSistema,
                                                                              MemorandoControlador.TipoMemorando tipo,
                                                                              String filtro, int inicio, int max) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select mem.* from Memorando mem ");
        if (MemorandoControlador.TipoMemorando.ENVIADO.equals(tipo)) {
            sql.append("inner join usuariosistema usu on  mem.usuarioorigem_id = usu.id ").
                append("inner join usuariosistema usuFiltro on  mem.usuariodestino_id = usuFiltro.id ");
        } else {
            sql.append("inner join usuariosistema usu on  mem.usuariodestino_id = usu.id ")
                .append("inner join usuariosistema usuFiltro on  mem.usuarioorigem_id = usuFiltro.id ");
        }
        sql.append(" inner join pessoafisica pf on usuFiltro.pessoafisica_id = pf.id ")
            .append(" where usu.id = :usuarioSistema ");
        if (MemorandoControlador.TipoMemorando.RECEBIDO.equals(tipo)) {
            sql.append(" and coalesce(mem.removido,0) = :removido ");
        }
        sql.append(" and (lower(mem.titulo) like :filtro or lower(pf.nome) like :filtro) ")
            .append(" order by mem.visualizado, mem.dataenvio desc ");
        Query q = em.createNativeQuery(sql.toString(), Memorando.class);
        if (MemorandoControlador.TipoMemorando.RECEBIDO.equals(tipo)) {
            q.setParameter("removido", Boolean.FALSE);
        }
        q.setParameter("usuarioSistema", usuarioSistema.getId());
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase().trim() + "%");
        q.setMaxResults(max);
        q.setFirstResult(inicio);
        return q.getResultList();
    }
    public List<MemorandoDTO> buscarMemorandoPessoaAndCMC(String login) {
        List<Pessoa> pessoa = pessoaFacade.listaPessoasAtivasPorCpfCnpj(login);
        if(pessoa.isEmpty()){
            return Lists.newArrayList();
        }
        return buscarMemorandoPessoaAndCMC(pessoa.get(0));
    }

    public List<MemorandoDTO> buscarMemorandoPessoaAndCMC(Pessoa pessoa) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select memorando.id, titulo, conteudo, dataenvio, usuarioorigem_id, usuariodestino_id, pessoadestino_id,  pf.nome ");
        sql.append(" from memorando ");
        sql.append(" inner join usuariosistema usuarioOrigem on memorando.usuarioorigem_id = usuarioOrigem.id ");
        sql.append(" left join pessoafisica pf on pf.id = usuarioOrigem.pessoafisica_id ");
        sql.append(" where pessoadestino_id in :ids ");
        Query q = em.createNativeQuery(sql.toString());
        q.setParameter("ids", Lists.newArrayList(pessoa.getId()));

        if (!q.getResultList().isEmpty()) {
            List<MemorandoDTO> memorandos = Lists.newArrayList();
            List<Object[]> resultado = q.getResultList();
            for (Object[] obj : resultado) {
                MemorandoDTO memorando = new MemorandoDTO();
                memorando.setId(((BigDecimal) obj[0]).longValue());
                memorando.setTitulo((String) obj[1]);
                memorando.setConteudo((String) obj[2]);
                memorando.setDataEnvio((Date) obj[3]);
                memorando.setUsuarioOrigem(obj[4] != null ? ((BigDecimal) obj[4]).longValue() : null);
                memorando.setUsuarioDestino(obj[5] != null ? ((BigDecimal) obj[5]).longValue() : null);
                memorando.setIdPessoaDestino(obj[6] != null ? ((BigDecimal) obj[6]).longValue() : null);
                memorando.setNomeUsuarioOrigem((String) obj[7]);
                memorandos.add(memorando);
            }
            return memorandos;
        }
        return Lists.newArrayList();
    }

    public void criarMemorandoNFSE(MemorandoDTO memorandoDTO) {
        Memorando memorando = new Memorando();
        memorando.setTitulo(memorandoDTO.getTitulo());
        memorando.setConteudo(memorandoDTO.getConteudo());
        memorando.setDataEnvio(new Date());
        memorando.setMemorandoOrigem(recuperar(memorandoDTO.getId()));
        memorando.setUsuarioDestino(usuarioSistemaFacade.recuperar(memorandoDTO.getUsuarioOrigem()));
        memorando.setRemovido(false);
        salvar(memorando);
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }
}
