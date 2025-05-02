package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.CalculoRBTrans;
import br.com.webpublico.entidades.DAM;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.consultaparcela.ConsultaParcela;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@ManagedBean(name = "consultaDebitosRBTransControlador")
@ViewScoped
@URLMappings(mappings = {

    @URLMapping(id = "listarDebitosRBTrans", pattern = "/lancamento-de-taxas-rb-trans/listar/",
        viewId = "/faces/tributario/rbtrans/debitos/lista.xhtml"),

    @URLMapping(id = "verDebitosRBTrans", pattern = "/lancamento-de-taxas-rb-trans/ver/#{consultaDebitosRBTransControlador.id}/",
        viewId = "/faces/tributario/rbtrans/debitos/visualizar.xhtml")
})
public class ConsultaDebitosRBTransControlador extends PrettyControlador<CalculoRBTrans> implements Serializable, CRUD {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaDebitosRBTransControlador.class);

    @EJB
    private CalculoRBTransFacade calculoRBTransFacade;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    @EJB
    private PermissaoTransporteFacade permissaoTransporteFacade;
    private List<ResultadoParcela> resultadoConsulta;


    public ConsultaDebitosRBTransControlador() {
        super(CalculoRBTrans.class);
    }


    @Override
    public String getCaminhoPadrao() {
        return "/lancamento-de-taxas-rb-trans/";
    }

    public String caminhoAtual() {
        if (selecionado.getId() != null) {
            return getCaminhoPadrao() + "editar/" + getUrlKeyValue() + "/";
        }
        return getCaminhoPadrao() + "novo/";
    }

    @Override
    public AbstractFacade getFacede() {
        return calculoRBTransFacade;
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "verDebitosRBTrans", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
        recuperaParcelas();
    }

    public List<ResultadoParcela> getResultadoConsulta() {
        return resultadoConsulta;
    }

    public void recuperaParcelas() {
        ConsultaParcela consultaParcela = new ConsultaParcela();
        consultaParcela.addParameter(ConsultaParcela.Campo.CALCULO_ID, ConsultaParcela.Operador.IGUAL, selecionado.getId());
        resultadoConsulta = consultaParcela.executaConsulta().getResultados();
    }

    public SistemaControlador getSistemaControlador() {
        return (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
    }

    public DAM recuperaDAM(Long parcela) {
        return calculoRBTransFacade.recuperaDAM(parcela);
    }

    public void gerarDamIndividual() {
        try {
            List<DAM> dams = Lists.newArrayList();
            for (ResultadoParcela resultadoParcela : resultadoConsulta) {
                DAM dam = consultaDebitoFacade.getDamFacade().gerarDAMUnicoViaApi(consultaDebitoFacade.getSistemaFacade().getUsuarioCorrente(),
                        resultadoParcela);
                dams.add(dam);
            }
            ImprimeDAM imprimeDAM = new ImprimeDAM();
            imprimeDAM.setGeraNoDialog(true);
            imprimeDAM.imprimirDamUnicoViaApi(dams);
        } catch (Exception e) {
            FacesUtil.addError("Não foi possível gerar o DAM", "Ocorreu um problema no servidor, tente denovo, se o problema persistir entre em contato com o suporte");
            logger.debug(e.getMessage());
        }
    }

    public void imprimirDemonstrativoTaxa() {
        new RelatorioDemonstrativo().imprimir();
    }

    public class RelatorioDemonstrativo extends AbstractReport {


        public void imprimir() {
            try {
                if (resultadoConsulta != null && !resultadoConsulta.isEmpty()) {
                    recuperaParcelas();
                }
                String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF") + "/report/";

                HashMap parameters = new HashMap();
                parameters.put("SUBREPORT_DIR", subReport);
                parameters.put("BRASAO", getCaminhoImagem());
                parameters.put("USUARIO", SistemaFacade.obtemLogin());

                parameters.put("ID_TAXA", selecionado.getId());
                this.setGeraNoDialog(true);
                gerarRelatorio("DemonstrativoCalculoRBTrans.jasper", parameters);
            } catch (Exception e) {
                logger.debug(e.getMessage());
            }
        }

    }

    public Boolean isPossivelEmitirDam() {
        if (selecionado == null) {
            return Boolean.FALSE;
        }
        if (resultadoConsulta == null || resultadoConsulta.isEmpty()) {
            recuperaParcelas();
        }
        for (ResultadoParcela resultadoParcela : resultadoConsulta) {
            if (SituacaoParcela.EM_ABERTO.equals(resultadoParcela.getSituacaoEnumValue())) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public Boolean isPossivelEmitirDemonstrativo() {
        if (selecionado == null) {
            return Boolean.FALSE;
        }
        if (resultadoConsulta == null || resultadoConsulta.isEmpty()) {
            recuperaParcelas();
        }
        for (ResultadoParcela resultadoParcela : resultadoConsulta) {
            if (resultadoParcela.getQuantidadeDamImpresso() > 0) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

}
