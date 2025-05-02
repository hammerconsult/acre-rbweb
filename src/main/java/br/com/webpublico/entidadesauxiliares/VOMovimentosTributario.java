package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.procuradoria.PessoaProcesso;
import br.com.webpublico.entidades.tributario.procuradoria.TramiteProcessoJudicial;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by André Gustavo on 03/11/2015.
 *
 * View Object criado para representar a simulação da transferencia de movimentos de pessoa do Tributário
 */
public class VOMovimentosTributario {
    private TransferenciaMovPessoa transferencia;
    private List<CalculoITBI> transmitenteCalculosITBI;
    private List<CalculoITBI> adquirenteCalculosITBI;
    private List<CertidaoDividaAtiva> assinanteCertidoesDividaAtiva;
    private List<CertidaoDividaAtiva> emissorCertidoesDividaAtiva;
    private List<CertidaoDividaAtiva> devedorCertidoesDividaAtiva;
    private List<ItemInscricaoDividaAtiva> contribuinteItensInscricaoDividaAtiva;
    private List<TransferenciaPermissaoTransporte> solicitanteTransferenciasPermissao;
    private List<PessoaProcesso> envolvidoProcessosJudiciais;
    private List<SolicitacaoDoctoOficial> solicitanteFisicoSolicitacoesDocumentoOficial;
    private List<SolicitacaoDoctoOficial> solicitanteJuridicoSolicitacoesDocumentoOficial;
    private List<InscricaoDividaAtiva> contribuinteInscricoesDividaAtiva;
    private List<ProcessoFiscalizacao> fiscalizadoProcessosDeFiscalizacao;
    private List<ContratoRendasPatrimoniais> locatarioContratosRendasPatrimoniais;
    private List<PropriedadeCartorio> proprietarioCartorios;
    private List<ProcessoParcelamento> devedorProcessosDeParcelamento;
    private List<ProcessoParcelamento> procuradorProcessosParcelamento;
    private List<ProcessoParcelamento> fiadorProcessosParcelamento;
    private List<NFSAvulsa> tomadorNotasFiscaisAvulsas;
    private List<NFSAvulsa> prestadorNotasFiscaisAvulsas;
    private List<Processo> autorRequerenteProcesso;
    private List<ProcessoRestituicao> restituinteProcessoDeRestituicao;
    private List<CalculoDividaDiversa> contribuinteCalculosDividaDiversa;
    private List<CalculoTaxasDiversas> contribuinteCalculosTaxasDiversas;
    private List<DocumentoOficial> pessoaDocumentosOficiais;
    private List<ProcessoIsencaoAcrescimos> pessoaProcessosDeIsencaoDeAcrescimos;
    private List<PagamentoJudicial> pessoaPagamentosJudiciais;
    private List<UsuarioSistema> pessoaFisicaUsuariosSistema;
    private List<TramiteProcessoJudicial> juizResponsavelProcessosJudiciais;
    private List<AutuacaoFiscalizacaoRBTrans> motoristaInfratorAutuacoesFiscalizacaoRBTrans;
    private List<ResultadoParcela> resultadosParcela;

    public VOMovimentosTributario() {
        transmitenteCalculosITBI = new ArrayList<>();
        adquirenteCalculosITBI = new ArrayList<>();
        assinanteCertidoesDividaAtiva = new ArrayList<>();
        emissorCertidoesDividaAtiva = new ArrayList<>();
        devedorCertidoesDividaAtiva = new ArrayList<>();
        contribuinteItensInscricaoDividaAtiva = new ArrayList<>();
        solicitanteTransferenciasPermissao = new ArrayList<>();
        envolvidoProcessosJudiciais = new ArrayList<>();
        solicitanteFisicoSolicitacoesDocumentoOficial = new ArrayList<>();
        solicitanteJuridicoSolicitacoesDocumentoOficial = new ArrayList<>();
        contribuinteInscricoesDividaAtiva = new ArrayList<>();
        fiscalizadoProcessosDeFiscalizacao = new ArrayList<>();
        locatarioContratosRendasPatrimoniais = new ArrayList<>();
        proprietarioCartorios = new ArrayList<>();
        devedorProcessosDeParcelamento = new ArrayList<>();
        procuradorProcessosParcelamento = new ArrayList<>();
        fiadorProcessosParcelamento = new ArrayList<>();
        tomadorNotasFiscaisAvulsas = new ArrayList<>();
        prestadorNotasFiscaisAvulsas = new ArrayList<>();
        autorRequerenteProcesso = new ArrayList<>();
        restituinteProcessoDeRestituicao = new ArrayList<>();
        contribuinteCalculosDividaDiversa = new ArrayList<>();
        contribuinteCalculosTaxasDiversas = new ArrayList<>();
        pessoaDocumentosOficiais = new ArrayList<>();
        pessoaProcessosDeIsencaoDeAcrescimos = new ArrayList<>();
        pessoaPagamentosJudiciais = new ArrayList<>();
        pessoaFisicaUsuariosSistema = new ArrayList<>();
        juizResponsavelProcessosJudiciais = new ArrayList<>();
        motoristaInfratorAutuacoesFiscalizacaoRBTrans = new ArrayList<>();
        resultadosParcela = new ArrayList<>();
    }

