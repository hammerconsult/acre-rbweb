package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.SistemaControlador;
import br.com.webpublico.entidades.CadastroImobiliario;
import br.com.webpublico.entidades.Construcao;
import br.com.webpublico.entidades.Propriedade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.JRException;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by israeleriston on 08/07/15.
 */
@ManagedBean(name = "relatorioImoveisContribuinteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioImoveisPorContribuinte",
        pattern = "/tributario/cadastromunicipal/relatorio/relatorio-imoveis-contribuinte/",
        viewId = "/faces/tributario/cadastromunicipal/relatorio/relatorioimoveiscontribuinte.xhtml")
})
public class RelatorioImoveisContribuinteControlador extends AbstractReport implements Serializable {


    private Propriedade propriedade;
    private Construcao construcao;
    private CadastroImobiliario cadastroImobiliario;
    private SistemaControlador sistemaControlador;
    private String proprietarioInicial;
    private String proprietarioFinal;
    private String inscricaoCadastralInicial;
    private String inscricaoCadastralFinal;
    private String filtro;
    private String tipoImovel;
    private String ativo;

    public static final String ARQUIVO_JASPER = "RelatorioImoveisContribuinte.jasper";
    public static final String ARQUIVO_SUBREPORT = "RelatorioImoveisContribuinteCabecalho_subreport.jasper";


    @URLAction(mappingId = "relatorioImoveisPorContribuinte", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        iniciarObjetos();
    }

    private void iniciarObjetos() {
        propriedade = new Propriedade();
        construcao = new Construcao();
        cadastroImobiliario = new CadastroImobiliario();
        sistemaControlador = new SistemaControlador();
        proprietarioInicial = new String();
        proprietarioFinal = new String();
        inscricaoCadastralInicial = new String();
        inscricaoCadastralFinal = new String();
        filtro = new String();
        tipoImovel = new String();
        ativo = new String();

    }


