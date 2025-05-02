package br.com.webpublico.negocios.comum;

import br.com.webpublico.entidades.comum.TermoUso;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.entidadesauxiliares.comum.UsuarioPortalWebDTO;
import br.com.webpublico.enums.Sistema;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.TermoUsoAppServidor;
import br.com.webpublico.nfse.domain.TermoUsoPortalContribuinte;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

@Stateless
public class TermoUsoFacade extends AbstractFacade<TermoUso> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UsuarioWebFacade usuarioWebFacade;

    public TermoUsoFacade() {
        super(TermoUso.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TermoUso buscarTermoUsoVigente(String tipo) {
        Query query = em.createNativeQuery(" SELECT " +
            " T.ID, " +
            " T.CONTEUDO " +
            " FROM TERMOUSO T " +
            " WHERE T.SISTEMA = :tipo " +
            "   AND T.INICIOVIGENCIA = (SELECT MAX(S.INICIOVIGENCIA) FROM TERMOUSO S WHERE S.SISTEMA = :tipo ) ");
        query.setParameter("tipo", Sistema.valueOf(tipo).name());
        List<Object[]> lista = query.getResultList();
        for (Object[] objeto : lista) {
            TermoUso dto = new TermoUso();
            dto.setId(((BigDecimal) objeto[0]).longValue());
            String conteudo = getConteudo(objeto);
            dto.setConteudo(conteudo);
            return dto;
        }
        return null;
    }

    private String getConteudo(Object[] objeto) {
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(((Clob) objeto[1]).getCharacterStream());

            int b;
            while ((b = reader.read()) != -1) {
                builder.append((char) b);
            }

            reader.close();
            return builder.toString();

//            return new BufferedReader(((Clob) objeto[1]).getCharacterStream()).readLine();
        } catch (IOException e) {
            logger.error("erro ao converter clob para string", e);
        } catch (SQLException e) {
            logger.error("erro ao converter clob para string", e);
        }
        return "";
    }

    public Boolean hasTermoParaAceite(UsuarioPortalWebDTO usuario) {
        Query query = em.createNativeQuery(" SELECT 1 AS VALUE " +
            " FROM TERMOUSO T " +
            " WHERE T.SISTEMA = :tipo " +
            "   AND T.INICIOVIGENCIA = (SELECT MAX(S.INICIOVIGENCIA) FROM TERMOUSO S WHERE S.SISTEMA = :tipo ) " +
            "   AND NOT EXISTS (SELECT 1 FROM TERMOUSOAPPSERVIDOR TN INNER JOIN UsuarioWeb usu on usu.id = tn.usuario_id  " +
            "                   WHERE TN.TERMOUSO_ID = T.ID " +
            "                     AND usu.login = :cpfUsuario)");
        query.setParameter("cpfUsuario", usuario.getCpf());
        query.setParameter("tipo", Sistema.APP_SERVIDOR.name());
        List<Object[]> lista = query.getResultList();

        return !lista.isEmpty();
    }

    public void aceitarTermoUso(Long termoUso, String usuario) {
        TermoUso termo = recuperar(termoUso);
        UsuarioWeb usuarioWeb = usuarioWebFacade.recuperarUsuarioPorLogin(usuario);


        if (termo == null || usuarioWeb == null) {
            logger.debug("termo ou usuário está nullos, termo:[{}], usuario: [{}]", termoUso, usuario);
            return;
        }

        UsuarioPortalWebDTO usuarioPortalWebDTO = new UsuarioPortalWebDTO();
        usuarioPortalWebDTO.setCpf(usuario);
        if(!hasTermoParaAceite(usuarioPortalWebDTO)){
            return;
        }

        TermoUsoAppServidor termoServidor = new TermoUsoAppServidor();
        termoServidor.setTermoUso(termo);
        termoServidor.setDataAceite(new Date());
        termoServidor.setUsuario(usuarioWeb);
        em.persist(termoServidor);
    }

    public TermoUso buscarTermoUsoVigente(Sistema sistema) {
        List<TermoUso> list = em.createNativeQuery(" select * from termouso tu " +
            " where tu.sistema = :sistema" +
            " and tu.iniciovigencia = (select max(s.iniciovigencia) from termouso s) ", TermoUso.class)
            .setParameter("sistema", sistema.name())
            .setMaxResults(1)
            .getResultList();
        return list != null && !list.isEmpty() ? (TermoUso) list.get(0) : null;
    }

    public Boolean hasTermoUsoPortalParaAceite(UsuarioWeb usuario) {
        List<TermoUso> list = em.createNativeQuery(" select tu.* " +
                "   from termouso tu " +
                " where tu.iniciovigencia = (select max(s.iniciovigencia) from termouso s where s.sistema = :sistema) " +
                "   and not exists(select 1 from TERMOUSOPORTALCONTRIB tup " +
                "                  where tup.termouso_id = tu.id and tup.usuarioweb_id = :usuario) ", TermoUso.class)
            .setParameter("sistema", Sistema.PORTAL_CONTRIBUINTE.name())
            .setParameter("usuario", usuario.getId())
            .getResultList();
        return list != null && !list.isEmpty();
    }

    public void aceitarTermoUsoPortal(UsuarioWeb usuarioWeb, TermoUso termoUso) {
       TermoUsoPortalContribuinte termoUsoPortalContribuinte = new TermoUsoPortalContribuinte();
       termoUsoPortalContribuinte.setDataAceite(new Date());
       termoUsoPortalContribuinte.setUsuarioWeb(usuarioWeb);
       termoUsoPortalContribuinte.setTermoUso(termoUso);
       em.persist(termoUsoPortalContribuinte);
    }

}
