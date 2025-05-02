package br.com.webpublico.controle;

import br.com.webpublico.entidades.CertidaoMarcaFogo;
import br.com.webpublico.entidades.DocumentoMarcaFogo;
import br.com.webpublico.entidades.ParametroMarcaFogo;
import br.com.webpublico.entidades.TaxaMarcaFogo;
import br.com.webpublico.enums.NaturezaDocumentoMarcaFogo;
import br.com.webpublico.enums.TipoEmissaoMarcaFogo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.ParametroMarcaFogoFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novoParametroMarcaFogo",
        pattern = "/tributario/parametro-marca-fogo/novo/",
        viewId = "/faces/tributario/parametromarcafogo/edita.xhtml"),
    @URLMapping(id = "listarParametroMarcaFogo",
        pattern = "/tributario/parametro-marca-fogo/listar/",
        viewId = "/faces/tributario/parametromarcafogo/lista.xhtml"),
    @URLMapping(id = "editarParametroMarcaFogo",
        pattern = "/tributario/parametro-marca-fogo/editar/#{parametroMarcaFogoControlador.id}/",
        viewId = "/faces/tributario/parametromarcafogo/edita.xhtml"),
    @URLMapping(id = "verParametroMarcaFogo",
        pattern = "/tributario/parametro-marca-fogo/ver/#{parametroMarcaFogoControlador.id}/",
        viewId = "/faces/tributario/parametromarcafogo/visualizar.xhtml")
})
public class ParametroMarcaFogoControlador extends PrettyControlador<ParametroMarcaFogo> implements CRUD {

    @EJB
    private ParametroMarcaFogoFacade parametroMarcaFogoFacade;
    private DocumentoMarcaFogo documento;
    private TaxaMarcaFogo taxa;
    private CertidaoMarcaFogo certidao;

    public ParametroMarcaFogoControlador() {
        super(ParametroMarcaFogo.class);
    }

    public DocumentoMarcaFogo getDocumento() {
        return documento;
    }

    public void setDocumento(DocumentoMarcaFogo documento) {
        this.documento = documento;
    }

    public TaxaMarcaFogo getTaxa() {
        return taxa;
    }

    public void setTaxa(TaxaMarcaFogo taxa) {
        this.taxa = taxa;
    }

    public CertidaoMarcaFogo getCertidao() {
        return certidao;
    }

    public void setCertidao(CertidaoMarcaFogo certidao) {
        this.certidao = certidao;
    }

    @Override
    public ParametroMarcaFogoFacade getFacede() {
        return parametroMarcaFogoFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/tributario/parametro-marca-fogo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novoParametroMarcaFogo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        criarNovoDocumento();
        criarNovaTaxa();
        criarNovaCertidao();
    }

    @URLAction(mappingId = "editarParametroMarcaFogo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        criarNovoDocumento();
        criarNovaTaxa();
        criarNovaCertidao();
    }

    @URLAction(mappingId = "verParametroMarcaFogo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public void editarDocumento(DocumentoMarcaFogo documento) {
        this.documento = documento;
        removerDocumento(this.documento);
    }

    public void adicionarDocumento() {
        try {
            validarDocumento();
            documento.setParametroMarcaFogo(selecionado);
            selecionado.getDocumentos().add(documento);
            criarNovoDocumento();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void validarDocumento() {
        ValidacaoException exception = new ValidacaoException();
        Util.validarCampos(documento);
        for (DocumentoMarcaFogo doc : selecionado.getDocumentos()) {
            if (doc.getNaturezaDocumento().equals(documento.getNaturezaDocumento())
                && doc.getDescricao().toLowerCase().trim().equals(documento.getDescricao().trim().toLowerCase())) {
                exception.adicionarMensagemDeOperacaoNaoPermitida("Já existe um documento adicionado com essa mesma descrição");
                break;
            }
        }
        if (exception.temMensagens()) {
            throw exception;
        }
    }


    @Override
    public boolean validaRegrasEspecificas() {
        ParametroMarcaFogo p = parametroMarcaFogoFacade.buscarParametroPeloExercicio(selecionado.getExercicio());
        if (p != null && !p.getId().equals(selecionado.getId())) {
            FacesUtil.addOperacaoNaoPermitida("Já existe um parametro cadastrado com o exercício de " + selecionado.getExercicio().getAno() + ".");
            return false;
        }
        return true;
    }

    private void criarNovoDocumento() {
        documento = new DocumentoMarcaFogo();
    }

    public void removerDocumento(DocumentoMarcaFogo documento) {
        selecionado.getDocumentos().remove(documento);
    }

    public List<SelectItem> getNaturezasDocumento() {
        return Util.getListSelectItem(NaturezaDocumentoMarcaFogo.values());
    }

    public void editarTaxa(TaxaMarcaFogo taxa) {
        this.taxa = taxa;
        removerTaxa(this.taxa);
    }

    public void adicionarTaxa() {
        try {
            validarTaxa();
            taxa.setParametroMarcaFogo(selecionado);
            selecionado.getTaxas().add(taxa);
            criarNovaTaxa();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void validarTaxa() {
        ValidacaoException exception = new ValidacaoException();
        Util.validarCampos(taxa);
        if (selecionado.getTaxas()
            .stream().anyMatch(t -> t.getTipoEmissao().equals(taxa.getTipoEmissao())
                && t.getTributo().equals(taxa.getTributo()))) {
            throw new ValidacaoException("Tributo já registrado para o tipo de emissão.");
        }
        if (exception.temMensagens()) {
            throw exception;
        }
    }

    private void criarNovaTaxa() {
        taxa = new TaxaMarcaFogo();
    }

    public void removerTaxa(TaxaMarcaFogo taxa) {
        selecionado.getTaxas().remove(taxa);
    }

    public List<SelectItem> getTiposEmissao() {
        return Util.getListSelectItem(TipoEmissaoMarcaFogo.values());
    }

    public void editarCertidao(CertidaoMarcaFogo certidao) {
        this.certidao = certidao;
        removerCertidao(this.certidao);
    }

    public void adicionarCertidao() {
        try {
            validarCertidao();
            certidao.setParametroMarcaFogo(selecionado);
            selecionado.getCertidoes().add(certidao);
            criarNovaCertidao();
        } catch (ValidacaoException va) {
            FacesUtil.printAllFacesMessages(va.getMensagens());
        }
    }

    public void validarCertidao() {
        ValidacaoException exception = new ValidacaoException();
        Util.validarCampos(certidao);
        if (selecionado.getCertidoes().stream().anyMatch(c -> c.getTipoEmissao().equals(certidao.getTipoEmissao()))) {
            throw new ValidacaoException("Certidão já registrada para o tipo de emissão.");
        }
        if (exception.temMensagens()) {
            throw exception;
        }
    }

    private void criarNovaCertidao() {
        certidao = new CertidaoMarcaFogo();
    }

    public void removerCertidao(CertidaoMarcaFogo certidao) {
        selecionado.getCertidoes().remove(certidao);
    }
}
