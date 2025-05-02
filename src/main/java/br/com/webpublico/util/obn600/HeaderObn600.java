/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn600;

import br.com.webpublico.enums.ConfiguracaoHeaderObn600;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.obn350.UtilArquivoBancario;
import com.google.common.base.Preconditions;

/**
 *
 * @author reidocrime
 */
public class HeaderObn600 {

    private StringBuilder linha;
    private String zeros;
    private String dataGeracaoArquivo;
    private String horaGeracaoArquivo;
    private String numeroRemessaConsecutiva;
    private String c10E001;
    private String numeroContrato;
    private String brancos;
    private String numeroSequencial;

    public HeaderObn600() {
        throw new ExcecaoNegocioGenerica("Construtor não permitido para esta classe: HeaderObn600. Utilize outro construtor que esteja disponivel!");
    }

    public HeaderObn600(String zeros, String dataGeracaoArquivo, String horaGeracaoArquivo, String numeroRemessaConsecutiva, String c10E001, String numeroContrato, String brancos, String numeroSequencial) {
        this.zeros = zeros;
        this.dataGeracaoArquivo = dataGeracaoArquivo;
        this.horaGeracaoArquivo = horaGeracaoArquivo;
        this.numeroRemessaConsecutiva = numeroRemessaConsecutiva;
        this.c10E001 = c10E001;
        this.numeroContrato = numeroContrato;
        this.brancos = brancos;
        this.numeroSequencial = numeroSequencial;
        linha = new StringBuilder();
        validaNull();
        formataInformacoes();
        validaTamanhos();
        validaCaracteresErrados();
    }

    public String getLinha() {
        return linha.toString();
    }

    public String getZeros() {
        return zeros;
    }

    public String getDataGeracaoArquivo() {
        return dataGeracaoArquivo;
    }

    public String getHoraGeracaoArquivo() {
        return horaGeracaoArquivo;
    }

    public String getNumeroRemessaConsecutiva() {
        return numeroRemessaConsecutiva;
    }

    public String getC10E001() {
        return c10E001;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public String getBrancos() {
        return brancos;
    }

    public String getNumeroSequencial() {
        return numeroSequencial;
    }

    private void validaTamanhos() {
        Preconditions.checkArgument(zeros.length() == ConfiguracaoHeaderObn600.ZEROS.getTamanho(), "A quantidade de Zeros não batem com o Layout do Header OBN600! ");
        Preconditions.checkArgument(brancos.length() == ConfiguracaoHeaderObn600.BRANCOS.getTamanho(), "A quantidade de Brancos não batem com o Layout do Header OBN600 !");
        Preconditions.checkArgument(c10E001.length() == ConfiguracaoHeaderObn600.C10E001.getTamanho(), "A quantidade de Caractere no 10E001 não batem com o Layout do Header OBN600!");
        Preconditions.checkArgument(dataGeracaoArquivo.length() == ConfiguracaoHeaderObn600.DATAGERACAOARQUIVO.getTamanho(), "A quantidade de caracteres da data não batem com o Layout do Header OBN600!");
        Preconditions.checkArgument(horaGeracaoArquivo.length() == ConfiguracaoHeaderObn600.HORAGERACAOARQUIVO.getTamanho(), "A quantidade de caracteres da hora não batem com o Layout do Header OBN600!");
        Preconditions.checkArgument(numeroContrato.length() == ConfiguracaoHeaderObn600.NUMEROCONTRATO.getTamanho(), "A quantidade de caracteres do contrato não batem com o Layout do Header OBN600!");
        Preconditions.checkArgument(numeroRemessaConsecutiva.length() == ConfiguracaoHeaderObn600.NUMEROREMESSACONSECUTIVO.getTamanho(), "A quantidade de caracteres da numero de remessa não batem com o Layout do Header OBN600!");
        Preconditions.checkArgument(numeroSequencial.length() == ConfiguracaoHeaderObn600.NUMEROSEQUENCIAL.getTamanho(), "A quantidade de caracteres do sequencial não batem com o Layout do Header OBN600!");
        Preconditions.checkArgument(linha.toString().length() == 350, "A quantidade de caracteres da linha é diferente de 350! Total" + linha.toString().length());
    }

    private void validaCaracteresErrados() {
        Preconditions.checkArgument(UtilArquivoBancario.soZeros(zeros), "A Linha que deveria conter apenas o numero \"9\" tem outras caracteres!");
        Preconditions.checkArgument(UtilArquivoBancario.soBrancos(brancos), "A Linha que deveria conter apenas o caracter (branco \" \") tem outras caracteres!");
    }

    private void validaNull() {
        Preconditions.checkNotNull(zeros, "O campo \"Zeros\" do header do Layout OBN600 esta null");
        Preconditions.checkNotNull(brancos, "O campo \"Brancos\" do header do Layout OBN600 esta null");
        Preconditions.checkNotNull(c10E001, "O campo \"10E001\" do header do Layout OBN600 esta null");
        Preconditions.checkNotNull(dataGeracaoArquivo, "O campo \"Data do Arquivo\" do header do Layout OBN600 esta null");
        Preconditions.checkNotNull(horaGeracaoArquivo, "O campo \"Hora do arquivo\" do header do Layout OBN600 esta null");
        Preconditions.checkNotNull(numeroContrato, "O campo \"Numero do Contrato\" do header do Layout OBN600 esta null");
        Preconditions.checkNotNull(numeroRemessaConsecutiva, "O campo \"Numero da Remessa\" do header do Layout OBN600 esta null");
        Preconditions.checkNotNull(numeroSequencial, "O campo \"Numero Sequencial\" do header do Layout OBN600 esta null");
    }

    private void formataInformacoes() {

        brancos = UtilArquivoBancario.completaBrancoDireita(" ", ConfiguracaoHeaderObn600.BRANCOS.getTamanho());
        c10E001 = UtilArquivoBancario.completaBrancoDireita(c10E001, ConfiguracaoHeaderObn600.C10E001.getTamanho());
        dataGeracaoArquivo = UtilArquivoBancario.completaBrancoDireita(dataGeracaoArquivo, ConfiguracaoHeaderObn600.DATAGERACAOARQUIVO.getTamanho());;
        horaGeracaoArquivo = UtilArquivoBancario.completaBrancoDireita(horaGeracaoArquivo, ConfiguracaoHeaderObn600.HORAGERACAOARQUIVO.getTamanho());;
        numeroContrato = UtilArquivoBancario.completaZerosEsquera(numeroContrato, ConfiguracaoHeaderObn600.NUMEROCONTRATO.getTamanho());;
        numeroRemessaConsecutiva = UtilArquivoBancario.completaZerosEsquera(numeroRemessaConsecutiva, ConfiguracaoHeaderObn600.NUMEROREMESSACONSECUTIVO.getTamanho());;
        numeroSequencial = UtilArquivoBancario.completaZerosEsquera(numeroSequencial, ConfiguracaoHeaderObn600.NUMEROSEQUENCIAL.getTamanho());;
        zeros = UtilArquivoBancario.completaZerosEsquera(zeros, ConfiguracaoHeaderObn600.ZEROS.getTamanho());;
    }
}
