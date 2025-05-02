package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoContratoRendasPatrimoniais;
import br.com.webpublico.negocios.ContratoRendasPatrimoniaisFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.ServletContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 16/07/14
 * Time: 15:46
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relacaoDebitosDosContratosRendasPatrimoniais", pattern = "/debitos-dos-contratos-rendas-patrimoniais/", viewId = "/faces/tributario/rendaspatrimoniais/relatorio/debitosdoscontratos.xhtml")
})
public class DebitosDosContratosRendasPatrimoniais extends AbstractReport {
    public SistemaControlador sistemaControlador;
    @EJB
    public ContratoRendasPatrimoniaisFacade contratoRendasPatrimoniaisFacade;
    private final String arquivoJasper = "DebitosDeContratosRendasPatrimoniais.jasper";
    private StringBuilder semDados;
    private StringBuilder where;
    private ContratoRendasPatrimoniais selecionado;
    private ConfiguracaoTributario configuracaoTributario;
    private Localizacao localizacao;
    private Exercicio exercicio;
    private SituacaoContratoRendasPatrimoniais situacao;


    public ContratoRendasPatrimoniais getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(ContratoRendasPatrimoniais selecionado) {
        this.selecionado = selecionado;
    }

    public DebitosDosContratosRendasPatrimoniais() {
        sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
        geraNoDialog = true;
    }

    public SituacaoContratoRendasPatrimoniais getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoContratoRendasPatrimoniais situacao) {
        this.situacao = situacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Localizacao getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(Localizacao localizacao) {
        this.localizacao = localizacao;
    }

    @URLAction(mappingId = "relacaoDebitosDosContratosRendasPatrimoniais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        selecionado = new ContratoRendasPatrimoniais();
        configuracaoTributario = contratoRendasPatrimoniaisFacade.getConfiguracaoTributarioFacade().retornaUltimo();
    }

    public boolean podeImprimirRelatorio() {
        boolean returno = true;
        if (this.getLocalizacao() == null) {
            FacesUtil.addOperacaoNaoRealizada("Campo Localização é obrigatório.");
            returno = false;
        }
        if (this.getExercicio() == null) {
            FacesUtil.addOperacaoNaoRealizada("Campo Exercício é obrigatório.");
            returno = false;
        }
        return returno;
    }

    public void montaRelatorio() throws IOException, JRException {
        if (podeImprimirRelatorio()) {
            semDados = new StringBuilder("Não foram encontrados registros");
            where = new StringBuilder("where ");
            StringBuffer local = new StringBuffer();
            HashMap parametros = new HashMap();
            where.append("localizacao.id =").append(this.getLocalizacao().getId()).append(" and extract(year from contrato.datainicio) = ").append(exercicio.getAno());

            if (this.situacao != null) {
                where.append(" and contrato.situacaocontrato = '").append(situacao.name()).append("'");
            }
            String subReport = ((ServletContext) (FacesContext.getCurrentInstance()).getExternalContext().getContext()).getRealPath("/WEB-INF/report") + "/";
            parametros.put("SUBREPORT_DIR", subReport);
            parametros.put("SEMDADOS", semDados.toString());
            parametros.put("BRASAO", getCaminhoImagem());
            parametros.put("SECRETARIA", "Secretaria Municipal de Finanças");
            parametros.put("NOMERELATORIO", "Relação de Permissionário por Pontos Comerciais");
            parametros.put("MODULO", "TRIBUTÁRIO");
            parametros.put("USUARIO", sistemaControlador.getUsuarioCorrente().getNome());
            parametros.put("WHERE", where.toString());
            parametros.put("LOCALIZACAO", local.toString());
            parametros.put("ANO", exercicio.getAno());
            parametros.put("USUARIO", contratoRendasPatrimoniaisFacade.getSistemaFacade().getUsuarioCorrente().getNome());
            gerarRelatorio(arquivoJasper, parametros);
        }
    }

    public List<Localizacao> completaLocalizacao(String parte) {
        return contratoRendasPatrimoniaisFacade.getLocalizacaoFacade().listaFiltrandoPorLotacao(selecionado.getLotacaoVistoriadora(), parte.trim(), "descricao");
    }

    public List<SelectItem> getSituacaoContrato() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "TODOS"));
        for (SituacaoContratoRendasPatrimoniais situacao : SituacaoContratoRendasPatrimoniais.values()) {
            retorno.add(new SelectItem(situacao, situacao.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getLotacoesRendasPatrimoniais() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, ""));
        for (LotacaoVistoriadoraConfigTribRendas lotacaoConfigTrib : configuracaoTributario.getRendasLotacoesVistoriadoras()) {
            retorno.add(new SelectItem(lotacaoConfigTrib.getLotacaoVistoriadora(), lotacaoConfigTrib.getLotacaoVistoriadora().toString()));
        }
        return retorno;
    }
}

