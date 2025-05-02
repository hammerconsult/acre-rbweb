package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.Operador;
import br.com.webpublico.enums.Perecibilidade;
import br.com.webpublico.enums.StatusMaterial;
import br.com.webpublico.enums.TipoConsulta;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ConsultaLocalEstoqueFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.administrativo.ConsultaLocalEstoqueDTO;
import br.com.webpublico.webreportdto.dto.administrativo.ConsultaLocalEstoqueLoteDTO;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "consultaLocalEstoque", pattern = "/local-de-estoque/consulta/", viewId = "/faces/administrativo/materiais/consulta/localestoque/consulta.xhtml")
})

public class ConsultaLocalEstoqueControlador implements Serializable {

    @EJB
    private ConsultaLocalEstoqueFacade facade;
    private ConsultaLocalEstoqueFiltro filtro;
    private ConsultaLocalEstoqueMaterial consultaLocalEstoqueMaterial;
    private List<ConsultaLocalEstoque> locaisEstoque;
    private List<HierarquiaOrganizacional> hierarquiasOrcamentarias;

    @URLAction(mappingId = "consultaLocalEstoque", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void consulta() {
        filtro = new ConsultaLocalEstoqueFiltro();
        filtro.setUsuarioSistema(facade.getSistemaFacade().getUsuarioCorrente());
        filtro.setDataOperacao(facade.getSistemaFacade().getDataOperacao());
        filtro.setDataReferencia(facade.getSistemaFacade().getDataOperacao());
        limparCampos();
    }

    public void limparCampos() {
        locaisEstoque = Lists.newArrayList();
        hierarquiasOrcamentarias = Lists.newArrayList();
        if (filtro != null && filtro.getLocalEstoque() != null) {
            hierarquiasOrcamentarias = facade.getHierarquiaOrganizacionalFacade().retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(filtro.getLocalEstoque().getUnidadeOrganizacional(), facade.getSistemaFacade().getDataOperacao());
        }
    }

    public boolean hasLocaisEstoquePesquisa() {
        return locaisEstoque != null && !locaisEstoque.isEmpty();
    }

    public void consultar() {
        try {
            validarPesquisa();
            atribuirUnidadesOrcamentariasFiltro();
            locaisEstoque = facade.buscarLocaisEstoque(filtro);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void selecionarMaterial(ConsultaLocalEstoqueMaterial mat) {
        consultaLocalEstoqueMaterial = mat;
    }

    public void selecionarMaterialBuscandoOrigemLocalEstoque(ConsultaLocalEstoqueMaterial mat) {
        consultaLocalEstoqueMaterial = mat;

        ConsultaLocalEstoqueFiltro filtroMov = new ConsultaLocalEstoqueFiltro();
        filtroMov.setDataReferencia(filtro.getDataReferencia());
        filtroMov.setLocalEstoque(filtro.getLocalEstoque());
        filtroMov.setIdMaterial(mat.getIdMaterial());
        filtroMov.setOrcamentaria(filtro.getOrcamentaria());

        MovimentacaoGrupoMaterial movGrupo = facade.getMovimentoGrupoMaterial(filtroMov);
        consultaLocalEstoqueMaterial.setMovimentacaoGrupoMaterial(movGrupo);
    }

    public void selecionarMaterialBuscandoOrigemLocalEstoqueOrcamentario(ConsultaLocalEstoqueMaterial mat, ConsultaLocalEstoqueOrcamentario consultaLocalEstoqueOrcamentario) {
        consultaLocalEstoqueMaterial = mat;

        ConsultaLocalEstoqueFiltro filtroMov = new ConsultaLocalEstoqueFiltro();
        filtroMov.setDataReferencia(filtro.getDataReferencia());
        filtroMov.setLocalEstoque(filtro.getLocalEstoque());
        filtroMov.setIdMaterial(mat.getIdMaterial());
        filtroMov.setOrcamentaria(consultaLocalEstoqueOrcamentario.getHierarquiaOrganizacional().getSubordinada());

        MovimentacaoGrupoMaterial movGrupo = facade.getMovimentoGrupoMaterial(filtroMov);
        consultaLocalEstoqueMaterial.setMovimentacaoGrupoMaterial(movGrupo);
    }

    private void validarPesquisa() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getLocalEstoque() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo local de estoque deve ser informado.");
        }
        if (filtro.getDataReferencia() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Referência deve ser informado.");
        }
        ve.lancarException();
    }

    public List<LocalEstoque> completarLocalEstoque(String s) {
        return facade.getLocalEstoqueFacade().buscarPorCodigoOrDescricao(s.trim());
    }

    public List<Material> completaMaterialDoLocalEstoque(String parte) {
        List<Material> lista = new ArrayList<>();
        if (filtro.getLocalEstoque() != null) {
            lista = facade.buscarMateriaisHierarquiaLocalEstoque(filtro, filtro.getLocalEstoque(), parte);
            if (lista == null || lista.isEmpty()) {
                FacesUtil.addAtencao("Não foram encontrados materias.");
            }
        } else {
            FacesUtil.addCampoObrigatorio("Selecione o local de estoque.");
        }
        return lista;
    }

    public List<SelectItem> getHierarquiasOrcamentarias() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todas"));
        if (hierarquiasOrcamentarias !=null && !hierarquiasOrcamentarias.isEmpty()) {
            for (HierarquiaOrganizacional obj : hierarquiasOrcamentarias) {
                toReturn.add(new SelectItem(obj.getSubordinada(), obj.toString()));
            }
        }
        return toReturn;
    }

