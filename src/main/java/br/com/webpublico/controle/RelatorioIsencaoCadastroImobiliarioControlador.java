package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroRelatorioIsencaoCadastroImobiliario;
import br.com.webpublico.enums.tributario.TipoCategoriaIsencaoIPTU;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.tributario.RelatorioIsencaoCadastroImobiliarioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.AgrupamentoRelatorioDTO;
import br.com.webpublico.webreportdto.dto.tributario.OrigemIsencaoIPTU;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioImovelsentos", pattern = "/relatorios/relatorio-imoveis-isentos/",
        viewId = "/faces/tributario/iptu/isencaoimpostos/relatorios/edita.xhtml"),
})

public class RelatorioIsencaoCadastroImobiliarioControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RelatorioIsencaoCadastroImobiliarioControlador.class);

    @EJB
    private RelatorioIsencaoCadastroImobiliarioFacade facade;
    private FiltroRelatorioIsencaoCadastroImobiliario filtroRelatorio;
    private List<ValorPossivel> valoresPatrimonio;
    private List<CategoriaIsencaoIPTU> categorias;

    public FiltroRelatorioIsencaoCadastroImobiliario getFiltroRelatorio() {
        return filtroRelatorio;
    }

    @URLAction(mappingId = "novoRelatorioImovelsentos", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparFiltros() {
        filtroRelatorio = new FiltroRelatorioIsencaoCadastroImobiliario();
        filtroRelatorio.inicializarFiltros(facade.getProcessoIsencaoIPTUFacade().getSistemaFacade().getExercicioCorrente());
        valoresPatrimonio = facade.getValoresPossiveisPatrimonio();
    }

    public List<UsuarioSistema> buscarUsuarios(String parte) {
        return facade.getProcessoIsencaoIPTUFacade().getUsuarioSistemaFacade().completaUsuarioSistemaAtivoPeloLogin(parte.trim());
    }

    public List<SelectItem> getOrigensIsencao() {
        return Arrays.stream(OrigemIsencaoIPTU.values()).map((o) -> new SelectItem(o, o.getDescricao()))
            .collect(Collectors.toList());
    }

    public List<ProcessoIsencaoIPTU> buscarProcessos(String parte) {
        try {
            return facade.getProcessoIsencaoIPTUFacade().buscarPorNumeroAndExercicioAndDescricaoCategoria(parte.trim());
        } catch (ExcecaoNegocioGenerica eng) {
            FacesUtil.addAtencao(eng.getMessage());
        } catch (Exception ex) {
            return new ArrayList<>();
        }
        return new ArrayList<>();
    }

    public List<SelectItem> getCategorias() {
        List<SelectItem> retorno = Lists.newArrayList();
        retorno.add(new SelectItem(null, "Todos"));
        if (categorias != null) {
            for (CategoriaIsencaoIPTU cat : categorias) {
                retorno.add(new SelectItem(cat, cat.toString()));
            }
        }
        return retorno;
    }

    public List<SelectItem> getTiposEfetivacao() {
        return Arrays.stream(FiltroRelatorioIsencaoCadastroImobiliario.TipoEfetivacao.values())
            .map((te) -> new SelectItem(te, te.getDescricao()))
            .collect(Collectors.toList());
    }

    public List<SelectItem> getAgrupamentos() {
        return Util.getListSelectItem(AgrupamentoRelatorioDTO.values(), false);
    }

    public List<SelectItem> getTiposDeCategoriasDeIsencao() {
        List<SelectItem> collect = Arrays.stream(TipoCategoriaIsencaoIPTU.values())
            .map((te) -> new SelectItem(te, te.getDescricao()))
            .collect(Collectors.toList());
        collect.add(0, new SelectItem(null, "Todos"));
        return collect;
    }

    public void selecionouTipoCategoria() {
        filtroRelatorio.setCategoria(null);
        categorias = Lists.newArrayList();
        if (filtroRelatorio.getTipoCategoriaIsencaoIPTU() != null) {
            categorias = facade.getProcessoIsencaoIPTUFacade().getCategoriaIsencaoIPTUFacade().buscarCategoriasPorTipoCategoria(filtroRelatorio.getTipoCategoriaIsencaoIPTU());
        } else {
            categorias = facade.getProcessoIsencaoIPTUFacade().getCategoriaIsencaoIPTUFacade().listaDecrescente();
        }
    }

    public List<SelectItem> getSelectItensValorPatrimonio() {
        List<SelectItem> collect = valoresPatrimonio.stream().map((vp) -> new SelectItem(vp, vp.getDescricao()))
            .collect(Collectors.toList());
        collect.add(0, new SelectItem(null, ""));
        return collect;
    }

    public void selecionouOrigemIsencao() {
        filtroRelatorio.setProcesso(null);
        filtroRelatorio.setTipoCategoriaIsencaoIPTU(null);
        categorias = Lists.newArrayList();
        filtroRelatorio.setCategoria(null);
        filtroRelatorio.setTipoEfetivacao(null);
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            filtroRelatorio.validarFiltros();
            List<Setor> setores = facade.buscarSetoresParaFiltrarRelatorio(filtroRelatorio);
            StringBuilder inSetores = new StringBuilder("(");
            if (setores != null && !setores.isEmpty()) {
                for (Setor s : setores) {
                    inSetores.append(s.getId()).append(", ");
                }
                inSetores = new StringBuilder(inSetores.substring(0, inSetores.length() - 2));
            }
            inSetores.append(")");
            facade.montarWhere(dto, filtroRelatorio, inSetores.toString());
            dto.adicionarParametro("agrupamento", filtroRelatorio.getAgrupamentoRelatorio());
            dto.adicionarParametro("origem", filtroRelatorio.getOrigemIsencao());
            dto.adicionarParametro("detalhado", filtroRelatorio.getDetalhado());
            dto.adicionarParametro("USUARIO", facade.getProcessoIsencaoIPTUFacade().getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.adicionarParametro("NOMERELATORIO", "Relatório de Isenção e Imunidade de IPTU");
            dto.adicionarParametro("MODULO", "Tributário");
            dto.adicionarParametro("FILTROS", filtroRelatorio.getCriteriosUtilizados());
            dto.adicionarParametro("FILTROS_EXCEL", filtroRelatorio.getCriteriosUtilizadosExcel());
            dto.setNomeRelatorio("RELATÓRIO-IMOVEIS-COM-ISENÇAO-IPTU");
            dto.setApi("tributario/relatorio-imoveis-isentos/" + (TipoRelatorioDTO.XLS.equals(dto.getTipoRelatorio()) ? "excel/" : ""));
            ReportService.getInstance().gerarRelatorio(facade.getProcessoIsencaoIPTUFacade().getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}
