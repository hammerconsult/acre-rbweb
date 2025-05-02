package br.com.webpublico.singletons;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.TipoGestor;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class SingletonUsuarioGestor implements Serializable {

    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private Map<ChaveUsuarioTipoGestor, List<HierarquiaOrganizacional>> mapHierarquia;

    public List<Long> getIdsUnidades(TipoGestor tipoGestor, UsuarioSistema usuarioSistema, String nivelEstrutura) {
        List<Long> ids = Lists.newArrayList();
        ChaveUsuarioTipoGestor chave = new ChaveUsuarioTipoGestor(usuarioSistema, tipoGestor, nivelEstrutura);

        preencherMapaHierarquia(chave);

        for (Map.Entry<ChaveUsuarioTipoGestor, List<HierarquiaOrganizacional>> entry : mapHierarquia.entrySet()) {
            for (HierarquiaOrganizacional ho : entry.getValue()) {
                if (chave.equals(entry.getKey())) {
                    ids.add(ho.getSubordinada().getId());
                }
            }
        }
        return ids;
    }

    public List<HierarquiaOrganizacional> getHierarquias(TipoGestor tipoGestor, UsuarioSistema usuarioSistema, String nivelEstrutura) {
        ChaveUsuarioTipoGestor chave = new ChaveUsuarioTipoGestor(usuarioSistema, tipoGestor, nivelEstrutura);
        preencherMapaHierarquia(chave);
        return mapHierarquia.get(chave);
    }

    public boolean isGestor(TipoGestor tipoGestor, UsuarioSistema usuarioSistema, String nivelEstrutura) {
        ChaveUsuarioTipoGestor novaChave = new ChaveUsuarioTipoGestor(usuarioSistema, tipoGestor, nivelEstrutura);
        preencherMapaHierarquia(novaChave);
        return mapHierarquia != null && mapHierarquia.containsKey(novaChave);
    }

    private void preencherMapaHierarquia(ChaveUsuarioTipoGestor chaveUsuarioTipoGestor) {

        if (mapHierarquia == null) {
            mapHierarquia = Maps.newHashMap();
        }
        if (!mapHierarquia.containsKey(chaveUsuarioTipoGestor)) {
            List<HierarquiaOrganizacional> hierarquias = hierarquiaOrganizacionalFacade.buscarHierarquiaPaiAndFilhoUsuarioPorTipoGestor(
                chaveUsuarioTipoGestor.getTipoGestor(), "", chaveUsuarioTipoGestor.getNivelEstrutura(), chaveUsuarioTipoGestor.getUsuarioSistema(),
                sistemaFacade.getDataOperacao());

            if (!hierarquias.isEmpty()) {
                mapHierarquia.put(chaveUsuarioTipoGestor, Lists.newArrayList(hierarquias));
            }
        }
    }

    public void resetarSingletonUsuario(UsuarioSistema usuarioSistema) {
        if (mapHierarquia !=null && !mapHierarquia.isEmpty()) {
            for (TipoGestor value : TipoGestor.values()) {
                ChaveUsuarioTipoGestor chave = new ChaveUsuarioTipoGestor(usuarioSistema, value, null);
                mapHierarquia.remove(chave);
            }
        }
    }


    public static class ChaveUsuarioTipoGestor {
        private UsuarioSistema usuarioSistema;
        private TipoGestor tipoGestor;
        private String nivelEstrutura;

        private ChaveUsuarioTipoGestor(UsuarioSistema usuarioSistema, TipoGestor tipoGestor, String nivelEstrutura) {
            this.usuarioSistema = usuarioSistema;
            this.tipoGestor = tipoGestor;
            this.nivelEstrutura = nivelEstrutura;
        }

        public UsuarioSistema getUsuarioSistema() {
            return usuarioSistema;
        }

        public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
            this.usuarioSistema = usuarioSistema;
        }

        private TipoGestor getTipoGestor() {
            return tipoGestor;
        }

        public void setTipoGestor(TipoGestor tipoGestor) {
            this.tipoGestor = tipoGestor;
        }

        private String getNivelEstrutura() {
            return nivelEstrutura;
        }

        public void setNivelEstrutura(String nivelEstrutura) {
            this.nivelEstrutura = nivelEstrutura;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChaveUsuarioTipoGestor that = (ChaveUsuarioTipoGestor) o;
            return Objects.equal(usuarioSistema, that.usuarioSistema) &&
                tipoGestor == that.tipoGestor;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(usuarioSistema, tipoGestor);
        }
    }
}
