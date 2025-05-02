package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.AtribuicaoComissao;
import br.com.webpublico.enums.TipoClassificacaoFornecedor;
import br.com.webpublico.util.DataUtil;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.resource.loader.StringResourceLoader;
import org.apache.velocity.runtime.resource.util.StringResourceRepository;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * Created by renat on 18/04/2016.
 */
@Stateless
public class DocumentoLicitacaoFacade extends AbstractFacade<DocumentoLicitacao> {


    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private LicitacaoFacade licitacaoFacade;
    @EJB
    private ItemPropostaFornecedorFacade itemPropostaFornecedorFacade;

    public DocumentoLicitacaoFacade() {
        super(DocumentoLicitacao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public String mesclaTagsModelo(ModeloDocto modeloDoDocumento, Licitacao selecionado, List<ItemProcessoDeCompra> listaDeItemProcessoDeCompraParaExibir) {
        CharSequence string1 = "$NUMERO[if !mso]";
        CharSequence string2 = "$NUMERO";

        modeloDoDocumento.setModelo(modeloDoDocumento.getModelo().replace(string1, string2));

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
        adicionaTagsNoContexto(context, modeloDoDocumento, selecionado, listaDeItemProcessoDeCompraParaExibir);
        Template template = ve.getTemplate("myTemplate", "UTF-8");
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    private void adicionaTagsNoContexto(VelocityContext context, ModeloDocto modelo, Licitacao licitacao, List<ItemProcessoDeCompra> listaDeItemProcessoDeCompraParaExibir) {
        if (modelo.getTipoModeloDocto().getDescricao().equals("Tags da Licitação")) {
            adicionarTagsParaLicitacao(context, licitacao, listaDeItemProcessoDeCompraParaExibir);
        }

        if (modelo.getTipoModeloDocto().getDescricao().equals("Tags de aviso de licitação")) {
            adicionarTagsAvisoDeLicitacao(context, licitacao, listaDeItemProcessoDeCompraParaExibir);
        }

        if (modelo.getTipoModeloDocto().getDescricao().equals("Tags da Ata da Licitação")) {
            adicionarTagsAtaDaLicitacao(context, licitacao, listaDeItemProcessoDeCompraParaExibir);
        }
    }

    private void adicionarTagsParaLicitacao(VelocityContext context, Licitacao licitacao, List<ItemProcessoDeCompra> listaDeItemProcessoDeCompraParaExibir) {
        tagsLicitacaoGeral(context, licitacao, listaDeItemProcessoDeCompraParaExibir);
        tagsMembrosComissao(context, licitacao);
        tagsParecer(context, licitacao);
        tagsRecurso(context, licitacao);
        tagsPublicacao(context, licitacao);
        tagsDocumentoHabilitacao(context, licitacao);
        tagsDaComissao(context, licitacao);
        tagsDoFornecedor(context, licitacao);
        tagsDoProcessoDeCompra(context, licitacao);
        tagItensProcessoDeCompra(context, licitacao, listaDeItemProcessoDeCompraParaExibir);
        tagsFornecedorRepresentante(context, licitacao);
        tagsFornecedorVencedor(context, licitacao);
    }

    private void adicionarTagsAvisoDeLicitacao(VelocityContext context, Licitacao selecionado, List<ItemProcessoDeCompra> listaDeItemProcessoDeCompraParaExibir) {
        context.put("MODALIDADE", selecionado.getModalidadeLicitacao().getDescricao());
        context.put("OBJETO", selecionado.getResumoDoObjeto());
        context.put("DATADEABERTURA", new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getAbertaEm()));
        context.put("DATADEEMISSAO", new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getEmitidaEm()));
        context.put("TIPODEAVALIACAO", selecionado.getTipoAvaliacao().getDescricao());
        context.put("NUMERO", selecionado.getNumeroLicitacao());
        context.put("EXERCICIO", selecionado.getExercicio());
        context.put("DATA", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        context.put("VALOR_MAXIMO", selecionado.getValorMaximo());
        context.put("VALOR_TOTAL", recuperaValorTotal(listaDeItemProcessoDeCompraParaExibir));
        context.put("ATO_LEGAL", selecionado.getComissao().getAtoDeComissao().getAtoLegal().getNome());
        tagsDaComissao(context, selecionado);
        tagsPublicacao(context, selecionado);
    }

    private BigDecimal recuperaValorTotal(List<ItemProcessoDeCompra> listaDeItemProcessoDeCompraParaExibir) {
        BigDecimal valor = BigDecimal.ZERO;
        for (ItemProcessoDeCompra item : listaDeItemProcessoDeCompraParaExibir) {
            valor = valor.add(item.getItemSolicitacaoMaterial().getPreco());
        }
        return valor;
    }

    private void adicionarTagsAtaDaLicitacao(VelocityContext context, Licitacao selecionado, List<ItemProcessoDeCompra> listaDeItemProcessoDeCompraParaExibir) {
        if (selecionado.getModalidadeLicitacao() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.MODALIDADE.name(), selecionado.getModalidadeLicitacao().getDescricao());
        }
        if (selecionado.getResumoDoObjeto() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.OBJETO.name(), selecionado.getResumoDoObjeto());
        }
        if (selecionado.getNumeroLicitacao() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.NUMERO.name(), selecionado.getNumeroLicitacao());
        }
        context.put(TipoModeloDocto.TagsAtasDaLicitacao.DATA.name(), new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        if (selecionado.getExercicio() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.EXERCICIO.name(), selecionado.getExercicio());
        }
        if (selecionado.getTipoAvaliacao() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.TIPODEAVALIACAO.name(), selecionado.getTipoAvaliacao().getDescricao());
        }
        if (selecionado.getTipoApuracao() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.TIPOAPURACAO.name(), selecionado.getTipoApuracao().getDescricao());
        }
        //Novas Tags.
        if (selecionado.getAbertaEm() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.HORADEABERTURAESCRITAPOREXTENSO.name(), recuperarHoraMinutoEscritaPorExtenso(selecionado.getAbertaEm()));
        }
        if (selecionado.getAbertaEm() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.DATADEABERTURAPOREXTENSO.name(), DataUtil.recuperarDataEscritaPorExtenso(selecionado.getAbertaEm()));
        }
        if (selecionado.getComissao() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.PREGOEIRO.name(), recuperarMembroComissaoProgoeiro(selecionado));
        }
        if (selecionado.getComissao() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.EQUIPEDEAPOIO.name(), getListaMembroComissao(selecionado));
        }
        if (selecionado.getProcessoDeCompra() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.UNIDADEQUESOLICITOUALICITACAO.name(), selecionado.getProcessoDeCompra().getUnidadeOrganizacional());
        }
        if (selecionado.getPublicacoes() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.LOCALDAPUBLICACAODOAVISODELICITACAO.name(), getLocalDaPublicacaoDoAvisoDeLicitacao(selecionado));
        }
        if (selecionado.getPublicacoes() != null && !selecionado.getPublicacoes().isEmpty()) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.DIADAPUBLICACAODOAVISODELICITACAO.name(), new SimpleDateFormat("dd/MM/yyyy").format(getDataUltimaPublicacao(selecionado)));
        }
        if (selecionado.getFornecedores() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.FORNECEDORESPARTICIPANTESCOMREPRESENTANTELEGAL.name(), getFornecedoresComRepresentante(selecionado));
        }
        if (licitacaoEstaEmAlgumPregao(selecionado)) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.RESULTADODOPREGAO.name(), getListaDeItensDoPregaoComSeusVencedores(selecionado));
        }
        if (selecionado.getFornecedores() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.EMPRESASINABILITADAS.name(), getEmpreasPorLicitacaoETipoClassificacao(selecionado, TipoClassificacaoFornecedor.INABILITADO));
        }
        if (selecionado.getFornecedores() != null) {
            context.put(TipoModeloDocto.TagsAtasDaLicitacao.EMPRESASHABILITADAS.name(), getEmpreasPorLicitacaoETipoClassificacao(selecionado, TipoClassificacaoFornecedor.HABILITADO));
        }
        tagsDoProcessoDeCompra(context, selecionado);
        tagsDoFornecedor(context, selecionado);
        tagsDoRepresentante(context, selecionado);
        tagsMembrosComissao(context, selecionado);
        if (selecionado.getProcessoDeCompra() != null) {
            context.put("UNIDADERESPONSAVEL_PROCESSO_COMPRA", selecionado.getProcessoDeCompra().getUnidadeOrganizacional().getDescricao());
        }
        tagItensProcessoDeCompra(context, selecionado, listaDeItemProcessoDeCompraParaExibir);
    }

    private void tagsDoFornecedor(VelocityContext context, Licitacao selecionado) {
        String fornecedores = "";
        if (selecionado.getFornecedores() != null) {
            for (LicitacaoFornecedor licitacaoFornecedor : selecionado.getFornecedores()) {
                context.put("FORNECEDORES", licitacaoFornecedor.getEmpresa().getNome());
                fornecedores += licitacaoFornecedor.getEmpresa().getNome() + " - CNPJ/CPF : " + licitacaoFornecedor.getEmpresa().getCpf_Cnpj() + ", ";
            }
        }
        context.put("FORNECEDORES", fornecedores);
    }

    private void tagsDoRepresentante(VelocityContext context, Licitacao selecionado) {
        String representante = "";
        if (selecionado.getFornecedores() != null) {
            for (LicitacaoFornecedor licitacaoFornecedor : selecionado.getFornecedores()) {
                if (licitacaoFornecedor.getRepresentante() != null) {
                    context.put("REPRESENTANTE", licitacaoFornecedor.getRepresentante().getNome());
                    context.put("DOCUMENTOREPRESENTANTE", licitacaoFornecedor.getRepresentante().getCpf());
                    representante += licitacaoFornecedor.getRepresentante().getNome()
                        + ", Documento: " + licitacaoFornecedor.getRepresentante().getCpf();
                }
            }
        }
        context.put("REPRESENTANTE", representante);
    }

    private void tagsDoProcessoDeCompra(VelocityContext context, Licitacao selecionado) {
        if (selecionado.getProcessoDeCompra() != null) {
            if (selecionado.getProcessoDeCompra().getDescricao() != null) {
                context.put("PROCESSODECOMPRA", selecionado.getProcessoDeCompra().getDescricao());
            }
            if (selecionado.getProcessoDeCompra() != null) {
                context.put("NUMERO_PROCESSO_COMPRA", selecionado.getProcessoDeCompra().getNumero());
            }
            if (selecionado.getProcessoDeCompra() != null) {
                context.put("EXERCICIO_PROCESSO_COMPRA", selecionado.getProcessoDeCompra().getExercicio());
            }
        }
    }

    private void tagsDocumentoHabilitacao(VelocityContext context, Licitacao selecionado) {
        String Documento = "";
        if (selecionado.getDocumentosProcesso() != null) {
            for (DoctoHabLicitacao doctoHabLicitacao : selecionado.getDocumentosProcesso()) {
                context.put("DOCUMENTOSHABILITACAO", doctoHabLicitacao.getDoctoHabilitacao().getDescricao());
                Documento = doctoHabLicitacao.getDoctoHabilitacao().getDescricao();
            }
        }
        context.put("DOCUMENTOSHABILITACAO", Documento);
    }

    private void tagsLicitacaoGeral(VelocityContext context, Licitacao selecionado, List<ItemProcessoDeCompra> listaDeItemProcessoDeCompraParaExibir) {

        if (selecionado.getResumoDoObjeto() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.OBJETO.name(), selecionado.getResumoDoObjeto());
        }
        if (selecionado.getNumero() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.NUMERO.name(), selecionado.getNumero());
        }
        if (selecionado.getNumeroLicitacao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.NUMEROLICITACAO.name(), selecionado.getNumeroLicitacao());
        }
        if (selecionado.getTipoAvaliacao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.TIPODEAVALIACAO.name(), selecionado.getTipoAvaliacao().getDescricao());
        }
        if (selecionado.getTipoApuracao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.TIPOAPURACAO.name(), selecionado.getTipoApuracao().getDescricao());
        }
        if (selecionado.getModalidadeLicitacao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.MODALIDADE.name(), selecionado.getModalidadeLicitacao().getDescricao());
        }
        if (selecionado.getExercicio() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.EXERCICIO.name(), selecionado.getExercicio().getAno());
        }
        if (selecionado.getExercicioModalidade() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.EXERCICIOMODALIDADE.name(), selecionado.getExercicioModalidade().getAno());
        }
        if (selecionado.getNaturezaDoProcedimento() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.PROCEDIMENTO.name(), selecionado.getNaturezaDoProcedimento().getDescricao());
        }
        if (selecionado.getStatusAtualLicitacao() != null && selecionado.getStatusAtualLicitacao().getTipoSituacaoLicitacao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.STATUSLICITACAO.name(), selecionado.getStatusAtualLicitacao().getTipoSituacaoLicitacao().getDescricao());
        }
        if (selecionado.getListaDeDocumentos() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DOCUMENTOS.name(), selecionado.getListaDeDocumentos().get(0).getModeloDocto().getNome());
        }
        if (selecionado.getAbertaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DATADEABERTURA.name(), new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getAbertaEm()));
        }
        if (selecionado.getEmitidaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DATADEEMISSAO.name(), new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getEmitidaEm()));
        }
        if (selecionado.getValorMaximo() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.VALOR_MAXIMO.name(), selecionado.getValorMaximo());
        }
        if (listaDeItemProcessoDeCompraParaExibir != null) {
            context.put(TipoModeloDocto.TagsLicitacao.VALOR_TOTAL.name(), recuperaValorTotal(listaDeItemProcessoDeCompraParaExibir));
        }
        if (selecionado.getComissao() != null && selecionado.getComissao().getAtoDeComissao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.ATO_LEGAL.name(), selecionado.getComissao().getAtoDeComissao().getAtoLegal().getNome());
        }
        if (selecionado.getHomologadaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DATAHOMOLOGACAO.name(), new SimpleDateFormat("dd/MM/yyyy").format(selecionado.getHomologadaEm()));
        }
        if (selecionado.getLocalDeEntrega() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.LOCALENTREGA.name(), selecionado.getLocalDeEntrega());
        }
        if (selecionado.getRegimeDeExecucao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.REGIMEEXECUCAO.name(), selecionado.getRegimeDeExecucao());
        }
        if (selecionado.getFormaDePagamento() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.FORMAPAGAMENTO.name(), selecionado.getFormaDePagamento());
        }
        if (selecionado.getPeriodoDeExecucao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.PERIODOEXECUCAO.name(), selecionado.getPeriodoDeExecucao());
        }
        if (selecionado.getPrazoDeVigencia() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.PRAZOVIGENCIA.name(), selecionado.getPrazoDeVigencia());
        }
        if (selecionado.getComissao() != null && selecionado.getComissao().getAtoDeComissao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.NOMECOMISSAOLICITACAO.name(), selecionado.getComissao().getAtoDeComissao().getAtoLegal().getNome());
        }
        if (selecionado.getAbertaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.HORAABERTURALICITACAO.name(), recuperarHoraAberturaLicitacao(selecionado.getAbertaEm()));
        }
        if (selecionado.getProcessoDeCompra() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.SECRETARIAQUESOLICITOULICITACAO.name(), selecionado.getProcessoDeCompra().getUnidadeOrganizacional());
        }
        if (selecionado.getProcessoDeCompra() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DOTACOESORCAMENTARIASLICITACAO.name(), recuperarDotacoesOrcamentariasLicitacao(selecionado));
        }
        if (selecionado.getComissao() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.PREGOEIRO.name(), recuperarMembroComissaoProgoeiro(selecionado));
        }
        if (selecionado.getAbertaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.HORARIOABERTURAESCRITAPOREXTENSO.name(), recuperarHoraMinutoEscritaPorExtenso(selecionado.getAbertaEm()));
        }
        if (selecionado.getAbertaEm() != null) {
            context.put(TipoModeloDocto.TagsLicitacao.DATAABERTURAPOREXTENSO.name(), DataUtil.recuperarDataEscritaPorExtenso(selecionado.getAbertaEm()));
        }
    }

    private void tagsMembrosComissao(VelocityContext context, Licitacao selecionado) {
        String membros = "";
        if (selecionado.getComissao() != null && selecionado.getComissao().getMembroComissao() != null) {
            for (MembroComissao membroComissao : selecionado.getComissao().getMembroComissao()) {
                membros += membroComissao.getPessoaFisica().getNome() + ", ";
            }
        }
        context.put("MEMBROSCOMISSAO", membros);
    }

    private void tagsParecer(VelocityContext context, Licitacao selecionado) {
        String parecer = "";
        if (selecionado.getPareceres() != null) {
            for (ParecerLicitacao parecerLicitacao : selecionado.getPareceres()) {
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

    private void tagsPublicacao(VelocityContext context, Licitacao selecionado) {
        String publicacao = "";
        if (selecionado.getPublicacoes() != null) {
            for (PublicacaoLicitacao publicacaoLicitacao : selecionado.getPublicacoes()) {
                context.put("DATAPUBLICACAO", new SimpleDateFormat("dd/MM/yyyy").format(publicacaoLicitacao.getDataPublicacao()));
                context.put("VEICULOPUBLICACAO", publicacaoLicitacao.getVeiculoDePublicacao().getNome());
                publicacao += "Veiculo: " + publicacaoLicitacao.getVeiculoDePublicacao().getNome()
                    + ", Publicado em: " + publicacaoLicitacao.getDataPublicacao();
            }
        }
        context.put("PUBLICACAO", publicacao);
    }

    private void tagsRecurso(VelocityContext context, Licitacao selecionado) {
        String recurso = "";
        if (selecionado.getListaDeRecursoLicitacao() != null) {
            for (RecursoLicitacao recursoLicitacao : selecionado.getListaDeRecursoLicitacao()) {
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

    private void tagItensProcessoDeCompra(VelocityContext context, Licitacao selecionado, List<ItemProcessoDeCompra> listaDeItemProcessoDeCompraParaExibir) {
        if (listaDeItemProcessoDeCompraParaExibir != null) {
            context.put("VALOR_TOTAL", recuperaValorTotal(listaDeItemProcessoDeCompraParaExibir));
            String itens = "";
            itens += "<table border=\"1\" width=\"100%\">"
                + "<tr><th align=\"center\" colspan=\"6\" bgcolor=\"#CDC9C9\"> ITENS </th></tr>"
                + "<tr>"
                + "<th align=\"center\"> Lote</th>"
                + "<th align=\"center\"> Item </th>"
                + "<th align=\"center\"> Descrição </th>"
                + "<th align=\"center\"> Unid.</th>"
                + "<th align=\"center\"> Qtd </th>"
                + "</tr>";


            for (ItemProcessoDeCompra itemProcessoDeCompra : listaDeItemProcessoDeCompraParaExibir) {
                itens += "<tr><td align=\"center\">" + itemProcessoDeCompra.getLoteProcessoDeCompra().getNumero() + "</td>"
                    + "<td align=\"center\">" + itemProcessoDeCompra.getNumero() + "</td>"
                    + "<td align=\"center\">" + itemProcessoDeCompra.getItemSolicitacaoMaterial().getDescricao() + "</td>"
                    + "<td align=\"center\">" + itemProcessoDeCompra.getItemSolicitacaoMaterial().getUnidadeMedida().getDescricao() + "</td>"
                    + "<td align=\"center\">" + itemProcessoDeCompra.getQuantidade() + "</td>";

            }
            itens += "</tr>"
                + "</table>";

            context.put("ITENS", itens);
        }
    }

    private void tagsDaComissao(VelocityContext context, Licitacao selecionado) {
        if (selecionado.getComissao() != null) {
            context.put("COMISSAO", selecionado.getComissao().getTitulo());
            for (LicitacaoMembroComissao licitacaoMembroComissao : selecionado.getListaDeLicitacaoMembroComissao()) {
                if (licitacaoMembroComissao.getMembroComissao().getAtribuicaoComissao().equals(AtribuicaoComissao.PRESIDENTE)) {
                    context.put("PRESIDENTECOMISSAO", licitacaoMembroComissao.getMembroComissao().getPessoaFisica().getNome());
                }
            }
        }
    }

    private void tagsFornecedorRepresentante(VelocityContext context, Licitacao selecionado) {
        if (selecionado.getFornecedores() != null) {
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
            for (LicitacaoFornecedor fornec : selecionado.getFornecedores()) {
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

    private void tagsFornecedorVencedor(VelocityContext context, Licitacao selecionado) {
        List<ItemPropostaFornecedor> itens = null;
        if (selecionado.getModalidadeLicitacao().isPregao()) {
            itens = itemPropostaFornecedorFacade.buscarItemPropostaFornecedorVencedorDoPregao(selecionado);
        } else {
            itens = itemPropostaFornecedorFacade.buscarItemPropostaFornecedorVencedorDoCertame(selecionado);
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

    private String recuperarMembroComissaoProgoeiro(Licitacao selecionado) {
        StringBuilder sb = new StringBuilder("");

        if (licitacaoFacade.getComissaoFacade().recuperarMembroComissaoAPartirDaLicitacao(selecionado) != null && !licitacaoFacade.getComissaoFacade().recuperarMembroComissaoAPartirDaLicitacao(selecionado).isEmpty()) {
            for (MembroComissao membroComissao : licitacaoFacade.getComissaoFacade().recuperarMembroComissaoAPartirDaLicitacao(selecionado)) {
                sb.append(membroComissao).append(", ");
            }

            return !"".equals(sb.toString()) ? sb.substring(0, sb.length() - 2) : "";
        }
        return sb.toString();
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

    private String recuperarHoraAberturaLicitacao(Date abertaEm) {
        return new SimpleDateFormat("HH:mm").format(abertaEm);
    }

    private String getEmpreasPorLicitacaoETipoClassificacao(Licitacao licitacao, TipoClassificacaoFornecedor classificacaoFornecedor) {
        StringBuilder sb = new StringBuilder("");

        if (licitacaoFacade.recuperarEmpresasPorLicitacaoETipoClassificacao(licitacao, classificacaoFornecedor) != null && !licitacaoFacade.recuperarEmpresasPorLicitacaoETipoClassificacao(licitacao, classificacaoFornecedor).isEmpty()) {
            for (LicitacaoFornecedor lf : licitacaoFacade.recuperarEmpresasPorLicitacaoETipoClassificacao(licitacao, classificacaoFornecedor)) {
                sb.append(lf).append(", ");
            }
            return !"".equals(sb.toString()) ? sb.substring(0, sb.length() - 2) : "";
        }

        return sb.toString();
    }

    private String getListaMembroComissao(Licitacao selecionado) {
        StringBuilder sb = new StringBuilder("");

        if (selecionado.getComissao().getMembroComissao() != null && !selecionado.getComissao().getMembroComissao().isEmpty()) {
            for (MembroComissao mc : selecionado.getComissao().getMembroComissao()) {
                sb.append(mc).append(", ");
            }

            return !"".equals(sb.toString()) ? sb.substring(0, sb.length() - 2) : "";
        }
        return sb.toString();
    }

    private String getFornecedoresComRepresentante(Licitacao licitacao) {
        StringBuilder sb = new StringBuilder("");

        if (licitacaoFacade.recuperarListaDeLicitacaoFornecedorComRepresentante(licitacao) != null && !licitacaoFacade.recuperarListaDeLicitacaoFornecedorComRepresentante(licitacao).isEmpty()) {
            for (LicitacaoFornecedor lf : licitacaoFacade.recuperarListaDeLicitacaoFornecedorComRepresentante(licitacao)) {
                sb.append(lf).append(", ");
            }

            return !"".equals(sb.toString()) ? sb.substring(0, sb.length() - 2) : "";
        }
        return sb.toString();
    }

    private String getListaDeItensDoPregaoComSeusVencedores(Licitacao selecionado) {
        StringBuilder sb = new StringBuilder("");

        if (licitacaoFacade.getPregaoFacade().recuperarListaDeItensPregao(licitacaoFacade.getPregaoFacade().recuperarPregaoAPartirDaLicitacao(selecionado)) != null
            && !licitacaoFacade.getPregaoFacade().recuperarListaDeItensPregao(licitacaoFacade.getPregaoFacade().recuperarPregaoAPartirDaLicitacao(selecionado)).isEmpty()) {

            for (ItemPregao item : licitacaoFacade.getPregaoFacade().recuperarListaDeItensPregao(licitacaoFacade.getPregaoFacade().recuperarPregaoAPartirDaLicitacao(selecionado))) {
                item = licitacaoFacade.getItemPregaoFacade().recuperar(item.getId());
                sb.append(" Item: ").append(item).append(" Vencedor: ").append(item.recuperarVencedor()).append(", ");
            }

            return !"".equals(sb.toString()) ? sb.substring(0, sb.length() - 2) : "";
        }
        return sb.toString();
    }

    private String getLocalDaPublicacaoDoAvisoDeLicitacao(Licitacao selecionado) {
        StringBuilder sb = new StringBuilder("");

        if (licitacaoFacade.recuperarLocalPublicacoesDoAvisoDaLicitacao(selecionado) != null && !licitacaoFacade.recuperarLocalPublicacoesDoAvisoDaLicitacao(selecionado).isEmpty()) {
            for (PublicacaoLicitacao pl : licitacaoFacade.recuperarLocalPublicacoesDoAvisoDaLicitacao(selecionado)) {
                sb.append(pl).append(", ");
            }

            return !"".equals(sb.toString()) ? sb.substring(0, sb.length() - 2) : "";
        }
        return sb.toString();
    }


    private String recuperarDotacoesOrcamentariasLicitacao(Licitacao selecionado) {
        StringBuilder sb = new StringBuilder("");

        for (DotacaoSolicitacaoMaterial dsm : licitacaoFacade.getDotacaoSolicitacaoMaterialFacade().recuperarDotacoesAPartirDaLicitacao(selecionado)) {
            sb.append(dsm).append(", ");
        }

        return !"".equals(sb.toString()) ? sb.substring(0, sb.length() - 2) : "";
    }

    private boolean licitacaoEstaEmAlgumPregao(Licitacao licitacao) {
        return licitacaoFacade.getPregaoFacade().verificarRelacaoDaLicitacaoEmUmPregao(licitacao);
    }

    private Date getDataUltimaPublicacao(Licitacao selecionado) {
        return licitacaoFacade.recuperarUltimaPublicacaoDaLicitacao(selecionado).getDataPublicacao();
    }

    public boolean fornecedorPessoaJuridica(LicitacaoFornecedor licForn) {
        if (licForn.getEmpresa() instanceof PessoaJuridica) {
            return true;
        }

        return false;
    }

}
