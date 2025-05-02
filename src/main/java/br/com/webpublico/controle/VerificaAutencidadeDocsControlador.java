package br.com.webpublico.controle;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidadesauxiliares.AutenticaAlvara;
import br.com.webpublico.entidadesauxiliares.AutenticaCertidao;
import br.com.webpublico.entidadesauxiliares.AutenticaITBI;
import br.com.webpublico.entidadesauxiliares.AutenticaNFSAvulsa;
import br.com.webpublico.enums.TipoAlvara;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.enums.TipoCertidao;
import br.com.webpublico.enums.TipoDocumentoValidacao;
import br.com.webpublico.negocios.*;
import br.com.webpublico.nfse.domain.dtos.AutenticaNfseDTO;
import br.com.webpublico.nfse.facades.NotaFiscalFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 27/01/15
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaConsultaCertidao", pattern = "/tributario/atendimento-ao-contribuinte/consulta-documentos/",
        viewId = "/faces/tributario/validadocumentos/edita.xhtml")
})
public class VerificaAutencidadeDocsControlador implements Serializable {

    @EJB
    private ConsultaAutenticidadeNFSAvulsaFacade consultaAutenticidadeNFSAvulsaFacade;
    @EJB
    private CalculoAlvaraFacade calculoAlvaraFacade;
    @EJB
    private DocumentoOficialFacade documentoOficialFacade;
    @EJB
    private CalculoITBIFacade calculoITBIFacade;
    @EJB
    private NotaFiscalFacade notaFiscalFacade;
    private AutenticaNFSAvulsa autenticaNFSAvulsa;
    private AutenticaAlvara autenticaAlvara;
    private AutenticaCertidao autenticaCertidao;
    private AutenticaITBI autenticaIIBI;
    private AutenticaNfseDTO autenticaNfse;
    private Boolean valido;
    private String opcaoCpfCnpj;
    private String cpfCnpj;
    private TipoDocumentoValidacao tipoDocumentoValidacao;
    private Boolean isNFSAfulsa;
    private Boolean isCertidao;
    private Boolean isAlvara;
    private Boolean isITBI;
    private Boolean isNfse;
    private Boolean validado;
    private Exercicio exercicio;

    public AutenticaCertidao getAutenticaCertidao() {
        return autenticaCertidao;
    }

    public void setAutenticaCertidao(AutenticaCertidao autenticaCertidao) {
        this.autenticaCertidao = autenticaCertidao;
    }

    public AutenticaNFSAvulsa getAutenticaNFSAvulsa() {
        return autenticaNFSAvulsa;
    }

    public void setAutenticaNFSAvulsa(AutenticaNFSAvulsa autenticaNFSAvulsa) {
        this.autenticaNFSAvulsa = autenticaNFSAvulsa;
    }

    public AutenticaNfseDTO getAutenticaNfse() {
        return autenticaNfse;
    }

    public void setAutenticaNfse(AutenticaNfseDTO autenticaNfse) {
        this.autenticaNfse = autenticaNfse;
    }

    public Boolean getNfse() {
        return isNfse;
    }

    public void setNfse(Boolean nfse) {
        isNfse = nfse;
    }

    public Boolean getNFSAfulsa() {
        return isNFSAfulsa;
    }

    public void setNFSAfulsa(Boolean NFSAfulsa) {
        isNFSAfulsa = NFSAfulsa;
    }

    public AutenticaITBI getAutenticaIIBI() {
        return autenticaIIBI;
    }

