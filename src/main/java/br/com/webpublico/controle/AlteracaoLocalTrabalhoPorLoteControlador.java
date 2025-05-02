package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.rh.sig.VinculosAlteracaoLocalTrabalho;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * @author octavio
 */
@ManagedBean(name = "alteracaoLocalTrabalhoPorLoteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoTransferenciasServidorPorLote", pattern = "/transferencias-servidor-por-lote/novo/", viewId = "/faces/rh/administracaodepagamento/transferenciasporlote/edita.xhtml"),
    @URLMapping(id = "listarTransferenciasServidorPorLote", pattern = "/transferencias-servidor-por-lote/listar/", viewId = "/faces/rh/administracaodepagamento/transferenciasporlote/lista.xhtml"),
    @URLMapping(id = "verTransferenciasServidorPorLotacao", pattern = "/transferencias-servidor-por-lote/ver/#{alteracaoLocalTrabalhoPorLoteControlador.id}/", viewId = "/faces/rh/administracaodepagamento/transferenciasporlote/visualizar.xhtml")
})
public class AlteracaoLocalTrabalhoPorLoteControlador extends PrettyControlador<AlteracaoLocalTrabalhoPorLote> implements Serializable, CRUD {

    @EJB
    private AlteracaoLocalTrabalhoPorLoteFacade alterarLocalTrabalhoPorLotacaoFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private HorarioContratoFPFacade horarioContratoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private HierarquiaOrganizacional novaHierarquiaOrganizacional;
    private LotacaoFuncional lotacaoFuncional;
    private AtoLegal atoLegal;
    private ConverterAutoComplete converterAtoLegal;
    private HorarioContratoFP horarioContratoFP;
    private ProvimentoFP provimentoFP;
    private Boolean selecionadoNaLista;
    private List<AlteracaoVinculoLotacao> alteracoesVinculoLotacao;
    private RecursoDoVinculoFP recursoDoVinculoFP;
    private List<VinculosAlteracaoLocalTrabalho> vinculosARemover;


    public AlteracaoLocalTrabalhoPorLoteControlador() {
        super(AlteracaoLocalTrabalhoPorLote.class);
    }

    @URLAction(mappingId = "novoTransferenciasServidorPorLote", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        selecionado = new AlteracaoLocalTrabalhoPorLote();
        super.novo();
        alteracoesVinculoLotacao = Lists.newArrayList();
        recursoDoVinculoFP = new RecursoDoVinculoFP();
        selecionado.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
        vinculosARemover = Lists.newArrayList();
    }

