package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.DocumentoProcesso;

public class WSDocumento {

    private String nome;
    private String numero;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public WSDocumento() {
    }

    public WSDocumento(DocumentoProcesso doc) {
        if (doc != null) {
            this.numero = doc.getNumeroDocumento();
            if (doc.getDocumento() != null) {
                this.nome = doc.getDocumento().getDescricao();
            }
        }
    }
}
