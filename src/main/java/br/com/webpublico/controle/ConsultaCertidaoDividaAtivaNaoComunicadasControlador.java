/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.CertidaoDividaAtiva;
import br.com.webpublico.entidades.ComunicacaoSoftPlan;
import br.com.webpublico.negocios.CertidaoDividaAtivaFacade;
import br.com.webpublico.negocios.ComunicaSofPlanFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.component.tabview.TabView;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

@ManagedBean(name = "consultaCertidaoDividaAtivaNaoComunicadasControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaNaoIntegradas",
        pattern = "/consulta-de-certidao-de-divida-ativa-nao-comunicadas/",
        viewId = "/faces/tributario/dividaativa/reemissaoconsultacertidao/cdaNaoComunicada.xhtml"),

})
public class ConsultaCertidaoDividaAtivaNaoComunicadasControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaCertidaoDividaAtivaNaoComunicadasControlador.class);
    @EJB
    private CertidaoDividaAtivaFacade certidaoDividaAtivaFacade;
    @EJB
    private ComunicaSofPlanFacade comunicaSofPlanFacade;
    private Map<ComunicaSofPlanFacade.ServiceId, LazyDataModel<ConsultaCertidaoDividaAtivaNaoComunicadasControlador.CdaDTO>> certidoesPorServiceId;
    private CertidaoDividaAtiva certidaoDividaAtiva;
    private Map<CertidaoDividaAtiva, List<ComunicacaoSoftPlan>> comunicacoes;


    @URLAction(mappingId = "novaConsultaNaoIntegradas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        certidoesPorServiceId = Maps.newHashMap();
        comunicacoes = Maps.newHashMap();
        carregarTipo(getServices().get(0));
    }

    public List<ComunicaSofPlanFacade.ServiceId> getServices() {
        return Lists.newArrayList(ComunicaSofPlanFacade.ServiceId.values());
    }

    public LazyDataModel<ConsultaCertidaoDividaAtivaNaoComunicadasControlador.CdaDTO> certidaoPorTipo(ComunicaSofPlanFacade.ServiceId serviceId) {
        return certidoesPorServiceId.get(serviceId);
    }

    public void alterarTabs(TabChangeEvent evt) {
        ComunicaSofPlanFacade.ServiceId serviceId = getServices().get(((TabView) evt.getComponent()).getActiveIndex());
        carregarTipo(serviceId);
        FacesUtil.atualizarComponente("Formulario:tabviewcda:tabela-" + serviceId.name());
    }

    public void carregarTipo(ComunicaSofPlanFacade.ServiceId serviceId) {
        certidoesPorServiceId.put(serviceId, new CertidaoDividaAtivaDataModel(serviceId));

    }

    public void enviar(Long id) throws InterruptedException {
        recuperaCertidao(id);
        Future future = comunicaSofPlanFacade.enviarCDA(Lists.newArrayList(certidaoDividaAtiva));
        while (!future.isDone()) {
            Thread.sleep(1000);
        }
        comunicacoes.put(certidaoDividaAtiva, Lists.<ComunicacaoSoftPlan>newArrayList());
        FacesUtil.atualizarComponente("Formulario");
        FacesUtil.atualizarComponente("formDetalhesEnvio");
        FacesUtil.executaJavaScript("dialogDetalhesEnvio.show()");

    }

    public void voltar() {
        FacesUtil.redirecionamentoInterno("/consulta-e-reemissao-de-certidao-de-divida-ativa");
    }

    public void recuperaCertidao(Long id) {
        certidaoDividaAtiva = certidaoDividaAtivaFacade.recuperar(id);
    }


    public CertidaoDividaAtiva getCertidaoDividaAtiva() {
        return certidaoDividaAtiva;
    }

    public List<ComunicacaoSoftPlan> getComunicacoesCDA() {
        if (certidaoDividaAtiva == null) {
            return Lists.newArrayList();
        }
        if (comunicacoes.get(certidaoDividaAtiva) == null || comunicacoes.get(certidaoDividaAtiva).isEmpty()) {
            comunicacoes.put(certidaoDividaAtiva, certidaoDividaAtivaFacade.recuperarComunicacoesCDA(certidaoDividaAtiva));
        }
        return comunicacoes.get(certidaoDividaAtiva);

    }

    public static class CdaDTO {
        private Long id;
        private String numero;
        private String nomePessoa;
        private String cpfPessoa;
        private String inscricaoCadastral;
        private Date dataGeracao;
        private String situacao;
        private String situacaoJudicial;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNumero() {
            return numero;
        }

        public void setNumero(String numero) {
            this.numero = numero;
        }

        public String getNomePessoa() {
            return nomePessoa;
        }

        public void setNomePessoa(String nomePessoa) {
            this.nomePessoa = nomePessoa;
        }

        public String getCpfPessoa() {
            return cpfPessoa;
        }

        public void setCpfPessoa(String cpfPessoa) {
            this.cpfPessoa = cpfPessoa;
        }

        public String getInscricaoCadastral() {
            return inscricaoCadastral;
        }

        public void setInscricaoCadastral(String inscricaoCadastral) {
            this.inscricaoCadastral = inscricaoCadastral;
        }

        public Date getDataGeracao() {
            return dataGeracao;
        }

        public void setDataGeracao(Date dataGeracao) {
            this.dataGeracao = dataGeracao;
        }

        public String getSituacao() {
            return situacao;
        }

        public void setSituacao(String situacao) {
            this.situacao = situacao;
        }

        public String getSituacaoJudicial() {
            return situacaoJudicial;
        }

        public void setSituacaoJudicial(String situacaoJudicial) {
            this.situacaoJudicial = situacaoJudicial;
        }
    }

    public class CertidaoDividaAtivaDataModel extends LazyDataModel<ConsultaCertidaoDividaAtivaNaoComunicadasControlador.CdaDTO> {
        private ComunicaSofPlanFacade.ServiceId serviceId;

        public CertidaoDividaAtivaDataModel(ComunicaSofPlanFacade.ServiceId serviceId) {
            this.serviceId = serviceId;
        }

        @Override
        public List<ConsultaCertidaoDividaAtivaNaoComunicadasControlador.CdaDTO> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {

            setRowCount(certidaoDividaAtivaFacade.contarCertidoesNaoComunicadasPorServiceId(serviceId));

            return certidaoDividaAtivaFacade.buscarCertidoesNaoComunicadasPorServiceId(
                first, pageSize, serviceId);
        }
    }
}
