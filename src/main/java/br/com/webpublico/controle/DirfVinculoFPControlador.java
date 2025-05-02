package br.com.webpublico.controle;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.entidades.rh.configuracao.AutorizacaoUsuarioRH;
import br.com.webpublico.entidades.rh.dirf.DirfPessoa;
import br.com.webpublico.entidades.rh.dirf.DirfValor;
import br.com.webpublico.entidades.rh.dirf.DirfVinculoFP;
import br.com.webpublico.enums.rh.TipoAutorizacaoRH;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.DirfVinculoFPFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.util.FacesUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ManagedBean
@SessionScoped
@URLMappings(mappings = {
    @URLMapping(id = "listar-informe-rendimentos", pattern = "/informe-rendimentos/listar/", viewId = "/faces/rh/informe-rendimentos/lista.xhtml"),
    @URLMapping(id = "ver-informe-rendimentos", pattern = "/informe-rendimentos/ver/#{dirfVinculoFPControlador.id}/", viewId = "/faces/rh/informe-rendimentos/visualizar.xhtml"),
    @URLMapping(id = "editar-informe-rendimentos", pattern = "/informe-rendimentos/editar/#{dirfVinculoFPControlador.id}/", viewId = "/faces/rh/informe-rendimentos/edita.xhtml")
})
public class DirfVinculoFPControlador extends PrettyControlador<DirfVinculoFP> implements Serializable, CRUD {

    @EJB
    private DirfVinculoFPFacade dirfVinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private DirfPessoa dirfPessoa;

    public DirfVinculoFPControlador() {
        super(DirfVinculoFP.class);
    }

    @Override
    @URLAction(mappingId = "ver-informe-rendimentos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    @URLAction(mappingId = "editar-informe-rendimentos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    public void validarPermissaoUsuario() {
        UsuarioSistema usuarioCorrente = sistemaFacade.getUsuarioCorrente();
        usuarioCorrente = usuarioSistemaFacade.recuperar(usuarioCorrente.getId());
        List<AutorizacaoUsuarioRH> autorizacaoUsuarioRH1 = usuarioCorrente.getAutorizacaoUsuarioRH();
        boolean hasPermissaoEditar = false;
        if (autorizacaoUsuarioRH1 != null) {
            for (AutorizacaoUsuarioRH autorizacaoUsuarioRH : autorizacaoUsuarioRH1) {
                if (TipoAutorizacaoRH.PERMITIR_ALTERAR_INFORME_RENDIMENTO.equals(autorizacaoUsuarioRH.getTipoAutorizacaoRH())) {
                    hasPermissaoEditar = true;
                    break;
                }
            }
        }
        if (!hasPermissaoEditar) {
            FacesUtil.addOperacaoNaoPermitida("O usuário não tem permissão para editar esse registro.");
            redireciona();
        }
    }

    public void abrirDialogValores(DirfPessoa dirfPessoa) {
        this.dirfPessoa = dirfPessoa;
        ordenarValores(dirfPessoa.getValoresRendimentosTributaveis());
        ordenarValores(dirfPessoa.getValoresSalarioFamilia());
    }

    private void ordenarValores(List<DirfValor> valores) {
        Collections.sort(valores, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                DirfValor s1 = (DirfValor) o1;
                DirfValor s2 = (DirfValor) o2;
                if (s1.getMes() != null && s2.getMes() != null) {
                    return s1.getMes().getNumeroMes().compareTo(s2.getMes().getNumeroMes());
                }
                return 0;
            }
        });
    }

    @Override
    public AbstractFacade getFacede() {
        return dirfVinculoFPFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/informe-rendimentos/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public DirfPessoa getDirfPessoa() {
        return dirfPessoa;
    }

    public void setDirfPessoa(DirfPessoa dirfPessoa) {
        this.dirfPessoa = dirfPessoa;
    }
}
