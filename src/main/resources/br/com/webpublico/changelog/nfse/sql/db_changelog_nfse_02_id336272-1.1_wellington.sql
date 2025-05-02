delete
from menu
where caminho = '/tributario/nfse/relatorio/receita-bruta-total.xhtml';

insert into RECURSOSISTEMA (ID, NOME, CAMINHO, CADASTRO, MODULO)
values (HIBERNATE_SEQUENCE.nextval,
        'TRIBUTÁRIO > NOTA FISCAL > RELATÓRIOS DA NFSE > RELATÓRIO DE RECEITA POR CONTRIBUINTE NO PERÍODO',
        '/tributario/nfse/relatorio/relatorio-receita-contribuinte.xhtml', 0, 'TRIBUTARIO');

insert into gruporecursosistema (GRUPORECURSO_ID, RECURSOSISTEMA_ID)
select GRUPORECURSO.id, RECURSOSISTEMA.id
from gruporecurso,
     recursosistema
where gruporecurso.nome = 'TRB - NFS-e - Gerencial'
  and recursosistema.caminho = '/tributario/nfse/relatorio/relatorio-receita-contribuinte.xhtml';

insert into menu (ID, LABEL, CAMINHO, PAI_ID, ORDEM, ICONE)
values (HIBERNATE_SEQUENCE.nextval, 'RELATÓRIO DE RECEITA POR CONTRIBUINTE NO PERÍODO',
        '/tributario/nfse/relatorio/relatorio-receita-contribuinte.xhtml',
        (select id from menu where LABEL = 'RELATÓRIOS DA NFSE'),
        200, null);
