/*
 * Codigo gerado automaticamente em Thu May 10 14:10:07 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.consultaentidade.TipoAlinhamento;
import br.com.webpublico.consultaentidade.TipoCampo;
import br.com.webpublico.controle.portaltransparencia.ModuloPrefeituraPortalFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.*;
import br.com.webpublico.enums.Operador;
import br.com.webpublico.enums.PortalTransparenciaSituacao;
import br.com.webpublico.enums.TipoPainelPortal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.PaginaPrefeituraPortalFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.beust.jcommander.internal.Lists;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@ManagedBean(name = "paginaPrefeituraPortalControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaPaginaPrefeituraPortal", pattern = "/portal/transparencia/pagina/novo/", viewId = "/faces/portaltransparencia/pagina/edita.xhtml"),
    @URLMapping(id = "editarPaginaPrefeituraPortal", pattern = "/portal/transparencia/pagina/editar/#{paginaPrefeituraPortalControlador.id}/", viewId = "/faces/portaltransparencia/pagina/edita.xhtml"),
    @URLMapping(id = "listarPaginaPortal", pattern = "/portal/transparencia/pagina/listar/", viewId = "/faces/portaltransparencia/pagina/lista.xhtml")
})
public class PaginaPortalControlador extends PrettyControlador<PaginaPrefeituraPortal> implements Serializable, CRUD {
    private static final String KEY_PARAMETRO = "AUX-PAGINA-PORTAL";
    private static final String KEY_PARAMETRO_SELECIONADO = "AUX-PAGINA-PORTAL-SELECIONADO";
    @EJB
    private PaginaPrefeituraPortalFacade paginaPrefeituraPortalFacade;
    @EJB
    private ModuloPrefeituraPortalFacade moduloFacade;
    private ConsultaPortal consultaPortal;
    private UsuarioPaginaPortal usuario;
    private List<UsuarioPaginaPortal> usuarios;
    private ConverterGenerico converterModulo;
    private TabPortal tabPortal;
    private PainelPortal painelPortal;
    private String stringDialogVisualizar;


    public PaginaPortalControlador() {
        super(PaginaPrefeituraPortal.class);
    }

    public ConsultaPortal getConsultaPortal() {
        return consultaPortal;
    }

    @URLAction(mappingId = "novaPaginaPrefeituraPortal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        ConsultaPortal recuperado = (ConsultaPortal) Web.pegaDaSessao(KEY_PARAMETRO);
        if (recuperado != null) {
            consultaPortal = recuperado;
        } else {
            consultaPortal = new ConsultaPortal();
        }
        PaginaPrefeituraPortal pagina = (PaginaPrefeituraPortal) Web.pegaDaSessao(KEY_PARAMETRO_SELECIONADO);
        if (pagina != null) {
            this.selecionado = pagina;
            this.selecionado.setTipoComponente(PaginaPrefeituraPortal.TipoComponentePaginaPortal.GRID);
            this.selecionado.setId(null);
            this.selecionado.setChave(pagina.getChave() + "-ver");
            this.selecionado.setNome("Visualizar de " + pagina.getNome());
        } else {
            super.novo();
        }

        usuario = new UsuarioPaginaPortal();
        usuarios = Lists.newArrayList();
    }

    @URLAction(mappingId = "editarPaginaPrefeituraPortal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        usuario = new UsuarioPaginaPortal();
        usuarios = paginaPrefeituraPortalFacade.buscarUsuarios(selecionado, null);
        try {
            consultaPortal = new ObjectMapper().readValue(selecionado.getConteudo(), ConsultaPortal.class);
            atualizarOrdemDasTabs();
        } catch (Exception e) {
            consultaPortal = new ConsultaPortal();
            consultaPortal.setConsulta(selecionado.getConteudo());
            logger.error("Erro ao recuperar consulta da página do portal", e);
        }
    }

    private void atualizarOrdemDasTabs() {
        if (consultaPortal != null && consultaPortal.getTabs() != null && !consultaPortal.getTabs().isEmpty()) {
            int ordem = 1;
            for (TabPortal tab : consultaPortal.getTabs()) {
                if (tab.getOrdem() == null) {
                    tab.setOrdem(ordem);
                    ordem++;
                }
            }
            consultaPortal.getTabs().sort(Comparator.comparing(TabPortal::getOrdem));
        }
    }

    public void importar(FileUploadEvent event) {
        try {
            UploadedFile file = event.getFile();
            InputStream inputstream = file.getInputstream();

            InputStreamReader in = new InputStreamReader(inputstream);
            BufferedReader bufferedReader = new BufferedReader(in);

            String linha;
            String conteudo = "";
            while ((linha = bufferedReader.readLine()) != null) {
                conteudo += linha;
            }

            consultaPortal = new ObjectMapper().readValue(conteudo, ConsultaPortal.class);
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
        }
    }

    public ConverterGenerico getConverterModulo() {
        if (converterModulo == null) {
            converterModulo = new ConverterGenerico(ModuloPrefeituraPortal.class, moduloFacade);
        }
        return converterModulo;
    }

    public List<ModuloPrefeituraPortal> buscarModulo(String parte) {
        if (selecionado.getPrefeitura() != null) {
            return moduloFacade.buscar(parte, selecionado.getPrefeitura());
        } else {
            return moduloFacade.buscar(parte, null);
        }
    }

    public void copiarPagina() {
        Web.poeNaSessao(KEY_PARAMETRO, consultaPortal);
        Web.poeNaSessao(KEY_PARAMETRO_SELECIONADO, selecionado);
        FacesUtil.redirecionamentoInterno("/portal/transparencia/pagina/novo/");
    }

    @Override
    public void salvar() {
        try {
            validarCamposObrigatorios();
            if (consultaPortal.getConsulta() != null) {
                if (!consultaPortal.getConsulta().contains(PaginaPrefeituraPortalFacade.PARAMETRO_WHERE)) {
                    consultaPortal.setConsulta(consultaPortal.getConsulta() + " " + PaginaPrefeituraPortalFacade.PARAMETRO_WHERE);
                }
            }
            String json = new ObjectMapper().writeValueAsString(consultaPortal);
            selecionado.setConteudo(json);
            if (PaginaPrefeituraPortal.TipoPaginaPortal.SQL.equals(selecionado.getTipoPaginaPortal())) {
                selecionado.setConteudoHtml("<div></div>");
            }
            super.salvar();
            for (UsuarioPaginaPortal usuario : usuarios) {
                List<UsuarioPaginaPortal> usuarioPaginaPortals = paginaPrefeituraPortalFacade.buscarUsuarios(selecionado, usuario);
                if (usuarioPaginaPortals == null || usuarioPaginaPortals.isEmpty()) {
                    UsuarioPagina usu = new UsuarioPagina(usuario, selecionado);
                    paginaPrefeituraPortalFacade.salvarUsuario(usu);
                }
            }
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception e) {
            logger.error("Erro ao salvar consulta da página do portal", e);
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void montarFieldsConsultaParaExportacaoEntidade() {
        try {
            if (Strings.isEmpty(consultaPortal.getCount()) && Strings.isNotEmpty(consultaPortal.getConsulta())) {
                String from = consultaPortal.getConsulta()
                    .substring(consultaPortal.consulta.indexOf("from"), consultaPortal.consulta.length());
                consultaPortal.setCount("select count(1) " + from);
            }
            paginaPrefeituraPortalFacade.montarFieldsConsultaParaExportacaoEntidade(consultaPortal, selecionado);
        } catch (Exception e) {
            FacesUtil.addErrorGenerico(e);
            logger.error("Erro ao gerar consulta da página do portal", e);
        }
    }

    public void validarCamposObrigatorios() {
        ValidacaoException ve = new ValidacaoException();
        if (PaginaPrefeituraPortal.TipoPaginaPortal.HTML.equals(selecionado.getTipoPaginaPortal())) {
            if (selecionado.getConteudoHtml() == null || selecionado.getConteudoHtml().isEmpty()) {
                ve.adicionarMensagemDeCampoObrigatorio("Campo Conteúdo HTML deve ser preenchido.");
            }
        }
        ve.lancarException();
    }

    public List<TAGPortal> getTags() {
        return Lists.newArrayList(TAGPortal.values());
    }

    public void montarSelectUnidadePortal() {
        try {
            this.consultaPortal.setConsulta(this.consultaPortal.getConsulta() + " and ho.SUBORDINADA_ID in (select UNIDADE_ID from UNIDADEPREFEITURAPORTAL where PREFEITURA_ID = $PREFEITURAPORTAL_ID) ");
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoPermitida("Erro ao adicionar SQL da unidade. Detalhe do erro: " + e.getMessage());
        }
    }

    public StreamedContent toJson() throws IOException {
        File txt = File.createTempFile(selecionado.getChave() + ".json", "json");
        FileOutputStream fos = new FileOutputStream(txt);
        InputStream stream = new FileInputStream(txt);
        String json = new ObjectMapper().writeValueAsString(consultaPortal);
        JSONObject jsonObject = new JSONObject(json);
        fos.write(jsonObject.toString(4).getBytes());
        fos.close();
        return new DefaultStreamedContent(stream, "application/json", selecionado.getChave() + ".json");
    }

    public void copiarParaPesquisavel(List<FieldPesquisavelPortal> pesquisaveis, FieldConsultaPortal field) {
        FieldPesquisavelPortal pesquisavel = new FieldPesquisavelPortal();
        pesquisavel.setDescricao(field.getDescricao());
        pesquisavel.setColumnName(field.getColumnName());
        pesquisavel.setColumnValue(field.getColumnValue());
        pesquisavel.setTipo(field.getTipo());
        pesquisavel.setTipoEnum(field.getTipoEnum());
        pesquisavel.setValorPadrao(field.getValorPadrao());
        pesquisaveis.add(pesquisavel);
    }

    public void moverItem(List<FieldConsultaPortal> fields, FieldConsultaPortal field, Long proximoIndex) {
        try {
            int index = fields.indexOf(field);
            FieldConsultaPortal temp = fields.get(index + proximoIndex.intValue());
            field.setPosicao(index + proximoIndex.intValue());
            fields.set(index + proximoIndex.intValue(), field);
            fields.set(index, temp);
        } catch (Exception e) {
            FacesUtil.addError("Operação não realizada", "Não foi possível mover o item");
        }
    }

    public void moverItemPesquisavel(List<FieldPesquisavelPortal> pesquisaveis, FieldPesquisavelPortal field, Long proximoIndex) {
        try {
            int index = pesquisaveis.indexOf(field);
            FieldPesquisavelPortal temp = pesquisaveis.get(index + proximoIndex.intValue());
            pesquisaveis.set(index + proximoIndex.intValue(), field);
            pesquisaveis.set(index, temp);
        } catch (Exception e) {
            FacesUtil.addError("Operação não realizada", "Não foi possível mover o item");
        }
    }

    public void selecionarTipoCampo(FieldConsultaPortal field) {
        if (TipoCampo.ENUM.equals(field.getTipo()) && Strings.isNotBlank(field.getTipoEnum())) {
            List<SelectItem> selectItems = tiposEnum(field.getTipoEnum());
            Enum[] enums = new Enum[selectItems.size()];
            for (SelectItem selectItem : selectItems) {
                enums[selectItems.indexOf(selectItem)] = (Enum) selectItem.getValue();
            }
            field.setValorPadrao(enums);
        }
        if (TipoCampo.MONETARIO.equals(field.getTipo())) {
            field.setTipoAlinhamento(TipoAlinhamento.DIREITA);
            field.setTotalizar(Boolean.TRUE);
        }
    }

    public void selecionarTipoCampoPesquisavel(FieldPesquisavelPortal field) {
        if (TipoCampo.ENUM.equals(field.getTipo()) && Strings.isNotBlank(field.getTipoEnum())) {
            List<SelectItem> selectItems = tiposEnum(field.getTipoEnum());
            Enum[] enums = new Enum[selectItems.size()];
            for (SelectItem selectItem : selectItems) {
                enums[selectItems.indexOf(selectItem)] = (Enum) selectItem.getValue();
            }
            field.setValorPadrao(enums);
        }
    }

    public void removerField(List<FieldConsultaPortal> fields, FieldConsultaPortal fieldExportarEntidade) {
        fields.remove(fieldExportarEntidade);
    }

    public void removerFieldPesquisavel(List<FieldPesquisavelPortal> pesquisaveis, FieldPesquisavelPortal field) {
        pesquisaveis.remove(field);
    }

    public void adiconarField(List<FieldConsultaPortal> fields) {
        FieldConsultaPortal fcp = new FieldConsultaPortal();
        fcp.setPosicao(fields.size() + 1);
        fields.add(fcp);
    }

    public void adiconarFieldPesquisavel(List<FieldPesquisavelPortal> pesquisaveis) {
        pesquisaveis.add(new FieldPesquisavelPortal());
    }

    @Override
    public String getCaminhoPadrao() {
        return "/portal/transparencia/pagina/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return paginaPrefeituraPortalFacade;
    }

    public List<SelectItem> getTiposPagina() {
        return Util.getListSelectItem(PaginaPrefeituraPortal.TipoPaginaPortal.values());
    }

    public List<SelectItem> getTiposComponentePagina() {
        return Util.getListSelectItem(PaginaPrefeituraPortal.TipoComponentePaginaPortal.values());
    }

    public List<SelectItem> getTiposPaineis() {
        return Util.getListSelectItemSemCampoVazio(TipoPainelPortal.values());
    }

    public List<SelectItem> getTiposAlinhamento() {
        return Util.getListSelectItem(TipoAlinhamento.values());
    }

    public List<SelectItem> getTiposCampo() {
        return Util.getListSelectItem(TipoCampo.values());
    }

    public List<SelectItem> getOperadorCampo() {
        return Util.getListSelectItem(Operador.values());
    }

    public List<SelectItem> getTiposMultiSelect() {
        return Util.getListSelectItem(TipoMultiSelect.values());
    }

    public List<SelectItem> tiposEnum(String nomeEnum) {
        try {
            Class enumClass = Class.forName(nomeEnum);
            return Util.getListSelectItem(enumClass.getEnumConstants(), false);
        } catch (Exception e) {
            logger.error("Erro ao tentar carregar o enum [{}]", nomeEnum);
        }
        return com.google.common.collect.Lists.newArrayList();
    }

    public UsuarioPaginaPortal getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioPaginaPortal usuario) {
        this.usuario = usuario;
    }

    public void adicionarTodosUsuario() {
        List<UsuarioPaginaPortal> usuarios = paginaPrefeituraPortalFacade.buscarUsuarios(null, null);
        if (usuarios != null && !usuarios.isEmpty()) {
            for (UsuarioPaginaPortal usuarioPaginaPortal : usuarios) {
                if (!this.usuarios.contains(usuarioPaginaPortal)) {
                    this.usuarios = Util.adicionarObjetoEmLista(this.usuarios, usuarioPaginaPortal);
                }
            }
        }
    }

    public void adicionarUsuario() {
        try {
            if (Strings.isEmpty(usuario.getLogin())) {
                throw new ExcecaoNegocioGenerica("O campo Login é obrigatório.");
            }
            if (Strings.isEmpty(usuario.getSenha())) {
                throw new ExcecaoNegocioGenerica("O campo senha é obrigatório.");
            }
            usuarios = Util.adicionarObjetoEmLista(usuarios, usuario);
            this.usuario = null;
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada(e.getMessage());
        }
    }

    public void removerUsuario(UsuarioPaginaPortal usuario) {
        try {
            usuarios.remove(usuario);
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada(e.getMessage());
        }
    }

    public void novaTabPortal() {
        tabPortal = new TabPortal();
    }

    public void cancelarTabPortal() {
        tabPortal = null;
    }

    public void adicionarTabPortal() {
        try {
            if (Strings.isEmpty(tabPortal.getTitulo())) {
                throw new ExcecaoNegocioGenerica("O campo Título é obrigatório.");
            }
            if (tabPortal.getOrdem() == null) {
                throw new ExcecaoNegocioGenerica("O campo Ordem é obrigatório.");
            }
            if (Strings.isEmpty(tabPortal.getConsulta())) {
                throw new ExcecaoNegocioGenerica("O campo Consulta é obrigatório.");
            }
            if (Strings.isEmpty(tabPortal.getCount())) {
                throw new ExcecaoNegocioGenerica("O campo Count é obrigatório.");
            }
            Util.adicionarObjetoEmLista(consultaPortal.getTabs(), tabPortal);
            cancelarTabPortal();
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada(e.getMessage());
        }
    }

    public void editarTabPortal(TabPortal tabPortal) {
        this.tabPortal = (TabPortal) Util.clonarObjeto(tabPortal);
    }

    public void removerTabPortal(TabPortal tabPortal) {
        consultaPortal.getTabs().remove(tabPortal);
    }

    public void novoPainelPortal() {
        painelPortal = new PainelPortal();
    }

    public void cancelarPainelPortal() {
        painelPortal = null;
    }

    public void adicionarPainelPortal() {
        try {
            if (Strings.isEmpty(painelPortal.getDescricao())) {
                throw new ExcecaoNegocioGenerica("O campo Descrição é obrigatório.");
            }
            if (Strings.isEmpty(painelPortal.getConsulta())) {
                throw new ExcecaoNegocioGenerica("O campo Consulta é obrigatório.");
            }
            if (painelPortal.getTipoPainelPortal() == null) {
                throw new ExcecaoNegocioGenerica("O campo Tipo do Painel é obrigatório.");
            }
            if (painelPortal.getTipoPainelPortal().isGrafico() && Strings.isEmpty(painelPortal.getTipoGrafico())) {
                throw new ExcecaoNegocioGenerica("O campo Tipo do Gráfico é obrigatório.");
            }
            if (painelPortal.getTipoPainelPortal().isGrafico() && Strings.isEmpty(painelPortal.getTipoValorGrafico())) {
                throw new ExcecaoNegocioGenerica("O campo Tipo do Valor do Gráfico é obrigatório.");
            }
            Util.adicionarObjetoEmLista(consultaPortal.getPaineis(), painelPortal);
            cancelarPainelPortal();
        } catch (Exception e) {
            FacesUtil.addOperacaoRealizada(e.getMessage());
        }
    }

    public void editarPainelPortal(PainelPortal painelPortal) {
        this.painelPortal = (PainelPortal) Util.clonarObjeto(painelPortal);
    }

    public void removerPainelPortal(PainelPortal painelPortal) {
        consultaPortal.getPaineis().remove(painelPortal);
    }

    public List<UsuarioPaginaPortal> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(List<UsuarioPaginaPortal> usuarios) {
        this.usuarios = usuarios;
    }

    public static class ConsultaPortal {
        private String consulta;
        private String count;
        private Integer totalRegistros;
        private List<FieldConsultaPortal> fields;
        private List<FieldPesquisavelPortal> pesquisaveis;
        private List<TabPortal> tabs;
        private List<PainelPortal> paineis;

        public ConsultaPortal() {
            fields = Lists.newArrayList();
            pesquisaveis = Lists.newArrayList();
            tabs = Lists.newArrayList();
            paineis = Lists.newArrayList();
        }

        public String getConsulta() {
            return consulta;
        }

        public void setConsulta(String consulta) {
            this.consulta = consulta;
        }

        public List<FieldConsultaPortal> getFields() {
            return fields;
        }

        public void setFields(List<FieldConsultaPortal> fields) {
            this.fields = fields;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public List<FieldPesquisavelPortal> getPesquisaveis() {
            return pesquisaveis;
        }

        public void setPesquisaveis(List<FieldPesquisavelPortal> pesquisaveis) {
            this.pesquisaveis = pesquisaveis;
        }

        public List<TabPortal> getTabs() {
            return tabs;
        }

        public void setTabs(List<TabPortal> tabs) {
            this.tabs = tabs;
        }

        public List<PainelPortal> getPaineis() {
            return paineis;
        }

        public void setPaineis(List<PainelPortal> paineis) {
            this.paineis = paineis;
        }

        public Integer getTotalRegistros() {
            return totalRegistros;
        }

        public void setTotalRegistros(Integer totalRegistros) {
            this.totalRegistros = totalRegistros;
        }
    }

    public static class FieldConsultaPortal {
        private Integer posicao;
        private String descricao;
        private String columnName;
        private String columnValue;
        private TipoAlinhamento tipoAlinhamento;
        private Boolean totalizar = false;
        private LinkConsultaPortal link;
        private TipoCampo tipo;
        private String tipoEnum;
        private Object valorPadrao;
        private Boolean escondido = false;
        private Boolean agrupador = false;

        public FieldConsultaPortal() {
        }

        public FieldConsultaPortal(Integer posicao, String descricao, String columnName, String columnValue, TipoAlinhamento tipoAlinhamento, TipoCampo tipo, String tipoEnum) {
            this.posicao = posicao;
            this.descricao = descricao;
            this.columnName = columnName;
            this.columnValue = columnValue;
            this.tipoAlinhamento = tipoAlinhamento;
            this.tipo = tipo;
            this.tipoEnum = tipoEnum;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(String columnValue) {
            this.columnValue = columnValue;
        }

        public LinkConsultaPortal getLink() {
            return link;
        }

        public void setLink(LinkConsultaPortal link) {
            this.link = link;
        }

        public TipoAlinhamento getTipoAlinhamento() {
            return tipoAlinhamento;
        }

        public void setTipoAlinhamento(TipoAlinhamento tipoAlinhamento) {
            this.tipoAlinhamento = tipoAlinhamento;
        }

        public Boolean getTotalizar() {
            return totalizar;
        }

        public void setTotalizar(Boolean totalizar) {
            this.totalizar = totalizar;
        }

        public void liberarLink() {
            this.link = new LinkConsultaPortal();
            this.link.setColumnOrigem(this.columnName);
        }

        public void removerLink() {
            this.link = null;
        }

        public TipoCampo getTipo() {
            return tipo;
        }

        public void setTipo(TipoCampo tipo) {
            this.tipo = tipo;
        }

        public Integer getPosicao() {
            return posicao;
        }

        public void setPosicao(Integer posicao) {
            this.posicao = posicao;
        }

        public String getTipoEnum() {
            return tipoEnum;
        }

        public void setTipoEnum(String tipoEnum) {
            this.tipoEnum = tipoEnum;
        }

        public Object getValorPadrao() {
            return valorPadrao;
        }

        public void setValorPadrao(Object valorPadrao) {
            this.valorPadrao = valorPadrao;
        }

        public Boolean getEscondido() {
            return escondido;
        }

        public void setEscondido(Boolean escondido) {
            this.escondido = escondido;
        }

        public Boolean getAgrupador() {
            return agrupador;
        }

        public void setAgrupador(Boolean agrupador) {
            this.agrupador = agrupador;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FieldConsultaPortal that = (FieldConsultaPortal) o;
            return Objects.equals(columnName, that.columnName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(columnName);
        }
    }

    public static class FieldPesquisavelPortal {
        private String descricao;
        private String columnName;
        private String columnValue;
        private TipoCampo tipo;
        private Operador operacaoPadrao;
        private String tipoEnum;
        private Object valorPadrao;
        private TipoMultiSelect tipoMultiSelect;

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getColumnName() {
            return columnName;
        }

        public void setColumnName(String columnName) {
            this.columnName = columnName;
        }

        public TipoCampo getTipo() {
            return tipo;
        }

        public void setTipo(TipoCampo tipo) {
            this.tipo = tipo;
        }


        public Operador getOperacaoPadrao() {
            return operacaoPadrao;
        }

        public void setOperacaoPadrao(Operador operacaoPadrao) {
            this.operacaoPadrao = operacaoPadrao;
        }

        public Object getValorPadrao() {
            return valorPadrao;
        }

        public void setValorPadrao(Object valorPadrao) {
            this.valorPadrao = valorPadrao;
        }

        public String getColumnValue() {
            return columnValue;
        }

        public void setColumnValue(String columnValue) {
            this.columnValue = columnValue;
        }

        public String getTipoEnum() {
            return tipoEnum;
        }

        public void setTipoEnum(String tipoEnum) {
            this.tipoEnum = tipoEnum;
        }

        public TipoMultiSelect getTipoMultiSelect() {
            return tipoMultiSelect;
        }

        public void setTipoMultiSelect(TipoMultiSelect tipoMultiSelect) {
            this.tipoMultiSelect = tipoMultiSelect;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FieldPesquisavelPortal that = (FieldPesquisavelPortal) o;
            return Objects.equals(columnName, that.columnName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(columnName);
        }
    }

    public static class LinkConsultaPortal {
        private String chaveDestino;
        private String columnOrigem;
        private String columnDestino;

        public String getChaveDestino() {
            return chaveDestino;
        }

        public void setChaveDestino(String chaveDestino) {
            this.chaveDestino = chaveDestino;
        }

        public String getColumnOrigem() {
            return columnOrigem;
        }

        public void setColumnOrigem(String columnOrigem) {
            this.columnOrigem = columnOrigem;
        }

        public String getColumnDestino() {
            return columnDestino;
        }

        public void setColumnDestino(String columnDestino) {
            this.columnDestino = columnDestino;
        }
    }

    public static class TabPortal {
        private String titulo;
        private String subTitulo;
        private String consulta;
        private String count;
        private Integer totalRegistros;
        private Integer ordem;
        private List<FieldPesquisavelPortal> pesquisaveis;
        private List<FieldConsultaPortal> fields;

        public TabPortal() {
            fields = Lists.newArrayList();
            pesquisaveis = Lists.newArrayList();
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getSubTitulo() {
            return subTitulo;
        }

        public void setSubTitulo(String subTitulo) {
            this.subTitulo = subTitulo;
        }

        public String getConsulta() {
            return consulta;
        }

        public void setConsulta(String consulta) {
            this.consulta = consulta;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public Integer getTotalRegistros() {
            return totalRegistros;
        }

        public void setTotalRegistros(Integer totalRegistros) {
            this.totalRegistros = totalRegistros;
        }

        public Integer getOrdem() {
            return ordem;
        }

        public void setOrdem(Integer ordem) {
            this.ordem = ordem;
        }

        public List<FieldConsultaPortal> getFields() {
            return fields;
        }

        public void setFields(List<FieldConsultaPortal> fields) {
            this.fields = fields;
        }

        public List<FieldPesquisavelPortal> getPesquisaveis() {
            return pesquisaveis;
        }

        public void setPesquisaveis(List<FieldPesquisavelPortal> pesquisaveis) {
            this.pesquisaveis = pesquisaveis;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TabPortal that = (TabPortal) o;
            return Objects.equals(ordem, that.ordem);
        }

        @Override
        public int hashCode() {
            return Objects.hash(ordem);
        }
    }

    public static class PainelPortal {
        private String descricao;
        private boolean metadeDaTela;
        private TipoPainelPortal tipoPainelPortal;
        private String tipoGrafico;
        private String tipoValorGrafico;
        private String consulta;

        public PainelPortal() {
            tipoPainelPortal = TipoPainelPortal.GRAFICO;
            metadeDaTela = false;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public boolean isMetadeDaTela() {
            return metadeDaTela;
        }

        public void setMetadeDaTela(boolean metadeDaTela) {
            this.metadeDaTela = metadeDaTela;
        }

        public TipoPainelPortal getTipoPainelPortal() {
            return tipoPainelPortal;
        }

        public void setTipoPainelPortal(TipoPainelPortal tipoPainelPortal) {
            this.tipoPainelPortal = tipoPainelPortal;
        }

        public String getTipoGrafico() {
            return tipoGrafico;
        }

        public void setTipoGrafico(String tipoGrafico) {
            this.tipoGrafico = tipoGrafico;
        }

        public String getTipoValorGrafico() {
            return tipoValorGrafico;
        }

        public void setTipoValorGrafico(String tipoValorGrafico) {
            this.tipoValorGrafico = tipoValorGrafico;
        }

        public String getConsulta() {
            return consulta;
        }

        public void setConsulta(String consulta) {
            this.consulta = consulta;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            PainelPortal that = (PainelPortal) o;
            return Objects.equals(descricao, that.descricao);
        }

        @Override
        public int hashCode() {
            return Objects.hash(descricao);
        }
    }

    public TabPortal getTabPortal() {
        return tabPortal;
    }

    public void setTabPortal(TabPortal tabPortal) {
        this.tabPortal = tabPortal;
    }

    public PainelPortal getPainelPortal() {
        return painelPortal;
    }

    public void setPainelPortal(PainelPortal painelPortal) {
        this.painelPortal = painelPortal;
    }

    public void atualizarStringDialog(String s) {
        stringDialogVisualizar = s;
    }

    public String getStringDialogVisualizar() {
        return stringDialogVisualizar;
    }

    public void setStringDialogVisualizar(String stringDialogVisualizar) {
        this.stringDialogVisualizar = stringDialogVisualizar;
    }

    public enum TipoMultiSelect implements EnumComDescricao {
        UNIDADE_ORCAMENTARIA("Unidade Orçamentária"),
        UNIDADE_ADMINISTRATIVA("Unidade Administrativa"),
        UNIDADE_ADMINISTRATIVA_NIVEL2("Unidade Administrativa Nv.2"),
        FONTE_DE_RECURSO("Fonte de Recurso"),
        NATUREZA_DA_DESPESA("Natureza da Despesa"),
        PROGRAMA_PPA("Programa PPA"),
        ACAO_PPA("Projeto/Atividade"),
        FUNCAO("Função"),
        SUB_FUNCAO("Subfunção"),
        PESSOA_EMPENHO("Pessoa Empenho"),
        MODALIDADE_CONTRATO_FP("Modalidade de ContratoFP"),
        CARGO("Cargo"),
        FUNCAO_GRATIFICADA("Função Gratificada"),
        NATUREZA_DA_RECEITA("Natureza da Receita");
        private String descricao;

        TipoMultiSelect(String descricao) {
            this.descricao = descricao;
        }

        @Override
        public String getDescricao() {
            return descricao;
        }

        @Override
        public String toString() {
            return descricao;
        }

    }

    public void limparTipoGrafico() {
        painelPortal.setTipoGrafico(null);
        painelPortal.setTipoValorGrafico(null);
    }

    public List<PaginaPrefeituraPortal> completarPaginas(String parte) {
        return paginaPrefeituraPortalFacade.buscarPaginas(parte, null);
    }

    public void atualizarConsultasQuandoAnexoGeral() {
        try {
            validarChaveAnexoGeral();
            if (selecionado.getAnexoGeral()) {
                selecionado.setTipoPaginaPortal(PaginaPrefeituraPortal.TipoPaginaPortal.SQL);
                selecionado.setTipoComponente(PaginaPrefeituraPortal.TipoComponentePaginaPortal.TABELA);
                consultaPortal.setConsulta(getSelectAnexoGeral());
                consultaPortal.setCount(getCountAnexoGeral());
                atualizarCamposTabelaAnexoGeral();
            } else {
                selecionado.setTipoPaginaPortal(null);
                selecionado.setTipoComponente(null);
                consultaPortal.setConsulta("");
                consultaPortal.setCount("");
                consultaPortal.getFields().clear();
            }
        } catch (ValidacaoException ve) {
            selecionado.setAnexoGeral(Boolean.FALSE);
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            selecionado.setAnexoGeral(Boolean.FALSE);
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private void validarChaveAnexoGeral() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getChave() == null || selecionado.getChave().trim().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo <b>Chave (url)</b> deve ser informado para que a consulta seja montada corretamente.");
        }
        ve.lancarException();
    }

    private void atualizarCamposTabelaAnexoGeral() {
        consultaPortal.getFields().add(new FieldConsultaPortal(1, "Nome", "NOME", "NOME", TipoAlinhamento.ESQUERDA, TipoCampo.STRING, null));
        consultaPortal.getFields().add(new FieldConsultaPortal(2, "Ano", "ANO", "ANO", TipoAlinhamento.ESQUERDA, TipoCampo.INTEGER, null));
        consultaPortal.getFields().add(new FieldConsultaPortal(3, "Mês", "MES", "MES", TipoAlinhamento.ESQUERDA, TipoCampo.ENUM, "Mes"));
        consultaPortal.getFields().add(new FieldConsultaPortal(4, "Unidade", "UNIDADE", "UNIDADE", TipoAlinhamento.ESQUERDA, TipoCampo.STRING, null));
        consultaPortal.getFields().add(new FieldConsultaPortal(5, "Observações", "OBSERVACOES", "OBSERVACOES", TipoAlinhamento.ESQUERDA, TipoCampo.STRING, null));
        consultaPortal.getFields().add(new FieldConsultaPortal(6, "Anexo", "IDARQUIVO", "IDARQUIVO", TipoAlinhamento.CENTRO, TipoCampo.ARQUIVO, null));
    }

    private String getSelectAnexoGeral() {
        return " select apt.nome, " +
            " ex.ano, " +
            " apt.mes, " +
            " case when vw.id is not null then vw.CODIGO || ' - ' || vw.descricao else '' end as unidade, " +
            " apt.observacoes, " +
            " apt.arquivo_id as idArquivo " +
            getFromAnexoGeral();
    }

    private String getCountAnexoGeral() {
        return " select count(apt.id) " +
            getFromAnexoGeral();
    }

    private String getFromAnexoGeral() {
        return " from anexoportaltransparencia apt " +
            "         inner join exercicio ex on ex.id = apt.exercicio_id " +
            "         inner join paginaprefeituraportal ppp on ppp.id = apt.paginaprefeituraportal_id " +
            "         left join vwhierarquiaadministrativa vw on vw.subordinada_id = apt.unidadeorganizacional_id " +
            "               and trunc(apt.datacadastro) between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, trunc(apt.datacadastro)) " +
            " where apt.situacao = '" + PortalTransparenciaSituacao.PUBLICADO.name() + "'" +
            "   and ppp.chave = '" + selecionado.getChave() + "'";
    }
}