    public TransferenciaMovPessoa getTransferencia() {
        return transferencia;
    }

    public void setTransferencia(TransferenciaMovPessoa transferencia) {
        this.transferencia = transferencia;
    }

    public List<CalculoITBI> getTransmitenteCalculosITBI() {
        return Collections.unmodifiableList(transmitenteCalculosITBI);
    }

    public List<CalculoITBI> getAdquirenteCalculosITBI() {
        return Collections.unmodifiableList(adquirenteCalculosITBI);
    }



    public List<CertidaoDividaAtiva> getAssinanteCertidoesDividaAtiva() {
        return Collections.unmodifiableList(assinanteCertidoesDividaAtiva);
    }

    public List<CertidaoDividaAtiva> getEmissorCertidoesDividaAtiva() {
        return Collections.unmodifiableList(emissorCertidoesDividaAtiva);
    }

    public List<CertidaoDividaAtiva> getDevedorCertidoesDividaAtiva() {
        return Collections.unmodifiableList(devedorCertidoesDividaAtiva);
    }

    public List<ItemInscricaoDividaAtiva> getContribuinteItensInscricaoDividaAtiva() {
        return Collections.unmodifiableList(contribuinteItensInscricaoDividaAtiva);
    }

    public List<TransferenciaPermissaoTransporte> getSolicitanteTransferenciasPermissao() {
        return Collections.unmodifiableList(solicitanteTransferenciasPermissao);
    }

    public List<PessoaProcesso> getEnvolvidoProcessosJudiciais() {
        return Collections.unmodifiableList(envolvidoProcessosJudiciais);
    }

    public List<SolicitacaoDoctoOficial> getSolicitanteFisicoSolicitacoesDocumentoOficial() {
        return Collections.unmodifiableList(solicitanteFisicoSolicitacoesDocumentoOficial);
    }

    public List<SolicitacaoDoctoOficial> getSolicitanteJuridicoSolicitacoesDocumentoOficial() {
        return Collections.unmodifiableList(solicitanteJuridicoSolicitacoesDocumentoOficial);
    }

    public List<InscricaoDividaAtiva> getContribuinteInscricoesDividaAtiva() {
        return Collections.unmodifiableList(contribuinteInscricoesDividaAtiva);
    }

    public List<ProcessoFiscalizacao> getFiscalizadoProcessosDeFiscalizacao() {
        return Collections.unmodifiableList(fiscalizadoProcessosDeFiscalizacao);
    }

    public List<ContratoRendasPatrimoniais> getLocatarioContratosRendasPatrimoniais() {
        return Collections.unmodifiableList(locatarioContratosRendasPatrimoniais);
    }

    public List<PropriedadeCartorio> getProprietarioCartorios() {
        return Collections.unmodifiableList(proprietarioCartorios);
    }

    public List<ProcessoParcelamento> getDevedorProcessosDeParcelamento() {
        return Collections.unmodifiableList(devedorProcessosDeParcelamento);
    }

    public List<ProcessoParcelamento> getProcuradorProcessosParcelamento() {
        return Collections.unmodifiableList(procuradorProcessosParcelamento);
    }

    public List<ProcessoParcelamento> getFiadorProcessosParcelamento() {
        return Collections.unmodifiableList(fiadorProcessosParcelamento);
    }

    public List<NFSAvulsa> getTomadorNotasFiscaisAvulsas() {
        return Collections.unmodifiableList(tomadorNotasFiscaisAvulsas);
    }

    public List<NFSAvulsa> getPrestadorNotasFiscaisAvulsas() {
        return Collections.unmodifiableList(prestadorNotasFiscaisAvulsas);
    }

    public List<Processo> getAutorRequerenteProcesso() {
        return Collections.unmodifiableList(autorRequerenteProcesso);
    }

