package br.com.webpublico.controle;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.PPA;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.LogoRelatorio;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.PPAFacade;
import br.com.webpublico.negocios.RelatoriosItemDemonstFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 29/08/13
 * Time: 16:46
 * To change this template use File | Settings | File Templates.
 */
@ViewScoped
@ManagedBean(name = "relatorioAnexo3ReceitaControlador")
@URLMappings(mappings = {
        @URLMapping(id = "relatorioAnexo3Receita", pattern = "/relatorios/anexo-3-receita/", viewId = "/faces/financeiro/relatorio/relatorioanexotresreceitappa.xhtml")
})
public class RelatorioAnexo3ReceitaControlador extends AbstractReport implements Serializable {

    @EJB
    private PPAFacade ppaFacade;
    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;
    private PPA ppa;
    private List<ItemDemonstrativoComponente> itens;
    private LogoRelatorio logoRelatorio;
    private RelatoriosItemDemonst relatoriosItemDemonst;

    public RelatorioAnexo3ReceitaControlador() {
        itens = Lists.newArrayList();
    }

    public List<SelectItem> getLogosRelatorios() {
        return Util.getListSelectItemSemCampoVazio(LogoRelatorio.values());
    }

    public List<PPA> completaPPA(String parte) {
        return ppaFacade.listaFiltrandoPPA(parte.trim());
    }

    @URLAction(mappingId = "relatorioAnexo3Receita", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        logoRelatorio = LogoRelatorio.PREFEITURA;
        ppa = null;
        relatoriosItemDemonst = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Anexo 3 - Receita", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.PPA);
    }

    public void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (ppa == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo PPA deve ser informado.");
        }
        if (relatoriosItemDemonst == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Nenhum Item Demonstrativo encontrado para o Relatório 'Anexo 3 - Receita'.");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            List<Exercicio> exercicios = ppaFacade.listaPpaEx(ppa);
            Collections.sort(exercicios, new Comparator<Exercicio>() {
                @Override
                public int compare(Exercicio o1, Exercicio o2) {
                    return o1.getAno().compareTo(o2.getAno());
                }
            });

            RelatorioDTO dto = new RelatorioDTO();
            boolean isCamara = LogoRelatorio.CAMARA.equals(logoRelatorio);
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome());
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", isCamara ? "CÂMARA MUNICIPAL DE RIO BRANCO" : "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("PPA_ID", ppa.getId());
            dto.adicionarParametro("PPA_DESC", ppa.getDescricao());
            dto.adicionarParametro("MODULO", "Planejamento Público");
            dto.adicionarParametro("EXERC_1", exercicios.get(0).getAno());
            dto.adicionarParametro("EXERC_2", exercicios.get(1).getAno());
            dto.adicionarParametro("EXERC_3", exercicios.get(2).getAno());
            dto.adicionarParametro("EXERC_4", exercicios.get(3).getAno());
            dto.adicionarParametro("isCamara", isCamara);
            dto.adicionarParametro("relatoriosItemDemonst", relatoriosItemDemonst.toDto());
            dto.adicionarParametro("idExercicioCorrente", getSistemaFacade().getExercicioCorrente().getId());
            dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
            dto.setNomeRelatorio("Relatório Anexo 3 - Receita PPA");
            dto.setApi("contabil/anexo3-receita/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public PPA getPpa() {
        return ppa;
    }

    public void setPpa(PPA ppa) {
        this.ppa = ppa;
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public LogoRelatorio getLogoRelatorio() {
        return logoRelatorio;
    }

    public void setLogoRelatorio(LogoRelatorio logoRelatorio) {
        this.logoRelatorio = logoRelatorio;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }
}
