package br.com.webpublico.controle;

import br.com.webpublico.entidades.RetornoHoleriteBB;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.RetornoHoleriteBBFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "retornoHoleriteBBControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRetornoHoleriteBB", pattern = "/retorno-holeritebb/novo/", viewId = "/faces/rh/administracaodepagamento/retornoholeritebb/edita.xhtml")
})
public class RetornoHoleriteBBControlador extends PrettyControlador<RetornoHoleriteBB> implements Serializable, CRUD {

    @EJB
    private RetornoHoleriteBBFacade retornoHoleriteBBFacade;
    private UploadedFile file;
    private String arquivoSelecionado;
    private List<ErroRetornoHoleriteBB> erroRetornoHoleriteBBs;

    public RetornoHoleriteBBControlador() {
        super(RetornoHoleriteBB.class);
    }

    public void gerar() throws IOException, FileNotFoundException {
        if (file != null) {
            InputStream stream = file.getInputstream();
            InputStreamReader streamReader = new InputStreamReader(stream);
            BufferedReader reader = new BufferedReader(streamReader);
            String linha = null;
            String tipoRegistro = "";

            while ((linha = reader.readLine()) != null) {
                ErroRetornoHoleriteBB erro = null;
                tipoRegistro = linha.substring(23,23);
                if(tipoRegistro == "0"){
                    erro = new ErroRetornoHoleriteBB(linha.substring(101,103), linha.substring(104,150));
                    erroRetornoHoleriteBBs.add(erro);
                }else if(tipoRegistro == "1"){
                    erro = new ErroRetornoHoleriteBB(linha.substring(101,103), linha.substring(104,150));
                    erroRetornoHoleriteBBs.add(erro);
                }else if(tipoRegistro == "2"){
                    erro = new ErroRetornoHoleriteBB(linha.substring(101,103), linha.substring(104,150));
                    erroRetornoHoleriteBBs.add(erro);
                }

                for (ErroRetornoHoleriteBB erroRetornoHoleriteBB : erroRetornoHoleriteBBs) {
                    if(erroRetornoHoleriteBB.getCodigo() == "000"){
                        FacesUtil.addInfo(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), "O Arquivo foi importado com sucesso e não apresentou erros.");
                        return;
                    }
                }
            }
        } else {
            FacesUtil.addWarn("Atençao", "Selecione o arquivo e clique em Upload.");
        }
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public String getArquivoSelecionado() {
        return arquivoSelecionado;
    }

    public void setArquivoSelecionado(String arquivoSelecionado) {
        this.arquivoSelecionado = arquivoSelecionado;
    }

    @Override
    public AbstractFacade getFacede() {
        return retornoHoleriteBBFacade;
    }

    public void importaArquivo(FileUploadEvent event) throws IOException, FileNotFoundException {
        file = event.getFile();
        arquivoSelecionado = file.getFileName();
    }


    @URLAction(mappingId = "novoRetornoHoleriteBB", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        erroRetornoHoleriteBBs = new ArrayList<>();
    }


    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        return true;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/retorno-holeritebb/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    private class ErroRetornoHoleriteBB {

        private String codigo;
        private String literal;


        private ErroRetornoHoleriteBB(String codigo, String literal) {
            this.codigo = codigo;
            this.literal = literal;
        }

        private String getCodigo() {
            return codigo;
        }

        private void setCodigo(String codigo) {
            this.codigo = codigo;
        }

        private String getLiteral() {
            return literal;
        }

        private void setLiteral(String literal) {
            this.literal = literal;
        }
    }
}
