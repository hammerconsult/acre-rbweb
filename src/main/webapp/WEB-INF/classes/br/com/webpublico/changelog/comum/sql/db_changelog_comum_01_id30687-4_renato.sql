update configuracaoderelatorio set urlwebpublico = 
case 
when chave = 'PREPRODUCAO' then 'http://preproducao.riobranco.ac.gov.br/'
when chave = 'HOMOLOG12C' then 'http://preproducao.riobranco.ac.gov.br/'
when chave = 'ADMINISTRATIVO' then 'http://administrativo.riobranco.ac.gov.br/'
when chave = 'ATUALIZACAODIARIA' then 'http://atualizacaodiaria.riobranco.ac.gov.br/'
when chave = 'WPPRODUCAO' then 'http://webpublico.riobranco.ac.gov.br/' end;
