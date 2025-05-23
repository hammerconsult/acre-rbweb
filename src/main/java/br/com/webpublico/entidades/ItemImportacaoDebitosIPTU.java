package br.com.webpublico.entidades;

import br.com.webpublico.enums.ListaDeInconsistencias;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by Filipe
 * On 18, Abril, 2019,
 * At 14:00
 */

@Entity
@Audited
@Etiqueta("Itens de retorno bancário de débitos de IPTU")
public class ItemImportacaoDebitosIPTU extends SuperEntidade {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Cpf do Portador")
    private String cpf;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código de Retorno Bancário")
    private String codigoRetorno;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Descrição de Inconsistência")
    private ListaDeInconsistencias listaDeInconsistencias;
    @ManyToOne
    private ImportacaoDebitosIPTU importacaoDebitosIPTU;
    private String linhaDoArquivo;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getLinhaDoArquivo() {
        return linhaDoArquivo;
    }

    public void setLinhaDoArquivo(String linhaDoArquivo) {
        this.linhaDoArquivo = linhaDoArquivo;
    }

    public String getCodigoRetorno() {
        return codigoRetorno;
    }

    public void setCodigoRetorno(String codigoRetorno) {
        this.codigoRetorno = codigoRetorno;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public ListaDeInconsistencias getListaDeInconsistencias() {
        return listaDeInconsistencias;
    }

    public void setListaDeInconsistencias(ListaDeInconsistencias listaDeInconsistencias) {
        this.listaDeInconsistencias = listaDeInconsistencias;
    }

    public ImportacaoDebitosIPTU getImportacaoDebitosIPTU() {
        return importacaoDebitosIPTU;
    }

    public void setImportacaoDebitosIPTU(ImportacaoDebitosIPTU importacaoDebitosIPTU) {
        this.importacaoDebitosIPTU = importacaoDebitosIPTU;
    }

    public String getCpfCnpjFormatado() {

        String cpf = getCpf().startsWith("000") ? getCpf().substring(3, 14) : "";

        if (!Strings.isNullOrEmpty(cpf)) {
            return new StringBuilder(cpf)
                    .insert(3, ".")
                    .insert(7, ".")
                    .insert(11, "-").toString();
        } else {
            return new StringBuilder(getCpf())
                    .insert(2, ".")
                    .insert(6, ".")
                    .insert(10, "/")
                    .insert(15, "-").toString();
        }

    }

    @Override
    public Long getId() {
        return null;
    }

    public void setId(Long id) {
        this.id = id;
    }
}


