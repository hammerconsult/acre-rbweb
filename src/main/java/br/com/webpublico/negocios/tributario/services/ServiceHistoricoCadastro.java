package br.com.webpublico.negocios.tributario.services;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Historico;
import br.com.webpublico.negocios.tributario.dao.JdbcHistoricoCadastroDAO;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Service
public class ServiceHistoricoCadastro {
    @PersistenceContext
    protected transient EntityManager em;
    @Autowired
    private JdbcHistoricoCadastroDAO historicoCadastroDAO;
    @Autowired
    private SingletonGeradorId geradorId;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void inserirAuditoria(Historico historico) {
        if (historico != null && historico.getCadastro() != null) {
            Long idRevisao = geradorId.getProximoId();
            CadastroImobiliario cadastroImobiliario = em.find(CadastroImobiliario.class, historico.getCadastro().getId());
            historicoCadastroDAO.inserirAuditoria(idRevisao, historico, cadastroImobiliario);
        }
    }
}
