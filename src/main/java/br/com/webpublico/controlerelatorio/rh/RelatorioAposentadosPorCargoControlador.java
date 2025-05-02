package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.entidades.Cargo;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.rh.relatorio.RelatorioAposentadosPorCargo;
import br.com.webpublico.enums.OperacaoRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.CargoFacade;
import br.com.webpublico.negocios.ModalidadeContratoFPFacade;
import br.com.webpublico.negocios.rh.relatorio.RelatorioAposentadosPorCargoFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.*;

/**
 * Created by William on 03/04/2018.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-aposentados-por-cargo", pattern = "/relatorio/aposentados-por-cargo/", viewId = "/faces/rh/relatorios/relatorioaposentadosporcargo.xhtml")
})
public class RelatorioAposentadosPorCargoControlador extends SuperRelatorioRH implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(RelatorioAposentadosPorCargoControlador.class);
    private static final String JASPER = "RelatorioAposentadosPorCargo.jasper";

    private Boolean todosVinculos;
    private ModalidadeContratoFP modalidadeContratoFP;
    @EJB
    private ModalidadeContratoFPFacade modalidadeContratoFPFacade;
    @EJB
    private RelatorioAposentadosPorCargoFacade relatorioAposentadosPorCargoFacade;
    @EJB
    private CargoFacade cargoFacade;
    private List<Cargo> grupoCargos;
    private Cargo[] cargosSelecionados;
    private String filtro;

    @URLAction(mappingId = "relatorio-aposentados-por-cargo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCampos();
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }

    public Boolean getTodosVinculos() {
        return todosVinculos;
    }

    public void setTodosVinculos(Boolean todosVinculos) {
        this.todosVinculos = todosVinculos;
    }

    public List<Cargo> getGrupoCargos() {
        return grupoCargos;
    }

    public void setGrupoCargos(List<Cargo> grupoCargos) {
        this.grupoCargos = grupoCargos;
    }

    public Cargo[] getCargosSelecionados() {
        return cargosSelecionados;
    }

    public void setCargosSelecionados(Cargo[] cargosSelecionados) {
        this.cargosSelecionados = cargosSelecionados;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (getMes() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Mês é obrigatório.");
        }
        if (getAno() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano é obrigatório.");
        }
        if (cargosSelecionados.length == 0) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione o(s) cargo(s) para gerar o relatório");
        }
        ve.lancarException();
    }

    public void gerarRelatorio() {
        try {
            HashMap parameters = new HashMap();
            validarCampos();
            JRBeanCollectionDataSource bean = new JRBeanCollectionDataSource(buscarDados());
            setGeraNoDialog(true);
            parameters.put("BRASAO", getCaminhoImagem());
            parameters.put("MUNICIPIO", "PREFEITURA MUNICIPAL DE " + getSistemaFacadeSemInjetar().getMunicipio().toUpperCase());
            parameters.put("MODULO", "Recursos Humanos");
            parameters.put("FILTROS", filtro);
            parameters.put("SECRETARIA", "SECRETARIA MUNICIPAL DE ADMINISTRAÇÃO");
            parameters.put("DEPARTAMENTO", "DEPARTAMENTO DE RECURSOS HUMANOS");
            parameters.put("NOMERELATORIO", "RELATÓRIO DE SERVIDORES APOSENTADOS POR CARGO");
            parameters.put("USUARIO", getSistemaFacadeSemInjetar().getUsuarioCorrente().getPessoaFisica().getNome());
            if (getTipoFolhaDePagamento() != null) {
                parameters.put("TIPOFOLHA", getTipoFolhaDePagamento().toString().toUpperCase());
            }
            gerarRelatorioComDadosEmCollection(JASPER, parameters, bean);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private HashSet<Long> getMontaGrupoCargo() {
        HashSet<Long> retorno = new HashSet<>();
        for (Cargo grupo : cargosSelecionados) {
            retorno.add(grupo.getId());
        }
        return retorno;
    }

    private List<RelatorioAposentadosPorCargo> buscarDados() {
        List<ParametrosRelatorios> listaParametros = new ArrayList<>();
        if (getMes() != null) {
            listaParametros.add(new ParametrosRelatorios(" folha.mes ", ":mes", null, OperacaoRelatorio.IGUAL, getMes().getNumeroMesIniciandoEmZero(), null, 0, false));
            filtro = "Mês: " + getMes().getDescricao() + "; ";
        }
        if (getAno() != null) {
            listaParametros.add(new ParametrosRelatorios(" folha.ano ", ":ano", null, OperacaoRelatorio.IGUAL, getAno(), null, 0, false));
            filtro += "Ano: " + getAno() + "; ";
        }
        if (getTipoFolhaDePagamento() != null) {
            listaParametros.add(new ParametrosRelatorios(" folha.tipoFolhaDePagamento ", ":tipoFolha", null, OperacaoRelatorio.IGUAL, getTipoFolhaDePagamento().name(), null, 0, false));
            filtro += "Tipo de Folha: " + getTipoFolhaDePagamento().getDescricao() + "; ";
        }
        if (getVersao() != null) {
            listaParametros.add(new ParametrosRelatorios(" folha.versao ", ":versao", null, OperacaoRelatorio.IGUAL, getVersao(), null, 0, false));
            filtro += "Versão: " + getVersao() + "; ";
        }
        listaParametros.add(new ParametrosRelatorios(" cargo.id ", ":cargo", null, OperacaoRelatorio.IN, getMontaGrupoCargo(), null, 0, false));
        return relatorioAposentadosPorCargoFacade.buscarAposentadosPorCargo(listaParametros);
    }

    public List<SelectItem> getModalidades() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (ModalidadeContratoFP modalidade : modalidadeContratoFPFacade.modalidadesAtivas()) {
            retorno.add(new SelectItem(modalidade, modalidade.toString()));
        }
        return retorno;
    }

    public void carregarListaDeCargosPorModalidade() {
        if (modalidadeContratoFP != null) {
            grupoCargos = cargoFacade.listaFiltrandoModalidade(modalidadeContratoFP, getSistemaFacade().getDataOperacao());
        }
        if (todosVinculos) {
            grupoCargos = cargoFacade.filtraCargosVigentesPorUnidadeOrganizacionalAndUsuario(getSistemaFacade().getDataOperacao());
        }
        Collections.sort(grupoCargos, new Comparator<Cargo>() {
            @Override
            public int compare(Cargo o1, Cargo o2) {
                try {
                    return Integer.valueOf(o1.getCodigoDoCargo()).compareTo(Integer.valueOf(o2.getCodigoDoCargo()));
                } catch (Exception e) {
                    return 0;
                }
            }
        });
    }

    @Override
    public void limparCampos() {
        super.limparCampos();
        grupoCargos = Lists.newArrayList();
        todosVinculos = false;
        modalidadeContratoFP = null;
    }
}
