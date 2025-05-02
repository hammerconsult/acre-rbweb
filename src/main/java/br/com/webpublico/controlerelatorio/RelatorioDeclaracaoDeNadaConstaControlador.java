/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.MatriculaFP;
import br.com.webpublico.negocios.MatriculaFPFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Carlos
 */
@ManagedBean(name = "relatorioDeclaracaoDeNadaConstaControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorioDeclaracaoDeNadaConsta", pattern = "/rh/nada-consta/", viewId = "/faces/rh/relatorios/declaracaodenadaconsta.xhtml")
})
public class RelatorioDeclaracaoDeNadaConstaControlador extends AbstractReport implements Serializable {

    private MatriculaFP matriculaFP;
    @EJB
    private MatriculaFPFacade matriculaFPFacade;
    private String responsavel;
    private String ocupacao;

    public RelatorioDeclaracaoDeNadaConstaControlador() {
        geraNoDialog = true;
    }

    @URLAction(mappingId = "relatorioDeclaracaoDeNadaConsta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        matriculaFP = null;
        ocupacao = null;
    }

    public MatriculaFP getMatriculaFP() {
        return matriculaFP;
    }

    public void setMatriculaFP(MatriculaFP matriculaFP) {
        this.matriculaFP = matriculaFP;
    }

    public List<MatriculaFP> completaMatricula(String parte) {
        return matriculaFPFacade.recuperaMatriculaFiltroPessoa(parte.trim());

    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getOcupacao() {
        return ocupacao;
    }

    public void setOcupacao(String ocupacao) {
        this.ocupacao = ocupacao;
    }

    public void gerarDeclaracao() throws JRException, IOException {
        if (validaCampos()) {
            String arquivo = "DeclaracaoDeNadaConsta.jasper";
            HashMap parameter = new HashMap();

            Calendar c = Calendar.getInstance();
            c.setTime(UtilRH.getDataOperacao());
            parameter.put("MATRICULA_ID", matriculaFP.getId());
            parameter.put("IMAGEM", super.getCaminhoImagem());
            parameter.put("TEXTO", getTexto().replace("$NOME",matriculaFP.getPessoa().getNome()).replace("$NUMERO", matriculaFP.getMatricula()));
            parameter.put("DATA", getData().replace("$DATA", DataUtil.recuperarDataPorExtenso(UtilRH.getDataOperacao())));
            parameter.put("RESPONSAVEL", responsavel);
            parameter.put("OCUPACAO", ocupacao);
            gerarRelatorio(arquivo, parameter);
        }
    }

    private boolean validaCampos() {
        Boolean deuCerto = true;
        if (matriculaFP == null) {
            FacesUtil.addCampoObrigatorio("Por favor selecione uma pessoa.");
            deuCerto = false;
        }

        if (responsavel == null || responsavel.trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("Por favor informe o nome do responsável.");
            deuCerto = false;
        }

        if (ocupacao == null || ocupacao.trim().isEmpty()) {
            FacesUtil.addCampoObrigatorio("Por favor informe a ocupação.");
            deuCerto = false;
        }
        return deuCerto;
    }

    public String getTexto(){
        return "    Declaramos, para fins de comprovação, junto ao Município de Rio Branco e preenchimento de requisito " +
                "formal contido na Instrução Normativa nº 01/2015, artigo 4º, incisos I alínea “c”, III alínea “c”, " +
                "IV alínea “c”, que de acordo com os nossos registros funcionais, o Sr(a) $NOME, servidor(a) do " +
                "quadro efetivo, matriculado(a) sob o nº $NUMERO, não sofreu punição disciplinar, após processo " +
                "de sindicância ou processo administrativo disciplinar, pelo período de 04 (quatro) anos na " +
                "permanência no nível em que se encontra.";
    }

    public String getData(){
        return "Rio Branco, $DATA";
    }

}
