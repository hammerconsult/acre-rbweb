package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.regularizacaoconstrucao.ResponsavelServico;
import br.com.webpublico.enums.tributario.regularizacaoconstrucao.TipoConstrucao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoRelatorioHabiteseConstrucao", pattern = "/relatorio/habitese-contrucao/", viewId = "/faces/tributario/habiteseconstrucao/relatorios/relatorio.xhtml")
})
public class RelatorioHabiteseContrucaoControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    private Filtro filtro;

    public Filtro getFiltro() {
        return filtro;
    }

    public void setFiltro(Filtro filtro) {
        this.filtro = filtro;
    }

    @URLAction(mappingId = "novoRelatorioHabiteseConstrucao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        filtro = new Filtro();
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            filtro.validarRegras();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.setNomeRelatorio("Relatório de Habite-se");
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_RELATORIO", "Relatório de Habite-se");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
            dto.adicionarParametro("MODULO", "TRIBUTÁRIO");
            dto.adicionarParametro("FILTRO", filtro);
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getPessoaFisica().getNome());
            dto.setApi("tributario/habitese-construcao/");
            ReportService.getInstance().gerarRelatorio(Util.getSistemaControlador().getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    public List<SelectItem> getTiposConstrucoes() {
        return Util.getListSelectItemSemCampoVazio(TipoConstrucao.values(), true);
    }

    public List<SelectItem> getTiposHabiteses() {
        return Util.getListSelectItem(CaracteristicaConstrucaoHabitese.TipoHabitese.values(), true);
    }

    public class Filtro {
        private Integer codigoProcesso;
        private Integer exercicioProcesso;
        private String cpfCnpjRequerente;
        private String nomeRequerente;
        private String inscricaoCadastral;
        private String bairro;
        private String logradouro;
        private TipoConstrucao tipoConstrucao;
        private BigDecimal areaInicial;
        private BigDecimal areaFinal;
        private Long codigoHabiteseInicial;
        private Long codigoHabiteseFinal;
        private CaracteristicaConstrucaoHabitese.TipoHabitese tipoHabitese;
        private Date dataLancamentoCalculoInicial;
        private Date dataLancamentoCalculoFinal;
        private Date dataExpedicaoInicial;
        private Date dataExpedicaoFinal;
        private Date dataVencimentoInicial;
        private Date dataVencimentoFinal;
        private Date dataVencimentoDebitoInicial;
        private Date dataVencimentoDebitoFinal;
        private String cpfCnpjResponsavelServico;
        private String nomeResponsavelServico;
        private String CEI;

        public Integer getCodigoProcesso() {
            return codigoProcesso;
        }

        public void setCodigoProcesso(Integer codigoProcesso) {
            this.codigoProcesso = codigoProcesso;
        }

        public Integer getExercicioProcesso() {
            return exercicioProcesso;
        }

        public void setExercicioProcesso(Integer exercicioProcesso) {
            this.exercicioProcesso = exercicioProcesso;
        }

        public String getCpfCnpjRequerente() {
            return cpfCnpjRequerente;
        }

        public void setCpfCnpjRequerente(String cpfCnpjRequerente) {
            this.cpfCnpjRequerente = cpfCnpjRequerente;
        }

        public String getNomeRequerente() {
            return nomeRequerente;
        }

        public void setNomeRequerente(String nomeRequerente) {
            this.nomeRequerente = nomeRequerente;
        }

        public String getInscricaoCadastral() {
            return inscricaoCadastral;
        }

        public void setInscricaoCadastral(String inscricaoCadastral) {
            this.inscricaoCadastral = inscricaoCadastral;
        }

        public String getBairro() {
            return bairro;
        }

        public void setBairro(String bairro) {
            this.bairro = bairro;
        }

        public String getLogradouro() {
            return logradouro;
        }

        public void setLogradouro(String logradouro) {
            this.logradouro = logradouro;
        }

        public TipoConstrucao getTipoConstrucao() {
            return tipoConstrucao;
        }

        public void setTipoConstrucao(TipoConstrucao tipoConstrucao) {
            this.tipoConstrucao = tipoConstrucao;
        }

        public BigDecimal getAreaInicial() {
            return areaInicial;
        }

        public void setAreaInicial(BigDecimal areaInicial) {
            this.areaInicial = areaInicial;
        }

        public BigDecimal getAreaFinal() {
            return areaFinal;
        }

        public void setAreaFinal(BigDecimal areaFinal) {
            this.areaFinal = areaFinal;
        }

        public Long getCodigoHabiteseInicial() {
            return codigoHabiteseInicial;
        }

        public void setCodigoHabiteseInicial(Long codigoHabiteseInicial) {
            this.codigoHabiteseInicial = codigoHabiteseInicial;
        }

        public Long getCodigoHabiteseFinal() {
            return codigoHabiteseFinal;
        }

        public void setCodigoHabiteseFinal(Long codigoHabiteseFinal) {
            this.codigoHabiteseFinal = codigoHabiteseFinal;
        }

        public CaracteristicaConstrucaoHabitese.TipoHabitese getTipoHabitese() {
            return tipoHabitese;
        }

        public void setTipoHabitese(CaracteristicaConstrucaoHabitese.TipoHabitese tipoHabitese) {
            this.tipoHabitese = tipoHabitese;
        }

        public Date getDataLancamentoCalculoInicial() {
            return dataLancamentoCalculoInicial;
        }

        public void setDataLancamentoCalculoInicial(Date dataLancamentoCalculoInicial) {
            this.dataLancamentoCalculoInicial = dataLancamentoCalculoInicial;
        }

        public Date getDataLancamentoCalculoFinal() {
            return dataLancamentoCalculoFinal;
        }

        public void setDataLancamentoCalculoFinal(Date dataLancamentoCalculoFinal) {
            this.dataLancamentoCalculoFinal = dataLancamentoCalculoFinal;
        }

        public Date getDataExpedicaoInicial() {
            return dataExpedicaoInicial;
        }

        public void setDataExpedicaoInicial(Date dataExpedicaoInicial) {
            this.dataExpedicaoInicial = dataExpedicaoInicial;
        }

        public Date getDataExpedicaoFinal() {
            return dataExpedicaoFinal;
        }

        public void setDataExpedicaoFinal(Date dataExpedicaoFinal) {
            this.dataExpedicaoFinal = dataExpedicaoFinal;
        }

        public Date getDataVencimentoInicial() {
            return dataVencimentoInicial;
        }

        public void setDataVencimentoInicial(Date dataVencimentoInicial) {
            this.dataVencimentoInicial = dataVencimentoInicial;
        }

        public Date getDataVencimentoFinal() {
            return dataVencimentoFinal;
        }

        public void setDataVencimentoFinal(Date dataVencimentoFinal) {
            this.dataVencimentoFinal = dataVencimentoFinal;
        }

        public Date getDataVencimentoDebitoInicial() {
            return dataVencimentoDebitoInicial;
        }

        public void setDataVencimentoDebitoInicial(Date dataVencimentoDebitoInicial) {
            this.dataVencimentoDebitoInicial = dataVencimentoDebitoInicial;
        }

        public Date getDataVencimentoDebitoFinal() {
            return dataVencimentoDebitoFinal;
        }

        public void setDataVencimentoDebitoFinal(Date dataVencimentoDebitoFinal) {
            this.dataVencimentoDebitoFinal = dataVencimentoDebitoFinal;
        }

        public String getCpfCnpjResponsavelServico() {
            return cpfCnpjResponsavelServico;
        }

        public void setCpfCnpjResponsavelServico(String cpfCnpjResponsavelServico) {
            this.cpfCnpjResponsavelServico = cpfCnpjResponsavelServico;
        }

        public String getNomeResponsavelServico() {
            return nomeResponsavelServico;
        }

        public void setNomeResponsavelServico(String nomeResponsavelServico) {
            this.nomeResponsavelServico = nomeResponsavelServico;
        }

        public String getCEI() {
            return CEI;
        }

        public void setCEI(String CEI) {
            this.CEI = CEI;
        }

        private void validarRegras() {
            ValidacaoException ve = new ValidacaoException();
            if (!Util.validaInicialFinal(areaInicial, areaFinal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Á área inicial e final não é valida, informe os dois campos sendo a área inicial ou igual menor que a final");
            }
            if (!Util.validaInicialFinal(codigoHabiteseInicial, codigoHabiteseFinal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O código do alvará não é valido, informe os dois campos sendo o código inicial menor ou igual que o final");
            }
            if (!Util.validaInicialFinal(dataLancamentoCalculoInicial, dataLancamentoCalculoFinal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de lançamento do calculo não é valida, informe os dois campos sendo a data inicial menor ou igual que a final");
            }
            if (!Util.validaInicialFinal(dataExpedicaoInicial, dataExpedicaoFinal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de expedição do alvara não é valida, informe os dois campos sendo a data inicial menor ou igual que a final");
            }
            if (!Util.validaInicialFinal(dataVencimentoInicial, dataVencimentoFinal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de vencimento do alvara não é valida, informe os dois campos sendo a data inicial menor ou igual que a final");
            }
            if (!Util.validaInicialFinal(dataVencimentoDebitoInicial, dataVencimentoDebitoFinal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data de vencimento do débito não é valida, informe os dois campos sendo a data inicial menor ou igual que a final");
            }
            ve.lancarException();
        }
    }


}