    public List<ProcessoRestituicao> getRestituinteProcessoDeRestituicao() {
        return Collections.unmodifiableList(restituinteProcessoDeRestituicao);
    }

    public List<CalculoDividaDiversa> getContribuinteCalculosDividaDiversa() {
        return Collections.unmodifiableList(contribuinteCalculosDividaDiversa);
    }

    public List<CalculoTaxasDiversas> getContribuinteCalculosTaxasDiversas() {
        return Collections.unmodifiableList(contribuinteCalculosTaxasDiversas);
    }

    public List<DocumentoOficial> getPessoaDocumentosOficiais() {
        return Collections.unmodifiableList(pessoaDocumentosOficiais);
    }

    public List<ProcessoIsencaoAcrescimos> getPessoaProcessosDeIsencaoDeAcrescimos() {
        return Collections.unmodifiableList(pessoaProcessosDeIsencaoDeAcrescimos);
    }

    public List<PagamentoJudicial> getPessoaPagamentosJudiciais() {
        return Collections.unmodifiableList(pessoaPagamentosJudiciais);
    }

    public List<UsuarioSistema> getPessoaFisicaUsuariosSistema() {
        return Collections.unmodifiableList(pessoaFisicaUsuariosSistema);
    }

    public List<TramiteProcessoJudicial> getJuizResponsavelProcessosJudiciais() {
        return Collections.unmodifiableList(juizResponsavelProcessosJudiciais);
    }

    public List<AutuacaoFiscalizacaoRBTrans> getMotoristaInfratorAutuacoesFiscalizacaoRBTrans() {
        if (motoristaInfratorAutuacoesFiscalizacaoRBTrans != null) {
            return Collections.unmodifiableList(motoristaInfratorAutuacoesFiscalizacaoRBTrans);
        }
        return motoristaInfratorAutuacoesFiscalizacaoRBTrans;
    }

    public List<ResultadoParcela> getResultadosParcela() {
        return Collections.unmodifiableList(resultadosParcela);
    }

    public void setTransmitenteCalculosITBI(List<CalculoITBI> transmitenteCalculosITBI) {
        this.transmitenteCalculosITBI = transmitenteCalculosITBI;
    }

    public void setAdquirenteCalculosITBI(List<CalculoITBI> adquirenteCalculosITBI) {
        this.adquirenteCalculosITBI = adquirenteCalculosITBI;
    }

    public void setAssinanteCertidoesDividaAtiva(List<CertidaoDividaAtiva> assinanteCertidoesDividaAtiva) {
        this.assinanteCertidoesDividaAtiva = assinanteCertidoesDividaAtiva;
    }

    public void setEmissorCertidoesDividaAtiva(List<CertidaoDividaAtiva> emissorCertidoesDividaAtiva) {
        this.emissorCertidoesDividaAtiva = emissorCertidoesDividaAtiva;
    }

    public void setDevedorCertidoesDividaAtiva(List<CertidaoDividaAtiva> devedorCertidoesDividaAtiva) {
        this.devedorCertidoesDividaAtiva = devedorCertidoesDividaAtiva;
    }

    public void setContribuinteItensInscricaoDividaAtiva(List<ItemInscricaoDividaAtiva> contribuinteItensInscricaoDividaAtiva) {
        this.contribuinteItensInscricaoDividaAtiva = contribuinteItensInscricaoDividaAtiva;
    }

    public void setSolicitanteTransferenciasPermissao(List<TransferenciaPermissaoTransporte> solicitanteTransferenciasPermissao) {
        this.solicitanteTransferenciasPermissao = solicitanteTransferenciasPermissao;
    }

    public void setEnvolvidoProcessosJudiciais(List<PessoaProcesso> envolvidoProcessosJudiciais) {
        this.envolvidoProcessosJudiciais = envolvidoProcessosJudiciais;
    }

    public void setSolicitanteFisicoSolicitacoesDocumentoOficial(List<SolicitacaoDoctoOficial> solicitanteFisicoSolicitacoesDocumentoOficial) {
        this.solicitanteFisicoSolicitacoesDocumentoOficial = solicitanteFisicoSolicitacoesDocumentoOficial;
    }

    public void setSolicitanteJuridicoSolicitacoesDocumentoOficial(List<SolicitacaoDoctoOficial> solicitanteJuridicoSolicitacoesDocumentoOficial) {
        this.solicitanteJuridicoSolicitacoesDocumentoOficial = solicitanteJuridicoSolicitacoesDocumentoOficial;
    }

    public void setContribuinteInscricoesDividaAtiva(List<InscricaoDividaAtiva> contribuinteInscricoesDividaAtiva) {
        this.contribuinteInscricoesDividaAtiva = contribuinteInscricoesDividaAtiva;
    }

