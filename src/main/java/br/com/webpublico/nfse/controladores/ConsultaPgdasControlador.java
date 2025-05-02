package br.com.webpublico.nfse.controladores;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.nfse.domain.pgdas.registros.*;
import br.com.webpublico.nfse.domain.pgdas.util.TabelaView;
import br.com.webpublico.nfse.domain.pgdas.util.UtilPgdas;
import br.com.webpublico.nfse.facades.ArquivoPgdasFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consulta-pgdas", pattern = "/simples-nacional/consulta-pgdas/",
        viewId = "/faces/tributario/simples-nacional/arquivo-pgdas/consulta.xhtml")
})
public class ConsultaPgdasControlador implements Serializable {

    @Autowired
    private ArquivoPgdasFacade service;
    private CadastroEconomico cadastroEconomico;
    private List<PgdasRegistro00000> registros00000;
    private List<PgdasRegistro00001> registros00001;
    private List<PgdasRegistro01000> registros01000;
    private List<PgdasRegistro01500> registros01500;
    private List<PgdasRegistro01501> registros01501;
    private List<PgdasRegistro01502> registros01502;
    private List<PgdasRegistro02000> registros02000;
    private List<PgdasRegistro03000> registros03000;
    private List<PgdasRegistro03100> registros03100;
    private List<PgdasRegistro03110> registros03110;
    private List<PgdasRegistro03111> registros03111;
    private List<PgdasRegistro03112> registros03112;
    private List<PgdasRegistro03120> registros03120;
    private List<PgdasRegistro03121> registros03121;
    private List<PgdasRegistro03122> registros03122;
    private List<PgdasRegistro03130> registros03130;
    private List<PgdasRegistro03131> registros03131;
    private List<PgdasRegistro03132> registros03132;
    private List<PgdasRegistro03500> registros03500;
    private List<PgdasRegistro04000> registros04000;

    public ConsultaPgdasControlador() {
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }


    @URLAction(mappingId = "consulta-pgdas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {


    }


    public void consultaPgdasControlador() {
        try {
            validarConsulta();
            String cnpj = cadastroEconomico.getPessoa().getCpf_Cnpj();
            cnpj = StringUtil.removeCaracteresEspeciaisSemEspaco(cnpj);
            registros00000 = service.buscarRegistro00000(cnpj);
            registros00001 = service.buscarRegistro00001(cnpj);
            registros01000 = service.buscarRegistro01000(cnpj);
            registros01500 = service.buscarRegistro01500(cnpj);
            registros01501 = service.buscarRegistro01501(cnpj);
            registros01502 = service.buscarRegistro01502(cnpj);
            registros02000 = service.buscarRegistro02000(cnpj);
            registros03000 = service.buscarRegistro03000(cnpj);
            registros03100 = service.buscarRegistro03100(cnpj);
            registros03110 = service.buscarRegistro03110(cnpj);
            registros03120 = service.buscarRegistro03120(cnpj);
            registros03130 = service.buscarRegistro03130(cnpj);
            registros03111 = service.buscarRegistro03111(cnpj);
            registros03121 = service.buscarRegistro03121(cnpj);
            registros03131 = service.buscarRegistro03131(cnpj);
            registros03112 = service.buscarRegistro03112(cnpj);
            registros03122 = service.buscarRegistro03122(cnpj);
            registros03132 = service.buscarRegistro03132(cnpj);
            registros03500 = service.buscarRegistro03500(cnpj);
            registros04000 = service.buscarRegistro04000(cnpj);
        } catch (ValidacaoException op) {
            FacesUtil.addErrorGenerico(op);
        }
    }

    private void validarConsulta() throws ValidacaoException {
        if (cadastroEconomico == null) {
            throw new ValidacaoException("O campo Cadastro Econômico deve ser informado.");
        }
    }

    public List<PgdasRegistro00000> getRegistros00000() {
        return registros00000;
    }

    public void setRegistros00000(List<PgdasRegistro00000> registros00000) {
        this.registros00000 = registros00000;
    }

    public List<PgdasRegistro00001> getRegistros00001() {
        return registros00001;
    }

    public void setRegistros00001(List<PgdasRegistro00001> registros00001) {
        this.registros00001 = registros00001;
    }

    public List<PgdasRegistro01000> getRegistros01000() {
        return registros01000;
    }

    public void setRegistros01000(List<PgdasRegistro01000> registros01000) {
        this.registros01000 = registros01000;
    }

    public List<PgdasRegistro01500> getRegistros01500() {
        return registros01500;
    }

    public void setRegistros01500(List<PgdasRegistro01500> registros01500) {
        this.registros01500 = registros01500;
    }

    public List<PgdasRegistro01501> getRegistros01501() {
        return registros01501;
    }

    public void setRegistros01501(List<PgdasRegistro01501> registros01501) {
        this.registros01501 = registros01501;
    }

