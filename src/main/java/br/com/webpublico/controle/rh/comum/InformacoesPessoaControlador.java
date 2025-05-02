package br.com.webpublico.controle.rh.comum;

import br.com.webpublico.controle.SuperControladorCRUD;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.LotacaoFuncional;
import br.com.webpublico.entidades.ModalidadeContratoFP;
import br.com.webpublico.entidades.PessoaFisica;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.entidadesauxiliares.rh.PessoaInfo;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.FichaFinanceiraFPFacade;
import br.com.webpublico.negocios.PessoaFisicaFacade;
import br.com.webpublico.negocios.VinculoFPFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;


@ManagedBean
@ViewScoped
public class InformacoesPessoaControlador extends SuperControladorCRUD implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(InformacoesPessoaControlador.class);

    @EJB
    private ContratoFPFacade contratoFPFacade;

    @EJB
    private FichaFinanceiraFPFacade fichaFinanceiraFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    private PessoaInfo pessoaInfo = new PessoaInfo();
    private LotacaoFuncional lotacaoFuncionalVigente;
    private ModalidadeContratoFP modalidadeContratoFP;

    @Override
    public AbstractFacade getFacede() {
        return vinculoFPFacade;
    }

    public PessoaInfo buscarInformacoesPessoa(PessoaFisica pessoaFisica) {
        if (pessoaFisica != null) {
            pessoaInfo = new PessoaInfo();
            pessoaFisica = pessoaFisicaFacade.recuperar(pessoaFisica.getId());
            pessoaInfo.setPessoa(pessoaFisica);
            pessoaInfo.setVinculos(vinculoFPFacade.listaTodosVinculosPorPessoa(pessoaFisica));
            buscarLocataoFuncionalVigenteAndModalidadeContrato();
        }
        return pessoaInfo;
    }

    public void buscarLocataoFuncionalVigenteAndModalidadeContrato() {
        if (pessoaInfo != null) {
            for (VinculoFP vinculoFP : pessoaInfo.getVinculos()) {
                if (vinculoFP != null) {
                    vinculoFP = vinculoFPFacade.recuperar(vinculoFP.getId());
                    lotacaoFuncionalVigente = vinculoFP.getLotacaoFuncionalVigente();
                    ContratoFP contratoFP = contratoFPFacade.recuperar(vinculoFP.getId());
                    modalidadeContratoFP = contratoFP.getModalidadeContratoFP();
                }
            }
        }
    }

    public PessoaInfo getPessoaInfo() {
        return pessoaInfo;
    }

    public void setPessoaInfo(PessoaInfo pessoaInfo) {
        this.pessoaInfo = pessoaInfo;
    }

    public LotacaoFuncional getLotacaoFuncionalVigente() {
        return lotacaoFuncionalVigente;
    }

    public void setLotacaoFuncionalVigente(LotacaoFuncional lotacaoFuncionalVigente) {
        this.lotacaoFuncionalVigente = lotacaoFuncionalVigente;
    }

    public ModalidadeContratoFP getModalidadeContratoFP() {
        return modalidadeContratoFP;
    }

    public void setModalidadeContratoFP(ModalidadeContratoFP modalidadeContratoFP) {
        this.modalidadeContratoFP = modalidadeContratoFP;
    }
}
