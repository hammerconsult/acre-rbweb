update menu
set label = 'FALE CONOSCO/RECLAMAÇÕES - NFS-E'
where CAMINHO in ('/tributario/nfse/faleconosco/lista.xhtml');

delete
from menu
where CAMINHO = '/tributario/nfse/faleconosco/lista-reclamacoes.xhtml';

delete
from GRUPORECURSOSISTEMA
where RECURSOSISTEMA_ID =
      (select id from RECURSOSISTEMA where CAMINHO = '/tributario/nfse/faleconosco/lista-reclamacoes.xhtml');

delete
from RECURSOSISTEMA
where CAMINHO = '/tributario/nfse/faleconosco/lista-reclamacoes.xhtml';
