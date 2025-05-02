/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn600;

import br.com.webpublico.enums.ConfiguracaoTrailerObn600;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.obn350.UtilArquivoBancario;
import com.google.common.base.Preconditions;

/**
 *
 * @author reidocrime
 */
public class TrailerObn600 {

    private StringBuilder linha;
    private String noves;
    private String brancos;
    private String somatoriaDosValores;
    private String somatariaSequenciaRegistros;

    public TrailerObn600() {
        throw new ExcecaoNegocioGenerica("Construtor não permitido para esta classe: TrailerObn600. Utilize outro construtor que esteja disponivel!");
    }

    public TrailerObn600(String noves, String brancos, String somatoriaDosValores, String somatariaSequenciaRegistros) {
        this.noves = noves;
        this.brancos = brancos;
        this.somatoriaDosValores = somatoriaDosValores;
        this.somatariaSequenciaRegistros = somatariaSequenciaRegistros;
        validaNull();
        linha = new StringBuilder();
        formataInformacoes();
        validaCaracteresErrados();
        validaTamanhos();
    }

    public String getNoves() {
        return noves;
    }

    public void setNoves(String noves) {
        this.noves = noves;
    }

    public String getBrancos() {
        return brancos;
    }

    public void setBrancos(String brancos) {
        this.brancos = brancos;
    }

    public String getSomatoriaDosValores() {
        return somatoriaDosValores;
    }

    public void setSomatoriaDosValores(String somatoriaDosValores) {
        this.somatoriaDosValores = somatoriaDosValores;
    }

    public String getSomatariaSequenciaRegistros() {
        return somatariaSequenciaRegistros;
    }

    public void setSomatariaSequenciaRegistros(String somatariaSequenciaRegistros) {
        this.somatariaSequenciaRegistros = somatariaSequenciaRegistros;
    }

    public String getLinha() {
        return linha.toString();
    }

    private void validaTamanhos() {
        Preconditions.checkArgument(noves.length() == ConfiguracaoTrailerObn600.NOVES.getTamanho(), "A quantidade de Noves não batem com o Layout do Trailer OBN600! ");
        Preconditions.checkArgument(brancos.length() == ConfiguracaoTrailerObn600.BRANCOS.getTamanho(), "A quantidade de Brancos não batem com o Layout do Trailer OBN600 !");
        Preconditions.checkArgument(somatariaSequenciaRegistros.length() == ConfiguracaoTrailerObn600.SOMATORIADETODASSEQUENCAIS.getTamanho(), "A quantidade de Caractere no valor não batem com o Layout do Trailer OBN600!");
        Preconditions.checkArgument(somatoriaDosValores.length() == ConfiguracaoTrailerObn600.SOMATORIADOVALORES.getTamanho(), "A quantidade de Noves não batem com o Layout do Trailer!");
        Preconditions.checkArgument(linha.toString().length() == 350, "A quantidade de Noves não batem com o Layout do Trailer!");
    }

    private void validaCaracteresErrados() {
        Preconditions.checkArgument(UtilArquivoBancario.soNoves(noves), "A Linha que deveria conter apenas o numero \"9\" tem outras caracteres!");
        Preconditions.checkArgument(UtilArquivoBancario.soBrancos(brancos), "A Linha que deveria conter apenas o caracter (branco \" \") tem outras caracteres!");
    }

    private void validaNull() {
        Preconditions.checkNotNull(noves, "O campo \"Noves\" do Trailer do Layout OBN600 esta null");
        Preconditions.checkNotNull(brancos, "O campo \"Brancos\" do Trailer do Layout OBN600 esta null");
        Preconditions.checkNotNull(somatariaSequenciaRegistros, "O campo \"Somatoria da Sequencia\" do Trailer do Layout OBN600 esta null");
        Preconditions.checkNotNull(somatoriaDosValores, "O campo \"Somatoria dos Valores\" do Trailer do Layout OBN600 esta null");
    }

    private void formataInformacoes() {
        noves = UtilArquivoBancario.completaZerosEsquera("9", ConfiguracaoTrailerObn600.NOVES.getTamanho());
        brancos = UtilArquivoBancario.completaBrancoDireita(" ", ConfiguracaoTrailerObn600.BRANCOS.getTamanho());
        somatariaSequenciaRegistros = UtilArquivoBancario.completaZerosEsquera(somatariaSequenciaRegistros, ConfiguracaoTrailerObn600.SOMATORIADETODASSEQUENCAIS.getTamanho());
        somatoriaDosValores = UtilArquivoBancario.completaZerosEsquera(somatoriaDosValores, ConfiguracaoTrailerObn600.SOMATORIADOVALORES.getTamanho());
        linha.append(noves);
        linha.append(brancos);
        linha.append(somatoriaDosValores);
        linha.append(somatariaSequenciaRegistros);
    }
}
