/*
 * Codigo gerado automaticamente em Fri Nov 25 14:51:04 BRST 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.interfaces.EntidadePagavelRH;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.EntidadeMetaData;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.convert.Converter;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "provimentoFPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoProvimentoFP", pattern = "/provimento/listar/", viewId = "/faces/rh/administracaodepagamento/consultaprovimentoservidor/lista.xhtml"),
        @URLMapping(id = "verProvimentoFP", pattern = "/provimento/ver/#{provimentoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/consultaprovimentoservidor/visualizar.xhtml")
})
public class ProvimentoFPControlador extends PrettyControlador<ProvimentoFP> implements Serializable, CRUD {

    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ProvimentoFPFacade provimentoFPFacade;
    @EJB
    private SituacaoFuncionalFacade situacaoFuncionalFacade;
    @EJB
    private PrevidenciaVinculoFPFacade previdenciaVinculoFPFacade;
    @EJB
    private EnquadramentoFuncionalFacade enquadramentoFuncionalFacade;
    @EJB
    private LotacaoFuncionalFacade lotacaoFuncionalFacade;
    @EJB
    private CargoConfiancaFacade cargoConfiancaFacade;
    @EJB
    private FuncaoGratificadaFacade funcaoGratificadaFacade;
    @EJB
    private ExoneracaoRescisaoFacade exoneracaoRescisaoFacade;
    @EJB
    private AposentadoriaFacade aposentadoriaFacade;
    @EJB
    private ContratoVinculoDeContratoFacade contratoVinculoDeContratoFacade;
    @EJB
    private RecursoDoVinculoFPFacade lotacaoFolhaExercicioFacade;
    @EJB
    private VinculoDeContratoFPFacade vinculoDeContratoFPFacade;
    @EJB
    private EnquadramentoFGFacade enquadramentoFGFacade;
    private ConverterAutoComplete converterVinculoFP;
    private VinculoFP vinculoFP;
    private EntidadeMetaData contratoMetaData;
    private Aposentadoria aposentadoria;
    private List<SituacaoContratoFP> situacaoFuncional;
    private List<PrevidenciaVinculoFP> previdenciaVinculoFP;
    private List<EnquadramentoFuncional> enquadramentoFuncional;
    private List<LotacaoFuncional> lotacaoFuncional;
    private List<CargoConfianca> cargoConfianca;
    private List<FuncaoGratificada> funcoesGratificadas;
    private List<ExoneracaoRescisao> exoneracaoRescisao;
    private List<ContratoVinculoDeContrato> contratoVinculoDeContrato;
    private List<RecursoDoVinculoFP> lotacaoFolhaExercicio;
    private RecursoDoVinculoFP lotacaoFolhaExercicioVigente;
    private final Integer CODIGO_NOMEACAO = 1;
    private final Integer CODIGO_ACESSO_CARGO_COMISSAO = 11;
    private final Integer CODIGO_FUNCAO_GRATIFICADA = 21;
    private CargoConfianca cargoConfiancaVigente;
    private EnquadramentoFG enquadramentoFGVigente;

    public ProvimentoFPControlador() {
        super(ProvimentoFP.class);
        contratoMetaData = new EntidadeMetaData(ContratoFP.class);
    }

    @Override
    public ProvimentoFPFacade getFacede() {
        return provimentoFPFacade;
    }

    public EntidadeMetaData getContratoMetaData() {
        return contratoMetaData;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public ContratoFPFacade getContratoFPFacade() {
        return contratoFPFacade;
    }

    public Converter getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public List<VinculoFP> completaVinculoFP(String parte) {
        return contratoFPFacade.buscaContratoFiltrandoAtributosMatriculaFP(parte.trim());
        //return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFP(parte.trim());
    }

    public List<ProvimentoFP> getListaProvimentos() {
        if (vinculoFP != null) {
            return provimentoFPFacade.recuperaProvimentos(vinculoFP.getMatriculaFP().getMatricula(), vinculoFP.getNumero());

        } else {
            return null;
        }
    }

    public void limpaCampos() {
        //contratoFP = null;
    }

    public ProvimentoFPFacade getProvimentoFPFacade() {
        return provimentoFPFacade;
    }

    public void setProvimentoFPFacade(ProvimentoFPFacade provimentoFPFacade) {
        this.provimentoFPFacade = provimentoFPFacade;
    }

    public List<SituacaoContratoFP> getSituacaoFuncional() {
        return situacaoFuncional;
    }

    public void setSituacaoFuncional(List<SituacaoContratoFP> situacaoFuncional) {
        this.situacaoFuncional = situacaoFuncional;
    }

    public List<PrevidenciaVinculoFP> getPrevidenciaVinculoFP() {
        return previdenciaVinculoFP;
    }

    public void setPrevidenciaVinculoFP(List<PrevidenciaVinculoFP> previdenciaVinculoFP) {
        this.previdenciaVinculoFP = previdenciaVinculoFP;
    }

    public List<EnquadramentoFuncional> getEnquadramentoFuncional() {
        return enquadramentoFuncional;
    }

    public void setEnquadramentoFuncional(List<EnquadramentoFuncional> enquadramentoFuncional) {
        this.enquadramentoFuncional = enquadramentoFuncional;
    }

    public List<LotacaoFuncional> getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(List<LotacaoFuncional> lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public List<CargoConfianca> getCargoConfianca() {
        return cargoConfianca;
    }

    public void setCargoConfianca(List<CargoConfianca> cargoConfianca) {
        this.cargoConfianca = cargoConfianca;
    }

    public List<FuncaoGratificada> getFuncoesGratificadas() {
        return funcoesGratificadas;
    }

    public void setFuncoesGratificadas(List<FuncaoGratificada> funcoesGratificadas) {
        this.funcoesGratificadas = funcoesGratificadas;
    }

    public List<ExoneracaoRescisao> getExoneracaoRescisao() {
        return exoneracaoRescisao;
    }

    public void setExoneracaoRescisao(List<ExoneracaoRescisao> exoneracaoRescisao) {
        this.exoneracaoRescisao = exoneracaoRescisao;
    }

    public Aposentadoria getAposentadoria() {
        return aposentadoria;
    }

    public void setAposentadoria(Aposentadoria aposentadoria) {
        this.aposentadoria = aposentadoria;
    }

    public List<ContratoVinculoDeContrato> getContratoVinculoDeContrato() {
        return contratoVinculoDeContrato;
    }

    public void setContratoVinculoDeContrato(List<ContratoVinculoDeContrato> contratoVinculoDeContrato) {
        this.contratoVinculoDeContrato = contratoVinculoDeContrato;
    }

    public List<RecursoDoVinculoFP> getLotacaoFolhaExercicio() {
        return lotacaoFolhaExercicio;
    }

    public void setLotacaoFolhaExercicio(List<RecursoDoVinculoFP> lotacaoFolhaExercicio) {
        this.lotacaoFolhaExercicio = lotacaoFolhaExercicio;
    }

    public Integer getCODIGO_NOMEACAO() {
        return CODIGO_NOMEACAO;
    }

    @URLAction(mappingId = "verProvimentoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        this.selecionado = provimentoFPFacade.recuperar(getId());
        EntidadePagavelRH ep = selecionado.getVinculoFP();
//        if (this.selecionado.getVinculoFP() instanceof Aposentadoria) {
//            this.selecionado.setContratoFP(((Aposentadoria) this.selecionado.getVinculoFP()).getContratoFP());
//        } else if (this.selecionado.getVinculoFP() instanceof Pensionista) {
//            this.selecionado.setContratoFP(((Pensionista) this.selecionado.getVinculoFP()).getPensao().getContratoFP());
//        } else if (this.selecionado.getVinculoFP() instanceof Beneficiario) {
//            this.selecionado.setContratoFP(((Beneficiario) this.selecionado.getVinculoFP()).getContratoFP());
//        } else {
//            this.selecionado.setContratoFP((ContratoFP) this.selecionado.getVinculoFP());
//        }

        this.selecionado.setContratoFP(ep.getContratoFP());

        ProvimentoFP p = this.selecionado;
        setSituacaoFuncional(situacaoFuncionalFacade.recuperaSituacaoFuncionalProvimento(p.getContratoFP(), p.getDataProvimento()));
        setPrevidenciaVinculoFP(previdenciaVinculoFPFacade.recuperaPrevidenciaVinculoFPProvimento(p.getContratoFP(), p.getDataProvimento()));
        setEnquadramentoFuncional(enquadramentoFuncionalFacade.recuperaEnquadramentosPorContratoEData(p.getContratoFP(), p.getDataProvimento()));
        setLotacaoFuncional(lotacaoFuncionalFacade.recuperaLotacaoFuncionalProvimento(p.getContratoFP(), p.getDataProvimento()));
        setCargoConfianca(cargoConfiancaFacade.recuperaCargoConfiacaProvimento(p.getContratoFP(), p.getDataProvimento()));
        setFuncoesGratificadas(funcaoGratificadaFacade.recuperaFuncaoGratificadaProvimento(p.getContratoFP(), p.getDataProvimento()));
        setExoneracaoRescisao(exoneracaoRescisaoFacade.recuperaExoneracaoRescisaoProvimento(p.getContratoFP(), p.getDataProvimento()));
        setContratoVinculoDeContrato(contratoVinculoDeContratoFacade.recuperaContratoVinculoDeContrato(p.getContratoFP()));
        setLotacaoFolhaExercicio(lotacaoFolhaExercicioFacade.recuperarRecursosDoVinculo(p.getContratoFP()));
        Aposentadoria ap = aposentadoriaFacade.recuperaAposentadoriaPorContratoFP(p.getContratoFP());
        lotacaoFolhaExercicioVigente = new RecursoDoVinculoFP();
        cargoConfiancaVigente = new CargoConfianca();
        enquadramentoFGVigente = new EnquadramentoFG();
        if (ap != null && p.getDataProvimento().getTime() >= ap.getInicioVigencia().getTime()) {
            setAposentadoria(aposentadoriaFacade.recuperar(ap.getId()));
        } else {
            setAposentadoria(null);
        }
    }

    @URLAction(mappingId = "novoProvimentoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        vinculoFP = null;
    }

    public int getTabIndex() {
        ProvimentoFP p = (ProvimentoFP) selecionado;

        //    5 - REINTEGRACAO;
        if (p.getTipoProvimento().getCodigo() == 5) {
            return 2;
        }
        //    8 - PROVIMENTO_PROGRESSAO;
        if (p.getTipoProvimento().getCodigo() == 8) {
            return 2;
        }
        //    10 - PROMOCAO
        if (p.getTipoProvimento().getCodigo() == 10) {
            return 2;
        }
        // 15 - ALTERACAO CARGO COMISSAO
        if (p.getTipoProvimento().getCodigo() == 15) {
            return 2;
        }
        // 22 - EXONERACAO FUNCAO GRATIFICADA
        if (p.getTipoProvimento().getCodigo() == 22) {
            return 2;
        }
        // 25 - ENQUADRAMENTO
        if (p.getTipoProvimento().getCodigo() == 25) {
            return 2;
        }
        // 27 - RETORNO CARGO CARREIRA
        if (p.getTipoProvimento().getCodigo() == 27) {
            return 2;
        }
        // 32 - ALTERACAO VINCULO E PERCENTUAL DE APOSENTADORIA
        if (p.getTipoProvimento().getCodigo() == 32) {
            return 2;
        }
        // 69 - ESTORNO EXONERACAO CARREIRA
        if (p.getTipoProvimento().getCodigo() == 69) {
            return 6;
        }
        // 70 - ESTORNO EXONERACAO COMISSAO
        if (p.getTipoProvimento().getCodigo() == 70) {
            return 6;
        }

        for (EnquadramentoFuncional ef : enquadramentoFuncional) {
            if (p.equals(ef.getProvimentoFP())) {
                return 2;
            }
        }

        for (LotacaoFuncional lf : lotacaoFuncional) {
            if (p.equals(lf.getProvimentoFP())) {
                return 3;
            }
        }

        for (CargoConfianca cc : cargoConfianca) {
            if (p.equals(cc.getProvimentoFP()) || p.equals(cc.getProvimentoRetorno())) {
                return 4;
            }
        }

        for (FuncaoGratificada fg : funcoesGratificadas) {
            if (p.equals(fg.getProvimentoFP())) {
                return 5;
            }
        }

        for (ExoneracaoRescisao e : exoneracaoRescisao) {
            if (p.equals(e.getProvimentoFP())) {
                return 6;
            }
        }

        if (aposentadoria != null && p.equals(aposentadoria.getProvimentoFP())) {
            return 7;
        }

        return 0;
    }

    public String recuperaLotacaoFuncional() {
//        if (((ProvimentoFP) selecionado).getTipoProvimento().getCodigo() == CODIGO_NOMEACAO) {
        LotacaoFuncional lotacaoFuncional = lotacaoFuncionalFacade.recuperaLotacaoFuncionalVigente(((ProvimentoFP) selecionado).getContratoFP(), ((ProvimentoFP) selecionado).getDataProvimento());
        if (lotacaoFuncional != null && lotacaoFuncional.getId() != null) {
            //return getDescricaoOrgaoEntidade.getUnidadeOrganizacional().getCodigoDescricao();
            return lotacaoFuncionalFacade.recuperaHierarquiaDaLotacao(lotacaoFuncional.getUnidadeOrganizacional());
        }
//        }
        return "";
    }

    public RecursoDoVinculoFP recuperaLotacaoFolhaExercicio() {
//        if (((ProvimentoFP) selecionado).getTipoProvimento().getCodigo() == CODIGO_NOMEACAO) {//Retirado o tipo, para mostrar em todos os provimentos.
        if (lotacaoFolhaExercicioVigente == null || lotacaoFolhaExercicioVigente.getId() == null) {
            //System.out.println("Data Provimento: " + ((ProvimentoFP) selecionado).getDataProvimento());
            List<RecursoDoVinculoFP> recursos = lotacaoFolhaExercicioFacade.recuperarRecursosDoVinculo(((ProvimentoFP) selecionado).getContratoFP(), ((ProvimentoFP) selecionado).getDataProvimento());
            if (!recursos.isEmpty()) {
                lotacaoFolhaExercicioVigente = recursos.get(0);
            }
        }
//        }
        return lotacaoFolhaExercicioVigente;
    }

    public String recuperaVinculoEmpregaticio() {
        VinculoDeContratoFP vinculoDeContratoFP = vinculoDeContratoFPFacade.recuperaVinculoDeContratoVigente(((ProvimentoFP) selecionado).getContratoFP(), ((ProvimentoFP) selecionado).getDataProvimento());
        if (vinculoDeContratoFP != null && vinculoDeContratoFP.getId() != null) {
            return vinculoDeContratoFP.getCodigo() + " - " + vinculoDeContratoFP.getDescricao();
        }
        return "";
    }

    public String recuperaCargoComissao() {
        EnquadramentoCC funcional = cargoConfiancaFacade.recuperaEnquadramentoCCVigente(((ProvimentoFP) selecionado).getContratoFP(), ((ProvimentoFP) selecionado).getDataProvimento());
        if (funcional != null && funcional.getId() != null) {
            return " (CARREIRA/COMISSÃO) ";
        }
        return "";
    }

    public String recuperaFuncaoGratificada() {
        EnquadramentoFG funcional = funcaoGratificadaFacade.recuperaEnquadramentoFGVigente(((ProvimentoFP) selecionado).getContratoFP(), ((ProvimentoFP) selecionado).getDataProvimento());
        if (funcional != null && funcional.getId() != null) {
            return " - FUNÇAO GRATIFICADA ";
        }
        return "";
    }

    public String recuperaEnquadramentoFuncional() {
        EnquadramentoFuncional enquadramentoFuncional = enquadramentoFuncionalFacade.recuperaEnquadramentoFuncionalVigente(((ProvimentoFP) selecionado).getContratoFP(), ((ProvimentoFP) selecionado).getDataProvimento());
        if (enquadramentoFuncional != null && enquadramentoFuncional.getId() != null) {
            String categoriaSuperior = enquadramentoFuncional.getCategoriaPCS().getSuperior().getDescricao();
            String categoria = enquadramentoFuncional.getCategoriaPCS().getDescricao();
            String progressao = enquadramentoFuncional.getProgressaoPCS().getDescricao();
            return categoriaSuperior + " - " + categoria + " - " + progressao;
        }
        return "Não encontrado!";
    }

    public CargoConfianca recuperaCargoConfianca() {
        if (isAcessoCargoComissao()) {
            if (cargoConfiancaVigente == null || cargoConfiancaVigente.getId() == null) {
                cargoConfiancaVigente = cargoConfiancaFacade.recuperaCargoConfiancaVigente(((ProvimentoFP) selecionado).getContratoFP(), ((ProvimentoFP) selecionado).getDataProvimento());
            }
            return cargoConfiancaVigente;
        }
        return new CargoConfianca();
    }

    public boolean isAcessoCargoComissao() {
        if (((ProvimentoFP) selecionado).getTipoProvimento().getCodigo().equals(CODIGO_ACESSO_CARGO_COMISSAO)) {
            return true;
        }
        return false;
    }

    public EnquadramentoFG recuperaEnquadramentoFG() {
        if (isFuncaoGratificada()) {
            if (enquadramentoFGVigente == null || enquadramentoFGVigente.getId() == null) {
                enquadramentoFGVigente = enquadramentoFGFacade.recuperaFGVigente(((ProvimentoFP) selecionado).getContratoFP(), ((ProvimentoFP) selecionado).getDataProvimento());
            }
            return enquadramentoFGVigente;
        }
        return null;
    }

    public boolean isFuncaoGratificada() {
        if (((ProvimentoFP) selecionado).getTipoProvimento().getCodigo().equals(CODIGO_FUNCAO_GRATIFICADA)) {
            return true;
        }
        return false;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/provimento/";
    }

    @Override
    public Object getUrlKeyValue() {
        return this.selecionado.getId();
    }
}
