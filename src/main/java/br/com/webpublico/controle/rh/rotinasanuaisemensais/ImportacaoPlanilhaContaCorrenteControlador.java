package br.com.webpublico.controle.rh.rotinasanuaisemensais;

import br.com.webpublico.entidadesauxiliares.rh.ExportacaoPlanilhaContaCorrenteCaixa;
import br.com.webpublico.entidadesauxiliares.rh.ImportacaoPlanilhaContaCorrenteCaixa;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.rh.rotinasanuaismensais.ImportacaoExportacaoPlanilhaContaCorrenteCaixaFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;


@ManagedBean(name = "importacaoPlanilhaContaCorrenteControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "importacao-planilha-bb-dados", pattern = "/importacao-dados/bb-conta-corrente/", viewId = "/faces/rh/rotinasanuaisemensais/planilhabb/edita.xhtml")
})
public class ImportacaoPlanilhaContaCorrenteControlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(ImportacaoPlanilhaContaCorrenteControlador.class);
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ImportacaoExportacaoPlanilhaContaCorrenteCaixaFacade facade;
    private List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas;
    private List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasInconsistentes;

    @URLAction(mappingId = "importacao-planilha-bb-dados", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        pessoasImportadas = Lists.newArrayList();
        pessoasInconsistentes = Lists.newArrayList();
    }

    public void limpar() {
        novo();
    }

    public void importar(FileUploadEvent event) {
        try {
            validarImportacaoArquivo();
            UploadedFile file = event.getFile();
            facade.importarPessoasPessoas(pessoasImportadas, file.getInputstream());
            int contador = 0;
            int contadorErro = 0;
            for (ImportacaoPlanilhaContaCorrenteCaixa pessoasImportada : pessoasImportadas) {
                if (pessoasImportada.getPessoaFisica() != null) {
                    contador++;
                } else {
                    //028.980.172-99
                    //02898017299
                    logger.debug("Pessoa não encontrada CPF: [{}]", pessoasImportada.getCpf());
                    contadorErro++;
                }
                if (facade.isRegistroInvalido(pessoasImportada)) {
                    pessoasInconsistentes.add(pessoasImportada);
                }
            }

            logger.debug("Total de pessoas encontradas: {}", contador);
            logger.debug("Total de pessoas não encontradas: {}", contadorErro);
            logger.debug("Iniciando parametrização dos dados importados.");
            //List<ExportacaoPlanilhaContaCorrenteCaixa> pessoas = facade.preencherDadosExportacao(pessoasImportadas);
            //verificarPessoasAdicionadas(pessoas, pessoasExportacao);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada("Ocorreu um erro durante a importação do arquivo: " + ex.getMessage());
        }
    }


    private void verificarPessoasAdicionadas(List<ExportacaoPlanilhaContaCorrenteCaixa> pessoas, List<ExportacaoPlanilhaContaCorrenteCaixa> pessoasExportacao) {
        List<ExportacaoPlanilhaContaCorrenteCaixa> lista = Lists.newLinkedList();

        for (ExportacaoPlanilhaContaCorrenteCaixa exportacaoPlanilhaContaCorrenteCaixa : pessoas) {
            if (!isCpfAdicionado(exportacaoPlanilhaContaCorrenteCaixa.getCpf(), pessoasExportacao)) {
                lista.add(exportacaoPlanilhaContaCorrenteCaixa);
            }
        }
        pessoasExportacao.addAll(lista);
        if (pessoasExportacao.isEmpty()) {
            pessoasExportacao.addAll(pessoas);
        }

    }

    public void criarContas() {
        if (!pessoasImportadas.isEmpty()) {
            facade.criarContas(pessoasImportadas);
            FacesUtil.addOperacaoRealizada("Contas Importadas Com Sucesso");
        }
    }

    private boolean isCpfAdicionado(String cpf, List<ExportacaoPlanilhaContaCorrenteCaixa> pessoas) {
        for (ExportacaoPlanilhaContaCorrenteCaixa pessoa : pessoas) {
            if (pessoa.getCpf().equals(cpf)) {
                return true;
            }
        }

        return false;
    }


    private void validarImportacaoArquivo() {

    }


    private String converterString(String descricao) {
        try {
            return Integer.valueOf(descricao).toString();
        } catch (Exception ex) {
            return "";
        }
    }


    public List<ImportacaoPlanilhaContaCorrenteCaixa> getPessoasImportadas() {
        return pessoasImportadas;
    }

    public void setPessoasImportadas(List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasImportadas) {
        this.pessoasImportadas = pessoasImportadas;
    }

    public enum CampoImportacao {
        CPF("CPF"),
        NOME("Nome");
        private String descricao;

        CampoImportacao(String descricao) {
            this.descricao = descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }


    public List<ImportacaoPlanilhaContaCorrenteCaixa> getPessoasInconsistentes() {
        return pessoasInconsistentes;
    }

    public void setPessoasInconsistentes(List<ImportacaoPlanilhaContaCorrenteCaixa> pessoasInconsistentes) {
        this.pessoasInconsistentes = pessoasInconsistentes;
    }
}
