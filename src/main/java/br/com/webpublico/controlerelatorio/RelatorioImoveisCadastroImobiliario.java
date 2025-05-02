package br.com.webpublico.controlerelatorio;


import br.com.webpublico.controle.SistemaControlador;
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
 * Created by israeleriston on 16/07/15.
 */
@ManagedBean(name = "relatorioImoveisCadastroImobiliario")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorioImoveisCadastro",
                pattern = "/tributario/cadastromunicipal/relatorio/relatorio-imoveis-cadastro-imobiliario/",
                viewId = "/faces/tributario/cadastromunicipal/relatorio/relatorioimoveiscadastroimobiliario.xhtml")
})
public class RelatorioImoveisCadastroImobiliario extends AbstractReport implements Serializable {
    private SistemaControlador sistemaControlador;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private String setorInicial;
    private String setorFinal;
    private String quadraInicial;
    private String quadraFinal;
    private String loteInicial;
    private String loteFinal;
    private String bairroInicial;
    private String bairroFinal;
    private String logradouroInicial;
    private String logradouroFinal;
    private String situacao;
    private String tipoImovel;
    private String filtro;
    public static final String ARQUIVO_JASPER = "RelatorioImoveisCadastroImobiliario.jasper";

    @URLAction(mappingId = "relatorioImoveisCadastro", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        iniciarObjetos();
    }

    private void iniciarObjetos() {
        sistemaControlador = new SistemaControlador();
        inscricaoInicial = new String();
        inscricaoFinal = new String();
        setorInicial = new String();
        setorFinal = new String();
        quadraInicial = new String();
        quadraFinal = new String();
        loteInicial = new String();
        loteFinal = new String();
        bairroInicial = new String();
        bairroFinal = new String();
        logradouroInicial = new String();
        logradouroFinal = new String();
        situacao = new String();
        tipoImovel = new String();
        filtro = new String();
    }


