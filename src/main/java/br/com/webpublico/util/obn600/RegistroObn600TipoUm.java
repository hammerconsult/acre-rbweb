/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.util.obn600;

import br.com.webpublico.enums.ConfiguracaoRegistroTipoUmOBN600;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.util.obn350.UtilArquivoBancario;
import com.google.common.base.Preconditions;

/**
 *
 * @author reidocrime
 */
public class RegistroObn600TipoUm {

    private StringBuilder linha;
    private String tipoUm;
    private String codigoAgenciaUnidadeGestoraDevolucao;
    private String codigoUnidadeGestora;
    private String contaUnidadeGestoraDevolucao;
    private String brancos;
    private String nomeUnidadeGestora;
    private String branco;

    public RegistroObn600TipoUm() {
        throw new ExcecaoNegocioGenerica("Construtor não permitido para esta classe: RegistroObn600TipoUm. Utilize outro construtor que esteja disponivel!");
    }

    public RegistroObn600TipoUm(String tipoUm, String codigoAgenciaUnidadeGestoraDevolucao, String codigoUnidadeGestora, String contaUnidadeGestoraDevolucao, String brancos, String nomeUnidadeGestora, String barnco) {
        this.tipoUm = tipoUm;
        this.codigoAgenciaUnidadeGestoraDevolucao = codigoAgenciaUnidadeGestoraDevolucao;
        this.codigoUnidadeGestora = codigoUnidadeGestora;
        this.contaUnidadeGestoraDevolucao = contaUnidadeGestoraDevolucao;
        this.brancos = brancos;
        this.nomeUnidadeGestora = nomeUnidadeGestora;
        this.branco = barnco;
        validaNull();
        formataInformacoes();
        validaTamanhos();
        validaCaracteresErrados();
    }

    public String getLinha() {
        return linha.toString();
    }

    public String getTipoUm() {
        return tipoUm;
    }

    public String getCodigoAgenciaUnidadeGestoraDevolucao() {
        return codigoAgenciaUnidadeGestoraDevolucao;
    }

    public String getCodigoUnidadeGestora() {
        return codigoUnidadeGestora;
    }

    public String getContaUnidadeGestoraDevolucao() {
        return contaUnidadeGestoraDevolucao;
    }

    public String getBrancos() {
        return brancos;
    }

    public String getNomeUnidadeGestora() {
        return nomeUnidadeGestora;
    }

    public String getBarnco() {
        return branco;
    }

    private void validaTamanhos() {
        Preconditions.checkArgument(tipoUm.length() == ConfiguracaoRegistroTipoUmOBN600.TIPO.getTamanho(), "A quantidade de caracteres no tipo da linha não batem com o Layout do Tipo Um OBN600! ");
        Preconditions.checkArgument(codigoAgenciaUnidadeGestoraDevolucao.length() == ConfiguracaoRegistroTipoUmOBN600.CODIGOAGENCIADEVOLUCAO.getTamanho(), "A quantidade de Caractere Agencia não batem com o Layout do Tipo Um OBN600 !");
        Preconditions.checkArgument(codigoUnidadeGestora.length() == ConfiguracaoRegistroTipoUmOBN600.CODIGOUNIDADEGESTORA.getTamanho(), "A quantidade de Caractere no Codigo da Unidade Gestora não batem com o Layout do Tipo Um OBN600!");
        Preconditions.checkArgument(contaUnidadeGestoraDevolucao.length() == ConfiguracaoRegistroTipoUmOBN600.CONTAUGDEVOLUCAO.getTamanho(), "A quantidade de caracteres da Conta de Devolução não batem com o Layout do Tipo Um OBN600!");
        Preconditions.checkArgument(brancos.length() == ConfiguracaoRegistroTipoUmOBN600.BRANCOS.getTamanho(), "A quantidade de caracteres do campo \"Brancos\"não batem com o Layout do Tipo Um OBN600!");
        Preconditions.checkArgument(nomeUnidadeGestora.length() == ConfiguracaoRegistroTipoUmOBN600.NOMEUG.getTamanho(), "A quantidade de caracteres do Nome da Unidade Gestora não batem com o Layout do Tipo Um OBN600!");
        Preconditions.checkArgument(branco.length() == ConfiguracaoRegistroTipoUmOBN600.BRANCO.getTamanho(), "A quantidade de caracteres do campo \"Branco\" não batem com o Layout do Tipo Um OBN600!");
        Preconditions.checkArgument(linha.toString().length() == 350, "A quantidade de caracteres da linha é diferente de 350! Total" + linha.toString().length());
    }

    private void validaCaracteresErrados() {
        Preconditions.checkArgument(UtilArquivoBancario.soBrancos(brancos), "A Linha que deveria conter apenas o caracter (branco \" \") tem outras caracteres!");
        Preconditions.checkArgument(UtilArquivoBancario.soBrancos(branco), "A Linha que deveria conter apenas o caracter (branco \" \") tem outras caracteres!");
    }

    private void validaNull() {
        Preconditions.checkNotNull(tipoUm, "O campo \"Tipo Linha\" do Tipo Um do Layout OBN600 esta null");
        Preconditions.checkNotNull(codigoAgenciaUnidadeGestoraDevolucao, "O campo \"Agencia Devolução U.G\"  Tipo Um do Layout OBN600 esta null");
        Preconditions.checkNotNull(codigoUnidadeGestora, "O campo \"Codigo Unidade Gestora\" do Tipo Um do Layout OBN600 esta null");
        Preconditions.checkNotNull(contaUnidadeGestoraDevolucao, "O campo \"Conta de Devolução da Unidade gestora\" do Tipo Um do Layout OBN600 esta null");
        Preconditions.checkNotNull(brancos, "O campo \"brancos\" do Tipo Um do Layout OBN600 esta null");
        Preconditions.checkNotNull(nomeUnidadeGestora, "O campo \"Nome da Unidade Gestora\" do Tipo Um do Layout OBN600 esta null");
        Preconditions.checkNotNull(branco, "O campo \"branco\" do Tipo Um do Layout OBN600 esta null");
    }

    private void formataInformacoes() {
        codigoAgenciaUnidadeGestoraDevolucao=UtilArquivoBancario.completaZerosEsquera(codigoAgenciaUnidadeGestoraDevolucao, ConfiguracaoRegistroTipoUmOBN600.CODIGOAGENCIADEVOLUCAO.getTamanho());
        codigoUnidadeGestora=UtilArquivoBancario.completaZerosEsquera(codigoUnidadeGestora,ConfiguracaoRegistroTipoUmOBN600.CODIGOUNIDADEGESTORA.getTamanho());
        contaUnidadeGestoraDevolucao=UtilArquivoBancario.completaZerosEsquera(contaUnidadeGestoraDevolucao,ConfiguracaoRegistroTipoUmOBN600.CONTAUGDEVOLUCAO.getTamanho());
        brancos=UtilArquivoBancario.completaBrancoEsquera(brancos, ConfiguracaoRegistroTipoUmOBN600.BRANCOS.getTamanho());
        branco=UtilArquivoBancario.completaBrancoEsquera(branco, ConfiguracaoRegistroTipoUmOBN600.BRANCO.getTamanho());
    }
}
