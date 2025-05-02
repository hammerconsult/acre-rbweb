package br.com.webpublico.seguranca.service;

import br.com.webpublico.entidades.GrupoRecurso;
import org.springframework.stereotype.Service;

import javax.persistence.Query;
import java.util.List;

@Service
public class GrupoRecursoService extends AbstractCadastroService<GrupoRecurso> {

    public List<GrupoRecurso> listaGrupo() {
        Query q = entityManager.createQuery(" from GrupoRecurso gru");
        return q.getResultList();
    }
}
