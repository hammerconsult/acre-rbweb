package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCedenciaContratoFP;
import br.com.webpublico.enums.TipoPeriodoAquisitivo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Stateless
public class BloqueioDesbloqueioUsuarioFacade extends AbstractFacade<BloqueioDesbloqueioUsuario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private TipoAfastamentoFacade tipoAfastamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public BloqueioDesbloqueioUsuarioFacade() {
        super(BloqueioDesbloqueioUsuario.class);
    }

    @Override
    public BloqueioDesbloqueioUsuario recuperar(Object id) {
        BloqueioDesbloqueioUsuario bloqueioDesbloqueioUsuario = em.find(BloqueioDesbloqueioUsuario.class, id);
        bloqueioDesbloqueioUsuario.getEmails().size();
        bloqueioDesbloqueioUsuario.getTiposDeAfastamento().size();
        return bloqueioDesbloqueioUsuario;
    }

    public List<BloqueioDesbloqueioUsuario> buscarBloqueiosVigentes(Date dataOperacao) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select * from BloqueioDesbloqueioUsuario bdu ")
            .append(" where to_date(:dataOperacao, 'dd/mm/yyyy') between bdu.iniciovigencia and coalesce(bdu.fimvigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ");
        Query q = em.createNativeQuery(sql.toString(), BloqueioDesbloqueioUsuario.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        return q.getResultList();
    }

    public List<UsuarioSistema> buscarUsuariosParaBloqueioPorAfastamento(Date dataOperacao, List<Long> tiposAfastamento, boolean isBloqueado) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select usu.* from afastamento afa ")
            .append("  inner join contratoFp cont on afa.CONTRATOFP_ID = cont.id ")
            .append("  inner join vinculofp vinc on cont.id = vinc.id ")
            .append("  inner join matriculafp mat on vinc.MATRICULAFP_ID = mat.id ")
            .append("  inner join pessoafisica pf on mat.PESSOA_ID = pf.id ")
            .append("  inner join usuariosistema usu on pf.id = usu.PESSOAFISICA_ID ")
            .append("  where usu.NAOBLOQUEARAUTOMATICAMENTE = 0 ")
            .append("  and usu.expira = :expira ")
            .append("  and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(afa.inicio) and trunc(afa.termino) ");
        if (!tiposAfastamento.isEmpty()) {
            sql.append(" and afa.tipoafastamento_id in (:tipos) ");
        }
        Query q = em.createNativeQuery(sql.toString(), UsuarioSistema.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("expira", isBloqueado);
        if (!tiposAfastamento.isEmpty()) {
            q.setParameter("tipos", tiposAfastamento);
        }
        return q.getResultList();
    }

    public List<UsuarioSistema> buscarUsuariosParaBloqueioPorCessaoSemOnus(Date dataOperacao, boolean isBloqueado) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select usu.* from CedenciaContratoFP cedencia ")
            .append("  inner join contratoFp cont on cedencia.CONTRATOFP_ID = cont.id ")
            .append("  inner join vinculofp vinc on cont.id = vinc.id ")
            .append("  inner join matriculafp mat on vinc.MATRICULAFP_ID = mat.id ")
            .append("  inner join pessoafisica pf on mat.PESSOA_ID = pf.id ")
            .append("  inner join usuariosistema usu on pf.id = usu.PESSOAFISICA_ID ")
            .append("  where usu.NAOBLOQUEARAUTOMATICAMENTE = 0 ")
            .append("  and usu.expira = :expira ")
            .append("  and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(cedencia.DATACESSAO) and trunc(cedencia.DATARETORNO) ")
            .append("  and cedencia.TIPOCONTRATOCEDENCIAFP = :tipoCedencia ");
        Query q = em.createNativeQuery(sql.toString(), UsuarioSistema.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("expira", isBloqueado);
        q.setParameter("tipoCedencia", TipoCedenciaContratoFP.SEM_ONUS.name());
        return q.getResultList();
    }

    public List<UsuarioSistema> buscarUsuariosParaBloqueioPorTipo(Date dataOperacao, TipoPeriodoAquisitivo tipoPeriodoAquisitivo, boolean isBloqueado) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select usu.* from CONCESSAOFERIASLICENCA conc ")
            .append(" inner join PERIODOAQUISITIVOFL periodo on conc.PERIODOAQUISITIVOFL_ID = periodo.id ")
            .append(" inner join basecargo base on periodo.BASECARGO_ID = base.id ")
            .append(" inner join BASEPERIODOAQUISITIVO baseper on base.BASEPERIODOAQUISITIVO_ID = baseper.id ")
            .append(" inner join contratoFp cont on periodo.CONTRATOFP_ID = cont.id ")
            .append(" inner join vinculofp vinc on cont.id = vinc.id ")
            .append(" inner join matriculafp mat on vinc.MATRICULAFP_ID = mat.id ")
            .append(" inner join pessoafisica pf on mat.PESSOA_ID = pf.id ")
            .append(" inner join usuariosistema usu on pf.id = usu.PESSOAFISICA_ID ")
            .append(" where baseper.TIPOPERIODOAQUISITIVO = :tipoPeriodo ")
            .append("   and usu.NAOBLOQUEARAUTOMATICAMENTE = 0 ")
            .append("   and usu.expira = :expira ")
            .append("   and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(conc.datainicial) and trunc(conc.datafinal) ");
        Query q = em.createNativeQuery(sql.toString(), UsuarioSistema.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("tipoPeriodo", tipoPeriodoAquisitivo.name());
        q.setParameter("expira", isBloqueado);
        return q.getResultList();
    }

    public List<UsuarioSistema> buscarUsuariosParaBloqueioPorFerias(Date dataOperacao, boolean isBloqueado) {
        String sql = " select vinc.id idVinculo, usu.id idUsuario from CONCESSAOFERIASLICENCA conc " +
            " inner join PERIODOAQUISITIVOFL periodo on conc.PERIODOAQUISITIVOFL_ID = periodo.id " +
            " inner join basecargo base on periodo.BASECARGO_ID = base.id " +
            " inner join BASEPERIODOAQUISITIVO baseper on base.BASEPERIODOAQUISITIVO_ID = baseper.id " +
            " inner join contratoFp cont on periodo.CONTRATOFP_ID = cont.id " +
            " inner join vinculofp vinc on cont.id = vinc.id " +
            " inner join matriculafp mat on vinc.MATRICULAFP_ID = mat.id " +
            " inner join pessoafisica pf on mat.PESSOA_ID = pf.id " +
            " inner join usuariosistema usu on pf.id = usu.PESSOAFISICA_ID " +
            " where baseper.TIPOPERIODOAQUISITIVO = :tipoPeriodo " +
            "   and usu.NAOBLOQUEARAUTOMATICAMENTE = 0 " +
            "   and usu.expira = :expira " +
            "   and to_date(:dataOperacao, 'dd/mm/yyyy') between trunc(conc.datainicial) and trunc(conc.datafinal) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("tipoPeriodo", TipoPeriodoAquisitivo.FERIAS.name());
        q.setParameter("expira", isBloqueado);
        List<Object[]> resultados = q.getResultList();

        Map<Long, Long> mapVinculoEUsuario = new HashMap<>();
        Map<Long, UsuarioSistema> mapUsuario = new HashMap<>();
        List<UsuarioSistema> usuarios = Lists.newArrayList();
        if (resultados != null && !resultados.isEmpty()) {
            for (Object[] obj : resultados) {
                mapVinculoEUsuario.put(((BigDecimal) obj[0]).longValue(), ((BigDecimal) obj[1]).longValue());
            }

            for (Long vinc : mapVinculoEUsuario.keySet()) {
                VinculoFP vinculo = contratoFPFacade.recuperarSomenteContrato(vinc);
                List<ContratoFP> multiplosVinculos = listaContratosVigentesPorPessoaFisica(vinculo.getMatriculaFP().getPessoa(), dataOperacao);
                boolean adicionaUsuario = true;
                Long idUsuario = mapVinculoEUsuario.get(vinc);
                for (ContratoFP cont : multiplosVinculos) {
                    if (!mapVinculoEUsuario.containsKey(cont.getId())) {
                        adicionaUsuario = false;
                    }
                }
                if (adicionaUsuario && !mapUsuario.containsKey(idUsuario)) {
                    mapUsuario.put(idUsuario, usuarioSistemaFacade.recuperarSomenteUsuario(idUsuario));
                }
            }
            usuarios.addAll(mapUsuario.values());
        }
        return usuarios;
    }


    public List<ContratoFP> listaContratosVigentesPorPessoaFisica(PessoaFisica pessoaFisica, Date dataOperacao) {
        Query q = em.createQuery(" select contrato from ContratoFP contrato"
            + " inner join contrato.matriculaFP matricula "
            + " inner join matricula.pessoa pf "
            + " where pf = :pessoaFisica "
            + " and to_date(:dataOperacao, 'dd/mm/yyyy') between contrato.inicioVigencia and coalesce(contrato.finalVigencia, to_date(:dataOperacao, 'dd/mm/yyyy')) ");
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("pessoaFisica", pessoaFisica);
        return q.getResultList();
    }

    public List<UsuarioSistema> buscarUsuariosParaBloqueioPorExoneracao(Date dataOperacao, boolean isBloqueado) {
        String sql = "select usu.* " +
            " from exoneracaorescisao e " +
            "         inner join vinculofp vinc on vinc.id = e.vinculofp_id " +
            "         inner join matriculafp mat on vinc.matriculafp_id = mat.id " +
            "         inner join pessoafisica pf on mat.pessoa_id = pf.id " +
            "         inner join usuariosistema usu on pf.id = usu.pessoafisica_id " +
            " where usu.naobloquearautomaticamente = 0 " +
            "  and usu.expira = :expira " +
            "  and not exists(select 1 " +
            "                 from vinculofp vinculo " +
            "                          inner join contratofp cont on vinculo.id = cont.id " +
            "                          inner join matriculafp matri on vinculo.matriculafp_id = matri.id " +
            "                 where to_date(:dataOperacao, 'dd/MM/yyyy') between vinculo.iniciovigencia and coalesce( " +
            "                         vinculo.finalvigencia, to_date(:dataOperacao, 'dd/MM/yyyy')) " +
            "                   and matri.pessoa_id = pf.id " +
            "                   and vinc.id <> vinculo.id) " +
            "  and to_date(:dataOperacao, 'dd/MM/yyyy') > trunc(e.datarescisao)";
        Query q = em.createNativeQuery(sql.toString(), UsuarioSistema.class);
        q.setParameter("dataOperacao", DataUtil.getDataFormatada(dataOperacao));
        q.setParameter("expira", isBloqueado);
        return q.getResultList();
    }

    public List<UsuarioSistema> buscarUsuariosBloqueados() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select usu.* from usuariosistema usu ")
            .append(" where usu.NAOBLOQUEARAUTOMATICAMENTE = 0 ")
            .append("   and usu.expira = 1 ");
        Query q = em.createNativeQuery(sql.toString(), UsuarioSistema.class);
        return q.getResultList();
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoAfastamentoFacade getTipoAfastamentoFacade() {
        return tipoAfastamentoFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}
