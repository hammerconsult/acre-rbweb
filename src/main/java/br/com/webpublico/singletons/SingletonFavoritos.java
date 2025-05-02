package br.com.webpublico.singletons;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidadesauxiliares.FavoritoDTO;
import br.com.webpublico.seguranca.service.SistemaService;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class SingletonFavoritos implements Serializable {
    @PersistenceContext
    protected transient EntityManager em;
    private Map<UsuarioSistema, List<FavoritoDTO>> favoritos;
    @Autowired
    private SistemaService sistemaService;


    public List<FavoritoDTO> getFavoritos() {
        inicializar();
        if (favoritos.containsKey(sistemaService.getUsuarioCorrente())) {
            return favoritos.get(sistemaService.getUsuarioCorrente());
        } else {
            List<FavoritoDTO> favorit = recuperarFavoritosUsuario(sistemaService.getUsuarioCorrente());
            favoritos.put(sistemaService.getUsuarioCorrente(), favorit);
            return favorit;
        }
    }

    private void inicializar() {
        if (favoritos == null) {
            favoritos = new HashMap<>();
        }
    }


    public void adicionarFavorito(FavoritoDTO favorito) {
        if (getFavoritos() != null) {
            getFavoritos().add(favorito);
        } else {
            favoritos.put(sistemaService.getUsuarioCorrente(), Lists.newArrayList(favorito));
        }
    }

    public void remover(FavoritoDTO favorito) {
        if (getFavoritos() != null && getFavoritos().contains(favorito)) {
            getFavoritos().remove(favorito);
        }
    }

    public List<FavoritoDTO> recuperarFavoritosUsuario(UsuarioSistema usuarioCorrente) {
        try {
            Query consulta = em.createNativeQuery("select f.id, f.nome, f.url from Favorito f where f.usuarioSistema_id = :id order by f.nome asc ");
            consulta.setParameter("id", usuarioCorrente.getId());
            List<FavoritoDTO> dtos = Lists.newArrayList();
            List<Object[]> resultList = consulta.getResultList();
            for (Object[] obj : resultList) {
                FavoritoDTO dto = new FavoritoDTO();
                dto.setId(((Number) obj[0]).longValue());
                dto.setNome((String) obj[1]);
                dto.setUrl((String) obj[2]);
                dtos.add(dto);
            }
            return dtos;
        } catch (Exception e) {
            return new ArrayList();
        }
    }
}