    public void setFiscalizadoProcessosDeFiscalizacao(List<ProcessoFiscalizacao> fiscalizadoProcessosDeFiscalizacao) {
        this.fiscalizadoProcessosDeFiscalizacao = fiscalizadoProcessosDeFiscalizacao;
    }

    public void setLocatarioContratosRendasPatrimoniais(List<ContratoRendasPatrimoniais> locatarioContratosRendasPatrimoniais) {
        this.locatarioContratosRendasPatrimoniais = locatarioContratosRendasPatrimoniais;
    }

    public void setProprietarioCartorios(List<PropriedadeCartorio> proprietarioCartorios) {
        this.proprietarioCartorios = proprietarioCartorios;
    }

    public void setDevedorProcessosDeParcelamento(List<ProcessoParcelamento> devedorProcessosDeParcelamento) {
        this.devedorProcessosDeParcelamento = devedorProcessosDeParcelamento;
    }

    public void setProcuradorProcessosParcelamento(List<ProcessoParcelamento> procuradorProcessosParcelamento) {
        this.procuradorProcessosParcelamento = procuradorProcessosParcelamento;
    }

    public void setFiadorProcessosParcelamento(List<ProcessoParcelamento> fiadorProcessosParcelamento) {
        this.fiadorProcessosParcelamento = fiadorProcessosParcelamento;
    }

    public void setTomadorNotasFiscaisAvulsas(List<NFSAvulsa> tomadorNotasFiscaisAvulsas) {
        this.tomadorNotasFiscaisAvulsas = tomadorNotasFiscaisAvulsas;
    }

    public void setPrestadorNotasFiscaisAvulsas(List<NFSAvulsa> prestadorNotasFiscaisAvulsas) {
        this.prestadorNotasFiscaisAvulsas = prestadorNotasFiscaisAvulsas;
    }

    public void setAutorRequerenteProcesso(List<Processo> autorRequerenteProcesso) {
        this.autorRequerenteProcesso = autorRequerenteProcesso;
    }

    public void setRestituinteProcessoDeRestituicao(List<ProcessoRestituicao> restituinteProcessoDeRestituicao) {
        this.restituinteProcessoDeRestituicao = restituinteProcessoDeRestituicao;
    }

    public void setContribuinteCalculosDividaDiversa(List<CalculoDividaDiversa> contribuinteCalculosDividaDiversa) {
        this.contribuinteCalculosDividaDiversa = contribuinteCalculosDividaDiversa;
    }

    public void setContribuinteCalculosTaxasDiversas(List<CalculoTaxasDiversas> contribuinteCalculosTaxasDiversas) {
        this.contribuinteCalculosTaxasDiversas = contribuinteCalculosTaxasDiversas;
    }

    public void setPessoaDocumentosOficiais(List<DocumentoOficial> pessoaDocumentosOficiais) {
        this.pessoaDocumentosOficiais = pessoaDocumentosOficiais;
    }

    public void setPessoaProcessosDeIsencaoDeAcrescimos(List<ProcessoIsencaoAcrescimos> pessoaProcessosDeIsencaoDeAcrescimos) {
        this.pessoaProcessosDeIsencaoDeAcrescimos = pessoaProcessosDeIsencaoDeAcrescimos;
    }

    public void setPessoaPagamentosJudiciais(List<PagamentoJudicial> pessoaPagamentosJudiciais) {
        this.pessoaPagamentosJudiciais = pessoaPagamentosJudiciais;
    }

    public void setPessoaFisicaUsuariosSistema(List<UsuarioSistema> pessoaFisicaUsuariosSistema) {
        this.pessoaFisicaUsuariosSistema = pessoaFisicaUsuariosSistema;
    }

    public void setJuizResponsavelProcessosJudiciais(List<TramiteProcessoJudicial> juizResponsavelProcessosJudiciais) {
        this.juizResponsavelProcessosJudiciais = juizResponsavelProcessosJudiciais;
    }

    public void setMotoristaInfratorAutuacoesFiscalizacaoRBTrans(List<AutuacaoFiscalizacaoRBTrans> motoristaInfratorAutuacoesFiscalizacaoRBTrans) {
        this.motoristaInfratorAutuacoesFiscalizacaoRBTrans = motoristaInfratorAutuacoesFiscalizacaoRBTrans;
    }

    public void setResultadosParcela(List<ResultadoParcela> resultadosParcela) {
        this.resultadosParcela = resultadosParcela;
    }
}
