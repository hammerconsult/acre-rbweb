package br.com.webpublico.nfse.controladores;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.PlanoGeralContasComentado;
import br.com.webpublico.nfse.enums.VersaoDesif;
import br.com.webpublico.nfse.facades.PlanoGeralContasComentadoFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.List;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "planoGeralContasComentadoListar",
                pattern = "/nfse/plano-geral-contas-comentado/listar/",
                viewId = "/faces/tributario/nfse/plano-geral-contas-comentado/lista.xhtml"),
        @URLMapping(id = "planoGeralContasComentadoVisualizar",
                pattern = "/nfse/plano-geral-contas-comentado/ver/#{planoGeralContasComentadoControlador.id}/",
                viewId = "/faces/tributario/nfse/plano-geral-contas-comentado/visualizar.xhtml"),
        @URLMapping(id = "planoGeralContasComentadoContaTributavel",
                pattern = "/nfse/plano-geral-contas-comentado/conta-tributavel/",
                viewId = "/faces/tributario/nfse/plano-geral-contas-comentado/contaTributavel.xhtml"),
        @URLMapping(id = "planoGeralContasComentadoCopiar",
                pattern = "/nfse/plano-geral-contas-comentado/copiar/",
                viewId = "/faces/tributario/nfse/plano-geral-contas-comentado/copiar.xhtml"),
})
public class PlanoGeralContasComentadoControlador extends PrettyControlador<PlanoGeralContasComentado> implements CRUD {

    @EJB
    private PlanoGeralContasComentadoFacade facade;
    private CadastroEconomico cadastroEconomico;
    private CadastroEconomico cadastroEconomicoDestino;
    private List<PlanoGeralContasComentado> contas;
    private String grupoCosif;
    private AssistenteBarraProgresso assistenteBarraProgresso;
    private Future future;

    public PlanoGeralContasComentadoControlador() {
        super(PlanoGeralContasComentado.class);
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public CadastroEconomico getCadastroEconomicoDestino() {
        return cadastroEconomicoDestino;
    }

    public void setCadastroEconomicoDestino(CadastroEconomico cadastroEconomicoDestino) {
        this.cadastroEconomicoDestino = cadastroEconomicoDestino;
    }

    public List<PlanoGeralContasComentado> getContas() {
        return contas;
    }

    public void setContas(List<PlanoGeralContasComentado> contas) {
        this.contas = contas;
    }

    public PlanoGeralContasComentadoFacade getFacade() {
        return facade;
    }

    public void setFacade(PlanoGeralContasComentadoFacade facade) {
        this.facade = facade;
    }

    public String getGrupoCosif() {
        return grupoCosif;
    }

    public void setGrupoCosif(String grupoCosif) {
        this.grupoCosif = grupoCosif;
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/plano-geral-contas-comentado/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public AssistenteBarraProgresso getAssistenteBarraProgresso() {
        return assistenteBarraProgresso;
    }

    public void setAssistenteBarraProgresso(AssistenteBarraProgresso assistenteBarraProgresso) {
        this.assistenteBarraProgresso = assistenteBarraProgresso;
    }

    @Override
    @URLAction(mappingId = "planoGeralContasComentadoVisualizar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "planoGeralContasComentadoContaTributavel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void marcacaoContasTributaveis() {
        cadastroEconomico = null;
        contas = Lists.newArrayList();
    }

    public boolean isVersaoAbrasf32() {
        return VersaoDesif.VERSAO_ABRASF_3_2.equals(selecionado.getCadastroEconomico().getEnquadramentoVigente().getVersaoDesif());
    }

    public boolean isVersao10() {
        return VersaoDesif.VERSAO_1_0.equals(selecionado.getCadastroEconomico().getEnquadramentoVigente().getVersaoDesif());
    }

    public void buscarContas() {
        contas = Lists.newArrayList();
        if (cadastroEconomico != null) {
            contas = facade.buscarContasVigentesPorCadastroEconomico(cadastroEconomico);
            if (contas == null || contas.isEmpty()) {
                FacesUtil.addAtencao("Nenhuma conta vigênte encontrada para o cadastro econômico selecionado.");
            }
        }
    }

    public void handleContaTributavel(PlanoGeralContasComentado pgcc) {
        facade.updateContaTributavel(pgcc);
    }

    public void marcarContasGrupoCosif(Boolean value) {
        if (!contas.isEmpty()) {
            for (PlanoGeralContasComentado conta : contas) {
                if (conta.getCosif().getConta().startsWith(grupoCosif)) {
                    conta.setContaTributavel(value);
                    facade.updateContaTributavel(conta);
                }
            }
        }
    }

    public void validarCopiaPGCC() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroEconomico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro Econômico (Origem) deve ser informado.");
        }
        if (cadastroEconomicoDestino == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Cadastro Econômico (Destino) deve ser informado.");
        }
        if (!ve.temMensagens()) {
            if (cadastroEconomico.equals(cadastroEconomicoDestino)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro Econômico (Origem) deve ser diferente do Cadastro Econômico (Destino).");
            }
            List<PlanoGeralContasComentado> contas = facade.buscarContasRootVigentes(cadastroEconomicoDestino);
            if (contas != null && !contas.isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Cadastro Econômico (Destino) já possui um PGCC vigênte.");
            }
        }
        ve.lancarException();
    }

    public void copiarPgcc() {
        try {
            validarCopiaPGCC();
            assistenteBarraProgresso = new AssistenteBarraProgresso();
            future = facade.copiarPgcc(assistenteBarraProgresso, cadastroEconomico, cadastroEconomicoDestino);
            FacesUtil.executaJavaScript("pollCopia.start()");
            FacesUtil.executaJavaScript("openDialog(dlgAcompanhamento)");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            logger.debug("Erro na cópia de pgcc.", e);
            logger.error("Erro na cópia de pgcc.");
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void acompanharCopiaPgcc() {
        if (future != null && future.isDone()) {
            FacesUtil.executaJavaScript("pollCopia.stop()");
            finalizarCopiaPgcc();
        }
    }

    private void finalizarCopiaPgcc() {
        FacesUtil.executaJavaScript("closeDialog(dlgAcompanhamento)");
        cadastroEconomico = null;
        cadastroEconomicoDestino = null;
        FacesUtil.atualizarComponente("formulario");
        FacesUtil.addOperacaoRealizada("PGCC copiado com sucesso!");
    }
}
