update RECURSOSISTEMA
set CAMINHO = replace(CAMINHO, '/rh/arquivos/caixaatuarial/', '/rh/arquivos/estudoatuarial/')
where id in (select rec.id
             from RECURSOSISTEMA rec
             where rec.CAMINHO like '/rh/arquivos/caixaatuarial/%');

update menu
set CAMINHO = '/rh/arquivos/estudoatuarial/lista.xhtml'
where LABEL = 'CAIXA ATUARIAL';

update menu
set LABEL = 'ESTUDO ATUARIAL'
where LABEL = 'CAIXA ATUARIAL';

