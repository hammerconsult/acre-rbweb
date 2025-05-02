/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoAcaoFiscal;
import br.com.webpublico.enums.TipoUsuarioTributario;
import br.com.webpublico.negocios.AcaoFiscalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.UsuarioSistemaFacade;
import br.com.webpublico.util.FacesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author MGA
 */
@ManagedBean
public class RelacaoAcaoFiscalPorFiscalControlador extends AbstractReport {

    private static final Logger logger = LoggerFactory.getLogger(RelacaoAcaoFiscalPorFiscalControlador.class);

    private String programacaoInicial;
    private String programacaoFinal;
    private String ordemServicoInicial;
    private String ordemServicoFinal;
    private String cmcInicial;
    private String cmcFinal;
    private SituacaoAcaoFiscal[] situacoes;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private AcaoFiscalFacade acaoFiscalFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    private UsuarioSistema[] usuarios;
    private String condicao;
    private String filtros;

    public String getProgramacaoInicial() {
        return programacaoInicial;
    }

    public void setProgramacaoInicial(String programacaoInicial) {
        this.programacaoInicial = programacaoInicial;
    }

    public String getProgramacaoFinal() {
        return programacaoFinal;
    }

    public void setProgramacaoFinal(String programacaoFinal) {
        this.programacaoFinal = programacaoFinal;
    }

    public String getOrdemServicoInicial() {
        return ordemServicoInicial;
    }

    public void setOrdemServicoInicial(String ordemServicoInicial) {
        this.ordemServicoInicial = ordemServicoInicial;
    }

    public String getOrdemServicoFinal() {
        return ordemServicoFinal;
    }

    public void setOrdemServicoFinal(String ordemServicoFinal) {
        this.ordemServicoFinal = ordemServicoFinal;
    }

    public String getCmcInicial() {
        return cmcInicial;
    }

    public void setCmcInicial(String cmcInicial) {
        this.cmcInicial = cmcInicial;
    }

    public String getCmcFinal() {
        return cmcFinal;
    }

    public void setCmcFinal(String cmcFinal) {
        this.cmcFinal = cmcFinal;
    }

    public SituacaoAcaoFiscal[] getSituacoes() {
        return situacoes;
    }

    public void setSituacoes(SituacaoAcaoFiscal[] situacoes) {
        this.situacoes = situacoes;
    }

    public UsuarioSistema[] getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(UsuarioSistema[] usuarios) {
        this.usuarios = usuarios;
    }

    public List<UsuarioSistema> getListaUsuarios() {
        return usuarioSistemaFacade.listaUsuarioTributarioVigentePorTipo(TipoUsuarioTributario.FISCAL_TRIBUTARIO);
    }

    public List<SituacaoAcaoFiscal> getListaSituacoes() {
        return Arrays.asList(SituacaoAcaoFiscal.values());
    }

    public void imprime() {
        montaCondicao();
        //System.out.println("where == " + condicao);
        String caminhoBrasao = getCaminhoImagem();
        String arquivoJasper = "RelacaoAcaoFiscalPorFiscal.jasper";
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("WHERE", condicao);
        parameters.put("BRASAO", caminhoBrasao);
        parameters.put("FILTROS", filtros);
        parameters.put("MODULO", "Tributário");
        parameters.put("SECRETARIA", "Secretaria de Finanças");
        parameters.put("NOMERELATORIO", "Demonstrativo de Ação Fiscal por Fiscal");
        parameters.put("USUARIO", sistemaFacade.getLogin());
        try {
            gerarRelatorio(arquivoJasper, parameters);
        } catch (Exception ex) {
            logger.error("Erro: ", ex);
            FacesUtil.addError("Atenção!", "O Relatório não pode ser gerado por um problema interno do servidor");
        }
    }

    private void montaCondicao() {
        String juncao = " where ";
        condicao = "";
        filtros = "";
        if (programacaoInicial != null && !programacaoInicial.trim().isEmpty()) {
            condicao += juncao + " programacao.numero > = " + programacaoInicial;
            juncao = " and ";
            filtros += " Programação Inicial " + programacaoInicial;
        }
        if (programacaoFinal != null && !programacaoFinal.trim().isEmpty()) {
            condicao += juncao + " programacao.numero < = " + programacaoFinal;
            juncao = " and ";
            filtros += " Programação Final " + programacaoFinal;
        }
        if (cmcInicial != null && !cmcInicial.trim().isEmpty()) {
            condicao += juncao + " cmc.inscricaocadastral > = '" + cmcInicial + "'";
            juncao = " and ";
            filtros += " C.M.C Inicial " + cmcInicial;
        }
        if (cmcFinal != null && !cmcFinal.trim().isEmpty()) {
            condicao += juncao + " cmc.inscricaocadastral < = '" + cmcFinal + "'";
            juncao = " and ";
            filtros += " C.M.C Final " + cmcFinal;
        }
        if (ordemServicoInicial != null && !ordemServicoInicial.trim().isEmpty()) {
            condicao += juncao + " acao.ordemservico > = " + ordemServicoInicial;
            juncao = " and ";
            filtros += " Ordem de Servio Inicial " + ordemServicoInicial;
        }
        if (ordemServicoFinal != null && !ordemServicoFinal.trim().isEmpty()) {
            condicao += juncao + " acao.ordemservico < = " + ordemServicoFinal;
            juncao = " and ";
            filtros += " Ordem de Servio Final " + ordemServicoFinal;
        }
        if (situacoes != null && situacoes.length > 0) {
            if (situacoes.length > 1) {
                String s = "(";
                for (int x = 0; x < situacoes.length; x++) {
                    s += "'" + situacoes[x].name() + "'";
                    if (x != situacoes.length - 1) {
                        s += ", ";
                    }
                }
                s += ")";
                condicao += juncao + " acao.situacaoacaofiscal in " + s;
                filtros += " Situações " + s;
            } else {
                condicao += juncao + " acao.situacaoacaofiscal = " + situacoes[0];
                filtros += " Situações " + situacoes[0];
            }
            juncao = " and ";
        }
        if (isDiretor()) {
            if (usuarios.length > 1) {
                String s = " ";
                s += "(";
                for (int x = 0; x < usuarios.length; x++) {
                    s += usuarios[x].getId();
                    if (x != usuarios.length - 1) {
                        s += ", ";
                    }
                }
                s += ")";
                condicao += juncao + " usuario.id in = " + s;
                filtros += " Usuários " + s;
            } else if (usuarios.length == 1) {
                condicao += juncao + " usuario.id = " + usuarios[0].getId();
                filtros += " Usuários " + usuarios[0];
            }
        } else {
            condicao += juncao + " usuario.id = " + sistemaFacade.getUsuarioCorrente().getId();
        }

    }

    public boolean isDiretor() {
        return acaoFiscalFacade.usuarioPodeReabrir(sistemaFacade.getUsuarioCorrente());
    }
}
