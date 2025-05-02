package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.LoteBaixa;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.util.AssistenteBarraProgresso;
import com.google.common.collect.Maps;

import java.util.Date;
import java.util.Map;

public class AssistenteGeracaoCadastrosDaf607 extends AssistenteBarraProgresso {

    private Map<String, CadastroEconomico> cadastros;
    private LoteBaixa loteBaixa;
    private String usuarioBanco;

    public AssistenteGeracaoCadastrosDaf607(LoteBaixa loteBaixa, Date dataOperacao, UsuarioSistema usuarioSistema, String usuarioBancoDeDados) {
        super();
        this.loteBaixa = loteBaixa;
        this.cadastros = Maps.newHashMap();
        this.usuarioBanco = usuarioBancoDeDados;
        this.setDataAtual(dataOperacao);
        this.setUsuarioSistema(usuarioSistema);
    }

    public void addCadastro(String cnpj, CadastroEconomico cadastro) {
        this.cadastros.put(cnpj, cadastro);
    }

    public Map<String, CadastroEconomico> getCadastros() {
        return cadastros;
    }

    public LoteBaixa getLoteBaixa() {
        return loteBaixa;
    }

    public String getUsuarioBanco() {
        return usuarioBanco;
    }

    public void setUsuarioBanco(String usuarioBanco) {
        this.usuarioBanco = usuarioBanco;
    }
}
