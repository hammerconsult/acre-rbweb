package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.exception.ValidacaoException;
import com.google.common.collect.Lists;
import org.primefaces.model.UploadedFile;

import java.io.Serializable;
import java.util.List;

public class ImportacaoAtributoCadastroImobiliario implements Serializable {

    private UploadedFile uploadedFile;
    private Integer indexInscricaoCadastral;
    private Boolean descartarPrimeiraLinha;
    private AtributoCadastroImobiliario atributo;
    private List<AtributoCadastroImobiliario> atributos;

    public ImportacaoAtributoCadastroImobiliario() {
        atributo = new AtributoCadastroImobiliario();
        atributos = Lists.newArrayList();
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public Integer getIndexInscricaoCadastral() {
        return indexInscricaoCadastral;
    }

    public void setIndexInscricaoCadastral(Integer indexInscricaoCadastral) {
        this.indexInscricaoCadastral = indexInscricaoCadastral;
    }

    public Boolean getDescartarPrimeiraLinha() {
        return descartarPrimeiraLinha;
    }

    public void setDescartarPrimeiraLinha(Boolean descartarPrimeiraLinha) {
        this.descartarPrimeiraLinha = descartarPrimeiraLinha;
    }

    public AtributoCadastroImobiliario getAtributo() {
        return atributo;
    }

    public void setAtributo(AtributoCadastroImobiliario atributo) {
        this.atributo = atributo;
    }

    public List<AtributoCadastroImobiliario> getAtributos() {
        return atributos;
    }

    public void setAtributos(List<AtributoCadastroImobiliario> atributos) {
        this.atributos = atributos;
    }

    public void addAtributo() {
        if (atributo.getIndex() != null &&
            atributo.getIndex() > 0 &&
            atributos.stream().noneMatch(a ->
                a.getAtributo().equals(atributo.getAtributo()))) {
            atributos.add(atributo);
            atributo = new AtributoCadastroImobiliario();
        }
    }

    public void removeAtributo(AtributoCadastroImobiliario atributo) {
        atributos.remove(atributo);
    }

    public void validar() {
        ValidacaoException ve = new ValidacaoException();
        if (uploadedFile == null) {
            ve.adicionarMensagemDeCampoObrigatorio("A planilha deve ser informada.");
        }
        if (indexInscricaoCadastral == null || indexInscricaoCadastral < 0) {
            ve.adicionarMensagemDeCampoObrigatorio("O Index da Inscrição Cadastral deve ser informado.");
        }
        if (atributos.isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("Pelo menos 1 atributo deve ser informado.");
        }
        ve.lancarException();
    }
}
