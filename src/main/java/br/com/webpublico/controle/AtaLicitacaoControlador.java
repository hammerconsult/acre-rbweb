package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.StatusLicitacaoException;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.AtaLicitacaoFacade;
import br.com.webpublico.negocios.ItemPropostaFornecedorFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Julio Cesar
 * Date: 17/03/14
 * Time: 10:19
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "ataLicitacaoControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaAta", pattern = "/ata-licitacao/novo/",
        viewId = "/faces/administrativo/licitacao/atalicitacao/edita.xhtml"),
    @URLMapping(id = "editarAta", pattern = "/ata-licitacao/editar/#{ataLicitacaoControlador.id}/",
        viewId = "/faces/administrativo/licitacao/atalicitacao/edita.xhtml"),
    @URLMapping(id = "verAta", pattern = "/ata-licitacao/ver/#{ataLicitacaoControlador.id}/",
        viewId = "/faces/administrativo/licitacao/atalicitacao/visualizar.xhtml"),
    @URLMapping(id = "listarAta", pattern = "/ata-licitacao/listar/",
        viewId = "/faces/administrativo/licitacao/atalicitacao/lista.xhtml")
})
public class AtaLicitacaoControlador extends PrettyControlador<AtaLicitacao> implements Serializable, CRUD {

    @EJB
    private AtaLicitacaoFacade ataLicitacaoFacade;
    @EJB
    private ItemPropostaFornecedorFacade itemPropostaFornecedorFacade;
    private List<LicitacaoFornecedor> listaFornecedor;
    private RatificacaoAta ratificacaoAtaSelecionada;

    public AtaLicitacaoControlador() {
        super(AtaLicitacao.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return ataLicitacaoFacade;
    }

    @URLAction(mappingId = "novaAta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "verAta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        operacao = Operacoes.VER;
        recuperarObjeto();
    }

    @URLAction(mappingId = "editarAta", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        recuperarAtaOrigem();
        try {
            ataLicitacaoFacade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId() + "/");
        }
    }

