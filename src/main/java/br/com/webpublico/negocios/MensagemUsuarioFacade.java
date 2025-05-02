/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.MensagemUsuario;
import br.com.webpublico.seguranca.MensagemTodosUsuariosService;
import br.com.webpublico.util.Util;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class MensagemUsuarioFacade extends AbstractFacade<MensagemUsuario> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    private MensagemTodosUsuariosService mensagemTodosUsuariosService;


    public MensagemUsuarioFacade() {
        super(MensagemUsuario.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MensagemTodosUsuariosService getComunicacaoTodosUsuariosSingleton() {
        if (mensagemTodosUsuariosService == null) {
            mensagemTodosUsuariosService = (MensagemTodosUsuariosService) Util.getSpringBeanPeloNome("mensagemTodosUsuariosService");
        }
        return mensagemTodosUsuariosService;
    }

    @PostConstruct
    public void init() {
        getComunicacaoTodosUsuariosSingleton().agendaTarefas();
    }

    @Override
    public void salvar(MensagemUsuario entity) {
        super.salvar(entity);
    }



    public void removerPublicacao(MensagemUsuario mensagemUsuario) {
        getComunicacaoTodosUsuariosSingleton().removerMensagensTodosUsuarios(mensagemUsuario.getId());
    }




}
