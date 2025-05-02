package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.NFSAvulsa;
import br.com.webpublico.util.StringUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.persistence.Query;
import java.util.List;

public class FiltroCancelamentoNFSa {

    private Exercicio exercicio;
    private String numero;
    private String nomeRazaoSocial;
    private String cpfCnpj;
    private List<NFSAvulsa> notas;

    public FiltroCancelamentoNFSa() {
        notas = Lists.newArrayList();
    }

    public String adicionarFiltros() {
        StringBuilder filtros = new StringBuilder();
        if (exercicio != null) {
            filtros.append(" and nfsa.exercicio_id = :exercicio ");
        }
        if (!Strings.isNullOrEmpty(numero)) {
            filtros.append(" and to_char(nfsa.numero) = :numero ");
        }
        if (!Strings.isNullOrEmpty(cpfCnpj)) {
            filtros.append(" and replace(replace(replace(coalesce(pf.cpf, pj.cnpj),'.',''),'-',''),'/','') like :cpfCnpj ");
        }
        if (!Strings.isNullOrEmpty(nomeRazaoSocial)) {
            filtros.append(" and coalesce(trim(lower(pf.nome)), trim(lower(pj.razaoSocial))) like :nomeRazaoSocial ");
        }
        return filtros.toString();
    }

    public void adicionarParametros(Query q) {
        if (exercicio != null){
            q.setParameter("exercicio", exercicio.getId());
        }
        if (!Strings.isNullOrEmpty(numero)) {
            q.setParameter("numero", numero.trim());
        }
        if (!Strings.isNullOrEmpty(cpfCnpj)){
            q.setParameter("cpfCnpj", "%" + StringUtil.retornaApenasNumeros(cpfCnpj).trim() + "%");
        }
        if (!Strings.isNullOrEmpty(nomeRazaoSocial)){
            q.setParameter("nomeRazaoSocial", "%" + nomeRazaoSocial.toLowerCase().trim() + "%");
        }
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNomeRazaoSocial() {
        return nomeRazaoSocial;
    }

    public void setNomeRazaoSocial(String nomeRazaoSocial) {
        this.nomeRazaoSocial = nomeRazaoSocial;
    }

    public String getCpfCnpj() {
        return cpfCnpj;
    }

    public void setCpfCnpj(String cpfCnpj) {
        this.cpfCnpj = cpfCnpj;
    }

    public List<NFSAvulsa> getNotas() {
        return notas;
    }

    public void setNotas(List<NFSAvulsa> notas) {
        this.notas = notas;
    }
}
