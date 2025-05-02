package br.com.webpublico.negocios;

import br.com.webpublico.entidades.AgendamentoViagemSaud;
import br.com.webpublico.entidades.ParametrosTransitoTransporte;
import br.com.webpublico.enums.SituacaoViagemSaud;
import br.com.webpublico.enums.TipoPermissaoRBTrans;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Stateless
public class AgendamentoViagemSaudFacade extends AbstractFacade<AgendamentoViagemSaud> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private CidadeFacade cidadeFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    @EJB
    private UsuarioSaudFacade usuarioSaudFacade;
    @EJB
    private ConfiguracaoTributarioFacade configuracaoTributarioFacade;
    @EJB
    private ParametrosTransitoTransporteFacade parametrosTransitoTransporteFacade;

    public AgendamentoViagemSaudFacade() {
        super(AgendamentoViagemSaud.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CidadeFacade getCidadeFacade() {
        return cidadeFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfiguracaoTributarioFacade getConfiguracaoTributarioFacade() {
        return configuracaoTributarioFacade;
    }

    public UsuarioSaudFacade getUsuarioSaudFacade() {
        return usuarioSaudFacade;
    }

    public ConsultaCepFacade getConsultaCepFacade() {
        return consultaCepFacade;
    }

    @Override
    public void preSave(AgendamentoViagemSaud entidade) {
        entidade.realizarValidacoes();
        ParametrosTransitoTransporte parametrosTransitoTransporte = parametrosTransitoTransporteFacade
            .recuperarParametroVigentePeloTipo(TipoPermissaoRBTrans.TAXI);
        entidade.validarHorarioViagem(parametrosTransitoTransporte != null  ?
            parametrosTransitoTransporte.getHoraAntecedenciaViagem() : null);
    }

    public List<AgendamentoViagemSaud> buscarFiltrando(String parte) {
        String sql = " SELECT AVS.* FROM AGENDAMENTOVIAGEMSAUD AVS" +
            " INNER JOIN USUARIOSAUD US ON AVS.USUARIOSAUD_ID = US.ID " +
            " WHERE TRIM(LOWER(us.nome)) LIKE :parte " +
            " OR TRIM(REGEXP_REPLACE(us.CPF, '[^0-9]', '')) LIKE :parte " +
            " OR TO_CHAR(AVS.DATAVIAGEM, 'dd/MM/yyyy') LIKE :parte ";
        Query q = em.createNativeQuery(sql, AgendamentoViagemSaud.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        return q.getResultList();
    }

    public void aprovar(AgendamentoViagemSaud agendamentoViagemSaud) {
        agendamentoViagemSaud.setSituacao(SituacaoViagemSaud.APROVADA);
        agendamentoViagemSaud.setUsuarioAvaliacao(sistemaFacade.getUsuarioCorrente());
        agendamentoViagemSaud.setDataAvaliacao(new Date());
        em.merge(agendamentoViagemSaud);
    }

    public void rejeitar(AgendamentoViagemSaud agendamentoViagemSaud) {
        agendamentoViagemSaud.setSituacao(SituacaoViagemSaud.REJEITADA);
        agendamentoViagemSaud.setUsuarioAvaliacao(sistemaFacade.getUsuarioCorrente());
        agendamentoViagemSaud.setDataAvaliacao(new Date());
        em.merge(agendamentoViagemSaud);
    }
}