    public List<SelectItem> selectItemTipoImovel() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(" ", "TODOS"));
        retorno.add(new SelectItem(1, "1-PREDIAL"));
        retorno.add(new SelectItem(0, "2-TERRITORIAL"));
        return retorno;
    }

    public List<SelectItem> selectItemSituacao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(" ", "TODOS"));
        retorno.add(new SelectItem(1, "1-ATIVO"));
        retorno.add(new SelectItem(0, "2-INATIVO"));
        return retorno;
    }

    public void imprimirRelatorio() throws JRException, IOException {

        if (validaCampos()) {
            HashMap parameter = new HashMap();
            SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
            parameter.put("BRASAO", getCaminhoImagem());
            parameter.put("NOMERELATORIO", "Relação de Imóveis por Contribuinte");
            parameter.put("USUARIO", sistemaControlador.getUsuarioCorrente().getNome());
            parameter.put("WHERE", montarCondicao());
            parameter.put("FILTRO", filtro);

            try {
                gerarRelatorio(ARQUIVO_JASPER, parameter);
            } catch (Exception e) {
                FacesUtil.addErroAoGerarRelatorio("Não foi possível gerar o relatório, se o problema pesistir consulte o suporte técnico");
            }
        }
    }


    public String montarCondicao() {
        StringBuilder sb = new StringBuilder("where");
        String and = " and ";
        String or = " or ";
        filtro = "";


        String filtroInicialProprietario = new String();
        String filtroFinalProprietario = new String();


        filtroInicialProprietario = proprietarioInicial.substring(0, proprietarioInicial.length()).toUpperCase();
        filtroFinalProprietario = proprietarioFinal.substring(0, proprietarioFinal.length()).toUpperCase();


        if (proprietarioInicial != null && proprietarioFinal != null) {
            sb.append(
                "  ((REGEXP_LIKE(pf.nome,'^" + filtroInicialProprietario + "') ").append(or).append(
                "   REGEXP_LIKE(pj.nomefantasia,'^" + filtroInicialProprietario + "')) ").append(or).append(
                "   REGEXP_LIKE(pf.nome,'^" + filtroFinalProprietario + "')  ").append(or).append(
                "   REGEXP_LIKE(pj.nomefantasia,'^" + filtroFinalProprietario + "')) ");
            filtro += "  Proprietário Inicial : " + proprietarioInicial + " ; " + "   Proprietário Final : " + proprietarioFinal + "   ";
        }

        if (tipoImovel != null) {
            if (tipoImovel.equals("1")) {
                sb.append(and).append("  REGEXP_LIKE(con.CANCELADA, '[^" + tipoImovel + "]') ");
                filtro += " ;  Tipo Imóvel: PREDIAL " + " ; ";
            }
            if (tipoImovel.equals("0")) {
                sb.append(and).append("  REGEXP_LIKE(con.CANCELADA, '[^" + tipoImovel + "]')  ");
                filtro += " ;  Tipo Imóvel: TERRITORIAL " + " ; ";
            }
        }
        if (ativo != null) {
            if (ativo.equals("1")) {
                sb.append(and).append("  ci.ativo <> 0  ");
                filtro += " Situação : ATIVO " + " ; ";
            }
            if (ativo.equals("0")) {
                sb.append(and).append(" ci.ativo = 0 ");
                filtro += " Situação : INATIVO " + " ;  ";
            }
        }
        if (!inscricaoCadastralInicial.trim().isEmpty()) {
            sb.append(and).append(" (ci.inscricaocadastral >='" + inscricaoCadastralInicial + "')  ");
            filtro += " ;  Inscrição Cadastral Inicial : " + inscricaoCadastralInicial + "   ";
        }
        if (!inscricaoCadastralFinal.trim().isEmpty()) {
            sb.append(and).append("(ci.inscricaocadastral <='" + inscricaoCadastralFinal + "') ");
            filtro += " ;  Inscrição Cadastral Final : " + inscricaoCadastralFinal + " ;  ";
        }


        return sb.toString();
    }


    public boolean validaCampos() {
        boolean valida = true;
        if (proprietarioInicial.trim().isEmpty()) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Proprietário Inicial deve ser informado.");
        } else if (proprietarioFinal.trim().isEmpty()) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Proprietário Final deve ser informado.");
        }
        return valida;
    }

    public void limparCampos() {
        novo();
    }

    public String getProprietarioInicial() {
        return proprietarioInicial;
    }

    public void setProprietarioInicial(String proprietarioInicial) {
        this.proprietarioInicial = proprietarioInicial;
    }

    public String getProprietarioFinal() {
        return proprietarioFinal;
    }

    public void setProprietarioFinal(String proprietarioFinal) {
        this.proprietarioFinal = proprietarioFinal;
    }

    public String getInscricaoCadastralInicial() {
        return inscricaoCadastralInicial;
    }

    public void setInscricaoCadastralInicial(String inscricaoCadastralInicial) {
        this.inscricaoCadastralInicial = inscricaoCadastralInicial;
    }

    public String getInscricaoCadastralFinal() {
        return inscricaoCadastralFinal;
    }

    public void setInscricaoCadastralFinal(String inscricaoCadastralFinal) {
        this.inscricaoCadastralFinal = inscricaoCadastralFinal;
    }

    public CadastroImobiliario getCadastroImobiliario() {
        return cadastroImobiliario;
    }

    public void setCadastroImobiliario(CadastroImobiliario cadastroImobiliario) {
        this.cadastroImobiliario = cadastroImobiliario;
    }

    public Construcao getConstrucao() {
        return construcao;
    }

    public void setConstrucao(Construcao construcao) {
        this.construcao = construcao;
    }

    public Propriedade getPropriedade() {
        return propriedade;
    }

    public void setPropriedade(Propriedade propriedade) {
        this.propriedade = propriedade;
    }


    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public String getTipoImovel() {
        return tipoImovel;
    }

    public void setTipoImovel(String tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public String getAtivo() {
        return ativo;
    }

    public void setAtivo(String ativo) {
        this.ativo = ativo;
    }
}