    public String getCaminhoOrigemReservaEstoque(ReservaEstoque reservaEstoque){
        if (reservaEstoque.isReservaRequisicaoMaterial()){
            return "/avaliacao-de-requisicao-de-material/ver/" + reservaEstoque.getIdOrigemReserva() + "/";
        }
        return "/entrada-por-compra/ver/" + reservaEstoque.getIdOrigemReserva() + "/";
    }


    public void atribuirUnidadesOrcamentariasFiltro() {
        filtro.setUnidadesOrcamentarias(new ArrayList<>());

        if (filtro.getOrcamentaria() != null) {
            filtro.getUnidadesOrcamentarias().add(filtro.getOrcamentaria());
        } else {
            for (HierarquiaOrganizacional ho : hierarquiasOrcamentarias) {
                filtro.getUnidadesOrcamentarias().add(ho.getSubordinada());
            }
        }
    }

    public List<SelectItem> getTiposConsulta() {
       return Util.getListSelectItemSemCampoVazio(TipoConsulta.values(), false);
    }

    public List<SelectItem> getPerecibilidades() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todas"));
        for (Perecibilidade value : Perecibilidade.values()) {
            toReturn.add(new SelectItem(value, value.getDescricao()));
        }
        return toReturn;
    }

    public void addGrupoMaterial() {
        try {
            validarGruposMateriais();
            if (filtro.getGruposMateriais() == null) {
                filtro.setGruposMateriais(Lists.newArrayList());
            }
            filtro.getGruposMateriais().add(filtro.getGrupoMaterial());
            filtro.setGrupoMaterial(null);
            FacesUtil.atualizarComponente("Formulario:pn-grupos");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    public void removeGrupoMaterial(GrupoMaterial grupo) {
        filtro.getGruposMateriais().remove(grupo);
        FacesUtil.atualizarComponente("Formulario:pn-grupos");
    }

    private void validarGruposMateriais() {
        ValidacaoException ve = new ValidacaoException();
        if (filtro.getGruposMateriais() != null && filtro.getGrupoMaterial() != null && filtro.getGruposMateriais().contains(filtro.getGrupoMaterial())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Grupo Material já adicionado!");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("administrativo/consulta-local-estoque/");
            ReportService.getInstance().gerarRelatorio(facade.getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void montarDtoSemApi(RelatorioDTO dto) {
        List<ConsultaLocalEstoqueDTO> dtos = Lists.newArrayList();
        if (filtro.getTipoConsulta().isConsultaSintetica()) {
            for (ConsultaLocalEstoque local : locaisEstoque) {
                for (ConsultaLocalEstoqueMaterial material : local.getMateriais()) {
                    ConsultaLocalEstoqueDTO consultaDTO = new ConsultaLocalEstoqueDTO();
                    consultaDTO.setMaterial(material.toString());
                    consultaDTO.setUnidade(material.getUnidadeMedida());
                    consultaDTO.setGrupoMaterial(material.getGrupoMaterial());
                    consultaDTO.setQuantidadeEstoque(material.getQuantidadeEstoqueFormatada());
                    consultaDTO.setQuantidadeReservada(material.getQuantidadeReservadaFormatada());
                    consultaDTO.setQuantidadeDisponivel(material.getQuantidadeDisponivelFormatada());
                    consultaDTO.setValorUnitario(material.getValorUnitarioFormatado());
                    consultaDTO.setValorTotal(material.getValorEstoque());
                    dtos.add(consultaDTO);
                }
            }
        } else {
            for (ConsultaLocalEstoque consultaLocalEstoque : locaisEstoque) {
                for (ConsultaLocalEstoqueOrcamentario orc : consultaLocalEstoque.getOrcamentarias()) {
                    for (ConsultaLocalEstoqueMaterial material : orc.getMateriais()) {
                        ConsultaLocalEstoqueDTO consultaDTO = new ConsultaLocalEstoqueDTO();
                        consultaDTO.setMaterial(material.getCodigo() + " - " + material.getDescricao());
                        consultaDTO.setUnidade(material.getUnidadeMedida());
                        consultaDTO.setGrupoMaterial(material.getGrupoMaterial());
                        consultaDTO.setMedicoHospitalar(material.getMedicoHospitalar());
                        consultaDTO.setControleDeLote(material.getControleLote());
                        consultaDTO.setQuantidadeDisponivel(material.getQuantidadeDisponivelFormatada());
                        consultaDTO.setQuantidadeEstoque(material.getQuantidadeEstoqueFormatada());
                        consultaDTO.setQuantidadeReservada(material.getQuantidadeReservadaFormatada());
                        consultaDTO.setValorUnitario(material.getValorUnitarioFormatado());
                        consultaDTO.setValorTotal(material.getValorEstoque());
                        if (consultaDTO.getControleDeLote()) {
                            for (ConsultaLocalEstoqueLoteMaterial lote : material.getLotes()) {
                                ConsultaLocalEstoqueLoteDTO loteDto = new ConsultaLocalEstoqueLoteDTO();
                                loteDto.setDescricao(lote.getIdentificacao());
                                loteDto.setValidade(lote.getValidade());
                                loteDto.setQuantidade(lote.getQuantidadeFormatada());
                                consultaDTO.getLotes().add(loteDto);
                            }
                        }
                        dtos.add(consultaDTO);
                    }
                }

            }
        }
        dto.adicionarParametro("itens", dtos);
        dto.adicionarParametro("consolidado", filtro.getTipoConsulta().isConsultaSintetica());
        dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco");
        dto.adicionarParametro("USUARIO", facade.getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.adicionarParametro("FILTRO", "Local de estoque: " + filtro.getLocalEstoque().toString());
        dto.setNomeRelatorio("Relatório da Consulta de Local de Estoque");
    }

    public StreamedContent gerarExcel() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            montarDtoSemApi(dto);
            dto.setApi("administrativo/consulta-local-estoque/excel/");
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<RelatorioDTO> request = new HttpEntity<>(dto, headers);
            ConfiguracaoDeRelatorio configuracao = facade.getConfiguracaoDeRelatorioFacade().getConfiguracaoPorChave();
            ResponseEntity<byte[]> responseEntity = new RestTemplate().exchange(configuracao.getUrl() + dto.getApi() + "gerar", HttpMethod.POST, request, byte[].class);
            byte[] bytes = responseEntity.getBody();
            InputStream stream = new ByteArrayInputStream(bytes);
            return new DefaultStreamedContent(stream, "application/xls", "CONSULTA_LOCAL_ESTOQUE" + ".xls");
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
        return null;
    }

    public ConsultaLocalEstoqueFiltro getFiltro() {
        return filtro;
    }

    public void setFiltro(ConsultaLocalEstoqueFiltro filtro) {
        this.filtro = filtro;
    }

    public List<ConsultaLocalEstoque> getLocaisEstoque() {
        return locaisEstoque;
    }

    public void setLocaisEstoque(List<ConsultaLocalEstoque> locaisEstoque) {
        this.locaisEstoque = locaisEstoque;
    }

    public ConsultaLocalEstoqueMaterial getConsultaLocalEstoqueMaterial() {
        return consultaLocalEstoqueMaterial;
    }

    public void setConsultaLocalEstoqueMaterial(ConsultaLocalEstoqueMaterial consultaLocalEstoqueMaterial) {
        this.consultaLocalEstoqueMaterial = consultaLocalEstoqueMaterial;
    }

    public List<SelectItem> getStatusMateriais() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todos"));
        toReturn.add(new SelectItem(StatusMaterial.DEFERIDO, StatusMaterial.DEFERIDO.getDescricao()));
        toReturn.add(new SelectItem(StatusMaterial.INDEFERIDO, StatusMaterial.INDEFERIDO.getDescricao()));
        toReturn.add(new SelectItem(StatusMaterial.INATIVO, StatusMaterial.INATIVO.getDescricao()));
        return toReturn;
    }

    public List<SelectItem> getTiposComparacao() {
        return Util.getListSelectItem(Operador.getOperadoresRelacionais(), false);
    }

    public void listenerPreRender() {
        filtro.setTipoComparacao(Operador.MAIOR);
        filtro.setQuantidade(BigDecimal.ZERO);
    }
}
