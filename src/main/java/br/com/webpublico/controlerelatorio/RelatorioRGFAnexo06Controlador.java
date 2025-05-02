package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.ItemDemonstrativoFacade;
import br.com.webpublico.relatoriofacade.RelatorioRGFAnexo06Facade;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by mateus on 27/04/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-rgf-anexo6", pattern = "/relatorio/rgf/anexo6/", viewId = "/faces/financeiro/relatorioslrf/relatoriorgfanexo06.xhtml")
})
public class RelatorioRGFAnexo06Controlador extends AbstractRelatorioItemDemonstrativo implements Serializable {
    @EJB
    private RelatorioRGFAnexo06Facade relatorioRGFAnexo06Facade;
    @EJB
    private ItemDemonstrativoFacade itemDemonstrativoFacade;
    private BigDecimal rcl;
    private BigDecimal rclAjustada;
    private boolean modeloNovo;

    public RelatorioRGFAnexo06Controlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-rgf-anexo6", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        rcl = BigDecimal.ZERO;
        rclAjustada = BigDecimal.ZERO;
        modeloNovo = true;
        portalTipoAnexo = PortalTipoAnexo.ANEXO6_RGF;
        super.limparCampos();
    }

    private Mes getMesInicial() {
        if (Mes.ABRIL.equals(mes)) {
            return Mes.JANEIRO;
        } else if (Mes.AGOSTO.equals(mes)) {
            return Mes.MAIO;
        } else {
            return Mes.SETEMBRO;
        }
    }

    private List<ParametrosRelatorios> criarParametros(Integer local) {
        List<ParametrosRelatorios> parametros = Lists.newArrayList();
        if (esferaDoPoder != null) {
            parametros.add(new ParametrosRelatorios(" vw.esferadopoder ", ":esferaDoPoder", null, OperacaoRelatorio.IGUAL, esferaDoPoder.name(), null, local, false));
        }
        return parametros;
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        RelatorioRGFAnexo05Controlador relatorioRGFAnexo05Controlador = (RelatorioRGFAnexo05Controlador) Util.getControladorPeloNome("relatorioRGFAnexo05Controlador");
        relatorioRGFAnexo05Controlador.limparCampos();
        relatorioRGFAnexo05Controlador.setEsferaDoPoder(esferaDoPoder);
        relatorioRGFAnexo05Controlador.setMes(mes);
        List<ParametrosRelatorios> parametrosAnexo05 = relatorioRGFAnexo05Controlador.montarParametros(relatorioRGFAnexo05Controlador.getData());
        parametrosAnexo05.addAll(criarParametros(2));
        Exercicio exAnterior = getExercicioFacade().getExercicioPorAno(getSistemaFacade().getExercicioCorrente().getAno() - 1);
        Exercicio exAnteriorAnterior = getExercicioFacade().getExercicioPorAno(exAnterior.getAno() - 1);
        RelatoriosItemDemonst relatorioAnexo01 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatorioAnexo01ExAnterior = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 1", exAnterior, TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatorioAnexo02 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 2", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatorioAnexo03RREO = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo03RREOExAnterior = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", exAnterior, TipoRelatorioItemDemonstrativo.RREO);
        RelatoriosItemDemonst relatorioAnexo03RGF = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 3", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatorioAnexo04 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 4", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatorioAnexo05 = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 5", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatoriosItemDemonstDTO = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 6", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.RGF);
        RelatoriosItemDemonst relatoriosItemDemonstDTOExAnterior = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Anexo 6", exAnterior, TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativoComponente> itensAnexo01 = popularConfiguracoesDoRelatorio(getSistemaFacade().getExercicioCorrente(), "Anexo 1", TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativoComponente> itensAnexo01ExAnterior = popularConfiguracoesDoRelatorio(exAnterior, "Anexo 1", TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativoComponente> itensAnexo02 = popularConfiguracoesDoRelatorio(getSistemaFacade().getExercicioCorrente(), "Anexo 2", TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativoComponente> itensAnexo03 = popularConfiguracoesDoRelatorio(getSistemaFacade().getExercicioCorrente(), "Anexo 3", TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativoComponente> itensAnexo04 = popularConfiguracoesDoRelatorio(getSistemaFacade().getExercicioCorrente(), "Anexo 4", TipoRelatorioItemDemonstrativo.RGF);
        List<ItemDemonstrativoComponente> itensAnexo05 = popularConfiguracoesDoRelatorio(getSistemaFacade().getExercicioCorrente(), "Anexo 5", TipoRelatorioItemDemonstrativo.RGF);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MODELO_NOVO", modeloNovo);
        dto.adicionarParametro("DATAINICIAL", getMesInicial().getDescricao().toUpperCase());
        dto.adicionarParametro("DATAFINAL", " " + mes.getDescricao().toUpperCase() + " DE " + getSistemaFacade().getExercicioCorrente().getAno());
        if (EsferaDoPoder.EXECUTIVO.equals(esferaDoPoder)) {
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER EXECUTIVO");
            dto.adicionarParametro("NOME_RELATORIO", "DEMONSTRATIVO SIMPLIFICADO DO RELATÓRIO DE GESTÃO FISCAL");
        } else if (EsferaDoPoder.LEGISLATIVO.equals(esferaDoPoder)) {
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC - PODER LEGISLATIVO");
            dto.adicionarParametro("NOME_RELATORIO", "DEMONSTRATIVO SIMPLIFICADO DO RELATÓRIO DE GESTÃO FISCAL");
        } else {
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("NOME_RELATORIO", "DEMONSTRATIVO CONSOLIDADO SIMPLIFICADO DO RELATÓRIO DE GESTÃO FISCAL");
        }
        dto.adicionarParametro("ID_EXERCICIO", getSistemaFacade().getExercicioCorrente().getId());
        dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR", exAnterior.getId());
        dto.adicionarParametro("ANO_EXERCICIO_ANTERIOR", exAnterior.getAno());
        dto.adicionarParametro("ID_EXERCICIO_ANTERIOR_ANTERIOR", exAnteriorAnterior.getId());
        dto.adicionarParametro("esferaDoPoder", esferaDoPoder != null ? esferaDoPoder.name() : "");
        dto.adicionarParametro("mesFinal", mes.getToDto());
        dto.adicionarParametro("parametrosRelatorio", ParametrosRelatorios.parametrosToDto(criarParametros(1)));
        dto.adicionarParametro("parametrosAnexo05", ParametrosRelatorios.parametrosToDto(parametrosAnexo05));
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("itensAnexo01", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo01));
        dto.adicionarParametro("itensAnexo01ExAnterior", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo01ExAnterior));
        dto.adicionarParametro("itensAnexo02", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo02));
        dto.adicionarParametro("itensAnexo03", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo03));
        dto.adicionarParametro("itensAnexo04", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo04));
        dto.adicionarParametro("itensAnexo05", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itensAnexo05));
        if (relatorioAnexo01 != null)
            dto.adicionarParametro("relatorioAnexo01", relatorioAnexo01.toDto());
        if (relatorioAnexo01ExAnterior != null)
            dto.adicionarParametro("relatorioAnexo01ExAnterior", relatorioAnexo01ExAnterior.toDto());
        if (relatorioAnexo02 != null)
            dto.adicionarParametro("relatorioAnexo02", relatorioAnexo02.toDto());
        if (relatorioAnexo03RGF != null)
            dto.adicionarParametro("relatorioAnexo03RGF", relatorioAnexo03RGF.toDto());
        if (relatorioAnexo03RREO != null)
            dto.adicionarParametro("relatorioAnexo03", relatorioAnexo03RREO.toDto());
        if (relatorioAnexo03RREOExAnterior != null)
            dto.adicionarParametro("relatorioAnexo03ExAnterior", relatorioAnexo03RREOExAnterior.toDto());
        if (relatorioAnexo04 != null)
            dto.adicionarParametro("relatorioAnexo04", relatorioAnexo04.toDto());
        if (relatorioAnexo05 != null)
            dto.adicionarParametro("relatorioAnexo05", relatorioAnexo05.toDto());
        if (relatoriosItemDemonstDTO != null)
            dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonstDTO.toDto());
        if (relatoriosItemDemonstDTO != null)
            dto.adicionarParametro("relatoriosItemDemonstDTOExAnterior", relatoriosItemDemonstDTOExAnterior.toDto());
        dto.setNomeRelatorio("LRF-RGF-ANEXO-6-" + getSistemaFacade().getExercicioCorrente().getAno() + "-" + mes.getNumeroMesString() + "-" + (esferaDoPoder != null ? esferaDoPoder.getDescricao().substring(0, 1) : "C"));
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioRGFAnexo06";
    }

    @Override
    public String getApi() {
        return "contabil/rgf-anexo6/";
    }

    private List<ItemDemonstrativoComponente> popularConfiguracoesDoRelatorio(Exercicio exercicio, String anexo, TipoRelatorioItemDemonstrativo tipo) {
        List<ItemDemonstrativoComponente> itens = Lists.newArrayList();
        List<ItemDemonstrativo> itensDemonstrativos = itemDemonstrativoFacade.buscarItensPorExercicioAndRelatorio(exercicio, "", anexo, tipo);
        for (ItemDemonstrativo itemDemonstrativo : itensDemonstrativos) {
            itens.add(new ItemDemonstrativoComponente(itemDemonstrativo));
        }
        return itens;
    }

    public BigDecimal getRcl() {
        return rcl;
    }

    public void setRcl(BigDecimal rcl) {
        this.rcl = rcl;
    }

    public BigDecimal getRclAjustada() {
        return rclAjustada;
    }

    public void setRclAjustada(BigDecimal rclAjustada) {
        this.rclAjustada = rclAjustada;
    }
}
