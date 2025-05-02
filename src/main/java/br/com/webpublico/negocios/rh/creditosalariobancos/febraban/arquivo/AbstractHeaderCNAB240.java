package br.com.webpublico.negocios.rh.creditosalariobancos.febraban.arquivo;

import br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.annotation.Campo;
import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.CARACTERE;
import static br.com.webpublico.negocios.rh.creditosalariobancos.exportadorarquivo.enumeration.TipoCampo.INTEIRO;

public abstract class AbstractHeaderCNAB240 extends CNAB240 {
    //Código do Banco - 1 até 3
    @Campo(posicao = 1, tipo = INTEIRO, tamanho = 3)
    private String banco;
    //Código do Lote de Serviço - 4 a 7
    @Campo(posicao = 2, tipo = INTEIRO, tamanho = 4)
    private String lote;
    //tipo de registro - 8 a 8
    @Campo(posicao = 3, tipo = INTEIRO, tamanho = 1)
    private String registro;

    // Cnab febraban - uso esclusivo - 9 a 17
    @Campo(posicao = 4, tipo = CARACTERE, tamanho = 9)
    private String usoExlcusivo;

    //Tipo de Inscricao - 18 a 18 - '2' = CGC/CNPJ
    @Campo(posicao = 5, tipo = CARACTERE, tamanho = 1)
    private String tipoInscricao;
    //Tipo de Inscricao - 19 a 32 - CNPJ
    @Campo(posicao = 6, tipo = CARACTERE, tamanho = 14)
    private String numeroInscricao;
    //Código Convênio - 33 a 52
    @Campo(posicao = 7, tipo = CARACTERE, tamanho = 20)
    private String codigoConvenio;
    //Agencia mantenedora da conta - 53 a 57
    @Campo(posicao = 8, tipo = CARACTERE, tamanho = 5)
    private String agenciaMantenedora;
    //Agencia mantenedora da conta - 53 a 57
    @Campo(posicao = 9, tipo = CARACTERE, tamanho = 1)
    private String digitoAgenciaMantenedora;
    //Número da conta do cliente - conta da prefeitura - 59 a 70
    @Campo(posicao = 10, tipo = CARACTERE, tamanho = 12)
    private String numeroConta;
    //Número da conta do  71 a 71
    @Campo(posicao = 11, tipo = CARACTERE, tamanho = 1)
    private String digitoVerificador;
    //Digito verificador da agẽncia/conta 72 a 72
    @Campo(posicao = 12, tipo = CARACTERE, tamanho = 1)
    private String digitoVerificadorAgenciaConta;
    //73 a 102
    @Campo(posicao = 13, tipo = CARACTERE, tamanho = 30)
    private String nomeEmpresa;
    //103 a 132
    @Campo(posicao = 14, tipo = CARACTERE, tamanho = 30)
    private String nomeBanco;
    //133 a 142
    @Campo(posicao = 15, tipo = CARACTERE, tamanho = 10)
    private String usoExlcusivo2;

    //143 a 143
    @Campo(posicao = 16, tipo = CARACTERE, tamanho = 1)
    private String codigoRemessa;
    //144 a 151
    @Campo(posicao = 17, tipo = CARACTERE, tamanho = 8)
    private String dataGeracao;
    //152 a 157
    @Campo(posicao = 18, tipo = CARACTERE, tamanho = 6)
    private String horaGeracao;
    //158 a 163
    @Campo(posicao = 19, tipo = CARACTERE, tamanho = 6)
    private String sequencial;
    //164 a 166
    @Campo(posicao = 20, tipo = CARACTERE, tamanho = 3)
    private String versaoLayout;
    //167 a 171
    @Campo(posicao = 21, tipo = CARACTERE, tamanho = 5)
    private String densidade;
    //172 a 191
    @Campo(posicao = 22, tipo = CARACTERE, tamanho = 20)
    private String reservadoBanco;
    //192 a 211
    @Campo(posicao = 23, tipo = CARACTERE, tamanho = 20)
    private String reservadoEmpresa; //identificação da empresa do arquivo gerado, colocar o id do registro gerado
    //212 a 240
    @Campo(posicao = 24, tipo = CARACTERE, tamanho = 29)
    private String usoExlcusivo3;

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public String getUsoExlcusivo() {
        return usoExlcusivo;
    }

