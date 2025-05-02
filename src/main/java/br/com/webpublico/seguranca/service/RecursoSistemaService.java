package br.com.webpublico.seguranca.service;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.ModuloSistema;
import br.com.webpublico.enums.SituacaoPeriodoFase;
import br.com.webpublico.seguranca.SingletonRecursosSistema;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.List;

@Service
public class RecursoSistemaService extends AbstractCadastroService<RecursoSistema> {

    @Autowired
    private SingletonRecursosSistema singletonRecursosSistema;
    @Autowired
    private SistemaService sistemaService;

    public SingletonRecursosSistema getSingletonRecursosSistema() {
        return singletonRecursosSistema;
    }

    public RecursoSistema findByUri(String uri) {
        return singletonRecursosSistema.getRecursoPorUri(uri);
    }

    public boolean jaExiste(String uri) {
        RecursoSistema recursoPorUri = singletonRecursosSistema.getRecursoPorUri(uri);
        return recursoPorUri != null;
    }

    public List<RecursoSistema> listaRecursosGrupoRecursos(GrupoRecurso grupoRecurso) {
        if (singletonRecursosSistema.getRecursosDoGrupo().containsKey(grupoRecurso)) {
            return singletonRecursosSistema.getRecursosDoGrupo().get(grupoRecurso);
        }
        Query q = entityManager.createQuery("select gr.recursos from GrupoRecurso gr where gr = :grupo");
        q.setParameter("grupo", grupoRecurso);
        singletonRecursosSistema.getRecursosDoGrupo().put(grupoRecurso, q.getResultList());
        return q.getResultList();
    }

    public List<RecursoSistema> listaRecursos() {
        String hql = "from RecursoSistema where modulo in (:modulos) ";
        Query q = entityManager.createQuery(hql);
        q.setParameter("modulos", Lists.newArrayList(ModuloSistema.values()));
        return q.getResultList();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Fase> listaFases() {
        List<Fase> fases = Lists.newArrayList();
        String hql = "from Fase f";
        Query q = entityManager.createQuery(hql);
        for (Fase fase : (List<Fase>) q.getResultList()) {
            Hibernate.initialize(fase.getPeriodoFases());
            Hibernate.initialize(fase.getRecursos());
            for (PeriodoFase periodoFase : fase.getPeriodoFases()) {
                Hibernate.initialize(periodoFase.getPeriodoFaseUnidades());
                for (PeriodoFaseUnidade periodoFaseUnidade : periodoFase.getPeriodoFaseUnidades()) {
                    Hibernate.initialize(periodoFaseUnidade.getUsuarios());
                }
            }
            fases.add(fase);
        }
        return fases;
    }

    public List<Fase> listaFasesRecurso(RecursoSistema recurso) {
        List<Fase> retorno = Lists.newArrayList();
        for (Fase fase : singletonRecursosSistema.getFases()) {
            if (fase.getRecursos().contains(recurso)) {
                retorno.add(fase);
            }
        }
        return retorno;
    }

    public List<PeriodoFaseUnidade> listaPeriodoFaseUnidades(RecursoSistema recursoSistema) {
        List<PeriodoFaseUnidade> retorno = Lists.newArrayList();
        for (Fase fase : singletonRecursosSistema.getFases()) {
            if (fase.getRecursos().contains(recursoSistema)) {
                for (PeriodoFase periodo : fase.getPeriodoFases()) {
                    for (PeriodoFaseUnidade periodoFaseUnidade : periodo.getPeriodoFaseUnidades()) {
                        PeriodoFaseUnidade e = verificarRegrasPeriodoFaseUnidade(periodo, periodoFaseUnidade);
                        if (e != null) {
                            retorno.add(e);
                        }
                    }
                }
            }
        }
        return retorno;
    }

    private PeriodoFaseUnidade verificarRegrasPeriodoFaseUnidade(PeriodoFase periodo, PeriodoFaseUnidade periodoFaseUnidade) {
        if (periodoFaseUnidade.getUnidadeOrganizacional().getId().equals(sistemaService.getOrcamentariaCorrente().getId())
            && periodo.getExercicio().getId().equals(sistemaService.getExercicioCorrente().getId())
            && periodoFaseUnidade.isVigente(sistemaService.getDataOperacao())
            && SituacaoPeriodoFase.ABERTO.equals(periodoFaseUnidade.getSituacaoPeriodoFase())) {
            if (periodoFaseUnidade.getUsuarios() == null || periodoFaseUnidade.getUsuarios().isEmpty()) {
                return periodoFaseUnidade;
            } else {
                for (PeriodoFaseUsuario periodoFaseUsuario : periodoFaseUnidade.getUsuarios()) {
                    if (periodoFaseUsuario.getUsuarioSistema().equals(sistemaService.getUsuarioCorrente())) {
                        return periodoFaseUnidade;
                    }
                }
            }
        }
        return null;
    }

    public RecursoSistema verificaAdicionaRecursoNaoCadastrado(String uri) {
        if (!jaExiste(uri) && uri.endsWith(".xhtml")) {
            RecursoSistema rs = new RecursoSistema();
            rs.setCadastro(false);
            rs.setCaminho(uri);
            rs.setNome(uri.replaceAll("/faces/", "").replaceAll(".xhtml", "").replaceAll("/", " "));
            return save(rs);
        } else {
            return findByUri(uri);
        }
    }
}