    private void recuperarAtaOrigem() {
        if (selecionado.isRetificacao()) {
            setRatificacaoAtaSelecionada(ataLicitacaoFacade.recuperarRatificaoDaAtaRatificao(selecionado));
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/ata-licitacao/";
    }


    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public void excluir() {
        if (selecionado.isRetificacao()) {
            try {
                ataLicitacaoFacade.excluirAtaRatificacao(selecionado);
                redireciona();
                FacesUtil.addOperacaoRealizada("Registro removido com sucesso.");
            } catch (Exception ex) {
                FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
            }
        } else {
            super.excluir();
        }
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            ataLicitacaoFacade.getLicitacaoFacade().verificarStatusLicitacao(selecionado.getLicitacao());
            selecionado.setDescricao(alterarURLImagens(selecionado.getDescricao()));
            selecionado = ataLicitacaoFacade.salvar(selecionado, ratificacaoAtaSelecionada);
            FacesUtil.addOperacaoRealizada("Registro salvo com sucesso.");
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (StatusLicitacaoException se) {
            FacesUtil.printAllFacesMessages(se.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public RatificacaoAta getRatificacaoAtaSelecionada() {
        return ratificacaoAtaSelecionada;
    }

    public void setRatificacaoAtaSelecionada(RatificacaoAta ratificacaoAtaSelecionada) {
        this.ratificacaoAtaSelecionada = ratificacaoAtaSelecionada;
    }

    public List<Licitacao> completaLicitacao(String parte) {
        return ataLicitacaoFacade.recuperaLicitacao(parte.trim());
    }

    public List<ModeloDocto> completaModelo(String parte) {
        return ataLicitacaoFacade.recuperaModelo(parte.trim());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            Util.validarCampos(selecionado);
            carregarListarDaLicitacao();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("TITULO", selecionado.getTitulo());
            dto.adicionarParametro("DESCRICAO", selecionado.getDescricao());
            dto.adicionarParametro("DESCRICAO", selecionado.getDescricao());
            dto.adicionarParametro("caminhoDaImagem", FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif");
            dto.setNomeRelatorio("ATA DE LICITAÇÃO DE " + selecionado.getTitulo());
            dto.setApi("administrativo/ata-licitacao/");
            ReportService.getInstance().gerarRelatorio(ataLicitacaoFacade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String alterarURLImagens(String conteudo) {
        try {
            conteudo = conteudo.replace("../", "");
            conteudo = conteudo.replace("<img src=\"", "<img src=\"" + FacesUtil.geraUrlImagemDir() + "img/Brasao_de_Rio_Branco.gif\"");
        } catch (Exception e) {
            conteudo = conteudo.replace("../../../", "../");
        }
        return conteudo;
    }

    public String mesclaTagsModelo(ModeloDocto modeloDoDocumento) {
        CharSequence string1 = "$NUMERO[if !mso]";
        CharSequence string2 = "$NUMERO";

        if (modeloDoDocumento != null) {
            modeloDoDocumento.setModelo(modeloDoDocumento.getModelo().replace(string1, string2));
        } else {
            modeloDoDocumento = new ModeloDocto();
            modeloDoDocumento.setModelo(selecionado.getDescricao());
        }

        Properties p = new Properties();
        p.setProperty("resource.loader", "string");
        p.setProperty("string.resource.loader.class",
            "org.apache.velocity.runtime.resource.loader.StringResourceLoader");
        VelocityEngine ve = new VelocityEngine();
        ve.init(p);

        StringResourceRepository repo = StringResourceLoader.getRepository();
        repo.putStringResource("myTemplate", modeloDoDocumento.getModelo());
        repo.setEncoding("UTF-8");

        VelocityContext context = new VelocityContext();
        adicionaTagsNoContexto(context, modeloDoDocumento);
        Template template = ve.getTemplate("myTemplate", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private void adicionaTagsNoContexto(VelocityContext context, ModeloDocto modelo) {

        if (TipoModeloDocto.TipoModeloDocumento.TAGS_ATA_LICITACAO.equals(modelo.getTipoModeloDocto())) {
            tagsAtaDaLicitacao(context);
        }
        if (TipoModeloDocto.TipoModeloDocumento.TAGSLICITACAO.equals(modelo.getTipoModeloDocto())) {
            tagsParaLicitacao(context);
        }
    }

    private void addTag(VelocityContext context, String chave, Object valor) {
        if (valor != null) {
            context.put(chave, valor);
        } else {
            context.put(chave, "");
        }
    }

    private void tagsAtaDaLicitacao(VelocityContext context) {
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.MODALIDADE.name(), selecionado.getLicitacao().getModalidadeLicitacao().getDescricao());
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.OBJETO.name(), selecionado.getLicitacao().getResumoDoObjeto());
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.NUMERO.name(), selecionado.getLicitacao().getNumeroLicitacao());
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.DATA.name(), new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.EXERCICIO.name(), selecionado.getLicitacao().getExercicio());
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.TIPODEAVALIACAO.name(), selecionado.getLicitacao().getTipoAvaliacao().getDescricao());
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.TIPOAPURACAO.name(), selecionado.getLicitacao().getTipoApuracao().getDescricao());
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.HORADEABERTURAESCRITAPOREXTENSO.name(), recuperarHoraMinutoEscritaPorExtenso(selecionado.getLicitacao().getAbertaEm()));
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.DATA.name(), DataUtil.getDataFormatada(selecionado.getLicitacao().getAbertaEm()));
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.DATADEABERTURAPOREXTENSO.name(), DataUtil.recuperarDataEscritaPorExtenso(selecionado.getLicitacao().getAbertaEm()));
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.PREGOEIRO.name(), recuperarMembroComissaoProgoeiro());
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.EQUIPEDEAPOIO.name(), getListaMembroComissao());
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.UNIDADEQUESOLICITOUALICITACAO.name(), selecionado.getLicitacao().getProcessoDeCompra().getUnidadeOrganizacional());
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.LOCALDAPUBLICACAODOAVISODELICITACAO.name(), getLocalDaPublicacaoDoAvisoDeLicitacao());
        if (selecionado.getLicitacao().getPublicacoes() != null && !selecionado.getLicitacao().getPublicacoes().isEmpty()) {
            addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.DIADAPUBLICACAODOAVISODELICITACAO.name(), new SimpleDateFormat("dd/MM/yyyy").format(getDataUltimaPublicacao()));
        }
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.FORNECEDORESPARTICIPANTESCOMREPRESENTANTELEGAL.name(), getFornecedoresComRepresentante(selecionado.getLicitacao()));
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.RESULTADODOPREGAO.name(), getListaDeItensDoPregaoComSeusVencedores());
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.EMPRESASINABILITADAS.name(), getEmpreasPorLicitacaoETipoClassificacao(selecionado.getLicitacao(), TipoClassificacaoFornecedor.INABILITADO));
        addTag(context, TipoModeloDocto.TagsAtasDaLicitacao.EMPRESASHABILITADAS.name(), getEmpreasPorLicitacaoETipoClassificacao(selecionado.getLicitacao(), TipoClassificacaoFornecedor.HABILITADO));

        tagsDoProcessoDeCompra(context);
        tagsDoFornecedor(context);
        tagsDoRepresentante(context);
        tagsMembrosComissao(context);
        if (selecionado.getLicitacao().getProcessoDeCompra() != null) {
            context.put("UNIDADERESPONSAVEL_PROCESSO_COMPRA", selecionado.getLicitacao().getProcessoDeCompra().getUnidadeOrganizacional().getDescricao());
        }
    }

    private String getEmpreasPorLicitacaoETipoClassificacao(Licitacao licitacao, TipoClassificacaoFornecedor classificacaoFornecedor) {
        StringBuilder sb = new StringBuilder("");

        if (ataLicitacaoFacade.getLicitacaoFacade().recuperarEmpresasPorLicitacaoETipoClassificacao(licitacao, classificacaoFornecedor) != null && !ataLicitacaoFacade.getLicitacaoFacade().recuperarEmpresasPorLicitacaoETipoClassificacao(licitacao, classificacaoFornecedor).isEmpty()) {
            for (LicitacaoFornecedor lf : ataLicitacaoFacade.getLicitacaoFacade().recuperarEmpresasPorLicitacaoETipoClassificacao(licitacao, classificacaoFornecedor)) {
                sb.append(lf).append(", ");
            }
            return sb.substring(0, sb.length() - 2);
        }

        return sb.toString();
    }

    private String getListaDeItensDoPregaoComSeusVencedores() {
        StringBuilder sb = new StringBuilder("");

        if (ataLicitacaoFacade.getLicitacaoFacade().getPregaoFacade().recuperarListaDeItensPregao(ataLicitacaoFacade.getLicitacaoFacade().getPregaoFacade().recuperarPregaoAPartirDaLicitacao(selecionado.getLicitacao())) != null
            && !ataLicitacaoFacade.getLicitacaoFacade().getPregaoFacade().recuperarListaDeItensPregao(ataLicitacaoFacade.getLicitacaoFacade().getPregaoFacade().recuperarPregaoAPartirDaLicitacao(selecionado.getLicitacao())).isEmpty()) {

            for (ItemPregao item : ataLicitacaoFacade.getLicitacaoFacade().getPregaoFacade().recuperarListaDeItensPregao(ataLicitacaoFacade.getLicitacaoFacade().getPregaoFacade().recuperarPregaoAPartirDaLicitacao(selecionado.getLicitacao()))) {
                item = ataLicitacaoFacade.getLicitacaoFacade().getItemPregaoFacade().recuperar(item.getId());
                sb.append(" Item: ").append(item).append(" Vencedor: ").append(item.recuperarVencedor()).append(", ");
            }

            return sb.substring(0, sb.length() - 2);
        }
        return sb.toString();
    }

    private boolean licitacaoEstaEmAlgumPregao(Licitacao licitacao) {
        return ataLicitacaoFacade.getLicitacaoFacade().getPregaoFacade().verificarRelacaoDaLicitacaoEmUmPregao(licitacao);
    }

    private String getFornecedoresComRepresentante(Licitacao licitacao) {
        StringBuilder sb = new StringBuilder("");

        if (ataLicitacaoFacade.getLicitacaoFacade().recuperarListaDeLicitacaoFornecedorComRepresentante(licitacao) != null && !ataLicitacaoFacade.getLicitacaoFacade().recuperarListaDeLicitacaoFornecedorComRepresentante(licitacao).isEmpty()) {
            for (LicitacaoFornecedor lf : ataLicitacaoFacade.getLicitacaoFacade().recuperarListaDeLicitacaoFornecedorComRepresentante(licitacao)) {
                sb.append(lf).append(", ");
            }

            return sb.substring(0, sb.length() - 2);
        }
        return sb.toString();
    }

    private Date getDataUltimaPublicacao() {
        return ataLicitacaoFacade.getLicitacaoFacade().recuperarUltimaPublicacaoDaLicitacao(selecionado.getLicitacao()).getDataPublicacao();
    }

    private String getListaMembroComissao() {
        StringBuilder sb = new StringBuilder("");

        if (selecionado.getLicitacao().getComissao().getMembroComissao() != null && !selecionado.getLicitacao().getComissao().getMembroComissao().isEmpty()) {
            for (MembroComissao mc : selecionado.getLicitacao().getComissao().getMembroComissao()) {
                sb.append(mc).append(", ");
            }

            return sb.substring(0, sb.length() - 2);
        }
        return sb.toString();
    }

    private String getLocalDaPublicacaoDoAvisoDeLicitacao() {
        StringBuilder sb = new StringBuilder("");

        if (ataLicitacaoFacade.getLicitacaoFacade().recuperarLocalPublicacoesDoAvisoDaLicitacao(selecionado.getLicitacao()) != null && !ataLicitacaoFacade.getLicitacaoFacade().recuperarLocalPublicacoesDoAvisoDaLicitacao(selecionado.getLicitacao()).isEmpty()) {
            for (PublicacaoLicitacao pl : ataLicitacaoFacade.getLicitacaoFacade().recuperarLocalPublicacoesDoAvisoDaLicitacao(selecionado.getLicitacao())) {
                sb.append(pl).append(", ");
            }

            return sb.substring(0, sb.length() - 2);
        }
        return sb.toString();
    }

    private void tagsParaLicitacao(VelocityContext context) {
        tagsLicitacaoGeral(context);
        tagsMembrosComissao(context);
        tagsParecer(context);
        tagsRecurso(context);
        tagsPublicacao(context);
        tagsDocumentoHabilitacao(context);
        tagsDaComissao(context);
        tagsDoFornecedor(context);
        tagsDoProcessoDeCompra(context);
        tagsFornecedorRepresentante(context);
        tagsFornecedorVencedor(context);
    }

    private void tagsLicitacaoGeral(VelocityContext context) {

        if (selecionado.getLicitacao().getResumoDoObjeto() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.OBJETO.name(), selecionado.getLicitacao().getResumoDoObjeto());
        }
        if (selecionado.getNumero() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.NUMERO.name(), selecionado.getLicitacao().getNumero());
        }
        if (selecionado.getLicitacao().getNumeroLicitacao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.NUMEROLICITACAO.name(), selecionado.getLicitacao().getNumeroLicitacao());
        }
        if (selecionado.getLicitacao().getTipoAvaliacao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.TIPODEAVALIACAO.name(), selecionado.getLicitacao().getTipoAvaliacao().getDescricao());
        }
        if (selecionado.getLicitacao().getTipoApuracao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.TIPOAPURACAO.name(), selecionado.getLicitacao().getTipoApuracao().getDescricao());
        }
        if (selecionado.getLicitacao().getModalidadeLicitacao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.MODALIDADE.name(), selecionado.getLicitacao().getModalidadeLicitacao().getDescricao());
        }
        if (selecionado.getLicitacao().getExercicio() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.EXERCICIO.name(), selecionado.getLicitacao().getExercicio().getAno());
        }
        if (selecionado.getLicitacao().getExercicioModalidade() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.EXERCICIOMODALIDADE.name(), selecionado.getLicitacao().getExercicioModalidade().getAno());
        }
        if (selecionado.getLicitacao().getNaturezaDoProcedimento() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.PROCEDIMENTO.name(), selecionado.getLicitacao().getNaturezaDoProcedimento().getDescricao());
        }
        if (selecionado.getLicitacao().getStatusAtualLicitacao() != null && selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.STATUSLICITACAO.name(), selecionado.getLicitacao().getStatusAtualLicitacao().getTipoSituacaoLicitacao().getDescricao());
        }
        if (selecionado.getLicitacao().getListaDeDocumentos() != null && !selecionado.getLicitacao().getListaDeDocumentos().isEmpty()) {
            context.put(TipoModeloDocto.TagsLicitacao.DOCUMENTOS.name(), selecionado.getLicitacao().getListaDeDocumentos().get(0).getModeloDocto().getNome());
        }
        if (selecionado.getLicitacao().getAbertaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DATADEABERTURA.name(), new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getLicitacao().getAbertaEm()));
        }
        if (selecionado.getLicitacao().getEmitidaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DATADEEMISSAO.name(), new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getLicitacao().getEmitidaEm()));
        }
        if (selecionado.getLicitacao().getValorMaximo() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.VALOR_MAXIMO.name(), selecionado.getLicitacao().getValorMaximo());
        }
        if (selecionado.getLicitacao().getComissao() != null && selecionado.getLicitacao().getComissao().getAtoDeComissao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.ATO_LEGAL.name(), selecionado.getLicitacao().getComissao().getAtoDeComissao().getAtoLegal().getNome());
        }
        if (selecionado.getLicitacao().getHomologadaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DATAHOMOLOGACAO.name(), new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getLicitacao().getHomologadaEm()));
        }
        if (selecionado.getLicitacao().getLocalDeEntrega() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.LOCALENTREGA.name(), selecionado.getLicitacao().getLocalDeEntrega());
        }
        if (selecionado.getLicitacao().getRegimeDeExecucao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.REGIMEEXECUCAO.name(), selecionado.getLicitacao().getRegimeDeExecucao());
        }
        if (selecionado.getLicitacao().getFormaDePagamento() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.FORMAPAGAMENTO.name(), selecionado.getLicitacao().getFormaDePagamento());
        }
        if (selecionado.getLicitacao().getPeriodoDeExecucao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.PERIODOEXECUCAO.name(), selecionado.getLicitacao().getPeriodoDeExecucao());
        }
        if (selecionado.getLicitacao().getPrazoDeVigencia() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.PRAZOVIGENCIA.name(), selecionado.getLicitacao().getPrazoDeVigencia());
        }
        if (selecionado.getLicitacao().getComissao() != null && selecionado.getLicitacao().getComissao().getAtoDeComissao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.NOMECOMISSAOLICITACAO.name(), selecionado.getLicitacao().getComissao().getAtoDeComissao().getAtoLegal().getNome());
        }
        if (selecionado.getLicitacao().getAbertaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.HORAABERTURALICITACAO.name(), recuperarHoraAberturaLicitacao(selecionado.getLicitacao().getAbertaEm()));
        }
        if (selecionado.getLicitacao().getProcessoDeCompra() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.SECRETARIAQUESOLICITOULICITACAO.name(), selecionado.getLicitacao().getProcessoDeCompra().getUnidadeOrganizacional());
        }
        if (selecionado.getLicitacao().getProcessoDeCompra() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DOTACOESORCAMENTARIASLICITACAO.name(), recuperarDotacoesOrcamentariasLicitacao());
        }
        if (selecionado.getLicitacao().getComissao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.PREGOEIRO.name(), recuperarMembroComissaoProgoeiro());
        }
        if (selecionado.getLicitacao().getAbertaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.HORARIOABERTURAESCRITAPOREXTENSO.name(), recuperarHoraMinutoEscritaPorExtenso(selecionado.getLicitacao().getAbertaEm()));
        }
        if (selecionado.getLicitacao().getAbertaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DATAABERTURAPOREXTENSO.name(), DataUtil.recuperarDataEscritaPorExtenso(selecionado.getLicitacao().getAbertaEm()));
        }
    }

    private String recuperarHoraAberturaLicitacao(Date abertaEm) {
        return new SimpleDateFormat("HH:mm").format(abertaEm);
    }

    private String recuperarDotacoesOrcamentariasLicitacao() {
        return StringUtils.join(ataLicitacaoFacade.getLicitacaoFacade().getDotacaoSolicitacaoMaterialFacade().recuperarDotacoesAPartirDaLicitacao(selecionado.getLicitacao()), ",");
    }

    private String recuperarMembroComissaoProgoeiro() {
        StringBuilder sb = new StringBuilder("");

        if (ataLicitacaoFacade.getLicitacaoFacade().getComissaoFacade().recuperarMembroComissaoAPartirDaLicitacao(selecionado.getLicitacao()) != null && !ataLicitacaoFacade.getLicitacaoFacade().getComissaoFacade().recuperarMembroComissaoAPartirDaLicitacao(selecionado.getLicitacao()).isEmpty()) {
            for (MembroComissao membroComissao : ataLicitacaoFacade.getLicitacaoFacade().getComissaoFacade().recuperarMembroComissaoAPartirDaLicitacao(selecionado.getLicitacao())) {
                sb.append(membroComissao).append(", ");
            }

            return sb.substring(0, sb.length() - 2);
        }
        return sb.toString();
    }

    private String recuperarHoraMinutoEscritaPorExtenso(Date abertaEm) {
        int h = abertaEm.getHours();
        int m = abertaEm.getMinutes();
        int s = abertaEm.getSeconds();

        String result = tempoPorExtenso(h, 1);

        if ((m != 0) && (s != 0)) // tem minutos e segundos
            result = result + ", " + tempoPorExtenso(m, 2) + " e " + tempoPorExtenso(s, 3);
        else if (m != 0) // so tem minutos (segundos = zero)
            result = result + " e " + tempoPorExtenso(m, 2);
        else if (s != 0) // so tem segundos (minutos = zero)
            result = result + " e " + tempoPorExtenso(s, 3);

        return result;
    }

    // o parâmetro "tipo" foi utilizado para indicar que o tempo recebido
    // (parâmetro "n") corresponde: 1- horas, 2- minutos e 3- segundos.
    private String tempoPorExtenso(int n, int tipo) {
        String parte[] = {"zero", "um", "dois", "três", "quatro", "cinco", "seis", "sete", "oito", "nove", "dez", "onze", "doze", "treze", "quatorze", "quinze", "dezesseis", "dezessete", "dezoito", "dezenove"};

        String dezena[] = {"", "", "vinte", "trinta", "quarenta", "cinquenta"};

        String s;

        if (n <= 19) {
            if ((tipo == 1) && (n == 1)) // uma hora da madrugada
                s = "uma";
            else if ((tipo == 1) && (n == 2)) // duas horas da madrugada
                s = "duas";
            else s = parte[n];
        } else {
            int dez = n / 10;
            int unid = n % 10;
            s = dezena[dez];

            if (unid != 0) {
                if ((tipo == 1) && (unid == 1)) // vinte e uma horas
                    s = s + " e uma";
                else if ((tipo == 1) && (unid == 2)) // vinte e duas horas
                    s = s + " e duas";
                else s = s + " e " + parte[unid];
            }
        }

        if (tipo == 1)
            s = s + " hora";
        else if (tipo == 2)
            s = s + " minuto";
        else s = s + " segundo";

        if (n > 1) // plural
            s = s + "s";

        return (s); // respondendo a chamada com o extenso do valor

    }

    private void tagsDoProcessoDeCompra(VelocityContext context) {
        //Processo de Compra
        if (selecionado.getLicitacao().getProcessoDeCompra() != null) {
            if (selecionado.getLicitacao().getProcessoDeCompra().getDescricao() != null) {
                context.put("PROCESSODECOMPRA", selecionado.getLicitacao().getProcessoDeCompra().getDescricao());
            }
            if (selecionado.getLicitacao().getProcessoDeCompra() != null) {
                context.put("NUMERO_PROCESSO_COMPRA", selecionado.getLicitacao().getProcessoDeCompra().getNumero());
            }
            if (selecionado.getLicitacao().getProcessoDeCompra() != null) {
                context.put("EXERCICIO_PROCESSO_COMPRA", selecionado.getLicitacao().getProcessoDeCompra().getExercicio());
            }
        }
    }

    private void tagsFornecedorRepresentante(VelocityContext context) {
        if (selecionado.getLicitacao().getFornecedores() != null) {
            String fornecedor = new String();
            fornecedor += "<table border=" + "1" + " width=" + "100%" + ">"
                + "<tr><th align=\"center\" colspan=\"7\" bgcolor=\"#CDC9C9\"> FORNECEDORES/REPRESENTANTES </th></tr>"
                + "<tr>"
                + "<th align=\"center\"> Código </th>"
                + "<th align=\"center\"> Fornecedor </th>"
                + "<th align=\"center\"> CPF/CNPJ </th>"
                + "<th align=\"center\"> Representante </th>"
                + "<th align=\"center\"> CPF/CNPJ </th>"
                + "<th align=\"center\"> Classificação </th>"
                + "<th align=\"center\"> Tipo de Empresa </th>"
                + "</tr>";
            for (LicitacaoFornecedor fornec : selecionado.getLicitacao().getFornecedores()) {
                fornecedor += "<tr><td align=\"center\">" + fornec.getCodigo() + "</td>"
                    + "<td align=\"center\">" + fornec.getEmpresa().getNome() + "</td>"
                    + "<td align=\"center\">" + fornec.getEmpresa().getCpf_Cnpj() + "</td>";
                if (fornec.getRepresentante() != null) {
                    fornecedor += "<td align=\"center\">" + fornec.getRepresentante().getNome() + "</td>";
                    if (fornec.getRepresentante().getCpf() != null) {
                        fornecedor += "<td align=\"center\">" + fornec.getRepresentante().getCpf() + "</td>";
                    } else {
                        fornecedor += "<td align=\"center\"> - </td>";
                    }
                } else {
                    fornecedor += "<td align=\"center\"> - </td>"
                        + "<td align=\"center\"> - </td>";
                }
                if (fornec.getTipoClassificacaoFornecedor() != null) {
                    fornecedor += "<td align=\"center\">" + fornec.getTipoClassificacaoFornecedor().getDescricao() + "</td>";
                } else {
                    fornecedor += "<td align=\"center\"> - </td>";
                }

                if (fornecedorPessoaJuridica(fornec) && ((PessoaJuridica) fornec.getEmpresa()).getTipoEmpresa() != null) {
                    fornecedor += "<td align=\"center\">" + ((PessoaJuridica) fornec.getEmpresa()).getTipoEmpresa().getDescricao() + "</td>";
                } else {
                    fornecedor += "<td align=\"center\"> - </td>";
                }
            }
            fornecedor += "</tr>"
                + "</table>";
            context.put("FORNECEDOR_REPRESENTANTE", fornecedor);
        }
    }

    private void tagsFornecedorVencedor(VelocityContext context) {
        List<ItemPropostaFornecedor> itens = null;
        if (selecionado.getLicitacao().getModalidadeLicitacao().isPregao()) {
            itens = itemPropostaFornecedorFacade.buscarItemPropostaFornecedorVencedorDoPregao(selecionado.getLicitacao());
        } else {
            itens = itemPropostaFornecedorFacade.buscarItemPropostaFornecedorVencedorDoCertame(selecionado.getLicitacao());
        }
        String tabelaDeItens = new String();
        if (itens != null) {

            tabelaDeItens += "<table border=" + "1" + " width=" + "100%" + ">"
                + "<tr><th align=\"center\" colspan=\"8\" bgcolor=\"#CDC9C9\"> FORNECEDOR(ES) VENCEDOR(ES) </th></tr>"
                + "<tr>"
                + "<th align=\"center\"> Lote</th>"
                + "<th align=\"center\"> Item </th>"
                + "<th align=\"center\"> Descrição </th>"
                + "<th align=\"center\"> Unid</th>"
                + "<th align=\"center\"> Fornecedor </th>"
                + "<th align=\"center\"> Marca </th>"
                + "<th align=\"center\"> Valor R$ </th>"
                + "<th align=\"center\"> Valor Total </th>"
                + "</tr>";

            for (ItemPropostaFornecedor itemPropostaFornecedor : itens) {
                tabelaDeItens += "<tr><td align=\"center\">" + itemPropostaFornecedor.getItemProcessoDeCompra().getLoteProcessoDeCompra().getNumero() + "</td>"
                    + "<td align=\"center\">" + itemPropostaFornecedor.getItemProcessoDeCompra().getNumero() + "</td>"
                    + "<td align=\"center\">" + itemPropostaFornecedor.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getDescricao() + "</td>"
                    + "<td align=\"center\">" + itemPropostaFornecedor.getItemProcessoDeCompra().getItemSolicitacaoMaterial().getUnidadeMedida().getDescricao() + "</td>"
                    + "<td align=\"center\">" + itemPropostaFornecedor.getPropostaFornecedor().getFornecedor().getNome() + "</td>"
                    + "<td align=\"center\">" + itemPropostaFornecedor.getMarca() + "</td>"
                    + "<td align=\"center\">" + "R$ " + itemPropostaFornecedor.getPreco() + ",00" + "</td>"
                    + "<td align=\"center\">" + "R$ " + itemPropostaFornecedor.getPreco().multiply(itemPropostaFornecedor.getItemProcessoDeCompra().getQuantidade()) + ",00" + "</td>";
            }
            tabelaDeItens += "</tr>"
                + "</table>";
        }
        context.put("FORNECEDOR_VENCEDOR", tabelaDeItens);
    }

    public boolean fornecedorPessoaJuridica(LicitacaoFornecedor licForn) {
        if (licForn.getEmpresa() instanceof PessoaJuridica) {
            return true;
        }

        return false;
    }

    private void tagsDoFornecedor(VelocityContext context) {
        String fornecedores = "";
        if (selecionado.getLicitacao().getFornecedores() != null) {
            for (LicitacaoFornecedor licitacaoFornecedor : selecionado.getLicitacao().getFornecedores()) {
                context.put("FORNECEDORES", licitacaoFornecedor.getEmpresa().getNome());
                fornecedores += licitacaoFornecedor.getEmpresa().getNome() + " - CNPJ/CPF : " + licitacaoFornecedor.getEmpresa().getCpf_Cnpj() + ", ";
            }
        }
        context.put("FORNECEDORES", fornecedores);
    }

    private void tagsDoRepresentante(VelocityContext context) {
        //Fornecedores
        String representante = "";
        if (listaFornecedor != null) {
            for (LicitacaoFornecedor licitacaoFornecedor : listaFornecedor) {
                if (licitacaoFornecedor.getRepresentante() != null) {
                    context.put("REPRESENTANTE", licitacaoFornecedor.getRepresentante().getNome());
                    context.put("DOCUMENTOREPRESENTANTE", licitacaoFornecedor.getRepresentante().getCpf());
                    representante += licitacaoFornecedor.getRepresentante().getNome()
                        + ", Documento: " + licitacaoFornecedor.getRepresentante().getCpf() ;
                }
            }
        }
        context.put("REPRESENTANTE", representante);
    }

    private void tagsMembrosComissao(VelocityContext context) {
        String membros = "";
        if (selecionado.getLicitacao().getComissao() != null && selecionado.getLicitacao().getComissao().getMembroComissao() != null) {
            for (MembroComissao membroComissao : selecionado.getLicitacao().getComissao().getMembroComissao()) {
                membros += membroComissao.getPessoaFisica().getNome() + ", ";
            }
        }
        context.put("MEMBROSCOMISSAO", membros);
    }

    private void tagsParecer(VelocityContext context) {
        String parecer = "";
        if (selecionado.getLicitacao().getPareceres() != null) {
            for (ParecerLicitacao parecerLicitacao : selecionado.getLicitacao().getPareceres()) {
                context.put("NUMEROPARECER", parecerLicitacao.getNumero());
                context.put("DATAPARECER", new SimpleDateFormat("dd/MM/yyyy").format(parecerLicitacao.getDataParecer()));
                context.put("TIPOPARECER", parecerLicitacao.getTipoParecerLicitacao().getDescricao());
                context.put("PESSOAPARECER", parecerLicitacao.getPessoa().getNome());
                context.put("OBSERVACAOPARECER", parecerLicitacao.getObservacoes());
                parecer += "Nº :" + parecerLicitacao.getNumero() + ", Data: " + parecerLicitacao.getDataParecer()
                    + ", Tipo: " + parecerLicitacao.getTipoParecerLicitacao().getDescricao()
                    + ", Pessoa: " + parecerLicitacao.getPessoa().getNome() + ", Observação: " + parecerLicitacao.getObservacoes();
            }
        }
        context.put("PARECER", parecer);
    }

    private void tagsRecurso(VelocityContext context) {
        String recurso = "";
        if (selecionado.getLicitacao().getListaDeRecursoLicitacao() != null) {
            for (RecursoLicitacao recursoLicitacao : selecionado.getLicitacao().getListaDeRecursoLicitacao()) {
                context.put("DATARECURSO", new SimpleDateFormat("dd/MM/yyyy").format(recursoLicitacao.getDataRecurso()));
                context.put("NUMERORECURSO", recursoLicitacao.getNumero());
                context.put("TIPORECURSO", recursoLicitacao.getTipoRecursoLicitacao().getDescricao());
                context.put("INTERPONENTERECURSO", recursoLicitacao.getInterponente().getNome());
                context.put("MOTIVORECURSO", recursoLicitacao.getMotivo());
                context.put("TIPOJULGAMENTORECURSO", recursoLicitacao.getTipoJulgamentoRecurso());
                context.put("DATAJULGAMENTORECURSO", new SimpleDateFormat("dd/MM/yyyy").format(recursoLicitacao.getDataJulgamento()));
                recurso += "Nº: " + recursoLicitacao.getNumero()
                    + ", Data: " + recursoLicitacao.getDataRecurso()
                    + ", Tipo: " + recursoLicitacao.getTipoRecursoLicitacao().getDescricao()
                    + ", Interponente: " + recursoLicitacao.getInterponente().getNome()
                    + ", Motivo: " + recursoLicitacao.getMotivo()
                    + ", Tipo Julgamento: " + recursoLicitacao.getTipoJulgamentoRecurso()
                    + ", Data Julgamento: " + recursoLicitacao.getDataJulgamento();
            }
        }
        context.put("RECURSO", recurso);
    }

    private void tagsPublicacao(VelocityContext context) {
        String publicacao = "";
        if (selecionado.getLicitacao().getPublicacoes() != null) {
            for (PublicacaoLicitacao publicacaoLicitacao : selecionado.getLicitacao().getPublicacoes()) {
                context.put("DATAPUBLICACAO", new SimpleDateFormat("dd/MM/yyyy").format(publicacaoLicitacao.getDataPublicacao()));
                context.put("VEICULOPUBLICACAO", publicacaoLicitacao.getVeiculoDePublicacao().getNome());
                publicacao += "Veiculo: " + publicacaoLicitacao.getVeiculoDePublicacao().getNome()
                    + ", Publicado em: " + publicacaoLicitacao.getDataPublicacao();
            }
        }
        context.put("PUBLICACAO", publicacao);
    }

    private void tagsDocumentoHabilitacao(VelocityContext context) {
        String Documento = "";
        if (selecionado.getLicitacao().getDocumentosProcesso() != null) {
            for (DoctoHabLicitacao doctoHabLicitacao : selecionado.getLicitacao().getDocumentosProcesso()) {
                context.put("DOCUMENTOSHABILITACAO", doctoHabLicitacao.getDoctoHabilitacao().getDescricao());
                Documento = doctoHabLicitacao.getDoctoHabilitacao().getDescricao();
            }
        }
        context.put("DOCUMENTOSHABILITACAO", Documento);
    }

    private void tagsDaComissao(VelocityContext context) {
        if (selecionado.getLicitacao().getComissao() != null) {
            context.put("COMISSAO", selecionado.getLicitacao().getComissao().getTitulo());
            for (LicitacaoMembroComissao licitacaoMembroComissao : selecionado.getLicitacao().getListaDeLicitacaoMembroComissao()) {
                if (licitacaoMembroComissao.getMembroComissao().getAtribuicaoComissao().equals(AtribuicaoComissao.PRESIDENTE)) {
                    context.put("PRESIDENTECOMISSAO", licitacaoMembroComissao.getMembroComissao().getPessoaFisica().getNome());
                }
            }
        }
    }

    public void recuperaLicitacao() {
        listaFornecedor = ataLicitacaoFacade.recarregarLicitacao(selecionado.getLicitacao()).getFornecedores();
        setarNumeroDaAta(ataLicitacaoFacade.getProximoNumeroDaAtaPorLicitacao(selecionado.getLicitacao()).intValue() + 1);

        selecionado.setModeloDocto(null);
        selecionado.setDescricao(null);
    }

    public void carregarLicitacaoDaAtaSelecionada() {
        selecionado.setLicitacao(ratificacaoAtaSelecionada.getAtaLicitacaoOrigem().getLicitacao());
        setarNumeroDaAta(ataLicitacaoFacade.getProximoNumeroDaAtaPorLicitacao(selecionado.getLicitacao()).intValue() + 1);
        if (selecionado.getModeloDocto() == null) {
            selecionado.setDescricao(ratificacaoAtaSelecionada.getAtaLicitacaoOrigem().getDescricao());
        }
    }

    private void setarNumeroDaAta(int numero) {
        selecionado.setNumero(numero);
    }

    public List<SelectItem> getTiposAtaLicitacao() {
        List<SelectItem> retorno = new ArrayList<SelectItem>();
        for (TipoAtaLicitacao tipoAtaLicitacao : TipoAtaLicitacao.values()) {
            retorno.add(new SelectItem(tipoAtaLicitacao, tipoAtaLicitacao.getDescricao()));
        }
        return retorno;
    }

    public List<AtaLicitacao> completaAtaLicitacao(String parte) {
        return ataLicitacaoFacade.listaDecrescente();
    }

    public void setarNull() {
        selecionado.setLicitacao(null);
        if (selecionado.isRetificacao()) {
            ratificacaoAtaSelecionada = new RatificacaoAta(selecionado);
        } else {
            setRatificacaoAtaSelecionada(null);
        }
    }

    public boolean isEditando() {
        return Operacoes.EDITAR.equals(operacao);
    }

    public void carregarDadosDescricao() {
        if (podeCarregarDescricao()) {
            carregarListarDaLicitacao();
            ModeloDocto modeloDocto = new ModeloDocto();
            modeloDocto.setModelo(mesclaTagsModelo(selecionado.getModeloDocto()));
            selecionado.setDescricao(modeloDocto.getModelo());
        }
    }

    private void carregarListarDaLicitacao() {
        selecionado.setLicitacao(ataLicitacaoFacade.getLicitacaoFacade().recuperar(selecionado.getLicitacao().getId()));
    }

    private boolean podeCarregarDescricao() {
        if (selecionado.getLicitacao() == null) {
            FacesUtil.addOperacaoNaoPermitida("Informe a licitação primeiro.");
            selecionado.setModeloDocto(null);
            return false;
        }
        return true;
    }

    public String valorMarginLeft() {
        if (selecionado.isRetificacao()) {
            return "58px";
        }
        return "50px";
    }
}