    public void setUsoExlcusivo(String usoExlcusivo) {
        this.usoExlcusivo = usoExlcusivo;
    }

    public String getTipoInscricao() {
        return tipoInscricao;
    }

    public void setTipoInscricao(String tipoInscricao) {
        this.tipoInscricao = tipoInscricao;
    }

    public String getNumeroInscricao() {
        return numeroInscricao;
    }

    public void setNumeroInscricao(String numeroInscricao) {
        this.numeroInscricao = numeroInscricao;
    }

    public String getCodigoConvenio() {
        return codigoConvenio;
    }

    public void setCodigoConvenio(String codigoConvenio) {
        this.codigoConvenio = codigoConvenio;
    }

    public String getAgenciaMantenedora() {
        return agenciaMantenedora;
    }

    public void setAgenciaMantenedora(String agenciaMantenedora) {
        this.agenciaMantenedora = agenciaMantenedora;
    }

    public String getDigitoAgenciaMantenedora() {
        return digitoAgenciaMantenedora;
    }

    public void setDigitoAgenciaMantenedora(String digitoAgenciaMantenedora) {
        this.digitoAgenciaMantenedora = digitoAgenciaMantenedora;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(String digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
    }

    public String getDigitoVerificadorAgenciaConta() {
        return digitoVerificadorAgenciaConta;
    }

    public void setDigitoVerificadorAgenciaConta(String digitoVerificadorAgenciaConta) {
        this.digitoVerificadorAgenciaConta = digitoVerificadorAgenciaConta;
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public String getNomeBanco() {
        return nomeBanco;
    }

    public void setNomeBanco(String nomeBanco) {
        this.nomeBanco = nomeBanco;
    }

    public String getUsoExlcusivo2() {
        return usoExlcusivo2;
    }

    public void setUsoExlcusivo2(String usoExlcusivo2) {
        this.usoExlcusivo2 = usoExlcusivo2;
    }

    public String getCodigoRemessa() {
        return codigoRemessa;
    }

    public void setCodigoRemessa(String codigoRemessa) {
        this.codigoRemessa = codigoRemessa;
    }

    public String getDataGeracao() {
        return dataGeracao;
    }

    public void setDataGeracao(String dataGeracao) {
        this.dataGeracao = dataGeracao;
    }

    public String getHoraGeracao() {
        return horaGeracao;
    }

    public void setHoraGeracao(String horaGeracao) {
        this.horaGeracao = horaGeracao;
    }

    public String getSequencial() {
        return sequencial;
    }

    public void setSequencial(String sequencial) {
        this.sequencial = sequencial;
    }

    public String getVersaoLayout() {
        return versaoLayout;
    }

    public void setVersaoLayout(String versaoLayout) {
        this.versaoLayout = versaoLayout;
    }

    public String getDensidade() {
        return densidade;
    }

    public void setDensidade(String densidade) {
        this.densidade = densidade;
    }

    public String getReservadoBanco() {
        return reservadoBanco;
    }

    public void setReservadoBanco(String reservadoBanco) {
        this.reservadoBanco = reservadoBanco;
    }

    public String getReservadoEmpresa() {
        return reservadoEmpresa;
    }

    public void setReservadoEmpresa(String reservadoEmpresa) {
        this.reservadoEmpresa = reservadoEmpresa;
    }

    public String getUsoExlcusivo3() {
        return usoExlcusivo3;
    }

    public void setUsoExlcusivo3(String usoExlcusivo3) {
        this.usoExlcusivo3 = usoExlcusivo3;
    }

}
