package br.com.webpublico.negocios.padrao;


import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.io.Serializable;
import java.util.List;

public abstract class AbstractService<T extends SuperEntidade> implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractService.class);

    private Class<T> classe;

    public AbstractService(Class<T> classe) {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        this.classe = classe;
    }

    public Class<T> getClasse() {
        return classe;
    }

    protected abstract WebPublicoRepository<T> getRepository();

    public void salvarNovo(T entity) throws ExcecaoNegocioGenerica {
        if (entity == null) {
            throw new ExcecaoNegocioGenerica("O Objeto a ser salvo não pode ser null");
        }
        realizarValidacoes(entity);
        getRepository().salvarNovo(entity);
    }

    public T salvar(T entity) throws ExcecaoNegocioGenerica {
        if (entity == null) {
            throw new ExcecaoNegocioGenerica("O Objeto a ser salvo não pode ser null");
        }
        realizarValidacoes(entity);
        return (T) getRepository().salvar(entity);
    }

    public void remover(T entity) {
        getRepository().remover(entity);
    }

    public T recuperar(Class c, Object id) {
        if (AbstractJPARepository.class.isAssignableFrom(getRepository().getClass())) {
            return (T) ((AbstractJPARepository) getRepository()).recuperar(c, id);
        } else {
            return getRepository().recuperar((Long) id);
        }
    }

    public T recuperar(Object id) {
        return (T) getRepository().recuperar((Long) id);
    }

    public List<T> buscarTodos() {
        return getRepository().listar(null, (String) null);
    }

    public List<T> buscarFiltrando(String s, String... atributos) {
        return getRepository().listar(s, atributos);
    }

    public long count() {
        return getRepository().contar();
    }

    public String getNomeDependencia(String message) throws ClassNotFoundException {
        return getRepository().getNomeDependencia(message);
    }

    public static <T> T initializeAndUnproxy(T var) {
        return AbstractJPARepository.initializeAndUnproxy(var);
    }

    public void realizarValidacoes(T selecionado) throws ExcecaoNegocioGenerica {
        if (selecionado instanceof SuperEntidade) {
            ((SuperEntidade) selecionado).realizarValidacoes();
        }
    }
}
