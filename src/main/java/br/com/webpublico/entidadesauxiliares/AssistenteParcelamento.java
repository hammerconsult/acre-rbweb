package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.tributario.consultadebitos.ResultadoParcela;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Lists;

import java.util.List;

public class AssistenteParcelamento extends AssistenteBarraProgresso {
    private List<ResultadoParcela> parcelas;
    private String ipUsuario;

    public AssistenteParcelamento() {
        super();
        this.parcelas = Lists.newArrayList();
    }

    public List<ResultadoParcela> getParcelas() {
        return parcelas;
    }

    public void setParcelas(List<ResultadoParcela> parcelas) {
        this.parcelas = parcelas;
    }

    public String getIpUsuario() {
        return ipUsuario;
    }

    public void setIpUsuario(String ipUsuario) {
        this.ipUsuario = ipUsuario;
    }
}
