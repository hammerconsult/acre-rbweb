update CONSULTAENTIDADE
set JSON = replace(JSON, '/auditoria/listar-por-entidade/$identificador/PESSOA_FISICA_RH/',
                   '/auditoria/listar-por-entidade/$identificador/PESSOA_FISICA/')
where CHAVE like 'PESSOA_FISICA_RH'