    public void setAutenticaIIBI(AutenticaITBI autenticaIIBI) {
        this.autenticaIIBI = autenticaIIBI;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getMascara() {
        if (("CPF").equals(autenticaNFSAvulsa.getCpfCnpj())) {
            return "999.999.999-99";
        } else {
            return "99.999.999/9999-99";
        }
    }

    public Boolean getITBI() {
        return isITBI;
    }

    public void setITBI(Boolean ITBI) {
        isITBI = ITBI;
    }

    public AutenticaAlvara getAutenticaAlvara() {
        return autenticaAlvara;
    }

    public void setAutenticaAlvara(AutenticaAlvara autenticaAlvara) {
        this.autenticaAlvara = autenticaAlvara;
    }

    public TipoDocumentoValidacao getTipoDocumentoValidacao() {
        return tipoDocumentoValidacao;
    }

    public void setTipoDocumentoValidacao(TipoDocumentoValidacao tipoDocumentoValidacao) {
        this.tipoDocumentoValidacao = tipoDocumentoValidacao;
    }

    public Boolean getCertidao() {
        return isCertidao;
    }

    public void setCertidao(Boolean certidao) {
        isCertidao = certidao;
    }

    public Boolean getAlvara() {
        return isAlvara;
    }

    public void setAlvara(Boolean alvara) {
        isAlvara = alvara;
    }

    public Boolean getValidado() {
        return validado;
    }

    public void setValidado(Boolean validado) {
        this.validado = validado;
    }

    public Boolean getValido() {
        return valido;
    }

    public void setValido(Boolean valido) {
        this.valido = valido;
    }

    public void validaTipoAutenticacao() {
        autenticaNFSAvulsa = new AutenticaNFSAvulsa();
        autenticaAlvara = new AutenticaAlvara();
        autenticaCertidao = new AutenticaCertidao();
        autenticaNfse = new AutenticaNfseDTO();
        autenticaIIBI = new AutenticaITBI();
        isAlvara = tipoDocumentoValidacao.equals(TipoDocumentoValidacao.ALVARA);
        isCertidao = tipoDocumentoValidacao.equals(TipoDocumentoValidacao.CERTIDAO);
        isNFSAfulsa = tipoDocumentoValidacao.equals(TipoDocumentoValidacao.NFA);
        isITBI = tipoDocumentoValidacao.equals(TipoDocumentoValidacao.ITBI);
        isNfse = tipoDocumentoValidacao.equals(TipoDocumentoValidacao.NFSE);
        validado = null;
    }

    public void limpaCpfCnpj() {
        cpfCnpj = "";
    }

    @URLAction(mappingId = "novaConsultaCertidao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        validado = null;
        opcaoCpfCnpj = "CPF";
        validado = null;
        cpfCnpj = "";
        tipoDocumentoValidacao = null;
    }

    public List<SelectItem> getTipoCertidaoValidacao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoDocumentoValidacao tipo : tipoDocumentoValidacao.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposAlvara() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoAlvara tipo : TipoAlvara.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricaoSimples()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposCertidao() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoCertidao tipo : TipoCertidao.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public List<SelectItem> getTiposCadastros() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (TipoCadastroDoctoOficial tipo : TipoCadastroDoctoOficial.values()) {
            retorno.add(new SelectItem(tipo, tipo.getDescricao()));
        }
        return retorno;
    }

    public boolean validaNFSAvulsa() {
        boolean retorno = true;
        if (TipoDocumentoValidacao.NFA.equals(tipoDocumentoValidacao)) {
            if (autenticaNFSAvulsa.getNumeroNota().trim().equals("")) {
                FacesUtil.addOperacaoNaoPermitida("O campo Número da Nota é Obrigatório.");
                retorno = false;
            }
            if (autenticaNFSAvulsa.getDataLancamento() == null) {
                FacesUtil.addOperacaoNaoPermitida("O campo Data de Emissão é Obrigatório.");
                retorno = false;
            }
            if (autenticaNFSAvulsa.getCpfCnpj() == null) {
                FacesUtil.addOperacaoNaoPermitida("Informe o CPF ou CNPJ.");
                retorno = false;
            }
            if (autenticaNFSAvulsa.getNumeroAutenticidade().trim().equals("")) {
                FacesUtil.addOperacaoNaoPermitida("O campo Número de Autenticidade é Obrigatório.");
                retorno = false;
            }
        }
        return retorno;
    }


    public boolean validarNFSe() {
        boolean retorno = true;
        if (TipoDocumentoValidacao.NFSE.equals(tipoDocumentoValidacao)) {
            if (Strings.isNullOrEmpty(autenticaNfse.getCodigoAutenticacao())) {
                FacesUtil.addOperacaoNaoPermitida("O código de verificação da nota fiscal é obrigatório.");
                retorno = false;
            }
            if (Strings.isNullOrEmpty(autenticaNfse.getNumeroNfse())) {
                FacesUtil.addOperacaoNaoPermitida("O número da nota fiscal é obrigatório.");
                retorno = false;
            }
            if (Strings.isNullOrEmpty(autenticaNfse.getCpfCnpjPrestador())) {
                FacesUtil.addOperacaoNaoPermitida("O cpf ou cnpj do prestador de serviços é obrigatório.");
                retorno = false;
            }

        }
        return retorno;
    }

