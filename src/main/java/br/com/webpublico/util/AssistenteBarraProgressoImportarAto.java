package br.com.webpublico.util;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.AtoLegalORC;
import br.com.webpublico.entidadesauxiliares.AtoLegalValorOperacaoCreditoESuplementacaoVO;
import br.com.webpublico.entidadesauxiliares.OrgaoAtoLegalVO;
import com.google.common.collect.Lists;
import org.primefaces.event.FileUploadEvent;

import java.util.List;

public class AssistenteBarraProgressoImportarAto extends AssistenteBarraProgresso {
    private List<AtoLegal> atosNaoCadastrados;
    private List<OrgaoAtoLegalVO> orgaosPorTipo;
    private StringBuilder errosAoImportar;
    private AtoLegalORC atoLegalORC;
    private AtoLegalValorOperacaoCreditoESuplementacaoVO valorOperacoesCredito;
    private FileUploadEvent evento;

    public AssistenteBarraProgressoImportarAto() {
        super();
        errosAoImportar = new StringBuilder();
        atosNaoCadastrados = Lists.newArrayList();
        atoLegalORC = new AtoLegalORC();
    }

    public List<AtoLegal> getAtosNaoCadastrados() {
        return atosNaoCadastrados;
    }

    public void setAtosNaoCadastrados(List<AtoLegal> atosNaoCadastrados) {
        this.atosNaoCadastrados = atosNaoCadastrados;
    }

    public List<OrgaoAtoLegalVO> getOrgaosPorTipo() {
        return orgaosPorTipo;
    }

    public void setOrgaosPorTipo(List<OrgaoAtoLegalVO> orgaosPorTipo) {
        this.orgaosPorTipo = orgaosPorTipo;
    }

    public StringBuilder getErrosAoImportar() {
        return errosAoImportar;
    }

    public void setErrosAoImportar(StringBuilder errosAoImportar) {
        this.errosAoImportar = errosAoImportar;
    }

    public AtoLegalORC getAtoLegalORC() {
        return atoLegalORC;
    }

    public void setAtoLegalORC(AtoLegalORC atoLegalORC) {
        this.atoLegalORC = atoLegalORC;
    }

    public AtoLegalValorOperacaoCreditoESuplementacaoVO getValorOperacoesCredito() {
        return valorOperacoesCredito;
    }

    public void setValorOperacoesCredito(AtoLegalValorOperacaoCreditoESuplementacaoVO valorOperacoesCredito) {
        this.valorOperacoesCredito = valorOperacoesCredito;
    }

    public void calcularLancamentos() {
        valorOperacoesCredito.calculaLancamento(atoLegalORC);
    }

    public FileUploadEvent getEvento() {
        return evento;
    }

    public void setEvento(FileUploadEvent evento) {
        this.evento = evento;
    }
}