    public List<SelectItem> selectItemTipoImovel() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(" ", "TODOS"));
        retorno.add(new SelectItem(0, "1-Predial"));
        retorno.add(new SelectItem(1, "2-Territorial"));
        return retorno;
    }

    public List<SelectItem> selectItemSituacao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(" ", "TODOS"));
        retorno.add(new SelectItem(1, "1-Ativo"));
        retorno.add(new SelectItem(0, "2-Inativo"));
        return retorno;
    }

    public void imprimirRelatorio() throws JRException, IOException {
        if (podeGerarRelatorio()) {
            SistemaControlador sistemaControlador = (SistemaControlador) Util.getControladorPeloNome("sistemaControlador");
            HashMap parameter = new HashMap();

            parameter.put("BRASAO", getCaminhoImagem());
            parameter.put("WHERE", montaCondicao());
            parameter.put("FILTRO", filtro);
            parameter.put("USUARIO", sistemaControlador.getUsuarioCorrente().getNome());
            parameter.put("NOMERELATORIO", "RELAÇÃO DE IMÓVEIS POR CADASTRO IMOBILIÁRIO");

            try {
                gerarRelatorio(ARQUIVO_JASPER, parameter);
            } catch (Exception e) {
                FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            }
        }
    }

    public void limparCampos() {
        novo();
    }

    public boolean podeGerarRelatorio() {
        boolean valida = true;
        if (inscricaoInicial.trim().isEmpty()) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Inscrição Inicial deve ser informado.");
        }
        if (inscricaoFinal.trim().isEmpty()) {
            valida = false;
            FacesUtil.addCampoObrigatorio("Inscrição Final deve ser informado.");
        }
        return valida;
    }

    public String montaCondicao() {
        StringBuilder sb = new StringBuilder(" where 1 = 1  ");
        String and = " and ";
        filtro = "";

        if (!inscricaoInicial.trim().isEmpty()) {
            sb.append(and).append(" ci.inscricaocadastral like '" + inscricaoInicial + "%' ");
            filtro += " Inscrição Inicial :  " + inscricaoInicial + " ; ";
        }
        if (!inscricaoFinal.trim().isEmpty()) {
            sb.append(and).append(" ci.inscricaocadastral like '" + inscricaoFinal + "%' ");
            filtro += "  Inscrição Final : " + inscricaoFinal + " ; ";
        }
        if (!setorInicial.trim().isEmpty()) {
            sb.append(and).append(" setor.nome like '" + setorInicial + "%' ");
            filtro += " Setor Inicial : " + setorInicial + " ; ";
        }
        if (!setorFinal.trim().isEmpty()) {
            sb.append(and).append(" setor.nome like '" + setorFinal + "%' ");
            filtro += " Setor Final : " + setorFinal + " ; ";
        }
        if (!quadraInicial.trim().isEmpty()) {
            sb.append(and).append(" quadra.descricao like '" + quadraInicial + "%' ");
            filtro += " Quadra Inicial : " + quadraInicial + " ; ";
        }
        if (!quadraFinal.trim().isEmpty()) {
            sb.append(and).append("quadra.descricao like '" + quadraFinal + "%' ");
            filtro += "Quadra Final : " + quadraFinal + " ; ";
        }
        if (!loteInicial.trim().isEmpty()) {
            sb.append(and).append(" l.codigolote like '" + loteInicial + "%' ");
            filtro += "Lote Inicial : " + loteInicial + " ; ";
        }
        if (!loteFinal.trim().isEmpty()) {
            sb.append(and).append(" l.codigolote like '" + loteFinal + "%' ");
            filtro += " Lote Final : " + loteFinal + " ; ";
        }
        if (!bairroInicial.trim().isEmpty()) {
            sb.append(and).append(" ba.descricao like '" + bairroInicial.toLowerCase() + "%' ");
            filtro += " Bairro Inicial : " + bairroInicial + " ; ";
        }
        if (!bairroFinal.trim().isEmpty()) {
            sb.append(and).append(" ba.descricao like '" + bairroFinal.toLowerCase() + "%' ");
            filtro += " Bairro Final : " + bairroFinal + " ; ";
        }
        if (!logradouroInicial.trim().isEmpty()) {
            sb.append(and).append(" logradouro.nomeusual like '" + logradouroInicial.toLowerCase() + "%' ");
            filtro += "Logradouro Inicial : " + logradouroInicial + " ; ";
        }
        if (!logradouroFinal.trim().isEmpty()) {
            sb.append(and).append(" logradouro.nomeusual like '" + logradouroFinal.toLowerCase() + "%' ");
            filtro += " Logradouro Final : " + logradouroFinal + " ; ";
        }
        if (tipoImovel != null) {
            if (tipoImovel.equals("1")) {
                sb.append(and).append("  con.cancelada like '" + tipoImovel + "%' ");
                filtro += "   Tipo Imóvel: TERRITORIAL " + " ; ";
            }
            if (tipoImovel.equals("0")) {
                sb.append(and).append("  con.cancelada like '" + tipoImovel + "%'  ");
                filtro += "   Tipo Imóvel: PREDIAL " + " ; ";
            }
        }
        if (situacao != null) {
            if (situacao.equals("1")) {
                sb.append(and).append("  ci.ativo <> 0  ");
                filtro += " Situação : ATIVO " + " ; ";
            }
            if (situacao.equals("0")) {
                sb.append(and).append(" ci.ativo = 0 ");
                filtro += " Situação : INATIVO " + " ;  ";
            }
        }


        return sb.toString();
    }

    public String getBairroFinal() {
        return bairroFinal;
    }

    public String getBairroInicial() {
        return bairroInicial;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public String getLogradouroFinal() {
        return logradouroFinal;
    }

    public String getLogradouroInicial() {
        return logradouroInicial;
    }

    public String getLoteFinal() {
        return loteFinal;
    }

    public String getSetorInicial() {
        return setorInicial;
    }

    public String getSetorFinal() {
        return setorFinal;
    }

    public String getQuadraInicial() {
        return quadraInicial;
    }

    public String getQuadraFinal() {
        return quadraFinal;
    }

    public String getLoteInicial() {
        return loteInicial;
    }

    public String getSituacao() {
        return situacao;
    }

    public String getTipoImovel() {
        return tipoImovel;
    }

    public String getFiltro() {
        return filtro;
    }

    public void setBairroFinal(String bairroFinal) {
        this.bairroFinal = bairroFinal;
    }

    public void setFiltro(String filtro) {
        this.filtro = filtro;
    }

    public void setBairroInicial(String bairroInicial) {
        this.bairroInicial = bairroInicial;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public void setLogradouroFinal(String logradouroFinal) {
        this.logradouroFinal = logradouroFinal;
    }

    public void setLogradouroInicial(String logradouroInicial) {
        this.logradouroInicial = logradouroInicial;
    }

    public void setLoteFinal(String loteFinal) {
        this.loteFinal = loteFinal;
    }

    public void setLoteInicial(String loteInicial) {
        this.loteInicial = loteInicial;
    }

    public void setQuadraFinal(String quadraFinal) {
        this.quadraFinal = quadraFinal;
    }

    public void setQuadraInicial(String quadraInicial) {
        this.quadraInicial = quadraInicial;
    }

    public void setSetorFinal(String setorFinal) {
        this.setorFinal = setorFinal;
    }

    public void setSetorInicial(String setorInicial) {
        this.setorInicial = setorInicial;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public void setTipoImovel(String tipoImovel) {
        this.tipoImovel = tipoImovel;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}