    public void validarTransferencias() {
        try {
            validarNovosCampos();
            validarViculos();
            vinculosARemover = Lists.newArrayList();
            for (AlteracaoVinculoLotacao vinculoAlteracao : selecionado.getAlteracoesVinculoLotacao()) {
                VinculoFP vinculo = vinculoFPFacade.recuperarVinculoFPComDependenciaLotacaoFuncional(vinculoAlteracao.getVinculoFP().getId());
                LotacaoFuncional lotacaoAtual = vinculo.getLotacaoFuncionalVigente();
                if ((lotacaoAtual.getUnidadeOrganizacional().equals(getNovaHierarquiaOrganizacional().getSubordinada()) &&
                    recursoDoVinculoFP.getRecursoFP().equals(vinculo.getRecursoDoVinculoFPVigente().getRecursoFP())) ||
                    (selecionado.getDataReferencia().compareTo(lotacaoAtual.getInicioVigencia()) <= 0)) {
                    String conteudo = vinculo.getMatriculaFP().getMatricula() + "/" + vinculo.getNumero() + " - " + vinculo.getMatriculaFP().getPessoa().getNome();
                    vinculosARemover.add(new VinculosAlteracaoLocalTrabalho(vinculoAlteracao, (selecionado.getDataReferencia().compareTo(lotacaoAtual.getInicioVigencia()) <= 0) ? "Data inicial da nova lotação igual ou menor a da lotação atual." : "Servidor já se encontra no recursoFP e lotação funcional informados.", conteudo));
                }
            }
            if (!vinculosARemover.isEmpty()) {
                FacesUtil.executaJavaScript("dialogLotacoesRecursosIguais.show()");
            } else {
                salvar();
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    @Override
    public void salvar() {
        try {
            FacesUtil.executaJavaScript("dialogLotacoesRecursosIguais.hide()");
            selecionado.setUnidadeOrganizacionalAtual(hierarquiaOrganizacional.getSubordinada());
            selecionado.setNovaUnidadeOrganizacional(novaHierarquiaOrganizacional.getSubordinada());
            selecionado.setAtoLegal(atoLegal);
            for (VinculosAlteracaoLocalTrabalho vinculoAlteracao : vinculosARemover) {
                selecionado.getAlteracoesVinculoLotacao().remove(vinculoAlteracao.getAlteracaoVinculoLotacao());
            }
            validarViculos();
            confirmarAlteracaoVinculo();
            selecionado.setRecursoFP(recursoDoVinculoFP.getRecursoFP());
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar a tranferência de servidor por lote {}", e);
            FacesUtil.addOperacaoNaoPermitida(e.getMessage());
        }
    }

    private void salvarHorarioContrato(HorarioContratoFP horarioAtual) {
        horarioContratoFPFacade.salvar(horarioAtual);
    }

    private void salvarFinalVigenciaLotacao(LotacaoFuncional lotacaoAtual, LotacaoFuncional novaLotacao) {

    }

    @URLAction(mappingId = "verTransferenciasServidorPorLotacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public AbstractFacade getFacede() {
        return alterarLocalTrabalhoPorLotacaoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/transferencias-servidor-por-lote/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public HierarquiaOrganizacional getNovaHierarquiaOrganizacional() {
        return novaHierarquiaOrganizacional;
    }

    public void setNovaHierarquiaOrganizacional(HierarquiaOrganizacional novaHierarquiaOrganizacional) {
        this.novaHierarquiaOrganizacional = novaHierarquiaOrganizacional;
    }

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return alterarLocalTrabalhoPorLotacaoFacade.getAtoLegalFacade().listaFiltrandoAtoLegal(parte.trim());
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public HorarioContratoFP getHorarioContratoFP() {
        return horarioContratoFP;
    }

    public void setHorarioContratoFP(HorarioContratoFP horarioContratoFP) {
        this.horarioContratoFP = horarioContratoFP;
    }

    public LotacaoFuncionalFacade getLotacaoFuncionalFacade() {
        return lotacaoFuncionalFacade;
    }

    public void setLotacaoFuncionalFacade(LotacaoFuncionalFacade lotacaoFuncionalFacade) {
        this.lotacaoFuncionalFacade = lotacaoFuncionalFacade;
    }

    public Boolean getSelecionadoNaLista() {
        return selecionadoNaLista;
    }

    public void setSelecionadoNaLista(Boolean selecionadoNaLista) {
        this.selecionadoNaLista = selecionadoNaLista;
    }

    public List<AlteracaoVinculoLotacao> getAlteracoesVinculoLotacao() {
        return alteracoesVinculoLotacao;
    }

    public void setAlteracoesVinculoLotacao(List<AlteracaoVinculoLotacao> alteracoesVinculoLotacao) {
        this.alteracoesVinculoLotacao = alteracoesVinculoLotacao;
    }

    public RecursoDoVinculoFP getRecursoDoVinculoFP() {
        return recursoDoVinculoFP;
    }

    public void setRecursoDoVinculoFP(RecursoDoVinculoFP recursoDoVinculoFP) {
        this.recursoDoVinculoFP = recursoDoVinculoFP;
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, alterarLocalTrabalhoPorLotacaoFacade.getAtoLegalFacade());
        }
        return converterAtoLegal;
    }

    public List<HierarquiaOrganizacional> completaHierarquiaOrganizacional(String parte) {
        if (selecionado.getDataReferencia() == null) {
            FacesUtil.addError("Atenção!", "A Data inicial da vigência deve ser informada!");
            return null;
        }
        return hierarquiaOrganizacionalFacade.filtrandoHierarquiaHorganizacionalTipo(parte.trim(), TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), UtilRH.getDataOperacao());
    }

    public void selecionarFuncionariosPorLotacao() {
        try {
            if (alteracoesVinculoLotacao != null) {
                alteracoesVinculoLotacao.clear();
            }
            validarCampos();
            alteracoesVinculoLotacao = alterarLocalTrabalhoPorLotacaoFacade.recuperarContratoVigentePorLotacao(hierarquiaOrganizacional, selecionado);
            selecionadoNaLista = Boolean.TRUE;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (hierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo hierarquia organizacional é obrigatório");
        }
        if (atoLegal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo ato legal é obrigatório");
        }
        ve.lancarException();
    }


    private void validarNovosCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (novaHierarquiaOrganizacional == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo nova hierarquia organizacional é obrigatório");
        }
        if (selecionado.getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo data de referência é obrigatório");
        }
        if (recursoDoVinculoFP == null || recursoDoVinculoFP.getRecursoFP() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Deve ser informado o Recurso FP.");
        }
        ve.lancarException();
    }

    private void validarViculos() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getAlteracoesVinculoLotacao().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Deve ser selecionado ao menos um servidor para transferência");
        }
        ve.lancarException();
    }


    private void confirmarAlteracaoVinculo() {
        for (AlteracaoVinculoLotacao vinculoAlteracao : selecionado.getAlteracoesVinculoLotacao()) {
            try {
                logger.debug("Criado alteração de local de trabalho para o servidor: " + vinculoAlteracao.getVinculoFP());
                criarAlteracaoVinculo(vinculoAlteracao);
                VinculoFP vinculo = vinculoFPFacade.recuperarVinculoFPComDependenciaLotacaoFuncional(vinculoAlteracao.getVinculoFP().getId());
                criarRecursoVinculoFP(vinculo);
                LotacaoFuncional lotacaoAtual = vinculo.getLotacaoFuncionalVigente();
                LotacaoFuncional novaLotacao = (LotacaoFuncional) Util.clonarObjeto(lotacaoAtual);
                HorarioContratoFP novoHorario = setarNovoHorarioContratoFP(lotacaoAtual);

                if (novaLotacao != null) {
                    lotacaoAtual.setFinalVigencia(DataUtil.getDataDiaAnterior(selecionado.getDataReferencia()));
                    lotacaoAtual.getHorarioContratoFP().setFinalVigencia(DataUtil.getDataDiaAnterior(selecionado.getDataReferencia()));
                    novaLotacao.getVinculoFP().getProvimentoFP().setAtoLegal(getAtoLegal());
                    gerarNovaLotacao(vinculo, novaLotacao, novoHorario);
                    vinculo = vinculoFPFacade.recuperarVinculoFPComDependenciaLotacaoFuncional(vinculo.getId());
                    alterarLocalTrabalhoPorLotacaoFacade.criarProvimento(vinculo, atoLegal);
                }
            } catch (Exception e) {
                logger.debug("Ocorreu um erro na alteração de local de trabalho do servidor: " + vinculoAlteracao.getVinculoFP());
            }
        }
    }

    private void criarAlteracaoVinculo(AlteracaoVinculoLotacao vinculoAlteracao) {
        AlteracaoVinculoLotacao alteracaoVinculoLotacao = new AlteracaoVinculoLotacao();
        alteracaoVinculoLotacao.setVinculoFP(vinculoAlteracao.getVinculoFP());
        alteracaoVinculoLotacao.setAlterLocTrabalhoLotacao(selecionado);
    }

    private void criarRecursoVinculoFP(VinculoFP vinculo) {
        if (vinculo.getRecursoDoVinculoFPVigente() != null) {
            vinculo.getRecursoDoVinculoFPVigente().setFinalVigencia(DataUtil.getDataDiaAnterior(selecionado.getDataReferencia()));
        }
        if (vinculo.getRecursosDoVinculoFP() == null) {
            vinculo.setRecursosDoVinculoFP(Lists.<RecursoDoVinculoFP>newArrayList());
        }
        recursoDoVinculoFP.setVinculoFP(vinculo);
        recursoDoVinculoFP.setInicioVigencia(selecionado.getDataReferencia());
        recursoDoVinculoFP.setDataRegistro(new Date());
        vinculo.getRecursosDoVinculoFP().add(recursoDoVinculoFP);
    }

    private HorarioContratoFP setarNovoHorarioContratoFP(LotacaoFuncional lotacaoAtual) {
        HorarioContratoFP horarioAtual = lotacaoAtual.getHorarioContratoFP();
        HorarioContratoFP novoHorario = (HorarioContratoFP) Util.clonarObjeto(horarioAtual);
        if (novoHorario != null) {
            novoHorario.setId(null);
        }
        if (horarioAtual != null) {
            horarioAtual.setFinalVigencia(DataUtil.getDataDiaAnterior(selecionado.getDataReferencia()));
            salvarHorarioContrato(horarioAtual);
        }
        return novoHorario;
    }

    private void gerarNovaLotacao(VinculoFP vinculo, LotacaoFuncional novaLotacao, HorarioContratoFP novoHorario) {
        novaLotacao.setId(null);
        novaLotacao.setVinculoFP(vinculo);
        novaLotacao.setHorarioContratoFP(novoHorario);
        novaLotacao.setUnidadeOrganizacional(getNovaHierarquiaOrganizacional().getSubordinada());
        novaLotacao.setInicioVigencia(selecionado.getDataReferencia());
        novaLotacao.setFinalVigencia(null);
        novaLotacao.getHorarioContratoFP().setInicioVigencia(selecionado.getDataReferencia());
        novaLotacao.getHorarioContratoFP().setFinalVigencia(null);
        vinculo.getLotacaoFuncionals().add(novaLotacao);
        HierarquiaOrganizacional orgao = hierarquiaOrganizacionalFacade.buscarOrgaoAdministrativoPorUnidadeAndVigencia
            (novaLotacao.getUnidadeOrganizacional(), UtilRH.getDataOperacao());
        if (orgao != null) {
            vinculo.setUnidadeOrganizacional(orgao.getSubordinada());
        }
        vinculoFPFacade.salvar(vinculo);
    }


    public boolean hasServidorAdicionado(AlteracaoVinculoLotacao alteracao) {
        return selecionado.getAlteracoesVinculoLotacao().contains(alteracao);
    }

    public boolean hasServidorAdicionado() {
        return !selecionado.getAlteracoesVinculoLotacao().isEmpty();
    }


    public void adicionarServidorAlteracaoVinculo(AlteracaoVinculoLotacao alteracao) {
        selecionado.getAlteracoesVinculoLotacao().add(alteracao);
    }

    public void adicionarTodosServidorAlteracaoVinculo() {
        selecionado.getAlteracoesVinculoLotacao().addAll(alteracoesVinculoLotacao);
    }

    public void removerTodosServidorAlteracaoVinculo() {
        selecionado.getAlteracoesVinculoLotacao().removeAll(alteracoesVinculoLotacao);
    }

    public List<VinculosAlteracaoLocalTrabalho> getVinculosARemover() {
        return vinculosARemover;
    }

    public void setVinculosARemover(List<VinculosAlteracaoLocalTrabalho> vinculosARemover) {
        this.vinculosARemover = vinculosARemover;
    }

    public void removerServidorAlteracaoVinculo(AlteracaoVinculoLotacao alteracao) {
        selecionado.getAlteracoesVinculoLotacao().remove(alteracao);
    }

    public List<SelectItem> getRecursosFP() {
        List<SelectItem> toRetorno = new ArrayList<>();
        List<RecursoFP> recursoFPs = Lists.newLinkedList();
        if (novaHierarquiaOrganizacional != null && novaHierarquiaOrganizacional.getId() != null) {
            recursoFPs = recursoFPFacade.retornaRecursoFPDo2NivelDeHierarquia(novaHierarquiaOrganizacional, selecionado.getDataReferencia());
        } else {
            recursoFPs.add(new RecursoFP(1L, "0", "A Hierarquia Organizacional Administrativa não foi selecionada!"));
        }
        for (RecursoFP fp : recursoFPs) {
            toRetorno.add(new SelectItem(fp, fp.toString()));
        }
        return toRetorno;
    }

    public void gerarPdf() {
        Util.geraPDF("Servidores_rejeitados_na_transferencia_por_lote", gerarConteudo(), FacesContext.getCurrentInstance());
    }

    public String gerarConteudo() {
        if (!vinculosARemover.isEmpty()) {
            String content = "";
            content += "<?xml version='1.0' encoding='iso-8859-1'?>\n";
            content += "<!DOCTYPE HTML PUBLIC \"HTML 4.01 Transitional//PT\" \"http://www.w3.org/TR/html4/loose.dtd\">\n";
            content += "<html>\n";
            content += " <style type=\"text/css\">@page{size: A4 landscape;}</style>";

            content += "<style type=\"text/css\">\n";
            content += "    table, th, td {\n";
            content += "        border: 1px solid black;\n";
            content += "        border-collapse: collapse;\n";
            content += "    }body{font-family: Arial, \"Helvetica Neue\", Helvetica, sans-serif;}\n";
            content += "</style>\n";
            content += "<div style='border:0;text-align: left'>\n";
            content += " <table style=\"border: none!important;\">  <tr>";
            content += " <td style=\"border: none!important; text-align: center!important;\"><img src=\"" + getCaminhoBrasao() + "\" alt=\"Smiley face\" height=\"90\" width=\"73\" /></td>   ";
            content += " <td style=\"border: none!important;\">   \n";
            content += " <td style=\"border: none!important;\">   <b> PREFEITURA MUNICIPAL DE RIO BRANCO <br/>\n";
            content += "        SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO<br/>\n";
            content += "        DEPARTAMENTO DE RECURSOS HUMANOS";
            content += "        ROTINA DE TRANSFERÊNCIA DE SERVIDORES POR LOTE</b></td>\n";
            content += " </tr> </table>  \n";
            content += "</div>\n";
            content += "<br/>";
            content += "<div style='border: 0; text-align: center'>\n";
            content += "    <b>Servidores que já se encontram no recurso fp e lotação funcional informados ou com data de inicio igual ou menor que a da lotação atual</b>\n";
            content += "</div>\n";
            content += "<br/>";
            content += "<div style='border:0'>\n";
            content += "    <table style=\"width: 100%\" >\n ";
            content += "            <tr align=\"center\">\n ";
            content += "                <td><b>" + "Vínculo" + "</b></td>";
            content += "                <td><b>" + "Motivo da Rejeição" + "</b></td>";
            content += "            </tr>\n";

            for (VinculosAlteracaoLocalTrabalho vinculo : vinculosARemover) {
                content += "            <tr align=\"center\">\n ";
                content += "                <td>" + vinculo.getDescricaoVinculo();
                content += "                </td>\n";
                content += "                     <td>" + vinculo.getMotivoRejeicao() + "</td>\n";
                content += "            </tr>\n";
            }
            content += "    </table>\n";

            content += "<br/>";
            content += "<table border=\"1px solid black\" style=\"width:100%;\">"
                + "<tr>"
                + "<td style=\"text-align:left\"><b>Usuário: " + Util.getSistemaControlador().getUsuarioCorrente().getNome() + "</b></td>";
            content += "<td style=\"text-align:center\"><b>Quantidade de Registro(s): " + vinculosARemover.size() + "</b></td>";
            content += "<td style=\"text-align:right\"><b>Data: " + Util.dateHourToString(new Date()) + "</b></td>";
            content += "</tr>";
            content += "</table>";
            content += "<hr>";
            content += "</div>\n";
            content += "</html>";
            content += "";
            return content;
        } else {
            return "Nenhum servidor rejeitado.";
        }
    }

    public String getCaminhoBrasao() {
        String imagem = FacesUtil.geraUrlImagemDir();
        imagem += "/img/Brasao_de_Rio_Branco.gif";
        return imagem;
    }
}
