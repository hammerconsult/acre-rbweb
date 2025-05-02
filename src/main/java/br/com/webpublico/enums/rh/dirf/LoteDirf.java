package br.com.webpublico.enums.rh.dirf;

import br.com.webpublico.entidadesauxiliares.rh.PessoaInfo;

import java.util.List;

/**
 * Created by peixe on 19/01/18.
 */
public class LoteDirf {
    private DirfReg35 dirfReg35;
    private List<PessoaInfo> pessoas;

    public LoteDirf() {
    }

    public LoteDirf(DirfReg35 dirfReg35, List<PessoaInfo> pessoas) {
        this.dirfReg35 = dirfReg35;
        this.pessoas = pessoas;
    }

    public DirfReg35 getDirfReg35() {
        return dirfReg35;
    }

    public void setDirfReg35(DirfReg35 dirfReg35) {
        this.dirfReg35 = dirfReg35;
    }

    public List<PessoaInfo> getPessoas() {
        return pessoas;
    }

    public void setPessoas(List<PessoaInfo> pessoas) {
        this.pessoas = pessoas;
    }
}