    public List<PgdasRegistro01502> getRegistros01502() {
        return registros01502;
    }

    public void setRegistros01502(List<PgdasRegistro01502> registros01502) {
        this.registros01502 = registros01502;
    }

    public List<PgdasRegistro02000> getRegistros02000() {
        return registros02000;
    }

    public void setRegistros02000(List<PgdasRegistro02000> registros02000) {
        this.registros02000 = registros02000;
    }

    public List<PgdasRegistro03000> getRegistros03000() {
        return registros03000;
    }

    public void setRegistros03000(List<PgdasRegistro03000> registros03000) {
        this.registros03000 = registros03000;
    }

    public List<PgdasRegistro03100> getRegistros03100() {
        return registros03100;
    }

    public void setRegistros03100(List<PgdasRegistro03100> registros03100) {
        this.registros03100 = registros03100;
    }

    public List<PgdasRegistro03110> getRegistros03110() {
        return registros03110;
    }

    public void setRegistros03110(List<PgdasRegistro03110> registros03110) {
        this.registros03110 = registros03110;
    }

    public List<PgdasRegistro03111> getRegistros03111() {
        return registros03111;
    }

    public void setRegistros03111(List<PgdasRegistro03111> registros03111) {
        this.registros03111 = registros03111;
    }

    public List<PgdasRegistro03112> getRegistros03112() {
        return registros03112;
    }

    public void setRegistros03112(List<PgdasRegistro03112> registros03112) {
        this.registros03112 = registros03112;
    }

    public List<PgdasRegistro03120> getRegistros03120() {
        return registros03120;
    }

    public void setRegistros03120(List<PgdasRegistro03120> registros03120) {
        this.registros03120 = registros03120;
    }

    public List<PgdasRegistro03121> getRegistros03121() {
        return registros03121;
    }

    public void setRegistros03121(List<PgdasRegistro03121> registros03121) {
        this.registros03121 = registros03121;
    }

    public List<PgdasRegistro03122> getRegistros03122() {
        return registros03122;
    }

    public void setRegistros03122(List<PgdasRegistro03122> registros03122) {
        this.registros03122 = registros03122;
    }

    public List<PgdasRegistro03130> getRegistros03130() {
        return registros03130;
    }

    public void setRegistros03130(List<PgdasRegistro03130> registros03130) {
        this.registros03130 = registros03130;
    }

    public List<PgdasRegistro03131> getRegistros03131() {
        return registros03131;
    }

    public void setRegistros03131(List<PgdasRegistro03131> registros03131) {
        this.registros03131 = registros03131;
    }

    public List<PgdasRegistro03132> getRegistros03132() {
        return registros03132;
    }

    public void setRegistros03132(List<PgdasRegistro03132> registros03132) {
        this.registros03132 = registros03132;
    }

    public List<PgdasRegistro03500> getRegistros03500() {
        return registros03500;
    }

    public void setRegistros03500(List<PgdasRegistro03500> registros03500) {
        this.registros03500 = registros03500;
    }

    public List<PgdasRegistro04000> getRegistros04000() {
        return registros04000;
    }

    public void setRegistros04000(List<PgdasRegistro04000> registros04000) {
        this.registros04000 = registros04000;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }


    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public TabelaView getTabelaPgdasRegistro00000() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros00000());
    }

    public TabelaView getTabelaPgdasRegistro00001() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros00001());
    }

    public TabelaView getTabelaPgdasRegistro01000() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros01000());
    }

    public TabelaView getTabelaPgdasRegistro01500() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros01500());
    }

    public TabelaView getTabelaPgdasRegistro01501() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros01501());
    }

    public TabelaView getTabelaPgdasRegistro01502() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros01502());
    }

    public TabelaView getTabelaPgdasRegistro02000() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros02000());
    }

    public TabelaView getTabelaPgdasRegistro03000() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03000());
    }

    public TabelaView getTabelaPgdasRegistro03100() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03100());
    }

    public TabelaView getTabelaPgdasRegistro03110() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03110());
    }

    public TabelaView getTabelaPgdasRegistro03120() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03120());
    }

    public TabelaView getTabelaPgdasRegistro03130() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03130());
    }

    public TabelaView getTabelaPgdasRegistro03111() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03111());
    }

    public TabelaView getTabelaPgdasRegistro03121() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03121());
    }

    public TabelaView getTabelaPgdasRegistro03131() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03131());
    }

    public TabelaView getTabelaPgdasRegistro03112() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03112());
    }

    public TabelaView getTabelaPgdasRegistro03122() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03122());
    }

    public TabelaView getTabelaPgdasRegistro03132() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03132());
    }

    public TabelaView getTabelaPgdasRegistro03500() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros03500());
    }

    public TabelaView getTabelaPgdasRegistro04000() {
        return new UtilPgdas().getViewObjetosJaRecuperados(getRegistros04000());
    }

}
