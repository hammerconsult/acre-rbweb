package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.DAM;
import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;

import java.io.File;
import java.util.List;

public class VODamRemessaFile {

    private DAM dam;
    private File fileCda;
    private File fileDam;
    private List<ResultadoParcela> parcelasCda;

    public VODamRemessaFile(DAM dam) {
        this.dam = dam;
    }

    public DAM getDam() {
        return dam;
    }

    public void setDam(DAM dam) {
        this.dam = dam;
    }

    public File getFileCda() {
        return fileCda;
    }

    public void setFileCda(File fileCda) {
        this.fileCda = fileCda;
    }

    public File getFileDam() {
        return fileDam;
    }

    public void setFileDam(File fileDam) {
        this.fileDam = fileDam;
    }

    public List<ResultadoParcela> getParcelasCda() {
        return parcelasCda;
    }

    public void setParcelasCda(List<ResultadoParcela> parcelasCda) {
        this.parcelasCda = parcelasCda;
    }
}