    public boolean validaAlvara() {
        boolean retorno = true;
        if (autenticaAlvara.getTipoAlvara() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Tipo do Alvará é Obrigatório.");
            retorno = false;
        }
        if (autenticaAlvara.getDataEmissao() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Data de Emissão é Obrigatório.");
            retorno = false;
        }
        if (autenticaAlvara.getIncricaoCadastral().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("O campo Inscrição Cadastral é Obrigatório.");
            retorno = false;
        }
        if (autenticaAlvara.getAssinaturaParaAutenticar().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("O campo Número de Autencidade é Obrigatório.");
            retorno = false;
        }
        return retorno;
    }

    public boolean validaCertidao() {
        boolean retorno = true;

        if (autenticaCertidao.getTipoCertidao() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Tipo é Obrigatório.");
            retorno = false;
        }

        if (autenticaCertidao.getDataEmissao() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Data de Expedição é Obrigatório.");
            retorno = false;
        }

        if (autenticaCertidao.getDataVencimento() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Data de Vencimento é Obrigatório.");
            retorno = false;
        }

        if (autenticaCertidao.getNumeroDocumento() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Número do Documento é Obrigatório.");
            retorno = false;
        }
        if (autenticaCertidao.getCodigoVerificacao().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("O campo Número de Autencidade é Obrigatório.");
            retorno = false;
        }
        if(TipoCertidao.CADASTRO_IMOBILIARIO.equals(autenticaCertidao.getTipoCertidao())){
            if(autenticaCertidao.getCpfCnpj() == null || autenticaCertidao.getCpfCnpj().isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("O campo CPF/CNPJ é Obrigatório");
                retorno = false;
            }
        }
        return retorno;
    }

    public boolean validaITBI() {
        boolean retorno = true;
        if (autenticaIIBI.getDataVencimento() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Data de Vencimento é Obrigatório.");
            retorno = false;
        }

        if (exercicio == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Exercício é Obrigatório.");
            retorno = false;
        }
        if (autenticaIIBI.getSequencia() == null) {
            FacesUtil.addOperacaoNaoPermitida("O campo Número do ITBI é Obrigatório.");
            retorno = false;
        }
        if (autenticaIIBI.getCodigoAutenticacao().trim().equals("")) {
            FacesUtil.addOperacaoNaoPermitida("O campo Número de Autencidade é Obrigatório.");
            retorno = false;
        }
        return retorno;
    }


    public void validar() {

        if (tipoDocumentoValidacao != null) {
            if (tipoDocumentoValidacao.equals(TipoDocumentoValidacao.NFA)) {
                if (validaNFSAvulsa()) {
                    validado = consultaAutenticidadeNFSAvulsaFacade.validouNotaFiscalAvulsa(autenticaNFSAvulsa);
                }
            }
            if (tipoDocumentoValidacao.equals(TipoDocumentoValidacao.ALVARA)) {
                if (validaAlvara()) {
                    validado = calculoAlvaraFacade.autenticouAlvara(autenticaAlvara);
                }
            }
            if (tipoDocumentoValidacao.equals(TipoDocumentoValidacao.CERTIDAO)) {
                if (validaCertidao()) {
                    validado = documentoOficialFacade.validouAutenticacao(autenticaCertidao);
                }
            }
            if (tipoDocumentoValidacao.equals(TipoDocumentoValidacao.ITBI)) {
                if (validaITBI()) {
                    validado = calculoITBIFacade.isAtenticouLaudoItbi(exercicio, autenticaIIBI);

                }
            }
            if (tipoDocumentoValidacao.equals(TipoDocumentoValidacao.NFSE) && validarNFSe()) {
                validado = notaFiscalFacade.hasNfseAutentica(autenticaNfse);
            }
        } else {
            FacesUtil.addOperacaoNaoPermitida("Informe o Tipo de Certidão");
        }
    }
}




